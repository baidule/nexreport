package bsh;

import java.lang.reflect.InvocationTargetException;


class BSHMethodInvocation extends ArgmentsNode {
    BSHMethodInvocation(int id) {
        super(id);
    }

    BSHAmbiguousName getNameNode() {
        return (BSHAmbiguousName) jjtGetChild(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public BSHArguments getArgsNode() {
        return (BSHArguments) jjtGetChild(1);
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
     * @throws TargetError DOCUMENT ME!
     */
    public Object eval(CallStack callstack, Interpreter interpreter)
        throws EvalError {
        NameSpace namespace = callstack.top();
        BSHAmbiguousName nameNode = getNameNode();

        if ((namespace.getParent() != null) && namespace.getParent().isClass &&
                (nameNode.text.equals("super") || nameNode.text.equals("this"))) {
            return Primitive.VOID;
        }

        Name name = nameNode.getName(namespace);

        try {
            return name.invokeMethod(interpreter, callstack, this);
        } catch (ReflectError e) {
            throw new EvalError("Error in method invocation: " + e.getMessage(), this, callstack);
        } catch (InvocationTargetException e) {
            e.printStackTrace();

            String msg = "Method Invocation " + name;
            Throwable te = e.getTargetException();

            /*
                    Try to squeltch the native code stack trace if the exception
                    was caused by a reflective call back into the bsh interpreter
                    (e.g. eval() or source()
            */
            boolean isNative = true;

            if (te instanceof EvalError) {
                if (te instanceof TargetError) {
                    isNative = ((TargetError) te).inNativeCode();
                } else {
                    isNative = false;
                }
            }

            throw new TargetError(msg, te, this, callstack, isNative);
        } catch (UtilEvalError e) {
            throw e.toEvalError(this, callstack);
        }
    }
}
