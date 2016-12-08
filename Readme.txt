==========================================
1.	Model to Orient DB data load 
==========================================

	Prerequisite
	--------------	
		Install OrientDB 2.2.13

	General
	--------------	
		1.	Update "DATABASE" with correct path of Installed and Created DB.
		2.	logs will be genrated under "logs/log4j-application.log"
		3.	Library required can be found under "lib" folder

	Steps
	--------------
		1.	Helper Class com.accolite.orient.OrientMain reads "model.ser" and obtains com.accolite.datamodel.Model
		2.	Loader Class com.accolite.orient.OrientLoader will populate OrientDB with Model
	
==========================================
2.	Oracle DB to Orient DB  
==========================================

	Prerequisite
	--------------	
		Step 1 - Model to Orient DB data load

	General
	--------------	
		1.	Update "resource/database.properties" with JDBC connection details and comment out other.
		2.	logs will be genrated under "logs/log4j-application.log"

	Steps
	--------------
		1.	Utility Class com.accolite.rdms.OracleUtility connects DataBase and created Model class.
		2.	Loader Class com.accolite.orient.OrientLoader will populate OrientDB with Model.
		
==========================================
3.	REST Service 
==========================================	

	User
	------
	a.	Get Table details
		-----------------------------------------
	
		Type : GET
		
		Request : http://localhost:8080/user/gettable/{database}/{table}
		
		Response : 
		{
		  "tableName": "TYPE_STRING",
		  "columns": 
		  [
			{
			  "columnName": "TYPE_STRING",
			  "foreignTable": "TYPE_STRING",
			  "foreignColumn": "TYPE_STRING",
			  "foreignKey": TYPE_BOOLEAN,
			  "primaryKey": TYPE_BOOLEAN
			},
			{ ... }
		  ]
		}
		
	b.	List all Database
		-----------------------------------------
		
		Type : GET
		
		Request : http://localhost:8080/user/listdb
		
		Response : 
		[
		  {
			"databaseName": "DB_1",
			"tables": 
				[
				  "TABLE_1",
				  "TABLE_2",
				  "TABLE_3",
				  "TABLE_4",
				  "",
				  "TABLE_N"
				]
		  },
		  {
			"databaseName": "DB_2",
			"tables": null
		  },
		  {
			...
		  }
		  {
			"databaseName": "DB_N",
			"tables": null
		  }
		]	
		
	c.	List table(s) mapping
		-----------------------------------------
		
		Type : GET
		
		Request : http://localhost:8080/user/getmappings/{database}/{table(s)}
		
		Response : 
		{
			tables": 
			[
				{
					"tableName": "TYPE_STRING",
					"columns": 
					[
						{
						  "columnName": "TYPE_STRING",
						  "foreignTable": "TYPE_STRING",
						  "foreignColumn": "TYPE_STRING",
						  "foreignKey": TYPE_BOOLEAN,
						  "primaryKey": TYPE_BOOLEAN
						},
						{ ... }
					]
					},
					{ ... }
				}
			],
			"paths": 
			[
				{
			      "sourceTable": "TYPE_STRING",
			      "sourceColumn": "TYPE_STRING",
			      "targetTable": "TYPE_STRING",
			      "targetColumn": "TYPE_STRING",
			    },
			    {...}
			]
		}		

	d.	List all paths table(s)
		-----------------------------------------
		
		Type : GET
		
		Request : http://localhost:8080/user/getallpaths/{database}/{table(s)}
		
		Response : 
		{
			tables": 
			[
				{
					"tableName": "TYPE_STRING",
					"columns": 
					[
						{
						  "columnName": "TYPE_STRING",
						  "foreignTable": "TYPE_STRING",
						  "foreignColumn": "TYPE_STRING",
						  "foreignKey": TYPE_BOOLEAN,
						  "primaryKey": TYPE_BOOLEAN
						},
						{ ... }
					]
					},
					{ ... }
				}
			],
			"paths": 
			[
				{
			      "sourceTable": "TYPE_STRING",
			      "sourceColumn": "TYPE_STRING",
			      "targetTable": "TYPE_STRING",
			      "targetColumn": "TYPE_STRING",
			    },
			    {...}
			]
		}		
	
	
	Admin
	------
	a.	Add new Configuration
		-----------------------------------------
		
		TYPE : POST
		
		Request : http://localhost:8080/config/add
		
		{
			"configName":"Sample",
			"databaseName":"Sample",
			"dbUserName":"Sample",
			"dbPassword":"Sample",
			"schemaName":"Sample",
			"schemaDetail":"Sample"
		}

		Response : Boolean
		
		
	b.	Get all Configuration
		-----------------------------------------
		
		TYPE : GET
		
		Request : http://localhost:8080/config/list
		
		Response : 
		[
		  {
			"configName": "Sample",
			"databaseName": "Sample",
			"dbUserName": "Sample",
			"schemaName": "Sample",
			"schemaDetail": "Sample",
			"dbPassword": "Sample"
		  },
		  {
			...
		  },
		  {
			...
		  }
		]