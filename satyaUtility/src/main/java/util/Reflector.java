/*
 * Created on Jan 4, 2007
 *
 * Contains reflection related methods  
 */
package util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * @author huchil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
/**
 * @author huchil
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Reflector {
	
	/**
	 * Copies all attributes from a source object into the target object. Only the public attributes are copied.
	 * @param target
	 * @param source
	 */
	public static void copyAttributes(Object target, Object source)
	{
		Class targetCls = target.getClass();
		Field[] sourceFields = source.getClass().getFields();
		for(int i=0; i < sourceFields.length; i++)
		{
			String fieldName = "";
			try{
				Field currField = sourceFields[i];
				fieldName = currField.getName();
				Object srcAttrib = currField.get(source);
				Field targetField = targetCls.getField(fieldName);
				targetField.set(target, srcAttrib);
			}catch(Exception ex){
				//System.out.println("Matching attributes for " + fieldName + " in Event not found");
			}
		}
	}
	
	/**
	 * Invokes a method on the given object using reflection
	 * @param someObj
	 * @param methodName
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public static Object invokeMethod(Object someObj, String methodName, Object[] args) throws Exception
	{
		Object returnObj = null;
		Class objCls = someObj.getClass();
		try{
			Method method = getMethod(objCls, methodName, args);
			  if(method == null)
				  throw new IllegalArgumentException("Cannot find method " + methodName + " in class " + objCls);
			  returnObj = method.invoke(someObj, args);
		}catch(Exception ex){
			throw ex;
		}
		return returnObj;
	}
	
	/**
	 * Returns the given method name in a class based on the method name and parameters
	 * @param mainCls
	 * @param methodName
	 * @param aclass
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Method getMethod(Class mainCls, String methodName, Class aclass[]) throws NoSuchMethodException
	{
		Method method;
		  try
		  {
			  method = mainCls.getMethod(methodName, aclass);
			  method.setAccessible(true);
			  return method;
		  }catch(NoSuchMethodException nosuchmethodexception)
		  {
		  	throw nosuchmethodexception;
		  }
	}
	/**
	 * Returns the given method name in a class based on the method name and parameters
	 * @param mainCls
	 * @param methodName
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 */
	public static Method getMethod(Class mainCls, String methodName, Object[] args) throws NoSuchMethodException
	{
		Class aclass[];
		  aclass = new Class[args.length];
		  for(int i = 0; i < aclass.length; i++)
			  aclass[i] = args[i].getClass();
		  
		  return getMethod(mainCls, methodName, aclass);
		  
	}
	
	/**
	 * Checks if a class implements a particular interface
	 * @param sourceCls
	 * @param interfaceName
	 * @return
	 */
	public static boolean doesImplement(Class sourceCls, String interfaceName)
	{
		Class[] intfs = sourceCls.getInterfaces();
		for(int i=0; i < intfs.length; i++)
		{
			Class currIntf = intfs[i];
			if(currIntf.getName().equals(interfaceName))
				return true;
			if(currIntf.getInterfaces() != null && currIntf.getInterfaces().length > 0)
			{
				boolean foundInterface = doesImplement(currIntf, interfaceName);
				if(foundInterface) return true;
			}
		}
		return false;
	}
}
