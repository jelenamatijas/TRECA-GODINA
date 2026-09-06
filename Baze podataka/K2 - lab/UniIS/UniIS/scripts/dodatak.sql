-- korisnicki nalog
create user 'referent'@'localhost' identified by 'referent';
grant select, insert, update, delete on uniis_prosireno.* to 'referent'@'localhost';
grant execute on procedure uniis_prosireno.dodaj_p_na_sp to 'referent'@'localhost';
grant execute on procedure uniis_prosireno.azuriraj_p_na_sp to 'referent'@'localhost';
flush privileges;