/***************************************
 * Copyright (c) Intalio, Inc 2010
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 ****************************************/

package com.intalio.bpmn2.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.AdHocOrdering;
import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.Artifact;
import org.eclipse.bpmn2.Assignment;
import org.eclipse.bpmn2.Association;
import org.eclipse.bpmn2.Auditing;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.Bpmn2Factory;
import org.eclipse.bpmn2.BusinessRuleTask;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.Category;
import org.eclipse.bpmn2.CategoryValue;
import org.eclipse.bpmn2.CompensateEventDefinition;
import org.eclipse.bpmn2.ConditionalEventDefinition;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.DataStore;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Documentation;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.Error;
import org.eclipse.bpmn2.ErrorEventDefinition;
import org.eclipse.bpmn2.Escalation;
import org.eclipse.bpmn2.EscalationEventDefinition;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.FlowNode;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Gateway;
import org.eclipse.bpmn2.GatewayDirection;
import org.eclipse.bpmn2.GlobalTask;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.Import;
import org.eclipse.bpmn2.InclusiveGateway;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.Interface;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.LaneSet;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.Monitoring;
import org.eclipse.bpmn2.MultiInstanceLoopCharacteristics;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.OutputSet;
import org.eclipse.bpmn2.PotentialOwner;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ProcessType;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.ResourceAssignmentExpression;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.ScriptTask;
import org.eclipse.bpmn2.SequenceFlow;
import org.eclipse.bpmn2.ServiceTask;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.SignalEventDefinition;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Task;
import org.eclipse.bpmn2.TextAnnotation;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.TimerEventDefinition;
import org.eclipse.bpmn2.UserTask;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.di.BpmnDiFactory;
import org.eclipse.bpmn2.util.Bpmn2Resource;
import org.eclipse.dd.dc.Bounds;
import org.eclipse.dd.dc.DcFactory;
import org.eclipse.dd.dc.Point;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Internal;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl.SimpleFeatureMapEntry;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jbpm.bpmn2.emfextmodel.EmfextmodelFactory;
import org.jbpm.bpmn2.emfextmodel.EmfextmodelPackage;
import org.jbpm.bpmn2.emfextmodel.GlobalType;
import org.jbpm.bpmn2.emfextmodel.ImportType;
import org.jbpm.bpmn2.emfextmodel.OnEntryScriptType;
import org.jbpm.bpmn2.emfextmodel.OnExitScriptType;
import org.jbpm.bpmn2.emfextmodel.impl.EmfextmodelPackageImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.intalio.bpmn2.BpmnMarshallerHelper;
import com.intalio.bpmn2.resource.JBPMBpmn2ResourceFactoryImpl;

/**
 * @author Antoine Toulme
 * @author Tihomir Surdilovic
 * 
 *         an unmarshaller to transform JSON into BPMN 2.0 elements.
 * 
 */
public class Bpmn2JsonUnmarshaller {

    // a list of the objects created, kept in memory with their original id for
    // fast lookup.
    private Map<Object, String> _objMap = new HashMap<Object, String>();
    
    private Map<String, Object> _idMap = new HashMap<String, Object>();

    // the collection of outgoing ids.
    // we reconnect the edges with the shapes as a last step of the construction
    // of our graph from json, as we miss elements before.
    private Map<Object, List<String>> _outgoingFlows = new HashMap<Object, List<String>>();
    private Set<String> _sequenceFlowTargets = new HashSet<String>();
    private Map<String, Bounds> _bounds = new HashMap<String, Bounds>();
    private Map<String, List<Point>> _dockers = new HashMap<String, List<Point>>();
    private List<Lane> _lanes = new ArrayList<Lane>();
    private List<Artifact> _artifacts = new ArrayList<Artifact>();
    private Map<String, ItemDefinition> _subprocessItemDefs = new HashMap<String, ItemDefinition>();
    private List<BpmnMarshallerHelper> _helpers;

    private Bpmn2Resource _currentResource;
    
    public Bpmn2JsonUnmarshaller() {
        _helpers = new ArrayList<BpmnMarshallerHelper>();
        EmfextmodelPackageImpl.init();
        // load the helpers to place them in field
        if (getClass().getClassLoader() instanceof BundleReference) {
            BundleContext context = ((BundleReference) getClass().getClassLoader()).
                getBundle().getBundleContext();
            try {
                ServiceReference[] refs = context.getAllServiceReferences(
                        BpmnMarshallerHelper.class.getName(), null);
                for (ServiceReference ref : refs) {
                    BpmnMarshallerHelper helper = (BpmnMarshallerHelper) context.getService(ref);
                    _helpers.add(helper);
                }
            } catch (InvalidSyntaxException e) {
            }
            
        }
    }

    public Bpmn2Resource unmarshall(String json, String preProcessingData) throws JsonParseException, IOException {
        return unmarshall(new JsonFactory().createJsonParser(json), preProcessingData);
    }

    public Bpmn2Resource unmarshall(File file, String preProcessingData) throws JsonParseException, IOException {
        return unmarshall(new JsonFactory().createJsonParser(file), preProcessingData);
    }

    /**
     * Start unmarshalling using the parser.
     * @param parser
     * @return the root element of a bpmn2 document.
     * @throws JsonParseException
     * @throws IOException
     */
    private Bpmn2Resource unmarshall(JsonParser parser, String preProcessingData) throws JsonParseException, IOException {
        try {
            parser.nextToken(); // open the object
            ResourceSet rSet = new ResourceSetImpl();
            rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("bpmn2",
                    new JBPMBpmn2ResourceFactoryImpl());
            Bpmn2Resource bpmn2 = (Bpmn2Resource) rSet.createResource(URI.createURI("virtual.bpmn2"));
            rSet.getResources().add(bpmn2);
            _currentResource = bpmn2;
            // do the unmarshalling now:
            Definitions def = (Definitions) unmarshallItem(parser, preProcessingData);
            revisitServiceTasks(def);
            revisitMessages(def);
            revisitCatchEvents(def);
            revisitThrowEvents(def);
            revisitLanes(def);
            revisitSubProcessItemDefs(def);
            revisitArtifacts(def);
            revisitGroups(def);
            reviseTaskAssociations(def);
            reconnectFlows();
            revisitGateways(def);
            revisitCatchEventsConvertToBoundary(def);
            revisitDataObjects(def);
            createDiagram(def);
            updateIDs(def);
            // return def;
            _currentResource.getContents().add(def);
            return _currentResource;
        } finally {
            parser.close();
            _objMap.clear();
            _idMap.clear();
            _outgoingFlows.clear();
            _sequenceFlowTargets.clear();
            _bounds.clear();
            _currentResource = null;
        }
    }
    
    public void revisitSubProcessItemDefs(Definitions def) {
    	Iterator<String> iter =  _subprocessItemDefs.keySet().iterator();
    	while(iter.hasNext()) {
    		String key = iter.next();
    		def.getRootElements().add(_subprocessItemDefs.get(key));
    	}
    	_subprocessItemDefs.clear();
    }
    
    public void updateIDs(Definitions def) {
    	// data object id update
    	List<RootElement> rootElements =  def.getRootElements();
        for(RootElement root : rootElements) {
        	if(root instanceof Process) {
        		Process process = (Process) root;
        		List<FlowElement> flowElements = process.getFlowElements();
	            for(FlowElement fe : flowElements) {
	            	if(fe instanceof DataObject) {
	            		DataObject da = (DataObject) fe;
	            		if(da.getName() != null) {
	                    	da.setId(da.getName());
	                    }
	            	}
	            }
        	}
        }
    }
    
    public void revisitDataObjects(Definitions def) {
    	List<RootElement> rootElements =  def.getRootElements();
    	List<ItemDefinition> itemDefinitionsToAdd = new ArrayList<ItemDefinition>();
        for(RootElement root : rootElements) {
        	if(root instanceof Process) {
        		Process process = (Process) root;
        		List<FlowElement> flowElements = process.getFlowElements();
	            for(FlowElement fe : flowElements) {
	            	if(fe instanceof DataObject) {
	            		DataObject da = (DataObject) fe;
	            		ItemDefinition itemdef =  Bpmn2Factory.eINSTANCE.createItemDefinition();
	            		itemdef.setId("_" + da.getId() + "Item");
	            		Iterator<FeatureMap.Entry> iter = da.getAnyAttribute().iterator();
	                    while(iter.hasNext()) {
	                        FeatureMap.Entry entry = iter.next();
	                        if(entry.getEStructuralFeature().getName().equals("datype")) {
	                        	itemdef.setStructureRef((String) entry.getValue());
	                        }
	                    }
	                    da.setItemSubjectRef(itemdef);
	                    itemDefinitionsToAdd.add(itemdef);
	            	}
	            }
        	}
        }
        
        for(ItemDefinition itemDef : itemDefinitionsToAdd) {
        	def.getRootElements().add(itemDef);
        }
        
        for(RootElement root : rootElements) {
        	if(root instanceof Process) {
	        	Process process = (Process) root;
	            List<Artifact> artifactElements = process.getArtifacts();
	            for(Artifact af : artifactElements) {
	            	if(af instanceof Association) {
	            		Association as = (Association) af;
	            		if(as.getSourceRef() != null && as.getSourceRef() instanceof DataObject 
	            				&& as.getTargetRef() != null && (as.getTargetRef() instanceof Task || as.getTargetRef() instanceof ThrowEvent)) {
	            			DataObject da = (DataObject) as.getSourceRef();
	            			if(as.getTargetRef() instanceof Task) {
	            				Task task = (Task) as.getTargetRef();
	            				if(task.getIoSpecification() == null) {
	            	                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
	            	                task.setIoSpecification(iospec);
	            	            }
	            				if(task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
	            	            	InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
	            	                task.getIoSpecification().getInputSets().add(inset);
	            	            }
	            				InputSet inSet = task.getIoSpecification().getInputSets().get(0);
	            				boolean foundDataInput = false;
	            				for(DataInput dataInput : inSet.getDataInputRefs()) {
	            					if(dataInput.getId().equals(task.getId() + "_" + da.getId() + "Input")) {
	            						foundDataInput = true;
	            					}
	            				}
	            				if(!foundDataInput) {
		            	        	DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
		            	            d.setId(task.getId() + "_" + da.getId() + "Input");
		            	            d.setName(da.getId() + "Input");
		            	            task.getIoSpecification().getDataInputs().add(d);
		            	            task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
		            	        	
		            	        	DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
		            	        	dia.setTargetRef(d);
		            	        	dia.getSourceRef().add(da);
		            	            task.getDataInputAssociations().add(dia);
	            				}
	            			} else if(as.getTargetRef() instanceof ThrowEvent) {
	            				ThrowEvent te = (ThrowEvent) as.getTargetRef();
	            				// update throw event data input and add data input association
	            				boolean foundDataInput = false;
	            				List<DataInput> dataInputs = te.getDataInputs();
	            				for(DataInput din : dataInputs) {
	            					if(din.getId().equals(te.getId() + "_" + da.getId() + "Input")) {
	            						foundDataInput = true;
	            					}
	            				}
	            				if(!foundDataInput) {
	            					DataInput datain = Bpmn2Factory.eINSTANCE.createDataInput();
	            					datain.setId(te.getId() + "_" + da.getId() + "Input");
	            					datain.setName(da.getId() + "Input");
	            					te.getDataInputs().add(datain);
	            					
	            					if(te.getInputSet() == null) {
		            	            	InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
		            	            	te.setInputSet(inset);
		            	            }
	            					te.getInputSet().getDataInputRefs().add(datain);
	            					
	            					DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
	            					dia.setTargetRef(datain);
	            					dia.getSourceRef().add(da);
	            					te.getDataInputAssociation().add(dia);
	            				}
	            			}
	            		} 
	            		if(as.getTargetRef() != null && as.getTargetRef() instanceof DataObject 
	            				&& as.getSourceRef() != null && (as.getSourceRef() instanceof Task || as.getSourceRef() instanceof CatchEvent)) {
	            			DataObject da = (DataObject) as.getTargetRef();
	            			if(as.getSourceRef() instanceof Task) {
	            				Task task = (Task) as.getSourceRef();
	            				if(task.getIoSpecification() == null) {
	            	                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
	            	                task.setIoSpecification(iospec);
	            	            }
	            				
	            				if(task.getIoSpecification().getOutputSets() == null || task.getIoSpecification().getOutputSets().size() < 1) {
	            	            	OutputSet outSet = Bpmn2Factory.eINSTANCE.createOutputSet();
	            	                task.getIoSpecification().getOutputSets().add(outSet);
	            	            }
	            				
	            				boolean foundDataOutput = false;
	            				OutputSet outSet = task.getIoSpecification().getOutputSets().get(0);
	            				for(DataOutput dataOut : outSet.getDataOutputRefs()) {
	            					if(dataOut.getId().equals(task.getId() + "_" + da.getId() + "Output")) {
	            						foundDataOutput = true;
	            					}
	            				}
	            				
	            				if(!foundDataOutput) {
		            	        	DataOutput d = Bpmn2Factory.eINSTANCE.createDataOutput();
		            	            d.setId(task.getId() + "_" + da.getId() + "Output");
		            	            d.setName(da.getId());
		            	            task.getIoSpecification().getDataOutputs().add(d);
		            	            task.getIoSpecification().getOutputSets().get(0).getDataOutputRefs().add(d);
		            	        	
		            	        	DataOutputAssociation dia = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
		            	        	dia.setTargetRef(da);
		            	        	dia.getSourceRef().add(d);
		            	            task.getDataOutputAssociations().add(dia);
	            				}
	            			} else if(as.getSourceRef() instanceof CatchEvent) {
	            				CatchEvent ce = (CatchEvent) as.getSourceRef();
	            				// update catch event data output and add data output association
	            				boolean foundDataOutput = false;
	            				List<DataOutput> dataOutputs = ce.getDataOutputs();
	            				for(DataOutput dout : dataOutputs) {
	            					if(dout.getId().equals(ce.getId() + "_" + da.getId() + "Output")) {
	            						foundDataOutput = true;
	            					}
	            				}
	            				if(!foundDataOutput) {
	            					DataOutput dataout = Bpmn2Factory.eINSTANCE.createDataOutput();
	            					dataout.setId(ce.getId() + "_" + da.getId() + "Output");
	            					dataout.setName(da.getId() + "Output");
	            					ce.getDataOutputs().add(dataout);
	            					
	            					if(ce.getOutputSet() == null) {
		            	            	OutputSet outset = Bpmn2Factory.eINSTANCE.createOutputSet();
		            	            	ce.setOutputSet(outset);
		            	            }
	            					ce.getOutputSet().getDataOutputRefs().add(dataout);
	            					
	            					DataOutputAssociation dia = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
	            					dia.setTargetRef(da);
	            					dia.getSourceRef().add(dataout);
	            					ce.getDataOutputAssociation().add(dia);
	            				}
	            			}
	            		} 
	            	}
	            }
	        }
        }
    }
    
    public void reviseTaskAssociations(Definitions def) {
    	List<RootElement> rootElements =  def.getRootElements();
        for(RootElement root : rootElements) {
            if(root instanceof Process) {
                Process process = (Process) root;
                List<FlowElement> flowElements = process.getFlowElements();
                for(FlowElement fe : flowElements) {
                	if(fe instanceof Task) {
                		Task t = (Task) fe;
                		if(t.getDataInputAssociations() != null) {
                			List<DataInputAssociation> inputList = t.getDataInputAssociations();
                			if(inputList != null) {
                				for(DataInputAssociation input : inputList) {
                					List<ItemAwareElement> sourceRef = input.getSourceRef();
                					if(sourceRef != null) {
                						for(ItemAwareElement iae : sourceRef) {
                							String[] iaeParts = iae.getId().split( "\\." );
                							if(iaeParts.length > 1) {
                								FormalExpression dataInputTransformationExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                								dataInputTransformationExpression.setBody(iae.getId());
                								input.setTransformation(dataInputTransformationExpression);
                    		                    iae.setId(iaeParts[0]);
                							}
                						}
                					}
                				}
                			}
                		}
                		if(t.getDataOutputAssociations() != null) {
                			List<DataOutputAssociation> outputList = t.getDataOutputAssociations();
                			if(outputList != null) {
                				for(DataOutputAssociation output : outputList) {
                					ItemAwareElement targetEle = output.getTargetRef();
                					if(targetEle != null) {
                						String[] targetEleParts = targetEle.getId().split( "\\." );
                						if(targetEleParts.length > 1) {
                							FormalExpression dataOutputTransformationExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                							dataOutputTransformationExpression.setBody(targetEle.getId());
            								output.setTransformation(dataOutputTransformationExpression);
                		                    targetEle.setId(targetEleParts[0]);
            							}
                					}
                				}
                			}
                		}
                	}
                }
            }
        }
    }
    
    public void revisitLanes(Definitions def) {
        List<RootElement> rootElements =  def.getRootElements();
        for(RootElement root : rootElements) {
            if(root instanceof Process) {
                Process process = (Process) root;
                if((process.getLaneSets() == null || process.getLaneSets().size() < 1) && _lanes.size() > 0) {
                	LaneSet ls = Bpmn2Factory.eINSTANCE.createLaneSet();
                	for(Lane lane : _lanes) {
                		ls.getLanes().add(lane);
                		List<FlowNode> laneFlowNodes = lane.getFlowNodeRefs();
                		for(FlowNode fl : laneFlowNodes) {
                			process.getFlowElements().add(fl);
                		}
                	}
                	process.getLaneSets().add(ls);
                }
            }
        }
    }
    
    public void revisitArtifacts(Definitions def) {
    	List<RootElement> rootElements =  def.getRootElements();
        for(RootElement root : rootElements) {
            if(root instanceof Process) {
                Process process = (Process) root;
                for(Artifact a : _artifacts) {
                	process.getArtifacts().add(a);
                }
            }
        }
    }
    
    public void revisitGroups(Definitions def) {
    	List<RootElement> rootElements =  def.getRootElements();
    	Category defaultCat = Bpmn2Factory.eINSTANCE.createCategory();
    	defaultCat.setName("default");
        for(RootElement root : rootElements) {
        	 if(root instanceof Process) {
        		 Process process = (Process) root;
        		 List<Artifact> processArtifacts = process.getArtifacts();
        		 if(processArtifacts != null) {
        			 for(Artifact ar : processArtifacts) {
        				 if(ar instanceof Group) {
        					 Group group = (Group) ar;
        					 Iterator<FeatureMap.Entry> iter = group.getAnyAttribute().iterator();
                             while(iter.hasNext()) {
                                 FeatureMap.Entry entry = iter.next();
                                 if(entry.getEStructuralFeature().getName().equals("categoryval")) {
                                	 CategoryValue catval = Bpmn2Factory.eINSTANCE.createCategoryValue();
                                	 catval.setValue((String) entry.getValue());
                                	 defaultCat.getCategoryValue().add(catval);
                                	 group.setCategoryValueRef(catval);
                                 }
                             }
        				 }
        			 }
        		 }
        	 }
        }
        // only add category if it includes at least one categoryvalue
        if(defaultCat.getCategoryValue() != null && defaultCat.getCategoryValue().size() > 0) {
        	rootElements.add(defaultCat);
        }
    }
    
    /**
     * Updates event definitions for all throwing events.
     * @param def Definitions
     */
    public void revisitThrowEvents(Definitions def) {
        List<RootElement> rootElements =  def.getRootElements();
        List<Signal> toAddSignals = new ArrayList<Signal>();
        List<Error> toAddErrors = new ArrayList<Error>();
        List<Escalation> toAddEscalations = new ArrayList<Escalation>();
        List<Message> toAddMessages = new ArrayList<Message>();
        List<ItemDefinition> toAddItemDefinitions = new ArrayList<ItemDefinition>();
        for(RootElement root : rootElements) {
            if(root instanceof Process) {
                Process process = (Process) root;
                List<FlowElement> flowElements =  process.getFlowElements();
                for(FlowElement fe : flowElements) {
                    if(fe instanceof ThrowEvent) {
                        if(((ThrowEvent)fe).getEventDefinitions().size() > 0) {
                            EventDefinition ed = ((ThrowEvent)fe).getEventDefinitions().get(0);
                            if (ed instanceof SignalEventDefinition) {
                                Signal signal = Bpmn2Factory.eINSTANCE.createSignal();
                                Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                                while(iter.hasNext()) {
                                    FeatureMap.Entry entry = iter.next();
                                    if(entry.getEStructuralFeature().getName().equals("signalrefname")) {
                                        signal.setName((String) entry.getValue());
                                    }
                                }
                                toAddSignals.add(signal);
                                ((SignalEventDefinition) ed).setSignalRef(signal);
                            } else if(ed instanceof ErrorEventDefinition) {
                                Error err = Bpmn2Factory.eINSTANCE.createError();
                                Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                                while(iter.hasNext()) {
                                    FeatureMap.Entry entry = iter.next();
                                    if(entry.getEStructuralFeature().getName().equals("erefname")) {
                                        err.setId((String) entry.getValue());
                                        err.setErrorCode((String) entry.getValue());
                                    }
                                }
                                
                                toAddErrors.add(err);
                                ((ErrorEventDefinition) ed).setErrorRef(err);
                                
                            } else if(ed instanceof EscalationEventDefinition) {
                                Escalation escalation = Bpmn2Factory.eINSTANCE.createEscalation();
                                Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                                while(iter.hasNext()) {
                                    FeatureMap.Entry entry = iter.next();
                                    if(entry.getEStructuralFeature().getName().equals("esccode")) {
                                        escalation.setEscalationCode((String) entry.getValue());
                                    }
                                }
                                toAddEscalations.add(escalation);
                                ((EscalationEventDefinition) ed).setEscalationRef(escalation);
                            } else if(ed instanceof MessageEventDefinition) {
                                ItemDefinition idef = Bpmn2Factory.eINSTANCE.createItemDefinition();
                                Message msg = Bpmn2Factory.eINSTANCE.createMessage();
                                Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                                while(iter.hasNext()) {
                                    FeatureMap.Entry entry = iter.next();
                                    if(entry.getEStructuralFeature().getName().equals("msgref")) {
                                        msg.setId((String) entry.getValue());
                                        idef.setId((String) entry.getValue() + "Type");
                                    }
                                }
                                msg.setItemRef(idef);
                                ((MessageEventDefinition) ed).setMessageRef(msg);
                                toAddMessages.add(msg);
                                toAddItemDefinitions.add(idef);
                            } else if(ed instanceof CompensateEventDefinition) {
                                Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                                while(iter.hasNext()) {
                                    FeatureMap.Entry entry = iter.next();
                                    if(entry.getEStructuralFeature().getName().equals("actrefname")) {
                                        String activityNameRef = (String) entry.getValue();
                                        // we have to iterate again through all flow elements
                                        // in order to find our activity name
                                        List<RootElement> re =  def.getRootElements();
                                        for(RootElement r : re) {
                                            if(r instanceof Process) {
                                                Process p = (Process) root;
                                                List<FlowElement> fes =  p.getFlowElements();
                                                for(FlowElement f : fes) {
                                                    if(f instanceof Activity && ((Activity) f).getName().equals(activityNameRef)) {
                                                        ((CompensateEventDefinition) ed).setActivityRef((Activity)f);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for(Signal s : toAddSignals) {
            def.getRootElements().add(s);
        }
        for(Error er : toAddErrors) {
            def.getRootElements().add(er);
        }
        for(Escalation es : toAddEscalations) {
            def.getRootElements().add(es);
        }
        for(ItemDefinition idef : toAddItemDefinitions) {
            def.getRootElements().add(idef);
        }
        for(Message msg : toAddMessages) {
            def.getRootElements().add(msg);
        }
    }
    
    private void revisitCatchEventsConvertToBoundary(Definitions def) {
    	List<CatchEvent> catchEventsToRemove = new ArrayList<CatchEvent>();
    	List<BoundaryEvent> boundaryEventsToAdd = new ArrayList<BoundaryEvent>();
    	List<RootElement> rootElements =  def.getRootElements();
    	for(RootElement root : rootElements) {
    		if(root instanceof Process) {
    			Process process = (Process) root;
                List<FlowElement> flowElements =  process.getFlowElements();
                for(FlowElement fe : flowElements) {
                	if(fe instanceof CatchEvent) {
                		CatchEvent ce = (CatchEvent) fe;
                		// check if we have an outgoing connection to this catch event from an activity
                		 for (Entry<Object, List<String>> entry : _outgoingFlows.entrySet()) {
                			 for (String flowId : entry.getValue()) {
                				 if (entry.getKey() instanceof Activity && flowId.equals(ce.getId())) {
                					 BoundaryEvent be = Bpmn2Factory.eINSTANCE.createBoundaryEvent();
                					 if(ce.getDataOutputs() != null) {
                						 be.getDataOutputs().addAll(ce.getDataOutputs());
                					 }
                					 if(ce.getDataOutputAssociation() != null) {
                						 be.getDataOutputAssociation().addAll(ce.getDataOutputAssociation());
                					 }
                					 if(ce.getOutputSet() != null) {
                						 be.setOutputSet(ce.getOutputSet());
                					 }
                					 if(ce.getEventDefinitions() != null) {
                						 be.getEventDefinitions().addAll(ce.getEventDefinitions());
                					 }
                					 if(ce.getEventDefinitionRefs() != null) {
                						 be.getEventDefinitionRefs().addAll(ce.getEventDefinitionRefs());
                					 }
                					 if(ce.getProperties() != null) {
                						 be.getProperties().addAll(ce.getProperties());
                					 }
                					 if(ce.getAnyAttribute() != null) {
                						 be.getAnyAttribute().addAll(ce.getAnyAttribute());
                					 }
                					 if(ce.getOutgoing() != null) {
                						 be.getOutgoing().addAll(ce.getOutgoing());
                					 }
                					 if(ce.getIncoming() != null) {
                						 be.getIncoming().addAll(ce.getIncoming());
                					 }
                					 if(ce.getProperties() != null) {
                						 be.getProperties().addAll(ce.getProperties());
                					 }
                					 
                					 be.setName(ce.getName());
                					 be.setId(ce.getId());
                					 
                					 be.setAttachedToRef(((Activity)entry.getKey()));
                					 ((Activity)entry.getKey()).getBoundaryEventRefs().add(be);
                					 catchEventsToRemove.add(ce);
                					 boundaryEventsToAdd.add(be);
                				 }
                			 }
                		 }
                	}
                }
                if(boundaryEventsToAdd.size() > 0) {
            		for(BoundaryEvent be : boundaryEventsToAdd) {
            			process.getFlowElements().add(be);
            		}
            	}
                if(catchEventsToRemove.size() > 0) {
                	for(CatchEvent ce : catchEventsToRemove) {
                		process.getFlowElements().remove(ce);
                	}
                }
    		}
    	}
    }
    
    /**
     * Updates event definitions for all catch events.
     * @param def Definitions
     */
    public void revisitCatchEvents(Definitions def) {
        List<RootElement> rootElements =  def.getRootElements();
        List<Signal> toAddSignals = new ArrayList<Signal>();
        List<Error> toAddErrors = new ArrayList<Error>();
        List<Escalation> toAddEscalations = new ArrayList<Escalation>();
        List<Message> toAddMessages = new ArrayList<Message>();
        List<ItemDefinition> toAddItemDefinitions = new ArrayList<ItemDefinition>();
        for(RootElement root : rootElements) {
            if(root instanceof Process) {
                Process process = (Process) root;
                List<FlowElement> flowElements =  process.getFlowElements();
                for(FlowElement fe : flowElements) {
                    if(fe instanceof CatchEvent) {
                        if(((CatchEvent)fe).getEventDefinitions().size() > 0) {
                            EventDefinition ed = ((CatchEvent)fe).getEventDefinitions().get(0);
                            if (ed instanceof SignalEventDefinition) {
                                Signal signal = Bpmn2Factory.eINSTANCE.createSignal();
                                Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                                while(iter.hasNext()) {
                                    FeatureMap.Entry entry = iter.next();
                                    if(entry.getEStructuralFeature().getName().equals("signalrefname")) {
                                        signal.setName((String) entry.getValue());
                                    }
                                }
                                toAddSignals.add(signal);
                                ((SignalEventDefinition) ed).setSignalRef(signal);
                            } else if(ed instanceof ErrorEventDefinition) {
                                Error err = Bpmn2Factory.eINSTANCE.createError();
                                Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                                while(iter.hasNext()) {
                                    FeatureMap.Entry entry = iter.next();
                                    if(entry.getEStructuralFeature().getName().equals("erefname")) {
                                        err.setId((String) entry.getValue());
                                        err.setErrorCode((String) entry.getValue());
                                    }
                                }
                                
                                toAddErrors.add(err);
                                ((ErrorEventDefinition) ed).setErrorRef(err);
                                
                            } else if(ed instanceof EscalationEventDefinition) {
                                Escalation escalation = Bpmn2Factory.eINSTANCE.createEscalation();
                                Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                                while(iter.hasNext()) {
                                    FeatureMap.Entry entry = iter.next();
                                    if(entry.getEStructuralFeature().getName().equals("esccode")) {
                                        escalation.setEscalationCode((String) entry.getValue());
                                    }
                                }
                                toAddEscalations.add(escalation);
                                ((EscalationEventDefinition) ed).setEscalationRef(escalation);
                            } else if(ed instanceof MessageEventDefinition) {
                                ItemDefinition idef = Bpmn2Factory.eINSTANCE.createItemDefinition();
                                Message msg = Bpmn2Factory.eINSTANCE.createMessage();
                                Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                                while(iter.hasNext()) {
                                    FeatureMap.Entry entry = iter.next();
                                    if(entry.getEStructuralFeature().getName().equals("msgref")) {
                                        msg.setId((String) entry.getValue());
                                        idef.setId((String) entry.getValue() + "Type");
                                    }
                                }
                                msg.setItemRef(idef);
                                ((MessageEventDefinition) ed).setMessageRef(msg);
                                toAddMessages.add(msg);
                                toAddItemDefinitions.add(idef);
                            } else if(ed instanceof CompensateEventDefinition) {
                                Iterator<FeatureMap.Entry> iter = ed.getAnyAttribute().iterator();
                                while(iter.hasNext()) {
                                    FeatureMap.Entry entry = iter.next();
                                    if(entry.getEStructuralFeature().getName().equals("actrefname")) {
                                        String activityNameRef = (String) entry.getValue();
                                        // we have to iterate again through all flow elements
                                        // in order to find our activity name
                                        List<RootElement> re =  def.getRootElements();
                                        for(RootElement r : re) {
                                            if(r instanceof Process) {
                                                Process p = (Process) root;
                                                List<FlowElement> fes =  p.getFlowElements();
                                                for(FlowElement f : fes) {
                                                    if(f instanceof Activity && ((Activity) f).getName().equals(activityNameRef)) {
                                                        ((CompensateEventDefinition) ed).setActivityRef((Activity)f);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        for(Signal s : toAddSignals) {
            def.getRootElements().add(s);
        }
        for(Error er : toAddErrors) {
            def.getRootElements().add(er);
        }
        for(Escalation es : toAddEscalations) {
            def.getRootElements().add(es);
        }
        for(ItemDefinition idef : toAddItemDefinitions) {
            def.getRootElements().add(idef);
        }
        for(Message msg : toAddMessages) {
            def.getRootElements().add(msg);
        }
    }
    
    /**
     * Updates the gatewayDirection attributes of all gateways.
     * @param def
     */
    private void revisitGateways(Definitions def) {
        List<RootElement> rootElements =  def.getRootElements();
        for(RootElement root : rootElements) {
            if(root instanceof Process) {
                Process process = (Process) root;
                List<FlowElement> flowElements =  process.getFlowElements();
                for(FlowElement fe : flowElements) {
                    if(fe instanceof Gateway) {
                        Gateway gateway = (Gateway) fe;
                        int incoming = gateway.getIncoming() == null ? 0 : gateway.getIncoming().size();
                        int outgoing = gateway.getOutgoing() == null ? 0 : gateway.getOutgoing().size();
                        if (incoming <= 1 && outgoing > 1) {
                            gateway.setGatewayDirection(GatewayDirection.DIVERGING);
                        } else if (incoming > 1 && outgoing <= 1) {
                            gateway.setGatewayDirection(GatewayDirection.CONVERGING);
                        } else if (incoming > 1 && outgoing > 1) {
                            gateway.setGatewayDirection(GatewayDirection.MIXED);
                        } else if (incoming == 1 && outgoing == 1) { 
                            // this handles the 1:1 case of the diverging gateways
                            gateway.setGatewayDirection(GatewayDirection.DIVERGING);
                        } else {
                            gateway.setGatewayDirection(GatewayDirection.UNSPECIFIED);
                        }
                    }
                    if(fe instanceof InclusiveGateway) {
                    	Iterator<FeatureMap.Entry> iter = fe.getAnyAttribute().iterator();
                    	while(iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if(entry.getEStructuralFeature().getName().equals("dg")) {
                            	for(FlowElement feg : flowElements) {
                            		if(feg instanceof SequenceFlow && feg.getId().equals((String) entry.getValue())) {
                            			((InclusiveGateway) fe).setDefault((SequenceFlow) feg);
                            		}
                            	}
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void revisitServiceTasks(Definitions def) {
        List<RootElement> rootElements =  def.getRootElements();
        List<Interface> toAddInterfaces = new ArrayList<Interface>();
        List<Message> toAddMessages = new ArrayList<Message>();
        for(RootElement root : rootElements) {
            if(root instanceof Process) {
                Process process = (Process) root;
                List<FlowElement> flowElements =  process.getFlowElements();
                for(FlowElement fe : flowElements) {
                    if(fe instanceof ServiceTask) {
                        Iterator<FeatureMap.Entry> iter = fe.getAnyAttribute().iterator();
                        String serviceInterface = null;
                        String serviceOperation = null;
                        while(iter.hasNext()) {
                            FeatureMap.Entry entry = iter.next();
                            if(entry.getEStructuralFeature().getName().equals("servicetaskinterface")) {
                                serviceInterface = (String) entry.getValue();
                            }
                            if(entry.getEStructuralFeature().getName().equals("servicetaskoperation")) {
                                serviceOperation = (String) entry.getValue();
                            }
                        }
                        Interface newInterface = Bpmn2Factory.eINSTANCE.createInterface();
                        if(serviceInterface != null) {
                            newInterface.setName(serviceInterface);
                            newInterface.setId(fe.getId() + "_ServiceInterface");
                        }
                        if(serviceOperation != null) {
                            Operation oper = Bpmn2Factory.eINSTANCE.createOperation();
                            oper.setId(fe.getId() + "_ServiceOperation");
                            oper.setName(serviceOperation);
                            
                            Message message = Bpmn2Factory.eINSTANCE.createMessage();
                            message.setId(fe.getId() + "_InMessage");
                            toAddMessages.add(message);
                            
                            oper.setInMessageRef(message);
                            newInterface.getOperations().add(oper);
                            ((ServiceTask) fe).setOperationRef(oper);
                        }
                        toAddInterfaces.add(newInterface);
                    }
                }
            }
        }
        for(Interface i : toAddInterfaces) {
            def.getRootElements().add(i);
        }
        for(Message m : toAddMessages) {
            def.getRootElements().add(m);
        }
    }
    
    /**
     * Revisit message to set their item ref to a item definition
     * @param def Definitions
     */
    private void revisitMessages(Definitions def) {
        List<RootElement> rootElements =  def.getRootElements();
        List<ItemDefinition> toAddDefinitions = new ArrayList<ItemDefinition>();
        for(RootElement root : rootElements) {
            if(root instanceof Message) {
                // add item definition for messages
                ItemDefinition itemdef =  Bpmn2Factory.eINSTANCE.createItemDefinition();
                itemdef.setId(root.getId() + "Type");
                toAddDefinitions.add(itemdef);
                ((Message) root).setItemRef(itemdef);
            }
        }
        for(ItemDefinition id : toAddDefinitions) {
            def.getRootElements().add(id);
        }
    }
        
    /**
     * Reconnect the sequence flows and the flow nodes.
     * Done after the initial pass so that we have all the target information.
     */
    private void reconnectFlows() {
        // create the reverse id map:
        for (Entry<Object, List<String>> entry : _outgoingFlows.entrySet()) {
            for (String flowId : entry.getValue()) {
                if (entry.getKey() instanceof SequenceFlow) { // if it is a sequence flow, we can tell its targets
                    ((SequenceFlow) entry.getKey()).setTargetRef((FlowNode) _idMap.get(flowId));
                } else if (entry.getKey() instanceof Association) {
                    ((Association) entry.getKey()).setTargetRef((BaseElement) _idMap.get(flowId));
                } else { // if it is a node, we can map it to its outgoing sequence flows
                    if (_idMap.get(flowId) instanceof SequenceFlow) {
                        ((FlowNode) entry.getKey()).getOutgoing().add((SequenceFlow) _idMap.get(flowId));
                    } else if (_idMap.get(flowId) instanceof Association) {
                        ((Association) _idMap.get(flowId)).setSourceRef((BaseElement) entry.getKey());
                    }
                }
            }
        }
    }
    
    private void createSubProcessDiagram(BPMNPlane plane, FlowElement flowElement, BpmnDiFactory factory) {
		SubProcess sp = (SubProcess) flowElement;
		for(FlowElement subProcessFlowElement : sp.getFlowElements()) {
			if(subProcessFlowElement instanceof SubProcess) {
				Bounds spb = _bounds.get(subProcessFlowElement.getId());
				if (spb != null) {
					BPMNShape shape = factory.createBPMNShape();
					shape.setBpmnElement(subProcessFlowElement);
					shape.setBounds(spb);
					plane.getPlaneElement().add(shape);
				}
				createSubProcessDiagram(plane, subProcessFlowElement, factory);
			} else if (subProcessFlowElement instanceof FlowNode) {
				Bounds spb = _bounds.get(subProcessFlowElement.getId());
				if (spb != null) {
					BPMNShape shape = factory.createBPMNShape();
					shape.setBpmnElement(subProcessFlowElement);
					shape.setBounds(spb);
					plane.getPlaneElement().add(shape);
				}
			} else if (subProcessFlowElement instanceof SequenceFlow) {
				SequenceFlow sequenceFlow = (SequenceFlow) subProcessFlowElement;
				BPMNEdge edge = factory.createBPMNEdge();
				edge.setBpmnElement(subProcessFlowElement);
				DcFactory dcFactory = DcFactory.eINSTANCE;
				Point point = dcFactory.createPoint();
				if(sequenceFlow.getSourceRef() != null) {
					Bounds sourceBounds = _bounds.get(sequenceFlow.getSourceRef().getId());
					point.setX(sourceBounds.getX() + (sourceBounds.getWidth()/2));
					point.setY(sourceBounds.getY() + (sourceBounds.getHeight()/2));
				}
				edge.getWaypoint().add(point);
				List<Point> dockers = _dockers.get(sequenceFlow.getId());
				for (int i = 1; i < dockers.size() - 1; i++) {
					edge.getWaypoint().add(dockers.get(i));
				}
				point = dcFactory.createPoint();
				if(sequenceFlow.getTargetRef() != null) {
					Bounds targetBounds = _bounds.get(sequenceFlow.getTargetRef().getId());
					point.setX(targetBounds.getX() + (targetBounds.getWidth()/2));
					point.setY(targetBounds.getY() + (targetBounds.getHeight()/2));
				}
				edge.getWaypoint().add(point);
				plane.getPlaneElement().add(edge);
			}
		}
		for (Artifact artifact : sp.getArtifacts()) {
            if (artifact instanceof TextAnnotation || artifact instanceof Group) {
            	Bounds ba = _bounds.get(artifact.getId());
            	if (ba != null) {
            		BPMNShape shape = factory.createBPMNShape();
            		shape.setBpmnElement(artifact);
            		shape.setBounds(ba);
            		plane.getPlaneElement().add(shape);
            	}
            }
            if (artifact instanceof Association){
                Association association = (Association)artifact;
                BPMNEdge edge = factory.createBPMNEdge();
                edge.setBpmnElement(association);
                DcFactory dcFactory = DcFactory.eINSTANCE;
                Point point = dcFactory.createPoint();
                Bounds sourceBounds = _bounds.get(association.getSourceRef().getId());
                point.setX(sourceBounds.getX() + (sourceBounds.getWidth()/2));
                point.setY(sourceBounds.getY() + (sourceBounds.getHeight()/2));
                edge.getWaypoint().add(point);
                List<Point> dockers = _dockers.get(association.getId());
                for (int i = 1; i < dockers.size() - 1; i++) {
                        edge.getWaypoint().add(dockers.get(i));
                }
                point = dcFactory.createPoint();
                Bounds targetBounds = _bounds.get(association.getTargetRef().getId());
                point.setX(targetBounds.getX() + (targetBounds.getWidth()/2));
                point.setY(targetBounds.getY() + (targetBounds.getHeight()/2));
                edge.getWaypoint().add(point);
                plane.getPlaneElement().add(edge);
            }
        }
    }
    
    private void createDiagram(Definitions def) {
    	for (RootElement rootElement: def.getRootElements()) {
    		if (rootElement instanceof Process) {
    			Process process = (Process) rootElement;
        		BpmnDiFactory factory = BpmnDiFactory.eINSTANCE;
        		BPMNDiagram diagram = factory.createBPMNDiagram();
        		BPMNPlane plane = factory.createBPMNPlane();
        		plane.setBpmnElement(process);
        		diagram.setPlane(plane);
    			// first process flowNodes
        		for (FlowElement flowElement: process.getFlowElements()) {
        			if (flowElement instanceof FlowNode) {
        				Bounds b = _bounds.get(flowElement.getId());
        				if (b != null) {
        					BPMNShape shape = factory.createBPMNShape();
        					shape.setBpmnElement(flowElement);
        					shape.setBounds(b);
        					plane.getPlaneElement().add(shape);
        					if(flowElement instanceof BoundaryEvent) {
        						BPMNEdge edge = factory.createBPMNEdge();
        						edge.setBpmnElement(flowElement);
        						List<Point> dockers = _dockers.get(flowElement.getId());
    	    					for (int i = 0; i < dockers.size(); i++) {
    	    						edge.getWaypoint().add(dockers.get(i));
    	    					}
    	    					plane.getPlaneElement().add(edge);
        					}
        				}
        				// check if its a subprocess
        				if(flowElement instanceof SubProcess) {
        					createSubProcessDiagram(plane, flowElement, factory);
        				}
        			} else if(flowElement instanceof DataObject) {
        				Bounds b = _bounds.get(flowElement.getId());
        				if (b != null) {
        					BPMNShape shape = factory.createBPMNShape();
        					shape.setBpmnElement(flowElement);
        					shape.setBounds(b);
        					plane.getPlaneElement().add(shape);
        				}
        			}
        			else if (flowElement instanceof SequenceFlow) {
        				SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
        				BPMNEdge edge = factory.createBPMNEdge();
    					edge.setBpmnElement(flowElement);
    					DcFactory dcFactory = DcFactory.eINSTANCE;
    					Point point = dcFactory.createPoint();
    					if(sequenceFlow.getSourceRef() != null) {
    						Bounds sourceBounds = _bounds.get(sequenceFlow.getSourceRef().getId());
    						point.setX(sourceBounds.getX() + (sourceBounds.getWidth()/2));
    						point.setY(sourceBounds.getY() + (sourceBounds.getHeight()/2));
    					}
    					edge.getWaypoint().add(point);
    					List<Point> dockers = _dockers.get(sequenceFlow.getId());
    					for (int i = 1; i < dockers.size() - 1; i++) {
    						edge.getWaypoint().add(dockers.get(i));
    					}
    					point = dcFactory.createPoint();
    					if(sequenceFlow.getTargetRef() != null) {
    						Bounds targetBounds = _bounds.get(sequenceFlow.getTargetRef().getId());
    						point.setX(targetBounds.getX() + (targetBounds.getWidth()/2));
    						point.setY(targetBounds.getY() + (targetBounds.getHeight()/2));
    					}
    					edge.getWaypoint().add(point);
    					plane.getPlaneElement().add(edge);
        			}
        		}
        		// artifacts
                if (process.getArtifacts() != null){
                    for (Artifact artifact : process.getArtifacts()) {
                        if (artifact instanceof TextAnnotation || artifact instanceof Group) {
                        	Bounds b = _bounds.get(artifact.getId());
                        	if (b != null) {
                        		BPMNShape shape = factory.createBPMNShape();
                        		shape.setBpmnElement(artifact);
                        		shape.setBounds(b);
                        		plane.getPlaneElement().add(shape);
                        	}
                        }
                        if (artifact instanceof Association){
                            Association association = (Association)artifact;
                            BPMNEdge edge = factory.createBPMNEdge();
                            edge.setBpmnElement(association);
                            DcFactory dcFactory = DcFactory.eINSTANCE;
                            Point point = dcFactory.createPoint();
                            Bounds sourceBounds = _bounds.get(association.getSourceRef().getId());
                            point.setX(sourceBounds.getX() + (sourceBounds.getWidth()/2));
                            point.setY(sourceBounds.getY() + (sourceBounds.getHeight()/2));
                            edge.getWaypoint().add(point);
                            List<Point> dockers = _dockers.get(association.getId());
                            for (int i = 1; i < dockers.size() - 1; i++) {
                                    edge.getWaypoint().add(dockers.get(i));
                            }
                            point = dcFactory.createPoint();
                            Bounds targetBounds = _bounds.get(association.getTargetRef().getId());
                            point.setX(targetBounds.getX() + (targetBounds.getWidth()/2));
                            point.setY(targetBounds.getY() + (targetBounds.getHeight()/2));
                            edge.getWaypoint().add(point);
                            plane.getPlaneElement().add(edge);
                        }
                    }
                }
        		// lanes
        		if(process.getLaneSets() != null && process.getLaneSets().size() > 0) {
        			for(LaneSet ls : process.getLaneSets()) {
        				for(Lane lane : ls.getLanes()) {
        					Bounds b = _bounds.get(lane.getId());
            				if (b != null) {
            					BPMNShape shape = factory.createBPMNShape();
            					shape.setBpmnElement(lane);
            					shape.setBounds(b);
            					plane.getPlaneElement().add(shape);
            				}
        				}
        			}
        		}
        		def.getDiagrams().add(diagram);
    		}
    	}
    }

    private BaseElement unmarshallItem(JsonParser parser, String preProcessingData) throws JsonParseException, IOException {
        String resourceId = null;
        Map<String, String> properties = null;
        String stencil = null;
        List<BaseElement> childElements = new ArrayList<BaseElement>();
        List<String> outgoing = new ArrayList<String>();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = parser.getCurrentName();
            parser.nextToken();
            if ("resourceId".equals(fieldname)) {
                resourceId = parser.getText();
            } else if ("properties".equals(fieldname)) {
                properties = unmarshallProperties(parser);
            } else if ("stencil".equals(fieldname)) {
                // "stencil":{"id":"Task"},
                parser.nextToken();
                parser.nextToken();
                stencil = parser.getText();
                parser.nextToken();
            } else if ("childShapes".equals(fieldname)) {
                while (parser.nextToken() != JsonToken.END_ARRAY) { // open the
                                                                    // object
                    // the childShapes element is a json array. We opened the
                    // array.
                    childElements.add(unmarshallItem(parser, preProcessingData));
                }
            } else if ("bounds".equals(fieldname)) {
                // bounds: {"lowerRight":{"x":484.0,"y":198.0},"upperLeft":{"x":454.0,"y":168.0}}
                parser.nextToken();
                parser.nextToken();
                parser.nextToken();
                parser.nextToken();
                Integer x2 = parser.getIntValue();
                parser.nextToken();
                parser.nextToken();
                Integer y2 = parser.getIntValue();
                parser.nextToken();
                parser.nextToken();
                parser.nextToken();
                parser.nextToken();
                parser.nextToken();
                Integer x1 = parser.getIntValue();
                parser.nextToken();
                parser.nextToken();
                Integer y1 = parser.getIntValue();
                parser.nextToken();
                parser.nextToken();
                Bounds b = DcFactory.eINSTANCE.createBounds();
                b.setX(x1);
                b.setY(y1);
                b.setWidth(x2 - x1);
                b.setHeight(y2 - y1);
                this._bounds.put(resourceId, b);
            } else if ("dockers".equals(fieldname)) {
                // "dockers":[{"x":50,"y":40},{"x":353.5,"y":115},{"x":353.5,"y":152},{"x":50,"y":40}],
            	List<Point> dockers = new ArrayList<Point>();
            	JsonToken nextToken = parser.nextToken();
            	boolean end = JsonToken.END_ARRAY.equals(nextToken);
            	while (!end) {
            		nextToken = parser.nextToken();
            		nextToken = parser.nextToken();
            		Integer x = parser.getIntValue();
                    parser.nextToken();
                    parser.nextToken();
                    Integer y = parser.getIntValue();
                    Point point = DcFactory.eINSTANCE.createPoint();
                    point.setX(x);
                    point.setY(y);
                    dockers.add(point);
                    parser.nextToken();
                    nextToken = parser.nextToken();
                    end = JsonToken.END_ARRAY.equals(nextToken);
            	}
            	this._dockers.put(resourceId, dockers);
            } else if ("outgoing".equals(fieldname)) {
                while (parser.nextToken() != JsonToken.END_ARRAY) {
                    // {resourceId: oryx_1AAA8C9A-39A5-42FC-8ED1-507A7F3728EA}
                    parser.nextToken();
                    parser.nextToken();
                    outgoing.add(parser.getText());
                    parser.nextToken();
                }
                // pass on the array
                parser.skipChildren();
            } else if ("target".equals(fieldname)) {
                // we already collected that info with the outgoing field.
                parser.skipChildren();
                // "target": {
                // "resourceId": "oryx_A75E7546-DF71-48EA-84D3-2A8FD4A47568"
                // }
                // add to the map:
                // parser.nextToken(); // resourceId:
                // parser.nextToken(); // the value we want to save
                // targetId = parser.getText();
                // parser.nextToken(); // }, closing the object
            }
        }
        properties.put("resourceId", resourceId);
        boolean customElement = isCustomElement(properties.get("tasktype"), preProcessingData);
        BaseElement baseElt = Bpmn20Stencil.createElement(stencil, properties.get("tasktype"), customElement);
        
        // register the sequence flow targets.
        if(baseElt instanceof SequenceFlow) {
            _sequenceFlowTargets.addAll(outgoing);
        }
        _outgoingFlows.put(baseElt, outgoing);
        _objMap.put(baseElt, resourceId); // keep the object around to do connections
        _idMap.put(resourceId, baseElt);
        // baseElt.setId(resourceId); commented out as bpmn2 seems to create
        // duplicate ids right now.
        applyProperties(baseElt, properties);
        if (baseElt instanceof Definitions) {
        	Process rootLevelProcess = null;
        	for (BaseElement child : childElements) {
                // tasks are only permitted under processes.
                // a process should be created implicitly for tasks at the root
                // level.

                // process designer doesn't make a difference between tasks and
                // global tasks.
                // if a task has sequence edges it is considered a task,
                // otherwise it is considered a global task.
//                if (child instanceof Task && _outgoingFlows.get(child).isEmpty() && !_sequenceFlowTargets.contains(_objMap.get(child))) {
//                    // no edges on a task at the top level! We replace it with a
//                    // global task.
//                    GlobalTask task = null;
//                    if (child instanceof ScriptTask) {
//                        task = Bpmn2Factory.eINSTANCE.createGlobalScriptTask();
//                        ((GlobalScriptTask) task).setScript(((ScriptTask) child).getScript());
//                        ((GlobalScriptTask) task).setScriptLanguage(((ScriptTask) child).getScriptFormat()); 
//                        // TODO scriptLanguage missing on scriptTask
//                    } else if (child instanceof UserTask) {
//                        task = Bpmn2Factory.eINSTANCE.createGlobalUserTask();
//                    } else if (child instanceof ServiceTask) {
//                        // we don't have a global service task! Fallback on a
//                        // normal global task
//                        task = Bpmn2Factory.eINSTANCE.createGlobalTask();
//                    } else if (child instanceof BusinessRuleTask) {
//                        task = Bpmn2Factory.eINSTANCE.createGlobalBusinessRuleTask();
//                    } else if (child instanceof ManualTask) {
//                        task = Bpmn2Factory.eINSTANCE.createGlobalManualTask();
//                    } else {
//                        task = Bpmn2Factory.eINSTANCE.createGlobalTask();
//                    }
//
//                    task.setName(((Task) child).getName());
//                    task.setIoSpecification(((Task) child).getIoSpecification());
//                    task.getDocumentation().addAll(((Task) child).getDocumentation());
//                    ((Definitions) baseElt).getRootElements().add(task);
//                    continue;
//                } else {
                    if (child instanceof SequenceFlow) {
                        // for some reason sequence flows are placed as root elements.
                        // find if the target has a container, and if we can use it:
                        List<String> ids = _outgoingFlows.get(child);
                        FlowElementsContainer container = null;
                        for (String id : ids) { // yes, we iterate, but we'll take the first in the list that will work.
                            Object obj = _idMap.get(id);
                            if (obj instanceof EObject && ((EObject) obj).eContainer() instanceof FlowElementsContainer) {
                                container = (FlowElementsContainer) ((EObject) obj).eContainer();
                                break;
                            }
                        }
                        if (container != null) {
                            container.getFlowElements().add((SequenceFlow) child);
                            continue;
                        }
                        
                    }
                    if (child instanceof Task || child instanceof SequenceFlow 
                            || child instanceof Gateway || child instanceof Event 
                            || child instanceof Artifact || child instanceof DataObject || child instanceof SubProcess
                            || child instanceof Lane || child instanceof CallActivity) {
                        if (rootLevelProcess == null) {
                            rootLevelProcess = Bpmn2Factory.eINSTANCE.createProcess();
                            // set the properties and item definitions first
                            if(properties.get("vardefs") != null && properties.get("vardefs").length() > 0) {
                                String[] vardefs = properties.get("vardefs").split( ",\\s*" );
                                for(String vardef : vardefs) {
                                    Property prop = Bpmn2Factory.eINSTANCE.createProperty();
                                    ItemDefinition itemdef =  Bpmn2Factory.eINSTANCE.createItemDefinition();
                                    // check if we define a structure ref in the definition
                                    if(vardef.contains(":")) {
                                        String[] vardefParts = vardef.split( ":\\s*" );
                                        prop.setId(vardefParts[0]);
                                        itemdef.setId("_" + prop.getId() + "Item");
                                        itemdef.setStructureRef(vardefParts[1]);
                                    } else {
                                        prop.setId(vardef);
                                        itemdef.setId("_" + prop.getId() + "Item");
                                    }
                                    prop.setItemSubjectRef(itemdef);
                                    rootLevelProcess.getProperties().add(prop);
                                    ((Definitions) baseElt).getRootElements().add(itemdef);
                                }
                            }
                            rootLevelProcess.setId(properties.get("id"));
                            applyProcessProperties(rootLevelProcess, properties);
                            ((Definitions) baseElt).getRootElements().add(rootLevelProcess);
                        }
                    }
                    if (child instanceof Task) {
                        rootLevelProcess.getFlowElements().add((Task) child);
                    } else if (child instanceof CallActivity) {
                    	rootLevelProcess.getFlowElements().add((CallActivity) child);
                    } else if (child instanceof RootElement) {
                        ((Definitions) baseElt).getRootElements().add((RootElement) child);
                    } else if (child instanceof SequenceFlow) {
                        rootLevelProcess.getFlowElements().add((SequenceFlow) child);
                    } else if (child instanceof Gateway) {
                        rootLevelProcess.getFlowElements().add((Gateway) child);
                    } else if (child instanceof Event) {
                        rootLevelProcess.getFlowElements().add((Event) child);
                    } else if (child instanceof Artifact) {
                        rootLevelProcess.getArtifacts().add((Artifact) child);
                    } else if (child instanceof DataObject) {
                        rootLevelProcess.getFlowElements().add((DataObject) child);
//                        ItemDefinition def = ((DataObject) child).getItemSubjectRef();
//                        if (def != null) {
//                            if (def.eResource() == null) {
//                                ((Definitions) rootLevelProcess.eContainer()).getRootElements().add(0, def);
//                            }
//                            Import imported = def.getImport();
//                            if (imported != null && imported.eResource() == null) {
//                                ((Definitions) rootLevelProcess.eContainer()).getImports().add(0, imported);
//                            }
//                        }
                    } else if(child instanceof SubProcess) {
                        rootLevelProcess.getFlowElements().add((SubProcess) child);
                    } else if(child instanceof Lane) {
                    	// lanes handled later
                    } else {
                        throw new IllegalArgumentException("Don't know what to do of " + child);
                    }
               // }
            }
        } else if (baseElt instanceof Process) {
            for (BaseElement child : childElements) {
                if (child instanceof Lane) {
                    if (((Process) baseElt).getLaneSets().isEmpty()) {
                        ((Process) baseElt).getLaneSets().add(Bpmn2Factory.eINSTANCE.createLaneSet());
                    }
                    ((Process) baseElt).getLaneSets().get(0).getLanes().add((Lane) child);
                    addLaneFlowNodes((Process) baseElt, (Lane) child);
                } else if (child instanceof Artifact) {
                    ((Process) baseElt).getArtifacts().add((Artifact) child);
                } else {
                    throw new IllegalArgumentException("Don't know what to do of " + child);
                }
            }
        } else if (baseElt instanceof SubProcess) {
            for (BaseElement child : childElements) {
                if (child instanceof FlowElement) {
                    ((SubProcess) baseElt).getFlowElements().add((FlowElement) child);
                } else if(child instanceof Artifact) { 
                	((SubProcess) baseElt).getArtifacts().add((Artifact) child);
                } else {
                    throw new IllegalArgumentException("Subprocess - don't know what to do of " + child);
                }
            }
        } else if (baseElt instanceof Message) {
            // we do not support base-element messages from the json. They are created dynamically for events that use them.
        } else if (baseElt instanceof Lane) {
        	((Lane) baseElt).setName(properties.get("name"));
        	for (BaseElement child : childElements) {
        		if (child instanceof FlowNode) {
        			((Lane) baseElt).getFlowNodeRefs().add((FlowNode) child);
        		}
        		// no support for child-lanes at this point
//        		else if (child instanceof Lane) {
//        			if (((Lane) baseElt).getChildLaneSet() == null) {
//        				((Lane) baseElt).setChildLaneSet(Bpmn2Factory.eINSTANCE.createLaneSet());
//        			}
//        			((Lane) baseElt).getChildLaneSet().getLanes().add((Lane) child);
//        		} 
        		else if(child instanceof Artifact){
        			_artifacts.add((Artifact) child);
        		} else {
        			throw new IllegalArgumentException("Don't know what to do of " + childElements);
        		}
        	}
        	_lanes.add((Lane) baseElt);
      } else {
            if (!childElements.isEmpty()) {
                throw new IllegalArgumentException("Don't know what to do of " + childElements + " with " + baseElt);
            }
        }
        return baseElt;
    }

    private void addLaneFlowNodes(Process process, Lane lane) {
        process.getFlowElements().addAll(lane.getFlowNodeRefs());
//        for (FlowNode node : lane.getFlowNodeRefs()) {
//            if (node instanceof DataObject) {
//                ItemDefinition def = ((DataObject) node).getItemSubjectRef();
//                if (def != null) {
//                    if (def.eResource() == null) {
//                        ((Definitions) process.eContainer()).getRootElements().add(0, ((DataObject) node).getItemSubjectRef());
//                    }
//                    Import imported = def.getImport();
//                    if (imported != null && imported.eResource() == null) {
//                        ((Definitions) process.eContainer()).getImports().add(0, ((DataObject) node).getItemSubjectRef().getImport());
//                    }
//                }
//            }
//        }
        if (lane.getChildLaneSet() != null) {
            for (Lane l : lane.getChildLaneSet().getLanes()) {
                addLaneFlowNodes(process, l);
            }
        }
    }

    private void applyProperties(BaseElement baseElement, Map<String, String> properties) {
        applyBaseElementProperties((BaseElement) baseElement, properties);
        if (baseElement instanceof SubProcess) {
            applySubProcessProperties((SubProcess) baseElement, properties);
        }
        if (baseElement instanceof AdHocSubProcess) {
            applyAdHocSubProcessProperties((AdHocSubProcess) baseElement, properties);
        }
        if (baseElement instanceof CallActivity) {
        	applyCallActivityProperties((CallActivity) baseElement, properties);
        }
        if (baseElement instanceof GlobalTask) {
            applyGlobalTaskProperties((GlobalTask) baseElement, properties);
        }
        if (baseElement instanceof Definitions) {
            applyDefinitionProperties((Definitions) baseElement, properties);
        }
        if (baseElement instanceof Process) {
            applyProcessProperties((Process) baseElement, properties);
        }
        if (baseElement instanceof Lane) {
            applyLaneProperties((Lane) baseElement, properties);
        }
        if (baseElement instanceof SequenceFlow) {
            applySequenceFlowProperties((SequenceFlow) baseElement, properties);
        }
        if (baseElement instanceof Task) {
            applyTaskProperties((Task) baseElement, properties);
        }
        if (baseElement instanceof UserTask) {
            applyUserTaskProperties((UserTask) baseElement, properties);
        }  
        if (baseElement instanceof BusinessRuleTask) {
            applyBusinessRuleTaskProperties((BusinessRuleTask) baseElement, properties);
        }
        if (baseElement instanceof ScriptTask) {
            applyScriptTaskProperties((ScriptTask) baseElement, properties);
        }
        if (baseElement instanceof ServiceTask) {
            applyServiceTaskProperties((ServiceTask) baseElement, properties);
        }
        if (baseElement instanceof Gateway) {
            applyGatewayProperties((Gateway) baseElement, properties);
        }
        if (baseElement instanceof Event) {
            applyEventProperties((Event) baseElement, properties);
        }
        if (baseElement instanceof CatchEvent) {
            applyCatchEventProperties((CatchEvent) baseElement, properties);
        }
        if (baseElement instanceof ThrowEvent) {
            applyThrowEventProperties((ThrowEvent) baseElement, properties);
        }
        if (baseElement instanceof TextAnnotation) {
            applyTextAnnotationProperties((TextAnnotation) baseElement, properties);
        }
        if (baseElement instanceof Group) {
            applyGroupProperties((Group) baseElement, properties);
        }
        if (baseElement instanceof DataObject) {
            applyDataObjectProperties((DataObject) baseElement, properties);
        }
        if (baseElement instanceof DataStore) {
            applyDataStoreProperties((DataStore) baseElement, properties);
        }
        if (baseElement instanceof Message) {
            applyMessageProperties((Message) baseElement, properties);
        }
        if (baseElement instanceof StartEvent) {
            applyStartEventProperties((StartEvent) baseElement, properties);
        }
        if (baseElement instanceof EndEvent) {
            applyEndEventProperties((EndEvent) baseElement, properties);
        }
        if(baseElement instanceof Association) {
        	applyAssociationProperties((Association) baseElement, properties);
        }
        // finally, apply properties from helpers:
        for (BpmnMarshallerHelper helper : _helpers) {
            helper.applyProperties(baseElement, properties);
        }
    }
    
    private void applySubProcessProperties(SubProcess sp, Map<String, String> properties) {
        if(properties.get("name") != null) {
            sp.setName(properties.get("name"));
        } else {
            sp.setName("");
        }
        // process on-entry and on-exit actions as custom elements
        if(properties.get("onentryactions") != null && properties.get("onentryactions").length() > 0) {
            String[] allActions = properties.get("onentryactions").split( "\\|\\s*" );
            for(String action : allActions) {
                OnEntryScriptType onEntryScript = EmfextmodelFactory.eINSTANCE.createOnEntryScriptType();
                onEntryScript.setScript(action);
                
                String scriptLanguage = "";
                if(properties.get("script_language").equals("java")) {
                    scriptLanguage = "http://www.java.com/java";
                } else if(properties.get("script_language").equals("mvel")) {
                    scriptLanguage = "http://www.mvel.org/2.0";
                } else {
                    // default to java
                    scriptLanguage = "http://www.java.com/java";
                }
                onEntryScript.setScriptFormat(scriptLanguage); 
                
                ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                sp.getExtensionValues().add(extensionElement);
                FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                        (Internal) EmfextmodelPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, onEntryScript);
                extensionElement.getValue().add(extensionElementEntry);
            }
        }
        
        if(properties.get("onexitactions") != null && properties.get("onexitactions").length() > 0) {
            String[] allActions = properties.get("onexitactions").split( "\\|\\s*" );
            for(String action : allActions) {
                OnExitScriptType onExitScript = EmfextmodelFactory.eINSTANCE.createOnExitScriptType();
                onExitScript.setScript(action);
                
                String scriptLanguage = "";
                if(properties.get("script_language").equals("java")) {
                    scriptLanguage = "http://www.java.com/java";
                } else if(properties.get("script_language").equals("mvel")) {
                    scriptLanguage = "http://www.mvel.org/2.0";
                } else {
                    // default to java
                    scriptLanguage = "http://www.java.com/java";
                }
                onExitScript.setScriptFormat(scriptLanguage); 
                
                ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                sp.getExtensionValues().add(extensionElement);
                FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                        (Internal) EmfextmodelPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, onExitScript);
                extensionElement.getValue().add(extensionElementEntry);
            }
        }
        
        // data input set
        if(properties.get("datainputset") != null && properties.get("datainputset").length() > 0) {
            String[] allDataInputs = properties.get("datainputset").split( ",\\s*" );
            if(sp.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                sp.setIoSpecification(iospec);
            }
            InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
            for(String dataInput : allDataInputs) {
                DataInput nextInput = Bpmn2Factory.eINSTANCE.createDataInput();
                nextInput.setId(sp.getId() + "_" + dataInput + "Input");
                nextInput.setName(dataInput);
                sp.getIoSpecification().getDataInputs().add(nextInput);
                
                inset.getDataInputRefs().add(nextInput);
            }
            sp.getIoSpecification().getInputSets().add(inset);
        } else {
            if(sp.getIoSpecification() != null) {
            	sp.getIoSpecification().getInputSets().add(Bpmn2Factory.eINSTANCE.createInputSet());
            }
        }
        
        // data output set
        if(properties.get("dataoutputset") != null && properties.get("dataoutputset").length() > 0) {
            String[] allDataOutputs = properties.get("dataoutputset").split( ",\\s*" );
            if(sp.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                sp.setIoSpecification(iospec);
            }
            
            OutputSet outset = Bpmn2Factory.eINSTANCE.createOutputSet();
            for(String dataOutput : allDataOutputs) {
                DataOutput nextOut = Bpmn2Factory.eINSTANCE.createDataOutput();
                nextOut.setId(sp.getId() + "_" + dataOutput + "Output");
                nextOut.setName(dataOutput);
                sp.getIoSpecification().getDataOutputs().add(nextOut);
                
                outset.getDataOutputRefs().add(nextOut);
            }
            sp.getIoSpecification().getOutputSets().add(outset);
        } else {
            if(sp.getIoSpecification() != null) {
            	sp.getIoSpecification().getOutputSets().add(Bpmn2Factory.eINSTANCE.createOutputSet());
            }
        }
        
        // assignments
        if(properties.get("assignments") != null && properties.get("assignments").length() > 0 && sp.getIoSpecification() != null) {
            String[] allAssignments = properties.get("assignments").split( ",\\s*" );
            for(String assignment : allAssignments) {
                if(assignment.contains("=")) {
                    String[] assignmentParts = assignment.split( "=\\s*" );
                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();

                    boolean foundTaskName = false;
                    if(sp.getIoSpecification() != null && sp.getIoSpecification().getDataOutputs() != null) {
                    	List<DataInput> dataInputs = sp.getIoSpecification().getDataInputs();
                    	for(DataInput di : dataInputs) {
                    		if(di.getId().equals(sp.getId() + "_" + assignmentParts[0] + "Input")) {
                    			dia.setTargetRef(di);
                    			if(di.getName().equals("TaskName")) {
                    				foundTaskName = true;
                    				break;
                    			}
                    		}
                    	}
                    }
                    
                    Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                    FormalExpression fromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    if(assignmentParts.length > 1) {
                        fromExpression.setBody(assignmentParts[1]);
                    } else {
                        fromExpression.setBody("");
                    }
                    FormalExpression toExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    toExpression.setBody(dia.getTargetRef().getId());
                    
                    a.setFrom(fromExpression);
                    a.setTo(toExpression);
                    
                    dia.getAssignment().add(a);
                    sp.getDataInputAssociations().add(dia);
                    
                } else if(assignment.contains("<->")) {
                    String[] assignmentParts = assignment.split( "<->\\s*" );
                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                    DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                    
                    ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                    ie.setId(assignmentParts[0]);
                    dia.getSourceRef().add(ie);
                    doa.setTargetRef(ie);
                    
                    List<DataInput> dataInputs = sp.getIoSpecification().getDataInputs();
                    for(DataInput di : dataInputs) {
                        if(di.getId().equals(sp.getId() + "_" + assignmentParts[1] + "Input")) {
                            dia.setTargetRef(di);
                            break;
                        }
                    }
                    List<DataOutput> dataOutputs = sp.getIoSpecification().getDataOutputs();
                    for(DataOutput dout : dataOutputs) {
                        if(dout.getId().equals(sp.getId() + "_" + assignmentParts[1] + "Output")) {
                            doa.getSourceRef().add(dout);
                            break;
                        }
                    }
                    
                    sp.getDataInputAssociations().add(dia);
                    sp.getDataOutputAssociations().add(doa);
                } else if(assignment.contains("->")) {
                    String[] assignmentParts = assignment.split( "->\\s*" );
                    // we need to check if this is an data input or data output assignment
                    boolean leftHandAssignMentIsDO = false;
                    List<DataOutput> dataOutputs = sp.getIoSpecification().getDataOutputs();
                    for(DataOutput dout : dataOutputs) {
                        if(dout.getId().equals(sp.getId() + "_" + assignmentParts[0] + "Output")) {
                            leftHandAssignMentIsDO = true;
                            break;
                        }
                    }
                    if(leftHandAssignMentIsDO) {
                        // doing data output
                        DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                        for(DataOutput dout : dataOutputs) {
                            if(dout.getId().equals(sp.getId() + "_" + assignmentParts[0] + "Output")) {
                                doa.getSourceRef().add(dout);
                                break;
                            }
                        }
                        
                        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                        ie.setId(assignmentParts[1]);
                        doa.setTargetRef(ie);
                        sp.getDataOutputAssociations().add(doa);
                    } else {
                        // doing data input
                        DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                        // association from process var to dataInput var
                        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                        ie.setId(assignmentParts[0]);
                        dia.getSourceRef().add(ie);

                        List<DataInput> dataInputs = sp.getIoSpecification().getDataInputs();
                        for(DataInput di : dataInputs) {
                            if(di.getId().equals(sp.getId() + "_" + assignmentParts[1] + "Input")) {
                                dia.setTargetRef(di);
                                break;
                            }
                        }
                        sp.getDataInputAssociations().add(dia);
                    }
                } else {
                    // TODO throw exception here?
                }
            }
        }
        
        // loop characteristics
        if(properties.get("collectionexpression") != null && properties.get("collectionexpression").length() > 0
        		&& properties.get("variablename") != null && properties.get("variablename").length() > 0) {
        	if(sp.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                sp.setIoSpecification(iospec);
            } else {
            	sp.getIoSpecification().getDataInputs().clear();
            	sp.getIoSpecification().getDataOutputs().clear();
            	sp.getIoSpecification().getInputSets().clear();
            	sp.getIoSpecification().getOutputSets().clear();
            	sp.getDataInputAssociations().clear();
            	sp.getDataOutputAssociations().clear();
            }
        	InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
        	DataInput multiInput = Bpmn2Factory.eINSTANCE.createDataInput();
        	multiInput.setId(sp.getId() + "_" + "input");
        	multiInput.setName("MultiInstanceInput");
            sp.getIoSpecification().getDataInputs().add(multiInput);
            inset.getDataInputRefs().add(multiInput);
            sp.getIoSpecification().getInputSets().add(inset);
            sp.getIoSpecification().getOutputSets().add(Bpmn2Factory.eINSTANCE.createOutputSet());
            DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
            ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
            ie.setId(properties.get("collectionexpression"));
            dia.getSourceRef().add(ie);
            dia.setTargetRef(multiInput);
            sp.getDataInputAssociations().add(dia);
            MultiInstanceLoopCharacteristics loopCharacteristics = Bpmn2Factory.eINSTANCE.createMultiInstanceLoopCharacteristics();
            loopCharacteristics.setLoopDataInputRef(multiInput);
            DataInput din = Bpmn2Factory.eINSTANCE.createDataInput();
            din.setId(properties.get("variablename"));
            ItemDefinition itemDef = Bpmn2Factory.eINSTANCE.createItemDefinition();
            itemDef.setId(sp.getId() + "_" + "multiInstanceItemType");
            din.setItemSubjectRef(itemDef);
            _subprocessItemDefs.put(itemDef.getId(), itemDef);
            loopCharacteristics.setInputDataItem(din);
            sp.setLoopCharacteristics(loopCharacteristics);
        }
        
        // properties
        if(properties.get("vardefs") != null && properties.get("vardefs").length() > 0) {
            String[] vardefs = properties.get("vardefs").split( ",\\s*" );
            for(String vardef : vardefs) {
                Property prop = Bpmn2Factory.eINSTANCE.createProperty();
                ItemDefinition itemdef =  Bpmn2Factory.eINSTANCE.createItemDefinition();
                // check if we define a structure ref in the definition
                if(vardef.contains(":")) {
                    String[] vardefParts = vardef.split( ":\\s*" );
                    prop.setId(vardefParts[0]);
                    itemdef.setId("_" + prop.getId() + "Item");
                    itemdef.setStructureRef(vardefParts[1]);
                } else {
                    prop.setId(vardef);
                    itemdef.setId("_" + prop.getId() + "Item");
                }
                prop.setItemSubjectRef(itemdef);
                sp.getProperties().add(prop);
                _subprocessItemDefs.put(itemdef.getId(), itemdef);
            }
        }
    }
    
    private void applyAdHocSubProcessProperties(AdHocSubProcess ahsp, Map<String, String> properties) {
    	if(properties.get("adhocordering") != null) {
    		if(properties.get("adhocordering") == null || properties.get("adhocordering").equals("Parallel")) {
    			ahsp.setOrdering(AdHocOrdering.PARALLEL);
    		} else {
    			ahsp.setOrdering(AdHocOrdering.SEQUENTIAL);
    		}
   
    	}
    }

    private void applyEndEventProperties(EndEvent ee, Map<String, String> properties) {
        ee.setId(properties.get("resourceId"));
        if(properties.get("name") != null) {
            ee.setName(properties.get("name"));
        } else {
            ee.setName("");
        }
    }
    
    private void applyAssociationProperties(Association association, Map<String, String> properties) {
    	if(properties.get("type") != null) {
    		ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                        "http://www.jboss.org/drools", "type", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                properties.get("type"));
            association.getAnyAttribute().add(extensionEntry);
    	}
    }
    
    private void applyStartEventProperties(StartEvent se, Map<String, String> properties) {
        if(properties.get("name") != null) {
            se.setName(properties.get("name"));
        } else {
            se.setName("");
        }
    }
    
    private void applyMessageProperties(Message msg, Map<String, String> properties) {
        if(properties.get("name") != null) {
            msg.setName(properties.get("name"));
            msg.setId(properties.get("name") + "Message");
        } else {
            msg.setName("");
            msg.setId("Message");
        }
    }

    private void applyDataStoreProperties(DataStore da, Map<String, String> properties) {
        if(properties.get("name") != null) {
            da.setName(properties.get("name"));
        } else {
            da.setName("");
        }
    }

    private void applyDataObjectProperties(DataObject da, Map<String, String> properties) {
        if(properties.get("name") != null) {
            da.setName(properties.get("name"));
        } else {
            da.setName("");
        }
        
        if(properties.get("type") != null && properties.get("type").length() > 0) {
        	ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                        "http://www.jboss.org/drools", "datype", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                properties.get("type"));
            da.getAnyAttribute().add(extensionEntry);
        }
    }

    private void applyTextAnnotationProperties(TextAnnotation ta, Map<String, String> properties) {
        if(properties.get("text") != null) {
            ta.setText(properties.get("text"));
        } else {
            ta.setText("");
        }
        // default
        ta.setTextFormat("text/plain");
    }
    
    private void applyGroupProperties(Group group, Map<String, String> properties) {
    	if(properties.get("name") != null) {
    		ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                        "http://www.jboss.org/drools", "categoryval", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                properties.get("name"));
            group.getAnyAttribute().add(extensionEntry);
    	}
    }

    private void applyEventProperties(Event event, Map<String, String> properties) {
        if(properties.get("name") != null) {
            event.setName(properties.get("name"));
        } else {
            event.setName("");
        }
        if (properties.get("auditing") != null && !"".equals(properties.get("auditing"))) {
            Auditing audit = Bpmn2Factory.eINSTANCE.createAuditing();
            audit.getDocumentation().add(createDocumentation(properties.get("auditing")));
            event.setAuditing(audit);
        }
        if (properties.get("monitoring") != null && !"".equals(properties.get("monitoring"))) {
            Monitoring monitoring = Bpmn2Factory.eINSTANCE.createMonitoring();
            monitoring.getDocumentation().add(createDocumentation(properties.get("monitoring")));
            event.setMonitoring(monitoring);
        }
        
    }
    
    private void applyCatchEventProperties(CatchEvent event, Map<String, String> properties) {
        if (properties.get("dataoutput") != null && !"".equals(properties.get("dataoutput"))) {
            String[] allDataOutputs = properties.get("dataoutput").split( ",\\s*" );
            OutputSet outSet = Bpmn2Factory.eINSTANCE.createOutputSet();
            for(String dataOutput : allDataOutputs) {
                DataOutput dataout = Bpmn2Factory.eINSTANCE.createDataOutput();
                // we follow jbpm here to set the id
                dataout.setId(event.getId() + "_" + dataOutput);
                dataout.setName(dataOutput);
                event.getDataOutputs().add(dataout);
                // add to output set as well
                outSet.getDataOutputRefs().add(dataout);
                
            }
            event.setOutputSet(outSet);
        }

        // data output associations
        if (properties.get("dataoutputassociations") != null && !"".equals(properties.get("dataoutputassociations"))) {
            String[] allAssociations = properties.get("dataoutputassociations").split( ",\\s*" );
            for(String association : allAssociations) {
                // data outputs are uni-directional
                String[] associationParts = association.split( "->\\s*" );
                DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                // for source refs we loop through already defined data outputs
                List<DataOutput> dataOutputs = event.getDataOutputs();
                if(dataOutputs != null) {
                    for(DataOutput ddo : dataOutputs) {
                        if(ddo.getId().equals(event.getId() + "_" + associationParts[0])) {
                            doa.getSourceRef().add(ddo);
                        }
                    }
                }
                // since we dont have the process vars defined yet..need to improvise
                ItemAwareElement e = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                e.setId(associationParts[1]);
                doa.setTargetRef(e);
                event.getDataOutputAssociation().add(doa);
            }
        }
        
        try {
            EventDefinition ed = event.getEventDefinitions().get(0);
            if(ed instanceof TimerEventDefinition) {
                if(properties.get("timedate") != null && !"".equals(properties.get("timedate"))) {
                    FormalExpression timeDateExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    timeDateExpression.setBody(properties.get("timedate"));
                    ((TimerEventDefinition) event.getEventDefinitions().get(0)).setTimeDate(timeDateExpression);
                }
                
                if(properties.get("timeduration") != null && !"".equals(properties.get("timeduration"))) {
                    FormalExpression timeDurationExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    timeDurationExpression.setBody(properties.get("timeduration"));
                    ((TimerEventDefinition) event.getEventDefinitions().get(0)).setTimeDuration(timeDurationExpression);
                }
                
                if(properties.get("timecycle") != null && !"".equals(properties.get("timecycle"))) {
                    FormalExpression timeCycleExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    timeCycleExpression.setBody(properties.get("timecycle"));
                    ((TimerEventDefinition) event.getEventDefinitions().get(0)).setTimeCycle(timeCycleExpression);
                }
            } else if (ed instanceof SignalEventDefinition) {
                if(properties.get("signalref") != null && !"".equals(properties.get("signalref"))) {
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools", "signalrefname", false, false);
                    EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                        properties.get("signalref"));
                    ((SignalEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            } else if(ed instanceof ErrorEventDefinition) {
                if(properties.get("errorref") != null && !"".equals(properties.get("errorref"))) {
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools", "erefname", false, false);
                    EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                        properties.get("errorref"));
                    ((ErrorEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            } else if(ed instanceof ConditionalEventDefinition) {
                FormalExpression  conditionExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                if(properties.get("conditionlanguage") != null && !"".equals(properties.get("conditionlanguage"))) {
                    // currently supporting drools and mvel
                    String languageStr;
                    if(properties.get("conditionlanguage").equals("drools")) {
                        languageStr = "http://www.jboss.org/drools/rule";
                    } else if(properties.get("conditionlanguage").equals("mvel")) {
                        languageStr = "http://www.mvel.org/2.0";
                    } else {
                        // default to drools
                        languageStr = "http://www.jboss.org/drools/rule";
                    }
                    conditionExpression.setLanguage(languageStr);
                }
                if(properties.get("conditionexpression") != null && !"".equals(properties.get("conditionexpression"))) {
                    conditionExpression.setBody(properties.get("conditionexpression"));
                }
                ((ConditionalEventDefinition) event.getEventDefinitions().get(0)).setCondition(conditionExpression);
            } else if(ed instanceof EscalationEventDefinition) {
                if(properties.get("escalationcode") != null && !"".equals(properties.get("escalationcode"))) {
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools", "esccode", false, false);
                    EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                        properties.get("escalationcode"));
                    ((EscalationEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            } else if(ed instanceof MessageEventDefinition) {
                if(properties.get("messageref") != null && !"".equals(properties.get("messageref"))) {
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools", "msgref", false, false);
                    EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                        properties.get("messageref"));
                    ((MessageEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            } else if(ed instanceof CompensateEventDefinition) {
                if(properties.get("activityref") != null && !"".equals(properties.get("activityref"))) {
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools", "actrefname", false, false);
                    EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                        properties.get("activityref"));
                    ((CompensateEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            // TODO we dont want to barf here as test for example do not define event definitions in the bpmn2....
        }
                
    }
    
    private void applyThrowEventProperties(ThrowEvent event, Map<String, String> properties) {
        if (properties.get("datainput") != null && !"".equals(properties.get("datainput"))) {
            String[] allDataInputs = properties.get("datainput").split( ",\\s*" );
            InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
            for(String dataInput : allDataInputs) {
                DataInput datain = Bpmn2Factory.eINSTANCE.createDataInput();
                // we follow jbpm here to set the id
                datain.setId(event.getId() + "_" + dataInput);
                datain.setName(dataInput);
                event.getDataInputs().add(datain);
                // add to input set as well
                inset.getDataInputRefs().add(datain);
                
            }
            event.setInputSet(inset);
        }

        // data input associations
        if (properties.get("datainputassociations") != null && !"".equals(properties.get("datainputassociations"))) {
            String[] allAssociations = properties.get("datainputassociations").split( ",\\s*" );
            for(String association : allAssociations) {
                // data inputs are uni-directional
                String[] associationParts = association.split( "->\\s*" );
                DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                
                // since we dont have the process vars defined yet..need to improvise
                ItemAwareElement e = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                e.setId(associationParts[0]);
                dia.getSourceRef().add(e);
                
                // for target ref we loop through already defined data inputs
                List<DataInput> dataInputs = event.getDataInputs();
                if(dataInputs != null) {
                    for(DataInput di : dataInputs) {
                        if(di.getId().equals(event.getId() + "_" + associationParts[1])) {
                            dia.setTargetRef(di);
                            break;
                        }
                    }
                }
                
                event.getDataInputAssociation().add(dia);
            }
        }
        
        try {
            EventDefinition ed = event.getEventDefinitions().get(0);
            if(ed instanceof TimerEventDefinition) {
                if(properties.get("timedate") != null && !"".equals(properties.get("timedate"))) {
                    FormalExpression timeDateExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    timeDateExpression.setBody(properties.get("timedate"));
                    ((TimerEventDefinition) event.getEventDefinitions().get(0)).setTimeDate(timeDateExpression);
                }
                
                if(properties.get("timeduration") != null && !"".equals(properties.get("timeduration"))) {
                    FormalExpression timeDurationExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    timeDurationExpression.setBody(properties.get("timeduration"));
                    ((TimerEventDefinition) event.getEventDefinitions().get(0)).setTimeDuration(timeDurationExpression);
                }
                
                if(properties.get("timecycle") != null && !"".equals(properties.get("timecycle"))) {
                    FormalExpression timeCycleExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    timeCycleExpression.setBody(properties.get("timecycle"));
                    ((TimerEventDefinition) event.getEventDefinitions().get(0)).setTimeCycle(timeCycleExpression);
                }
            } else if (ed instanceof SignalEventDefinition) {
                if(properties.get("signalref") != null && !"".equals(properties.get("signalref"))) {
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools", "signalrefname", false, false);
                    EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                        properties.get("signalref"));
                    ((SignalEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            } else if(ed instanceof ErrorEventDefinition) {
                if(properties.get("errorref") != null && !"".equals(properties.get("errorref"))) {
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools", "erefname", false, false);
                    EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                        properties.get("errorref"));
                    ((ErrorEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            } else if(ed instanceof ConditionalEventDefinition) {
                FormalExpression  conditionExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                if(properties.get("conditionlanguage") != null && !"".equals(properties.get("conditionlanguage"))) {
                    // currently supporting drools and mvel
                    String languageStr;
                    if(properties.get("conditionlanguage").equals("drools")) {
                        languageStr = "http://www.jboss.org/drools/rule";
                    } else if(properties.get("conditionlanguage").equals("mvel")) {
                        languageStr = "http://www.mvel.org/2.0";
                    } else {
                        // default to drools
                        languageStr = "http://www.jboss.org/drools/rule";
                    }
                    conditionExpression.setLanguage(languageStr);
                }
                if(properties.get("conditionexpression") != null && !"".equals(properties.get("conditionexpression"))) {
                    conditionExpression.setBody(properties.get("conditionexpression"));
                }
                ((ConditionalEventDefinition) event.getEventDefinitions().get(0)).setCondition(conditionExpression);
            } else if(ed instanceof EscalationEventDefinition) {
                if(properties.get("escalationcode") != null && !"".equals(properties.get("escalationcode"))) {
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools", "esccode", false, false);
                    EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                        properties.get("escalationcode"));
                    ((EscalationEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            } else if(ed instanceof MessageEventDefinition) {
                if(properties.get("messageref") != null && !"".equals(properties.get("messageref"))) {
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools", "msgref", false, false);
                    EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                        properties.get("messageref"));
                    ((MessageEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            } else if(ed instanceof CompensateEventDefinition) {
                if(properties.get("activityref") != null && !"".equals(properties.get("activityref"))) {
                    ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
                    EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                                "http://www.jboss.org/drools", "actrefname", false, false);
                    EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                        properties.get("activityref"));
                    ((CompensateEventDefinition) event.getEventDefinitions().get(0)).getAnyAttribute().add(extensionEntry);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            // TODO we dont want to barf here as test for example do not define event definitions in the bpmn2....
        }
    }

    private void applyGlobalTaskProperties(GlobalTask globalTask, Map<String, String> properties) {
        if(properties.get("name") != null) {
            globalTask.setName(properties.get("name"));
        } else {
            globalTask.setName("");
        }
    }

    private void applyBaseElementProperties(BaseElement baseElement, Map<String, String> properties) {
        if (properties.get("documentation") != null && !"".equals(properties.get("documentation"))) {
            baseElement.getDocumentation().add(createDocumentation(properties.get("documentation")));
        }
        if(baseElement.getId() == null || baseElement.getId().length() < 1) {
            baseElement.setId(properties.get("resourceId"));
        }
        if(properties.get("bgcolor") != null && properties.get("bgcolor").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                        "http://www.jboss.org/drools", "bgcolor", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                properties.get("bgcolor"));
            baseElement.getAnyAttribute().add(extensionEntry);
        }
    }

    private void applyDefinitionProperties(Definitions def, Map<String, String> properties) {
        def.setTypeLanguage(properties.get("typelanguage"));
        //def.setTargetNamespace(properties.get("targetnamespace"));
        def.setTargetNamespace("http://www.omg.org/bpmn20");
        def.setExpressionLanguage(properties.get("expressionlanguage"));

        ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
        EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "xsi", "schemaLocation", false, false);
        EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
            "http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd");
        def.getAnyAttribute().add(extensionEntry);
        
        //_currentResource.getContents().add(def);// hook the definitions object to the resource early.
    }

    private void applyProcessProperties(Process process, Map<String, String> properties) {
        if(properties.get("name") != null) {
            process.setName(properties.get("name"));
        } else {
            process.setName("");
        }
        if (properties.get("auditing") != null && !"".equals(properties.get("auditing"))) {
            Auditing audit = Bpmn2Factory.eINSTANCE.createAuditing();
            audit.getDocumentation().add(createDocumentation(properties.get("auditing")));
            process.setAuditing(audit);
        }
        process.setProcessType(ProcessType.getByName(properties.get("processtype")));
        process.setIsClosed(Boolean.parseBoolean(properties.get("isclosed")));  
        process.setIsExecutable(Boolean.parseBoolean(properties.get("executable")));
        // get the drools-specific extension packageName attribute to Process if defined
        if(properties.get("package") != null && properties.get("package").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                        "http://www.jboss.org/drools", "packageName", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                properties.get("package"));
            process.getAnyAttribute().add(extensionEntry);
        }
        
        // add version attrbute to process
        if(properties.get("version") != null && properties.get("version").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                        "http://www.jboss.org/drools", "version", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                properties.get("version"));
            process.getAnyAttribute().add(extensionEntry);
        }
        
        if (properties.get("monitoring") != null && !"".equals(properties.get("monitoring"))) {
            Monitoring monitoring = Bpmn2Factory.eINSTANCE.createMonitoring();
            monitoring.getDocumentation().add(createDocumentation(properties.get("monitoring")));
            process.setMonitoring(monitoring);
        }
        
        // import extension elements
        ExtensionAttributeValue extensionElement = null;
        if(properties.get("imports") != null && properties.get("imports").length() > 0) {
            String[] allImports = properties.get("imports").split( ",\\s*" );
            for(String importStr : allImports) {
                ImportType importType = EmfextmodelFactory.eINSTANCE.createImportType();
                importType.setName(importStr);
                
                extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                process.getExtensionValues().add(extensionElement);
                FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                        (Internal) EmfextmodelPackage.Literals.DOCUMENT_ROOT__IMPORT, importType);
                extensionElement.getValue().add(extensionElementEntry);
            }
        }
        
        // globals extension elements
        if(properties.get("globals") != null && properties.get("globals").length() > 0) {
            String[] allGlobals = properties.get("globals").split( ",\\s*" );
            for(String globalStr : allGlobals) {
                String[] globalParts = globalStr.split( ":\\s*" ); // identifier:type
                if(globalParts.length == 2) {
                    GlobalType globalType = EmfextmodelFactory.eINSTANCE.createGlobalType();
                    globalType.setIdentifier(globalParts[0]);
                    globalType.setType(globalParts[1]);
                    if(extensionElement == null) {
                    	extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                    	process.getExtensionValues().add(extensionElement);
                    } 
                    FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                            (Internal) EmfextmodelPackage.Literals.DOCUMENT_ROOT__GLOBAL, globalType);
                    extensionElement.getValue().add(extensionElementEntry);
                }
            }
        }
    }

    private void applyBusinessRuleTaskProperties(BusinessRuleTask task, Map<String, String> properties) {
        if(properties.get("name") != null) {
            task.setName(properties.get("name"));
        } else {
            task.setName("");
        }
        if(properties.get("ruleflowgroup") != null &&  properties.get("ruleflowgroup").length() > 0) {
            // add droolsjbpm-specific attribute "ruleFlowGroup"
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools", "ruleFlowGroup", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                    properties.get("ruleflowgroup"));
            task.getAnyAttribute().add(extensionEntry);
        }
    }
    
    private void applyScriptTaskProperties(ScriptTask scriptTask, Map<String, String> properties) {
        if(properties.get("name") != null) {
            scriptTask.setName(properties.get("name"));
        } else {
            scriptTask.setName("");
        }
        
        if(properties.get("script") != null && properties.get("script").length() > 0) {
        	scriptTask.setScript(properties.get("script"));
        }
        
        if(properties.get("script_language") != null && properties.get("script_language").length() > 0) {
            String scriptLanguage = "";
            if(properties.get("script_language").equals("java")) {
                scriptLanguage = "http://www.java.com/java";
            } else if(properties.get("script_language").equals("mvel")) {
                scriptLanguage = "http://www.mvel.org/2.0";
            } else {
                // default to java
                scriptLanguage = "http://www.java.com/java";
            }
            scriptTask.setScriptFormat(scriptLanguage);
        }
    }
    
    public void applyServiceTaskProperties(ServiceTask serviceTask,  Map<String, String> properties) {
        if(properties.get("interface") != null) {
            serviceTask.setImplementation("Other");
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools", "servicetaskinterface", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                    properties.get("interface"));
            serviceTask.getAnyAttribute().add(extensionEntry); 
        }
        if(properties.get("operation") != null) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools", "servicetaskoperation", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                    properties.get("operation"));
            serviceTask.getAnyAttribute().add(extensionEntry);
        }
    }

    private void applyLaneProperties(Lane lane, Map<String, String> properties) {
        if(properties.get("name") != null) {
            lane.setName(properties.get("name"));
        } else {
            lane.setName("");
        }
    }
    
    private void applyCallActivityProperties(CallActivity callActivity, Map<String, String> properties) {
    	if(properties.get("name") != null) {
    		callActivity.setName(properties.get("name"));
        } else {
        	callActivity.setName("");
        }
    	
    	if(properties.get("independent") != null && properties.get("independent").length() > 0) {
    		ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools", "independent", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                    properties.get("independent"));
            callActivity.getAnyAttribute().add(extensionEntry);
    	}
    	
    	if(properties.get("waitforcompletion") != null && properties.get("waitforcompletion").length() > 0) {
    		ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools", "waitForCompletion", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                    properties.get("waitforcompletion"));
            callActivity.getAnyAttribute().add(extensionEntry);
    	}
    	
    	if(properties.get("calledelement") != null && properties.get("calledelement").length() > 0) {
    		callActivity.setCalledElement(properties.get("calledelement"));
    	}
    	
    	//callActivity data input set
        if(properties.get("datainputset") != null && properties.get("datainputset").length() > 0) {
            String[] allDataInputs = properties.get("datainputset").split( ",\\s*" );
            if(callActivity.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                callActivity.setIoSpecification(iospec);
            }
            InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
            for(String dataInput : allDataInputs) {
                DataInput nextInput = Bpmn2Factory.eINSTANCE.createDataInput();
                nextInput.setId(callActivity.getId() + "_" + dataInput + "Input");
                nextInput.setName(dataInput);
                callActivity.getIoSpecification().getDataInputs().add(nextInput);
                
                inset.getDataInputRefs().add(nextInput);
            }
            callActivity.getIoSpecification().getInputSets().add(inset);
        } else {
            if(callActivity.getIoSpecification() != null) {
            	callActivity.getIoSpecification().getInputSets().add(Bpmn2Factory.eINSTANCE.createInputSet());
            }
        }
        
        //callActivity data output set
        if(properties.get("dataoutputset") != null && properties.get("dataoutputset").length() > 0) {
            String[] allDataOutputs = properties.get("dataoutputset").split( ",\\s*" );
            if(callActivity.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                callActivity.setIoSpecification(iospec);
            }
            
            OutputSet outset = Bpmn2Factory.eINSTANCE.createOutputSet();
            for(String dataOutput : allDataOutputs) {
                DataOutput nextOut = Bpmn2Factory.eINSTANCE.createDataOutput();
                nextOut.setId(callActivity.getId() + "_" + dataOutput + "Output");
                nextOut.setName(dataOutput);
                callActivity.getIoSpecification().getDataOutputs().add(nextOut);
                
                outset.getDataOutputRefs().add(nextOut);
            }
            callActivity.getIoSpecification().getOutputSets().add(outset);
        } else {
            if(callActivity.getIoSpecification() != null) {
            	callActivity.getIoSpecification().getOutputSets().add(Bpmn2Factory.eINSTANCE.createOutputSet());
            }
        }
        
        //callActivity assignments
        if(properties.get("assignments") != null && properties.get("assignments").length() > 0) {
            String[] allAssignments = properties.get("assignments").split( ",\\s*" );
            for(String assignment : allAssignments) {
                if(assignment.contains("=")) {
                    String[] assignmentParts = assignment.split( "=\\s*" );
                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();

                    boolean foundTaskName = false;
                    if(callActivity.getIoSpecification() != null && callActivity.getIoSpecification().getDataOutputs() != null) {
                    	List<DataInput> dataInputs = callActivity.getIoSpecification().getDataInputs();
                    	for(DataInput di : dataInputs) {
                    		if(di.getId().equals(callActivity.getId() + "_" + assignmentParts[0] + "Input")) {
                    			dia.setTargetRef(di);
                    			if(di.getName().equals("TaskName")) {
                    				foundTaskName = true;
                    				break;
                    			}
                    		}
                    	}
                    }
                    
                    Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                    FormalExpression fromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    if(assignmentParts.length > 1) {
                        fromExpression.setBody(assignmentParts[1]);
                    } else {
                        fromExpression.setBody("");
                    }
                    FormalExpression toExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    toExpression.setBody(dia.getTargetRef().getId());
                    
                    a.setFrom(fromExpression);
                    a.setTo(toExpression);
                    
                    dia.getAssignment().add(a);
                    callActivity.getDataInputAssociations().add(dia);
                    
                } else if(assignment.contains("<->")) {
                    String[] assignmentParts = assignment.split( "<->\\s*" );
                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                    DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                    
                    ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                    ie.setId(assignmentParts[0]);
                    dia.getSourceRef().add(ie);
                    doa.setTargetRef(ie);
                    
                    List<DataInput> dataInputs = callActivity.getIoSpecification().getDataInputs();
                    for(DataInput di : dataInputs) {
                        if(di.getId().equals(callActivity.getId() + "_" + assignmentParts[1] + "Input")) {
                            dia.setTargetRef(di);
                            break;
                        }
                    }
                    List<DataOutput> dataOutputs = callActivity.getIoSpecification().getDataOutputs();
                    for(DataOutput dout : dataOutputs) {
                        if(dout.getId().equals(callActivity.getId() + "_" + assignmentParts[1] + "Output")) {
                            doa.getSourceRef().add(dout);
                            break;
                        }
                    }
                    
                    callActivity.getDataInputAssociations().add(dia);
                    callActivity.getDataOutputAssociations().add(doa);
                } else if(assignment.contains("->")) {
                    String[] assignmentParts = assignment.split( "->\\s*" );
                    // we need to check if this is an data input or data output assignment
                    boolean leftHandAssignMentIsDO = false;
                    List<DataOutput> dataOutputs = callActivity.getIoSpecification().getDataOutputs();
                    for(DataOutput dout : dataOutputs) {
                        if(dout.getId().equals(callActivity.getId() + "_" + assignmentParts[0] + "Output")) {
                            leftHandAssignMentIsDO = true;
                            break;
                        }
                    }
                    if(leftHandAssignMentIsDO) {
                        // doing data output
                        DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                        for(DataOutput dout : dataOutputs) {
                            if(dout.getId().equals(callActivity.getId() + "_" + assignmentParts[0] + "Output")) {
                                doa.getSourceRef().add(dout);
                                break;
                            }
                        }
                        
                        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                        ie.setId(assignmentParts[1]);
                        doa.setTargetRef(ie);
                        callActivity.getDataOutputAssociations().add(doa);
                    } else {
                        // doing data input
                        DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                        // association from process var to dataInput var
                        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                        ie.setId(assignmentParts[0]);
                        dia.getSourceRef().add(ie);

                        List<DataInput> dataInputs = callActivity.getIoSpecification().getDataInputs();
                        for(DataInput di : dataInputs) {
                            if(di.getId().equals(callActivity.getId() + "_" + assignmentParts[1] + "Input")) {
                                dia.setTargetRef(di);
                                break;
                            }
                        }
                        callActivity.getDataInputAssociations().add(dia);
                    }
                } else {
                    // TODO throw exception here?
                }
            }
        }
        
        // process on-entry and on-exit actions as custom elements
        if(properties.get("onentryactions") != null && properties.get("onentryactions").length() > 0) {
            String[] allActions = properties.get("onentryactions").split( "\\|\\s*" );
            for(String action : allActions) {
                OnEntryScriptType onEntryScript = EmfextmodelFactory.eINSTANCE.createOnEntryScriptType();
                onEntryScript.setScript(action);
                
                String scriptLanguage = "";
                if(properties.get("script_language").equals("java")) {
                    scriptLanguage = "http://www.java.com/java";
                } else if(properties.get("script_language").equals("mvel")) {
                    scriptLanguage = "http://www.mvel.org/2.0";
                } else {
                    // default to java
                    scriptLanguage = "http://www.java.com/java";
                }
                onEntryScript.setScriptFormat(scriptLanguage); 
                
                ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                callActivity.getExtensionValues().add(extensionElement);
                FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                        (Internal) EmfextmodelPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, onEntryScript);
                extensionElement.getValue().add(extensionElementEntry);
            }
        }
        
        if(properties.get("onexitactions") != null && properties.get("onexitactions").length() > 0) {
            String[] allActions = properties.get("onexitactions").split( "\\|\\s*" );
            for(String action : allActions) {
                OnExitScriptType onExitScript = EmfextmodelFactory.eINSTANCE.createOnExitScriptType();
                onExitScript.setScript(action);
                
                String scriptLanguage = "";
                if(properties.get("script_language").equals("java")) {
                    scriptLanguage = "http://www.java.com/java";
                } else if(properties.get("script_language").equals("mvel")) {
                    scriptLanguage = "http://www.mvel.org/2.0";
                } else {
                    // default to java
                    scriptLanguage = "http://www.java.com/java";
                }
                onExitScript.setScriptFormat(scriptLanguage); 
                
                ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                callActivity.getExtensionValues().add(extensionElement);
                FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                        (Internal) EmfextmodelPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, onExitScript);
                extensionElement.getValue().add(extensionElementEntry);
            }
        }
    }

    private void applyTaskProperties(Task task, Map<String, String> properties) {
        if(properties.get("name") != null) {
            task.setName(properties.get("name"));
        } else {
            task.setName("");
        }
        DataInput taskNameDataInput = null;
        if(properties.get("taskname") != null && properties.get("taskname").length() > 0) {
            // add droolsjbpm-specific attribute "taskName"
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools", "taskName", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                    properties.get("taskname"));
            task.getAnyAttribute().add(extensionEntry);
            
            // map the taskName to iospecification
            taskNameDataInput = Bpmn2Factory.eINSTANCE.createDataInput();
            taskNameDataInput.setId(task.getId() + "_TaskNameInput");
            taskNameDataInput.setName("TaskName");
            
            if(task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                task.setIoSpecification(iospec);
            }
            task.getIoSpecification().getDataInputs().add(taskNameDataInput);
            // taskName also needs to be in dataInputAssociation
            DataInputAssociation taskNameDataInputAssociation = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
            taskNameDataInputAssociation.setTargetRef(taskNameDataInput);
        
            Assignment taskNameAssignment = Bpmn2Factory.eINSTANCE.createAssignment();
            FormalExpression fromExp = Bpmn2Factory.eINSTANCE.createFormalExpression();
            fromExp.setBody(properties.get("taskname"));
            taskNameAssignment.setFrom(fromExp);
            FormalExpression toExp = Bpmn2Factory.eINSTANCE.createFormalExpression();
            toExp.setBody(task.getId() + "_TaskNameInput");
            taskNameAssignment.setTo(toExp);
            taskNameDataInputAssociation.getAssignment().add(taskNameAssignment);
        
            task.getDataInputAssociations().add(taskNameDataInputAssociation);
        }
        
        //process lanes
        if(properties.get("lanes") != null && properties.get("lanes").length() > 0) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools", "lanes", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                    properties.get("lanes"));
            task.getAnyAttribute().add(extensionEntry);
        }
        
        //process data input set
        if(properties.get("datainputset") != null && properties.get("datainputset").length() > 0) {
            String[] allDataInputs = properties.get("datainputset").split( ",\\s*" );
            if(task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                task.setIoSpecification(iospec);
            }
            InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
            for(String dataInput : allDataInputs) {
                DataInput nextInput = Bpmn2Factory.eINSTANCE.createDataInput();
                nextInput.setId(task.getId() + "_" + dataInput + "Input");
                nextInput.setName(dataInput);
                task.getIoSpecification().getDataInputs().add(nextInput);
                
                inset.getDataInputRefs().add(nextInput);
            }
            // add the taskName as well if it was defined
            if(taskNameDataInput != null) {
                inset.getDataInputRefs().add(taskNameDataInput);
            }
            task.getIoSpecification().getInputSets().add(inset);
        } else {
            if(task.getIoSpecification() != null) {
                task.getIoSpecification().getInputSets().add(Bpmn2Factory.eINSTANCE.createInputSet());
            }
        }
        
        //process data output set
        if(properties.get("dataoutputset") != null && properties.get("dataoutputset").length() > 0) {
            String[] allDataOutputs = properties.get("dataoutputset").split( ",\\s*" );
            if(task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                task.setIoSpecification(iospec);
            }
            
            OutputSet outset = Bpmn2Factory.eINSTANCE.createOutputSet();
            for(String dataOutput : allDataOutputs) {
                DataOutput nextOut = Bpmn2Factory.eINSTANCE.createDataOutput();
                nextOut.setId(task.getId() + "_" + dataOutput + "Output");
                nextOut.setName(dataOutput);
                task.getIoSpecification().getDataOutputs().add(nextOut);
                
                outset.getDataOutputRefs().add(nextOut);
            }
            task.getIoSpecification().getOutputSets().add(outset);
        } else {
            if(task.getIoSpecification() != null) {
                task.getIoSpecification().getOutputSets().add(Bpmn2Factory.eINSTANCE.createOutputSet());
            }
        }
        
        //process assignments
        if(properties.get("assignments") != null && properties.get("assignments").length() > 0) {
            String[] allAssignments = properties.get("assignments").split( ",\\s*" );
            for(String assignment : allAssignments) {
                if(assignment.contains("=")) {
                    String[] assignmentParts = assignment.split( "=\\s*" );
                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();

                    boolean foundTaskName = false;
                    if(task.getIoSpecification() != null && task.getIoSpecification().getDataOutputs() != null) {
                    	List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
                    	for(DataInput di : dataInputs) {
                    		if(di.getId().equals(task.getId() + "_" + assignmentParts[0] + "Input")) {
                    			dia.setTargetRef(di);
                    			if(di.getName().equals("TaskName")) {
                    				foundTaskName = true;
                    				break;
                    			}
                    		}
                    	}
                    }
                    // if we are dealing with TaskName and none has been defined, add it
                    if(assignmentParts[0].equals("TaskName") && !foundTaskName) {
                        DataInput assignmentTaskNameDataInput = Bpmn2Factory.eINSTANCE.createDataInput();
                        assignmentTaskNameDataInput.setId(task.getId() + "_TaskNameInput");
                        assignmentTaskNameDataInput.setName("TaskName");
                        if(task.getIoSpecification() == null) {
                            InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                            task.setIoSpecification(iospec);
                        }
                        task.getIoSpecification().getDataInputs().add(assignmentTaskNameDataInput);
                        dia.setTargetRef(assignmentTaskNameDataInput);
                        InputSet inset = task.getIoSpecification().getInputSets().get(0);
                        inset.getDataInputRefs().add(assignmentTaskNameDataInput);
                    }
                    
                    Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                    FormalExpression fromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    if(assignmentParts.length > 1) {
                        fromExpression.setBody(assignmentParts[1]);
                    } else {
                        fromExpression.setBody("");
                    }
                    FormalExpression toExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                    toExpression.setBody(dia.getTargetRef().getId());
                    
                    a.setFrom(fromExpression);
                    a.setTo(toExpression);
                    
                    dia.getAssignment().add(a);
                    task.getDataInputAssociations().add(dia);
                    
                } else if(assignment.contains("<->")) {
                    String[] assignmentParts = assignment.split( "<->\\s*" );
                    DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                    DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                    
                    ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                    ie.setId(assignmentParts[0]);
                    dia.getSourceRef().add(ie);
                    doa.setTargetRef(ie);
                    
                    List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
                    for(DataInput di : dataInputs) {
                        if(di.getId().equals(task.getId() + "_" + assignmentParts[1] + "Input")) {
                            dia.setTargetRef(di);
                            break;
                        }
                    }
                    List<DataOutput> dataOutputs = task.getIoSpecification().getDataOutputs();
                    for(DataOutput dout : dataOutputs) {
                        if(dout.getId().equals(task.getId() + "_" + assignmentParts[1] + "Output")) {
                            doa.getSourceRef().add(dout);
                            break;
                        }
                    }
                    
                    task.getDataInputAssociations().add(dia);
                    task.getDataOutputAssociations().add(doa);
                } else if(assignment.contains("->")) {
                    String[] assignmentParts = assignment.split( "->\\s*" );
                    // we need to check if this is an data input or data output assignment
                    boolean leftHandAssignMentIsDO = false;
                    List<DataOutput> dataOutputs = task.getIoSpecification().getDataOutputs();
                    for(DataOutput dout : dataOutputs) {
                        if(dout.getId().equals(task.getId() + "_" + assignmentParts[0] + "Output")) {
                            leftHandAssignMentIsDO = true;
                            break;
                        }
                    }
                    if(leftHandAssignMentIsDO) {
                        // doing data output
                        DataOutputAssociation doa = Bpmn2Factory.eINSTANCE.createDataOutputAssociation();
                        for(DataOutput dout : dataOutputs) {
                            if(dout.getId().equals(task.getId() + "_" + assignmentParts[0] + "Output")) {
                                doa.getSourceRef().add(dout);
                                break;
                            }
                        }
                        
                        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                        ie.setId(assignmentParts[1]);
                        doa.setTargetRef(ie);
                        task.getDataOutputAssociations().add(doa);
                    } else {
                        // doing data input
                        DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
                        // association from process var to dataInput var
                        ItemAwareElement ie = Bpmn2Factory.eINSTANCE.createItemAwareElement();
                        ie.setId(assignmentParts[0]);
                        dia.getSourceRef().add(ie);

                        List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
                        for(DataInput di : dataInputs) {
                            if(di.getId().equals(task.getId() + "_" + assignmentParts[1] + "Input")) {
                                dia.setTargetRef(di);
                                break;
                            }
                        }
                        task.getDataInputAssociations().add(dia);
                    }
                } else {
                    // TODO throw exception here?
                }
            }
            
            // check if multiple taskname datainput associations exist and remove them
            List<DataInputAssociation> dataInputAssociations = task.getDataInputAssociations();
            boolean haveTaskNameInput = false;
            for(Iterator<DataInputAssociation> itr = dataInputAssociations.iterator(); itr.hasNext();)  
            {  
                DataInputAssociation da = itr.next();
                if(da.getAssignment() != null && da.getAssignment().size() > 0) {
                    Assignment a = da.getAssignment().get(0);
                    if(((FormalExpression) a.getTo()).getBody().equals(task.getId() + "_TaskNameInput")) {
                        if(!haveTaskNameInput) {
                            haveTaskNameInput = true;
                        } else {
                            itr.remove();
                        }
                    }
                }
            }  
        }
        
        // process on-entry and on-exit actions as custom elements
        if(properties.get("onentryactions") != null && properties.get("onentryactions").length() > 0) {
            String[] allActions = properties.get("onentryactions").split( "\\|\\s*" );
            for(String action : allActions) {
                OnEntryScriptType onEntryScript = EmfextmodelFactory.eINSTANCE.createOnEntryScriptType();
                onEntryScript.setScript(action);
                
                String scriptLanguage = "";
                if(properties.get("script_language").equals("java")) {
                    scriptLanguage = "http://www.java.com/java";
                } else if(properties.get("script_language").equals("mvel")) {
                    scriptLanguage = "http://www.mvel.org/2.0";
                } else {
                    // default to java
                    scriptLanguage = "http://www.java.com/java";
                }
                onEntryScript.setScriptFormat(scriptLanguage); 
                
                ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                task.getExtensionValues().add(extensionElement);
                FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                        (Internal) EmfextmodelPackage.Literals.DOCUMENT_ROOT__ON_ENTRY_SCRIPT, onEntryScript);
                extensionElement.getValue().add(extensionElementEntry);
            }
        }
        
        if(properties.get("onexitactions") != null && properties.get("onexitactions").length() > 0) {
            String[] allActions = properties.get("onexitactions").split( "\\|\\s*" );
            for(String action : allActions) {
                OnExitScriptType onExitScript = EmfextmodelFactory.eINSTANCE.createOnExitScriptType();
                onExitScript.setScript(action);
                
                String scriptLanguage = "";
                if(properties.get("script_language").equals("java")) {
                    scriptLanguage = "http://www.java.com/java";
                } else if(properties.get("script_language").equals("mvel")) {
                    scriptLanguage = "http://www.mvel.org/2.0";
                } else {
                    // default to java
                    scriptLanguage = "http://www.java.com/java";
                }
                onExitScript.setScriptFormat(scriptLanguage); 
                
                ExtensionAttributeValue extensionElement = Bpmn2Factory.eINSTANCE.createExtensionAttributeValue();
                task.getExtensionValues().add(extensionElement);
                FeatureMap.Entry extensionElementEntry = new SimpleFeatureMapEntry(
                        (Internal) EmfextmodelPackage.Literals.DOCUMENT_ROOT__ON_EXIT_SCRIPT, onExitScript);
                extensionElement.getValue().add(extensionElementEntry);
            }
        }
    }
    
    private void applyUserTaskProperties(UserTask task, Map<String, String> properties) {
        if(properties.get("actors") != null && properties.get("actors").length() > 0) {
            String[] allActors = properties.get("actors").split( ",\\s*" );
            for(String actor : allActors) {
                PotentialOwner po = Bpmn2Factory.eINSTANCE.createPotentialOwner();
                ResourceAssignmentExpression rae = Bpmn2Factory.eINSTANCE.createResourceAssignmentExpression();
                FormalExpression fe = Bpmn2Factory.eINSTANCE.createFormalExpression();
                fe.setBody(actor);
                rae.setExpression(fe);
                po.setResourceAssignmentExpression(rae);
                task.getResources().add(po);
            }
        }
        if(properties.get("script_language") != null && properties.get("script_language").length() > 0) {
            String scriptLanguage = "";
            if(properties.get("script_language").equals("java")) {
                scriptLanguage = "http://www.java.com/java";
            } else if(properties.get("script_language").equals("mvel")) {
                scriptLanguage = "http://www.mvel.org/2.0";
            } else {
                // default to java
                scriptLanguage = "http://www.java.com/java";
            }
            
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl scriptLanguageElement = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools", "scriptFormat", false   , false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(scriptLanguageElement,
                    scriptLanguage);
            task.getAnyAttribute().add(extensionEntry);
        }
        
        if(properties.get("groupid") != null && properties.get("groupid").length() > 0) {
        	if(task.getIoSpecification() == null) {
                InputOutputSpecification iospec = Bpmn2Factory.eINSTANCE.createInputOutputSpecification();
                task.setIoSpecification(iospec);
            }
        	List<DataInput> dataInputs = task.getIoSpecification().getDataInputs();
        	boolean foundGroupIdInput = false;
        	DataInput foundInput = null;
        	for(DataInput din : dataInputs) {
        		if(din.getName().equals("GroupId")) {
        			foundGroupIdInput = true;
        			foundInput = din;
        			break;
        		}
        	}
        	
        	if(!foundGroupIdInput) {
        		DataInput d = Bpmn2Factory.eINSTANCE.createDataInput();
                d.setId(task.getId() + "_" + "GroupId" + "Input");
                d.setName("GroupId");
                task.getIoSpecification().getDataInputs().add(d);
                foundInput = d;
                
                if(task.getIoSpecification().getInputSets() == null || task.getIoSpecification().getInputSets().size() < 1) {
                	InputSet inset = Bpmn2Factory.eINSTANCE.createInputSet();
                	task.getIoSpecification().getInputSets().add(inset);
                }
                task.getIoSpecification().getInputSets().get(0).getDataInputRefs().add(d);
        	}
        	
        	boolean foundGroupIdAssociation = false;
        	List<DataInputAssociation> inputAssociations = task.getDataInputAssociations();
        	for(DataInputAssociation da : inputAssociations) {
        		if(da.getTargetRef().getId().equals(foundInput.getId())) {
        			foundGroupIdAssociation = true;
        			((FormalExpression) da.getAssignment().get(0).getFrom()).setBody(properties.get("groupid"));
        		}
        	}
        	
        	if(!foundGroupIdAssociation) {
        		DataInputAssociation dia = Bpmn2Factory.eINSTANCE.createDataInputAssociation();
        		dia.setTargetRef(foundInput);
        		
        		Assignment a = Bpmn2Factory.eINSTANCE.createAssignment();
                FormalExpression groupFromExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                groupFromExpression.setBody(properties.get("groupid"));
                
                FormalExpression groupToExpression = Bpmn2Factory.eINSTANCE.createFormalExpression();
                groupToExpression.setBody(foundInput.getId());
                
                a.setFrom(groupFromExpression);
                a.setTo(groupToExpression);
                
                dia.getAssignment().add(a);
                task.getDataInputAssociations().add(dia);
        	}
        }
    }
    
    private void applyGatewayProperties(Gateway gateway, Map<String, String> properties) {
        if(properties.get("name") != null) {
            gateway.setName(properties.get("name"));
        } else {
            gateway.setName("");
        }
        if(properties.get("defaultgate") != null && gateway instanceof InclusiveGateway) {
        	ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl extensionAttribute = (EAttributeImpl) metadata.demandFeature(
                        "http://www.jboss.org/drools", "dg", false, false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(extensionAttribute,
                properties.get("defaultgate"));
            gateway.getAnyAttribute().add(extensionEntry);
        }
    }

    private void applySequenceFlowProperties(SequenceFlow sequenceFlow, Map<String, String> properties) {
        // sequence flow name is options
        if(properties.get("name") != null && !"".equals(properties.get("name"))) {
            sequenceFlow.setName(properties.get("name"));
        }
        if (properties.get("auditing") != null && !"".equals(properties.get("auditing"))) {
            Auditing audit = Bpmn2Factory.eINSTANCE.createAuditing();
            audit.getDocumentation().add(createDocumentation(properties.get("auditing")));
            sequenceFlow.setAuditing(audit);
        }
        if (properties.get("conditionexpression") != null && !"".equals(properties.get("conditionexpression"))) {
            FormalExpression expr = Bpmn2Factory.eINSTANCE.createFormalExpression();
            expr.setBody(properties.get("conditionexpression"));
            // check if language was specified 
            if (properties.get("conditionexpressionlanguage") != null && !"".equals(properties.get("conditionexpressionlanguage"))) {
                String languageStr;
                if(properties.get("conditionexpressionlanguage").equals("drools")) {
                    languageStr = "http://www.jboss.org/drools/rule";
                } else if(properties.get("conditionexpressionlanguage").equals("mvel")) {
                    languageStr = "http://www.mvel.org/2.0";
                } else if(properties.get("conditionexpressionlanguage").equals("java")) {
                    languageStr = "http://www.java.com/java";
                } else {
                    // default to mvel
                    languageStr = "http://www.mvel.org/2.0";
                }
                expr.setLanguage(languageStr);
            }
            sequenceFlow.setConditionExpression(expr);
        }
        
        if (properties.get("priority") != null && !"".equals(properties.get("priority"))) {
            ExtendedMetaData metadata = ExtendedMetaData.INSTANCE;
            EAttributeImpl priorityElement = (EAttributeImpl) metadata.demandFeature(
                    "http://www.jboss.org/drools", "priority", false   , false);
            EStructuralFeatureImpl.SimpleFeatureMapEntry extensionEntry = new EStructuralFeatureImpl.SimpleFeatureMapEntry(priorityElement,
                    properties.get("priority"));
            sequenceFlow.getAnyAttribute().add(extensionEntry);
        }
        
        if (properties.get("monitoring") != null && !"".equals(properties.get("monitoring"))) {
            Monitoring monitoring = Bpmn2Factory.eINSTANCE.createMonitoring();
            monitoring.getDocumentation().add(createDocumentation(properties.get("monitoring")));
            sequenceFlow.setMonitoring(monitoring);
        }
        sequenceFlow.setIsImmediate(Boolean.parseBoolean(properties.get("isimmediate")));
    }

    private Map<String, String> unmarshallProperties(JsonParser parser) throws JsonParseException, IOException {
        Map<String, String> properties = new HashMap<String, String>();
        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldname = parser.getCurrentName();
            parser.nextToken();
            properties.put(fieldname, parser.getText());
        }
        return properties;
    }

    private Documentation createDocumentation(String text) {
        Documentation doc = Bpmn2Factory.eINSTANCE.createDocumentation();
        doc.setText(text);
        return doc;
    }
    
    private boolean isCustomElement(String taskType, String preProcessingData) {
        if(taskType != null && taskType.length() > 0 && preProcessingData != null && preProcessingData.length() > 0) {
            String[] preProcessingDataElements = preProcessingData.split( ",\\s*" );
            for(String preProcessingDataElement : preProcessingDataElements) {
                if(taskType.equals(preProcessingDataElement)) {
                    return true;
                }
            }
        }
        return false;
    }
}

