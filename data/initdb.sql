-- List of supported database vendors
create table if not exists db_type (
	id int auto_increment not null,
	name varchar2(20) not null,
	ref varchar2(100) not null,
	driver varchar2(100) not null,
	jar_name varchar2(255) default null
);

-- User preferences (key/value pairs)
create table if not exists app_property (
	key varchar2(200),
	value varchar2(200)
);

-- Instances of databases (db_type data)
create table if not exists user_db (
	id int auto_increment not null,
	db_name varchar2(100) not null,
	url varchar2(200) not null,
	user_id varchar2(100),
	user_pw varchar2(300),
	db_type_id int not null
);

-- Store the required database version number
create table if not exists config (
	db_version int not null
);

-- Store saved Hadoop config information
create table if not exists hadoop (
	id int auto_increment not null,
	label varchar2(100) not null,
	name varchar2(200) not null,
	url varchar2(200) not null
);

-- Store a session
create table if not exists session (
	-- id int auto_increment not null,
	source_type_id int not null,
	target_type_id int not null,
	source_id int not null,
	target_id int not null,
	source_name varchar2(100),
	target_name varchar2(100),
	source_delim varchar2(10),
	target_delim varchar2(10)
);

-- Set the support data types (SQL, Hadoop, CSV)
create table if not exists data_type (
	id int not null,
	name varchar2(100) not null
);

-- Store the output header columns for a session
create table if not exists header (
	id int auto_increment not null,
	session_id int not null,
	col_name varchar2(100) not null,
	col_type_id int not null,
	date_format varchar2(100)
);

-- Store column types for headers
create table if not exists col_type (
	id int auto_increment not null,
	is_str bool,
	is_num bool,
	is_bool bool,
	is_date bool,
	type_name varchar2(50)
);

-- Inserts for col_type
insert into col_type (is_str, is_num, is_bool, is_date, type_name)
	values (true, false, false, false, 'String');
insert into col_type (is_str, is_num, is_bool, is_date, type_name)
	values (false, true, false, false, 'Number');
insert into col_type (is_str, is_num, is_bool, is_date, type_name)
	values (false, false, true, false, 'Boolean');
insert into col_type (is_str, is_num, is_bool, is_date, type_name)
	values (false, false, false, true, 'Date');

-- Add data types (sources or targets for data)
insert into data_type (id, name) values (1, 'SQL');
insert into data_type (id, name) values (2, 'SQL File');
insert into data_type (id, name) values (3, 'Hadoop');
insert into data_type (id, name) values (4, 'CSV');
insert into data_type (id, name) values (5, 'JSON');
insert into data_type (id, name) values (6, 'YAML');
insert into data_type (id, name) values (7, 'Text');

-- Add the supported database vendors
insert into db_type (name, ref, driver) values
	('Derby', 'derby', 'org.apache.derby.jdbc.EmbeddedDriver');
insert into db_type (name, ref, driver) values
	('H2', 'h2', 'org.h2.Driver');
insert into db_type (name, ref, driver) values
	('MySQL', 'mysql', 'com.mysql.jdbc.Driver');
insert into db_type (name, ref, driver) values
	('MariaDB', 'mariadb', 'org.mariadb.jdbc.Driver');
insert into db_type (name, ref, driver) values
	('Oracle', 'oracle', 'oracle.jdbc.OracleDriver');
insert into db_type (name, ref, driver) values
	('Hive', 'hive', 'org.apache.hadoop.hive.jdbc.HiveDriver');
insert into db_type (name, ref, driver) values
	('Neo4J', 'neo4j', 'org.neo4j.jdbc.Driver');
insert into db_type (name, ref, driver) values
	('PostgreSQL', 'postgresql', 'org.postgresql.Driver');
insert into db_type (name, ref, driver) values
	('DB2', 'db2', 'com.ibm.db2.jcc.DB2Driver');
insert into db_type (name, ref, driver) values
	('Redis', 'redis', 'br.com.svvs.jdbc.redis.RedisDriver');
insert into db_type (name, ref, driver) values
	('Cassandra', 'cassandra', 'org.apache.cassandra.cql.jdbc.CassandraDriver');
insert into db_type (name, ref, driver) values
	('MongoDB', 'mongo', 'mongodb.jdbc.MongoDriver');
insert into db_type (name, ref, driver) values
	('HBase', 'phoenix', 'org.apache.phoenix.jdbc.PhoenixDriver');
insert into db_type (name, ref, driver) values
	('SQL Server', 'sqlserver', 'com.microsoft.sqlserver.jdbc.SQLServerDriver');
insert into db_type (name, ref, driver) values
	('SQLite', 'sqlite', 'org.sqlite.JDBC');
insert into db_type (name, ref, driver) values
	('Sybase', 'jtds', 'net.sourceforge.jtds.jdbc.Driver');

-- Set the database version number
insert into config (db_version) values (1);
