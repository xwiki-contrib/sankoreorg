package org.xwiki.sankore;

import java.util.*;

import com.xpn.xwiki.web.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.sankore.internal.SpaceClass;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.classes.GroupsClass;
import com.xpn.xwiki.user.api.XWikiGroupService;

public class MembersGroup extends Api
{
    public static final String FIELD_SEPARATORS = " ,|";
    public static final String DEFAULT_FIELD_SEPARATOR = ",";

    public static final String XWIKI_SPACE = "XWiki";

    public static final String XWIKIGROUPS_CLASS_NAME = "XWikiGroups";
    public static final String XWIKIRIGHTS_CLASS_NAME = "XWikiRights";
    public static final String XWIKIGLOBALRIGHTS_CLASS_NAME = "XWikiGlobalRights";

    public static final String RIGHTS_LEVELS_VIEW = "view";
    public static final String RIGHTS_LEVELS_EDIT = "edit";
    public static final String RIGHTS_LEVELS_COMMENT = "comment";
    public static final String RIGHTS_LEVELS_DELETE = "delete";
    public static final String RIGHTS_LEVELS_ADMIN = "admin";

    public static final String XWIKIALLGROUP_NAME = "XWikiAllGroup";
    public static final EntityReference XWIKIALLGROUP_REFERENCE = new EntityReference(XWIKIALLGROUP_NAME,
            EntityType.DOCUMENT, new EntityReference(XWIKI_SPACE, EntityType.SPACE));

    public static final String XWIKIADMINGROUP_NAME = "XWikiAdminGroup";
    public static final EntityReference XWIKIADMINGROUP_REFERENCE = new EntityReference(XWIKIADMINGROUP_NAME,
            EntityType.DOCUMENT, new EntityReference(XWIKI_SPACE, EntityType.SPACE));


    public static final EntityReference XWIKIGROUPS_CLASS_REFERENCE = new EntityReference(XWIKIGROUPS_CLASS_NAME,
            EntityType.DOCUMENT, new EntityReference(XWIKI_SPACE, EntityType.SPACE));
    public static final String XWIKIGROUPS_MEMBER = "member";

    public static final EntityReference XWIKIRIGHTS_CLASS_REFERENCE = new EntityReference(XWIKIRIGHTS_CLASS_NAME,
            EntityType.DOCUMENT, new EntityReference(XWIKI_SPACE, EntityType.SPACE));
    public static final EntityReference XWIKIGLOBALRIGHTS_CLASS_REFERENCE = new EntityReference(XWIKIGLOBALRIGHTS_CLASS_NAME,
            EntityType.DOCUMENT, new EntityReference(XWIKI_SPACE, EntityType.SPACE));
    public static final String FIELD_GROUPS = "group";
    public static final String FIELD_LEVELS = "levels";
    public static final String FIELD_ALLOW = "allow";


    private XWikiDocument xWikiDocument;
    private XWikiGroupService xWikiGroupService;

    @SuppressWarnings("unchecked")
    private DocumentReferenceResolver<EntityReference> currentReferenceDocumentReferenceResolver = Utils.getComponent(
            DocumentReferenceResolver.class, "current/reference");

    public MembersGroup(XWikiDocument xWikiDocument, XWikiContext xWikiContext) throws XWikiException
    {
        super(xWikiContext);
        this.xWikiDocument = xWikiDocument;
        this.xWikiGroupService = xWikiContext.getWiki().getGroupService(xWikiContext);
    }

    public boolean addUserOrGroup(String userOrGroup) throws XWikiException
    {
        List<BaseObject> xObjects = this.xWikiDocument.getXObjects(
                currentReferenceDocumentReferenceResolver.resolve(XWIKIGROUPS_CLASS_REFERENCE));

        for (BaseObject xObject : xObjects) {
            if (StringUtils.equals(xObject.getStringValue(XWIKIGROUPS_MEMBER), userOrGroup))
                return false;
        }

        BaseObject member = this.xWikiDocument.newXObject(XWIKIGROUPS_CLASS_REFERENCE, this.context);
        member.setStringValue(XWIKIGROUPS_MEMBER, userOrGroup);

        this.context.getWiki().saveDocument(this.xWikiDocument, this.context);

        return true;
    }

    public boolean removeUserOrGroup(String userOrGroup) throws XWikiException
    {
        boolean needUpdate = false;

        List<BaseObject> xObjects = xWikiDocument.getXObjects(
                currentReferenceDocumentReferenceResolver.resolve(XWIKIGROUPS_CLASS_REFERENCE));

        for (BaseObject xObject : xObjects) {
            if (StringUtils.equals(xObject.getStringValue(XWIKIGROUPS_MEMBER), userOrGroup)) {
                needUpdate = this.xWikiDocument.removeXObject(xObject);
            }
        }

        if (needUpdate)
            this.context.getWiki().saveDocument(this.xWikiDocument, this.context);

        return needUpdate;
    }

    public int countAllMembers()
    {
        return this.xWikiDocument.getXObjects(
                currentReferenceDocumentReferenceResolver.resolve(XWIKIGROUPS_CLASS_REFERENCE)).size();
    }

    public List<String> getAllMembers() throws XWikiException
    {
        List<BaseObject> xObjects = this.xWikiDocument.getXObjects(
                currentReferenceDocumentReferenceResolver.resolve(XWIKIGROUPS_CLASS_REFERENCE));

        List<String> members = new ArrayList<String>();

        for (BaseObject xObject : xObjects) {
            members.add(xObject.getStringValue(XWIKIGROUPS_MEMBER));
        }

        return members;
    }

    public boolean isMember(String userOrGroup) throws XWikiException
    {
        List<BaseObject> xObjects = this.xWikiDocument.getXObjects(
                currentReferenceDocumentReferenceResolver.resolve(XWIKIGROUPS_CLASS_REFERENCE));

        for (BaseObject xObject : xObjects) {
            if (StringUtils.equals(xObject.getStringValue(XWIKIGROUPS_MEMBER), userOrGroup))
                return true;
        }

        return false;
    }

    protected void  validateRights(SpaceReference spaceReference) throws XWikiException
    {
        XWikiDocument preferencesDocument = this.context.getWiki().getDocument(
                new DocumentReference(SpaceClass.PREFERENCES_NAME, spaceReference),
                this.context);

        Map<String, BaseObject> allowRightsMap = new HashMap<String, BaseObject>();
        Map<String, BaseObject> denyRightsMap = new HashMap<String, BaseObject>();

        Map<String, List<String>> allowMembersMap = new HashMap<String, List<String>>();
        Map<String, List<String>> denyMembersMap = new HashMap<String, List<String>>();

        List<BaseObject> rightsObjects = preferencesDocument.getXObjects(
                currentReferenceDocumentReferenceResolver.resolve(XWIKIGLOBALRIGHTS_CLASS_REFERENCE));
        if (rightsObjects != null) {
        for (BaseObject rightsObject : rightsObjects) {
            if (rightsObject == null) {
                continue;
            }
            String[] members = StringUtils.split(rightsObject.getLargeStringValue(FIELD_GROUPS), FIELD_SEPARATORS);
            String[] levels = StringUtils.split(rightsObject.getStringValue(FIELD_LEVELS), FIELD_SEPARATORS);
            boolean allowdeny = rightsObject.getIntValue(FIELD_ALLOW, 0) != 0;
            if (levels.length > 1) {
                if (allowdeny) {
                    for (String level : levels) {
                        List<String> allowMembers = allowMembersMap.get(level);
                        List<String> denyMembers = denyMembersMap.get(level);
                        if (allowMembers == null)
                            allowMembers = new ArrayList<String>();
                        if (denyMembers == null)
                            denyMembers = new ArrayList<String>();

                        for (String member : members) {
                            if (!allowMembers.contains(member))
                                allowMembers.add(member);
                            if (denyMembers.contains(member))
                                denyMembers.remove(member);
                        }

                        allowMembersMap.put(level, allowMembers);
                        denyMembersMap.put(level, denyMembers);
                    }
                } else {
                    for (String level : levels) {
                        List<String> allowMembers = allowMembersMap.get(level);
                        List<String> denyMembers = denyMembersMap.get(level);
                        if (allowMembers == null)
                            allowMembers = new ArrayList<String>();
                        if (denyMembers == null)
                            denyMembers = new ArrayList<String>();

                        for (String member : members) {
                            if (allowMembers.contains(member))
                                allowMembers.remove(member);
                            if (!denyMembers.contains(member))
                                denyMembers.add(member);
                        }

                        allowMembersMap.put(level, allowMembers);
                        denyMembersMap.put(level, denyMembers);
                    }
                }
                preferencesDocument.removeXObject(rightsObject);
            } else {
                String level = levels[0];
                if (allowdeny) {
                    BaseObject allowRights = allowRightsMap.get(level);
                    List<String> allowMembers = allowMembersMap.get(level);
                    List<String> denyMembers = denyMembersMap.get(level);
                    if (allowMembers == null)
                        allowMembers = new ArrayList<String>();
                    if (denyMembers == null)
                        denyMembers = new ArrayList<String>();

                    for (String member : members) {
                        if (!allowMembers.contains(member))
                            allowMembers.add(member);
                        if (denyMembers.contains(member))
                            denyMembers.remove(member);
                    }

                    allowMembersMap.put(level, allowMembers);
                    denyMembersMap.put(level, denyMembers);

                    if (allowRights != null) {
                        preferencesDocument.removeXObject(rightsObject);
                    } else {
                        allowRightsMap.put(level, rightsObject);
                    }
                } else {
                    BaseObject denyRights = denyRightsMap.get(level);
                    List<String> allowMembers = allowMembersMap.get(level);
                    List<String> denyMembers = denyMembersMap.get(level);
                    if (allowMembers == null)
                        allowMembers = new ArrayList<String>();
                    if (denyMembers == null)
                        denyMembers = new ArrayList<String>();

                    for (String member : members) {
                        if (allowMembers.contains(member))
                            allowMembers.remove(member);
                        if (!denyMembers.contains(member))
                            denyMembers.add(member);
                    }

                    allowMembersMap.put(level, allowMembers);
                    denyMembersMap.put(level, denyMembers);

                    if (denyRights != null) {
                        preferencesDocument.removeXObject(rightsObject);
                    } else {
                        denyRightsMap.put(level, rightsObject);
                    }
                }
            }
        }
        }

        // validate allow/deny members
        for (String level : allowRightsMap.keySet()) {
            BaseObject rightsObject = allowRightsMap.get(level);
            List<String> allowMembers = allowMembersMap.get(level);
            if (allowMembers != null && allowMembers.size() > 0)
                rightsObject.setLargeStringValue(FIELD_GROUPS, StringUtils.join(allowMembers.toArray(), DEFAULT_FIELD_SEPARATOR));
            else
                preferencesDocument.removeXObject(rightsObject);
        }

        for (String level : denyRightsMap.keySet()) {
            BaseObject rightsObject = denyRightsMap.get(level);
            List<String> denyMembers = denyMembersMap.get(level);
            if (denyMembers != null && denyMembers.size() > 0)
                rightsObject.setLargeStringValue(FIELD_GROUPS, StringUtils.join(denyMembers.toArray(), DEFAULT_FIELD_SEPARATOR));
            else
                preferencesDocument.removeXObject(rightsObject);
        }
    }

    public void addRights(SpaceReference spaceReference, String rightsLevel, boolean allow) throws XWikiException
    {
        validateRights(spaceReference);

        XWikiDocument preferencesDocument = this.context.getWiki().getDocument(
                new DocumentReference(SpaceClass.PREFERENCES_NAME, spaceReference),
                this.context);

        BaseObject allowRights = null;
        BaseObject denyRights = null;

        List<BaseObject> rightsObjects = preferencesDocument.getXObjects(
                currentReferenceDocumentReferenceResolver.resolve(XWIKIGLOBALRIGHTS_CLASS_REFERENCE));
        if (rightsObjects != null) {
        for (BaseObject rightsObject : rightsObjects) {
            if (StringUtils.equals(rightsObject.getStringValue(FIELD_LEVELS), rightsLevel)) {
                if (rightsObject.getIntValue(FIELD_ALLOW, 0) == 0)
                    denyRights = rightsObject;
                else
                    allowRights = rightsObject;
            }
        }
        }

        List<String> allowMembers = new ArrayList<String>();
        List<String> denyMembers = new ArrayList<String>();
        if (allowRights != null)
            allowMembers.addAll(Arrays.asList(StringUtils.split(allowRights.getLargeStringValue(FIELD_GROUPS), FIELD_SEPARATORS)));
        if (denyRights != null)
            denyMembers.addAll(Arrays.asList(StringUtils.split(denyRights.getLargeStringValue(FIELD_GROUPS), FIELD_SEPARATORS)));

        if (allow && !allowMembers.contains(this.xWikiDocument.toString())) {
            if (allowRights == null) {
                allowRights = preferencesDocument.newXObject(XWIKIGLOBALRIGHTS_CLASS_REFERENCE, this.context);
                allowRights.setLargeStringValue(FIELD_GROUPS, this.xWikiDocument.toString());
                allowRights.setStringValue(FIELD_LEVELS, rightsLevel);
                allowRights.setIntValue(FIELD_ALLOW, 1);
            } else {
                allowMembers.add(this.xWikiDocument.toString());
                allowRights.setLargeStringValue(FIELD_GROUPS, StringUtils.join(allowMembers.toArray(), DEFAULT_FIELD_SEPARATOR));
            }
            // validate deny
            if (denyRights != null) {
                if (denyMembers.contains(this.xWikiDocument.toString()))
                    denyMembers.remove(this.xWikiDocument.toString());
                if (denyMembers.size() == 0)
                    preferencesDocument.removeXObject(denyRights);
            }
        }
        if (!allow && !denyMembers.contains(this.xWikiDocument.toString())) {
            if (denyRights == null) {
                denyRights = preferencesDocument.newXObject(XWIKIGLOBALRIGHTS_CLASS_REFERENCE, this.context);
                denyRights.setLargeStringValue(FIELD_GROUPS, this.xWikiDocument.toString());
                denyRights.setStringValue(FIELD_LEVELS, rightsLevel);
                denyRights.setIntValue(FIELD_ALLOW, 0);
            } else {
                denyMembers.add(this.xWikiDocument.toString());
                denyRights.setLargeStringValue(FIELD_GROUPS, StringUtils.join(denyMembers.toArray(), DEFAULT_FIELD_SEPARATOR));
            }
            // validate deny
            if (allowRights != null) {
                if (allowMembers.contains(this.xWikiDocument.toString()))
                    allowMembers.remove(this.xWikiDocument.toString());
                if (allowMembers.size() == 0)
                    preferencesDocument.removeXObject(allowRights);
            }
        }
    }

    public void removeRights(SpaceReference spaceReference, String rightsLevel, boolean allow) throws XWikiException
    {
        // TODO
    }

    public boolean isNew()
    {
        return this.xWikiDocument.isNew();
    }

    public boolean isDirty()
    {
        return this.xWikiDocument.isContentDirty()
                || this.xWikiDocument.isMetaDataDirty();
    }

    public void save() throws XWikiException
    {
        this.context.getWiki().saveDocument(this.xWikiDocument, this.context);
    }
}
