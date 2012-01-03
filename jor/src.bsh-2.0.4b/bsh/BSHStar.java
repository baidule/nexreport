package bsh;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class BSHStar extends SimpleNode {
    /**
     * DOCUMENT ME!
     */
    public final static Object STAR = new Object();

    BSHStar(int id) {
        super(id);
    }

    /**
     * DOCUMENT ME!
     *
     * @param callstack DOCUMENT ME!
     * @param it DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws EvalError DOCUMENT ME!
     */
    public Object eval(CallStack callstack, Interpreter it)
        throws EvalError {
        return STAR;
    }
}
