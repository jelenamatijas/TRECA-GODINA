-- zabrana kreiranja projekta za neaktivnu zadrugu
delimiter $$
create trigger TzabranaProjektaNeaktivnojZadruzi before insert on projekat
for each row
begin
	declare vAktivna tinyint;
    select Aktivna into vAktivna
    from zadruga
    where JIB=new.ZADRUGA_JIB;
    
    if vAktivna=0 then
		signal sqlstate '45000'
        set message_text='GRESKA: Nije moguce kreirati projekat za neaktivnu zadrugu.';
	end if;
end $$
delimiter ;

-- zabrana kreiranja haba za neaktivnu zadrugu
delimiter $$
create trigger TzabranaHabaNeaktivnojZadruzi before insert on hub
for each row
begin
	declare vAktivna tinyint;
    select Aktivna into vAktivna
    from zadruga
    where JIB=new.ZADRUGA_JIB;
    
    if vAktivna=0 then
		signal sqlstate '45000'
        set message_text='GRESKA: Nije moguce kreirati hub za neaktivnu zadrugu.';
	end if;
end $$
delimiter ;

-- provjera zaliha i automatsko smanjivanje stanja na zalihama nakon prodaje
delimiter $$
create trigger TprovjeraZalihaiAzuriranje before insert on stavka
for each row
begin
	declare vZalihe decimal(10,2);
    
    select StanjeNaZalihama into vZalihe
    from proizvod
    where sifra=new.PROIZVOD_Sifra;
    
    if vZalihe < new.Kolicina then
		signal sqlstate '45000'
        set message_text = 'GRESKA: Nema dovoljno proizvoda na zalihama.';
	else
		update proizvod
        set StanjeNaZalihama = StanjeNaZalihama - new.Kolicina
        where Sifra = new.PROIZVOD_Sifra;
	end if;
end $$
delimiter ;

-- validacija datuma projekta
delimiter $$
create trigger tValidacijaDatumaProjekta before insert on projekat
for each row
begin
	if new.Zavrsetak < new.Pocetak then
		signal sqlstate '45000'
        set message_text = 'GRESKA: Datum zavrsetka projekta ne moze biti prije datuma pocetka projekta.';
	end if;
end $$
delimiter ;

-- Automatsko azuriranje ukupnog iznosa narudzbe nakon dodavanja nove stavke
delimiter $$
create trigger tAzurirajIznosNarudzbe after insert on stavka
for each row
begin
	update narudzba
    set UkupanIznos = UkupanIznos + (new.Kolicina*new.Cijena)
    where Sifra = new.NARUDZBA_Sifra;
end $$
delimiter ;

-- validacija datuma berbe
delimiter $$
create trigger TValidacijaDatumaBerbe before insert on prinos
for each row
begin
	declare vDatumSadnje date;
    select DatumSadnje into vDatumSadnje
    from zasad
    where KULTURA_Naziv = new.ZASAD_KULTURA_Naziv and
		PARCELA_ID_Parcele = new.ZASAD_PARCELA_ID_Parcele;
	
    if new.DatumBerbe < vDatumSadnje then
		signal sqlstate '45000'
        set message_text = 'GRESKA: Datum berbe ne moze biti prije datuma sadnje.';
    end if;
end $$
delimiter ;

-- Provjera da li parcela za sadnju pripada aktivnom zadrugaru
delimiter $$
create trigger tZabranaZasadaNeaktivnomZadrugaru before insert on zasad
for each row
begin
	declare vStatus tinyint;
    declare vJMB varchar(13);
	
    select ZADRUGAR_JMB into vJMB
    from parcela 
    where ID_Parcele = new.PARCELA_ID_Parcele;
    
    select Status into vStatus
    from zadrugar
    where JMB = vJMB;
    
    if vStatus = 0 then
		signal sqlstate '45000'
        set message_text = 'GRESKA: Neaktivan zadrugar ne moze registrovati movi zasad.';
	end if;
end $$
delimiter ;

-- zabrana dodavanja nove stavke u zavrsenu narudzbu
delimiter $$
create trigger tZabranaDodavanjaStavkeZavrseneNarudzbe before insert on stavka
for each row
begin
	declare vStatus tinyint;
    select Status into vStatus
    from narudzba
    where Sifra = new.NARUDZBA_Sifra;
	
    if vStatus = 2 or vStatus = 4 then -- isporuceno ili otkazano
		signal sqlstate '45000'
        set message_text = 'GRESKA: Nije moguce dodati stavku u narudzbu koja je zavrsena ili otkazana.';
	end if;
end $$
delimiter ;

-- zabrana brisanja stavki iz narudzbe koja je zavrsena
delimiter $$
create trigger tZabranaBrisanjaStavkeZavrseneNarudzbe before delete on stavka
for each row
begin
	declare vStatus tinyint;
    select Status into vStatus
    from narudzba
    where Sifra = old.NARUDZBA_Sifra;
    
    if vStatus = 2 then
		signal sqlstate '45000'
        set message_text = 'GRESJA: Nije moguce obrisati stavku iz zavrsene narudzbe.';
	end if;
end $$
delimiter ;

-- povrat proizvoda i novca pri otkazivanju neke stavke iz narudzbe
delimiter $$
create trigger tObrisiStavkuAzurirajZaliheiIznos after delete on stavka
for each row
begin
	update proizvod
    set StanjeNaZalihama = StanjeNaZalihama + old.Kolicina
    where Sifra = old.PROIZVOD_Sifra;
    
    update narudzba
    set UkupanIznos = UkupanIznos - (old.Kolicina * old.Cijena)
    where Sifra = old.NARUDZBA_Sifra;
end $$
delimiter ;

-- povrat zaliha na stanje ukoliko je narudzba otkazana
delimiter $$
create trigger tPovratZalihaPriOtkazivanjuNarudzbe after update on narudzba
for each row
begin
	if old.Status <> 4 and new.Status = 4 then
		update proizvod p
        join stavka s on p.Sifra = s.PROIZVOD_Sifra
        set p.StanjeNaZalihama = p.StanjeNaZalihama + s.Kolicina
        where s.NARUDZBA_Sifra = new.Sifra;
	end if;
end $$
delimiter ;

-- deaktivacija zadruge nakon sto je neki od njenih zadrugara postao neaktivan
delimiter $$
create trigger tNakonDeaktivacijeZadrugara after update on zadrugar
for each row
begin
    if old.Status = 1 and new.Status = 0 then
        update zadruga z
        inner join zadrugar_zadruga zz on z.JIB = zz.ZADRUGA_JIB
        set z.Aktivna = 0
        where zz.ZADRUGAR_JMB = new.JMB;
    end if;
end $$
delimiter ;

-- sprjecavanje brisanja parcele koja ima zasade i IoT uredjaje
delimiter $$
create trigger tSprijeciBrisanjeParcele before delete on parcela
for each row
begin
    declare vBrojZasada int;
    declare vBrojUredjaja int;

    select count(*) into vBrojZasada
    from zasad 
    where PARCELA_ID_Parcele = old.ID_Parcele;

    select count(*) into vBrojUredjaja 
    from IoTUredjaj
    where PARCELA_ID_Parcele = old.ID_Parcele;

    if vBrojZasada > 0 or vBrojUredjaja > 0 then
        signal sqlstate '45000'
        set message_text = 'Brisanje otkazano! Na parceli postoje aktivni zasadi ili IoT uređaji.';
    end if;
end $$
delimiter ;
