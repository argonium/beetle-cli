package io.miti.beetle.console;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import io.miti.beetle.app.ArgumentParser;
import io.miti.beetle.cache.DBTypeCache;
import io.miti.beetle.cache.UserDBCache;
import io.miti.beetle.dbutil.ConnManager;
import io.miti.beetle.dbutil.Database;
import io.miti.beetle.model.ContentType;
import io.miti.beetle.model.DbType;
import io.miti.beetle.model.Session;
import io.miti.beetle.model.UserDb;
import io.miti.beetle.processor.CSVJoiner;
import io.miti.beetle.processor.DataProcessor;
import io.miti.beetle.util.Content;
import io.miti.beetle.util.FakeNode;
import io.miti.beetle.util.FakeSpecParser;
import io.miti.beetle.util.Faker;
import io.miti.beetle.util.ListFormatter;
import io.miti.beetle.util.Logger;
import io.miti.beetle.util.TimeSpan;
import io.miti.beetle.util.Utility;
import io.miti.beetle.util.FileEntry;
import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.completer.StringsCompleter;

public final class LineConsole
{
  private static List<String> supportedCommands = null;
  private static final boolean useSmartREPL = true;
  private final Session session;
  
  static {
    populateSupportedCommands();
  }


  public LineConsole() {
    super();
    session = new Session();
    loadSession();
  }
  
  
  /**
   * Start the console.
   */
  public void start() {

    // Run a REPL
    if (useSmartREPL) {
      runSmartREPL();
    } else {
      runREPL();
    }
  }


  /**
   * Use the jline2 library for reading from the console.
   */
  public void runSmartREPL() {

    try {
      // Instantiate the console reader for the JLine2 library
      ConsoleReader console = new ConsoleReader();

      // Set the prompt
      setPrompt(console);

      // Add string completion for known commands
      final Collection<String> cmds = getSupportedCommandsForCompletion();
      console.addCompleter(new StringsCompleter(cmds));

      // Keep reading lines until we need to stop
      String line = null;
      while ((line = console.readLine()) != null) {
        if (!processCommand(line, console)) {
          // The user wants to quit, so break out of the loop
          break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        TerminalFactory.get().restore();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
  
  
  public void setSession(final Session pSession) {
    session.setId(pSession.getId());
    session.setSourceData(pSession.getSourceData());
    session.setSourceId(pSession.getSourceId());
    session.setSourceName(pSession.getSourceName());
    session.setSourceTypeId(pSession.getSourceTypeId());
    session.setTargetData(pSession.getTargetData());
    session.setTargetId(pSession.getTargetId());
    session.setTargetName(pSession.getTargetName());
    session.setTargetTypeId(pSession.getTargetTypeId());
  }


  /**
   * Return the list of strings to use for command completion by jline2.
   * 
   * @return the list of command strings
   */
  private Collection<String> getSupportedCommandsForCompletion() {

    Set<String> cmds = new HashSet<String>(supportedCommands.size());
    for (String cmd : supportedCommands) {

      // Save the index of starting characters [ and <
      final int angleIndex = cmd.indexOf('<');
      final int bracketIndex = cmd.indexOf('[');

      if ((angleIndex < 0) && (bracketIndex < 0)) {
        cmds.add(cmd.trim());
      } else if (angleIndex < 0) {
        cmds.add(cmd.substring(0, bracketIndex - 1).trim());
      } else if (bracketIndex < 0) {
        cmds.add(cmd.substring(0, angleIndex - 1).trim());
      } else {
        cmds.add(cmd.substring(0, Math.min(angleIndex, bracketIndex) - 1)
            .trim());
      }
    }

    return cmds;
  }


  /**
   * Use a simple command line interpreter.
   */
  public void runREPL() {

    // Get the system console, for reading input
    final Console console = System.console();
    while (true) {
      // Read the command and process it
      String input = console.readLine("~> ");
      if (!processCommand(input, null)) {
        // The user wants to quit, so break out of the loop
        break;
      }
    }
  }


  /**
   * Process the input command. Return false if we should exit.
   * 
   * @param input the input command
   * @return whether to continue processing
   */
  private boolean processCommand(final String input, final ConsoleReader console) {

    // Check the input
    if (input == null) {
      // EOF sent
      return false;
    } else if (input.trim().isEmpty()) {
      // Stop processing of the command
      return true;
    }

    // Parse the input command into a list of strings (supports quotes)
    final List<String> cmds = new LineParser().parseIntoPhrases(input);

    // Process the command entered by the user
    final String line = input.trim();
    if (line.equals("quit") || line.equals("exit")) {
      saveSession();
      System.out.println("Shutting down");
      System.exit(0);
    } else if (line.equals("debug on")) {
      Logger.updateLogLevel(1);
      System.out.println("Debug is on");
    } else if (line.equals("debug off")) {
      Logger.updateLogLevel(3);
      System.out.println("Debug is off");
    } else if (line.equals("debug")) {
      int logLevel = Logger.getLogLevel();
      System.out.println("Debug is " + ((logLevel == 1) ? "on" : "off"));
    } else if (line.equals("help")) {
      printHelp();
    } else if (validateCommand(cmds, 2, "help")) {
      printHelp(cmds.get(1));
    } else if (validateCommand(cmds, 2, "list", "dbtypes")) {
      listDatabaseTypes();
    } else if (validateCommand(cmds, 5, "add", "dbtype")) {
      addDBType(cmds.get(2), cmds.get(3), cmds.get(4));
    } else if (validateCommand(cmds, 2, "list", "userdbs")) {
      listUserDatabases();
    } else if (validateCommand(cmds, 5, "add", "userdb")) {
      addUserDatabase(cmds, console);
    } else if (validateCommand(cmds, 3, "delete", "userdb")) {
      deleteUserDatabase(cmds.get(2));
    } else if (validateCommand(cmds, 3, "connect", "userdb")) {
      connectUserDatabase(cmds.get(2));
    } else if (validateCommand(cmds, 2, "meta", "username")) {
      System.out.println("Using the username for database metadata schema name? " + Database.useUserNameForSchema());
    } else if (validateCommand(cmds, 3, "meta", "username", "on")) {
      Database.useUserNameForSchema(true);
    } else if (validateCommand(cmds, 3, "meta", "username", "off")) {
      Database.useUserNameForSchema(false);
    } else if (validateCommand(cmds, 4, "import", "db", "table")) {
      importUserTable(cmds.get(3));
    } else if (validateCommand(cmds, 4, "import", "db", "query")) {
      importUserQuery(cmds.get(3));
    } else if (validateCommand(cmds, 4, "import", "db", "file")) {
      importUserFile(cmds.get(3));
    } else if (validateCommand(cmds, 2, "fake")) {
      generateFakeData(cmds.get(1));
    } else if (validateCommand(cmds, 3, "parse", "fake")) {
      parseFakeSpec(cmds.get(2));
    } else if (validateCommand(cmds, 2, "pbcopy")) {
      pbCopy(cmds.get(1));
    } else if (validateCommand(cmds, 3, "export", "csv")) {
      exportCSV(cmds.get(2));
    } else if (validateCommand(cmds, 3, "export", "json")) {
      exportJSON(cmds.get(2));
    } else if (validateCommand(cmds, 3, "export", "toml")) {
      exportTOML(cmds.get(2));
    } else if (validateCommand(cmds, 4, "export", "sql")) {
      exportSQLFile(cmds.get(2), cmds.get(3));
    } else if (validateCommand(cmds, 3, "export", "yaml")) {
      exportYAML(cmds.get(2));
    } else if (validateCommand(cmds, 3, "export", "xml")) {
      exportXML(cmds.get(2));
    } else if (validateCommand(cmds, 5, "set", "dbtype", null, "jar")) {
      setDBTypeJar(cmds.get(2), cmds.get(4));
    } else if (validateCommand(cmds, 4, "clear", "dbtype", null, "jar")) {
      clearDBTypeJar(cmds.get(2));
    } else if (validateCommand(cmds, 2, "print", "session")) {
      printSession();
    } else if (validateCommand(cmds, 2, "reset", "session")) {
      resetSession();
    } else if (validateCommand(cmds, 3, "select", "schema")) {
      selectSchema(cmds.get(2));
    } else if (line.equals("run")) {
      runSession();
    } else if (validateCommand(cmds, 2, "run")) {
      runSession(cmds.get(1));
    } else if (validateCommand(cmds, 2, "jar")) {
      loadJar(cmds.get(1));
    } else if (validateCommand(cmds, 3, "describe", "table")) {
      describeTable(cmds.get(2));
    } else if (validateCommand(cmds, 2, "list", "tables")) {
      printTables();
    } else if (validateCommand(cmds, 3, "csvgroup")) {
      groupCSVFile(cmds.get(1), cmds.get(2));
    } else if (line.equals("dir")) {
      printDirPath(".", false);
    } else if (validateCommand(cmds, 2, "dir")) {
      printDirPath(cmds.get(1), false);
    } else if (validateCommand(cmds, 2, "list", "schemas")) {
      printSchemas();
    } else if (line.equals("time")) {
      printTime();
    } else if (line.startsWith("time ")) {
      timeCommand(line.substring(5), console);
    } else if (validateCommand(cmds, 2, "count", "tables")) {
      countTables();
//    } else if (validateCommand(cmds, 3, "count", "rows")) {
//      countTableRows(cmds.get(2));
    } else if (line.equals("gc")) {
      gc();
    } else if (line.equals("mem")) {
      mem();
    } else if (validateCommand(cmds, 2, "cat")) {
      catFile(cmds.get(1));
    } else if (validateCommand(cmds, 2, "head")) {
      headFile(cmds.get(1));
    } else if (line.equals("version")) {
      ver();
    } else if (!line.startsWith("-")) {
      // It's not a comment, so it's an unknown command
      System.out.println("Unknown command");
    }

    return true;
  }
  
  
  /**
   * Select a specific schema.
   * 
   * @param schema the schema name
   */
  private void selectSchema(final String schema) {
    
    // Find the user DB with the specified ID
    final UserDb userDb = UserDBCache.get().find(session.getSourceId());
    if (userDb == null) {
      Logger.error("Error: Invalid database ID in the session");
      return;
    }
    
    // Make sure the JDBC DB's driver class is loaded
    final DbType dbType = DBTypeCache.get().find(userDb.getDbTypeId());
    ConnManager.get().addDriverClass(dbType);
    
    // Open a connection to the database
    // TODO Verify this doesn't break anything.  Change create() to getConn()?
    ConnManager.get().init(userDb.getUrl(), userDb.getUserId(), userDb.getUserPw());
    if (!ConnManager.get().create()) {
      Logger.error("Unable to connect to database " + userDb.getUrl());
      return;
    }
    
    // Check the DB connection
    if (!ConnManager.get().isValid()) {
      System.out.println("No database connection found");
    } else {
      boolean rc = ConnManager.connectToSchema(schema, ConnManager.get().getConn());
      if (!rc) {
        Logger.error("Unable to connect to schema");
      }
    }
  }


  /**
   * Group the data in a CSV file.
   * 
   * @param fname the name of the input file
   * @param keyList the list of key field IDs (1-based)
   */
  private void groupCSVFile(final String fname, final String keyList) {
    // Get the input file
    final File file = new File(fname);
    if (!file.exists()) {
      System.out.println("Input file not found");
      return;
    } else if (file.isDirectory()) {
      System.out.println("Input must be a file, not a directory");
      return;
    }
    
    // Set the name of the output file
    final String outfname = "group-" + file.getName();
    final File outputFile = new File(outfname);
    
    // Get the list of keys (0-based)
    final int keys[] = parseKeyList(keyList);
    if (keys == null) {
      System.out.println("Invalid key list");
      return;
    }
    
    // Call a method to group the data
    new CSVJoiner().join(file, outputFile, keys);
    System.out.println("Output written to " + outfname);
  }
  
  
  /**
   * Parse a string into a list of integers, changing from 1-based
   * to 0-based.
   * 
   * @param keyList a list of IDs (e.g., "1,2,5")
   * @return the IDs as an array
   */
  private int[] parseKeyList(final String keyList) {
    
    // Parse the list
    List<Integer> list = new ArrayList<Integer>(5);
    StringTokenizer st = new StringTokenizer(keyList, ",");
    boolean result = true;
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      final int val = Utility.getStringAsInteger(token, -1, -1);
      if ((val < 1) || (val > 100)) {
        result = false;
        break;
      }
      list.add(Integer.valueOf(val));
    }
    
    // Check for an error
    if (!result) {
      return null;
    }
    
    // Convert the list to an array
    Logger.debug("Found " + list.size() + " key IDs");
    final int keys[] = new int[list.size()];
    int index = 0;
    for (Integer key : list) {
      keys[index++] = key.intValue() - 1;
    }
    
    // Debugging
    final int len = keys.length;
    for (int i = 0; i < len; ++i) {
      Logger.debug("Key[" + i + "] = " + keys[i]);
    }
    
    return keys;
  }


  private void pbCopy(final String fname) {
    // Verify the file exists
    final File file = new File(fname);
    if (!file.exists() || !file.isFile()) {
      Logger.error("The input file was not found");
      return;
    }
    
    // Get a path variable to the file
    final Path path = Paths.get(fname);
    StringSelection text = null;
    try {
      // Read the file contents into a byte array
      final byte[] bytes = Files.readAllBytes(path);
      
      // Create a string selection from the text
      text = new StringSelection(new String(bytes, StandardCharsets.UTF_8));
    } catch (IOException e) {
      Logger.error(e);
      text = null;
    }
    
    // Verify we read the file contents OK
    if (text != null) {
      // Copy the file text to the clipboard
      final Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
      clpbrd.setContents(text, null);
    }
  }
  
  
  private void addDBType(final String name, final String ref, final String driver) {
    // Add the database type to the cache
    DBTypeCache.get().add(name, ref, driver);
  }


  private void parseFakeSpec(final String spec) {
    final FakeSpecParser fsp = new FakeSpecParser();
    final boolean rc = fsp.parse(spec);
    System.out.println("The parsing was " + (rc ? "" : "un") + "successful");
    final List<FakeNode> nodes = fsp.getNodes();
    if (nodes == null) {
      System.out.println("The list of nodes is null");
    } else if (nodes.isEmpty()) {
      System.out.println("The list of nodes is empty");
    } else {
      // Get the data as a list of list of strings
      final List<List<String>> table = fsp.getNodesAsListList();

      // Format and print the list
      ListFormatter fmt = new ListFormatter(table);
      List<String> fmtd = fmt.format(3, table);
      System.out.print(ListFormatter.getTextLine(fmtd));
    }
  }
  
  
  private void resetSession() {
    session.reset();
  }
  
  
  private void printSession() {
    String line1 = String.format("Source ID: %d  Type: %s  Name: %s  Data: %s",
        session.getSourceId(), ContentType.getById(session.getSourceTypeId()), session.getSourceName(), session.getSourceData());
    String line2 = String.format("Target ID: %d  Type: %s  Name: %s  Data: %s",
        session.getTargetId(), ContentType.getById(session.getTargetTypeId()), session.getTargetName(), session.getTargetData());
    
    System.out.println(line1);
    System.out.println(line2);
  }
  
  
  private void runSession() {
    new DataProcessor(session).run();
  }
  
  
  private void runSession(final String sRunCount) {
    // Get the number of times to run (only used for fake data)
    final int nRunCount = Utility.getStringAsInteger(sRunCount, -1, -1);
    if (nRunCount < 1) {
      Logger.error("The run count must be at least 1");
      return;
    }
    
    // Reset the ID and incrementor fields
    Faker.setId(1, 1);
    
    // Run the data processor
    final DataProcessor dp = new DataProcessor(session);
    dp.setRunCount(nRunCount);
    dp.run();
  }
  
  
  private void exportJSON(final String filename) {
    session.setTargetTypeId(ContentType.JSON.getId());
    session.setTargetName(filename);
  }
  
  
  private void exportCSV(final String filename) {
    session.setTargetTypeId(ContentType.CSV.getId());
    session.setTargetName(filename);
  }
  
  
  private void exportTOML(final String filename) {
    session.setTargetTypeId(ContentType.TOML.getId());
    session.setTargetName(filename);
  }
  
  
  private void exportSQLFile(final String filename, final String tableName) {
    session.setTargetTypeId(ContentType.SQL_FILE.getId());
    session.setTargetName(filename);
    session.setTargetData(tableName);
  }
  
  
  private void exportYAML(final String filename) {
    session.setTargetTypeId(ContentType.YAML.getId());
    session.setTargetName(filename);
  }
  
  
  private void exportXML(final String filename) {
    session.setTargetTypeId(ContentType.XML.getId());
    session.setTargetName(filename);
  }


  private void importUserTable(final String tname) {
    session.setSourceTypeId(ContentType.SQL.getId());
    session.setSourceName("select * from " + tname);
  }
  
  
  private void generateFakeData(final String spec) {
    session.setSourceTypeId(ContentType.FAKE.getId());
    session.setSourceName(spec);
  }
  
  
  private void importUserFile(final String fname) {
    // Open the specified file
    final File file = new File(fname);
    if (!file.exists() || !file.isFile()) {
      Logger.error("The specified file was not found");
      return;
    }
    
    // Read the contents
    String text = Content.getFileAsText(file);
    if ((text == null) || text.isEmpty()) {
      Logger.error("No contents found in file");
      return;
    }
    
    // Verify this is a 'select' statement
    final String testQuery = text.trim().toLowerCase();
    if (!testQuery.startsWith("select ")) {
      Logger.error("Only SELECT statements are allowed");
      return;
    }
    
    // Save the query
    session.setSourceTypeId(ContentType.SQL.getId());
    session.setSourceName(text);
  }
  
  
  private void importUserQuery(final String query) {
    
    // Check there is a query
    if ((query == null) || query.trim().isEmpty()) {
      Logger.error("The query is null or empty");
      return;
    }
    
    // Verify this is a 'select' statement
    final String testQuery = query.trim().toLowerCase();
    if (!testQuery.startsWith("select ")) {
      Logger.error("Only SELECT statements are allowed");
      return;
    }
    
    // Save the query
    session.setSourceTypeId(ContentType.SQL.getId());
    session.setSourceName(query);
  }
  
  
  private void connectUserDatabase(final String dbId) {

    // Get the numeric ID
    int id = Utility.getStringAsInteger(dbId, -1, -1);
    if (id < 0) {
      System.err.println("Error: Invalid ID specified");
      return;
    }

    // Find a match (by ID)
    final UserDb userDb = UserDBCache.get().find(id);
    if (userDb == null) {
      System.out.println("No match found");
    } else {
      // If the DB type is configured with a valid JAR file, save
      // the user DB ID in the session
      if (DBTypeCache.get().isValid(userDb.getDbTypeId())) {
        session.setSourceTypeId(ContentType.SQL.getId());
        session.setSourceId(id);
      } else {
        System.out.println("No DB type found for ID " + userDb.getDbTypeId());
      }
    }
  }
  
  
  private void deleteUserDatabase(final String dbId) {
    // Get the numeric ID
    int id = Utility.getStringAsInteger(dbId, -1, -1);
    if (id < 0) {
      System.err.println("Error: Invalid ID specified");
      return;
    }

    // Delete the object (if found)
    final boolean result = UserDBCache.get().remove(id);
    if (result) {
      System.out.println("User database reference deleted");
    } else {
      System.err.println("No matching reference found");
    }
  }


  private void addUserDatabase(final List<String> cmds,
      final ConsoleReader console) {
    // Format: add userdb <name> <URL> <user ID>
    final String dbName = cmds.get(2);
    final String url = cmds.get(3);
    final String userId = cmds.get(4);

    // Prompt for the password
    final String passwd = getPassword(console);

    // Create the object for the database
    final UserDb userDb = new UserDb(dbName, url, userId, passwd);

    // Save the object to the database and update the cache
    userDb.insert();
    UserDBCache.get().add(userDb);

    System.out.println("User database created.  ID = " + userDb.getId());
  }


  private void clearDBTypeJar(final String sTypeID) {
    // Get the numeric ID
    int id = Utility.getStringAsInteger(sTypeID, -1, -1);
    if (id < 0) {
      System.err.println("Error: Invalid ID specified");
      return;
    }

    // Clear the jar setting for the object
    final boolean result = DBTypeCache.get().clearJar(id);
    if (result) {
      System.out.println("DBType row updated");
    } else {
      System.err.println("Error occurred");
    }
  }


  private void setDBTypeJar(final String sTypeID, final String jarName) {
    // Get the numeric ID
    int id = Utility.getStringAsInteger(sTypeID, -1, -1);
    if (id < 0) {
      System.err.println("Error: Invalid ID specified");
      return;
    }

    // Check the file
    File file = new File(jarName);
    if (file.isDirectory() || !file.exists()) {
      System.err.println("Error: The JAR file was not found");
      return;
    }

    // Save the jar name and update the database
    DbType match = DBTypeCache.get().find(id);
    if (match != null) {
      match.setJarName(jarName);
      if (!match.update()) {
        System.err.println("Error updating the table");
      }
      System.out.println("DBType row updated");
    } else {
      System.err.println("No matching DBType found");
    }
  }


  private void listUserDatabases() {
    List<UserDb> list = UserDBCache.get().getList();
    if ((list == null) || list.isEmpty()) {
      System.out.println("No user databases found");
    } else {
      // Print the list
      String table = new ListFormatter().getTable(list, 3,
          new String[] { "id", "dbName", "url", "userId" },
          new String[] { "ID", "Name", "URL", "User" });
      System.out.print(table);
    }
  }


  private void listDatabaseTypes() {
    List<DbType> list = DBTypeCache.get().getList();
    if ((list == null) || list.isEmpty()) {
      System.out.println("No database supported");
    } else {
      // Print the list
      String table = new ListFormatter().getTable(list, 3,
          new String[] { "id", "name", "driver", "jarName" },
          new String[] { "ID", "Name", "Class", "Jar File" });
      System.out.print(table);
    }
  }


  private void timeCommand(final String cmdToTime, final ConsoleReader console) {
    // Record the starting time
    final long start = System.currentTimeMillis();

    // Process the command
    processCommand(cmdToTime, console);

    // Compute the string describing the elapsed time span
    final long end = System.currentTimeMillis();
    final String span = TimeSpan.millisToTimeSpan(end - start);

    // Print the time string
    System.out.println("Elapsed Time: " + span);
  }


  /**
   * Print the first 10 lines of a file.
   * 
   * @param fname the filename
   */
  public void headFile(final String fname) {
    printLocalFile(fname, 10);
  }


  /**
   * Print a file.
   * 
   * @param fname the filename
   */
  public void catFile(final String fname) {
    printLocalFile(fname, -1);
  }


  /**
   * Print some number of lines of a file.
   * 
   * @param source the filename
   * @param rowCount the number of rows to print
   */
  private void printLocalFile(final String source, final int rowCount) {

    // Open the file
    final File file = new File(source);
    if (!file.exists()) {
      System.out.println("Error: The file does not exist");
    } else if (!file.isFile()) {
      System.out.println("Error: The input file is a directory");
    } else {
      // Read the file
      final String text = Content.getFileAsText(file, rowCount);
      if (text.isEmpty()) {
        System.out.println("The file is empty");
      } else {
        // See if the file ends with a newline or carriage return.
        // If it does, just print it. Else, println it.
        char lastChar = text.charAt(text.length() - 1);
        final boolean endsWithNL = ((lastChar == '\r') || (lastChar == '\n'));
        if (endsWithNL) {
          System.out.print(text);
        } else {
          System.out.println(text);
        }
      }
    }
  }
  
  
  public void describeTable(final String table) {
    
    // Find the user DB with the specified ID
    final UserDb userDb = UserDBCache.get().find(session.getSourceId());
    if (userDb == null) {
      Logger.error("Error: Invalid database ID in the session");
      return;
    }
    
    // Make sure the JDBC DB's driver class is loaded
    final DbType dbType = DBTypeCache.get().find(userDb.getDbTypeId());
    ConnManager.get().addDriverClass(dbType);
    
    // Open a connection to the database
    ConnManager.get().init(userDb.getUrl(), userDb.getUserId(), userDb.getUserPw());
    if (!ConnManager.get().create()) {
      Logger.error("Unable to connect to database " + userDb.getUrl());
      return;
    }
    
    // Check the DB connection
    if (!ConnManager.get().isValid()) {
      System.out.println("No database connection found");
    } else {
      // Get the info
      List<List<String>> results = Database.getColumns(table, true);
      if (results == null) {
        System.out.println("No table description found (result is null)");
        return;
      } else if (results.size() < 1) {
        System.out.println("No table description found");
        return;
      }

      // Add a header row
      List<String> labels = new ArrayList<String>(5);
      labels.add("#");
      labels.add("Name");
      labels.add("Type");
      labels.add("Nullable?");
      labels.add("PK?");
      results.add(0, labels);

      // Format and print the list
      ListFormatter fmt = new ListFormatter(results);
      List<String> fmtd = fmt.format(5, results);
      System.out.print(ListFormatter.getTextLine(fmtd));
    }
    
    ConnManager.get().close();
  }


  /**
   * Get a directory listing.
   * 
   * @param path the directory path
   */
  public void printDirPath(final String path, final boolean useLongPath) {
    final File dir = new File(path);
    final File[] files = dir.listFiles();
    if ((files == null) || (files.length < 1)) {
      System.out.println("No files found in the directory");
      return;
    }

    // Convert the list into another list, for better formatting
    List<FileEntry> listing = new ArrayList<FileEntry>(files.length);
    for (File file : files) {
      final boolean isDir = file.isDirectory();
      final String fname = getFileName(file, useLongPath);
      listing.add(new FileEntry(isDir, fname, (isDir) ? 0 : file.length(), file
          .lastModified()));
    }

    // Print the table
    final String table = new ListFormatter().getTable(listing, new String[] {
        "name", "len", "lastModified" }, new String[] { "Name", "Size",
        "Last Modified" });
    System.out.print(table);
  }


  /**
   * Get the canonical version of a file name.
   * 
   * @param file the file
   * @param useLongPath whether to get the full path of the file
   * @return the file name
   */
  private static String getFileName(final File file, final boolean useLongPath) {
    if (!useLongPath) {
      return file.getName();
    }

    String name = null;
    try {
      name = file.getCanonicalPath();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return name;
  }


  // Get the list of database tables
  private void printTables() {

    // Check the database connection
    if (session.getSourceId() < 0) {
      System.out.println("No database connection found");
      return;
    }
    
    // Find the user DB with the specified ID
    final UserDb userDb = UserDBCache.get().find(session.getSourceId());
    if (userDb == null) {
      Logger.error("Error: Invalid database ID in the session");
      return;
    }
    
    // Make sure the JDBC DB's driver class is loaded
    final DbType dbType = DBTypeCache.get().find(userDb.getDbTypeId());
    ConnManager.get().addDriverClass(dbType);
    
    // Open a connection to the database
    ConnManager.get().init(userDb.getUrl(), userDb.getUserId(), userDb.getUserPw());
    if (!ConnManager.get().create()) {
      Logger.error("Unable to connect to database " + userDb.getUrl());
      return;
    }
    
    // Get the list of tables in the session schema
    final List<String> tables = Database.getTableNames(null);
    if (tables == null) {
      System.out.println("No tables were found (list is null)");
    } else if (tables.size() < 1) {
      System.out.println("No tables were found");
    } else {
      // Print out the list of database tables
      Collections.sort(tables);
      for (String table : tables) {
        System.out.println(table);
      }
    }
  }


  /**
   * Print the number of database tables.
   */
  private void countTables() {

    // Check the database connection
    if (session.getSourceId() < 0) {
      System.out.println("No database connection found");
      return;
    }
    
    // Find the user DB with the specified ID
    final UserDb userDb = UserDBCache.get().find(session.getSourceId());
    if (userDb == null) {
      Logger.error("Error: Invalid database ID in the session");
      return;
    }
    
    // Make sure the JDBC DB's driver class is loaded
    final DbType dbType = DBTypeCache.get().find(userDb.getDbTypeId());
    ConnManager.get().addDriverClass(dbType);
    
    // Open a connection to the database
    ConnManager.get().init(userDb.getUrl(), userDb.getUserId(), userDb.getUserPw());
    if (!ConnManager.get().create()) {
      Logger.error("Unable to connect to database " + userDb.getUrl());
      return;
    }
    
    // Get the list of tables in the session schema
    final List<String> tables = Database.getTableNames(null);
    if (tables == null) {
      System.out.println("No tables were found (list is null)");
    } else if (tables.size() < 1) {
      System.out.println("No tables were found");
    } else {
      System.out.println("Number of database tables: " + tables.size());
    }
  }


  /**
   * Print the list of schemas.
   */
  private void printSchemas() {

    // Check the database connection
    if (session.getSourceId() < 0) {
      System.out.println("No database connection found");
      return;
    }
    
    // Find the user DB with the specified ID
    final UserDb userDb = UserDBCache.get().find(session.getSourceId());
    if (userDb == null) {
      Logger.error("Error: Invalid database ID in the session");
      return;
    }
    
    // Make sure the JDBC DB's driver class is loaded
    final DbType dbType = DBTypeCache.get().find(userDb.getDbTypeId());
    ConnManager.get().addDriverClass(dbType);
    
    // Open a connection to the database
    ConnManager.get().init(userDb.getUrl(), userDb.getUserId(), userDb.getUserPw());
    if (!ConnManager.get().create()) {
      Logger.error("Unable to connect to database " + userDb.getUrl());
      return;
    }
    
    // Get the list of schemas
    final List<String> schemas = Database.getSchemas();
    if (schemas == null) {
      System.out.println("No schemas were found (list is null)");
    } else if (schemas.size() < 1) {
      System.out.println("No schemas were found");
    } else {
      // Print out the list of database schemas
      Collections.sort(schemas);
      for (String schema : schemas) {
        System.out.println(schema);
      }
    }
  }


  private void loadJar(final String jarName) {
    // Confirm the filename
    if (!jarName.toLowerCase().endsWith(".jar")) {
      System.out.println("Only JAR files can be added to the class path");
    } else {
      final File file = new File(jarName);
      if (!file.exists()) {
        System.out.println("The JAR file could not be found");
      } else if (!file.isFile()) {
        System.out.println("The specified directory cannot be added to the class path");
      } else {
        // Add the jar to the class path
        try {
          final URL urlJar = file.toURI().toURL();
          try {
            ConnManager.addURL(urlJar);
          } catch (IOException e) {
            Logger.error(e);
          }
          System.out.println("Successful");
        } catch (MalformedURLException e) {
          System.out.println("Exception: " + e.getMessage());
        }
      }
    }
  }


  private void ver() {
    System.out.println(ArgumentParser.VER_ROOT_STR);
  }


  private String getPassword(final ConsoleReader console) {
    if (console == null) {
      final char[] pw = System.console().readPassword("Password: ");
      return new String(pw);
    }

    console.setPrompt("Password: ");
    String pw = null;
    try {
      pw = console.readLine('*');
    } catch (IOException e) {
      System.out.println("Exception while reading PW: " + e.getMessage());
    }

    setPrompt(console);
    return pw;
  }


  private void setPrompt(final ConsoleReader console) {
    if (console != null) {
      console.setPrompt("-> ");
    }
  }


  /**
   * Print the current time.
   */
  private void printTime() {
    final String dateStr = Utility.getDateTimeString();
    System.out.println("Current date/time: " + dateStr);
  }


  /**
   * Print memory usage information.
   */
  private void mem() {

    // Save the memory statistics
    final long freeMem = Runtime.getRuntime().freeMemory();
    final long maxMem = Runtime.getRuntime().maxMemory();
    final long totalMem = Runtime.getRuntime().totalMemory();

    // Print the data
    System.out.println("Free memory:  " + Utility.formatLong(freeMem));
    System.out.println("Max memory:   " + Utility.formatLong(maxMem));
    System.out.println("Total memory: " + Utility.formatLong(totalMem));
  }


  /**
   * Run the Java garbage collector.
   */
  private void gc() {
    System.gc();
    System.gc();
    System.gc();
  }


  /**
   * Validate a command entered by the user.
   * 
   * @param cmds the list of strings entered by the user
   * @param numCmds the expected number of fields
   * @param fields the known field values to validate
   * @return whether the command is valid
   */
  private static boolean validateCommand(final List<String> cmds,
      final int numCmds, final String... fields) {

    // Check the inputs
    if ((cmds == null) || cmds.isEmpty()) {
      return false;
    } else if (cmds.size() != numCmds) {
      return false;
    } else if (fields == null) {
      return false;
    }

    final int numFields = fields.length;
    if ((numFields < 1) || (numFields > numCmds)) {
      return false;
    }

    boolean result = true;
    for (int i = 0; i < numFields; ++i) {
      if (cmds.get(i) == null) {
        result = false;
        break;
      } else if (fields[i] == null) {
        continue;
      }

      if (!cmds.get(i).equals(fields[i])) {
        result = false;
        break;
      }
    }

    return result;
  }


  /**
   * Print the list of commands that start with the supplied filter.
   * 
   * @param filter the start of commands we want to print
   */
  private void printHelp(final String filter) {

    final List<String> cmds = new ArrayList<String>(20);
    for (String cmd : supportedCommands) {
      if (cmd.startsWith(filter)) {
        cmds.add(cmd);
      }
    }

    if (cmds.isEmpty()) {
      System.out.println("No matching commands found");
    } else {
      printList(cmds);
    }
  }


  /**
   * Save the list of supported commands.
   */
  private static void populateSupportedCommands() {

    final String[] array = new String[] { "debug", "debug off", "debug on",
        "help", "quit", "gc", "mem", "time", "version", "time <command>",
        "cat <file>", "head <file>", "dir [<path>]", "list userdbs",
        "list dbtypes", "set dbtype <id> jar <filename>", "import db query <query>",
        "clear dbtype <id> jar", "add userdb <name> <url> <user>",
        "meta username", "meta username on", "meta username off",
        "delete userdb <id>", "connect userdb <id>", "import db table <name>",
        "export csv <filename>", "export json <filename>", "pbcopy <filename>",
        "add dbtype <Name> <JDBC reference> <JDBC driver class name>",
        "export toml <filename>", "export yaml <filename>", "export xml <filename>",
        "print session", "reset session", "run [count]", "import db file <filename>",
        "list tables", "describe table <table name>", "fake <specification>",
        "parse fake <specification>", "export sql <filename> <tablename>",
        "help <start of a command>", "jar <filename>", "list schemas",
        "csvgroup <filename> <list of key field IDs>", "count tables",
        "select schema <schema name>" };
    
    supportedCommands = Arrays.asList(array);
    Collections.sort(supportedCommands);
  }
  
  
  private void loadSession() {
    // Get the list of sessions from the database (should be zero or one)
    List<Session> list = Session.getList();
    
    // If none found, return
    if ((list == null) || list.isEmpty()) {
      return;
    }
    
    // Save the session from the database to the object in memory
    setSession(list.get(0));
  }
  
  
  private void saveSession() {
    // Only keep one session in the database for now, so
    // delete any in the database first
    Session.delete();
    
    // Save the current one
    session.insert();
  }


  /**
   * Print a list of strings.
   * 
   * @param list the list to print
   */
  private static void printList(final List<String> list) {
    for (String item : list) {
      System.out.println(item);
    }
  }


  /**
   * Print the list of supported commands.
   */
  private void printHelp() {
    printList(supportedCommands);
  }
}
