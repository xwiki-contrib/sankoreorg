package org.xwiki.sankore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.sankore.internal.ClassManager;
import org.xwiki.sankore.internal.GroupObjectDocument;
import org.xwiki.sankore.internal.UserObjectDocument;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;

@Component
@Singleton
public class DefaultGroupManager implements GroupManager
{
    @Inject
    private Logger logger;

    @Inject
    private Execution execution;

    @Inject
    SpaceManager spaceManager;

    @Inject
    @Named("UsersClass")
    ClassManager<UserObjectDocument> usersClass;

    @Inject
    @Named("GroupClass")
    ClassManager<GroupObjectDocument> groupClass;

    @Inject
    MembershipManager membershipManager;

    @Inject
    private DocumentReferenceResolver<String> stringDocumentReferenceResolver;

    @Inject
    @Named("local")
    private EntityReferenceSerializer<String> referenceSerializer;

    @Inject
    @Named("current/reference")
    private DocumentReferenceResolver<EntityReference> currentReferenceDocumentReferenceResolver;

    public Group getGroup(String groupName) throws XWikiException
    {
        if (groupName.startsWith("Group_")) {
            return new Group(groupClass.getDocumentObject(
                    stringDocumentReferenceResolver.resolve(groupName + ".WebPreferences")),
                    execution.getContext());
        } else if (groupName.startsWith("Messages_Group_")) {
            return new Group(groupClass.getDocumentObject(
                    stringDocumentReferenceResolver.resolve(groupName.replaceFirst("Messages_", "") + ".WebPreferences")),
                    execution.getContext());
        }

        return new Group(groupClass.getDocumentObject(
                stringDocumentReferenceResolver.resolve("Group_" + groupName + ".WebPreferences")),
                execution.getContext());
    }

    public Group createGroup(String groupName) throws XWikiException
    {
        String groupSpaceName = groupName;
        if (!groupName.startsWith("Group_")) {
            groupSpaceName = "Group_" + groupName;
        }

        GroupObjectDocument groupObjectDocument = groupClass.newDocumentObject(
                stringDocumentReferenceResolver.resolve(groupSpaceName));
        Group group = new Group(groupObjectDocument, execution.getContext());

        //if (!group.isNew() || this.spaceManager.countDocuments(group.getGroupSpace().getSpaceReference()) == 0) {
        //    return null;
        //}

        groupClass.saveDocumentObject(groupObjectDocument);

        return group;
    }

    public Group createGroupFromRequest() throws XWikiException
    {
        /*
        XWikiRequest xWikiRequest = ContextUtils.getXWikiRequest(execution.getContext());
        String groupName = xWikiRequest.get("groupName");
        String templateSpaceName = xWikiRequest.get("template");

        if (StringUtils.isEmpty(groupName))
            return null;

        Group group = null;
        if (StringUtils.isEmpty(templateSpaceName))
        {
            group = this.createGroup(groupName);
        } else {
            group = this.createGroupFromTemplate(groupName, templateSpaceName);
        }

        group.updateFromRequest();
        */

        return null;
    }

    public Group updateGroupFromRequest() throws XWikiException
    {
        /*
        XWikiRequest xWikiRequest = ContextUtils.getXWikiRequest(execution.getContext());
        String groupName = xWikiRequest.get("groupName");

        if (StringUtils.isEmpty(groupName))
            return null;

        Group group = this.getGroup(groupName);
        if (group.isNew()) {
            return null;
        }
        group.updateFromRequest();
        */
        return null;
    }

    public Group createGroupFromTemplate(String groupName, String templateName) throws XWikiException
    {
        String groupSpaceName = StringUtils.EMPTY;

        if (groupName.startsWith("Group_")) {
            groupSpaceName = groupName;
        } else {
            groupSpaceName = "Group_" + groupName;
        }

        GroupObjectDocument groupObjectDocument = groupClass.newDocumentObject(
                stringDocumentReferenceResolver.resolve(groupSpaceName + ".WebPreferences"));
        Group group = new Group(groupObjectDocument, execution.getContext());
        //if (!group.isNew()) {
        //    return null;
        //}

        //Space groupSpace = group.getGroupSpace();
        //this.spaceManager.copySpace(this.spaceManager.createSpaceReference(groupSpace.getWiki(), templateName),
        //        groupSpace.getSpaceReference(), true);

        this.spaceManager.copySpace(
                new SpaceReference(templateName, new WikiReference(group.getGroupSpaceReference().getParent())),
                group.getGroupSpaceReference(),
                true);

        group.save();

        this.membershipManager.addMember(group, this.membershipManager.getUser().getFullName());
        this.membershipManager.addMemberRole(group, this.membershipManager.getUser().getFullName(), "admin");

        return group;
    }

    protected void addUserToMemberGroup(String username, String membergroupName) throws XWikiException
    {
        XWiki xwiki = ContextUtils.getXWiki(execution.getContext());
        XWikiContext xwikiContext = ContextUtils.getXWikiContext(execution.getContext());

        xwiki.getGroupService(xwikiContext).addUserToGroup(username, xwiki.getDatabase(), membergroupName, xwikiContext);
    }

    protected boolean isUserInMemberGroup(String username, String membergroupName) throws XWikiException
    {
        XWiki xwiki = ContextUtils.getXWiki(execution.getContext());
        XWikiContext xwikiContext = ContextUtils.getXWikiContext(execution.getContext());

        Collection<String> memberNames = xwiki.getGroupService(xwikiContext).getAllMembersNamesForGroup(membergroupName, 0, 0, xwikiContext);
        return memberNames.contains(username);
    }

    public List<String> getGroupNamesFor(String userName)
            throws XWikiException
    {
        List<DocumentReference> references = getContext().getWiki().getStore()
                .searchDocumentReferences(
                        ", BaseObject as obj, StringProperty as prop where doc.name='MemberGroup' and obj.name=doc.fullName and obj.className='XWiki.XWikiGroups' and prop.id.id=obj.id and prop.name='member' and prop.value='" +
                                userName + "'", getContext());

        List<String> groupNames = new ArrayList<String>();
        if (references != null) {
            for (DocumentReference reference : references) {
                String spaceName = reference.getLastSpaceReference().getName();
                groupNames.add(spaceName);
            }
        }

        return groupNames;
    }

    private XWikiContext getContext()
    {
        return (XWikiContext) this.execution.getContext().getProperty("xwikicontext");
    }
}
