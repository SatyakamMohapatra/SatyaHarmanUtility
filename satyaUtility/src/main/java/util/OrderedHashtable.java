/*
 * @(#)OrderedHashtable      Feb 15, 2002
 *
 * Copyright (c) 2002 Symphony Services. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Symphony Services, ("Confidential Information").  You shall not
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
 * @version    3.2
 * @author     kkumar
 */
package util;

import java.io.Serializable;
import java.util.Vector;

/**
 * @author kkumar
 *
 *Class Description 
 */
public class OrderedHashtable implements Serializable
{
	private Vector keys;
	private Vector values;
	
	public OrderedHashtable()
	{
		this(15);	
	}
	
	public OrderedHashtable(int size)
	{
		keys = new Vector(size);	
		values = new Vector(size);	
	}


	public synchronized void clear()
	{
		keys.removeAllElements();
		values.removeAllElements();
	}
	
	public synchronized boolean containsKey(Object key)
	{
		int index = keys.indexOf(key);
		if(index>=0)
		{
			return true;
		}else
		{
			return false;
		}
	}

	public synchronized boolean containsValue(Object value)
	{
		int index = values.indexOf(value);
		if(index>=0)
		{
			return true;
		}else
		{
			return false;
		}
	}

	public synchronized boolean isEmpty()
	{
		if(keys.size() == 0)
		{
			return true;
		}else
		{
			return false;
		}
	}

	public synchronized int hashCode() 
	{
		return keys.hashCode() + values.hashCode();
	}
				
	public synchronized Object get(Object key)
	{
		int index = keys.indexOf(key);
		if(index>=0)
		{
			return values.elementAt(index);
		}else
		{
			return null;
		}
	}

	public synchronized Object getKey(Object value)
	{
		int index = values.indexOf(value);
		if(index>=0)
		{
			return keys.elementAt(index);
		}else
		{
			return null;
		}
	}

	public synchronized Object remove(Object key)
	{
		int index = keys.indexOf(key);
		if(index>=0)
		{
			keys.remove(index);
			Object returnObject = values.elementAt(index);
			values.removeElementAt(index);
			return returnObject;
			
		}else
		{
			return null;
		}
	}
	
	public synchronized void put(Object key,Object value)
	{
		int currIndex = keys.indexOf(key);
		if(currIndex<0)
		{
			keys.addElement(key);
			values.addElement(value);
		}else
		{
			values.removeElementAt(currIndex);
			values.insertElementAt(value,currIndex);
		}
	}
	
	public synchronized Vector values()
	{
		return values;
	}
	
	public synchronized Vector keySet()
	{
		return keys;
	}
	
	public synchronized int size()
	{
		return keys.size();
	}
	
	public synchronized void putAll(OrderedHashtable oMap)
	{
		if(oMap!=null && (oMap.keySet()!=null && oMap.values()!=null) && (oMap.keySet().size() == oMap.values().size()))
		{
			this.keySet().addAll(oMap.keySet());
			this.values().addAll(oMap.values());
		}
	}
	
	public synchronized String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		if(keys!=null && values!=null)
		{
			int  keysSize = keys.size();
			for(int i=0;i<keysSize;i++)
			{
				buffer.append("("+keys.elementAt(i)+"="+values.elementAt(i)+") ");
			}
		}
		buffer.append("]");
		return buffer.toString();
	}
	
	public static void main(String args[])
	{
		OrderedHashtable map = new OrderedHashtable();
		map.put("1","one");
		map.put("2","two");
		map.put("3","three");
		map.put("2","four");
	}
	
}
