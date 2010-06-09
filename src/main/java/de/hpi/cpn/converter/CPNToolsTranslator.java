package de.hpi.cpn.converter;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.*;

import de.hpi.cpn.CPNWorkspaceElement;
import de.hpi.cpn.attributes.CPNBendpoint;
import de.hpi.cpn.attributes.CPNProperty;
import de.hpi.cpn.elements.CPNArc;
import de.hpi.cpn.elements.CPNPage;
import de.hpi.cpn.elements.CPNPlace;
import de.hpi.cpn.elements.CPNTransition;
import de.hpi.cpn.globbox.CPNBlock;
import de.hpi.cpn.globbox.CPNColor;
import de.hpi.cpn.globbox.CPNGlobbox;
import de.hpi.cpn.globbox.CPNProduct;
import de.hpi.cpn.globbox.CPNVariable;
import de.hpi.cpn.mapperhelper.CPNArcRelations;

public class CPNToolsTranslator 
{
	private Diagram oryxDiagram;
	private CPNWorkspaceElement cpnfile;
	private CPNArcRelations arcRelations;
	private JSONArray declarations;
	private int[] relativeBounds;
	
	public CPNToolsTranslator(CPNWorkspaceElement cpnfile)
	{
		setCpnfile(cpnfile);		
	}	
	
	public String translatePagesIntoDiagrams(String[] pagesToImport) throws JSONException
	{
		String resultDiagrams = "";
		
		// Setting up the CPN diagram
		setOryxDiagram(CPNDiagram.newColoredPetriNetDiagram());
		
		// Beginning the mapping with the declarations
		insertDeclarationsIntoDiagram();
		
		// Getting all pages
		ArrayList<CPNPage> pages = getCpnfile().getCpnet().getPages();
		
		for (int i = 0; i < pages.size(); i++)
		{
			CPNPage page = pages.get(i);
			if (page == null)
				continue;
			
			// Getting the name of the page
			String pageName = page.getPageattr().getName();
			
			// Checking if the page is chosen to be exported
			if (! isPageAnImportPage(pageName, pagesToImport))
				continue;
			
			// In order calculate the position correctly you have to 
			// find out what are the biggest bounds in the page 
			setRelativeBounds(CPNDiagram.getMaxBounds(page));
			
			CPNDiagram.setDiagramBounds(getOryxDiagram(), getRelativeBounds());
			
			// Preparing the Arc Relations, so that every arc knows which id is its 
			// source or target
			prepareArcRelations(page);
			
			insertModellingElementsIntoDiagram(page);
			
			// ";;;" is the limiter
			resultDiagrams += JSONBuilder.parseModeltoString(getOryxDiagram()) + ";;;";
			
			// For importing another diagram the important variables must be cleared
			// otherwise there would occur errors. For example if the chilShapes array would
			// not be cleared then it would contain the childShapes of the last import too.
			resetPageVariable();			
		}
		
		// Cutting the last ";;;"
		resultDiagrams = resultDiagrams.substring(0, resultDiagrams.length() - 3);
		
		return resultDiagrams;
	}
	
	private void resetPageVariable()
	{
		// Setting up like it was in the beginning of the 
		setArcRelations(null);
		getOryxDiagram().setBounds(null);
		getOryxDiagram().setChildShapes(new ArrayList<Shape>());
	}
	
	private boolean isPageAnImportPage(String pageToTest, String[] pagesToImport)
	{
		// Looking whether the page is in the array
		for (int i = 0; i < pagesToImport.length; i++)
		{
			if (pageToTest.equals(pagesToImport[i]))
				return true;
		}
		
		return false;
	}	
	
	private void prepareArcRelations(CPNPage tempPage)
	{
		getArcRelations().fill(tempPage);
	}
	
	private void insertModellingElementsIntoDiagram(CPNPage page)
	{		
		ArrayList<CPNPlace> places = page.getPlaces();
		ArrayList<CPNTransition> transitions = page.getTransitions();
		ArrayList<CPNArc> arcs = page.getArcs();
		
		// Mapping all arcs 
		for (int i = 0; i < places.size(); i++)
		{
			CPNPlace tempPlace = places.get(i);
			
			if (tempPlace != null)
				insertNewModellingElement(tempPlace);
		}
		
		// Mapping all transitions
		for (int i = 0; i < transitions.size(); i++)
		{
			CPNTransition tempTransitions = transitions.get(i);
			
			if (tempTransitions != null)
				insertNewModellingElement(tempTransitions);
		}
		
		// Mapping all arcs
		for (int i = 0; i < arcs.size(); i++)
		{
			CPNArc tempArc = arcs.get(i);
			
			if (tempArc != null)
				insertNewModellingElement(tempArc);
		}
		
		
	}
	
	private void insertDeclarationsIntoDiagram() throws JSONException
	{
		// Getting all necessary variables from the object
		CPNGlobbox globbox = getCpnfile().getCpnet().getGlobbox();
		ArrayList<CPNColor> colors = globbox.getColors();
		ArrayList<CPNVariable> variables = globbox.getVars();
		ArrayList<CPNBlock> blocks = globbox.getBlocks();
		
		// Looking in the colors array
		if (colors != null)
		{
			// For all colors in the globbox node of the XML
			for (int i = 0; i < colors.size(); i++)
			{
				CPNColor tempColor = colors.get(i);
				if (tempColor != null)
					insertNewDeclaration(tempColor);			
			}
		}
		
		if (variables != null)
		{
			// For all variables in the globbox node of the XML
			for (int i = 0; i < variables.size(); i++)
			{
				CPNVariable tempVariable = variables.get(i);
				if (tempVariable != null)
					insertNewDeclaration(tempVariable);			
			}
		}
		
		if (blocks == null)
			return;
		
		// For all blocks in the globbox node of the XML
		for (int i = 0; i < blocks.size(); i++)
		{
			CPNBlock tempBlock = blocks.get(i);
			
			if (tempBlock != null)
			{
				colors = tempBlock.getColors();
				variables = tempBlock.getVars();
				
				if (colors != null)
				{
					// For all colors in the block node of the XML 
					for (int j = 0; j < colors.size(); j++)
					{
						CPNColor tempColor = colors.get(j);
						if (tempColor != null)
							insertNewDeclaration(tempColor);			
					}
				}
				
				if (variables != null)
				{
					// For all variables in the block node of the XML 
					for (int j = 0; j < variables.size(); j++)
					{
						CPNVariable tempVariable = variables.get(j);
						if (tempVariable != null)
							insertNewDeclaration(tempVariable);			
					}
				}				
			}			
		}
		
		// Adding declarations to the diagram property
		JSONObject declarationJSON = CPNDiagram.getDeclarationJSONObject(getDeclarations());
		getOryxDiagram().putProperty("declarations", declarationJSON.toString());
	}
	
	private void insertNewModellingElement(CPNArc tempArc)
	{
		String arcId = tempArc.getId();
		
		Shape arc = CPNDiagram.getanArc(arcId);
		
		// Properties
		arc.putProperty("id", arcId);
									// sorry for the long way
		arc.putProperty("label", tempArc.getAnnot().getText().getText());
		
		// Bounds
		// I don't have to take care of the bounds of the arc; the bounds can be 0
		CPNDiagram.setArcBounds(arc); 
				
		// Outgoing
		String targetId = getArcRelations().getTargetValue(arcId);
		Shape targetShape = new Shape(targetId);
		arc.addOutgoing(targetShape);
		arc.setTarget(targetShape);
		
		// Dockers		
		setDockers(arc, tempArc);
		
		getOryxDiagram().getChildShapes().add(arc);		
	}
	
	private void setDockers(Shape arc, CPNArc tempArc)
	{
		// See the comment in the first line of the class CPNArc to understand better why
		// this has to be done
		
		// If the arc starts from a transition
		if (tempArc.getOrientation().equals("TtoP"))
		{				
			ArrayList<CPNBendpoint> bendPoints = tempArc.getBendpoints();
			
			// Sorting the bendpoints so that they in correct order for Oryx
			sortBendPoints(bendPoints, true);			
			insertBendPointsIntoDockers(arc, bendPoints);				   
				
			// (20;20) is the center of the transition shape. These last docker entries
			// are necessary, because they define from which point the arc begins and ends.
			// The position of the first and the last docker are relative to the bounds
			// of the shape that contains the corresponding docker. These are inserted
			// at the beginning and at the end of the dockers array.
			arc.getDockers().add(0, new Point(20.0, 20.0));			
			arc.getDockers().add(arc.getDockers().size(), new Point(32.0, 32.0));
		}
		else // Otherwise "PtoT"
		{
			ArrayList<CPNBendpoint> bendPoints = tempArc.getBendpoints();
		
			// Sorting the bendpoints so that they in correct order for Oryx; but now in
			// the reverse order.   
			sortBendPoints(bendPoints, false);			
			insertBendPointsIntoDockers(arc, bendPoints);
			
			arc.getDockers().add(0, new Point(32.0, 32.0));
			arc.getDockers().add(arc.getDockers().size(), new Point(20.0, 20.0));
		}		
	}
	
	private void insertBendPointsIntoDockers(Shape arc, ArrayList<CPNBendpoint> bendPoints)
	{
		for (int i = 0; i < bendPoints.size(); i++)
		{
			CPNBendpoint bendPoint = bendPoints.get(i);
			Point dockerPoint = CPNDiagram.getDockerBendpoint(bendPoint, getRelativeBounds());
			arc.getDockers().add(dockerPoint);
		}
	}
	
	private void sortBendPoints(ArrayList<CPNBendpoint> bendPoints, boolean smallToBig)
	{
		int smallToBigFactor = (smallToBig) ? 1 : -1;
		
		// Implementing bubbleSort
		for(int i = 0; i < bendPoints.size(); i++)
		{
			for(int j = bendPoints.size() - 1; j > i; j--)
			{
				int serialA = Integer.parseInt(bendPoints.get(j-1).getSerial()) * smallToBigFactor;
				int serialB = Integer.parseInt(bendPoints.get(j).getSerial()) * smallToBigFactor;
				
				if (serialA > serialB)
				{
					CPNBendpoint tempBendpoint = bendPoints.get(j-1);
					bendPoints.set(j - 1, bendPoints.get(j));
					bendPoints.set(j, tempBendpoint);
				}
			}
		}
	}
	
	private void insertNewModellingElement(CPNTransition tempTransition)
	{
		String transitionId = tempTransition.getId();
		
		Shape transition = CPNDiagram.getaTransition(transitionId);
		
		// Properties
		transition.putProperty("id", transitionId);
		transition.putProperty("title", tempTransition.getText());
													// sorry for the long way
		String guard = tempTransition.getCond().getText().getText();
		guard.replace(" ", "\n");
		transition.putProperty("guard", guard);
		
		// Bounds
		CPNDiagram.setTransitionBounds(transition, getRelativeBounds(), tempTransition); 
				
		// Outgoing
		Iterator<String> outgoingIter = getArcRelations().getSourcesFor(transitionId).iterator();
		
		while (outgoingIter.hasNext())
		{
			String outgoingId = outgoingIter.next();
			transition.addOutgoing(new Shape(outgoingId));
		}		
		
		getOryxDiagram().getChildShapes().add(transition);		
	}
	
	private void insertNewModellingElement(CPNPlace tempPlace)
	{
		String placeId = tempPlace.getId();
		
		Shape place = CPNDiagram.getaPlace(placeId);
		
		// Mapping the properties
		place.putProperty("id", placeId);
		String title = tempPlace.getText();
		// For not having the whole title of the place in one line
		title.replace(" ", "\n");
		
		place.putProperty("title", title);
		
		// Sorry for the long way
		String type = tempPlace.getType().getText().getText();											
		place.putProperty("colorsettype", type);
		
		// Bounds
		CPNDiagram.setPlaceBounds(place, getRelativeBounds(), tempPlace);
		
		// Tokens
		insertNewTokens(place, tempPlace.getInitmark()); 
				
		// Outgoing
		Iterator<String> outgoingIter = getArcRelations().getSourcesFor(placeId).iterator();
		
		while (outgoingIter.hasNext())
		{
			String outgoingId = outgoingIter.next();
			place.addOutgoing(new Shape(outgoingId));
		}		
		
		getOryxDiagram().getChildShapes().add(place);		
	}
	
	private void insertNewTokens(Shape place, CPNProperty tempInitMark)
	{
		String initialDefinition = tempInitMark.getText().getText();
		
		if ( initialDefinition.equals(""))
			return;
		
		// Looking if initialDefinition contains the string "++"
		if (initialDefinition.indexOf("++") != -1)
		{	
			// A regex which means ++
			// So the string is split when a "++" occurs
			String[] initialDefinitionParts = initialDefinition.split("\\+\\+");
			
			for (int i = 0; i < initialDefinitionParts.length; i++)
			{
				Shape token = CPNDiagram.getaToken(tempInitMark.getId() + i);
				
				if (initialDefinitionParts[i].indexOf("`") != -1)
				{
					try
					{
						String[] initialDefinitionParts2 = initialDefinitionParts[i].split("`");
						// Properties
						token.putProperty("initialmarking", initialDefinitionParts2[1]);
						token.putProperty("quantity", initialDefinitionParts2[0]);
						
						// Bounds
						CPNDiagram.setTokenBounds(token, i);
						
						place.getChildShapes().add(token);
					}
					catch (IndexOutOfBoundsException e){ }
				}
				else
				{					
					// Properties
					token.putProperty("initialmarking", initialDefinitionParts[i]);
					token.putProperty("quantity", "1");
					
					// Bounds
					CPNDiagram.setTokenBounds(token, i);
					
					place.getChildShapes().add(token);
				}
			}
		}
		else 
		// Otherwise the whole text of the initialmarking is written into one token
		{
			// The same like above
			Shape token = CPNDiagram.getaToken(tempInitMark.getId());
			
			// Properties
			token.putProperty("initialmarking", initialDefinition);
			token.getProperties().put("quantity", "1");
			
			// Bounds
			CPNDiagram.setTokenBounds(token, 0);
			
			place.getChildShapes().add(token);
		}
		
	}
	
	private void insertNewDeclaration(CPNVariable tempVariable) throws JSONException
	{
		String name, type, declarationtype;
		name = tempVariable.getIdtag();
		declarationtype = "Variable";
		type = tempVariable.getType().getId();		
		
		getDeclarations().put(CPNDiagram.getOneDeclaration(name, type, declarationtype));
	}
	
	private void insertNewDeclaration(CPNColor tempColor) throws JSONException
	{
		String name, type, declarationtype;
		name = tempColor.getIdtag();
		declarationtype = "Colorset";
		type = "";
		
		// Looking which type this color have
		if (tempColor.getStringtag() != null)
			type = "String";
		
		else if (tempColor.getBooleantag() != null)
			type = "Boolean";
		
		else if (tempColor.getIntegertag() != null)
			type = "Integer";
				
		else if (tempColor.getProducttag() != null)
		{
			CPNProduct tempProduct = tempColor.getProducttag();
			int i;
			for (i = 0; i < tempProduct.getIds().size() - 1; i++)
				type += tempProduct.getId(i) + " * ";			
			
			type += tempProduct.getId(i);
		}
		else if (tempColor.getListtag() != null)
		{
			String listType = tempColor.getListtag().getId();
			type  = "list " + listType;
		}
		else if (tempColor.getUnittag() != null)
			type = "Unit";
		
		else
			type = "no support for: " + tempColor.getLayout();
		
		// Creating a declaration entry
		getDeclarations().put(CPNDiagram.getOneDeclaration(name, type, declarationtype));		
	}
	
	
	public Diagram getOryxDiagram()
	{
		return oryxDiagram;
	}
	private void setOryxDiagram(Diagram diagram)
	{
		oryxDiagram = diagram;
	}

	private void setCpnfile(CPNWorkspaceElement cpnfile) 
	{
		this.cpnfile = cpnfile;
	}
	public CPNWorkspaceElement getCpnfile()
	{
		return cpnfile;
	}

	private JSONArray getDeclarations()
	{
		if (declarations == null)
			declarations = new JSONArray();
		return declarations;
	}
	
	private CPNArcRelations getArcRelations()
	{
		if (arcRelations == null)
			arcRelations = new CPNArcRelations();
		return arcRelations;
	}
	
	private void setArcRelations(CPNArcRelations _ArcRelations)
	{
		this.arcRelations = _ArcRelations;
	}

	private void setRelativeBounds(int[] relativeBounds)
	{
		this.relativeBounds = new int[2];
		// 50 is only a padding, so that there exists a little distance to the
		// border of the canvas.
		this.relativeBounds[0] = relativeBounds[0] * -1 + 50;
		this.relativeBounds[1] = relativeBounds[1] * -1 - 50;
	}
	private int[] getRelativeBounds() {
		return relativeBounds;
	}	
}
