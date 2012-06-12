package org.xwiki.sankore.internal;

import org.xwiki.context.ExecutionContext;
import org.xwiki.sankore.ContextUtils;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.plugin.applicationmanager.core.doc.objects.classes.AbstractXClassManager;

/**
 * Created with IntelliJ IDEA. User: XWIKI Date: 6/1/12 Time: 2:26 PM To change this template use File | Settings | File
 * Templates.
 */
public class MembershipRequestClass extends AbstractXClassManager<MembershipRequestXObjectDocument>
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

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_STATUS = "Status";

    public static final String FIELD_STATUS_NONE = "0";
    public static final String FIELD_STATUS_CREATED = "1";
    public static final String FIELD_STATUS_SENT = "2";
    public static final String FIELD_STATUS_ACCEPTED = "3";
    public static final String FIELD_STATUS_REFUSED = "4";
    public static final String FIELD_STATUS_CANCELED = "5";

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

    /**
     * Name of field <code>urlshortcut</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELD_RESPONDER = "responder";

    /**
     * Pretty name of field <code>type</code> for the XWiki class XWiki.XWikiSpaceClass.
     */
    public static final String FIELDPN_RESPONDER = "Responder";

    /**
     * Space of class document.
     */
    public static final String CLASS_SPACE = "XWiki";

    /**
     * Prefix of class document.
     */
    public static final String CLASS_PREFIX = "MembershipRequest";

    /**
     * Unique instance of XWikiSpaceClass.
     */
    private static MembershipRequestClass instance;

    /**
     * Default constructor for XWikiSpaceClass.
     */
    protected MembershipRequestClass()
    {
        super(CLASS_SPACE, CLASS_PREFIX, false);
    }

    public static MembershipRequestClass getInstance(ExecutionContext executionContext) throws XWikiException
    {
        return MembershipRequestClass.getInstance(ContextUtils.getXWikiContext(executionContext));
    }

    /**
     * Return unique instance of SpaceClass and update documents for this context.
     *
     * @param xwikiContext Context.
     * @return SpaceClass instance.
     * @throws XWikiException error when checking for class, class template and class sheet.
     */
    public static MembershipRequestClass getInstance(XWikiContext xwikiContext) throws XWikiException
    {
        synchronized (MembershipRequestClass.class) {
            if (instance == null) {
                instance = new MembershipRequestClass();
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

        needsUpdate |= baseClass.addDateField(FIELD_REQUESTDATE, FIELDPN_REQUESTDATE);
        needsUpdate |= baseClass.addDateField(FIELD_RESPONSEDATE, FIELDPN_RESPONSEDATE);
        needsUpdate |= baseClass.addTextAreaField(FIELD_TEXT, FIELDPN_TEXT, 40, 5);
        needsUpdate |= baseClass.addTextAreaField(FIELD_MAP, FIELDPN_MAP, 40, 5);
        needsUpdate |= baseClass.addTextField(FIELD_GROUP, FIELDPN_GROUP, 30);
        needsUpdate |= baseClass.addStaticListField(FIELD_STATUS, FIELDPN_STATUS, 1, false, FIELDL_STATUS);
        needsUpdate |= baseClass.addTextField(FIELD_REQUESTER, FIELDPN_REQUESTER, 30);
        needsUpdate |= baseClass.addTextField(FIELD_RESPONDER, FIELDPN_RESPONDER, 30);

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
    public MembershipRequestXObjectDocument newXObjectDocument(XWikiDocument xdoc, int objId, XWikiContext context) throws XWikiException
    {
        return new MembershipRequestXObjectDocument(xdoc, objId, context);
    }
}
