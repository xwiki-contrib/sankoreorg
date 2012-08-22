package org.xwiki.sankore.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;

import org.apache.commons.lang.StringUtils;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.context.Execution;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.classes.BaseClass;

@Component
@Named("MemberGroupClass")
@Singleton
public class MemberGroupClass implements ClassManager<MemberGroupObjectDocument>, Initializable
{
    public static final String DEFAULT_FIELDS_SEPARATOR = "|";

    public static final String[] DOCUMENT_FIELDS = {"name", "fullName", "space"};

    public static final String FIELD_ROLE = "role";

    @Inject
    private Logger logger;

    @Inject
    Execution execution;

    @Inject
    private QueryManager queryManager;

    @Inject
    @Named("current/reference")
    DocumentReferenceResolver<EntityReference> currentReferenceDocumentReferenceResolver;

    @Inject
    @Named("local")
    private EntityReferenceSerializer<String> referenceSerializer;

    private EntityReference classReference = new EntityReference("MemberGroupClass", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private EntityReference sheetReference = new EntityReference("MemberGroupSheet", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private EntityReference templateReference = new EntityReference("MemberGroupTemplate", EntityType.DOCUMENT,
            new EntityReference("XWiki", EntityType.SPACE));

    private XObjectDocumentClass<MemberGroupObjectDocument> xClass;

    private XWikiContext getContext()
    {
        return (XWikiContext) this.execution.getContext().getProperty("xwikicontext");
    }

    public void initialize() throws InitializationException
    {
        try {
            xClass = new DefaultXObjectDocumentClass<MemberGroupObjectDocument>(getClassDocumentReference(), getContext());
        } catch (XWikiException e) {
            //throw new InitializationException("Could not initialize object document class.", e);
        }
    }

    public MemberGroupObjectDocument getDocumentObject(DocumentReference documentReference, int objectId) throws XWikiException
    {
        return getDocumentObject(documentReference);
    }

    public MemberGroupObjectDocument getDocumentObject(DocumentReference documentReference) throws XWikiException
    {
        XWikiContext context = getContext();
        XWikiDocument doc = context.getWiki().getDocument(documentReference, context);
        BaseObject obj = doc.getXObject(getClassDocumentReference());

        if (obj == null) {
            return null;
        }

        return new MemberGroupObjectDocument(xClass, doc, obj, context);
    }

    public MemberGroupObjectDocument newDocumentObject(DocumentReference documentReference) throws XWikiException
    {
        XWikiContext context = getContext();
        XWikiDocument doc = context.getWiki().getDocument(documentReference, context);
        BaseObject obj = doc.getXObject(getClassDocumentReference(), true, context);

        if (obj == null) {
            return null;
        }

        return new MemberGroupObjectDocument(xClass, doc, obj, context);
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

    public List<MemberGroupObjectDocument> searchByField(String fieldName, Object fieldValue)
            throws XWikiException, QueryException
    {
        Map<String, Object> fields = new HashMap<String, Object>();
        fields.put(fieldName, fieldValue);

        return searchByFields(fields);
    }

    public List<MemberGroupObjectDocument> searchByFields(Map<String, Object> fields)
            throws XWikiException, QueryException
    {
        String statement = "select distinct doc.space, doc.name from Document doc, doc.object(XWiki.MemberGroupClass) as obj where 1=1";
        BaseClass baseClass = getContext().getWiki().getXClass(getClassDocumentReference(), getContext());
        String[] propertyNames =  baseClass.getPropertyNames();

        for (String field : fields.keySet()) {
            String fieldValue = (String) fields.get(field);
            if (ArrayUtils.contains(propertyNames, field)) {
                statement = statement + " and obj." + field + "='" + fieldValue + "'";
            } else if ((StringUtils.contains(field, '.') && StringUtils.contains(field, "_")) || StringUtils.startsWith(field, "_") ) {
                String className = StringUtils.substringBefore(field, "_");
                String fieldName = StringUtils.substringAfter(field, "_");
                if (StringUtils.isNotBlank(className)) {
                    // its new class property
                    statement = statement + " and doc.object(" + className + ")." + fieldName + "='" + fieldValue + "'";
                } else {
                    // its document metadata
                    statement = statement + " and doc." + fieldName + "='" + fieldValue + "'";
                }
            }
        }

        List<MemberGroupObjectDocument> objectDocuments = new ArrayList<MemberGroupObjectDocument>();
        List<Object> results = queryManager.createQuery(statement, Query.XWQL).execute();
        String docSpace = StringUtils.EMPTY;
        String docName = StringUtils.EMPTY;
        for (Object result : results) {
            Object[] resultParams = (Object[]) result;
            docSpace = resultParams[0].toString();
            docName = resultParams[1].toString();
            DocumentReference documentReference = currentReferenceDocumentReferenceResolver.resolve(
                    new EntityReference(docName, EntityType.DOCUMENT, new EntityReference(docSpace, EntityType.SPACE)));
            objectDocuments.add(getDocumentObject(documentReference));
        }

        return objectDocuments;
    }

    public void saveDocumentObject(MemberGroupObjectDocument documentObject) throws XWikiException
    {
        documentObject.saveDocument("MemberGroupObjectDocument saved.", false);
    }

    public void deleteDocumentObject(MemberGroupObjectDocument documentObject) throws XWikiException
    {
        documentObject.delete();
    }

    @Override
    public String toString()
    {
        return referenceSerializer.serialize(this.classReference);
    }
}