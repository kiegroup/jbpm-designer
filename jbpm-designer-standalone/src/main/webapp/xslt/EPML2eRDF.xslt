<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:epml="http://www.epml.de"
        exclude-result-prefixes="epml">

	<xsl:output method="xml" />

	<!-- Root element -->
	<xsl:template match="epml:epml">
		<!-- test : number of epc elements - only one supported at the moment -->
		<xsl:variable name="number-of-epc" select="count(./directory/epc)" />
		<xsl:if test="not($number-of-epc=1)">
			<error type="Wrong number of model elements"><xsl:value-of select="$number-of-epc" /></error>
		</xsl:if>
		<xsl:if test="$number-of-epc=1">
		        <root>
				<xsl:apply-templates />
					<div id="oryxcanvas" class="-oryx-canvas">
						<span class="oryx-mode">writeable</span>
						<span class="oryx-mode">fullscreen</span>
	  					<a rel="oryx-stencilset" href="./stencilsets/epc/epc.json"/>
						<xsl:call-template name="add-render" />
	 				</div>
		         </root>
		</xsl:if>
	</xsl:template>

	<!-- event -->
	<xsl:template match="event">
		<xsl:call-template name="add-standard-node-properties">
			<xsl:with-param name="type">http://b3mn.org/stencilset/epc#Event</xsl:with-param>
			<xsl:with-param name="additional-property-name">oryx-frequency</xsl:with-param>
			<xsl:with-param name="additional-property-value">
				<xsl:value-of select="./attribute[@typeRef='AT_FRQ_DAY']/attribute::value" />
			</xsl:with-param>			
		</xsl:call-template>
	</xsl:template>

	<!-- function -->
	<xsl:template match="function">
		<xsl:variable name="time0">
			<xsl:value-of select="./attribute[@typeRef='AT_TIME_AVG_PRCS']/attribute::value" />
		</xsl:variable>
		<xsl:variable name="time">
			<xsl:choose>
				<xsl:when test="substring-after($time0, ';')='43'"><xsl:value-of select="concat(substring-before($time0, ';'), ' sec')" /></xsl:when>
				<xsl:when test="substring-after($time0, ';')='44'"><xsl:value-of select="concat(substring-before($time0, ';'), ' min')" /></xsl:when>
				<xsl:when test="substring-after($time0, ';')='45'"><xsl:value-of select="concat(substring-before($time0, ';'), ' h')" /></xsl:when>
				<xsl:when test="substring-after($time0, ';')='46'"><xsl:value-of select="concat(substring-before($time0, ';'), ' days')" /></xsl:when>
				<xsl:when test="substring-after($time0, ';')='47'"><xsl:value-of select="concat(substring-before($time0, ';'), ' months')" /></xsl:when>
				<xsl:when test="substring-after($time0, ';')='48'"><xsl:value-of select="concat(substring-before($time0, ';'), ' years')" /></xsl:when>
				<xsl:otherwise><xsl:value-of select="$time0" /></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:call-template name="add-standard-node-properties">
			<xsl:with-param name="type">http://b3mn.org/stencilset/epc#Function</xsl:with-param>
			<xsl:with-param name="additional-property-name">oryx-time</xsl:with-param>
			<xsl:with-param name="additional-property-value">
				<xsl:value-of select="$time" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- and -->
	<xsl:template match="and">
		<xsl:call-template name="add-connector-properties">
			<xsl:with-param name="type">http://b3mn.org/stencilset/epc#AndConnector</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<!-- xor -->
	<xsl:template match="xor">
		<xsl:call-template name="add-connector-properties">
			<xsl:with-param name="type">http://b3mn.org/stencilset/epc#XorConnector</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<!-- or -->
	<xsl:template match="or">
		<xsl:call-template name="add-connector-properties">
			<xsl:with-param name="type">http://b3mn.org/stencilset/epc#OrConnector</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- process interface -->
	<xsl:template match="processInterface">
		<xsl:call-template name="add-standard-node-properties">
			<xsl:with-param name="type">http://b3mn.org/stencilset/epc#ProcessInterface</xsl:with-param>
			<xsl:with-param name="additional-property-name">oryx-refuri</xsl:with-param>
			<xsl:with-param name="additional-property-value">
				<xsl:value-of select="./toProcess/attribute::linkToEpcId" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- application -->
	<xsl:template match="application">
		<xsl:call-template name="add-standard-node-properties">
			<xsl:with-param name="type">http://b3mn.org/stencilset/epc#System</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- data field -->
	<xsl:template match="dataField">
		<xsl:call-template name="add-standard-node-properties">
			<xsl:with-param name="type">http://b3mn.org/stencilset/epc#Data</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- participant -->
	<xsl:template match="participant">
		<xsl:variable name="type" select="./attribute[@typeRef='AT_ORYX_ParticipantType']/attribute::value" />
		<xsl:if test="$type='Position'">
			<xsl:call-template name="add-standard-node-properties">
				<xsl:with-param name="type">http://b3mn.org/stencilset/epc#Position</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="not($type='Position')">
			<xsl:call-template name="add-standard-node-properties">
				<xsl:with-param name="type">http://b3mn.org/stencilset/epc#Organization</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!-- arc -->
	<xsl:template match="arc">
		<div>
			<!-- standard attributes -->
			<xsl:attribute name="id"><xsl:value-of select="concat('resource', @id)" /></xsl:attribute>
			<span class="oryx-type">http://b3mn.org/stencilset/epc#ControlFlow</span>
			<span class="oryx-probability"><xsl:value-of select="./attribute[@typeRef='AT_PROB']/attribute::value" /></span>
			<!-- bounds -->
			<span class="oryx-bounds">
				<xsl:call-template name="edge-bounds" />
			</span>
			<!-- outgoing -->
			<a rel="raziel-outgoing">
				<xsl:attribute name="href"><xsl:value-of select="concat('#', ./flow/@target)" /></xsl:attribute>
			</a>
			<a rel="raziel-target">
				<xsl:attribute name="href"><xsl:value-of select="concat('#', ./flow/@target)" /></xsl:attribute>
			</a>
			<!-- dockers -->
			<span class="oryx-dockers">
				<xsl:call-template  name="coordinates-of-node">
					<xsl:with-param name="node-id" select="./flow/@source" />
				</xsl:call-template>
				<xsl:variable name="count" select="count(./graphics/position)" />
				<xsl:for-each select="./graphics/position">
					<xsl:if test="not(position()=1 or position()=$count)">
						<xsl:value-of select="concat(@x div 3, ' ', @y div 3, ' ')"/>
					</xsl:if>
				</xsl:for-each>
				<xsl:call-template  name="coordinates-of-node">
					<xsl:with-param name="node-id" select="./flow/@target" />
				</xsl:call-template>
				<xsl:value-of select="' # '" />
			</span>
			<!-- parent -->
			<a rel="raziel-parent" href="#"/>
		</div>
	</xsl:template>

	<!-- relation -->
	<xsl:template match="relation">
		<div>
			<!-- standard attributes -->
			<xsl:attribute name="id"><xsl:value-of select="concat('resource', @id)" /></xsl:attribute>
			<span class="oryx-type">http://b3mn.org/stencilset/epc#Relation</span>
			<span class="oryx-description"><xsl:value-of select="./attribute[@typeRef='AT_ORYX_Description']/attribute::value" /></span>
			<xsl:variable name="iFlow" select="./attribute[@typeRef='AT_ORYX_InformationFlow']/attribute::value" />
			<xsl:if test="$iFlow='True'">
				<span class="oryx-informationflow">True</span>
			</xsl:if>
			<xsl:if test="not($iFlow='True')">
				<span class="oryx-informationflow">False</span>
			</xsl:if>
			<!-- bounds -->
			<span class="oryx-bounds">
				<xsl:call-template name="edge-bounds" />
			</span>
			<!-- outgoing -->
			<a rel="raziel-outgoing">
				<xsl:attribute name="href"><xsl:value-of select="concat('#', @to)" /></xsl:attribute>
			</a>
			<!-- dockers -->
			<span class="oryx-dockers">
				<xsl:call-template  name="coordinates-of-node">
					<xsl:with-param name="node-id" select="@from" />
				</xsl:call-template>
				<xsl:variable name="count" select="count(./graphics/position)" />
				<xsl:for-each select="./graphics/position">
					<xsl:if test="not(position()=1 or position()=$count)">
						<xsl:value-of select="concat(@x div 3, ' ', @y div 3, ' ')"/>
					</xsl:if>
				</xsl:for-each>
				<xsl:call-template  name="coordinates-of-node">
					<xsl:with-param name="node-id" select="@to" />
				</xsl:call-template>
				<xsl:value-of select="' # '" />
			</span>
			<!-- parent -->
			<a rel="raziel-parent" href="#"/>
		</div>
	</xsl:template>

	<!-- Adds renderer informations --> 
	<xsl:template name="add-render">
		<xsl:for-each select="./directory/epc/event">
			<a rel="oryx-render"><xsl:attribute name="href"><xsl:value-of select="concat('#', @id)" /></xsl:attribute></a>
		</xsl:for-each>
		<xsl:for-each select="./directory/epc/function">
			<a rel="oryx-render"><xsl:attribute name="href"><xsl:value-of select="concat('#', @id)" /></xsl:attribute></a>
		</xsl:for-each>
		<xsl:for-each select="./directory/epc/and">
			<a rel="oryx-render"><xsl:attribute name="href"><xsl:value-of select="concat('#', @id)" /></xsl:attribute></a>
		</xsl:for-each>
		<xsl:for-each select="./directory/epc/xor">
			<a rel="oryx-render"><xsl:attribute name="href"><xsl:value-of select="concat('#', @id)" /></xsl:attribute></a>
		</xsl:for-each>
		<xsl:for-each select="./directory/epc/or">
			<a rel="oryx-render"><xsl:attribute name="href"><xsl:value-of select="concat('#', @id)" /></xsl:attribute></a>
		</xsl:for-each>
		<xsl:for-each select="./directory/epc/processInterface">
			<a rel="oryx-render"><xsl:attribute name="href"><xsl:value-of select="concat('#', @id)" /></xsl:attribute></a>
		</xsl:for-each>
		<xsl:for-each select="./directory/epc/application">
			<a rel="oryx-render"><xsl:attribute name="href"><xsl:value-of select="concat('#', @id)" /></xsl:attribute></a>
		</xsl:for-each>
		<xsl:for-each select="./directory/epc/participant">
			<a rel="oryx-render"><xsl:attribute name="href"><xsl:value-of select="concat('#', @id)" /></xsl:attribute></a>
		</xsl:for-each>
		<xsl:for-each select="./directory/epc/dataField">
			<a rel="oryx-render"><xsl:attribute name="href"><xsl:value-of select="concat('#', @id)" /></xsl:attribute></a>
		</xsl:for-each>
		<xsl:for-each select="./directory/epc/arc">
			<a rel="oryx-render"><xsl:attribute name="href"><xsl:value-of select="concat('#', @id)" /></xsl:attribute></a>
		</xsl:for-each>
		<xsl:for-each select="./directory/epc/relation">
			<a rel="oryx-render"><xsl:attribute name="href"><xsl:value-of select="concat('#', @id)" /></xsl:attribute></a>
		</xsl:for-each>
	</xsl:template>

	<!-- Adds standard node properties --> 
	<xsl:template name="add-standard-node-properties">
		<xsl:param name="type" />
		<xsl:param name="additional-property-name" />
		<xsl:param name="additional-property-value" />
		<xsl:variable name="at_desc">
			<xsl:value-of select="./attribute[@typeRef='AT_DESC']/attribute::value" />
		</xsl:variable>
		<xsl:variable name="id">
			<xsl:value-of select="@id" />
		</xsl:variable>
	 	<div>
			<!-- standard attributes -->
			<xsl:attribute name="id"><xsl:value-of select="concat('resource', $id)" /></xsl:attribute>
			<span class="oryx-type"><xsl:value-of select="$type" /></span>
			<span class="oryx-title"><xsl:value-of select="./name" /></span>
			<span class="oryx-description">
				<xsl:value-of select="./description" />
				<xsl:if test="not($at_desc='')"><xsl:value-of select="$at_desc" /></xsl:if>
			</span>
			<!-- additional attributes -->
			<xsl:if test="not($additional-property-name='')">
				<span>
					<xsl:attribute name="class"><xsl:value-of select="$additional-property-name" /></xsl:attribute>
					<xsl:value-of select="$additional-property-value" />
				</span>
			</xsl:if>
			<!-- bounds -->
			<xsl:if test="count(./graphics/position) > 0" >
				<xsl:variable name="x" select="./graphics/position/@x div 3" />
				<xsl:variable name="y" select="./graphics/position/@y div 3" />
				<xsl:variable name="w">
					<xsl:call-template name="get-width">
						<xsl:with-param name="name" select="$type" />
						<xsl:with-param name="value" select="./graphics/position/@width div 3" />
					</xsl:call-template>
				</xsl:variable>
				<xsl:variable name="h">
					<xsl:call-template name="get-height">
						<xsl:with-param name="name" select="$type" />
						<xsl:with-param name="value" select="./graphics/position/@height div 3" />
					</xsl:call-template>
				</xsl:variable>
				<span class="oryx-bounds">
					<xsl:value-of select="concat($x, ',', $y, ',', $x+$w, ',', $y+$h)" />
				</span>
			</xsl:if>
			<xsl:if test="not(count(./graphics/position) > 0)" >
				<span class="oryx-bounds">
					<xsl:value-of select="'200, 200, 300, 260'" />
				</span>
			</xsl:if>
			<!-- outgoing -->
			<xsl:for-each select="//directory/epc/arc/flow[@source=$id]">
				<a rel="raziel-outgoing">
					<xsl:attribute name="href"><xsl:value-of select="concat('#', parent::arc/attribute::id)" /></xsl:attribute>
				</a>
			</xsl:for-each>
			<xsl:for-each select="//directory/epc/relation[@from=$id]">
				<a rel="raziel-outgoing">
					<xsl:attribute name="href"><xsl:value-of select="concat('#', @id)" /></xsl:attribute>
				</a>
			</xsl:for-each>
		</div>
	</xsl:template>	

	<!-- Adds connector properties --> 
	<xsl:template name="add-connector-properties">
		<xsl:param name="type" />
		<xsl:variable name="id">
			<xsl:value-of select="@id" />
		</xsl:variable>
	 	<div>
			<!-- standard attributes -->
			<xsl:attribute name="id"><xsl:value-of select="concat('resource', $id)" /></xsl:attribute>
			<span class="oryx-type"><xsl:value-of select="$type" /></span>
			<!-- bounds -->
			<xsl:if test="count(./graphics/position) > 0" >
				<xsl:variable name="x" select="./graphics/position/@x div 3" />
				<xsl:variable name="y" select="./graphics/position/@y div 3" />
				<span class="oryx-bounds">
					<xsl:value-of select="concat($x, ',', $y, ',', $x+30, ',', $y+30)" />
				</span>
			</xsl:if>
			<xsl:if test="not(count(./graphics/position) > 0)" >
				<span class="oryx-bounds">
					<xsl:value-of select="'100, 100, 130, 130'" />
				</span>
			</xsl:if>
			<!-- outgoing -->
			<xsl:for-each select="//directory/epc/arc/flow[@source=$id]">
				<a rel="raziel-outgoing">
					<xsl:attribute name="href"><xsl:value-of select="concat('#', parent::arc/attribute::id)" /></xsl:attribute>
				</a>
			</xsl:for-each>
		</div>
	</xsl:template>	

	<!-- Adds the coordinates of a node's centrum -->
	<xsl:template name="coordinates-of-node">
		<xsl:param name="node-id" />
		<xsl:for-each select="//directory/epc/child::*[@id=$node-id]">
			<xsl:choose>
				<xsl:when test="name()='and' or name()='xor' or name()='or'">
					<xsl:value-of select="'15 15 '"/>
	  			</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="w">
						<xsl:call-template name="get-width">
							<xsl:with-param name="value" select="./graphics/position/@width div 3" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:variable name="h">
						<xsl:call-template name="get-height">
							<xsl:with-param name="value" select="./graphics/position/@height div 3" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:value-of select="concat($w*0.5, ' ', $h*0.5, ' ')"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>	

	<!-- Adds the coordinates of a node's centrum -->
	<xsl:template name="edge-bounds">
		<xsl:if test="count(./graphics/position) > 0" >
			<xsl:variable name="x" select="./graphics/position/@x"/>
			<xsl:variable name="y" select="./graphics/position/@y"/>
			
			<xsl:variable name="x1">
				<xsl:value-of select="$x[position()=1]"/>
			</xsl:variable>
			<xsl:variable name="y1">
				<xsl:value-of select="$y[position()=1]"/>
			</xsl:variable>		
			<xsl:variable name="x2">
				<xsl:value-of select="$x[position()=2]"/>
			</xsl:variable>
			<xsl:variable name="y2">
				<xsl:value-of select="$y[position()=2]"/>
			</xsl:variable>
			<xsl:value-of select="concat($x1 div 3, ',', $y1 div 3, ',', $x2 div 3, ',', $y2 div 3)" />
		</xsl:if>
		<xsl:if test="not(count(./graphics/position) > 0)" >
			<xsl:value-of select="concat(0, ',', 0, ',', 100, ',', 100)" />
		</xsl:if>
	</xsl:template>

	<!-- Returns a width value within the constraints -->
	<xsl:template name="get-width">
		<xsl:param name="name" />
		<xsl:param name="value" />
		<xsl:variable name="min">
			<xsl:choose>
				<xsl:when test="$name='http://b3mn.org/stencilset/epc#Organization'">60</xsl:when>
				<xsl:otherwise><xsl:choose>
					<xsl:when test="$name='http://b3mn.org/stencilset/epc#Position'">55</xsl:when>
					<xsl:otherwise>50</xsl:otherwise>
				</xsl:choose></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="max">
			<xsl:choose>
				<xsl:when test="$name='http://b3mn.org/stencilset/epc#Organization'">240</xsl:when>
				<xsl:otherwise><xsl:choose>
					<xsl:when test="$name='http://b3mn.org/stencilset/epc#Position'">220</xsl:when>
					<xsl:otherwise>200</xsl:otherwise>
				</xsl:choose></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="$value > $max"><xsl:value-of select="$max" /></xsl:if>
		<xsl:if test="not($value > $max)">
			<xsl:if test="$value > $min"><xsl:value-of select="$value" /></xsl:if>
			<xsl:if test="not($value > $min)"><xsl:value-of select="$min" /></xsl:if>
		</xsl:if>
	</xsl:template>
	<!-- Returns a height value within the constraints -->	
	<xsl:template name="get-height">
		<xsl:param name="name" />
		<xsl:param name="value" />
		<xsl:variable name="min">
			<xsl:choose>
				<xsl:when test="$name='http://b3mn.org/stencilset/epc#Organization'">25</xsl:when>
				<xsl:otherwise><xsl:choose>
					<xsl:when test="$name='http://b3mn.org/stencilset/epc#Position'">20</xsl:when>
					<xsl:otherwise>30</xsl:otherwise>
				</xsl:choose></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="max">
			<xsl:choose>
				<xsl:when test="$name='http://b3mn.org/stencilset/epc#Organization'">100</xsl:when>
				<xsl:otherwise><xsl:choose>
					<xsl:when test="$name='http://b3mn.org/stencilset/epc#Position'">80</xsl:when>
					<xsl:otherwise>120</xsl:otherwise>
				</xsl:choose></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:if test="$value > $max"><xsl:value-of select="$max" /></xsl:if>
		<xsl:if test="not($value > $max)">
			<xsl:if test="$value > $min"><xsl:value-of select="$value" /></xsl:if>
			<xsl:if test="not($value > $min)"><xsl:value-of select="$min" /></xsl:if>
		</xsl:if>
	</xsl:template>




	<!-- minimum of a set -->
	<xsl:template name="min">
		<xsl:param name="set" />
		<xsl:for-each select="$set">
			<xsl:sort data-type="number"/>
			<xsl:if test="position()=1">
				<xsl:value-of select="."/>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<!-- maximum of a set -->
	<xsl:template name="max">
		<xsl:param name="set" />
		<xsl:for-each select="$set">
			<xsl:sort data-type="number" order="descending"/>
			<xsl:if test="position()=1">
				<xsl:value-of select="."/>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>	
						
	<!-- Blocking Template -->
	<xsl:template match="HELPMEPLEASE" />

</xsl:stylesheet>