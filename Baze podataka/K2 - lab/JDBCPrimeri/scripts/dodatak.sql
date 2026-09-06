use uniis;

-- uskladistene procedure
-- prosek_ocena
delimiter $$
create procedure prosek_ocena(in pJMB char(13), in pIdSP int, out pProsekOcena double)
begin
  select round(avg(Ocena), 2) into pProsekOcena
  from polaze
  where ocena>5 and JMB=pJMB and IdSP=pIdSP;
end$$
delimiter ;

-- prosek_ocena_sp
delimiter $$
create procedure prosek_ocena_sp(in pIdSP int)
begin
  select o.JMB, Prezime, Ime, round(avg(Ocena), 2) as ProsekOcena
  from polaze p
  inner join osoba o on o.JMB=p.JMB
  where Ocena>5 and IdSP=pIdSP
  group by o.JMB
  order by ProsekOcena desc;
end$$
delimiter ;

-- korisnicki nalog
create user 'student'@'localhost' identified by 'student';
grant select, insert, update, delete on uniis.* to 'student'@'localhost';
grant execute on procedure uniis.prosek_ocena to 'student'@'localhost';
grant execute on procedure uniis.prosek_ocena_sp to 'student'@'localhost';
flush privileges;