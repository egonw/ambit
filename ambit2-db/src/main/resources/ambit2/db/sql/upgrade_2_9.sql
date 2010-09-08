DROP TABLE IF EXISTS `query_results`;
DROP TABLE IF EXISTS `models`;
DROP TABLE IF EXISTS `query`;
DROP TABLE IF EXISTS `sessions`;

CREATE TABLE  `sessions` (
  `idsessions` int(10) unsigned NOT NULL auto_increment,
  `user_name` varchar(16) collate utf8_bin NOT NULL,
  `started` timestamp NOT NULL default CURRENT_TIMESTAMP,
  `completed` timestamp,
  `title` varchar(45) collate utf8_bin NOT NULL default 'Default',
  PRIMARY KEY  (`idsessions`),
  UNIQUE KEY `Index_3` USING BTREE (`title`,`user_name`),
  KEY `FK_sessions_1` (`user_name`),
  CONSTRAINT `FK_sessions_1` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE  `query` (
  `idquery` int(10) unsigned NOT NULL auto_increment,
  `idsessions` int(10) unsigned NOT NULL,
  `name` text collate utf8_bin NOT NULL,
  `content` text collate utf8_bin NOT NULL,
  PRIMARY KEY  (`idquery`),
  UNIQUE KEY `Index_3` USING BTREE (`name`(255),`idsessions`),
  KEY `FK_query_1` (`idsessions`),
  CONSTRAINT `FK_query_1` FOREIGN KEY (`idsessions`) REFERENCES `sessions` (`idsessions`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='Structure queries';


CREATE TABLE  `query_results` (
  `idquery` int(10) unsigned NOT NULL,
  `idchemical` int(10) unsigned NOT NULL,
  `idstructure` int(11) unsigned NOT NULL,
  `selected` tinyint(1) NOT NULL default '1',
  `metric` float(10,6) default NULL,
  `text` varchar(200) collate utf8_bin default NULL,
  PRIMARY KEY  (`idquery`,`idchemical`,`idstructure`),
  KEY `FK_query_results_2` (`idstructure`),
  KEY `FK_query_results_3` (`idchemical`),
  KEY `Index_4` USING BTREE (`idquery`,`metric`),
  KEY `Index_5` (`idquery`),
  KEY `Index_6` (`idquery`,`text`),
  CONSTRAINT `FK_query_results_1` FOREIGN KEY (`idquery`) REFERENCES `query` (`idquery`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_query_results_2` FOREIGN KEY (`idstructure`) REFERENCES `structure` (`idstructure`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_query_results_3` FOREIGN KEY (`idchemical`) REFERENCES `chemicals` (`idchemical`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;




CREATE TABLE  `models` (
  `idmodel` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(45) collate utf8_bin NOT NULL,
  `idquery` int(10) unsigned default NULL COMMENT 'dataset',
  `predictors` int(10) unsigned NOT NULL COMMENT 'template for predictors',
  `dependent` int(10) unsigned NOT NULL COMMENT 'template for dependent variables',
  `content` blob NOT NULL,
  PRIMARY KEY  (`idmodel`),
  UNIQUE KEY `Index_5` USING BTREE (`name`),
  KEY `FK_models_predictors` (`predictors`),
  KEY `FK_models_dataset` (`idquery`),
  KEY `FK_models_dependent` (`dependent`),
  CONSTRAINT `FK_models_dataset` FOREIGN KEY (`idquery`) REFERENCES `query` (`idquery`) ON UPDATE CASCADE,
  CONSTRAINT `FK_models_dependent` FOREIGN KEY (`dependent`) REFERENCES `template` (`idtemplate`) ON UPDATE CASCADE,
  CONSTRAINT `FK_models_predictors` FOREIGN KEY (`predictors`) REFERENCES `template` (`idtemplate`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

insert into version (idmajor,idminor,comment) values (2,9,"AMBIT2 schema");

ALTER TABLE `structure` ADD COLUMN `preference` INTEGER UNSIGNED NOT NULL DEFAULT 9999 AFTER `atomproperties`, ADD INDEX `Index_6`(`preference`);

ALTER TABLE `models` ADD COLUMN `hidden` BOOLEAN NOT NULL DEFAULT 0 AFTER `predicted`;

ALTER TABLE `catalog_references` ADD COLUMN `type` ENUM('Unknown','Dataset','Algorithm','Model','BibtexEntry','BibtexArticle','BibtexBook') NOT NULL DEFAULT 'Dataset' AFTER `url`;


ALTER TABLE `structure` DROP INDEX `Index_6`,
 ADD INDEX `Index_6` USING BTREE(`preference`, `idchemical`);

ALTER TABLE `models` MODIFY COLUMN `content` LONGBLOB NOT NULL;

-- 3.0
ALTER TABLE `structure` DROP INDEX `Index_6`, ADD INDEX `Index_6` USING BTREE(`idchemical`, `preference`, `idstructure`);
ALTER TABLE `structure` DROP INDEX `Index_pref`, ADD INDEX `Index_pref` USING BTREE( `preference`, `idchemical`);



select idchemical, comments, group_concat(distinct(value_num)),group_concat(distinct(value)), 
group_concat(distinct(title)), group_concat(distinct(url)) 
from property_values left join property_string using(idvalue_string) 
join structure using(idstructure)
 join properties using(idproperty)
  join catalog_references using(idreference) where idchemical = 684 and 
  ( comments="http://www.opentox.org/api/1.1#ChemicalName" or 
  comments="http://www.opentox.org/api/1.1#IUPACName" or 
  comments="http://www.opentox.org/api/1.1#CASRN" or 
  comments="http://www.opentox.org/api/1.1#SMILES" or 
  comments="http://www.opentox.org/api/1.1#EINECS" or 
  comments="http://www.opentox.org/api/1.1#InChI" or
   comments="http://www.opentox.org/api/1.1#REACHRegistrationDate" ) 
   group by comments order by comments\G

   -- 3.1 
   --- pairwise properties
   
-- -----------------------------------------------------
-- Table `property_pairstruc` values, assigned for pair of structures
-- -----------------------------------------------------
DROP TABLE IF EXISTS `property_pairstruc`;
CREATE TABLE  `property_pairstruc` (
  `idstructure1` int(10) unsigned NOT NULL auto_increment COMMENT 'First structure id',
  `idstructure2` int(10) unsigned NOT NULL COMMENT 'Second structure id',
  `idproperty` int(10) unsigned NOT NULL COMMENT 'Property id',
  `user_name` varchar(16) collate utf8_bin NOT NULL COMMENT 'User',
  `status` enum('OK','UNKNOWN','ERROR','TRUNCATED') collate utf8_bin NOT NULL,
  `text` text collate utf8_bin COMMENT 'Text value, if longer than allowed by property_string',
  `idvalue_string` int(10) unsigned NOT NULL COMMENT 'link to property_string',
  `value_num` double(14,4) NOT NULL COMMENT 'numeric value',
  `idtype` enum('STRING','NUMERIC') collate utf8_bin NOT NULL,
  PRIMARY KEY  USING BTREE (`idstructure1`,`idstructure2`,`idproperty`),
  KEY `FK_relationship_struc_2` (`idstructure2`),
  KEY `FK_relationship_struc_3` (`idproperty`),
  KEY `FK_relationship_struc_4` (`user_name`),
  KEY `FK_relationship_struc_5` (`idvalue_string`),
  CONSTRAINT `FK_relationship_struc_1` FOREIGN KEY (`idstructure1`) REFERENCES `structure` (`idstructure`),
  CONSTRAINT `FK_relationship_struc_2` FOREIGN KEY (`idstructure2`) REFERENCES `structure` (`idstructure`),
  CONSTRAINT `FK_relationship_struc_3` FOREIGN KEY (`idproperty`) REFERENCES `properties` (`idproperty`),
  CONSTRAINT `FK_relationship_struc_4` FOREIGN KEY (`user_name`) REFERENCES `users` (`user_name`),
  CONSTRAINT `FK_relationship_struc_5` FOREIGN KEY (`idvalue_string`) REFERENCES `property_string` (`idvalue_string`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;