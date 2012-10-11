<html>
<head>
<meta charset="utf-8">
</head>
<body>
$processtaskformjavascript()$
$processtaskformstyle()$
<div id="container">
	<div id="header">
		New Process Instance: $tfi.pkgName$.$tfi.processName$
	</div>
	<div id="content">
		<fieldset>
            <legend>Process inputs</legend>
            <form action="complete" method="POST" enctype="multipart/form-data" onsubmit="return taskFormValidator()">
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
              <div class="button_div">
                <input name="Submit" type="submit" value="Submit" class="buttons" />
              </div>
            </form>
            <div class="clear"></div>
          </fieldset>
	</div>
	<div id="footer">
	</div>
</div>
</body>
</html>