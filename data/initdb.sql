create table if not exists db_type (
	id int not null,
	name varchar2(20) not null,
	ref varchar2(100) not null,
	driver varchar2(100) not null,
	jar_name varchar2(255)
);

create table if not exists app_property (
	key varchar2(200),
	value varchar2(200)
);

create table if not exists db (
	id int not null,
	name varchar2(100) not null,
	url varchar2(200) not null,
	id2 varchar2(100) not null,
	pw varchar2(300),
	dbtype int not null
);

create table if not exists config (
	db_version int not null
);

create table if not exists hadoop (
	id int not null,
	label varchar2(100) not null,
	name varchar2(200) not null,
	url varchar2(200) not null
);

create table if not exists session (
	id int not null,
	source_type_id int not null,
	target_type_id int not null,
	source_delim varchar2(10),
	target_delim varchar2(10)
);

create table if not exists data_type (
	id int not null,
	name varchar2(100) not null
);

create table if not exists header (
	id int not null,
	session_id int not null,
	col_name varchar2(100) not null,
	col_type_id int not null
);

create table if not exists col_type (
	id int not null,
	data_type varchar2(100)
);

-- Inserts for col_type, data_type and db_type

insert into config (db_version) values (1);
