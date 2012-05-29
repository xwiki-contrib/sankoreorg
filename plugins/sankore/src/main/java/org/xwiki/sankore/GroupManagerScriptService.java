package org.xwiki.sankore;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentManager;
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

    /**
     * Used to dynamically look up component implementations based on a given hint.
     */
    @Inject
    private ComponentManager componentManager;

    public Group getGroup(String groupName) throws XWikiException
    {
        return groupManager.getGroup(groupName);
    }

    public Group createGroupFromTemplate(String groupName, String templateSpaceName)
    {
        Group group = null;
        try {
            group = this.groupManager.createGroupFromTemplate(groupName, templateSpaceName);
        } catch (XWikiException xe) {

        }

        return group;
    }
}
