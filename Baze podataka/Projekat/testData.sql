USE `smartvillage`;

-- -----------------------------------------------------
-- NIVO 1: Tabele bez stranih ključeva (Nezavisni entiteti)
-- -----------------------------------------------------

-- 1. Tabela: DIREKTOR
INSERT INTO `smartvillage`.`DIREKTOR` (`JMB`, `Ime`, `Prezime`, `Telefon`, `DatumStupanjaNaDuznost`, `TrajanjeMandata`) VALUES
('1508975172345', 'Marko', 'Marković', '065/111-222', '2024-01-15', 4),
('2210982187654', 'Nikola', 'Nikolić', '066/333-444', '2025-03-01', 4);

-- 2. Tabela: ZADRUGAR
INSERT INTO `smartvillage`.`ZADRUGAR` (`JMB`, `Ime`, `Prezime`, `Telefon`, `Status`) VALUES
('0503965171122', 'Jovan', 'Jovanović', '065/555-666', 1),
('1211978183344', 'Petar', 'Petrović', '066/777-888', 1),
('2802991124455', 'Milica', 'Milić', '063/999-000', 1);

-- 3. Tabela: KULTURA
INSERT INTO `smartvillage`.`KULTURA` (`Naziv`, `Vrsta`) VALUES
('Zlatni Delišes', 'Jabuka'),
('Viljamovka', 'Kruška'),
('Grapolo', 'Paradajz');

-- 4. Tabela: KUPAC
INSERT INTO `smartvillage`.`KUPAC` (`Sifra`, `Naziv`, `Telefon`) VALUES
('KUP-001', 'Supermarket Tropic', '051/123-456'),
('KUP-002', 'Domaći Trgovački Lanac Kort', '053/777-888');


-- -----------------------------------------------------
-- NIVO 2: Tabele koje zavise direktno od Nivoa 1
-- -----------------------------------------------------

-- 5. Tabela: ZADRUGA (Zavisi od tabela: DIREKTOR)
INSERT INTO `smartvillage`.`ZADRUGA` (`JIB`, `Naziv`, `Adresa`, `Telefon`, `Direktor_JMB`, `Aktivna`) VALUES
('4401234560007', 'Zadruga KrajinaFrut', 'Krajinskih brigada 12, Banja Luka', '051/222-333', '1508975172345', 1),
('4409876540003', 'Zadruga SemberijaAgro', 'Patkovača bb, Bijeljina', '055/444-555', '2210982187654', 1);

-- 6. Tabela: NARUDZBA (Zavisi od tabela: KUPAC)
INSERT INTO `smartvillage`.`NARUDZBA` (`Sifra`, `Datum`, `UkupanIznos`, `KUPAC_Sifra`, `Status`) VALUES
('ORD-2026-001', '2026-05-10', 472.00, 'KUP-001', 1),
('ORD-2026-002', '2026-06-01', 280.00, 'KUP-002', 0);


-- -----------------------------------------------------
-- NIVO 3: Tabele koje zavise od ZADRUGE i drugih entiteta
-- -----------------------------------------------------

-- 7. Tabela: ZADRUGAR_ZADRUGA (Zavisi od: ZADRUGAR, ZADRUGA)
INSERT INTO `smartvillage`.`ZADRUGAR_ZADRUGA` (`ZADRUGAR_JMB`, `ZADRUGA_JIB`) VALUES
('0503965171122', '4401234560007'),
('1211978183344', '4401234560007'),
('2802991124455', '4409876540003');

-- 8. Tabela: HUB (Zavisi od: ZADRUGA)
INSERT INTO `smartvillage`.`HUB` (`Naziv`, `Opis`, `ZADRUGA_JIB`) VALUES
('Hub Jabuka', 'Pametni hab za praćenje zasada jabuka i krušaka', '4401234560007'),
('Hub Plastenička Proizvodnja', 'Sistem senzora za kontrolu temperature u plastenicima', '4409876540003');

-- 9. Tabela: ZAPOSLENI (Zavisi od: ZADRUGA)
INSERT INTO `smartvillage`.`ZAPOSLENI` (`JMB`, `Ime`, `Prezime`, `Telefon`, `RadnoMjesto`, `ZADRUGA_JIB`) VALUES
('1004990175566', 'Dragan', 'Dragić', '065/444-333', 'Agronom', '4401234560007'),
('1812985189900', 'Stefan', 'Stefanić', '066/555-111', 'Tehničar održavanja', '4401234560007'),
('2505993302211', 'Ana', 'Anić', '063/222-888', 'Upravnik skladišta', '4409876540003');

-- 10. Tabela: PROJEKAT (Zavisi od: ZADRUGA)
INSERT INTO `smartvillage`.`PROJEKAT` (`Sifra`, `Naziv`, `ImeInvestitora`, `Budzet`, `Pocetak`, `Zavrsetak`, `ZADRUGA_JIB`) VALUES
('PRJ-2025-01', 'Digitalizacija Krajine', 'Ministarstvo poljoprivrede', 50000.00, '2025-03-01', '2026-03-01', '4401234560007');

-- 11. Tabela: EDUKACIJA (Zavisi od: ZADRUGA)
INSERT INTO `smartvillage`.`EDUKACIJA` (`Sifra`, `Naziv`, `Predavac`, `Pocetak`, `Zavrsetak`, `MaksBrojUcesnika`, `ZADRUGA_JIB`) VALUES
('EDU-09', 'Upotreba IoT senzora u voćarstvu', 'prof. dr Ivan Ivanović', '2026-04-10', '2026-04-12', 30, '4401234560007');

-- 12. Tabela: DOBAVLJAC (Zavisi od: ZADRUGA)
INSERT INTO `smartvillage`.`DOBAVLJAC` (`Sifra`, `Naziv`, `VrstaProizvoda`, `ZADRUGA_JIB`) VALUES
('DOB-001', 'AgroMihajlović d.o.o.', 'Sistemi za navodnjavanje', '4409876540003'),
('DOB-002', 'Rasadnik Zeleni Raj', 'Sadnice voća', '4401234560007');


-- -----------------------------------------------------
-- NIVO 4: Tabele koje zavise od HUB-a i ZAPOSLENIH
-- -----------------------------------------------------

-- 13. Tabela: HUB_has_ZAPOSLENI (Zavisi od: HUB, ZAPOSLENI)
INSERT INTO `smartvillage`.`HUB_has_ZAPOSLENI` (`HUB_Naziv`, `HUB_ZADRUGA_JIB`, `ZAPOSLENI_JMB`) VALUES
('Hub Jabuka', '4401234560007', '1004990175566'),
('Hub Jabuka', '4401234560007', '1812985189900'),
('Hub Plastenička Proizvodnja', '4409876540003', '2505993302211');

-- 14. Tabela: PLASTENIK (Zavisi od: HUB)
INSERT INTO `smartvillage`.`PLASTENIK` (`Sifra`, `Tip`, `Povrsina`, `VrstaNavodnjavanja`, `HUB_Naziv`, `HUB_ZADRUGA_JIB`) VALUES
('PLAS-01', 'Polikarbonatni visoki', 500.00, 'Kap po kap', 'Hub Plastenička Proizvodnja', '4409876540003'),
('PLAS-02', 'Tunelski folijski', 300.00, 'Raspršivači', 'Hub Plastenička Proizvodnja', '4409876540003');

-- 15. Tabela: PROIZVOD (Zavisi od: HUB)
INSERT INTO `smartvillage`.`PROIZVOD` (`Sifra`, `Naziv`, `Opis`, `Cijena`, `StanjeNaZalihama`, `HUB_Naziv`, `HUB_ZADRUGA_JIB`) VALUES
('PRD-01', 'Gajba Jabuka Klasa I', 'Svježe obrana jabuka Zlatni Delišes', 15.50, 250.00, 'Hub Jabuka', '4401234560007'),
('PRD-02', 'Sok od Kruške 1L', '100% prirodni cijeđeni sok bez šećera', 4.20, 1200.00, 'Hub Jabuka', '4401234560007'),
('PRD-03', 'Domaći Paradajz kg', 'Organski uzgojen paradajz Grapolo', 2.80, 500.00, 'Hub Plastenička Proizvodnja', '4409876540003');

-- 16. Tabela: PARCELA (Zavisi od: ZADRUGAR, HUB)
INSERT INTO `smartvillage`.`PARCELA` (`ID_Parcele`, `Naziv`, `Povrsina`, `KatastarskaOpstina`, `ZADRUGAR_JMB`, `HUB_Naziv`, `HUB_ZADRUGA_JIB`) VALUES
(101, 'Voćnjak Prijakovci', 2.50, 104, '0503965171122', 'Hub Jabuka', '4401234560007'),
(102, 'Plantaža Dragočaj', 4.15, 104, '1211978183344', 'Hub Jabuka', '4401234560007'),
(201, 'Njiva Patkovača 1', 1.80, 302, '2802991124455', 'Hub Plastenička Proizvodnja', '4409876540003');


-- -----------------------------------------------------
-- NIVO 5: Tabele koje direktno zavise od PARCELE i PROIZVODA
-- -----------------------------------------------------

-- 17. Tabela: STAVKA (Zavisi od: NARUDZBA, PROIZVOD)
INSERT INTO `smartvillage`.`STAVKA` (`Kolicina`, `Cijena`, `NARUDZBA_Sifra`, `PROIZVOD_Sifra`) VALUES
(20.00, 15.50, 'ORD-2026-001', 'PRD-01'),
(38.57, 4.20, 'ORD-2026-001', 'PRD-02'),
(100.00, 2.80, 'ORD-2026-002', 'PRD-03');

-- 18. Tabela: ZASAD (Zavisi od: PARCELA, KULTURA)
INSERT INTO `smartvillage`.`ZASAD` (`DatumSadnje`, `BrojSadnica`, `PARCELA_ID_Parcele`, `KULTURA_Naziv`) VALUES
('2022-11-10', 1200, 101, 'Zlatni Delišes'),
('2023-03-15', 800, 102, 'Viljamovka'),
('2026-02-20', 3500, 201, 'Grapolo');

-- 19. Tabela: IoTUredjaj (Zavisi od: HUB, ali i od PARCELE preko složenog ključa)
-- NAPOMENA: Prvi uređaj ima PARCELA_ID_Parcele = NULL, što je dozvoljeno jer je kolona NULLABLE
INSERT INTO `smartvillage`.`IoTUredjaj` (`SerijskiBroj`, `Status`, `Opis`, `HUB_Naziv`, `HUB_ZADRUGA_JIB`, `PARCELA_ID_Parcele`) VALUES
('SN-JAB-001', 1, 'Senzor vlage i temperature zemljišta - Skladište', 'Hub Jabuka', '4401234560007', NULL),
('SN-JAB-002', 1, 'Meteo stanica sa senzorom za mraz', 'Hub Jabuka', '4401234560007', 101),
('SN-PLAS-099', 1, 'Senzor vlažnosti vazduha i CO2 u plasteniku', 'Hub Plastenička Proizvodnja', '4409876540003', 201);


-- -----------------------------------------------------
-- NIVO 6: Tabele koje zavise od ZASADA i PLASTENIKA
-- -----------------------------------------------------

-- 20. Tabela: ZASAD_has_PLASTENIK (Zavisi od: ZASAD, PLASTENIK)
INSERT INTO `smartvillage`.`ZASAD_has_PLASTENIK` (`ZASAD_KULTURA_Naziv`, `ZASAD_PARCELA_ID_Parcele`, `PLASTENIK_Sifra`) VALUES
('Grapolo', 201, 'PLAS-01');

-- 21. Tabela: PRINOS (Zavisi od: ZASAD)
INSERT INTO `smartvillage`.`PRINOS` (`DatumBerbe`, `Kolicina`, `KvalitetOcjena`, `ZASAD_KULTURA_Naziv`, `ZASAD_PARCELA_ID_Parcele`) VALUES
('2025-09-15', 4500.50, 9, 'Zlatni Delišes', 101),
('2025-10-02', 2100.00, 8, 'Viljamovka', 102);