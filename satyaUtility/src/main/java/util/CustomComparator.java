/* 
 * @(#)CustomComparator.java Aug 24, 2012
 *
 * Copyright (c) 2012 Symphony Services. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Symphony Services, ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Symphony Services.
 *
 * SYMPHONY SERVICES MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SYMPHONY SERVICES SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * @version : $Revision: 1.0 $
 * @author  : syarra
 */

package util;

import java.lang.reflect.Method;
import java.util.Comparator;

import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;

/**
 * 
 * Cusom Comparator class comparison functin which imposes a total ordering on
 * some collection of user defined objects(or custom objects) based on
 * methodName or sorting colunName which is available in the custom objects.
 * <p>
 * Customcomparator can be passed to a sort method (such as Collections.sort) to
 * allow precise control over the sort order. Customcomparator can also be used
 * to control the order of certain data structures (such as TreeSet or TreeMap).
 * <p>
 * Manatory pass the sorting methodName in constructor arguments while creating
 * instance of CustomComparator. It takes sorting columnName as
 * setSortingColumnName which is available in the custom object and column field
 * should be getter method without no arguments if getMethodName is null or
 * length is not greater than zero.
 * <p>
 * This class can throws CastClassException if Sorting methodName or columnName
 * returns not implements Comparable data type object.
 * <p>
 * It can also throws Exception If no such method or column exists while
 * sorting.
 * 
 * @author syarra
 */
public class CustomComparator implements Comparator {

	private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER,
			CustomComparator.class.getName());

	/** sort Column Name */
	private String sortColumnName;

	/** Sorting column method Name */
	private String methodName;

	private final static String get = "get";

	/** Sorting column MethodClass Instance */
	private Method methodClassInstance;

	/**
	 * Constructs {@code CustomComparator} object with sorting methodName. This
	 * methodname is case sensitive while sorting data on specified object.
	 * 
	 * @param methodNme
	 */
	public CustomComparator(String methodNme) {
		this.methodName = methodNme;
	}

	/**
	 * Compares its two arguments for order. Returns a negative integer, zero,
	 * or a positive integer as the first argument is less than, equal to, or
	 * greater than the second.
	 * 
	 * @param obj1
	 * @param obj2
	 * @return -1 or 0 or 1
	 */
	public int compare(Object obj1, Object obj2) {
		if (obj1 == obj2) {
			return 0;
		} else if (obj1 == null) {
			return -1;
		} else if (obj2 == null) {
			return 1;
		}
		try {
			Object value1 = executeMethod(obj1);
			Object value2 = executeMethod(obj2);
			if (value1 instanceof Comparable && value2 instanceof Comparable) {
				Comparable c1 = (Comparable) value1;
				return c1.compareTo(value2);
			} else {
				throw new ClassCastException(
						" This data type is not supported for sort ");
			}
		} catch (Exception ex) {
			logger
					.fatal(
							"Sorting method Name is not exist in specified object and methodname is case sensitive :",
							ex);
		}
		return 0;
	}

	/**
	 * Execute the underlying method represented by getMethodName if method name
	 * is not null otherwise gets matching method on the specified object
	 * through sorting column name.
	 * 
	 * @param classInstance
	 * @param methodNme
	 * @return object
	 * @throws Exception
	 */
	protected Object executeMethod(Object classInstance) throws Exception {
		Object[] methodArgs = {};
		Class[] methodArgsTypes = {};
		if (methodClassInstance == null) {
			Class classVar = Class.forName(classInstance.getClass().getName());
			if (getMethodName() != null && getMethodName().trim().length() > 0) {
				methodClassInstance = classVar.getMethod(getMethodName(),
						                                 methodArgsTypes);
			} else {
				Method[] methods = classVar.getMethods();
				for (int i = 0; i < methods.length; i++) {
					Method method = methods[i];
					if (method.getName().equalsIgnoreCase(
							get + getSortColumnName())) {
						methodClassInstance = method;
						break;
					}
				}
			}
		}
		if (methodClassInstance == null)
			throw new Exception(
					"methodName or sorting ColumnName does not exist in specified object");
		return methodClassInstance.invoke(classInstance, methodArgs);
	}

	/**
	 * Returns the Sorting columnName
	 * 
	 * @return sortColumnName
	 */
	public String getSortColumnName() {
		return sortColumnName;
	}

	/**
	 * sets the Sorting columnName. This column name is not case sensitive while
	 * sorting data on specified object.
	 * 
	 * @param columnName
	 */
	public void setSortColumnName(String columnName) {
		this.sortColumnName = columnName;
	}

	public String getMethodName() {
		return methodName;
	}
}
