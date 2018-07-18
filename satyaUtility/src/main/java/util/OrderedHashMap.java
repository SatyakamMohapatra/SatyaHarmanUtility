/*
 * @(#)OrderedHashMap      Feb 15, 2002
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.symphonyrpm.applayer.common.constants.CommonConstants;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;


/**
 * @author kkumar
 *
 *Class Description 
 */
public class OrderedHashMap implements Serializable
{
	// added to resolve serialization uid issue
	static final long serialVersionUID = -8247958097656091633L;

	private ArrayList keys;
	private ArrayList values;
	/*
	 * The following conditional logic is being introduced to migrate the
	 * legacy OrderedHashMap implementation to use Java LinkedHashMap.
	 * Since there are lot of code that needs to change, the OrderedHashMap
	 * class implementation is being changed to use LinkedHashMap. Also to avoid
	 * runtime failure issues the condition would be controlled using system.properties
	 * file and on a later date would be phased out.
	 */
	private boolean useLHM = ConfigManager.getInstance().getPropertyAsBoolean(
		CommonConstants.SYS_PROPS_FILE, CommonConstants.USE_LINKED_HASHMAP, true) ;
//	private static boolean useLHM = true ;
	private LinkedHashMap linkedMap ;
	
	public OrderedHashMap()
	{
		this(15);	
		if (this.useLHM)
			linkedMap = new LinkedHashMap(15) ;
	}
	
	public OrderedHashMap(int size)
	{
		if (this.useLHM)
			linkedMap = new LinkedHashMap(size) ;
		else
		{
			keys = new ArrayList(size);	
			values = new ArrayList(size);	
		}
	}


	public void clear()
	{
		if (this.useLHM)
			linkedMap.clear();
		else
		{
			keys.clear();
			values.clear();
		}
	}
	
	public boolean containsKey(Object key)
	{
		if (this.useLHM)
			return linkedMap.containsKey(key) ;
		else
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
	}

	public boolean containsValue(Object value)
	{
		if (this.useLHM)
			return linkedMap.containsValue(value) ;
		else
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
	}

	public boolean isEmpty()
	{
		if (this.useLHM)
			return linkedMap.isEmpty();
		else
		{
			if(keys.size() == 0)
			{
				return true;
			}else
			{
				return false;
			}
		}
	}

	public int hashCode() 
	{
		if (this.useLHM)
			return linkedMap.hashCode() ;
		else
			return keys.hashCode() + values.hashCode();
	}
				
	public Object get(Object key)
	{
		if (this.useLHM)
			return linkedMap.get(key) ;
		else
		{
			int index = keys.indexOf(key);
			if(index>=0)
			{
				return values.get(index);
			}else
			{
				return null;
			}
		}
	}
	
	public Object getKey(int index)
	{
		if (this.useLHM)
		{
			if(index >= 0 && index < linkedMap.size())
			{
				Iterator iter = linkedMap.keySet().iterator() ;
				int i = 0 ;
				Object obj = null ;
				while (iter.hasNext())
				{
					obj = iter.next() ;
					if (index == i)
						break ;
					i++ ;
				}
					return obj ;
			}
			else 
				return null ;
		}
		else
		{
			if(index>=0 && index<keys.size())
			{
				return keys.get(index);
			}else
			{
				return null;
			}
		}
	}
	
	public Object getKey(Object value)
	{
		if (this.useLHM)
		{
			return getKeyForValue(value) ;
		}
		else
		{
			int index = values.indexOf(value);
			if(index>=0)
			{
				return keys.get(index);
			}else
			{
				return null;
			}
		}
	}

	/**
	 * @param value
	 * @return
	 */
	private Object getKeyForValue(Object value) {
		Iterator iter = linkedMap.keySet().iterator() ;
		while (iter.hasNext())
		{
			Object key = iter.next() ;
			Object mapValue = linkedMap.get(key)  ;
			if (value.equals(mapValue))
				return key ;
		}
		return null ;
	}

	public Object remove(Object key)
	{
		if (this.useLHM)
			return linkedMap.remove(key) ;
		else
		{
			int index = keys.indexOf(key);
			if(index>=0)
			{
				keys.remove(index);
				return values.remove(index);
			
			}else
			{
				return null;
			}
		}
	}
	
	public void put(Object key,Object value)
	{
		if (this.useLHM)
			linkedMap.put(key, value) ;
		else
		{
			int currIndex = keys.indexOf(key);
			if(currIndex<0)
			{
				keys.add(key);
				values.add(value);
			}else
			{
				values.remove(currIndex);
				values.add(currIndex,value);
			}
		}
	}
	
	public ArrayList values()
	{
		if (this.useLHM)
			return getValuesList() ;
		else
			return values;
	}
	
	/**
	 * @return
	 */
	private ArrayList getValuesList() {
		ArrayList list = new ArrayList() ;
		int size = linkedMap.size() ;
		if (size > 0)
		{
			list = new ArrayList(size) ;
			Iterator iter = linkedMap.keySet().iterator() ;
			while (iter.hasNext())
			{
				list.add(linkedMap.get(iter.next())) ;
			}
		}
		return list;
	}

	public ArrayList keySet()
	{
		if (this.useLHM)
			return getKeyList() ;
		else
			return keys;
	}
	
	/**
	 * @return
	 */
	private ArrayList getKeyList() {
		ArrayList list = new ArrayList() ;
		int size = linkedMap.size() ;
		if (size > 0)
		{
			list = new ArrayList(size) ;
			Iterator iter = linkedMap.keySet().iterator() ;
			while (iter.hasNext())
			{
				list.add(iter.next()) ;
			}
		}
		return list;
	}

	public Iterator iterator()
	{
		if (this.useLHM)
			return linkedMap.keySet().iterator() ;
		else
			return keys.iterator();
	}
	
	public int size()
	{
		if (this.useLHM)
			return linkedMap.size() ;
		else
			return keys.size();
	}
	
	public void putAll(OrderedHashMap oMap)
	{
		if (this.useLHM)
		{
			linkedMap.putAll(oMap.getLinkedHashMap()) ;
		}
		else
		{
			if(oMap!=null)
			{
				Iterator iterator = oMap.iterator();
				while(iterator.hasNext())		
				{
					Object key = iterator.next();
					Object value = oMap.get(key);
					int index = keys.indexOf(key);
					if(index>=0)	 
					{
						values.remove(index);
						values.add(index,value);
						//replace
					}else
					{
						put(key,value);
					}
				}
			}
		}
	}
	
	public void putAll(Map oMap)
	{
		if (this.useLHM)
			linkedMap.putAll(oMap) ;
		else
		{
			if(oMap!=null)
			{
				Iterator iterator = oMap.keySet().iterator();
				while(iterator.hasNext())		
				{
					Object key = iterator.next();
					Object value = oMap.get(key);
					int index = keys.indexOf(key);
					if(index>=0)	 
					{
						values.remove(index);
						values.add(index,value);
						//replace
					}else
					{
						put(key,value);
					}
				}
			}
		}
	}
	
	public String toString()
	{
		if (this.useLHM)
		{
			return linkedMap.toString() ;
		}
		else
		{
			StringBuffer buffer = new StringBuffer();
			buffer.append("[");
			if(keys!=null && values!=null)
			{
				int  keysSize = keys.size();
				for(int i=0;i<keysSize;i++)
				{
					buffer.append("("+keys.get(i)+"="+values.get(i)+") ");
				}
			}
			buffer.append("]");
			return buffer.toString();
		}
	}
	
	public static void main(String args[])
	{
		OrderedHashMap map = new OrderedHashMap();
		map.put("1","one");
		map.put("2","two");
		map.put("3","three");
		map.put("2","four");
		testLinkedHashMap(map) ;
		print("Testing LinkedHashMap funtionality...") ;
		OrderedHashMap ohm = new OrderedHashMap() ;
		print(ohm.getLinkedHashMap()) ;
	}

	/**
	 * @param map
	 */
	private static void testLinkedHashMap(OrderedHashMap map) {
		Iterator iter = map.iterator();
		while (iter.hasNext())
		{
			print(iter.next()) ;
		}
		
	}

	/**
	 * 
	 */
	private LinkedHashMap getLinkedHashMap() {
		return linkedMap ;
	}

	/**
	 * @param string
	 */
	private static void print(Object obj) {
		
	}
	
    /**
     * @return
     */
    public ArrayList getKeys()
    {
        return keys;
    }

    /**
     * @return
     */
    public ArrayList getValues()
    {
        return values;
    }

    /**
     * @param list
     */
    public void setKeys(ArrayList list)
    {
        keys= list;
    }

    /**
     * @param list
     */
    public void setValues(ArrayList list)
    {
        values= list;
    }

/**
 * @return
 */
public LinkedHashMap getLinkedMap()
{
    return linkedMap;
}

    /**
     * @return
     */
    public boolean isUseLHM()
    {
        return useLHM;
    }

/**
 * @param map
 */
public void setLinkedMap(LinkedHashMap map)
{
    linkedMap= map;
}

public void setLinkedMap(Map map)
{
	linkedMap = new LinkedHashMap(map);
}

    /**
     * @param b
     */
    public void setUseLHM(boolean b)
    {
        useLHM= b;
    }

}
