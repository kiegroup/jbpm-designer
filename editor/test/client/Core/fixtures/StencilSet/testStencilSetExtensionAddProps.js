var testStencilSetExtensionAddPropJSON = {
	"title":"TestStencilSetExtention",
	"namespace":"http://testStencilSetExtension#",
	"description":"Test extension",
	"extends":"http://b3mn.org/stencilset/testB#",
	"stencils":[],
	"properties":[
		{
			"roles": [
				"Service1",
				"Service2"
			],
			"properties": [
				{
					"id":"testExtensionProp",
					"type":"Integer",
					"title":"extensionTestProp",
					"description":"test test"
				}
			]
		}
	],
	"rules": {
		"connectionRules": [],
        "cardinalityRules": [],
		"containmentRules": []
	},
	"removestencils": [
		
	],
	"removeproperties": [
	]
}
