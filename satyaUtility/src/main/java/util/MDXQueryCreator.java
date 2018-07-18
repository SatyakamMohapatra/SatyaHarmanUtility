package util;
/*
 * @(#)MDXQueryCreator.java      02/04/2003
 *
 * Copyright (c)2002-2003 SymphonyRPM, Inc. or its subsidiaries. All Rights Reserved.
 *
 * This software (the "Software") is supplied under a license agreement entered
 * into with SymphonyRPM, Inc. The Software may only be used or disclosed in 
 * accordance with the terms of such agreement. The Software is confidential
 * and proprietary to SymphonyRPM, Inc. and is protected by the terms of such
 * license agreement, copyright law, patent law and other intellectual property
 * law. No part of this Software may be reproduced, transmitted, or translated 
 * in any form or by any means, electronic, mechanical, manual, optical, or 
 * otherwise, without the prior written permission of SymphonyRPM, Inc. 
 * SymphonyRPM, Inc. reserves all copyrights, trademarks, patent rights, trade 
 * secrets and all other intellectual property rights in the Software.
 *
 * OTHER THAN THE TERMS OF THE LICENSE UNDER WHICH THIS SOFTWARE WAS SUPPLIED, 
 * SYMPHONYRPM, INC. MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY 
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR 
 * NON-INFRINGEMENT. SYMPHONYRPM, INC. SHALL NOT BE LIABLE FOR ANY DAMAGES 
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS 
 * SOFTWARE OR ITS DERIVATIVES.
 *
 * @version  	:  	1
 * @author     	:	Akrish
 * @reviewed by 	:	reviewerName
 * @date reviewed :	dateReviewed (mm/dd/yyyy):	
 */

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import com.symphonyrpm.applayer.common.coreservices.AppLinkLogger;
import com.symphonyrpm.applayer.common.coreservices.IModules;
import com.symphonyrpm.applayer.common.coreservices.LogManager;

public class MDXQueryCreator
{
private static AppLinkLogger logger = LogManager.getLogger(IModules.SERVER,HelperUtility.class.getName());	
String mdxRowQuery  = new String();
String mdxColsQuery  = new String();
String mdxFilterQuery  = new String();
String mdxQuery = new String();
String ONROWSFROMQRY = "ON ROWS";
String ONCOLSFROMQRY = "ON COLUMNS";
String SELECTCROSSJOINFROMQRY = "SELECT crossjoin (";
String ONROWSCROSSJOINFROMQRY ="ON ROWS ,crossjoin (";
StringBuffer mdxRowQueryBuff  = new StringBuffer();
StringBuffer mdxColQueryBuff  = new StringBuffer();
StringBuffer mdxFilQueryBuff  = new StringBuffer();
StringBuffer mdxQueryBuff = new StringBuffer();
StringBuffer mdxQueryBuffFinal = new StringBuffer();
StringBuffer mdxRowQueryBuffFinal = new StringBuffer();
StringBuffer mdxColQueryBuffFinal = new StringBuffer();
StringBuffer mdxFilQueryBuffFinal = new StringBuffer();
String aliasMDXQuery = new String();
String constDelimiter = "." ;
String openCrBracket = "(";
String closeCrBracket = ")";
String openSqBracket = "[";
String closeSqBracket = "]";
String singleQuotes = "'";
String SELECT = "SELECT ";
String CROSSJOIN = "crossjoin ";
String opencurlybrace = "{";
String closecurlybrace = "}";
String wordMeasure = "MEASURES";
String quoteswithdotWordMeasure = "[MEASURES].";
String quotesWordMeasure = "[MEASURES]";
String comma = ",";
String vecRowDim = "VECROWDIM";
String vecRowMes = "VECROWMES";
String vecColDim = "VECCOLDIM";
String vecColMes = "VECCOLMES";
String vecFilterTable = "VECFILTER";
String vecFilMes = "VECFILMES";
String vecFilDim = "VECFILDIM";
String finalMDXQuery = "FINALMDXQUERY";
Hashtable hFinal ;
String ONCOLS = "ON AXIS(0)";
String ONROWS = "ON AXIS(1)";
String SELECTCROSSJOIN = "SELECT crossjoin (";
String ONROWSCROSSJOIN ="ON AXIS(0) , crossjoin (";
String WHERE = "WHERE";
Vector vRowDim = new Vector();
Vector vRowMes = new Vector();
Vector vColDim = new Vector();
Vector vColMes = new Vector();
Vector vFilter = new Vector();
Vector vFilDim = new Vector();
Vector vFilMes = new Vector();
String crossjoin = "crossjoin";
String measures = new String("[Measures]");
String CHILDREN = "CHILDREN";
String MeasuresForFormatMask = "Measures:";


//Constructor
public MDXQueryCreator()
{
	hFinal = new Hashtable();
}

/**
	 * Method called by the calling class. This is the method to create a MDX query . This conforms to the WLMDX
	 * standards, and not MS MDX standards . It takes in hashtable and string modelname
	 *  returns a Hashtable that contains  vectors used for storing it in DB ( for display) and string that is used to fire MDX query into DV GRID. This is mainly used for 
	 * loading scenario and reports.More of an overlaoded method
	 * @throws	Exception
	 * @param Hashtable , String
 */

public Hashtable createMDXqueryfromHashtable(Hashtable mdxHash ,String modelName) throws Exception
{
	Hashtable hMdx = null;
	try
	{
		
		logger.debug(" The contents receivedfrom the calling method "+ mdxHash.toString() + " the modelname being " + modelName);
		Vector vRows = new Vector();
		Vector vCols = new Vector();
		Vector vFils = new Vector();
		Vector vROWDIM = (Vector)mdxHash.get(vecRowDim);
		Vector vROWMES = (Vector)mdxHash.get(vecRowMes);
		Vector vCOLDIM = (Vector)mdxHash.get(vecColDim);
		Vector vCOLMES = (Vector)mdxHash.get(vecColMes);
		Vector vFILMES = (Vector)mdxHash.get(vecFilMes);
		Vector vFILDIM = (Vector)mdxHash.get(vecFilDim);
		if(vROWDIM != null)
		{
			if(vROWDIM.size()!=0)
			{
				vRows.addAll(vROWDIM);
			}
		}
		
		if(vROWMES != null)
		{
			if(vROWMES.size()!=0)
			{
				vRows.addAll(vROWMES);
			}
		}
		
		if(vCOLDIM != null)
		{
			if(vCOLDIM.size()!=0)
			{
				vCols.addAll(vCOLDIM);
			}
		}
		
		if(vCOLMES != null)
		{
			if(vCOLMES.size()!=0)
			{
				vCols.addAll(vCOLMES);
			}
		}
		
		if(vFILMES != null)
		{
			if(vFILMES.size()!=0)
			{
				vFils.addAll(vFILMES);
			}
		}
		if(vFILDIM != null)
		{
			if(vFILDIM.size()!=0)
			{
				vFils.addAll(vFILDIM);
			}
		}
	logger.debug( "The contents of the vector before being passed onto the method of create Qry" + " ROWS Vector :" + vRows.toString () + " COLUMN Vector " + vCols.toString() + " FILTER VECTOR " + vFils.toString());
	hMdx = createMDXquery(vRows,vCols, vFils,modelName);
	logger.debug( "The contents of the hashtable " + hMdx.toString());
		
	}
	catch( Exception ex)
	{
		logger.fatal( " Exception in this block , really critical " + ex );
		throw ex; 		
	}
	
	return hMdx;
}
/**
	 * Method called by the calling class. This is the method to create a MDX query . This conforms to the WLMDX
	 * standards, and not MS MDX standards . It takes in vectors of row,columns and filters ( basically tuple obtained through ODBO or from applet (dimension tree) 
	 * and the data source and returns a Hashtable that contains 
	 * vectors used for storing it in DB ( for display) and string that is used to fire MDX query into DV GRID. This is mainly used for 
	 * loading scenario and reports.
	 * @throws	Exception
	 * @param (Vector rows,Vector columns,Vector filter,String)
 */

public Hashtable createMDXquery( Vector vRows,Vector vColumns,Vector vFilters,String dataSrcName) throws Exception
{
try
{
  if( vRows.size() != 0) 
		{
		logger.info(" The Row " + vRows.toString() +" columns " + vColumns.toString() + " filter " + vFilters.toString() + "datasource name " + dataSrcName);
		StringTokenizer stkzMdxRowsQuery ;
		String initialRowAlias = new String();
		Vector vTempCompRow = new Vector();
		Vector vRowDim = new Vector();
		Vector vRowMes = new Vector();
		
		//dumps evrything into a vector and then sorts and group all the relevant dimensions together, generic algo 
		//irrespective of the dimensions and measures
				  for (Enumeration erows = vRows.elements() ; erows.hasMoreElements() ;) 
					{
						String tempMdxRowQuery = (String)erows.nextElement();
							stkzMdxRowsQuery = new StringTokenizer(tempMdxRowQuery,constDelimiter);
							String stkzAliasRowsValue = stkzMdxRowsQuery.nextToken(); 
							// This would take care of grouping measures
							if(stkzMdxRowsQuery.countTokens()>2)
							{
								vTempCompRow.add(stkzAliasRowsValue);
							}
							else
							{
								vTempCompRow.add(quotesWordMeasure);
							}
					}
					//logger.info(" The Tempvector for sorting and grouping  " + vTempCompRow.toString());
						int j = 0;
						Object vecRowElement = new Object();
						String prevElement = new String();
						int tempCrossJoinCheck = 0;
						
						for ( int i = j ;i < vTempCompRow.size(); i++)
							{
										int mdxoccurance =0;
										if( i != 0)
										{
											prevElement = (String)vecRowElement;
										}
										vecRowElement = vTempCompRow.elementAt(i);
										Object vecRowMesDim = vRows.elementAt(i);
										if(( tempCrossJoinCheck == 0) && (i!=0))
										{
											if( prevElement.compareTo(vecRowElement.toString())!= 0)
											tempCrossJoinCheck++;
										}
										mdxRowQueryBuff.append(vecRowMesDim.toString());
										mdxoccurance = vTempCompRow.indexOf(vecRowElement,++i);
										int compValRow = quotesWordMeasure.compareTo(vecRowElement.toString());
										if(compValRow != 0)
										{
											vRowDim.add(vecRowMesDim);
										}
										else
										{
											vRowMes.add(vecRowMesDim);
										}
										if ( mdxoccurance > 0)
										{
													//logger.info( " printing the occurance for  " + mdxoccurance + " for the element in Row" + vTempCompRow.elementAt(i) );
													Object tempRowObject = vRows.elementAt(mdxoccurance);
													Object tempRowVecObject = vTempCompRow.elementAt(mdxoccurance);
													vRows.removeElementAt(mdxoccurance);
													vRows.insertElementAt(tempRowObject,i);
													vTempCompRow.removeElementAt(mdxoccurance);
													vTempCompRow.insertElementAt(tempRowVecObject,i);
													mdxRowQueryBuff.append(comma) ;
													
											} //end of if loop	
											else
											{
												tempCrossJoinCheck++;
												if(tempCrossJoinCheck !=1)
												{
													mdxRowQueryBuff.append(closecurlybrace +closeCrBracket+ comma + opencurlybrace);
												}
												else
												{
													mdxRowQueryBuff.append(closecurlybrace + comma + opencurlybrace);
												}
												
											}
																						
										i=j;j++; //do not remove this line
							}// end of for loop
							
							if ( tempCrossJoinCheck != 0)
							{
							//logger.info(" the tempcrossjoin chekc " + tempCrossJoinCheck);
								mdxRowQueryBuffFinal.append(SELECT) ;
								for ( int i = 0 ; i<tempCrossJoinCheck-1;++i)
								{
									mdxRowQueryBuffFinal.append(CROSSJOIN + openCrBracket + " ") ;
								}
								mdxRowQueryBuffFinal.append(opencurlybrace) ;
								mdxRowQueryBuffFinal.append(mdxRowQueryBuff.toString());
								mdxRowQueryBuffFinal = mdxRowQueryBuffFinal.delete(mdxRowQueryBuffFinal.length()-2,mdxRowQueryBuffFinal.length());
								//mdxRowQueryBuffFinal.append(closeCrBracket);
								mdxRowQueryBuffFinal.append(" ON ROWS ");
								mdxRowQueryBuffFinal.append(comma);
							}
							else
							{
								mdxRowQueryBuffFinal.append(SELECT + opencurlybrace) ;
								mdxRowQueryBuffFinal.append(mdxRowQueryBuff.toString());
								mdxRowQueryBuffFinal = mdxRowQueryBuffFinal.delete(mdxRowQueryBuffFinal.length()-2,mdxRowQueryBuffFinal.length());
								mdxRowQueryBuffFinal.append(" ON ROWS ");
								mdxRowQueryBuffFinal.append(comma);
							}
							hFinal.put(vecRowDim,vRowDim);
							hFinal.put(vecRowMes,vRowMes);
					//logger.info( " The reorganized row vector " + vRows.toString());
					//logger.fatal(" THe Vector for Row and Dimension combination is " + vRowDim.toString() );
					//logger.fatal(" THe Vector for Row and Measures combination is " + vRowMes.toString() );
					logger.debug(" The row MDX query being \n" + mdxRowQueryBuffFinal.toString());
					
			}// end of main if
		
	if( vColumns.size() != 0) 
		{
		StringTokenizer stkzMdxColsQuery ;
		String initialColAlias = new String();
		Vector vTempCompCol = new Vector();
		Vector vColDim = new Vector();
		Vector vColMes = new Vector();
		
		//dumps evrything into a vector and then sorts and group all the relevant dimensions together, generic algo 
		//irrespective of the dimensions and measures
				  for (Enumeration ecols = vColumns.elements() ; ecols.hasMoreElements() ;) 
					{
						String tempMdxColQuery = (String)ecols.nextElement();
							stkzMdxColsQuery = new StringTokenizer(tempMdxColQuery,constDelimiter);
							String stkzAliasColsValue = stkzMdxColsQuery.nextToken(); 
							// This would take care of grouping measures
							if(stkzMdxColsQuery.countTokens()>2)
							{
								vTempCompCol.add(stkzAliasColsValue);
							}
							else
							{
								vTempCompCol.add(quotesWordMeasure);
							}
					}
					//logger.info(" The Tempvector for sorting and grouping  " + vTempCompCol.toString());
						int j = 0;
						Object vecColElement = new Object();
						String prevElement = new String();
						int tempColCrossJoinCheck = 0;
						
						for ( int i = j ;i < vTempCompCol.size(); i++)
							{
										int mdxoccurance =0;
										if( i != 0)
										{
											prevElement = (String)vecColElement;
										}
										vecColElement = vTempCompCol.elementAt(i);
										Object vecColMesDim = vColumns.elementAt(i);
										
										
											if(( tempColCrossJoinCheck == 0) && ( i !=0))
											{
												if( prevElement.compareTo(vecColElement.toString())!= 0)
												tempColCrossJoinCheck++;
											}
																				
										mdxColQueryBuff.append(vecColMesDim.toString());
																	
										mdxoccurance = vTempCompCol.indexOf(vecColElement,++i);
										int compValCol = quotesWordMeasure.compareTo(vecColElement.toString());
										if(compValCol != 0)
										{
											vColDim.add(vecColMesDim);
										}
										else
										{
											vColMes.add(vecColMesDim);
										}
										if ( mdxoccurance > 0)
										{
													//logger.info( " printing the occurance for  " + mdxoccurance + " for the element in Columns" + vTempCompCol.elementAt(i) );
													Object tempColObject = vColumns.elementAt(mdxoccurance);
													Object tempColVecObject = vTempCompCol.elementAt(mdxoccurance);
													vColumns.removeElementAt(mdxoccurance);
													vColumns.insertElementAt(tempColObject,i);
													vTempCompCol.removeElementAt(mdxoccurance);
													vTempCompCol.insertElementAt(tempColVecObject,i);
													mdxColQueryBuff.append(comma) ;
													
											} //end of if loop	
											else
											{
												tempColCrossJoinCheck++;
												if(tempColCrossJoinCheck !=1)
												{
													mdxColQueryBuff.append(closecurlybrace +closeCrBracket+ comma + opencurlybrace);
												}
												else
												{
													mdxColQueryBuff.append(closecurlybrace + comma + opencurlybrace);
												}
												
											}
																						
										
										i=j;j++; //do not remove this line
							}// end of for loop
							
							if ( tempColCrossJoinCheck != 0)
							{
							//logger.info(" the tempcolcross" + tempColCrossJoinCheck);
								for ( int i = 0 ; i<tempColCrossJoinCheck-1;++i)
								{
									mdxRowQueryBuffFinal.append(CROSSJOIN + openCrBracket + " ") ;
								}
								mdxColQueryBuffFinal.append(opencurlybrace) ;
								mdxColQueryBuffFinal.append(mdxColQueryBuff.toString()) ;
								mdxColQueryBuffFinal = mdxColQueryBuffFinal.delete(mdxColQueryBuffFinal.length()-2,mdxColQueryBuffFinal.length());
								//mdxColQueryBuffFinal.append(closeCrBracket);
								mdxColQueryBuffFinal.append(" ON COLUMNS ");
								mdxColQueryBuffFinal.append( "FROM " + dataSrcName);
							}
							else
							{
								mdxColQueryBuffFinal.append( opencurlybrace) ;
								mdxColQueryBuffFinal.append(mdxColQueryBuff.toString());
								//logger.info("mdxcolqry" + mdxColQueryBuff.toString());
								mdxColQueryBuffFinal = mdxColQueryBuffFinal.delete(mdxColQueryBuffFinal.length()-2,mdxColQueryBuffFinal.length());
								mdxColQueryBuffFinal.append(" ON COLUMNS ");
								mdxColQueryBuffFinal.append( "FROM " + dataSrcName);
							}
							hFinal.put(vecColDim,vColDim);
							hFinal.put(vecColMes,vColMes);
					//logger.info( " The reorganized Column vector " + vColumns.toString());
					//logger.info(" THe Vector for Column and Dimension combination is " + vColDim.toString() );
					//logger.info(" THe Vector for Column and Measures combination is " + vColMes.toString() );
					logger.info(" The Column MDX query being \n" + mdxColQueryBuffFinal.toString());
					
			}// end of column if 
			
		if( vFilters.size() != 0)
		{
		
		Vector vFilterTable = new Vector();
		Vector vTempFilCol = new Vector();
		
		StringTokenizer stkzMdxFilsQuery ;
			   mdxFilQueryBuffFinal.append(" WHERE ("); 
				for (Enumeration efilter = vFilters.elements() ; efilter.hasMoreElements() ;) 
				{
						String efiltervalue = (String)efilter.nextElement();
						stkzMdxFilsQuery = new StringTokenizer(efiltervalue,constDelimiter);
						String stkzAliasFilsValue = stkzMdxFilsQuery.nextToken(); 
							// This would take care of grouping measures
							if(stkzMdxFilsQuery.countTokens()>2)
							{
								vTempFilCol.add(stkzAliasFilsValue);
							}
							else
							{
								vTempFilCol.add(quotesWordMeasure);
							}
				
						vFilterTable.add(efiltervalue);
						mdxFilQueryBuffFinal.append(efiltervalue);
						if(efilter.hasMoreElements()== true)
						{
							mdxFilQueryBuffFinal.append(",");
						}
				}// end of for

				// added to split the filter into measures and dimensions
						int j = 0;
						Object vecFilElement = new Object();
						for ( int i = j ;i < vTempFilCol.size(); i++)
						{
							vecFilElement = vTempFilCol.elementAt(i);
							Object vecFilMesDim = vFilters.elementAt(i);
							int compValFil = quotesWordMeasure.compareTo(vecFilElement.toString());
										if(compValFil != 0)
										{
											vFilDim.add(vecFilMesDim);
										}
										else
										{
											vFilMes.add(vecFilMesDim);
										}
						}
				// end of change		    	
				mdxFilQueryBuffFinal.append(")");
				hFinal.put(vecFilterTable,vFilterTable);
				hFinal.put(vecFilMes,vFilMes);
				hFinal.put(vecFilDim,vFilDim);
				logger.info( " After filter \n"+ mdxFilQueryBuffFinal);
			}// end of filter if 

				mdxQueryBuffFinal.append(mdxRowQueryBuffFinal.toString());
				mdxQueryBuffFinal.append(mdxColQueryBuffFinal.toString());
				mdxQueryBuffFinal.append(mdxFilQueryBuffFinal.toString());
				
				hFinal.put(finalMDXQuery,mdxQueryBuffFinal);
				logger.debug( "Final MDX QUERY \n" + mdxQueryBuffFinal.toString() );
			
	} //end of try
			
	catch ( Exception e)
	{
		logger.fatal( " Exception in this block , really critical " + e );
		throw e; 
	}

return hFinal;
}

/**
	 * This method is called by the calling class typically by some one who receives a MDX query from 
	 * ABX . This is then stored into the Database, which can be used for retrieval and display in the summary grid.
	 * 
	 * @throws	Exception
	 * param String
*/
public Hashtable splitMDXonSave(String mdx) throws Exception
{
	
	try
	{
	logger.debug("The MDX query obtained from ABX " + mdx);	
	int rowscnt = mdx.indexOf(ONROWS);
	int colscnt = mdx.indexOf(ONCOLS);
	String colString = new String();
	String rowString = new String();
	

	//logger.info(" The values are " + colString +"\n ::: \n" + rowString);
	if(colscnt != -1)
	{
		colString = mdx.substring(0,colscnt-2).trim();
	
	
	int splitcolCnt = 0;
 	
		splitcolCnt = colString.indexOf(SELECTCROSSJOIN);
		if( splitcolCnt == -1)
		{
			colString = colString.substring(8,colString.length()).trim();
		}
		else
		{
			colString = colString.substring(7,colString.length()).trim();
			//if(colString.indexOf(crossjoin) != -1)
			do
				{
					colString = colString.substring(12,colString.length()).trim();
				}
			while(colString.indexOf(crossjoin) != -1);
		}
		//logger.info(" \n The splitcntcol" +colString );
		tokenData(colString,"C");
	}
	if(rowscnt != -1)
	{
		rowString = mdx.substring(colscnt,rowscnt).trim();
	
		int splitrowCnt = 0;
		splitrowCnt = rowString.indexOf(ONROWSCROSSJOIN);
		if( splitrowCnt == -1)
		{
			rowString = rowString.substring(13,rowString.length()-1).trim();
		}
		else
		{
			rowString = rowString.substring(13,rowString.length()).trim();
				//if(rowString.indexOf(crossjoin) != -1)
				do
				{
						rowString = rowString.substring(12,rowString.length()).trim();
				}
				while(rowString.indexOf(crossjoin) != -1);
		}
	
		tokenData(rowString,"R");
	}
		String filString = null;
		int filscnt = mdx.indexOf(WHERE);
		//logger.info(" the fliter cnt is " + filscnt);
		if(filscnt!= -1)
		{
			filString = mdx.substring(filscnt+6,mdx.length()).trim();
			tokenData(filString,"F");
		}
	
		//logger.info(" \n ***The splitcntrow***" +rowString );
		//logger.info(" \n ---The splitcntcol---" +colString );
		//logger.info(" \n +++The splitcntfil+++" +filString );
	
		if( vRowDim.size()!=0)
		{
			hFinal.put(vecRowDim,vRowDim);
		}
		if(vRowMes.size()!=0)
		{
			hFinal.put(vecRowMes,vRowMes);
		}
		if(vColDim.size()!=0)
		{	
			hFinal.put(vecColDim,vColDim);
		}
		if(vColMes.size()!=0)
		{
			hFinal.put(vecColMes,vColMes);
		}
		if(vFilMes.size()!=0)
		{
			//hFinal.put(vecFilterTable,vFilter);
			hFinal.put(vecFilMes,vFilMes);
		}
		if(vFilDim.size()!=0)
		{
			hFinal.put(vecFilDim,vFilDim);	
		}
		
		logger.debug(" \n The hashtble being" +hFinal.toString() );
	
	}
	
	catch ( Exception e)
	{
		logger.fatal("Exception in splitsave to MDX esp during savesAS " + e);
		throw e;
	}

return hFinal;

}
/**
	 * Method called by the splitSave fromMDX , should not be used from outside. It does the necessary formating for display , but used for storing it into DB.
	 * The parameters are MDX sub type and also whether its row , column or filter
	 * 
	 * @throws	Exception
	 * @ param String ,String
	 */
private void tokenData( String subMDX,String type) throws Exception
{

try
{
	StringTokenizer tokenData = new StringTokenizer(subMDX,",");
	boolean flag_Child = false;
  while(tokenData.hasMoreTokens())
	{
	  String tempTokenData = tokenData.nextToken().trim();
		
		tempTokenData = tempTokenData.replace('}',' ');
		tempTokenData = tempTokenData.replace('{',' ');
		tempTokenData = tempTokenData.replace(')',' ');
		tempTokenData = tempTokenData.replace('(',' ');
		tempTokenData.trim();
		logger.info(" \n The tempTokenData " +tempTokenData);
		
				int childCnt = tempTokenData.indexOf(".Children");
				int mesCnt = tempTokenData.indexOf(measures);
				if ((childCnt != -1) && (mesCnt != -1))
				{
					flag_Child = true;
					tempTokenData = tempTokenData.substring(0,childCnt).trim();
					logger.info(" \n The tempTokenData after substring in case of children" +tempTokenData);
				}
		StringTokenizer tempTData = new StringTokenizer(tempTokenData,".");	

		if( type.equals("R")==true)
		{
		//logger.info(" the tempTData in rows \n" + tempTData.nextElement());
		String tmpDimData =(String)tempTData.nextElement();
			if(tmpDimData.trim().equals(measures))
			{
					do
					{
						tempTokenData = (String)tempTData.nextToken();
				}
					while (tempTData.hasMoreTokens()) ;
					
					if(flag_Child == true)
					{
						vRowMes.addElement(tempTokenData +".children");
					}
					else
					{
						vRowMes.addElement(tempTokenData );
					}
			}
			else
			{
				vRowDim.addElement(tempTokenData);				
			}
		}
		else if(type.equals("C")==true)
		{
		//logger.info(" the tempTData in columns \n" + tempTData.nextElement());
		String tmpColData =(String)tempTData.nextElement();
			if(tmpColData.trim().equals(measures))
			{
				do
				{
					tempTokenData = (String)tempTData.nextToken();
			}
				while (tempTData.hasMoreTokens()) ;
				if(flag_Child == true)
				{
					vColMes.addElement(tempTokenData+".children");
				}
				else
				{
					vColMes.addElement(tempTokenData);
				}
				
			}
			else
			{
					vColDim.addElement(tempTokenData);
			}
		
		}
		
		else if(type.equals("F")==true)
		{
			//vFilter.addElement(tempTokenData);
			String tmpFilData = (String)tempTData.nextElement();
			if(tmpFilData.trim().equals(measures))
			{
				do
				{
					tempTokenData = (String)tempTData.nextToken();
				}
				while (tempTData.hasMoreTokens()) ;
				vFilMes.addElement(tempTokenData.trim());
			}
			else
			{
				vFilDim.addElement(tempTokenData);
			}
		
		}
		 
	}//end of while
	//logger.info(" \n The vector for rowDIm " +vRowDim.toString());
	//logger.info(" \n The vector for rowMes " +vRowMes.toString());
	//logger.info(" \n The vector for ColDIm " +vColDim.toString());
	//logger.info(" \n The vector for ColMes " +vColMes.toString());
	//logger.info(" \n The vector for Filter " +vFilter.toString());
}
catch( Exception te)
	{
		logger.fatal ( "fatal exception in token data " + te);
		throw te;
	} 
}	
/**
	 * Method used for storing the data into the database , This method is rarely used and takes in 2 hashtables, sort of deprecated
	 * 
	 * @throws	Exception
	 * 
	 */
private void insertAliasintoDB(Hashtable hdimAlias, Hashtable hmesAlias) throws Exception
{
Hashtable userMesAlias = new Hashtable();
Hashtable userDimAlias = new Hashtable();
try
{
		for (Enumeration eDimAlias = hdimAlias.keys() ; eDimAlias.hasMoreElements() ;) 
		{
			String aliasDimKey = (String) eDimAlias.nextElement();
			String aliasDimValue =(String) hdimAlias.get(aliasDimKey);
			StringTokenizer stkzDimAliasKey = new StringTokenizer(aliasDimKey,constDelimiter);
			String stkzDimAliasValue = stkzDimAliasKey.nextToken() + constDelimiter + openSqBracket + aliasDimValue + closeSqBracket;
			aliasDimKey = singleQuotes + aliasDimKey + singleQuotes;
			logger.info(" The alias key Dimension being \n" + aliasDimKey + " \n and the value being " + stkzDimAliasValue + "\n");
			userDimAlias.put(aliasDimKey,stkzDimAliasValue);
		}
		
		for (Enumeration eMesAlias = hmesAlias.keys() ; eMesAlias.hasMoreElements() ;) 
		{
			String aliasMesKey = (String) eMesAlias.nextElement();
			String aliasMesValue =(String) hmesAlias.get(aliasMesKey);
			StringTokenizer stkzMesAliasKey = new StringTokenizer(aliasMesKey,constDelimiter);
			String stkzMesAliasValue = openSqBracket + wordMeasure + closeSqBracket + constDelimiter + stkzMesAliasKey.nextToken() ;
			aliasMesKey = singleQuotes + aliasMesKey + singleQuotes;
			logger.info(" The alias key Measure being \n" + aliasMesKey + " \n and the value being " + stkzMesAliasValue + "\n");
			userMesAlias.put(aliasMesKey,stkzMesAliasValue);
		}

}
catch ( Exception ioe)
{
	logger.fatal(" fatal error in instertAlias in DB " + ioe);
	throw ioe;
}

}

/**
	 * Method to display in report summary . The display format should contain only the dimension name .member name  for dimensions
	 * and for measures it is just the measure member name. It takes in an array list and return the same .
	 * 
	 * @throws	Exception
	 * @param ArrayList
	 */
public ArrayList displayFrontEndMDXTuple (ArrayList dimmes) throws Exception
{
	ArrayList newDimmes = new ArrayList();
	boolean flagChild = true;
	String tDimMes = new String();
	try
	{
	 //logger.fatal(" The values passed in the arry list are " + dimmes.toString());
	for ( int cnt = 0;cnt < dimmes.size();cnt++)
	{
			  String tempMDX = (String)dimmes.get(cnt);
				  tempMDX=tempMDX.replace('[',' ');
				tempMDX=tempMDX.replace('"',' ');
				tempMDX=tempMDX.replace(']',' ');
				int childCnt = tempMDX.indexOf(CHILDREN);
				if(childCnt != -1)
				{
					flagChild = false;
					tempMDX = tempMDX.substring(0,childCnt).trim();
					//logger.debug("Flag child=="+flagChild);
				}
			  StringTokenizer tokenMDX = new StringTokenizer(tempMDX,".");
				StringBuffer finDispMDX = new StringBuffer();
				String tempDimMes = new String();
				tDimMes = (String)tokenMDX.nextElement();
				StringBuffer tempDimDisp = new StringBuffer(tDimMes);
				if((tokenMDX.countTokens()>2)&&!(tDimMes.equals(measures)))
				{
						do
						{
							tempDimMes = (String)tokenMDX.nextToken();
						}	
						while (tokenMDX.hasMoreTokens()) ;
					finDispMDX.append(tempDimDisp.toString());
					finDispMDX.append("."+tempDimMes.toString());
					if(flagChild == false)
						{
							finDispMDX.append(".");
							finDispMDX.append(CHILDREN);
							//logger.debug("APPEND>>>>:"+finDispMDX);
						}
					newDimmes.add(finDispMDX.toString());
				}//end of if for not equal measures
				else if((tokenMDX.countTokens()>2)&&(tDimMes.equals(measures)))
				{
					//logger.debug(">>>Parsing a measure");
						do
						{
							tempDimMes = (String)tokenMDX.nextToken();
						}	
						while (tokenMDX.hasMoreTokens()) ;
					finDispMDX.append(tempDimMes.toString());
					if(flagChild == false)
						{
							finDispMDX.append(".");
							finDispMDX.append(CHILDREN);
							//logger.debug("MESH APPEND>>>:"+finDispMDX);
						}
					newDimmes.add(finDispMDX.toString());
				}// end of else if for MEasures
				else 
				{
					//logger.debug(">>>Others");
					//finDispMDX.append("Measures . ");
					finDispMDX.append(tempMDX);
					if(flagChild == false)
						{
							finDispMDX.append(CHILDREN);
							//logger.debug("MESH APPEND 2:"+finDispMDX);
						}
					newDimmes.add(finDispMDX.toString());
				}
			//logger.debug("the newDimmes \n" + newDimmes.toString());
			flagChild = true;
		}//end of for 
	}// end of try
	
	catch( Exception ee)
	{
		  logger.fatal(" Problem in display of front end tuple " + ee);
		  throw ee;
	}
		return newDimmes;
}
/**
	 * This method is called by the calling class typically by some one who receives a MDX query from 
	 * the querycreator method (called scenarios) . This is then stored into the Database, which can be used for retrieval and display in the summary grid.
	 * 
	 * @throws	Exception
	 * param String
*/
public void splitMDXonSavefromQryCreator(String mdx) throws Exception
{
	
	try
	{

	logger.debug (" The mdx qry received " + mdx);	
	int rowscnt = mdx.indexOf(ONROWSFROMQRY);
	int colscnt = mdx.indexOf(ONCOLSFROMQRY);
	logger.debug(" the value of rowscnt " + rowscnt +"column cnt" + colscnt);
	String colString = new String();
	String rowString = new String();
	if(rowscnt!= -1)
	{
	rowString = mdx.substring(0,rowscnt-2).trim();
	}
	if(colscnt!= -1)
	{
	colString = mdx.substring(rowscnt,colscnt).trim();
	}
	
	logger.debug(" The values are " + colString +"\n ::: \n" + rowString);
	int splitrowCnt = 0;
	if(rowscnt!=-1)
		{
		splitrowCnt = rowString.indexOf(SELECTCROSSJOINFROMQRY);
		if( splitrowCnt == -1)
		{
				rowString = rowString.substring(8,rowString.length()).trim();
		}
		else
		{
				rowString = rowString.substring(7,rowString.length()).trim();
				do
				{
					rowString = rowString.substring(12,rowString.length()).trim();
				}
				while(rowString.indexOf(crossjoin) != -1);
		}
		tokenDatafromQryCreator(rowString,"R");
	}
		int splitcolCnt = 0;
		if(colscnt!= -1)
		{
		splitcolCnt = colString.indexOf(ONROWSCROSSJOINFROMQRY);
		if( splitcolCnt == -1)
		{
				colString = colString.substring(9,colString.length()-1).trim();
		}
		else
		{
			colString = colString.substring(9,colString.length()).trim();
				do
				{
						colString = colString.substring(12,colString.length()).trim();
				}
				while(colString.indexOf(crossjoin) != -1);
		}
		tokenDatafromQryCreator(colString,"C");
		}
		String filString = null;
		
		int filscnt = mdx.indexOf(WHERE);
		if(filscnt!= -1)
		{
			filString = mdx.substring(filscnt+6,mdx.length()).trim();
			tokenDatafromQryCreator(filString,"F");
		}
	
		logger.debug(" \n ***The splitcntrow***" +rowString );
		logger.debug(" \n ---The splitcntcol---" +colString );
		logger.debug(" \n +++The splitcntfil+++" +filString );
		if( vRowDim.size()!= 0)
		{
			hFinal.put(vecRowDim,vRowDim);
		}
		if(vRowMes.size()!=0)
		{
			hFinal.put(vecRowMes,vRowMes);
		}
		if(vColDim.size()!=0)
		{	
			hFinal.put(vecColDim,vColDim);
		}
		if(vColMes.size()!=0)
		{
			hFinal.put(vecColMes,vColMes);
		}
		if(vFilMes.size()!=0)
		{
			hFinal.put(vecFilMes,vFilMes);
		}
		if(vFilDim.size()!=0)
		{
			hFinal.put(vecFilDim,vFilDim);	
		}
		
		logger.debug(" \n The hashtble being" +hFinal.toString() );
	
	}
	catch ( Exception e)
	{
				logger.fatal("Criticalexception " + e);
				throw e;
	}


}

/**
	 * Method called by the splitSave fromMDX , should not be used from outside. It does the necessary formating for display , but used for storing it into DB.
	 * The parameters are MDX sub type and also whether its row , column or filter, used mainly for scenario module
	 * 
	 * @throws	Exception
	 * @ param String ,String
	 */

private void tokenDatafromQryCreator( String subMDX,String type) throws Exception
{

try
{
	StringTokenizer tokenData = new StringTokenizer(subMDX,",");
	boolean _flagchild = false;
  while(tokenData.hasMoreTokens())
	{
	  String tempTokenData = tokenData.nextToken().trim();
		tempTokenData = tempTokenData.replace('}',' ');
		tempTokenData = tempTokenData.replace('{',' ');
		tempTokenData = tempTokenData.replace(')',' ');
		tempTokenData = tempTokenData.replace('(',' ');
		logger.debug(" The tempTokenData " +tempTokenData);
		tempTokenData.trim();
		StringTokenizer tempTData = null;
		int childCnt = tempTokenData.indexOf(".Children");
		int mesCnt = tempTokenData.indexOf(measures);
				if((childCnt != -1) && ( mesCnt != -1))
				{
					_flagchild = true;
					tempTData = new StringTokenizer(tempTokenData,".");	
				}

		if( type.equals("R")==true)
		{
		int cntRowTok = tempTData.countTokens();
		String tempch2 =(String)tempTData.nextElement();
			if(tempch2.trim().equals(measures) || cntRowTok <=2)
			{
					if(_flagchild == true)
					{
						vRowMes.addElement(tempTokenData.trim()+".children");
					}
					else
					{
						vRowMes.addElement(tempTokenData.trim());
					}
			}
			else
			{
				vRowDim.addElement(tempTokenData.trim());				
			}
		}
		else if(type.equals("C")==true)
		{
		int cntColTok = tempTData.countTokens();
		String tempch1 =(String)tempTData.nextElement();
			if(tempch1.trim().equals(measures)|| cntColTok<=2)
			{
				if(_flagchild == true)
				{
					vColMes.addElement(tempTokenData.trim()+".children");
				}
				else
				{
					vColMes.addElement(tempTokenData.trim());
				}
			}
			else
			{
				vColDim.addElement(tempTokenData.trim());				
			}
			
		}// end of main else if 
		else if(type.equals("F")==true)
		{
			//vFilter.addElement(tempTokenData);
			int cntFilTok = tempTData.countTokens();
			String tempch =(String)tempTData.nextElement();
			if(tempch.trim().equals(measures) || cntFilTok <=2)
			{
				vFilMes.addElement(tempTokenData.trim());
			}
			else
			{
				vFilDim.addElement(tempTokenData.trim());
			}
		}
		
				 
	}//end of while
	logger.debug(" \n The vector for rowDIm " +vRowDim.toString());
	logger.debug(" \n The vector for rowMes " +vRowMes.toString());
	logger.debug(" \n The vector for ColDIm " +vColDim.toString());
	logger.debug(" \n The vector for ColMes " +vColMes.toString());
	logger.debug(" \n The vector for FilDim " +vFilMes.toString());
	logger.debug(" \n The vector for FilMes " +vFilDim.toString());
}//end of try
catch( Exception te)
	{
		logger.fatal("The exception being" + te);	
		throw te;
	} 
}	//end of method 

/**
	 * Method called by the reports/scenario to store format mask only for measures 
	 * 
	 * @throws	Exception
	 * @ param ArrayList
	 */


public ArrayList formatMaskForMeasures( ArrayList allArrayList) throws Exception
{
ArrayList mesArrayList = new ArrayList();
	try
	{
		for (int iCnt = 0 ; iCnt < allArrayList.size();iCnt++)
		{
				String tempStringFromArrayList = (String)allArrayList.get(iCnt);
				int isMes = tempStringFromArrayList.indexOf(MeasuresForFormatMask);
				if( isMes != -1)
				{
					mesArrayList.add(tempStringFromArrayList);
				}
				
		}
	}
	catch ( Exception e)
	{
		logger.debug(" The exception is quite crtical " + e);
	}

return mesArrayList;
}


}		

