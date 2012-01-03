package bsh;

import java.lang.reflect.InvocationTargetException;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public interface MethodInvoker {
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object invoke(Object object, String methodName, Interpreter interpreter,
        CallStack callstack, ArgmentsNode callerInfo)
        throws ReflectError, EvalError, InvocationTargetException;
}
