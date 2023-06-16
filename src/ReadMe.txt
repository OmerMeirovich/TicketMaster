************ HOW TO INSTALL IDE ENVIRORMENT **************
- Install NetBeansIDE 8.2 (with glass fish by default  ) 
* make sure to install netbeans with the JDK in the version 1.8
- Install Derby sql - https://mvnrepository.com/artifact/org.apache.derby/derby/10.14.2.0
- install my-sql-connector - https://mvnrepository.com/artifact/mysql/mysql-connector-java/8.0.28
in the netBeans press File -> Open project and choose the file directory 
- after opening project, click on project -> properties -> Libraries -> add the derby sql and mysql connector.
than press Window->services 
-Right click on databases and select create new database with the name ticket_master
-copy the contents of the text file "table creation" in the database package in the project's file
- Than press right click on the ticket_master DB and choos execute command 
-paste the contents and run the sql string 
(you can also fill the tables with the sql string in "TablesTestingParams.txt" )
------- the project is ready and you can press play or right click on an xhtml file and selecting run file -------------

