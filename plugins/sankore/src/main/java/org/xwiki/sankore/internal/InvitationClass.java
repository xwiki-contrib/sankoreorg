package org.xwiki.sankore.internal;

import java.util.ArrayList;
import java.util.List;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.xwiki.context.ExecutionContext;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.sankore.ContextUtils;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.classes.BaseClass;
import com.xpn.xwiki.plugin.applicationmanager.core.doc.objects.classes.AbstractXClassManager;
import com.xpn.xwiki.plugin.applicationmanager.core.doc.objects.classes.XObjectDocumentDoesNotExistException;

/**
 * Created with IntelliJ IDEA. User: XWIKI Date: 6/1/12 Time: 1:12 AM To change this template use File | Settings | File
 * Templates.
 */
public class InvitationClass extends AbstractXClassManager<InvitationXObjectDocument>
{

    public static final int OBJECTID = 0;

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

    /**
     * Space of class document.
     */
    public static final String CLASS_SPACE = "XWiki";

    /**
     * Prefix of class document.
     */
    public static final String CLASS_PREFIX = "Invitation";

    /**
     * Unique instance of XWikiSpaceClass.
     */
    private static InvitationClass instance;

    /**
     * Default constructor for XWikiSpaceClass.
     */
    protected InvitationClass()
    {
        super(CLASS_SPACE, CLASS_PREFIX, false);
    }

    public static InvitationClass getInstance(ExecutionContext executionContext) throws XWikiException
    {
        return InvitationClass.getInstance(ContextUtils.getXWikiContext(executionContext));
    }

    /**
     * Return unique instance of SpaceClass and update documents for this context.
     *
     * @param xwikiContext Context.
     * @return SpaceClass instance.
     * @throws XWikiException error when checking for class, class template and class sheet.
     */
    public static InvitationClass getInstance(XWikiContext xwikiContext) throws XWikiException
    {
        synchronized (InvitationClass.class) {
            if (instance == null) {
                instance = new InvitationClass();
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
        needsUpdate |= baseClass.addDBListField(FIELD_ROLES, FIELDPN_ROLES, 1, true, true, FIELDQ_ROLES);
        needsUpdate |= baseClass.addTextField(FIELD_INVITEE, FIELDPN_INVITEE, 30);
        needsUpdate |= baseClass.addTextField(FIELD_INVITER, FIELDPN_INVITER, 30);
        needsUpdate |= baseClass.addTextField(FIELD_KEY, FIELDPN_KEY, 30);

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
    public InvitationXObjectDocument newXObjectDocument(XWikiDocument xWikiDocument, int objId, XWikiContext xWikiContext) throws XWikiException
    {
        if ((objId > 0 && xWikiDocument.getXObjectSize(getClassDocument(xWikiContext).getDocumentReference()) < objId) || objId == 0)
            return new InvitationXObjectDocument(xWikiDocument, objId, xWikiContext);

        return null;
    }

    public InvitationXObjectDocument newXObjectDocument(XWikiDocument xWikiDocument, XWikiContext xWikiContext) throws XWikiException
    {
        return newXObjectDocument(xWikiDocument, 0, xWikiContext);
    }

    public InvitationXObjectDocument newXObjectDocument(String invitee, SpaceReference spaceReference, XWikiContext xWikiContext) throws XWikiException
    {
        DocumentReference documentReference = new DocumentReference(invitee, spaceReference);
        XWikiDocument xWikiDocument = xWikiContext.getWiki().getDocument(documentReference, xWikiContext);

        return newXObjectDocument(xWikiDocument, xWikiContext);
    }

    public InvitationXObjectDocument getXObjectDocument(String invitee, SpaceReference spaceReference, XWikiContext xWikiContext) throws XWikiException
    {
        DocumentReference documentReference = new DocumentReference(invitee, spaceReference);
        XWikiDocument xWikiDocument = xWikiContext.getWiki().getDocument(documentReference, xWikiContext);

        int objId = xWikiDocument.getXObjectSize(this.getClassDocument(xWikiContext).getDocumentReference());

        if (xWikiDocument.isNew() || objId <= 0 ) {
            throw new XObjectDocumentDoesNotExistException(documentReference.toString() + " object document does not exist");
        }

        return new InvitationXObjectDocument(xWikiDocument, objId, xWikiContext);
    }

    public List<InvitationXObjectDocument> getXObjectDocuments(String invitee, SpaceReference spaceReference, XWikiContext xWikiContext) throws XWikiException
    {
        DocumentReference documentReference = new DocumentReference(invitee, spaceReference);
        XWikiDocument xWikiDocument = xWikiContext.getWiki().getDocument(documentReference, xWikiContext);

        return getXObjectDocuments(xWikiDocument, xWikiContext);

    }

    public InvitationXObjectDocument getXObjectDocument(XWikiDocument xWikiDocument, int objId, XWikiContext xWikiContext) throws XWikiException
    {
        if (objId > 0 && xWikiDocument.getXObjectSize(this.getClassDocument(xWikiContext).getDocumentReference()) >= objId)
            return new InvitationXObjectDocument(xWikiDocument, objId, xWikiContext);

        return null;
    }

    public List<InvitationXObjectDocument> getXObjectDocuments(XWikiDocument xWikiDocument, XWikiContext xWikiContext) throws XWikiException
    {
        List<InvitationXObjectDocument> xObjectDocuments = new ArrayList<InvitationXObjectDocument>();
        if (xWikiDocument.getXObjectSize(this.getClassDocument(xWikiContext).getDocumentReference()) > 0) {
            List<BaseObject> xObjects = xWikiDocument.getXObjects(this.getClassDocument(xWikiContext).getDocumentReference());
            for (BaseObject xObject : xObjects) {
                xObjectDocuments.add(new InvitationXObjectDocument(xWikiDocument, xObject.getNumber(), xWikiContext));
            }
        }

        return xObjectDocuments;
    }

    @Override
    public List<InvitationXObjectDocument> searchXObjectDocumentsByFields(Object[][] fieldDescriptors, XWikiContext context)
            throws XWikiException
    {
        List<Object> parameterValues = new ArrayList<Object>();
        String where = createWhereClause(fieldDescriptors, parameterValues);

        return newXObjectDocumentList(context.getWiki().getStore().searchDocuments(where, parameterValues, context),
                context);
    }
}
