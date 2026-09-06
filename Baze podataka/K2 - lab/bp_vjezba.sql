/*create schema bp_vjezba
default character set utf8
default collate utf8_unicode_ci;*/

-- Komentar minus minuz razmak
# Komentar taraba
/* C komentar */

use bp_vjezba;
create table fakultet
(
	NazivFakulteta varchar(100) primary key,
    Adresa varchar(100) not null
    # primary key (NazivFakulteta)
);

create table predmet(
	IdPredmeta int auto_increment,
    NazivPredmeta varchar(100) not null,
    ECTS tinyint default 6 not null,
    NazivF varchar(100) not null,
    primary key (IdPredmeta),
    constraint FK_predmet_fakultet
    foreign key(NazivF)
    references fakultet(NazivFakulteta)
);


drop table predmet;
create table predmet(
	IdPredmeta int auto_increment,
    NazivPredmeta varchar(100) not null,
    ECTS tinyint default 6 not null,
    NazivF varchar(100) not null,
    primary key (IdPredmeta),
    constraint FK_predmet_fakultet
    foreign key(NazivF)
    references fakultet(NazivFakulteta)
    on update cascade on delete restrict
    #umjesto cascade moze biti no action, set null samo ako nije not null kod stranog kljuca
);

create table student(
	JMB char(13),
    BrojIndeksa char(10) not null, #moze ovdje unique umjesto constraint unique
    Ime varchar(20) not null,
    Prezime varchar(20) not null,
    primary key(JMB),
    constraint UQ_student_broj_indeksa #moze se izostaviti naziv ali onda DBMS dodaje neki naziv
    unique(BrojIndeksa)
);

alter table student add column ProsjekOcjena double not null; #after BrojIndeksa;
alter table student modify column Prezime varchar(100) not null;
alter table student drop column ProsjekOcjena;
alter table student drop primary key; #moze jer nije povezano ni sa cim
#alter table fakultet drop primary key; ne moze jer fakultet jeste povezan sa predmetom

alter table student drop constraint UQ_student_broj_indeksa;
alter table student add primary key (BrojIndeksa);
alter table student add constraint UQ_student_jmb unique(JMB);
alter table predmet drop constraint FK_predmet_fakultet;
alter table predmet 
	add constraint FK_predmet_fakultet 
    foreign key(NazivF) 
    references fakultet(NazivFakulteta) 
    on update cascade on delete restrict;

create index IX_predmet_naziv on predmet(NazivPredmeta);
create index IX_student_ime_prezime on student(Prezime, Ime);
drop index IX_student_ime_prezime on student;

drop schema bp_vjezba;
