package de.hpi.epc.layouting.model;

public class EPCType {
		public final static String PREFIX = "http://b3mn.org/stencilset/epc#";
		
		// Canvas
		public final static String EPCDiagram = PREFIX + "EPCDiagram";

		public static boolean isADiagram(String type) {
			return type.equals(EPCDiagram);
		}

		// Functions
		public final static String Function = PREFIX + "Function";
		public final static String ProcessInterface = PREFIX + "ProcessInterface";
		public final static String Subprocess = PREFIX + "Subprocess";

		public static boolean isAFunction(String type) {
			return type.equals(Function) || type.equals(ProcessInterface);
		}
		
		public final static String Event = PREFIX + "Event";

		public static boolean isAnEvent(String type) {
			return type.equals(Event);
		}


		// Artifacts
		public final static String System = PREFIX + "System";
		public final static String Data   = PREFIX + "Data";
		public final static String Organization = PREFIX + "Organization";
		public final static String Position = PREFIX + "Position";
		public final static String TextNote = PREFIX + "TextNote";

		public static boolean isAArtifact(String type) {
			return type.equals(System) || type.equals(Data)
				|| type.equals(Organization)
				|| type.equals(Position)
				|| type.equals(TextNote);
		}

		// Connecting Elements
		public final static String ControlFlow = PREFIX + "ControlFlow";
		public final static String Relation = PREFIX + "Relation";

		public static boolean isAConnectingElement(String type) {
			return type.equals(ControlFlow) || type.equals(Relation);
		}

		// Connector
		public final static String OrConnector = PREFIX + "OrConnector";
		public final static String AndConnector = PREFIX + "AndConnector";
		public final static String XorConnector = PREFIX + "XorConnector";

		public static boolean isAConnector(String type) {
			return type.equals(OrConnector)
					|| type.equals(AndConnector)
					|| type.equals(XorConnector);
		}
	}
