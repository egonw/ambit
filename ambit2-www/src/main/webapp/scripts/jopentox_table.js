/**
 * jOpenTox JavaScript Library v0.0.1 (alpha)
 * 
 * Copyright 2012, IDEAconsult Ltd.
 * http://www.ideaconsult.net/
 * 
 * TODO: Licence.
 */

var modelArray;

$(function() {$( ".datatable" ).dataTable({
	'bJQueryUI': true, 
	'bPaginate': true,
	"sDom": 'T<"clear"><"fg-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix"lfr>t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>' 
	});
});

/**
 * 
 */
$(document).ready(function() {
	    
	/* Initialize */
	oTable = $( ".modeltable" ).dataTable({
		'bProcessing': true,
		'bJQueryUI': true, 
		'bPaginate': true,
		"sDom": 'T<"clear"><"fg-toolbar ui-widget-header ui-corner-tl ui-corner-tr ui-helper-clearfix"lfr>t<"fg-toolbar ui-widget-header ui-corner-bl ui-corner-br ui-helper-clearfix"ip>' ,
		"aaData": modelArray,
		"aoColumns": [
		            { "sClass": "center", "bSortable": false, "mDataProp":null, sWidth: "5%", "bUseRendered" : "true",	
				          "fnRender": function ( o, val ) {
				                  return "<img src='/ambit2/images/zoom_in.png'>";
				          }
		            },		              
		  			{ "sTitle": "Title", "mDataProp":"title", sWidth: "50%",
		              "bUseRendered" : "false",	
		              "fnRender": function ( o, val ) {
		                  return "<a href='"+o.aData.URI +"'>" +val + "</a>";
		                }
		  			},
		  			{  "sClass": "center", "mDataProp":"algorithm.algFormat", sWidth: "5%",
			              "bUseRendered" : "false",	"bSortable": true,
			              "fnRender": function ( o, val ) {
			                  return "<img src='"+o.aData.algorithm.img +"'>";
			                }
			  		},		  			
		  			{ "sTitle": "Algorithm", "mDataProp":"algorithm.URI" , sWidth: "40%",
		  	          "bUseRendered" : "false",	
			          "fnRender": function ( o, val ) {
			        	    uri = val;
			        		pos = val.lastIndexOf("/");
			        		if (pos>=0) val = val.substring(pos+1); 
			                return "<a href='"+ uri +"'>"+val+"</a>";
			          }
		  			},
		  			{ "sTitle": "Training Dataset", "mDataProp":"trainingDataset", sWidth: "40%",
			          "bUseRendered" : "false",	
			          "fnRender": function ( o, val ) {
			        	  	shortURI = val;
			        	  	if (val.length > 60) shortURI = val.substring(val,60) + "..."; 	
			                  return "<a href='"+ val +"'>"+shortURI+"</a>";
			          }
		  			}
		  ],
		  "aaSorting": [[1, 'asc']]		  
		});
	
	/* event listener  */
	   $('.modeltable tbody td img').live( 'click', function () {
	        var nTr = $(this).parents('tr')[0];
	        if ( oTable.fnIsOpen(nTr) )
	        {
	            /* This row is already open - close it */
	            this.src = "/ambit2/images/zoom_in.png";
	            oTable.fnClose( nTr );
	        }
	        else
	        {
	            /* Open this row */
	        	this.src = "/ambit2/images/zoom_out.png";
	            oTable.fnOpen( nTr, fnFormatDetails(nTr), 'details' );
	        }
	    } );
	
	/* Formating function for row details */
	function fnFormatDetails ( nTr )
	{
	    var model = oTable.fnGetData( nTr );
	    var sOut = '<div class="ui-widget" style="margin-top: 20px; padding: 0 .7em;" >';
	    sOut += '<div class="ui-widget-header ui-corner-top"><p><b>Model: </b>' + model.title + '</p></div>';
	    sOut += '<div class="ui-widget-content ui-corner-bottom ">';
	    sOut += '<table cellpadding="5" cellspacing="0" width="100%" style="padding-left:50px;">';
	    
	    sOut += '<tr><th>Model URI</th><td><a href=\"' + model.URI + '\">' + model.URI + '</a></td><td rowspan="7">';
	    if (model.algorithm.URI.toLowerCase().indexOf('toxtree')>=0) {
	    	sOut += '<img src="'+model.ambitprop.legend+'">';
		}
	    sOut += '</td></tr>\n';
	    //form to apply the model
	    sOut += '<tr><form action="'+ model.URI + '" method="POST">';
	    sOut += '<th colspan="2">Enter dataset or compound URI to apply the model</th></tr><tr>';
	    sOut += '<th>Dataset URI</th><td><input type="text" size="80" name="dataset_uri" value=""></td></tr>';
	    sOut += '<th></th><td><input type="submit" class="ui-button ui-widget ui-state-default ui-corner-all"  aria-disabled="false" role="button" value="Predict"></td>';
	    sOut += '</form></tr>';

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

	    sOut += '</table>';
	    sOut += '</div></div>\n';
	     
	    return sOut;
	}
});

