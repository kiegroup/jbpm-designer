var pathLineAbsD = "M100,10 L100,10 40,180 z";
var pathCubicBezierAbsD = "M40,140 L40,100 10,100 C10,10 90,10 90,100";
var pathQuadraticBezierAbsD = "M50,50 Q-30,100 50,150 100,230 160,170";
var pathEllipticArcAbsD = "M10,150 A15 15 180 0 1 70 140 A15 25 180 0 0 130 130";

var pathLineRelD = "M100,10 l100,10 40,180 z";
var pathCubicBezierRelD = "M40,140 l40,100 10,100 c10,10 90,10 90,100";
var pathQuadraticBezierRelD = "M50,50 q-30,100 50,150 100,230 160,170";
var pathEllipticArcRelD = "M10,150 a15 15 180 0 1 70 140 a15 25 180 0 0 130 130";

/**
 * Test ORYX.Core.SVG.PointsPathHandler for a line path with absolute coordinates
 */
function testPointsLineAbs() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.PointsPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathLineAbsD);

	assertEquals(6, handler.points.length);
	assertEquals(100, handler.points[0]);
	assertEquals(10, handler.points[1]);
	assertEquals(100, handler.points[2]);
	assertEquals(10, handler.points[3]);
	assertEquals(40, handler.points[4]);
	assertEquals(180, handler.points[5]);
}

/**
 * Test ORYX.Core.SVG.PointsPathHandler for a cubic Bezier path with absolute coordinates
 */
function testPointsCubicBezierAbs() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.PointsPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathCubicBezierAbsD);

	assertEquals(8, handler.points.length);
	assertEquals(40, handler.points[0]);
	assertEquals(140, handler.points[1]);
	assertEquals(40, handler.points[2]);
	assertEquals(100, handler.points[3]);
	assertEquals(10, handler.points[4]);
	assertEquals(100, handler.points[5]);
	assertEquals(90, handler.points[6]);
	assertEquals(100, handler.points[7]);
}

/**
 * Test ORYX.Core.SVG.PointsPathHandler for a quadratic Bezier path with absolute coordinates
 */
function testPointsQuadraticBezierAbs() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.PointsPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathQuadraticBezierAbsD);

	assertEquals(6, handler.points.length);
	assertEquals(50, handler.points[0]);
	assertEquals(50, handler.points[1]);
	assertEquals(50, handler.points[2]);
	assertEquals(150, handler.points[3]);
	assertEquals(160, handler.points[4]);
	assertEquals(170, handler.points[5]);
}

/**
 * Test ORYX.Core.SVG.PointsPathHandler for a elliptic arc path with absolute coordinates
 */
function testPointsEllipticArcAbs() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.PointsPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathEllipticArcAbsD);

	assertEquals(6, handler.points.length);
	assertEquals(10, handler.points[0]);
	assertEquals(150, handler.points[1]);
	assertEquals(70, handler.points[2]);
	assertEquals(140, handler.points[3]);
	assertEquals(130, handler.points[4]);
	assertEquals(130, handler.points[5]);
}

/**
 * Test ORYX.Core.SVG.PointsPathHandler for a line path with relative coordinates
 */
function testPointsLineRel() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.PointsPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathLineRelD);

	assertEquals(6, handler.points.length);
	assertEquals(100, handler.points[0]);
	assertEquals(10, handler.points[1]);
	assertEquals(200, handler.points[2]);
	assertEquals(20, handler.points[3]);
	assertEquals(240, handler.points[4]);
	assertEquals(200, handler.points[5]);
}

/**
 * Test ORYX.Core.SVG.PointsPathHandler for a cubic Bezier path with relative coordinates
 */
function testPointsCubicBezierRel() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.PointsPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathCubicBezierRelD);

	assertEquals(8, handler.points.length);
	assertEquals(40, handler.points[0]);
	assertEquals(140, handler.points[1]);
	assertEquals(80, handler.points[2]);
	assertEquals(240, handler.points[3]);
	assertEquals(90, handler.points[4]);
	assertEquals(340, handler.points[5]);
	assertEquals(180, handler.points[6]);
	assertEquals(440, handler.points[7]);
}

/**
 * Test ORYX.Core.SVG.PointsPathHandler for a quadratic Bezier path with relative coordinates
 */
function testPointsQuadraticBezierRel() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.PointsPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathQuadraticBezierRelD);

	assertEquals(6, handler.points.length);
	assertEquals(50, handler.points[0]);
	assertEquals(50, handler.points[1]);
	assertEquals(100, handler.points[2]);
	assertEquals(200, handler.points[3]);
	assertEquals(260, handler.points[4]);
	assertEquals(370, handler.points[5]);
}

/**
 * Test ORYX.Core.SVG.PointsPathHandler for a elliptic arc path with relative coordinates
 */
function testPointsEllipticArcRel() {
	var parser = new PathParser();
	var handler = new ORYX.Core.SVG.PointsPathHandler();
	parser.setHandler(handler);
	parser.parseData(pathEllipticArcRelD);

	assertEquals(6, handler.points.length);
	assertEquals(10, handler.points[0]);
	assertEquals(150, handler.points[1]);
	assertEquals(80, handler.points[2]);
	assertEquals(290, handler.points[3]);
	assertEquals(210, handler.points[4]);
	assertEquals(420, handler.points[5]);
}