package org.xwiki.sankore;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;

import com.xpn.xwiki.XWikiException;

/**
 * Created with IntelliJ IDEA. User: XWIKI Date: 5/22/12 Time: 1:26 PM To change this template use File | Settings |
 * File Templates.
 */
@Component
@Named("groupmgr")
@Singleton
public class GroupManagerScriptService implements ScriptService
{
    @Inject
    private GroupManager groupManager;

    public Group getGroup(String groupName)
            throws XWikiException
    {
        return this.groupManager.getGroup(groupName);
    }

    public Group createGroupFromTemplate(String groupName, String templateSpaceName)
            throws XWikiException
    {
        return this.groupManager.createGroupFromTemplate(groupName, templateSpaceName);
    }

    public List<String> getGroupNamesFor(String userName)
            throws XWikiException
    {
        return this.groupManager.getGroupNamesFor(userName);
    }
}
