package bsh;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public interface Column extends Index, Filter {
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object sum();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object min();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object max();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object avg();



    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Column group();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Column group_desc();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Column order();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Column order_desc();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Column rank();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Column rank_desc();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object[] values();
}
