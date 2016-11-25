package com.accolite.rdms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.accolite.datamodel.ColumnDetail;
import com.accolite.datamodel.Model;
import com.accolite.datamodel.TableDetail;
import com.accolite.orient.OrientLoader;

public class OracleUtility {

	final static Logger logger = Logger.getLogger(OracleUtility.class);
	static Model model = new Model();
	static HashMap<String, TableDetail> tableMap = new HashMap<>();
	static String TABLE_LIST = "";
	
	static String table = "SELECT TABLE_NAME FROM user_tables ORDER BY TABLE_NAME";
	
	static String tableColumn = "SELECT A.TABLE_NAME, A.COLUMN_NAME "
			+ "FROM ALL_TAB_COLUMNS A INNER JOIN user_tables B "
			+ "ON A.TABLE_NAME = B.TABLE_NAME "
			+ "ORDER BY A.TABLE_NAME";
	
	static String foreignKeys = "SELECT B.TABLE_NAME,B.COLUMN_NAME,C.TABLE_NAME as FORIEGN_TABLE,C.COLUMN_NAME AS FORIEGN_COLUMN "
			+ "FROM all_cons_columns B, all_cons_columns C, all_constraints A "
			+ "WHERE B.constraint_name = A.constraint_name AND A.owner = B.owner AND B.position = C.position "
			+ "AND C.constraint_name = A.r_constraint_name AND C.owner = A.r_owner AND A.constraint_type = 'R' "
			+ "AND B.table_name IN (TABLE_LIST) ORDER BY B.TABLE_NAME";
	
	static String primaryKey = "SELECT TABLE_NAME,COLUMN_NAME "
			+ "FROM all_cons_columns B "
			+ "WHERE B.constraint_name IN "
			+ "( "
			+ "SELECT A.constraint_name "
			+ "FROM all_constraints A  "
			+ "WHERE A.TABLE_NAME IN (TABLE_LIST) AND A.constraint_type = 'P' AND A.constraint_name LIKE 'SYS%'"
			+ ") ORDER BY TABLE_NAME";
	
	public static void main(String[] args) {
		initiateProcess();
	}
	
	public static void initiateProcess() {
		logger.info(" Request : Oracle to Orient begin.. ");
		OrientLoader.initiateLoad(process());
		logger.info(" Request : Oracle to Orient end.. ");
	}
	
	private static void getTableList(final List<String> input) {
		StringBuffer tableList = new StringBuffer();
		
		for (String string : input) {
			tableList = tableList.append("'");
			tableList = tableList.append(string.split(":")[0]);
			tableList = tableList.append("',");
		}
		
		tableList = tableList.deleteCharAt(tableList.lastIndexOf(","));
		System.out.println(tableList.toString());
		TABLE_LIST = tableList.toString();
	}
	
	private static Model process() {
	
		logger.info("Fecthing tables ...");
		List<String> tables = executeQuery(table);
		getTableList(tables);
		
		logger.info("Fecthing Table and Column names ...");
		List<String> tableColumns = executeQuery(tableColumn);
		
		logger.info("Fecthing Primary Keys ...");
		primaryKey = primaryKey.replace("TABLE_LIST", TABLE_LIST);
		List<String> primaryKeys = executeQuery(primaryKey);
		
		logger.info("Fecthing Foreign Keys ...");
		foreignKeys = foreignKeys.replace("TABLE_LIST", TABLE_LIST);
		List<String> foriegnKeys = executeQuery(foreignKeys);		
		
		logger.info("Process data into Model Class.");
		String prevTable = null;
		HashMap<String, TableDetail> tableDetails = new HashMap<>();
		HashMap<String, ColumnDetail> columns = null;
		TableDetail td = null;
		
		for (String tableCol : tableColumns) {
			String tableName = tableCol.split(":")[0];
			String columnName = tableCol.split(":")[1];

			if(prevTable == null || !prevTable.equals(tableName) ) 
			{
				if(null != prevTable)
				{
					// Update old Table
					td.setColumns(columns);
					tableDetails.put((prevTable == null)?tableName:prevTable, td);
					logger.debug("TableName : "+prevTable);
					logger.debug("Columns : "+td.toString());
				}
				
				// Initialize for Next Table
				td = new TableDetail();
				td.setTableName(tableName);
				columns = new HashMap<>();
				prevTable = tableName;
			}		
			
			ColumnDetail cd = new ColumnDetail();
			cd.setColumnName(columnName);
			
			for (String pKey: primaryKeys) {
				String tN = pKey.split(":")[0];
				String cN = pKey.split(":")[1];
				
				if(cN.equals(columnName) && tN.equals(tableName))
				{
					cd.setPrimaryKey(true);
					break;
				}
			}
			
			for (String fKey: foriegnKeys) {
				String[] arr = fKey.split(":");
								
				if(arr[1].equals(columnName) && arr[0].equals(tableName))
				{
					cd.setForeignKey(true);
					cd.setForeignKeyTable(arr[2]);
					cd.setForeignKeyColumn(arr[3]);
					break;
				}
			}
			
			columns.put(tableName+":"+columnName, cd);
		}
		
		Model model = new Model();
		model.setTableMap(tableDetails);
		
		return model;
	}
	
	private static List<String> executeQuery(String query) {
		List<String> list = new ArrayList<>();
		logger.debug("QUERY : "+query);
		
		try(Connection connection = RDMSUtility.getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {	
			
			int columnCount = resultSet.getMetaData().getColumnCount();

			while(resultSet.next())
			{
				StringBuffer str = new StringBuffer();
				
				for(int i=1;i<=columnCount;++i)
					str = str.append(resultSet.getString(i)+RDMSUtility.FIELD_SEPERATOR);
				
				str = str.deleteCharAt(str.lastIndexOf(RDMSUtility.FIELD_SEPERATOR));
				list.add(str.toString());
			}
			
			return list;
			
		} catch (SQLException e) {
			logger.error(e.getStackTrace());
			return null;
		}
	}
}
