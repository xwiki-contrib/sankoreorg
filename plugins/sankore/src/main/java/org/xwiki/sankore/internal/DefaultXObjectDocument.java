/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xwiki.sankore.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.model.reference.DocumentReference;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.*;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.objects.BaseProperty;

/**
 * Default implementation of XObjectDocument. This class manage an XWiki document containing provided XWiki class. It
 * add some specifics methods, getters and setters for this type of object and fields. It also override {@link Document}
 * (and then {@link XWikiDocument}) isNew concept considering as new a document that does not contains an XWiki object
 * of the provided XWiki class.
 * 
 * @version $Id: a464b3a6d93586631fbddc3bfa55e2b1c3bdb2c5 $
 * @see XObjectDocument
 * @see XObjectDocumentClass
 * @since Application Manager 1.0RC1
 */
public class DefaultXObjectDocument extends Document implements XObjectDocument
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultXObjectDocument.class);
    /**
     * Value in int for {@link Boolean#TRUE}.
     */
    private static final int BOOLEAN_TRUE = 1;

    /**
     * Value in int for {@link Boolean#FALSE}.
     */
    private static final int BOOLEAN_FALSE = 0;

    /**
     * true if this is a new document of this class (this document can exist but does not contains object of this
     * class).
     */
    protected boolean isNew;

    protected XObjectDocumentClass xClass;

    protected BaseObject xObject;

    /**
     * Create instance of DefaultXObjectDocument from provided XWikiDocument.
     * 
     * @param xClass the class manager for this document.
     * @param doc the XWikiDocument to manage.
     * @param obj the id of the XWiki object included in the document to manage.
     * @param context the XWiki context.
     * @throws XWikiException error when calling.
     */
    public DefaultXObjectDocument(XObjectDocumentClass<? extends XObjectDocument> xClass, XWikiDocument doc, BaseObject obj, XWikiContext context) throws XWikiException
    {
        super(doc, context);
        this.xClass = xClass;
        this.xObject = obj;
        this.isNew = false;
    }

    protected BaseObject getXObject()
    {
        if (!this.cloned) {
            this.doc = this.doc.clone();
            this.xObject = this.doc.getXObject(this.xClass.getClassDocumentReference(), this.xObject.getNumber());
            this.cloned = true;
        }

        return this.xObject;
    }

    @Override
    protected XWikiDocument getDoc()
    {
        if (!this.cloned) {
            this.doc = this.doc.clone();
            this.xObject = this.doc.getXObject(this.xClass.getClassDocumentReference(), this.xObject.getNumber());
            this.cloned = true;
        }

        return this.doc;
    }

    /**
     * {@inheritDoc}
     * 
     * @see XObjectDocument#getXClassManager()
     */
    public XObjectDocumentClass getXClassManager()
    {
        return this.xClass;
    }

    public DocumentReference getClassDocumentReference()
    {
        return this.xClass.getClassDocumentReference();
    }

    /**
     * {@inheritDoc}
     * 
     * @see XObjectDocument#isNew()
     */
    @Override
    public boolean isNew()
    {
        return super.isNew() || this.isNew;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.xpn.xwiki.api.Document#saveDocument(java.lang.String, boolean)
     */
    @Override
    protected void saveDocument(String comment, boolean minorEdit) throws XWikiException
    {
        super.saveDocument(comment, minorEdit);
        this.isNew = false;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.xpn.xwiki.api.Document#delete()
     */
    @Override
    public void delete() throws XWikiException
    {
        // TODO TEST
        if (this.xObject == null || this.xClass == null) {
            LOGGER.info("null encontered: " + this.xObject + "; " + this.xClass);
            return;
        }

        if (this.doc.getXObjectSize(this.xClass.getClassDocumentReference()) == 1) {
            LOGGER.info("delete() with 1 object: " + this.getDocumentReference().toString());
            super.deleteDocument();
        } else {
            LOGGER.info("delete() more than 1 object: " + this.xObject + " : " + this.xClass.getClassDocumentReference().toString());
            this.doc.removeXObject(this.xObject);
            saveDocument("ObjectDocument deleted.", false);
        }

        this.isNew = true;
        this.xObject = this.doc.getXObject(this.xClass.getClassDocumentReference(), true, this.context);
    }

    /**
     * Get the value of the field <code>fieldName</code> of the managed object's class.
     * 
     * @param fieldName the name of the field from the managed object's class where to find the value.
     * @return the value in {@link String} of the field <code>fieldName</code> of the managed object's class.
     * @see com.xpn.xwiki.doc.XWikiDocument#getStringValue(java.lang.String)
     */
    public String getStringValue(String fieldName)
    {
        return this.xObject.getStringValue(fieldName);
    }

    /**
     * Modify the value of the field <code>fieldName</code> of the managed object's class.
     * <p>
     * This method makes sure the right property type between LargeStringProperty and StringProperty is used.
     * 
     * @param fieldName the name of the field from the managed object's class where to find the value.
     * @param value the new value of the field <code>fieldName</code> of the managed object's class.
     * @see com.xpn.xwiki.doc.XWikiDocument#setStringValue(java.lang.String,java.lang.String,java.lang.String)
     */
    public void setStringValue(String fieldName, String value)
    {
        getXObject().setStringValue(fieldName,  value);
    }

    /**
     * Get the value of the field <code>fieldName</code> of the managed object's class.
     * 
     * @param fieldName the name of the field from the managed object's class where to find the value.
     * @return the value in {@link String} of the field <code>fieldName</code> of the managed object's class.
     * @see com.xpn.xwiki.doc.XWikiDocument#getStringValue(java.lang.String)
     * @deprecated Use {@link #getStringValue(String)} which support LargeStringProperty and StringProperty.
     */
    @Deprecated
    public String getLargeStringValue(String fieldName)
    {
        return this.xObject.getLargeStringValue(fieldName);
    }

    /**
     * Modify the value of the field <code>fieldName</code> of the managed object's class.
     * <p>
     * This method makes sure the right property type between LargeStringProperty and StringProperty is used.
     * 
     * @param fieldName the name of the field from the managed object's class where to find the value.
     * @param value the new value of the field <code>fieldName</code> of the managed object's class.
     * @see com.xpn.xwiki.doc.XWikiDocument#setLargeStringValue(java.lang.String,java.lang.String,java.lang.String)
     * @deprecated Use {@link #setStringValue(String, String)} which support LargeStringProperty and StringProperty.
     */
    @Deprecated
    public void setLargeStringValue(String fieldName, String value)
    {
        getXObject().setLargeStringValue(fieldName, value);
    }

    /**
     * Get the value of the field <code>fieldName</code> of the managed object's class.
     * 
     * @param fieldName the name of the field from the managed object's class where to find the value.
     * @return the value in {@link List} of the field <code>fieldName</code> of the managed object's class.
     * @see com.xpn.xwiki.doc.XWikiDocument#getListValue(java.lang.String)
     * @since 1.4
     */
    public List<String> getStringListValue(String fieldName)
    {
        return (List<String>) this.xObject.getListValue(fieldName);
    }

    /**
     * Modify the value of the field <code>fieldName</code> of the managed object's class.
     * 
     * @param fieldName the name of the field from the managed object's class where to find the value.
     * @param value the new value of the field <code>fieldName</code> of the managed object's class.
     * @see com.xpn.xwiki.doc.XWikiDocument#setStringListValue(java.lang.String,java.lang.String,java.util.List)
     * @since 1.4
     */
    public void setStringListValue(String fieldName, List<String> value)
    {
        getXObject().setListValue(fieldName, value);
    }

    /**
     * Get the value of the field <code>fieldName</code> of the managed object's class.
     * 
     * @param fieldName the name of the field from the managed object's class where to find the value.
     * @return the value in {@link List} of the field <code>fieldName</code> of the managed object's class.
     * @see com.xpn.xwiki.doc.XWikiDocument#getListValue(java.lang.String)
     * @deprecated Use {@link #getStringListValue(String)} instead. Since 1.4.
     */
    @Deprecated
    public List getListValue(String fieldName)
    {
        return this.xObject.getListValue(fieldName);
    }

    /**
     * Modify the value of the field <code>fieldName</code> of the managed object's class.
     * 
     * @param fieldName the name of the field from the managed object's class where to find the value.
     * @param value the new value of the field <code>fieldName</code> of the managed object's class.
     * @see com.xpn.xwiki.doc.XWikiDocument#setStringListValue(java.lang.String,java.lang.String,java.util.List)
     * @deprecated Use {@link #getStringListValue(String)} instead. Since 1.4.
     */
    @Deprecated
    public void setListValue(String fieldName, List value)
    {
        getXObject().setListValue(fieldName, value);
    }

    /**
     * Get the value of the field <code>fieldName</code> of the managed object's class.
     * 
     * @param fieldName the name of the field from the managed object's class where to find the value.
     * @return the value in int of the field <code>fieldName</code> of the managed object's class.
     * @see com.xpn.xwiki.doc.XWikiDocument#getListValue(java.lang.String)
     */
    public int getIntValue(String fieldName)
    {
        return this.xObject.getIntValue(fieldName);
    }

    /**
     * Modify the value of the field <code>fieldName</code> of the managed object's class.
     * 
     * @param fieldName the name of the field from the managed object's class where to find the value.
     * @param value the new value of the field <code>fieldName</code> of the managed object's class.
     * @see com.xpn.xwiki.doc.XWikiDocument#setIntValue(String, String, int)
     */
    public void setIntValue(String fieldName, int value)
    {
        getXObject().setIntValue(fieldName, value);
    }

    /**
     * Get the value of the field <code>fieldName</code> of the managed object's class.
     * 
     * @param fieldName the name of the field from the managed object's class where to find the value.
     * @return the value in {@link Boolean} of the field <code>fieldName</code> of the managed object's class.
     * @see com.xpn.xwiki.doc.XWikiDocument#getListValue(java.lang.String)
     */
    public Boolean getBooleanValue(String fieldName)
    {
        int intValue = getIntValue(fieldName);

        return intValue == BOOLEAN_TRUE ? Boolean.TRUE : (intValue == BOOLEAN_FALSE ? Boolean.FALSE : null);
    }

    /**
     * Modify the value of the field <code>fieldName</code> of the managed object's class.
     * 
     * @param fieldName the name of the field from the managed object's class where to find the value.
     * @param value the new value of the field <code>fieldName</code> of the managed object's class.
     * @see com.xpn.xwiki.doc.XWikiDocument#setIntValue(String, String, int)
     */
    public void setBooleanValue(String fieldName, Boolean value)
    {
        setIntValue(fieldName, value == null ? BOOLEAN_FALSE : (value ? BOOLEAN_TRUE : BOOLEAN_FALSE));
    }

    public Date getDateValue(String fieldName)
    {
        return this.xObject.getDateValue(fieldName);
    }

    public void setDateValue(String fieldName, Date value)
    {
        getXObject().setDateValue(fieldName, value);
    }

    public void updateFromRequest() throws XWikiException
    {
        //LOGGER.info("updateFromRequest() className: " + this.xClass.toString());
        getDoc().updateXObjectFromRequest(this.xClass.getClassDocumentReference(), getXWikiContext());
        //updateObjectFromRequest(this.xClass.toString());
        // TODO
        //this.updateObjectFromRequest(this.xClassManager.getClassFullName());
    }

    public List<Property> getProperties()
            throws XWikiException
    {
        List<Property> properties = new ArrayList<Property>();
        for (String propName : this.xObject.getPropertyNames()) {
            properties.add(new Property( (BaseProperty)this.xObject.get(propName), getXWikiContext()));
        }
        return properties;
    }
}
