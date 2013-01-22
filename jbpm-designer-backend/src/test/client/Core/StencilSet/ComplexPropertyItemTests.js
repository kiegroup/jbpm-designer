// Mock ORYX.CONFIG
ORYX.CONFIG = {};
ORYX.CONFIG.TYPE_CHOICE = "choice";
ORYX.CONFIG.TYPE_COMPLEX = "complex";

// Mock property item objects
var complexItem1 = {
    				id:"id1",
    				name:"n1",
    				type:"String",
    				value:"v1",
    				width:100
    			  },
    complexItem2 = {
    				id:"id2",
    				name:"n2",
    				type:"Choice",
    				value:"v2",
    				width:200,
    				optional:true,
    				items: [
    					{
    						id:"c1",
    						title:"c1",
    						value:"vc1"
    					},
    					{
    						id:"c2",
    						title:"c2",
    						value:"vc2"
    					}
    				]
    			  },
    simpleItem = {
					id:"id1",
					title:"t1",
					value:"v1"
				};
    			
var propComplItem1, propComplItem2, propComplItem3, propComplItem1Dupl, propSimplItem;

// Mock property object
var prop1 = {
		id: "id1",
		equals: function(p) {return p==prop1}
	},
	prop2 = {
		id: "id2",
		equals: function(p) {return p==prop2}
	};

//Stub for StencilSet.getTranslation
ORYX.Core.StencilSet.getTranslation = function(jsonProp, prop) {
	return jsonProp[prop];
}

function setUp() {
	propComplItem1 = new ORYX.Core.StencilSet.ComplexPropertyItem(complexItem1, "testNS", prop1);
	propComplItem2 = new ORYX.Core.StencilSet.ComplexPropertyItem(complexItem2, "testNS", prop1);
	propComplItem3 = new ORYX.Core.StencilSet.ComplexPropertyItem(complexItem1, "testNS", prop2);
	propComplItem1Dupl = new ORYX.Core.StencilSet.ComplexPropertyItem(complexItem1, "testNS", prop1);
	
	propSimplItem = new ORYX.Core.StencilSet.PropertyItem(simpleItem, "testNS", prop1);
}

/*
 * Test constructor function
 */
function testConstruct() {
	assertEquals("id1", propComplItem1.id());
	assertEquals("string", propComplItem1.type());
	assertEquals(100, propComplItem1.width());
	assert(propComplItem1.optional()==false);
	assertEquals("v1", propComplItem1.value());
	assertEquals("n1", propComplItem1.name());
	assertEquals("id1", propComplItem1.property().id);
	assertEquals("testNS", propComplItem1.namespace());
	
	assertEquals("id2", propComplItem2.id());
	assertEquals("choice", propComplItem2.type());
	assertEquals(200, propComplItem2.width());
	assertTrue(propComplItem2.optional());
	assertEquals("v2", propComplItem2.value());
	assertEquals("n2", propComplItem2.name());
	assertEquals("id1", propComplItem2.property().id);
	assertEquals("testNS", propComplItem2.namespace());
	assertEquals(2, propComplItem2.items().length);
	assertEquals("vc1", propComplItem2.items()[0]);
	assertEquals("vc2", propComplItem2.items()[2]);
}

/*
 * Test equals function
 */
function testEquals() {
	assertTrue(propComplItem1.equals(propComplItem1));
	assertTrue(propComplItem1.equals(propComplItem1Dupl));
	assertFalse(propComplItem1.equals(propComplItem2));
	assertFalse(propComplItem1.equals(propComplItem3));
}

function tearDown() {
	delete propComplItem1;
	delete propComplItem2;
	delete propComplItem3;
	delete propComplItem1Dupl;
}
