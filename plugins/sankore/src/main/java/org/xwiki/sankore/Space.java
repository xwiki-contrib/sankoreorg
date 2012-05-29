package org.xwiki.sankore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.context.ExecutionContext;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.query.QueryException;
import org.xwiki.sankore.internal.SpaceClass;
import org.xwiki.sankore.internal.SpaceXObjectDocument;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.api.Document;
import com.xpn.xwiki.api.Property;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.web.Utils;

public class Space extends Api
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Space.class);

    public static final String FIELD_SEPARATORS = " ,|";
    public static final String DEFAULT_FIELD_SEPARATOR = ",";

    public static final String XWIKI_SPACE = "XWiki";
    public static final String XWIKIGLOBALRIGHTS_CLASS_NAME = "XWikiGlobalRights";
    public static final EntityReference XWIKIGLOBALRIGHTS_CLASS_REFERENCE = new EntityReference(XWIKIGLOBALRIGHTS_CLASS_NAME,
            EntityType.DOCUMENT, new EntityReference(XWIKI_SPACE, EntityType.SPACE));
    public static final String FIELD_GROUPS = "groups";
    public static final String FIELD_LEVELS = "levels";
    public static final String FIELD_ALLOW = "allow";

    public static final String HOME_PAGE_NAME = "WebHome";
    public static final String PREFERENCES_PAGE_NAME = "WebPreferences";

    @SuppressWarnings("unchecked")
    private DocumentReferenceResolver<EntityReference> currentReferenceDocumentReferenceResolver = Utils.getComponent(
            DocumentReferenceResolver.class, "current/reference");

    @SuppressWarnings("unchecked")
    private EntityReferenceSerializer<String> localEntityReferenceSerializer = Utils.getComponent(
            EntityReferenceSerializer.class, "local");
    /**
     * The XWikiDocument object wrapped by this API.
     */
    protected SpaceXObjectDocument spaceXObjectDocument;

    protected SpaceReference spaceReference;
    protected boolean isDirty = false;

    public Space(SpaceReference spaceReference, ExecutionContext executionContext) throws XWikiException
    {
        this(new SpaceXObjectDocument(spaceReference, executionContext),
                ContextUtils.getXWikiContext(executionContext));
    }

    /**
     * Create instance of space descriptor.
     *
     * @param spaceXObjectDocument the encapsulated XWikiSpace.
     * @param xwikiContext the XWiki context.
     * @throws XWikiException error when creating {@link Api}.
     */
    public Space(SpaceXObjectDocument spaceXObjectDocument, XWikiContext xwikiContext) throws XWikiException
    {
        super(xwikiContext);
        this.spaceXObjectDocument = spaceXObjectDocument;
        this.spaceReference = spaceXObjectDocument.getDocumentReference().getLastSpaceReference();
        if (spaceXObjectDocument.isNew()) {
            this.isDirty = true;
        }
        if (Utils.getComponent(SpaceManager.class).countDocuments(this.spaceReference) > 0) {
            this.isDirty = true;
        }
    }

    /**
     * Get the XWikiDocument wrapped by this API. This function is accessible only if you have the programming rights
     * give access to the priviledged API of the Document.
     *
     * @return The XWikiDocument wrapped by this API.
     */
    public SpaceXObjectDocument getSpaceXObjectDocument()
    {
        if (hasProgrammingRights()) {
            return this.spaceXObjectDocument;
        } else {
            return null;
        }
    }

    public SpaceReference getSpaceReference()
    {
        // Clone the document reference since it can be modified
        return new SpaceReference(this.spaceXObjectDocument.getDocumentReference().getLastSpaceReference());
    }

    /**
     * return the name of a document. for exemple if the fullName of a document is "MySpace.Mydoc", the name is MyDoc.
     *
     * @return the name of the document
     */
    public String getName()
    {
        return this.spaceXObjectDocument.getSpace();
    }

    /**
     * Get the name wiki where the document is stored.
     *
     * @return The name of the wiki where this document is stored.
     * @since XWiki Core 1.1.2, XWiki Core 1.2M2
     */
    public String getWiki()
    {
        return this.spaceXObjectDocument.getWiki();
    }

    public String getTitle()
    {
        return this.spaceXObjectDocument.getTitle();
    }

    public void setTitle(String title)
    {
        this.spaceXObjectDocument.setTitle(title);
        this.isDirty = true;
    }

    public String getDescription()
    {
        return this.spaceXObjectDocument.getDescription();
    }

    public void setDescription(String description)
    {
        this.spaceXObjectDocument.setDescription(description);
        this.isDirty = true;
    }

    public String getType()
    {
        return this.spaceXObjectDocument.getType();
    }

    public void setType(String type)
    {
        this.spaceXObjectDocument.setType(type);
        this.isDirty = true;
    }

    public String getUrlShortcut()
    {
        return this.spaceXObjectDocument.getUrlShortcut();
    }

    public void setUrlShortcut(String urlShortcut)
    {
        this.spaceXObjectDocument.setUrlShortcut(urlShortcut);
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
        return spaceXObjectDocument.isNew();
    }

    public boolean isDirty()
    {
        return this.isDirty
                || this.spaceXObjectDocument.getDocument().isMetaDataDirty();
    }

    public void save() throws XWikiException
    {
        this.spaceXObjectDocument.save();
        this.isDirty = false;
    }

    // implementation done only for members groups
    public boolean hasAccessLevel(DocumentReference memberReference, String accessLevel)
    {
        boolean hasAccess = false;

        DocumentReference xWikiGlobalRightsClassReference =
                currentReferenceDocumentReferenceResolver.resolve(XWIKIGLOBALRIGHTS_CLASS_REFERENCE);
        String member = localEntityReferenceSerializer.serialize(memberReference);

        List<BaseObject> rightsObjects = this.spaceXObjectDocument.getDocument().getXObjects(xWikiGlobalRightsClassReference);
        if (rightsObjects != null) {
            for (BaseObject rightsObject : rightsObjects) {
                String[] members = StringUtils.split(rightsObject.getLargeStringValue(FIELD_GROUPS), FIELD_SEPARATORS);
                String[] levels = StringUtils.split(rightsObject.getStringValue(FIELD_LEVELS), FIELD_SEPARATORS);
                boolean allow = rightsObject.getIntValue(FIELD_ALLOW, 0) != 0;
                if (Arrays.asList(levels).contains(accessLevel) && Arrays.asList(members).contains(member))
                    hasAccess = allow;
            }
        }

        return hasAccess;
    }

    // implementation done only for members groups
    public void setAccessLevel(DocumentReference memberReference, String accessLevel, boolean allow) throws XWikiException
    {
        if (allow == hasAccessLevel(memberReference, accessLevel))
            return;

        String member = localEntityReferenceSerializer.serialize(memberReference);
        int allowInt = 0;
        if (allow)
            allowInt = 1;

        // add new global rights
        BaseObject rightsObject = this.spaceXObjectDocument.getDocument()
                .newXObject(XWIKIGLOBALRIGHTS_CLASS_REFERENCE, this.context);
        rightsObject.setLargeStringValue(FIELD_GROUPS, member);
        rightsObject.setStringValue(FIELD_LEVELS, accessLevel);
        rightsObject.setIntValue(FIELD_ALLOW, allowInt);
    }

    public void updateFromRequest() throws XWikiException
    {
        this.spaceXObjectDocument.updateObjectFromRequest(this.spaceXObjectDocument.getXClassManager().getClassFullName());
    }

    public String display(String fieldname)
    {
        return this.spaceXObjectDocument.display(fieldname);
    }

    public String display(String fieldname, String mode)
    {
        return this.spaceXObjectDocument.display(fieldname, mode);
    }

    public String getHomeURL()
    {
        DocumentReference homeReference = new DocumentReference(HOME_PAGE_NAME, this.spaceReference);
        return this.context.getWiki().getURL(homeReference, "view", this.context);
    }

    public Document getHomeDocument() throws XWikiException
    {
        DocumentReference homeReference = new DocumentReference(HOME_PAGE_NAME, this.spaceReference);
        return new Document(this.context.getWiki().getDocument(homeReference, this.context), this.context);
    }

    public Document getPreferencesDocument()
    {
        return this.spaceXObjectDocument;
    }

    public List<Property> getMetadata()
    {
        List<Property> metadata = new ArrayList<Property>();

        com.xpn.xwiki.api.Object spaceClassObject =
                this.spaceXObjectDocument.getObject(this.spaceXObjectDocument.getXClassManager().getClassFullName());
        for (java.lang.Object propName : spaceClassObject.getPropertyNames()) {
            metadata.add(spaceClassObject.getProperty((String)propName));
        }

        return metadata;
    }

    @Override
    public String toString()
    {
        return this.spaceReference.getName();
    }
}