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
@Named("MembershipRequestClass")
@Singleton
public class MembershipRequestClass implements ClassManager<MembershipRequestObjectDocument>
{
    public static final String DEFAULT_FIELDS_SEPARATOR = "|";
    /**
     * Name of field <code>prettyname</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_REQUESTDATE = "requestDate";

    /**
     * Pretty name of field <code>title</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_REQUESTDATE = "Membership request date";

    /**
     * Name of field <code>description</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_RESPONSEDATE = "responseDate";

    /**
     * Pretty name of field <code>description</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_RESPONSEDATE = "Membership response date";

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

    public static final String FIELDT_STATUS = "StringListProperty";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_STATUS = "Status";

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
    public static final String FIELD_REQUESTER = "requester";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_REQUESTER = "Requester";

    public static final String FIELDT_REQUESTER = "StringProperty";

    /**
     * Name of field <code>urlshortcut</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_RESPONDER = "responder";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_RESPONDER = "Responder";

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

    private EntityReference classReference = new EntityReference("MembershipRequestClass", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private EntityReference sheetReference = new EntityReference("MembershipRequestSheet", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private EntityReference templateReference = new EntityReference("MembershipRequestTemplate", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private XWikiContext getContext()
    {
        return (XWikiContext) this.execution.getContext().getProperty("xwikicontext");
    }

    public MembershipRequestObjectDocument getDocumentObject(DocumentReference documentReference, int objectId) throws XWikiException
    {
        XWikiContext context = getContext();
        DefaultXObjectDocumentClass<MembershipRequestObjectDocument> cls =
                new DefaultXObjectDocumentClass<MembershipRequestObjectDocument>(getClassDocumentReference(), context);
        XWikiDocument doc = context.getWiki().getDocument(documentReference, context);
        BaseObject obj = doc.getXObject(getClassDocumentReference(), objectId);

        if (obj == null) {
            return null;
        }

        return new MembershipRequestObjectDocument(cls, doc, obj, context);
    }

    public MembershipRequestObjectDocument getDocumentObject(DocumentReference documentReference) throws XWikiException
    {
        XWikiContext context = getContext();
        DefaultXObjectDocumentClass<MembershipRequestObjectDocument> cls =
                new DefaultXObjectDocumentClass<MembershipRequestObjectDocument>(getClassDocumentReference(), context);
        XWikiDocument doc = context.getWiki().getDocument(documentReference, context);
        BaseObject obj = doc.getXObject(getClassDocumentReference());

        if (obj == null) {
            return null;
        }

        return new MembershipRequestObjectDocument(cls, doc, obj, context);
    }

    public MembershipRequestObjectDocument newDocumentObject(DocumentReference documentReference) throws XWikiException
    {
        XWikiContext context = getContext();
        DefaultXObjectDocumentClass<MembershipRequestObjectDocument> cls =
                new DefaultXObjectDocumentClass<MembershipRequestObjectDocument>(getClassDocumentReference(), context);
        XWikiDocument doc = context.getWiki().getDocument(documentReference, context);
        BaseObject obj = doc.getXObject(getClassDocumentReference(), true, context);

        if (obj == null)
            return null;

        return new MembershipRequestObjectDocument(cls, doc, obj, context);
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

    public List<MembershipRequestObjectDocument> searchDocumentObjectsByField(String fieldName, Object fieldValue)
            throws XWikiException
    {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(fieldName, fieldValue);

        return searchDocumentObjectsByFields(fields);
    }

    public List<MembershipRequestObjectDocument> searchDocumentObjectsByFields(Map<String, Object> fields) throws XWikiException
    {
        String from = "select distinct doc.space, doc.name, obj.number from XWikiDocument as doc, BaseObject as obj";
        String where = " where obj.name=doc.fullName and obj.className='XWiki.MembershipRequestClass'";
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

        List<MembershipRequestObjectDocument>
                membershipRequestObjectDocuments = new ArrayList<MembershipRequestObjectDocument>();
        List<Object> results = getContext().getWiki().getStore().search(from + where, 0, 0, getContext());

        logger.info("MembershipRequestClass search: " + from + where + "; results: " + results.size());

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
            membershipRequestObjectDocuments.add(getDocumentObject(documentReference, objNumber));
        }

        return membershipRequestObjectDocuments;
    }

    public void saveDocumentObject(MembershipRequestObjectDocument documentObject) throws XWikiException
    {
        documentObject.saveDocument("MembershipRequestObjectDocument saved.", false);
        //documentObject.save();
        //getContext().getWiki().saveDocument(documentObject.getDocument(), getContext());
    }

    @Override
    public String toString()
    {
        return referenceSerializer.serialize(this.classReference);
    }
}
