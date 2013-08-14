<script type='text/javascript'>
	function notEmpty(elem)$bopen$
		if(elem.value.length == 0)$bopen$
			return false;
		$bclose$
		return true;
	$bclose$
	
	function isNumeric(elem)$bopen$
		var numericExpression = /^[0-9]+\$/;
		if(elem.value.match(numericExpression))$bopen$
			return true;
		$bclose$ else $bopen$
			return false;
		$bclose$
	$bclose$
	
	function isAlphabet(elem)$bopen$
        var alphaExp = /^[a-zA-Z0-9\u00A1-\uFFFF\_ .-@]+\$/;
        if(elem.value.match(alphaExp))$bopen$
            return true;
        $bclose$ else $bopen$
            return false;
        $bclose$
    $bclose$

    function isAlphanumeric(elem)$bopen$
        var alphaExp = /^[a-zA-Z0-9\u00A1-\uFFFF\_ .-@]+\$/;
        if(elem.value.match(alphaExp) && !isNumeric(elem))$bopen$
            return true;
        $bclose$ else $bopen$
            return false;
        $bclose$
    $bclose$
	
	function isFloat(elem)$bopen$
   		if(elem.value.indexOf(".") < 0)$bopen$
     		return false;
   		$bclose$ else $bopen$
      		if(parseFloat(elem.value)) $bopen$
              return true;
          	$bclose$ else $bopen$
              return false;
          	$bclose$
   		$bclose$
	$bclose$
	
	function taskFormValidator() $bopen$
		var i=0;
		var myInputs = new Array();
		$tfi.taskOutputs:{ output |
			myInputs[i] = document.getElementById("$output.name$");
			i++;
		}$
		
		var j=0;
		$tfi.taskOutputs:{ output |
			$if(output.integerRefType)$
				if(notEmpty(myInputs[j]) && !isNumeric(myInputs[j])) $bopen$
					alert("Please enter valid $output.name$");
					myInputs[j].focus();
					return false;
				$bclose$
			$elseif(output.floatRefType)$
				if(notEmpty(myInputs[j]) && !isFloat(myInputs[j])) $bopen$
					alert("Please enter valid $output.name$");
					myInputs[j].focus();
					return false;
				$bclose$
			$else$
				if(notEmpty(myInputs[j]) && !isAlphanumeric(myInputs[j])) $bopen$
					alert("Please enter valid $output.name$");
					myInputs[j].focus();
					return false;
				$bclose$
			$endif$	
			j++;
		}$
		return true;
	$bclose$
</script>