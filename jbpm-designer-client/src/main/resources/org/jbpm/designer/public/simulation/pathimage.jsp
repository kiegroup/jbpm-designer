<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
</head>
<body onload="showPathImage();">
<div id="pathimagediv"></div>
<script>
    function showPathImage() {
        var pathSVG = parent.getPathSVG();
        document.getElementById('pathimagediv').innerHTML = pathSVG;
    }
</script>
</body>
</html>