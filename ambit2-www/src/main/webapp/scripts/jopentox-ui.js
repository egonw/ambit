function defineAlgorithmTable(root,url) {

	var oTable = $('#algorithm').dataTable( {
		"sAjaxDataProp" : "algorithm",
		"bProcessing": true,
		"bServerSide": false,
		"bStateSave": false,
		"aoColumnDefs": [
				{ "mDataProp": "uri" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 0 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "sWidth" : "25%",
				  "fnRender" : function(o,val) {
					  if (o.aData["id"]==null) return "Algorithm";
					  return "<a href='"+val+"' title='"+o.aData["id"]+"'>"+o.aData["name"]+"</a>";
				  }
				},
				{ "mDataProp": "endpoint" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 1 ],	
				  "bSearchable" : true,
				  "bUseRendered" : false,
				  "bSortable" : true,
				  "sWidth" : "20%",
				  "fnRender" : function(o,val) {
					  //create link to the ontology server
					  var p = val.indexOf("#");
					  if (p>0) return val.substring(p+1);
					  else return val;
				  }
				},
				{ "mDataProp": "isDataProcessing" , "asSorting": [ "asc", "desc" ],
				  "aTargets": [ 2 ],
				  "bSearchable" : true,
				  "bSortable" : true,
				  "sWidth" : "25%",
				  "bUseRendered" : false,
				  "fnRender" : function(o,val) {
					  
					  var icon = '<span class="ui-icon ui-icon-pin-s" style="float: left; margin-right: .1em;"></span>';
					  var sOut = icon + (val?"Processes a dataset":"Builds a model.");
					  sOut += o.aData["requiresDataset"]?("<br>"+icon+"Requires input dataset"):"";
					  sOut += o.aData["requires"]==""?"":"<br>"+icon+"Requires " + o.aData["requires"];
					  sOut += o.aData["isSupevised"]?("<br>"+icon+"Requires target variable"):"";
					  return sOut;
				  }
				},
				{ "mDataProp": "type" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 3 ],
					  "bSearchable" : true,
					  "bSortable" : true,
					  "bUseRendered" : false,
					  "sWidth" : "15%",
					  "fnRender" : function(o,val) {
						  var sOut = "";
						  $.each(val, function(index, value) {
							  var p = value.indexOf("#");
							  var name = value;  if (p>0) name =  value.substring(p+1);
							  sOut += "<a href='"+val+"'>"+name+"</a>";
							  sOut += " ";
							});
						  return sOut;
					  }
				},
				{ "mDataProp": "implementationOf" , "asSorting": [ "asc", "desc" ],
					  "aTargets": [ 4 ],
					  "bSearchable" : true,
					  "bSortable" : true,
					  "sWidth" : "15%",
					  "bUseRendered" : false,
					  "fnRender" : function(o,val) {
						  var p = val.indexOf("#");
						  var name = val;  if (p>0) name =  val.substring(p+1);
						  return "<a href='http://apps.ideaconsult.net:8080/ontology?uri=" + encodeURIComponent(val) +"' target=_blank>" +name +"</a>";
					  }
				}	
			],
		"sSearch": "Filter:",
		"bJQueryUI" : true,
		"bSearchable": true,
		"sAjaxSource": url,
		"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
		"bPaginate" : true,
		"sPaginationType": "full_numbers",
		"sPaginate" : ".dataTables_paginate _paging",
		"oLanguage": {
	            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
	            "sLoadingRecords": "No algorithms found.",
	            "sInfo": "Showing _TOTAL_ algorithms (_START_ to _END_)",
	            "sLengthMenu": 'Display <select>' +
              '<option value="10">10</option>' +
              '<option value="20">20</option>' +
              '<option value="50">50</option>' +
              '<option value="100">100</option>' +
              '<option value="-1">all</option>' +
              '</select> algorithms.'	            
	    }
	} );
	return oTable;
}

/**
 * Read single algorithm by json
 * @param root
 * @param url
 */
function readAlgorithm(root,url) {
	  $.ajax({
	        dataType: "json",
	        url: url,
	        success: function(data, status, xhr) {
	        	$.each(data["algorithm"],function(index, entry) {
	        		renderAlgorithm(entry,root,null);
	        	});
	        },
	        error: function(xhr, status, err) {
	        	renderAlgorithm(null,root,err);
	        },
	        complete: function(xhr, status) {
	        }
	     });
}

/**
 * 
 * @param entry
 * @param root
 * @param err
 */
function renderAlgorithm(entry,root,err) {
	if (entry==null) {
		$("#alg_title").text("N/A");
		return;
	}
	var icon = '<span class="ui-icon ui-icon-pin-s" style="float: left; margin-right: .1em;"></span>';
	$("#alg_title").text(entry["name"]);
	$("#alg_title").prop("title",entry["content"]);
	if ((entry["endpoint"]===undefined) ||(entry["endpoint"]=="")) {
		$("#predictsEndpoint").hide();
	} else {
		var val = entry["endpoint"];
		var p = val.indexOf("#");
		if (p>0) val = val.substring(p+1);
		$("#alg_endpoint").text(val);
		$("#predictsEndpoint").show();
	}
	$("#alg_implementation").html(entry["implementationOf"]);
	$("#alg_implementation").prop('href','http://apps.ideaconsult.net:8080/ontology?uri='+encodeURIComponent(entry["implementationOf"]));
	
	$("#alg_requires").text(entry["requires"]=="structure"?"Chemical structure":entry["requires"]);
	$("#alg_dataset").text(entry["isDataProcessing"]?"Processes a dataset":"Builds a model");
	$("#help_action").html(entry["isDataProcessing"]?
		"The result is a dataset, identified by a <a href='"  + root + "/dataset' target='dataset'>dataset URI</a>.":
		"Once a model is built, it is assigned a <a href='" + 
		root + "/model' target='model'>model URI</a> and can be applied to <a href='" + 
		root + "/dataset' target='dataset'>datasets</a> and <a href='" +
		root + "/compound' target='compound'>compounds</a>.");
	if (entry["requiresDataset"]) $("#requiresDataset").show(); else $("#requiresDataset").hide();
	if (entry["isSupevised"]) $("#requiresTarget").show(); else $("#requiresTarget").hide();
	var sOut = "";
	var isFinder = false;
	var isSuperBuilder = false;
	var isSuperService = false;
	$.each(entry["type"], function(index, value) {
		  var p = value.indexOf("#");
		  var name = value;  if (p>0) name =  value.substring(p+1);
		  sOut += name;
		  sOut += "&nbsp;|&nbsp;";
		  if ("Finder"==name) isFinder = true;
		  else if ("SuperService"==name) isSuperService = true;
		  else if ("SuperBuilder"==name) isSuperBuilder = true;
	});
	sOut += "</ul>";
	$("#alg_type").html(sOut);
	
	if (isFinder) {
		$("#alg_dataset").text("Finds chemical structures by querying online services by a compound identifier");
		$("#requiresDataset").show();
		$("#finder").show();
	}  else $("#finder").hide();
	if (isSuperBuilder) {
		$("#superBuilder").show();
		$("#alg_dataset").text("Calculate descriptors, prepares a dataset and builds the model");
	}  else $("#superBuilder").hide();
	if (isSuperService) {
		$("#alg_dataset").text("Calculate descriptors, prepares a dataset and runs the model");
		$("#superService").show(); 
	} else $("#superService").hide();
}

/**
 * Models table
 * @param root
 * @param url
 * @returns
 */
function defineModelTable(root,url) {
	var oTable = $('.modeltable').dataTable( {
	"sAjaxDataProp" : "model",
	"sAjaxSource": url,	
	"sSearch": "Filter:",
	"bJQueryUI" : true,
	"bSearchable": true,
	"bProcessing" : true,
	"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
	"sSearch": "Filter:",
	"bPaginate" : true,
	"sPaginationType": "full_numbers",
	"sPaginate" : ".dataTables_paginate _paging",
	"oLanguage": {
            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
            "sZeroRecords": "No models found.",
            "sInfo": "Showing _TOTAL_ models (_START_ to _END_)",
            "sLoadingRecords": "No models found.",
            "sLengthMenu": 'Display <select>' +
          '<option value="10">10</option>' +
          '<option value="20">20</option>' +
          '<option value="50">50</option>' +
          '<option value="100">100</option>' +
          '<option value="-1">all</option>' +
          '</select> models.'	            
    },	
    "aoColumnDefs": [
    				{ //0
    					"aTargets": [ 0 ],	
    					"sClass" : "center",
    					"bSortable" : false,
    					"bSearchable" : false,
    					"mDataProp" : null,
    					sWidth : "32px",
    					"fnRender" : function(o,val) {
    						 return  "<span class='ui-icon ui-icon-folder-collapsed zoomstruc' style='float: left; margin: .1em;' title='Click to show model details'></span>";			
    					}
    				},	     	            
    	  			{ "sTitle": "Stars", 
     	              "mDataProp":"stars",
     	              "aTargets": [ 1 ]
    	  			},     	 
    	  			{ "sTitle": "Title", 
    	  			  "mDataProp":"title", 
    	  			  "aTargets": [ 2 ],	
    	  			  sWidth: "50%",
    		          "bUseRendered" : "false",	
    		          "fnRender": function ( o, val ) {
    		                return "<a href='"+o.aData.URI +"'>" +val + "</a>";
    		          }
    		  		},   
    	  			{ "sTitle": "Algorithm", 
    	  			  "mDataProp":"algorithm.URI" , 
    	  			  "aTargets": [ 3 ],	
    	  			  sWidth: "40%",
    		  	      "bUseRendered" : "false",	
    			       "fnRender": function ( o, val ) {
    			        	    uri = val;
    			        		pos = val.lastIndexOf("/");
    			        		if (pos>=0) val = val.substring(pos+1); 
        			      	  	var shortURI = val;
        			      	  	if (val.length > 20) shortURI = val.substring(val,20) + "..."; 	
    			                return "<a href='"+ uri +"' title='"+uri+"'>"+shortURI+"</a>";
    			      }
    		  		},    	  			
    	  			{ "sTitle": "Training Dataset", 
    		  		  "mDataProp":"trainingDataset",
    		  		  "aTargets": [ 4 ],	
    		  		  sWidth: "40%",
    			      "bUseRendered" : "false",	
    			      "fnRender": function ( o, val ) {
    			      	  	shortURI = val;
    			      	  	if (val.length > 20) shortURI = val.substring(val,20) + "..."; 	
    			                 return "<a href='"+ val +"' title='"+val+"'>"+shortURI+"</a>";
    			       }
    		  		},
    	  			{ "sClass": "center", 
      		  		  "mDataProp":"algorithm.algFormat", 
      		  		  "aTargets": [ 5 ],	
      		  		  sWidth: "5%",
    		              "bUseRendered" : "false",	"bSortable": true,
    		              "fnRender": function ( o, val ) {
    		                  return "<img src='"+root + o.aData.algorithm.img +"'>";
    		                }
      	  			}    		  		
     				],
 	  "aaSorting": [[1, 'asc']]		  
	});
	return oTable;
}

/**
 * Models row detais
 * @param oTable
 * @param nTr
 * @param root
 * @returns {String}
 */
function modelFormatDetails( oTable, nTr ,root ) {
    var model = oTable.fnGetData( nTr );
    var sOut = '<div class="ui-widget" >';
   // sOut += '<div class="ui-widget-header ui-corner-top"><b>Model: </b>' + model.title + '</div>';
   // sOut += '<div class="ui-widget-content ui-corner-bottom ">';
    
    sOut += '<table cellpadding="5" cellspacing="0" width="100%" style="padding-left:50px;">';
    sOut += '<tbody>';

    sOut += '<tr><td colspan="2"></td><td rowspan="7">';
    if (model.algorithm.URI.toLowerCase().indexOf('toxtree')>=0) {
    	sOut += '<img src="'+model.ambitprop.legend+'">';
	}
    sOut += '</td></tr>\n';
    
    sOut += '<tr><td>Model name</td><td><a href=\"' + model.uri + '\">' + model.title + '</a></td></tr>';
    
    if (model.trainingDataset.indexOf("http")>=0) {
	    sOut += '<tr><th>Training dataset</th><td><a href="' + model.trainingDataset + '">' + model.trainingDataset + '</a></td></tr>';
    }
	
    sOut += '<tr><td>Training algorithm</td><td><a href="' + model.algorithm.URI + '">' + model.algorithm.URI + '</a></td></tr>';
    sOut += '<tr><td>Variables</td><td>';
    sOut += '<a href="' + model.independent + '">Independent</a>|&nbsp;';
    sOut += '<a href="' + model.dependent + '">Dependent</a>|&nbsp;';
    sOut += '<a href="' + model.predicted + '">Predicted</a>&nbsp;';
    sOut += '</td></tr>\n';
    sOut += '<tr><td>Model content</td><td>'+model.ambitprop.content+' ['+model.ambitprop.mimetype+']</td></tr>';
    sOut += '<tr><td>Model URI</td><td><a href=\"' + model.URI + '\">' + model.URI + '</a></td></tr>';
    sOut += '</tbody></table>';
    //form to apply the model
    sOut += '<div class="row ui-widget-content ui-corner-all" style="padding:5px;">';
    sOut += '<form action="'+ model.URI + '" method="POST" >'
    sOut += '<label class="five columns alpha">Enter <a href="'+root+'/dataset" target="dataset">dataset</a> or <a href="'+root+'/compound" target="compound">compound URI</a> <br> to apply the model</label>'
    sOut += '<input class="eight columns omega" type="text" name="dataset_uri" value="">';
    sOut += '<input class="three columns omega" type="submit" class="ui-button ui-widget ui-state-default ui-corner-all"  aria-disabled="false" role="button" value="Predict">';
    sOut += '</form></div>';
     
    return sOut;
}

/**
 * Read single model
 */
function readModel(root,url) {
	  $.ajax({
	        dataType: "json",
	        url: url,
	        success: function(data, status, xhr) {
	        	$.each(data["model"],function(index, entry) {
	        		renderModel(entry,root,null);
	        	});
	        },
	        error: function(xhr, status, err) {
	        	renderModel(null,root,err);
	        },
	        complete: function(xhr, status) {
	        }
	     });
}

/**
 * 
 * @param entry
 * @param root
 * @param err
 */
function renderModel(entry,root,err) {
	if (entry==null) {
		$("#model_title").text("N/A");
		return;
	}
	var icon = '<span class="ui-icon ui-icon-pin-s" style="float: left; margin-right: .1em;"></span>';
	$("#model_title").text(entry["title"]);
	$("#model_title").prop("title",entry["ambitprop"]["content"]);

	$("#model_algorithm").html(entry["algorithm"]["URI"]);
	$("#model_algorithm").prop('href',entry["algorithm"]["URI"]);
	
	if ((entry["trainingDataset"]===undefined) || (entry["trainingDataset"]=="") || (entry["trainingDataset"]==null)) {
		$("#model_training").html("N/A");
	} else {
		$("#model_training").html(entry["trainingDataset"]);
		$("#model_training").prop('href',entry["trainingDataset"]);
	}
	
	$("#model_img").prop('src',root + entry["algorithm"]["img"]);
	
	$("#help_action").html(
		"Once a model is built, it is assigned a <a href='" + 
		root + "/model' target='model'>model URI</a> and can be applied to <a href='" + 
		root + "/dataset' target='dataset'>datasets</a> and <a href='" +
		root + "/compound' target='compound'>compounds</a>." +
		"The result is a dataset, identified by a <a href='"  + root + "/dataset' target='dataset'>dataset URI</a>.");

	
}

function defineDatasetsTable(root,url) {
	var oTable = $('.datasetstable').dataTable( {
	"sAjaxDataProp" : "dataset",
	"sAjaxSource": url,	
	"sSearch": "Filter:",
	"bJQueryUI" : true,
	"bSearchable": true,
	"bProcessing" : true,
	"sDom" : '<"help remove-bottom"i><"help"p>Trt<"help"lf>',
	"sSearch": "Filter:",
	"bPaginate" : true,
	"sPaginationType": "full_numbers",
	"sPaginate" : ".dataTables_paginate _paging",
	"oLanguage": {
            "sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
            "sLoadingRecords": "No datasets found.",
            "sZeroRecords": "No datasets found.",
            "sEmptyTable": "No datasets available.",
            "sInfo": "Showing _TOTAL_ datasets (_START_ to _END_)",
            "sLengthMenu": 'Display <select>' +
          '<option value="10">10</option>' +
          '<option value="20">20</option>' +
          '<option value="50">50</option>' +
          '<option value="100">100</option>' +
          '<option value="-1">all</option>' +
          '</select> datasets.'	            
    },	
    "aoColumnDefs": [
    				{ //0
    					"aTargets": [ 0 ],	
    					"sClass" : "center",
    					"bSortable" : false,
    					"bSearchable" : false,
    					"mDataProp" : null,
    					sWidth : "48px",
    					"fnRender" : function(o,val) {
     		               	var sOut = "<a href='"+o.aData.URI +"?page=0&pagesize=100'><span class='ui-icon ui-icon-link' style='float: left; margin: .1em;' title='Click to browse the dataset'></span></a>&nbsp;";
     		                sOut += "<a href='"+root + "/model?dataset=" + encodeURIComponent(o.aData.URI) +"'><span class='ui-icon ui-icon-calculator' style='float: left; margin: .1em;' title='Retrieve models using this dataset as a training dataset'></span></a>&nbsp;";
     		                sOut += "<a href='"+o.aData.URI +"/similarity?search=c1ccccc1'><span class='ui-icon ui-icon-heart' style='float: left; margin: .1em;' title='Similarity search within the dataset'></span></a>&nbsp;";
     		                sOut += "<a href='"+o.aData.URI +"/smarts?search=c1ccccc1'><span class='ui-icon ui-icon-search' style='float: left; margin: .1em;' title='Substructure search within the dataset'></span></a> ";
    						//sOut += "<br/><span class='ui-icon ui-icon-folder-collapsed zoomstruc' style='float: left; margin: .1em;' title='Click to show datasets details'></span>";
    						return sOut;
    					}
    				},	     	            
    	  			{ "sTitle": "Stars", 
    				  "bSortable" : true,
     	              "mDataProp":"stars",
     	              "aTargets": [ 1 ],
     	              sWidth : "1em"
    	  			},     	 
    	  			{ "sTitle": "Title", 
    	  			  "mDataProp":"title", 
    	  			  "aTargets": [ 2 ],	
    		          "bUseRendered" : "false",	
    		          "fnRender": function ( o, val ) {
    		        	   var sOut = val;
    		               var seeAlso =  o.aData["seeAlso"];
    		               if ((seeAlso != undefined) && (seeAlso != null)) {
    		            	   if (seeAlso.indexOf('http')==0)
    		            		   sOut += ("<br/>Source: <a href='" + seeAlso + "' target=_blank>"+seeAlso+"</a>");
    		               }
    		               var rights =  o.aData["rights"];
    		               if ((rights != undefined) && (rights != null)) {
    		            	   if (rights["URI"].indexOf('http')==0)
    		            		   sOut += ("<br/>" + rights["type"]+ ": <a href='" + rights["URI"] + "' target=_blank>"+rights["URI"]+"</a>");
    		            	   else sOut += "<br/>" + rights["URI"];
    		               }    		           
   		                   sOut += "<br/><a href='"+root + "/ui/query?option=auto&type=url&search=" + encodeURIComponent(o.aData.URI) +"&page=0&pagesize=100'>Browse structures and properties</a>";
   		                   sOut += " | <a href='"+o.aData.URI +"/compounds'>Compounds only</a>";
   		                   sOut += " | <a href='"+o.aData.URI +"/feature'>Columns</a>";
   		                   sOut += " | <a href='"+o.aData.URI +"/metadata'>Metadata</a>";
    		               return sOut;
    		          }
    		  		},   
    	  			{ "sTitle": "Download", 
    	  			  "mDataProp":null , 
    	  			  "aTargets": [ 3 ],	
    	  			  sWidth: "15%",
    		  	      "bUseRendered" : "false",	
    			       "fnRender": function ( o, val ) {
    			    	   val = o.aData["URI"];
    			    	   var sOut = "<a href='"+getMediaLink(val,"chemical/x-mdl-sdfile")+"' id='sdf'><img src='"+root+"/images/sdf.jpg' alt='SDF' title='Download as SDF' /></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"chemical/text/csv")+"' id='csv'><img src='"+root+"/images/excel.png' alt='CSV' title='Download as CSV (Comma delimited file)'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/plain")+"' id='txt'><img src='"+root+"/images/excel.png' alt='TXT' title='Download as TXT'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"chemical/x-cml")+"' id='cml'><img src='"+root+"/images/cml.jpg' alt='CML' title='Download as CML (Chemical Markup Language)'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"chemical/x-daylight-smiles")+"' id='smiles'><img src='"+root+"/images/smi.png' alt='SMILES' title='Download as SMILES'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"chemical/x-inchi")+"' id='inchi'><img src='"+root+"/images/inchi.png' alt='InChI' title='Download as InChI'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/x-arff")+"' id='arff'><img src='"+root+"/images/weka.jpg' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/x-arff-3col")+"' id='arff3col'><img src='"+root+"/images/weka.jpg' alt='ARFF' title='Download as ARFF (Weka machine learning library I/O format)'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"application/rdf+xml")+"' id='rdfxml'><img src='"+root+"/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"text/n3")+"' id='rdfn3'><img src='"+root+"/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'/></a> ";
    			    	   sOut += "<a href='"+getMediaLink(val,"application/json")+"' id='json' target=_blank><img src='"+root+"/images/json.png' alt='json' title='Download as JSON'/></a>";
    			    	   return sOut;
    			      }
    		  		}	  		
     				],
 	  "aaSorting": [[1, 'desc']]		  
	});
	return oTable;
}

function getMediaLink(uri, media) {
	return uri + "?media=" + encodeURIComponent(media);
}