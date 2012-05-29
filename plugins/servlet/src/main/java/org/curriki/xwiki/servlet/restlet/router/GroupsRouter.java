package org.curriki.xwiki.servlet.restlet.router;


import org.restlet.Context;
import org.restlet.Router;
import org.curriki.xwiki.servlet.restlet.resource.groups.GroupResource;
import org.curriki.xwiki.servlet.restlet.resource.groups.GroupsResource;
import org.curriki.xwiki.servlet.restlet.resource.groups.MetadataResource;
import org.curriki.xwiki.servlet.restlet.resource.groups.GroupCollectionsResource;
import org.restlet.util.Template;

/**
 */
public class GroupsRouter extends Router {
    public GroupsRouter(Context context) {
        super(context);
        attach("", GroupsResource.class).getTemplate().setMatchingMode(Template.MODE_EQUALS);
        attach("/{groupName}", GroupResource.class);
        attach("/{groupName}/metadata", MetadataResource.class);
        attach("/{groupName}/collections", GroupCollectionsResource.class);
    }
}
