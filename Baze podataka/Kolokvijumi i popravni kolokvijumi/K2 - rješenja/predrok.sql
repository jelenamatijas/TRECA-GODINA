create schema film default character set utf8
default collate utf8_unicode_ci;
use film;

create table osoba(
	IdOsobe int auto_increment primary key,
    Ime varchar(20) not null,
    Prezime varchar(20) not null,
    DatumRodjenja date not null
);

create table zaposleni(
	IdOsobe int primary key,
    Plata double not null,
    Aktivan bool not null,
    constraint fk_zaposleni_osoba
    foreign key(IdOsobe) references osoba(IdOsobe)
);

create table clan(
	IdOsobe int primary key,
    BrojClanskeKarte int not null,
    DatumUclanjivanja date not null,
    constraint fk_clan_osoba
    foreign key(IdOsobe) references osoba(IdOsobe)
);

create table film(
	IdFilma int auto_increment primary key,
    Naslov varchar(40) not null,
    Opis varchar(100) not null
);

create table video_artikal(
	BrojKopije int ,
    IdFilma int,
    VrstaMedija varchar(20) not null,
    DatumNabavke date not null,
    primary key(BrojKopije, IdFilma),
    constraint fk_va_film
    foreign key(IdFilma) references film(IdFilma)
);

create table iznajmljivanje(
	IdIznajmljivanja int auto_increment primary key,
    DatumIznajmljivanja date not null,
    DatumVracanja date,
    IdZad int not null,
    IdRazd int,
    IdCl int not null,
    BrojKopije int not null,
    IdFilma int not null,
    constraint fk_iznajmljivanje_zaposleni
    foreign key(IdZad) references zaposleni(IdOsobe),
    constraint fk_iznajmljivanje_zaposleni2
    foreign key(IdRazd) references zaposleni(IdOsobe),
    constraint fk_iznamjljivanje_clan
    foreign key(IdCl) references clan(IdOsobe),
    constraint fk_iznajmljivanje_va
    foreign key(BrojKopije, IdFilma) references video_artikal(BrojKopije, IdFilma)
);


-- -----------------------------------------------------------------------------------------------------------

-- =========================================================================
-- 1. UNOS PODATAKA U TABELU: osoba (Kreiramo 35 osoba)
-- =========================================================================
INSERT INTO osoba (Ime, Prezime, DatumRodjenja) VALUES
('Marko', 'Marković', '1985-05-12'), -- IdOsobe: 1
('Janko', 'Janković', '1990-08-23'), -- IdOsobe: 2
('Petar', 'Petrović', '1988-11-02'), -- IdOsobe: 3
('Ana', 'Anić', '1995-03-15'),      -- IdOsobe: 4
('Marija', 'Marić', '1992-07-19'),   -- IdOsobe: 5
('Nikola', 'Nikolić', '1980-01-30'), -- IdOsobe: 6
('Luka', 'Lukić', '1997-09-25'),     -- IdOsobe: 7
('Stefan', 'Stefanović', '1993-04-14'),-- IdOsobe: 8
('Milica', 'Milić', '1994-12-05'),   -- IdOsobe: 9
('Jelena', 'Jelenić', '1989-06-22'), -- IdOsobe: 10
('Igor', 'Igorić', '1991-10-10'),    -- IdOsobe: 11
('Dejan', 'Dejanović', '1984-02-18'),-- IdOsobe: 12
('Ivana', 'Ivanović', '1996-08-08'), -- IdOsobe: 13
('Katarina', 'Katić', '1993-11-30'), -- IdOsobe: 14
('Milan', 'Milanović', '1987-03-21'),-- IdOsobe: 15
('Zoran', 'Zorić', '1979-05-17'),    -- IdOsobe: 16
('Goran', 'Gorić', '1983-07-29'),    -- IdOsobe: 17
('Sanja', 'Sanjić', '1995-01-11'),   -- IdOsobe: 18
('Tanis', 'Tanić', '1998-02-14'),    -- IdOsobe: 19
('Filip', 'Filipović', '1992-09-09'),-- IdOsobe: 20
('Ognjen', 'Ognjenović', '1994-06-16'),-- IdOsobe: 21
('Sara', 'Sarić', '1997-10-24'),     -- IdOsobe: 22
('Miloš', 'Milošević', '1986-12-25'),-- IdOsobe: 23
('Dušan', 'Dušanić', '1990-04-03'),  -- IdOsobe: 24
('Anja', 'Anjić', '1999-07-07'),     -- IdOsobe: 25
('Božidar', 'Božić', '1982-11-11'),  -- IdOsobe: 26
('Vesna', 'Vesnić', '1985-03-13'),   -- IdOsobe: 27
('Nemanja', 'Nemnjić', '1991-05-19'),-- IdOsobe: 28
('Tamara', 'Tamarić', '1996-12-12'), -- IdOsobe: 29
('Pavle', 'Pavlović', '1993-01-01'), -- IdOsobe: 30
('Maja', 'Majić', '1994-08-20'),     -- IdOsobe: 31
('Đorđe', 'Đorđević', '1988-10-15'), -- IdOsobe: 32
('Sofija', 'Sofić', '1997-03-03'),   -- IdOsobe: 33
('Uroš', 'Urošević', '1995-05-05'),  -- IdOsobe: 34
('Elena', 'Elenić', '1999-11-11');   -- IdOsobe: 35

-- =========================================================================
-- 2. UNOS PODATAKA U TABELU: zaposleni (15 unosa, IdOsobe od 1 do 15)
-- =========================================================================
INSERT INTO zaposleni (IdOsobe, Plata, Aktivan) VALUES
(1, 850.00, 1),
(2, 900.00, 1),
(3, 820.00, 1),
(4, 880.00, 1),
(5, 1100.00, 1), -- Menadžer npr.
(6, 750.00, 0), -- Neaktivan
(7, 800.00, 1),
(8, 830.00, 1),
(9, 850.00, 1),
(10, 750.00, 0), -- Neaktivan
(11, 920.00, 1),
(12, 870.00, 1),
(13, 890.00, 1),
(14, 950.00, 1),
(15, 1200.00, 1);

-- =========================================================================
-- 3. UNOS PODATAKA U TABELU: clan (15 unosa, IdOsobe od 16 do 30)
-- =========================================================================
INSERT INTO clan (IdOsobe, BrojClanskeKarte, DatumUclanjivanja) VALUES
(16, 1001, '2023-01-15'),
(17, 1002, '2023-02-20'),
(18, 1003, '2023-03-10'),
(19, 1004, '2023-05-18'),
(20, 1005, '2023-06-01'),
(21, 1006, '2023-08-12'),
(22, 1007, '2023-09-25'),
(23, 1008, '2023-11-05'),
(24, 1009, '2024-01-10'),
(25, 1010, '2024-02-14'),
(26, 1011, '2024-03-03'),
(27, 1012, '2024-04-22'),
(28, 1013, '2024-05-17'),
(29, 1014, '2024-06-30'),
(30, 1015, '2024-07-15');

-- =========================================================================
-- 4. UNOS PODATAKA U TABELU: film (15 unosa)
-- =========================================================================
INSERT INTO film (Naslov, Opis) VALUES
('Inception', 'Sci-Fi triler o krađi snova.'),             -- IdFilma: 1
('The Matrix', 'Kultni sajberpank klasik.'),                -- IdFilma: 2
('Interstellar', 'Putovanje kroz svemir i vrijeme.'),       -- IdFilma: 3
('The Godfather', 'Kriminalističko remek-djelo.'),          -- IdFilma: 4
('Pulp Fiction', 'Prepletene priče iz podzemlja.'),         -- IdFilma: 5
('Fight Club', 'Anarhija, sapun i nesanica.'),              -- IdFilma: 6
('Forrest Gump', 'Život je kao kutija čokolada.'),          -- IdFilma: 7
('Gladiator', 'Istorijski spektakl u Rimu.'),               -- IdFilma: 8
('The Dark Knight', 'Najbolji film o Betmenu.'),            -- IdFilma: 9
('Schindlers List', 'Potresna istorijska drama.'),          -- IdFilma: 10
('Se7en', 'Mračni triler o sedam grijehova.'),              -- IdFilma: 11
('Goodfellas', 'Život u mafiji iz prve ruke.'),             -- IdFilma: 12
('The Silence of the Lambs', 'Psihološki triler sa Hanibalom.'),-- IdFilma: 13
('The Green Mile', 'Čuda se dešavaju na neočekivanim mjestima.'),-- IdFilma: 14
('Parasite', 'Izvanredna južnokorejska crna komedija.');     -- IdFilma: 15

-- =========================================================================
-- 5. UNOS PODATAKA U TABELU: video_artikal (15 unosa, kombinacije kopija i filmova)
-- =========================================================================
INSERT INTO video_artikal (BrojKopije, IdFilma, VrstaMedija, DatumNabavke) VALUES
(1, 1, 'DVD', '2023-01-20'),
(2, 1, 'Blu-ray', '2023-01-22'),
(1, 2, 'DVD', '2023-02-15'),
(1, 3, 'Blu-ray', '2023-03-05'),
(1, 4, 'DVD', '2023-04-12'),
(2, 4, 'DVD', '2023-04-12'),
(1, 5, 'Blu-ray', '2023-05-20'),
(1, 6, 'DVD', '2023-06-18'),
(1, 7, 'DVD', '2023-07-01'),
(1, 8, 'Blu-ray', '2023-08-22'),
(1, 9, 'Blu-ray', '2023-09-10'),
(2, 9, 'DVD', '2023-09-15'),
(1, 10, 'DVD', '2023-10-05'),
(1, 11, 'Blu-ray', '2023-11-12'),
(1, 12, 'DVD', '2023-12-01');

-- =========================================================================
-- 6. UNOS PODATAKA U TABELU: iznajmljivanje (15 unosa)
--    - IdZad (Zaposleni koji izdaje) -> aktivni ID-evi iz `zaposleni` (npr. 1-5, 7-9)
--    - IdRazd (Zaposleni koji razdužuje) -> može biti i NULL, ili aktivni zaposleni
--    - IdCl (Član koji iznajmljuje) -> ID-evi iz tabele `clan` (16-30)
--    - BrojKopije i IdFilma -> moraju postojati u `video_artikal`
-- =========================================================================
INSERT INTO iznajmljivanje (DatumIznajmljivanja, DatumVracanja, IdZad, IdRazd, IdCl, BrojKopije, IdFilma) VALUES
('2024-01-05', '2024-01-12', 1, 1, 16, 1, 1),
('2024-01-10', '2024-01-15', 2, 2, 17, 2, 1),
('2024-01-15', '2024-01-22', 3, 1, 18, 1, 2),
('2024-02-01', '2024-02-07', 4, 4, 19, 1, 3),
('2024-02-10', '2024-02-17', 5, 2, 20, 1, 4),
('2024-02-20', '2024-02-25', 1, 1, 21, 2, 4),
('2024-03-01', '2024-03-08', 7, 7, 22, 1, 5),
('2024-03-05', '2024-03-12', 8, 9, 23, 1, 6),
('2024-03-15', '2024-03-20', 9, 1, 24, 1, 7),
('2024-04-01', '2024-04-10', 11, 11, 25, 1, 8),
('2024-04-15', '2024-04-22', 12, 13, 26, 1, 9),
('2024-05-01', '2024-05-06', 13, 12, 27, 2, 9),
('2024-05-10', '2024-05-17', 14, 14, 28, 1, 10),
-- Primjer gdje film još uvijek NIJE vraćen (DatumVracanja je NULL, IdRazd je NULL)
('2024-05-25', NULL, 15, NULL, 29, 1, 11),
('2024-05-26', NULL, 2, NULL, 30, 1, 12);


-- -----------------------------------------------------------------------------------------------------------


-- SQL upit kojim se za svaki nevraćeni video artikal prikazuje identifikator i naslov filma, 
-- broj kopije, vrsta medija, datum iznajmljivanja te broj članske karte, ime i prezime člana koji je iznajmio 
-- dati video artikal, počevši od video artikla koji je najduže iznajmljen.

select vaf.IdFilma, vaf.Naslov, vaf.BrojKopije, vaf.VrstaMedija, i.DatumIznajmljivanja, c.BrojClanskeKarte, Ime, Prezime
from osoba o 
inner join clan c on c.IdOsobe=o.IdOsobe
inner join iznajmljivanje i on i.IdCl=o.IdOsobe
inner join (
	select f.IdFilma, f.Naslov, va.BrojKopije, va.VrstaMedija
    from video_artikal va 
    inner join film f on f.IdFilma=va.IdFilma
    ) vaf on (vaf.BrojKopije, vaf.IdFilma)=(i.BrojKopije, i.IdFilma)
where DatumVracanja is null
order by DatumIznajmljivanja asc;

-- SQL upit kojim se za svaki film prikazuje identifikator, naziv, opis i 
-- ukupan broj iznajmljivanja, počevši od filma sa najviše iznajmljivanja.

select f.IdFilma, f.Naslov, f.Opis, ifnull(count(i.IdIznajmljivanja), 0) as IznajmljenoUkupno
from film f 
left outer join video_artikal va on va.IdFilma=f.IdFilma
left outer join iznajmljivanje i on (i.BrojKopije, i.IdFilma)=(va.BrojKopije, va.IdFilma)
group by f.IdFilma
order by IznajmljenoUkupno desc;

-- SQL iskaz kojim se kreira pogled koji sadrži sledeće podatke o svakom aktivnom zaposlenom: 
-- identifikator, ime, prezime, datum rođenja i broj članske karte (za zaposlene koji su ujedno i članovi).

create view pogled(vIdOsobe, vIme, vPrezime, vDatumRodjenja, vBrojClanskeKarte) as
	select o.IdOsobe, Ime, Prezime, DatumRodjenja, ifnull(c.BrojClanskeKarte, "-")
    from osoba o 
    inner join zaposleni z on z.IdOsobe=o.IdOsobe
    left outer join clan c on c.IdOsobe=o.IdOsobe
    where z.Aktivan is true;


-- ==========================================================
-- 1. KORAK: Unos novih osoba u tabelu 'osoba'
-- ==========================================================
INSERT INTO osoba (Ime, Prezime, DatumRodjenja) VALUES
('Nikolina', 'Zarić', '1991-04-12'), -- Dobija npr. IdOsobe: 36
('Mladen', 'Gajić', '1988-12-05');    -- Dobija npr. IdOsobe: 37

insert into osoba(Ime, Prezime, DatumRodjenja) values
("Pero", "Perovic",'1988-12-05');

-- ==========================================================
-- 2. KORAK: Unos istih tih osoba u tabelu 'zaposleni'
-- ==========================================================
INSERT INTO zaposleni (IdOsobe, Plata, Aktivan) VALUES
(36, 950.00, 1), -- Nikolina je AKTIVAN zaposleni
(37, 880.00, 0); -- Mladen je NEAKTIVAN zaposleni

insert into zaposleni(IdOsobe, Plata, Aktivan) values
(38,2000.00, 1);

-- ==========================================================
-- 3. KORAK: Unos ISTIH tih osoba u tabelu 'clan'
-- ==========================================================
INSERT INTO clan (IdOsobe, BrojClanskeKarte, DatumUclanjivanja) VALUES
(36, 2001, '2025-01-10'), -- Nikolina ima člansku kartu 2001
(37, 2002, '2025-02-15'); -- Mladen ima člansku kartu 2002

insert into clan(IdOsobe, BrojClanskeKarte, DatumUclanjivanja) values
(38,2003, '2025-02-15');

select * from pogled;








