ALTER TABLE `bundle_chemicals` CHANGE COLUMN `remarks` `remarks` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL ;
ALTER TABLE `bundle_substance` CHANGE COLUMN `remarks` `remarks` TEXT CHARACTER SET 'utf8' COLLATE 'utf8_bin' NULL DEFAULT NULL  ;

insert into version (idmajor,idminor,comment) values (8,12,"AMBIT2 schema");