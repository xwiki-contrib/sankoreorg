package org.xwiki.sankore;

import java.util.List;

import org.xwiki.component.annotation.ComponentRole;
import org.xwiki.sankore.internal.InvitationObjectDocument;
import org.xwiki.sankore.internal.MembershipRequestObjectDocument;
import org.xwiki.sankore.internal.UserObjectDocument;
import org.xwiki.sankore.internal.UserProfileObjectDocument;

import com.xpn.xwiki.XWikiException;

@ComponentRole
public interface MembershipManager
{
    public UserObjectDocument getUser() throws XWikiException;
    public UserObjectDocument getUser(String userName) throws XWikiException;
    public UserProfileObjectDocument getUserProfile(Group group, UserObjectDocument user) throws XWikiException;

    public boolean join(Group group) throws XWikiException;
    public boolean join(Group group, String message) throws XWikiException;
    public MembershipRequestObjectDocument getMembershipRequest(Group group, String userName) throws XWikiException;
    public List<MembershipRequestObjectDocument> getMembershipRequests(Group group) throws XWikiException;
    public List<MembershipRequestObjectDocument> getMembershipRequestsByStatus(Group group, String status) throws XWikiException;
    public boolean acceptMembershipRequest(Group group, MembershipRequestObjectDocument membershipRequest) throws XWikiException;
    public boolean rejectMembershipRequest(Group group, MembershipRequestObjectDocument membershipRequest) throws XWikiException;
    public boolean cancelMembershipRequest(Group group, MembershipRequestObjectDocument membershipRequest) throws XWikiException;

    public List<InvitationObjectDocument> getInvitations(Group group, String usernameOrEmail, String status)
            throws XWikiException;

    public List<MembershipRequestObjectDocument> getMembershipRequests(Group group, String username, String status)
            throws XWikiException;

    public boolean invite(Group group, String usernameOrEmail, String message) throws XWikiException;

    public List<InvitationObjectDocument> getInvitationsSent(Group group) throws XWikiException;
    public InvitationObjectDocument getInvitation(Group group, String usernameOrEmail, String key) throws XWikiException;
    public boolean acceptInvitation(Group group, InvitationObjectDocument invitation) throws XWikiException;
    public boolean rejectInvitation(Group group, InvitationObjectDocument invitation) throws XWikiException;
    public boolean cancelInvitation(Group group, InvitationObjectDocument invitation) throws XWikiException;

    public boolean isAdmin(Group group) throws XWikiException;
    public boolean isMember(Group group) throws XWikiException;
    public boolean isAdmin(Group group, UserObjectDocument user) throws XWikiException;
    public boolean isMember(Group group, UserObjectDocument user) throws XWikiException;

    public List<String> getMemberNames(Group group) throws XWikiException;

    public boolean addMember(Group group,String userName) throws XWikiException;
    public boolean addMemberRole(Group group, String userName, String role) throws XWikiException;
    public boolean removeMember(Group group, String userName) throws XWikiException;
    public boolean removeMemberRole(Group group, String userName, String role) throws XWikiException;

    public String getLastError();
}
