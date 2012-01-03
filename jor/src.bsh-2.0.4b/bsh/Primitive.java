package bsh;

import java.math.BigDecimal;

import java.util.Hashtable;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public final class Primitive implements ParserConstants, java.io.Serializable {
    static Hashtable wrapperMap = new Hashtable();

    static {
        wrapperMap.put(Boolean.TYPE, Boolean.class);
        wrapperMap.put(Byte.TYPE, Byte.class);
        wrapperMap.put(Short.TYPE, Short.class);
        wrapperMap.put(Character.TYPE, Character.class);
        wrapperMap.put(Integer.TYPE, Integer.class);
        wrapperMap.put(Long.TYPE, Long.class);
        wrapperMap.put(Float.TYPE, Float.class);
        wrapperMap.put(Double.TYPE, Double.class);
        wrapperMap.put(Boolean.class, Boolean.TYPE);
        wrapperMap.put(Byte.class, Byte.TYPE);
        wrapperMap.put(Short.class, Short.TYPE);
        wrapperMap.put(Character.class, Character.TYPE);
        wrapperMap.put(Integer.class, Integer.TYPE);
        wrapperMap.put(Long.class, Long.TYPE);
        wrapperMap.put(Float.class, Float.TYPE);
        wrapperMap.put(Double.class, Double.TYPE);
    }

    public static final Primitive NULL = new Primitive(Special.NULL_VALUE);
    public static final Primitive VOID = new Primitive(Special.VOID_TYPE);
    public static final Object VOID_OBJECT = new Object();
    
    public static final Primitive VOID2 = new Primitive(VOID_OBJECT);
    
    private Object value;

    /**
     * Creates a new Primitive object.
     *
     * @param value DOCUMENT ME!
     */
    public Primitive(Object value) {
        if (value == null) {
            throw new InterpreterError("Use Primitve.NULL instead of Primitive(null)");
        }

//        if ((value != Special.NULL_VALUE) && (value != Special.VOID_TYPE) &&
//                !isWrapperType(value.getClass())) {
//            throw new InterpreterError("Not a wrapper type: " + value);
//        }

        this.value = value;
    }

    /**
     * Creates a new Primitive object.
     *
     * @param value DOCUMENT ME!
     */
    public Primitive(boolean value) {
        this(new Boolean(value));
    }

    /**
     * Creates a new Primitive object.
     *
     * @param value DOCUMENT ME!
     */
    public Primitive(byte value) {
        this(new Byte(value));
    }

    /**
     * Creates a new Primitive object.
     *
     * @param value DOCUMENT ME!
     */
    public Primitive(short value) {
        this(new Short(value));
    }

    /**
     * Creates a new Primitive object.
     *
     * @param value DOCUMENT ME!
     */
    public Primitive(char value) {
        this(new Character(value));
    }

    /**
     * Creates a new Primitive object.
     *
     * @param value DOCUMENT ME!
     */
    public Primitive(int value) {
        this(new Integer(value));
    }

    /**
     * Creates a new Primitive object.
     *
     * @param value DOCUMENT ME!
     */
    public Primitive(long value) {
        this(new Long(value));
    }

    /**
     * Creates a new Primitive object.
     *
     * @param value DOCUMENT ME!
     */
    public Primitive(float value) {
        this(new Float(value));
    }

    /**
     * Creates a new Primitive object.
     *
     * @param value DOCUMENT ME!
     */
    public Primitive(double value) {
        this(new Double(value));
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getValue() {
        if (value == Special.NULL_VALUE) {
            return null;
        } else if (value == Special.VOID_TYPE) {
            throw new InterpreterError("attempt to unwrap void type");
        } else {
            return value;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        if (value == Special.NULL_VALUE) {
            return "null";
        } else if (value == Special.VOID_TYPE) {
            return "void";
        } else {
            return value.toString();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Class getType() {
        if (this == Primitive.VOID) {
            return Void.TYPE;
        }

        if (this == Primitive.NULL) {
            return null;
        }

        return unboxType(value.getClass());
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj1 DOCUMENT ME!
     * @param obj2 DOCUMENT ME!
     * @param kind DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     * @throws UtilTargetError DOCUMENT ME!
     */
    public static Object binaryOperation(Object obj1, Object obj2, int kind)
        throws UtilEvalError {
        if ((obj1 == NULL) || (obj2 == NULL)) {
            throw new UtilEvalError("Null value or 'null' literal in binary operation");
        }

        if ((obj1 == VOID) || (obj2 == VOID)) {
            throw new UtilEvalError(
                "Undefined variable, class, or 'void' literal in binary operation");
        }

        Class lhsOrgType = obj1.getClass();
        Class rhsOrgType = obj2.getClass();

        if (obj1 instanceof Primitive) {
            obj1 = ((Primitive) obj1).getValue();
        }

        if (obj2 instanceof Primitive) {
            obj2 = ((Primitive) obj2).getValue();
        }

        Object[] operands = promotePrimitives(obj1, obj2);
        Object lhs = operands[0];
        Object rhs = operands[1];

        if (lhs.getClass() != rhs.getClass()) {
            throw new UtilEvalError("Type mismatch in operator.  " + lhs.getClass() +
                " cannot be used with " + rhs.getClass());
        }

        Object result;

        try {
            result = binaryOperationImpl(lhs, rhs, kind);
        } catch (ArithmeticException e) {
            throw new UtilTargetError("Arithemetic Exception in binary op", e);
        }

        if (((lhsOrgType == Primitive.class) && (rhsOrgType == Primitive.class)) ||
                result instanceof Boolean) {
            return new Primitive(result);
        } else {
            return result;
        }
    }

    static Object binaryOperationImpl(Object lhs, Object rhs, int kind)
        throws UtilEvalError {
        if (lhs instanceof Boolean) {
        	
            return booleanBinaryOperation((Boolean) lhs, (Boolean) rhs, kind);
        } else if (lhs instanceof Integer) {
            return intBinaryOperation((Integer) lhs, (Integer) rhs, kind);
        } else if (lhs instanceof Long) {
            return longBinaryOperation((Long) lhs, (Long) rhs, kind);
        } else if (lhs instanceof Float) {
            return floatBinaryOperation((Float) lhs, (Float) rhs, kind);
        } else if (lhs instanceof Double) {
            return doubleBinaryOperation((Double) lhs, (Double) rhs, kind);
        } else if (lhs instanceof BigDecimal) {
            return bigDecimalBinaryOperation((BigDecimal) lhs, (BigDecimal) rhs, kind);
        } else {
            throw new UtilEvalError("Invalid types in binary operator");
        }
    }

    static Object bigDecimalBinaryOperation(BigDecimal D1, BigDecimal D2, int kind)
        throws UtilEvalError {
        double lhs = D1.doubleValue();
        double rhs = D2.doubleValue();

        switch (kind) {
        case LT:
            return new Boolean(lhs < rhs);

        case GT:
            return new Boolean(lhs > rhs);

        case EQ:
            return new Boolean(lhs == rhs);

        case LE:
            return new Boolean(lhs <= rhs);

        case GE:
            return new Boolean(lhs >= rhs);

        case NE:
            return new Boolean(lhs != rhs);

        case PLUS:
            return new Double(lhs + rhs);

        case MINUS:
            return new Double(lhs - rhs);

        case STAR:
            return new Double(lhs * rhs);

        case SLASH:
            return new Double(lhs / rhs);

        case MOD:
            return new Double(lhs % rhs);

        case LSHIFT:
        case RSIGNEDSHIFT:

        case RUNSIGNEDSHIFT:
            throw new UtilEvalError("Can't shift doubles");

        default:
            throw new InterpreterError("Unimplemented binary double operator");
        }
    }

    static Boolean booleanBinaryOperation(Boolean B1, Boolean B2, int kind) {
        boolean lhs = B1.booleanValue();
        boolean rhs = B2.booleanValue();

        switch (kind) {
        case EQ:
            return new Boolean(lhs == rhs);

        case NE:
            return new Boolean(lhs != rhs);

        case BOOL_OR:
            return new Boolean(lhs || rhs);

        case BOOL_AND:
            return new Boolean(lhs && rhs);

        default:
            throw new InterpreterError("unimplemented binary operator");
        }
    }

    static Object longBinaryOperation(Long L1, Long L2, int kind) {
        long lhs = L1.longValue();
        long rhs = L2.longValue();

        switch (kind) {
        case LT:
            return new Boolean(lhs < rhs);

        case GT:
            return new Boolean(lhs > rhs);

        case EQ:
            return new Boolean(lhs == rhs);

        case LE:
            return new Boolean(lhs <= rhs);

        case GE:
            return new Boolean(lhs >= rhs);

        case NE:
            return new Boolean(lhs != rhs);

        case PLUS:
            return new Long(lhs + rhs);

        case MINUS:
            return new Long(lhs - rhs);

        case STAR:
            return new Long(lhs * rhs);

        case SLASH:
            return new Long(lhs / rhs);

        case MOD:
            return new Long(lhs % rhs);

        case LSHIFT:
            return new Long(lhs << rhs);

        case RSIGNEDSHIFT:
            return new Long(lhs >> rhs);

        case RUNSIGNEDSHIFT:
            return new Long(lhs >>> rhs);

        case BIT_AND:
            return new Long(lhs & rhs);

        case BIT_OR:
            return new Long(lhs | rhs);

        case XOR:
            return new Long(lhs ^ rhs);

        default:
            throw new InterpreterError("Unimplemented binary long operator");
        }
    }

    static Object intBinaryOperation(Integer I1, Integer I2, int kind) {
        int lhs = I1.intValue();
        int rhs = I2.intValue();

        switch (kind) {
        case LT:
            return new Boolean(lhs < rhs);

        case GT:
            return new Boolean(lhs > rhs);

        case EQ:
            return new Boolean(lhs == rhs);

        case LE:
            return new Boolean(lhs <= rhs);

        case GE:
            return new Boolean(lhs >= rhs);

        case NE:
            return new Boolean(lhs != rhs);

        case PLUS:
            return new Integer(lhs + rhs);

        case MINUS:
            return new Integer(lhs - rhs);

        case STAR:
            return new Integer(lhs * rhs);

        case SLASH:
            return new Integer(lhs / rhs);

        case MOD:
            return new Integer(lhs % rhs);

        case LSHIFT:
            return new Integer(lhs << rhs);

        case RSIGNEDSHIFT:
            return new Integer(lhs >> rhs);

        case RUNSIGNEDSHIFT:
            return new Integer(lhs >>> rhs);

        case BIT_AND:
            return new Integer(lhs & rhs);

        case BIT_OR:
            return new Integer(lhs | rhs);

        case XOR:
            return new Integer(lhs ^ rhs);

        default:
            throw new InterpreterError("Unimplemented binary integer operator");
        }
    }

    static Object doubleBinaryOperation(Double D1, Double D2, int kind)
        throws UtilEvalError {
        double lhs = D1.doubleValue();
        double rhs = D2.doubleValue();

        switch (kind) {
        case LT:
            return new Boolean(lhs < rhs);

        case GT:
            return new Boolean(lhs > rhs);

        case EQ:
            return new Boolean(lhs == rhs);

        case LE:
            return new Boolean(lhs <= rhs);

        case GE:
            return new Boolean(lhs >= rhs);

        case NE:
            return new Boolean(lhs != rhs);

        case PLUS:
            return new Double(lhs + rhs);

        case MINUS:
            return new Double(lhs - rhs);

        case STAR:
            return new Double(lhs * rhs);

        case SLASH:
            return new Double(lhs / rhs);

        case MOD:
            return new Double(lhs % rhs);

        case LSHIFT:
        case RSIGNEDSHIFT:

        case RUNSIGNEDSHIFT:
            throw new UtilEvalError("Can't shift doubles");

        default:
            throw new InterpreterError("Unimplemented binary double operator");
        }
    }

    static Object floatBinaryOperation(Float F1, Float F2, int kind)
        throws UtilEvalError {
        float lhs = F1.floatValue();
        float rhs = F2.floatValue();

        switch (kind) {
        case LT:
            return new Boolean(lhs < rhs);

        case GT:
            return new Boolean(lhs > rhs);

        case EQ:
            return new Boolean(lhs == rhs);

        case LE:
            return new Boolean(lhs <= rhs);

        case GE:
            return new Boolean(lhs >= rhs);

        case NE:
            return new Boolean(lhs != rhs);

        case PLUS:
            return new Float(lhs + rhs);

        case MINUS:
            return new Float(lhs - rhs);

        case STAR:
            return new Float(lhs * rhs);

        case SLASH:
            return new Float(lhs / rhs);

        case MOD:
            return new Float(lhs % rhs);

        case LSHIFT:
        case RSIGNEDSHIFT:

        case RUNSIGNEDSHIFT:
            throw new UtilEvalError("Can't shift floats ");

        default:
            throw new InterpreterError("Unimplemented binary float operator");
        }
    }

    static Object promoteToInteger(Object wrapper) {
        if (wrapper instanceof Character) {
            return new Integer(((Character) wrapper).charValue());
        } else if ((wrapper instanceof Byte) || (wrapper instanceof Short)) {
            return new Integer(((Number) wrapper).intValue());
        }

        return wrapper;
    }

   public static Object[] promotePrimitives(Object lhs, Object rhs) {
        lhs = promoteToInteger(lhs);
        rhs = promoteToInteger(rhs);

        if ((lhs instanceof Number) && (rhs instanceof Number)) {
            Number lnum = (Number) lhs;
            Number rnum = (Number) rhs;

            boolean b;

            if ((b = (lnum instanceof BigDecimal)) || (rnum instanceof BigDecimal)) {
                if (b) {
                    rhs = new BigDecimal(rnum.doubleValue());
                } else {
                    lhs = new BigDecimal(lnum.doubleValue());
                }
            } else if ((b = (lnum instanceof Double)) || (rnum instanceof Double)) {
                if (b) {
                    rhs = new Double(rnum.doubleValue());
                } else {
                    lhs = new Double(lnum.doubleValue());
                }
            } else if ((b = (lnum instanceof Float)) || (rnum instanceof Float)) {
                if (b) {
                    rhs = new Float(rnum.floatValue());
                } else {
                    lhs = new Float(lnum.floatValue());
                }
            } else if ((b = (lnum instanceof Long)) || (rnum instanceof Long)) {
                if (b) {
                    rhs = new Long(rnum.longValue());
                } else {
                    lhs = new Long(lnum.longValue());
                }
            }
        }

        return new Object[] {
            lhs,
            rhs
        };
    }

    /**
     * DOCUMENT ME!
     *
     * @param val DOCUMENT ME!
     * @param kind DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     * @throws InterpreterError DOCUMENT ME!
     */
    public static Primitive unaryOperation(Primitive val, int kind)
        throws UtilEvalError {
        if (val == NULL) {
            throw new UtilEvalError("illegal use of null object or 'null' literal");
        }

        if (val == VOID) {
            throw new UtilEvalError("illegal use of undefined object or 'void' literal");
        }

        Class operandType = val.getType();
        Object operand = promoteToInteger(val.getValue());

        if (operand instanceof Boolean) {
            return new Primitive(booleanUnaryOperation((Boolean) operand, kind));
        } else if (operand instanceof Integer) {
            int result = intUnaryOperation((Integer) operand, kind);

            if ((kind == INCR) || (kind == DECR)) {
                if (operandType == Byte.TYPE) {
                    return new Primitive((byte) result);
                }

                if (operandType == Short.TYPE) {
                    return new Primitive((short) result);
                }

                if (operandType == Character.TYPE) {
                    return new Primitive((char) result);
                }
            }

            return new Primitive(result);
        } else if (operand instanceof Long) {
            return new Primitive(longUnaryOperation((Long) operand, kind));
        } else if (operand instanceof Float) {
            return new Primitive(floatUnaryOperation((Float) operand, kind));
        } else if (operand instanceof Double) {
            return new Primitive(doubleUnaryOperation((Double) operand, kind));
        } else {
            throw new InterpreterError("An error occurred.  Please call technical support.");
        }
    }

    static boolean booleanUnaryOperation(Boolean B, int kind)
        throws UtilEvalError {
        boolean operand = B.booleanValue();

        switch (kind) {
        case BANG:
            return !operand;

        default:
            throw new UtilEvalError("Operator inappropriate for boolean");
        }
    }

    static int intUnaryOperation(Integer I, int kind) {
        int operand = I.intValue();

        switch (kind) {
        case PLUS:
            return operand;

        case MINUS:
            return -operand;

        case TILDE:
            return ~operand;

        case INCR:
            return operand + 1;

        case DECR:
            return operand - 1;

        default:
            throw new InterpreterError("bad integer unaryOperation");
        }
    }

    static long longUnaryOperation(Long L, int kind) {
        long operand = L.longValue();

        switch (kind) {
        case PLUS:
            return operand;

        case MINUS:
            return -operand;

        case TILDE:
            return ~operand;

        case INCR:
            return operand + 1;

        case DECR:
            return operand - 1;

        default:
            throw new InterpreterError("bad long unaryOperation");
        }
    }

    static float floatUnaryOperation(Float F, int kind) {
        float operand = F.floatValue();

        switch (kind) {
        case PLUS:
            return operand;

        case MINUS:
            return -operand;

        default:
            throw new InterpreterError("bad float unaryOperation");
        }
    }

    static double doubleUnaryOperation(Double D, int kind) {
        double operand = D.doubleValue();

        switch (kind) {
        case PLUS:
            return operand;

        case MINUS:
            return -operand;

        default:
            throw new InterpreterError("bad double unaryOperation");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public int intValue() throws UtilEvalError {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else {
            throw new UtilEvalError("Primitive not a number");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public boolean booleanValue() throws UtilEvalError {
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        } else {
            throw new UtilEvalError("Primitive not a boolean");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isNumber() {
        return (!(value instanceof Boolean) && !(this == NULL) && !(this == VOID));
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public Number numberValue() throws UtilEvalError {
        Object value = this.value;

        if (value instanceof Character) {
            value = new Integer(((Character) value).charValue());
        }

        if (value instanceof Number) {
            return (Number) value;
        } else {
            throw new UtilEvalError("Primitive not a number");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean equals(Object obj) {
        if (obj instanceof Primitive) {
            return ((Primitive) obj).value.equals(this.value);
        } else {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int hashCode() {
        return this.value.hashCode() * 21;
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Object unwrap(Object obj) {
        if (obj == Primitive.VOID) {
            return null;
        }

        if (obj instanceof Primitive) {
            return ((Primitive) obj).getValue();
        } else {
            return obj;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Object[] unwrap(Object[] args) {
        Object[] oa = new Object[args.length];

        for (int i = 0; i < args.length; i++)
            oa[i] = unwrap(args[i]);

        return oa;
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     * @param paramTypes DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Object[] wrap(Object[] args, Class[] paramTypes) {
        if (args == null) {
            return null;
        }

        Object[] oa = new Object[args.length];

        for (int i = 0; i < args.length; i++)
            oa[i] = wrap(args[i], paramTypes[i]);

        return oa;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Object wrap(Object value, Class type) {
        if (type == Void.TYPE) {
            return Primitive.VOID;
        }

        if (value == null) {
            return Primitive.NULL;
        }

        if (type.isPrimitive()) {
            return new Primitive(value);
        }

        return value;
    }
    
    public static Object wrap(Object value) {
       
        if (value == null) {
            return Primitive.NULL;
        }

        if (value.getClass().isPrimitive()) {
            return new Primitive(value);
        }

        return value;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Primitive getDefaultValue(Class type) {
        if ((type == null) || !type.isPrimitive()) {
            return Primitive.NULL;
        }

        if (type == Boolean.TYPE) {
            return new Primitive(false);
        }

        try {
            return new Primitive((int) 0).castToType(type, Types.CAST);
        } catch (UtilEvalError e) {
            throw new InterpreterError("bad cast");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param primitiveType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Class boxType(Class primitiveType) {
        Class c = (Class) wrapperMap.get(primitiveType);

        if (c != null) {
            return c;
        }

        throw new InterpreterError("Not a primitive type: " + primitiveType);
    }

    /**
     * DOCUMENT ME!
     *
     * @param wrapperType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Class unboxType(Class wrapperType) {
        Class c = (Class) wrapperMap.get(wrapperType);

        if (c != null) {
            return c;
        }

        throw new InterpreterError("Not a primitive wrapper type: " + wrapperType);
    }

    /**
     * DOCUMENT ME!
     *
     * @param toType DOCUMENT ME!
     * @param operation DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public Primitive castToType(Class toType, int operation)
        throws UtilEvalError {
        return castPrimitive(toType, getType(), this, false, operation);
    }

    static Primitive castPrimitive(Class toType, Class fromType, Primitive fromValue,
        boolean checkOnly, int operation) throws UtilEvalError {
        if (checkOnly && (fromValue != null)) {
            throw new InterpreterError("bad cast param 1");
        }

        if (!checkOnly && (fromValue == null)) {
            throw new InterpreterError("bad cast param 2");
        }

        if ((fromType != null) && !fromType.isPrimitive()) {
            throw new InterpreterError("bad fromType:" + fromType);
        }

        if ((fromValue == Primitive.NULL) && (fromType != null)) {
            throw new InterpreterError("inconsistent args 1");
        }

        if ((fromValue == Primitive.VOID) && (fromType != Void.TYPE)) {
            throw new InterpreterError("inconsistent args 2");
        }

        if (fromType == Void.TYPE) {
            if (checkOnly) {
                return Types.INVALID_CAST;
            } else {
            	return Primitive.VOID2 ;
              //  throw Types.castError(Reflect.normalizeClassName(toType), "void value", operation);
            }
        }

        Object value = null;

        if (fromValue != null) {
            value = fromValue.getValue();
        }

        if (toType.isPrimitive()) {
            if (fromType == null) {
                if (checkOnly) {
                    return Types.INVALID_CAST;
                } else {
                    throw Types.castError("primitive type:" + toType, "Null value", operation);
                }
            }
        } else {
            if (fromType == null) {
                return checkOnly ? Types.VALID_CAST : Primitive.NULL;
            }

            if (checkOnly) {
                return Types.INVALID_CAST;
            } else {
                throw Types.castError("object type:" + toType, "primitive value", operation);
            }
        }

        if (fromType == Boolean.TYPE) {
            if (toType != Boolean.TYPE) {
                if (checkOnly) {
                    return Types.INVALID_CAST;
                } else {
                    throw Types.castError(toType, fromType, operation);
                }
            }

            return checkOnly ? Types.VALID_CAST : fromValue;
        }

        if ((operation == Types.ASSIGNMENT) && !Types.isJavaAssignable(toType, fromType)) {
            if (checkOnly) {
                return Types.INVALID_CAST;
            } else {
                throw Types.castError(toType, fromType, operation);
            }
        }

        return checkOnly ? Types.VALID_CAST : new Primitive(castWrapper(toType, value));
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static boolean isWrapperType(Class type) {
        return (wrapperMap.get(type) != null) && !type.isPrimitive();
    }

    static Object castWrapper(Class toType, Object value) {
        if (!toType.isPrimitive()) {
            throw new InterpreterError("invalid type in castWrapper: " + toType);
        }

        if (value == null) {
            throw new InterpreterError("null value in castWrapper, guard");
        }

        if (value instanceof Boolean) {
            if (toType != Boolean.TYPE) {
                throw new InterpreterError("bad wrapper cast of boolean");
            } else {
                return value;
            }
        }

        if (value instanceof Character) {
            value = new Integer(((Character) value).charValue());
        }

        if (!(value instanceof Number)) {
            throw new InterpreterError("bad type in cast");
        }

        Number number = (Number) value;

        if (toType == Byte.TYPE) {
            return new Byte(number.byteValue());
        }

        if (toType == Short.TYPE) {
            return new Short(number.shortValue());
        }

        if (toType == Character.TYPE) {
            return new Character((char) number.intValue());
        }

        if (toType == Integer.TYPE) {
            return new Integer(number.intValue());
        }

        if (toType == Long.TYPE) {
            return new Long(number.longValue());
        }

        if (toType == Float.TYPE) {
            return new Float(number.floatValue());
        }

        if (toType == Double.TYPE) {
            return new Double(number.doubleValue());
        }

        throw new InterpreterError("error in wrapper cast");
    }

    private static class Special implements java.io.Serializable {
        public static final Special NULL_VALUE = new Special();
        public static final Special VOID_TYPE = new Special();
      
        private Special() {
        }
    }
}
