package org.xwiki.sankore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.api.Property;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.context.ExecutionContext;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.*;
import org.xwiki.sankore.internal.GroupClass;
import org.xwiki.sankore.internal.GroupXObjectDocument;
import org.xwiki.sankore.internal.SpaceClass;
import org.xwiki.sankore.internal.SpaceXObjectDocument;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.web.Utils;

public class Group extends Api
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Group.class);

    public static final String GROUP_SPACE_PREFIX = "Group_";
    public static final String GROUP_COLL_SPACE_PREFIX = "Coll_Group_";
    public static final String GROUP_DOCUMENTATION_SPACE_PREFIX = "Documentation_Group_";
    public static final String GROUP_INVITATIONS_SPACE_PREFIX = "Invitations_Group_";
    public static final String GROUP_MESSAGES_SPACE_PREFIX = "Messages_Group_";
    public static final String GROUP_USERPROFILES_SPACE_PREFIX = "UserProfiles_Group_";

    public static final String ADMINGROUP_NAME = "AdminGroup";
    public static final String MEMBERGROUP_NAME = "MemberGroup";
    public static final String ROLE_AFFILIATEGROUP_NAME = "Role_AffiliateGroup";
    public static final String ROLE_CONTRIBUTORPARTICIPANTGROUP_NAME = "Role_ContributorParticipantGroup";

    public static final String XWIKI_SPACE = "XWiki";

    public static final String XWIKIALLGROUP_NAME = "XWikiAllGroup";
    public static final EntityReference XWIKIALLGROUP_REFERENCE = new EntityReference(XWIKIALLGROUP_NAME,
            EntityType.DOCUMENT, new EntityReference(XWIKI_SPACE, EntityType.SPACE));

    public static final String XWIKIADMINGROUP_NAME = "XWikiAdminGroup";
    public static final EntityReference XWIKIADMINGROUP_REFERENCE = new EntityReference(XWIKIADMINGROUP_NAME,
            EntityType.DOCUMENT, new EntityReference(XWIKI_SPACE, EntityType.SPACE));

    public static final String RIGHTS_LEVELS_VIEW = "view";
    public static final String RIGHTS_LEVELS_EDIT = "edit";
    public static final String RIGHTS_LEVELS_COMMENT = "comment";
    public static final String RIGHTS_LEVELS_DELETE = "delete";
    public static final String RIGHTS_LEVELS_ADMIN = "admin";

    protected Space groupSpace;
    protected Space collSpace;
    protected Space documentationSpace;
    protected Space invitationsSpace;
    protected Space messagesSpace;
    protected Space userProfilesSpace;
    protected GroupXObjectDocument groupXObjectDocument;

    @SuppressWarnings("unchecked")
    private DocumentReferenceResolver<EntityReference> currentReferenceDocumentReferenceResolver = Utils.getComponent(
            DocumentReferenceResolver.class, "current/reference");

    @SuppressWarnings("unchecked")
    private EntityReferenceSerializer<String> localEntityReferenceSerializer = Utils.getComponent(
            EntityReferenceSerializer.class, "local");

    protected MembersGroup adminGroup;
    protected MembersGroup memberGroup;

    protected String groupName;

    protected boolean isDirty;
    protected boolean isRightsDirty;

    public Group(String groupName, ExecutionContext executionContext) throws XWikiException
    {
        this(new GroupXObjectDocument(new SpaceReference(GROUP_SPACE_PREFIX + groupName, new WikiReference(
                ContextUtils.getXWikiContext(executionContext).getDatabase())), executionContext),
                ContextUtils.getXWikiContext(executionContext));
    }

    /**
     * Create instance of space descriptor.
     *
     * @param groupXObjectDocument
     * @param xWikiContext the XWiki context.
     * @throws com.xpn.xwiki.XWikiException error when creating {@link Api}.
     */
    public Group(GroupXObjectDocument groupXObjectDocument, XWikiContext xWikiContext) throws XWikiException
    {
        super(xWikiContext);
        this.groupName = groupXObjectDocument.getSpace().replaceFirst(GROUP_SPACE_PREFIX, "");
        this.groupXObjectDocument = groupXObjectDocument;
        XWiki xWiki = xWikiContext.getWiki();
        WikiReference wikiReference = groupXObjectDocument.getDocumentReference().getWikiReference();
        // groupXObjectDocument = groupSpace.spaceXObjectDocument
        this.groupSpace = new Space(
                new SpaceXObjectDocument(
                        this.groupXObjectDocument.getDocument(),
                        SpaceClass.OBJECTID,
                        xWikiContext),
                xWikiContext);
        this.collSpace = new Space(
                new SpaceXObjectDocument(
                        xWiki.getDocument(
                                new DocumentReference(
                                        SpaceClass.PREFERENCES_NAME,
                                        new SpaceReference(GROUP_COLL_SPACE_PREFIX + this.groupName, wikiReference)),
                                xWikiContext),
                        SpaceClass.OBJECTID,
                        xWikiContext),
                xWikiContext);
        this.documentationSpace = new Space(
                new SpaceXObjectDocument(
                        xWiki.getDocument(
                                new DocumentReference(
                                        SpaceClass.PREFERENCES_NAME,
                                        new SpaceReference(GROUP_DOCUMENTATION_SPACE_PREFIX + this.groupName, wikiReference)),
                                xWikiContext),
                        SpaceClass.OBJECTID,
                        xWikiContext),
                xWikiContext);
        this.invitationsSpace = new Space(
                new SpaceXObjectDocument(
                        xWiki.getDocument(
                                new DocumentReference(
                                        SpaceClass.PREFERENCES_NAME,
                                        new SpaceReference(GROUP_INVITATIONS_SPACE_PREFIX + this.groupName, wikiReference)),
                                xWikiContext),
                        SpaceClass.OBJECTID,
                        xWikiContext),
                xWikiContext);
        this.messagesSpace = new Space(
                new SpaceXObjectDocument(
                        xWiki.getDocument(
                                new DocumentReference(
                                        SpaceClass.PREFERENCES_NAME,
                                        new SpaceReference(GROUP_MESSAGES_SPACE_PREFIX + this.groupName, wikiReference)),
                                xWikiContext),
                        SpaceClass.OBJECTID,
                        xWikiContext),
                xWikiContext);
        this.userProfilesSpace = new Space(
                new SpaceXObjectDocument(
                        xWiki.getDocument(
                                new DocumentReference(
                                        SpaceClass.PREFERENCES_NAME,
                                        new SpaceReference(GROUP_USERPROFILES_SPACE_PREFIX + this.groupName, wikiReference)),
                                xWikiContext),
                        SpaceClass.OBJECTID,
                        xWikiContext),
                xWikiContext);

        this.adminGroup = new MembersGroup(
                xWiki.getDocument(
                        new DocumentReference(ADMINGROUP_NAME, this.groupSpace.getSpaceReference()),
                        this.context),
                this.context);
        this.memberGroup = new MembersGroup(
                xWiki.getDocument(
                        new DocumentReference(MEMBERGROUP_NAME, this.groupSpace.getSpaceReference()),
                        this.context),
                this.context);

        if (this.groupXObjectDocument.isNew()) {
            // add current user to MemberGroup and AdminGroup
            this.memberGroup.addUserOrGroup(localEntityReferenceSerializer.serialize(this.context.getUserReference()));
            this.adminGroup.addUserOrGroup(localEntityReferenceSerializer.serialize(this.context.getUserReference()));
            // set dirty
            this.isDirty = true;
            this.isRightsDirty = true;
        }
    }

    /**
     * Get the XWikiDocument wrapped by this API. This function is accessible only if you have the programming rights
     * give access to the priviledged API of the Document.
     *
     * @return The XWikiDocument wrapped by this API.
     */
    public GroupXObjectDocument getGroupXObjectDocument()
    {
        if (hasProgrammingRights()) {
            return this.groupXObjectDocument;
        } else {
            return null;
        }
    }

    /**
     * return the name of a document. for exemple if the fullName of a document is "MySpace.Mydoc", the name is MyDoc.
     *
     * @return the name of the document
     */
    public String getName()
    {
        return this.groupName;
    }

    /**
     * Get the name wiki where the document is stored.
     *
     * @return The name of the wiki where this document is stored.
     * @since XWiki Core 1.1.2, XWiki Core 1.2M2
     */
    public String getWiki()
    {
        return this.groupXObjectDocument.getWiki();
    }

    public String getTitle()
    {
        return this.groupSpace.getTitle();
    }

    public void setTitle(String title)
    {
        this.groupSpace.setTitle(title);
    }

    public String getDescription()
    {
        return this.groupSpace.getDescription();
    }

    public void setDescription(String description)
    {
        this.groupSpace.setDescription(description);
    }

    public String getUrlShortcut()
    {
        return this.groupSpace.getUrlShortcut();
    }

    public void setUrlShortcut(String urlShortcut)
    {
        this.groupSpace.setUrlShortcut(urlShortcut);
    }

    public String getLogo()
    {
        return groupXObjectDocument.getLogo();
    }

    public void setLogo(String logo)
    {
        this.groupXObjectDocument.setLogo(logo);
        this.isDirty = true;
    }

    public String getPolicy()
    {
        return this.groupXObjectDocument.getPolicy();
    }

    public void setPolicy(String policy)
    {
        this.groupXObjectDocument.setPolicy(policy);
        this.isDirty = true;
    }

    public String getAccessLevel()
    {
        return this.groupXObjectDocument.getAccessLevel();
    }

    public void setAccessLevel(String accessLevel)
    {
        if (!StringUtils.equals(this.groupXObjectDocument.getAccessLevel(), accessLevel)) {
            this.isRightsDirty = true;
        }

        this.groupXObjectDocument.setAccessLevel(accessLevel);
        this.isDirty = true;
    }

    public String getLanguage()
    {
        return this.groupXObjectDocument.getLanguage();
    }

    public void setLanguage(String language)
    {
        this.groupXObjectDocument.setLanguage(language);
        this.isDirty = true;
    }

    public String getEducationSystem()
    {
        return this.groupXObjectDocument.getEducationSystem();
    }

    public void setEducationSystem(String educationSystem)
    {
        this.groupXObjectDocument.setEducationSystem(educationSystem);
        this.isDirty = true;
    }

    public List<String> getEducationalLevel()
    {
        return this.groupXObjectDocument.getEducationalLevel();
    }

    public void setEducationalLevel(List<String> educationalLevel)
    {
        this.groupXObjectDocument.setEducationalLevel(educationalLevel);
        this.isDirty = true;
    }

    public List<String> getDisciplines()
    {
        return  this.groupXObjectDocument.getDisciplines();
    }

    public void setDisciplines(List<String> disciplines)
    {
        this.groupXObjectDocument.setDisciplines(disciplines);
        this.isDirty = true;
    }

    public String getLicense()
    {
        return this.groupXObjectDocument.getLicense();
    }

    public void setLicense(String license)
    {
        this.groupXObjectDocument.setLicense(license);
        this.isDirty = true;
    }

    /**
     * Delete the space.
     *
     * @throws XWikiException error deleting the wiki.
     * @since 1.1
     */
    public void delete() throws XWikiException
    {

    }

    public boolean isNew()
    {
        return this.groupXObjectDocument.isNew();
    }

    public boolean isDirty()
    {
        return this.isDirty
                || this.groupXObjectDocument.getDocument().isMetaDataDirty()
                || this.groupSpace.isDirty()
                || this.documentationSpace.isDirty()
                || this.invitationsSpace.isDirty()
                || this.messagesSpace.isDirty()
                || this.userProfilesSpace.isDirty();
    }

    public void save() throws XWikiException
    {
        if (this.isRightsDirty) {
            this.updateRights();
        }

        // Save only dirty spaces
        if (this.groupSpace.isDirty()) {
            this.groupSpace.save();
        }
        if (this.documentationSpace.isDirty()) {
            this.documentationSpace.save();
        }
        if (this.invitationsSpace.isDirty()) {
            this.invitationsSpace.save();
        }
        if (this.messagesSpace.isDirty()) {
            this.invitationsSpace.save();
        }
        if (this.userProfilesSpace.isDirty()) {
            this.userProfilesSpace.save();
        }

        if (this.adminGroup.isDirty()) {
            this.adminGroup.save();
        }

        if (this.memberGroup.isDirty()) {
            this.memberGroup.save();
        }

        //this.groupXObjectDocument.save();
        this.isDirty = false;
    }

    public Space getGroupSpace()
    {
        return this.groupSpace;
    }

    public void updateFromRequest() throws XWikiException
    {
        this.groupSpace.updateFromRequest();
        this.groupXObjectDocument.updateObjectFromRequest(this.groupXObjectDocument.getXClassManager().getClassFullName());
    }

    public MembersGroup getAdminGroup() throws XWikiException
    {
        return new MembersGroup(
                this.context.getWiki().getDocument(
                        new DocumentReference(ADMINGROUP_NAME, this.groupSpace.getSpaceReference()),
                        this.context),
                this.context);
    }

    public MembersGroup getMemberGroup() throws XWikiException
    {
        return new MembersGroup(
                this.context.getWiki().getDocument(
                        new DocumentReference(MEMBERGROUP_NAME, this.groupSpace.getSpaceReference()),
                        this.context),
                this.context);
    }

    public MembersGroup getAffiliateGroup() throws XWikiException
    {
        return new MembersGroup(
                this.context.getWiki().getDocument(
                        new DocumentReference(ROLE_AFFILIATEGROUP_NAME, this.groupSpace.getSpaceReference()),
                        this.context),
                this.context);
    }

    public MembersGroup getContributorParticipantGroup() throws XWikiException
    {
        return new MembersGroup(
                this.context.getWiki().getDocument(
                        new DocumentReference(ROLE_CONTRIBUTORPARTICIPANTGROUP_NAME, this.groupSpace.getSpaceReference()),
                        this.context),
                this.context);
    }

    private void updateRights() throws XWikiException
    {
        //DocumentReference xWikiAdminGroupReference =
        //        currentReferenceDocumentReferenceResolver.resolve(XWIKIADMINGROUP_REFERENCE);
        //DocumentReference xWikiAllGroupReference =
        //        currentReferenceDocumentReferenceResolver.resolve(XWIKIALLGROUP_REFERENCE);
        DocumentReference adminGroup = new DocumentReference(ADMINGROUP_NAME, this.groupSpace.getSpaceReference());
        DocumentReference memberGroup = new DocumentReference(MEMBERGROUP_NAME, this.groupSpace.getSpaceReference());

        this.groupSpace.setAccessLevel(adminGroup, RIGHTS_LEVELS_EDIT, true);

        if (this.groupXObjectDocument.getAccessLevel().equals(GroupClass.FIELD_ACCESS_LEVEL_PUBLIC)) {
            this.groupSpace.setAccessLevel(adminGroup, RIGHTS_LEVELS_EDIT, true);
        }

        if (this.groupXObjectDocument.getAccessLevel().equals(GroupClass.FIELD_ACCESS_LEVEL_PROTECTED)) {
            this.groupSpace.setAccessLevel(adminGroup, RIGHTS_LEVELS_EDIT, true);
            this.collSpace.setAccessLevel(memberGroup, RIGHTS_LEVELS_EDIT, true);
        }

        if (this.groupXObjectDocument.getAccessLevel().equals(GroupClass.FIELD_ACCESS_LEVEL_PRIVATE)) {
            this.groupSpace.setAccessLevel(adminGroup, RIGHTS_LEVELS_EDIT, true);
            this.collSpace.setAccessLevel(memberGroup, RIGHTS_LEVELS_VIEW, true);
            this.collSpace.setAccessLevel(memberGroup, RIGHTS_LEVELS_EDIT, true);
        }

        this.groupSpace.getSpaceXObjectDocument().getDocument().setMetaDataDirty(true);
        this.collSpace.getSpaceXObjectDocument().getDocument().setMetaDataDirty(true);
    }

    public boolean joinGroup() throws XWikiException
    {
        // TODO
        // exceptions for policy closed

        // add user profile
        this.memberGroup.addUserOrGroup(this.context.getUser());
        // send mail

        return true;
    }

    public boolean isAdmin() throws XWikiException
    {
        return this.adminGroup.isMember(localEntityReferenceSerializer.serialize(this.context.getUserReference()));
    }

    public boolean isAdmin(String user) throws XWikiException
    {
        return this.adminGroup.isMember(user);
    }

    public boolean isMember() throws XWikiException
    {
        return this.memberGroup.isMember(localEntityReferenceSerializer.serialize(this.context.getUserReference()));
    }

    public boolean isMember(String user) throws XWikiException
    {
        return this.memberGroup.isMember(user);
    }

    public String display(String fieldname)
    {
        if (Arrays.asList(this.groupXObjectDocument.getXClassManager().getBaseClass().getPropertyNames()).contains(fieldname))
            return this.groupXObjectDocument.display(fieldname);

        return this.groupSpace.display(fieldname);
    }

    public String display(String fieldname, String mode)
    {
        if (Arrays.asList(this.groupXObjectDocument.getXClassManager().getBaseClass().getPropertyNames()).contains(fieldname))
            return this.groupXObjectDocument.display(fieldname, mode);

        return this.groupSpace.display(fieldname, mode);
    }

    public Date getCreationDate()
    {
        return this.groupXObjectDocument.getCreationDate();
    }

    public String getHomeURL()
    {
        return this.groupSpace.getHomeURL();
    }

    public Document getHomeDocument() throws XWikiException
    {
        return this.groupSpace.getHomeDocument();
    }

    public List<Property> getMetadata() {

        List<Property> metadata = new ArrayList<Property>();

        metadata.addAll(this.groupSpace.getMetadata());

        com.xpn.xwiki.api.Object groupClassObject =
                this.groupXObjectDocument.getObject(this.groupXObjectDocument.getXClassManager().getClassFullName());
        for (java.lang.Object propName : groupClassObject.getPropertyNames()) {
            metadata.add(groupClassObject.getProperty((String)propName));
        }

        return metadata;
    }

    @Override
    public String toString()
    {
        return this.groupName;
    }
}
