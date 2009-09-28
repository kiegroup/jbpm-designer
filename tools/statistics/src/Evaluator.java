import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;
import org.oryxeditor.server.diagram.Shape;
import org.springframework.util.FileCopyUtils;

/**
 * 
 */

/**
 * @author Philipp
 *
 */
public class Evaluator {
	static class JSONFilter implements FilenameFilter {
	    public boolean accept(File dir, String name) {
	        return (name.endsWith(".json"));
	    }
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int count=0;
		StringBuilder invalid= new StringBuilder();
		if (args.length != 1) {
			System.err.println("Wrong Number of Arguments!");
			System.err.println(usage());
			return;
		}
		String jsonDirPath = args[0];
		File dir = new File(jsonDirPath);
		System.out.println(jsonDirPath);
		File[] files=dir.listFiles(new JSONFilter());
		List<Diagram> diagrams=new ArrayList<Diagram>();
		HashSet<String> sets=new HashSet<String>();
		HashMap<String, String> lines=new HashMap<String, String>();

		for(File f:files){
			try {
				String str=FileCopyUtils.copyToString(new FileReader(f) );
				str=str.replace("\"target\":{},", "");
				Diagram d = DiagramBuilder.parseJson(str);
				String set=d.getStencilset().getUrl();
				if(!set.contains("bpmn1.1.json") && !set.contains("bpmn.json"))
					continue;
				HashMap<String, Integer> counter=new HashMap<String, Integer>();
				countDiagram(d, counter);
				String models=lines.get("");
				// add for each new property empty cells for each old model
				int modelCount=0;
				if(models!=null)
					modelCount=models.split(";").length;
				Set<String>keys=new HashSet<String>(counter.keySet());
				keys.removeAll(lines.keySet());
				for(String key:keys){
					for(int i=0;i<modelCount;i++){
						addOrAppend(lines,key, "");
						}
				}
				// add for each unused property a empty cell
				Set<String>oldKeys=new HashSet<String>(lines.keySet());
				oldKeys.removeAll(counter.keySet());
				for(String key:oldKeys){
					if(key.equals(""))
						continue;
					addOrAppend(lines,key, "");
				}
				addOrAppend(lines, "", f.getName());
				for(Entry<String, Integer> entry:counter.entrySet()){
					addOrAppend(lines, entry.getKey(), entry.getValue()+"");
				}
				for(String line:lines.values()){
					assert(line.split(";").length==modelCount);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
				invalid.append(f.getName()+"\n");
				count++;
			} 
		}
		for(String s:sets)System.out.println(s);
		System.out.println(count+"\n");
		System.out.println(invalid);
		
		List<String> entries=new ArrayList<String>();
		entries.addAll(lines.keySet());
		Collections.sort(entries);
		StringBuilder file=new StringBuilder();
		for(String entry:entries){
			file.append(entry+";"+lines.get(entry)+"\n");
		}
		try {
			FileWriter writer= new FileWriter(jsonDirPath+File.separator+"output.csv");
			writer.write(file.toString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();}

	}
	/**
	 * @param d
	 * @param counter
	 */
	private static void countDiagram(Diagram d, HashMap<String, Integer> counter) {
		for(Shape shape:d.getShapes()){
			if(shape.getStencilId()==null)
				continue;
			if(shape.getStencilId().equals("Task")){
				int i=0;
				for(Shape income:shape.getIncomings()){
					if(income.getStencilId()!= null && income.getStencilId().equals("SequenceFlow"))
						i++;
				}
				if(i>1)
					addOrIncrement(counter, "implicit XOR-join");
				int x=0;
				for(Shape income:shape.getOutgoings()){
					if(income.getStencilId()!= null && income.getStencilId().equals("SequenceFlow"))
						x++;
				}
				if(x>1)
					addOrIncrement(counter, "implicit AND-split");
				
				if(shape.getProperty("looptype")!=null && shape.getProperty("looptype").equalsIgnoreCase("Standard")){
					addOrIncrement(counter, "Activity Looping");
					continue;
				};
				if(shape.getProperty("looptype")!=null && shape.getProperty("looptype").equalsIgnoreCase("MultiInstance")){
					addOrIncrement(counter, "Multiple Instance");
					continue;
				};
				if(shape.getProperty("iscompensation")!=null && shape.getProperty("iscompensation").equals("true")){
					addOrIncrement(counter, "Compensation");
					continue;
				};
				
			}
				
			String id = shape.getStencilId();
			addOrIncrement(counter, id);
		}
		
	}
	/**
	 * @param counter
	 * @param id
	 */
	private static void addOrIncrement(HashMap<String, Integer> counter,
			String id) {
		if(counter.containsKey(id)){
			int x=counter.get(id);
			counter.put(id, ++x);
		}else{
			counter.put(id, 1);
		}
	}
	private static void addOrAppend(HashMap<String, String> counter,
			String id, String entry) {
		if(counter.containsKey(id)){
			String x=counter.get(id);
			counter.put(id, x+";"+entry);
		}else{
			counter.put(id, entry);
		}
	}
	private static String usage(){
		return "no idea, you are wrong";
	}
}
