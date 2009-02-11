
var stencilSet1 = new ORYX.Core.StencilSet.StencilSet("../../data/stencilsets/bpmn1.1/bpmn1.1.json");
var stencilSetInvalid = new ORYX.Core.StencilSet.StencilSet("../../data/stencilsets/inval/invalid.json");

function setUpPage() {
	setTimeout("setUpPageStatus = 'complete'", 6000);
}

function testConstruct() {
	alert(stencilSetInvalid);
	assert(stencilSet1.errornous!=true);
}