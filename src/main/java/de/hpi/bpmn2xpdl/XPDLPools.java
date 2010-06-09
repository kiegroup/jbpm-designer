package de.hpi.bpmn2xpdl;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Pools")
public class XPDLPools extends XMLConvertible {

	@Element("Pool")
	protected ArrayList<XPDLPool> pools;

	public void add(XPDLPool newPool) {
		initializePools();
		
		getPools().add(newPool);
	}
	
	public void createAndDistributeMapping(Map<String, XPDLThing> mapping) {
		if (getPools() != null) {
			for (XPDLPool pool: getPools()) {
				pool.setResourceIdToObject(mapping);
				String id=pool.getId();
				mapping.put(id, pool);
				pool.createAndDistributeMapping(mapping);
			}
		}
	}
	
	public ArrayList<XPDLPool> getPools() {
		return pools;
	}

	public void readJSONpoolsunknowns(JSONObject modelElement) {
		readUnknowns(modelElement, "poolsunknowns");
	}
	
	public void setPools(ArrayList<XPDLPool> pool) {
		this.pools = pool;
	}
	
	public void writeJSONchildShapes(JSONObject modelElement) throws JSONException {
		ArrayList<XPDLPool> poolsList = getPools();
		if (poolsList != null) {
			initializeChildShapes(modelElement);
			
			JSONArray childShapes = modelElement.getJSONArray("childShapes");
			for (int i = 0; i < poolsList.size(); i++) {
				XPDLPool convertPool = poolsList.get(i);
				
				if (convertPool.getMainPool()) {
					convertPool.writeMainPool(modelElement);
				} else {
					JSONObject newPool = new JSONObject();
					convertPool.write(newPool);
					childShapes.put(newPool);
				}
			}
		}
	}
	
	public void writeJSONpoolsunknowns(JSONObject modelElement) throws JSONException {
		writeUnknowns(modelElement, "poolsunknowns");
	}
	
	protected void initializeChildShapes(JSONObject modelElement) throws JSONException {
		if (modelElement.optJSONArray("childShapes") == null) {
			modelElement.put("childShapes", new JSONArray());
		}
	}
	
	protected void initializePools() {
		if (getPools() == null) {
			setPools(new ArrayList<XPDLPool>());
		}
	}
}
