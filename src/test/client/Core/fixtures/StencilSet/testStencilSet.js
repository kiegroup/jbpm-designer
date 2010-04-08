testStencilSetJSON = {
	"title":"Test",
	"namespace":"http://b3mn.org/stencilset/testB#",
	"description":"Simple stencil set for ... diagrams.",
	"stencils": [
		{
			"type":			"node",
			"id":			"Diagram",
			"title":		"Diagram",	
			"groups":		["Diagram"],
			"description":	"... Diagram",
			"view":			"node.diagram.svg",
			"icon":			"diagram.png",
			"mayBeRoot":		true,
			"hide":				true,
			"roles":		["all"],
			"properties": [
				{
					"id":"id",
					"type":"String",
					"title":"Name",
					"value":"test plan",
					"description":"It describes the type of the webservice.",
					"readonly":false,
					"optional":false,
					"length":"50"
				}
			]
		},
		{
			"type":			"node",
			"id":			"Service1",
			"title":		"Service1",	
			"groups":		["Services"],
			"description":	"... Service",
			"view":			"node.task.svg",
			"icon":			"node.task.png",
			"roles":		["all", "Service"],
			"properties": [
				{
					"id":"id",
					"type":"String",
					"title":"Type",
					"value":"Service1",
					"description":"It describes the type of the webservice.",
					"refToView":"acttext",
					"readonly":false,
					"optional":false,
					"length":"50"
				}
			]
		},
		{
			"type":			"node",
			"id":			"Service2",
			"title":		"Service2",	
			"groups":		["Services"],
			"description":	"... Service",
			"view":			"node.task.svg",
			"icon":			"node.task.png",
			"roles":		["all", "Service"],
			"properties": [
				{
					"id":"id",
					"type":"String",
					"title":"Type",
					"value":"Service2",
					"description":"It describes the type of the webservice.",
					"refToView":"acttext",
					"readonly":false,
					"optional":false,
					"length":"50"
				},
				{
					"id":"attrA",
					"type":"String",
					"title":"AttributeA",
					"value":"attrA",
					"description":"It describes the type of the webservice.",
					"readonly":false,
					"optional":false,
					"length":"50"
				},
				{
					"id":"attrB",
					"type":"String",
					"title":"AttributeB",
					"value":"attrB",
					"description":"It describes the type of the webservice.",
					"readonly":false,
					"optional":false,
					"length":"50"
				}
			]
		},
		{
			"type":			"node",
			"id":			"Service3",
			"title":		"Service3",	
			"groups":		["Services"],
			"description":	"... Service",
			"view":			"node.task.svg",
			"icon":			"node.task.png",
			"roles":		["all", "Service"],
			"properties": [
				{
					"id":"id",
					"type":"String",
					"title":"Type",
					"value":"Service3",
					"description":"It describes the type of the webservice.",
					"refToView":"acttext",
					"readonly":false,
					"optional":false,
					"length":"50"
				},
				{
					"id":"attrA",
					"type":"String",
					"title":"AttributeA",
					"value":"attrA",
					"description":"It describes the type of the webservice.",
					"readonly":false,
					"optional":false,
					"length":"50"
				},
				{
					"id":"attrB",
					"type":"String",
					"title":"AttributeB",
					"value":"attrB",
					"description":"It describes the type of the webservice.",
					"readonly":false,
					"optional":false,
					"length":"50"
				}
			]
		},
		{
			"type":			"edge",
			"id":			"SequenceFlow",
			"title":		"SequenceFlow",	
			"groups":		["Connection"],
			"description":	"... SequenceFlow",
			"view":			"edge.sequenceflow.svg",
			"icon":			"edge.sequenceflow.png",
			"roles":		["all"],
			"properties": [
				{
					"id":"id",
					"type":"String",
					"title":"Type",
					"value":"TypeC",
					"description":"It describes the type of the webservice.",
					"readonly":false,
					"optional":false,
					"length":"50"
				},
				{
					"id":"attrB",
					"type":"String",
					"title":"AttributeA",
					"value":"attrA",
					"description":"It describes the type of the webservice.",
					"readonly":false,
					"optional":false,
					"length":"50"
				},
				{
					"id":"attrB",
					"type":"String",
					"title":"AttributeB",
					"value":"attrB",
					"description":"It describes the type of the webservice.",
					"readonly":false,
					"optional":false,
					"length":"50"
				}
			]
		}
	],
	"rules":{
		"containmentRules":	[
			{
				"role":		"Diagram",
				"contains": [
							"all"
				]
			}
		],
		"connectionRules": [
			{
				"role":"SequenceFlow",
				"connects": [
					{
						"from":"Service1",
						"to":["Service"]
					},
					{
						"from":"Service2",
						"to":["Service3"]
					},
					{
						"from":"Service3",
						"to":["Service1", "Service2"]
					}
				]
			}
		]
	}
}