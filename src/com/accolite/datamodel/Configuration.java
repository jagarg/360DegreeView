package com.accolite.datamodel;

public class Configuration {

	private String configName;
	private String databaseName;
	private String dbUserName;
	private String dbPassword;
	private String schemaName;
	private JDBCConnection connection;
	
	public String getConfigName() {
		return configName;
	}
	public void setConfigName(String configName) {
		this.configName = configName;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getDbUserName() {
		return dbUserName;
	}
	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	public JDBCConnection getConnection() {
		return connection;
	}
	public void setConnection(JDBCConnection connection) {
		this.connection = connection;
	}
}
