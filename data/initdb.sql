create table db_type if not exists (
id int not null,
name varchar2(20) not null,
ref varchar2(100) not null,
driver varchar2(100) not null,
jar_name varchar2(255)
);

create table app_property if not exists (
key varchar2(200),
value varchar2(200)
);

create table db if not exists (
id int not null,
name varchar2(100) not null,
url varchar2(200) not null,
id varchar2(100) not null,
pw varchar2(300),
dbtype int not null
);

create table hadoop if not exists (
id int not null,
label varchar2(100) not null,
name varchar2(200) not null,
url varchar2(200) not null
);

create table session if not exists (
id int not null,
source_type_id int not null,
target_type_id int not null,
source_delim varchar2(10),
target_delim varchar2(10)
);

create table data_type if not exists (
id int not null,
name varchar2(100) not null
);

create table header if not exists (
id int not null,
session_id int not null,
col_name varchar2(100) not null,
col_type_id int not null
);

create table col_type if not exists (
id int not null,
data_type varchar2(100)
);


// Inserts for col_type, data_type and db_type



