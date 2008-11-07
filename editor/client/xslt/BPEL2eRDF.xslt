<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
		
	<xsl:output method="xml" />
	
	<!-- Root element -->
	<xsl:template match="process">
		<div class="processdata">
			<div id="oryxcanvas" class="-oryx-canvas">
				<span class="oryx-type">http://b3mn.org/stencilset/bpel#Process</span>
				<span class="oryx-mode">writeable</span>
				<span class="oryx-mode">fullscreen</span>
				<a rel="oryx-stencilset" href="./stencilsets/bpel/bpel.json"/>
				<xsl:call-template name="add-render"/>
			</div>	
	         <div id="oryx_1">
				<span class="oryx-type">http://b3mn.org/stencilset/bpel#BPEL</span>
				<span class="oryx-bounds">114,18,714,518</span>
				<a rel="raziel-parent" href="#oryx-canvas123"/>
				<xsl:call-template name="add-attributes"/>
				<xsl:call-template name="add-children-nodes">
					<xsl:with-param name="parentID">oryx_1</xsl:with-param>
					<xsl:with-param name="parentBoundLeftUpperX">114</xsl:with-param>
					<xsl:with-param name="parentBoundLeftUpperY">18</xsl:with-param>
				</xsl:call-template>		
			</div>
		</div>	 
	</xsl:template>
	
	
	<xsl:template name="add-attributes">		
		<xsl:for-each select="@*">
			<xsl:variable name="attributeName" select="translate(concat('oryx-',name()),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
				
			<span>
				<xsl:attribute name="class">
					<xsl:value-of select="$attributeName"/>
				</xsl:attribute>	
				<xsl:value-of select="."/>
			</span>	
		</xsl:for-each>			
	</xsl:template>
	
	
	<xsl:template name="add-children-nodes">
		<xsl:param name="parentID"/>
		<xsl:param name="parentBoundLeftUpperX"/>
		<xsl:param name="parentBoundLeftUpperY"/>
	
		<xsl:for-each select="invoke|receive|reply|assign|copy|empty|opaqueActivity|validate|extensionActivity|wait|throw|exit|rethrow|if|elseif|else|flow|sequence|link|pick|onMessage|onAlarm|while|repeatUntil|forEach|compensate|compensateScope|scope|onEvent|terminationHandler|catch|catchAll|compensationHandler">
			<xsl:variable name="id" select="concat($parentID,'_',position())" />
			<xsl:variable name="BoundLUX" select="$parentBoundLeftUpperX + position()*3+ 10" />
			<xsl:variable name="BoundLUY" select="$parentBoundLeftUpperY + position()*3+ 10" />
			<xsl:variable name="BoundRLX" select="$BoundLUX + 100" />
			<xsl:variable name="BoundRLY" select="$BoundLUY + 80" />
			
			<div>		
		 		<xsl:attribute name="id">
		 			<xsl:value-of select="$id"/>
		 		</xsl:attribute>		
		   		<span class="oryx-type">
					<xsl:value-of select="concat('http://b3mn.org/stencilset/bpel#',name())" />
				</span>
				
				<xsl:call-template name="add-attributes"/>
				
				<xsl:call-template name="add-elements"/>			
						
				<span class="oryx-bounds">
					<xsl:value-of select="concat($BoundLUX,',',$BoundLUY,',',$BoundRLX,',',$BoundRLY)" />
				</span>
				<a rel="raziel-parent">
					<xsl:attribute name="href">
						<xsl:value-of select="concat('#',$parentID)"/>
					</xsl:attribute>
				</a>		
				
			</div>	
			
			<xsl:if test="name()='assign' or name()='if' or name()='elseif' or name()='else' or name()='flow' or name()='pick' or name()='onMessage' or name()='sequence' or name()='while' or name()='repeatUntil' or name()='forEach' or name()='scope' or name()='onAlarm' or name()='onEvent' or name()='compensationHandler' or name()='terminationHandler' or name()='catch' or name()='catchAll'">
			    <xsl:call-template name="add-children-nodes">
					<xsl:with-param name="parentID" select="$id"/>	
					<xsl:with-param name="parentBoundLeftUpperX" select="$BoundLUX"/>
					<xsl:with-param name="parentBoundLeftUpperY" select="$BoundLUY"/>
				</xsl:call-template>	
			</xsl:if>				
		</xsl:for-each>	
	</xsl:template>


	<xsl:template name="add-complex-type-elements">
	</xsl:template>


	<xsl:template name="add-elements">
		
		<xsl:call-template name="add-standard-element">
			<xsl:with-param name="elementName" select="./documentation"/>
		</xsl:call-template>	
		
		<xsl:call-template name="add-complex-type-elements"/>
	</xsl:template>
	
			
	<xsl:template name="add-render">
		 <a rel="oryx-render" href="#oryx_1" />
		<xsl:call-template name="DFS-for-adding-render">
			<xsl:with-param name="parentID">#oryx_1</xsl:with-param>
	    </xsl:call-template>
	</xsl:template>
	
	
	<xsl:template name="add-standard-element">
		<xsl:param name="elementName"/>
			
		<!--???xsl:variable name="valueOfElement" select="$elementName"/>
		
		<xsl:if test="not($valueOfElement='')">
	 		<xsl:variable name="oryxElementName" select="translate(concat('oryx-',$elementName),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
				
			<span>
				<xsl:attribute name="class">
					<xsl:value-of select="$oryxElementName"/>
				</xsl:attribute>	
				<xsl:value-of select="$valueOfElement"/>
			</span>	
		</xsl:if-->	
	</xsl:template>
		
		
	<xsl:template name="DFS-for-adding-render">
		<xsl:param name="parentID"/>
		
		<xsl:for-each select="invoke|receive|reply|assign|copy|empty|opaqueActivity|validate|extensionActivity|wait|throw|exit|rethrow|if|elseif|else|flow|sequence|link|pick|onMessage|onAlarm|while|repeatUntil|forEach|compensate|compensateScope|scope|onEvent|terminationHandler|catch|catchAll|compensationHandler"> 
		    <xsl:variable name="id">
 		    	<xsl:value-of select="concat($parentID,'_',position())"/>
			</xsl:variable>	
 		    		
 			<a rel="oryx-render">
 				<xsl:attribute name="href">
 					<xsl:value-of select="$id" />
				</xsl:attribute>
			</a>
			
  			<xsl:call-template name="DFS-for-adding-render">
   				<xsl:with-param name="parentID" select="$id"/>
  			</xsl:call-template>
		</xsl:for-each>
    </xsl:template>		

</xsl:stylesheet>
		