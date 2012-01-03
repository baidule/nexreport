/*****************************************************************************
 *                                                                           *
 *  This file is part of the BeanShell Java Scripting distribution.          *
 *  Documentation and updates may be found at http://www.beanshell.org/      *
 *                                                                           *
 *  Sun Public License Notice:                                               *
 *                                                                           *
 *  The contents of this file are subject to the Sun Public License Version  *
 *  1.0 (the "License"); you may not use this file except in compliance with *
 *  the License. A copy of the License is available at http://www.sun.com    *
 *                                                                           *
 *  The Original Code is BeanShell. The Initial Developer of the Original    *
 *  Code is Pat Niemeyer. Portions created by Pat Niemeyer are Copyright     *
 *  (C) 2000.  All Rights Reserved.                                          *
 *                                                                           *
 *  GNU Public License Notice:                                               *
 *                                                                           *
 *  Alternatively, the contents of this file may be used under the terms of  *
 *  the GNU Lesser General Public License (the "LGPL"), in which case the    *
 *  provisions of LGPL are applicable instead of those above. If you wish to *
 *  allow use of your version of this file only under the  terms of the LGPL *
 *  and not to allow others to use your version of this file under the SPL,  *
 *  indicate your decision by deleting the provisions above and replace      *
 *  them with the notice and other provisions required by the LGPL.  If you  *
 *  do not delete the provisions above, a recipient may use your version of  *
 *  this file under either the SPL or the LGPL.                              *
 *                                                                           *
 *  Patrick Niemeyer (pat@pat.net)                                           *
 *  Author of Learning Java, O'Reilly & Associates                           *
 *  http://www.pat.net/~pat/                                                 *
 *                                                                           *
 *****************************************************************************/
package bsh;

public class BSHAmbiguousName extends SimpleNode {
    /**
     * DOCUMENT ME!
     */
    public String text;

    BSHAmbiguousName(int id) {
        super(id);
    }

    /**
     * DOCUMENT ME!
     *
     * @param namespace DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Name getName(NameSpace namespace) {
        return namespace.getNameResolver(text);
    }

    private int getSuffixType() {
        BSHPrimarySuffix firstSuffix = null;
        int size = this.parent.jjtGetNumChildren();

        for (int i = 0; i < size; i++) {
            if (this.parent.jjtGetChild(i) == this) {
                if ((i < (size - 1)) && this.parent.jjtGetChild(i + 1) instanceof BSHPrimarySuffix) {
                    firstSuffix = (BSHPrimarySuffix) this.parent.jjtGetChild(i + 1);
                }

                break;
            }
        }

        return (firstSuffix != null) ? firstSuffix.operation : (-1);
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
//    private Object toObject(CallStack callstack, Interpreter interpreter)
//        throws EvalError {
//        return toObject(callstack, interpreter, false);
//    }

    Object toObject(CallStack callstack, Interpreter interpreter, boolean forceClass/*,boolean ignorevalue*/)
        throws EvalError {
    //	System.out.println(this.getSuffixType() );
    	
        try {
            Object obj = getName(callstack.top()).toObject(callstack, interpreter, forceClass);

            return obj;
        } catch (UtilEvalError e) {
            //e.printStackTrace();
            throw e.toEvalError(this, callstack);
        }
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
    public Class toClass(CallStack callstack, Interpreter interpreter)
        throws EvalError {
        try {
            return getName(callstack.top()).toClass();
        } catch (ClassNotFoundException e) {
            throw new EvalError(e.getMessage(), this, callstack);
        } catch (UtilEvalError e2) {
            // ClassPathException is a type of UtilEvalError
            throw e2.toEvalError(this, callstack);
        }
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
    public LHS toLHS(CallStack callstack, Interpreter interpreter)
        throws EvalError {
        try {
            return getName(callstack.top()).toLHS(callstack, interpreter);
        } catch (UtilEvalError e) {
            throw e.toEvalError(this, callstack);
        }
    }

    /*
            The interpretation of an ambiguous name is context sensitive.
            We disallow a generic eval( ).
    */

    /**
     * DOCUMENT ME!
     *
     * @param callstack DOCUMENT ME!
     * @param interpreter DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws EvalError DOCUMENT ME!
     * @throws InterpreterError DOCUMENT ME!
     */
    public Object eval(CallStack callstack, Interpreter interpreter)
        throws EvalError {
    	return toObject(callstack,interpreter,false);

    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return "AmbigousName: " + text;
    }
}
