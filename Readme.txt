Model to Orient DB data load 
------------------------------

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
	