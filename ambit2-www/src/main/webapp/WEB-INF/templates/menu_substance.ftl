
<ul id="selectable">
<li class="ui-selectee">
<a href="${ambit_root}/ui"><span class="ui-icon ui-icon-home" style="float: left; margin-right: .3em;"></span>Home</a>
</li>

<li class="ui-selectee">
<a href="${ambit_root}/ui/query"><span class="ui-icon ui-icon-search" style="float: left; margin-right: .3em;"></span>Structure search</a>
</li>

<li class="ui-selectee">
	<a href="${ambit_root}/substance?page=0&amp;pagesize=100">
	<span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>All substances</a>
</li> 

<li class="ui-selectee"><span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>
<a href="${ambit_root}/ui/uploadsubstance">Import substance</a>
</li>

<#if openam_token??>

<#if username??>
<li class="ui-selectee">
<a href="${ambit_root}/bookmark/${username}"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>My workspace</a>
</li>
</#if>
<li class="ui-selectee">
<a href="${ambit_root}/admin"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>Admin</a>
</li>
<li class="ui-selectee">
<a href="${ambit_root}/admin/policy"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>View/Define access rights</a>
</li>
</#if>

</ul>