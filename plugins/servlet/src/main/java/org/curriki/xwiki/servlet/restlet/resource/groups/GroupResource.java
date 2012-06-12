package org.curriki.xwiki.servlet.restlet.resource.groups;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;

import net.sf.json.JSONObject;
import com.xpn.xwiki.api.Property;
import com.xpn.xwiki.XWikiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public class GroupResource extends BaseResource {
    public GroupResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String groupName = (String) request.getAttributes().get("groupName");

        Map<String,Object> groupInfo = new HashMap<String, Object>(/*plugin.fetchGroupInfo(groupName)*/);
        //try {
            //groupInfo = ;
        //} catch (XWikiException e) {
        //    throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        //}

        JSONObject json = new JSONObject();
        json.put("groupName", groupName);
        if (groupInfo != null) {
            for(String propName : groupInfo.keySet()) {
                if (propName.equals("displayTitle")
                        || propName.equals("description")
                        || propName.equals("collectionCount")
                        || propName.equals("editableCollectionCount")) {
                    json.put(propName, groupInfo.get(propName));
                }
            }
        }

        return formatJSON(json, variant);
    }
}
