ALTER TABLE `bundle_chemicals` CHANGE COLUMN `remarks` `remarks` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ;
ALTER TABLE `bundle_substance` CHANGE COLUMN `remarks` `remarks` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL  ;

ALTER TABLE `substance_protocolapplication` 
ADD INDEX `guidance` (`topcategory` ASC, `endpointcategory` ASC, `guidance` ASC, `interpretation_result` ASC) ;

-- missing updates
-- ALTER TABLE `substance` ADD INDEX `owner-name` (`owner_name` ASC) ;
-- ALTER TABLE `substance_protocolapplication` CHANGE COLUMN `isRobustStudy` `isRobustStudy` TINYINT(1) NULL DEFAULT NULL  , CHANGE COLUMN `isUsedforClassification` `isUsedforClassification` TINYINT(1) NULL DEFAULT NULL  , CHANGE COLUMN `isUsedforMSDS` `isUsedforMSDS` TINYINT(1) NULL DEFAULT NULL  ;
-- DROP TABLE IF EXISTS `chemstats`;

ALTER TABLE `ontobucket` CHANGE COLUMN `relation` `relation` ENUM('label','subclass','db','endpoint','endpointhash','hash','protocol','substancetype') CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT 'subclass'  ;

insert into version (idmajor,idminor,comment) values (8,12,"AMBIT2 schema");