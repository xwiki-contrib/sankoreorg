package org.xwiki.sankore.internal;

import org.xwiki.context.ExecutionContext;
import org.xwiki.sankore.ContextUtils;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.plugin.applicationmanager.core.doc.objects.classes.AbstractXClassManager;

public class GroupClass  extends AbstractXClassManager<GroupXObjectDocument>
{
    public static final String DEFAULT_FIELDS_SEPARATOR = "|";

    public static final String PREFERENCES_NAME = "WebPreferences";
    public static final int OBJECTID = 0;

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

    /**
     * Space of class document.
     */
    private static final String CLASS_SPACE = "XWiki";

    /**
     * Prefix of class document.
     */
    private static final String CLASS_PREFIX = "Group";

    /**
     * Unique instance of GroupClass.
     */
    private static GroupClass instance;

    /**
     * Default constructor for XWikiSpaceClass.
     */
    protected GroupClass()
    {
        super(CLASS_SPACE, CLASS_PREFIX, false);
    }

    public static GroupClass getInstance(ExecutionContext executionContext) throws XWikiException
    {
        return GroupClass.getInstance(ContextUtils.getXWikiContext(executionContext));
    }

    /**
     * Return unique instance of SpaceClass and update documents for this context.
     *
     * @param xwikiContext Context.
     * @return SpaceClass instance.
     * @throws XWikiException error when checking for class, class template and class sheet.
     */
    public static GroupClass getInstance(XWikiContext xwikiContext) throws XWikiException
    {
        synchronized (GroupClass.class) {
            if (instance == null) {
                instance = new GroupClass();
            }
        }

        instance.check(xwikiContext);

        return instance;
    }

    @Override
    public boolean forceValidDocumentName()
    {
        // All wiki descriptors are of the form <code>XWiki.XWikiServer%</code>
        return true;
    }

    @Override
    protected boolean updateBaseClass(BaseClass baseClass)
    {
        boolean needsUpdate = super.updateBaseClass(baseClass);

        needsUpdate |= baseClass.addTextField(FIELD_LOGO, FIELDPN_LOGO, 30);
        needsUpdate |= baseClass.addStaticListField(FIELD_POLICY, FIELDPN_POLICY, FIELDL_POLICY);
        needsUpdate |= baseClass.addStaticListField(FIELD_ACCESS_LEVEL, FIELDPN_ACCESS_LEVEL, FIELDL_ACCESS_LEVEL);
        needsUpdate |= baseClass.addTextField(FIELD_LANGUAGE, FIELDPN_LANGUAGE, 30);
        needsUpdate |= baseClass.addDBListField(FIELD_EDUCATION_SYSTEM, FIELDPN_EDUCATION_SYSTEM, 1, false, true, FIELDQ_EDUCATION_SYSTEM);
        needsUpdate |= baseClass.addDBTreeListField(FIELD_EDUCATIONAL_LEVEL, FIELDPN_EDUCATIONAL_LEVEL, 1, true, true, FIELDQ_EDUCATIONAL_LEVEL);
        needsUpdate |= baseClass.addDBTreeListField(FIELD_DISCIPLINES, FIELDPN_DISCIPLINES, 1, true, true, FIELDQ_DISCIPLINES);
        needsUpdate |= baseClass.addDBListField(FIELD_LICENSE, FIELDPN_LICENSE, 1, false, false, FIELDQ_LICENSE);

        return needsUpdate;
    }

    @Override
    protected boolean updateClassTemplateDocument(XWikiDocument xdoc)
    {
        boolean needsUpdate = false;

        /*
        if (!DEFAULT_PAGE_PARENT.equals(doc.getParent())) {
            doc.setParent(DEFAULT_PAGE_PARENT);
            needsUpdate = true;
        }

        needsUpdate |= updateDocStringValue(doc, FIELD_HOMEPAGE, DEFAULT_HOMEPAGE);

        needsUpdate |= updateDocBooleanValue(doc, FIELD_SECURE, DEFAULT_SECURE);

        needsUpdate |= updateDocBooleanValue(doc, FIELD_ISWIKITEMPLATE, DEFAULT_ISWIKITEMPLATE);
        */

        return needsUpdate;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Override abstract method using XWikiApplication as
     * {@link com.xpn.xwiki.plugin.applicationmanager.core.doc.objects.classes.XObjectDocument}.
     *
     * @see com.xpn.xwiki.plugin.applicationmanager.core.doc.objects.classes.AbstractXClassManager#newXObjectDocument(com.xpn.xwiki.doc.XWikiDocument,
     *      int, com.xpn.xwiki.XWikiContext)
     */
    @Override
    public GroupXObjectDocument newXObjectDocument(XWikiDocument xwikiDocument, int objId, XWikiContext context) throws XWikiException
    {
        return new GroupXObjectDocument(xwikiDocument, objId, context);
    }
}
