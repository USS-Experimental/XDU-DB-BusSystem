/*公司*/
create table Company
(
  Company_name VARCHAR(20) PRIMARY KEY,
  Location VARCHAR(50)
);

/*车队成员*/
create table Tmember
(
  job_index INTEGER PRIMARY KEY,  /*工号*/
  Sname VARCHAR(10) NOT NULL,
  Ssex CHAR(2) CHECK(Ssex IN('M','F')),
  Sage INTEGER NOT NULL,
  Snative_place VARCHAR(100),
  Sentry_time DATE NOT NULL,
  id_card CHAR(18) NOT NULL UNIQUE,
  phone CHAR(11),
  Sjob VARCHAR(10) CHECK(Sjob IN('队长','路队长','司机'))
);

/*车队*/
create table Team
(
  team_index INTEGER PRIMARY KEY,  /* 车队号 */
  create_date DATE,
  company_name VARCHAR(20),
  
  FOREIGN KEY(company_name) REFERENCES company(Company_name)
);

/* 线路 */
create table Line
(
  line_index INTEGER PRIMARY KEY,  /* 线路号 */
  team_index INTEGER,
  create_date DATE,
  FOREIGN KEY(team_index) REFERENCES Team(team_index)  /* 所属车队号 */
);

/* 车站 */
create table Station
(
  station_name VARCHAR(40),
  station_location VARCHAR(100),
  PRIMARY KEY(station_name)
);

CREATE TABLE station_line
(
  line_index INTEGER,
  station_index INTEGER,
  station_name VARCHAR(40),
  
  PRIMARY KEY(line_index, station_index, station_name),
  FOREIGN KEY(station_name) REFERENCES Station(station_name),
  FOREIGN KEY(line_index) REFERENCES Line(line_index)
);

/*车*/
create table Bus
(
  bus_license CHAR(10) PRIMARY KEY,  /*车牌号*/
  bus_brand VARCHAR(10) NOT NULL,
  tot_seat INTEGER NOT NULL,
  bus_age SMALLINT
);

create table bus_line
(
  bus_license CHAR(10) PRIMARY KEY,
  line_index INTEGER,
  FOREIGN KEY(line_index) REFERENCES line(line_index)  /* 所属线路号 */
);

/*队长表*/
create table team_leader
(
  leader_index INTEGER,  /*队长编号*/
  team_index INTEGER,
  PRIMARY KEY(leader_index, team_index),
  FOREIGN KEY(leader_index) REFERENCES Tmember(job_index),
  FOREIGN KEY(team_index) REFERENCES Team(team_index)  /* 所属车队号 */
);

/*成员-线路*/
create table mem_line
(
  job_index INTEGER PRIMARY KEY,  /*工号*/
  line_index INTEGER,
  FOREIGN KEY(job_index) REFERENCES Tmember(job_index),
  FOREIGN KEY(line_index) REFERENCES Line(line_index)  /* 所属线路号 */
);

/*警告*/
create TABLE alert
(
  alert_level INTEGER PRIMARY KEY,
  actions VARCHAR(50) NOT NULL
);

/*违章类*/
create TABLE vio_categories
(
  violation_name VARCHAR(30) PRIMARY KEY,
  alert_level INTEGER,
  FOREIGN KEY(alert_level) REFERENCES alert(alert_level)
);

/*违章信息*/
create table Violation_records
(
  violation_index INTEGER PRIMARY KEY,
  driver_index INTEGER,
  bus_license CHAR(10),
  dates DATE,
  vio_location VARCHAR(100),   /* 事发地  */
  violation_name VARCHAR(30),
  recoder_index INTEGER,

  FOREIGN KEY(driver_index) REFERENCES Tmember(job_index),
  FOREIGN KEY(recoder_index) REFERENCES Tmember(job_index),
  FOREIGN KEY(bus_license) REFERENCES bus(bus_license),
  FOREIGN KEY(violation_name) REFERENCES vio_categories(violation_name)
);

/* 车队违章统计视图 */
CREATE VIEW stat_team_violation
AS
SELECT DISTINCT team_index, violation_name, dates
from line, mem_line, violation_records;

/* 司机违章详细信息统计视图 */
CREATE VIEW stat_driver_violation
AS
SELECT DISTINCT driver_index, Sname, bus_license, dates, violation_records.violation_name, vio_location, actions, recoder_index
from tmember, violation_records, vio_categories, alert
where tmember.job_index = violation_records.driver_index and violation_records.violation_name = vio_categories.violation_name
      and vio_categories.alert_level = alert.alert_level;

/* 司机信息视图 */
CREATE VIEW driver_info
AS
SELECT DISTINCT tmember.job_index, Sname, Ssex, Sage, Snative_place, phone, id_card, Sentry_time, tmember.sjob, team_index, mem_line.line_index 
from tmember, line, mem_line;

/* 汽车信息视图 */
CREATE VIEW bus_info
AS
SELECT bus.bus_license, bus_brand, tot_seat, bus_age, circuit_index 
from bus, bus_line;

CREATE VIEW line_leader
AS
SELECT DISTINCT line_index, tmember.job_index, tmember.sname
FROM mem_line, tmember
WHERE tmember.sjob = '路队长';

