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
import org.xwiki.query.QueryManager;
import org.xwiki.sankore.internal.ClassManager;
import org.xwiki.sankore.internal.GroupClass;
import org.xwiki.sankore.internal.InvitationClass;
import org.xwiki.sankore.internal.InvitationObjectDocument;
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

    public static final String DEFAULT_RESOURCES_SPACE = "Groups";
    public static final String DEFAULT_MAILTEMPLATE_NAME = "MailTemplate";

    private XWikiContext getContext()
    {
        return (XWikiContext) this.execution.getContext().getProperty("xwikicontext");
    }

    private DocumentReference getDefaultMailTemplate(String type, String action)
    {
        String templateName = DEFAULT_MAILTEMPLATE_NAME + StringUtils.capitalize(action) + StringUtils.capitalize(type);
        EntityReference templateReference = new EntityReference(templateName, EntityType.DOCUMENT, new EntityReference(DEFAULT_RESOURCES_SPACE, EntityType.SPACE));

        return currentReferenceDocumentReferenceResolver.resolve(templateReference);
    }

    private boolean isRegisteredUser(String usernameOrEmail) throws XWikiException
    {
        /*
        usernameOrEmail = usernameOrEmail.trim();
        if (!isEmailAddress(usernameOrEmail)) {
            if (StringUtils.isEmpty(usernameOrEmail))
                return false;

            XWikiContext xWikiContext = ContextUtils.getXWikiContext(this.execution.getContext());

            UserXObjectDocument UserXObjectDocument = XWikiUsersClass.getInstance(xWikiContext)
                    .newXObjectDocument(usernameOrEmail, xWikiContext);

            if (UserXObjectDocument.isNew()) {
                    return false;
            }

            return true;
        }

        XWikiUsersClass.getInstance(execution.getContext()).searchXObjectDocumentsByField("email", usernameOrEmail, "String", )

        // email address
        List<String> results = new ArrayList<String>();

        try {
            results = this.queryManager.createQuery("select distinct doc.fullName from XWikiDocument as doc, BaseObject as obj, StringProperty as prop where doc.space='" + UserXObjectDocument.DEFAULT_USERS_SPACE + "'" + " and obj.name=doc.fullName and obj.className='" + XWikiUsersClass.getInstance(this.execution).getClassFullName() + "' and prop.id.id=obj.id and prop.name='" + XWikiUsersClass.FIELD_EMAIL + "' and prop.value='" + usernameOrEmail + "'", "xwql")
                    .setLimit(1).setOffset(0).execute();
        } catch (QueryException qe) {
        }

        if (!results.isEmpty()) {
             return true;
        }  */

        return false;
    }

    public boolean invite(Group group, String usernameOrEmail, String message) throws XWikiException
    {
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
            List<UserObjectDocument> users = usersClass.searchDocumentObjectsByField(
                    UserClass.FIELD_EMAIL,
                    usernameOrEmail);

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
        boolean sent = false;
        logger.info("sendInvitation() status: " + invitation.getStatus());
        if (invitation.getStatus().equals(InvitationClass.FIELD_STATUS_CREATED)) {

            String from = wikiPreferences.getProperty(ADMIN_EMAIL_PREFERENCE_KEY, String.class);

            String emailTo = invitation.getInvitee();
            if (!StringUtils.contains(emailTo, "@")) {
                UserObjectDocument invitee =
                        usersClass.getDocumentObject(stringDocumentReferenceResolver.resolve(emailTo));
                emailTo = invitee.getEmail();
            }

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("invitation", invitation);
            sent = mailSender.sendMailFromTemplate(
                    getDefaultMailTemplate("Invitation", "Send"),
                    from,
                    emailTo,
                    null,
                    null,
                    params);

            logger.info("Mail send from:" + from + " to:" + emailTo + " template:" +
                    getDefaultMailTemplate("Invitation", "Send"));
            logger.info("LOGGER: " + invitation.getStatus());
            if (sent) {
                invitation.setStatus(InvitationClass.FIELD_STATUS_SENT);
                invitationClass.saveDocumentObject(invitation);
                logger.info("LOGGER: " + invitation.getStatus());
                logger.info("Mail sent from:" + from + " to:" + emailTo + " template:" +
                        getDefaultMailTemplate("Invitation", "Send"));
            }
        }


        return sent;
    }

    /*
    public boolean verifyInvitation(String usernameOrEmail, String code)
            throws XWikiException
    {
        Object[][] filters = new Object[][]{
                {InvitationClass.FIELD_INVITEE, InvitationClass.FIELDT_INVITEE, usernameOrEmail},
                {InvitationClass.FIELD_KEY, InvitationClass.FIELDT_KEY, code},
                {InvitationClass.FIELD_STATUS, InvitationClass.FIELDT_STATUS, InvitationClass.FIELD_STATUS_SENT}};
        List<Invitation> list = InvitationClass.getInstance(execution.getContext()).searchXObjectDocumentsByFields(
                filters);
        if (!list.isEmpty()) {

            for (Invitation invitation : list) {
                acceptInvitation(invitation);
            }

            return true;
        }

        return false;
    }*/

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

        return invitationClass.searchDocumentObjectsByFields(filters);
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

        List<InvitationObjectDocument> invitations =  invitationClass.searchDocumentObjectsByFields(filters);
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

        return invitationClass.searchDocumentObjectsByFields(filters);
    }

    public List<InvitationObjectDocument> getInvitationsByStatus(Group group, String status)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put(InvitationClass.FIELD_GROUP, group.getName());
        filters.put(InvitationClass.FIELD_STATUS, status);

        return invitationClass.searchDocumentObjectsByFields(filters);
    }

    public List<InvitationObjectDocument> getInvitationsSent(Group group)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put(InvitationClass.FIELD_GROUP, group.getName());
        filters.put(InvitationClass.FIELD_STATUS, InvitationClass.FIELD_STATUS_SENT);

        return invitationClass.searchDocumentObjectsByFields(filters);
    }

    public List<InvitationObjectDocument> getInvitationsSent(Group group, String usernameOrEmail)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put(InvitationClass.FIELD_GROUP, group.getName());
        filters.put(InvitationClass.FIELD_INVITEE, usernameOrEmail);
        filters.put(InvitationClass.FIELD_STATUS, InvitationClass.FIELD_STATUS_SENT);

        return invitationClass.searchDocumentObjectsByFields(filters);
    }

    public List<InvitationObjectDocument> getInvitationsRejected(Group group, String usernameOrEmail)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put(InvitationClass.FIELD_GROUP, group.getName());
        filters.put(InvitationClass.FIELD_INVITEE, usernameOrEmail);
        filters.put(InvitationClass.FIELD_STATUS, InvitationClass.FIELD_STATUS_REFUSED);

        return invitationClass.searchDocumentObjectsByFields(filters);
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
        //if (XInvitation.isNew())
        //    return false;

        String status = invitation.getStatus();
        if (StringUtils.equals(status, InvitationClass.FIELD_STATUS_CREATED)
                || StringUtils.equals(status, InvitationClass.FIELD_STATUS_SENT)) {

            invitation.setStatus(InvitationClass.FIELD_STATUS_CANCELED);
            invitation.setResponseDate(new Date());
            invitationClass.saveDocumentObject(invitation);

            UserObjectDocument invitee = getUser(invitation.getInvitee());

            // notify user by email
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("invitation", invitation);
            notifyByTemplate(getDefaultMailTemplate("Invitation", "Cancel"), invitee.getEmail(), params);

            return true;
        }

        return false;
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

        return membershipRequestClass.searchDocumentObjectsByFields(filters);
    }

    public List<MembershipRequestObjectDocument> getMembershipRequestsByStatus(Group group, String status)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();
        filters.put(MembershipRequestClass.FIELD_GROUP, group.getName());
        filters.put(MembershipRequestClass.FIELD_STATUS, status);

        return membershipRequestClass.searchDocumentObjectsByFields(filters);
    }

    public List<MembershipRequestObjectDocument> getMembershipRequests(Group group, String requester, String status)
            throws XWikiException
    {
        Map<String, Object> filters = new HashMap<String, Object>();

        filters.put(MembershipRequestClass.FIELD_GROUP, group.getName());
        filters.put(MembershipRequestClass.FIELD_REQUESTER, requester);
        filters.put(MembershipRequestClass.FIELD_STATUS, MembershipRequestClass.FIELD_STATUS_CREATED);

        return membershipRequestClass.searchDocumentObjectsByFields(filters);
    }

    public UserObjectDocument getUser() throws XWikiException
    {
        return this.usersClass.getDocumentObject(getContext().getUserReference());
    }

    public UserObjectDocument getUser(String userNameOrEmail) throws XWikiException
    {
        if (StringUtils.contains(userNameOrEmail, "@")) {
            List<UserObjectDocument> users = usersClass.searchDocumentObjectsByField(UserClass.FIELD_EMAIL, userNameOrEmail);
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
        String userFullName = user.getFullName();
        UserProfileObjectDocument userProfile = userProfileClass.newDocumentObject(
                new DocumentReference(userFullName, group.getUserProfilesSpaceReference()));
        userProfile.setProfile(profile);
        userProfile.setAllowNotifications(allowNotifications);
        userProfile.setAllowNotificationsFromSelf(allowNotificationsFromSelf);
        userProfileClass.saveDocumentObject(userProfile);

        return userProfile;
    }

    public UserProfileObjectDocument getUserProfile(Group group, UserObjectDocument user) throws XWikiException
    {
        String userFullName = user.getFullName();
        logger.info("getUserProfile: " + userFullName);
        UserProfileObjectDocument userProfile = this.userProfileClass.getDocumentObject(
                new DocumentReference(userFullName, group.getUserProfilesSpaceReference()));
        if (userProfile == null) {
            userProfile = this.userProfileClass.newDocumentObject(
                    new DocumentReference(userFullName, group.getUserProfilesSpaceReference()));
        }

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

    public boolean removeMember(Group group, String userName) throws XWikiException
    {
        MembersGroup membersGroup = new MembersGroup(
                new DocumentReference("MemberGroup", group.getGroupSpaceReference()), execution.getContext());
        MembersGroup adminsGroup = new MembersGroup(
                new DocumentReference("AdminGroup", group.getGroupSpaceReference()), execution.getContext());

        if (adminsGroup.isMember(userName)) {
            adminsGroup.removeUserOrGroup(userName);
            adminsGroup.save();
        }

        if (membersGroup.isMember(userName)) {
            membersGroup.removeUserOrGroup(userName);
            membersGroup.save();

            // delete user profile
            UserProfileObjectDocument userProfile = this.userProfileClass.getDocumentObject(
                    new DocumentReference(userName, group.getUserProfilesSpaceReference()));
            if (userProfile == null) {
                logger.info("USER PROFILE: null");
            } else {
                logger.info("USER PROFILE: " + userProfile.getDocumentReference().toString());
                userProfile.delete();
            }
        }

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
