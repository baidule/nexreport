package bsh;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public interface Filter {
    /**
     * DOCUMENT ME!
     *
     * @param callstack DOCUMENT ME!
     * @param interpreter DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws EvalError DOCUMENT ME!
     */
    public Object filter(CallStack callstack, Interpreter interpreter, bsh.Node parameterNode)
        throws EvalError;
}
