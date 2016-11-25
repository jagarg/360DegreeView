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