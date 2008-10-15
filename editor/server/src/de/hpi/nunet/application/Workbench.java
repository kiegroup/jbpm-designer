package de.hpi.nunet.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import de.hpi.nunet.InterconnectionModel;
import de.hpi.nunet.Marking;
import de.hpi.nunet.NuNet;
import de.hpi.nunet.EnabledTransition;
import de.hpi.nunet.correlatability.CorrelatabilityChecker;
import de.hpi.nunet.simulation.Interpreter;
import de.hpi.nunet.simulation.SigmaBisimulationChecker;
import de.hpi.nunet.validation.SyntaxValidator;

public class Workbench {
	
	public static final String version = "1.0";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Welcome to the nu*-nets workbench!");
		System.out.println("Version "+version+" (c) Gero Decker 2007");
		run();
	}
	
	private static Map<String,NuNet> nunets = new HashMap();
	
	private static final String LOAD = "load";
	private static final String SIMULATE = "simulate";
	private static final String BISIM = "eq";
	private static final String CORR = "corr";
	private static final String DESCRIBE = "describe";
	private static final String LIST = "list";
	private static final String QUIT = "q";
	private static final String HELP = "help";
	
	private static void run() {
		while (true) {
			List<String> command = getNextCommand();
			if (command.size() == 0)
				continue;
			
			String cmd = command.get(0);
			
			if (cmd.equals(LOAD)) {
				doLoad(command);

			} else if (cmd.equals(SIMULATE)) {
				doSimulate(command);
			
			} else if (cmd.equals(BISIM)) {
				doCheckBisimulation(command);
			
			} else if (cmd.equals(CORR)) {
				doCheckCorrelatability(command);
			
			} else if (cmd.equals(LIST)) {
				printNuNetList();
			
			} else if (cmd.equals(DESCRIBE)) {
				printNuNetDescription(command);
			
			} else if (cmd.equals(QUIT)) {
				break;
			
			} else if (cmd.equals(HELP)) {
				printHelp();
			
			} else {
				printUnknownCommand();
			}
		}
	}

	private static void doLoad(List<String> command) {
		if (command.size() < 2) {
			System.out.println("Parameter missing: load [<varname>] <filename>");
			return;
		}
		
		String varname;
		String filename;
		if (command.size() == 2) {
			varname = "default";
			filename = command.get(1);
		} else {
			varname = command.get(1);
			filename = command.get(2);
		}
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new File(filename));

			PNMLImporter importer = new PNMLImporter();
			NuNet net = importer.loadNuNet(doc);
			
			// check syntax
			SyntaxValidator validator = new SyntaxValidator();
			if (net instanceof InterconnectionModel) {
				if (!validator.isValidInterconnectionModel((InterconnectionModel)net)) {
					System.out.println("Could not load interconnection model due to invalid syntax");
					System.out.println(validator.getErrorCode());
					return;
				}
			} else {
				if (!validator.isValidNuNet(net)) {
					System.out.println("Could not load nu*-net due to invalid syntax");
					System.out.println(validator.getErrorCode());
					return;
				}
			}
			
			nunets.put(varname, net);
			if (net instanceof InterconnectionModel)
				System.out.println("interconnection model '"+varname+"' successfully loaded");
			else
				System.out.println("nu*-net '"+varname+"' successfully loaded");

		} catch (Exception e) {
			System.out.println("An error occurred while loading "+filename);
			e.printStackTrace();
		}
	}

	private static void doSimulate(List<String> command) {
		String varname = "default";
		if (command.size() > 1)
			varname = command.get(1);
		NuNet net = nunets.get(varname);
		if (net == null) {
			System.out.println("No nu*-net loaded under name '"+varname+"'");
			return;
		}

		doSimulate(net);
	}

	private static void doSimulate(NuNet net) {
		Interpreter interpreter = new Interpreter();
		Marking marking = net.getInitialMarking();
		while (true) {
			List<EnabledTransition> modes = interpreter.getEnabledTransitions(net, marking);
			if (modes.size() == 0) {
				System.out.println("Simulation done.");
				return;
			}
			
			printEnabledTransitions(modes, marking);

			int i = getNextTransition();
			if (i == -1)
				return;
			if (i >= 0 && i < modes.size())
				interpreter.fireTransition(net, marking, modes.get(i));
		}
	}

	private static void printEnabledTransitions(List<EnabledTransition> modes, Marking marking) {
		int i=0;
		String newname = null;
		for (Iterator<EnabledTransition> it=modes.iterator(); it.hasNext(); i++) {
			EnabledTransition tmode = it.next();
			if (tmode.createsFreshName()) {
				if (newname == null)
					newname = createFreshName(marking);
				tmode.mode.put(NuNet.NEW, newname);
			}
			System.out.println(i+": "+tmode.toString());
		}
	}
	
	private static int freshNameCounter = 0;

	private static String createFreshName(Marking marking) {
		freshNameCounter++; // this guarantees that no previously used name is used again
		while (marking.containsName("new#"+freshNameCounter)) {
			freshNameCounter++;
		}
		return "new#"+freshNameCounter;
	}

	private static int getNextTransition() {
		System.out.print(">> ");
		String str = readln();
		if (str.equals("q"))
			return -1;
		try {
			return new Integer(str);
		} catch (Exception e) {
			return -2;
		}
	}

	private static void doCheckCorrelatability(List<String> command) {
		String varname = "default";
		if (command.size() > 1)
			varname = command.get(1);
		NuNet net = nunets.get(varname);
		if (net == null) {
			System.out.println("No interconnection model loaded under name '"+varname+"'");
			return;
		}
		if (!(net instanceof InterconnectionModel)) {
			System.out.println("'"+varname+"' is not an interconnection model");
			return;
		}
		
		CorrelatabilityChecker checker = new CorrelatabilityChecker((InterconnectionModel)net);
		if (checker.checkCorrelatability()) {
			System.out.println("'"+varname+"' is correlatable");
		} else {
			System.out.print("'"+varname+"' is not correlatable. Show details (y/n)? ");
			if (readln().equals("y")) {
				System.out.println();
				Marking[] lastmarkings = checker.getLastMarkingsChecked();
				System.out.println(checker.getLastTransitionModeChecked().toString());
				System.out.println("in marking    "+lastmarkings[0].toString());
				System.out.println("cannot be simulated in   "+lastmarkings[1].toString());
			}
		}
	}
	
	private static void doCheckBisimulation(List<String> command) {
		if (command.size() < 3) {
			System.out.println("Parameter missing: eq <varname> <varname>");
			return;
		}
		NuNet net1 = nunets.get(command.get(1));
		if (net1 == null) {
			System.out.println("No nu*-net loaded under name "+command.get(1));
			return;
		}
		NuNet net2 = nunets.get(command.get(2));
		if (net2 == null) {
			System.out.println("No nu*-net loaded under name "+command.get(2));
			return;
		}

		SigmaBisimulationChecker checker = new SigmaBisimulationChecker();
		if (checker.checkSigmaBisimilarity(net1, net2)) {
			System.out.println("The two models are sigma-bisimilar (relation size = "+checker.getBisimulationRelationSize()+").");
		} else {
			System.out.print("The two models are not sigma-bisimilar. Show details (y/n)? ");
			if (readln().equals("y")) {
				System.out.println();
				Marking[] lastmarkings = checker.getLastMarkingsChecked();
				System.out.println(checker.getLastTransitionModeChecked().toString());
				System.out.println("in marking    "+lastmarkings[0].toString());
				System.out.println("cannot be simulated in   "+lastmarkings[1].toString());
			}
		}
	}

	private static void printNuNetList() {
		System.out.println("Currently loaded nu*-nets:");
		for (Iterator<String> it=nunets.keySet().iterator(); it.hasNext(); )
			System.out.println("'"+it.next()+"'");
	}

	private static void printNuNetDescription(List<String> command) {
		String varname = "default";
		if (command.size() > 1)
			varname = command.get(1);
		NuNet net = nunets.get(varname);
		if (net == null) {
			System.out.println("No nu*-net loaded under name '"+varname+"'");
			return;
		}
//		System.out.println("Nu*-net '"+varname+"':");
		System.out.println(net.getPlaces().size()+" places: "+net.getPlaces());
		System.out.println(net.getTransitions().size()+" transitions: "+net.getTransitions());
		System.out.println(net.getFlowRelationships().size()+" arcs: "+net.getFlowRelationships());
		System.out.print(net.getInitialMarking().getNumTokens()+" tokens and ");
		System.out.println(net.getInitialMarking().getNames().size()+" names in the initial marking: "+net.getInitialMarking().getNames());
		System.out.println("Initial marking: "+net.getInitialMarking().toString());
	}
	
	private static void printHelp() {
		System.out.println();
		System.out.println("load [<varname>] <filename> --  Loads a nu*-net");
		System.out.println("describe [<varname>]        --  Shows info about a nu*-net");
		System.out.println("simulate [<varname>]        --  Simulates a nu*-net");
		System.out.println("eq <varname> <varname>      --  Checks two nu*-nets for sigma-bisimilarity");
		System.out.println("corr [<varname>]            --  Checks a nu*-net for correlatability");
		System.out.println("list                        --  Enumerates all nu*-nets loaded");
		System.out.println("help                        --  Prints this help menu");
		System.out.println("q                           --  Quits the workbench");
	}

	private static void printUnknownCommand() {
		System.out.println("Unknown command. Press 'help' for help");
	}

	private static List<String> getNextCommand() {
		System.out.println();
		System.out.print("> ");
		String str = readln();
		List<String> command = new ArrayList();
		
		int currStart = -1;
		boolean isReading = false;
		for (int i=0; i<str.length(); i++) {
			char ch = str.charAt(i);
			if (ch == ' ') {
				if (isReading && str.charAt(currStart) != '"') {
					isReading = false;
					command.add(str.substring(currStart, i));
				}
			} else if (ch == '"' && isReading && str.charAt(currStart) == '"') {
				isReading = false;
				command.add(str.substring(currStart+1, i));
			} else {
				if (!isReading) {
					isReading = true;
					currStart = i;
				}
			}
		}
		if (isReading)
			if (str.charAt(currStart) == '"')
				command.add(str.substring(currStart+1, str.length()));
			else
				command.add(str.substring(currStart, str.length()));
		
		
		return command;
	}

	private static BufferedReader m_reader;

	static {
		try {
			m_reader = new BufferedReader(new InputStreamReader(System.in));
		}
		catch (Throwable e) {
			System.out.println("Warning: Input from console not possible.");
		}
	}

    private static String readln () {
    	try {
    		return m_reader.readLine();
        	}
        catch (Throwable e) {
        	return "";
        }
	}
    
}
