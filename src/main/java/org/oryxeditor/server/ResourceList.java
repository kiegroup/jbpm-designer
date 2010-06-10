package org.oryxeditor.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResourceList extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 940662816134311750L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
		doPost(req, res);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
		//get transmitted parameter
		String value = req.getParameter("allocation");
		
		PrintWriter pw = res.getWriter();
		//set attributes for response
		res.setContentType("text/json");
		res.setCharacterEncoding("UTF-8");
		try{
			JSONObject jsonObject = new JSONObject();
			JSONArray dataElements = new JSONArray();
			if(value.equals("direct")) { //create array with distinct names for direct allocation
				String[] name = new String[7];
				name[0] = "Peter Fischer";
				name[1] = "Axel Koch";
				name[2] = "Nadja Richter";
				name[3] = "Petra Larsen";
				name[4] = "Lars Herrmann";
				name[5] = "Vanessa Schmidt";
				name[6] = "Petra Weber";
				for(int i=0; i<name.length; i++) {
					JSONObject directAllocation = new JSONObject();
					directAllocation.put("resource", name[i]);
					dataElements.put(directAllocation);
				}
			} else if(value.equals("functional")) { //create array with distinct functional roles for role-based allocation
				String[] functionalRole = new String[4];
				functionalRole[0] = "sales manager";
				functionalRole[1] = "sales representative";
				functionalRole[2] = "consultant";
				functionalRole[3] = "legal expert";
				for(int i=0; i<functionalRole.length; i++) {
					JSONObject functionalRoleAllocation = new JSONObject();
					functionalRoleAllocation.put("resource", functionalRole[i]);
					dataElements.put(functionalRoleAllocation);
				}
			} else if(value.equals("organisational")) { //create array with distinct organisational roles for organisational allocation
				String[] organisationalRole = new String[3];
				organisationalRole[0] = "Business Contract Signing Group";
				organisationalRole[1] = "Internal Credit Unit";
				organisationalRole[2] = "Researcher";
				for(int i=0; i<organisationalRole.length; i++) {
					JSONObject organisationalRoleAllocation = new JSONObject();
					organisationalRoleAllocation.put("resource", organisationalRole[i]);
					dataElements.put(organisationalRoleAllocation);
				}
			}
			//final return value - JSON object with the appropriate content
			jsonObject.put("resource", dataElements);
			pw.append(jsonObject.toString());
		} catch (JSONException exception) {
			exception.printStackTrace();
		}
	}
}