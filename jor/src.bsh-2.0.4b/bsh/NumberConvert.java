package bsh;

import java.math.BigDecimal;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class NumberConvert {
    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Object toBigDecimal(Object o) {
        if (o instanceof BigDecimal) {
            return o;
        }

        if (o instanceof Number) {
            return new BigDecimal(((Number) o).doubleValue());
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Object toDoublel(Object o) {
        if (o instanceof Double) {
            return o;
        }

        if (o instanceof Number) {
            return new Double(((Number) o).doubleValue());
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Object toFloat(Object o) {
        if (o instanceof Float) {
            return o;
        }

        if (o instanceof Number) {
            return new Float(((Number) o).floatValue());
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Object toLong(Object o) {
        if (o instanceof Long) {
            return o;
        }

        if (o instanceof Number) {
            return new Long(((Number) o).longValue());
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Object toShort(Object o) {
        if (o instanceof Short) {
            return o;
        }

        if (o instanceof Number) {
            return new Short(((Number) o).shortValue());
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param o DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Object toInteger(Object o) {
        if (o instanceof Integer) {
            return o;
        }

        if (o instanceof Number) {
            return new Integer(((Number) o).intValue());
        }

        return null;
    }
}
