# Beetle CLI
Beetle is a command-line application (Java) that started as an ETL tool (hence the
"ETL" in the name), but now provides two pieces of functionality:

* Execute a SQL query and save the results to an output file
* Generate fake data and save the results to an output file

Both options support the same output file formats:

* CSV
* JSON
* TOML
* YAML
* XML
* SQL (INSERT statements)

This is the list of supported commands:

* add dbtype <name> <JDBC reference> <JDBC driver class name> - Add a new database type
* add userdb <name> <url> <user> - Add a JDBC database reference
* cat <file> - Print a local file
* clear dbtype <id> jar - Clear the JAR reference for a database type
* connect userdb <id> - Connect to the specified user database
* debug - Print whether we're in debug mode or not
* debug off - Disable debug mode
* debug on - Enable debug mode
* delete userdb <id> - Delete a user-defined database
* describe table <table name> - Describe a table in the connected user-database
* dir [<path>] - Print the directory listing for an optional directory (default is '.')
* export csv <filename> - Set the session to export data to CSV
* export json <filename> - Set the session to export data to JSON
* export sql <filename> <tablename> -  - Set the session to export data to SQL
* export toml <filename> - Set the session to export data to TOML
* export xml <filename> - Set the session to export data to XML
* export yaml <filename> - Set the session to export data to YAML
* fake <specification> - Set the session input to fake data
* gc - Run the garbage collector
* head <file> - Print the first 10 lines of a local file
* help - Print the list of commands
* help <start of a command> - Print the list of commands matching the parameter
* import db file <filename> - Set the session input to the SQL query in the file
* import db query <query> - Set the session input to the query
* import db table <name> - Set the session input to the database table
* jar <filename> - Add the JAR file to the classpath
* list dbtypes - List the known types of databases
* list tables - List the tables in the connected database
* list userdbs - List the known user-defined databases
* mem - Print memory usage statistics
* parse fake <specification> - Parse the specification and print the output
* print session - Print input / output information on the session
* quit - Exit the application
* reset session - Clear out the session
* run [count] - Run the session 'count' times (default is 1)
* set dbtype <id> jar <filename> - Set the JDBC JAR driver for the database type
* time - Print the current time
* time <command> - Print the execution time for a command
* version - Print version information

## Exporting database data to a file
Saving data from a database requires a few preliminary steps:

* Ensure the type of database is listed when running "list dbtypes"
* Ensure the type of database has the JDBC JAR referenced
* The database is defined in Beetle as a user-defined database

### Adding a database type
Before Beetle can retrieve information from a database, Beetle needs
to know the name of the JDBC driver class, and where the JDBC JAR
driver is.  To see the list of supported database types, run this
command (shown with sample output):

-> list dbtypes 
ID   Name         Class                                           Jar File
1    Derby        org.apache.derby.jdbc.EmbeddedDriver            
2    H2           org.h2.Driver     
3    MySQL        com.mysql.jdbc.Driver                           
4    MariaDB      org.mariadb.jdbc.Driver                         
5    Oracle       oracle.jdbc.OracleDriver                        
6    Hive         org.apache.hadoop.hive.jdbc.HiveDriver          
7    Neo4J        org.neo4j.jdbc.Driver                           
8    PostgreSQL   org.postgresql.Driver                           
9    DB2          com.ibm.db2.jcc.DB2Driver                       
10   Redis        br.com.svvs.jdbc.redis.RedisDriver              
11   Cassandra    org.apache.cassandra.cql.jdbc.CassandraDriver   
12   MongoDB      mongodb.jdbc.MongoDriver                        
13   HBase        org.apache.phoenix.jdbc.PhoenixDriver           
14   SQL Server   com.microsoft.sqlserver.jdbc.SQLServerDriver    
15   SQLite       org.sqlite.JDBC                                 
16   Sybase       net.sourceforge.jtds.jdbc.Driver                

As you can see from the above output, none of these database types
have been assigned a JDBC JAR file, so currently none are available
for use as a source database.

If your database is not of one of the types listed above, you'll
need to add a reference.  Run this command:

add dbtype <Name> <JDBC reference> <Driver class name>

The name is just the name you want to use to refer to the database type,
the JDBC reference is the second string in the JDBC URL for that database,
and the driver class name is the name of the driver class.

For example, if I wanted to add support for H2 databases (assume Beetle
didn't already support it), I would use this command:

add dbtype H2 h2 org.h2.Driver

### Adding the JDBC JAR
The next step is to ensure that your database type has a link to a local
copy of the JAR file with the JDBC driver class for your database.

To set this, you'll first need the ID of the database type.  Run the
'list dbtypes' command (output shown above).  If we want to add an
Oracle driver, the ID is 5, and the JAR file will need to contain
the oracle.jdbc.OracleDriver class.  Assuming you have an ojdbc.jar
file that meets this requirement, and it's in the same directory
as Beetle, run this command

set dbtype 5 jar ojdbc.jar

### Adding a user-defined database
Now that Beetle supports your database type, you now need to add
a reference to the database.  