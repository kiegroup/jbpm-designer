$genericstyle()$
<div id="container">
	<div id="header">
		Form Generation Results for Process $manager.packageName$.$manager.assetName$
	</div>
	<div id="content">
          <fieldset>
            <legend>Generated Templates</legend>
              $manager.taskFormInformationList:{ info | 
            	<label for="name">$info.id$</label>
            	<div class="div_texbox">
            		<a href="$profile$://$host$/$subdomain$/rest/packages/$info.pkgName$/assets/$info.id$/source" target="_self">View Source</a>
              	</div>
              }$
              <div class="button_div">
              <form method="post">
			  	<input name="Close" type="button" value="Close" onclick="window.close()" class="buttons" />
			  </form>
              </div>
            <div class="clear"></div>
          </fieldset>
	</div>
	<div id="footer">
	</div>
</div>