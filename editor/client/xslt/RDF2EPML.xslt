<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:oryx="http://oryx-editor.org/"
	xmlns:raziel="http://raziel.org/">

	<xsl:output method="xml" />

	<!-- Root elemen -->
	<xsl:template match="rdf:RDF">
		<epml:epml xmlns:epml="http://www.epml.de" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.epml.de http://wi.wu-wien.ac.at/home/mendling/EPML/EPML_12.xsd" >
			<coordinates xOrigin="leftToRight" yOrigin="topToBottom" />
			<definitions>
				<xsl:for-each select="//rdf:RDF/rdf:Description">
					<xsl:if test="./oryx:type='http://b3mn.org/stencilset/epc#Event' or ./oryx:type='http://b3mn.org/stencilset/epc#Function' or ./oryx:type='http://b3mn.org/stencilset/epc#ProcessInterface' or ./oryx:type='http://b3mn.org/stencilset/epc#Data' or ./oryx:type='http://b3mn.org/stencilset/epc#System' or ./oryx:type='http://b3mn.org/stencilset/epc#Organization' or ./oryx:type='http://b3mn.org/stencilset/epc#Position' or ./oryx:type='http://b3mn.org/stencilset/epc#OrConnector' or ./oryx:type='http://b3mn.org/stencilset/epc#XorConnector' or ./oryx:type='http://b3mn.org/stencilset/epc#AndConnector'">
						<definition>
							<xsl:attribute name="defId">
								<xsl:call-template name="get-id-string">
									<xsl:with-param name="id_"><xsl:value-of select="@rdf:about" /></xsl:with-param>
								</xsl:call-template>
							</xsl:attribute>
						</definition>
					</xsl:if>
				</xsl:for-each>
			</definitions>
			<attributeTypes>
				<attributeType typeId="AT_ORYX_ParticipantType" />
				<attributeType typeId="AT_ORYX_InformationFlow" />
				<attributeType typeId="AT_ORYX_Description" />			
				<attributeType typeId="AT_FRQ_DAY"/>
				<attributeType typeId="AT_PROB"/>
				<attributeType typeId="AT_TIME_AVG_PRCS"/>
			</attributeTypes>
			<directory name="Root">
				<epc>
					<!-- workaround for the moment being -->	
					<xsl:attribute name="epcId">1</xsl:attribute>
					<xsl:attribute name="name">
						<xsl:for-each select="//*[@rdf:about='#generatedProcessInfos']">
							<xsl:value-of select="./oryx:name" />
						</xsl:for-each>
					</xsl:attribute>
					<xsl:apply-templates />	
				</epc>
			</directory>
		</epml:epml>
	</xsl:template>

	<!-- Nodes and Egdes   -->
	<xsl:template match="rdf:Description">
		<xsl:variable name="type" select="./oryx:type" />
		<xsl:variable name="title" select="./oryx:title" />
		<xsl:variable name="realID"><xsl:value-of select="@rdf:about" /></xsl:variable>
		<xsl:variable name="id">
			<xsl:call-template name="get-id-string">
				<xsl:with-param name="id_"><xsl:value-of select="$realID" /></xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<!-- Events -->
		<xsl:if test="$type='http://b3mn.org/stencilset/epc#Event'">
			<event>
				<xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
				<xsl:attribute name="defRef"><xsl:value-of select="$id" /></xsl:attribute>
				<name><xsl:value-of select="$title" /></name>
				<description><xsl:value-of select="./oryx:description" /></description>
				<xsl:call-template name="node-position" />
				<xsl:variable name="frequency" select="./oryx:frequency" />
				
				<xsl:if test="$frequency!=''">
					<attribute typeRef="AT_FRQ_DAY"><xsl:attribute name="value">
						<xsl:value-of select="$frequency" />
					</xsl:attribute></attribute>
				</xsl:if>
			</event>
	 	</xsl:if>
		<!-- Functions -->
		<xsl:if test="$type='http://b3mn.org/stencilset/epc#Function'">
			<function>
				<xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
				<xsl:attribute name="defRef"><xsl:value-of select="$id" /></xsl:attribute>
				<name><xsl:value-of select="$title" /></name>
				<description><xsl:value-of select="./oryx:description" /></description>
				<xsl:call-template name="node-position" />
				<xsl:variable name="execTime" select="./oryx:time" />
				<xsl:if test="$execTime!=''">
					<attribute typeRef="AT_TIME_AVG_PRCS"><xsl:attribute name="value">
						<xsl:value-of select="$execTime" />
					</xsl:attribute></attribute>
				</xsl:if>
			</function>
	 	</xsl:if>
		<!-- And connectors -->
		<xsl:if test="$type='http://b3mn.org/stencilset/epc#AndConnector'">
			<and>
				<xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
				<xsl:attribute name="defRef"><xsl:value-of select="$id" /></xsl:attribute>
				<name /><description />
				<xsl:call-template name="node-position" />
			</and>
	 	</xsl:if>
		<!-- Xor connectors -->
		<xsl:if test="$type='http://b3mn.org/stencilset/epc#XorConnector'">
			<xor>
				<xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
				<xsl:attribute name="defRef"><xsl:value-of select="$id" /></xsl:attribute>
				<name /><description />
				<xsl:call-template name="node-position" />
			</xor>
	 	</xsl:if>
		<!-- Or connectors -->
		<xsl:if test="$type='http://b3mn.org/stencilset/epc#OrConnector'">
			<or>
				<xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
				<xsl:attribute name="defRef"><xsl:value-of select="$id" /></xsl:attribute>
				<name /><description />
				<xsl:call-template name="node-position" />
			</or>
	 	</xsl:if>
		<!-- Control Flow -->
		<xsl:if test="$type='http://b3mn.org/stencilset/epc#ControlFlow'">
			<xsl:variable name="sourceId">
				<xsl:for-each select="//*/raziel:outgoing[@rdf:resource=$realID]">
					<xsl:value-of select="parent::rdf:Description/attribute::rdf:about" />
				</xsl:for-each>
			</xsl:variable>
			<xsl:variable name="targetId" select="child::raziel:outgoing/attribute::rdf:resource" />
			<arc>
				<xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
				<!--<xsl:attribute name="defRef"><xsl:value-of select="$id" /></xsl:attribute>-->
				<flow>
					<xsl:attribute name="source">
						<xsl:call-template name="get-id-string">
							<xsl:with-param name="id_"><xsl:value-of select="$sourceId" /></xsl:with-param>
						</xsl:call-template>						
					</xsl:attribute>
					<xsl:attribute name="target">
						<xsl:call-template name="get-id-string">
							<xsl:with-param name="id_"><xsl:value-of select="$targetId" /></xsl:with-param>
						</xsl:call-template>	
					</xsl:attribute>
				</flow>
				<xsl:call-template name="edge-position">
					<xsl:with-param name="sourceId"><xsl:value-of select="$sourceId" /></xsl:with-param>
					<xsl:with-param name="targetId"><xsl:value-of select="$targetId" /></xsl:with-param>
				</xsl:call-template>
				
				<xsl:variable name="probability" select="./oryx:probability" />
				<xsl:if test="$probability!=''">
					<attribute typeRef="AT_PROB"><xsl:attribute name="value">
						<xsl:value-of select="$probability" />
					</xsl:attribute></attribute>
				</xsl:if>
				
			</arc>
	 	</xsl:if>
		<!-- Process Interface -->
		<xsl:if test="$type='http://b3mn.org/stencilset/epc#ProcessInterface'">
			<processInterface>
				<xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
				<xsl:attribute name="defRef"><xsl:value-of select="$id" /></xsl:attribute>
				<name><xsl:value-of select="$title" /></name>
				<description><xsl:value-of select="./oryx:description" /></description>
				<xsl:call-template name="node-position" />
				<toProcess>
					<xsl:attribute name="linkToEpcId"><xsl:value-of select="./oryx:refuri" /></xsl:attribute>
				</toProcess>
			</processInterface>
	 	</xsl:if>
		<!-- Data -->
		<xsl:if test="$type='http://b3mn.org/stencilset/epc#Data'">
			<dataField>
				<xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
				<xsl:attribute name="defRef"><xsl:value-of select="$id" /></xsl:attribute>
				<name><xsl:value-of select="$title" /></name>
				<description><xsl:value-of select="./oryx:description" /></description>
				<xsl:call-template name="node-position" />
			</dataField>
	 	</xsl:if>
		<!-- System -->
		<xsl:if test="$type='http://b3mn.org/stencilset/epc#System'">
			<application>
				<xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
				<xsl:attribute name="defRef"><xsl:value-of select="$id" /></xsl:attribute>
				<name><xsl:value-of select="$title" /></name>
				<description><xsl:value-of select="./oryx:description" /></description>
				<xsl:call-template name="node-position" />
			</application>
	 	</xsl:if>
		<!-- Organization -->
		<xsl:if test="$type='http://b3mn.org/stencilset/epc#Organization'">
			<participant>
				<xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
				<xsl:attribute name="defRef"><xsl:value-of select="$id" /></xsl:attribute>
				<name><xsl:value-of select="$title" /></name>
				<description><xsl:value-of select="./oryx:description" /></description>
				<xsl:call-template name="node-position" />
				<attribute typeRef="AT_ORYX_ParticipantType" value="Organization" />
			</participant>
	 	</xsl:if>
		<!-- Position-->
		<xsl:if test="$type='http://b3mn.org/stencilset/epc#Position'">
			<participant>
				<xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
				<xsl:attribute name="defRef"><xsl:value-of select="$id" /></xsl:attribute>
				<name><xsl:value-of select="$title" /></name>
				<description><xsl:value-of select="./oryx:description" /></description>
				<xsl:call-template name="node-position" />
				<attribute typeRef="AT_ORYX_ParticipantType" value="Position" />
			</participant>
	 	</xsl:if>			
	
		<!-- Relation -->
		<xsl:if test="$type='http://b3mn.org/stencilset/epc#Relation'">
			<xsl:variable name="sourceId">
				<xsl:for-each select="//*/raziel:outgoing[@rdf:resource=$realID]">
					<xsl:value-of select="parent::rdf:Description/attribute::rdf:about" />
				</xsl:for-each>
			</xsl:variable>
			<xsl:variable name="targetId" select="child::raziel:outgoing/attribute::rdf:resource" />
			<relation>
				<xsl:attribute name="id"><xsl:value-of select="$id" /></xsl:attribute>
				<!--<xsl:attribute name="defRef"><xsl:value-of select="$id" /></xsl:attribute>-->
				<xsl:attribute name="from">
					<xsl:call-template name="get-id-string">
						<xsl:with-param name="id_"><xsl:value-of select="$sourceId" /></xsl:with-param>
					</xsl:call-template>
				</xsl:attribute>
				<xsl:attribute name="to">
					<xsl:call-template name="get-id-string">
						<xsl:with-param name="id_"><xsl:value-of select="$targetId" /></xsl:with-param>
					</xsl:call-template>	
				</xsl:attribute>
				<xsl:call-template name="edge-position">
					<xsl:with-param name="sourceId"><xsl:value-of select="$sourceId" /></xsl:with-param>
					<xsl:with-param name="targetId"><xsl:value-of select="$targetId" /></xsl:with-param>
				</xsl:call-template>
				<attribute typeRef="AT_ORYX_InformationFlow">
					<xsl:attribute name="value"><xsl:value-of select="./oryx:informationflow" /></xsl:attribute>
				</attribute>
				<attribute typeRef="AT_ORYX_Description">
					<xsl:attribute name="value"><xsl:value-of select="./oryx:description" /></xsl:attribute>
				</attribute>
			</relation>
		</xsl:if>
	</xsl:template>

	<!-- Node's position -->
	<xsl:template name="node-position">
		<xsl:variable name="bounds" select="./oryx:bounds" />
		<xsl:variable name="x1" select="3*round(substring-before($bounds, ','))" />
		<xsl:variable name="y1" select="3*round(substring-before(substring-after($bounds, ','), ','))" />
		<xsl:variable name="x2" select="3*round(substring-before(substring-after(substring-after($bounds, ','), ','), ','))" />
		<xsl:variable name="y2" select="3*round(substring-after(substring-after(substring-after($bounds, ','), ','), ','))" />
		<graphics>
			<position>
				<xsl:attribute name="x">
					<xsl:value-of select="$x1" />
				</xsl:attribute>
				<xsl:attribute name="y">
					<xsl:value-of select="$y1" />
				</xsl:attribute>
				<xsl:attribute name="width">
					<xsl:value-of select="$x2 - $x1" />
				</xsl:attribute>
				<xsl:attribute name="height">
					<xsl:value-of select="$y2 - $y1" />
				</xsl:attribute>
			</position>
		</graphics>
	</xsl:template>

	<!-- Egde's position(s) -->
	<xsl:template name="edge-position">
		<xsl:param name="sourceId" />
		<xsl:param name="targetId" />
		<graphics>
			<xsl:call-template name="edge-position-recursive">
				<xsl:with-param name="start">1</xsl:with-param>
				<xsl:with-param name="sourceId"><xsl:value-of select="$sourceId" /></xsl:with-param>
				<xsl:with-param name="targetId"><xsl:value-of select="$targetId" /></xsl:with-param>
				<xsl:with-param name="dockers"><xsl:value-of select="./oryx:dockers" /></xsl:with-param>
			</xsl:call-template>
		</graphics>
	</xsl:template>

	<!-- Recursive Template RDF-Docker -> EPML-Positions -->
	<xsl:template name="edge-position-recursive">
		<xsl:param name="start">0</xsl:param>
		<xsl:param name="sourceId" />
		<xsl:param name="targetId" />
		<xsl:param name="dockers" />
		<xsl:variable name="x" select="3*round(substring-before($dockers, ' '))" />
		<xsl:variable name="y" select="3*round(substring-before(substring-after($dockers, ' '), ' '))" />
		<xsl:variable name="rest" select="substring-after(substring-after($dockers, ' '), ' ')" />

		<xsl:if test="not($rest=' # ')">
			<!-- Case 1: First Docker -->
			<xsl:if test="$start='1'">
				<xsl:variable name="x0">
					<xsl:call-template name="node-upper-left-x">
						<xsl:with-param name="id"><xsl:value-of select="$sourceId" /></xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="y0">
					<xsl:call-template name="node-upper-left-y">
						<xsl:with-param name="id"><xsl:value-of select="$sourceId" /></xsl:with-param>
					</xsl:call-template>
				</xsl:variable>

				<position>
					<xsl:attribute name="x">
						<xsl:value-of select="$x + $x0" />
					</xsl:attribute>
					<xsl:attribute name="y">
						<xsl:value-of select="$y + $y0" />
					</xsl:attribute>
				</position>
			</xsl:if>
			<!-- Case 2: Docker in between -->
			<xsl:if test="not($start='1')">
				<position>
					<xsl:attribute name="x">
						<xsl:value-of select="$x" />
					</xsl:attribute>
					<xsl:attribute name="y">
						<xsl:value-of select="$y" />
					</xsl:attribute>
				</position>
			</xsl:if>
			<!-- In the case of 1 or 2 : Recursive call -->
			<xsl:call-template name="edge-position-recursive">
				<xsl:with-param name="sourceId"><xsl:value-of select="$sourceId" /></xsl:with-param>
				<xsl:with-param name="targetId"><xsl:value-of select="$targetId" /></xsl:with-param>
				<xsl:with-param name="dockers"><xsl:value-of select="$rest" /></xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<!-- Case 3: Last Docker -->
		<xsl:if test="$rest=' # '">
			<xsl:variable name="x0">
				<xsl:call-template name="node-upper-left-x">
					<xsl:with-param name="id"><xsl:value-of select="$targetId" /></xsl:with-param>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="y0">
				<xsl:call-template name="node-upper-left-y">
					<xsl:with-param name="id"><xsl:value-of select="$targetId" /></xsl:with-param>
				</xsl:call-template>
			</xsl:variable>

			<position>
				<xsl:attribute name="x">
					<xsl:value-of select="$x + $x0" />
				</xsl:attribute>
				<xsl:attribute name="y">
					<xsl:value-of select="$y + $y0" />
				</xsl:attribute>
			</position>
		</xsl:if>
	</xsl:template>

	<!-- Determines the upper left x coordinate of a node -->
	<xsl:template name="node-upper-left-x">
		<xsl:param name="id" />
			<xsl:for-each select="//*[@rdf:about=$id]">
				<xsl:variable name="bounds" select="./oryx:bounds" />
				<xsl:value-of select="3*round(substring-before($bounds, ','))" />
			</xsl:for-each>
	</xsl:template>

	<!-- Determines the upper left y coordinate of a node -->
	<xsl:template name="node-upper-left-y">
		<xsl:param name="id" />
			<xsl:for-each select="//*[@rdf:about=$id]">
				<xsl:variable name="bounds" select="./oryx:bounds" />
				<xsl:value-of select="3*round(substring-before(substring-after($bounds, ','), ','))" />
			</xsl:for-each>
	</xsl:template>

	<!-- Returns the ID without leading # -->
	<xsl:template name="get-id-string">
		<xsl:param name="id_" />
		<xsl:value-of select="substring-after($id_, '#')" />
	</xsl:template>

	<!-- Blocking Template -->
	<xsl:template match="rdf:Description[@rdf:about='generatedProcessInfos']" />

</xsl:stylesheet>