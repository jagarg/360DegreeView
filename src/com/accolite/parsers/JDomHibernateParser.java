package com.accolite.parsers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class JDomHibernateParser {
	public static void main(String[] args) {
		try {
			File inputFile = new File("C:\\AnkitM\\DR\\sample_hibernate_xml\\hibernateSample4.xml");
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(inputFile);

			System.out.println("Root element :" + document.getRootElement().getName());
			Element hibernateMappingElement = document.getRootElement();

			if (hibernateMappingElement.getName().equals("hibernate-mapping")) {
				System.out.println("** packageName: " + hibernateMappingElement.getAttributeValue("package"));
				Element classElement = hibernateMappingElement.getChild("class");

				if (classElement != null) {
					String className = classElement.getAttributeValue("name");
					String tableName = classElement.getAttributeValue("table");
					System.out.println("** className: " + className);
					System.out.println("** tableName: " + tableName);
					System.out.println("** catalog: " + classElement.getAttributeValue("catalog"));
					List<Element> propertyList = classElement.getChildren();
					System.out.println("----------------------------");

					for (int temp = 0; temp < propertyList.size(); temp++) {
						Element property = propertyList.get(temp);
						System.out.println("\nCurrent Element :" + property.getName());
						
						if (property.getName().equals("id")) {
							System.out.println("** primaryKey bool : true ");
							String columnName = property.getAttributeValue("column") != null ? property.getAttributeValue("column") : (property.getChild("column")!=null ? property.getChild("column").getAttributeValue("name"):null);
							System.out.println("** columnName: " + columnName);
							System.out.println("** localFieldName: " + property.getAttributeValue("name"));
							System.out.println("** type: " + property.getAttributeValue("type"));

						}
						
						if (property.getName().equals("property")) {
							String columnName = property.getAttributeValue("column") != null ? property.getAttributeValue("column") : (property.getChild("column")!=null ? property.getChild("column").getAttributeValue("name"):null);
							System.out.println("** columnName: " + columnName);
							System.out.println("** localFieldName: " + property.getAttributeValue("name"));
							System.out.println("** type: " + property.getAttributeValue("type"));
						}

						if (property.getName().equals("many-to-one")) {
							System.out.println("** foreignKey bool : true ");
							System.out
									.println("** columnName: " + property.getChild("column").getAttributeValue("name"));
							System.out.println("** foreignFieldName: "+property.getAttributeValue("name"));
							System.out.println("** foreignKeyClass: " + property.getAttributeValue("class"));
							// get foreignKeyTable from classMap
							// System.out.println("** foreignKeyTable:
							// "+property.getAttributeValue("name"));
							System.out.println(
									"** foreignKeyColumn: " + property.getChild("column").getAttributeValue("name"));
							// System.out.println("** type:
							// "+property.getAttributeValue("type"));
						}
						
						// many-to-many relationship + join table + no extra column
						Element manyToMany = property.getChild("many-to-many");
						if(property.getName().equals("set") && (property.getAttribute("inverse") == null || property.getAttributeValue("inverse").equals("false")) && manyToMany != null ){
							System.out.println("** many to many realtionship");
							// class name of join table can not be fetched as there is no class for it.
							//System.out.println("** className: " + classElement.getAttributeValue("name"));
							System.out.println("** tableName: " + property.getAttributeValue("table"));
							//System.out.println("** catalog: " + classElement.getAttributeValue("catalog"));
							
							Element key = property.getChild("key");
							if ( key != null) {
								System.out.println("** primaryKey bool : true ");
								String columnName = key.getAttribute("column") != null ? key.getAttributeValue("column"): key.getChild("column").getAttributeValue("name");
								System.out.println("** columnName: " + columnName);
								// localFieldName name of this column can not be fetched as there is no class for it.
								//System.out.println("** localFieldName: " + property.getAttributeValue("name"));
								//System.out.println("** type: " + property.getAttributeValue("type"));
								
								// and this primary key is foreign key also
								System.out.println("** foreignKey bool : true ");

								// foreignFieldName name of this column can not be fetched as there is no class for it.
								// System.out.println("** foreignFieldName: "+property.getChild("key").getAttributeValue("column"));
								
								System.out.println("** foreignKeyClass: " + className);
								System.out.println("** foreignKeyTable:"+ tableName);
								System.out.println("** foreignKeyColumn: " + columnName);
								// System.out.println("** type:
								// "+property.getAttributeValue("type"));
							}
							
							
							if( manyToMany != null){
								System.out.println("** primaryKey bool : true ");
								String columnName = manyToMany.getAttribute("column") != null ? manyToMany.getAttributeValue("column"): manyToMany.getChild("column").getAttributeValue("name");
								System.out.println("** columnName: " + columnName);
								// localFieldName name of this column can not be fetched as there is no class for it.
								
								// and this primary key is foreign key also
								System.out.println("** foreignKey bool : true ");
								// foreignFieldName name of this column can not be fetched as there is no class for it.
								
								String foreignKeyClass = manyToMany.getAttributeValue("class") != null ? manyToMany.getAttributeValue("class") : manyToMany.getAttributeValue("entity-name");
								System.out.println("** foreignKeyClass: " + foreignKeyClass);

								// find from map
								//System.out.println("** foreignKeyTable:"+ classElement.getAttributeValue("table"));
								//System.out.println("** foreignKeyColumn: " + property.getChild("key").getAttributeValue("column"));
							}
							
							}
						}
					}
				}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
