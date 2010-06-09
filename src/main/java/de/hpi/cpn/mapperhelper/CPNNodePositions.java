package de.hpi.cpn.mapperhelper;

import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

public class CPNNodePositions
{
	private Hashtable<String, int[]> nodePositionTable = new Hashtable<String, int[]>();

	public void newNodePosition(String id, JSONObject modelElement) throws JSONException
	{
		JSONObject upperLeftJSON = modelElement.getJSONObject("bounds").getJSONObject("upperLeft");
		JSONObject lowerRightJSON = modelElement.getJSONObject("bounds").getJSONObject("lowerRight");
		
		
		getNodePositionTable().put(id, center(upperLeftJSON, lowerRightJSON));
	}
	
	private int[] center(JSONObject upperLeft, JSONObject lowerRight) throws JSONException
	{
		// Calculating the center position of the element
		// This method returns coordinates that don' have to be converted again into
		// CPN Tools coordinates.
		
		int[] center = new int[2];
		
		int ULx = upperLeft.getInt("x");
		int ULy = upperLeft.getInt("y");
		int LRx = lowerRight.getInt("x");
		int LRy = lowerRight.getInt("y");
		
		int centerX = (int) ((ULx + LRx) / 2);
		int centerY = (int) ((ULy + LRy) / 2);
		
		center[0] = centerX;
		// Multiplying -1 is needed to export the y-value 
		center[1] =  -1 * centerY;
		
		return center;
	}
	
	public int[] getPositionOfNode(String id)
	{
		return getNodePositionTable().get(id);
	}
	
	public void setNodePositionTable(Hashtable<String, int[]> nodePositionTable)
	{
		this.nodePositionTable = nodePositionTable;
	}

	public Hashtable<String, int[]> getNodePositionTable()
	{
		return nodePositionTable;
	}
}
