package org.xwiki.sankore;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.query.QueryException;
import org.xwiki.sankore.internal.InvitationObjectDocument;
import org.xwiki.sankore.internal.MemberGroupObjectDocument;
import org.xwiki.sankore.internal.MembershipRequestObjectDocument;
import org.xwiki.sankore.internal.UserObjectDocument;
import org.xwiki.sankore.internal.UserProfileObjectDocument;
import org.xwiki.script.service.ScriptService;

import com.xpn.xwiki.XWikiException;

@Component
@Named("membershipmgr")
@Singleton
public class MembershipManagerScriptService implements ScriptService
{
    @Inject
    private Logger logger;

    @Inject
    private MembershipManager membershipManager;

    public UserObjectDocument getUser() throws XWikiException
    {
        return this.membershipManager.getUser();
    }

    public UserObjectDocument getUser(String userName) throws XWikiException
    {
        return this.membershipManager.getUser(userName);
    }

    public UserProfileObjectDocument getUserProfile(Group group, UserObjectDocument user) throws XWikiException
    {
        return this.membershipManager.getUserProfile(group, user);
    }

    public List<InvitationObjectDocument> getInvitationsSent(Group group)
            throws XWikiException
    {
        return this.membershipManager.getInvitationsSent(group);
    }

    public List<MembershipRequestObjectDocument> getMembershipRequests(Group group)
            throws XWikiException
    {
        return this.membershipManager.getMembershipRequests(group);
    }

    public List<MembershipRequestObjectDocument> getMembershipRequestsByStatus(Group group, String status)
            throws XWikiException
    {
        return membershipManager.getMembershipRequestsByStatus(group, status);
    }

    public boolean invite(Group group, String usernameOrEmail, String message)
            throws XWikiException
    {
        return this.membershipManager.invite(group, usernameOrEmail, message);
    }

    public InvitationObjectDocument getInvitation(Group group, String usernameOrEmail, String invitationKey)
            throws XWikiException
    {
        return this.membershipManager.getInvitation(group, usernameOrEmail, invitationKey);
    }

    public boolean acceptInvitation(Group group, InvitationObjectDocument invitation)
            throws XWikiException
    {
        return this.membershipManager.acceptInvitation(group, invitation);
    }

    public boolean rejectInvitation(Group group, InvitationObjectDocument invitation)
            throws XWikiException
    {
        return this.membershipManager.rejectInvitation(group, invitation);
    }

    public boolean cancelInvitation(Group group, InvitationObjectDocument invitation)
            throws XWikiException
    {
        return this.membershipManager.cancelInvitation(group, invitation);
    }

    // current user join group
    public boolean join(Group group) throws XWikiException
    {
        return this.membershipManager.join(group);
    }

    public boolean join(Group group, String message) throws XWikiException
    {
        return this.membershipManager.join(group, message);
    }

    public MembershipRequestObjectDocument getMembershipRequest(Group group, String userName)
            throws XWikiException
    {
        return this.membershipManager.getMembershipRequest(group, userName);
    }

    public boolean acceptMembershipRequest(Group group, MembershipRequestObjectDocument membershipRequest)
            throws XWikiException
    {
        return this.membershipManager.acceptMembershipRequest(group, membershipRequest);
    }

    public boolean rejectMembershipRequest(Group group, MembershipRequestObjectDocument membershipRequest)
            throws XWikiException
    {
        return this.membershipManager.rejectMembershipRequest(group, membershipRequest);
    }

    public boolean cancelMembershipRequest(Group group, MembershipRequestObjectDocument membershipRequest)
            throws XWikiException
    {
        return this.membershipManager.cancelMembershipRequest(group, membershipRequest);
    }

    public boolean isAdmin(Group group)
            throws XWikiException
    {
        return this.membershipManager.isAdmin(group);
    }

    public boolean isAdmin(Group group, String userName)
            throws XWikiException
    {
        UserObjectDocument user = this.membershipManager.getUser(userName);
        if (user != null) {
            return this.membershipManager.isAdmin(group, user);
        }

        return false;
    }

    public boolean isMember(Group group)
            throws XWikiException
    {
        return this.membershipManager.isMember(group);
    }

    public boolean isMember(Group group, String userName)
            throws XWikiException
    {
        UserObjectDocument user = this.membershipManager.getUser(userName);
        if (user != null) {
            return this.membershipManager.isMember(group, user);
        }

        return false;
    }

    public List<String> getMemberNames(Group group)
            throws XWikiException
    {
        return this.membershipManager.getMemberNames(group);
    }

    public boolean addMember(Group group, String userName)
            throws XWikiException
    {
        return this.membershipManager.addMember(group, userName);
    }

    public boolean addMemberRole(Group group, String userName, String role)
            throws XWikiException
    {
        return this.membershipManager.addMemberRole(group, userName, role);
    }

    public boolean removeMember(Group group, String userName)
            throws XWikiException
    {
        return this.membershipManager.removeMember(group, userName);
    }

    public boolean removeMemberRole(Group group, String userName, String role)
            throws XWikiException
    {
        return this.membershipManager.removeMemberRole(group, userName, role);
    }

    public String getLastError()
    {
        return this.membershipManager.getLastError();
    }

    public List<MemberGroupObjectDocument> getMemberGroups(Group group)
            throws XWikiException
    {
        return membershipManager.getMemberGroups(group);
    }

    public List<MemberGroupObjectDocument> getMemberGroups(Group group, String userName)
            throws XWikiException
    {
        return membershipManager.getMemberGroups(group, userName);
    }

    public MemberGroupObjectDocument getMemberGroupForRole(Group group, String role)
            throws XWikiException
    {
        return membershipManager.getMemberGroupForRole(group, role);
    }
}
