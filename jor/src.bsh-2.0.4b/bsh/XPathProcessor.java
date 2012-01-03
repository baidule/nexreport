package bsh;

import jatools.accessor.ProtectPublic;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public interface XPathProcessor extends ProtectPublic{
    /**
     * DOCUMENT ME!
     *
     * @param path DOCUMENT ME!
     * @param callstack DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object selectNode(String path, CallStack callstack);
}
