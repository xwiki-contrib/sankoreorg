package org.curriki.xwiki.servlet.restlet.resource.groups;

import java.util.Map;

import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;
import net.sf.json.JSONObject;
import net.sf.json.JSONException;
import com.xpn.xwiki.XWikiException;

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

        String spaceTitle = null;
        try {
            spaceTitle = json.getString("spaceTitle");
            if (spaceTitle.length() < 1){
                spaceTitle = null;
            }
        } catch (JSONException e) {
            // No parent key to get
        }

        String templateSpaceName = null;
        try {
            templateSpaceName = json.getString("templateSpaceName");
            if (templateSpaceName.length() < 1){
                templateSpaceName = null;
            }
        } catch (JSONException e) {
            // No parent key to get
        }

        Map<String, Object> groupInfo = null;
        //try {
            //groupInfo = plugin.createGroup(spaceTitle);
        //} catch (XWikiException e) {

        //}

        getResponse().redirectSeeOther(getChildReference(getRequest().getResourceRef(), groupInfo.get("groupName").toString()));
    }
}