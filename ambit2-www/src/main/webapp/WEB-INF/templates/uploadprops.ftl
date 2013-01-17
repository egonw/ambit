<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					$( "#selectable" ).selectable( "option", "distance", 18);
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
			<h3 class="remove-bottom">
					Properties import	
			</h3>
		    <h6>Import properties for compounds already in the database</h6>			
		</div>
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu.ftl">
<ul id="selectable">
<li class="ui-selectee">
<a href="${ambit_root}/admin"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>Admin</a>
</li>
</ul>
</div>

<div class="eleven columns remove-bottom" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;Import properties</div>
<div class='ui-widget-content ui-corner-bottom'>	
		
	<div style="margin:5px;padding:5px;">	
	<form action="${ambit_root}/dataset?method=put" id="uploadForm"  method="POST"   ENCTYPE="multipart/form-data">		
	
	<div class='row remove-bottom'>
		<label class='three columns alpha' for="file">File <em>*</em></label>
		<input class='eight columns alpha half-bottom'  type="file" name="file" accept="chemical/x-mdl-sdfile" title='Add new dataset (SDF, MOL, SMI, CSV, TXT, ToxML (.xml) file)' size="60">
		<div class='five columns omega'></div>
	</div>
	<div class='row remove-bottom'>
		<label class='three columns alpha' for="title">Dataset name</label>
		<input class='eight columns alpha half-bottom' type="text" id='title' value='' name='title' title='Dataset name (dc:title)' size="60">
		<div class='five columns omega'></div>
	</div>
	<div class='row remove-bottom'>
		<label class='three columns alpha' for="seeAlso">URL</label>
		<input class='eight columns alpha half-bottom' type="text" name='seeAlso' title='Related URL (rdfs:seeAlso)' size="60">
		<div class='five columns omega'></div>
	</div>		
	<div class='row remove-bottom'>
		<label class='three columns alpha' for="match">Match</label>
		<select  class='eight columns alpha half-bottom'  name='match'>
			<option title='On import, finds the same compound in the database by matching with the selected criteria "Match by CAS registry number"' value='CAS' selected>Match by CAS registry number</option>
			<option title='On import, finds the same compound in the database by matching with the selected criteria "Match by EINECS registry number"' value='EINECS' >Match by EINECS registry number</option>
			<option title='On import, finds the same compound in the database by matching with the selected criteria "Match by PubChem Compound ID (PUBCHEM_COMPOUND_CID)"' value='PubChemID' >Match by PubChem Compound ID (PUBCHEM_COMPOUND_CID)</option>
			<option title='On import, finds the same compound in the database by matching with the selected criteria "Match by DSSTox Chemical ID (DSSTox_CID) number uniquely assigned to a particular STRUCTURE across all DSSTox files"' value='DSSToxCID' >Match by DSSTox Chemical ID (DSSTox_CID) number uniquely ass...</option>
			<option title='On import, finds the same compound in the database by matching with the selected criteria "Match by DSSTox Record ID (DSSTox_RID) is number uniquely assigned to each DSSTox record across all DSSTox files"' value='DSSToxRID' >Match by DSSTox Record ID (DSSTox_RID) is number uniquely as...</option>
			<option title='On import, finds the same compound in the database by matching with the selected criteria "Records with the same DSSTox_Generic_SID (Generic Substance ID) will share all DSSTox Standard Chemical Fields, including STRUCTURE. Field distinguishes at the level of "Test Substance" across all DSSTox data files, most often corresponding to the level of CASRN distinction, but not always."' value='DSSToxGenericSID' >Records with the same DSSTox_Generic_SID (Generic Substance ...</option>
			<option title='On import, finds the same compound in the database by matching with the selected criteria "Match by InChI"' value='InChI' >Match by InChI</option>
			<option title='On import, finds the same compound in the database by matching with the selected criteria "Match by SMILES"' value='SMILES' >Match by SMILES</option>
			<option title='On import, finds the same compound in the database by matching with the selected criteria "http://rdf.farmbio.uu.se/chembl/onto/#forMolecule"' value='ChEMBL' >http://rdf.farmbio.uu.se/chembl/onto/#forMolecule</option>
			<option title='On import, finds the same compound in the database by matching with the selected criteria "Match by column "SAMPLE""' value='SAMPLE' >Match by column "SAMPLE"</option>
			<option title='On import, finds the same compound in the database by matching with the selected criteria "Match by chemical name"' value='NAME' >Match by chemical name</option>
			<option title='On import, finds the same compound in the database by matching with the selected criteria "Don't match, add as a new structure"' value='None' >Don't match, add as a new structure</option>
		</select>
		<div class='ten columns omega'></div>
	</div>
			
	<div class='row remove-bottom'>
		<label class='three columns alpha'  for="license">License</label>
		<select  class='eight columns alpha half-bottom'  name='license'>
			<option title='License not defined' value='Unknown'>Unknown</option>
			<option title="Public Domain Dedication and License (PDDL) - 'Public Domain for data/databases'" value='http://www.opendatacommons.org/licenses/pddl/'> http://www.opendatacommons.org/licenses/pddl/ </option>
			<option title="Open Data Commons Attribution (ODC-By) - 'Attribution for data/databases'" value='http://www.opendatacommons.org/licenses/by/'>http://www.opendatacommons.org/licenses/by/</option>
			<option title="Open Database License (ODC-ODbL) - 'Attribution Share-Alike for data/databases'" value='http://www.opendatacommons.org/licenses/odbl/'>http://www.opendatacommons.org/licenses/odbl/</option><option title='CC0 1.0 Universal - 'Creative Commons public domain waiver'' value='http://creativecommons.org/publicdomain/zero/1.0/'>http://creativecommons.org/publicdomain/zero/1.0/</option>
			<option title="Creative Commons Attribution-ShareAlike (CC-BY-SA)" value='http://creativecommons.org/licenses/by-sa/3.0/'>http://creativecommons.org/licenses/by-sa/3.0/</option>
			<option title="GNU Free Documentation License (GFDL)" value='http://www.gnu.org/copyleft/fdl.html'>http://www.gnu.org/copyleft/fdl.html</option>
			<option title="Open Database License v1.0" value='http://opendatacommons.org/licenses/odbl/1.0/'>http://opendatacommons.org/licenses/odbl/1.0/</option>
			<option title="http://opendatacommons.org/licenses/by/1.0/" value='http://opendatacommons.org/licenses/by/1.0/'>http://opendatacommons.org/licenses/by/1.0/</option>
		</select>
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
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>
<div class="two columns" style="margin:0;padding:0;" >
<#include "/help.ftl" >
</div>

</form>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>