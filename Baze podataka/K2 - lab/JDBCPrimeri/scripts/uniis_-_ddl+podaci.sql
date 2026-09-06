drop schema if exists uniis;
create schema uniis default character set utf8 default collate utf8_unicode_ci;
use uniis;

create table osoba
(
  JMB char(13),
  Prezime varchar(20) not null,
  Ime varchar(20) not null,
  DatumRodjenja date not null,
  Adresa varchar(50) not null,
  primary key (JMB)
);
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('0308983126116', 'Mirković', 'Ana', '1983-08-03', 'Ulica 3');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('0512983100067', 'Popović', 'Slavko', '1983-12-05', 'Ulica 17');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('0607989100027', 'Stojanović', 'Danijel', '1989-07-06', 'Ulica 19');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('0702964105027', 'Gavrić', 'Mirjana', '1964-02-07', 'Ulica 74');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('1002952100005', 'Mitrović', 'Nikola', '1952-02-10', 'Ulica 63');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('1006949100067', 'Soldat', 'Stanko', '1949-06-10', 'Ulica 101');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('1010988101124', 'Babić', 'Dejan', '1988-10-10', 'Ulica 5');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('1206986101234', 'Mirković', 'Marko', '1986-06-12', 'Ulica 1');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('1210987100018', 'Janković', 'Petar', '1987-10-12', 'Ulica 16');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('1312981163309', 'Filipović', 'Mirko', '1981-12-13', 'Ulica 70');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('1503990125037', 'Vasković', 'Jelena', '1990-03-15', 'Ulica 6');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('1804964163303', 'Savić', 'Nenad', '1964-04-18', 'Ulica 26');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('1907951100012', 'Lazić', 'Zoran', '1951-07-19', 'Ulica 30');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('2102979163201', 'Janković', 'Janko', '1979-02-21', 'Ulica 2');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('2108968196769', 'Petković', 'Milena', '1968-08-21', 'Ulica 84');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('2108988105034', 'Đukić', 'Milijana', '1988-08-21', 'Ulica 34');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('2208988105039', 'Miljević', 'Branka', '1988-08-22', 'Ulica 80');
insert into osoba (JMB, Prezime, Ime, DatumRodjenja, Adresa) values ('2804950103891', 'Ninković', 'Miloš', '1950-04-28', 'Ulica 4');

create table nastavnik
(
  JMB char(13),
  NastavnoZvanje varchar(20) not null,
  Plata decimal(6,2) not null,
  JMBSefaKatedre char(13),
  primary key (JMB),
  constraint FK_nastavnik_osoba
  foreign key (JMB)
  references osoba (JMB),
  constraint FK_nastavnik_nastavnik
  foreign key (JMBSefaKatedre)
  references nastavnik (JMB)
);
insert into nastavnik (JMB, NastavnoZvanje, Plata, JMBSefaKatedre) values ('0702964105027', 'docent', 1800.00, NULL);
insert into nastavnik (JMB, NastavnoZvanje, Plata, JMBSefaKatedre) values ('1907951100012', 'redovni profesor', 2300.00, NULL);
insert into nastavnik (JMB, NastavnoZvanje, Plata, JMBSefaKatedre) values ('2804950103891', 'redovni profesor', 2400.00, NULL);
insert into nastavnik (JMB, NastavnoZvanje, Plata, JMBSefaKatedre) values ('1002952100005', 'redovni profesor', 2400.00, '1907951100012');
insert into nastavnik (JMB, NastavnoZvanje, Plata, JMBSefaKatedre) values ('1006949100067', 'vanredni profesor', 2000.00, '1907951100012');
insert into nastavnik (JMB, NastavnoZvanje, Plata, JMBSefaKatedre) values ('1804964163303', 'vanredni profesor', 2000.00, '2804950103891');
insert into nastavnik (JMB, NastavnoZvanje, Plata, JMBSefaKatedre) values ('2108968196769', 'docent', 1800.00, '1907951100012');

create table asistent
(
  JMB char(13),
  SaradnickoZvanje varchar(20) not null,
  Plata decimal(6,2) not null,
  JMBSefaKatedre char(13) not null,
  primary key (JMB),
  constraint FK_asistent_osoba
  foreign key (JMB)
  references osoba (JMB),
  constraint FK_asistent_nastavnik
  foreign key (JMBSefaKatedre)
  references nastavnik (JMB)
);
insert into asistent (JMB, SaradnickoZvanje, Plata, JMBSefaKatedre) values ('0308983126116', 'viši asistent', 1300.00, '1907951100012');
insert into asistent (JMB, SaradnickoZvanje, Plata, JMBSefaKatedre) values ('0512983100067', 'asistent', 1200.00, '2804950103891');
insert into asistent (JMB, SaradnickoZvanje, Plata, JMBSefaKatedre) values ('1206986101234', 'asistent', 1200.00, '1907951100012');
insert into asistent (JMB, SaradnickoZvanje, Plata, JMBSefaKatedre) values ('1312981163309', 'viši asistent', 1350.00, '1907951100012');
insert into asistent (JMB, SaradnickoZvanje, Plata, JMBSefaKatedre) values ('2102979163201', 'viši asistent', 1300.00, '2804950103891');

create table student
(
  JMB char(13),
  AdresaStudiranja varchar(50),
  primary key (JMB),
  constraint FK_student_osoba
  foreign key (JMB)
  references osoba (JMB)
);
insert into student (JMB, AdresaStudiranja) values ('0607989100027', 'Adresa 2');
insert into student (JMB, AdresaStudiranja) values ('1010988101124', 'Adreda 101');
insert into student (JMB, AdresaStudiranja) values ('1206986101234', 'Adresa 8');
insert into student (JMB, AdresaStudiranja) values ('1210987100018', 'Adresa 47');
insert into student (JMB, AdresaStudiranja) values ('1503990125037', 'Adresa 73');
insert into student (JMB, AdresaStudiranja) values ('2108988105034', 'Adresa 19');
insert into student (JMB, AdresaStudiranja) values ('2208988105039', 'Adresa 50');

create table fakultet
(
  NazivFakulteta varchar(50),
  Adresa varchar(50) not null,
  primary key (NazivFakulteta)
);
insert into fakultet (NazivFakulteta, Adresa) values ('Elektrotehnički fakultet', 'Patre 5, Banja Luka');
insert into fakultet (NazivFakulteta, Adresa) values ('Prirodno-matematički fakultet', 'Mladena Stojanovića 2, Banja Luka');

create table telefon_fakulteta
(
  Telefon varchar(20),
  NazivFakulteta varchar(50),
  primary key (Telefon, NazivFakulteta),
  constraint FK_telefon_fakulteta_fakultet
  foreign key (NazivFakulteta)
  references fakultet (NazivFakulteta)
);
insert into telefon_fakulteta (Telefon, NazivFakulteta) values ('+387 (0) 51 221 820', 'Elektrotehnički fakultet');
insert into telefon_fakulteta (Telefon, NazivFakulteta) values ('+387 (0) 51 221 855', 'Elektrotehnički fakultet');
insert into telefon_fakulteta (Telefon, NazivFakulteta) values ('+387 (0) 51 319 142', 'Prirodno-matematički fakultet');

create table predmet
(
  IdPredmeta int,
  NazivPredmeta varchar(50) not null,
  ECTS tinyint not null,
  NazivFakulteta varchar(50) not null,
  primary key (IdPredmeta),
  constraint FK_predmet_fakultet
  foreign key (NazivFakulteta)
  references fakultet (NazivFakulteta)
);
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (1111, 'Programski jezici 1', 7, 'Elektrotehnički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (1112, 'Programski jezici 2', 6, 'Elektrotehnički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (1113, 'Softversko inženjerstvo', 6, 'Elektrotehnički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (1114, 'Baze podataka', 8, 'Elektrotehnički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (1115, 'Informacioni sistemi', 6, 'Elektrotehnički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (1121, 'Digitalne telekomunikacije', 5, 'Elektrotehnički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (1122, 'Analogni i digitalni filtri', 7, 'Elektrotehnički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (1123, 'Komutacioni sistemi', 6, 'Elektrotehnički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (1124, 'Ekonomični prenos informacija', 6, 'Elektrotehnički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (1211, 'Internet tehnologije', 6, 'Elektrotehnički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (1212, 'Sigurnost računarskih sistema', 8, 'Elektrotehnički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (1213, 'Baze podataka (viši nivo)', 8, 'Elektrotehnički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (1311, 'Internet programiranje', 7, 'Elektrotehnički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (1312, 'Kriptografija', 7, 'Elektrotehnički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (2111, 'Linearna algebra', 6, 'Prirodno-matematički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (2112, 'Kvantna mehanika', 5, 'Prirodno-matematički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (2113, 'Astrofizika', 6, 'Prirodno-matematički fakultet');
insert into predmet (IdPredmeta, NazivPredmeta, ECTS, NazivFakulteta) values (2114, 'Fizika atoma i molekula', 7, 'Prirodno-matematički fakultet');

create table studijski_program
(
  IdSP int,
  NazivSP varchar(50) not null,
  Ciklus tinyint not null,
  Trajanje tinyint not null,
  UkupnoECTS smallint not null,
  Zvanje varchar(50) not null,
  NazivFakulteta varchar(50) not null,
  primary key (IdSP),
  constraint FK_studijski_program_fakultet
  foreign key (NazivFakulteta)
  references fakultet (NazivFakulteta)
);
insert into studijski_program (IdSP, NazivSP, Ciklus, Trajanje, UkupnoECTS, Zvanje, NazivFakulteta) values (111, 'Računarstvo i informatika', 1, 8, 240, 'diplomirani inženjer elektrotehnike', 'Elektrotehnički fakultet');
insert into studijski_program (IdSP, NazivSP, Ciklus, Trajanje, UkupnoECTS, Zvanje, NazivFakulteta) values (112, 'Elektronika i telekomunikacije', 1, 8, 240, 'diplomirani inženjer elektrotehnike', 'Elektrotehnički fakultet');
insert into studijski_program (IdSP, NazivSP, Ciklus, Trajanje, UkupnoECTS, Zvanje, NazivFakulteta) values (121, 'Računarstvo i informatika', 2, 2, 60, 'magistar računarstva i informatike', 'Elektrotehnički fakultet');
insert into studijski_program (IdSP, NazivSP, Ciklus, Trajanje, UkupnoECTS, Zvanje, NazivFakulteta) values (131, 'Informaciono-komunikacione tehnologije', 3, 6, 180, 'doktor nauka', 'Elektrotehnički fakultet');
insert into studijski_program (IdSP, NazivSP, Ciklus, Trajanje, UkupnoECTS, Zvanje, NazivFakulteta) values (211, 'Fizika', 1, 8, 240, 'diplomirani fizičar', 'Prirodno-matematički fakultet');

create table p_na_sp
(
  IdPredmeta int,
  IdSP int,
  Semestar tinyint not null,
  TipPredmeta char(1) not null,
  primary key (IdPredmeta, IdSP),
  constraint FK_p_na_sp_predmet
  foreign key (IdPredmeta)
  references predmet (IdPredmeta),
  constraint FK_p_na_sp_studijski_program
  foreign key (IdSP)
  references studijski_program (IdSP)
);
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1111, 111, 2, 'O');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1112, 111, 3, 'O');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1113, 111, 4, 'I');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1114, 111, 6, 'O');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1114, 112, 3, 'O');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1115, 111, 7, 'I');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1121, 112, 5, 'O');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1122, 112, 6, 'O');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1123, 112, 7, 'I');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1124, 112, 8, 'I');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1211, 121, 1, 'O');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1212, 121, 1, 'O');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1213, 121, 1, 'I');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1311, 131, 1, 'O');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (1312, 131, 2, 'O');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (2111, 111, 1, 'O');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (2111, 112, 1, 'O');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (2112, 211, 3, 'I');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (2113, 211, 5, 'O');
insert into p_na_sp (IdPredmeta, IdSP, Semestar, TipPredmeta) values (2114, 211, 6, 'O');

create table predaje
(
  JMB char(13),
  IdPredmeta int,
  IdSP int,
  primary key (JMB, IdPredmeta, IdSP),
  constraint FK_predaje_nastavnik
  foreign key (JMB)
  references nastavnik (JMB),
  constraint FK_predaje_p_na_sp
  foreign key (IdPredmeta, IdSP)
  references p_na_sp (IdPredmeta, IdSP)
);
insert into predaje (JMB, IdPredmeta, IdSP) values ('2108968196769', 1111, 111);
insert into predaje (JMB, IdPredmeta, IdSP) values ('2108968196769', 1112, 111);
insert into predaje (JMB, IdPredmeta, IdSP) values ('1907951100012', 1113, 111);
insert into predaje (JMB, IdPredmeta, IdSP) values ('1006949100067', 1114, 111);
insert into predaje (JMB, IdPredmeta, IdSP) values ('1006949100067', 1114, 112);
insert into predaje (JMB, IdPredmeta, IdSP) values ('1907951100012', 1115, 111);
insert into predaje (JMB, IdPredmeta, IdSP) values ('2804950103891', 1121, 112);
insert into predaje (JMB, IdPredmeta, IdSP) values ('2804950103891', 1122, 112);
insert into predaje (JMB, IdPredmeta, IdSP) values ('1804964163303', 1123, 112);
insert into predaje (JMB, IdPredmeta, IdSP) values ('1804964163303', 1124, 112);
insert into predaje (JMB, IdPredmeta, IdSP) values ('2108968196769', 1211, 121);
insert into predaje (JMB, IdPredmeta, IdSP) values ('1907951100012', 1212, 121);
insert into predaje (JMB, IdPredmeta, IdSP) values ('1006949100067', 1213, 121);
insert into predaje (JMB, IdPredmeta, IdSP) values ('1002952100005', 1311, 131);
insert into predaje (JMB, IdPredmeta, IdSP) values ('1002952100005', 1312, 131);
insert into predaje (JMB, IdPredmeta, IdSP) values ('0702964105027', 2111, 111);
insert into predaje (JMB, IdPredmeta, IdSP) values ('0702964105027', 2111, 112);
insert into predaje (JMB, IdPredmeta, IdSP) values ('0702964105027', 2112, 211);
insert into predaje (JMB, IdPredmeta, IdSP) values ('0702964105027', 2113, 211);
insert into predaje (JMB, IdPredmeta, IdSP) values ('0702964105027', 2114, 211);

create table asistira
(
  JMB char(13),
  IdPredmeta int,
  IdSP int,
  primary key (JMB, IdPredmeta, IdSP),
  constraint FK_asistira_asistent
  foreign key (JMB)
  references asistent (JMB),
  constraint FK_asistira_p_na_sp
  foreign key (IdPredmeta, IdSP)
  references p_na_sp (IdPredmeta, IdSP)
);
insert into asistira (JMB, IdPredmeta, IdSP) values ('1206986101234', 1111, 111);
insert into asistira (JMB, IdPredmeta, IdSP) values ('0308983126116', 1112, 111);
insert into asistira (JMB, IdPredmeta, IdSP) values ('1206986101234', 1112, 111);
insert into asistira (JMB, IdPredmeta, IdSP) values ('0308983126116', 1113, 111);
insert into asistira (JMB, IdPredmeta, IdSP) values ('1206986101234', 1114, 111);
insert into asistira (JMB, IdPredmeta, IdSP) values ('1206986101234', 1114, 112);
insert into asistira (JMB, IdPredmeta, IdSP) values ('0308983126116', 1115, 111);
insert into asistira (JMB, IdPredmeta, IdSP) values ('1206986101234', 1115, 111);
insert into asistira (JMB, IdPredmeta, IdSP) values ('0512983100067', 1121, 112);
insert into asistira (JMB, IdPredmeta, IdSP) values ('0512983100067', 1122, 112);
insert into asistira (JMB, IdPredmeta, IdSP) values ('0512983100067', 1123, 112);
insert into asistira (JMB, IdPredmeta, IdSP) values ('0512983100067', 1124, 112);
insert into asistira (JMB, IdPredmeta, IdSP) values ('2102979163201', 1211, 121);
insert into asistira (JMB, IdPredmeta, IdSP) values ('2102979163201', 1212, 121);
insert into asistira (JMB, IdPredmeta, IdSP) values ('0308983126116', 1213, 121);
insert into asistira (JMB, IdPredmeta, IdSP) values ('2102979163201', 1311, 131);
insert into asistira (JMB, IdPredmeta, IdSP) values ('2102979163201', 1312, 131);
insert into asistira (JMB, IdPredmeta, IdSP) values ('1312981163309', 2111, 111);
insert into asistira (JMB, IdPredmeta, IdSP) values ('1312981163309', 2111, 112);
insert into asistira (JMB, IdPredmeta, IdSP) values ('1312981163309', 2112, 211);
insert into asistira (JMB, IdPredmeta, IdSP) values ('1312981163309', 2113, 211);

create table upisao
(
  JMB char(13),
  IdPredmeta int,
  IdSP int,
  primary key (JMB, IdPredmeta, IdSP),
  constraint FK_upisao_student
  foreign key (JMB)
  references student (JMB),
  constraint FK_upisao_p_na_sp
  foreign key (IdPredmeta, IdSP)
  references p_na_sp (IdPredmeta, IdSP)
);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1010988101124', 1111, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1206986101234', 1111, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1210987100018', 1111, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1503990125037', 1111, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1010988101124', 1112, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1206986101234', 1112, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1210987100018', 1112, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1010988101124', 1113, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1206986101234', 1113, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1210987100018', 1113, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1206986101234', 1114, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1210987100018', 1114, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('2108988105034', 1114, 112);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1206986101234', 1115, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1210987100018', 1115, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('2108988105034', 1121, 112);
insert into upisao (JMB, IdPredmeta, IdSP) values ('2108988105034', 1122, 112);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1206986101234', 1211, 121);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1206986101234', 1212, 121);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1206986101234', 1213, 121);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1010988101124', 2111, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1206986101234', 2111, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1210987100018', 2111, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('1503990125037', 2111, 111);
insert into upisao (JMB, IdPredmeta, IdSP) values ('0607989100027', 2111, 112);
insert into upisao (JMB, IdPredmeta, IdSP) values ('2108988105034', 2111, 112);
insert into upisao (JMB, IdPredmeta, IdSP) values ('2208988105039', 2112, 211);
insert into upisao (JMB, IdPredmeta, IdSP) values ('2208988105039', 2113, 211);
insert into upisao (JMB, IdPredmeta, IdSP) values ('2208988105039', 2114, 211);

create table upisan_na
(
  JMB char(13),
  IdSP int,
  BrojIndeksa char(8) not null,
  Semestar tinyint not null,
  primary key (JMB, IdSP),
  constraint FK_upisan_na_student
  foreign key (JMB)
  references student (JMB),
  constraint FK_upisan_na_studijski_program
  foreign key (IdSP)
  references studijski_program (IdSP)
);
insert into upisan_na (JMB, IdSP, BrojIndeksa, Semestar) values ('0607989100027', 112, '90/08', 2);
insert into upisan_na (JMB, IdSP, BrojIndeksa, Semestar) values ('1010988101124', 111, '31/07', 4);
insert into upisan_na (JMB, IdSP, BrojIndeksa, Semestar) values ('1206986101234', 111, '38/05', 8);
insert into upisan_na (JMB, IdSP, BrojIndeksa, Semestar) values ('1206986101234', 121, '12/09', 2);
insert into upisan_na (JMB, IdSP, BrojIndeksa, Semestar) values ('1210987100018', 111, '65/06', 8);
insert into upisan_na (JMB, IdSP, BrojIndeksa, Semestar) values ('1503990125037', 111, '78/09', 2);
insert into upisan_na (JMB, IdSP, BrojIndeksa, Semestar) values ('2108988105034', 112, '48/07', 6);
insert into upisan_na (JMB, IdSP, BrojIndeksa, Semestar) values ('2208988105039', 211, '10/07', 6);

create table ispit
(
  DatumIspita date,
  IdPredmeta int,
  IdSP int,
  Lokacija varchar(20) not null,
  primary key (DatumIspita, IdPredmeta, IdSP),
  constraint FK_ispit_p_na_sp
  foreign key (IdPredmeta, IdSP)
  references p_na_sp (IdPredmeta, IdSP)
);
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2006-01-31', 1112, 111, '002');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2006-02-03', 1115, 111, '005');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2006-02-06', 2111, 111, '006');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2006-07-03', 1111, 111, '001');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2006-07-10', 2111, 111, '006');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2006-09-11', 2111, 111, '006');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2007-01-30', 1112, 111, '002');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2007-02-05', 2111, 111, '006');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2007-07-02', 1111, 111, '001');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2007-07-04', 1113, 111, '003');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2007-09-03', 1111, 111, '001');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2007-09-05', 1113, 111, '003');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2008-02-05', 1112, 111, '002');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2008-02-19', 2111, 112, '012');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2008-07-08', 1112, 111, '002');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2008-07-09', 1113, 111, '003');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2008-07-10', 1114, 111, '004');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2008-07-14', 2111, 111, '006');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2008-09-01', 1111, 111, '001');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2008-09-08', 2111, 111, '006');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2009-02-06', 1115, 111, '005');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2009-02-09', 2111, 111, '006');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2009-02-10', 1114, 112, '007');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2009-02-11', 1121, 112, '008');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2009-02-17', 2111, 112, '012');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2009-02-25', 2112, 211, '018');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2009-07-06', 1111, 111, '001');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2009-07-14', 1114, 112, '007');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2009-07-21', 2111, 112, '012');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2009-09-10', 1114, 111, '004');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2009-09-22', 2111, 112, '012');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2010-02-02', 1112, 111, '002');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2010-02-10', 1121, 112, '008');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2010-02-16', 2111, 112, '012');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2010-02-17', 1211, 121, '013');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2010-02-18', 1212, 121, '014');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2010-02-19', 1213, 121, '015');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2010-02-25', 2113, 211, '019');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2010-07-06', 1112, 111, '002');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2010-07-07', 1113, 111, '003');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2010-07-20', 2111, 112, '012');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2010-07-22', 1212, 121, '014');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2010-07-30', 2114, 211, '020');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2010-09-22', 1211, 121, '013');
insert into ispit (DatumIspita, IdPredmeta, IdSP, Lokacija) values ('2010-10-01', 2114, 211, '020');

create table polaze
(
  JMB char(13),
  DatumIspita date,
  IdPredmeta int,
  IdSP int,
  Ocena tinyint not null,
  primary key (JMB, DatumIspita, IdPredmeta, IdSP),
  constraint FK_polaze_student
  foreign key (JMB)
  references student (JMB),
  constraint FK_polaze_ispit
  foreign key (DatumIspita, IdPredmeta, IdSP)
  references ispit (DatumIspita, IdPredmeta, IdSP)
);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('0607989100027', '2009-02-17', 2111, 112, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('0607989100027', '2009-07-21', 2111, 112, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('0607989100027', '2009-09-22', 2111, 112, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('0607989100027', '2010-02-16', 2111, 112, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('0607989100027', '2010-07-20', 2111, 112, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1010988101124', '2008-07-14', 2111, 111, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1010988101124', '2008-09-01', 1111, 111, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1010988101124', '2008-09-08', 2111, 111, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1010988101124', '2009-02-09', 2111, 111, 6);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1010988101124', '2009-07-06', 1111, 111, 7);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1010988101124', '2010-02-02', 1112, 111, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1010988101124', '2010-07-06', 1112, 111, 7);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1010988101124', '2010-07-07', 1113, 111, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1206986101234', '2006-02-06', 2111, 111, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1206986101234', '2006-07-03', 1111, 111, 10);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1206986101234', '2006-07-10', 2111, 111, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1206986101234', '2006-09-11', 2111, 111, 7);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1206986101234', '2007-01-30', 1112, 111, 9);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1206986101234', '2007-07-04', 1113, 111, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1206986101234', '2007-09-05', 1113, 111, 8);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1206986101234', '2008-07-10', 1114, 111, 10);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1206986101234', '2009-02-06', 1115, 111, 9);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1206986101234', '2010-02-17', 1211, 121, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1206986101234', '2010-02-18', 1212, 121, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1206986101234', '2010-02-19', 1213, 121, 10);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1206986101234', '2010-07-22', 1212, 121, 10);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1206986101234', '2010-09-22', 1211, 121, 9);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1210987100018', '2007-02-05', 2111, 111, 9);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1210987100018', '2007-07-02', 1111, 111, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1210987100018', '2007-09-03', 1111, 111, 8);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1210987100018', '2008-02-05', 1112, 111, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1210987100018', '2008-07-08', 1112, 111, 9);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1210987100018', '2008-07-09', 1113, 111, 6);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('1210987100018', '2009-09-10', 1114, 111, 7);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('2108988105034', '2008-02-19', 2111, 112, 8);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('2108988105034', '2009-02-10', 1114, 112, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('2108988105034', '2009-07-14', 1114, 112, 7);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('2108988105034', '2010-02-10', 1121, 112, 7);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('2208988105039', '2009-02-25', 2112, 211, 9);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('2208988105039', '2010-02-25', 2113, 211, 10);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('2208988105039', '2010-07-30', 2114, 211, 5);
insert into polaze (JMB, DatumIspita, IdPredmeta, IdSP, Ocena) values ('2208988105039', '2010-10-01', 2114, 211, 9);