var p1 = {x: 0, y: 0};
var p2 = {x: 4, y: 3.5};
var p3 = {x: 2, y: 1.75};

function testMidPoint() {
	assertEquals(ORYX.Core.Math.midPoint(p1, p2).x, 2);
	assertEquals(ORYX.Core.Math.midPoint(p1, p2).y, 1.75);
}

function testIsPointInLine() {
	assertTrue(ORYX.Core.Math.isPointInLine(p3.x, p3.y, p1.x, p1.y, p2.x, p2.y));
	assertFalse(ORYX.Core.Math.isPointInLine(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y));
	assertTrue(ORYX.Core.Math.isPointInLine(p3.x+1, p3.y+1, p1.x, p1.y, p2.x, p2.y, 2));
	assertTrue(ORYX.Core.Math.isPointInLine(p3.x+1, p3.y+1, p1.x, p1.y, p2.x, p2.y, -2));
	assertFalse(ORYX.Core.Math.isPointInLine(p3.x+1, p3.y+1, p1.x, p1.y, p2.x + 2, p2.y, 1));
}

function testIsPointInEllipse() {
	assertTrue(ORYX.Core.Math.isPointInEllipse(p2.x, p2.y, 5, 4, 2, 1));
	assertFalse(ORYX.Core.Math.isPointInEllipse(p3.x, p3.y, 5, 4, 2, 1));
}

function testIsPointInPolygone() {
	var polygone = [ 1,2, 4,2.5, 3,3, 6,9, 1,8 ];
	assertTrue(ORYX.Core.Math.isPointInPolygone(2, 2.4, polygone));
	assertTrue(ORYX.Core.Math.isPointInPolygone(5, 8, polygone));
	assertFalse(ORYX.Core.Math.isPointInPolygone(4, 3, polygone));
	assertFalse(ORYX.Core.Math.isPointInPolygone(1, 9, polygone));
}