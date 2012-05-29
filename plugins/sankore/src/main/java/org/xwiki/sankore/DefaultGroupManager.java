package org.xwiki.sankore;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.xpn.xwiki.web.XWikiRequest;
import org.apache.axis.utils.StringUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.container.Request;
import org.xwiki.context.Execution;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;

@Component
@Singleton
public class DefaultGroupManager implements GroupManager
{
    private static final String DEFAULT_RESOLVER_HINT = "currentmixed/reference";

    @Inject
    private Logger logger;

    @Inject
    private Execution execution;

    @Inject
    private SpaceManager spaceManager;

    @Inject
    private ComponentManager componentManager;

    public Group getGroup(String groupName) throws XWikiException
    {
        if (groupName.startsWith("Group_"))
            return new Group(groupName.replaceFirst("Group_", ""), execution.getContext());

        return new Group(groupName, execution.getContext());
    }

    public Group createGroup(String groupName) throws XWikiException
    {
        Group group = new Group(groupName, execution.getContext());
        if (!group.isNew() || this.spaceManager.countDocuments(group.getGroupSpace().getSpaceReference()) == 0) {
            return null;
        }

        group.save();
        return group;
    }

    public Group createGroupFromRequest() throws XWikiException
    {
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

        return group;
    }

    public Group updateGroupFromRequest() throws XWikiException
    {
        XWikiRequest xWikiRequest = ContextUtils.getXWikiRequest(execution.getContext());
        String groupName = xWikiRequest.get("groupName");

        if (StringUtils.isEmpty(groupName))
            return null;

        Group group = this.getGroup(groupName);
        if (group.isNew()) {
            return null;
        }
        group.updateFromRequest();
        return group;
    }

    public Group createGroupFromTemplate(String groupName, String templateName) throws XWikiException
    {
        Group group = new Group(groupName, execution.getContext());
        if (!group.isNew()) {
            return null;
        }

        Space groupSpace = group.getGroupSpace();
        this.spaceManager.copySpace(this.spaceManager.createSpaceReference(groupSpace.getWiki(), templateName),
                groupSpace.getSpaceReference(), true);

        group.save();
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
}
