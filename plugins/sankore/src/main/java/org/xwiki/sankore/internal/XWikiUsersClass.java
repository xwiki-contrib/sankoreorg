package org.xwiki.sankore.internal;

import org.xwiki.context.ExecutionContext;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.sankore.ContextUtils;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.plugin.applicationmanager.core.doc.objects.classes.AbstractXClassManager;
import com.xpn.xwiki.plugin.applicationmanager.core.doc.objects.classes.XObjectDocument;

public class XWikiUsersClass extends AbstractXClassManager<UserXObjectDocument>
{
    public static final String DEFAULT_USERS_SPACE = "XWiki";

    public static final int OBJECTID = 0;

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

    public static final String FIELDT_EMAIL = "StringProperty";

    /**
     * Space of class document.
     */
    public static final String CLASS_SPACE = "XWiki";

    /**
     * Prefix of class document.
     */
    public static final String CLASS_PREFIX = "XWikiUsers";

    private static final String XWIKI_CLASS_SUFFIX = "";

    /**
     * Unique instance of XWikiSpaceClass.
     */
    private static XWikiUsersClass instance;

    /**
     * Default constructor for XWikiSpaceClass.
     */
    protected XWikiUsersClass()
    {
        super(CLASS_SPACE, CLASS_PREFIX, false);
    }

    public static XWikiUsersClass getInstance(ExecutionContext executionContext) throws XWikiException
    {
        return XWikiUsersClass.getInstance(ContextUtils.getXWikiContext(executionContext));
    }

    /**
     * Return unique instance of SpaceClass and update documents for this context.
     *
     * @param xwikiContext Context.
     * @return SpaceClass instance.
     * @throws XWikiException error when checking for class, class template and class sheet.
     */
    public static XWikiUsersClass getInstance(XWikiContext xwikiContext) throws XWikiException
    {
        synchronized (XWikiUsersClass.class) {
            if (instance == null) {
                instance = new XWikiUsersClass();
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
        //boolean needsUpdate = super.updateBaseClass(baseClass);

        //needsUpdate |= baseClass.addTextField(FIELD_TITLE, FIELDPN_TITLE, 30);
       // needsUpdate |= baseClass.addTextAreaField(FIELD_DESCRIPTION, FIELDPN_DESCRIPTION, 40, 5);
        //needsUpdate |= baseClass.addTextField(FIELD_TYPE, FIELDPN_TYPE, 30);
        //needsUpdate |= baseClass.addTextField(FIELD_URLSHORTCUT, FIELDPN_URLSHORTCUT, 30);

        //return needsUpdate;

        return false;
    }

    @Override
    protected boolean updateClassTemplateDocument(XWikiDocument xWikiDocument)
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
    public UserXObjectDocument newXObjectDocument(XWikiDocument xWikiDocument, int objId, XWikiContext xWikiContext) throws XWikiException
    {
        return newXObjectDocument(xWikiDocument, xWikiContext);
    }

    public UserXObjectDocument newXObjectDocument(XWikiDocument xWikiDocument, XWikiContext xWikiContext) throws XWikiException
    {
        return new UserXObjectDocument(xWikiDocument, OBJECTID, xWikiContext);
    }

    @Override
    public UserXObjectDocument newXObjectDocument(String userName, int objId, XWikiContext xWikiContext) throws XWikiException
    {
        return newXObjectDocument(xWikiContext.getWiki().getDocument(newXObjectDocumentReference(userName, xWikiContext), xWikiContext),
                xWikiContext);
    }

    public UserXObjectDocument newXObjectDocument(String userName, ExecutionContext executionContext)
        throws XWikiException
    {
        return newXObjectDocument(userName, ContextUtils.getXWikiContext(executionContext));
    }

    public UserXObjectDocument newXObjectDocument(String userName, XWikiContext xWikiContext) throws XWikiException
    {
        return newXObjectDocument(xWikiContext.getWiki().getDocument(newXObjectDocumentReference(userName, xWikiContext), xWikiContext), xWikiContext);
    }


    public static DocumentReference newXObjectDocumentReference(String userName, XWikiContext xWikiContext)
    {
        if (userName.startsWith(DEFAULT_USERS_SPACE + XObjectDocument.SPACE_DOC_SEPARATOR)) {
            return new DocumentReference(xWikiContext.getDatabase(),
                    DEFAULT_USERS_SPACE,
                    userName.replaceFirst(DEFAULT_USERS_SPACE + XObjectDocument.SPACE_DOC_SEPARATOR, ""));
        } else {
            return new DocumentReference(xWikiContext.getDatabase(), DEFAULT_USERS_SPACE, userName);
        }
    }
}
