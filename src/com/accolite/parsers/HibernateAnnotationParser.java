package com.accolite.parsers;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.persistence.*;

import com.accolite.datamodel.ColumnDetail;
import com.accolite.datamodel.Model;
import com.accolite.datamodel.TableDetail;
import com.accolite.utility.ExtractJarUtil;

public class HibernateAnnotationParser {

	private static Model model = new Model();

	public static void main(String[] args) {
		String filename = "C:\\AnkitM\\DR\\sample_hibernate_annotation\\filenames.txt";
		ArrayList<String> listOfFiles = new ArrayList<>();
		ExtractJarUtil.readTextFile(filename, listOfFiles);
		parseHibernateAnnotation(listOfFiles);

	}

	public static void parseHibernateAnnotation(ArrayList<String> listOfFiles) {

		// TODO AM - find way to load all jars into classpath. As of now
		// manually loaded.

		/*
		 * We need to get name of all classes. 2 step process. 1: unjar the jar
		 * 2: find all the class names but instead of these two steps: we can
		 * use this command to grep all class file names from jar: jar tf
		 * HibernateSampleAnnotations.jar | grep.class
		 */
		/*
		 * String jarPath = "C:\\AnkitM\\DR\\JarUnjar"; String unJarPath =
		 * "C:\\AnkitM\\DR\\JarUnjar\\unjar"; String type = ".class";
		 * ExtractJarUtil.extractJarFiles(jarPath, unJarPath, type);
		 * ArrayList<String> files = new ArrayList<>();
		 * ExtractJarUtil.listOfFiles(unJarPath, files, type);
		 */

		/*
		 * //File file = new
		 * File("C:\\AnkitM\\DR\\sample_hibernate_annotation\\classes"); //URL
		 * url; //url = file.toURI().toURL(); //URL[] urls = new URL[] { url };
		 * //ClassLoader cl = new URLClassLoader(urls); //Class<?> aClass =
		 * cl.loadClass("ankit.Actor");
		 */
		for (String file : listOfFiles) {
			System.out.println("prcessing file: "+ file);
			Class<?> aClass = null;
			try {
				aClass = Class.forName(file);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return;
			}

			// process class level annotations
			if(processClassAnnotation(aClass)){

			// process primary key first
			Method[] methods = aClass.getMethods();
			for (Method aMethod : methods) {
				if (aMethod.getAnnotation(Id.class) instanceof Id) {
					processMethodAnnotation(aMethod, aClass);
				}
			}

			// process method level annotations
			for (Method aMethod : methods) {
				if (!(aMethod.getAnnotation(Id.class) instanceof Id)) {
					processMethodAnnotation(aMethod, aClass);
				}
			}

			// process field level annotations
			Field[] fields = aClass.getDeclaredFields();
			for (Field aField : fields) {
				//System.out.println("field name: " + aField.getName());
				processFieldAnnotation(aField);
			}
			}
		}
		HashMap<String, TableDetail> tableMap = model.getTableMap();
		for (Entry<String, TableDetail> entry : tableMap.entrySet()) {
			System.out.println(entry.getKey() + "/" + entry.getValue().toString());
		}
	}

	private static void processMethodAnnotation(Method aMethod, Class<?> aClass) {
		ColumnDetail columnDetail = new ColumnDetail();
		TableDetail table = getTable(aClass);

		// Process column & Primary key
		Annotation annotation_column = aMethod.getAnnotation(Column.class);
		if (annotation_column instanceof Column) {
			if (aMethod.getAnnotation(Id.class) instanceof Id) {
				columnDetail.setPrimaryKey(true);
			}
			Column columnAnnotation = (Column) annotation_column;
			String columnName = columnAnnotation.name();
			columnDetail.setColumnName(columnName);
			table.getColumns().put(columnName, columnDetail);
		}

		// Process foreign key of other tables. OneToMany Annotation 
		Annotation annotation_onetomany = aMethod.getAnnotation(OneToMany.class);
		if (annotation_onetomany instanceof OneToMany) {
			OneToMany oneToManyAnnotation = (OneToMany) annotation_onetomany;
			
			Class<?> newClass = getReturnTypeOfGetMethod(aMethod); // getReturnTypeOfGetMethod() will never return null in this case.
			processClassAnnotation(newClass);
			columnDetail.setForeignKey(true);
			columnDetail.setForeignKeyTable(table.getTableName());
			columnDetail.setForeignKeyColumn(getPrimaryKey(table)); // getPrimaryKey() will never return null in this case.
			String joinColumnName = getJoinColumnName(newClass, oneToManyAnnotation.mappedBy());
			columnDetail.setColumnName(joinColumnName);
			TableDetail newTable = getTable(newClass);
			newTable.getColumns().put(joinColumnName, columnDetail);
		}
		
		// Process ManyToMany Annotation. Join Table - with no extra column.
		Annotation annotation_joinTable = aMethod.getAnnotation(JoinTable.class);
		if (aMethod.getAnnotation(ManyToMany.class) instanceof ManyToMany && annotation_joinTable instanceof JoinTable) {
			JoinTable joinTableAnnotation = (JoinTable) annotation_joinTable;
			TableDetail joinTable = new TableDetail();
			String joinTablename = joinTableAnnotation.name();
			joinTable.setTableName(joinTablename);
			
			// set joinColumn
			{
			JoinColumn[] jc = joinTableAnnotation.joinColumns();
			String joinColumnName = jc[0].name();
			columnDetail.setPrimaryKey(true);
			columnDetail.setColumnName(joinColumnName); // assumption - there is just one join column
			columnDetail.setForeignKey(true);
			columnDetail.setForeignKeyTable(table.getTableName());
			columnDetail.setForeignKeyColumn(getPrimaryKey(table)); // getPrimaryKey() will never return null in this case.
			joinTable.getColumns().put(joinColumnName, columnDetail);
			}
			
			// set inverse join column
			{
				
			JoinColumn[] ijc = joinTableAnnotation.inverseJoinColumns();
			String i_joinColumnName = ijc[0].name();
			ColumnDetail i_columnDetail = new ColumnDetail();
			i_columnDetail.setPrimaryKey(true);
			i_columnDetail.setColumnName(i_joinColumnName); // assumption - there is just one join column
			i_columnDetail.setForeignKey(true);
			Class<?> newClass = getReturnTypeOfGetMethod(aMethod); // getReturnTypeOfGetMethod() will never return null in this case.
			if(model.getClassMap().get(newClass.getName()) == null){
				processClassAnnotation(newClass);	
			}
			String t_name = model.getClassMap().get(newClass.getName());
			i_columnDetail.setForeignKeyTable(t_name);
			String p_name = getPrimaryKey(model.getTableMap().get(t_name));
			if( p_name == null){
				// process primary key first
				Method[] methods = newClass.getMethods();
				for (Method m : methods) {
					if (m.getAnnotation(Id.class) instanceof Id) {
						processMethodAnnotation(m, newClass);
						p_name = getPrimaryKey(model.getTableMap().get(t_name));
					}
				}
			}
				i_columnDetail.setForeignKeyColumn(p_name);
			joinTable.getColumns().put(i_joinColumnName, i_columnDetail);
			}
			model.getTableMap().put(joinTablename, joinTable);
		}
		
	}

	private static boolean processClassAnnotation(Class<?> aClass) {
		Annotation annotation = aClass.getAnnotation(Table.class);
		if (annotation instanceof Table) {
			Table tableAnnotation = (Table) annotation;
			String tableName = tableAnnotation.name();
			//System.out.println("table name: " + tableName);
			//System.out.println("db name: " + tableAnnotation.catalog());
			TableDetail table = model.getTableMap().get(tableName);
			if (table == null) {
				table = new TableDetail();
				table.setTableName(tableName);
			}
			table.setPackageName(aClass.getPackage().getName());
			String className = aClass.getName();
			table.setClassName(className);
			model.getTableMap().put(tableName, table);
			model.getClassMap().put(className, tableName);
			return true;
		}else{
			return false;
		}
	}

	private static void processFieldAnnotation(Field aField) {
		Annotation[] annotations = aField.getDeclaredAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Column) {
				@SuppressWarnings("unused")
				Column columnAnnotation = (Column) annotation;
				//System.out.println("column name: " + columnAnnotation.name());
			}
		}
	}

	private static String getJoinColumnName(@SuppressWarnings("rawtypes") Class aClass, String fieldOfNewClass) {

		try {
			String matchingClass = aClass.getDeclaredField(fieldOfNewClass).getType().getName();
			Method[] methods = aClass.getMethods();
			for (Method aMethod : methods) {

				if ((aMethod.getReturnType().getName()).equals(matchingClass)) {
					return aMethod.getAnnotation(JoinColumn.class).name();
				}
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static TableDetail getTable(@SuppressWarnings("rawtypes") Class aClass) {
		return model.getTableMap().get(model.getClassMap().get(aClass.getName()));
	}
	
	private static String getPrimaryKey(TableDetail table){
		HashMap<String, ColumnDetail> coulmnMap = table.getColumns();
		for (Entry<String, ColumnDetail> entry : coulmnMap.entrySet()) {
			if (entry.getValue().isPrimaryKey()) {
				return entry.getKey();
			}
		}
		return null;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private static Class getReturnTypeOfGetMethod(Method aMethod){
		Type returnType = aMethod.getGenericReturnType();
		if (returnType instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType) returnType;
			Type[] typeArguments = type.getActualTypeArguments();
			for (Type typeArgument : typeArguments) {
				return (Class) typeArgument;
			}
		}
		return null;
	}
}