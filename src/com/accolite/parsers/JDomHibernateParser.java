package com.accolite.parsers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.accolite.datamodel.ColumnDetail;
import com.accolite.datamodel.Model;
import com.accolite.datamodel.TableDetail;
import com.accolite.orient.OrientLoader;

public class JDomHibernateParser {
	
	public static void main(String[] args) {
		String dirName = "C:\\AnkitM\\DR\\sample_hibernate_xml\\sample_hibernate_xml2";
		//File inputFile = new File("C:\\AnkitM\\DR\\sample_hibernate_xml\\hibernateSample4.xml");
		Model model = new Model();
		parseXml(listOfFiles(dirName, new ArrayList<String>()), model);
		OrientLoader.initiateLoad(model);
	}
	
	public static void parseXml(ArrayList<String> files, Model model ) {
		for (String file : files) {
			File inputFile = new File(file);
		try {
			
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(inputFile);

			//System.out.println("Root element :" + document.getRootElement().getName());
			Element hibernateMappingElement = document.getRootElement();

			if (hibernateMappingElement.getName().equals("hibernate-mapping")) {
				String packageName = hibernateMappingElement.getAttributeValue("package");
				Element classElement = hibernateMappingElement.getChild("class");

				if (classElement != null) {
					String className = classElement.getAttributeValue("name");
					String tableName = classElement.getAttributeValue("table");
					//System.out.println("** className: " + className);
					//System.out.println("** tableName: " + tableName);
					//System.out.println("** catalog: " + classElement.getAttributeValue("catalog"));
					
					TableDetail table = model.getTableMap().get(tableName);
					if(table == null){
						table = new TableDetail();
						table.setTableName(tableName);
					}
					table.setPackageName(packageName);
					table.setClassName(className);
					// model.getClassMap().put(className, tableName);
					// not setting catalog as of now
					
					List<Element> propertyList = classElement.getChildren();
					//System.out.println("----------------------------");

					for (int temp = 0; temp < propertyList.size(); temp++) {
						Element property = propertyList.get(temp);
						//System.out.println("\nCurrent Element :" + property.getName());
						
						if (property.getName().equals("id") || property.getName().equals("property")) {
							ColumnDetail columnDetail = new ColumnDetail();
							if(property.getName().equals("id")){
								columnDetail.setPrimaryKey(true);								
							}
							String columnName = property.getAttributeValue("column") != null ? property.getAttributeValue("column") : (property.getChild("column")!=null ? property.getChild("column").getAttributeValue("name"):null);
							//System.out.println("** columnName: " + columnName);
							//System.out.println("** type: " + property.getAttributeValue("type"));
							columnDetail.setColumnName(columnName);
							columnDetail.setLocalFieldName(property.getAttributeValue("name"));
							// not setting column type as of now.
							table.getColumns().put(columnName, columnDetail);
						}
						
						/*if (property.getName().equals("many-to-one")) {
							System.out.println("** foreignKey bool : true ");
							columnDetail.setForeignKey(true);
							columnDetail.setColumnName(property.getChild("column").getAttributeValue("name"));
							columnDetail.setLocalFieldName(property.getAttributeValue("name"));
							columnDetail.setForeignKeyClass(property.getAttributeValue("class"));
							// assuming that foreignKeyColumn name is same as column name. otherwise read from map
							columnDetail.setForeignKeyColumn(columnDetail.getColumnName()); 
							
							// get foreignKeyTable from classMap
							// System.out.println("** foreignKeyTable:
							// "+property.getAttributeValue("name"));
						}*/
						
						
						Element oneToMany = property.getChild("one-to-many");
						if(property.getName().equals("set") && property.getAttributeValue("inverse").equals("true") && oneToMany != null ){
							String newTableName = property.getAttributeValue("table");
							//System.out.println("newTableName "+newTableName);
							TableDetail newTable = model.getTableMap().get(newTableName);
							if(newTable == null){
								newTable = new TableDetail();
								newTable.setTableName(newTableName);
							}
							ColumnDetail newTableColumnDetail = new ColumnDetail();
							newTableColumnDetail.setForeignKey(true);
							Element key = property.getChild("key");
							newTableColumnDetail.setColumnName(key.getAttribute("column") != null ? key.getAttributeValue("column"): key.getChild("column").getAttributeValue("name"));
							HashMap<String, ColumnDetail> coulmnMap = table.getColumns();
							for (Entry<String, ColumnDetail> entry : coulmnMap.entrySet())
							{
								if(entry.getValue().isPrimaryKey()){
									newTableColumnDetail.setForeignKeyColumn(entry.getKey());
								}
							}
							newTableColumnDetail.setForeignKeyTable(tableName);
							newTable.getColumns().put(newTableColumnDetail.getColumnName(), newTableColumnDetail);
							if(model.getTableMap().get(newTableName) == null){
								model.getTableMap().put(newTableName, newTable);	
							}
						}
						
						
						
						
						
						
						
						
						
						// code to parse join table
						// hibernateSample5.xml && hibernateSample6.xml are pending to parse. 
						// many-to-many relationship + join table + no extra column
						Element manyToMany = property.getChild("many-to-many");
						if(property.getName().equals("set") && manyToMany != null ){
							//System.out.println("** many to many realtionship");
							String joinTableName = property.getAttributeValue("table");
							TableDetail joinTable = model.getTableMap().get(joinTableName);
							if(joinTable == null){
								joinTable = new TableDetail();
								joinTable.setTableName(joinTableName);
							}
							
							Element key = property.getChild("key");
							if ( key != null) {
								String columnName = key.getAttribute("column") != null ? key.getAttributeValue("column"): key.getChild("column").getAttributeValue("name");
								ColumnDetail joinTableColumnDetail = new ColumnDetail();
								joinTableColumnDetail.setPrimaryKey(true);
								joinTableColumnDetail.setColumnName(columnName);
								
								// and this primary key is foreign key also
								joinTableColumnDetail.setForeignKey(true);
								joinTableColumnDetail.setForeignKeyTable(tableName);
								HashMap<String, ColumnDetail> coulmnMap = table.getColumns();
								for (Entry<String, ColumnDetail> entry : coulmnMap.entrySet())
								{
									if(entry.getValue().isPrimaryKey()){
										joinTableColumnDetail.setForeignKeyColumn(entry.getKey());
									}
								}
								joinTable.getColumns().put(joinTableColumnDetail.getColumnName(), joinTableColumnDetail);
							}
							
							/*if( manyToMany != null){
								ColumnDetail joinTableColumnDetail = new ColumnDetail();
								joinTableColumnDetail.setPrimaryKey(true);
								String columnName = manyToMany.getAttribute("column") != null ? manyToMany.getAttributeValue("column"): manyToMany.getChild("column").getAttributeValue("name");
								joinTableColumnDetail.setColumnName(columnName);
								
								// and this primary key is foreign key also
								System.out.println("** foreignKey bool : true ");
								joinTableColumnDetail.setForeignKey(true);
								String foreignKeyClass = manyToMany.getAttributeValue("class") != null ? manyToMany.getAttributeValue("class") : manyToMany.getAttributeValue("entity-name");
								joinTableColumnDetail.setForeignKeyClass(foreignKeyClass);
								
								
								// find from map
								// joinTableColumnDetail.setForeignKeyTable();
								// joinTableColumnDetail.setForeignKeyColumn();
								joinTable.getColumns().put(joinTableColumnDetail.getColumnName(), joinTableColumnDetail);
							}*/
							if(model.getTableMap().get(joinTableName) == null){
								model.getTableMap().put(joinTableName, joinTable);	
							}
							}
						}
					if(model.getTableMap().get(tableName) == null){
						model.getTableMap().put(tableName, table);	
					}
					
					}
				}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
		//System.out.println("model size: "+model.getTableMap().size());
		
		HashMap<String, TableDetail> tableMap = model.getTableMap();
		for (Entry<String, TableDetail> entry : tableMap.entrySet())
		{
			System.out.println(entry.getKey() + "/" + entry.getValue().toString());
		}

		
	}
	
	// TODO AM - delete this method and try to use method from ExtractJarUtil
	public static ArrayList<String> listOfFiles(String directoryName, ArrayList<String> files) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()  && file.getName().endsWith("xml")) {
	            files.add(file.getAbsolutePath());
	        } else if (file.isDirectory()) {
	        	listOfFiles(file.getAbsolutePath(), files);
	        }
	    }
	    return files;
	}
}
