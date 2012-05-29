package org.xwiki.sankore.internal;

import org.xwiki.context.ExecutionContext;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.sankore.ContextUtils;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.plugin.applicationmanager.core.doc.objects.classes.AbstractXClassManager;

public class SpaceClass extends AbstractXClassManager<SpaceXObjectDocument>
{
    public static final String PREFERENCES_NAME = "WebPreferences";
    public static final int OBJECTID = 0;

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

    /**
     * Space of class document.
     */
    public static final String CLASS_SPACE = "XWiki";

    /**
     * Prefix of class document.
     */
    public static final String CLASS_PREFIX = "Space";

    /**
     * Unique instance of XWikiSpaceClass.
     */
    private static SpaceClass instance;

    /**
     * Default constructor for XWikiSpaceClass.
     */
    protected SpaceClass()
    {
        super(CLASS_SPACE, CLASS_PREFIX, false);
    }

    public static SpaceClass getInstance(ExecutionContext executionContext) throws XWikiException
    {
        return SpaceClass.getInstance(ContextUtils.getXWikiContext(executionContext));
    }

    /**
     * Return unique instance of SpaceClass and update documents for this context.
     *
     * @param xwikiContext Context.
     * @return SpaceClass instance.
     * @throws XWikiException error when checking for class, class template and class sheet.
     */
    public static SpaceClass getInstance(XWikiContext xwikiContext) throws XWikiException
    {
        synchronized (SpaceClass.class) {
            if (instance == null) {
                instance = new SpaceClass();
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

        needsUpdate |= baseClass.addTextField(FIELD_TITLE, FIELDPN_TITLE, 30);
        needsUpdate |= baseClass.addTextAreaField(FIELD_DESCRIPTION, FIELDPN_DESCRIPTION, 40, 5);
        needsUpdate |= baseClass.addTextField(FIELD_TYPE, FIELDPN_TYPE, 30);
        needsUpdate |= baseClass.addTextField(FIELD_URLSHORTCUT, FIELDPN_URLSHORTCUT, 30);

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
    public SpaceXObjectDocument newXObjectDocument(XWikiDocument xdoc, int objId, XWikiContext context) throws XWikiException
    {
        return new SpaceXObjectDocument(xdoc, objId, context);
    }
}