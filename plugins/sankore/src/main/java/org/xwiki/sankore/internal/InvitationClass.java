package org.xwiki.sankore.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.context.Execution;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.EntityReferenceSerializer;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.objects.classes.PropertyClass;

@Component
@Named("InvitationClass")
@Singleton
public class InvitationClass implements ClassManager<InvitationObjectDocument>, Initializable
{
    public static final String DEFAULT_FIELDS_SEPARATOR = "|";
    /**
     * Name of field <code>prettyname</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_REQUESTDATE = "requestDate";

    /**
     * Pretty name of field <code>title</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_REQUESTDATE = "Invitation request date";

    /**
     * Name of field <code>description</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_RESPONSEDATE = "responseDate";

    /**
     * Pretty name of field <code>description</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_RESPONSEDATE = "Invitation response date";

    /**
     * Name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_TEXT = "text";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_TEXT = "Text";

    /**
     * Name of field <code>urlshortcut</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_MAP = "map";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_MAP = "Map";

    /**
     * Name of field <code>urlshortcut</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_GROUP = "group";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_GROUP = "Group name";

    /**
     * Name of field <code>urlshortcut</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_STATUS = "status";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_STATUS = "Status";

    public static final String FIELDT_STATUS = "StringProperty";

    public static final String FIELD_STATUS_NONE = "none";
    public static final String FIELD_STATUS_CREATED = "created";
    public static final String FIELD_STATUS_SENT = "sent";
    public static final String FIELD_STATUS_ACCEPTED = "accepted";
    public static final String FIELD_STATUS_REFUSED = "refused";
    public static final String FIELD_STATUS_CANCELED = "canceled";

    public static final String FIELDL_STATUS = FIELD_STATUS_NONE +
            DEFAULT_FIELDS_SEPARATOR + FIELD_STATUS_CREATED +
            DEFAULT_FIELDS_SEPARATOR + FIELD_STATUS_SENT +
            DEFAULT_FIELDS_SEPARATOR + FIELD_STATUS_ACCEPTED +
            DEFAULT_FIELDS_SEPARATOR + FIELD_STATUS_REFUSED +
            DEFAULT_FIELDS_SEPARATOR + FIELD_STATUS_CANCELED;

    /**
     * Name of field <code>urlshortcut</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_ROLES = "roles";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_ROLES = "Roles";

    public static final String FIELDQ_ROLES = "";

    /**
     * Name of field <code>urlshortcut</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_INVITEE = "invitee";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_INVITEE = "Invitee";

    public static final String FIELDT_INVITEE = "StringProperty";

    /**
     * Name of field <code>urlshortcut</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_INVITER = "inviter";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_INVITER = "Inviter";

    /**
     * Name of field <code>urlshortcut</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_KEY = "key";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_KEY = "Validation key";

    public static final String FIELDT_KEY = "StringProperty";

    @Inject
    private Logger logger;

    @Inject
    Execution execution;

    @Inject
    @Named("current/reference")
    DocumentReferenceResolver<EntityReference> currentReferenceDocumentReferenceResolver;

    @Inject
    @Named("local")
    private EntityReferenceSerializer<String> referenceSerializer;

    private EntityReference classReference = new EntityReference("InvitationClass", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private EntityReference sheetReference = new EntityReference("InvitationSheet", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private EntityReference templateReference = new EntityReference("InvitationTemplate", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private XObjectDocumentClass<InvitationObjectDocument> xClass;

    private XWikiContext getContext()
    {
        return (XWikiContext) this.execution.getContext().getProperty("xwikicontext");
    }

    public void initialize() throws InitializationException
    {
        try {
            xClass = new DefaultXObjectDocumentClass<InvitationObjectDocument>(getClassDocumentReference(), getContext());
        } catch (XWikiException e) {
            throw new InitializationException("Could not initialize object document class.", e);
        }
    }

    public InvitationObjectDocument getDocumentObject(DocumentReference documentReference, int objectId) throws XWikiException
    {
        XWikiContext context = getContext();
        XWikiDocument doc = context.getWiki().getDocument(documentReference, context);
        BaseObject obj = doc.getXObject(getClassDocumentReference(), objectId);

        if (obj == null) {
            return null;
        }

        return new InvitationObjectDocument(xClass, doc, obj, context);
    }

    public InvitationObjectDocument getDocumentObject(DocumentReference documentReference) throws XWikiException
    {
        XWikiContext context = getContext();
        XWikiDocument doc = context.getWiki().getDocument(documentReference, context);
        BaseObject obj = doc.getXObject(getClassDocumentReference());

        if (obj == null) {
            return null;
        }

        return new InvitationObjectDocument(xClass, doc, obj, context);
    }

    public InvitationObjectDocument newDocumentObject(DocumentReference documentReference) throws XWikiException
    {
        XWikiContext context = getContext();
        XWikiDocument doc = context.getWiki().getDocument(documentReference, context);
        BaseObject obj = doc.newXObject(getClassDocumentReference(), context);

        if (obj == null)
            return null;

        return new InvitationObjectDocument(xClass, doc, obj, context);
    }

    public DocumentReference getClassDocumentReference() throws XWikiException
    {
        return currentReferenceDocumentReferenceResolver.resolve(classReference);
    }

    public DocumentReference getClassSheetDocumentReference() throws XWikiException
    {
        return currentReferenceDocumentReferenceResolver.resolve(sheetReference);
    }

    public DocumentReference getClassTemplateDocumentReference() throws XWikiException
    {
        return currentReferenceDocumentReferenceResolver.resolve(templateReference);
    }

    public List<InvitationObjectDocument> searchByField(String fieldName, Object fieldValue) throws XWikiException
    {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(fieldName, fieldValue);

        return searchByFields(fields);
    }

    public List<InvitationObjectDocument> searchByFields(Map<String, Object> fields) throws XWikiException
    {
        String from = "select distinct doc.space, doc.name, obj.number from XWikiDocument as doc, BaseObject as obj";
        String where = " where obj.name=doc.fullName and obj.className='XWiki.InvitationClass'";
        BaseClass baseClass = getContext().getWiki().getXClass(getClassDocumentReference(), getContext());
        List<PropertyClass> enabledProperties = baseClass.getEnabledProperties();

        int i = 0;
        String propName = StringUtils.EMPTY;
        String propType = StringUtils.EMPTY;
        String propValue = StringUtils.EMPTY;
        for (PropertyClass propertyClass : enabledProperties) {
            if (fields.keySet().contains(propertyClass.getName())) {
                propName = "prop" + Integer.toString(i);
                propType =  propertyClass.newProperty().getClass().getSimpleName();
                propValue = fields.get(propertyClass.getName()).toString();
                from = from.concat(", " + propType + " as " + propName);
                where = where.concat(" and " + propName + ".id.id=obj.id and " + propName + ".name='" + propertyClass.getName() + "'");
                if (StringUtils.equals(propType, "IntegerProperty")) {
                    if (StringUtils.equals(propValue, "true"))
                        where = where.concat(" and " + propName + ".value>0");
                    else
                        where = where.concat(" and " + propName + ".value=0");
                } else if (StringUtils.equals(propType, "LongProperty")
                        || StringUtils.equals(propType, "FloatProperty")
                        || StringUtils.equals(propType, "DoubleProperty")) {
                    where = where.concat(" and " + propName + ".value=" + fields.get(propertyClass.getName()).toString());
                } else if (StringUtils.equals(propType, "StringProperty")
                        || StringUtils.equals(propType, "LargeStringProperty")
                        || StringUtils.equals(propType, "DateProperty")) {
                    where = where.concat(" and " + propName + ".value='" + fields.get(propertyClass.getName()).toString() + "'");
                } else if (StringUtils.equals(propType, "StringListProperty")) {
                    where = where.concat(" and " + propName + ".textValue='" + fields.get(propertyClass.getName()).toString() + "'");
                } else if (StringUtils.equals(propType, "DBStringListProperty")) {
                    from = from.concat(" join " + propName + ".list " + propName + "list");
                    where = where.concat(" and " + propName + "list='" + fields.get(propertyClass.getName()).toString() + "'");
                }
                i++;
            }
        }

        List<InvitationObjectDocument> invitationObjectDocuments = new ArrayList<InvitationObjectDocument>();
        List<Object> results = getContext().getWiki().getStore().search(from + where, 0, 0, getContext());

        logger.info("InvitationClass search: " + from + where + "; results: " + results.size());

        String docSpace = StringUtils.EMPTY;
        String docName = StringUtils.EMPTY;
        int objNumber = 0;
        for (Object result : results) {
            Object[] resultParams = (Object[]) result;
            docSpace = resultParams[0].toString();
            docName = resultParams[1].toString();
            objNumber = Integer.parseInt(resultParams[2].toString());
            DocumentReference documentReference = currentReferenceDocumentReferenceResolver.resolve(
                    new EntityReference(docName, EntityType.DOCUMENT, new EntityReference(docSpace, EntityType.SPACE)));
            invitationObjectDocuments.add(getDocumentObject(documentReference, objNumber));
        }

        return invitationObjectDocuments;
    }

    public void saveDocumentObject(InvitationObjectDocument documentObject) throws XWikiException
    {
        documentObject.saveDocument("InvitationObjectDocument saved.", false);
    }

    public void deleteDocumentObject(InvitationObjectDocument documentObject) throws XWikiException
    {
        documentObject.delete();
    }

    @Override
    public String toString()
    {
        return referenceSerializer.serialize(this.classReference);
    }
}
