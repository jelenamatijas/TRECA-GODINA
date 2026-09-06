create schema koncert default character set utf8 
default collate utf8_unicode_ci;

use koncert;

create table organizator(
	IdOrg int auto_increment primary key,
    Naziv varchar(20) not null
);

create table lokacija(
	IdLokacije int auto_increment primary key,
    Naziv varchar(20) not null,
    Adresa varchar(50) not null
);

create table dogadjaj(
	IdDogadjaja int auto_increment primary key,
    Naziv varchar(50) not null,
    Opis varchar(100) not null,
    DatumVrijeme datetime not null,
    IdLokacije int not null,
    constraint FK_dogadjaj_lokacija
    foreign key(IdLokacije) references lokacija(IdLokacije)
);

create table org_dog(
	IdOrg int,
    IdDogadjaja int,
    primary key(IdOrg, IdDogadjaja),
    constraint FK_orgdog_org
    foreign key(IdOrg) references organizator(IdOrg),
    constraint FK_orgdog_dog
    foreign key(IdDogadjaja) references dogadjaj(IdDogadjaja)
);

create table zona(
	NazivZone varchar(20),
    IdDogadjaja int not null,
    CojenaKarte int not null,
    BrojMjesta int not null,
    primary key(NazivZone, IdDogadjaja),
    constraint FK_zona_dogadjaj
    foreign key(IdDogadjaja) references dogadjaj(IdDogadjaja)
);

create table karta(
	BrojKarte int auto_increment primary key,
    NazivZone varchar(20) not null,
    IdDogadjaja int not null,
    constraint FK_karta_zona
    foreign key(NazivZone, IdDogadjaja) references zona(NazivZone, IdDogadjaja)
);

-- ----------------------------------------------------------------------------------------------------------------------

-- 1. Unos organizatora (IdOrg se generiše automatski: 1, 2, 3...)
INSERT INTO organizator (Naziv) VALUES
('Live Nation'),    -- ID: 1
('Eventim'),        -- ID: 2
('KupiKartu'),      -- ID: 3
('Promotim'),       -- ID: 4
('Balkan Fun');     -- ID: 5

-- 2. Unos lokacija (IdLokacije se generiše automatski: 1, 2, 3...)
INSERT INTO lokacija (Naziv, Adresa) VALUES
('Arena Zagreb', 'Lanište 30, Zagreb'),                       -- ID: 1
('Stark Arena', 'Bulevar Arsenija Čarnojevića 58, Beograd'), -- ID: 2
('Zetra', 'Alipašina bb, Sarajevo'),                          -- ID: 3
('Borik', 'Aleja Svetog Save, Banja Luka'),                  -- ID: 4
('Tvrdjava Kastel', 'Trg srpskih vladara, Banja Luka');       -- ID: 5

-- 3. Unos događaja (IdDogadjaja se generiše automatski: 1, 2, 3...)
-- Kolona IdLokacije koristi ID-ove koje je baza gore automatski dodijelila
INSERT INTO dogadjaj (Naziv, Opis, DatumVrijeme, IdLokacije) VALUES
('Dino Merlin', 'Turneja 2026', '2026-06-15 20:00:00', 1),           -- ID: 1 (Lokacija: Arena Zagreb)
('Zdravko Colic', 'Veliki letnji koncert', '2026-07-20 21:00:00', 2), -- ID: 2 (Lokacija: Stark Arena)
('Dubioza Kolektiv', 'Promocija novog albuma', '2026-08-10 20:30:00', 3), -- ID: 3 (Lokacija: Zetra)
('Gibonni', 'Unplugged koncert', '2026-10-12 21:00:00', 4),          -- ID: 4 (Lokacija: Borik)
('Bijelo Dugme', '50 godina benda', '2026-07-15 22:00:00', 5);        -- ID: 5 (Lokacija: Kastel)

-- 4. Unos u veznu tabelu org_dog
-- Sparujemo automatski generisane ID-ove organizatora i događaja
INSERT INTO org_dog (IdOrg, IdDogadjaja) VALUES
(1, 1), -- Live Nation organizuje Dina Merlina
(2, 2), -- Eventim organizuje Zdravka Čolića
(3, 3), -- KupiKartu organizuje Dubiozu
(4, 4), -- Promotim organizuje Gibonnija
(5, 5); -- Balkan Fun organizuje Bijelo Dugme

-- 5. Unos zona za događaje
-- Koristimo generisane ID-ove događaja (1, 2, 3, 4, 5)
INSERT INTO zona (NazivZone, IdDogadjaja, CojenaKarte, BrojMjesta) VALUES
('VIP', 1, 150, 100),
('Parter', 1, 50, 2000),
('Tribina', 1, 70, 1500),
('VIP', 2, 200, 50),
('Fan Pit', 2, 100, 500),
('Parter', 3, 40, 3000),
('VIP', 4, 120, 30),
('Fan Pit', 5, 80, 800);

-- 6. Unos karata (BrojKarte se generiše automatski: 1, 2, 3...)
-- Sparujemo sa tačnim kombinacijama NazivZone i IdDogadjaja koje postoje u tabeli 'zona'
INSERT INTO karta (NazivZone, IdDogadjaja) VALUES
('VIP', 1),     -- BrojKarte: 1
('VIP', 1),     -- BrojKarte: 2
('Parter', 1),  -- BrojKarte: 3
('VIP', 2),     -- BrojKarte: 4
('Fan Pit', 2), -- BrojKarte: 5
('Parter', 3),  -- BrojKarte: 6
('VIP', 4),     -- BrojKarte: 7
('Fan Pit', 5); -- BrojKarte: 8
-- ----------------------------------------------------------------------------------------------------------------------


-- SQL upit kojim se prikazuje ukupan broj predstojećih događaja.

select count(*) as UkupnoPredstojecihDogadjaja
from dogadjaj
where DatumVrijeme>now();

-- SQL upit kojim se za organizatore koji su organizovali barem jedan događaj prikazuje identifikator, naziv i ukupan broj organizovanih događaja.

select o.IdOrg, Naziv, count(od.IdDogadjaja) as Organizovano
from organizator o 
inner join org_dog od using(IdOrg)
group by o.IdOrg;

-- SQL upit kojim se za svaki događaj prikazuje identifikator, naziv, opis, datum 
-- i vreme održavanja, naziv lokacije, adresa održavanja te ukupan broj prodatih karata, 
-- sortirano opadajuće po datumu i vremenu održavanja.

select d.IdDogadjaja, d.Naziv, d.Opis, d.DatumVrijeme, l.Naziv, l.Adresa, count(k.IdDogadjaja) as Prodano
from dogadjaj d 
inner join lokacija l using(IdLokacije)
left outer join karta k on d.IdDogadjaja=k.IdDogadjaja
group by d.IdDogadjaja
order by d.DatumVrijeme desc;

-- SQL iskaz kojim se kreira indeks po nazivu događaja.
create index IX_dogadjaj on dogadjaj(Naziv);

-- SQL iskaz kojim se kreira triger koji prilikom evidentiranja nove karte proverava da li ima slobodnog 
-- mesta u zoni za koju se prodaje karta. Ako nema slobodnog mesta, potrebno je signalizirati grešku sa 
-- sqlstate vrednošću '45000' te odgovarajućom porukom. Atribut BrojMesta entitetskog tipa ZONA predstavlja 
-- ukupan broj mesta (broj slobodnih mesta + broj zauzetih mesta) u datoj zoni.

delimiter $$
create trigger triger before insert on karta
for each row
	begin
		declare vBrojZauzetih int default 0;
        declare vUkupno int default 0;
        
        select count(*) into vBrojZauzetih
        from karta
        where NazivZone=new.NazivZone and IdDogadjaja=new.IdDogadjaja;
        
        select BrojMjesta into vUkupno 
        from zona z
        where z.NazivZone=new.NazivZone and z.IdDogadjaja=new.IdDogadjaja;
        
        if vUkupno <= vBrojZauzetih then
			signal sqlstate '45000'
            set message_text='Nema slobodnih mjesta u odabranoj zoni.';
		end if;
    end $$
delimiter ;

INSERT INTO zona (NazivZone, IdDogadjaja, CojenaKarte, BrojMjesta) VALUES
('mojazona', 1, 100, 2);

INSERT INTO karta (NazivZone, IdDogadjaja) VALUES
('mojazona', 1);