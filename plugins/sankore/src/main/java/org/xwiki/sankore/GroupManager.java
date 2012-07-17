package org.xwiki.sankore;

import java.util.List;

import org.xwiki.component.annotation.ComponentRole;

import com.xpn.xwiki.XWikiException;

@ComponentRole
public interface GroupManager
{
    public Group getGroup(String groupName) throws XWikiException;

    public Group createGroup(String groupName) throws XWikiException;

    public Group createGroupFromTemplate(String groupName, String templateName) throws XWikiException;

    public Group createGroupFromRequest() throws XWikiException;

    public Group updateGroupFromRequest() throws XWikiException;

    public List<String> getGroupNamesFor(String userName) throws XWikiException;
}
