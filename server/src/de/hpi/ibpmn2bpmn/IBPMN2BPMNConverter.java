package de.hpi.ibpmn2bpmn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hpi.bpmn.ANDGateway;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.Container;
import de.hpi.bpmn.Edge;
import de.hpi.bpmn.EndEvent;
import de.hpi.bpmn.EndPlainEvent;
import de.hpi.bpmn.Event;
import de.hpi.bpmn.Gateway;
import de.hpi.bpmn.IntermediateEvent;
import de.hpi.bpmn.IntermediateMessageEvent;
import de.hpi.bpmn.Lane;
import de.hpi.bpmn.MessageFlow;
import de.hpi.bpmn.Node;
import de.hpi.bpmn.Pool;
import de.hpi.bpmn.SequenceFlow;
import de.hpi.bpmn.StartEvent;
import de.hpi.bpmn.StartPlainEvent;
import de.hpi.bpmn.SubProcess;
import de.hpi.bpmn.XORDataBasedGateway;
import de.hpi.bpmn.XOREventBasedGateway;
import de.hpi.bpmn.serialization.BPMNSerialization;
import de.hpi.ibpmn.IBPMNDiagram;
import de.hpi.ibpmn.Interaction;
import de.hpi.ibpmn.OwnedXORDataBasedGateway;

/**
 * @author Gero.Decker
 *
 * (1) duplicate the processes,
 *     (1.1) replace interaction with send / receive message events or dummy nodes
 *     (1.2) replace XOR data-based gateways with XOR event-based gateways if necessary
 * (2) reduce processes, 
 *     (2.1) remove dummy nodes from processes
 *     (2.2) remove empty subprocesses
 *     (2.3) remove unnecessary gateways (using SESE-regions)
 *     (2.4) handle structure following XOR event-based gateways
 * (3) add message flows
 * 
 * Current limitations of the mapping: see ConstraintChecker
 *   
 */
public class IBPMN2BPMNConverter {
	
	protected IBPMNDiagram ibpmn;
	protected BPMNFactory factory;
	
	public IBPMN2BPMNConverter(IBPMNDiagram diagram) {
		this.ibpmn = diagram;
		factory = new BPMNFactory(); 
	}
	
	public BPMNDiagram convert() throws ConversionException {
		ConversionContext c = new ConversionContext();
		
		// for all pools duplicate + reduce
		for (Node n: ibpmn.getChildNodes()) {
			if (n instanceof Pool) {
				Pool role = (Pool) n;
				
				// (1) duplicate the processes
				Pool pool = addDuplicate(role, c);
				
				// (2) reduce processes
				reduceDuplicate(pool, c);
			}
		}
		// (3) add message flows
		addMessageFlows(ibpmn, c);
		
		return c.bpmn;
	}
	
	// ---- helper classes ----

	protected class ConversionContext {
		BPMNDiagram bpmn = factory.createBPMNDiagram();
		Map<Pool,Pool> roleMap = new HashMap<Pool,Pool>();
		Map<Interaction,List<Node>> senderMap = new HashMap<Interaction, List<Node>>();
		Map<Interaction,List<Node>> receiverMap = new HashMap<Interaction, List<Node>>();
		Map<Node,Interaction> reverseMap = new HashMap<Node, Interaction>();
		
		public void addSender(Interaction i, Node n) {
			List<Node> nodes = senderMap.get(i);
			if (nodes == null) {
				nodes = new ArrayList<Node>();
				senderMap.put(i, nodes);
			}
			nodes.add(n);
			reverseMap.put(n, i);
		}
		public void addReceiver(Interaction i, Node n) {
			List<Node> nodes = receiverMap.get(i);
			if (nodes == null) {
				nodes = new ArrayList<Node>();
				receiverMap.put(i, nodes);
			}
			nodes.add(n);
			reverseMap.put(n, i);
		}
	}
	
	protected class DummyNode extends Node {
		@Override
		public StringBuilder getSerialization(BPMNSerialization serialization) {
			return null;
		}
	}
	
	public class ConversionException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	// ---- implementation ----
	
	// ---- (3) message flows ----

	protected void addMessageFlows(Container cont, ConversionContext c) {
		for (Node n: cont.getChildNodes()) {
			if (n instanceof Interaction) {
				Interaction i = (Interaction)n;
				
				for (Node sender: c.senderMap.get(i))
					for (Node receiver: c.receiverMap.get(i)) {
						MessageFlow newflow = factory.createMessageFlow();
						newflow.setSource(sender);
						newflow.setTarget(receiver);
						c.bpmn.getEdges().add(newflow);
					}
			} else if (n instanceof SubProcess) {
				addMessageFlows((SubProcess)n, c);
			}
		}
	}
	
	// ---- (1) duplication

	protected Pool addDuplicate(Pool role, ConversionContext c) {
		// create pool with lane
		Pool newpool = factory.createPool();
		newpool.setParent(c.bpmn);
		newpool.setId(role.getId());
		newpool.setLabel(role.getLabel());
//		newpool.setResourceId(role.getResourceId());
		
		Lane newlane = factory.createLane();
		newlane.setParent(newpool);
		
		// duplicate the process
		addProcessDuplicate(role, ibpmn, newlane, c, new HashMap<Node, Node>());
		return newpool;
	}

	protected void addProcessDuplicate(Pool role, Container src, Container target, ConversionContext c, Map<Node,Node> nodeMap) {
		for (Node n: src.getChildNodes()) {
			Node newnode = null;
			if (n instanceof Interaction) {
				newnode = handleInteraction(n, role, target, c);
			} else if (n instanceof SubProcess) {
				newnode = handleSubProcess(n, role, target, c, nodeMap);
			} else if (n instanceof XORDataBasedGateway) {
				newnode = handleXORDataBasedGateway(n, role, target, c);
			} else if (n instanceof Gateway) {
				newnode = n.getCopy();
			} else if (n instanceof Event) {
				newnode = n.getCopy();
			} else if (!(n instanceof Pool)) { // reduce everything else...
				newnode = new DummyNode();
			}
			if (newnode != null) {
				newnode.setParent(target);
				if (n.getId() != null)
					newnode.setId(n.getId()+role.getLabel());
				if (n.getLabel() != null)
					newnode.setLabel(n.getLabel());
//				if (n.getResourceId() != null)
//					newnode.setResourceId(n.getResourceId()+role.getLabel());
				nodeMap.put(n, newnode);
			}
		}
		if (src == ibpmn)
			for (Edge e: ibpmn.getEdges())
				if (e instanceof SequenceFlow) {
					SequenceFlow newflow = factory.createSequenceFlow();
					c.bpmn.getEdges().add(newflow);
					newflow.setConditionExpression(newflow.getConditionExpression());
					newflow.setId(e.getId()+role.getLabel());
					newflow.setName(e.getName());
//					newflow.setResourceId(e.getResourceId()+role.getLabel());
					newflow.setSource(nodeMap.get(e.getSource()));
					newflow.setTarget(nodeMap.get(e.getTarget()));
				}
	}

	protected Node handleInteraction(Node n, Pool role, Container target, ConversionContext c) {
		Interaction i = (Interaction)n;
		Node newnode = null;
		
		if (i.getReceiverRole() == role) {
			
			if (i instanceof StartEvent) {
				newnode = factory.createStartMessageEvent();
				
			} else if (i instanceof IntermediateEvent) {
				newnode = factory.createIntermediateMessageEvent();
				
			} else if (i instanceof EndEvent) {
				// create message send event + plain end event
				newnode = factory.createIntermediateMessageEvent();
				((IntermediateMessageEvent)newnode).setThrowing(true);
				addEndPlainEvent(newnode, target, c);
			}
			c.addReceiver(i, newnode);
			
		} else if (i.getSenderRole() == role) {
			
			if (i instanceof StartEvent) {
				// create plain start event + message send event
				newnode = factory.createIntermediateMessageEvent();
				((IntermediateMessageEvent)newnode).setThrowing(true);
				addStartPlainEvent(newnode, target, c);
				
			} else if (i instanceof IntermediateEvent) {
				newnode = factory.createIntermediateMessageEvent();
				((IntermediateMessageEvent)newnode).setThrowing(true);

			} else if (i instanceof EndEvent) {
				newnode = factory.createEndMessageEvent();
			}
			c.addSender(i, newnode);
			
		} else {
			newnode = new DummyNode();
			if (i instanceof StartEvent) {
				// create plain start event + dummy node
				addStartPlainEvent(newnode, target, c);
			} else if (i instanceof EndEvent) {
				// create dummy node + plain end event
				addEndPlainEvent(newnode, target, c);
			}
		}
		
		return newnode;
	}
	
	protected void addStartPlainEvent(Node newnode, Container target, ConversionContext c) {
		Node startevent = factory.createStartPlainEvent();
		startevent.setParent(target);
		SequenceFlow flow = factory.createSequenceFlow();
		c.bpmn.getEdges().add(flow);
		flow.setSource(startevent);
		flow.setTarget(newnode);
	}

	protected void addEndPlainEvent(Node newnode, Container target, ConversionContext c) {
		Node endevent = factory.createEndPlainEvent();
		endevent.setParent(target);
		SequenceFlow flow = factory.createSequenceFlow();
		c.bpmn.getEdges().add(flow);
		flow.setSource(newnode);
		flow.setTarget(endevent);
	}

	protected Node handleSubProcess(Node n, Pool role, Container target, ConversionContext c, Map<Node,Node> nodeMap) {
		SubProcess newp = factory.createSubProcess();
		addProcessDuplicate(role, (SubProcess)n, newp, c, nodeMap);
		return newp;
	}

	protected Node handleXORDataBasedGateway(Node n, Pool role, Container target, ConversionContext c) {
		Gateway newg;
		if (countSequenceFlows(n.getOutgoingEdges()) == 1 || isDecisionOwner((OwnedXORDataBasedGateway)n, role)) {
			newg = factory.createXORDataBasedGateway();
		} else {
			newg = factory.createXOREventBasedGateway();
		}
		return newg;
	}

	protected boolean isDecisionOwner(OwnedXORDataBasedGateway gateway, Pool role) {
		if (gateway.getOwners().contains(role))
			return true;
		return false;
	}
	
	// ---- (2) reduction rules ----

	protected void reduceDuplicate(Container cont, ConversionContext c) throws ConversionException {
		// get rid of dummy nodes and empty subprocesses
		List<Node> removeList = new ArrayList<Node>();
		boolean containsEventBasedGateways = false;
		for (Node n: cont.getChildNodes()) {
			if (n instanceof Pool || n instanceof Lane)
				reduceDuplicate((Container)n, c);
			else if (n instanceof DummyNode) {
				// (2.1)
				removeNode((DummyNode)n, removeList, c);
			} else if (n instanceof SubProcess) {
				reduceSubProcess((SubProcess)n, removeList, c);
			} else if (n instanceof XOREventBasedGateway) {
				containsEventBasedGateways = true;
			}
		}
		cont.getChildNodes().removeAll(removeList);

		// (2.3) TODO get rid of unnecessary gateways
		removeUnnecessaryGateways(cont, c);
		
		// (2.4)
		if (containsEventBasedGateways)
			handleEventBasedGateways(cont, c);
	}

	protected void reduceSubProcess(SubProcess sub, List<Node> removeList, ConversionContext c) throws ConversionException {
		reduceDuplicate(sub, c);
		
		// TODO watch out for attached intermediate events!
		
		// (2.2) reduce empty subprocess
		if (containsTrivialStructure(sub)) {
			removeNode(sub, removeList, c);
		}
	}

	// ---- (2.1) dummy nodes / nodes removal in general ----
	
	protected void removeNode(Node n, List<Node> removeList, ConversionContext c) {
		Edge outgoing = n.getOutgoingEdges().get(0);
		n.getIncomingEdges().get(0).setTarget(outgoing.getTarget());
		outgoing.setSource(null);
		outgoing.setTarget(null);
		c.bpmn.getEdges().remove(outgoing);
		if (removeList != null)
			removeList.add(n);
	}
	
	// ---- (2.2) subprocesses ----
	
	protected boolean containsTrivialStructure(SubProcess sub) {
		for (Node n: sub.getChildNodes()) {
			if (!(n instanceof StartPlainEvent || n instanceof EndPlainEvent))
				return false;
		}
		return true;
	}

	// ---- (2.3) event-based gateways ----
	
	protected void removeUnnecessaryGateways(Container cont, ConversionContext c) {
		List<Gateway> candidateList = new ArrayList<Gateway>();
		for (Node n: cont.getChildNodes()) 
			if (n instanceof Gateway && isCandidateGateway(n))
				candidateList.add((Gateway)n);
		if (candidateList.size() == 0) return;
		
		// TODO identify SESE regions
	}

	// ---- (2.4) event-based gateways ----
	
	protected boolean isCandidateGateway(Node n) {
		// TODO Auto-generated method stub
		return false;
	}

	protected void handleEventBasedGateways(Container cont, ConversionContext c) throws ConversionException {
		boolean more = true; // this strange implementation is needed as the list is modified
		while (more) {
			more = false;
			for (Node n: cont.getChildNodes())
				if (n instanceof XOREventBasedGateway) {
					more |= handleEventBasedGateway((XOREventBasedGateway)n, cont, c);
					if (more)
						break;
				}
		}				
	}

	protected boolean handleEventBasedGateway(XOREventBasedGateway g, Container cont, ConversionContext c) throws ConversionException {
		for (Edge e: g.getOutgoingEdges()) {
			if (e instanceof SequenceFlow) {
				Node n = (Node)e.getTarget();
				
				if (n instanceof IntermediateEvent) {
					// case (4) from the paper
					if (n instanceof IntermediateMessageEvent && ((IntermediateMessageEvent)n).isThrowing()) {
						throw new ConversionException();
					}
					// if catching, then we don't need to do anything
					// (all other possible events are of catching nature (timer, message))
					
				} else if (n instanceof EndEvent) {
					if (n instanceof EndPlainEvent) {
						// remove node (case (5)) 
						// Nota bene: the partner will not be aware of the conversion end!
						apply5(e, cont, c);
						return true;
					} else {
						// this cannot happen, as no other end event type is produced during the conversion
						throw new ConversionException();
					}
					
				} else if (n instanceof XORDataBasedGateway) {
					if (countSequenceFlows(n.getOutgoingEdges()) == 1) {
						// event-based gateway followed by XOR merge leads to duplication
						// case (1a)
						apply1a2a(g, e, cont, c);
						return true;
					} else if (countSequenceFlows(n.getIncomingEdges()) == 1) {
						// case (1b)
						apply1b(g, e, cont, c);
					} else {
						throw new ConversionException();
					}
					
				} else if (n instanceof XOREventBasedGateway) {
					if (countSequenceFlows(n.getIncomingEdges()) > 1 && countSequenceFlows(n.getOutgoingEdges()) == 1) {
						apply1a2a(g, e, cont, c);
					} else if (countSequenceFlows(n.getIncomingEdges()) == 1) {
						apply2b(g, e, cont, c);
					} else {
						apply2c(g, e, cont, c);
					}
					return true;
					
				} else if (n instanceof ANDGateway) {
					if (countSequenceFlows(n.getIncomingEdges()) > 1) {
						// AND-join: not allowed! (BPMN does not support non-free-choice-ness)
						// case (3a)
						throw new ConversionException();
					} else {
						apply3b(g, e, cont, c);
						return true;
					}
				}
			}
		}
		return false;
	}

	protected int countSequenceFlows(List<Edge> edges) {
		int count = 0;
		for (Edge e: edges)
			if (e instanceof SequenceFlow)
				count++;
		return count;
	}

	// precondition: count(outgoing sequence flows) = 1
	protected void apply1a2a(XOREventBasedGateway g, Edge g_out, Container parent, ConversionContext c) {
//		Node n = (Node)g_out.getTarget();
//
//		for (Edge e: n.getOutgoingEdges())
//			if (e instanceof SequenceFlow) {
//				Node m = (Node)e.getTarget();
//				Node newm = getCopy(m, parent, c);
//				
//				SequenceFlow new_n_out = factory.createSequenceFlow();
//				c.bpmn.getEdges().add(new_n_out);
//				new_n_out.setSource(g);
//				new_n_out.setTarget(m);
//				
//				for (Edge m_out: m.getOutgoingEdges()) {
//					if (m_out instanceof SequenceFlow) {
//						Gateway newg = factory.createXORDataBasedGateway();
//						newg.setParent(parent);
//						
//						SequenceFlow newflow = factory.createSequenceFlow();
//						c.bpmn.getEdges().add(newflow);
//						newflow.setSource(newg);
//						newflow.setTarget(m_out.getTarget());
//						
//						m_out.setTarget(newg);
//						
//						SequenceFlow new_m_out = factory.createSequenceFlow();
//						c.bpmn.getEdges().add(new_m_out);
//						new_m_out.setSource(newm);
//						new_m_out.setTarget(newg);
//					}
//				}
//			}
//		g_out.setSource(null);
//		g_out.setTarget(null);
//		c.bpmn.getEdges().remove(g_out);
//		
//		if (countSequenceFlows(n.getIncomingEdges()) == 1 && countM == 1) {
//			removeNode(n, null, c);
//			n.setParent(null);
//		}
	}

	private void apply1b(XOREventBasedGateway g, Edge e, Container cont, ConversionContext c) {
//		// TODO Auto-generated method stub
//		
	}

	private void apply2c(XOREventBasedGateway g, Edge e, Container cont, ConversionContext c) {
//		// TODO Auto-generated method stub
//		
	}

	private void apply5(Edge e, Container cont, ConversionContext c) {
//		// TODO Auto-generated method stub
//		
	}

	protected Node getCopy(Node m, Container parent, ConversionContext c) {
		Node newm = m.getCopy();
		newm.setParent(parent);
		Interaction i = c.reverseMap.get(m);
		if (i != null) {
			if (m instanceof IntermediateMessageEvent && !((IntermediateMessageEvent)m).isThrowing()) {
				c.addReceiver(i, newm);
			} else {
				c.addSender(i, newm);
			}
		}
		return newm;
	}

	protected void apply2b(Gateway g, Edge g_out, Container cont, ConversionContext c) {
		Node n = (Node)g_out.getTarget();
		// rearrange edges
		for (Edge e: n.getOutgoingEdges())
			if (e instanceof SequenceFlow)
				e.setSource(g);
		g_out.setSource(null);
		g_out.setTarget(null);
		c.bpmn.getEdges().remove(g_out);
		// remove the second gateway
		n.setParent(null);
	}

	protected void apply3b(XOREventBasedGateway g, Edge g_out, Container parent, ConversionContext c) {
		Node n = (Node)g_out.getTarget();
		int countM = 0;
		for (Edge n_out: n.getOutgoingEdges())
			if (n_out instanceof SequenceFlow) {
				Node m = (Node)n_out.getTarget();
				countM++;
				
				for (Edge m_out: m.getOutgoingEdges())
					if (m_out instanceof SequenceFlow) {
						XORDataBasedGateway newg = factory.createXORDataBasedGateway();
						newg.setParent(parent);
						
						SequenceFlow newflow = factory.createSequenceFlow();
						c.bpmn.getEdges().add(newflow);
						newflow.setSource(newg);
						newflow.setTarget(m_out.getTarget());
						
						m_out.setTarget(newg);
					}
			}
		
		for (Edge n_out: n.getOutgoingEdges())
			if (n_out instanceof SequenceFlow) {
				Node m = (Node)n_out.getTarget();
				
				n_out.setSource(g);

				SequenceFlow newflow = factory.createSequenceFlow();
				c.bpmn.getEdges().add(newflow);
				newflow.setSource(m);
				
				Node newAND = null;
				if (countM > 2) {
					newAND = factory.createANDGateway();
					newAND.setParent(parent);
					newflow.setTarget(newAND);
				}

				for (Edge n_out_prime: n.getOutgoingEdges())
					if (n_out_prime instanceof SequenceFlow && n_out != n_out_prime) {
						Node k = (Node)n_out_prime.getTarget();
						
						Node k_copy = k.getCopy();
						k_copy.setParent(parent);

						if (countM <= 2) {
							newflow.setTarget(k_copy);
						} else {
							SequenceFlow newflow3 = factory.createSequenceFlow();
							c.bpmn.getEdges().add(newflow3);
							newflow3.setSource(newAND);
							newflow3.setTarget(k_copy);
						}
						
						for (Edge k_out: k.getOutgoingEdges())
							if (k_out instanceof SequenceFlow) {
								SequenceFlow newflow2 = factory.createSequenceFlow();
								c.bpmn.getEdges().add(newflow2);
								newflow2.setSource(k_copy);
								newflow2.setTarget(k_out.getTarget());
							}
					}
			}
		
		g_out.setSource(null);
		g_out.setTarget(null);
		c.bpmn.getEdges().remove(g_out);
				
		n.setParent(null);
	}

	// TODO inclusion as servlet / plugin
	// TODO implement checker for ensuring constraints
	// TODO write test case for XOR-gateway
	// TODO handling subprocesses
	// TODO reduction for empty subprocesses
	// TODO reduction for unnecessary gateways
	// TODO reduction rule for event-based gateways
	// TODO display of generated BPMN
	// TODO implement preprocessor for expanding macros

}


