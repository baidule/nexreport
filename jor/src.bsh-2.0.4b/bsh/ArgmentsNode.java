package bsh;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public abstract class ArgmentsNode extends SimpleNode {
    /**
     * Creates a new ArgmentsNode object.
     *
     * @param i DOCUMENT ME!
     */
    public ArgmentsNode(int i) {
        super(i);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract BSHArguments getArgsNode();
}
