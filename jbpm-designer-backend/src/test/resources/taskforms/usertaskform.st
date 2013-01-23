<html>
<head>
<meta charset="utf-8">
</head>
<body>
$usertaskformjavascript()$
$usertaskformstyle()$
<div id="container">
	<div id="header">
		User Task Form: $tfi.processName$.$tfi.taskName$
	</div>
	<div id="content">
		<fieldset>
            <legend>Task Info</legend>
            	<label for="name">Owners</label>
            	<div class="div_checkbox">
					$tfi.taskOwners:{ owner | 
						$owner$
              		};separator=", "$            	
            	</div>
            	<label for="name">Actor ID</label>
            	<div class="div_checkbox">$tfi.actorId$</div>
            	<label for="name">Group</label>
            	<div class="div_checkbox">$tfi.groupId$</div>
            	<label for="name">Skippable</label>
            	<div class="div_checkbox">$tfi.skippable$</div>
            	<label for="name">Priority</label>
            	<div class="div_checkbox">$tfi.priority$</div>
            	<label for="name">Comment</label>
            	<div class="div_checkbox">$tfi.comment$</div>
            <div class="clear"></div>
          </fieldset>
	
		<fieldset>
            <legend>Task Inputs</legend>
              $tfi.taskInputs:{ input | 
              		<label for="name">$input.name$</label>
              		<div class="div_checkbox">
                		$input.value$
              		</div>
              }$
            <div class="clear"></div>
          </fieldset>
          
          <fieldset>
            <legend>Task Outputs</legend>
            <form action="complete" method="POST" enctype="multipart/form-data" onsubmit="return taskFormValidator()">
              $tfi.taskOutputs:{ output | 
              	$if(output.booleanRefType)$
              		<label for="name">$output.name$</label>
              		<div class="div_checkbox">
                		<input name="$output.name$" type="checkbox" class="checkbox" id="$output.name$" value="$output.value$" />
              		</div>
              	$else$
              		<label for="name">$output.name$</label>
              		<div class="div_texbox">
                		<input name="$output.name$" type="text" class="textbox" id="$output.name$" value="$output.value$" />
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