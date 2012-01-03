package bsh;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class BSHColumn extends SimpleNode {
    boolean to;
    boolean step;

    BSHColumn(int id) {
        super(id);
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     * @param callstack DOCUMENT ME!
     * @param interpreter DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws EvalError DOCUMENT ME!
     */
    public Object eval(Class type, CallStack callstack, Interpreter interpreter)
        throws EvalError {
        return eval(callstack, interpreter);
    }

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
    public Object eval(CallStack callstack, Interpreter interpreter)
        throws EvalError {
        int numChildren = jjtGetNumChildren();

        Object[] vals = new Object[numChildren];

        for (int i = 0; i < numChildren; i++) {
            vals[i] = ((SimpleNode) jjtGetChild(i)).eval(callstack, interpreter);
            vals[i] = Primitive.unwrap(vals[i]);
        }

        if (to) {
            int _from = ((Number) vals[0]).intValue();
            int _to = ((Number) vals[1]).intValue();

            int _step = step ? ((Number) vals[2]).intValue() : 1;

            if (_step == 0) {
                throw new EvalError("步长(step)不能为 0", this, callstack);
            } else if (((_step > 0) && (_from > _to)) || ((_step < 0) && (_from < _to))) {
                throw new EvalError("非法列数据定义,开始/结束/步长(" + _from + "/" + _to + "/" + _step + ")",
                    this, callstack);
            } else {
                vals = new Object[(Math.abs(_to - _from) + 1) / Math.abs(_step)];

                for (int i = 0; i < vals.length; i++) {
                    vals[i] = new Integer(_from);
                    _from += _step;
                }

                return vals;
            }
        }

        return vals;
    }
}
