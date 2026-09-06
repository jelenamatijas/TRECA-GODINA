create schema polaganje default character set utf8 
default collate utf8_unicode_ci;
use polaganje;

create table student(
	BrojIndeksa varchar(10) primary key,
    Prezime varchar(20) not null,
    Ime varchar(20) not null
);

create table predmet(
	Sifra varchar(10) primary key,
    Naziv varchar(30) not null,
    ECTS int not null
);

create table nastavnik(
	JMB varchar(13) primary key,
    Prezime varchar(20) not null,
    Ime varchar(20) not null
);

create table ispit(
	DatumIspita date,
    Sifra varchar(10),
    JMB varchar(13) not null,
    primary key(DatumIspita, Sifra),
    constraint fk_ispit_predmet
    foreign key(Sifra) references predmet(Sifra),
    constraint fk_ispit_nastavnik
    foreign key(JMB) references nastavnik(JMB)
);

create table polaze(
	BrojIndeksa varchar(10),
    DatumIspita date,
    Sifra varchar(10),
    Ocjena int not null,
    primary key(BrojIndeksa, DatumIspita, Sifra),
    constraint fk_polaze_student
    foreign key(BrojIndeksa) references student(BrojIndeksa),
    constraint fk_polaze_ispit
    foreign key(DatumIspita, Sifra) references Ispit(DatumIspita, Sifra)
);

create table komisijski(
	BrojIndeksa varchar(10),
    DatumIspita date,
    Sifra varchar(10),
    Prvi varchar(13) not null,
    Drugi varchar(13) not null,
    primary key(BrojIndeksa, DatumIspita, Sifra),
    constraint fk_komisijski_polaze
    foreign key(BrojIndeksa, DatumIspita, Sifra) references polaze(BrojIndeksa, DatumIspita, Sifra),
    constraint fk_komisijski_nastavnik_1
    foreign key(Prvi) references nastavnik(JMB),
    constraint fk_komisijski_nastavnik_2
    foreign key(Drugi) references nastavnik(JMB)
);

-- ---------------------------------------------------------------------------------------------------------

-- 1. Unos studenata (BrojIndeksa je PK)
INSERT INTO student (BrojIndeksa, Prezime, Ime) VALUES
('10/22', 'Markovic', 'Marko'),
('15/22', 'Jovanovic', 'Jelena'),
('21/21', 'Petrovic', 'Petar'),
('05/23', 'Nikolic', 'Nikola');

-- 2. Unos predmeta (Sifra je PK)
INSERT INTO predmet (Sifra, Naziv, ECTS) VALUES
('BPD', 'Baze podataka', 6),
('PRG1', 'Programiranje 1', 8),
('MAT', 'Matematika', 7),
('RM', 'Racunarske mreze', 5);

-- 3. Unos nastavnika (JMB je PK - mora imati tačno ili do 13 karaktera kako je definisano u šemi)
INSERT INTO nastavnik (JMB, Prezime, Ime) VALUES
('1010975150011', 'Savic', 'Sava'),
('1505980150022', 'Ristic', 'Rada'),
('2003972150033', 'Ilic', 'Ivan'),
('1212985150044', 'Kostic', 'Kosta'),
('0504978150055', 'Simic', 'Sima');

-- 4. Unos ispita (DatumIspita i Sifra čine PK, JMB je strani ključ na nastavnika koji drži ispit)
INSERT INTO ispit (DatumIspita, Sifra, JMB) VALUES
('2026-01-15', 'BPD', '1010975150011'), -- Sava Savic drži Baze u januaru
('2026-01-20', 'PRG1', '1505980150022'), -- Rada Ristic drži Programiranje 1
('2026-02-05', 'MAT', '2003972150033'),  -- Ivan Ilic drži Matematiku
('2026-02-10', 'BPD', '1010975150011'), -- Sava Savic drži Baze u februaru
('2026-06-15', 'RM', '1212985150044');  -- Kosta Kostic drži Mreže

-- 5. Unos polaganja (Studenti izlaze na ispite i dobijaju ocjene)
-- Kombinacija BrojIndeksa, DatumIspita i Sifra mora postojati u tabelama student i ispit
INSERT INTO polaze (BrojIndeksa, DatumIspita, Sifra, Ocjena) VALUES
('10/22', '2026-01-15', 'BPD', 8),  -- Marko je položio Baze
('15/22', '2026-01-15', 'BPD', 5),  -- Jelena je pala Baze u januaru
('21/21', '2026-01-20', 'PRG1', 9), -- Petar je dobio 9 iz Programiranja
('05/23', '2026-02-05', 'MAT', 6),  -- Nikola je dobio 6 iz Matematike
('15/22', '2026-02-10', 'BPD', 5),  -- Jelena opet izlazi na Baze u februaru (komisijski rok)
('10/22', '2026-06-15', 'RM', 10);  -- Marko je razbio Mreže

-- 6. Unos komisijskog ispita 
-- Referenciramo polaganje Jelene iz februara ('15/22', '2026-02-10', 'BPD'). 
-- 'Prvi' i 'Drugi' član komisije moraju biti JMB-ovi postojećih nastavnika.
INSERT INTO komisijski (BrojIndeksa, DatumIspita, Sifra, Prvi, Drugi) VALUES
('15/22', '2026-02-10', 'BPD', '2003972150033', '1212985150044'); 
-- Članovi komisije Jeleni su Ivan Ilic i Kosta Kostic

-- ---------------------------------------------------------------------------------------------------------

-- SQL upit kojim se za svaki predstojeći ispit prikazuje šifra predmeta, naziv predmeta, datum ispita 
-- te ime i prezime zaduženog nastavnika, počevši od najskorijeg ispita (ispite koji se održavaju isti dan sortirati rastuće po nazivu).

select i.Sifra, Naziv, DatumIspita, Ime, Prezime
from ispit i 
inner join predmet p on i.Sifra=p.Sifra
inner join nastavnik n on i.JMB=n.JMB
where DatumIspita > now()
order by DatumIspita asc, Naziv asc;

-- SQL upit kojim se prikazuje ukupan broj ispita u tekućoj godini sa barem jednim komisijskim polaganjem.

select count(*) as Ukupno
from polaze p 
left outer join ispit i on (i.DatumIspita,i.Sifra)=(p.DatumIspita, p.Sifra)
inner join komisijski k on (k.BrojIndeksa, k.DatumIspita,k.Sifra)=(p.BrojIndeksa, p.DatumIspita, p.Sifra)
where year(p.DatumIspita)=year(now());

-- ili

SELECT COUNT(*) AS Ukupno
FROM ispit i
WHERE YEAR(i.DatumIspita) = YEAR(CURDATE())
  AND EXISTS (
      SELECT 1 
      FROM komisijski k 
      WHERE k.DatumIspita = i.DatumIspita AND k.Sifra = i.Sifra
  );
  
-- ili

SELECT COUNT(DISTINCT DatumIspita, Sifra) AS Ukupno
FROM komisijski
WHERE YEAR(DatumIspita) = YEAR(CURDATE());

-- SQL upit kojim se za svakog studenta prikazuje broj indeksa, 
-- ime i prezime te prosečna ocena, počevši od studenta sa najvećom prosečnom ocenom.

select s.BrojIndeksa, s.Ime, s.Prezime, avg(p.Ocjena) as Prosjek
from student s
left outer join polaze p on p.BrojIndeksa=s.BrojIndeksa
group by s.BrojIndeksa
order by Prosjek desc;

-- SQL iskaz kojim se kreira uskladištena procedura koja proverava i vraća 
-- (logička vrednost, izlazni parametar procedure) informaciju o tome da li je student 
-- (broj indeksa je prvi ulazni parametar procedure) položio predmet čija je šifra drugi ulazni 
-- parametar procedure. Dakle, ulazni parametri procedure su broj indeksa i šifra predmeta.

delimiter $$
create procedure procedura(
	in pBrojIndeksa varchar(10),
    in pSifra varchar(10),
    out pPolozio bool)
    begin
		set pPolozio = false;
        if exists(select * from polaze where BrojIndeksa=pBrojIndeksa and Sifra=pSifra and Ocjena>5) then
			set pPolozio = true;
		end if;
    end$$
delimiter ;

call procedura('10/22', 'BPD', @p);
select @p;

-- SQL iskaz kojim se kreira triger koji prilikom evidentiranja izlaska na ispit (polaganje ispita) 
-- proverava da li je student ranije položio dati predmet. Ako je student ranije položio predmet, 
-- potrebno je signalizirati grešku sa sqlstate vrednošću '45000' te odgovarajućom porukom.

delimiter $$
create trigger triger before insert on polaze
for each row
	begin
		declare vPolozio bool;
        call procedura(new.BrojIndeksa, new.Sifra, vPolozio);
        if vPolozio is true then
			signal sqlstate '45000'
            set message_text = 'Student je vec polozio predmet.';
		end if;
    end $$
delimiter ;

INSERT INTO polaze (BrojIndeksa, DatumIspita, Sifra, Ocjena) VALUES
('10/22', '2026-01-15', 'BPD', 8);
INSERT INTO polaze (BrojIndeksa, DatumIspita, Sifra, Ocjena) VALUES
('10/22', '2026-01-20', 'PRG1', 8);