package com.accolite.parsers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.accolite.Utility.Utility;
import com.accolite.datamodel.ColumnDetail;
import com.accolite.datamodel.Model;
import com.accolite.datamodel.TableDetail;

public class JdoXmlParser {

	static boolean table_found;
	static ColumnDetail virtualPrimaryKey = null;

	public static Model ParseJdoXml(ArrayList<String> files,String classPath) {
		Model model = new Model();
		for (String file : files) {
			TableDetail table = new TableDetail();
			table_found = false;
			virtualPrimaryKey = null;
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				Document document = db.parse(new File(file));
				document.getDocumentElement().normalize();

				Node jdoNode = document.getElementsByTagName("jdo").item(0);
				Element jdoElement = (Element) jdoNode;

				Element packageElement = (Element) jdoElement.getElementsByTagName("package").item(0);
				table.setPackageName(packageElement.getAttribute("name"));

				Element classElement = (Element) packageElement.getElementsByTagName("class").item(0);
				table.setClassName(table.getPackageName() + "." + classElement.getAttribute("name"));
				table.setSuperClassName(classElement.getAttribute("persistence-capable-superclass"));

				NodeList classExtensionNodes = classElement.getElementsByTagName("extension");
				for (int x = 0; x < classExtensionNodes.getLength(); x++) {
					Node xmlNode = classExtensionNodes.item(x);
					processExtensionNode((Element) xmlNode, table);
				}

				if (!table_found) {
					String implClassName = null;
					for (int x = 0; x < classExtensionNodes.getLength(); x++) {
						Node xmlNode = classExtensionNodes.item(x);
						implClassName = processNonTableClassMapJdo((Element) xmlNode, table.getPackageName());
					}
					if (implClassName != null && !implClassName.isEmpty())
						model.getClassMap().put(table.getClassName(), implClassName);
					else if (classExtensionNodes.getLength() != 0) {
						System.out.println(
								"Table Information and Class information not present. Skipping file : " + file);
					}
					continue;
				}

				NodeList classFieldNodes = classElement.getElementsByTagName("field");
				for (int x = 0; x < classFieldNodes.getLength(); x++) {
					Node xmlNode = classFieldNodes.item(x);
					processFieldNode((Element) xmlNode, table);
				}
				// storing virtualPrimaryKey
				if (virtualPrimaryKey != null)
					table.getColumns().put(virtualPrimaryKey.getLocalFieldName(), virtualPrimaryKey);
			} catch (ParserConfigurationException | SAXException | IOException e) {
				System.out.println(e.getMessage());
			}

			model.getTableMap().put(table.getClassName(), table);
		}
		
		
		//enrich the jdo Model
		try {
			Utility.enrichJdoModel(model,classPath);
		} catch (ClassNotFoundException | SecurityException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return model;
	}

	static void processExtensionNode(Element xmlNode, TableDetail node) {
		String val = xmlNode.getAttribute("key");
		if (val.equalsIgnoreCase("table-name")) {
			node.setTableName(xmlNode.getAttribute("value"));
			table_found = true;
		} else if (val.equalsIgnoreCase("virtual-primary-key")) {
			virtualPrimaryKey = new ColumnDetail();
			virtualPrimaryKey.setLocalFieldName(xmlNode.getAttribute("value"));
			virtualPrimaryKey.setPrimaryKey(true);
		} else if (val.equalsIgnoreCase("sequence-name")) {
			node.setSequenceName(xmlNode.getAttribute("value"));
		}
	}

	//// to store mapping of class/interface and the implementation class
	static String processNonTableClassMapJdo(Element xmlNode, String packageName) {
		String val = xmlNode.getAttribute("value");
		if (val.contains(packageName)) {
			return val;
		}
		return null;
	}

	static void processFieldNode(Element xmlNode, TableDetail node) {
		ColumnDetail cd = new ColumnDetail();
		boolean primaryKey = false;
		if (xmlNode.getAttribute("primary-key") != null
				&& xmlNode.getAttribute("primary-key").equalsIgnoreCase("true")) {
			cd.setPrimaryKey(true);
			primaryKey = true;
		}

		if (xmlNode.getAttribute("name") != null)
			cd.setLocalFieldName(xmlNode.getAttribute("name"));

		NodeList extNodes = xmlNode.getElementsByTagName("collection");
		if (extNodes.getLength() > 0)
			return;
		extNodes = xmlNode.getElementsByTagName("extension");
		for (int x = 0; x < extNodes.getLength(); x++) {
			Element fieldElement = (Element) extNodes.item(x);
			// for column-name
			if (fieldElement.getAttribute("key") != null
					&& fieldElement.getAttribute("key").equalsIgnoreCase("column-name")) {
				cd.setColumnName(fieldElement.getAttribute("value"));
				if (primaryKey && virtualPrimaryKey != null)
					virtualPrimaryKey.setColumnName(fieldElement.getAttribute("value"));

				// embedded extension - look for foreign-key
				NodeList extExtNodes = fieldElement.getElementsByTagName("extension");
				Element extExtNode = (Element) extExtNodes.item(0);
				if (extExtNode != null && extExtNode.getAttribute("key") != null
						&& extExtNode.getAttribute("key").equalsIgnoreCase("foreign-key")) {
					cd.setForeignKey(true);
					cd.setForeignFieldName(extExtNode.getAttribute("value"));
				}
			}
			// for transcriber
			else if (fieldElement.getAttribute("key") != null
					&& fieldElement.getAttribute("key").equalsIgnoreCase("transcriber")) {
				// embedded extension - look for column-name
				NodeList extExtNodes = fieldElement.getElementsByTagName("extension");
				Element extExtNode = (Element) extExtNodes.item(0);
				if (extExtNode != null && extExtNode.getAttribute("key") != null
						&& extExtNode.getAttribute("key").equalsIgnoreCase("column-name")) {
					cd.setColumnName(extExtNode.getAttribute("value"));
					if (primaryKey && virtualPrimaryKey != null)
						virtualPrimaryKey.setColumnName(fieldElement.getAttribute("value"));
				}
			}
		}

		node.getColumns().put(cd.getLocalFieldName(), cd);
	}

}
