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
@Named("GroupClass")
@Singleton
public class GroupClass implements ClassManager<GroupObjectDocument>
{
    public static final String DEFAULT_FIELDS_SEPARATOR = "|";

    /**
     * Name of field <code>prettyname</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_TITLE = "title";

    /**
     * Pretty name of field <code>title</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_TITLE = "Space title";

    /**
     * Name of field <code>description</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_DESCRIPTION = "description";

    /**
     * Pretty name of field <code>description</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_DESCRIPTION = "Space description";

    /**
     * Name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_TYPE = "type";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_TYPE = "Space type";

    /**
     * Name of field <code>urlshortcut</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_URLSHORTCUT = "urlshortcut";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_URLSHORTCUT = "Space URL shortcut";

    public static final String FIELD_LOGO = "logo";
    public static final String FIELDPN_LOGO = "Group logo";

    public static final String FIELD_POLICY = "policy";
    public static final String FIELDPN_POLICY = "Group membership policy";
    public static final String FIELD_POLICY_OPEN = "open";
    public static final String FIELD_POLICY_CLOSED = "closed";
    public static final String FIELDL_POLICY = FIELD_POLICY_OPEN + DEFAULT_FIELDS_SEPARATOR + FIELD_POLICY_CLOSED;

    public static final String FIELD_ACCESS_LEVEL = "access_level";
    public static final String FIELDPN_ACCESS_LEVEL = "Group access level";
    public static final String FIELD_ACCESS_LEVEL_PUBLIC = "public";
    public static final String FIELD_ACCESS_LEVEL_PROTECTED = "protected";
    public static final String FIELD_ACCESS_LEVEL_PRIVATE = "private";
    public static final String FIELDL_ACCESS_LEVEL = FIELD_ACCESS_LEVEL_PUBLIC + DEFAULT_FIELDS_SEPARATOR + FIELD_ACCESS_LEVEL_PROTECTED + DEFAULT_FIELDS_SEPARATOR + FIELD_ACCESS_LEVEL_PRIVATE;

    public static final String FIELD_LANGUAGE = "language";
    public static final String FIELDPN_LANGUAGE = "Group language";

    public static final String FIELD_EDUCATION_SYSTEM = "education_system";
    public static final String FIELDPN_EDUCATION_SYSTEM = "Group education system";
    public static final String FIELDQ_EDUCATION_SYSTEM = "select doc.fullName, prop.value from XWikiDocument as doc, BaseObject as obj, StringProperty as prop where doc.space='AssetMetadata' and doc.fullName=obj.name and obj.className='CurrikiCode.EducationSystemClass' and prop.id.id=obj.id and prop.name='language'";

    public static final String FIELD_EDUCATIONAL_LEVEL = "educational_level";
    public static final String FIELDPN_EDUCATIONAL_LEVEL = "Group educational level";
    public static final String FIELDQ_EDUCATIONAL_LEVEL = "select doc.fullName, prop.value, doc.parent from XWikiDocument as doc, BaseObject as obj, StringProperty as prop, StringProperty as pivot where doc.space in ('AssetMetadata') and doc.fullName=obj.name and obj.className='CurrikiCode.EducationalLevelClass' and obj.id=prop.id.id and prop.name='education_system' and obj.id=pivot.id.id and pivot.name='pivot' order by pivot.value";

    public static final String FIELD_DISCIPLINES = "disciplines";
    public static final String FIELDPN_DISCIPLINES = "Group disciplines";
    public static final String FIELDQ_DISCIPLINES = "select doc.fullName, prop.textValue, doc.parent from XWikiDocument as doc, BaseObject as obj, StringListProperty as prop where doc.space in ('Disciplines') and doc.fullName=obj.name and obj.className='CurrikiCode.DisciplineClass' and obj.id=prop.id.id and prop.name='educational_levels' order by doc.fullName";

    public static final String FIELD_LICENSE = "license";
    public static final String FIELDPN_LICENSE = "Group license";
    public static final String FIELDQ_LICENSE = "select obj.name, prop.value from BaseObject as obj, StringProperty as prop, IntegerProperty as oprop where  obj.className='XWiki.LicenceClass' and prop.id.id = obj.id  and prop.id.name = 'name' and prop.value not like 'DEPRECATED:%' and oprop.id.id = obj.id and oprop.id.name = 'order' order by oprop.value";

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

    private EntityReference classReference = new EntityReference("GroupClass", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private EntityReference sheetReference = new EntityReference("GroupSheet", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private EntityReference templateReference = new EntityReference("GroupTemplate", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private XWikiContext getContext()
    {
        return (XWikiContext) this.execution.getContext().getProperty("xwikicontext");
    }

    public GroupObjectDocument getDocumentObject(DocumentReference documentReference, int objectId) throws XWikiException
    {
        return getDocumentObject(documentReference);
    }

    public GroupObjectDocument getDocumentObject(DocumentReference documentReference) throws XWikiException
    {
        XWikiContext context = getContext();
        DefaultXObjectDocumentClass<GroupObjectDocument> cls =
                new DefaultXObjectDocumentClass<GroupObjectDocument>(getClassDocumentReference(), context);
        XWikiDocument doc = context.getWiki().getDocument(documentReference, context);
        BaseObject obj = doc.getXObject(getClassDocumentReference());

        if (obj == null)
            return null;

        return new GroupObjectDocument(cls, doc, obj, context);
    }

    public GroupObjectDocument newDocumentObject(DocumentReference documentReference) throws XWikiException
    {
        XWikiContext context = getContext();
        DefaultXObjectDocumentClass<GroupObjectDocument> cls =
                new DefaultXObjectDocumentClass<GroupObjectDocument>(getClassDocumentReference(), context);
        XWikiDocument doc = context.getWiki().getDocument(documentReference, context);
        BaseObject obj = doc.getXObject(getClassDocumentReference(), true, context);

        if (obj == null)
            return null;

        return new GroupObjectDocument(cls, doc, obj, context);
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

    public List<GroupObjectDocument> searchDocumentObjectsByField(String fieldName, Object fieldValue) throws XWikiException
    {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(fieldName, fieldValue);

        return searchDocumentObjectsByFields(fields);
    }

    public List<GroupObjectDocument> searchDocumentObjectsByFields(Map<String, Object> fields) throws XWikiException
    {
        String from = "select distinct doc.space, doc.name from XWikiDocument as doc, BaseObject as obj";
        String where = " where obj.name=doc.fullName and obj.className='XWiki.GroupClass'";
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

        List<GroupObjectDocument> groupObjectDocuments = new ArrayList<GroupObjectDocument>();
        List<Object> results = getContext().getWiki().getStore().search(from + where, 0, 0, getContext());
        String docSpace = StringUtils.EMPTY;
        String docName = StringUtils.EMPTY;
        for (Object result : results) {
            Object[] resultParams = (Object[]) result;
            docSpace = resultParams[0].toString();
            docName = resultParams[1].toString();
            DocumentReference documentReference = currentReferenceDocumentReferenceResolver.resolve(
                    new EntityReference(docName, EntityType.DOCUMENT, new EntityReference(docSpace, EntityType.SPACE)));
            groupObjectDocuments.add(getDocumentObject(documentReference));
        }

        return groupObjectDocuments;
    }

    public void saveDocumentObject(GroupObjectDocument documentObject) throws XWikiException
    {
        //documentObject.save();
        documentObject.saveDocument("GroupObjectDocument saved.", false);
        //getContext().getWiki().saveDocument(documentObject.getDocument(), getContext());
    }

    @Override
    public String toString()
    {
        return referenceSerializer.serialize(this.classReference);
    }
}
