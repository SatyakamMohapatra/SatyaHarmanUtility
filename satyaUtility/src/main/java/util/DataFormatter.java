/*
 * Created on Jun 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author skumar
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DataFormatter {

		private static final char DEF_PARAM_DELIMETER = '|';
		private static final char DEF_NAME_VAL_DELIMETER = '=';
		
		public static Map constructMapFromString(String delimitedString,char paramDelimeter)
		{
			Map constructedMap = new HashMap();
			StringTokenizer tokenizer = new StringTokenizer(delimitedString,""+DEF_PARAM_DELIMETER);
			while(tokenizer.hasMoreTokens())
			{
				String token = tokenizer.nextToken();
				int nIndex = token.indexOf(DEF_NAME_VAL_DELIMETER);
				if(nIndex>0)
				{
					String name = token.substring(0,nIndex);
					String value = token.substring(nIndex+1);
					constructedMap.put(name,value);
				}
			}
			return constructedMap;
		}
		
		public static Map constructMapFromString(String delimitedString)
		{
			return constructMapFromString(delimitedString,DEF_PARAM_DELIMETER);
		}
		
		public static String formatCollectionData(String name,Collection data,char delimeter)
		{
			StringBuffer buffer = new StringBuffer();
			if(name!=null && name.length()>0)
			{
				buffer.append(name).append(DEF_NAME_VAL_DELIMETER);
			}
			if(data!=null)
			{
				Iterator dataIterator = data.iterator();
				if(dataIterator.hasNext())buffer.append(dataIterator.next());
				while(dataIterator.hasNext())buffer.append(delimeter).append(dataIterator.next());			
			}
			return buffer.toString();
		}

		public static String formatCollectionData(String name,Collection data)
		{
			return formatCollectionData(name,data,DEF_PARAM_DELIMETER);
		}

		public static String formatCollectionData(Collection data)
		{
			return formatCollectionData(null,data,DEF_PARAM_DELIMETER);
		}

		public static String formatCollectionData(Collection data,char delimeter)
		{
			return formatCollectionData(null,data,delimeter);
		}

		public static String formatMapData(Map data,char delimeter)
		{
			StringBuffer buffer = new StringBuffer();
			Iterator dataIterator = data.keySet().iterator();
			if(dataIterator.hasNext())
			{
				String name = (String)dataIterator.next();
				String value = (String)data.get(name);
				buffer.append(name).append(DEF_NAME_VAL_DELIMETER).append(value);
			}
			while(dataIterator.hasNext())
			{
				buffer.append(delimeter);
				String name = (String)dataIterator.next();
				String value = (String)data.get(name);
				buffer.append(name).append(DEF_NAME_VAL_DELIMETER).append(value);
			}
			return buffer.toString();
		}

		public static String formatMapData(Map data)
		{
			return formatMapData(data,DEF_PARAM_DELIMETER);
		}
}

