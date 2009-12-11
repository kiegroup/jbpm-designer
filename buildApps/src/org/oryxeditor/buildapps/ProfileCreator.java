package org.oryxeditor.buildapps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.FileCopyUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * @author Philipp Berger
 * 
 */
public class ProfileCreator {
	/**
	 * @param args
	 *            path to plugin dir and output dir
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws IOException,
	ParserConfigurationException, SAXException, JSONException {
		if (args.length != 2) {
			System.err.println("Wrong Number of Arguments!");
			System.err.println(usage());
			return;
		}
		String pluginDirPath = args[0];
		;
		String pluginXMLPath = pluginDirPath + "/plugins.xml";// args[0];
		String profilePath = pluginDirPath + "/profiles.xml";// ;
		String outputPath = args[1];
		File outDir = new File(outputPath);
		outDir.mkdir();
		HashMap<String, String> nameSrc = new HashMap<String, String>();
		HashMap<String, ArrayList<String>> profilName = new HashMap<String, ArrayList<String>>();
		ArrayList<String> coreNames = new ArrayList<String>();

		extractPluginData(pluginXMLPath, nameSrc, coreNames);
		extractProfileData(profilePath, profilName);
		for (String key : profilName.keySet()) {
			ArrayList<String> pluginNames = profilName.get(key);
			//add core plugins to each profile
			pluginNames.addAll(coreNames);

			writeProfileJS(pluginDirPath, outputPath, nameSrc, key, pluginNames);

			writeProfileXML(pluginXMLPath, profilePath, outputPath, key,
					pluginNames);
		}

	}

	/**
	 * Create the profileName.js by reading all path out of the nameSrc Hashmap, the required names
	 * are given
	 * @param pluginDirPath
	 * @param outputPath
	 * @param nameSrc
	 *            plugin name to js source file
	 * @param profileName
	 *            name of the profile, serve as name for the js file
	 * @param pluginNames
	 *            all plugins for this profile
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void writeProfileJS(String pluginDirPath, String outputPath,
			HashMap<String, String> nameSrc, String profileName,
			ArrayList<String> pluginNames) throws IOException,
			FileNotFoundException {
		HashSet<String> pluginNameSet = new HashSet<String>();
		pluginNameSet.addAll(pluginNames);

		File profileFile = new File(outputPath + File.separator + profileName +"Uncompressed.js");
		profileFile.createNewFile();
		FileWriter writer = new FileWriter(profileFile);
		for (String name : pluginNameSet) {
			String source = nameSrc.get(name);
			FileReader reader = new FileReader(pluginDirPath + File.separator + source);
			writer.append(FileCopyUtils.copyToString(reader));
		}
		writer.close();
		File compressOut=new File(outputPath + File.separator + profileName +".js");
		FileReader reader = new FileReader(profileFile);
		FileWriter writer2 = new FileWriter(compressOut);
		try{
			com.yahoo.platform.yui.compressor.JavaScriptCompressor x= new JavaScriptCompressor(reader, null);
			x.compress(writer2, 1, true, false, false, false);
		}catch (Exception e) {
			System.err.println("Profile Compression failed! profile: "+compressOut.getAbsolutePath()+ " uncompressed version is used, please ensure javascript correctness");
			e.printStackTrace();
			FileCopyUtils.copy(reader, writer2);

		}finally{
			writer2.close();
		}
	}

	/**
	 * @param pluginDirPath
	 * @param outputPath
	 * @param ProfileName
	 *            name of the profile, serve as name for the js file
	 * @param pluginNames
	 *            all plugins for this profile
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws JSONException 
	 */
	private static void writeProfileXML(String pluginXMLPath,
			String profileXMLPath, String outputPath, String ProfileName,
			ArrayList<String> pluginNames) throws IOException,
			FileNotFoundException, JSONException {

		FileCopyUtils.copy(new FileInputStream(pluginXMLPath),
				new FileOutputStream(outputPath + File.separator + ProfileName + ".xml"));
		InputStream reader = new FileInputStream(outputPath + File.separator + ProfileName
				+ ".xml");
		DocumentBuilder builder;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		try {
			builder = factory.newDocumentBuilder();
			Document outProfileXMLdocument = builder.parse(reader);
			reader = new FileInputStream(profileXMLPath);
			builder = factory.newDocumentBuilder();
			Document profilesXMLdocument = builder.parse(reader);
			NodeList pluginNodeList = outProfileXMLdocument
			.getElementsByTagName("plugin");
			Node profileNode = getProfileNodeFromDocument(ProfileName,
					profilesXMLdocument);

			if (profileNode == null)
				throw new IllegalArgumentException(
						"profile not defined in profile xml");

			NamedNodeMap attr = profileNode.getAttributes();
			JSONObject config=new JSONObject();
			for(int i=0;i<attr.getLength();i++){
				config.put(attr.item(i).getNodeName(), attr.item(i).getNodeValue());
			}
			for (int profilePluginNodeIndex = 0; profilePluginNodeIndex < profileNode
			.getChildNodes().getLength(); profilePluginNodeIndex++) {
				Node tmpNode = profileNode.getChildNodes().item(profilePluginNodeIndex);
				
				if("plugin".equals(tmpNode.getNodeName()) || tmpNode==null)
						continue;
				JSONObject nodeObject = new JSONObject();
				NamedNodeMap attr1 = tmpNode.getAttributes();
				if(attr1==null)
					continue;
				for(int i=0;i<attr1.getLength();i++){
					nodeObject.put(attr1.item(i).getNodeName(), attr1.item(i).getNodeValue());
				}
				
				if(config.has(tmpNode.getNodeName())){
					config.getJSONArray(tmpNode.getNodeName()).put(nodeObject);
				}else{
					config.put(tmpNode.getNodeName(), new JSONArray().put(nodeObject));
				}
				
			}
			FileCopyUtils.copy(config.toString(), new FileWriter(outputPath + File.separator+ProfileName+".conf"));
			// for each plugin in the copied plugin.xml
			for (int i = 0; i < pluginNodeList.getLength(); i++) {
				Node pluginNode = pluginNodeList.item(i);
				String pluginName = pluginNode.getAttributes().getNamedItem(
				"name").getNodeValue();
				// if plugin is in the current profile
				if (pluginNames.contains(pluginName)) {
					// mark plugin as active
					((Element) pluginNode).setAttribute("engaged", "true");

					// throw new
					// IllegalArgumentException("profile not defined in profile xml");
					// plugin defintion found copy or replace properties
					Node profilePluginNode = getLastPluginNode(profileNode,
							pluginName);
					if(profilePluginNode==null){System.out.println("Plugin: "+pluginName+" assumed to be core");break;}
					saveOrUpdateProperties(pluginNode, profilePluginNode);

				}else{
					((Element) pluginNode).setAttribute("engaged", "false");
				}
			}
			writeXMLToFile(outProfileXMLdocument, outputPath + File.separator
					+ ProfileName + ".xml");
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Node getLastPluginNode(Node profileNode, String pluginName) {
		Node profilePluginNode = null;
		// search plugin definition in profile xml
		for (int profilePluginNodeIndex = 0; profilePluginNodeIndex < profileNode
		.getChildNodes().getLength(); profilePluginNodeIndex++) {
			Node tmpNode = profileNode.getChildNodes().item(
					profilePluginNodeIndex);
			if (tmpNode.getAttributes() != null
					&& tmpNode.getAttributes().getNamedItem("name") != null
					&& tmpNode.getAttributes().getNamedItem("name")
					.getNodeValue().equals(pluginName))
				profilePluginNode = tmpNode;
		}
		if (profilePluginNode == null) {
			String[] dependsOnProfiles = getDependencies(profileNode);
			if (dependsOnProfiles==null ||dependsOnProfiles.length == 0) {
				return profilePluginNode;
			}
			for (String dependsProfile : dependsOnProfiles) {
				profilePluginNode = getLastPluginNode(
						getProfileNodeFromDocument(dependsProfile, profileNode
								.getOwnerDocument()), pluginName);
				if (profilePluginNode != null)
					break;
			}
			// plugin definition not found, plugin defined in depended profiles
			// TODO handle recursive property search
		}
		;
		return profilePluginNode;
	};

	/**
	 * @param outProfileXMLdocument
	 * @param xmlFileName
	 * @throws FileNotFoundException
	 */
	private static void writeXMLToFile(Document outProfileXMLdocument,
			String xmlFileName) throws FileNotFoundException {
		// ---- Use a XSLT transformer for writing the new XML file ----
		try {
			Transformer transformer = TransformerFactory.newInstance()
			.newTransformer();
			DOMSource source = new DOMSource(outProfileXMLdocument);
			FileOutputStream os = new FileOutputStream(new File(xmlFileName));
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param pluginNode
	 * @param profilePluginNode
	 * @throws DOMException
	 */
	private static void saveOrUpdateProperties(Node pluginNode,
			Node profilePluginNode) throws DOMException {
		//		for(String dependent:getDependencies(profilePluginNode.getParentNode())){
		//			saveOrUpdateProperties(pluginNode,getProfileNodeFromDocument(dependent, profilePluginNode.getOwnerDocument()));
		//		};
		// for each child node in the profile xml
		for (int index = 0; index < profilePluginNode.getChildNodes()
		.getLength(); index++) {
			Node profilePluginChildNode = profilePluginNode.getChildNodes()
			.item(index);
			// check if property
			if (profilePluginChildNode.getNodeName() == "property") {
				boolean found = false;
				// search for old definitions
				for (int childIndex = 0; childIndex < pluginNode
				.getChildNodes().getLength(); childIndex++) {
					Node pluginChildNode = pluginNode.getChildNodes().item(
							childIndex);
					if (pluginChildNode.getNodeName() == "property") {
						NamedNodeMap propertyAttributes = profilePluginChildNode
						.getAttributes();
						for (int attrIndex = 0; attrIndex < propertyAttributes
						.getLength(); attrIndex++) {
							String newPropertyName = profilePluginChildNode
							.getAttributes().item(attrIndex)
							.getNodeName();
							Node oldPropertyNode = pluginChildNode
							.getAttributes().getNamedItem(
									newPropertyName);
							if (oldPropertyNode != null) {
								// old definition found replace value
								found = true;
								String newValue = profilePluginChildNode
								.getAttributes().item(attrIndex).getNodeValue();
								oldPropertyNode.setNodeValue(newValue);
							}
						}
					}
				}
				if (!found) {
					// no definition found add some
					Node property = pluginNode.getOwnerDocument()
					.createElement("property");
					((Element) property).setAttribute("name",
							profilePluginChildNode.getAttributes()
							.getNamedItem("name").getNodeValue());
					((Element) property).setAttribute("value",
							profilePluginChildNode.getAttributes()
							.getNamedItem("value").getNodeValue());
					pluginNode.appendChild(property);
				}
			}
		}
	}

	/**
	 * @param ProfileName
	 * @param profilesXMLdocument
	 * @throws DOMException
	 */
	private static Node getProfileNodeFromDocument(String ProfileName,
			Document profilesXMLdocument) throws DOMException {
		Node profileNode = null;
		NodeList profileNodes = profilesXMLdocument
		.getElementsByTagName("profile");
		for (int i = 0; i < profileNodes.getLength(); i++) {
			if (profileNodes.item(i).getAttributes().getNamedItem("name")
					.getNodeValue().equals(ProfileName)) {
				profileNode = profileNodes.item(i);
				break;
			}
		}
		return profileNode;
	}

	/**
	 * @param profilePath
	 * @param profilName
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws DOMException
	 */
	private static void extractProfileData(String profilePath,
			HashMap<String, ArrayList<String>> profilName)
	throws FileNotFoundException, ParserConfigurationException,
	SAXException, IOException, DOMException {
		InputStream reader = new FileInputStream(profilePath);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document document = factory.newDocumentBuilder().parse(reader);
		NodeList profiles = document.getElementsByTagName("profile");
		for (int i = 0; i < profiles.getLength(); i++) {
			Node profile = profiles.item(i);
			String name = profile.getAttributes().getNamedItem("name")
			.getNodeValue();
			profilName.put(name, new ArrayList<String>());
			for (int q = 0; q < profile.getChildNodes().getLength(); q++) {
				if (profile.getChildNodes().item(q).getNodeName()
						.equalsIgnoreCase("plugin")) {
					profilName.get(name).add(
							profile.getChildNodes().item(q).getAttributes()
							.getNamedItem("name").getNodeValue());
				}
				;
			}
		}
		resolveDependencies(profilName, profiles);
	}

	/**
	 * @param profilName
	 * @param profiles
	 * @throws DOMException
	 */
	private static void resolveDependencies(
			HashMap<String, ArrayList<String>> profilName, NodeList profiles)
	throws DOMException {
		HashMap<String, String[]> profileDepends = new HashMap<String, String[]>();

		for (int i = 0; i < profiles.getLength(); i++) {
			Node profile = profiles.item(i);
			String name = profile.getAttributes().getNamedItem("name")
			.getNodeValue();
			profileDepends.put(name, getDependencies(profile));
		}

		ArrayList<String> completedProfiles = new ArrayList<String>();
		for (String key : profileDepends.keySet()) {
			if (profileDepends.get(key) == null) {
				completedProfiles.add(key);
			}
		}
		for (String cur : completedProfiles)
			profileDepends.remove(cur);

		while (!profileDepends.isEmpty()) {
			for (String key : profileDepends.keySet()) {
				boolean allIn = true;
				for (String name : profileDepends.get(key)) {
					if (!completedProfiles.contains(name)) {
						allIn = false;
						break;
					}
				}
				if (allIn) {
					for (String name : profileDepends.get(key)) {
						profilName.get(key).addAll(profilName.get(name));
						completedProfiles.add(key);
					}

				}
			}
			for (String cur : completedProfiles)
				profileDepends.remove(cur);
		}
	}

	/**
	 * @param profile
	 *            DocumentNode containing a profile
	 * @throws DOMException
	 */
	private static String[] getDependencies(Node profil) throws DOMException {
		String[] dependencies = null;
		if (profil.getAttributes().getNamedItem("depends") != null) {
			dependencies = profil.getAttributes().getNamedItem("depends")
			.getNodeValue().split(",");
		}
		return dependencies;
	}

	/**
	 * @param pluginXMLPath
	 * @param nameSrc
	 *            HashMap links Pluginnames and Sourcefiles
	 * @param coreNames
	 *            ArrayList containing Names of all core plugins
	 * @throws FileNotFoundException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws DOMException
	 */
	private static void extractPluginData(String pluginXMLPath,
			HashMap<String, String> nameSrc, ArrayList<String> coreNames)
	throws FileNotFoundException, ParserConfigurationException,
	SAXException, IOException, DOMException {
		InputStream reader = new FileInputStream(pluginXMLPath);
		DocumentBuilder builder;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		Document document = builder.parse(reader);
		NodeList plugins = document.getElementsByTagName("plugin");

		for (int i = 0; i < plugins.getLength(); i++) {
			String name = plugins.item(i).getAttributes().getNamedItem("name")
			.getNodeValue();
			String src = plugins.item(i).getAttributes().getNamedItem("source")
			.getNodeValue();
			nameSrc.put(name, src);
			if (plugins.item(i).getAttributes().getNamedItem("core") != null) {
				if (plugins.item(i).getAttributes().getNamedItem("core")
						.getNodeValue().equalsIgnoreCase("true")) {
					coreNames.add(name);
				}
			}
		}
	}

	public static String usage() {
		String use = "Profiles Creator\n"
			+ "Use to parse the profiles.xml and creates\n"
			+ "for each profile an .js-source. Therefore additional\n"
			+ "information from the plugins.xml is required.\n"
			+ "usage:\n" + "java ProfileCreator pluginPath outputDir";
		return use;
	}

}
