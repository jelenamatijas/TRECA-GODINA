create schema narucivanje default character set utf8
default collate utf8_unicode_ci;
use narucivanje;

create table kupac(
	IdKupca int primary key,
    Ime varchar(50) not null,
    Adresa varchar(50) not null
);

create table pica(
	IdPice int primary key,
    Naziv varchar(50) not null,
    Opis varchar(50) not null,
    Cijena double not null
);

create table dodatak(
	IdDodatka int primary key,
    Naziv varchar(50) not null
);

create table narudzba(
	IdNarudzbe int primary key,
    DatumVrijeme datetime not null,
    UkupanIznos double not null,
    IdKupca int not null,
    constraint FK_narudzba_kupac
    foreign key(IdKupca) references kupac(IdKupca)
);

create table stavka(
	BrojStavke int,
    IdNarudzbe int,
    Cijena double not null,
    IdPice int not null,
    primary key(BrojStavke, IdNarudzbe),
    constraint FK_stavka_narudzba
    foreign key(IdNarudzbe) references narudzba(IdNarudzbe),
    constraint FK_stavka_pica
    foreign key(IdPice) references pica(IdPice)
);

create table stavka_dodatak(
	IdDodatka int,
    BrojStavke int,
    IdNarudzbe int,
    primary key(IdDodatka, BrojStavke, IdNarudzbe),
    constraint FK_sd_d
    foreign key(IdDodatka) references dodatak(IdDodatka),
    constraint FK_sd_s
    foreign key(BrojStavke, IdNarudzbe) references stavka(BrojStavke, IdNarudzbe)
);

-- -------------------------------------------------------------------------------------------------------------------
USE narucivanje;

-- --------------------------------------------------------
-- 1. Kupci (Ostaje isto)
-- --------------------------------------------------------
INSERT INTO kupac (IdKupca, Ime, Adresa) VALUES
(1, 'Marko Marković', 'Gospodska 5'),
(2, 'Ana Anić', 'Aleja Svetog Save 12'),
(3, 'Petar Petrović', 'Bulevar cara Dušana 8'),
(4, 'Jovana Jovanović', 'Cara Lazara 45'),
(5, 'Milan Milić', 'Mladena Stojanovića 11'),
(6, 'Djordje Djordjević', 'Srpska 22'),
(7, 'Jelena Savić', 'Kralja Petra I 55'),
(8, 'Nikola Nikolić', 'Gundulićeva 3'),
(9, 'Stefan Stefanović', 'Jevrejska 18'),
(10, 'Ivana Ivanović', 'Bana Milosavljevića 9');

-- --------------------------------------------------------
-- 2. Pice (Ostaje isto)
-- --------------------------------------------------------
INSERT INTO pica (IdPice, Naziv, Opis, Cijena) VALUES
(1, 'Margarita', 'Pelat, sir', 10.00),
(2, 'Kaprićoza', 'Pelat, sir, šunka, šampinjoni', 12.00),
(3, 'Vezuvio', 'Pelat, sir, šunka', 11.00),
(4, 'Kvatro Formađi', 'Četiri vrste sira', 14.50),
(5, 'Mađarica', 'Pelat, sir, kulen, ljuta paprika', 13.00),
(6, 'Vegetarijana', 'Pelat, sir, sezonsko povrće', 11.50),
(7, 'Meksička', 'Pelat, sir, kukuruz, grah, kulen', 13.50),
(8, 'Havaji', 'Pelat, sir, šunka, ananas', 12.50),
(9, 'Peperoni', 'Pelat, sir, peperoni kobasica', 13.00),
(10, 'Specijal kuća', 'Sve od navedenog, duplo tijesto', 16.00);

-- --------------------------------------------------------
-- 3. Dodaci (Ostaje isto)
-- --------------------------------------------------------
INSERT INTO dodatak (IdDodatka, Naziv) VALUES
(1, 'Kečap'),
(2, 'Majoneza'),
(3, 'Origano'),
(4, 'Pavlaka'),
(5, 'Masline'),
(6, 'Kulen'),
(7, 'Šunka'),
(8, 'Jalapeno papričice'),
(9, 'Ekstra sir'),
(10, 'Rukola');

-- --------------------------------------------------------
-- 4. Narudžbe (Ostaje isto)
-- --------------------------------------------------------
INSERT INTO narudzba (IdNarudzbe, DatumVrijeme, UkupanIznos, IdKupca) VALUES
(1, '2026-05-28 10:15:00', 25.00, 1),
(2, '2026-05-28 10:45:00', 12.00, 2),
(3, '2026-05-28 11:30:00', 29.00, 3),
(4, '2026-05-27 15:20:00', 14.50, 4),
(5, '2026-05-27 18:05:00', 11.50, 5),
(6, '2026-05-26 12:00:00', 27.00, 6),
(7, '2026-05-26 19:30:00', 32.00, 7),
(8, '2026-05-25 14:15:00', 13.00, 8),
(9, '2026-05-24 20:40:00', 16.00, 9),
(10, '2026-05-24 22:10:00', 22.00, 10);

-- --------------------------------------------------------
-- 5. NOVI UNOSI ZA TABELU: stavka
-- Primijeti kako BrojStavke kreće od 1 unutar svake narudžbe
-- --------------------------------------------------------
INSERT INTO stavka (BrojStavke, IdNarudzbe, Cijena, IdPice) VALUES
(1, 1, 12.00, 2),  -- Narudžba 1, stavka br. 1 (Kaprićoza)
(2, 1, 13.00, 5),  -- Narudžba 1, stavka br. 2 (Mađarica)

(1, 2, 12.00, 2),  -- Narudžba 2, stavka br. 1 (Kaprićoza)

(1, 3, 14.50, 4),  -- Narudžba 3, stavka br. 1 (Kvatro Formađi)
(2, 3, 14.50, 4),  -- Narudžba 3, stavka br. 2 (Kvatro Formađi)

(1, 4, 14.50, 4),  -- Narudžba 4, stavka br. 1 (Kvatro Formađi)
(1, 5, 11.50, 6),  -- Narudžba 5, stavka br. 1 (Vegetarijana)
(1, 6, 13.50, 7),  -- Narudžba 6, stavka br. 1 (Meksička)
(1, 7, 16.00, 10), -- Narudžba 7, stavka br. 1 (Specijal kuća)
(1, 8, 13.00, 9);  -- Narudžba 8, stavka br. 1 (Peperoni)

-- --------------------------------------------------------
-- 6. NOVI UNOSI ZA TABELU: stavka_dodatak
-- Sada prosljeđujemo i IdNarudzbe da bismo znali tačan kontekst
-- --------------------------------------------------------
INSERT INTO stavka_dodatak (IdDodatka, BrojStavke, IdNarudzbe) VALUES
(1, 1, 1),   -- Kečap (1) na prvu stavku (1) prve narudžbe (1)
(4, 1, 1),   -- Pavlaka (4) takođe na prvu stavku (1) prve narudžbe (1)
(2, 2, 1),   -- Majoneza (2) na drugu stavku (2) prve narudžbe (1)

(3, 1, 2),   -- Origano (3) na prvu stavku (1) druge narudžbe (2)

(9, 1, 3),   -- Ekstra sir (9) na prvu stavku (1) treće narudžbe (3)
(5, 2, 3),   -- Masline (5) na drugu stavku (2) treće narudžbe (3)

(8, 1, 4),   -- Jalapeno (8) na prvu stavku (1) četvrte narudžbe (4)
(10, 1, 5),  -- Rukola (10) na prvu stavku (1) pete narudžbe (5)
(6, 1, 6),   -- Kulen (6) na prvu stavku (1) šeste narudžbe (6)
(7, 1, 7);   -- Šunka (7) na prvu stavku (1) sedme narudžbe (7)

-- -------------------------------------------------------------------------------------------------------------------

-- SQL upit kojim se prikazuje promet (ukupan iznos svih narudžbi) u tekućoj godini.

select sum(UkupanIznos) from narudzba
where year(DatumVrijeme)=year(curdate());

-- SQL upit kojim se za kupce koji imaju barem jednu narudžbu prikazuje identifikator, 
-- ime, adresa i ukupan iznos narudžbi, počevši od kupca sa najvećim ukupnim iznosom narudžbi. 

select k.IdKupca, Ime, Adresa, sum(UkupanIznos) as Ukupno
from kupac k
inner join narudzba n using(IdKupca)
group by k.IdKupca
order by Ukupno desc;

-- SQL upit kojim se za svaku picu prikazuje identifikator, naziv, broj prodatih 
-- pica te ukupan iznos prodatih pica, sortirano rastuće po nazivu pice.

select p.IdPice, Naziv, count(s.BrojStavke)as Prodano, ifnull(sum(s.Cijena), 0) as Iznos
from pica p 
left outer join stavka s using(IdPice)
group by p.IdPice
order by Naziv asc;

-- ili

SELECT p.IdPice, p.Naziv, count(s.BrojStavke) AS Prodano, ifnull(SUM(s.Cijena), 0) AS Iznos
FROM stavka s
RIGHT OUTER JOIN pica p USING(IdPice)
GROUP BY p.IdPice
ORDER BY p.Naziv ASC;

-- SQL iskaz kojim se kreira indeks po nazivu pice. 

create index IX_pica on pica(Naziv);

-- SQL iskaz kojim se kreira uskladištena procedura koja omogućava dodavanje stavke na narudžbu. 
-- Parametri procedure su identifikator narudžbe i identifikator pice. Voditi računa o postavljanju 
-- vrednosti za kolone BrojStavke (redni broj stavke na narudžbi) i Cena. U slučaju uspešnog dodavanja 
-- stavke na narudžbu, potrebno je ažurirati vrednost ukupnog iznosa date narudžbe.

delimiter $$
create procedure procedura(
	in pIdNarudzbe int,
	in pIdPice int)
    begin
		declare vCijena double;
        declare vBroj int;
        
        select Cijena into vCijena
        from pica
        where IdPice=pIdPice;
        
        select count(*) into vBroj
        from stavka
        where IdNarudzbe=pIdNarudzbe;
        
        set vBroj = vBroj+1;
        
        insert into stavka values(vBroj, pIdNarudzbe, vCijena, pIdPice);
        update narudzba set UkupanIznos = UkupanIznos + vCijena
        where IdNarudzbe=pIdNarudzbe;
	
    
    end $$
delimiter ;

call procedura(1,2);
select * from stavka where IdNarudzbe=3;
call procedura(1,5);
call procedura(3,10);




