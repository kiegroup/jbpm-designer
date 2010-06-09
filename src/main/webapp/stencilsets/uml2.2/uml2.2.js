{
	"title":"UML 2.2",
	"namespace":"http://b3mn.org/stencilset/uml2.2#",
	"description":"Stencil set for UML 2.2 class diagrams.",
	"stencils": [
		
		{
			"type":        "node",
			"id":          "Diagram",
			"title":       "Diagram",	
			"groups":      ["Diagram"],
			"description": "UML Diagram",
			"view":        "diagram.svg",
			"icon":        "diagram.png",
			"roles":       []
		},
		
		{
			"type":        "node",
			"id":          "SimpleClass",
			"title":       "Simple Class",	
			"groups":      ["Class"],
			"description": "Simple Class",
			"view":        "classes/simple_class.svg",
			"icon":        "class.png",
			"roles":       ["class", "ClassMorph", "annotatable"],
			"properties" : [
				{
					"id" : "className",
					"type" : "String",
					"title" : "Name",
					"value" : "Class",
					"refToView" : "className"
				}
			]
		},
		
		{
			"type":        "node",
			"id":          "ComplexClass",
			"title":       "Complex Class",	
			"groups":      ["Class"],
			"description": "Complex Class",
			"view":        "classes/complex_class.svg",
			"icon":        "complex_class.png",
			"roles":       ["class", "ClassMorph", "annotatable"],
			"properties" : [
				{
					"id" : "className",
					"type" : "String",
					"title" : "Name",
					"value" : "Class",
					"refToView" : "className"
				}
			]
		},
		
		{
			"type":        "node",
			"id":          "Note",
			"title":       "Note",	
			"groups":      ["Annotation"],
			"description": "Note",
			"view":        "annotations/note.svg",
			"icon":        "annotation.png",
			"roles":       ["annotation", "boxMorph"],
			"properties" : [
				{
					"id" : "text",
					"type" : "String",
					"title" : "Text",
					"value" : "",
					"refToView" : "text",
					"wrapLines":true
				}
			]
		},
		
		{
			"type" : "edge",
			"id" : "UndirectedAssociation",
			"title" : "Undirected Association",
			"groups" : ["Connector"],
			"description" : "",
			"view" : "connectors/association.svg",
			"icon" : "connectors/association.png",
			"roles" : ["edge", "association", "AssociationMorph"],
			"properties" : [
				{
					"id" : "cardinalityStart",
					"type" : "String",
					"title" : "Cardinality Left",
					"value" : "",
					"refToView" : "cardinalityStart"
				},
				{
					"id" : "cardinalityEnd",
					"type" : "String",
					"title" : "Cardinality Right",
					"value" : "",
					"refToView" : "cardinalityEnd"
				},
				{
					"id" : "name",
					"type" : "String",
					"title" : "Name",
					"value" : "",
					"refToView" : "name"
				},
				{
					"id":"hideLabels",
					"type":"Boolean",
					"title":"Show Labels",
					"value":true,
					"description":"",
					"readonly":false,
					"optional":false,
					"refToView": ["cardinalityStart", "cardinalityEnd", "name"]
				}
			]
		},
		
		{
			"type" : "edge",
			"id" : "DirectedAssociation",
			"title" : "Directed Association",
			"groups" : ["Connector"],
			"description" : "",
			"view" : "connectors/directed_association.svg",
			"icon" : "connectors/directed_association.png",
			"roles" : ["edge", "association", "AssociationMorph"],
			"properties" : [
				{
					"id" : "cardinalityStart",
					"type" : "String",
					"title" : "Cardinality Left",
					"value" : "",
					"refToView" : "cardinalityStart"
				},
				{
					"id" : "cardinalityEnd",
					"type" : "String",
					"title" : "Cardinality Right",
					"value" : "",
					"refToView" : "cardinalityEnd"
				},
				{
					"id" : "name",
					"type" : "String",
					"title" : "Name",
					"value" : "",
					"refToView" : "name"
				},
				{
					"id":"hideLabels",
					"type":"Boolean",
					"title":"Show Labels",
					"value":true,
					"description":"",
					"readonly":false,
					"optional":false,
					"refToView": ["cardinalityStart", "cardinalityEnd", "name"]
				}
			]
		},
		
		{
			"type" : "edge",
			"id" : "UnnavigableDirectedAssociation",
			"title" : "Unnavigable Directed Association",
			"groups" : ["Connector"],
			"description" : "",
			"view" : "connectors/unnavigable_directed_association.svg",
			"icon" : "connectors/unnavigable_directed_association.png",
			"roles" : ["edge", "association", "AssociationMorph"],
			"properties" : [
				{
					"id" : "cardinalityStart",
					"type" : "String",
					"title" : "Cardinality Left",
					"value" : "",
					"refToView" : "cardinalityStart"
				},
				{
					"id" : "cardinalityEnd",
					"type" : "String",
					"title" : "Cardinality Right",
					"value" : "",
					"refToView" : "cardinalityEnd"
				},
				{
					"id" : "name",
					"type" : "String",
					"title" : "Name",
					"value" : "",
					"refToView" : "name"
				},
				{
					"id":"hideLabels",
					"type":"Boolean",
					"title":"Show Labels",
					"value":true,
					"description":"",
					"readonly":false,
					"optional":false,
					"refToView": ["cardinalityStart", "cardinalityEnd", "name"]
				}
			]
		},
		
		{
			"type" : "edge",
			"id" : "Generalization",
			"title" : "Generalization",
			"groups" : ["Connector"],
			"description" : "",
			"view" : "connectors/generalization.svg",
			"icon" : "connectors/generalization.png",
			"roles" : ["edge"],
			"properties" : [
				{
					"id" : "name",
					"type" : "String",
					"title" : "Name",
					"value" : "",
					"refToView" : "name"
				},
				{
					"id":"hideLabels",
					"type":"Boolean",
					"title":"Show Labels",
					"value":true,
					"description":"",
					"readonly":false,
					"optional":false,
					"refToView": ["cardinalityStart", "cardinalityEnd", "name"]
				}
			]
		},
		
		{
			"type" : "edge",
			"id" : "Realization",
			"title" : "Realization",
			"groups" : ["Connector"],
			"description" : "",
			"view" : "connectors/realization.svg",
			"icon" : "connectors/realization.png",
			"roles" : ["edge"],
			"properties" : [
				{
					"id" : "name",
					"type" : "String",
					"title" : "Name",
					"value" : "",
					"refToView" : "name"
				},
				{
					"id":"hideLabels",
					"type":"Boolean",
					"title":"Show Labels",
					"value":true,
					"description":"",
					"readonly":false,
					"optional":false,
					"refToView": ["cardinalityStart", "cardinalityEnd", "name"]
				}
			]
		},
		
		{
			"type" : "edge",
			"id" : "Dependency",
			"title" : "Dependency",
			"groups" : ["Connector"],
			"description" : "",
			"view" : "connectors/dependency.svg",
			"icon" : "connectors/dependency.png",
			"roles" : ["edge"],
			"properties" : [
				{
					"id" : "name",
					"type" : "String",
					"title" : "Name",
					"value" : "",
					"refToView" : "name"
				},
				{
					"id":"hideLabels",
					"type":"Boolean",
					"title":"Show Labels",
					"value":true,
					"description":"",
					"readonly":false,
					"optional":false,
					"refToView": ["cardinalityStart", "cardinalityEnd", "name"]
				}
			]
		},
		
		{
			"type" : "edge",
			"id" : "Aggregation",
			"title" : "Aggregation",
			"groups" : ["Connector"],
			"description" : "",
			"view" : "connectors/aggregation.svg",
			"icon" : "connectors/aggregation.png",
			"roles" : ["edge", "aggregation", "AggregationMorph"],
			"properties" : [
				{
					"id" : "cardinalityStart",
					"type" : "String",
					"title" : "Cardinality Left",
					"value" : "",
					"refToView" : "cardinalityStart"
				},
				{
					"id" : "cardinalityEnd",
					"type" : "String",
					"title" : "Cardinality Right",
					"value" : "",
					"refToView" : "cardinalityEnd"
				},
				{
					"id" : "name",
					"type" : "String",
					"title" : "Name",
					"value" : "",
					"refToView" : "name"
				},
				{
					"id":"hideLabels",
					"type":"Boolean",
					"title":"Show Labels",
					"value":true,
					"description":"",
					"readonly":false,
					"optional":false,
					"refToView": ["cardinalityStart", "cardinalityEnd", "name"]
				}
			]
		},
		
		{
			"type" : "edge",
			"id" : "Composition",
			"title" : "Composition",
			"groups" : ["Connector"],
			"description" : "",
			"view" : "connectors/composition.svg",
			"icon" : "connectors/composition.png",
			"roles" : ["edge", "aggregation", "AggregationMorph"],
			"properties" : [
				{
					"id" : "cardinalityStart",
					"type" : "String",
					"title" : "Cardinality Left",
					"value" : "",
					"refToView" : "cardinalityStart"
				},
				{
					"id" : "cardinalityEnd",
					"type" : "String",
					"title" : "Cardinality Right",
					"value" : "",
					"refToView" : "cardinalityEnd"
				},
				{
					"id" : "name",
					"type" : "String",
					"title" : "Name",
					"value" : "",
					"refToView" : "name"
				},
				{
					"id":"hideLabels",
					"type":"Boolean",
					"title":"Show Labels",
					"value":true,
					"description":"",
					"readonly":false,
					"optional":false,
					"refToView": ["cardinalityStart", "cardinalityEnd", "name"]
				}
			]
		},
		
		{
			"type" : "edge",
			"id" : "Annotation Edge",
			"title" : "Annotation Edge",
			"groups" : ["Annotation"],
			"description" : "",
			"view" : "connectors/annotation.svg",
			"icon" : "connectors/annotation.png",
			"roles" : ["annotationEdge"]
		}
	],
	
	"rules": {
		
		"morphingRules": [
			{
				"role" : "ClassMorph",
				"baseMorphs" : ["SimpleClass", "ComplexClass", "Note"]
			},
			{
				"role" : "AggregationMorph",
				"baseMorphs" : ["Aggregation", "Composition"]
			},
			{
				"role" : "AssociationMorph",
				"baseMorphs" : ["UndirectedAssociation", "DirectedAssociation", "UnnavigableDirectedAssociation"]
			}
		],
		
		"connectionRules": [
			{
				"role" : "edge",
				"connects" : [
					{ "from" : "class",
					    "to" : "class" }
				]
			},
			{
				"role" : "annotationEdge",
				"connects" : [
					{ "from" : "annotatable",
					    "to" : "annotation" },
					{ "from" : "annotation",
					    "to" : "annotatable" }
				]
			},
			{
				"role" : "edge",
				"connects" : [
					{ "from" : "edge",
					    "to" : "class" }
			  ]
			}
 		],
		
		"containmentRules":	[
			{
				"role" : "Diagram",
				"contains" : [
					"class",
					"annotation"
				]
			}
		]
	}
}
