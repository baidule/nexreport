package bsh;


import jatools.dom.field.IndexField;
import jatools.math.Math;

import java.lang.reflect.InvocationTargetException;



/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class FieldMethodInvoker implements MethodInvoker {
	private FieldMethodInvoker(){}
    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     * @param methodName DOCUMENT ME!
     * @param interpreter DOCUMENT ME!
     * @param callstack DOCUMENT ME!
     * @param callerInfo DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws EvalError DOCUMENT ME!
     */
    public Object invoke(Object obj, String methodName, Interpreter interpreter,
        CallStack callstack, ArgmentsNode callerInfo)
        throws ReflectError, EvalError, InvocationTargetException {
    	
        if ("sum,max,min,avg,count".indexOf(methodName)>-1) {
        	
        	Object[] vals = ( (IndexField) obj).values() ;
        	if(methodName.equals( "sum"))
        		return Math.sum(vals );
        	else if(methodName.equals( "max"))
        		return Math.max( vals);
        	else if(methodName.equals( "min"))
        		return Math.min( vals);
        	else if(methodName.equals( "avg"))
        		return Math.avg( vals);
        	else if(methodName.equals( "count"))
        		return Math.count( vals);
        	else
        		return null;
        }else
        	
            return Reflect.invokeObjectMethod(obj, methodName,
                callerInfo.getArgsNode().getArguments(callstack, interpreter), interpreter,
                callstack, callerInfo);
        }
 
    
//    private Object[] getArrays(IndexField f)
//    {
//    	Object o = f.value() ;
//    	if(o instanceof Object[])
//    		return (Object[]) o;
//    	else
//    		return new Object[]{o};
//    }
}
