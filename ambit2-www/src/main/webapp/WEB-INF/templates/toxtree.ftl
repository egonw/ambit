<#include "/html.ftl" >
<head>
<#include "/header_updated_noambitcss.ftl" >

<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>
<link rel="stylesheet" href="${ambit_root}/style/ketcher.css"/>
<link rel="stylesheet" href='${ambit_root}/scripts/toxtree/ui-toxtree.css'/>

<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.3.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/oecdcategories.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/ketcher.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/toxtree/ui-toxtree.js'></script>
  


<script>
	$(document).ready(function() {
		try {
			var url = parseURL(document.location);
			$("#query-needle").attr('value',url.params.search);
		} catch (err) { $("#query-needle").attr('value','');}
		
		jQuery("#breadCrumb ul").append('<li><a href="#" title="Demo">Demo</a></li>');
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/toxtree" title="Toxtree - Toxic Hazard Estimation by decision tree approach">Toxtree - Toxic Hazard Estimation by decision tree approach</a></li>');
		loadHelp("${ambit_root}","toxtree");

	});

</script>
</head>
<body>

<!-- any of these break the layout ...

<div class="container" style="margin:0;padding:0;">
 include "/banner_crumbs.ftl" 
<div class="sixteen columns remove-bottom" style="padding:0;" >
	
-->

<!-- toxtree starts -->
 <div class="jtox-toolkit" data-kit="query" data-cross-domain="true" data-configuration="config_toxtree" data-base-url="http://apps.ideaconsult.net:8080/biodeg">
    <div id="tt-searchbar" class="jtox-toolkit jtox-widget cc-fixed" data-kit="search"></div>
    <div id="tt-bigpane" class="cc-flex">
      <div>
        <div id="tt-browser-panel">
    			<div id="tt-features" class="cc-flex">
      			<div class="jtox-inline cc-fixed"><h5 class="counter-field">Available structure attributes </h5></div>
      			<div class="jtox-inline tt-controls"><a class="paginate_disabled_previous prev-field" tabindex="0" role="button">Previous</a><a class="paginate_disabled_next next-field" tabindex="0" role="button">Next</a></div>
      			<div class="list jtox-panel cc-flex"></div>
      		</div>
    			<div id="tt-diagram" class="jtox-foldable cc-fixed">
    			  <h5 class="title">Structure diagram</h5>
    			  <div class="content">
              <img class="toxtree-diagram" alt="Compoung diagram"/>
    			  </div>
          </div>
        </div>
        <div id="tt-models-panel">
          <div class="cc-fixed">
            <div class="title jtox-inline"><h5>Toxicity prediction modules (0/0)</h5></div>
            <div class="jtox-inline selections"><a href="#" class="select-unselect" title="(Un)select all algorithms" data-other="unselect">select</a>&nbsp;<a href="#" class="expand-collapse" title="Expand/collapse all algorithm panes" data-other="collapse">expand</a>&nbsp;<a href="#" class="run-selected" title="Run predictions for all selected algorithms">run</a>&nbsp;<a href="#" class="show-hide" data-other="show" title="Hide/show unselected algorithms">hide</a></div>
          </div>
          <div class="jtox-toolkit jtox-widget cc-flex" data-kit="model" data-algorithms="true" data-no-interface="true" data-on-loaded="onAlgoLoaded" data-algorithm-needle="ToxTree"></div>
        </div>
        <div id="tt-table" class="jtox-toolkit jtox-panel" data-kit="compound" data-configuration="config_toxtree" data-manual-init="true" data-pre-details="onTableDetails" data-show-tabs="false" data-on-loaded="onDataLoaded" data-selection-handler="checked"></div>
      </div>
    </div>
    <div id="sidebar">
      <div class="side-title">
        <div data-mode="table">Table view mode</div>
        <div data-mode="single">Single view mode</div>
      </div>
    </div>
  </div>
  
  <div class="jtox-template">
    <div id="tt-feature" class="tt-feature">
      <div class="data-field tt-name" data-field="title"></div><div class="data-field tt-value" data-field="value"></div>
    </div>
    <div id="tt-class" class="tt-class">
      <span class="data-field" data-field="title"></span><span class="ui-icon ui-icon-check"></span>
    </div>
    <div id="tt-algorithm" class="tt-algorithm jtox-foldable folded">
      <div class="title" >
        <a target="_blank" class="data-field attribute" data-field="uri" data-attribute="href"><span class="ui-icon ui-icon-link jtox-inline"></span></a>
        <span class="data-field" data-field="name" data-format="formatAlgoName">?</span>
        <div class="jtox-inline float-right">
          <button class="tt-toggle jtox-handler predict" data-handler="runPredict" title="Run prediction with the algorithm on current compound">R</button>
          <button class="tt-toggle jtox-handler model" data-handler="makeModel" title="Prepare the model for this algorithm">M</button>
          <button class="tt-toggle jtox-handler auto" data-handler="markAuto" title="Run automatically on new queries">A</button>
        </div>
        <div class="tt-classification">
        </div>
      </div>
      <div class="content">
        <div class="tt-explanation"></div>
      </div>
    </div>
  </div>


<!-- toxtree ends -->

<!-- uncomment when layout breakup is solved ..
</div>
<#include "/footer.ftl" >
</div>
-->

</body>


</html>
