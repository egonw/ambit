var facet = {
		root: null,
		substanceComponent : null,
		searchStudy : function (event) {
			var selected = $("input[name^='category']:checked:enabled",'#fsearchForm');
			var params = [{'name' : 'type', 'value' : 'facet'}];
			$.each(selected,function(index,value) {
				var item = {};
				item['name']  = $(value).attr('name');
				item['value'] = $(value).attr('value');
				params.push(item);
			
				var name = 'endpoint.' +item['value'];
				var endpointBox = $("input[name^='"+name+"']",'#fsearchForm');
				var val = endpointBox.val();
				if ((val != undefined) && (val != null) && ("" != val))
					params.push({"name" : name , "value" : val});
				
				var name = 'lovalue.' +item['value'];
				var endpointvalueBox = $("input[name^='"+name+"']",'#fsearchForm');
				val = endpointvalueBox.val();
				if ((val != undefined) && (val != null) && ("" != val))
					params.push({"name" : name , "value" : val});
				
				name = 'upvalue.' +item['value'];
				var endpointvalueBox = $("input[name^='"+name+"']",'#fsearchForm');
				val = endpointvalueBox.val();
				if ((val != undefined) && (val != null) && ("" != val))
					params.push({"name" : name , "value" : val});				

			});
			if (this.substanceComponent != undefined && this.substanceComponent!= null)  {
				var substanceQuery =  this.root + '/substance?' + jQuery.param(params);
				this.substanceComponent.querySubstance(substanceQuery);
				$("#hits").show();
			}	
			if (event!=null) event.preventDefault();
		},
		defineStudySearchFacets : function (root,url,selector) {
			var oTable = $(selector).dataTable( {
				"sAjaxDataProp" : "facet",
				"bProcessing": true,
				"bServerSide": false,
				"bStateSave": false,
				"aaSorting": [[1, 'asc']],
				"aoColumnDefs": [
				        {
				        	"mData": "value" ,
				        	"aTargets": [ 0 ],	
				        	"bSortable" : false,
				        	"bSearchable" : false,
				        	"bUseRendered" : false,
				        	 "fnRender" : function(o,val) {
				        		 return "<input type='checkbox' name='category' value='"+o.aData["subcategory"]+"."+o.aData["endpoint"]+"'>"; 
				        	 }
				        },         
		 				{ "mData": "value" , 
		 				  "asSorting": [ "asc", "desc" ],
						  "aTargets": [ 1 ],	
						  "bSearchable" : true,
						  "bUseRendered" : false,
						  "sClass" : "details-control",
						  "bSortable" : true,
						  "fnRender" : function(o,val) {
							  var sOut = (o.aData["value"]===undefined)? o.aData["uri"]:o.aData["value"];
							  sOut =
								  "<span class='ui-icon ui-icon-folder-collapsed' style='float:left;margin: .1em;' title='Click to show endpoints'></span>"+
								  sOut + " (<a href='"+o.aData["uri"]+"' title='Click to view substances'>"+ +o.aData["substancescount"] +"</a>)" +
								  " <span title='number of values'>[" + o.aData["count"] + "]</span>"
								  ;
							  return sOut;
						  }
						}			
					],
				"sDom" : '<"help remove-bottom"><"help">Trt<"help">',
				"bJQueryUI" : true,
				"bPaginate" : true,
				"sPaginationType": "full_numbers",
				"sPaginate" : ".dataTables_paginate _paging",
				"bDeferRender": true,
				"bSearchable": true,
				"sAjaxSource": url,
				"iDisplayLength" : -1,
				"fnDrawCallback" : function() {
					    $(selector + " thead").remove();
			    },
				"oLanguage": {
						"sSearch": "Filter:",
						"sProcessing": "<img src='"+root+"/images/24x24_ambit.gif' border='0'>",
			            "sLoadingRecords": "No records found."
			    }
			} );
			
			$(selector + ' tbody').on('click','td.details-control span',function() {
				var nTr = $(this).parents('tr')[0];
				if (oTable.fnIsOpen(nTr)) {
					$(this).removeClass("ui-icon-folder-open");
					$(this).addClass("ui-icon-folder-collapsed");
					this.title='Click to show endpoints';
					oTable.fnClose(nTr);
				} else {
					$(this).removeClass("ui-icon-folder-collapsed");
					$(this).addClass("ui-icon-folder-open");
					this.title='Click to close endpoints';
					oTable.fnOpen(nTr, facet.endpointFormatDetails(oTable,nTr,"${ambit_root}"),	'details');
											       
				}
			});	
			return oTable;
		},
		

		endpointFormatDetails : function ( oTable, nTr ,root ) {
		    var model = oTable.fnGetData( nTr );
		    var div = $('<div class="ui-widget remove-bottom" />');
		    var id = model.subcategory+"."+model.endpoint;
		    
		    div.append($('<label>Endpoint</label>'));
		    var endpointBox = $('<input type="text">');
		    endpointBox.attr("id", "endpoint."+id).attr("name", "endpoint."+id);
		    div.append(endpointBox);
		    div.append($('<label>Value</label>'));
		    var endpointvalueBox = $('<input type="text" size="20">');
		    endpointvalueBox.attr("id", "lovalue."+id).attr("name", "lovalue."+id);
		    div.append(endpointvalueBox);
		    endpointvalueBox = $('<input type="text" size="20">');
		    endpointvalueBox.attr("id", "upvalue."+id).attr("name", "upvalue."+id);
		    div.append(endpointvalueBox);
		    return div;
		}
}






