
<ul>

<li class="ui-selectee">
	<a href="${ambit_root}/ui/query?option=similarity&amp;type=url&amp;pagesize=10&amp;threshold=0.75&amp;search=${ambit_request?url('UTF-8')}">
	<span class="ui-icon ui-icon-heart" style="float: left; margin-right: .3em;"></span>Find similar</a>
</li> 

<li class="ui-selectee">
	<a href="${ambit_root}/ui/query?option=smarts&amp;type=url&amp;pagesize=10&amp;threshold=0.75&amp;search=${ambit_request?url('UTF-8')}">
	<span class="ui-icon ui-icon-search" style="float: left; margin-right: .3em;"></span>Find substructure</a>
</li> 

<li class="ui-selectee">
	<a href="${ambit_root}/compound/${cmpid}/conformer" title='All available structures for this chemical compound'>
	<span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>All structures</a>
</li> 

<li class="ui-selectee" >
	<span class="ui-icon ui-icon-disk" style="float: left; margin-right: .3em;" ></span>
	Download
</li>

<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
</div>

<li class="ui-selectee" >
	<span class="ui-icon ui-icon-star" style="float: left; margin-right: .3em;" ></span>
	Structure QA
</li>

<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<div id='structype_${cmpid}' title='Comparison label'></div>
</div>
<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<div id='consensus_${cmpid}' title='Consensus label'></div>
</div>
</ul>

<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'>		
</div>
<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'>		
</div>	