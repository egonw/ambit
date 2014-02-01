<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<#if taskid??>
	<script type='text/javascript'>
		var task = readTask("${ambit_root}","${ambit_request_json}");
	</script>
<#else>
	<script type='text/javascript'>
	
	$(document).ready(function() {
	  	var oTable = defineTaskTable("${ambit_root}","${ambit_request_json}");
	});
	</script>
</#if>

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					$( "#selectable" ).selectable( "option", "distance", 18);
					loadHelp("${ambit_root}","task");
					downloadForm("${ambit_request}");
				});
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="seven columns alpha">
			<div class="remove-bottom h3">
					Tasks status
			</div>
		    <div class='h6'>Long running computing or data import procedures</div>			
		</div>
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu.ftl">
	<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list'></a>
	<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
	<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
	<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
	</div>
	
</div>

<div class="eleven columns remove-bottom" style="padding:0;" >


		<!-- Page Content
		================================================== -->
		<#if taskid??>
		<div class="row" style="padding:0;" >			
			<div class="ui-widget-header ui-corner-top"><a href='${ambit_root}/task/${taskid}'>Job</a> started <span id=task_started></span> &nbsp;</div>
			<div class="ui-widget-content ui-corner-bottom">
			<span id=task_status>
			<script>
				checkTask('${ambit_root}/task/${taskid}','result', 'status', '${ambit_root}/images/tick.png', '${ambit_root}/images/cross.png');
			</script>
			</span>
			<p>Name:&nbsp;<strong id=task_name></strong></p>
			<p>Status:&nbsp;<a href='#' id='result'></a>&nbsp;<img src='${ambit_root}/images/24x24_ambit.gif' id='status'>
			<span id="task_errorreport"></span>
			</p>
			</div>
		<#else>
 		<div class="row remove-bottom ui-widget-header ui-corner-top">
 		&nbsp;
 		</div>		
		<div class="row " style="padding:0;" >
			<table id='task' class='ambit2' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th>Job status</th>
			<th>Description</th>
			<th>Started at</th>
			<th>Completed at</th>
			</tr>
			</thead>
			<tbody></tbody>
			</table>
		
		</#if>
		
		</div>
		


<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>
<#include "/chelp.ftl" >



<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
