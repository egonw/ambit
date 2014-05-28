<#include "/html.ftl" >

<head>
<#include "/header_updated.ftl" >

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					loadHelp("${ambit_root}","substance");
					
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Substance">Substances</a></li>');
		    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/uploadsubstance" title="File upload">Import</a></li>');
		    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/uploadsubstance1" title="Upload substances as defined in a single IUCLID5 .i5d or .i5z file">Single .i5z file upload</a></li>');
		    jQuery("#breadCrumb").jBreadCrumb();
					jQuery("#welcome").text("Substance Import");
				});
</script>

</head>
<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="two columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu_substance.ftl">

</div>


		<!-- Page Content
		================================================== -->

<div class="eleven columns remove-bottom" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;Import new substance(s)</div>
<div class='ui-widget-content ui-corner-bottom'>	
		
	<div style="margin:5px;padding:5px;">	
	<form action="${ambit_root}/substance" id="uploadForm"  method="POST"   ENCTYPE="multipart/form-data">		
	
	<div class='row remove-bottom'>
		<label class='three columns alpha' for="file">File (.i5z or .i5d)<a href='#' class='chelp i5z'>?</a><em>*</em></label>
		<input class='eight columns alpha half-bottom'  type="file" name="files[]" title='Add new substance(s) (.i5d or .i5z file)' size="60">
		<div class='five columns omega'></div>
	</div>
	<div class='row'>
		<label class='three columns alpha'>&nbsp;</label>
		<input class='three columns alpha' type='submit' class='submit' value='Submit'>
		<div class='ten columns omega'></div>
	</div>
	</form>	
</div>			
</div>


<div class='row' >
		&nbsp;
	</div>	
	<div class='row half-bottom'>
		<div class='four columns alpha'>&nbsp;</div>
		<div class='twelve columns omega half-bottom'>
		    Substance import options: 
			<a href="${ambit_root}/ui/uploadsubstance" title="Multiple .i5z files upload">Multiple .i5z files upload</a>
			 | 
			<a href="${ambit_root}/ui/uploadsubstance1" title="Single .i5z file upload">Single .i5z file upload</a>
			 | 
			<a href="${ambit_root}/ui/updatesubstancei5" title="Retrieve substance(s) from IUCLID5 server">Retrieve substance(s) from IUCLID5 server</a>
		</div>	
	</div>
	
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/chelp.ftl" >


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
