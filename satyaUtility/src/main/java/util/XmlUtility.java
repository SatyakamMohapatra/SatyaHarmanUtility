/*
 * @(#)XmlUtility      01/04/2003
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
 * PURPOSE, OR NON-INFRINGEMENT. SYMPHONY SERSHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * @version    3.2
 * @author     Kiran Kumar.T
 */


package util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.Character.UnicodeBlock;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.symphonyrpm.applayer.common.constants.ConfigurationConstants;
import com.symphonyrpm.applayer.common.coreservices.ConfigManager;




/**
 * Utility for parsing the XML file.This has methods to find a specfied child element.
 * 
 */

public class XmlUtility
{
    /** parser */
	private static ClassLoader classLoader;	
	private static DocumentBuilderFactory dbf;
	private static TransformerFactory transformerFactory;
	private static XPathFactory xpathFactory;
	 
    /* create the parser when this class is loaded.*/
    static
    {
        try
        {
            dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			transformerFactory = TransformerFactory.newInstance();
			xpathFactory = XPathFactory.newInstance();
            classLoader = XmlUtility.class.getClassLoader();
        }catch (Exception ex)
        {
        }
    }

    final static String[] escapedChars = new String[128];
    final static boolean[] isEscapeChar = new boolean[128];
	static {
		for (int charCode = 0; charCode < 128; charCode++) {
			if (charCode == 46 || charCode == 32 || (charCode >= 48 && charCode <= 57) || (charCode >=65 && charCode <= 90) || (charCode >= 97 && charCode <= 122)) {
				escapedChars[charCode] = null;
				isEscapeChar[charCode] = false;
			}
			else {
				escapedChars[charCode] = "&#" + charCode + ";";
				isEscapeChar[charCode] = true;
			}
		}
	}
	
	private static DocumentBuilder getDOMParserInstance()throws Exception
	{
		DocumentBuilder parser = dbf.newDocumentBuilder();
		return parser;
	}
    /**
     * Parse a XML file and get a Doc object for DOM file model.
     * @param uri location of XML file and defined as URL style.
     * @return DOM file object. if any exception, return null.
     */
    public static Document parse(URL url)throws IOException
    {
        InputStream is = null;
        return parse(url.openStream());
    }

    public static Document parse(InputStream is)
    {
        try
        {
            Document doc = getDOMParserInstance().parse(is);
            is.close();
            return doc;
        }catch(Exception ex)
        {
			return null;
		}
	}
    /**
     * Parse a String and get a Doc object for DOM file model.
     * @param String str xml as a String
     * @return DOM file object. if any exception, return null.
     */
	public static Document parseFromString(String str)throws Exception
	{
		StringReader sr = new StringReader(str);
		Document doc = getDOMParserInstance().parse(new InputSource(sr));
		normalize(doc);
		return doc;
	}
	
	public static Document parse(Node node)throws Exception
	{
		Document newDocument  = getDOMParserInstance().newDocument();
		Node importedNode = newDocument.importNode(node,true);
		newDocument.appendChild(importedNode);
		
		normalize(newDocument);
		return newDocument ;
	}
	
	public static Document parse(InputSource isrc) throws Exception {
		return parse(isrc, false);
	}
	
	public static Document parse(InputSource isrc, boolean normalize) throws Exception {
		Document doc = getDOMParserInstance().parse(isrc);
		if (normalize) {
			normalize(doc);
		}
		return doc;
	}
	
	private static boolean removeBlankNodes(Node node)
	{
		if(node!=null)
		{
			NodeList nodeList = node.getChildNodes();
			int nLength = 0;			
			if(nodeList!=null && (nLength = nodeList.getLength())>0)
			{
				for(int i=0;i<nLength;i++)
				{
					if(removeBlankNodes(nodeList.item(i)))i--;
				}
			}
			if(node.getNodeType() == Node.TEXT_NODE)
			{
				String nodeVal = node.getNodeValue();
				if(nodeVal==null || nodeVal.trim().length()==0)
				{
					node.getParentNode().removeChild(node);
					return true;							
				}
			}
		}
		return false;
	}
	
	public static void normalize(Document doc)
	{
		if(doc!=null)
		{
			removeBlankNodes(doc);
			doc.normalize();
		}
	}
	
    /**
     * Parse a XML file and get a Doc object for DOM file model.
     * @param path XMLfile's path and defined as String.
     * @param isURI the path is URI or not.
     * @return DOM file object. if any exception, return null.
     * @throws IOException
     * @throws SAXException
     */
    public static Document parse (String filePath, boolean isURI)throws Exception
    {
            if (isURI)
            {
                return getDOMParserInstance().parse(filePath);
            }
            else
            {
                File theFile = new File(filePath);
                return getDOMParserInstance().parse(theFile);
            }
    }


    public static Document parse (String filePath)throws IOException, SAXException
    {
    	InputStream in = classLoader.getResourceAsStream(filePath);
    	if(in == null)
    	{
    		throw new IOException("File not found "+filePath);
    	}
    	return parse(in);	
    }
	
	public static Collection escape(Collection stringCollection)
	{
		return escape(stringCollection, false);
	}

	public static Collection escape(Collection stringCollection, boolean doEscapeDot)
	{
		if(stringCollection==null)return null;
		Collection escapedList = new ArrayList(stringCollection.size());
		Iterator stringIterator = stringCollection.iterator();
		while(stringIterator.hasNext())escapedList.add(escape((String)stringIterator.next(), doEscapeDot));
		return escapedList;
	}
	
	public static boolean isCharToBeEscaped(int charCode,boolean doEscapeDot)
	{
		//should escape only the required once - special characters only
		boolean isCharToBeEscaped = doEscapeDot ? (charCode==32 || (charCode>=48 && charCode<=57) || (charCode>=65 && charCode<=90) || (charCode>=97 && charCode<=122)) : (charCode==46 || charCode==32 || (charCode>=48 && charCode<=57) || (charCode>=65 && charCode<=90) || (charCode>=97 && charCode<=122));
		if(isCharToBeEscaped) 
		{
			return false;
		}
		return true;
		
		/*if(doEscapeDot)
		{
			if(charCode==32 || (charCode>=48 && charCode<=57) || (charCode>=65 && charCode<=90) || (charCode>=97 && charCode<=122)) return false;
		}
		else
		{
			if(charCode==46 || charCode==32 || (charCode>=48 && charCode<=57) || (charCode>=65 && charCode<=90) || (charCode>=97 && charCode<=122)) return false;
			
		}	
	 return true;*/
	}
	
	private static String escapeOLD(String temp,boolean doEscapeDot)
	{
		if(temp == null ) return null;
		char [] charArray = temp.toCharArray();
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<charArray.length;i++)
			{
		 		int ival = (int)charArray[i];
				if(isCharToBeEscaped(ival,doEscapeDot)) 
				{
					buffer.append("&#"+ival+";");
				}else
				{
					buffer.append(charArray[i]);
				}
			}	
			return buffer.toString();
	}
	
	public static String escape(String temp, boolean doEscapeDot) {
		if(temp == null ) return null;
		char [] charArray = temp.toCharArray();
		StringBuilder buffer = new StringBuilder(temp);
		int len = charArray.length;
		if (doEscapeDot) {
			for (int i = len - 1; i >= 0; i--) {
		 		int ival = (int)charArray[i];
		 		if (ival == 46 || ival >= isEscapeChar.length) {
		 			buffer.delete(i, i + 1);
					buffer.insert(i, "&#" + ival + ";");
		 		}
		 		else if (isEscapeChar[ival]) {
					buffer.delete(i, i + 1);
					buffer.insert(i, escapedChars[ival]);
				}
			}	
		}
		else {
			for (int i = len - 1; i >= 0; i--) {
		 		int ival = (int)charArray[i];
		 		if (ival >= isEscapeChar.length) {
		 			buffer.delete(i, i + 1);
					buffer.insert(i, "&#" + ival + ";");
		 		}
		 		else if (isEscapeChar[ival]) {
					buffer.delete(i, i + 1);
					buffer.insert(i, escapedChars[ival]);
				}
			}
		}
		return buffer.toString();
	}
	
	public static String escape(String temp)
	{
		return escape(temp,false);	
	}

    /**
     * create a blank DOM doc.
     * @return a blank DOM doc.
     */
    public static Document createDocument()
    {
        try {
				return getDOMParserInstance().newDocument();	
		} catch (Exception e) {
		}
        return null;
    }

    public static Node createDocument(Document doc)
    {
        return doc.getDocumentElement();
    }

	public static Element createAndAppendElement(Element parent,String elementName,Map attributeMap)
	{
		Document rootDoc = parent.getOwnerDocument();
		Element newElement = rootDoc.createElement(elementName);
		if(attributeMap!=null && attributeMap.size()>0)
		{
			Iterator attrIterator = attributeMap.keySet().iterator();		
			while(attrIterator.hasNext())
			{
				String attributeName = (String)attrIterator.next();
				String attributeValue = (String)attributeMap.get(attributeName);
				newElement.setAttribute(attributeName,attributeValue);
			}
		}
		parent.appendChild(newElement);
		return newElement;
	}

	public static Element createAndAppendElement(Element parent,String elementName,String attributeName,String attributeValue)
	{
		Document rootDoc = parent.getOwnerDocument();
		Element newElement = rootDoc.createElement(elementName);
		newElement.setAttribute(attributeName,attributeValue);
		parent.appendChild(newElement);
		return newElement;
	}

	public static Element findChildElement(Element parent, String tag, String attr, String value,boolean allLevels) {
		if(allLevels)
		{
			NodeList kids = parent.getElementsByTagName(tag);
			Element retval = null, temp = null;
			for (int i = 0; i < kids.getLength(); i++) {
				temp = (Element) kids.item(i);
				Attr attrNode = temp.getAttributeNode(attr);
				if (attrNode != null) {
					String val = attrNode.getNodeValue();
					if (val.equals(value)) {
						retval = temp;
						break;
					}
				}
			}
			return retval;
		}else
		return findChildElement(parent, tag, attr,value);
	}

    /**
     * Search for a subnode according to the subnode's name and
attributes. if the many matching      * the conditions, return the
first one. If no mathing, rentun null.
     * @param parent parent node.
     * @param childElementName name of the child Elemtnt. if null,
search all and return the first      * one.
     * @param childAttributeName attributes name. if null, search all
and return the first one.
     * @param childAttributeValue ��attributes name of the searched
subnode's attribute's name.
     * @return result.
     */
    public static Element findChildElement(Node parent, String childElementName,
    	String childAttributeName, String childAttributeValue)
    {
        if (parent==null)
        {
            return null;
        }

        NodeList nodelist = parent.getChildNodes();
        /* circulate for all the subnodes of the parent node.*/
        for (int i=0;i<nodelist.getLength();i++)
        {
            Node childNode = nodelist.item(i);
            /* if it is Element*/
            if (childNode.getNodeType()==Node.ELEMENT_NODE)
            {
                Element childElement = (Element)childNode;
                if (childElementName == null || childElement.getTagName().equals(childElementName))
                {
                    if (childAttributeName==null || childElement.getAttribute(childAttributeName).equals(childAttributeValue))
                    {
                        return childElement;
                    }

                }
            }
        }/* end of circulating*/
        return null;
   }

   /**
	* Returns the first Element given a NodeList
	* @param pNodeList list of Nodes
	* @return null if the first Node is not of type Element; otherwise return it as Element
	*/
   private static Element findElementNS(NodeList pNodeList) {
	   if ((pNodeList != null) && pNodeList.getLength() >= 1) {
		   Node pNode = pNodeList.item(0);
		   if (pNode.getNodeType() == Node.ELEMENT_NODE) {	
			   return (Element) pNode;
		   }
	   }
	   return null;
   }

   /**
	* Finds the specified Element
	* @param doc XML document
	* @param nameSpace specific namespace
	* @param elemName name of Element to find
	* @return the first match
	*/
   public static Element findElementNS(Document doc, String nameSpace, String elemName)
   {
	   return findElementNS( doc.getElementsByTagNameNS(nameSpace, elemName));
   }

    /**
     * get node's attributes and return a hashtable.
     * @param node
     * @return A hashtable including all the node's attibutes. If no
attributes, return a hashtable      * without any elements.
     */
    public static Hashtable getAttributes(Node node)
    {
        NamedNodeMap nnm = node.getAttributes();
        Hashtable hashtable =new Hashtable();
        /* if attributes is null, return a hashtable without any
elements.*/
        if (nnm==null)
        {
            return hashtable;
        }
        /* circulate for all attribute nodes.*/
        for (int i=0;i<nnm.getLength();i++)
        {
            Node child = nnm.item(i);
            String attrName = child.getNodeName();
            String attrValue = child.getNodeValue();
            /* if attribute name and value are not null*/
            if (attrName!=null && attrValue!=null)
            {
                hashtable.put(attrName,attrValue);
            }
        }/* end of circulating*/
        return hashtable;
    }
	/**
	 * Method gets all attributes of an element in string format.
	 */
	public static String getAllAttributesAsString(Element eventElem, String delim, boolean inQuotes)
	{
		StringBuffer sbAttribs = new StringBuffer();
		NamedNodeMap attrMap = eventElem.getAttributes();
		for(int i=0; i < attrMap.getLength(); i++)
		{
			Node attribNode = attrMap.item(i);
			sbAttribs.append(" ").append(attribNode.getNodeName());
			if(inQuotes)
				sbAttribs.append("=\"");
			else
				sbAttribs.append("=");
			sbAttribs.append(attribNode.getNodeValue());
			if(inQuotes)
				sbAttribs.append("\" ");
			else
				sbAttribs.append(" ");
		}
		return sbAttribs.toString();
	}
	
	public static NodeList evalXpath(Node context, String xPathExpr) throws Exception
	{
		//Commented for compliance with portlet
		
		/*xPathExpr =  unEscapeHtmlSequence(xPathExpr,false);
		XObject xObj = XPathAPI.eval(context, xPathExpr);
		NodeIterator nIterator = xObj.nodeset();
		NodeListImpl nodeList = new NodeListImpl();
		Node node = null;
		while((node=nIterator.nextNode())!=null)
		{
			nodeList.add(node);
		}
		return nodeList;*/
		xPathExpr = unEscapeHtmlSequence(xPathExpr,false);
		NodeList nodeListXPathResult = XPathAPI.selectNodeList(context, xPathExpr);
		return nodeListXPathResult;
	}
	
	/**
	 * Use an XPath string to check if a node is present
	 *
	 * @param context The node to start searching from.
	 * @param xPathExpr A valid XPath string.
	 * @return true if node exists else false
	 */
	public static boolean isNodePresent(Node context, String xPathExpr) throws Exception
	{
		boolean nodePresent = false;
		
		xPathExpr = unEscapeHtmlSequence(xPathExpr,false);
		Node nodeXPathResult = XPathAPI.selectSingleNode(context, xPathExpr);
		
		if ( nodeXPathResult != null )
		{
			nodePresent = true;
		}
		
		return nodePresent;
	}

	public static Node[] getChildNodes(Node parentElement, String childName)
		{
		if(parentElement == null || childName == null || childName.equals(""))
			return null;

		NodeList nl = parentElement.getChildNodes();
		int actualCount = 0, nodeListLength = nl.getLength();
		Node[] retNodeArr = new Node[nodeListLength];
		Node volatileNode = null;
		for(int count=0; count < nodeListLength; count++)
		{
			volatileNode = nl.item(count);
			if(volatileNode.getNodeName().equalsIgnoreCase(childName))
			{
				retNodeArr[actualCount] = volatileNode ;
				actualCount++;
			}
		}

		if(nodeListLength != actualCount)
		{
			Node[] newRetNodeArr = new Node[actualCount];
			if(actualCount != 0)
				System.arraycopy(retNodeArr, 0, newRetNodeArr, 0, actualCount);
			return newRetNodeArr;
		}
		return retNodeArr;
	}
	
	public static Node[] getChildNodes(Node parentElement, String[] childNames)
	{
		if(parentElement == null || childNames == null || childNames.length == 0)
			return null;

		List<String> names = Arrays.asList(childNames);
		NodeList nl = parentElement.getChildNodes();
		int actualCount = 0, nodeListLength = nl.getLength();
		Node[] retNodeArr = new Node[nodeListLength];
		Node volatileNode = null;
		for(int count=0; count < nodeListLength; count++)
		{
			volatileNode = nl.item(count);
			if(names.contains(volatileNode.getNodeName()))
			{
				retNodeArr[actualCount] = volatileNode ;
				actualCount++;
			}
		}

		if(nodeListLength != actualCount)
		{
			Node[] newRetNodeArr = new Node[actualCount];
			if(actualCount != 0)
				System.arraycopy(retNodeArr, 0, newRetNodeArr, 0, actualCount);
			return newRetNodeArr;
		}
		return retNodeArr;
	}
	public static Node getChildNode(Node parentElement, String childName)
	{
		Node[] nl = getChildNodes(parentElement, childName);
		if (nl != null && nl.length > 0)
			return nl[0];
		else
			return null;
	}
	

	public static String documentToXML(Document document)throws Exception
	{
		return nodeToString(document);
	}
	
	public static String nodeToString(Node node, OutputFormat outputFormat)throws Exception
	{
		StringWriter stringWriter = new StringWriter();
		XMLSerializer xmlserializer = new XMLSerializer(stringWriter, outputFormat);
		xmlserializer.asDOMSerializer();
		if(node instanceof Document)
			xmlserializer.serialize((Document) node);
		else
			xmlserializer.serialize((Element) node);
	
		return stringWriter.toString();		
	}

	public static String nodeToString(Node node)throws Exception
	{
		return nodeToString(node, false);
	}
	
	public static String nodeToString(Node node, boolean doIndent)throws Exception
	{
		OutputFormat outputformat = new OutputFormat();
		outputformat.setOmitXMLDeclaration(true);
		outputformat.setIndenting(doIndent);
		outputformat.setLineWidth(999);
		StringWriter stringWriter = new StringWriter();
		XMLSerializer xmlserializer = new XMLSerializer(stringWriter, outputformat);
		xmlserializer.asDOMSerializer();
		if(node instanceof Document)
			xmlserializer.serialize((Document) node);
		else
			xmlserializer.serialize((Element) node);
	
		return stringWriter.toString();		
	}
	
	/**
	 * Write node xml to file
	 * @param node
	 * @param filename
	 * @throws Exception
	 */
	public static void nodeToFile(Node node, String filename) throws Exception {
		// Prepare the DOM document for writing
		Source source = new DOMSource(node);

		// Prepare the output file
		File file = new File(filename);
		Result result = new StreamResult(file);

		// Write the DOM document to the file
		Transformer xformer = transformerFactory.newTransformer();
		xformer.transform(source, result);
	}


	public static String getFormattedXMLString(Node node) throws Exception
	{
		return nodeToString(node, true);
	}

	public static Node getDescendant(Node parentElement, String childName)
	{
		NodeList nl = getDescendants(parentElement, childName);
		if(nl != null && nl.getLength() > 0)
			return nl.item(0);		else
			return null;
	}
	public static NodeList getDescendants(Node parentElement, String childName)
	{
		NodeList nl = ((Element)parentElement).getElementsByTagName(childName);
		return nl;
	}

	static private class NodeListImpl implements NodeList
    {
    	ArrayList nodes = new ArrayList();
    	
    	public int getLength()
    	{
    		return nodes.size();
    	}
		
		public Node item(int i)    	
		{
			return (Node)nodes.get(i);
		}
		
		void add(Node node)
		{
			if(node!=null)
			{
				nodes.add(node);
			}
		}
    }
	
	public static String unEscapeColon(String strEscaped){
		if(strEscaped == null ) return null;
		StringBuilder unEscapedBuffer = new StringBuilder();
		char charArray[] = strEscaped.toCharArray();
		for (int i = 0; i < charArray.length; i++)
		{
			if(charArray[i]=='&' && (charArray.length > i+1 && charArray[i+1]=='#') && ((charArray.length>i+4 && charArray[i+4]==';') || (charArray.length>i+5 && charArray[i+5]==';') || (charArray.length>i+6 && charArray[i+6]==';')))
			{
				if("&#58;".equals(""+charArray[i]+charArray[i+1]+charArray[i+2]+charArray[i+3]+charArray[i+4]))
				{
					unEscapedBuffer.append(":");
					i = i+5;
				}
			}
			unEscapedBuffer.append(charArray[i]);
	   }
	   return unEscapedBuffer.toString();
	}
   
	private static String unEscapeHtmlSequenceOLD(String strEscaped, boolean doUnescapeDbQuote)
	{
		if(strEscaped==null)return null;
		char charArray[] = strEscaped.toCharArray();
		StringBuffer unEscapedBuffer = new StringBuffer();
		for (int i = 0; i < charArray.length; i++)
		{
			if(charArray[i]=='&' && (charArray.length > i+1 && charArray[i+1]=='#') && ((charArray.length>i+4 && charArray[i+4]==';') || (charArray.length>i+5 && charArray[i+5]==';') || (charArray.length>i+6 && charArray[i+6]==';') || (charArray.length>i+7 && charArray[i+7]==';')))
			{
				if(!doUnescapeDbQuote && "&#34;".equals(""+charArray[i]+charArray[i+1]+charArray[i+2]+charArray[i+3]+charArray[i+4]))
				{
					unEscapedBuffer.append("&quot;");
					i = i+4;continue;
				}
				if(charArray[i+4]==';')
				{
					try{
							unEscapedBuffer.append((char)Integer.parseInt(""+charArray[i+2]+charArray[i+3]));
							i = i+4;continue;
						}catch (NumberFormatException e){}
				}else if(charArray[i+5]==';')
				{
					try{
							unEscapedBuffer.append((char)Integer.parseInt(""+charArray[i+2]+charArray[i+3]+charArray[i+4]));
							i = i+5;continue;
					   }catch (NumberFormatException e){}
				}else if(charArray[i+6]==';')
				{
					try{
							unEscapedBuffer.append((char)Integer.parseInt(""+charArray[i+2]+charArray[i+3]+charArray[i+4]+charArray[i+5]));
							i = i+6;continue;
					   }catch (NumberFormatException e){}
				}
				else if(charArray[i+7]==';')
				{
					try{
							unEscapedBuffer.append((char)Integer.parseInt(""+charArray[i+2]+charArray[i+3]+charArray[i+4]+charArray[i+5]+charArray[i+6] ));
							i = i+7;continue;
					   }catch (NumberFormatException e){}
				}
			}
			unEscapedBuffer.append(charArray[i]);
	   }
	   return unEscapedBuffer.toString(); 
		
	}
	
	public static String unEscapeHtmlSequence(String strEscaped, boolean doUnescapeDbQuote) {
		if (strEscaped == null) {
			return null;
		}
		
		if (!doUnescapeDbQuote) {
			strEscaped = strEscaped.replaceAll("&#34;", "&quot;");
		}
		
		StringBuilder unEscapedBuffer = new StringBuilder(strEscaped);
		int searchStartIndex = 0;
		int ampHashIndex = 0;
		do {
			ampHashIndex = unEscapedBuffer.indexOf("&#", searchStartIndex);
			if (ampHashIndex >= 0) {
				int semicolonIndex = unEscapedBuffer.indexOf(";", ampHashIndex);
				if (semicolonIndex > 0 && (semicolonIndex - ampHashIndex) > 3 && (semicolonIndex - ampHashIndex) < 8) {
					String codePoint = unEscapedBuffer.substring(ampHashIndex + 2, semicolonIndex);
					try {
						int intCode = Integer.parseInt(codePoint);
						unEscapedBuffer.delete(ampHashIndex, semicolonIndex + 1);
						unEscapedBuffer.insert(ampHashIndex, (char)intCode);
					}
					catch (NumberFormatException nfe) {}
					searchStartIndex = ampHashIndex + 1;
				}
				else {
					searchStartIndex = searchStartIndex + 2;
				}
			}	
		}
		while (ampHashIndex >= 0 && searchStartIndex >= 0);
		
		return unEscapedBuffer.toString(); 
	}
	
   /*
    *  This method is used to un escape the HTML escaped characters in a 
    *  given String.
    */
   
	public static String unEscapeHtmlSequence(String strEscaped)
	{
		return unEscapeHtmlSequence(strEscaped,true);
	}

	public static String unEscapeDOT(String strEscaped)
	{
		if(strEscaped==null)return null;
		String dot="&#46;";
		char charArray[] = strEscaped.toCharArray();
		StringBuilder unEscapedBuffer = new StringBuilder();
		for (int i = 0; i < charArray.length; i++)
		{
			if(charArray[i]=='&' && charArray[i+1]=='#' && ((charArray.length>i+4 && charArray[i+4]==';')))
			{
					if(dot.equals(""+charArray[i]+charArray[i+1]+charArray[i+2]+charArray[i+3]+charArray[i+4]))
					{
						try{
								unEscapedBuffer.append((char)Integer.parseInt(""+charArray[i+2]+charArray[i+3]));
								i = i+4;continue;
							}catch (NumberFormatException e){}
					}
			}
			unEscapedBuffer.append(charArray[i]);
	   }
	   return unEscapedBuffer.toString(); 
	}
	

	public static String getAttributeValue(Node parent, String attrName) {
		NamedNodeMap nnm = parent.getAttributes(); 
		String retVal = "";
		if (nnm != null) { 
			int len = nnm.getLength() ; 
			Attr attr; 
			for ( int i = 0; i < len; i++ ) { 
				attr = (Attr)nnm.item(i);
				if (attr.getNodeName().equalsIgnoreCase(attrName)) {
					retVal = attr.getNodeValue();
					break;
				}
			}
		}
		return retVal;
	}
	
	public static Map<String, String> getAttributeValueMap(Node node) {
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		if(node !=null){
			NamedNodeMap namedNodeMap = node.getAttributes();
			for (int index = 0; (namedNodeMap != null)  && (index < namedNodeMap.getLength()); index++) {
				Attr attr = (Attr)namedNodeMap.item(index);
				if(attr !=null){
					map.put(attr.getNodeName(), attr.getNodeValue());
				}
			}
		}
		return map;
	}
	
	public static Node getDescendant(Node parent, String tag, String attr, String value)
	{
		{
			NodeList kids = ((Element) parent).getElementsByTagName(tag);
			Node retval = null, temp = null;
			for (int i = 0; i < kids.getLength(); i++)
			{
				temp = (Element) kids.item(i);
				Attr attrNode = ((Element) temp).getAttributeNode(attr);
				if (attrNode != null)
				{
					String val = attrNode.getNodeValue();
					if (value == null || val.equals(value))
					{
						retval = temp;
						break;
					}
				}
			}
			return retval;
		}
	}

	public static Node getChildNode(Node parent, String tag, String attr, String value)
	{
		{
			NodeList kids = getChildNodesList(parent,tag);
			Node retval = null, temp = null;
			for (int i = 0; i < kids.getLength(); i++)
			{
				temp = (Element) kids.item(i);
				Attr attrNode = ((Element) temp).getAttributeNode(attr);
				if (attrNode != null)
				{
					String val = attrNode.getNodeValue();
					if (value == null || val.equals(value))
					{
						retval = temp;
						break;
					}
				}
			}
			return retval;
		}
	}
	
	public static Node createNode(Node parentNode, String tagName )
	{
		if(parentNode instanceof Document)
			return ((Document)parentNode).createElement(tagName);
		else 
			return parentNode.getOwnerDocument().createElement(tagName);
	}

	public static NodeList getChildNodesList(Node parentElement, String childName)
		{
		if(parentElement == null || childName == null || childName.equals(""))
			return null;

		NodeList nl = parentElement.getChildNodes();
		int actualCount = 0, nodeListLength = nl.getLength();
		NodeListImpl retNodeArr = new NodeListImpl();
		Node volatileNode = null;
		for(int count=0; count < nodeListLength; count++)
		{
			volatileNode = nl.item(count);
			if(volatileNode.getNodeName().equalsIgnoreCase(childName))
			{
				retNodeArr.add(volatileNode) ;
				actualCount++;
			}
		}

		if(nodeListLength != actualCount)
		{
			/*Node[] newRetNodeArr = new Node[actualCount];
			if(actualCount != 0)
				System.arraycopy(retNodeArr, 0, newRetNodeArr, 0, actualCount);
			return retNodeArr;*/
		}
		return retNodeArr;
	}

	public static NodeList getChildNodesList(Node parentElement, String childName, 
												String attr, String value) {
		if (parentElement == null || childName == null || childName.length() == 0 
			|| attr == null || attr.length() == 0) {
			return null;
		}	
		
		NodeList nl = parentElement.getChildNodes();
		int nodeListLength = nl.getLength();
		NodeListImpl retNodeArr = new NodeListImpl();
		Node volatileNode = null;
		
		for (int count = 0; count < nodeListLength; count++) {
			volatileNode = nl.item(count);
			if (volatileNode.getNodeName().equalsIgnoreCase(childName) 
				&& ((Element) volatileNode).getAttribute(attr).equals(value)) {
				retNodeArr.add(volatileNode);
			}
		}

		return retNodeArr;
	}

	public static NodeList getDescendants(Node parent, String tag, String attr, String value)
	{
		{
			NodeList kids = ((Element) parent).getElementsByTagName(tag);
			NodeListImpl returnKids = new NodeListImpl();
			Node temp = null;
			for (int i = 0; i < kids.getLength(); i++)
			{
				temp = (Element) kids.item(i);
				Attr attrNode = ((Element) temp).getAttributeNode(attr);
				if (attrNode != null)
				{
					String val = attrNode.getNodeValue();
					if (value == null || val.equals(value))
					{
						returnKids.add(temp);
					}
				}
			}
			return returnKids;
		}
	}

	/**
	 * @param newParentNode
	 * @param oldParentNode
	 */
	public static void copyChildNodes(Node newParentNode, Node oldParentNode)
	{
		//if(newParentNode.getOwnerDocument() != oldParentNode.getOwnerDocument())
		//	oldParentNode = newParentNode.getOwnerDocument().importNode(oldParentNode,true);
		Node temp = null;
		NodeList oldNodeChildren = oldParentNode.getChildNodes(); 
		for (int i = 0; i < oldNodeChildren.getLength(); i++)
		{
			temp = (Element) oldNodeChildren.item(i);
			if(newParentNode.getOwnerDocument() != oldParentNode.getOwnerDocument())
			{
				temp = newParentNode.getOwnerDocument().importNode(temp, true);
			}
			newParentNode.appendChild(temp);
		}		
	}
	
	/**
	 * This method escapes the XML characters there in the string.
	 * <p>
	 * @param xml			The XML string.
	 * @return
	 */
	/*public static String escapeXML(String xml)
	{
		char charArray [] = xml.toCharArray();
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<charArray.length;i++)
		{
			switch(charArray[i])
			{
				case  '&'  : buffer.append("&amp;");
							 break;
					case  '<'  : buffer.append("&lt;");
								 break;			
					case  '>'  : buffer.append("&gt;");
								 break;			
					case  '\"'  : buffer.append("&quot;");
								 break;				
					case  '\''  : buffer.append("&#39;");
								 break;											 						 
				default    : buffer.append(charArray[i]);			 			
			}
		}
		return buffer.toString();
	}*/
	
	public static String unEscapeXML(String xmlStr)
	{
		if(xmlStr.indexOf("&lt;") > -1){
            xmlStr = xmlStr.replaceAll("&lt;", "<");
		}
		if (xmlStr.indexOf("&gt;") > -1) {
            xmlStr = xmlStr.replaceAll("&gt;", ">");
		}
		if(xmlStr.indexOf("&quot;") > -1){
            xmlStr = xmlStr.replaceAll("&quot;", "\"");
		}
		if (xmlStr.indexOf("&apos;") > -1)  {
			xmlStr = xmlStr.replaceAll("&apos;", "'");
		}
		if(xmlStr.indexOf("&amp;") > -1){
			xmlStr = xmlStr.replaceAll("&amp;", "&");
		}
		return xmlStr;
	}
	
	public static String[] unEscapeXML(String[] xmlStr)
	{
		for(int i=0; i < xmlStr.length; i++)
		{
			String currXml = xmlStr[i];
			currXml = unEscapeXML(currXml);
			xmlStr[i] = currXml;
		}
		return xmlStr;
	}
	/**
	 * This method escapes the XML characters there in the string. This method works same as escapeXml in rpmadapter's XMLUtils class method. 
	 * <p>
	 * @param xml			The XML string.
	 * @return
	 */
	public static String escapeXML(String xml)
	{
		if(xml == null)return "";
		char charArray [] = xml.toCharArray();
		StringBuilder buffer = new StringBuilder();
		for(int i=0;i<charArray.length;i++)
		{
			switch(charArray[i])
			{
				case  '&'  : buffer.append("&#38;");
							 break;
				case  '<'  : buffer.append("&#60;");
								 break;			
				case  '>'  : buffer.append("&#62;");
								 break;			
				case  '\"' : buffer.append("&#34;");
								 break;				
				case  '\'' : buffer.append("&#39;");
								 break;		
				case  '{'  : buffer.append("&#123;");
								break;	
				case  '}'  : buffer.append("&#125;");
								break;	
				case  ','  : buffer.append("&#44;");
								break;
				case  '\\'  : buffer.append("&#92;");
								break;	
				case  '|'  : buffer.append("&#124;");
								break;														 						 
				default    : buffer.append(charArray[i]);			 			
			}
		}
		return buffer.toString();
	}	
	
	public static String escapeXML(String xml, boolean andOptr)
	{
		char charArray [] = xml.toCharArray();
		StringBuilder buffer = new StringBuilder();
		for(int i=0;i<charArray.length;i++)
		{
			switch(charArray[i])
			{
				case  '&'  : buffer.append("&#38;");
							 break;	 						 
				default    : buffer.append(charArray[i]);			 			
			}
		}
		return buffer.toString();
	}		
	
	public static String escapeXMLEntities(String strTobeEscaped) 
	{
		String escapedString = strTobeEscaped ;
		if(escapedString !=null && escapedString.trim().length()>0)
		{
			char charArray[] = strTobeEscaped.toCharArray();
			StringBuilder buffer = new StringBuilder();
			for(int i=0; i<charArray.length; i++)
			{
				switch(charArray[i])
				{
					case '&':
						buffer.append("&amp;");
						break;
					case '\'':
						buffer.append("&apos;");
						break;
					case '\"':
						buffer.append("&quot;");
						break;
					case '<':
						buffer.append("&lt;");
						break;
					case '>':
						buffer.append("&gt;");
						break;
					default :
						buffer.append(charArray[i]);
						break;
				}
			}
			escapedString = buffer.toString();
		}

		return escapedString ;
	}
	
	/**
	 * Escapes the HTML entities in a string. Escaped to display appropriately in the front-end.
	 * @param inputStr
	 * @return
	 */
	public static String escapeHTMLEntities(String inputStr) {
		String output = inputStr; 
		try {
			output = output.replaceAll("&", "&amp;");
			output = output.replaceAll("\"", "&quot;");
			output = output.replaceAll("<", "&lt;");
			output = output.replaceAll(">", "&gt;");
		}
		catch (Exception ex) {} 
		return output; 
	}
	/**
	 * @param sourceXML Source XML from which Nodes have to be copied.
	 * @param destinationXML Destination XML to which Nodes have to be copied.
	 * @param nodeName Names of the node which is to be copied. 
	 * The Source and the Destination XML should be identical in structure in all respects. 
	 * @return
	 * @throws Exception
	 */
	public static String copyNodes(String sourceXML, String destinationXML, String nodeName, HashMap hmpNodeMap) throws Exception
	{
		String reconciledXML = null;
		Document sourceXMLDoc = parseFromString(sourceXML);
		Document destinationXMLDoc = parseFromString(destinationXML);
		
		NodeList sourcePropertyNodesList = XmlUtility.getDescendants(sourceXMLDoc.getDocumentElement(),nodeName);
		NodeList destinationPropertyNodesList = XmlUtility.getDescendants(destinationXMLDoc.getDocumentElement(),nodeName);
		//System.out.println(" sournce property list " + sourcePropertyNodesList.getLength() + ": destinationPropertyNodesList :" + destinationPropertyNodesList.getLength());
		for (int i = 0; i < destinationPropertyNodesList.getLength(); i++)
		{
	
			if (hmpNodeMap.containsKey(new Integer(i)))
			{
				
				Integer intj =  (Integer)hmpNodeMap.get(new Integer(i));
				int j = intj.intValue();
				Node sourcePropertyNode = sourcePropertyNodesList.item(j);
				Node destinationPropertyNode = null;
				if(sourcePropertyNode!=null)
				{
					destinationPropertyNode = destinationPropertyNodesList.item(i);
					sourcePropertyNode = destinationXMLDoc.importNode(sourcePropertyNode,true);
					destinationPropertyNode.getParentNode().replaceChild(sourcePropertyNode,destinationPropertyNode);
				}
			}
			
		}
		
		reconciledXML = nodeToString(destinationXMLDoc.getDocumentElement());
		return reconciledXML; 
	}

	/**
	 * @param newChild - The node to insert.
	 * @param refChild - The reference node, i.e., the node before which the new node must be inserted. 
	 * @return
	 */
	public static void insertAfter(Node newChild, Node refChild){
		try{
			Node parentNode = refChild.getParentNode();
			Node tempNode = refChild.getNextSibling();
			if(tempNode != null)
				parentNode.insertBefore(newChild, tempNode);
			else parentNode.appendChild(newChild);	
		}
		catch(Exception exp){}
	}
	public static String removeSpaces(String s) {
		  StringTokenizer st = new StringTokenizer(s," ",false);
		  String t="";
		  while (st.hasMoreElements()) t += st.nextElement();
		  return t.replaceAll("\"","");
	}
	
	/**
	 * This method ensures that the output String has only
	 * valid XML unicode characters as specified by the
	 * XML 1.0 standard.
	 * @param in The String whose non-valid characters we want to remove.
	 * @return The in String, stripped of non-valid characters.
	 */
	public String removeReplaceInValidXMLCharacters(String in) 
	{
		StringBuffer str = new StringBuffer();
	  	char current; 
	  	if ( in == null || ("".equals(in))) return ""; 
	  	for (int i = 0; i < in.length(); i++) 
	  	{
		  current = in.charAt(i); 
		  //When text is copied from Microsoft Word, Word assigns its own UTF-8/ASCII value to '(single quote) and
		  //"(double quote) which is not recognised by other applications. So we need to replace them with standard 
		  //characters.
		  int curr = current;
		  //Replace '(single quote) copied from Word
		  if ( curr == 145 || curr == 146 ) 
		  {
		  	current = '\'';
		  }
		  //Replace "(double quote) copied from Word
		  if ( curr == 147 || curr == 148 ) 
		  {
		  	current = '"';
		  }
		  
		  if ( ( current == 0x9 ) ||
			   ( current == 0xA ) ||
			   ( current == 0xD ) ||
			   ( ( current >= 0x20 ) && ( current <= 0xD7FF ) ) ||
			   ( ( current >= 0xE000 ) && ( current <= 0xFFFD ) ) ||
			   ( ( current >= 0x10000 ) && ( current <= 0x10FFFF ) ) )
		  {
	          str.append(current);
		  }
	  	}
	 	return str.toString();
  	}
	/**
	 * This method does the escape opreation for the occurence of special characters.
	 * @param temp
	 * @return
	 */
	public static String escapeSpecialChars(String temp)
	{
		StringBuilder buffer = new StringBuilder();		
		if (temp != null) {
		char [] charArray = temp.toCharArray();
		for(int i=0;i<charArray.length;i++)
			{
				int ival = (int)charArray[i];
				if(charArray[i] == '"' || charArray[i] == '*' || charArray[i] == '<' || charArray[i] == '&' || charArray[i] == '%' || charArray[i] == '.') 
				{
					buffer.append("&#"+ival+";");
				}else
				{
					buffer.append(charArray[i]);
				}
			}
		}		
			return buffer.toString();
	}
  	public static void setTextContent(String xPathExpr , Document document ,String isVisible)throws Exception{
  		Node node = getXpathNode(xPathExpr, document);
		node.setTextContent(isVisible);
  	}
  	
  	public static Node getXpathNode(String xPathExpr , Node document)throws Exception{
  		//XPath xpath = XPathFactory.newInstance().newXPath();
  		XPath xpath = xpathFactory.newXPath();
		XPathExpression expr = xpath.compile(xPathExpr);

		return (Node)expr.evaluate(document, XPathConstants.NODE);
  	}
  	
  	public static String setAttribute(String xPathExpr , String attributeName ,String attributeValue, String xml){
		try {
			Document document = XmlUtility.parseFromString(xml);
			setAttribute(xPathExpr, attributeName, attributeValue, document);
			xml = XmlUtility.getFormattedXMLString(document);
		} 
		catch (Exception e) {
		}
  		return xml;
  	}
  	
  	/**
  	 * @Description Customize method to override node attribute value with <tt>attributeValue</tt> according to <tt>xPathExpr</tt>
  	 * @param xPathExpr
  	 * @param attributeName
  	 * @param attributeValue
  	 * @param document
  	 * @throws Exception
  	 */
	public static void setAttribute(String xPathExpr , String attributeName ,String attributeValue, Document document)throws Exception{
		NodeList nodeList = XmlUtility.evalXpath(document.getDocumentElement(), xPathExpr);
		for(int i=0;i<nodeList.getLength();i++){
			Element node = (Element)nodeList.item(i);
			node.setAttribute(attributeName, attributeValue);
		}
	}
	
	public static String setAttribute(String xPathExpr , String attributeName ,String attributeValue, Node node)throws Exception{
		NodeList nodeList = XmlUtility.evalXpath(node, xPathExpr);
		for(int i=0;i<nodeList.getLength();i++){
			Element element = (Element)nodeList.item(i);
			element.setAttribute(attributeName, attributeValue);
		}
		return XmlUtility.nodeToString(node);
	}
	
	public static String escapeChineseCharacter(String stringName){
		boolean  isUtf8EncodingEnabled = ConfigManager.getInstance().getPropertyAsBoolean(ConfigurationConstants.RPM_PROPERTIES_FILE_NAME, ConfigurationConstants.IS_UTF8_ENCODING_ENABLED, false);
		if (!isUtf8EncodingEnabled){
			return stringName;
		}
		StringBuilder out = new StringBuilder();
		Set<UnicodeBlock> chineseUnicodeBlocks = new HashSet<UnicodeBlock>() {{
		    add(UnicodeBlock.CJK_COMPATIBILITY);
		    add(UnicodeBlock.CJK_COMPATIBILITY_FORMS);
		    add(UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS);
		    add(UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT);
		    add(UnicodeBlock.CJK_RADICALS_SUPPLEMENT);
		    add(UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION);
		    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
		    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
		    add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B);
		    add(UnicodeBlock.KANGXI_RADICALS);
		    add(UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS);
		}};
		for (char c : stringName.toCharArray()) {
		    if (chineseUnicodeBlocks.contains(UnicodeBlock.of(c))) {
		    	int ival = (int)c;
		    	out = out.append("&#").append(ival).append(';');
		    } else {
		    	out = out.append(c);
		    }
		}
		return out.toString();
	}
	
	public static void documentToFile(Document doc, String filename) throws Exception {
		Source source = new DOMSource(doc);
		Result result = new StreamResult(new File(filename).getAbsolutePath());
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(source, result);
	}
	
	/**
  	 * @Description Customize method to override node attribute value with <tt>attributeValue</tt>
  	 * @param node
  	 * @param attributeName
  	 * @param attributeValue
  	 */
	public static void setNodeAttribute(Node node,String attributeName ,String attributeValue){
		Element element = (Element)node;
		element.setAttribute(attributeName, attributeValue);
	}
	
	/*public static byte[] documentToBytes(Document doc) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StreamResult result = new StreamResult(bos);
		transformer.get().transform(new DOMSource(doc), result);
		byte[] bytes = bos.toByteArray();
		return bytes;
	}*/
	
	public static void saveDocumentToFile(Document doc, String path) throws Exception {
	    // Use a Transformer for output
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Transformer transformer = tFactory.newTransformer();
	    DOMSource source = new DOMSource(doc);
	    StreamResult result = new StreamResult(new File(path).getPath());
	    transformer.transform(source, result);
	}  
	
}
