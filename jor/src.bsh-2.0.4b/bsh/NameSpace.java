package bsh;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class NameSpace implements java.io.Serializable, BshClassManager.Listener, NameSource {
    public static final NameSpace JAVACODE = new NameSpace((BshClassManager) null,
            "Called from compiled Java code.");

    static {
        JAVACODE.isMethod = true;
    }

    private String nsName;
    private NameSpace parent;
    private Hashtable variables;
    private Hashtable methods;
    protected Hashtable importedClasses;
    private ArrayList importedPackages;
    private ArrayList importedCommands;
    private ArrayList importedObjects;
    private ArrayList importedStatic;
    private String packageName;
    transient private BshClassManager classManager;
    private This thisReference;
    private Hashtable names;
    SimpleNode callerInfoNode;
    boolean isMethod;
    boolean isClass;
    Class classStatic;
    Object classInstance;
    transient private Hashtable classCache;
    ArrayList nameSourceListeners;

    /**
     * Creates a new NameSpace object.
     *
     * @param parent DOCUMENT ME!
     * @param name DOCUMENT ME!
     */
    public NameSpace(NameSpace parent, String name) {
        this(parent, null, name);
    }

    /**
     * Creates a new NameSpace object.
     *
     * @param classManager DOCUMENT ME!
     * @param name DOCUMENT ME!
     */
    public NameSpace(BshClassManager classManager, String name) {
        this(null, classManager, name);
    }

    /**
     * Creates a new NameSpace object.
     *
     * @param name DOCUMENT ME!
     */
    public NameSpace(String name) {
        setName(name);
    }

    /**
     * Creates a new NameSpace object.
     *
     * @param parent DOCUMENT ME!
     * @param classManager DOCUMENT ME!
     * @param name DOCUMENT ME!
     */
    public NameSpace(NameSpace parent, BshClassManager classManager, String name) {
        setName(name);
        setParent(parent);
        setClassManager(classManager);

        if (classManager != null) {
            classManager.addListener(this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NameSpace getRoot() {
        NameSpace r = this;

        while (r.getParent() != null) {
            r = r.getParent();
        }

        return r;
    }

    void setClassStatic(Class clas) {
        this.classStatic = clas;
        importStatic(clas);
    }

    void setClassInstance(Object instance) {
        this.classInstance = instance;
        importObject(instance);
    }

    Object getClassInstance() throws UtilEvalError {
        if (classInstance != null) {
            return classInstance;
        }

        if (classStatic != null) {
            throw new UtilEvalError("Can't refer to class instance from static context.");
        } else {
            throw new InterpreterError("Can't resolve class instance 'this' in: " + this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     */
    public void setName(String name) {
        this.nsName = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getName() {
        return this.nsName;
    }

    void setNode(SimpleNode node) {
        callerInfoNode = node;
    }

    SimpleNode getNode() {
        if (callerInfoNode != null) {
            return callerInfoNode;
        }

        if (parent != null) {
            return parent.getNode();
        } else {
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param interpreter DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public Object get(String name, Interpreter interpreter)
        throws UtilEvalError {
        CallStack callstack = new CallStack(this);

        return getNameResolver(name).toObject(callstack, interpreter, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param strictJava DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public void setVariable(String name, Object value, boolean strictJava)
        throws UtilEvalError {
        boolean recurse = Interpreter.LOCALSCOPING ? strictJava : true;
        setVariable(name, value, strictJava, recurse);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param strictJava DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public void setLocalVariable(String name, Object value, boolean strictJava)
        throws UtilEvalError {
        setLocalVariable(name, value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param strictJava DOCUMENT ME!
     * @param recurse DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     * @throws InterpreterError DOCUMENT ME!
     */
    public void setVariable(String name, Object value, boolean strictJava, boolean recurse)
        throws UtilEvalError {
        if (variables == null) {
            variables = new Hashtable();
        }

        if (value == null) {
            throw new InterpreterError("null variable value");
        }

        Variable existing = getVariableImpl(name, recurse);

        if (existing != null) {
            try {
                existing.setValue(value, Variable.ASSIGNMENT);
            } catch (UtilEvalError e) {
                throw new UtilEvalError("Variable assignment: " + name + ": " + e.getMessage());
            }
        } else {
            if (strictJava) {
                throw new UtilEvalError("(Strict Java mode) Assignment to undeclared variable: " +
                    name);
            }

            NameSpace varScope = this;

            varScope.variables.put(name, new Variable(name, value, null));

            nameSpaceChanged();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param value DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public void setLocalVariable(String name, Object value)
        throws UtilEvalError {
        if (variables == null) {
            variables = new Hashtable();
        }

        this.variables.put(name, new Variable(name, value, null));

        nameSpaceChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     */
    public void unsetVariable(String name) {
        if (variables != null) {
            variables.remove(name);
            nameSpaceChanged();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getVariableNames() {
        if (variables == null) {
            return new String[0];
        } else {
            return enumerationToStringArray(variables.keys());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param results DOCUMENT ME!
     */
    public void getVariableNames(List results) {
        if (variables != null) {
            results.addAll(this.variables.keySet());
        }
        
        if(parent != null)
        	parent.getVariableNames(results);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getMethodNames() {
        if (methods == null) {
            return new String[0];
        } else {
            return enumerationToStringArray(methods.keys());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public BshMethod[] getMethods() {
        if (methods == null) {
            return new BshMethod[0];
        } else {
            return flattenMethodCollection(methods.elements());
        }
    }

    private String[] enumerationToStringArray(Enumeration e) {
        ArrayList v = new ArrayList();

        while (e.hasMoreElements())
            v.add(e.nextElement());

        String[] sa = (String[]) v.toArray(new String[0]);

        return sa;
    }

    private BshMethod[] flattenMethodCollection(Enumeration e) {
        ArrayList v = new ArrayList();

        while (e.hasMoreElements()) {
            Object o = e.nextElement();

            if (o instanceof BshMethod) {
                v.add(o);
            } else {
                ArrayList ov = (ArrayList) o;

                for (int i = 0; i < ov.size(); i++)
                    v.add(ov.get(i));
            }
        }

        return (BshMethod[]) v.toArray(new BshMethod[0]);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NameSpace getParent() {
        return parent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param declaringInterpreter DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public This getSuper(Interpreter declaringInterpreter) {
        if (parent != null) {
            return parent.getThis(declaringInterpreter);
        } else {
            return getThis(declaringInterpreter);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param declaringInterpreter DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public This getGlobal(Interpreter declaringInterpreter) {
        if (parent != null) {
            return parent.getGlobal(declaringInterpreter);
        } else {
            return getThis(declaringInterpreter);
        }
    }

    This getThis(Interpreter declaringInterpreter) {
        if (thisReference == null) {
            thisReference = This.getThis(this, declaringInterpreter);
        }

        return thisReference;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public BshClassManager getClassManager() {
        if (classManager != null) {
            return classManager;
        }

        if ((parent != null) && (parent != JAVACODE)) {
            return parent.getClassManager();
        }

        System.out.println("experiment: creating class manager");
        classManager = BshClassManager.createClassManager(null);

        return classManager;
    }

    void setClassManager(BshClassManager classManager) {
        this.classManager = classManager;
    }

    /**
     * DOCUMENT ME!
     */
    public void prune() {
        if (this.classManager == null) {
            setClassManager(BshClassManager.createClassManager(null));
        }

        setParent(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     */
    public void setParent(NameSpace parent) {
        this.parent = parent;

        if (parent == null) {
            loadDefaultImports();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public Object getVariable(String name) throws UtilEvalError {
        return getVariable(name, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param recurse DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public Object getVariable(String name, boolean recurse)
        throws UtilEvalError {
        Variable var = getVariableImpl(name, recurse);

        Object obj = unwrapVariable(var);

        if (obj instanceof ValueAlways) {
            obj = ((ValueAlways) obj).value();
        }

        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getLocalVariable(String name) {
        if ((name != null) && (variables != null)) {
            Variable var = (Variable) variables.get(name);

            if (var != null) {
                try {
                    return var.getValue();
                } catch (UtilEvalError e) {
                    e.printStackTrace();
                }
            }
        }

        if (this.parent != null) {
            return getLocalVariable(name);
        } else {
            return null;
        }
    }

    protected Variable getVariableImpl(String name, boolean recurse)
        throws UtilEvalError {
        Variable var = null;

        if ((var == null) && isClass) {
            var = getImportedVar(name);
        }

        if ((var == null) && (variables != null)) {
            var = (Variable) variables.get(name);
        }

        if ((var == null) && !isClass) {
            var = getImportedVar(name);
        }

        if (recurse && (var == null) && (parent != null)) {
            var = parent.getVariableImpl(name, recurse);
        }

        return var;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Variable[] getDeclaredVariables() {
        if (variables == null) {
            return new Variable[0];
        }

        Variable[] vars = new Variable[variables.size()];
        int i = 0;

        for (Enumeration e = variables.elements(); e.hasMoreElements();)
            vars[i++] = (Variable) e.nextElement();

        return vars;
    }

    protected Object unwrapVariable(Variable var) throws UtilEvalError {
        return (var == null) ? Primitive.VOID : var.getValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param type DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param isFinal DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public void setTypedVariable(String name, Class type, Object value, boolean isFinal)
        throws UtilEvalError {
        Modifiers modifiers = new Modifiers();

        if (isFinal) {
            modifiers.addModifier(Modifiers.FIELD, "final");
        }

        setTypedVariable(name, type, value, modifiers);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param type DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param modifiers DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public void setTypedVariable(String name, Class type, Object value, Modifiers modifiers)
        throws UtilEvalError {
        if (variables == null) {
            variables = new Hashtable();
        }

        Variable existing = getVariableImpl(name, false);

        if (existing != null) {
            if (existing.getType() != null) {
                if (existing.getType() != type) {
                    throw new UtilEvalError("Typed variable: " + name +
                        " was previously declared with type: " + existing.getType());
                } else {
                    existing.setValue(value, Variable.DECLARATION);

                    return;
                }
            }
        }

        variables.put(name, new Variable(name, type, value, modifiers));
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param method DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public void setMethod(String name, BshMethod method)
        throws UtilEvalError {
        if (methods == null) {
            methods = new Hashtable();
        }

        Object m = methods.get(name);

        if (m == null) {
            methods.put(name, method);
        } else if (m instanceof BshMethod) {
            ArrayList v = new ArrayList();
            v.add(m);
            v.add(method);
            methods.put(name, v);
        } else {
            ((ArrayList) m).add(method);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param sig DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public BshMethod getMethod(String name, Class[] sig)
        throws UtilEvalError {
        return getMethod(name, sig, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param sig DOCUMENT ME!
     * @param declaredOnly DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public BshMethod getMethod(String name, Class[] sig, boolean declaredOnly)
        throws UtilEvalError {
        BshMethod method = null;

        if ((method == null) && isClass && !declaredOnly) {
            method = getImportedMethod(name, sig);
        }

        Object m = null;

        if ((method == null) && (methods != null)) {
            m = methods.get(name);

            if (m != null) {
                BshMethod[] ma;

                if (m instanceof ArrayList) {
                    ArrayList vm = (ArrayList) m;
                    ma = (BshMethod[]) vm.toArray(new BshMethod[0]);
                } else {
                    ma = new BshMethod[] { (BshMethod) m };
                }

                Class[][] candidates = new Class[ma.length][];

                for (int i = 0; i < ma.length; i++)
                    candidates[i] = ma[i].getParameterTypes();

                int match = Reflect.findMostSpecificSignature(sig, candidates);

                if (match != -1) {
                    method = ma[match];
                }
            }
        }

        if ((method == null) && !isClass && !declaredOnly) {
            method = getImportedMethod(name, sig);
        }

        if (!declaredOnly && (method == null) && (parent != null)) {
            return parent.getMethod(name, sig);
        }

        return method;
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     */
    public void importClass(String name) {
        if (importedClasses == null) {
            importedClasses = new Hashtable();
        }

        importedClasses.put(Name.suffix(name, 1), name);
        nameSpaceChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     */
    public void importPackage(String name) {
        if (importedPackages == null) {
            importedPackages = new ArrayList();
        }

        if (importedPackages.contains(name)) {
            importedPackages.remove(name);
        }

        importedPackages.add(name);
        nameSpaceChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     */
    public void importCommands(String name) {
        if (importedCommands == null) {
            importedCommands = new ArrayList();
        }

        name = name.replace('.', '/');

        if (!name.startsWith("/")) {
            name = "/" + name;
        }

        if ((name.length() > 1) && name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
        }

        if (importedCommands.contains(name)) {
            importedCommands.remove(name);
        }

        importedCommands.add(name);
        nameSpaceChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param argTypes DOCUMENT ME!
     * @param interpreter DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public Object getCommand(String name, Class[] argTypes, Interpreter interpreter)
        throws UtilEvalError {
        if (Interpreter.DEBUG) {
            Interpreter.debug("getCommand: " + name);
        }

        BshClassManager bcm = interpreter.getClassManager();

        if (importedCommands != null) {
            for (int i = importedCommands.size() - 1; i >= 0; i--) {
                String path = (String) importedCommands.get(i);

                String scriptPath;

                if (path.equals("/")) {
                    scriptPath = path + name + ".bsh";
                } else {
                    scriptPath = path + "/" + name + ".bsh";
                }

                Interpreter.debug("searching for script: " + scriptPath);

                InputStream in = bcm.getResourceAsStream(scriptPath);

                if (in != null) {
                    return loadScriptedCommand(in, name, argTypes, scriptPath, interpreter);
                }

                String className;

                if (path.equals("/")) {
                    className = name;
                } else {
                    className = path.substring(1).replace('/', '.') + "." + name;
                }

                Interpreter.debug("searching for class: " + className);

                Class clas = bcm.classForName(className);

                if (clas != null) {
                    return clas;
                }
            }
        }

        if (parent != null) {
            return parent.getCommand(name, argTypes, interpreter);
        } else {
            return null;
        }
    }

    protected BshMethod getImportedMethod(String name, Class[] sig)
        throws UtilEvalError {
        if (importedObjects != null) {
            for (int i = 0; i < importedObjects.size(); i++) {
                Object object = importedObjects.get(i);
                Class clas = object.getClass();
                Method method = Reflect.resolveJavaMethod(getClassManager(), clas, name, sig, false);

                if (method != null) {
                    return new BshMethod(method, object);
                }
            }
        }

        if (importedStatic != null) {
            for (int i = 0; i < importedStatic.size(); i++) {
                Class clas = (Class) importedStatic.get(i);
                Method method = Reflect.resolveJavaMethod(getClassManager(), clas, name, sig, true);

                if (method != null) {
                    return new BshMethod(method, null);
                }
            }
        }

        return null;
    }

    protected Variable getImportedVar(String name) throws UtilEvalError {
        if (importedObjects != null) {
            for (int i = 0; i < importedObjects.size(); i++) {
                Object object = importedObjects.get(i);
                Class clas = object.getClass();
                Field field = Reflect.resolveJavaField(clas, name, false);

                if (field != null) {
                    return new Variable(name, field.getType(), new LHS(object, field));
                }
            }
        }

        if (importedStatic != null) {
            for (int i = 0; i < importedStatic.size(); i++) {
                Class clas = (Class) importedStatic.get(i);
                Field field = Reflect.resolveJavaField(clas, name, true);

                if (field != null) {
                    return new Variable(name, field.getType(), new LHS(field));
                }
            }
        }

        return null;
    }

    private BshMethod loadScriptedCommand(InputStream in, String name, Class[] argTypes,
        String resourcePath, Interpreter interpreter) throws UtilEvalError {
        try {
            interpreter.eval(new InputStreamReader(in), this, resourcePath);
        } catch (EvalError e) {
            Interpreter.debug(e.toString());
            throw new UtilEvalError("Error loading script: " + e.getMessage());
        }

        BshMethod meth = getMethod(name, argTypes);

        return meth;
    }

    void cacheClass(String name, Class c) {
        if (classCache == null) {
            classCache = new Hashtable();
        }

        classCache.put(name, c);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public Class getClass(String name) throws UtilEvalError {
        Class c = getClassImpl(name);

        if (c != null) {
            return c;
        } else if (parent != null) {
            return parent.getClass(name);
        } else {
            return null;
        }
    }

    private Class getClassImpl(String name) throws UtilEvalError {
        Class c = null;

        if (classCache != null) {
            c = (Class) classCache.get(name);

            if (c != null) {
                return c;
            }
        }

        boolean unqualifiedName = !Name.isCompound(name);

        if (unqualifiedName) {
            if (c == null) {
                c = getImportedClassImpl(name);
            }

            if (c != null) {
                cacheClass(name, c);

                return c;
            }
        }

        c = classForName(name);

        if (c != null) {
            if (unqualifiedName) {
                cacheClass(name, c);
            }

            return c;
        }

        if (Interpreter.DEBUG) {
            Interpreter.debug("getClass(): " + name + " not	found in " + this);
        }

        return null;
    }

    private Class getImportedClassImpl(String name) throws UtilEvalError {
        String fullname = null;

        if (importedClasses != null) {
            fullname = (String) importedClasses.get(name);
        }

        if (fullname != null) {
            Class clas = classForName(fullname);

            if (clas == null) {
                if (Name.isCompound(fullname)) {
                    try {
                        clas = getNameResolver(fullname).toClass();
                    } catch (ClassNotFoundException e) {
                    }
                } else if (Interpreter.DEBUG) {
                    Interpreter.debug("imported unpackaged name not found:" + fullname);
                }

                if (clas != null) {
                    getClassManager().cacheClassInfo(fullname, clas);

                    return clas;
                }
            } else {
                return clas;
            }

            return null;
        }

        if (importedPackages != null) {
            for (int i = importedPackages.size() - 1; i >= 0; i--) {
                String s = ((String) importedPackages.get(i)) + "." + name;
                Class c = classForName(s);

                if (c != null) {
                    return c;
                }
            }
        }

        BshClassManager bcm = getClassManager();

        if (bcm.hasSuperImport()) {
            String s = bcm.getClassNameByUnqName(name);

            if (s != null) {
                return classForName(s);
            }
        }

        return null;
    }

    private Class classForName(String name) {
        return getClassManager().classForName(name);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getAllNames() {
        ArrayList vec = new ArrayList();
        getAllNamesAux(vec);

        String[] names = (String[]) vec.toArray(new String[0]);

        return names;
    }

    protected void getAllNamesAux(ArrayList vec) {
        Enumeration varNames = variables.keys();

        while (varNames.hasMoreElements())
            vec.add(varNames.nextElement());

        Enumeration methodNames = methods.keys();

        while (methodNames.hasMoreElements())
            vec.add(methodNames.nextElement());

        if (parent != null) {
            parent.getAllNamesAux(vec);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param listener DOCUMENT ME!
     */
    public void addNameSourceListener(NameSource.Listener listener) {
        if (nameSourceListeners == null) {
            nameSourceListeners = new ArrayList();
        }

        nameSourceListeners.add(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @throws UtilEvalError DOCUMENT ME!
     */
    public void doSuperImport() throws UtilEvalError {
        getClassManager().doSuperImport();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return "NameSpace: " +
        ((nsName == null) ? super.toString() : (nsName + " (" + super.toString() + ")")) +
        (isClass ? " (isClass) " : "") + (isMethod ? " (method) " : "") +
        ((classStatic != null) ? " (class static) " : "") +
        ((classInstance != null) ? " (class instance) " : "");
    }

    private synchronized void writeObject(java.io.ObjectOutputStream s)
        throws IOException {
        names = null;

        s.defaultWriteObject();
    }

    /**
     * DOCUMENT ME!
     *
     * @param methodName DOCUMENT ME!
     * @param args DOCUMENT ME!
     * @param interpreter DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws EvalError DOCUMENT ME!
     */
    public Object invokeMethod(String methodName, Object[] args, Interpreter interpreter)
        throws EvalError {
        return invokeMethod(methodName, args, interpreter, null, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param methodName DOCUMENT ME!
     * @param args DOCUMENT ME!
     * @param interpreter DOCUMENT ME!
     * @param callstack DOCUMENT ME!
     * @param callerInfo DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws EvalError DOCUMENT ME!
     */
    public Object invokeMethod(String methodName, Object[] args, Interpreter interpreter,
        CallStack callstack, SimpleNode callerInfo) throws EvalError {
        return getThis(interpreter)
                   .invokeMethod(methodName, args, interpreter, callstack, callerInfo, false);
    }

    /**
     * DOCUMENT ME!
     */
    public void classLoaderChanged() {
        nameSpaceChanged();
    }

    /**
     * DOCUMENT ME!
     */
    public void nameSpaceChanged() {
        classCache = null;
        names = null;
    }

    /**
     * DOCUMENT ME!
     */
    public void loadDefaultImports() {
        importClass("bsh.EvalError");
        importClass("bsh.Interpreter");
        importPackage("javax.swing.event");
        importPackage("javax.swing");
        importPackage("java.awt.event");
        importPackage("java.awt");
        importPackage("java.net");
        importPackage("java.util");
        importPackage("java.io");
        importPackage("java.lang");
        importCommands("/bsh/commands");
    }

    Name getNameResolver(String ambigname) {
        if (names == null) {
            names = new Hashtable();
        }

        Name name = (Name) names.get(ambigname);

        if (name == null) {
            name = new Name(this, ambigname);
            names.put(ambigname, name);
        }

        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getInvocationLine() {
        SimpleNode node = getNode();

        if (node != null) {
            return node.getLineNumber();
        } else {
            return -1;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getInvocationText() {
        SimpleNode node = getNode();

        if (node != null) {
            return node.getText();
        } else {
            return "<invoked from Java code>";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param ci DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Class identifierToClass(ClassIdentifier ci) {
        return ci.getTargetClass();
    }

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        variables = null;
        methods = null;
        importedClasses = null;
        importedPackages = null;
        importedCommands = null;
        importedObjects = null;

        if (parent == null) {
            loadDefaultImports();
        }

        classCache = null;
        names = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     */
    public void importObject(Object obj) {
        if (importedObjects == null) {
            importedObjects = new ArrayList();
        }

        if (importedObjects.contains(obj)) {
            importedObjects.remove(obj);
        }

        importedObjects.add(obj);
        nameSpaceChanged();
    }

    /**
     * DOCUMENT ME!
     *
     * @param clas DOCUMENT ME!
     */
    public void importStatic(Class clas) {
        if (importedStatic == null) {
            importedStatic = new ArrayList();
        }

        if (importedStatic.contains(clas)) {
            importedStatic.remove(clas);
        }

        importedStatic.add(clas);
        nameSpaceChanged();
    }

    void setPackage(String packageName) {
        this.packageName = packageName;
    }

    String getPackage() {
        if (packageName != null) {
            return packageName;
        }

        if (parent != null) {
            return parent.getPackage();
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param commandName DOCUMENT ME!
     * @param interpreter DOCUMENT ME!
     * @param callstack DOCUMENT ME!
     * @param callerInfo DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws EvalError DOCUMENT ME!
     */
    public Object invokeLocalMethod(String commandName, Interpreter interpreter,
        CallStack callstack, BSHMethodInvocation callerInfo)
        throws EvalError {
        Object res = null;
        Object[] args = callerInfo.getArgsNode().getArguments(callstack, interpreter);
        Class[] argTypes = Types.getTypes(args);

        BshMethod meth = null;

        try {
            meth = getMethod(commandName, argTypes);
        } catch (UtilEvalError e) {
            throw e.toEvalError("Local method invocation", callerInfo, callstack);
        }

        if (meth != null) {
            res = meth.invoke(args, interpreter, callstack, callerInfo);
        }

        return res;
    }
}
