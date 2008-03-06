package de.hpi.nunet.instanceisolation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hpi.nunet.FlowRelationship;
import de.hpi.nunet.InterconnectionModel;
import de.hpi.nunet.Node;
import de.hpi.nunet.NuNet;
import de.hpi.nunet.NuNetFactory;
import de.hpi.nunet.Place;
import de.hpi.nunet.ProcessModel;
import de.hpi.nunet.Token;
import de.hpi.nunet.Transition;

public class ConversationDuplicator {
	
	private NuNetFactory factory;
	private InterconnectionModel model;

	public ConversationDuplicator(InterconnectionModel model) {
		this.model = model;
		this.factory = NuNetFactory.eINSTANCE;
	}

	public InterconnectionModel getDuplicatedModel(List<Transition> competitionTransitions) {
		competitionTransitions.clear();
		InterconnectionModel newmodel = factory.createInterconnectionModel();
		Map map = new HashMap();
		
		copyProcessModels(newmodel, map);
		
		addCommunicationPlaces(newmodel, map);
		addInternalPlacesAndTransitions(newmodel, map);
		addInternalPlacesAndTransitions(newmodel, map);
		
		addCompetitionTransitions(newmodel, map, competitionTransitions);
		
		return newmodel;
	}

	private void copyProcessModels(InterconnectionModel newmodel, Map map) {
		for (Iterator<ProcessModel> iter=model.getProcessModels().iterator(); iter.hasNext(); ) {
			ProcessModel pm = iter.next();
			ProcessModel newpm = factory.createProcessModel();
			newmodel.getProcessModels().add(newpm);
			newpm.setName(pm.getName());
			map.put(pm, newpm);
		}
	}

	private void addCommunicationPlaces(InterconnectionModel newmodel, Map map) {
		for (Iterator<Place> iter = model.getPlaces().iterator(); iter.hasNext(); ) {
			Place p = iter.next();
			if (p.isCommunicationPlace()) {
				Place newp = factory.createPlace();
				newmodel.getPlaces().add(newp);
				newp.setLabel(p.getLabel());
				map.put(p, newp);
			}
		}
	}

	private void addInternalPlacesAndTransitions(InterconnectionModel newmodel, Map map) {
		for (Iterator<Place> iter = model.getPlaces().iterator(); iter.hasNext(); ) {
			Place p = iter.next();
			if (p.isInternalPlace()) {
				Place newp = factory.createPlace();
				newmodel.getPlaces().add(newp);
				newp.setLabel(p.getLabel());
				newp.setProcessModel((ProcessModel)map.get(p.getProcessModel()));
				map.put(p, newp);
				for (Iterator<Token> iter2=model.getInitialMarking().getTokens(p).iterator(); iter2.hasNext(); ) {
					newmodel.getInitialMarking().getTokens(newp).add(copyToken(iter2.next()));
				}
			}
		}
		for (Iterator<Transition> iter = model.getTransitions().iterator(); iter.hasNext(); ) {
			Transition t = iter.next();
			Transition newt = factory.createTransition();
			newmodel.getTransitions().add(newt);
			newt.setLabel(t.getLabel());
			newt.setProcessModel((ProcessModel)map.get(t.getProcessModel()));
			map.put(t, newt);
		}
		for (Iterator<FlowRelationship> iter = model.getFlowRelationships().iterator(); iter.hasNext(); ) {
			FlowRelationship rel = iter.next();
			FlowRelationship newrel = factory.createFlowRelationship();
			newmodel.getFlowRelationships().add(newrel);
			newrel.setSource((Node)map.get(rel.getSource()));
			newrel.setTarget((Node)map.get(rel.getTarget()));
			newrel.getVariables().addAll(rel.getVariables());
		}
	}

	private Token copyToken(Token token) {
		Token newtoken = factory.createToken();
		newtoken.getNames().addAll(token.getNames());
		return newtoken;
	}

	private void addCompetitionTransitions(InterconnectionModel newmodel, Map map, List<Transition> competitionTransitions) {
		
		Map<ProcessModel, Place> apmap = new HashMap(newmodel.getProcessModels().size()); 
		
		// add additional places
		for (Iterator<ProcessModel> iter=newmodel.getProcessModels().iterator(); iter.hasNext(); ) {
			ProcessModel pm = iter.next();
			Place newp = factory.createPlace();
			newmodel.getPlaces().add(newp);
			newp.setLabel("add"+pm.getName());
			newp.setProcessModel(pm);
			apmap.put(pm, newp);
		}
		
		// add flow relationships between send/receive transitions and additional places
		for (Iterator<Transition> iter=newmodel.getTransitions().iterator(); iter.hasNext(); ) {
			Transition t = iter.next();
			if (t.isCommunicationTransition()) {
				addFlowRelationship(newmodel, t, apmap.get(t.getProcessModel()));
				// empty variable list
			}
		}
		
		// add transitions
		for (Iterator<Place> iter=newmodel.getPlaces().iterator(); iter.hasNext(); ) {
			Place p = iter.next();
			if (p.isCommunicationPlace()) {
				for (int i=0; i<p.getOutgoingFlowRelationships().size(); i++) {
					for (int j=i+1; j<p.getOutgoingFlowRelationships().size(); j++) { // optimization: combinations only in one direction
						if (i==j) continue;

						FlowRelationship rel1 = p.getOutgoingFlowRelationships().get(i);
						FlowRelationship rel2 = p.getOutgoingFlowRelationships().get(j);
						if (rel1.getTarget().getProcessModel() != rel2.getTarget().getProcessModel()) continue;
						
						Transition newt = factory.createTransition();
						newmodel.getTransitions().add(newt);
						competitionTransitions.add(newt);
						newt.setLabel("comp"+rel1.getTarget().getLabel()+rel2.getTarget().getLabel());

						// from addition place to competition transition
						addFlowRelationship(newmodel, apmap.get(rel1.getTarget().getProcessModel()), newt);
						// empty variable list
						
						// copy connections targeting t1
						for (Iterator<FlowRelationship> iter2=rel1.getTarget().getIncomingFlowRelationships().iterator(); iter2.hasNext(); ) {
							FlowRelationship rel = iter2.next();
							FlowRelationship newrel = addFlowRelationship(newmodel, rel.getSource(), newt);
							newrel.getVariables().addAll(rel.getVariables());
						}

						// renaming of variables
						Map<String, String> varmap = createVarMap(rel1, rel2);
						
						// copy connections targeting t2 (=> consider renaming!)
						for (Iterator<FlowRelationship> iter2=rel2.getTarget().getIncomingFlowRelationships().iterator(); iter2.hasNext(); ) {
							FlowRelationship rel = iter2.next();
							if (rel == rel2) continue;
							FlowRelationship newrel = addFlowRelationship(newmodel, rel.getSource(), newt);
							for (Iterator<String> iter3=rel.getVariables().iterator(); iter3.hasNext(); )
								newrel.getVariables().add(varmap.get(iter3.next()));
						}
					}
				}
			}
		}
	}
	
	private FlowRelationship addFlowRelationship(NuNet net, Node source, Node target) {
		FlowRelationship newrel = factory.createFlowRelationship();
		net.getFlowRelationships().add(newrel);
		newrel.setSource(source);
		newrel.setTarget(target);
		return newrel;
	}

	// variables from target transition of rel1 will be kept
	// resulting map will contain rewrites for target transition of rel2
	private Map<String, String> createVarMap(FlowRelationship rel1, FlowRelationship rel2) {
		Map<String, String> map = new HashMap();
		
		Set<String> variables = new HashSet();
		for (Iterator<FlowRelationship> iter=rel1.getTarget().getIncomingFlowRelationships().iterator(); iter.hasNext(); ) {
			FlowRelationship rel = iter.next();
			variables.addAll(rel.getVariables());
		}
		
		// rewrite variables from rel2
		for (int i=0; i<rel1.getVariables().size(); i++) {
			String v1 = rel1.getVariables().get(i);
			String v2 = rel2.getVariables().get(i);
			map.put(v2, v1); 
		}
		
		// rewrite remaining variables targeting t2
		for (Iterator<FlowRelationship> iter=rel2.getTarget().getIncomingFlowRelationships().iterator(); iter.hasNext(); ) {
			FlowRelationship rel = iter.next();
			if (rel == rel2) continue;
			for (Iterator<String> iter2=rel.getVariables().iterator(); iter2.hasNext(); ) {
				String v = iter2.next();
				if (!map.keySet().contains(v)) {
					if (!variables.contains(v))
						map.put(v, v);
					else
						map.put(v, v+"#");
				}
			}
		}
		
		return map;
	}

}
