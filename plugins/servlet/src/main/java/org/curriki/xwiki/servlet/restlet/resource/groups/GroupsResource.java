package org.curriki.xwiki.servlet.restlet.resource.groups;

import java.util.Map;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;
import org.xwiki.sankore.Group;
import org.xwiki.sankore.GroupManager;

import net.sf.json.JSONObject;
import net.sf.json.JSONException;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.Utils;

/**
 */
public class GroupsResource extends BaseResource {
    public GroupsResource(Context context, Request request, Response response) {
        super(context, request, response);
        setModifiable(true);
        defaultVariants();
    }

    @Override public void acceptRepresentation(Representation representation) throws ResourceException {
        setupXWiki();

        JSONObject json = representationToJSONObject(representation);

        GroupManager groupManager = Utils.getComponent(GroupManager.class);

        String title = null;
        try {
            title = json.getString("title");
            if (title.length() < 1){
                title = null;
            }
        } catch (JSONException e) {
            // No parent key to get
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }

        String template = null;
        try {
            template = json.getString("template");
            if (template.length() < 1){
                template = null;
            }
        } catch (JSONException e) {
            // No parent key to get
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }

        Group group = null;
        try {
            group = groupManager.createGroupFromTemplate(title, template);
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }

        //Map<String, Object> groupInfo = null;
        //try {
        //    groupInfo = plugin.createGroup(title);
        //} catch (XWikiException e) {
        //
        //}

        getResponse().redirectSeeOther(getChildReference(getRequest().getResourceRef(), group.getName()));
    }
}