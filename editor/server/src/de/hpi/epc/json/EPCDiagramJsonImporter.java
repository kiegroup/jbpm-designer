package de.hpi.epc.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.diagram.Diagram;
import de.hpi.diagram.DiagramEdge;
import de.hpi.diagram.DiagramNode;
import de.hpi.diagram.DiagramObject;

/**
 * This is JSON importer for EPCs
 * 
 * @author Stefan Krumnow
 */
public class EPCDiagramJsonImporter {
	
	protected JSONObject jsonObj;
	
	public EPCDiagramJsonImporter(JSONObject jsonObj){
		this.jsonObj = jsonObj;
	}
	
	@SuppressWarnings("unchecked")
	public void loadEpcIntoDiagram(Diagram diagram) throws JSONException {
		
		ImportContext c = new ImportContext();
		c.diagram = diagram;
		c.nodes = new HashMap<String, DiagramNode>(); // key = resource id, value = node
		c.connections = new HashMap<String, DiagramNode>(); // key = to resource id, value = from
		
		List<JSONObject> edges = new ArrayList<JSONObject>();
		
		JSONArray shapeArray = jsonObj.getJSONArray("childShapes");
		
		for (int i = 0; i < shapeArray.length(); i++) {
			JSONObject child = shapeArray.getJSONObject(i);
			String type = child.getJSONObject("stencil").getString("id");
			if (type.equals("Function") || type.equals("Event") ||
					type.equals("AndConnector") || type.equals("OrConnector") ||
					type.equals("XorConnector") || type.equals("ProcessInterface") ||
					type.equals("Organization") || type.equals("Position") ||
					type.equals("Data") || type.equals("System") || type.equals("TextNote")){
				addDiagramNode(type, child, c);
			} else if (type.equals("ControlFlow") || type.equals("Relation")) {
				edges.add(child);
			}
		}
		for (JSONObject edge : edges) {
			String type = edge.getJSONObject("stencil").getString("id");
			addDiagramEdge(type, edge, c);
		}
		JSONObject jsonProperties =  this.jsonObj.getJSONObject("properties");
		Iterator<String> keys = (Iterator<String>)jsonProperties.keys();
		Map<String, String> propertiesMap = new HashMap<String, String>();
		while (keys.hasNext()) {
			String key = keys.next();
			propertiesMap.put(key, jsonProperties.getString(key));
		}
		diagram.setProperties(propertiesMap);
		
		return;
	}
	
	protected void addDiagramNode(String type, JSONObject obj, ImportContext c) throws JSONException{
		DiagramNode n = new DiagramNode();
		n.setType(type);
		String resourceId = obj.getString("resourceId");
		n.setResourceId(resourceId);
		c.diagram.getNodes().add(n);
		c.nodes.put(resourceId, n);
		JSONArray outgoings = obj.getJSONArray("outgoing");
		for (int i = 0; i < outgoings.length(); i++) {
			String targetId = outgoings.getJSONObject(i).getString("resourceId");
			c.connections.put(targetId, n);
		}
		addPropertyMap(n, obj.getJSONObject("properties"));
	}
	
	protected void addDiagramEdge(String type, JSONObject obj, ImportContext c) throws JSONException{
		DiagramEdge e = new DiagramEdge();
		e.setType(type);
		String resourceId = obj.getString("resourceId");
		e.setResourceId(resourceId);
		c.diagram.getEdges().add(e);
		e.setSource(c.connections.get(resourceId));
		String targetId = obj.getJSONObject("target").getString("resourceId");
		e.setTarget(c.nodes.get(targetId));
		addPropertyMap(e, obj.getJSONObject("properties"));
	}
	
	@SuppressWarnings("unchecked")
	protected void addPropertyMap(DiagramObject o, JSONObject jsonProperties) throws JSONException {
		Iterator<String> keys = (Iterator<String>)jsonProperties.keys();
		Map<String, String> propertiesMap = new HashMap<String, String>();
		while (keys.hasNext()) {
			String key = keys.next();
			propertiesMap.put(key, jsonProperties.getString(key));
		}
		o.setProperties(propertiesMap);
	}

//	/*
//	 * Main method in order to test the implementation above!
//	 */
//	public static void main(String[] args) {
//		
//		String testJson = "{\"resourceId\":\"canvas\",\"properties\":{\"title\":\"\",\"version\":\"\",\"author\":\"\",\"description\":\"\"},\"stencil\":{\"id\":\"Diagram\"},\"childShapes\":[{\"resourceId\":\"oryx_9F6208F9-4093-4BFA-B0CF-406D9FC573C2\",\"properties\":{\"title\":\"\",\"commonness\":\"2323\",\"description\":\"\",\"bgcolor\":\"#ffafff\"},\"stencil\":{\"id\":\"Event\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_DB7AF7C0-0752-43C5-B4AE-5BAE4D634246\"}],\"bounds\":{\"lowerRight\":{\"x\":505,\"y\":375},\"upperLeft\":{\"x\":405,\"y\":315}},\"dockers\":[]},{\"resourceId\":\"oryx_1F8967E2-BBD1-4DC7-99D1-735F264A3601\",\"properties\":{\"title\":\"\",\"time\":\"\",\"description\":\"\",\"refuri\":\"\",\"bgcolor\":\"#96ff96\"},\"stencil\":{\"id\":\"Function\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_C2CAC9E1-8EF8-483D-AF1B-71E4A53EE312\"}],\"bounds\":{\"lowerRight\":{\"x\":475,\"y\":180},\"upperLeft\":{\"x\":375,\"y\":120}},\"dockers\":[]},{\"resourceId\":\"oryx_C2CAC9E1-8EF8-483D-AF1B-71E4A53EE312\",\"properties\":{\"probability\":\"232323\"},\"stencil\":{\"id\":\"ControlFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_B5B6530B-799F-4A4D-84EA-0F860E35D4FB\"}],\"bounds\":{\"lowerRight\":{\"x\":425,\"y\":225},\"upperLeft\":{\"x\":345.84375,\"y\":180.296875}},\"dockers\":[{\"x\":50,\"y\":30},{\"x\":425,\"y\":225},{\"x\":15,\"y\":15}],\"target\":{\"resourceId\":\"oryx_B5B6530B-799F-4A4D-84EA-0F860E35D4FB\"}},{\"resourceId\":\"oryx_4029B84F-2553-4825-86B9-DD9A1B4E71BE\",\"properties\":{\"title\":\"\",\"time\":\"3\",\"description\":\"\",\"refuri\":\"\",\"bgcolor\":\"#96ff96\"},\"stencil\":{\"id\":\"Function\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_48A86D15-758E-469C-9991-732C8AD18B5C\"}],\"bounds\":{\"lowerRight\":{\"x\":685,\"y\":330},\"upperLeft\":{\"x\":585,\"y\":270}},\"dockers\":[]},{\"resourceId\":\"oryx_DB7AF7C0-0752-43C5-B4AE-5BAE4D634246\",\"properties\":{\"probability\":\"\"},\"stencil\":{\"id\":\"ControlFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_4029B84F-2553-4825-86B9-DD9A1B4E71BE\"}],\"bounds\":{\"lowerRight\":{\"x\":584.3952168938973,\"y\":345},\"upperLeft\":{\"x\":505.9375,\"y\":326.47924697412344}},\"dockers\":[{\"x\":50,\"y\":30},{\"x\":549,\"y\":345},{\"x\":50,\"y\":30}],\"target\":{\"resourceId\":\"oryx_4029B84F-2553-4825-86B9-DD9A1B4E71BE\"}},{\"resourceId\":\"oryx_AA028580-0FB4-4E73-AB79-8F3D5F62D6FF\",\"properties\":{\"title\":\"\",\"commonness\":\"\",\"description\":\"\",\"bgcolor\":\"#ffafff\"},\"stencil\":{\"id\":\"Event\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_999108FB-3E1C-4D84-BB91-B2B633D03EAA\"}],\"bounds\":{\"lowerRight\":{\"x\":380,\"y\":450},\"upperLeft\":{\"x\":280,\"y\":390}},\"dockers\":[]},{\"resourceId\":\"oryx_48A86D15-758E-469C-9991-732C8AD18B5C\",\"properties\":{\"probability\":\"\"},\"stencil\":{\"id\":\"ControlFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_AA028580-0FB4-4E73-AB79-8F3D5F62D6FF\"}],\"bounds\":{\"lowerRight\":{\"x\":635,\"y\":420},\"upperLeft\":{\"x\":380.443359375,\"y\":331}},\"dockers\":[{\"x\":50,\"y\":30},{\"x\":635,\"y\":420},{\"x\":50,\"y\":30}],\"target\":{\"resourceId\":\"oryx_AA028580-0FB4-4E73-AB79-8F3D5F62D6FF\"}},{\"resourceId\":\"oryx_B5B6530B-799F-4A4D-84EA-0F860E35D4FB\",\"properties\":{\"bgcolor\":\"#ffffff\"},\"stencil\":{\"id\":\"XorConnector\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_1AC2E373-5E4E-445A-9DC1-92AE3EB88AF9\"},{\"resourceId\":\"oryx_A2B1CD32-EF5A-4D3B-BA1F-2578BDD5169D\"}],\"bounds\":{\"lowerRight\":{\"x\":345,\"y\":240},\"upperLeft\":{\"x\":315,\"y\":210}},\"dockers\":[]},{\"resourceId\":\"oryx_1AC2E373-5E4E-445A-9DC1-92AE3EB88AF9\",\"properties\":{\"probability\":\"23\"},\"stencil\":{\"id\":\"ControlFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_9F6208F9-4093-4BFA-B0CF-406D9FC573C2\"}],\"bounds\":{\"lowerRight\":{\"x\":454.78644128838454,\"y\":314.5156495377486},\"upperLeft\":{\"x\":343.53391528433684,\"y\":232.49213564832615}},\"dockers\":[{\"x\":15,\"y\":15},{\"x\":454.6427185054876,\"y\":294},{\"x\":50,\"y\":30}],\"target\":{\"resourceId\":\"oryx_9F6208F9-4093-4BFA-B0CF-406D9FC573C2\"}},{\"resourceId\":\"oryx_44AC07EE-090E-46A4-8BDA-699FE926B503\",\"properties\":{\"bgcolor\":\"#ffffff\"},\"stencil\":{\"id\":\"XorConnector\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_A63104D8-144E-4F38-BD72-4B532D8058B8\"}],\"bounds\":{\"lowerRight\":{\"x\":225,\"y\":345},\"upperLeft\":{\"x\":195,\"y\":315}},\"dockers\":[]},{\"resourceId\":\"oryx_CE389BF5-5688-499E-8FC0-9FCF3A991BE4\",\"properties\":{\"title\":\"\",\"commonness\":\"\",\"description\":\"\",\"bgcolor\":\"#ffafff\"},\"stencil\":{\"id\":\"Event\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_8FD2EE96-FB13-4C98-8D46-D8A23267B221\"}],\"bounds\":{\"lowerRight\":{\"x\":190,\"y\":270},\"upperLeft\":{\"x\":90,\"y\":210}},\"dockers\":[]},{\"resourceId\":\"oryx_A2B1CD32-EF5A-4D3B-BA1F-2578BDD5169D\",\"properties\":{\"probability\":\"66\"},\"stencil\":{\"id\":\"ControlFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_CE389BF5-5688-499E-8FC0-9FCF3A991BE4\"}],\"bounds\":{\"lowerRight\":{\"x\":314.46875,\"y\":240},\"upperLeft\":{\"x\":190.21875,\"y\":225}},\"dockers\":[{\"x\":15,\"y\":15},{\"x\":252.5,\"y\":225},{\"x\":252.5,\"y\":240},{\"x\":50,\"y\":30}],\"target\":{\"resourceId\":\"oryx_CE389BF5-5688-499E-8FC0-9FCF3A991BE4\"}},{\"resourceId\":\"oryx_8FD2EE96-FB13-4C98-8D46-D8A23267B221\",\"properties\":{\"probability\":\"\"},\"stencil\":{\"id\":\"ControlFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_44AC07EE-090E-46A4-8BDA-699FE926B503\"}],\"bounds\":{\"lowerRight\":{\"x\":194.78125,\"y\":330},\"upperLeft\":{\"x\":140,\"y\":270.53125}},\"dockers\":[{\"x\":50,\"y\":30},{\"x\":140,\"y\":330},{\"x\":15,\"y\":15}],\"target\":{\"resourceId\":\"oryx_44AC07EE-090E-46A4-8BDA-699FE926B503\"}},{\"resourceId\":\"oryx_999108FB-3E1C-4D84-BB91-B2B633D03EAA\",\"properties\":{\"probability\":\"\"},\"stencil\":{\"id\":\"ControlFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_44AC07EE-090E-46A4-8BDA-699FE926B503\"}],\"bounds\":{\"lowerRight\":{\"x\":330,\"y\":389.46875},\"upperLeft\":{\"x\":225.0625,\"y\":330}},\"dockers\":[{\"x\":50,\"y\":30},{\"x\":330,\"y\":330},{\"x\":15,\"y\":15}],\"target\":{\"resourceId\":\"oryx_44AC07EE-090E-46A4-8BDA-699FE926B503\"}},{\"resourceId\":\"oryx_F6CBCCAA-926E-4BC8-BAC2-6C3AFB5B717C\",\"properties\":{\"title\":\"\",\"time\":\"\",\"description\":\"\",\"refuri\":\"\",\"bgcolor\":\"#96ff96\"},\"stencil\":{\"id\":\"Function\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_451465C2-3FCA-4D3F-AB16-88D9AB0BA622\"}],\"bounds\":{\"lowerRight\":{\"x\":215,\"y\":492},\"upperLeft\":{\"x\":115,\"y\":432}},\"dockers\":[]},{\"resourceId\":\"oryx_A63104D8-144E-4F38-BD72-4B532D8058B8\",\"properties\":{\"probability\":\"\"},\"stencil\":{\"id\":\"ControlFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_F6CBCCAA-926E-4BC8-BAC2-6C3AFB5B717C\"}],\"bounds\":{\"lowerRight\":{\"x\":210,\"y\":431.140625},\"upperLeft\":{\"x\":165,\"y\":345.625}},\"dockers\":[{\"x\":15,\"y\":15},{\"x\":210,\"y\":388.5},{\"x\":165,\"y\":388.5},{\"x\":50,\"y\":30}],\"target\":{\"resourceId\":\"oryx_F6CBCCAA-926E-4BC8-BAC2-6C3AFB5B717C\"}},{\"resourceId\":\"oryx_3FA53D9C-4F74-40F9-B628-C26658D7D4D8\",\"properties\":{\"title\":\"\",\"commonness\":\"\",\"description\":\"\",\"bgcolor\":\"#ffafff\"},\"stencil\":{\"id\":\"Event\"},\"childShapes\":[],\"outgoing\":[],\"bounds\":{\"lowerRight\":{\"x\":215,\"y\":597},\"upperLeft\":{\"x\":115,\"y\":537}},\"dockers\":[]},{\"resourceId\":\"oryx_451465C2-3FCA-4D3F-AB16-88D9AB0BA622\",\"properties\":{\"probability\":\"\"},\"stencil\":{\"id\":\"ControlFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_3FA53D9C-4F74-40F9-B628-C26658D7D4D8\"}],\"bounds\":{\"lowerRight\":{\"x\":166,\"y\":536.46875},\"upperLeft\":{\"x\":164,\"y\":492.53125}},\"dockers\":[{\"x\":50,\"y\":30},{\"x\":50,\"y\":30}],\"target\":{\"resourceId\":\"oryx_3FA53D9C-4F74-40F9-B628-C26658D7D4D8\"}},{\"resourceId\":\"oryx_5475F160-2D92-49B0-B6F1-82D4FFCC61B3\",\"properties\":{\"title\":\"\",\"commonness\":\"\",\"description\":\"\",\"bgcolor\":\"#ffafff\"},\"stencil\":{\"id\":\"Event\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_3E99E8D4-30DD-4D0E-BC75-9DA97AEF1DD5\"}],\"bounds\":{\"lowerRight\":{\"x\":380,\"y\":105},\"upperLeft\":{\"x\":280,\"y\":45}},\"dockers\":[]},{\"resourceId\":\"oryx_3E99E8D4-30DD-4D0E-BC75-9DA97AEF1DD5\",\"properties\":{\"probability\":\"\"},\"stencil\":{\"id\":\"ControlFlow\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_1F8967E2-BBD1-4DC7-99D1-735F264A3601\"}],\"bounds\":{\"lowerRight\":{\"x\":374.2734375,\"y\":150},\"upperLeft\":{\"x\":330,\"y\":105.296875}},\"dockers\":[{\"x\":50,\"y\":30},{\"x\":330,\"y\":150},{\"x\":50,\"y\":30}],\"target\":{\"resourceId\":\"oryx_1F8967E2-BBD1-4DC7-99D1-735F264A3601\"}},{\"resourceId\":\"oryx_0D6270DE-398B-4DA5-921F-672EBD286A60\",\"properties\":{\"title\":\"\",\"description\":\"\",\"bgcolor\":\"#ffff80\"},\"stencil\":{\"id\":\"Position\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_845AA652-AADB-489D-B8B7-F4618629F7DD\"}],\"bounds\":{\"lowerRight\":{\"x\":387,\"y\":536},\"upperLeft\":{\"x\":277,\"y\":496}},\"dockers\":[]},{\"resourceId\":\"oryx_845AA652-AADB-489D-B8B7-F4618629F7DD\",\"properties\":{\"description\":\"\",\"informationflow\":\"False\"},\"stencil\":{\"id\":\"Relation\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_F6CBCCAA-926E-4BC8-BAC2-6C3AFB5B717C\"}],\"bounds\":{\"lowerRight\":{\"x\":276.25163141505004,\"y\":497.9735814156449},\"upperLeft\":{\"x\":215.52961858494996,\"y\":478.3389185843551}},\"dockers\":[{\"x\":55,\"y\":20},{\"x\":50,\"y\":30}],\"target\":{\"resourceId\":\"oryx_F6CBCCAA-926E-4BC8-BAC2-6C3AFB5B717C\"}},{\"resourceId\":\"oryx_4375693E-F316-4819-BE70-22E72056E278\",\"properties\":{\"title\":\"\",\"description\":\"\",\"bgcolor\":\"#ffff80\"},\"stencil\":{\"id\":\"Position\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_48A08908-F2AC-40F1-A2CD-00295AB3A621\"}],\"bounds\":{\"lowerRight\":{\"x\":780,\"y\":414},\"upperLeft\":{\"x\":670,\"y\":374}},\"dockers\":[]},{\"resourceId\":\"oryx_48A08908-F2AC-40F1-A2CD-00295AB3A621\",\"properties\":{\"description\":\"\",\"informationflow\":\"False\"},\"stencil\":{\"id\":\"Relation\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_4029B84F-2553-4825-86B9-DD9A1B4E71BE\"}],\"bounds\":{\"lowerRight\":{\"x\":705.324053500874,\"y\":373.4495669898017},\"upperLeft\":{\"x\":663.816571499126,\"y\":330.0973080101983}},\"dockers\":[{\"x\":55,\"y\":20},{\"x\":50,\"y\":30}],\"target\":{\"resourceId\":\"oryx_4029B84F-2553-4825-86B9-DD9A1B4E71BE\"}}],\"bounds\":{\"lowerRight\":{\"x\":1485,\"y\":1050},\"upperLeft\":{\"x\":0,\"y\":0}},\"stencilset\":{\"url\":\"/editor/stencilsets/epc/epc.json\",\"namespace\":\"http://b3mn.org/stencilset/epc#\"},\"ssextensions\":[]}";
//		try {
//			JSONObject jsonObject = new JSONObject(testJson);
//			EPCDiagramJsonImporter importer = new EPCDiagramJsonImporter(jsonObject);
//			Diagram diagram = importer.loadEPCDiagram();
//			System.out.println("Finished: " + diagram.toString());
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}	
//	}
	
	protected class ImportContext {
		public Diagram diagram;
		public Map<String, DiagramNode> nodes; // key = resource id, value = node
		public Map<String, DiagramNode> connections; // key = to resource, value = from
	}

}
