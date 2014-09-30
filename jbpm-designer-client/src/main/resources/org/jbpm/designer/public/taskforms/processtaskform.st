$processtaskformjavascript()$
$processtaskformstyle()$
<div id="container">
	<div id="header">
		New Process Instance: $tfi.pkgName$.$tfi.processName$
	</div>
	<div id="content">
	    <input type="hidden" name="processId" value="$dollar$$bopen$process.id$bclose$"/>
		<fieldset>
            <legend>Process inputs</legend>
              $tfi.taskInputs:{ input |
              	$if(input.booleanRefType)$
              		<label for="name">$input.name$</label>
              		<div class="div_checkbox">
                		<input name="$input.name$" type="checkbox" class="checkbox" id="$input.name$" value="true" />
              		</div>
              	$else$
              		<label for="name">$input.name$</label>
              		<div class="div_texbox">
                		<input name="$input.name$" type="text" class="textbox" id="$input.name$" value="" />
              		</div>
              	$endif$	
              }$
          </fieldset>
	</div>
	<div id="footer">
	</div>
</div>