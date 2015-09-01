# Beetle CLI
Beetle is a command-line application (Java) that started as an ETL tool (hence the
"ETL" in the name), but now provides three pieces of functionality:

* Execute a SQL query and save the results to an output file
* Generate fake data and save the results to an output file
* Group together the lines in a CSV file having the same key

The first two options support the same output file formats:

* CSV
* JSON
* TOML
* YAML
* XML
* TSV (tab-separated values)
* Markdown (Github-flavored, supporting tables)
* SQL (INSERT statements)

This is the list of supported commands:

* add dbtype &lt;name&gt; &lt;JDBC reference&gt; &lt;JDBC driver class name&gt; - Add a new database type
* add userdb &lt;name&gt; &lt;url&gt; &lt;user&gt; - Add a JDBC database reference
* backup database &lt;filename&gt; - Backup the preferences database to a SQL file 
* cat &lt;file&gt; - Print a local file to the console
* clear dbtype &lt;id&gt; jar - Clear the JAR reference for a database type
* connect userdb &lt;id&gt; - Connect to the specified user database
* count tables - Count the number of tables in the connected database
* csvgroup &lt;filename&gt; &lt;comma-separated list of indexes for key columns&gt; - Group together the rows in a CSV input file having the same key
* debug - Print whether we're in debug mode or not
* debug off - Disable debug mode
* debug on - Enable debug mode
* delete userdb &lt;id&gt; - Delete a user-defined database
* describe table &lt;table name&gt; - Describe a table in the connected user-database
* dir [&lt;path&gt;] - Print the directory listing for an optional directory (default is '.')
* export csv &lt;filename&gt; - Set the session to export data to CSV
* export json &lt;filename&gt; - Set the session to export data to JSON
* export markdown &lt;filename&gt; - Set the session to export data to Markdown
* export sql &lt;filename&gt; &lt;tablename&gt; -  - Set the session to export data to SQL
* export toml &lt;filename&gt; - Set the session to export data to TOML
* export tsv &lt;filename&gt; - Set the session to export data to tab-separated values
* export xml &lt;filename&gt; - Set the session to export data to XML
* export yaml &lt;filename&gt; - Set the session to export data to YAML
* fake &lt;specification&gt; - Set the session input to fake data
* gc - Run the garbage collector
* head &lt;file&gt; - Print the first 10 lines of a local file
* help - Print the list of commands
* help &lt;start of a command&gt; - Print the list of commands matching the parameter
* import db file &lt;filename&gt; - Set the session input to the SQL query in the file
* import db query &lt;query&gt; - Set the session input to the query
* import db table &lt;name&gt; - Set the session input to the database table
* jar &lt;filename&gt; - Add the JAR file to the classpath
* list dbtypes - List the known types of databases
* list schemas - List the schemas in the connected database
* list tables - List the tables in the connected database
* list userdbs - List the known user-defined databases
* mem - Print memory usage statistics
* meta username - Print the value used for the JDBC schema name in metadata calls
* meta username on - Use the username for the JDBC schema name in metadata calls
* meta username off - Use 'null' for the JDBC schema name in metadata calls
* parse fake &lt;specification&gt; - Parse the specification and print the output
* pbcopy &lt;filename&gt; - Copy a file's contents to the clipboard
* print session - Print input / output information on the session
* quit - Exit the application
* reset session - Clear out the session
* run [count] - Run the session 'count' times (default is 1)
* set dbtype &lt;id&gt; jar &lt;filename&gt; - Set the JDBC JAR driver for the database type
* time - Print the current time
* time &lt;command&gt; - Print the execution time for a command
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

```
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
```

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
a reference to the database.  The format of the command is:

    add userdb <name> <url> <username>

You will be prompted for the password.  Here is a sample command:

    add userdb Students jdbc:h2:./students sa
    
You can run the "list userdbs" command to verify it was added.
This will also give you the ID, which you'll need to extract
data from it.

### Retrieving data from a database
First, we'll need to connect to the user-defined database, using
the ID listed when running "list userdbs".  Assuming the ID for
the database is 10, enter this command:

    connect userdb 10

There are three options for setting the input for a database
query:

```
import db file <filename> - Load the query from the filename
import db query <query> - Set the query explicitly
import db table <table name> - Set the query to 'select * from <table>'
```

For this example, we'll use the second option.  Let's say
you want the input query to be 'select id, name, grade from students';
to set this as the input query in the session, enter this command:

    import db query "select id, name, grade from students"

### Setting the output file
To see the list of options for output file formats, run the command:

    help export

Here is sample output:

```
-> help export
export csv <filename>
export json <filename>
export sql <filename> <tablename>
export toml <filename>
export xml <filename>
export yaml <filename>
```

Any of the formats listed above can be used to save the query
results.  For this example, we want to save the results to JSON:

    export json results.json

To verify the session is set correctly, run this command:

    print session

This should print information consistent with what we've entered.
For example:

```
-> print session 
Source ID: 1  Type: SQL  Name: select * from STUDENTS  Data: null
Target ID: -1  Type: JSON  Name: results.json  Data: null
```

### Exporting the data
Now that the session is configured, you can run the query:

    run

This will execute the input query and save the results to the
output file.

## Generating fake data
It's often helpful to be able to produce a file with fake data,
for testing purposes, for example.  Beetle supports these types
of data fields:

| Function | Description | Sample value |
--- | --- | ---
bconst:&lt;value&gt; | Constant boolean | 'bconst:false' always produces 'false'
bool | Random boolean | true
car | Car make and model | Mini Cooper
city | City name | Albany
color | Color | Blue
country | Country | Turkey
date | Date | 04/15/1962
datetime | Date and time | 12/15/2001 21:42:16
dconst:&lt;value&gt; | Constant double | 'dconst:37.9' always produces '37.9'
double | Random double | 37.1651
email | Email address | alskd@gmail.com
firstname | First name | Alan
fullname | Full name | Jennifer Thomas
gender | Gender | male
iconst:&lt;value&gt; | Constant integer | 'iconst:17' always produces '17'
id | ID field, starts with 1 | 5
int | Random integer | 15
ipaddress | IP address | 127.25.19.250
maritalstatus | Marital status | Married
phone | Phone number | 571-295-1234
sconst:&lt;value&gt; | Constant string | 'sconst:Hello' always produces 'Hello'
ssn | Social security number | 123-45-6789
state | US state | Idaho
streetaddress | Street address | 123 Main St.
surname | Surname | Simmons
time | Time | 15:37:53
word | Word (not always pronounceable) | asdkfj
wordlist:word1:word2:word3... | One of the subsequent words | wordlist:blue:yellow can produce blue or yellow
zip | ZIP Code | 12345

The format for a specification is:

    COLNAME@function,COLNAME@function,....

The 'COLNAME' field is the name you want for that output column,
and 'function' is the function name given in the above table. 

For example, here is a sample run:

```
-> fake ID@id,STUDENT_NAME@fullname,DOB@datetime,EMAIL@email
-> export json students.json
-> print session
Source ID: -1  Type: FAKE  Name: ID@id,STUDENT_NAME@fullname,DOB@datetime,EMAIL@email  Data: null
Target ID: -1  Type: JSON  Name: students.json  Data: null
-> run 3
-> cat students.json
[
  {
    "ID": 1,
    "STUDENT_NAME": "Bertha Phelps",
    "DOB": "09/29/1900 08:14:41",
    "EMAIL": "xqvzlh@fastmail.com"
  },
  {
    "ID": 2,
    "STUDENT_NAME": "Joanna Mann",
    "DOB": "08/14/1938 20:56:35",
    "EMAIL": "yflvy@fastmail.com"
  },
  {
    "ID": 3,
    "STUDENT_NAME": "Levi Francis",
    "DOB": "06/19/1971 18:32:10",
    "EMAIL": "lriahl@lycos.com"
  }
]
```

You will normally want to run this command multiple times.  For example,
if you want 1000 rows of sample data, use the command "run 1000".  Each
row will be different.


## Grouping lines in a CSV file having the same key
When working with a CSV file, it is sometimes useful to group together the
data from those lines sharing the same key, where the key is composed of
one or more fields from each row.  This command will collapse the file
so that all rows having the same key are converted into a single row in
the output file, with the non-key fields appended to the first row having
that key value.  For example, if our input file "data.csv" has this data:

    Key3,Value1,Key1,Key2,Value2,Value3
    key-a3,val-a1,key-a1,key-a2,val-a2,val-a3
    key-a3,val-b1,key-a1,key-a2,val-b2,val-b3
    key-a3,val-c1,key-a1,key-a2,val-c2,val-c3
    key-d3,val-d1,key-d1,key-d2,val-d2,val-d3
    key-d3,val-e1,key-d1,key-d2,val-e2,val-e3

Then we can run this command:

    -> csvgroup data.csv 3,4,1
 
This tells Beetle that we want to group the data using a key of fields #3, #4
and #1.  The output is a file named "group-data.csv", and looks like this:

    Key1,Key2,Key3,Value1,Value2,Value3
    key-a1,key-a2,key-a3,val-a1,val-a2,val-a3,val-b1,val-b2,val-b3,val-c1,val-c2,val-c3
    key-d1,key-d2,key-d3,val-d1,val-d2,val-d3,val-e1,val-e2,val-e3

The grouping will rearrange the columns to match the order of the keys.
No database connection is required for this command.

This command can be helpful after creating a CSV file using a SQL Select
statement, and you want some fields grouped together.

Any header row is not treated differently than the rest of the file.
Since the values for the header's key fields are usually not duplicated
in the next line, this is generally not a problem.


## Compiling and running the application
To compile the application from the command-line, you'll need
Apache Ant installed and in the path.  Use the command:

    ant clean dist

This will generate beetle.jar.  To run it, run:

    java -jar beetle.jar

The application is entirely contained within beetle.jar.
Nothing else is needed to run Beetle.

The first time you run Beetle, it'll create the necessary database
used by the application to persist information.

## Third Party Libraries
Beetle uses the following 3rd party libraries and code:

1. [H2](http://h2database.com/) - In-memory database written in Java
1. [JLine2](https://github.com/jline/jline2) - Java library for handling console input
1. CSVParser.java - Parse CSV input, copyright Lucent Technologies
1. XXTEA.java - Encrypt and decrypt using TEA, copyright Ma Bingyao

## License
The Beetle source code is released under the MIT license.  The 3rd party source
code referenced in the previous section remains the property of their respective
copyright holders.
