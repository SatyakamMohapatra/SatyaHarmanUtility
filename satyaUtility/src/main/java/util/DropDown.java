/**
 * @(#)DropDown.java      04/30/2003
 *
 * Copyright � 2002-2003 SymphonyRPM, Inc. or its subsidiaries. All Rights Reserved.	
 * This software (the "Software") is supplied under a license agreement entered into with 
 * SymphonyRPM,  Inc. 

 * The Software may only be used or disclosed in accordance with the terms of such agreement.
 * The Software is confidential and proprietary to SymphonyRPM, Inc. and 
 * is protected by the terms of such license agreement, copyright law, patent law
 * and other intellectual property law. No part of this Software may be reproduced, transmitted, 
 * or translated in any form or by any means, electronic, mechanical, manual, optical, or otherwise,
 * without the prior written permission of SymphonyRPM, Inc. SymphonyRPM, Inc. reserves all copyrights, 
 * trademarks, patent rights, trade secrets and all other intellectual property rights in the Software.

 * OTHER THAN THE TERMS OF THE LICENSE UNDER WHICH THIS SOFTWARE WAS SUPPLIED, SYMPHONYRPM, INC. 
 * MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, 
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SYMPHONYRPM, INC. 
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, 
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * @version  	:  	$Revision: 1.4 $
 * @author     	:	Sivaraj Palani Kumar
*/

package util;
import java.util.ArrayList;
import java.util.StringTokenizer;
public class DropDown {
	private String dropDownWidth = "153";
	//private String dropDownWidth = "100";
	private String actionEvent = "onClick";
	private String triggerFunction = "body_onClick()";
	private String type = "Menu";
	private String contextPath ="..";

			
	public void setContextPath(String path)	
	{
		if(path!=null)
		{
			contextPath = path;
		}
	}
	
	private String getActions(){
		StringTokenizer strtkAction = new StringTokenizer(actionEvent,",");
		StringTokenizer strtkFunction = new StringTokenizer(triggerFunction,",");
		String tempAction = strtkAction.nextToken();
		String tempFunction = strtkFunction.nextToken();
		String returnString="";
		while(true){
			returnString += tempAction + "=\"";
			returnString += tempFunction + "\"; ";
			if(!strtkAction.hasMoreTokens() || !strtkFunction.hasMoreTokens())
				break;
		}
		return returnString;	
	}
	
	public DropDown(){
	}
	
	public DropDown(int width){
		dropDownWidth = Integer.toString(width);
	}
	
	public DropDown(String width){
		dropDownWidth = width;
	}
	
	public void setType(String type){
		if(type!=null && (type.equalsIgnoreCase("menu") || type.equalsIgnoreCase("list")))
			this.type = type;
	}
	public void setActionEvent(String actionEvent){
		if(actionEvent!=null && actionEvent.length()>1)
			this.actionEvent = actionEvent;
	}
	public void setTriggerFunction(String triggerFunction){
		if(triggerFunction!=null && triggerFunction.length()>1)
			this.triggerFunction = triggerFunction;
	}
	public void setDropDownWidth(String dropDownWidth){
			this.dropDownWidth = dropDownWidth;
}
	
	public String getDropDown(ArrayList dimensionList){
		StringBuffer outStr = new StringBuffer();
		String tableWidth = this.dropDownWidth + "";
//		String tableWidth = this.dropDownWidth + "px";
		outStr.append("<table width=").append(tableWidth).append(" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" id=\"TopTable1\" style=\"table-layout:fixed\" editable=false currentOption=0>");
		outStr.append("\n<tr>\n<td width=\"100%\">");
		outStr.append("\n<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\"selectTable\" >");
		outStr.append("\n<tr>");
		String title = "";
		String content = "&nbsp;";
		String tempKeyValue = "";
		if(dimensionList!=null){
			if(dimensionList.size()>0){
				Object key = dimensionList.get(0);
				if(key != null){
					tempKeyValue = key.toString();
					
					String temp = "";
					while(tempKeyValue != null && tempKeyValue.indexOf("\"")>=0){
								temp = tempKeyValue.substring(0,tempKeyValue.indexOf("\""));
								tempKeyValue = temp + "&quot;" + tempKeyValue.substring(tempKeyValue.indexOf("\"")+1);
					}
				}
				title = tempKeyValue;
				content = tempKeyValue;
			}
		}
		
		outStr.append("\n<td id=\"TopRow\" valign=\"middle\" width=\"100%\" style=\"cursor:default;\" title='").append(title).append("' nowrap  ").append(getActions()).append(" nowrap>");
		outStr.append("<table id=\"InnerTable\" style=\"height:19;table-layout:fixed\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\">");
		outStr.append("<tr id=\"InnerTr\"><td id=\"InnerTd\"  class=\"dropdownselected\" height=\"100%\" valign=\"middle\" width=\"100%\">");
		outStr.append("<label id=\"LBLTopRow\" class=\"truncate\" style=\"width:100%;\"><NOBR id=\"NRBTopRow\">");
		outStr.append(content).append("</NOBR></label></td></tr></table></td>");
		outStr.append("\n<td valign=\"top\">");
		outStr.append("\n<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">");
		outStr.append("\n<tr>");
		outStr.append("\n<td><a ").append(getActions()).append(" onMouseOver=\"this.style.cursor='default'\" id=\"ATopRow\"><img id=\"ImgTopRow\" src=\""+contextPath+"/images/global/dropdown-img.gif\" border=\"0\"></a></td>");
		outStr.append("\n</tr>");
		outStr.append("\n</table>");
		outStr.append("\n</td>");
		outStr.append("\n</tr>");
		outStr.append("\n</table>");
		outStr.append("\n</td>");
		outStr.append("</tr>");
		outStr.append("\n<tr>");
		outStr.append("<td>");
		outStr.append("<div id=\"bottomTable\" class=\"dropdownList\" style='display:none'>");
		outStr.append("<table id=\"bottomTable1\" border=\"0\" cellspacing=\"1\" cellpadding=\"0\" style=\"width:100%;background-color:white;table-layout:fixed\"  onMouseOver=\"this.style.cursor='default'\">");
		outStr.append("\n<col width=\"100%\">");
		
		if(dimensionList!=null){
				if(dimensionList.size()>0){
				  	int a=-1;	
				  	for(a=0; a<dimensionList.size(); a++) {
				  		Object dimension = dimensionList.get(a);
				  		if(dimension != null){
							tempKeyValue = dimension.toString();
							String temp = "";
							while(tempKeyValue != null && tempKeyValue.indexOf("\"")>=0){
								temp = tempKeyValue.substring(0,tempKeyValue.indexOf("\""));
								tempKeyValue = temp + "&quot;" + tempKeyValue.substring(tempKeyValue.indexOf("\"")+1);
							}
							
							outStr.append("\n<tr>").append("<td width=\"100%\" class=\"normal\" onMouseOver=\"selectRow(this)\" onMouseOut=\"undoSelect(this)\" onClick=\"setText(this)\" title=\"").append(tempKeyValue).append("\" nowrap>");
							outStr.append("<label id=\"Options\" class=\"truncate\" style=\"width:100%\">").append(dimension.toString()).append("</label></td>");
							outStr.append("</tr>");
				  		}
					}
				 }else{
				outStr.append("\n<tr>").append("<td width=\"100%\" onMouseOver=\"selectRow(this)\" onMouseOut=\"undoSelect(this)\" class=\"normal\">&nbsp;</td>");
				outStr.append("</tr>");
				}
		}else{
				outStr.append("\n<tr>").append("<td width=\"100%\" onMouseOver=\"selectRow(this)\" onMouseOut=\"undoSelect(this)\" class=\"normal\">&nbsp;</td>");
				outStr.append("</tr>");
		}
		outStr.append("\n</table>");
		outStr.append("\n</div>");
		outStr.append("</td>");
		outStr.append("\n</tr>");
		outStr.append("\n</table>");

		return outStr.toString();
	}
}
