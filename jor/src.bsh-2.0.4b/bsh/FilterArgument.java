package bsh;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public interface FilterArgument {
    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param callstack DOCUMENT ME!
     * @param interpreter DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object toObject(String name, CallStack callstack, Interpreter interpreter);
}
