select * from osoba;
select Prezime from osoba; 
select all Prezime from osoba;
select distinct Prezime from osoba;
select distinct Prezime, Ime from osoba;


select * from predmet where not ECTS=6 and NazivFakulteta='Elektrotehnički fakultet';
select * from osoba where Prezime not like '%ic';
select * from osoba where Prezime like '%ic';
select * from osoba where Ime like 'mi%';
select * from osoba where Ime like '%an%';
select * from osoba where Ime like '___';
select * from osoba where Ime like '____%';

select JMB, NastavnoZvanje, Plata/1.95 from nastavnik;
select JMB, NastavnoZvanje, Plata/1.95 as PlataEUR from nastavnik;
select JMB, NastavnoZvanje, Plata/1.95 as PlataEUR from nastavnik where Plata/1.95>1000;
select JMB, NastavnoZvanje, round(Plata/1.95,2) as PlataEUR from nastavnik where NastavnoZvanje like '%profesor';

select * from osoba where char_length(Ime)=3;
select JMB, concat(substr(Ime,1,1), '.', substr(Prezime, 1,1), '.') as Inicijal from osoba;

select Ime, Prezime from osoba where substr(Ime, 1, 1) = substr(Prezime, 1,1);
select * from osoba where substr(Ime, 2,2)='ir';
select * from osoba where DatumRodjenja=date'1983-08-03';
select * from osoba where DatumRodjenja between date'1983-01-01' and date'1983-12-31';
select JMB, date_format(DatumRodjenja, '%d.%m.%Y') from osoba;
select * from nastavnik where NastavnoZvanje like '%profesor' order by Plata desc;
select * from osoba order by Prezime desc, Ime desc; /*podrazumijevano adc*/
select * from nastavnik where JMBSefaKatedre is not null;

select 10 as k, 2 as t; 
select 1+null as t;
select * from osoba where false;
select 10 is not null as t;
select JMB, coalesce(JMBSefaKatedre, 'N-A') from nastavnik;

select avg(Plata) from nastavnik;
select sum(Plata) from nastavnik;
select min(Plata) from nastavnik;
select max(Plata) from nastavnik;
select count(*) from nastavnik where NastavnoZvanje='docent';
select count(JMBSefaKatedre) from nastavnik;
select JMBSefaKatedre is not null from nastavnik;
select count(JMBSefaKatedre is not null) from nastavnik;
select count(distinct JMBSefaKatedre is not null) from nastavnik;
select 1 from nastavnik;

select  NastavnoZvanje, avg(Plata), count(*) as BrojProfesora from nastavnik group by NastavnoZvanje;
select  NastavnoZvanje, avg(Plata), count(*) as BrojProfesora from nastavnik where NastavnoZvanje<>'docent' group by NastavnoZvanje;
select  NastavnoZvanje, avg(Plata), count(*) as BrojProfesora from nastavnik group by NastavnoZvanje having NastavnoZvanje='docent'; /*radi ali nije ispavno*/
select  NastavnoZvanje, avg(Plata), count(*) as BrojProfesora from nastavnik group by NastavnoZvanje having count(*)>2 order by 2 desc;

select * from nastavnik, osoba where nastavnik.JMB=osoba.JMB;
select * from nastavnik n, osoba o where n.JMB=o.JMB and NastavnoZvanje='docent';
select * from nastavnik n inner join osoba o on n.JMB=o.JMB where NastavnoZvanje='docent';
select * from nastavnik n inner join osoba o using(JMB) where NastavnoZvanje='docent'; /*moze ako su kolone na osnovu kojih spajamo iste*/
select * from nastavnik n natural join osoba o where NastavnoZvanje='docent';

/*****************************************************************************************************************************************/
select @@version;

select p.JMB, Ime, Prezime, p.IdSP, NazivSP, Ciklus, avg(Ocena) as Prosjek
from polaze p inner join osoba o on o.JMB=p.JMB inner join studijski_program sp on sp.IdSP=p.IdSP where Ocena>5 
group by p.JMB, p.IdSP;

select f.*, count(*) as UkupnoBrojeva from fakultet f inner join telefon_fakulteta tf on tf.NazivFakulteta=f.NazivFakulteta
group by f.NazivFakulteta;

select  f.*, count(tf.NazivFakulteta) as UkupnoBrojeva -- ako stavimo count(*) dobicemo 1 za ekonmski jer broji redove u grupi
from fakultet f left outer join telefon_fakulteta tf on tf.NazivFakulteta=f.NazivFakulteta
group by f.NazivFakulteta;

select  f.*, group_concat(Telefon separator '; ') as UkupnoBrojeva -- ako stavimo count(*) dobicemo 1 za ekonmski jer broji redove u grupi
from fakultet f left outer join telefon_fakulteta tf on tf.NazivFakulteta=f.NazivFakulteta
group by f.NazivFakulteta;


select * from Nastavnik n join osoba o on o.JMB=n.JMB
left join osoba osk on osk.JMB=n.JMBSefaKatedre;

select * from nastavnik where Plata>(select avg(Plata)from nastavnik);
select * from nastavnik where Plata=(select max(Plata) from nastavnik);
select * from nastavnik where  Plata>=all (select Plata from nastavnik);

select * from predmet where ECTS in (5,7);
select * from osoba where JMB in (select JMBSefaKatedre from nastavnik);
select * from nastavnik order by Plata desc limit 4,1; -- ofset, broj redova, moze i samo limit broj_redova;

select *, (select count(*) from telefon_fakulteta tf where f.NazivFakulteta=tf.NazivFakulteta) as UkupnoBrojeva,
(select count(*) from telefon_fakulteta) as UkuponoUB
from fakultet f;

select o.* from osoba o inner join (select JMB from upisan_na where IdSP=111 and Semestar=8) as u on u.JMB=o.JMB;
with u as (select JMB from upisan_na where IdSP=111 and Semestar=8) select * from osoba o inner join u on u.JMB=o.JMB;

(select JMB, Prezime, Ime, NastavnoZvanje as Zvanje from nastavnik n join osoba o using (JMB)) 
union -- duplikati se eliminisu iz rezultata
(select JMB, Prezime, Ime, SaradnickoZvanje from asistent a join osoba o using (JMB));

(select JMB, Prezime, Ime, NastavnoZvanje as Zvanje from nastavnik n join osoba o using (JMB)) 
union all -- duplikati se ne eliminisu iz rezultata
(select JMB, Prezime, Ime, SaradnickoZvanje from asistent a join osoba o using (JMB));


/******************************************************************************************************************/
delete from telefon_fakulteta where NazivFakulteta='elektrotehnicki fakultet';
delete from telefon_fakulteta;

insert into predmet(IdPredmeta, NazivPredmeta, NazivFakulteta, ECTS) values (1117, 'EP', 'Elektrotehnicki fakultet', 6);
insert into predmet values (1118, 'M1', 7, 'Ekonomski fakultet');

update asistent
set
	SaradnickoZvanje='va',
    Plata=Plata+100
    where JMB='0702964105027';
    
update nastavnik set Plata=Plata*1.1;

/******************************* Pogledi ********************************/

create view nastavnik_info as 
select JMB, Ime, Prezime, NastavnoZvanje from nastavnik n inner join osoba o using (JMB);

select * from nastavnik_info;
select * from nastavnik_info where JMB in (select JMBSefaKatedre from nastavnik);

alter table osoba add column Pol varchar(6);
update osoba set Pol=if(substr(JMB, 10, 1) <= '4', 'muski', 'zenski');

create trigger postavi_pol before insert on osoba
for each row 
set new.Pol=if(substr(new.JMB, 10, 1) <= '4', 'muski', 'zenski'); -- old za trigger za brisanje

insert into osoba values('0101998110007', 'Petroviv', 'Petar', '1998-01-01', 'n/a', null);
select * from osoba where osoba.JMB='0101998110007';

drop trigger postavi_pol;
delimiter $$
create trigger postavi_pol before insert on osoba
for each row 
begin 
set new.Pol=if(substr(new.JMB, 10, 1) <= '4', 'muski', 'zenski');
end$$
delimiter ;

/************************************************************************************************************/

alter table ispit add column Izaslo int default 0 not null;
alter table ispit add column Polozilo int default 0 not null;

/*korelisani upit*/
update ispit 
set Izaslo=(select count(*) from polaze p 
where p.DatumIspita=ispit.DatumIspita and 
p.IdPredmeta=ispit.IdPredmeta 
and p.IdSP=ispit.IdSP) ,
Polozilo=(select count(*) from polaze p 
where Ocena>5 and
p.DatumIspita=ispit.DatumIspita and 
p.IdPredmeta=ispit.IdPredmeta 
and p.IdSP=ispit.IdSP);

create trigger ispit_novo_polaganje after insert on polaze 
for each row 
update ispit 
set Izaslo=Izaslo+1, 
Polozilo=Polozilo+(new.Ocena>5)
where new.DatumIspita=ispit.DatumIspita and 
new.IdPredmeta=ispit.IdPredmeta 
and new.IdSP=ispit.IdSP;

create trigger ispit_brisanje after delete on polaze 
for each row 
update ispit 
set Izaslo=Izaslo-1, 
Polozilo=Polozilo-(old.Ocena>5)
where old.DatumIspita=ispit.DatumIspita and 
old.IdPredmeta=ispit.IdPredmeta 
and old.IdSP=ispit.IdSP;

delimiter $$
create procedure prosjek_ocjena(in pJMB char(13), in pIdSP int, out pProsjek double)
begin
	select round(avg(Ocena), 2) into pProsjek
	from polaze
	where Ocena>5 and JMB=pJMB and IdSP=pIdSP;
end$$
delimiter ;

set @prosjek=null;
call prosjek_ocjena('1206986101234', 121, @prosjek);
select @prosjek;

drop procedure prosjek_ocjena;

delimiter $$
create function prosjek_ocjena_f(pJMB char(13), pIdSP int)
returns double
reads sql data -- u novim verzijama MySQL-a moramo navesti tip funkcije
begin
	declare vProsjek double;
	select round(avg(Ocena), 2) into vProsjek
	from polaze
	where Ocena>5 and JMB=pJMB and IdSP=pIdSP;
    return vProsjek;
end$$
delimiter ;

select prosjek_ocjena_f('1206986101234', 121);

select JMB, IdSP, prosjek_ocjena_f(JMB, IdSP) as Prosjek 
from upisan_na
order by Prosjek desc;

delimiter $$
create procedure prosjek_ocjena_sp(in pIdSP int)
begin
	select un.JMB, Prezime, Ime, 
    prosjek_ocjena_f(un.JMB, pIdSP) as Prosjek
    from upisan_na un 
    join osoba o on o.JMB=un.JMB
    where IdSP=pIdSP
    order by Prosjek desc;
end$$
delimiter ;

call prosjek_ocjena_sp(111);

drop procedure prosjek_ocjena_sp;

delimiter $$
create procedure evidentiraj_rezultat_ispita(in pJMB char(13), in pDatumispita date, in pIdPredmeta int, in pIdSP int, in pOcena tinyint)
begin
	declare vSlusao, vPolozio bool default false;
    
    select coun(*) into vSlusao
    from upisao
    where JMB=pJMB and IdPredmeta=pIdPredmeta and IdSP=pIdSP;
    
    if not vSlusao then
		signal sqlstate '45000' set message_text='Nije slusao';
	end if;
    
    select count(*) into vPolozio
    from polaze
    where Ocena>5 and JMB=pJMB and IdPredmeta=pIdPredmeta and IdSP=pIdSP;
    
    if vPolozio then
		signal sql state '45000' set message_text='Polozio';
	end if;
    
    insert into polaze values (pJMB, pDatumIspita, pIdPredmeta, pIdSP, pOcena);
end;
delimiter ;

select * from x;

delimiter $$
create procedure t1()
begin
	select * from x;
end$$
delimiter ;

delimiter $$
create procedure t2()
begin
	declare continue handler for 1146 select 1;
    select 2;
	select * from x;
    select 3;
end$$
delimiter ;

call t2();














