-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema smartvillage
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema smartvillage
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `smartvillage` DEFAULT CHARACTER SET utf8 ;
USE `smartvillage` ;

-- -----------------------------------------------------
-- Table `smartvillage`.`DIREKTOR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`DIREKTOR` (
  `JMB` VARCHAR(13) NOT NULL,
  `Ime` VARCHAR(20) NOT NULL,
  `Prezime` VARCHAR(20) NOT NULL,
  `Telefon` VARCHAR(15) NOT NULL,
  `DatumStupanjaNaDuznost` DATE NOT NULL,
  `TrajanjeMandata` INT NOT NULL,
  PRIMARY KEY (`JMB`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`ZADRUGA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`ZADRUGA` (
  `JIB` VARCHAR(30) NOT NULL,
  `Naziv` VARCHAR(60) NOT NULL,
  `Adresa` VARCHAR(100) NOT NULL,
  `Telefon` VARCHAR(15) NOT NULL,
  `Direktor_JMB` VARCHAR(13) NOT NULL,
  `Aktivna` TINYINT NOT NULL,
  PRIMARY KEY (`JIB`),
  INDEX `fk_ZADRUGA_Direktor1_idx` (`Direktor_JMB` ASC) VISIBLE,
  CONSTRAINT `fk_ZADRUGA_Direktor1`
    FOREIGN KEY (`Direktor_JMB`)
    REFERENCES `smartvillage`.`DIREKTOR` (`JMB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`ZADRUGAR`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`ZADRUGAR` (
  `JMB` VARCHAR(13) NOT NULL,
  `Ime` VARCHAR(20) NOT NULL,
  `Prezime` VARCHAR(20) NOT NULL,
  `Telefon` VARCHAR(15) NOT NULL,
  `Status` TINYINT NOT NULL,
  PRIMARY KEY (`JMB`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`ZADRUGAR_ZADRUGA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`ZADRUGAR_ZADRUGA` (
  `ZADRUGAR_JMB` VARCHAR(13) NOT NULL,
  `ZADRUGA_JIB` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`ZADRUGAR_JMB`, `ZADRUGA_JIB`),
  INDEX `fk_ZADRUGAR_has_ZADRUGA_ZADRUGA1_idx` (`ZADRUGA_JIB` ASC) VISIBLE,
  INDEX `fk_ZADRUGAR_has_ZADRUGA_ZADRUGAR1_idx` (`ZADRUGAR_JMB` ASC) VISIBLE,
  CONSTRAINT `fk_ZADRUGAR_has_ZADRUGA_ZADRUGAR1`
    FOREIGN KEY (`ZADRUGAR_JMB`)
    REFERENCES `smartvillage`.`ZADRUGAR` (`JMB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ZADRUGAR_has_ZADRUGA_ZADRUGA1`
    FOREIGN KEY (`ZADRUGA_JIB`)
    REFERENCES `smartvillage`.`ZADRUGA` (`JIB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`HUB`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`HUB` (
  `Naziv` VARCHAR(45) NOT NULL,
  `Opis` VARCHAR(450) NOT NULL,
  `ZADRUGA_JIB` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`Naziv`, `ZADRUGA_JIB`),
  INDEX `fk_HUB_ZADRUGA1_idx` (`ZADRUGA_JIB` ASC) VISIBLE,
  CONSTRAINT `fk_HUB_ZADRUGA1`
    FOREIGN KEY (`ZADRUGA_JIB`)
    REFERENCES `smartvillage`.`ZADRUGA` (`JIB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`PARCELA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`PARCELA` (
  `ID_Parcele` INT NOT NULL,
  `Naziv` VARCHAR(45) NOT NULL,
  `Povrsina` DECIMAL(10,2) NOT NULL,
  `KatastarskaOpstina` INT NOT NULL,
  `ZADRUGAR_JMB` VARCHAR(13) NOT NULL,
  `HUB_Naziv` VARCHAR(45) NOT NULL,
  `HUB_ZADRUGA_JIB` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`ID_Parcele`),
  INDEX `fk_PARCELA_ZADRUGAR1_idx` (`ZADRUGAR_JMB` ASC) VISIBLE,
  INDEX `fk_PARCELA_HUB1_idx` (`HUB_Naziv` ASC, `HUB_ZADRUGA_JIB` ASC) VISIBLE,
  UNIQUE INDEX `index_unique_parcela_hub` (`ID_Parcele` ASC, `HUB_Naziv` ASC, `HUB_ZADRUGA_JIB` ASC) VISIBLE,
  CONSTRAINT `fk_PARCELA_ZADRUGAR1`
    FOREIGN KEY (`ZADRUGAR_JMB`)
    REFERENCES `smartvillage`.`ZADRUGAR` (`JMB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PARCELA_HUB1`
    FOREIGN KEY (`HUB_Naziv` , `HUB_ZADRUGA_JIB`)
    REFERENCES `smartvillage`.`HUB` (`Naziv` , `ZADRUGA_JIB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`KULTURA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`KULTURA` (
  `Naziv` VARCHAR(30) NOT NULL,
  `Vrsta` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`Naziv`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`ZASAD`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`ZASAD` (
  `DatumSadnje` DATE NOT NULL,
  `BrojSadnica` INT NOT NULL,
  `PARCELA_ID_Parcele` INT NOT NULL,
  `KULTURA_Naziv` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`KULTURA_Naziv`, `PARCELA_ID_Parcele`),
  INDEX `fk_ZASAD_KULTURA1_idx` (`KULTURA_Naziv` ASC) VISIBLE,
  CONSTRAINT `fk_ZASAD_PARCELA1`
    FOREIGN KEY (`PARCELA_ID_Parcele`)
    REFERENCES `smartvillage`.`PARCELA` (`ID_Parcele`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ZASAD_KULTURA1`
    FOREIGN KEY (`KULTURA_Naziv`)
    REFERENCES `smartvillage`.`KULTURA` (`Naziv`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`ZAPOSLENI`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`ZAPOSLENI` (
  `JMB` VARCHAR(13) NOT NULL,
  `Ime` VARCHAR(20) NOT NULL,
  `Prezime` VARCHAR(20) NOT NULL,
  `Telefon` VARCHAR(15) NOT NULL,
  `RadnoMjesto` VARCHAR(35) NULL,
  `ZADRUGA_JIB` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`JMB`),
  INDEX `fk_ZAPOSLENI_ZADRUGA1_idx` (`ZADRUGA_JIB` ASC) VISIBLE,
  CONSTRAINT `fk_ZAPOSLENI_ZADRUGA1`
    FOREIGN KEY (`ZADRUGA_JIB`)
    REFERENCES `smartvillage`.`ZADRUGA` (`JIB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`PROJEKAT`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`PROJEKAT` (
  `Sifra` VARCHAR(20) NOT NULL,
  `Naziv` VARCHAR(45) NOT NULL,
  `ImeInvestitora` VARCHAR(45) NOT NULL,
  `Budzet` DECIMAL(14,2) NOT NULL,
  `Pocetak` DATE NOT NULL,
  `Zavrsetak` DATE NOT NULL,
  `ZADRUGA_JIB` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`Sifra`, `ZADRUGA_JIB`),
  INDEX `fk_PROJEKAT_ZADRUGA1_idx` (`ZADRUGA_JIB` ASC) VISIBLE,
  CONSTRAINT `fk_PROJEKAT_ZADRUGA1`
    FOREIGN KEY (`ZADRUGA_JIB`)
    REFERENCES `smartvillage`.`ZADRUGA` (`JIB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`EDUKACIJA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`EDUKACIJA` (
  `Sifra` VARCHAR(20) NOT NULL,
  `Naziv` VARCHAR(45) NOT NULL,
  `Predavac` VARCHAR(45) NOT NULL,
  `Pocetak` DATE NOT NULL,
  `Zavrsetak` DATE NOT NULL,
  `MaksBrojUcesnika` INT NOT NULL,
  `ZADRUGA_JIB` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`Sifra`, `ZADRUGA_JIB`),
  INDEX `fk_EDUKACIJA_ZADRUGA1_idx` (`ZADRUGA_JIB` ASC) VISIBLE,
  CONSTRAINT `fk_EDUKACIJA_ZADRUGA1`
    FOREIGN KEY (`ZADRUGA_JIB`)
    REFERENCES `smartvillage`.`ZADRUGA` (`JIB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`PRINOS`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`PRINOS` (
  `ID_Prinosa` INT NOT NULL AUTO_INCREMENT,
  `DatumBerbe` DATE NOT NULL,
  `Kolicina` DECIMAL(8,2) NOT NULL,
  `KvalitetOcjena` INT NOT NULL,
  `ZASAD_KULTURA_Naziv` VARCHAR(30) NOT NULL,
  `ZASAD_PARCELA_ID_Parcele` INT NOT NULL,
  INDEX `fk_PRINOS_ZASAD1_idx` (`ZASAD_KULTURA_Naziv` ASC, `ZASAD_PARCELA_ID_Parcele` ASC) VISIBLE,
  UNIQUE INDEX `index_unique_prinos_zasad` (`DatumBerbe` ASC, `ZASAD_KULTURA_Naziv` ASC, `ZASAD_PARCELA_ID_Parcele` ASC) VISIBLE,
  PRIMARY KEY (`ID_Prinosa`),
  CONSTRAINT `fk_PRINOS_ZASAD1`
    FOREIGN KEY (`ZASAD_KULTURA_Naziv` , `ZASAD_PARCELA_ID_Parcele`)
    REFERENCES `smartvillage`.`ZASAD` (`KULTURA_Naziv` , `PARCELA_ID_Parcele`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`IoTUredjaj`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`IoTUredjaj` (
  `SerijskiBroj` VARCHAR(20) NOT NULL,
  `Status` TINYINT NOT NULL,
  `Opis` VARCHAR(450) NOT NULL,
  `HUB_Naziv` VARCHAR(45) NOT NULL,
  `HUB_ZADRUGA_JIB` VARCHAR(30) NOT NULL,
  `PARCELA_ID_Parcele` INT NULL,             
  PRIMARY KEY (`SerijskiBroj`),
  INDEX `fk_IoTUredjaj_HUB1_idx` (`HUB_Naziv` ASC, `HUB_ZADRUGA_JIB` ASC) VISIBLE,
  INDEX `fk_IoTUredjaj_PARCELA1_idx` (`PARCELA_ID_Parcele` ASC) VISIBLE,
  UNIQUE INDEX `index_unique_IoT_hub` (`SerijskiBroj` ASC, `HUB_Naziv` ASC, `HUB_ZADRUGA_JIB` ASC) VISIBLE,
  
  CONSTRAINT `fk_IoTUredjaj_HUB1`
    FOREIGN KEY (`HUB_Naziv` , `HUB_ZADRUGA_JIB`)
    REFERENCES `smartvillage`.`HUB` (`Naziv` , `ZADRUGA_JIB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    
  CONSTRAINT `fk_IoTUredjaj_PARCELA1`
    FOREIGN KEY (`PARCELA_ID_Parcele`, `HUB_Naziv`, `HUB_ZADRUGA_JIB`) 
    REFERENCES `smartvillage`.`PARCELA` (`ID_Parcele`, `HUB_Naziv`, `HUB_ZADRUGA_JIB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`PLASTENIK`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`PLASTENIK` (
  `Sifra` VARCHAR(20) NOT NULL,
  `Tip` VARCHAR(45) NOT NULL,
  `Povrsina` DECIMAL(8,2) NOT NULL,
  `VrstaNavodnjavanja` VARCHAR(45) NOT NULL,
  `HUB_Naziv` VARCHAR(45) NOT NULL,
  `HUB_ZADRUGA_JIB` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`Sifra`),
  INDEX `fk_PLASTENIK_HUB1_idx` (`HUB_Naziv` ASC, `HUB_ZADRUGA_JIB` ASC) VISIBLE,
  UNIQUE INDEX `index_unique_plastenik_hub` (`Sifra` ASC, `HUB_Naziv` ASC, `HUB_ZADRUGA_JIB` ASC) VISIBLE,
  CONSTRAINT `fk_PLASTENIK_HUB1`
    FOREIGN KEY (`HUB_Naziv` , `HUB_ZADRUGA_JIB`)
    REFERENCES `smartvillage`.`HUB` (`Naziv` , `ZADRUGA_JIB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`PROIZVOD`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`PROIZVOD` (
  `Sifra` VARCHAR(20) NOT NULL,
  `Naziv` VARCHAR(45) NOT NULL,
  `Opis` VARCHAR(450) NOT NULL,
  `Cijena` DECIMAL(8,2) NOT NULL,
  `StanjeNaZalihama` DECIMAL(10,2) NOT NULL,
  `HUB_Naziv` VARCHAR(45) NOT NULL,
  `HUB_ZADRUGA_JIB` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`Sifra`),
  INDEX `fk_PROIZVOD_HUB1_idx` (`HUB_Naziv` ASC, `HUB_ZADRUGA_JIB` ASC) VISIBLE,
  UNIQUE INDEX `index_unique_proizvod_hub` (`Sifra` ASC, `HUB_Naziv` ASC, `HUB_ZADRUGA_JIB` ASC) VISIBLE,
  CONSTRAINT `fk_PROIZVOD_HUB1`
    FOREIGN KEY (`HUB_Naziv` , `HUB_ZADRUGA_JIB`)
    REFERENCES `smartvillage`.`HUB` (`Naziv` , `ZADRUGA_JIB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`KUPAC`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`KUPAC` (
  `Sifra` VARCHAR(20) NOT NULL,
  `Naziv` VARCHAR(45) NOT NULL,
  `Telefon` VARCHAR(15) NOT NULL,
  PRIMARY KEY (`Sifra`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`NARUDZBA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`NARUDZBA` (
  `Sifra` VARCHAR(20) NOT NULL,
  `Datum` DATE NOT NULL,
  `UkupanIznos` DECIMAL(12,2) NOT NULL,
  `KUPAC_Sifra` VARCHAR(20) NOT NULL,
  `Status` TINYINT NOT NULL,
  PRIMARY KEY (`Sifra`),
  INDEX `fk_NARUDZBA_KUPAC1_idx` (`KUPAC_Sifra` ASC) VISIBLE,
  CONSTRAINT `fk_NARUDZBA_KUPAC1`
    FOREIGN KEY (`KUPAC_Sifra`)
    REFERENCES `smartvillage`.`KUPAC` (`Sifra`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`STAVKA`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`STAVKA` (
  `ID_Stavke` INT NOT NULL AUTO_INCREMENT,
  `Kolicina` DECIMAL(10,2) NOT NULL,
  `Cijena` DECIMAL(8,2) NOT NULL,
  `NARUDZBA_Sifra` VARCHAR(20) NOT NULL,
  `PROIZVOD_Sifra` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`ID_Stavke`),
  INDEX `fk_STAVKA_NARUDZBA1_idx` (`NARUDZBA_Sifra` ASC) VISIBLE,
  UNIQUE INDEX `index_unique_stavka_proizvod` (`ID_Stavke` ASC, `PROIZVOD_Sifra` ASC, `NARUDZBA_Sifra` ASC) VISIBLE,
  INDEX `fk_STAVKA_PROIZVOD1_idx` (`PROIZVOD_Sifra` ASC) VISIBLE,
  CONSTRAINT `fk_STAVKA_NARUDZBA1`
    FOREIGN KEY (`NARUDZBA_Sifra`)
    REFERENCES `smartvillage`.`NARUDZBA` (`Sifra`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_STAVKA_PROIZVOD1`
    FOREIGN KEY (`PROIZVOD_Sifra`)
    REFERENCES `smartvillage`.`PROIZVOD` (`Sifra`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`DOBAVLJAC`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`DOBAVLJAC` (
  `Sifra` VARCHAR(20) NOT NULL,
  `Naziv` VARCHAR(50) NOT NULL,
  `VrstaProizvoda` VARCHAR(30) NOT NULL,
  `ZADRUGA_JIB` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`Sifra`),
  INDEX `fk_DOBAVLJAC_ZADRUGA1_idx` (`ZADRUGA_JIB` ASC) VISIBLE,
  CONSTRAINT `fk_DOBAVLJAC_ZADRUGA1`
    FOREIGN KEY (`ZADRUGA_JIB`)
    REFERENCES `smartvillage`.`ZADRUGA` (`JIB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`ZASAD_has_PLASTENIK`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`ZASAD_has_PLASTENIK` (
  `ZASAD_KULTURA_Naziv` VARCHAR(30) NOT NULL,
  `ZASAD_PARCELA_ID_Parcele` INT NOT NULL,
  `PLASTENIK_Sifra` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`ZASAD_KULTURA_Naziv`, `ZASAD_PARCELA_ID_Parcele`, `PLASTENIK_Sifra`),
  INDEX `fk_ZASAD_has_PLASTENIK_PLASTENIK1_idx` (`PLASTENIK_Sifra` ASC) VISIBLE,
  INDEX `fk_ZASAD_has_PLASTENIK_ZASAD1_idx` (`ZASAD_KULTURA_Naziv` ASC, `ZASAD_PARCELA_ID_Parcele` ASC) VISIBLE,
  CONSTRAINT `fk_ZASAD_has_PLASTENIK_ZASAD1`
    FOREIGN KEY (`ZASAD_KULTURA_Naziv` , `ZASAD_PARCELA_ID_Parcele`)
    REFERENCES `smartvillage`.`ZASAD` (`KULTURA_Naziv` , `PARCELA_ID_Parcele`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ZASAD_has_PLASTENIK_PLASTENIK1`
    FOREIGN KEY (`PLASTENIK_Sifra`)
    REFERENCES `smartvillage`.`PLASTENIK` (`Sifra`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `smartvillage`.`HUB_has_ZAPOSLENI`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `smartvillage`.`HUB_has_ZAPOSLENI` (
  `HUB_Naziv` VARCHAR(45) NOT NULL,
  `HUB_ZADRUGA_JIB` VARCHAR(30) NOT NULL,
  `ZAPOSLENI_JMB` VARCHAR(13) NOT NULL,
  PRIMARY KEY (`HUB_Naziv`, `HUB_ZADRUGA_JIB`, `ZAPOSLENI_JMB`),
  INDEX `fk_HUB_has_ZAPOSLENI_ZAPOSLENI1_idx` (`ZAPOSLENI_JMB` ASC) VISIBLE,
  INDEX `fk_HUB_has_ZAPOSLENI_HUB1_idx` (`HUB_Naziv` ASC, `HUB_ZADRUGA_JIB` ASC) VISIBLE,
  CONSTRAINT `fk_HUB_has_ZAPOSLENI_HUB1`
    FOREIGN KEY (`HUB_Naziv` , `HUB_ZADRUGA_JIB`)
    REFERENCES `smartvillage`.`HUB` (`Naziv` , `ZADRUGA_JIB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_HUB_has_ZAPOSLENI_ZAPOSLENI1`
    FOREIGN KEY (`ZAPOSLENI_JMB`)
    REFERENCES `smartvillage`.`ZAPOSLENI` (`JMB`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
