select A.JMB, Ime, Prezime
from asistent A 
inner join osoba O on A.JMB=O.JMB
where SaradnickoZvanje='asistent';

select o.JMB, Ime, Prezime from osoba o
inner join asistent a on a.JMB=o.JMB
where SaradnickoZvanje='asistent';

select pnp.IdPredmeta, Semestar, TipPredmeta, JMB
from p_na_sp pnp
left outer join asistira a on a.IdPredmeta=pnp.IdPredmeta
where pnp.IdSP=211;

select pnp.IdPredmeta, Semestar, TipPredmeta, JMB
from asistira a
right outer join p_na_sp pnp on pnp.IdPredmeta=a.IdPredmeta
where pnp.IdSP=211;

select * from telefon_fakulteta;

select f.NazivFakulteta, Adresa, Telefon
from fakultet f
cross join telefon_fakulteta tf where tf.NazivFakulteta=f.NazivFakulteta;

select o.JMB, Ime, Prezime, NazivSP, Ciklus, round(avg(Ocena), 2) as Prosjek
from osoba o
inner join polaze p on p.JMB=o.JMB
inner join studijski_program sp on sp.IdSP=p.IdSP
where Ocena>5
group by o.JMB, SP.IdSP
order by Prosjek desc;


select o.Ime, o.Prezime, n.Plata, osk.Ime as ImeSK, osk.Prezime as PrezimeSK, nsk.Plata as PlataSK
from nastavnik n
inner join osoba o on n.JMB=o.JMB
inner join nastavnik nsk on n.JMBSefaKatedre=nsk.JMB
inner join osoba osk on nsk.JMB=osk.JMB;

select o.Ime, o.Prezime, n.plata, osk.Ime as ImeSK, osk.Prezime as PrezimeSK, nsk.Plata as PlataSK
from nastavnik n
inner join osoba o on n.JMB=o.JMB
left outer join nastavnik nsk on n.JMBSefaKatedre=nsk.JMB
left outer join osoba osk on nsk.JMB=osk.JMB;

select o.Ime, o.Prezime, o.JMB
from osoba o
where o.JMB in (select JMBSefaKatedre from nastavnik);

select distinct o.JMB, o.Ime, o.Prezime
from osoba o 
inner join nastavnik n on n.JMBSefaKatedre=o.JMB;

select Ime, Prezime, o.JMB
from osoba o
inner join nastavnik n on n.JMB=o.JMB
where Plata=(select max(Plata) from nastavnik);

select o.JMB, Ime, Prezime, Plata
from osoba o 
inner join nastavnik n on n.JMB=o.JMB
where Plata>(select avg(Plata) from nastavnik);

select o.JMB, Ime, Prezime
from osoba o
inner join nastavnik n on n.JMB=o.JMB
where NastavnoZvanje=(select NastavnoZvanje from nastavnik where JMB='2804950103891') and Plata=(select Plata from nastavnik where JMB='2804950103891')
and o.JMB not like '2804950103891';

select o.JMB, Ime, Prezime, NazivSP, Ciklus, round(avg(Ocena),2) as Prosjek
from osoba  o
inner join polaze p on p.JMB=o.JMB
inner join studijski_program sp on p.IdSP=sp.IdSP
where ocena>5
group by o.JMB, Ime, Prezime, NazivSP, Ciklus
having Prosjek>=all (select round(avg(Ocena),2) as po from polaze where Ocena>5 group by JMB, IdSP);