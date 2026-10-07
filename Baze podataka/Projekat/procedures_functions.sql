-- provjera narudzbe pri zakljucavanju (da se ne zakljuca prazna narudzba)
delimiter $$
create procedure pZavrsiNarudzbu(in pSifraNarudzbe varchar(20))
begin
	declare vBrojStavki int;
    
    select count(*) into vBrojStavki
    from stavka
    where NARUDZBA_Sifra = pSifraNarudzbe;
    
    if vBrojStavki = 0 then
		signal sqlstate '45000'
        set message_text = 'GRESKA: Nije moguce zavrsiti narudzbu koja u sebi nema jos ni jednu stavku.';
	else
		update narudzba
        set Status = 2
        where Sifra = pSifraNarudzbe;
	end if;
end $$
delimiter ;

-- provjera da li postoji direktor koji se postavlja kao direktor zadruge
delimiter $$
create procedure pPromijeniDirektoraZadruge(in pJIB varchar(30), in pJMB varchar(13))
begin
	declare vPostoji int;
    
    select count(*) into vPostoji
    from direktor
    where JMB = pJMB;
	
    if vPostoji = 0 then
		signal sqlstate '45000'
        set message_text = 'GRESKA: Direktor sa unesenim JMB ne postoji.';
	else
		update zadruga
        set Direktor_JMB = pJMB
        where JIB = pJIB;
	end if;
end $$
delimiter ;

-- pregled ukunog broja kilograma koji je obran za neku kulturu na svim parcelama
delimiter $$
create function fUkupanPrinosKultureUGodini(pKultura varchar(30), pGodina int) returns decimal(10,2)
deterministic reads sql data
begin
	declare vUkupno decimal(10,2);
    select sum(Kolicina) into vUkupno
    from prinos
    where ZASAD_KULTURA_Naziv = pKultura and year(DatumBerbe) = pGodina;
    
    return ifnull(vUkupno, 0.00);
end $$
delimiter ;

-- racuna ukupnu zaradu za nekog kupca
delimiter $$ 
create function fUkupnaZaradaOdKupca(pKupac varchar(20)) returns decimal(10,2)
deterministic reads sql data
begin
	declare vUkupnaZarada decimal(10,2);
    select sum(UkupanIznos) into vUkupnaZarada
    from narudzba 
    where KUPAC_Sifra = pKupac and 'Status' = 2;
    
    return ifnull(vUkupnaZarada, 0.00);
end $$
delimiter ;

-- pregled svih zadruga koje pripadaju odabranom zadrugaru
delimiter $$
create procedure pZadrugeDatogZadrugara(in pJMB varchar(13))
begin
    select z.Naziv, z.JIB 
    from zadruga z 
    inner join zadrugar_zadruga zz on z.JIB = zz.ZADRUGA_JIB 
    where zz.ZADRUGAR_JMB = pJMB;
end $$
delimiter ;

-- pregled svih parcela koje pripadaju odabranom zadrugaru
delimiter $$
create procedure pParceleDatogZadrugara(in pJMB varchar(13))
begin
    select ID_Parcele, Povrsina, KatastarskaOpstina 
    from parcela 
    where ZADRUGAR_JMB = pJMB;
end $$
delimiter ;

-- pregled prinosa zadruge
delimiter $$
create procedure pPregledPrinosaZadruge(
    in pJIB varchar(30)
)
begin
	select
        pr.ID_Prinosa,
        pr.DatumBerbe,
        pr.Kolicina,
        pr.KvalitetOcjena,
        pr.ZASAD_KULTURA_Naziv as Kultura,
        p.Naziv as ParcelaNaziv
    from prinos pr
    join zasad z on pr.ZASAD_KULTURA_Naziv = z.KULTURA_Naziv 
                and pr.ZASAD_PARCELA_ID_Parcele = z.PARCELA_ID_Parcele
    join parcela p on z.PARCELA_ID_Parcele = p.ID_Parcele
    where p.HUB_ZADRUGA_JIB = pJIB; 
end $$
delimiter ;

