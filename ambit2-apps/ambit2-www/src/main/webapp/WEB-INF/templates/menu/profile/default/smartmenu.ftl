<ul id='smartmenu' class="sm sm-mint">
<li>
	<a href="${ambit_root}/ui/_search" title="Chemical structure search">Search</a>
	<ul>
		<li ><a href="${ambit_root}/substance?page=0&pagesize=100" title="search for  substances with identifiers (fragment)">Search substances</a></li>
		<li><a href="${ambit_root}/query/study" title="Search substances by experimental data">Search substances by endpoint data</a></li>
		<li><a href="${ambit_root}/ui/_search" title="Chemical structure search">Search structures and associated data</a></li>		
	</ul>
</li>
<li>
	<a href="${ambit_root}/bundle">Assessments and export</a>
	<#include "/menu/profile/default/assessment_menu.ftl">
</li>	


<li>
<a href="#">Enhanced functions</a>
<ul>
<li >
	<a href="${ambit_root}/ui/toxtree" title="Toxtree web release">Toxtree predictions</a>
</li>

<#if username??>
<#if openam_token??>

	<li>
		<a href="${ambit_root}/algorithm" title="Descriptor calculations, model building and data processing algorithms">Algorithms</a>
		<#include "/menu/profile/default/algorithm_menu.ftl">
	</li>
	<li>
		<a href="${ambit_root}/model" title="Regression, classification, clustering, structural alerts, applicability domain, structure optimisation.">Models</a>
		<#include "/menu/profile/default/model_menu.ftl">
	</li>
	<li><a href="${ambit_root}/dataset?page=0&pagesize=100" title="Chemical structures and calculated properties">Chemical structures</a></li>	
	</ul>
	</li>

	
	<li>
		<a href="${ambit_root}/admin">Admin</a>
		<#include "/menu/profile/default/admin_menu.ftl">
	</li>	
<#else>

		<#if ambit_admin?? && ambit_admin>
			<li>
			<a href="${ambit_root}/algorithm" title="Descriptor calculations, model building and data processing algorithms">Algorithms</a>
			<#include "/menu/profile/default/algorithm_menu.ftl">
			</li>
			<li>
			<a href="${ambit_root}/model" title="Regression, classification, clustering, structural alerts, applicability domain, structure optimisation.">Models</a>
			<#include "/menu/profile/default/model_menu.ftl">
			</li>
			<li><a href="${ambit_root}/dataset?page=0&pagesize=100" title="Chemical structures and calculated properties">Chemical structures</a></li>
			</ul>		
			</li>
			<li>
				<a href="${ambit_root}/admin">Admin</a>
				<#include "/menu/profile/default/admin_menu.ftl">
			</li>
		<#else>	
			<#if (ambit_modeller?? && ambit_modeller)>
			<li>
				<a href="${ambit_root}/algorithm" title="Descriptor calculations, model building and data processing algorithms">Algorithms</a>
				<#include "/menu/profile/default/algorithm_menu.ftl">
			</li>
			<li>
				<a href="${ambit_root}/model" title="Regression, classification, clustering, structural alerts, applicability domain, structure optimisation.">Models</a>
				<#include "/menu/profile/default/model_menu.ftl">
			</li>
			</#if>
			
			<#if (ambit_datasetmgr?? && ambit_datasetmgr) >		
				<li><a href="${ambit_root}/dataset?page=0&pagesize=100" title="Chemical structures and calculated properties">Chemical structures</a></li>	
			</#if>
			</ul>		
			</li>		
		</#if>		
</#if>
</#if>

		
<li>
	<a href="${ambit_root}/help">Help</a>
	<#include "/menu/profile/default/help_menu.ftl">
</li>		


</ul>


