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
        var isChrome = /Chrome/.test(navigator.userAgent) && /Google Inc/.test(navigator.vendor);
        if(isChrome) {
            var iDefs = pathSVG.indexOf('<defs>');
            var iWidth = pathSVG.indexOf('width="50"');
            if (iWidth > 0 && iWidth < iDefs) {
                pathSVG = pathSVG.replace('width="50"', 'width="' + parent.parent.ORYX.CONFIG.MAXIMUM_SIZE + '"');
            }
            var iHeight = pathSVG.indexOf('height="50"');
            if (iHeight > 0 && iHeight < iDefs) {
                pathSVG = pathSVG.replace('height="50"', 'height="' + parent.parent.ORYX.CONFIG.MAXIMUM_SIZE + '"');
            }
        }
        document.getElementById('pathimagediv').innerHTML = pathSVG;
    }
</script>
</body>
</html>