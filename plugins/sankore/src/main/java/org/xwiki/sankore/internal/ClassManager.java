package org.xwiki.sankore.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xwiki.component.annotation.ComponentRole;
import org.xwiki.model.reference.DocumentReference;

import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.api.Document;

@ComponentRole
public interface ClassManager<T extends XObjectDocument>
{
    T getDocumentObject(DocumentReference documentReference, int objectId) throws XWikiException;
    T getDocumentObject(DocumentReference documentReference) throws XWikiException;
    T newDocumentObject(DocumentReference documentReference) throws XWikiException;

    public DocumentReference getClassDocumentReference() throws XWikiException;
    //public DocumentReference getClassSheetDocumentReference() throws XWikiException;
    //public DocumentReference getClassTemplateDocumentReference() throws XWikiException;

    public List<T> searchDocumentObjectsByField(String fieldName, Object fieldValue)
            throws XWikiException;
    public List<T> searchDocumentObjectsByFields(Map<String, Object> fields)
            throws XWikiException;

    public void saveDocumentObject(T documentObject) throws XWikiException;
}
