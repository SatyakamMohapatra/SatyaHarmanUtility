package util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *This class is a utility class to build xml dom structures
 * @author rsrivast
 */
public class XmlBuilder {
	
	Document xmlDocument;
	Node rootNode;
	Node currentNode;
	
	/**
	 * Create an xml document with the given root node name
	 * @param rootNodeName
	 * @throws Exception
	 */
	public XmlBuilder(String rootNodeName) throws Exception{
		xmlDocument = getDocument();
		rootNode = xmlDocument.createElement(rootNodeName);
		xmlDocument.appendChild(rootNode);
		currentNode = rootNode;
	}
	
	/**
	 * Get the xml document
	 * @return
	 */
	public Document getXmlDocument(){
		return xmlDocument;
	}
	
	private Document getDocument() throws Exception{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder(); 
		return documentBuilder.newDocument();
	}
	
	/**
	 * Add a child node to the current node
	 * @param childNodeName
	 */
	public void addChild(String childNodeName){
		addTo(currentNode, childNodeName);
	}
	
	/**
	 * Add a sibling node to the current node
	 * @param siblingNodeName
	 */
	public void addSibling(String siblingNodeName){
		if(currentNode.getParentNode() != null){
			addTo(currentNode.getParentNode(), siblingNodeName);
		}else{
			throw new RuntimeException("cannot add sibling to root node " + currentNode.getNodeName()); 
		}
	}
	
	/**
	 * Add a child node to a given parent node
	 * @param parentNodeName
	 * @param childNodeName
	 */
	public void addToParent(String parentNodeName, String childNodeName){
		Node parentNode = findParentBy(parentNodeName);
		if(parentNode == null){
			throw new RuntimeException("missing parent node: " + parentNodeName);
		}
		addTo(parentNode, childNodeName);
	}
	
	public void addTo(Node parentNode, String childNodeName) {
		Node childNode = xmlDocument.createElement(childNodeName);
		parentNode.appendChild(childNode);
		currentNode = childNode;
	}

	public Node findParentBy(String parentNodeName) {
		Node parentNode = currentNode;
		while(parentNode != null){
			if(parentNodeName.equals(parentNode.getNodeName())){
				return parentNode;
			}
			parentNode = parentNode.getParentNode();
		}
		return null;
	}
	
	/**
	 * Set an attribute to current node
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, String value){
		if(currentNode instanceof Element){
			((Element)currentNode).setAttribute(name, value);
		}
	}

	/**
	 * Set text value to current node
	 * @param value
	 */
	public void setValue(String value){
		if(currentNode instanceof Element){
			currentNode.appendChild(xmlDocument.createTextNode(value));
		}
	}

	/**
	 * Set character data to current node
	 * @param value
	 */
	public void setCdata(String value){
		if(currentNode instanceof Element){
			currentNode.appendChild(xmlDocument.createCDATASection(value));
		}
	}
	
	/**
	 * Check if current node has child nodes
	 * @return
	 */
	public boolean hasChildNodes(){
		if(currentNode.getChildNodes() != null && currentNode.getChildNodes().getLength() > 0){
			return true;			
		}
		
		return false;
	}
	
	/**
	 * @return
	 */
	public Node getCurrentNode() {
		return currentNode;
	}
	
	/**
	 * @param node
	 */
	public void setCurrentNode(Node node) {
		currentNode = node;
	}
	
	public Node importNode(Node importNode,boolean deep){
			return xmlDocument.importNode(importNode,deep);
		}
	
	public void appendChild(Node childNode){
		currentNode.appendChild(childNode);
	}
}
