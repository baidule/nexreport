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

/*
        Note: great care (and lots of typing) were taken to insure that the
        namespace and interpreter references are passed on the stack and not
        (as they were erroneously before) installed in instance variables...
        Each of these node objects must be re-entrable to allow for recursive
        situations.

        The only data which should really be stored in instance vars here should
        be parse tree data... features of the node which should never change (e.g.
        the number of arguments, etc.)

        Exceptions would be public fields of simple classes that just publish
        data produced by the last eval()... data that is used immediately. We'll
        try to remember to mark these as transient to highlight them.

*/
public class SimpleNode implements Node {
	SimpleNode next;
    /**
     * DOCUMENT ME!
     */
    public static SimpleNode JAVACODE = new SimpleNode(-1) {
            public String getSourceFile() {
                return "<Called from Java Code>";
            }

            public int getLineNumber() {
                return -1;
            }

            public String getText() {
                return "<Compiled Java Code>";
            }
        };

    protected Node parent;
    public Node[] children;
    protected int id;
    Token firstToken;
    Token lastToken;
    String as;
    String dec;

    /** the source of the text from which this was parsed */
    String sourceFile;

    /**
     * Creates a new SimpleNode object.
     *
     * @param i DOCUMENT ME!
     */
    public SimpleNode(int i) {
        id = i;
    }

    /**
     * DOCUMENT ME!
     *
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
   

    /**
     * DOCUMENT ME!
     */
    public void jjtOpen() {
    }

    /**
     * DOCUMENT ME!
     */
    public void jjtClose() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param n DOCUMENT ME!
     */
    public void jjtSetParent(Node n) {
        parent = n;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Node jjtGetParent() {
        return parent;
    }

    //public SimpleNode getParent() { return (SimpleNode)parent; }
    /**
     * DOCUMENT ME!
     *
     * @param n DOCUMENT ME!
     * @param i DOCUMENT ME!
     */
    public void jjtAddChild(Node n, int i) {
        if (children == null) {
            children = new Node[i + 1];
        } else if (i >= children.length) {
            Node[] c = new Node[i + 1];
            System.arraycopy(children, 0, c, 0, children.length);
            children = c;
        }

        children[i] = n;
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Node jjtGetChild(int i) {
        return children[i];
    }
    
    public Node jjtGetPreviousSibling()
    {
    	if(this.parent != null)
    	{
    		Node[] children = ((SimpleNode )this.parent).children ;
    		for (int i = 0; i < children.length; i++) {
				if(children[i] == this)
				{
					if(i >0)
						return children[i-1];
					
					break;
				}
			}
    		
    	}
    	
    	return null;


    	
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public SimpleNode getChild(int i) {
        return (SimpleNode) jjtGetChild(i);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int jjtGetNumChildren() {
        return (children == null) ? 0 : children.length;
    }

    /*
            You can override these two methods in subclasses of SimpleNode to
            customize the way the node appears when the tree is dumped.  If
            your output uses more than one line you should override
            toString(String), otherwise overriding toString() is probably all
            you need to do.
    */
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return ParserTreeConstants.jjtNodeName[id];
    }

    /**
     * DOCUMENT ME!
     *
     * @param prefix DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString(String prefix) {
        return prefix + toString();
    }

    /*
            Override this method if you want to customize how the node dumps
            out its children.
    */
    /**
     * DOCUMENT ME!
     *
     * @param prefix DOCUMENT ME!
     */
    public void dump(String prefix) {
      

        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                SimpleNode n = (SimpleNode) children[i];

                if (n != null) {
                    n.dump(prefix + " ");
                }
            }
        }
    }

    //  ---- BeanShell specific stuff hereafter ----  //

    /**
            Detach this node from its parent.
            This is primarily useful in node serialization.
            (see BSHMethodDeclaration)
    */
    public void prune() {
        jjtSetParent(null);
    }

    /**
            This is the general signature for evaluation of a node.
    */
    public Object eval(CallStack callstack, Interpreter interpreter)
        throws EvalError {
        throw new InterpreterError("Unimplemented or inappropriate for " + getClass().getName());
    }

    /**
            Set the name of the source file (or more generally source) of
            the text from which this node was parsed.
    */
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    /**
            Get the name of the source file (or more generally source) of
            the text from which this node was parsed.
            This will recursively search up the chain of parent nodes until
            a source is found or return a string indicating that the source
            is unknown.
    */
    public String getSourceFile() {
        if (sourceFile == null) {
            if (parent != null) {
                return ((SimpleNode) parent).getSourceFile();
            } else {
                return "<unknown file>";
            }
        } else {
            return sourceFile;
        }
    }

    /**
            Get the line number of the starting token
    */
    public int getLineNumber() {
        return firstToken.beginLine;
    }

    /**
            Get the ending line number of the starting token
    public int getEndLineNumber() {
            return lastToken.endLine;
    }
    */

    /**
            Get the text of the tokens comprising this node.
    */
    public String getText() {
        StringBuffer text = new StringBuffer();
        Token t = firstToken;

        while (t != null) {
            text.append(t.image);

            if (!t.image.equals(".")) {
                text.append(" ");
            }

            if ((t == lastToken) || t.image.equals("{") || t.image.equals(";")) {
                break;
            }

            t = t.next;
        }

        return text.toString();
    }
}
