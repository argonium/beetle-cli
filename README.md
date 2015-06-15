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
