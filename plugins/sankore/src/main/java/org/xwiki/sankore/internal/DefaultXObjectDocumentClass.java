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

import org.xwiki.model.reference.DocumentReference;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Api;
import com.xpn.xwiki.objects.classes.BaseClass;

public class DefaultXObjectDocumentClass<T extends XObjectDocument> extends Api implements XObjectDocumentClass<T>
{
    protected BaseClass xClass;

    public DefaultXObjectDocumentClass(DocumentReference classReference, XWikiContext context) throws XWikiException
    {
        super(context);
        this.xClass = context.getWiki().getXClass(classReference, context);
    }

    public DocumentReference getClassDocumentReference()
    {
        return new DocumentReference(this.xClass.getDocumentReference());
    }

    public DocumentReference getSheetDocumentRefence()
    {
        return new DocumentReference(this.xClass.getDocumentReference().getName() + "Sheet",
                this.xClass.getDocumentReference().getLastSpaceReference());
    }

    public DocumentReference getTemplateDocumentReference()
    {
        return new DocumentReference(this.xClass.getDocumentReference().getName() + "Template",
                this.xClass.getDocumentReference().getLastSpaceReference());
    }

    public BaseClass getXClass()
    {
        return this.xClass;
    }
}
