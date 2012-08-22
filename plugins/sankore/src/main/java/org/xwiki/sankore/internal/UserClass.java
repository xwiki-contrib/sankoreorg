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
@Named("UsersClass")
@Singleton
public class UserClass implements ClassManager<UserObjectDocument>, Initializable
{
    /**
     * Name of field <code>prettyname</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_FIRST_NAME = "first_name";

    /**
     * Pretty name of field <code>title</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_FIRST_NAME = "First name";

    /**
     * Name of field <code>description</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_LAST_NAME = "last_name";

    /**
     * Pretty name of field <code>description</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_LAST_NAME = "Last name";

    /**
     * Name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_MEMBER_TYPE = "member_type";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_MEMBER_TYPE = "Member type";

    /**
     * Name of field <code>urlshortcut</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_EMAIL = "email";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_EMAIL = "Email";

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

    private EntityReference classReference = new EntityReference("XWikiUsers", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private EntityReference sheetReference = new EntityReference("XWikiUserSheet", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private EntityReference templateReference = new EntityReference("XWikiUserTemplate", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private XObjectDocumentClass<UserObjectDocument> xClass;

    private XWikiContext getContext()
    {
        return (XWikiContext) this.execution.getContext().getProperty("xwikicontext");
    }

    public void initialize() throws InitializationException
    {
        try {
            xClass = new DefaultXObjectDocumentClass<UserObjectDocument>(getClassDocumentReference(), getContext());
        } catch (XWikiException e) {
            throw new InitializationException("Could not initialize object document class.", e);
        }
    }

    public UserObjectDocument getDocumentObject(DocumentReference documentReference, int objectId) throws XWikiException
    {
        return getDocumentObject(documentReference);
    }

    public UserObjectDocument getDocumentObject(DocumentReference documentReference)
            throws XWikiException
    {
        XWikiContext context = getContext();
        XWikiDocument doc = context.getWiki().getDocument(documentReference, context);
        BaseObject obj = doc.getXObject(getClassDocumentReference());

        if (obj == null) {
            return null;
        }

        return new UserObjectDocument(xClass, doc, obj, context);
    }

    public UserObjectDocument newDocumentObject(DocumentReference documentReference) throws XWikiException
    {
        XWikiContext context = getContext();
        XWikiDocument doc = context.getWiki().getDocument(documentReference, context);
        BaseObject obj = doc.getXObject(getClassDocumentReference(), true, context);

        if (obj == null) {
            return null;
        }

        return new UserObjectDocument(xClass, doc, obj, context);
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

    public List<UserObjectDocument> searchByField(String fieldName, Object fieldValue) throws XWikiException
    {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(fieldName, fieldValue);

        return searchByFields(fields);
    }

    public List<UserObjectDocument> searchByFields(Map<String, Object> fields) throws XWikiException
    {
        String from = "select distinct doc.space, doc.name from XWikiDocument as doc, BaseObject as obj";
        String where = " where obj.name=doc.fullName and obj.className='XWiki.XWikiUsers'";
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

        List<UserObjectDocument> userObjectDocuments = new ArrayList<UserObjectDocument>();
        List<Object> results = getContext().getWiki().getStore().search(from + where, 0, 0, getContext());
        logger.info("UserClass search: " + from + where + "; results: " + results.size());
        String docSpace = StringUtils.EMPTY;
        String docName = StringUtils.EMPTY;
        for (Object result : results) {
            Object[] resultParams = (Object[]) result;
            docSpace = resultParams[0].toString();
            docName = resultParams[1].toString();
            DocumentReference documentReference = currentReferenceDocumentReferenceResolver.resolve(
                    new EntityReference(docName, EntityType.DOCUMENT, new EntityReference(docSpace, EntityType.SPACE)));
            userObjectDocuments.add(getDocumentObject(documentReference));
        }

        return userObjectDocuments;
    }

    public void saveDocumentObject(UserObjectDocument documentObject) throws XWikiException
    {
        documentObject.saveDocument("UserObjectDocument saved", false);
    }

    public void deleteDocumentObject(UserObjectDocument documentObject) throws XWikiException
    {
        documentObject.delete();
    }

    @Override
    public String toString()
    {
        return referenceSerializer.serialize(this.classReference);
    }
}
