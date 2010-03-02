// Mock ORYX.CONFIG
ORYX.CONFIG = {};
ORYX.CONFIG.TYPE_CHOICE = "choice";
ORYX.CONFIG.TYPE_COMPLEX = "complex";

// Mock Stencil object
var stencil = {};


// Mock JSON property objects

var jsonPropString = {
	id: "testId",
	type: "String",
	prefix: "oryx",
	title: "testTitle",
	value: "testValue",
	description: "testDescription",
	readonly: false,
	optional: true,
	refToView: "testRef",
	length: 100,
	wrapLines: true
}

var jsonPropInt = {
	id: "testId2",
	type: "Integer",
	prefix: "oryx",
	title: "testTitle2",
	value: 11,
	description: "testDescription2",
	readonly: false,
	optional: false,
	min: 10,
	max: 100
}

var jsonPropChoice = {
	id: "testId3",
	type: "Choice",
	prefix: "oryx",
	title: "testTitle3",
	value: 0,
	description: "testDescription3",
	readonly: true,
	optional: true,
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
}

var jsonPropComplex = {
	id: "testId4",
	type: "Complex",
	prefix: "oryx",
	title: "testTitle4",
	value: "",
	description: "testDescription4",
	complexItems: [
			{
				id:"id1",
				name:"n1",
				type:"String",
				value:"v1",
				width:100,
				optional:false 
			},
			{
				id:"id2",
				name:"n2",
				type:"Choice",
				value:"v2",
				width:100,
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
			}
		]
}

//Stub for StencilSet.getTranslation
ORYX.Core.StencilSet.getTranslation = function(jsonProp, prop) {
	return jsonProp[prop];
}

/*
 * Test constructor function for property with type string 
 */
function testConstructString() {
	var prop1 = new ORYX.Core.StencilSet.Property(jsonPropString, "testNS", stencil);
	assertEquals("testid", prop1.id());
	assertEquals("string", prop1.type());
	assertEquals("oryx", prop1.prefix());
	assertEquals("testTitle", prop1.title());
	assertEquals("testValue", prop1.value());
	assertEquals("testDescription", prop1.description());
	assertEquals(false, prop1.readonly());
	assertEquals(true, prop1.optional());
	assertEquals("testRef", prop1.refToView()[0]);
	assertEquals(100, prop1.length());
	assertEquals(true, prop1.wrapLines());
}

/*
 * Test constructor function for property with type integer 
 */
function testConstructInteger() {
	var prop1 = new ORYX.Core.StencilSet.Property(jsonPropInt, "testNS", stencil);
	assertEquals("testid2", prop1.id());
	assertEquals("integer", prop1.type());
	assertEquals("oryx", prop1.prefix());
	assertEquals("testTitle2", prop1.title());
	assertEquals(11, prop1.value());
	assertEquals("testDescription2", prop1.description());
	assertEquals(false, prop1.readonly());
	assertEquals(false, prop1.optional());
	assertEquals(10, prop1.min());
	assertEquals(100, prop1.max());
}

/*
 * Test constructor function for property with type choice 
 */
function testConstructChoice() {
	var prop1 = new ORYX.Core.StencilSet.Property(jsonPropChoice, "testNS", stencil);
	assertEquals(true, prop1.readonly());
	assertEquals(true, prop1.optional());
	assertEquals("vc1", prop1.item("vc1").value());
	assertEquals("vc2", prop1.item("vc2").value());
}

/*
 * Test constructor function for property with type complex 
 */
function testConstructComplex() {
	var prop1 = new ORYX.Core.StencilSet.Property(jsonPropComplex, "testNS", stencil);
	assertEquals(false, prop1.readonly());
	assertEquals(true, prop1.optional());
	
	assertEquals("v1", prop1.complexItem("id1").value());
	assertEquals("n1", prop1.complexItem("id1").name());
	assertEquals("string", prop1.complexItem("id1").type());
	assertEquals(100, prop1.complexItem("id1").width());
	assertEquals(false, prop1.complexItem("id1").optional());
	
	assertEquals("v2", prop1.complexItem("id2").value());
	assertEquals("n2", prop1.complexItem("id2").name());
	assertEquals("choice", prop1.complexItem("id2").type());
	assertEquals("vc1", prop1.complexItem("id2").items()[0].value());
	assertEquals("vc2", prop1.complexItem("id2").items()[1].value());
}


