// Mock property item objects
var item1 = {
		id:"c1",
		title:"c1",
		value:"vc1"
	},
	item2 = {
		id:"c2",
		title:"c2",
		value:"vc2",
		refToView: "refToView2"
	};

var propItem1, propItem2, propItem3, propItem1Dupl;

// Mock property object
var prop1 = {
		id: "id1",
		equals: function(p) {return p==prop1}
	},
	prop2 = {
		id: "id2",
		equals: function(p) {return p==prop2}
	};


function setUp() {
	propItem1 = new ORYX.Core.StencilSet.PropertyItem(item1, "testNS", prop1);
	propItem2 = new ORYX.Core.StencilSet.PropertyItem(item2, "testNS", prop1);
	propItem3 = new ORYX.Core.StencilSet.PropertyItem(item1, "testNS", prop2);
	propItem1Dupl = new ORYX.Core.StencilSet.PropertyItem(item1, "testNS", prop1);
}

/*
 * Test constructor function
 */
function testConstruct() {
	assertEquals("vc1", propItem1.value());
	assertEquals("id1", propItem1.property().id);
	assertEquals("testNS", propItem1.namespace());
	
	assertEquals("vc2", propItem2.value());
	assertEquals("id1", propItem2.property().id);
	assertEquals("testNS", propItem2.namespace());
	assertEquals("refToView2", propItem2.refToView()[0]);
	assertEquals(1, propItem2.refToView().length)
	
	assertEquals("id2", propItem3.property().id);
}

/*
 * Test equals function
 */
function testEquals() {
	assertTrue(propItem1.equals(propItem1));
	assertTrue(propItem1.equals(propItem1Dupl));
	assertFalse(propItem1.equals(propItem2));
	assertFalse(propItem1.equals(propItem3));
}

function tearDown() {
	delete propItem1;
	delete propItem2;
	delete propItem3;
	delete propItem1Dupl;
}
