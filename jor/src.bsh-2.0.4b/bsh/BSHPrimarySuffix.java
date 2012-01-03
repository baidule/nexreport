package bsh;

import jatools.engine.InterpreterAware;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class BSHPrimarySuffix extends ArgmentsNode {
    public static final int CLASS = 0;
    public static final int INDEX = 1;
    public static final int NAME = 2;
    public static final int PROPERTY = 3;
    public static final int EQUAL_PROPERTY = 5;
    public static final int PATH = 4;
    public int operation;
    Object index;
    public String field;

    BSHPrimarySuffix(int id) {
        super(id);
    }

    /*
            Perform a suffix operation on the given object and return the
            new value.
            <p>
    
            obj will be a Node when suffix evaluation begins, allowing us to
            interpret it contextually. (e.g. for .class) Thereafter it will be
            an value object or LHS (as determined by toLHS).
            <p>
    
            We must handle the toLHS case at each point here.
            <p>
    */

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     * @param toLHS DOCUMENT ME!
     * @param callstack DOCUMENT ME!
     * @param interpreter DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws EvalError DOCUMENT ME!
     * @throws TargetError DOCUMENT ME!
     */
    public Object doSuffix(Object obj, boolean toLHS, CallStack callstack, Interpreter interpreter)
        throws EvalError {
        if (operation == CLASS) {
            if (obj instanceof BSHType) {
                if (toLHS) {
                    throw new EvalError("Can't assign .class", this, callstack);
                }

                NameSpace namespace = callstack.top();

                return ((BSHType) obj).getType(callstack, interpreter);
            } else {
                throw new EvalError("Attempt to use .class suffix on non class.", this, callstack);
            }
        }

        /*
                Evaluate our prefix if it needs evaluating first.
                If this is the first evaluation our prefix mayb be a Node
                (directly from the PrimaryPrefix) - eval() it to an object.
                If it's an LHS, resolve to a value.
        
                Note: The ambiguous name construct is now necessary where the node
                may be an ambiguous name.  If this becomes common we might want to
                make a static method nodeToObject() or something.  The point is
                that we can't just eval() - we need to direct the evaluation to
                the context sensitive type of result; namely object, class, etc.
        */
        String tmpname = null;

        if (obj instanceof SimpleNode) {
            if (obj instanceof BSHAmbiguousName) {
                tmpname = ((BSHAmbiguousName) obj).text;

                try {
                    obj = ((BSHAmbiguousName) obj).toObject(callstack, interpreter, false);
                } catch (EvalError e) {
                    if (operation == PROPERTY) {
                        obj = Primitive.VOID;
                    } else {
                        throw e;
                    }
                }
            } else {
                obj = ((SimpleNode) obj).eval(callstack, interpreter);
            }
        } else if (obj instanceof LHS) {
            try {
                obj = ((LHS) obj).getValue();
            } catch (UtilEvalError e) {
                throw e.toEvalError(this, callstack);
            }
        }

        if (operation == PROPERTY) {
            if ((tmpname != null) && (obj == Primitive.VOID)) {
                return doVoidProperty(tmpname, callstack, interpreter);
            } else if (obj instanceof Filter) {
                Node n = (jjtGetNumChildren() == 0) ? null : (Node) jjtGetChild(0);

                return ((Filter) obj).filter(callstack, interpreter, n);
            }
        }

        try {
            switch (operation) {
            case INDEX:
                return doIndex(obj, toLHS, callstack, interpreter);

            case NAME:
                return doName(obj, toLHS, callstack, interpreter);

            case PROPERTY:
                return doProperty(toLHS, obj, callstack, interpreter);

            case PATH:
                return doPath(obj, toLHS, callstack, interpreter);

            default:
                throw new InterpreterError("Unknown suffix type");
            }
        } catch (ReflectError e) {
            throw new EvalError("reflection error: " + e, this, callstack);
        } catch (InvocationTargetException e) {
            throw new TargetError("target exception", e.getTargetException(), this, callstack, true);
        }
    }

    private Object doVoidProperty(String tmpname, CallStack callstack, Interpreter interpreter)
        throws EvalError {
        Node n = jjtGetChild(0);

        if (n instanceof BSHArguments) {
            BSHArguments args = (BSHArguments) n;

            if (args.jjtGetNumChildren() == 1) {
                Object obj = args.getArguments(callstack, interpreter)[0];

                if (obj instanceof FilterArgument) {
                    FilterArgument fa = (FilterArgument) obj;

                    return fa.toObject(tmpname, callstack, interpreter);
                }
            }
        }

        return null;
    }

    /*
        Field access, .length on array, or a method invocation
        Must handle toLHS case for each.
    */
    private Object doName(Object obj, boolean toLHS, CallStack callstack, Interpreter interpreter)
        throws EvalError, ReflectError, InvocationTargetException {
        try {
            if (field.equals("length") && obj.getClass().isArray()) {
                if (toLHS) {
                    throw new EvalError("Can't assign array length", this, callstack);
                } else {
                    return new Primitive(Array.getLength(obj));
                }
            }

            if (jjtGetNumChildren() == 0) {
                if (obj instanceof PropertyGetter) {
                    Object tmp = ((PropertyGetter) obj).getProperty(field, callstack, interpreter);

                    if (tmp != Primitive.VOID) {
                        return tmp;
                    }
                }

                if (toLHS) {
                    return Reflect.getLHSObjectField(obj, field);
                } else {
                    return Reflect.getObjectFieldValue(obj, field);
                }
            }

            try {
                if (obj instanceof InterpreterAware) {
                    ((InterpreterAware) obj).setInterpreter(interpreter);
                }

                return Reflect.invokeObjectMethod(obj, field,
                    getArgsNode().getArguments(callstack, interpreter), interpreter, callstack, this);
            } catch (ReflectError e) {
                throw new EvalError("Error in method invocation: " + e.getMessage(), this, callstack);
            } catch (InvocationTargetException e) {
                String msg = "Method Invocation " + field;
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
            }
        } catch (UtilEvalError e) {
            throw e.toEvalError(this, callstack);
        }
    }

    static int getIndexAux(Object obj, CallStack callstack, Interpreter interpreter,
        SimpleNode callerInfo) throws EvalError {
        int index;

        try {
            Object indexVal = ((SimpleNode) callerInfo.jjtGetChild(0)).eval(callstack, interpreter);

            if (!(indexVal instanceof Primitive)) {
                indexVal = Types.castObject(indexVal, Integer.TYPE, Types.ASSIGNMENT);
            }

            index = ((Primitive) indexVal).intValue();
        } catch (UtilEvalError e) {
            Interpreter.debug("doIndex: " + e);
            throw e.toEvalError("Arrays may only be indexed by integer types.", callerInfo,
                callstack);
        }

        return index;
    }

    private Object doIndex(Object obj, boolean toLHS, CallStack callstack, Interpreter interpreter)
        throws EvalError, ReflectError {
        int index = getIndexAux(obj, callstack, interpreter, this);

        if (obj instanceof Index) {
            Index idx = (Index) obj;

            return idx.get(index);
        } else if (toLHS) {
            return new LHS(obj, index);
        } else {
            try {
                return Reflect.getIndex(obj, index);
            } catch (UtilEvalError e) {
                throw e.toEvalError(this, callstack);
            }
        }
    }

    private Object doProperty(boolean toLHS, Object obj, CallStack callstack,
        Interpreter interpreter) throws EvalError {
        if (obj == Primitive.VOID) {
            throw new EvalError("Attempt to access property on undefined variable or class name",
                this, callstack);
        }

        if (obj instanceof Primitive) {
            throw new EvalError("Attempt to access property on a primitive", this, callstack);
        }

        Object value = ((SimpleNode) jjtGetChild(0)).eval(callstack, interpreter);

        if (!(value instanceof String)) {
            throw new EvalError("Property expression must be a String or identifier.", this,
                callstack);
        }

        if (toLHS) {
            return new LHS(obj, (String) value);
        }

        CollectionManager cm = CollectionManager.getCollectionManager();

        if (cm.isMap(obj)) {
            Object val = cm.getFromMap(obj, value /*key*/);

            return ((val == null) ? (val = Primitive.NULL) : val);
        }

        try {
            return Reflect.getObjectProperty(obj, (String) value);
        } catch (UtilEvalError e) {
            throw e.toEvalError("Property: " + value, this, callstack);
        } catch (ReflectError e) {
            throw new EvalError("No such property: " + value, this, callstack);
        }
    }

    private Object doPath(Object obj, boolean toLHS, CallStack callstack, Interpreter it)
        throws EvalError, ReflectError {
        if (obj instanceof Path) {
            Path path = (Path) obj;

            return path.path(this.field);
        } else {
            return new EvalError("不是Path对象,不能使用路径(\\)操作符. " + this.field, this, callstack);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public BSHArguments getArgsNode() {
        return (BSHArguments) jjtGetChild(0);
    }
}
