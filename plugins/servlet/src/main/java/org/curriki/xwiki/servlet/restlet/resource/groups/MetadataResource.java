package org.curriki.xwiki.servlet.restlet.resource.groups;

import org.restlet.resource.Representation;
import org.restlet.resource.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.curriki.xwiki.servlet.restlet.resource.BaseResource;
import org.xwiki.sankore.Group;
import org.xwiki.sankore.GroupManager;

import net.sf.json.JSONObject;
import com.xpn.xwiki.api.Object;
import com.xpn.xwiki.api.Property;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.web.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 */
public class MetadataResource extends BaseResource {
    public MetadataResource(Context context, Request request, Response response) {
        super(context, request, response);
        setReadable(true);
        setModifiable(true);
        defaultVariants();
    }

    @Override public Representation represent(Variant variant) throws ResourceException {
        setupXWiki();

        Request request = getRequest();
        String groupName = (String) request.getAttributes().get("groupName");

        GroupManager groupManager = Utils.getComponent(GroupManager.class);
        List<Property> groupInfo = new ArrayList<Property>();
        try {
            Group group = groupManager.getGroup(groupName);
            groupInfo.addAll(group.getMetadata());
        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }

        JSONObject json = new JSONObject();
        for (Property prop : groupInfo) {
            json.put(prop.getName(), prop.getValue());
        }

        return formatJSON(json, variant);
    }

    @Override public void storeRepresentation(Representation representation) throws ResourceException {
        setupXWiki();

        JSONObject json = representationToJSONObject(representation);

        Request request = getRequest();
        String groupName = (String) request.getAttributes().get("groupName");

        GroupManager groupManager = Utils.getComponent(GroupManager.class);

        Group group = null;
        try {
            group = groupManager.getGroup(groupName);

            // title
            if (json.has("title")) {
                group.setTitle(json.getString("title"));
            }

            // description
            if (json.has("description")) {
                group.setDescription(json.getString("description"));
            }

            // description
            if (json.has("urlshortcut")) {
                group.setUrlShortcut(json.getString("urlshortcut"));
            }

            // language
            if (json.has("language")) {
                group.setLanguage(json.getString("language"));
            }

            // education system
            if (json.has("education_system")) {
                group.setEducationSystem(json.getString("education_system"));
            }

            // educational_level (array)
            List<String> educational_level = new ArrayList<String>();
            if (json.has("educational_level")) {
                for (java.lang.Object level : json.getJSONArray("educational_level")) {
                    educational_level.add((String )level);
                }
            }
            group.setEducationalLevel(educational_level);

            // fw_items (array)
            List<String> disciplines = new ArrayList<String>();
            if (json.has("fw_items")) {
                for (java.lang.Object level : json.getJSONArray("fw_items")) {
                    disciplines.add((String )level);
                }
            }
            group.setDisciplines(disciplines);

            // access level
            if (json.has("access_level")) {
                group.setAccessLevel(json.getString("access_level"));
            }

            // policy
            if (json.has("policy")) {
                group.setPolicy(json.getString("policy"));
            }

            // license
            if (json.has("license")) {
                group.setLicense(json.getString("license"));
            }

            group.save();

        } catch (XWikiException e) {
            throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }

        Map<String,java.lang.Object> groupInfo = new HashMap<String, java.lang.Object>(/*plugin.fetchGroupInfo(groupName)*/);
        //try {
        //groupInfo = ;
        //} catch (XWikiException e) {
        //    throw error(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        //}

        json = new JSONObject();
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

        getResponse().setEntity(formatJSON(json, getPreferredVariant()));
    }
}
