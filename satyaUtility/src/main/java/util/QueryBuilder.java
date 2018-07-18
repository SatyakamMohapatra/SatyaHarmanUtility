/*
 * @(#)QueryBuilder      Feb 24, 2002
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

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;
import com.symphonyrpm.applayer.common.constants.SummaryConstants;
import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;


public class QueryBuilder {

	private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER,QueryBuilder.class.getClass().getName());	
	
	private static String fromString = "FROM";
	private static String selectString = "SELECT";
	private static String countString = "COUNT";
	private static String distinctKeyWord = "DISTINCT";

	private static ConfigManager configManager = ConfigManager.getInstance();
	private static final String sqlConfigFileName = configManager.getProperty(ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME,ConfigurationConstants.LOOKUP_SQL_PROPERTIES_FILE_PROP_NAME);
		
	private static String DB2DateFormat = configManager.getPropertyAsString(sqlConfigFileName,ConfigurationConstants.DBDateFormat,"yyyy-MM-dd");
	private static String dateFunction = configManager.getPropertyAsString(sqlConfigFileName,ConfigurationConstants.DateFunction,"DATE");
	private static String lCaseFunction = configManager.getPropertyAsString(sqlConfigFileName,ConfigurationConstants.LCaseFunction,"LCASE");
	private static String ceilFunction=configManager.getPropertyAsString(sqlConfigFileName,ConfigurationConstants.CeilFunction,"ceil");
	private static String escapeKeyword = configManager.getPropertyAsString(sqlConfigFileName,ConfigurationConstants.EscapeKeyword,"ESCAPE");
	private static char escapeChar = configManager.getPropertyAsChar(sqlConfigFileName,ConfigurationConstants.EscapeChar,'^');
	private static boolean useAliases = configManager.getPropertyAsBoolean(sqlConfigFileName,ConfigurationConstants.USE_ALIASES,true);
	
	public static String getFetchQuery(String query,String rowsNo)
	{
		return getFetchQuery(query,rowsNo,false);
	}
	
	public static String getFetchQuery(String query,String rowsNo,boolean appendAndClause)
	{
		return getOrderbySortorderFetchQuery(query,null,null,rowsNo,appendAndClause);
	}
	
	public static String getOrderbySortorderFetchQuery(String query,String orderBy, String sortOrder, String rowsNo)
	{
		return getOrderbySortorderFetchQuery(query,orderBy,sortOrder,rowsNo,false);
	}
	
	public static String getOrderbySortorderFetchQuery(String query,String orderBy, String sortOrder, String rowsNo, boolean appendAndClause)
	{
		String databaseSelected  = configManager.getProperty(ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME,ConfigurationConstants.DATABASE_VENDOR_PROP);
		if(databaseSelected != null) databaseSelected  = databaseSelected.trim();
		logger.debug("databaseSelected = " + databaseSelected);
		if( databaseSelected != null && databaseSelected.equalsIgnoreCase(ConfigurationConstants.ORACLE_DATABASE ) ) 
		{
			logger.debug(" the data base is oracle .... ");
			/* append the order by and sort order clause */
			query = getOrderByQuery(query,orderBy,sortOrder);

			/* append the number of rows to fetch clause */
			if(rowsNo != null && !rowsNo.trim().equals("0"))
			{
				query = "SELECT * FROM ( "+query+" ) WHERE ROWNUM<=" + rowsNo;					
			}
		} else if(databaseSelected != null && databaseSelected.equalsIgnoreCase(ConfigurationConstants.DB2_DATABASE))
		{
			logger.debug(" the data base is db2 .... ");
			
			/* append the order by and sort order clause */
			query = getOrderByQuery(query,orderBy,sortOrder);
			
			/* append the number of rows to fetch clause */
			if(rowsNo != null && !rowsNo.trim().equals("0"))
			{
				query += " FETCH FIRST " + rowsNo + " ROWS ONLY";;
			}
			
		}  else if( databaseSelected != null && databaseSelected.equalsIgnoreCase(ConfigurationConstants.SQLSERVER_DATABASE ) ) 
		{
			logger.debug(" the data base is sqlserver .... ");
			query = getOrderByQuery(query,orderBy,sortOrder);
		}  
		 
		logger.debug("query getting returned = " + query);
		return query;
	}
	
	
	public static String getOrderByQuery(String query,String orderByColName,String sortOrder)
	{			
		if(orderByColName!=null && (orderByColName=orderByColName.trim()).length()>0)
		{
			query = query + " ORDER BY  " + orderByColName;
			if(sortOrder!=null && sortOrder.equals(ConfigurationConstants.DESC_SORT_ORDER))
			{
				query +=  " " + sortOrder;
			}
		}
		logger.info("Order By query is "+query);
		return query;
	}
	
	public static String getSortOrderByQuery(String query, String orderByColName, String sortOrder) {			
		if (orderByColName != null && (orderByColName = orderByColName.trim()).length() > 0) {
			query = query + " ORDER BY  " + orderByColName.toUpperCase();
			if (sortOrder != null &&  (sortOrder = sortOrder.trim()).length() > 0)	{
				query +=  " " + sortOrder.toUpperCase();
			} else {
				query +=  " " + ConfigurationConstants.AESC_SORT_ORDER;
			}
		}
		logger.info("Order By query is "+query);
		return query;
	}

	public static String formCommaSeparatedString(Object col)
	{
		String str = "";
		if(col!=null)
		{
			Iterator iterator = null;
			if(col instanceof Collection)
			{
				iterator = ((Collection)col).iterator();
			}else if (col instanceof Iterator)
			{
				iterator = (Iterator)col;
			}else
			{
				return str;
			}
			while(iterator.hasNext())
			{
				Object obj = iterator.next();
				if(obj instanceof String)
				{   
					String stringObj = (String)obj;
					stringObj= stringObj.contains("'")?stringObj.replaceAll("'", "''"):stringObj;
					str = str+"'"+stringObj+"',";
				}else if(obj instanceof Integer)
				{
					str = str+((Integer)obj).intValue()+",";
				}else if(obj instanceof Double)
				{
					str = str+((Double)obj).doubleValue()+",";
				}
			}
			if(str.indexOf(",")>0)								
			{
				str = str.substring(0,str.length()-1);		
			}
		}
		logger.info("Comma separeted String "+str);
		return str;
	}

	public  static String appendORCondition(String query,String colNames[],String[] values)
	{
		if(colNames!=null && values!=null)
		{
			if(colNames.length > 0 && values.length>0)
			{ 
				String orString=lCaseFunction+"("+colNames[0]+") like '%"+escapeValue(values[0])+"%' "+escapeKeyword +" '"+escapeChar+"' ";		
				for(int i=1;i<colNames.length && i<values.length;i++)
				{
					orString = orString+" OR "+lCaseFunction+"("+colNames[i]+") like '%"+escapeValue(values[i])+"%' "+escapeKeyword +" '"+escapeChar+"' ";		
				}
				
				if(orString.length()>0)
				{
					String tmpquery = query.toUpperCase();
					if(tmpquery.indexOf("WHERE") <= 0)
					{
						query = query +	" WHERE ";
					}else
					{
						query = query + " AND ";	
					}
					query = query+" ( "+orString+" ) ";
				}
			}
		}
		return query;	
	}
	
	private static String appendCondition(String query,String columnName,boolean functionUse,String function,boolean doAppendWhere)
	{
		String tmpquery = query.toUpperCase();
		if (tmpquery.indexOf("WHERE") <= 0 || doAppendWhere) {
			query = query +	" WHERE ";
		} else {
			query = query + " AND ";	
		}
		if (functionUse) {
			query = query+" "+function+"("+columnName+")";		
		} else {
			query = query+columnName;		
		}
		return query;
	}

	private static String appendConditionforSQLServer(String query,String columnName,String value,boolean functionUse,String function,boolean doAppendWhere)
	{
		String tmpquery = query.toUpperCase();
		if(tmpquery.indexOf("WHERE") <= 0 || doAppendWhere)
		{
			query = query +	" WHERE ";
		}else
		{
			query = query + " AND ";	
		}
		if(functionUse)
		{
			query = query+" "+function+columnName+", 126) = '" + value + "'";		
		}else
		{
			query = query+columnName;		
		}
		return query;
	}

	private static String appendCondition(String query,String columnName,String value,boolean isPartialSearch,boolean doAppendWhere)
	{
		if(value!=null && (value=value.trim()).length()>0)
		{
			query = appendCondition(query,columnName,true,lCaseFunction,doAppendWhere);
			value = value.toLowerCase();
			value = value.replace('*','%');
			value = escapeValue(value);
			
/*			if(!value.startsWith("%") && !isPartialSearch)
			{
				value = "%"+value;
			}
			if(!value.endsWith("%"))
			{
				value = value+"%";
			}
*/			
			value = "%"+value+"%";
			query = query +" LIKE '"+value+"' "+escapeKeyword +" '"+escapeChar+"' ";
		}
		return query;
	}
	
	private static String escapeValue(String value)
	{
		char charArray [] = value.toCharArray();
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<charArray.length;i++)
		{
			switch(charArray[i])
			{
				case  '_'  : buffer.append(escapeChar).append('_'); 
							 break;
				case  '%'  : buffer.append(escapeChar).append('%');
							 break;
				case  '^'  : buffer.append(escapeChar).append('^');
				 			 break;			 
				default    : buffer.append(charArray[i]);			 			
			}
		}
		return buffer.toString();
	}
	
	public static String formConditionQuery(Collection columnNames,Map searchMap,String query)
	{
		if(columnNames!=null)
		{
			Iterator colNameIterator = columnNames.iterator();
			while(colNameIterator.hasNext())
			{
				String colName = (String)colNameIterator.next();
				query = formConditionQuery(colName,searchMap,query);
			}
		}
		return query;
	}

	public static String formConditionQuery(String columnName,Map searchMap,String query,Class type)
	{
		if(searchMap!=null)
		{
			Object obj = searchMap.get(columnName);
			if(obj!=null)
			{
				try{
						Object typeObj = convertObjectToType(type,obj);
						searchMap.put(columnName,typeObj);			
						query = formConditionQuery(columnName,searchMap,query);
				}catch(Exception e)
				{
					logger.debug(e.getMessage(),e);								
				}
			}
		}
		return query;
	}
	
	public static Object convertObjectToType(Class type,Object toConvert)throws Exception
	{
		Constructor con = type.getConstructor(new Class[]{toConvert.getClass()});
		Object typeObj = con.newInstance(new Object[]{toConvert});
		return typeObj;
	}
	
	public static String formConditionQuery(String columnName,Map searchMap,String query)
	{
		return formConditionQuery(columnName,searchMap,query,false);
	}
	
	public static String appendConditionToQuery(String query,Map searchMap,boolean doAppendWhere)throws Exception
	{
		if(searchMap == null)return query;
		Object tmp =  searchMap.get(SummaryConstants.SEARCH_COLUMN_NAME);
		Object tmp2 = searchMap.get(SummaryConstants.SEARCH_COLUMN_TEXT);
		if(tmp==null || tmp.toString().length()==0 || tmp2==null || tmp2.toString().length()==0)return query;
		String columnName = tmp.toString();
		tmp = searchMap.get(SummaryConstants.SEARCH_COLUMN_DATA_TYPE);
		Class type = String.class;
		if(tmp!=null)
		{
			if(ConfigurationConstants.TYPE_DATE.equalsIgnoreCase(tmp.toString()))
			{
				type = Date.class;
			}else if(ConfigurationConstants.TYPE_NUMBER.equalsIgnoreCase(tmp.toString()))
			{
				type = Double.class;
			}else if(ConfigurationConstants.TYPE_NUMBER_INTEGER.equalsIgnoreCase(tmp.toString()))
			{
				type = Integer.class;
			}
		}
		Object colValue = convertObjectToType(type,tmp2);
		return formConditionQuery(query,columnName,colValue,true,doAppendWhere);
	}

	public static String appendConditionToQuery(String query,String columnName,Object columnValue,boolean doAppendWhere) throws Exception {
		if (columnName == null || columnName.trim().length()== 0 || columnValue == null || columnValue.toString().trim().length() == 0) {
			return query;
		}
		return formConditionQuery(query,columnName,columnValue,false,doAppendWhere);
	}

	public static String appendConditionToQuery(String query,Map searchMap)throws Exception
	{
		return appendConditionToQuery(query,searchMap,false);
	}
	
	private static String formConditionQuery(String query,String columnName,Object columnValue,boolean isPartialSearch,boolean doAppendWhere)
	{

		String databaseSelected  = configManager.getProperty(ConfigurationConstants.SYSTEM_PROPERTIES_FILE_NAME,ConfigurationConstants.DATABASE_VENDOR_PROP);
		if(databaseSelected != null) databaseSelected  = databaseSelected.trim();
		logger.debug("databaseSelected = " + databaseSelected);
		
		if(columnValue!=null)
		{
			String value = null;
			if(columnValue instanceof String)
			{
				value = (String)columnValue;
				query = appendCondition(query,columnName,value,isPartialSearch,doAppendWhere);					
			}else if(columnValue instanceof Date)
			{
				SimpleDateFormat df = DateFormatter.getLocaleDateFormat(DB2DateFormat);
				    
				Date date = (Date)columnValue;
				value = df.format(date);
				if(databaseSelected != null && databaseSelected.equalsIgnoreCase(ConfigurationConstants.SQLSERVER_DATABASE))
				{
					logger.debug(" the data base is sqlserver .... ");
					query = appendConditionforSQLServer(query,columnName,value,true,dateFunction,doAppendWhere);
				} else {	// for oracle,db2
				   query = appendCondition(query,columnName,true,dateFunction,doAppendWhere);
				   query = query+" = "+dateFunction+"('"+value+"')";							
				}
					
			}else if(columnValue instanceof Integer)
			{
				int val = ((Integer)columnValue).intValue();
				query = appendCondition(query,columnName,false,null,doAppendWhere);
				query = query+" = "+val;							
			}else if(columnValue instanceof Double)
			{
				//added ceil method, db value is 1.11076013590000E+009 and value
				// displayed at front end is 1,110,760,136, if search on 1,110,760,136 no rows are returned. 

				double val = ((Double)columnValue).doubleValue();
				//query = appendCondition(query,columnName,false,null);
				query = appendCondition(query,columnName,true,ceilFunction,doAppendWhere);
				//query = query+" >= "+val;							
				//query = query+" >= "+ceilFunction+"("+val+")";
				//commented for bugId 25297
				query = query+" = ("+val+")";                           
			}
			else if(columnValue instanceof Collection || columnValue instanceof Iterator)
			{
				String commaString = formCommaSeparatedString(columnValue);
				if(commaString.length()>0)
				{
					query = appendCondition(query,columnName,false,null,doAppendWhere);
					query = query+" IN ("+commaString+")";
				}
			}				
		}
		return query;
	}
	
	public static String formConditionQuery(String columnName,Map searchMap,String query,boolean isPartialSearch)
	{
		if(searchMap!=null)
		{
			Object obj = searchMap.get(columnName);
			query = formConditionQuery(query,columnName,obj,isPartialSearch,false);
		}
		logger.info("Condition Query "+query);
		return query;
	}
	
	public static String getCountQuery(String query)
	{
		return getCountQuery(query,"");
	}

	public static String getCountQuery(String query,String distColName)
	{
		StringBuffer sBuff = new StringBuffer();
		String countQuery = null;
		
		if(distColName == null || (distColName=distColName.trim()).length()==0)
		{
			distColName	="*";
		}else
		{
			distColName	= distinctKeyWord+" "+distColName.trim();
		}
		
		logger.info("Count Query String "+query);
		if(query!=null)
		{
			query = query.trim();
			String tmpquery = query.toUpperCase();
			
			int sIndex = tmpquery.indexOf(selectString);
			if(sIndex == 0)
			{
				int fIndex	= tmpquery.indexOf(fromString);
				String afterFrom   = query.substring(fIndex);
				sBuff.append(selectString+" ");
				sBuff.append(countString);
				sBuff.append("(");
				sBuff.append(distColName);
				sBuff.append(") ");
				sBuff.append(afterFrom+" ");
			}
			countQuery=sBuff.toString();
		}
		logger.info("Returning Count Query String "+countQuery);		
		return countQuery;
	}

	public static String getCountQuery(String query,String distColNames[])
	{
		if(distColNames==null || distColNames.length==0)
		{		
			return getCountQuery(query);
		}else if(distColNames!=null && distColNames.length==1)
		{
			return getCountQuery(query,distColNames[0]);	
		} 
		
		StringBuffer sBuff = new StringBuffer();		
		String countQuery = null;		
		String countCol = "*";		
		String distColString = "";
		if(distColNames!=null)
		{
			distColString = distColNames[0];
			for(int i=1;i<distColNames.length;i++)
			{
				if(distColNames[i]!=null)
					distColString = distColString+","+distColNames[i];		
			}
		}
		logger.info("Count Query String "+query);
		if(query!=null)
		{
			query = query.trim();
			String tmpquery = query.toUpperCase();
			
			int sIndex = tmpquery.indexOf(selectString);
			if(sIndex == 0)
			{
				int fIndex	= tmpquery.indexOf(fromString);
				String afterFrom   = query.substring(fIndex);
				sBuff.append(selectString+" ");
				sBuff.append(countString);
				sBuff.append("(");
				sBuff.append(countCol);
				sBuff.append(") ");
				sBuff.append(" FROM (");
				sBuff.append(selectString+" ");
				sBuff.append(distinctKeyWord+" "+distColString+" ");
				sBuff.append(afterFrom+" ");
				sBuff.append(" ) ");
				if(useAliases)sBuff.append(" AS rs");
				
			}
			countQuery=sBuff.toString();
		}
		logger.info("Returning Count Query String "+countQuery);		
		return countQuery;
	}

	public static String getQueryForPagenation(String query,String sortOrder,String sortOrderCol,int start,int end)
	{
		if(start<0 || end<0)return query;
		if(ConfigurationConstants.DESC_SORT_ORDER.equalsIgnoreCase(sortOrder)){
			sortOrder = "DESC";
		}else{
			sortOrder = "";
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("select * from (select ROWNUMBER() OVER ( ");
		buffer.append(" ORDER BY ").append(sortOrderCol).append(" ").append(sortOrder);
		buffer.append(" )AS ROWNUMBER , M.*  from ( ");
		buffer.append(query);
		buffer.append(" ) as M) as T where ROWNUMBER between )").append(start).append(" and ").append(end);		
		return buffer.toString();
	}
	
/*	
	public static String getQueryForPagenation(String query,String primaryKeyColName,String from,String to,String sortOrder,String sortOrderCol)
	{
		return getQueryForPagenation(query,new String[]{primaryKeyColName},from,to,sortOrder,sortOrderCol);
	}

	public static String getQueryForPagenation(String query,String primaryKeyColNames[],String from,String to,String sortOrder,String sortOrderCol)
	{

		String primaryKeyColName = "";
		if(primaryKeyColNames!=null)
		{
			primaryKeyColName = removeDot(primaryKeyColNames[0]);
			for(int i=1;i<primaryKeyColNames.length;i++)
			{
				if(primaryKeyColNames[i]!=null)
				{
					primaryKeyColName = primaryKeyColName+","+removeDot(primaryKeyColNames[i]);		
				}
			}
		}
		logger.info("Primary Col Name "+primaryKeyColName);
		
		StringBuffer sBuff = null;
		String formedQuery = null;		
		if(from == null)
		{
			from = "0";	
		}
		if(to == null)	
		{
			to = ""+(Integer.parseInt(from)+20);
		}
		
		logger.info("Got Query "+query);
		if(query!=null)
		{
			sBuff = new StringBuffer();
			String tmpquery = query.toUpperCase();
			sBuff.append("SELECT *  FROM ( ");

			if(ConfigurationConstants.DESC_SORT_ORDER.equalsIgnoreCase(sortOrder))			
			{
				sortOrder = "DESC";
			}else
			{
				sortOrder = "";
			}
			String beforeFrom = null;
			String afterFrom =null;

			int fIndex	= tmpquery.indexOf(fromString);
			beforeFrom  = query.substring(0,fIndex);
			afterFrom   = query.substring(fIndex);
			
			beforeFrom = removeAbsoluteColRef(beforeFrom);
			
			sBuff.append(beforeFrom);
			sBuff.append(", dense_rank() OVER (order by "+primaryKeyColName+" ) AS Ranking ");			
			//sBuff.append(", dense_rank() OVER (order by "+primaryKeyColName+" "+sortOrder+") AS Ranking ");
			//sBuff.append(afterFrom);
			
			sBuff.append(fromString);
			sBuff.append("(");
			sBuff.append(query);
			sBuff.append(") AS RS_RESULT ");		
			
			if(sortOrderCol!=null && (sortOrderCol=sortOrderCol.trim()).length()>0)
			{
				sBuff.append("ORDER BY  "+removeDot(sortOrderCol)+" "+sortOrder);		
			}
			
			sBuff.append(") as t where Ranking between "+from+" and "+to);
			formedQuery	= sBuff.toString();		
		}
		logger.info("Returning Query "+formedQuery);
		return formedQuery;
	}
*/

	private static String removeAbsoluteColRef(String colString)
	{
		StringBuffer buffer = new StringBuffer();
		StringTokenizer tokenizer = new StringTokenizer(colString,",",true);
		String token=null;
		while(tokenizer.hasMoreTokens())	 
		{
			token = tokenizer.nextToken();
			if(token.equals(","))
			{
				buffer.append(token);
			}else
			{
				buffer.append(removeDot(token));
			}
		}
			 
		return buffer.toString();
	}
	
	private static String removeDot(String s)
	{
		if(s!=null )
		{
			StringTokenizer subTokenizer = new StringTokenizer(s);					
			StringBuffer buffer = new StringBuffer();
			String st = null;
			while(subTokenizer.hasMoreTokens())
			{
				st = subTokenizer.nextToken();	
				if(st.equalsIgnoreCase("as"))
				{
					return subTokenizer.nextToken();		
				}else
				{
					int dIndex = st.indexOf(".");
					if(dIndex>=0)
					{
						st = st.substring(dIndex+1);
					}
					buffer.append(st+" ");				
				}
			}
			s = buffer.toString();
		}
		return s;
	}
	
	public static void main(String[] args)throws Exception 
	{
		//String query = "SELECT  DISTINCT B.REPORT_ID, B.FOLDER_ID, A.CREATED, A.LAST_UPD,A.LAST_UPD_BY, B.USER_ID, B.STATUS, A.NAME, A.RPT_DESC FROM REPORT_TBL A, RPT_FLDR_MAP_TBL B WHERE A.REPORT_ID=B.REPORT_ID AND B.USER_ID= 'sham'";
		//String query = "SELECT DCSN_SUMM_TBL.DCSN_ID DCSN_ID,TITLE,SCN_ID,SCN_FLOW_ID,VER_ID,DCSN_SUMM_TBL.FOLDER_ID FOLDER_ID,DCSN_SUMM_TBL.DCSN_FLDR_ID DCSN_FLDR_ID,VER_NAME,DCSN_SUMM_TBL.STATUS STATUS,SENDER,DCSN_SUMM_TBL.COMMENTS COMMENTS,DCSN_SUMM_TBL.CREATED CREATED,DCSN_SUMM_TBL.LAST_UPD LAST_UPD,APPROVER_ID,DCSN_APPROVER_TBL.DCSN_FLDR_ID AFOLDER_ID,DCSN_APPROVER_TBL.STATUS ASTATUS, DCSN_APPROVER_TBL.COMMENTS ACOMMENTS,DCSN_APPROVER_TBL.LAST_UPD ALAST_UPD from DCSN_SUMM_TBL,DCSN_APPROVER_TBL where DCSN_SUMM_TBL.DCSN_ID = DCSN_APPROVER_TBL.DCSN_ID AND ((SENDER = 'march18scene' AND DCSN_SUMM_TBL.DCSN_FLDR_ID = 1) OR (APPROVER_ID = 'march18scene' AND DCSN_APPROVER_TBL.DCSN_FLDR_ID = 1) )";
		String query = "select DISTINCT S.DCSN_ID AS DCSN_ID ,S.STATUS AS STATUS,S.TITLE AS TITLE,S.SENDER AS SENDER, S.SENDER_NAME AS SENDER_NAME, S.CREATED AS CREATED, S.LAST_UPD AS LAST_UPD,S.INVN_IMPACT AS INVN_IMPACT, S.COST_IMPACT AS COST_IMPACT, S.MARGIN_IMPACT AS MARGIN_IMPACT, S.REV_IMPACT AS REV_IMPACT, S.DCSN_DESC AS DESCRIPTION,S.DCSN_FLDR_ID AS DCSN_FLDR_ID  from DCSN_SUMM_TBL S, DCSN_APPROVER_TBL A where ((S.SENDER=?  AND S.DCSN_FLDR_ID=?) or (A.APPROVER_ID=? AND A.DCSN_FLDR_ID=?)) AND S.SENDER!=A.APPROVER_ID AND A.DCSN_ID=S.DCSN_ID";
		String primKeyCol[] = {"B.FOLDER_ID","A.CREATED"};//"DCSN_SUMM_TBL.DCSN_ID";
		
		
		Map searchMap = new HashMap();
		searchMap.put(SummaryConstants.SEARCH_COLUMN_NAME,"TITLE");
		searchMap.put(SummaryConstants.SEARCH_COLUMN_TEXT,"DCS");
		searchMap.put(SummaryConstants.SEARCH_COLUMN_DATA_TYPE,"String");
	}
	
	/**
	 * Quote dimension name.
	 * 
	 * @param dimensionName the dimension name
	 * 
	 * @return the string
	 */
	public static String quoteDimName(String dimensionName){
		int index = dimensionName.indexOf(" ");
		if (index > - 1) {
			dimensionName = "\"" + dimensionName + "\"";
			return dimensionName;
		} else {
			return dimensionName;
		}		
	}
	
	public static String appendLikeCondition(String query, String columnName, String value, boolean doAppendWhere) {
		if(value != null && (value = value.trim()).length() > 0) {
			query = appendCondition(query,columnName,true,lCaseFunction,doAppendWhere);
			value = value.toLowerCase();
			value = escapeValue(value);	
			value = "%"+value+"%";
			query = query +" LIKE '"+value+"' "+escapeKeyword +" '"+escapeChar+"' ";
		}
		return query;
	}
}
