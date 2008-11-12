
/**
 *  Defines all XML-Namespaces
 */

XMLNS = {
	ATOM:	"http://www.w3.org/2005/Atom",
	XHTML:	"http://www.w3.org/1999/xhtml",
	ERDF:	"http://purl.org/NET/erdf/profile",
	RDFS:	"http://www.w3.org/2000/01/rdf-schema#",
	RDF:	"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
	RAZIEL: "http://b3mn.org/Raziel",

	SCHEMA: ""
};

if( !Repository ){ Repository = {} }
if( !Repository.Config ){ Repository.Config = {} }

Repository.Config.BACKEND_PATH		= '/backend/poem';
Repository.Config.STENCILSET_URI	= "/oryx/stencilsets/stencilsets.json"
Repository.Config.PATH 				= '/backend/repository2/'
Repository.Config.PLUGIN_PATH 		= 'plugins/'
Repository.Config.PLUGIN_CONFIG 	= 'plugins.xml'


Repository.Config.SORT_DESC 		= 'desc'
Repository.Config.SORT_ASC 			= 'asc'
