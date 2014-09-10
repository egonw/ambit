{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/uri-list",
        "image/png",
        "application/x-javascript"
    ],		
    "resourcePath": "/substance",
	"apis": [
			{
			    "path": "/substance",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "List substances",
			            "notes": "Returns a list of substances, according to the search criteria",
			            "type": "Substance",
			            "nickname": "getSubstances",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "search",
							    "description": "Search parameter",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false,
							    "defaultValue": "formaldehyde"
							},
							{
							    "name": "type",
							    "description": "Query type",
							    "required": true,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false,
							    "defaultValue": "name",
							    "enum" : [
							       "name",
							       "uuid",
							       "CompTox",
							       "DOI",
							       "reliability",
							       "purposeFlag",
							       "studyResultType",
							       "isRobustStudy",
							       "citation",
							       "topcategory",
							       "endpointcategory",
							       "params",
							       "owner_name",
							       "owner_uuid",
							       "related",
							       "reference",
							       "facet"
							    ]
							},	
							{
							    "name": "compound_uri",
							    "description": "If type=related finds all substances containing this compound; if type=reference - finds all substances with this compound as reference structure",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false
							},							
							<#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			     			{
			     				"code": 200,
			     				 "message": "OK. Substance(s) found"
			     			},				                                 
			                {
			                    "code": 404,
			                    "message": "Substances not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        {
			            "method": "POST",
			            "summary": "Import substance(s) and studies",
			            "notes": "Import substance(s) and studies. Supports IUCLID5 <a href='http://iuclid.eu/index.php?fuseaction=home.format'>*.i5z</a> files and custom <a href='https://github.com/ideaconsult/Protein_Corona'>12 line header</a> CSV files",
			            "type": "Task",
			            "nickname": "uploadSubstance",
		                "consumes": [
		                       "multipart/form-data",
		                       "application/x-www-form-urlencoded"
		                ],			            
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "files[]",
							    "description": "Files to upload",
							    "required": true,
							    "type": "File",
							    "paramType": "form",
							    "allowMultiple": true
							},
							{
							    "name": "qaenabled",
							    "description": "Import only high quality study records",
							    "required": false,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false,
							    "defaultValue": "name",
							    "enum" : [
							       "checked","on","yes","off",""
							    ]
							},	
							{
							    "name": "clearMeasurements",
							    "description": "Clear existing study records of imported substance(s)",
							    "required": false,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false,
							    "defaultValue": "name",
							    "enum" : [
							       "checked","on","yes","off",""
							    ]
							},	
							{
							    "name": "clearComposition",
							    "description": "Clear existing composition records of imported substance(s)",
							    "required": false,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false,
							    "defaultValue": "name",
							    "enum" : [
							       "checked","on","yes","off",""
							    ]
							},		
							{
							    "name": "purposeflag",
							    "description": "Purpose flag: 921 (key study); 1590 (supporting study); 1661 (weight of evidence); 8108 (disregarded study); NOT_SPECIFIED; null",							    
							    "required": false,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": true,
							    "defaultValue": "name",
							    "enum" : [
							       "921","1590","1661","8108","NOT_SPECIFIED","null"	
							    ]
							},			
							{
							    "name": "studyresulttype",
							    "description": "Study result type: 1895 (experimental result);1896 (experimental study planned); 2303 (read-across based on grouping of substances (category approach)); 2304 (read-across from supporting substance (structural analogue or surrogate)); 14 ((Q)SAR); 1342 (other); 1173 (no data); NOT_SPECIFIED; null",							    
							    "required": false,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": true,
							    "defaultValue": "name",
							    "enum" : [
							       "1895","1896","2303","2304","14","1342","1173","NOT_SPECIFIED","null"						       
							    ]
							},		
							{
							    "name": "testmaterial",
							    "description": "Test material : 2480 (yes); 2158 (no); NOT_SPECIFIED; null",
							    "required": false,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": true,
							    "defaultValue": "name",
							    "enum" : [
							       "2480","2158","NOT_SPECIFIED","null"						       
							    ]
							},	
							{
							    "name": "reliability",
							    "description": "Reliability : 16 - 1 (reliable without restriction); 18 - 2 (reliable with restrictions); 22 - 3 (not reliable); 24 - 4 (not assignable); 1342 - other; NOT_SPECIFIED; null",		
							    "required": false,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": true,
							    "defaultValue": "name",
							    "enum" : [
							       "16","18","22","24","1342","NOT_SPECIFIED", "null"							       
							    ]
							},	
							{
							    "name": "referencetype",
							    "description": "Reference type: 1586 (study report); 266  (other company data; 1433 (publication); 1486 (review article or handbook);\n1542 (secondary source);811 (grey literature);1342 (title);NOT_SPECIFIED; null",						    
							    "required": false,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": true,
							    "defaultValue": "name",
							    "enum" : [
							         "1586","266","1433","1486","1542","811","1342","NOT_SPECIFIED","null"
							    ]
							}	            
			            ],
			            "responseMessages": [
			         	    <#include "/apidocs/error_task.ftl" >,	
			                {
			                  "code": 404,
			                   "message": "Model not found"
			                 },
			                {
			       	           "code": 400,
			       	           "message": "Bad request"
			       	        },	 		                    
			                <#include "/apidocs/error_aa.ftl" >,
			                <#include "/apidocs/error_500.ftl" >			                
			            ]
			        }			        
			    ]
			},
			{
			    "path": "/substance/{uuid}",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get a substance",
			            "notes": "Returns substance representation",
			            "type": "Substance",
			            "nickname": "getSubstanceByUUID",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
			                <#include "/apidocs/parameter_substance_uuid.ftl" >
			                ,
							{
							    "name": "property_uris[]",
							    "description": "Property URIs",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": true
							}							
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid substance identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Substance not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			},
			{
			    "path": "/substance/{uuid}/studysummary",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get study summary for the substance",
			            "notes": "Study summary",
			            "type": "StudySummaryFacet",
			            "nickname": "getSubstanceStudySummary",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
			                  <#include "/apidocs/parameter_substance_uuid.ftl" >,
								{
								    "name": "top",
								    "description": "Top endpoint category",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false,
								    "enum" : ["P-CHEM","ENV FATE","ECOTOX","TOX"]
								},
								{
								    "name": "category",
								    "description": "Endpoint category (The value in the facet.category.title field)",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false,
								    "defaultValue" :"",
								    <#include "/apidocs/parameter_endpointcategory_enum.ftl" >
								},
								{
								    "name": "property",
								    "description": "Property UUID,  see Property service",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false
								},
								{
								    "name": "property_uri",
								    "description": "Property URI ${ambit_root}/property/{UUID} , see Property service",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false
								}			                  
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid substance identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Substance not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			},
			{
			    "path": "/substance/{uuid}/study",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get substance study",
			            "notes": "Substance study",
			            "type": "SubstanceStudy",
			            "nickname": "getSubstanceStudy",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
			                  <#include "/apidocs/parameter_substance_uuid.ftl" >,
								{
								    "name": "top",
								    "description": "Top endpoint category",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false,
								    "enum" : ["P-CHEM","ENV FATE","ECOTOX","TOX"]
								},
								{
								    "name": "category",
								    "description": "Endpoint category (The value in the protocol.category.code field)",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false,
								    "defaultValue" :"",
								    <#include "/apidocs/parameter_endpointcategory_enum.ftl" >
								},
								{
								    "name": "property",
								    "description": "Property UUID",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false
								},
								{
								    "name": "property_uri",
								    "description": "Property URI ${ambit_root}/property/{UUID} , see Property service",
								    "required": false,
								    "type": "string",
								    "paramType": "query",
								    "allowMultiple": false
								},
								<#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid substance identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Substance not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			},			
			{
			    "path": "/substance/{uuid}/composition",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get substance composition",
			            "notes": "Substance composition",
			            "type": "SubstanceComposition",
			            "nickname": "getSubstanceComposition",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
			                  <#include "/apidocs/parameter_substance_uuid.ftl" >,
							  <#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid substance identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Substance not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			},
			{
			    "path": "/substance/{uuid}/structures",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get substance composition as a dataset",
			            "notes": "Substance composition",
			            "type": "Dataset",
			            "nickname": "getSubstanceComposition",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
			                  <#include "/apidocs/parameter_substance_uuid.ftl" >,
  							  <#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid substance identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Substance not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			}
				
    ],
    "models" : {
    	"StudySummaryFacet" : {
    		
    	}
    },
	<#include "/apidocs/info.ftl" >  
}