create schema predavanja default character set utf8 default collate utf8_unicode_ci;
use predavanja;

create table seminar(
	IdSeminara int primary key,
    Naziv varchar(50) not null,
    Od date not null,
    DoDatum date not null
);

create table osoba(
	IdOsobe int primary key,
    Ime varchar(50) not null,
    Prezime varchar(50) not null
);

create table predavac(
	IdOsobe int primary key,
    constraint FK_predavac_osoba
    foreign key(IdOsobe) references osoba(IdOsobe)
);

create table predavanje(
	IdPredavanja int primary key,
    Naziv varchar(50) not null,
    Datum date not null,
    IdSeminara int not null,
    IdPredavaca int not null,
    constraint FK_p_seminar
    foreign key(IdSeminara) references seminar(IdSeminara),
    constraint FK_p_predavac
    foreign key(IdPredavaca) references predavac(IdOsobe)
);

create table prijava(
	IdOsobe int,
    IdSeminara int,
    primary key(IdOsobe, IdSeminara),
    constraint FK_p_o
    foreign key(IdOsobe) references osoba(IdOsobe),
    constraint FK_p_s
    foreign key(IdSeminara) references seminar(IdSeminara)
);

create table prisustvo(
	IdOsobe int,
    IdPredavanja int,
    primary key(IdOsobe, IdPredavanja),
    constraint FK_p_osoba
    foreign key(IdOsobe) references osoba(IdOsobe),
    constraint FK_p_predavanje
    foreign key(IdPredavanja) references predavanje(IdPredavanja)
);

-- ==========================================
-- 1. PUNJENJE TABELE: seminar
-- ==========================================
-- Tri različita seminara sa različitim trajanjem u 2026. godini
INSERT INTO seminar (IdSeminara, Naziv, Od, DoDatum) VALUES
(1, 'Web Razvoj 2026', '2026-03-01', '2026-03-05'),
(2, 'Uvod u AI i ML', '2026-04-10', '2026-04-12'),
(3, 'Soft Skills Akademija', '2026-05-20', '2026-05-22');

-- ==========================================
-- 2. PUNJENJE TABELE: osoba
-- ==========================================
-- Miks predavača i običnih polaznika (ukupno 10 osoba)
INSERT INTO osoba (IdOsobe, Ime, Prezime) VALUES
(1, 'Marko', 'Marić'),     -- Predavač 1
(2, 'Ana', 'Horvat'),       -- Predavač 2
(3, 'Ivan', 'Kovačić'),    -- Predavač 3
(4, 'Petra', 'Knez'),       -- Polaznik
(5, 'Luka', 'Babić'),       -- Polaznik
(6, 'Elena', 'Jurić'),      -- Polaznik
(7, 'Filip', 'Novak'),      -- Polaznik
(8, 'Lana', 'Zorić'),       -- Polaznik
(9, 'Igor', 'Tomić'),       -- Polaznik
(10, 'Mia', 'Brkić');       -- Polaznik

-- ==========================================
-- 3. PUNJENJE TABELE: predavac
-- ==========================================
-- Mapiramo prve tri osobe kao predavače
INSERT INTO predavac (IdOsobe) VALUES
(1),
(2),
(3);

-- ==========================================
-- 4. PUNJENJE TABELE: predavanje
-- ==========================================
-- Datumi predavanja se savršeno uklapaju u periode seminara.
-- Različiti predavači drže predavanja na istom seminaru.
INSERT INTO predavanje (IdPredavanja, Naziv, Datum, IdSeminara, IdPredavaca) VALUES
-- Predavanja za Seminar 1 (Web Razvoj: 03-01 do 03-05)
(101, 'HTML & CSS Osnove', '2026-03-01', 1, 1),
(102, 'Uvod u JavaScript', '2026-03-02', 1, 1),
(103, 'React Komponente', '2026-03-04', 1, 2),

-- Predavanja za Seminar 2 (AI i ML: 04-10 do 04-12)
(201, 'Python za Data Sci.', '2026-04-10', 2, 3),
(202, 'Neuralne Mreže', '2026-04-11', 2, 3),

-- Predavanja za Seminar 3 (Soft Skills: 05-20 do 05-22)
(301, 'Uspješna Komunikacija', '2026-05-20', 3, 2),
(302, 'Upravljanje Vremenom', '2026-05-21', 3, 1);

-- ==========================================
-- 5. PUNJENJE TABELE: prijava
-- ==========================================
-- Simulacija prijava polaznika na seminare (neki slušaju više seminara)
INSERT INTO prijava (IdOsobe, IdSeminara) VALUES
-- Prijave za Seminar 1
(4, 1), (5, 1), (6, 1), (7, 1),
-- Prijave za Seminar 2
(5, 2), (8, 2), (9, 2), (10, 2),
-- Prijave za Seminar 3
(4, 3), (6, 3), (8, 3), (10, 3);

-- ==========================================
-- 6. PUNJENJE TABELE: prisustvo
-- ==========================================
-- Raznovrsna evidencija: Neko je došao na sve, neko je preskočio poneko predavanje
INSERT INTO prisustvo (IdOsobe, IdPredavanja) VALUES
-- Predavanje 101 (Svi došli osim Filipa - 7)
(4, 101), (5, 101), (6, 101),
-- Predavanje 102 (Svi došli osim Elene - 6)
(4, 102), (5, 102), (7, 102),
-- Predavanje 103 (Svi prisutni)
(4, 103), (5, 103), (6, 103), (7, 103),

-- Predavanje 201
(5, 201), (8, 201), (9, 201), -- Mia (10) opravdano odsutna
-- Predavanje 202
(5, 202), (8, 202), (9, 202), (10, 202),

-- Predavanje 301
(4, 301), (6, 301), (8, 301), (10, 301),
-- Predavanje 302 (Samo Lana i Mia došle)
(8, 302), (10, 302);


-- ==========================================
-- 1. DODATNI SEMINARI (Prošlost i Budućnost)
-- ==========================================
INSERT INTO seminar (IdSeminara, Naziv, Od, DoDatum) VALUES
-- Seminar iz prošlosti (2025. godina)
(4, 'Uvod u SQL (2025)', '2025-11-10', '2025-11-12'),

-- Seminari u budućnosti (u odnosu na maj 2026.)
(5, 'DevOps i Cloud Osnove', '2026-06-15', '2026-06-18'),
(6, 'Napredni React i Next.js', '2026-09-01', '2026-09-05');


-- ==========================================
-- 2. DODATNE OSOBE
-- ==========================================
INSERT INTO osoba (IdOsobe, Ime, Prezime) VALUES
(11, 'Amar', 'Hadžić'),    -- Novi polaznik
(12, 'Sanja', 'Nikolić');  -- Novi predavač


-- ==========================================
-- 3. DODATNI PREDAVAČI
-- ==========================================
INSERT INTO predavac (IdOsobe) VALUES
(12); -- Sanja postaje zvanični predavač


-- ==========================================
-- 4. DODATNA PREDAVANJA
-- ==========================================
INSERT INTO predavanje (IdPredavanja, Naziv, Datum, IdSeminara, IdPredavaca) VALUES
-- Predavanje za prošli seminar (IdSeminara: 4)
(401, 'Osnove baza podataka', '2025-11-10', 4, 1),

-- Predavanja za buduće seminare (IdSeminara: 5 i 6)
(501, 'Docker i Kontejneri', '2026-06-16', 5, 12),
(601, 'Next.js App Router', '2026-09-02', 6, 2);


-- ==========================================
-- 5. DODATNE PRIJAVE
-- ==========================================
-- Polaznici se regularno prijavljuju za buduće seminare unaprijed
INSERT INTO prijava (IdOsobe, IdSeminara) VALUES
-- Prijava za prošli seminar
(11, 4),

-- Prijave za buduće seminare (ljudi planiraju unaprijed)
(4, 5),   -- Petra se prijavila za DevOps
(11, 5),  -- Amar se prijavio za DevOps
(6, 6);   -- Elena se prijavila za Next.js


-- ==========================================
-- 6. DODATNA PRISUSTVA
-- ==========================================
-- VAŽNO: Prisustvo bilježimo SAMO za predavanje iz prošlosti (401).
-- Buduća predavanja (501, 601) NE SMIJU imati zapise ovdje jer tek treba da se održe!
INSERT INTO prisustvo (IdOsobe, IdPredavanja) VALUES
(11, 401);


---------------------------------------------------------------------------------------------------------------
-- SQL upit kojim se prikazuje ukupan broj održanih seminara (voditi računa o tome da se seminari planiraju unapred).
select count(*) as UkupanBrojOdrzanih
from seminar
where DoDatum<curdate();

-- SQL upit kojim se za svako predavanje koje se održava tekućeg dana prikazuje naziv
-- seminara, naziv predavanja te ime i prezime predavača.
select s.Naziv, p.Naziv, Ime, Prezime
from predavanje p 
inner join seminar s on s.IdSeminara=p.IdSeminara
inner join (
	select o.IdOsobe, o.Ime, o.Prezime from osoba o 
    inner join predavac p on p.IdOsobe=o.IdOsobe
    ) pr on p.IdPredavaca=pr.IdOsobe
where p.Datum=curdate();

-- ili

SELECT s.Naziv AS Seminar, p.Naziv AS Predavanje, o.Ime, o.Prezime
FROM predavanje p 
INNER JOIN seminar s ON s.IdSeminara = p.IdSeminara
INNER JOIN predavac pr ON p.IdPredavaca = pr.IdOsobe
INNER JOIN osoba o ON pr.IdOsobe = o.IdOsobe
WHERE p.Datum = CURDATE(); 

-- SQL upit kojim se za svaki predstojeći seminar prikazuje identifikator, naziv, datum početka
-- (od), datum završetka (do) te ukupan broj prijavljenih osoba, počevši od najskorijeg seminara.

select s.IdSeminara, Naziv, Od as DatumPocetka, DoDatum as DatumZavrsetka, ifnull(count(p.IdOsobe), 0) as BrojPrijavljenih
from prijava p 
left outer join seminar s on p.IdSeminara=s.IdSeminara
inner join osoba o on p.IdOsobe=o.IdOsobe
where Od>=curdate()
group by s.IdSeminara
order by Od;

-- SQL iskaz kojim se kreira uskladištena procedura koja proverava i vraća (logička vrednost,
-- izlazni parametar procedure) informaciju o tome da li je osoba (identifikator osobe je prvi ulazni
-- parametar procedure) prijavljena na seminar kojem pripada predavanje čiji je identifikator drugi
-- ulazni parametar procedure. Dakle, ulazni parametri procedure su identifikator osobe i
-- identifikator predavanja

delimiter $$
create procedure procedura(in pIdOsobe int, in pIdPredavanja int, out pPrijavljena bool)
	begin
		declare vIdSeminara int;
        set pPrijavljena = false;
        
        select IdSeminara into vIdSeminara
        from predavanje
        where IdPredavanja=pIdPredavanja;
        
        if vIdSeminara is not null and exists (
			select * from prijava
            where IdSeminara=vIdSeminara and IdOsobe= pIdOsobe
		) then
			set pPrijavljena = true;
            
		end if;
	end $$
delimiter ;
drop procedure procedura;
-- SQL iskaz kojim se kreira triger koji prilikom evidentiranja prisustva (na predavanju)
-- proverava da li je osoba prijavljena na seminar. Ako osoba nije prijavljena na seminar, potrebno
-- je signalizirati grešku sa sqlstate vrednošću '45000' te odgovarajućom porukom.

delimiter $$
create trigger triger before insert on prisustvo
	for each row
    begin
		declare prijavljena bool;
        call procedura(new.IdOsobe, new.IdPredavanja, prijavljena);
        if prijavljena is not true then
			signal sqlstate '45000'
            set message_text='Osoba nie prijavljena.';
		end if;
	end $$
delimiter ;
drop trigger triger;

INSERT INTO prisustvo (IdOsobe, IdPredavanja) 
VALUES (4, 201);
INSERT INTO prisustvo (IdOsobe, IdPredavanja) 
VALUES (11, 401);

SELECT * FROM prisustvo;
delete from prisustvo where IdOsobe=11;

