<#include "/html.ftl">
<head>
  <#include "/header_updated.ftl">
  <script type='text/javascript'>

$(document)
		.ready(
				function() {
						jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/login" title="OpenTox log in">Log in</a></li>');
					    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/login" title="OpenTox log in">via OpenAM identity service</a></li>');
	   					jQuery("#breadCrumb").jBreadCrumb();
	   					jQuery("#welcome").text("OpenTox log in");
    					loadHelp("${ambit_root}","opensso");
				});
</script>
</head>
<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
&nbsp;
</div>

		
		<!-- Page Content
		================================================== -->
		<div class="eleven columns remove-bottom" style="padding:0;" >
		
	    <div class="row remove-bottom ui-widget-header ui-corner-top">
	    <#if openam_token??>
	    &nbsp;Welcome to OpenTox
	    <#else>
	    &nbsp;Log in to OpenTox
	    </#if>
	    </div>
    	<div class="ui-widget-content ui-corner-bottom">
		<div style='margin:5px;padding:5px;'>
		<form method='post' action='${ambit_root}/openssouser?targetUri=${ambit_root}/login' autocomplete='off'>
		
		
		
		<div class="row">		
		<label class='three columns alpha'>OpenAM service</label> 
		<div class='five columns omega'>
			${openam_service}
		</div>
		<div class='three columns omega'>&nbsp;</div>
		<div class='five columns omega'>
			<a title='Register at OpenTox site' target=_blank href='http://opentox.org/join_form' class='h5 qxternal' >Create an OpenTox account</a>
		</div>
		</div>
		
		<div class="row remove-bottom">		
		<label class='three columns alpha'>OpenTox User name</label> 
		<#if username??>
		<div class='five columns omega add-bottom'>${username}</div>
		<#else>
		<input class='five columns omega' type='text' size='40' name='user' value=''>
		</#if>
		
		</div>
		
		<#if openam_token??>
		<div class="row">		
		<label class='three columns alpha'>OpenAM token</label> 
		<div class='thirteen columns omega'>
			${openam_token}
		</div>	
		</div>
		</#if>
		
		<#if username??>
		<#else>
		<div class="row remove-bottom">		
		<label class='three columns alpha'>Password</label> 
		<input class='five columns omega' type='password' size='40' name='password' value=''>
		<!--
		<div class='eight columns omega'><a href="${ambit_root}/forgotten">Forgotten password?</a></div>
		-->
		</div>
		</#if>

		<#if username??>
		<#else>
		<div class="row half-bottom">		
		<label class='three columns alpha'>&nbsp;</label>		
		<div class='eight columns omega'>
		<input type=CHECKBOX name='subjectid_secure' CHECKED>
		Use secure cookie for the OpenSSO token
		</div>
		</div>
		
		<div class="row half-bottom">		
		<label class='five columns alpha'>&nbsp;</label>		
		<input class='three columns omega'  type="submit" value="Log in">
		<input type='hidden' size='40' name='targetURI' value='${ambit_root}/openssouser'>
		</div>
		
		</#if>
		
		</form>
		</div>
		</div>		
		</div>
		

		<!-- Right column and footer
		================================================== -->

<#include "/chelp.ftl" >

<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>