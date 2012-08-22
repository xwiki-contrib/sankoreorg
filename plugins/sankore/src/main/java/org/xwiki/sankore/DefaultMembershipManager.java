package org.xwiki.sankore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.context.Execution;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;
import org.xwiki.sankore.internal.ClassManager;
import org.xwiki.sankore.internal.GroupClass;
import org.xwiki.sankore.internal.InvitationClass;
import org.xwiki.sankore.internal.InvitationObjectDocument;
import org.xwiki.sankore.internal.MemberGroupObjectDocument;
import org.xwiki.sankore.internal.MembershipRequestClass;
import org.xwiki.sankore.internal.MembershipRequestObjectDocument;
import org.xwiki.sankore.internal.UserClass;
import org.xwiki.sankore.internal.UserObjectDocument;
import org.xwiki.sankore.internal.UserProfileObjectDocument;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;

@Component
@Singleton
public class DefaultMembershipManager implements MembershipManager
{
    private static final String ERROR_KEY = "membership_manager_error";

    private static final String ERROR_INVITEE_MISSING = "membership_manager.invitee_missing";
    private static final String ERROR_INVALID_EMAIL = "membership_manager.invalid_email";
    private static final String ERROR_ALREADY_INVITED = "membership_manager.already_invited";
    private static final String ERROR_ALREADY_MEMBER = "membership_manager.already_member";
    private static final String ERROR_RIGHTS_REQUIRED = "membership_manager.rights_required";

    private static final String ERROR_INVALID_STATUS = "membership_manager.invalid_status";

    private static final String ERROR_DOCUMENTOBJECT_NOT_FOUND = "membership_manager.documentobject_not_found";

    private static final String ERROR_MAILSENDER_EXCEPTION = "membership_manager.mailsender_exception";
    private static final String ERROR_NULLPOINTER_EXCEPTION = "membership_manager.nullpointer_exception";
    private static final String ERROR_QUERY_EXCEPTION = "membership_manager.query_exception";

    @Inject
    private Logger logger;

    @Inject
    private Execution execution;

    @Inject
    private QueryManager queryManager;

    @Inject
    private MailSender mailSender;

    @Inject
    @Named("UsersClass")
    ClassManager<UserObjectDocument> usersClass;

    @Inject
    @Named("UserProfileClass")
    ClassManager<UserProfileObjectDocument> userProfileClass;

    @Inject
    @Named("InvitationClass")
    ClassManager<InvitationObjectDocument> invitationClass;

    @Inject
    @Named("MembershipRequestClass")
    ClassManager<MembershipRequestObjectDocument> membershipRequestClass;

    @Inject
    @Named("MemberGroupClass")
    ClassManager<MemberGroupObjectDocument> memberGroupClass;

    @Inject
    @Named("wiki")
    ConfigurationSource wikiPreferences;

    @Inject
    private DocumentReferenceResolver<String> stringDocumentReferenceResolver;

    @Inject
    @Named("local")
    private EntityReferenceSerializer<String> referenceSerializer;

    @Inject
    @Named("current/reference")
    private DocumentReferenceResolver<EntityReference> currentReferenceDocumentReferenceResolver;


    private static final String ADMIN_EMAIL_PREFERENCE_KEY = "admin_email";

    public static final String DEFAULT_RESOURCES_SPACENAME = "Groups";
    public static final String DEFAULT_MAILTEMPLATE_PAGENAME = "MailTemplate";

    private XWikiContext getContext()
    {
        return (XWikiContext) this.execution.getContext().getProperty("xwikicontext");
    }

    private DocumentReference getDefaultMailTemplate(String type, String action)
    {
        String templateName = DEFAULT_MAILTEMPLATE_PAGENAME + StringUtils.capitalize(action) + StringUtils.capitalize(type);
        EntityReference templateReference = new EntityReference(templateName, EntityType.DOCUMENT,
                new EntityReference(DEFAULT_RESOURCES_SPACENAME, EntityType.SPACE));

        return currentReferenceDocumentReferenceResolver.resolve(templateReference);
    }

    public boolean invite(Group group, String usernameOrEmail, String message)
            throws XWikiException
    {
        if (group == null) {
            setError(ERROR_NULLPOINTER_EXCEPTION);
            return false;
        }

        if (StringUtils.isEmpty(usernameOrEmail)) {
            // invitee missing
            setError(ERROR_INVITEE_MISSING);
            return false;
        }

        UserObjectDocument inviter = getUser();
        if (inviter == null) {
            // inviter is guest
            setError(ERROR_RIGHTS_REQUIRED);
            return false;
        }

        InvitationObjectDocument invitation = null;
        UserObjectDocument invitee = null;

        // email address
        if (usernameOrEmail.contains("@")) {
            List<UserObjectDocument> users;
            try {
                users = usersClass.searchByField(
                        UserClass.FIELD_EMAIL,
                        usernameOrEmail);
            } catch (QueryException qe) {
                setError(ERROR_QUERY_EXCEPTION);
                return false;
            }

            if (users.size() == 1) {
                invitee = users.get(0);
            } else if (users.size() > 1) {
                // no unique mail
                setError(ERROR_INVALID_EMAIL);
                return false;
            }
        } else {
            invitee = usersClass.getDocumentObject(stringDocumentReferenceResolver.resolve(usernameOrEmail));
        }

        if (invitee != null) {
            MembersGroup membersGroup = new MembersGroup(group.getMembersGroupDocumentReference(), execution.getContext());
            // already member
            if (membersGroup.isMember(invitee.getFullName())) {
                setError(ERROR_ALREADY_MEMBER);
                return false;
            }

            // invitation pending
            if (!getInvitations(group, invitee.getFullName(), InvitationClass.FIELD_STATUS_SENT).isEmpty()) {
                // already invited
                setError(ERROR_ALREADY_INVITED);
                return false;
            }

            invitation = createInvitation(group, invitee.getFullName(), inviter.getFullName(), message);
        } else {
            // invitation pending
            if (!getInvitations(group, usernameOrEmail, InvitationClass.FIELD_STATUS_SENT).isEmpty()) {
                // already invited
                setError(ERROR_ALREADY_INVITED);
                return false;
            }

            invitation = createInvitation(group, usernameOrEmail, inviter.getFullName(), message);
        }

        return sendInvitation(invitation);
    }

    public InvitationObjectDocument createInvitation(Group group, String invitee, String inviter, String message)
        throws XWikiException
    {
        InvitationObjectDocument invitation =
                invitationClass.newDocumentObject(new DocumentReference(invitee, group.getInvitationsSpaceReference()));

        invitation.setRequestDate(new Date());
        invitation.setGroup(group.getName());
        invitation.setInvitee(invitee);
        invitation.setInviter(inviter);
        invitation.setText(message);
        // set unique random key
        invitation.setKey(RandomStringUtils.randomAlphabetic(8).toLowerCase());
        invitation.setStatus(InvitationClass.FIELD_STATUS_CREATED);

        invitationClass.saveDocumentObject(invitation);

        return invitation;
    }

    public boolean sendInvitation(InvitationObjectDocument invitation)
        throws XWikiException
    {
        if (invitation == null) {
            setError(ERROR_NULLPOINTER_EXCEPTION);
            return false;
        }

        String status = invitation.getStatus();
        if (StringUtils.equals(status, InvitationClass.FIELD_STATUS_CREATED)) {

            String emailTo = invitation.getInvitee();
            if (!StringUtils.contains(emailTo, "@")) {
                UserObjectDocument invitee =
                        usersClass.getDocumentObject(stringDocumentReferenceResolver.resolve(emailTo));
                emailTo = invitee.getEmail();
            }

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("invitation", invitation);
            if (notifyByTemplate(getDefaultMailTemplate("Invitation", "Send"), emailTo, params)) {
                invitation.setStatus(InvitationClass.FIELD_STATUS_SENT);
                invitationClass.saveDocumentObject(invitation);

                return true;
            } else {
                setError(ERROR_NULLPOINTER_EXCEPTION);
                return false;
            }
        } else {
            setError(ERROR_INVALID_STATUS);
            return false;
        }
    }

    public List<InvitationObjectDocument> getInvitations(Group group, String usernameOrEmail, String status)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        if (StringUtils.isBlank(usernameOrEmail) && StringUtils.isBlank(status)) {
            filters.put(InvitationClass.FIELD_GROUP, group.getName());
        } else if (StringUtils.isBlank(usernameOrEmail)) {
            return getInvitationsByStatus(group, status);
        } else if (StringUtils.isBlank(usernameOrEmail)) {
            return getInvitationsByInvitee(group, usernameOrEmail);
        } else {
            filters.put(InvitationClass.FIELD_GROUP, group.getName());
            filters.put(InvitationClass.FIELD_INVITEE, usernameOrEmail);
            filters.put(InvitationClass.FIELD_STATUS, status);
        }

        List<InvitationObjectDocument> invitations;
        try {
            invitations = invitationClass.searchByFields(filters);
        } catch (QueryException qe) {
            setError(ERROR_QUERY_EXCEPTION);
            return null;
        }

        return invitations;
    }

    public InvitationObjectDocument getInvitation(Group group, String usernameOrEmail, String key)
            throws XWikiException
    {
        if (StringUtils.isBlank(usernameOrEmail) && StringUtils.isBlank(key)) {
            return null;
        }

        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put(InvitationClass.FIELD_GROUP, group.getName());
        filters.put(InvitationClass.FIELD_INVITEE, usernameOrEmail);
        filters.put(InvitationClass.FIELD_KEY, key);
        filters.put(InvitationClass.FIELD_STATUS, InvitationClass.FIELD_STATUS_SENT);

        List<InvitationObjectDocument> invitations;
        try {
            invitations =  invitationClass.searchByFields(filters);
        } catch (QueryException qe) {
            setError(ERROR_QUERY_EXCEPTION);
            return null;
        }

        if (CollectionUtils.isEmpty(invitations)) {
            return null;
        }
        // TODO test size > 1. invitation should be unique
        return invitations.get(0);
    }

    public List<InvitationObjectDocument> getInvitationsByInvitee(Group group, String usernameOrEmail)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put(InvitationClass.FIELD_GROUP, group.getName());
        filters.put(InvitationClass.FIELD_INVITEE, usernameOrEmail);

        List<InvitationObjectDocument> invitations;
        try {
            invitations = invitationClass.searchByFields(filters);
        } catch (QueryException qe) {
            setError(ERROR_QUERY_EXCEPTION);
            return null;
        }
        return invitations;
    }

    public List<InvitationObjectDocument> getInvitationsByStatus(Group group, String status)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put(InvitationClass.FIELD_GROUP, group.getName());
        filters.put(InvitationClass.FIELD_STATUS, status);

        List<InvitationObjectDocument> invitations;
        try {
            invitations = invitationClass.searchByFields(filters);
        } catch (QueryException qe) {
            setError(ERROR_QUERY_EXCEPTION);
            return null;
        }

        return invitations;
    }

    public List<InvitationObjectDocument> getInvitationsSent(Group group)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put(InvitationClass.FIELD_GROUP, group.getName());
        filters.put(InvitationClass.FIELD_STATUS, InvitationClass.FIELD_STATUS_SENT);

        List<InvitationObjectDocument> invitations;
        try {
            invitations = invitationClass.searchByFields(filters);
        } catch (QueryException qe) {
            setError(ERROR_QUERY_EXCEPTION);
            return null;
        }

        return invitations;
    }

    public List<InvitationObjectDocument> getInvitationsSent(Group group, String usernameOrEmail)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put(InvitationClass.FIELD_GROUP, group.getName());
        filters.put(InvitationClass.FIELD_INVITEE, usernameOrEmail);
        filters.put(InvitationClass.FIELD_STATUS, InvitationClass.FIELD_STATUS_SENT);

        List<InvitationObjectDocument> invitations;
        try {
            invitations = invitationClass.searchByFields(filters);
        } catch (QueryException qe) {
            setError(ERROR_QUERY_EXCEPTION);
            return null;
        }

        return invitations;
    }

    public List<InvitationObjectDocument> getInvitationsRejected(Group group, String usernameOrEmail)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put(InvitationClass.FIELD_GROUP, group.getName());
        filters.put(InvitationClass.FIELD_INVITEE, usernameOrEmail);
        filters.put(InvitationClass.FIELD_STATUS, InvitationClass.FIELD_STATUS_REFUSED);

        List<InvitationObjectDocument> invitations;
        try {
            invitations = invitationClass.searchByFields(filters);
        } catch (QueryException qe) {
            setError(ERROR_QUERY_EXCEPTION);
            return null;
        }

        return invitations;
    }

    public boolean acceptInvitations(Group group, String usernameOrEmail)
            throws XWikiException
    {
        List<InvitationObjectDocument> list = new ArrayList<InvitationObjectDocument>();

        list.addAll(getInvitationsSent(group, usernameOrEmail));
        list.addAll(getInvitationsRejected(group, usernameOrEmail));
        if (!list.isEmpty()) {

            for (InvitationObjectDocument invitation : list) {
                acceptInvitation(group, invitation);
            }
            return true;
        }

        return false;
    }

    public boolean acceptInvitation(Group group, InvitationObjectDocument invitation)
            throws XWikiException
    {
        //if (XInvitation.isNew())
        //    return false;

        String status = invitation.getStatus();
        if (StringUtils.equals(status, InvitationClass.FIELD_STATUS_CREATED)
                || StringUtils.equals(status, InvitationClass.FIELD_STATUS_SENT)) {

            UserObjectDocument invitee = getUser(invitation.getInvitee());

            invitation.setStatus(InvitationClass.FIELD_STATUS_ACCEPTED);
            invitation.setResponseDate(new Date());
            invitation.setInvitee(invitee.getFullName());

            // add member
            MembersGroup membersGroup = new MembersGroup(group.getMembersGroupDocumentReference(), execution.getContext());
            if (membersGroup.isMember(invitee.getFullName())) {
                setError(ERROR_ALREADY_MEMBER);
                return false;
            }
            membersGroup.addUserOrGroup(invitee.getFullName());
            membersGroup.save();

            // create user profile
            createUserProfile(group, invitee, "Invited.", true, true);

            invitationClass.saveDocumentObject(invitation);

            // notify user by email
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("invitation", invitation);
            notifyByTemplate(getDefaultMailTemplate("Invitation", "Accept"), invitee.getEmail(), params);

            return true;
        } else {
            return false;
        }
    }

    public boolean rejectInvitation(Group group, InvitationObjectDocument invitation)
        throws XWikiException
    {
        //if (XInvitation.isNew())
        //    return false;
        String status = invitation.getStatus();
        if (StringUtils.equals(status, InvitationClass.FIELD_STATUS_CREATED)
                || StringUtils.equals(status, InvitationClass.FIELD_STATUS_SENT)) {

            invitation.setStatus(InvitationClass.FIELD_STATUS_REFUSED);
            invitation.setResponseDate(new Date());
            invitationClass.saveDocumentObject(invitation);

            UserObjectDocument invitee = getUser(invitation.getInvitee());

            // notify user by email
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("invitation", invitation);
            notifyByTemplate(getDefaultMailTemplate("Invitation", "Reject"), invitee.getEmail(), params);

            return true;
        }

        return false;
    }

    public boolean cancelInvitation(Group group, InvitationObjectDocument invitation)
            throws XWikiException
    {
        if (group == null || invitation == null) {
            setError(ERROR_NULLPOINTER_EXCEPTION);
            return false;
        }

        String status = invitation.getStatus();
        if (StringUtils.equals(status, InvitationClass.FIELD_STATUS_CREATED)
                || StringUtils.equals(status, InvitationClass.FIELD_STATUS_SENT)) {

            invitation.setStatus(InvitationClass.FIELD_STATUS_CANCELED);
            invitation.setResponseDate(new Date());
            invitationClass.saveDocumentObject(invitation);

            String invitee = invitation.getInvitee();
            if (!StringUtils.contains(invitee, "@")) {
                invitee = getUser(invitee).getEmail();
            }

            // notify user by email
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("invitation", invitation);
            notifyByTemplate(getDefaultMailTemplate("Invitation", "Cancel"), invitee, params);

            return true;
        } else {
            setError(ERROR_INVALID_STATUS);
            return false;
        }
    }

    public boolean join(Group group)
            throws XWikiException
    {
        return join(group, StringUtils.EMPTY);
    }

    public boolean join(Group group, String message)
        throws XWikiException
    {
        MembersGroup membersGroup = new MembersGroup(group.getMembersGroupDocumentReference(), execution.getContext());
        MembersGroup adminGroup = new MembersGroup(group.getAdminMembersGroupReference(), execution.getContext());

        UserObjectDocument user = getUser();
        if (user == null) {
            // TODO revise this error
            setError(ERROR_RIGHTS_REQUIRED);
            return false;
        }

        String userFullName = user.getFullName();
        if (membersGroup.isMember(userFullName)) {
            setError(ERROR_ALREADY_MEMBER);
            return false;
        }

        // if open membership, skip creation of membership request
        if (StringUtils.equals(GroupClass.FIELD_POLICY_OPEN, group.getPolicy())) {
            membersGroup.addUserOrGroup(userFullName);
            membersGroup.save();

            // create user profile
            createUserProfile(group, user, "Joined.", true, true);

            return true;
        } else {
            // has a membership request
            List<MembershipRequestObjectDocument> membershipRequests =
                    getMembershipRequests(group, userFullName, MembershipRequestClass.FIELD_STATUS_CREATED);

            if (!membershipRequests.isEmpty()) {
                // TODO revise this error
                setError(ERROR_ALREADY_INVITED);
                return false;
            }

            // create membership request
            MembershipRequestObjectDocument membershipRequest = createMembershipRequest(group, userFullName, message, new Date());
            // notify user by email
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("membershipRequest", membershipRequest);
            notifyByTemplate(getDefaultMailTemplate("Request", "Create"), user.getEmail(), params);

            return true;
        }
    }

    public MembershipRequestObjectDocument createMembershipRequest(Group group, String requester, String message, Date requestDate)
            throws XWikiException
    {
        MembershipRequestObjectDocument membershipRequest = membershipRequestClass.newDocumentObject(
                new DocumentReference(requester, group.getInvitationsSpaceReference()));
        membershipRequest.setText(message);
        membershipRequest.setRequestDate(requestDate);
        membershipRequest.setRequester(requester);
        membershipRequest.setStatus(MembershipRequestClass.FIELD_STATUS_CREATED);
        membershipRequestClass.saveDocumentObject(membershipRequest);

        return membershipRequest;
    }

    public boolean acceptMembershipRequest(Group group, MembershipRequestObjectDocument membershipRequest)
            throws XWikiException
    {
        String status = membershipRequest.getStatus();
        if (StringUtils.equals(status, MembershipRequestClass.FIELD_STATUS_CREATED)
                || StringUtils.equals(status, MembershipRequestClass.FIELD_STATUS_SENT)) {
            UserObjectDocument responder = getUser();
            String responderFullName = responder.getFullName();
            MembersGroup membersGroup = new MembersGroup(group.getMembersGroupDocumentReference(), execution.getContext());
            MembersGroup adminsGroup = new MembersGroup(group.getAdminMembersGroupReference(), execution.getContext());

            if (!adminsGroup.isMember(responderFullName)) {
                setError(ERROR_RIGHTS_REQUIRED);
                return false;
            }

            String requesterFullName = membershipRequest.getRequester();
            if (membersGroup.isMember(requesterFullName)) {
                setError(ERROR_ALREADY_MEMBER);
                return false;
            }

            UserObjectDocument requester = getUser(requesterFullName);
            //if (requester.isNew())
            //    return false;

            membersGroup.addUserOrGroup(requesterFullName);

            membershipRequest.setResponseDate(new Date());
            membershipRequest.setResponder(responderFullName);
            membershipRequest.setStatus(MembershipRequestClass.FIELD_STATUS_ACCEPTED);
            membershipRequestClass.saveDocumentObject(membershipRequest);

            createUserProfile(group, requester, "Requested.", true, true);

            // notify user by email
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("membershipRequest", membershipRequest);
            notifyByTemplate(getDefaultMailTemplate("Request", "Accept"), requester.getEmail(), params);

            return true;
        }

        return false;
    }

    public boolean notifyByTemplate(DocumentReference templateReference, String emailTo, Map<String, Object> extraParams)
            throws XWikiException
    {
        String from = wikiPreferences.getProperty(ADMIN_EMAIL_PREFERENCE_KEY, String.class);
        return mailSender.sendMailFromTemplate(templateReference, from, emailTo, StringUtils.EMPTY, StringUtils.EMPTY, extraParams);
    }

    public boolean rejectMembershipRequest(Group group, MembershipRequestObjectDocument membershipRequest)
            throws XWikiException
    {
        String status = membershipRequest.getStatus();
        if (StringUtils.equals(status, MembershipRequestClass.FIELD_STATUS_CREATED)
                || StringUtils.equals(status, MembershipRequestClass.FIELD_STATUS_SENT)) {

            UserObjectDocument responder = getUser();
            String responderFullName = responder.getFullName();

            MembersGroup adminsGroup = new MembersGroup(group.getAdminMembersGroupReference(), execution.getContext());
            if (!adminsGroup.isMember(responderFullName)) {
                setError(ERROR_RIGHTS_REQUIRED);
                return false;
            }

            UserObjectDocument requester = getUser(membershipRequest.getRequester());

            membershipRequest.setResponseDate(new Date());
            membershipRequest.setResponder(responderFullName);
            membershipRequest.setStatus(MembershipRequestClass.FIELD_STATUS_REFUSED);
            membershipRequestClass.saveDocumentObject(membershipRequest);

            // notify user by email
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("membershipRequest", membershipRequest);
            notifyByTemplate(getDefaultMailTemplate("Request", "Reject"), requester.getEmail(), params);

            return true;
        }

        return false;
    }

    public boolean cancelMembershipRequest(Group group, MembershipRequestObjectDocument membershipRequest)
            throws XWikiException
    {
        String status = membershipRequest.getStatus();
        if (StringUtils.equals(status, MembershipRequestClass.FIELD_STATUS_CREATED)
                || StringUtils.equals(status, MembershipRequestClass.FIELD_STATUS_SENT)) {

            UserObjectDocument responder = getUser();
            String responderFullName = responder.getFullName();

            MembersGroup adminsGroup = new MembersGroup(group.getAdminMembersGroupReference(), execution.getContext());
            if (!adminsGroup.isMember(responderFullName)) {
                setError(ERROR_RIGHTS_REQUIRED);
                return false;
            }

            membershipRequest.setResponseDate(new Date());
            membershipRequest.setResponder(responderFullName);
            membershipRequest.setStatus(MembershipRequestClass.FIELD_STATUS_CANCELED);
            membershipRequestClass.saveDocumentObject(membershipRequest);

            return true;
        }

        return false;
    }

    public MembershipRequestObjectDocument getMembershipRequest(Group group, String userName)
            throws XWikiException
    {
        //String userFullName = user.getFullName();
        List<MembershipRequestObjectDocument> membershipRequests =
                getMembershipRequests(group, userName, MembershipRequestClass.FIELD_STATUS_CREATED);

        MembershipRequestObjectDocument membershipRequest = null;
        if (!membershipRequests.isEmpty()) {
            membershipRequest = membershipRequests.get(0);
        }

        return membershipRequest;
    }

    public List<MembershipRequestObjectDocument> getMembershipRequests(Group group)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put(MembershipRequestClass.FIELD_GROUP, group.getName());
        filters.put(MembershipRequestClass.FIELD_STATUS, MembershipRequestClass.FIELD_STATUS_CREATED);

        List<MembershipRequestObjectDocument> membershipRequests;
        try {
            membershipRequests = membershipRequestClass.searchByFields(filters);
        } catch (QueryException qe) {
            setError(ERROR_QUERY_EXCEPTION);
            return null;
        }

        return membershipRequests;
    }

    public List<MembershipRequestObjectDocument> getMembershipRequestsByStatus(Group group, String status)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put(MembershipRequestClass.FIELD_GROUP, group.getName());
        filters.put(MembershipRequestClass.FIELD_STATUS, status);

        List<MembershipRequestObjectDocument> membershipRequests;
        try {
            membershipRequests = membershipRequestClass.searchByFields(filters);
        } catch (QueryException qe) {
            setError(ERROR_QUERY_EXCEPTION);
            return null;
        }

        return membershipRequests;
    }

    public List<MembershipRequestObjectDocument> getMembershipRequests(Group group, String requester, String status)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();

        filters.put(MembershipRequestClass.FIELD_GROUP, group.getName());
        filters.put(MembershipRequestClass.FIELD_REQUESTER, requester);
        filters.put(MembershipRequestClass.FIELD_STATUS, MembershipRequestClass.FIELD_STATUS_CREATED);

        List<MembershipRequestObjectDocument> membershipRequests;
        try {
            membershipRequests = membershipRequestClass.searchByFields(filters);
        } catch (QueryException qe) {
            setError(ERROR_QUERY_EXCEPTION);
            return null;
        }

        return membershipRequests;
    }

    public UserObjectDocument getUser() throws XWikiException
    {
        return this.usersClass.getDocumentObject(getContext().getUserReference());
    }

    public UserObjectDocument getUser(String userNameOrEmail)
            throws XWikiException
    {
        if (StringUtils.contains(userNameOrEmail, "@")) {
            List<UserObjectDocument> users;
            try {
                users = usersClass.searchByField(UserClass.FIELD_EMAIL, userNameOrEmail);
            } catch (QueryException qe) {
                setError(ERROR_QUERY_EXCEPTION);
                return null;
            }
            if (CollectionUtils.isNotEmpty(users)) {
                return users.get(0);
            } else {
                setError(ERROR_INVALID_EMAIL);
                return null;
            }
        }

        return usersClass.getDocumentObject(stringDocumentReferenceResolver.resolve(userNameOrEmail));
    }

    public UserProfileObjectDocument createUserProfile(Group group, UserObjectDocument user, String profile, boolean allowNotifications, boolean allowNotificationsFromSelf)
            throws XWikiException
    {
        if (group == null || user == null) {
            // TODO setError
            return null;
        }

        UserProfileObjectDocument userProfile = userProfileClass.newDocumentObject(
                new DocumentReference(user.getFullName(), group.getUserProfilesSpaceReference()));
        if (userProfile == null) {
            // TODO setError
            return null;
        }
        userProfile.setProfile(profile);
        userProfile.setAllowNotifications(allowNotifications);
        userProfile.setAllowNotificationsFromSelf(allowNotificationsFromSelf);
        userProfileClass.saveDocumentObject(userProfile);

        return userProfile;
    }

    public UserProfileObjectDocument getUserProfile(Group group, UserObjectDocument user)
            throws XWikiException
    {
        if (group == null || user == null) {
            // TODO setError
            return null;
        }

        UserProfileObjectDocument userProfile = userProfileClass.getDocumentObject(
                new DocumentReference(user.getFullName(), group.getUserProfilesSpaceReference()));

        return userProfile;
    }

    public boolean isAdmin(Group group)
            throws XWikiException
    {
        return isAdmin(group, getUser());
    }

    public boolean isAdmin(Group group, UserObjectDocument user)
            throws XWikiException
    {
        if (user == null) {
            return false;
        }

        String userFullName = user.getFullName();
        MembersGroup adminGroup = new MembersGroup(group.getAdminMembersGroupReference(), execution.getContext());

        return adminGroup.isMember(userFullName);
    }

    public boolean isMember(Group group)
            throws XWikiException
    {
        return isMember(group, getUser());
    }

    public boolean isMember(Group group, UserObjectDocument user)
            throws XWikiException
    {
        if (user == null) {
            return false;
        }

        String userFullName = user.getFullName();
        MembersGroup membersGroup = new MembersGroup(group.getMembersGroupDocumentReference(), execution.getContext());

        return membersGroup.isMember(userFullName);
    }

    public List<String> getMemberNames(Group group) throws XWikiException
    {
        MembersGroup membersGroup = new MembersGroup(group.getMembersGroupDocumentReference(), execution.getContext());
        return membersGroup.getAllMembers();
    }

    public boolean addMember(Group group, String userName) throws XWikiException
    {
        MembersGroup membersGroup = new MembersGroup(group.getMembersGroupDocumentReference(), execution.getContext());
        if (!membersGroup.isMember(userName)) {
            membersGroup.addUserOrGroup(userName);
            membersGroup.save();

            return true;
        }
        return false;
    }

    public List<MemberGroupObjectDocument> getMemberGroups(Group group)
            throws XWikiException
    {
        List<MemberGroupObjectDocument> memberGroups;
        try {
            memberGroups = memberGroupClass.searchByField("_space", group.getGroupSpaceReference().getName());
        } catch (QueryException qe) {
            setError(ERROR_QUERY_EXCEPTION);
            return null;
        }

        return memberGroups;
    }

    public List<MemberGroupObjectDocument> getMemberGroups(Group group, String userName)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put("_space", group.getGroupSpaceReference().getName());
        filters.put("XWiki.XWikiGroups_member", userName);

        List<MemberGroupObjectDocument> memberGroups;
        try {
            memberGroups = memberGroupClass.searchByFields(filters);
        } catch (QueryException qe) {
            setError(ERROR_QUERY_EXCEPTION);
            return null;
        }
        return memberGroups;
    }

    public MemberGroupObjectDocument getMemberGroupForRole(Group group, String role)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put("_space", group.getGroupSpaceReference().getName());
        filters.put("role", role);

        List<MemberGroupObjectDocument> memberGroups;
        try {
            memberGroups = memberGroupClass.searchByFields(filters);
        } catch (QueryException qe) {
            setError(ERROR_QUERY_EXCEPTION);
            return null;
        }

        if (CollectionUtils.isEmpty(memberGroups)) {
            setError(ERROR_DOCUMENTOBJECT_NOT_FOUND);
            return null;
        }

        return memberGroups.get(0);
    }

    public boolean addMemberRole(Group group, String userName, String role) throws XWikiException
    {
        if (!StringUtils.equals(role, "admin")) {
            return false;
        }

        MembersGroup membersGroup = new MembersGroup(group.getMembersGroupDocumentReference(), execution.getContext());
        MembersGroup adminsGroup = new MembersGroup(group.getAdminMembersGroupReference(), execution.getContext());

        if (!membersGroup.isMember(userName)) {
            return false;
        }

        if (adminsGroup.isMember(userName)) {
            return false;
        }

        adminsGroup.addUserOrGroup(userName);
        adminsGroup.save();

        return true;
    }

    private MembersGroup getMemberGroup(Group group)
            throws XWikiException
    {
        return new MembersGroup(group.getMembersGroupDocumentReference(), execution.getContext());
    }

    private MembersGroup getAdminsGroup(Group group)
            throws XWikiException
    {
        return new MembersGroup(group.getAdminMembersGroupReference(), execution.getContext());
    }

    public boolean removeMember(Group group, String userName) throws XWikiException
    {
        MembersGroup membersGroup = getMemberGroup(group);
        MembersGroup adminsGroup = getAdminsGroup(group);

        if (adminsGroup.isMember(userName)) {
            adminsGroup.removeUserOrGroup(userName);
            adminsGroup.save();
        }

        if (membersGroup.isMember(userName)) {
            membersGroup.removeUserOrGroup(userName);
            membersGroup.save();

            // delete user profile if present
            UserProfileObjectDocument userProfile = getUserProfile(group, getUser(userName));
            if (userProfile != null) {
                userProfile.delete();
            }
        }

        // TODO return false in case the user doesn't exists or wasn't a member
        return true;
    }

    public boolean removeMemberRole(Group group, String userName, String role) throws XWikiException
    {
        if (!StringUtils.equals(role, "admin")) {
            return false;
        }

        MembersGroup membersGroup = new MembersGroup(group.getMembersGroupDocumentReference(), execution.getContext());
        MembersGroup adminsGroup = new MembersGroup(group.getAdminMembersGroupReference(), execution.getContext());

        if (!membersGroup.isMember(userName)) {
            return false;
        }

        if (!adminsGroup.isMember(userName)) {
            return false;
        }

        adminsGroup.removeUserOrGroup(userName);
        adminsGroup.save();

        return true;
    }

    public boolean addMember(Group group, UserObjectDocument user)
            throws XWikiException
    {
        String userFullName = user.getFullName();
        MembersGroup adminGroup = new MembersGroup(group.getAdminMembersGroupReference(), execution.getContext());
        MembersGroup memberGroup = new MembersGroup(group.getMembersGroupDocumentReference(), execution.getContext());
        if (StringUtils.isNotBlank(userFullName)
                && adminGroup.isMember(getUser().getFullName())
                && !memberGroup.isMember(userFullName)) {
            memberGroup.addUserOrGroup(userFullName);
            memberGroup.save();

            return true;
        }

        return false;
    }

    public boolean removeMember(Group group, UserObjectDocument user)
            throws XWikiException
    {
        String userFullName = user.getFullName();
        MembersGroup adminGroup = new MembersGroup(group.getAdminMembersGroupReference(), execution.getContext());
        MembersGroup memberGroup = new MembersGroup(group.getMembersGroupDocumentReference(), execution.getContext());
        if (StringUtils.isNotBlank(userFullName)
                && adminGroup.isMember(getUser().getFullName())
                && memberGroup.isMember(userFullName)) {
            memberGroup.removeUserOrGroup(userFullName);
            memberGroup.save();

            return true;
        }
        return false;
    }

    public String getLastError()
    {
        return (String) execution.getContext().getProperty(ERROR_KEY);
    }

    private void setError(String error)
    {
        execution.getContext().setProperty(ERROR_KEY, error);
    }
}
