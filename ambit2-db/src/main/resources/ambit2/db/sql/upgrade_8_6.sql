CREATE TABLE `pc1024` (
  `idchemical` int(10) unsigned NOT NULL DEFAULT '0',
  `fp1` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp2` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp3` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp4` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp5` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp6` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp7` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp8` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp9` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp10` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp11` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp12` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp13` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp14` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp15` bigint(20) unsigned NOT NULL DEFAULT '0',
  `fp16` bigint(20) unsigned NOT NULL DEFAULT '0',
  `time` int(10) unsigned DEFAULT '0',
  `bc` int(6) NOT NULL DEFAULT '0',
  `status` enum('invalid','valid','error') COLLATE utf8_bin NOT NULL DEFAULT 'invalid',
  `updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `version` int(10) unsigned zerofill NOT NULL DEFAULT '0000000000',
  PRIMARY KEY (`idchemical`),
  KEY `pcall` (`fp1`,`fp2`,`fp3`,`fp4`,`fp5`,`fp6`,`fp7`,`fp8`,`fp9`,`fp10`,`fp11`,`fp12`,`fp13`,`fp14`,`fp15`,`fp16`),
  KEY `time` (`time`),
  KEY `status` (`status`),
  CONSTRAINT `pc1024_ibfk_1` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


ALTER TABLE `structure` ADD COLUMN `hash` VARBINARY(20) NULL DEFAULT NULL  AFTER `preference` ;
ALTER TABLE `structure` ADD INDEX `Index_hash` (`hash` ASC) ;
update structure set `hash`= unhex(sha1(uncompress(structure))) ;

-- -----------------------------------------------------
-- A collection of substances and endpoints 
-- metadata
-- -----------------------------------------------------
CREATE TABLE `bundle` (
  `idbundle` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin NOT NULL DEFAULT 'default',
  `user_name` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `idreference` int(11) unsigned NOT NULL,  
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `licenseURI` varchar(128) COLLATE utf8_bin NOT NULL DEFAULT 'Unknown',
  `rightsHolder` varchar(128) COLLATE utf8_bin NOT NULL DEFAULT 'Unknown',
  `maintainer` varchar(45) COLLATE utf8_bin NOT NULL DEFAULT 'Unknown',
  `stars` int(10) unsigned NOT NULL DEFAULT '5',
  PRIMARY KEY (`idbundle`),
  UNIQUE KEY `assessment_name` (`name`),
  KEY `FK_assessment_1` (`user_name`),
  KEY `Index_6` (`maintainer`),
  KEY `Index_7` (`stars`),
  CONSTRAINT `FK_investigation_ref` FOREIGN KEY (`idreference`) REFERENCES `catalog_references` (`idreference`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


-- -----------------------------------------------------
-- A collection of substances and endpoints 
-- -----------------------------------------------------
CREATE TABLE `bundle_substance` (
  `idsubstance` int(11) NOT NULL,
  `idbundle` int(10) unsigned NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idsubstance`,`idbundle`),
  KEY `s_bundle` (`idbundle`),
  KEY `a_substance_idx` (`idsubstance`),
  CONSTRAINT `a_metadata` FOREIGN KEY (`idbundle`) REFERENCES `bundle` (`idbundle`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `a_substance` FOREIGN KEY (`idsubstance`) REFERENCES `substance` (`idsubstance`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

insert into version (idmajor,idminor,comment) values (8,6,"AMBIT2 schema");