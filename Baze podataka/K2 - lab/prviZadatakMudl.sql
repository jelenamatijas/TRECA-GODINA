create schema seminari default collate utf8_unicode_ci;
use seminari;

create table seminar(
	IdSeminara int auto_increment not null,
    Naziv varchar(100) not null,
    DatumOd date not null,
    DatumDo date not null,
    primary key (IdSeminara)
);

create table osoba(
	IdOsobe int auto_increment not null,
    Ime varchar(20) not null,
    Prezime varchar(20) not null,
    primary key (IdOsobe)
);

create table predavac(
	IdOsobe int not null,
    primary key(IdOsobe),
    constraint FK_Predavac_Osoba
		foreign key (IdOsobe) references osoba (IdOsobe)
		on update cascade on delete restrict
);

create table predavanje(
	IdPredavanja int auto_increment not null,
    IdSeminara int not null,
    Naziv varchar(20) not null,
    Datum date not null,
    IdPredavaca int,
    primary key (IdPredavanja),
    constraint FK_Predavanje_Seminar
		foreign key (IdSeminara) references seminar (IdSeminara)
        on update cascade on delete restrict,
    constraint FK_Predavanje_Predavac
		foreign key (IdPredavaca) references predavac (IdOsobe)
		on update cascade on delete set null
);

create table prijavljena(
	IdSeminara int not null,
    IdOsobe int not null,
    primary key (IdSeminara, IdOsobe),
    constraint FK_Prijavljena_Seminar
		foreign key (IdSeminara) references seminar (IdSeminara)
		on update cascade,
	constraint FK_Prijavljena_Osoba
		foreign key (IdOsobe) references osoba (IdOsobe)
		on update cascade
);

create table prisustvuje (
    IdPredavanja int not null,
    IdOsobe int not null,
    primary key(IdPredavanja, IdOsobe),
    constraint FK_Prisustvuje_Predavanje
        foreign key (IdPredavanja) references predavanje (IdPredavanja)
        on update cascade,
    constraint FK_Prisustvuje_Osoba
        foreign key (IdOsobe) references osoba (IdOsobe)
        on update cascade
);

