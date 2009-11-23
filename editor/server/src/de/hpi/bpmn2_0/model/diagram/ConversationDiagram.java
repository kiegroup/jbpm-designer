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

package de.hpi.bpmn2_0.model.diagram;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;

import de.hpi.bpmn2_0.model.conversation.Conversation;

/**
 * Class representing a conversation diagram
 * 
 * @author Sven Wagner-Boysen
 *
 */
@XmlRootElement(name = "conversationDiagram")
@XmlAccessorType(XmlAccessType.FIELD)
public class ConversationDiagram extends BpmnDiagram {
	@XmlElementRefs({
		/* Conversation nodes */
		@XmlElementRef(type = CommunicationShape.class),
		@XmlElementRef(type = SubConversationShape.class),
		@XmlElementRef(type = ConversationParticipantShape.class),
		@XmlElementRef(type = CallConversationShape.class)
	})
	protected List<BpmnNode> shape;
	
	@XmlElementRefs({
		@XmlElementRef(type = ConversationLinkConnector.class)
	})
	protected List<BpmnConnector> connector;
	
	@XmlElementRef(type = ConversationParticipantShape.class)
	protected List<ConversationParticipantShape> participant;
	
	@XmlIDREF
	@XmlAttribute
	protected Conversation conversation;
	
	/* Getter & Setter */
	
	/**
	 * @return the shape
	 */
	public List<BpmnNode> getShape() {
		if(this.shape == null) {
			this.shape = new ArrayList<BpmnNode>();
		}
		return this.shape;
	}

	/**
	 * @return the conversation
	 */
	public Conversation getConversation() {
		return conversation;
	}

	/**
	 * @param conversation the conversation to set
	 */
	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	/**
	 * @return the connector
	 */
	public List<BpmnConnector> getConnector() {
		if(this.connector == null) {
			this.connector = new ArrayList<BpmnConnector>();
		}
		return this.connector;
	}

	/**
	 * @return the participant
	 */
	public List<ConversationParticipantShape> getParticipant() {
		if(this.participant == null) {
			this.participant = new ArrayList<ConversationParticipantShape>();
		}
		return participant;
	}
}
