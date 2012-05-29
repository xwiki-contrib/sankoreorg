package org.xwiki.sankore;

import java.util.List;

import org.xwiki.component.annotation.ComponentRole;
import org.xwiki.model.reference.SpaceReference;

import com.xpn.xwiki.XWikiException;

@ComponentRole
public interface SpaceManager
{
    public Space getSpace(SpaceReference reference) throws XWikiException;

    public Space createSpace(SpaceReference reference) throws XWikiException;

    public void copySpace(SpaceReference spaceReference, SpaceReference newReference, boolean doUpdateParents) throws XWikiException;

    public Space createSpaceFromTemplate(SpaceReference spaceReference, SpaceReference templateReference) throws XWikiException;


    public Space createSpaceFromRequest();

    public Space updateSpaceFromRequest();

    public SpaceReference createSpaceReference(String wikiName, String spaceName);

    public int countDocuments(SpaceReference spaceReference);

    public List<String> searchDocumentsNames(SpaceReference spaceReference, int nb, int start);
    public List<String> searchDocumentsNames(SpaceReference spaceReference, String whereHql, int nb, int start);
}


