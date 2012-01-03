package bsh;

public class BSHRootNode extends SimpleNode
{
	static final String ROOT = "$r";

    BSHRootNode(int id) { super(id); }

    public Object eval( CallStack callstack, Interpreter it )
		throws EvalError
    {
        return it.get( "$r");
    }

  
}
