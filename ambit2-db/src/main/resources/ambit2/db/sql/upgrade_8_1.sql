-- Some I5 sections have long names
ALTER TABLE `substance_protocolapplication` CHANGE COLUMN `endpointcategory` `endpointcategory` VARCHAR(45) NULL DEFAULT NULL  ;

-- and some results are really just strings
ALTER TABLE `substance_experiment` CHANGE COLUMN `upvalue` `upValue` DOUBLE NULL DEFAULT NULL  , ADD COLUMN `textValue` VARCHAR(255) NULL DEFAULT NULL  AFTER `upValue` ;

insert into version (idmajor,idminor,comment) values (8,1,"AMBIT2 schema");

-- extend the view
CREATE  OR REPLACE VIEW `substance_study_view` AS
select idsubstance,substance_prefix,substance_uuid,documentType,format,
name,publicname,content,substanceType,
rs_prefix,rs_uuid,
owner_prefix,owner_uuid,owner_name,
p.document_prefix,p.document_uuid,
topcategory,endpointcategory,p.endpoint,
guidance,
reliability,isRobustStudy,purposeFlag,studyResultType,
params,interpretation_result,interpretation_criteria,
reference,updated,idresult,
e.endpoint as effectendpoint,conditions,unit, 
loQualifier, loValue, upQualifier, upValue, textValue from substance s
join substance_protocolapplication p on
s.prefix=p.substance_prefix and s.uuid=p.substance_uuid
left join substance_experiment e on
p.document_prefix=e.document_prefix and p.document_uuid=e.document_uuid
;