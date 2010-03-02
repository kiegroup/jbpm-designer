<?xml version="1.0" encoding="UTF-8"?>
<!-- edited with XMLSpy v2009 sp1 (http://www.altova.com) by Robert Shapiro (private) -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xpdl="http://www.wfmc.org/2008/XPDL2.1" xmlns="http://www.wfmc.org/2008/XPDL2.1" xmlns:b="http://schema.omg.org/spec/BPMN/2.0" xmlns:bpmndi="http://bpmndi.org" xsi:schemaLocation="http://www.wfmc.org/2008/XPDL2.1 C:\Users\shapiror\Documents\mydocs\capevisions\XPDL2~1.1\VERSIO~1\bpmnxpdl_31d.xsd">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:variable name="processDiagram"/>
	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="b:definitions">
		<Package Id="{generate-id(.)}" xsi:schemaLocation="http://www.wfmc.org/2008/XPDL2.1 C:\Users\shapiror\Documents\mydocs\capevisions\XPDL2~1.1\VERSIO~1\bpmnxpdl_31d.xsd">
			<PackageHeader>
				<XPDLVersion/>
				<Vendor/>
				<Created/>
			</PackageHeader>
			<!--Here we need to puy in logic for generating Participants, Pages, Pools, MessageFlows, Associations, Artifacts etc.-->
			<Participants>
				<xsl:for-each select="/descendant::b:resource">
					<Participant Id="{./@id}" Name="{./@name}">
						<ParticipantType Type="ROLE"/>
					</Participant>
				</xsl:for-each>
			</Participants>
			<Pages>
				<xsl:for-each select="/descendant::bpmndi:collaborationDiagram">
					<!--Instead of assuming that the poolid has a suffix which indicates page, we could assume that the collaboration diagrams are in page order and use positiion() as the pageid.???-->
					<xsl:variable name="poolId" select="bpmndi:pool[1]/@id"/>
					<xsl:variable name="pageId" select='substring-after(string($poolId),".")'/>
					<Page Id="{$pageId}" Name="{./@name}"/>
				</xsl:for-each>
				<xsl:if test="count(/descendant::bpmndi:collaborationDiagram)=0">
					<Page Id="1"/>
				</xsl:if>
				<xsl:for-each select="/descendant::bpmndi:subprocessShape [@isExpanded = true()]">
					<!--[@isExpanded = true()]-->
					<xsl:variable name="pagebump" select="count(/descendant::bpmndi:collaborationDiagram)"/>
					<Page Name="{./@name}">
						<xsl:choose>
							<xsl:when test="$pagebump=0">
								<xsl:attribute name="Id" select="1+position()"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:attribute name="Id" select="$pagebump+position()"/>
							</xsl:otherwise>
						</xsl:choose>
					</Page>
				</xsl:for-each>
			</Pages>
			<Pools>
				<xsl:for-each select="/descendant::bpmndi:collaborationDiagram">
					<xsl:for-each select="./bpmndi:pool">
						<!--pass position() to pool template to be used as pageid.???-->
						<xsl:call-template name="pool"/>
					</xsl:for-each>
				</xsl:for-each>
				<xsl:if test="count(/descendant::bpmndi:collaborationDiagram)=0">
					<!--In case there are no pools, make one-->
					<Pool BoundaryVisible="{false()}" Id="1">
						<xsl:attribute name="Process" select="/descendant::b:process/@id"/>
						<NodeGraphicsInfos>
							<!--Other important attributes are ToolId, PageId and LaneId-->
							<NodeGraphicsInfo PageId="1">
								<Coordinates XCoordinate="0" YCoordinate="0"/>
							</NodeGraphicsInfo>
						</NodeGraphicsInfos>
					</Pool>
				</xsl:if>
				<xsl:for-each select="/descendant::bpmndi:subprocessShape [@isExpanded = true()]">
					<!--this is for embedded expanded subprocesses-->
					<Pool Id="{./@id}" Name="{./@name}" BoundaryVisible="{false()}" Process="{./@activityRef}">
						<NodeGraphicsInfos>
							<!--Other important attributes are ToolId, PageId and LaneId-->
							<NodeGraphicsInfo>
								<!--{$pageId}-->
								<xsl:variable name="pagebump" select="count(/descendant::bpmndi:collaborationDiagram)"/>
								<xsl:choose>
									<xsl:when test="$pagebump=0">
										<xsl:attribute name="PageId" select="1+position()"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:attribute name="PageId" select="$pagebump+position()"/>
									</xsl:otherwise>
								</xsl:choose>
								<!--These coordinates probably have to be adjusted-->
								<Coordinates XCoordinate="0" YCoordinate="0"/>
							</NodeGraphicsInfo>
						</NodeGraphicsInfos>
					</Pool>
				</xsl:for-each>
			</Pools>
			<xsl:if test="count(descendant::b:messageFlow)&gt;0">
				<MessageFlows>
					<xsl:for-each select="descendant::b:messageFlow">
						<xsl:call-template name="messageFlow"/>
					</xsl:for-each>
				</MessageFlows>
			</xsl:if>
			<xsl:if test="count(descendant::b:association)+count(descendant::b:dataInputAssociation)+count(descendant::b:dataOutputAssociation)&gt;100">
				<Associations>
					<xsl:for-each select="descendant::b:association">
						<xsl:call-template name="association"/>
					</xsl:for-each>
					<xsl:for-each select="descendant::b:dataInputAssociation">
						<xsl:call-template name="association"/>
					</xsl:for-each>
					<xsl:for-each select="descendant::b:dataOutputAssociation">
						<xsl:call-template name="association"/>
					</xsl:for-each>
				</Associations>
			</xsl:if>
			<xsl:if test="count(descendant::b:dataObject)+count(descendant::b:textAnnotation)&gt;100">
				<Artifacts>
					<xsl:for-each select="descendant::b:dataObject">
						<xsl:call-template name="dataObject"/>
					</xsl:for-each>
					<xsl:for-each select="descendant::b:textAnnotation">
						<xsl:call-template name="textAnnotation"/>
					</xsl:for-each>
				</Artifacts>
			</xsl:if>
			<WorkflowProcesses>
				<xsl:for-each select="b:process">
					<xsl:call-template name="process"/>
				</xsl:for-each>
			</WorkflowProcesses>
		</Package>
	</xsl:template>
	<xsl:template name="pool">
		<!--have to figure out if this is a top level pool. In a collaboration the isVisible flag might work. If not then need to test that situation. Lets try to do that. What about multiple copies of empty pool? Maybe we need to consider constructing messages from the di part?-->
		<!--add pageid as param provided by caller,  based on position() of collaboration diagram.???-->
		<Pool Id="{./@id}" Name="{./@name}" BoundaryVisible="{./@isVisible}">
			<!--replace with pageid param???-->
			<xsl:variable name="page" select='substring-after(string(./@id),".")'/>
			<xsl:variable name="boundary" select="./@isVisible"/>
			<xsl:variable name="participantRef" select="./@participantRef"/>
			<xsl:for-each select="/descendant::b:participant[@id = $participantRef ]">
				<xsl:variable name="processId" select='concat(./@processRef,".",$page)'/>
				<xsl:for-each select="/descendant::bpmndi:processDiagram[@id=$processId]">
					<xsl:attribute name="Process" select="./@processRef"/>
				</xsl:for-each>
				<xsl:if test="true()">
					<!--previously testing boundary for true-->
					<Lanes>
						<xsl:for-each select="/descendant::bpmndi:processDiagram[@id=$processId]">
							<xsl:variable name="processRef" select="./processRef"/>
							<xsl:for-each select="./bpmndi:laneCompartment">
								<Lane Id="{./@id}" Name="{./@name}">
									<xsl:call-template name="nodeGraphics"/>
								</Lane>
							</xsl:for-each>
						</xsl:for-each>
					</Lanes>
				</xsl:if>
			</xsl:for-each>
			<xsl:choose>
				<xsl:when test="$boundary=false()">
					<NodeGraphicsInfos>
						<!--Other important attributes are ToolId, PageId and LaneId-->
						<NodeGraphicsInfo>
							<!--{$pageId}-->
							<xsl:attribute name="PageId" select="$page"/>
							<!--These coordinates probably have to be adjusted-->
							<Coordinates XCoordinate="0" YCoordinate="0"/>
						</NodeGraphicsInfo>
					</NodeGraphicsInfos>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="nodeGraphics"/>
				</xsl:otherwise>
			</xsl:choose>
		</Pool>
	</xsl:template>
	<xsl:template name="messageFlow">
		<MessageFlow Id="{./@id}">
			<xsl:call-template name="messageFlowSource"/>
			<xsl:call-template name="messageFlowTarget"/>
			<xsl:for-each select="./@name">
				<xsl:attribute name="Name" select="."/>
			</xsl:for-each>
			<xsl:variable name="transition" select="./@id"/>
			<xsl:for-each select="/descendant::bpmndi:messageFlowConnector[@messageFlowRef=$transition]">
				<xsl:call-template name="connectorGraphics"/>
			</xsl:for-each>
		</MessageFlow>
	</xsl:template>
	<xsl:template name="messageFlowSource">
		<xsl:variable name="sourceOrTargetId" select="string(./@sourceRef)"/>
		<xsl:choose>
			<xsl:when test="count(/descendant::b:participant[@id=$sourceOrTargetId])&gt;0">
				<xsl:variable name="transition" select="./@id"/>
				<xsl:variable name="messageflow" select="/descendant::bpmndi:messageFlowConnector[@messageFlowRef=$transition]"/>
				<xsl:attribute name="Source" select="$messageflow/@sourceRef"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="Source" select="./@sourceRef"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="messageFlowTarget">
		<xsl:variable name="sourceOrTargetId" select="string(./@targetRef)"/>
		<xsl:choose>
			<xsl:when test="count(/descendant::b:participant[@id=$sourceOrTargetId])&gt;0">
				<xsl:variable name="transition" select="./@id"/>
				<xsl:variable name="messageflow" select="/descendant::bpmndi:messageFlowConnector[@messageFlowRef=$transition]"/>
				<xsl:attribute name="Target" select="$messageflow/@targetRef"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="Target" select="./@targetRef"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="association">
		<Association Id="{./@id}" Name="{./@name}" AssociationDirection="To">
			<xsl:attribute name="Source"><xsl:choose><xsl:when test="./b:sourceRef"><xsl:value-of select="./b:sourceRef"/></xsl:when><xsl:otherwise><xsl:value-of select="./@sourceRef"/></xsl:otherwise></xsl:choose></xsl:attribute>
			<xsl:attribute name="Target"><xsl:choose><xsl:when test="./b:targetRef"><xsl:value-of select="./b:targetRef"/></xsl:when><xsl:otherwise><xsl:value-of select="./@targetRef"/></xsl:otherwise></xsl:choose></xsl:attribute>
			<Object Id="{./@id}" Name="{./@name}"/>
			<xsl:variable name="transition" select="./@id"/>
			<!--in the bpmn:di what kind of a ref will this be?-->
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:associationConnector[@associationRef=$transition]">
				<xsl:call-template name="connectorGraphics"/>
			</xsl:for-each>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:dataAssociationConnector[@dataAssociationRef=$transition]">
				<xsl:call-template name="connectorGraphics"/>
			</xsl:for-each>
		</Association>
	</xsl:template>
	<xsl:template name="dataObject">
		<Artifact Id="{./@id}" Name="{./@name}" ArtifactType="DataObject">
			<DataObject Id="{./@id}" Name="{./@name}"/>
			<xsl:variable name="nodeId" select="./@id"/>
			<!--in the bpmn:di what kind of a ref will this be?-->
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:subprocessShape[@activityRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Artifact>
	</xsl:template>
	<xsl:template name="textAnnotation">
		<Artifact Id="{./@id}" Name="{./@name}" ArtifactType="Annotation">
			<xsl:attribute name="TextAnnotation"><xsl:copy-of select="."/></xsl:attribute>
			<xsl:variable name="nodeId" select="./@id"/>
			<!--in the bpmn:di what kind of a ref will this be?-->
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:subprocessShape[@activityRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Artifact>
	</xsl:template>
	<xsl:template name="process">
		<WorkflowProcess Id="{./@id}" Name="{./@name}">
			<ProcessHeader/>
			<ActivitySets>
				<xsl:for-each select="b:subProcess">
					<xsl:call-template name="subProcess"/>
				</xsl:for-each>
			</ActivitySets>
			<xsl:call-template name="activities"/>
			<xsl:call-template name="transitions"/>
		</WorkflowProcess>
	</xsl:template>
	<xsl:template name="subProcess">
		<ActivitySet Id="{./@id}" Name="{./@name}">
			<xsl:call-template name="activities"/>
			<xsl:call-template name="transitions"/>
		</ActivitySet>
	</xsl:template>
	<xsl:template name="transitions">
		<Transitions>
			<xsl:for-each select="./b:sequenceFlow">
				<Transition Id="{./@id}" From="{./@sourceRef}" To="{./@targetRef}">
					<!--we also need to deal with the Condition element-->
					<xsl:for-each select="./@name">
						<xsl:attribute name="Name" select="."/>
					</xsl:for-each>
					<xsl:variable name="sourceactivity" select="./@sourceRef"/>
					<xsl:variable name="transition" select="./@id"/>
					<xsl:choose>
						<xsl:when test="../*[@default=$transition]">
							<Condition Type="OTHERWISE"/>
						</xsl:when>
						<xsl:when test="./b:conditionExpression">
							<Condition Type="CONDITION">
								<Expression>
									<xsl:value-of select="./b:conditionExpression"/>
								</Expression>
							</Condition>
						</xsl:when>
					</xsl:choose>
					<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:sequenceFlowConnector[@sequenceFlowRef=$transition]">
						<xsl:call-template name="connectorGraphics"/>
					</xsl:for-each>
				</Transition>
			</xsl:for-each>
		</Transitions>
	</xsl:template>
	<xsl:template name="activities">
		<Activities>
			<xsl:for-each select="./b:inclusiveGateway">
				<xsl:call-template name="inclusiveGateway"/>
			</xsl:for-each>
			<xsl:for-each select="./b:exclusiveGateway">
				<xsl:call-template name="exclusiveGateway"/>
			</xsl:for-each>
			<xsl:for-each select="./b:eventBasedGateway">
				<xsl:call-template name="eventBasedGateway"/>
			</xsl:for-each>
			<xsl:for-each select="./b:parallelGateway">
				<xsl:call-template name="parallelGateway"/>
			</xsl:for-each>
			<xsl:for-each select="./b:startEvent">
				<xsl:call-template name="startEvent"/>
			</xsl:for-each>
			<xsl:for-each select="./b:endEvent">
				<xsl:call-template name="endEvent"/>
			</xsl:for-each>
			<xsl:for-each select="./b:intermediateThrowEvent">
				<xsl:call-template name="intermediateThrowEvent"/>
			</xsl:for-each>
			<xsl:for-each select="./b:intermediateCatchEvent">
				<xsl:call-template name="intermediateCatchEvent"/>
			</xsl:for-each>
			<xsl:for-each select="./b:sendTask">
				<xsl:call-template name="sendTask"/>
			</xsl:for-each>
			<xsl:for-each select="./b:receiveTask">
				<xsl:call-template name="receiveTask"/>
			</xsl:for-each>
			<xsl:for-each select="./b:serviceTask">
				<xsl:call-template name="serviceTask"/>
			</xsl:for-each>
			<xsl:for-each select="./b:task">
				<xsl:call-template name="task"/>
			</xsl:for-each>
			<xsl:for-each select="./b:userTask">
				<xsl:call-template name="userTask"/>
			</xsl:for-each>
			<xsl:for-each select="./b:subProcess">
				<!--What about other kinds of subprocess, for instance callActivity???-->
				<xsl:call-template name="BlockActivity"/>
			</xsl:for-each>
			<xsl:for-each select="./b:boundaryEvent">
				<xsl:call-template name="boundaryEvent"/>
			</xsl:for-each>
		</Activities>
	</xsl:template>
	<xsl:template name="inclusiveGateway">
		<Activity Id="{./@id}" Name="{./@name}">
			<Route GatewayType="Inclusive"/>
			<xsl:variable name="nodeId" select="./@id"/>
			<!--Other elements under activity before nodegraphics include Performer and TransistionRestriction.  Need to decide whether we will handle implicit resource/performer assignment via lane membership.-->
			<!--Have to generate TransitionRestrictions. We need to figure out gatewayDirection if it is not present. Also have to deal with default, e.g. otherwise.-->
			<TransitionRestrictions>
				<!--We need to determine whether this is a split or join and be able to generate the TransitionRefs for the split case-->
				<TransitionRestriction>
					<xsl:choose>
						<xsl:when test="count(../b:sequenceFlow[@sourceRef=$nodeId])&gt;1">
							<Split Type="Inclusive">
								<xsl:call-template name="transitionrefs">
									<xsl:with-param name="node" select="$nodeId"/>
								</xsl:call-template>
							</Split>
						</xsl:when>
						<xsl:when test="count(../b:sequenceFlow[@targetRef=$nodeId])&gt;1">
							<Join Type="Inclusive"/>
						</xsl:when>
						<xsl:otherwise/>
					</xsl:choose>
				</TransitionRestriction>
			</TransitionRestrictions>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:gatewayShape[@gatewayRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="exclusiveGateway">
		<Activity Id="{./@id}" Name="{./@name}">
			<Route GatewayType="Exclusive" ExclusiveType="Data"/>
			<xsl:variable name="nodeId" select="./@id"/>
			<!--Other elements under activity before nodegraphics include Performer and TransistionRestriction.  Need to decide whether we will handle implicit resource/performer assignment via lane membership.-->
			<!--Have to generate TransitionRestrictions. We need to figure out gatewayDirection if it is not present. Also have to deal with default, e.g. otherwise.-->
			<TransitionRestrictions>
				<!--We need to determine whether this is a split or join and be able to generate the TransitionRefs for the split case-->
				<TransitionRestriction>
					<xsl:choose>
						<xsl:when test="count(../b:sequenceFlow[@sourceRef=$nodeId])&gt;1">
							<Split Type="Exclusive" ExclusiveType="Data">
								<xsl:call-template name="transitionrefs">
									<xsl:with-param name="node" select="$nodeId"/>
								</xsl:call-template>
							</Split>
						</xsl:when>
						<xsl:when test="count(../b:sequenceFlow[@targetRef=$nodeId])&gt;1">
							<Join Type="Exclusive" ExclusiveType="Data"/>
						</xsl:when>
					</xsl:choose>
				</TransitionRestriction>
			</TransitionRestrictions>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:gatewayShape[@gatewayRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="eventBasedGateway">
		<Activity Id="{./@id}" Name="{./@name}">
			<Route GatewayType="Exclusive" ExclusiveType="Event" MarkerVisible="true"/>
			<xsl:variable name="nodeId" select="./@id"/>
			<!--Other elements under activity before nodegraphics include Performer and TransistionRestriction.  Need to decide whether we will handle implicit resource/performer assignment via lane membership.-->
			<!--Have to generate TransitionRestrictions. We need to figure out gatewayDirection if it is not present. Also have to deal with default, e.g. otherwise.-->
			<TransitionRestrictions>
				<!--We need to determine whether this is a split or join and be able to generate the TransitionRefs for the split case-->
				<TransitionRestriction>
					<xsl:choose>
						<xsl:when test="count(../b:sequenceFlow[@sourceRef=$nodeId])&gt;1">
							<Split Type="Exclusive" ExclusiveType="Event">
								<xsl:call-template name="transitionrefs">
									<xsl:with-param name="node" select="$nodeId"/>
								</xsl:call-template>
							</Split>
						</xsl:when>
						<xsl:when test="count(../b:sequenceFlow[@targetRef=$nodeId])&gt;1">
							<Join Type="Exclusive" ExclusiveType="Event"/>
						</xsl:when>
					</xsl:choose>
				</TransitionRestriction>
			</TransitionRestrictions>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:gatewayShape[@gatewayRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="parallelGateway">
		<Activity Id="{./@id}" Name="{./@name}">
			<Route GatewayType="Parallel"/>
			<xsl:variable name="nodeId" select="./@id"/>
			<!--Other elements under activity before nodegraphics include Performer and TransistionRestriction.  Need to decide whether we will handle implicit resource/performer assignment via lane membership.-->
			<!--Have to generate TransitionRestrictions. We need to figure out gatewayDirection if it is not present. Also have to deal with default, e.g. otherwise.-->
			<TransitionRestrictions>
				<!--We need to determine whether this is a split or join and be able to generate the TransitionRefs for the split case-->
				<TransitionRestriction>
					<xsl:choose>
						<xsl:when test="count(../b:sequenceFlow[@sourceRef=$nodeId])&gt;1">
							<Split Type="Parallel">
								<xsl:call-template name="transitionrefs">
									<xsl:with-param name="node" select="$nodeId"/>
								</xsl:call-template>
							</Split>
						</xsl:when>
						<xsl:when test="count(../b:sequenceFlow[@targetRef=$nodeId])&gt;1">
							<Join Type="Parallel"/>
						</xsl:when>
					</xsl:choose>
				</TransitionRestriction>
			</TransitionRestrictions>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:gatewayShape[@gatewayRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="transitionrefs">
		<xsl:param name="node"/>
		<TransitionRefs>
			<xsl:for-each select="../b:sequenceFlow[@sourceRef=$node]">
				<TransitionRef Id="{@id}"/>
			</xsl:for-each>
		</TransitionRefs>
	</xsl:template>
	<xsl:template name="BlockActivity">
		<Activity Id="{./@id}" Name="{./@name}">
			<BlockActivity ActivitySetId="{./@id}"/>
			<xsl:variable name="nodeId" select="./@id"/>
			<!--Other elements under activity before nodegraphics include Performer and TransistionRestriction.  Need to decide whether we will handle implicit resource/performer assignment via lane membership.-->
			<xsl:call-template name="transitionrestrictionsforactivities">
				<xsl:with-param name="activity" select="."/>
			</xsl:call-template>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:subprocessShape[@activityRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="sendTask">
		<Activity Id="{./@id}" Name="{./@name}">
			<Implementation>
				<Task>
					<TaskSend Implementation="WebService"/>
				</Task>
			</Implementation>
			<xsl:variable name="nodeId" select="./@id"/>
			<!--Other elements under activity before nodegraphics include Performer and TransistionRestriction.  Need to decide whether we will handle implicit resource/performer assignment via lane membership.-->
			<xsl:call-template name="transitionrestrictionsforactivities">
				<xsl:with-param name="activity" select="."/>
			</xsl:call-template>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:taskShape[@activityRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="receiveTask">
		<Activity Id="{./@id}" Name="{./@name}">
			<Implementation>
				<Task>
					<TaskReceive Implementation="WebService"/>
				</Task>
			</Implementation>
			<xsl:variable name="nodeId" select="./@id"/>
			<!--Other elements under activity before nodegraphics include Performer and TransistionRestriction.  Need to decide whether we will handle implicit resource/performer assignment via lane membership.-->
			<xsl:call-template name="transitionrestrictionsforactivities">
				<xsl:with-param name="activity" select="."/>
			</xsl:call-template>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:taskShape[@activityRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="serviceTask">
		<Activity Id="{./@id}" Name="{./@name}">
			<Implementation>
				<Task>
					<TaskService Implementation="WebService"/>
				</Task>
			</Implementation>
			<xsl:variable name="nodeId" select="./@id"/>
			<!--Other elements under activity before nodegraphics include Performer and TransistionRestriction.  Need to decide whether we will handle implicit resource/performer assignment via lane membership.-->
			<xsl:call-template name="transitionrestrictionsforactivities">
				<xsl:with-param name="activity" select="."/>
			</xsl:call-template>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:taskShape[@activityRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="task">
		<Activity Id="{./@id}" Name="{./@name}">
			<Implementation>
				<Task>
					<TaskUser Implementation="WebService"/>
				</Task>
			</Implementation>
			<xsl:variable name="nodeId" select="./@id"/>
			<!--Other elements under activity before nodegraphics include Performer and TransistionRestriction.  Need to decide whether we will handle implicit resource/performer assignment via lane membership.-->
			<xsl:call-template name="transitionrestrictionsforactivities">
				<xsl:with-param name="activity" select="."/>
			</xsl:call-template>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:activityShape[@activityRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="userTask">
		<Activity Id="{./@id}" Name="{./@name}">
			<Implementation>
				<Task>
					<TaskUser Implementation="WebService"/>
				</Task>
			</Implementation>
			<xsl:variable name="nodeId" select="./@id"/>
			<!--Other elements under activity before nodegraphics include Performer and TransistionRestriction.  Need to decide whether we will handle implicit resource/performer assignment via lane membership.-->
			<xsl:call-template name="transitionrestrictionsforactivities">
				<xsl:with-param name="activity" select="."/>
			</xsl:call-template>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:taskShape[@activityRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="transitionrestrictionsforactivities">
		<!--only generating for multiple outputs-->
		<xsl:param name="activity"/>
		<xsl:variable name="nodeId" select="$activity/@id"/>
		<xsl:if test="count($activity/../b:sequenceFlow[@sourceRef=$nodeId])&gt;1">
			<xsl:choose>
				<xsl:when test="count($activity/../b:sequenceFlow[@sourceRef=$nodeId]/b:conditionExpression)">
					<TransitionRestrictions>
						<TransitionRestriction>
							<Split>
								<xsl:attribute name="Type" select="'Inclusive'"/>
								<xsl:call-template name="transitionrefs">
									<xsl:with-param name="node" select="$nodeId"/>
								</xsl:call-template>
							</Split>
						</TransitionRestriction>
					</TransitionRestrictions>
				</xsl:when>
				<xsl:otherwise>
					<TransitionRestrictions>
						<TransitionRestriction>
							<Split>
								<xsl:attribute name="Type" select="'Parallel'"/>
								<xsl:call-template name="transitionrefs">
									<xsl:with-param name="node" select="$nodeId"/>
								</xsl:call-template>
							</Split>
						</TransitionRestriction>
					</TransitionRestrictions>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	<xsl:template name="startEvent">
		<Activity Id="{./@id}" Name="{./@name}">
			<Event>
				<StartEvent>
					<xsl:choose>
						<xsl:when test="./b:messageEventDefinition">
							<xsl:attribute name="Trigger" select="'Message'"/>
						</xsl:when>
						<xsl:when test="./b:timerEventDefinition">
							<xsl:attribute name="Trigger" select="'Timer'"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="Trigger" select="'None'"/>
						</xsl:otherwise>
					</xsl:choose>
				</StartEvent>
			</Event>
			<xsl:variable name="nodeId" select="./@id"/>
			<xsl:if test="count(../b:sequenceFlow[@sourceRef=$nodeId])&gt;1">
				<TransitionRestrictions>
					<TransitionRestriction>
						<Split Type="Parallel">
							<xsl:call-template name="transitionrefs">
								<xsl:with-param name="node" select="$nodeId"/>
							</xsl:call-template>
						</Split>
					</TransitionRestriction>
				</TransitionRestrictions>
			</xsl:if>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:eventShape[@eventRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="endEvent">
		<Activity Id="{./@id}" Name="{./@name}">
			<Event>
				<EndEvent>
					<xsl:choose>
						<xsl:when test="./b:messageEventDefinition">
							<!--The xpdl schema is inconsistent because this attribute 'Result' is optional for EndEvent but 'Trigger' is required for StartEvent-->
							<xsl:attribute name="Result" select="'Message'"/>
						</xsl:when>
						<xsl:when test="./b:errorEventDefinition">
							<!--The xpdl schema is inconsistent because this attribute 'Result' is optional for EndEvent but 'Trigger' is required for StartEvent-->
							<xsl:attribute name="Result" select="'Error'"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="Result" select="'None'"/>
						</xsl:otherwise>
					</xsl:choose>
				</EndEvent>
			</Event>
			<xsl:variable name="nodeId" select="./@id"/>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:eventShape[@eventRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="intermediateThrowEvent">
		<Activity Id="{./@id}" Name="{./@name}">
			<Event>
				<IntermediateEvent>
					<xsl:choose>
						<xsl:when test="./b:messageEventDefinition">
							<!--The xpdl schema is inconsistent because this attribute 'Result' is optional for EndEvent but 'Trigger' is required for StartEvent-->
							<xsl:attribute name="Trigger" select="'Message'"/>
							<TriggerResultMessage CatchThrow="THROW"/>
						</xsl:when>
						<xsl:when test="./b:timerEventDefinition">
							<!--The xpdl schema is inconsistent because this attribute 'Result' is optional for EndEvent but 'Trigger' is required for StartEvent-->
							<xsl:attribute name="Trigger" select="'Timer'"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="Trigger" select="'None'"/>
						</xsl:otherwise>
					</xsl:choose>
				</IntermediateEvent>
			</Event>
			<xsl:variable name="nodeId" select="./@id"/>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:eventShape[@eventRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="intermediateCatchEvent">
		<Activity Id="{./@id}" Name="{./@name}">
			<Event>
				<IntermediateEvent>
					<xsl:choose>
						<xsl:when test="./b:messageEventDefinition">
							<!--The xpdl schema is inconsistent because this attribute 'Result' is optional for EndEvent but 'Trigger' is required for StartEvent-->
							<xsl:attribute name="Trigger" select="'Message'"/>
							<TriggerResultMessage CatchThrow="CATCH"/>
						</xsl:when>
						<xsl:when test="./b:timerEventDefinition">
							<!--The xpdl schema is inconsistent because this attribute 'Result' is optional for EndEvent but 'Trigger' is required for StartEvent-->
							<xsl:attribute name="Trigger" select="'Timer'"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="Trigger" select="'None'"/>
						</xsl:otherwise>
					</xsl:choose>
				</IntermediateEvent>
			</Event>
			<xsl:variable name="nodeId" select="./@id"/>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:eventShape[@eventRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="boundaryEvent">
		<Activity Id="{./@id}" Name="{./@name}">
			<Event>
				<IntermediateEvent Target="{./@attachedToRef}">
					<xsl:choose>
						<xsl:when test="./b:messageEventDefinition">
							<!--The xpdl schema is inconsistent because this attribute 'Result' is optional for EndEvent but 'Trigger' is required for StartEvent-->
							<xsl:attribute name="Trigger" select="'Message'"/>
							<TriggerResultMessage CatchThrow="CATCH"/>
						</xsl:when>
						<xsl:when test="./b:timerEventDefinition">
							<!--The xpdl schema is inconsistent because this attribute 'Result' is optional for EndEvent but 'Trigger' is required for StartEvent-->
							<xsl:attribute name="Trigger" select="'Timer'"/>
						</xsl:when>
						<xsl:when test="./b:errorEventDefinition">
							<!--The xpdl schema is inconsistent because this attribute 'Result' is optional for EndEvent but 'Trigger' is required for StartEvent-->
							<xsl:attribute name="Trigger" select="'Error'"/>
							<ResultError/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="Trigger" select="'None'"/>
						</xsl:otherwise>
					</xsl:choose>
				</IntermediateEvent>
			</Event>
			<xsl:variable name="nodeId" select="./@id"/>
			<xsl:for-each select="/b:definitions/bpmndi:processDiagram/bpmndi:laneCompartment/bpmndi:eventShape[@eventRef=$nodeId]">
				<xsl:call-template name="nodeGraphics"/>
			</xsl:for-each>
		</Activity>
	</xsl:template>
	<xsl:template name="nodeGraphics">
		<NodeGraphicsInfos>
			<!--Other important attributes are ToolId, PageId and LaneId-->
			<NodeGraphicsInfo Height="{./@height}" Width="{./@width}">
				<!--{$pageId}-->
				<xsl:call-template name="pageId">
					<xsl:with-param name="node_indicator" select="true()"/>
				</xsl:call-template>
				<!--These coordinates probably have to be adjusted-->
				<Coordinates XCoordinate="{./@x}" YCoordinate="{./@y}"/>
			</NodeGraphicsInfo>
		</NodeGraphicsInfos>
	</xsl:template>
	<xsl:template name="pageId">
		<xsl:param name="node_indicator"/>
		<xsl:variable name="pageId" select='substring-after(string(./@id),".")'/>
		<xsl:variable name="element_id" select="./@id"/>
		<xsl:message select="$element_id"/>
		<xsl:message select="$node_indicator"/>
		<xsl:message select="$pageId"/>
		<xsl:variable name="pagebypos">
			<xsl:for-each select="/descendant::bpmndi:processDiagram">
				<xsl:if test="count(*/*[@id=$element_id])&gt;0">
					<xsl:value-of select="position()"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:variable>
		<xsl:message select="$pagebypos"/>
		<xsl:if test="$pageId=$pagebypos">
			<xsl:message select='"equal"'/>
		</xsl:if>
		<xsl:variable name="anyRef" select='substring-before(./@id,".")'/>
		<xsl:choose>
			<xsl:when test="count(/descendant::b:subProcess/*[@id=$anyRef])&gt;0">
				<xsl:for-each select="/descendant::b:subProcess/*[@id=$anyRef]">
					<xsl:variable name="subProcessId" select="../@id"/>
					<xsl:choose>
						<xsl:when test="count(/descendant::bpmndi:subprocessShape[@activityRef=$subProcessId and @isExpanded=true()])&gt;0">
							<xsl:for-each select="/descendant::bpmndi:subprocessShape [@isExpanded = true()]">
								<!--[@isExpanded = true()]-->
								<xsl:variable name="pagebump" select="count(/descendant::bpmndi:collaborationDiagram)"/>
								<xsl:for-each select="/descendant::bpmndi:subprocessShape[@activityRef=$subProcessId and @isExpanded=true()]">
									<xsl:choose>
										<xsl:when test="$pagebump=0">
											<xsl:attribute name="PageId" select="1+$pagebump"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="PageId" select="$pagebump+position()"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:for-each>
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="nodeGraphics_part2">
								<xsl:with-param name="pageId" select="$pageId"/>
								<xsl:with-param name="node_indicator" select="$node_indicator"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="nodeGraphics_part2">
					<xsl:with-param name="pageId" select="$pageId"/>
					<xsl:with-param name="node_indicator" select="$node_indicator"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="nodeGraphics_part2">
		<xsl:param name="pageId"/>
		<xsl:param name="node_indicator"/>
		<xsl:choose>
			<xsl:when test="$pageId=''">
				<xsl:attribute name="PageId" select="1"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="PageId" select="$pageId"/>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="$node_indicator=true()">
			<xsl:if test="count(ancestor::bpmndi:laneCompartment)=1">
				<xsl:variable name="visible" select="ancestor::bpmndi:laneCompartment/@isVisible"/>
				<xsl:if test="$visible=true()">
					<xsl:attribute name="LaneId" select="../@id"/>
				</xsl:if>
			</xsl:if>
		</xsl:if>
	</xsl:template>
	<xsl:template name="connectorGraphics">
		<ConnectorGraphicsInfos>
			<!--Other important attributes are ToolId, PageId and LaneId-->
			<ConnectorGraphicsInfo>
				<xsl:call-template name="pageId">
					<xsl:with-param name="node_indicator" select="false()"/>
				</xsl:call-template>
				<!--These coordinates probably have to be adjusted-->
				<xsl:for-each select="bpmndi:bendpoint">
					<Coordinates XCoordinate="{./@x}" YCoordinate="{./@y}"/>
				</xsl:for-each>
			</ConnectorGraphicsInfo>
		</ConnectorGraphicsInfos>
	</xsl:template>
	<xsl:template match="*|@*">
		<xsl:copy>
			<xsl:apply-templates select="@*|*|text()"/>
		</xsl:copy>
	</xsl:template>
	<!-- Process everything else by just passing it through -->
	<xsl:template match="text()">
		<xsl:value-of select="." disable-output-escaping="yes"/>
	</xsl:template>
</xsl:stylesheet>
