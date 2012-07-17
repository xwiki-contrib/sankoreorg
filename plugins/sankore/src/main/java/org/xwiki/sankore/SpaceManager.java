package org.xwiki.sankore;

import java.util.List;

import org.xwiki.component.annotation.ComponentRole;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.sankore.internal.SpaceObjectDocument;

import com.xpn.xwiki.XWikiException;

@ComponentRole
public interface SpaceManager
{
    public SpaceObjectDocument getSpace(SpaceReference reference) throws XWikiException;

    public SpaceObjectDocument createSpace(SpaceReference reference) throws XWikiException;

    public void copySpace(SpaceReference spaceReference, SpaceReference newReference, boolean doUpdateParents) throws XWikiException;

    public SpaceObjectDocument createSpaceFromTemplate(SpaceReference spaceReference, SpaceReference templateReference) throws XWikiException;

    public SpaceObjectDocument createSpaceFromRequest();

    public SpaceObjectDocument updateSpaceFromRequest();

    public SpaceReference createSpaceReference(String wikiName, String spaceName);

    public int countDocuments(SpaceReference spaceReference);

    public List<String> searchDocumentsNames(SpaceReference spaceReference, int nb, int start);
    public List<String> searchDocumentsNames(SpaceReference spaceReference, String whereHql, int nb, int start);
}


