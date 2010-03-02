/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.hpi.bpmn2_0.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import de.hpi.bpmn2_0.model.choreography.Choreography;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.MessageFlow;
import de.hpi.bpmn2_0.model.conversation.Conversation;
import de.hpi.bpmn2_0.model.conversation.ConversationLink;
import de.hpi.bpmn2_0.model.data_object.Message;
import de.hpi.bpmn2_0.model.diagram.BpmnDiagram;
import de.hpi.bpmn2_0.model.diagram.ChoreographyDiagram;
import de.hpi.bpmn2_0.model.diagram.CollaborationDiagram;
import de.hpi.bpmn2_0.model.diagram.ConversationDiagram;
import de.hpi.bpmn2_0.model.diagram.ProcessDiagram;
import de.hpi.bpmn2_0.model.event.CompensateEventDefinition;
import de.hpi.bpmn2_0.model.event.EventDefinition;
import de.hpi.bpmn2_0.validation.BPMN2SyntaxChecker;

/**
 * <p>
 * Java class for tDefinitions complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="tDefinitions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.omg.org/bpmn20}import" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}extension" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}rootElement" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://bpmndi.org}diagram" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.omg.org/bpmn20}relationship" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="targetNamespace" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="expressionLanguage" type="{http://www.w3.org/2001/XMLSchema}anyURI" default="http://www.w3.org/1999/XPath" />
 *       &lt;attribute name="typeLanguage" type="{http://www.w3.org/2001/XMLSchema}anyURI" default="http://www.w3.org/2001/XMLSchema" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "definitions")
@XmlType(name = "tDefinitions", propOrder = {
		"targetNamespace",
		"expressionLanguage",
		"typeLanguage",
		"rootElement", 
		"diagram"
})
public class Definitions {

	// @XmlElement(name = "import")
	// protected List<TImport> _import;
	// protected List<TExtension> extension;
	
	@XmlElementRefs( { @XmlElementRef(type = Process.class),
			@XmlElementRef(type = Choreography.class),
			@XmlElementRef(type = Collaboration.class),
			@XmlElementRef(type = Conversation.class) })
	protected List<RootElement> rootElement;
	
	@XmlElementRefs( { @XmlElementRef(type = ProcessDiagram.class),
			@XmlElementRef(type = CollaborationDiagram.class),
			@XmlElementRef(type = ChoreographyDiagram.class),
			@XmlElementRef(type = ConversationDiagram.class) })
	protected List<BpmnDiagram> diagram;
	// protected List<TRelationship> relationship;
	
	@XmlAttribute
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlID
	@XmlSchemaType(name = "ID")
	protected String id;

	@XmlAttribute(required = true)
	@XmlSchemaType(name = "anyURI")
	protected String targetNamespace;

	@XmlAttribute
	@XmlSchemaType(name = "anyURI")
	protected String expressionLanguage;
	
	@XmlAttribute
	@XmlSchemaType(name = "anyURI")
	protected String typeLanguage;

	@XmlAnyAttribute
	private Map<QName, String> otherAttributes = new HashMap<QName, String>();

	@XmlTransient
	private Map<String, String> namespaces;

	/**
	 * 
	 * @return
	 */
	public ConversationDiagram getConversationDiagram() {
		for (BpmnDiagram dia : this.getDiagram()) {
			if (dia instanceof ConversationDiagram) {
				return (ConversationDiagram) dia;
			}
		}

		return null;
	}

	public ChoreographyDiagram getChoreographyDiagram() {
		for (BpmnDiagram dia : this.getDiagram()) {
			if (dia instanceof ChoreographyDiagram) {
				return (ChoreographyDiagram) dia;
			}
		}

		return null;
	}

	public CollaborationDiagram getCollaborationDiagram() {
		for (BpmnDiagram dia : this.getDiagram()) {
			if (dia instanceof CollaborationDiagram) {
				return (CollaborationDiagram) dia;
			}
		}

		return null;
	}

	public ProcessDiagram getProcessDiagram() {
		for (BpmnDiagram dia : this.getDiagram()) {
			if (dia instanceof ProcessDiagram) {
				return (ProcessDiagram) dia;
			}
		}

		return null;
	}
	
	/**
	 * The {@link Marshaller} invokes this method right before marshaling to 
	 * XML. The namespace are added as attributes to the definitions element.
	 * @param marshaller 
	 * 		The marshaling context
	 */
	public void beforeMarshal(Marshaller marshaller) {
		for (String prefix : this.getNamespaces().keySet()) {
			QName namespacePrefix = new QName("xmlns:" + prefix);
			this.getOtherAttributes().put(namespacePrefix,
					this.getNamespaces().get(prefix));
		}
	}

	/* Getter & Setter */

	/**
	 * Gets the value of the import property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the import property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getImport().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link TImport }
	 * 
	 * 
	 */
	// public List<TImport> getImport() {
	// if (_import == null) {
	// _import = new ArrayList<TImport>();
	// }
	// return this._import;
	// }

	/**
	 * Gets the value of the extension property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the extension property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getExtension().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link TExtension }
	 * 
	 * 
	 */
	// public List<TExtension> getExtension() {
	// if (extension == null) {
	// extension = new ArrayList<TExtension>();
	// }
	// return this.extension;
	// }

	/**
	 * Gets the value of the rootElement property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the rootElement property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getRootElement().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link JAXBElement }{@code <}{@link TMessageEventDefinition }{@code >}
	 * {@link JAXBElement }{@code <}{@link TGlobalBusinessRuleTask }{@code >}
	 * {@link JAXBElement }{@code <}{@link ErrorEventDefinition }{@code >}
	 * {@link JAXBElement }{@code <}{@link Collaboration }{@code >}
	 * {@link JAXBElement }{@code <}{@link Conversation }{@code >}
	 * {@link JAXBElement }{@code <}{@link ConditionalEventDefinition }{@code >}
	 * {@link JAXBElement }{@code <}{@link Process }{@code >} {@link JAXBElement }
	 * {@code <}{@link Category }{@code >} {@link JAXBElement }{@code <}
	 * {@link TItemDefinition }{@code >} {@link JAXBElement }{@code <}
	 * {@link TEscalationEventDefinition }{@code >} {@link JAXBElement }{@code <}
	 * {@link Message }{@code >} {@link JAXBElement }{@code <}
	 * {@link CompensateEventDefinition }{@code >} {@link JAXBElement }{@code <}
	 * {@link TCorrelationProperty }{@code >} {@link JAXBElement }{@code <}
	 * {@link TEscalation }{@code >} {@link JAXBElement }{@code <}
	 * {@link TInterface }{@code >} {@link JAXBElement }{@code <}
	 * {@link TTimerEventDefinition }{@code >} {@link JAXBElement }{@code <}
	 * {@link TGlobalUserTask }{@code >} {@link JAXBElement }{@code <}
	 * {@link GlobalCommunication }{@code >} {@link JAXBElement }{@code <}
	 * {@link Choreography }{@code >} {@link JAXBElement }{@code <}
	 * {@link TResource }{@code >} {@link JAXBElement }{@code <}
	 * {@link SignalEventDefinition }{@code >} {@link JAXBElement }{@code <}
	 * {@link TEndPoint }{@code >} {@link JAXBElement }{@code <}
	 * {@link RootElement }{@code >} {@link JAXBElement }{@code <}
	 * {@link TPartnerEntity }{@code >} {@link JAXBElement }{@code <}
	 * {@link TGlobalManualTask }{@code >} {@link JAXBElement }{@code <}
	 * {@link TGlobalScriptTask }{@code >} {@link JAXBElement }{@code <}
	 * {@link EventDefinition }{@code >} {@link JAXBElement }{@code <}
	 * {@link TError }{@code >} {@link JAXBElement }{@code <}
	 * {@link LinkEventDefinition }{@code >} {@link JAXBElement }{@code <}
	 * {@link CancelEventDefinition }{@code >} {@link JAXBElement }{@code <}
	 * {@link TPartnerRole }{@code >} {@link JAXBElement }{@code <}
	 * {@link TGlobalTask }{@code >} {@link JAXBElement }{@code <}{@link TSignal }
	 * {@code >} {@link JAXBElement }{@code <}{@link TTerminateEventDefinition }
	 * {@code >} {@link JAXBElement }{@code <}{@link TGlobalChoreographyTask }
	 * {@code >}
	 * 
	 * 
	 */
	public List<RootElement> getRootElement() {
		if (rootElement == null) {
			rootElement = new ArrayList<RootElement>();
		}
		return this.rootElement;
	}

	/**
	 * This Method return a List containing all edges within the diagram
	 * 
	 * @return List<Edge>
	 */
	public List<Edge> getEdges() {
		if (this.rootElement == null) {
			return new ArrayList<Edge>();
		}

		ArrayList<Edge> edges = new ArrayList<Edge>();

		for (RootElement rootElement : this.rootElement) {

			if (rootElement instanceof Process) {

				for (FlowElement flowElement : ((Process) rootElement)
						.getFlowElement()) {

					if (flowElement instanceof Edge) {
						edges.add((Edge) flowElement);
					}
				}
			} else if (rootElement instanceof Collaboration) {

				for (MessageFlow messageFlow : ((Collaboration) rootElement)
						.getMessageFlow())
					edges.add(messageFlow);

			} else if (rootElement instanceof Choreography) {

				for (FlowElement flowElement : ((Choreography) rootElement)
						.getFlowElement()) {

					if (flowElement instanceof Edge)
						edges.add((Edge) flowElement);
				}

			} else if (rootElement instanceof Conversation) {

				for (ConversationLink cl : ((Conversation) rootElement)
						.getConversationLink())
					edges.add(cl);

				for (MessageFlow mf : ((Conversation) rootElement)
						.getMessageFlow())
					edges.add(mf);
			}
		}

		return edges;
	}

	/**
	 * Gets the value of the diagram property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the diagram property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDiagram().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link JAXBElement }{@code <}{@link BpmnDiagram }{@code >}
	 * {@link JAXBElement }{@code <}{@link CollaborationDiagram }{@code >}
	 * {@link JAXBElement }{@code <}{@link ProcessDiagram }{@code >}
	 * 
	 * 
	 */
	public List<BpmnDiagram> getDiagram() {
		if (diagram == null) {
			diagram = new ArrayList<BpmnDiagram>();
		}
		return this.diagram;
	}

	public BPMN2SyntaxChecker getSyntaxChecker() {
		return new BPMN2SyntaxChecker(this);
	}

	/**
	 * Gets the value of the relationship property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the relationship property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getRelationship().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link TRelationship }
	 * 
	 * 
	 */
	// public List<TRelationship> getRelationship() {
	// if (relationship == null) {
	// relationship = new ArrayList<TRelationship>();
	// }
	// return this.relationship;
	// }

	/**
	 * Gets the value of the id property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setId(String value) {
		this.id = value;
	}

	/**
	 * Gets the value of the targetNamespace property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTargetNamespace() {
		return targetNamespace;
	}

	/**
	 * Sets the value of the targetNamespace property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTargetNamespace(String value) {
		this.targetNamespace = value;
	}

	/**
	 * Gets the value of the expressionLanguage property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExpressionLanguage() {
		if (expressionLanguage == null) {
			return "http://www.w3.org/1999/XPath";
		} else {
			return expressionLanguage;
		}
	}

	/**
	 * Sets the value of the expressionLanguage property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExpressionLanguage(String value) {
		this.expressionLanguage = value;
	}

	/**
	 * Gets the value of the typeLanguage property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTypeLanguage() {
		if (typeLanguage == null) {
			return "http://www.w3.org/2001/XMLSchema";
		} else {
			return typeLanguage;
		}
	}

	/**
	 * Sets the value of the typeLanguage property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTypeLanguage(String value) {
		this.typeLanguage = value;
	}

	/**
	 * Gets a map that contains attributes that aren't bound to any typed
	 * property on this class.
	 * 
	 * <p>
	 * the map is keyed by the name of the attribute and the value is the string
	 * value of the attribute.
	 * 
	 * the map returned by this method is live, and you can add new attribute by
	 * updating the map directly. Because of this design, there's no setter.
	 * 
	 * 
	 * @return always non-null
	 */
	public Map<QName, String> getOtherAttributes() {
		return otherAttributes;
	}

	/**
	 * The namespaces property contains an arbitrary set of namespace prefixes
	 * their related URLs referenced inside the diagram.
	 * 
	 * @return the namespaces property. always non-null
	 */
	public Map<String, String> getNamespaces() {
		if (this.namespaces == null) {
			this.namespaces = new HashMap<String, String>();
		}

		return namespaces;
	}

}
