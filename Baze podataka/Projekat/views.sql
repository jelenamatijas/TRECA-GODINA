-- ispis proizvoda, koji su na stanju, svih zadruga koje su aktivne
create view vKatalogProizvoda as select 
	p.Sifra as Proizvod_Sifra,
	p.Naziv as Proizvod_Naziv,
    p.Opis as Proizvod_Opis,
    p.Cijena as Cijena_KM,
    p.StanjeNaZalihama,
    h.Naziv as Hub_Naziv,
    z.Naziv as Zadruga_Naziv
from proizvod p 
join hub h on p.HUB_Naziv = h.Naziv and p.HUB_ZADRUGA_JIB = h.ZADRUGA_JIB
join zadruga z on h.ZADRUGA_JIB = z.JIB
where p.StanjeNaZalihama > 0 and z.Aktivna = 1;

-- pregled aktivnih projekata
create view vPregledAktivnihProjekata as select
	pr.Sifra as Projekat_Sifra,
    pr.Naziv as Naziv_Projekta,
    pr.ImeInvestitora,
    pr.Budzet,
    pr.Pocetak,
    pr.Zavrsetak,
    datediff(pr.Zavrsetak, curdate()) as Preostalo_Dana,
    z.Naziv as Zadruga_Naziv
from projekat pr 
join zadruga z on pr.ZADRUGA_JIB = z.JIB
where curdate() between pr.Pocetak and Pr.Zavrsetak;

-- pregled uredjaja na parcelama koji nisu aktivni (pokvaren)
create view vKriticneParceleIoT as select
	iot.SerijskiBroj as Uredjaj_Serijski_Broj,
    iot.Opis as Opis_Uredjaja,
    p.Naziv as Naziv_Parcele,
    p.Povrsina,
    concat(z.Ime, ' ' , z.Prezime) as Vlasnik_Parcele,
    z.Telefon as Telefon_Zadrugara
from IoTUredjaj iot
join parcela p on iot.PARCELA_ID_Parcele = p.ID_Parcele
join zadrugar z on p.ZADRUGAR_JMB = z.JMB
where iot.Status = 0;

-- pregled uredjaja na parcelama koji su aktivni 
create view vAktivniUredjaji as select
	iot.SerijskiBroj as Uredjaj_Serijski_Broj,
    iot.Opis as Opis_Uredjaja,
    p.Naziv as Naziv_Parcele,
    p.Povrsina,
    concat(z.Ime, ' ' , z.Prezime) as Vlasnik_Parcele,
    z.Telefon as Telefon_Zadrugara
from IoTUredjaj iot
join parcela p on iot.PARCELA_ID_Parcele = p.ID_Parcele
join zadrugar z on p.ZADRUGAR_JMB = z.JMB
where iot.Status = 1;

-- pregled prinosa za svaku kulturu
create view vPrinosKultura as select
	k.Naziv as Kultura,
    k.Vrsta as Vrsta_Kulture,
    p.Naziv as Parcela,
    sum(pr.Kolicina) as Ukupno_Obrano,
    round(avg(pr.KvalitetOcjena), 1) as Prosjecna_Ocjena_Kvaliteta,
    count(pr.DatumBerbe) as Broj_Berbi
from prinos pr 
join zasad z on pr.ZASAD_KULTURA_Naziv = z.KULTURA_Naziv and pr.ZASAD_PARCELA_ID_Parcele = z.PARCELA_ID_Parcele
join kultura k on z.KULTURA_Naziv = k.Naziv
join parcela p on z.PARCELA_ID_Parcele = p.ID_Parcele
group by k.Naziv, k.Vrsta, p.Naziv;

-- pregled svih direktora kojima je istekao mandat
create view vIstekliMandati as select 
    JMB, 
    Ime, 
    Prezime, 
    Telefon,
    DatumStupanjaNaDuznost, 
    TrajanjeMandata,
    date_add(DatumStupanjaNaDuznost, interval TrajanjeMandata year) as Datum_Isteka
from direktor 
where date_add(DatumStupanjaNaDuznost, interval TrajanjeMandata year) < curdate();

-- pregled date zadruge i njenog direktora
create view vPregledZadruge as select 
    z.JIB,
    z.Naziv,
    z.Adresa,
    z.Telefon,
    z.Aktivna,
    z.Direktor_JMB,
    d.Ime,
    d.Prezime,
    d.Telefon as DirTel,
    d.DatumStupanjaNaDuznost,
    d.TrajanjeMandata
from zadruga z
join direktor d on z.Direktor_JMB = d.JMB;

-- pregled zaposlenih za dati hub
create view vPregledRadnikaUHubu as select 
    hz.HUB_Naziv,
    hz.HUB_ZADRUGA_JIB,
    z.JMB,
    z.Ime,
    z.Prezime
from HUB_has_ZAPOSLENI hz
join zaposleni z on hz.ZAPOSLENI_JMB = z.JMB;

-- pregled parcela i zadrugara koji su vlasnici
create view vPregledParcelaZadruge as select
p.ID_Parcele,
    p.Naziv as ParcelaNaziv,
    p.Povrsina,
    p.KatastarskaOpstina,
    zz.ZADRUGA_JIB,
    z.JMB as ZadrugarJMB,
    z.Ime as ZadrugarIme,
    z.Prezime as ZadrugarPrezime
from parcela p
join zadrugar z on p.ZADRUGAR_JMB = z.JMB
join zadrugar_zadruga zz on z.JMB = zz.ZADRUGAR_JMB;

-- pregled kultura i zasada na parceli
create view vPregledZasadaNaParceli as select 
    zs.PARCELA_ID_Parcele,
    zs.KULTURA_Naziv,
    k.Vrsta as KulturaVrsta,
    zs.DatumSadnje,
    zs.BrojSadnica
from zasad zs
join kultura k on zs.KULTURA_Naziv = k.Naziv;

-- pregled uredjaja na parceli
create view vPregledIotUredjajaNaParceli as select
    SerijskiBroj,
    Opis,
    'Status',
    PARCELA_ID_Parcele
from IoTUredjaj;

-- pregled svih zasada zadruge
create view vPregledSvihZasadaZadruge as select
    zs.KULTURA_Naziv as Kultura,
    k.Vrsta as VrstaKulture,
    zs.DatumSadnje,
    zs.BrojSadnica,
    p.ID_Parcele,
    p.Naziv as ParcelaNaziv,
    pl.Sifra as PlastenikSifra,
    pl.Tip as PlastenikTip,
    zz.ZADRUGA_JIB
from zasad zs
join kultura k on zs.KULTURA_Naziv = k.Naziv
join parcela p on zs.PARCELA_ID_Parcele = p.ID_Parcele
join zadrugar z on p.ZADRUGAR_JMB = z.JMB
join zadrugar_zadruga zz on z.JMB = zz.ZADRUGAR_JMB
left join zasad_has_plastenik zhp on zs.KULTURA_Naziv = zhp.ZASAD_KULTURA_Naziv 
                                and zs.PARCELA_ID_Parcele = zhp.ZASAD_PARCELA_ID_Parcele
left join plastenik pl on zhp.PLASTENIK_Sifra = pl.Sifra;

-- pregled plastenika zadruge
create view vPregledPlastenikaZadruge as select 
    p.Sifra,
    p.Tip,
    p.Povrsina,
    p.VrstaNavodnjavanja,
    p.HUB_Naziv,
    p.HUB_ZADRUGA_JIB as ZADRUGA_JIB
from plastenik p;

-- pregled prinosa zadruge
create view vPregledPrinosaZadruge as select 
    pr.ID_Prinosa,
    pr.DatumBerbe,
    pr.Kolicina,
    pr.KvalitetOcjena,
    pr.ZASAD_KULTURA_Naziv as Kultura,
    p.Naziv as ParcelaNaziv,
    p.HUB_ZADRUGA_JIB as ZADRUGA_JIB
from prinos pr
join zasad z on pr.ZASAD_KULTURA_Naziv = z.KULTURA_Naziv 
            and pr.ZASAD_PARCELA_ID_Parcele = z.PARCELA_ID_Parcele
join parcela p on z.PARCELA_ID_Parcele = p.ID_Parcele;

-- pregled svih uredjaja zadruge
create view vPregledUredjajaZadruge as select 
    i.SerijskiBroj,
    i.Status,
    i.Opis,
    i.HUB_Naziv,
    i.HUB_ZADRUGA_JIB as ZADRUGA_JIB,
    i.PARCELA_ID_Parcele,
    p.Naziv as ParcelaNaziv
from IoTUredjaj i
join parcela p on i.PARCELA_ID_Parcele = p.ID_Parcele;
