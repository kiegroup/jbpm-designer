<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:exc="http://docs.oasis-open.org/wsbpel/2.0/process/executable"
	xmlns:abs="http://docs.oasis-open.org/wsbpel/2.0/process/abstract">	
	<xsl:output method="xml" />
	
	<!-- Root element -->
	<xsl:template match="process|exc:process|abs:process">
		<root>
			
			<div class="-oryx-canvas" id="oryx-canvas123" style="display: none; width: 1200px; height: 600px;">
				<span class="oryx-type">http://b3mn.org/stencilset/bpel#worksheet</span>
				<span class="oryx-mode">writeable</span>
				<span class="oryx-mode">fullscreen</span>
				<a rel="oryx-stencilset" href="./stencilsets/bpel/bpel.json"/>
				<a rel="oryx-render" href="#oryx_0" />
				<xsl:call-template name="DFS-for-adding-render"/>
			</div>	
			
	         <div id="oryx_0">
				<span class="oryx-type">http://b3mn.org/stencilset/bpel#process</span>
				<span class="oryx-bounds">114,18,714,518</span>
				<xsl:call-template name="add-attributes"/>
				<xsl:call-template name="add-elements"/>
				<a rel="raziel-parent" href="#oryx-canvas123"/>
			</div>
			
			<xsl:call-template name="add-children-nodes">
				<xsl:with-param name="parentID">oryx_0</xsl:with-param>
			</xsl:call-template>	
			
			<xsl:call-template name="add-link-edges"/>
		</root>	 
	</xsl:template>
	
	<xsl:template name="add-attributes">		
		<xsl:for-each select="@*">
			<xsl:if test="name()!='id' and name()!='boundLUX' and name()!='boundLUY' and name()!='boundRLX' and name()!='boundRLY' and name()!='messageType' and name()!='element' and name()!='isNodeStencilSet' and name()!='isEdgeStencilSet'">
				<xsl:variable name="attributeName" select="translate(concat('oryx-',name()),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>		
				<span>
					<xsl:attribute name="class">
						<xsl:value-of select="$attributeName"/>
					</xsl:attribute>	
					<xsl:value-of select="."/>
				</span>	
			</xsl:if>	
		</xsl:for-each>			
	</xsl:template>
			
	<xsl:template name="add-children-nodes">
		<xsl:param name="parentID"/>
		
		<xsl:for-each select="*">
			
			<xsl:variable name="isNodeStencilSet" select="@isNodeStencilSet"/>
				
			<xsl:if test = "$isNodeStencilSet = 'true'">
				
				<xsl:variable name="id" select="@id"/>	
				<xsl:variable name="BoundLUX" select="@boundLUX" />
				<xsl:variable name="BoundLUY" select="@boundLUY" />
				<xsl:variable name="BoundRLX" select="@boundRLX" />
				<xsl:variable name="BoundRLY" select="@boundRLY" />
				
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
				
				<xsl:if test="name()='assign' or name()='if' or name()='elseif' or name()='else' or name()='flow' or name()='pick' or name()='onMessage' or name()='sequence' or name()='while' or name()='repeatUntil' or name()='forEach' or name()='scope' or name()='onAlarm' or name()='onEvent' or name()='eventHandlers'or name()='faultHandlers'or name()='compensationHandler' or name()='terminationHandler' or name()='catch' or name()='catchAll'">
				    <xsl:call-template name="add-children-nodes">
						<xsl:with-param name="parentID" select="$id"/>	
					</xsl:call-template>	
				</xsl:if>	
			</xsl:if>				
		</xsl:for-each>	
	</xsl:template>


	<xsl:template name="add-complex-type-elements">
		
		<!-- in <process> -->
		<xsl:if test="name()='process'">	
			<!--<oryx-extensions>-->
			<xsl:for-each select="extensions">
				<xsl:variable name="numberOfExtensionElement" select="count(extension)"/>
				<xsl:if test="not($numberOfExtensionElement=0)">
					<span>
						<xsl:attribute name="class">oryx-extensions</xsl:attribute>	
						
						<xsl:value-of select="concat('%7B%27totalCount%27%3A',$numberOfExtensionElement,'%2C%20%27items%27%3A%5B%7B')"/>
						
						<xsl:for-each select="extension[not(position()=last())]">
							<xsl:variable name="namespace" select='@namespace'/>
							<xsl:variable name="mustUnderstand" select='@mustUnderstand'/>
							
							<xsl:value-of select="concat('namespace%3A%22',$namespace,'%22%2C%20mustUnderstand%3A%22',$mustUnderstand,'%22%7D%2C%20%7B')"/>				
						</xsl:for-each>	
						
						<xsl:for-each select="extension[position()=last()]">
							<xsl:variable name="namespace" select='@namespace'/>
							<xsl:variable name="mustUnderstand" select='@mustUnderstand'/>
							
							<xsl:value-of select="concat('namespace%3A%22',$namespace,'%22%2C%20mustUnderstand%3A%22',$mustUnderstand,'%22%7D%5D%7D')"/>				
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>	
			
			<!--<oryx-import>-->
			<xsl:variable name="numberOfImportElement" select="count(import)"/>
			<xsl:if test="not($numberOfImportElement=0)">
				<span>
					<xsl:attribute name="class">oryx-import</xsl:attribute>	
					
					<xsl:value-of select="concat('%7B%27totalCount%27%3A',$numberOfImportElement,'%2C%20%27items%27%3A%5B%7B')"/>
					
					<xsl:for-each select="import[not(position()=last())]">
						<xsl:variable name="namespace" select='@namespace'/>
						<xsl:variable name="location" select='@location'/>
						<xsl:variable name="importType" select='@importType'/>
						
						<xsl:value-of select="concat('namespace%3A%22',$namespace,'%22%2C%20location%3A%22',$location,'%22%2C%20importType%3A%22',$location,'%22%7D%2C%20%7B')"/>				
					</xsl:for-each>	
					
					<xsl:for-each select="import[position()=last()]">
						<xsl:variable name="namespace" select='@namespace'/>
						<xsl:variable name="location" select='@location'/>
						<xsl:variable name="importType" select='@importType'/>
						
						<xsl:value-of select="concat('namespace%3A%22',$namespace,'%22%2C%20location%3A%22',$location,'%22%2C%20importType%3A%22',$location,'%22%7D%5D%7D')"/>				
					</xsl:for-each>	
				</span>
			</xsl:if>		
		</xsl:if>
		
		<!--- in <process> or <scope> -->
		<xsl:if test="name()='process' or name()='scope'">
			<!--<oryx-variables>-->
			<xsl:for-each select="variables">
				<xsl:variable name="numberOfVariableElement" select="count(variable)"/>
				<xsl:if test="not($numberOfVariableElement=0)">
					<span>
						<xsl:attribute name="class">oryx-variables</xsl:attribute>	
						
						<xsl:value-of select="concat('%7B%27totalCount%27%3A',$numberOfVariableElement,'%2C%20%27items%27%3A%5B%7B')"/>
						
						<xsl:for-each select="variable[not(position()=last())]">
							<xsl:variable name="name" select='@name'/>
							<xsl:variable name="messageType" select='@messageType'/>
							<xsl:variable name="type" select='@type'/>
							<xsl:variable name="element" select='@element'/>
							<xsl:variable name="fromspectype" select='from/@type'/>
							<xsl:variable name="fromspecvariablename" select='from/@variable'/>
							<xsl:variable name="fromspecpart" select='from/@part'/>
							<xsl:variable name="fromspecpartnerLink" select='from/@partnerLink'/>
							<xsl:variable name="fromspecendpointReference" select='from/@endpointReference'/>
							<xsl:variable name="fromspecquerylanguage" select='from/query/@queryLanguage'/>
							<xsl:variable name="fromspecquery" select='from/query'/>
							<xsl:variable name="fromspecproperty" select='from/@property'/>
							<xsl:variable name="fromspecexpressionlanguage" select='from/@expressionLanguage'/>
							<xsl:variable name="fromspecexpression" select='from'/>
							<xsl:variable name="fromspecliteral" select='from/literal'/>
							
							<xsl:value-of select="concat('name%3A%22',$name,'%22%2C%20messageType%3A%22',$messageType,'%22%2C%20type%3A%22',$type,'%22%2C%20element%3A%22',$element,'%22%2C%20fromspectype%3A%22',$fromspectype,'%22%2C%20fromspecvariablename%3A%22',$fromspecvariablename,'%22%2C%20fromspecpart%3A%22',$fromspecpart,'%22%2C%20fromspecpartnerLink%3A%22',$fromspecpartnerLink,'%22%2C%20fromspecendpointReference%3A%22',$fromspecendpointReference,'%22%2C%20fromspecquerylanguage%3A%22',$fromspecquerylanguage,'%22%2C%20fromspecquery%3A%22',$fromspecquery,'%22%2C%20fromspecproperty%3A%22',$fromspecproperty,'%22%2C%20fromspecexpressionlanguage%3A%22',$fromspecexpressionlanguage,'%22%2C%20fromspecexpression%3A%22',$fromspecexpression,'%22%2C%20fromspecliteral%3A%22',$fromspecliteral,'%22%7D%2C%20%7B')"/>				
						</xsl:for-each>	
						
						<xsl:for-each select="variable[position()=last()]">
							<xsl:variable name="name" select='@name'/>
							<xsl:variable name="messageType" select='@messageType'/>
							<xsl:variable name="type" select='@type'/>
							<xsl:variable name="element" select='@element'/>
							<xsl:variable name="fromspectype" select='from/@type'/>
							<xsl:variable name="fromspecvariablename" select='from/@variable'/>
							<xsl:variable name="fromspecpart" select='from/@part'/>
							<xsl:variable name="fromspecpartnerLink" select='from/@partnerLink'/>
							<xsl:variable name="fromspecendpointReference" select='from/@endpointReference'/>
							<xsl:variable name="fromspecquerylanguage" select='from/query/@queryLanguage'/>
							<xsl:variable name="fromspecquery" select='from/query'/>
							<xsl:variable name="fromspecproperty" select='from/@property'/>
							<xsl:variable name="fromspecexpressionlanguage" select='from/@expressionLanguage'/>
							<xsl:variable name="fromspecexpression" select='from'/>
							<xsl:variable name="fromspecliteral" select='from/literal'/>
							
							<xsl:value-of select="concat('name%3A%22',$name,'%22%2C%20messageType%3A%22',$messageType,'%22%2C%20type%3A%22',$type,'%22%2C%20element%3A%22',$element,'%22%2C%20fromspectype%3A%22',$fromspectype,'%22%2C%20fromspecvariablename%3A%22',$fromspecvariablename,'%22%2C%20fromspecpart%3A%22',$fromspecpart,'%22%2C%20fromspecpartnerLink%3A%22',$fromspecpartnerLink,'%22%2C%20fromspecendpointReference%3A%22',$fromspecendpointReference,'%22%2C%20fromspecquerylanguage%3A%22',$fromspecquerylanguage,'%22%2C%20fromspecquery%3A%22',$fromspecquery,'%22%2C%20fromspecproperty%3A%22',$fromspecproperty,'%22%2C%20fromspecexpressionlanguage%3A%22',$fromspecexpressionlanguage,'%22%2C%20fromspecexpression%3A%22',$fromspecexpression,'%22%2C%20fromspecliteral%3A%22',$fromspecliteral,'%22%7D%5D%7D')"/>				
						</xsl:for-each>
					</span>
				</xsl:if>	
			</xsl:for-each>
			
			<!--<oryx-PartnerLinks>-->
			<xsl:for-each select="partnerLinks">
				<xsl:variable name="numberOfPartnerLinkElement" select="count(partnerLink)"/>
				<xsl:if test="not($numberOfPartnerLinkElement=0)">
					<span>
						<xsl:attribute name="class">oryx-partnerlinks</xsl:attribute>	
						
						<xsl:value-of select="concat('%7B%27totalCount%27%3A',$numberOfPartnerLinkElement,'%2C%20%27items%27%3A%5B%7B')"/>
						
						<xsl:for-each select="partnerLink[not(position()=last())]">
							<xsl:variable name="name" select='@name'/>
							<xsl:variable name="PartnerLinkType" select='@partnerLinkType'/>
							<xsl:variable name="myRole" select='@myRole'/>
							<xsl:variable name="partnerRole" select='@partnerRole'/>
							<xsl:variable name="initializePartnerRole" select='@initializePartnerRole'/>
							
							<xsl:value-of select="concat('name%3A%22',$name,'%22%2C%20PartnerLinkType%3A%22',$PartnerLinkType,'%22%2C%20myRole%3A%22',$myRole,'%22%2C%20partnerRole%3A%22',$partnerRole,'%22%2C%20initializePartnerRole%3A%22',$initializePartnerRole,'%22%7D%2C%20%7B')"/>				
						</xsl:for-each>	
						
						<xsl:for-each select="partnerLink[position()=last()]">
							<xsl:variable name="name" select='@name'/>
							<xsl:variable name="PartnerLinkType" select='@partnerLinkType'/>
							<xsl:variable name="myRole" select='@myRole'/>
							<xsl:variable name="partnerRole" select='@partnerRole'/>
							<xsl:variable name="initializePartnerRole" select='@initializePartnerRole'/>
							
							<xsl:value-of select="concat('name%3A%22',$name,'%22%2C%20PartnerLinkType%3A%22',$PartnerLinkType,'%22%2C%20myRole%3A%22',$myRole,'%22%2C%20partnerRole%3A%22',$partnerRole,'%22%2C%20initializePartnerRole%3A%22',$initializePartnerRole,'%22%7D%5D%7D')"/>				
						</xsl:for-each>
					</span>
				</xsl:if>	
			</xsl:for-each>	
			
			<!--<oryx-CorrelationSets>-->
			<xsl:for-each select="correlationSets">
				<xsl:variable name="numberOfCorrelationSetElement" select="count(correlationSet)"/>
				<xsl:if test="not($numberOfCorrelationSetElement=0)">
					<span>
						<xsl:attribute name="class">oryx-correlationsets</xsl:attribute>	
						
						<xsl:value-of select="concat('%7B%27totalCount%27%3A',$numberOfCorrelationSetElement,'%2C%20%27items%27%3A%5B%7B')"/>
						
						<xsl:for-each select="correlationSet[not(position()=last())]">
							<xsl:variable name="name" select='@name'/>
							<xsl:variable name="properties" select='@properties'/>
							
							<xsl:value-of select="concat('name%3A%22',$name,'%22%2C%20properties%3A%22',$properties,'%22%7D%2C%20%7B')"/>				
						</xsl:for-each>	
						
						<xsl:for-each select="correlationSet[position()=last()]">
							<xsl:variable name="name" select='@name'/>
							<xsl:variable name="properties" select='@properties'/>
							
							<xsl:value-of select="concat('name%3A%22',$name,'%22%2C%20properties%3A%22',$properties,'%22%7D%5D%7D')"/>				
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>	
			
			<!--<oryx-MessageExchanges>-->
			<xsl:for-each select="messageExchanges">
				<xsl:variable name="numberOfMessageExchangeElement" select="count(messageExchange)"/>
				<xsl:if test="not($numberOfMessageExchangeElement=0)">
					<span>
						<xsl:attribute name="class">oryx-messageexchanges</xsl:attribute>	
						
						<xsl:value-of select="concat('%7B%27totalCount%27%3A',$numberOfMessageExchangeElement,'%2C%20%27items%27%3A%5B%7B')"/>
						
						<xsl:for-each select="messageExchange[not(position()=last())]">
							<xsl:variable name="name" select='@name'/>
							
							<xsl:value-of select="concat('name%3A%22',$name,'%22%7D%2C%20%7B')"/>				
						</xsl:for-each>	
						
						<xsl:for-each select="messageExchange[position()=last()]">
							<xsl:variable name="name" select='@name'/>
							
							<xsl:value-of select="concat('name%3A%22',$name,'%22%7D%5D%7D')"/>				
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>	
		</xsl:if>	
		
		<!-- in <invoke> or <receive> or <reply> -->
		<xsl:if test="name()='invoke' or name()='receive' or name()='reply'">
			<!--<oryx-Correlations>-->
			<xsl:for-each select="correlations">
				<xsl:variable name="numberOfCorrelationElement" select="count(correlation)"/>
				<xsl:if test="not($numberOfCorrelationElement=0)">
					<span>
						<xsl:attribute name="class">oryx-correlations</xsl:attribute>	
						
						<xsl:value-of select="concat('%7B%27totalCount%27%3A',$numberOfCorrelationElement,'%2C%20%27items%27%3A%5B%7B')"/>
						
						<xsl:for-each select="correlation[not(position()=last())]">
							<xsl:variable name="Correlation" select='@set'/>
							<xsl:variable name="initiate" select='@initiate'/>
							<xsl:variable name="pattern" select='@pattern'/>
							
							<xsl:value-of select="concat('Correlation%3A%22',$Correlation,'%22%2C%20initiate%3A%22',$initiate,'%22%2C%20pattern%3A%22',$pattern,'%22%7D%2C%20%7B')"/>				
						</xsl:for-each>	
						
						<xsl:for-each select="correlation[position()=last()]">
							<xsl:variable name="Correlation" select='@set'/>
							<xsl:variable name="initiate" select='@initiate'/>
							<xsl:variable name="pattern" select='@pattern'/>
							
							<xsl:value-of select="concat('Correlation%3A%22',$Correlation,'%22%2C%20initiate%3A%22',$initiate,'%22%2C%20pattern%3A%22',$pattern,'%22%7D%5D%7D')"/>				
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>	
		</xsl:if>	
		
		<!-- in <onMessage> or <onEvent> -->
		<xsl:if test="name()='onMessage' or name()='onEvent'">
			<!--<oryx-Correlations>-->
			<xsl:for-each select="correlations">
				<xsl:variable name="numberOfCorrelationElement" select="count(correlation)"/>
				<xsl:if test="not($numberOfCorrelationElement=0)">
					<span>
						<xsl:attribute name="class">oryx-correlations</xsl:attribute>	
						
						<xsl:value-of select="concat('%7B%27totalCount%27%3A',$numberOfCorrelationElement,'%2C%20%27items%27%3A%5B%7B')"/>
						
						<xsl:for-each select="correlation[not(position()=last())]">
							<xsl:variable name="Correlation" select='@set'/>
							<xsl:variable name="initiate" select='@initiate'/>
							
							<xsl:value-of select="concat('Correlation%3A%22',$Correlation,'%22%2C%20initiate%3A%22',$initiate,'%22%7D%2C%20%7B')"/>				
						</xsl:for-each>	
						
						<xsl:for-each select="correlation[position()=last()]">
							<xsl:variable name="Correlation" select='@set'/>
							<xsl:variable name="initiate" select='@initiate'/>
							
							<xsl:value-of select="concat('Correlation%3A%22',$Correlation,'%22%2C%20initiate%3A%22',$initiate,'%22%7D%5D%7D')"/>				
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>	
		</xsl:if>
		
		<!-- in <invoke> or <reply> -->
		<xsl:if test="name()='invoke' or name()='reply'">
			<!--<oryx-toParts>-->
			<xsl:for-each select="toParts">
				<xsl:variable name="numberOfToPartElement" select="count(toPart)"/>
				<xsl:if test="not($numberOfToPartElement=0)">
					<span>
						<xsl:attribute name="class">oryx-toparts</xsl:attribute>	
						
						<xsl:value-of select="concat('%7B%27totalCount%27%3A',$numberOfToPartElement,'%2C%20%27items%27%3A%5B%7B')"/>
						
						<xsl:for-each select="toPart[not(position()=last())]">
							<xsl:variable name="part" select='@part'/>
							<xsl:variable name="fromVariable" select='@fromVariable'/>
							
							<xsl:value-of select="concat('part%3A%22',$part,'%22%2C%20fromVariable%3A%22',$fromVariable,'%22%7D%2C%20%7B')"/>				
						</xsl:for-each>	
						
						<xsl:for-each select="toPart[position()=last()]">
							<xsl:variable name="part" select='@part'/>
							<xsl:variable name="fromVariable" select='@fromVariable'/>
							
							<xsl:value-of select="concat('part%3A%22',$part,'%22%2C%20fromVariable%3A%22',$fromVariable,'%22%7D%5D%7D')"/>				
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>		
		</xsl:if>
		
		<!-- in <invoke> or <onEvent>  or <onMessage>-->
		<xsl:if test="name()='invoke' or name()='onEvent' or name()='onMessage'">
			<!--<oryx-fromParts>-->
			<xsl:for-each select="fromParts">
				<xsl:variable name="numberOfFromPartElement" select="count(fromPart)"/>
				<xsl:if test="not($numberOfFromPartElement=0)">
					<span>
						<xsl:attribute name="class">oryx-fromparts</xsl:attribute>	
						
						<xsl:value-of select="concat('%7B%27totalCount%27%3A',$numberOfFromPartElement,'%2C%20%27items%27%3A%5B%7B')"/>
						
						<xsl:for-each select="fromPart[not(position()=last())]">
							<xsl:variable name="part" select='@part'/>
							<xsl:variable name="toVariable" select='@toVariable'/>
							
							<xsl:value-of select="concat('part%3A%22',$part,'%22%2C%20toVariable%3A%22',$toVariable,'%22%7D%2C%20%7B')"/>				
						</xsl:for-each>	
						
						<xsl:for-each select="fromPart[position()=last()]">
							<xsl:variable name="part" select='@part'/>
							<xsl:variable name="toVariable" select='@toVariable'/>
							
							<xsl:value-of select="concat('part%3A%22',$part,'%22%2C%20toVariable%3A%22',$toVariable,'%22%7D%5D%7D')"/>				
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>		
		</xsl:if>		
	</xsl:template>


	<xsl:template name="add-elements">
		<!--<documentation>-->
		<xsl:for-each select="documentation">
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName" select="name()"/>
				<xsl:with-param name="valueOfElement" select="."/>
			</xsl:call-template>	
		</xsl:for-each>	
		
		<!--<targets>-->
		<xsl:for-each select="targets">
			<xsl:for-each select="joinCondition">
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">joincond_explang</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="@expressionLanguage"/>
				</xsl:call-template>
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">joincond_boolexp</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="."/>
				</xsl:call-template>
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">joincond_opaque</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="@opaque"/>
				</xsl:call-template>
			</xsl:for-each>		
		</xsl:for-each>
		
		<!--from-spec and to-spec in <copy>-->
		<xsl:if test="name()='copy'">
			<xsl:call-template name="add-from-spec-elements"/>				
			<xsl:call-template name="add-to-spec-elements"/>
		</xsl:if>	
		
		<!--<extensionActivity>-->
		<xsl:if test="name()='extensionActivity'">
			<xsl:for-each select="*">
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">elementname</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="name()"/>
				</xsl:call-template>
			
				<xsl:call-template name="add-attributes"/>
				
				<xsl:call-template name="add-elements"/>		
			</xsl:for-each>	
		</xsl:if>
		
		<!--<for>|<Until> element in <wait> or <onAlarm>  -->
		<xsl:if test="name()='wait' or name()='onAlarm'">
			<xsl:for-each select="for|until">
				<xsl:if test="name()='for'">
					<xsl:call-template name="add-standard-element">
						<xsl:with-param name="elementName">forOrUntil</xsl:with-param>
						<xsl:with-param name="valueOfElement">for</xsl:with-param>
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="name()='until'">
					<xsl:call-template name="add-standard-element">
						<xsl:with-param name="elementName">forOrUntil</xsl:with-param>
						<xsl:with-param name="valueOfElement">until</xsl:with-param>
					</xsl:call-template>
				</xsl:if>		
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">comum_expressionLanguage</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="@expressionLanguage"/>
				</xsl:call-template>
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">expressionForOrUntil</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="."/>
				</xsl:call-template>
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">ForUntil_opaque</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="@opaque"/>
				</xsl:call-template>
			</xsl:for-each>	
		</xsl:if>

		<!--<condition> element in <while> or <repeatUntil>-->
		<xsl:if test="name()='while' or name()='repeatUntil'">
        	<xsl:for-each select="condition">
        		<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">condition_expressionLanguage</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="@expressionLanguage"/>
				</xsl:call-template>
        		<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">condition_booleanExpression</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="."/>
				</xsl:call-template>				
        		<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">condition_opaque</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="@opaque"/>
				</xsl:call-template> 
        	</xsl:for-each>	
		</xsl:if>
		
		<!--<startCounterValue> <finalCounterValue> <compleationCondition> elements in <forEach>-->
		<xsl:if test="name()='forEach'">
			<xsl:for-each select="startCounterValue">
        		<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">start_expLang</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="@expressionLanguage"/>
				</xsl:call-template>				
        		<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">start_intExp</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="."/>
				</xsl:call-template>
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">start_opaque</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="@opaque"/>
				</xsl:call-template>				
			</xsl:for-each>
			<xsl:for-each select="finalCounterValue">
        		<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">final_expLang</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="@expressionLanguage"/>
				</xsl:call-template>				
        		<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">final_intExp</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="."/>
				</xsl:call-template>				
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">final_opaque</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="@opaque"/>
				</xsl:call-template>
			</xsl:for-each>
			<xsl:for-each select="completionCondition">
				<xsl:for-each select="branches">
	        		<xsl:call-template name="add-standard-element">
						<xsl:with-param name="elementName">compCond_expLang</xsl:with-param>
						<xsl:with-param name="valueOfElement" select="@expressionLanguage"/>
					</xsl:call-template>				
	        		<xsl:call-template name="add-standard-element">
						<xsl:with-param name="elementName">successfulBranchesOnly</xsl:with-param>
						<xsl:with-param name="valueOfElement" select="@successfulBranchesOnly"/>
					</xsl:call-template>
	        		<xsl:call-template name="add-standard-element">
						<xsl:with-param name="elementName">branches_intExp</xsl:with-param>
						<xsl:with-param name="valueOfElement" select="."/>
					</xsl:call-template>
					<xsl:call-template name="add-standard-element">
						<xsl:with-param name="elementName">branches_opaque</xsl:with-param>
						<xsl:with-param name="valueOfElement" select="@opaque"/>
					</xsl:call-template>				
					</xsl:for-each>								
			</xsl:for-each>
		</xsl:if>		
		
		<!--<repeatEvery> element in <onAlarm>-->
		<xsl:if test="name()='onAlarm'">
			<xsl:for-each select="repeatEvery">
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">repeatExpressionLanguage</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="@expressionLanguage"/>
				</xsl:call-template>				
        		<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">repeatTimeExpression</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="."/>
				</xsl:call-template>
        		<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">repeat_opaque</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="@opaque"/>
				</xsl:call-template>				
			</xsl:for-each>	
		</xsl:if>
		
		<!-- <messageType> or <element> attributes in <onEvent>-->
		<xsl:if test="name()='onEvent'">
			<xsl:variable name="messageType" select="@messageType"/>
			<xsl:variable name="element" select="@element"/>
			
			<xsl:if test="$messageType!=''">
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">choiceType</xsl:with-param>
					<xsl:with-param name="valueOfElement">message</xsl:with-param>
				</xsl:call-template>
        		<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">choiceValue</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="$messageType"/>
				</xsl:call-template>
			</xsl:if>
			<xsl:if test="$element!=''">
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">choiceType</xsl:with-param>
					<xsl:with-param name="valueOfElement">element</xsl:with-param>
				</xsl:call-template>
        		<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">choiceValue</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="$element"/>
				</xsl:call-template>
			</xsl:if>						
		</xsl:if>	
		
		<!-- <outgoing> in activity -->
		<xsl:for-each select="outgoing">
			<xsl:variable name="linkID" select="@linkID"/>
						
    		<a rel="raziel-outgoing">
    			<xsl:attribute name="href">
					<xsl:value-of select="concat('#',$linkID)"/>
				</xsl:attribute>	
    		</a>					
		</xsl:for-each>	
		
    	<xsl:call-template name="add-complex-type-elements"/>
	</xsl:template>
	
	<xsl:template name="add-from-spec-elements">
		<xsl:for-each select="from">
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">fromspecvariablename</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="@variable"/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">fromspecpart</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="@part"/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">fromspecpartnerlink</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="@partnerLink"/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">fromspecendpointreference</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="@endpointReference"/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">fromspecquerylanguage</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="query/@queryLanguage"/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">fromspecquery</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="query"/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">fromspecproperty</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="@property"/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">fromspecexpressionlanguage</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="@expressionLanguage"/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">fromspecexpression</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="."/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">fromspecliteral</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="literal"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>	
		
		
	<xsl:template name="add-link-edges">
		<xsl:for-each select="linkInfoSet">
			<div> 
				<xsl:variable name="id" select="@id"/>
				
				<span class="oryx-type">http://b3mn.org/stencilset/bpel#link</span>
				
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">linkname</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="@linkname"/>
				</xsl:call-template>
				
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">tc_expressionlanguage</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="transitionCondition/@expressionLanguage"/>
				</xsl:call-template>
				
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">transition_expression</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="transitionCondition"/>
				</xsl:call-template>
				
				<xsl:call-template name="add-standard-element">
					<xsl:with-param name="elementName">tc_opaque</xsl:with-param>
					<xsl:with-param name="valueOfElement" select="transitionCondition/@opaque"/>
				</xsl:call-template>
				
				<xsl:variable name="targetID" select="@targetID"/>
				
				<a rel="raziel-outgoing">
	    			<xsl:attribute name="href">
						<xsl:value-of select="concat('#',$targetID)"/>
					</xsl:attribute>	
	    		</a>	
				
				<a rel="raziel-parent" href="#oryx-canvas123"/>
				
    			<!--span class="oryx-dockers">50 40 50 40  # </span-->
				
				<a rel="raziel-target">
	    			<xsl:attribute name="href">
						<xsl:value-of select="concat('#',$targetID)"/>
					</xsl:attribute>	
	    		</a>
			</div>	
		</xsl:for-each>
	</xsl:template>			


	<xsl:template name="add-standard-element">
		<xsl:param name="elementName"/>			
		<xsl:param name="valueOfElement"/>
		
		<xsl:if test="not($valueOfElement='')">
	 		<xsl:variable name="oryxElementName" select="translate(concat('oryx-',$elementName),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
				
			<span>
				<xsl:attribute name="class">
					<xsl:value-of select="$oryxElementName"/>
				</xsl:attribute>	
				<xsl:value-of select="$valueOfElement"/>
			</span>	
		</xsl:if>	
	</xsl:template>
		
	<xsl:template name="add-to-spec-elements">
		<xsl:for-each select="to">
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">tospecvariablename</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="@variable"/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">tospecpart</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="@part"/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">tospecpartnerlink</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="@partnerLink"/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">tospecquerylanguage</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="query/@queryLanguage"/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">tospecquery</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="query"/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">tospecproperty</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="@property"/>
			</xsl:call-template>
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName">tospecexpression"</xsl:with-param>
				<xsl:with-param name="valueOfElement" select="."/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>	

	<xsl:template name="DFS-for-adding-render">
		<xsl:for-each select="*">
			<xsl:variable name="isNodeStencilSet" select="@isNodeStencilSet"/>
		    <xsl:variable name="isEdgeStencilSet" select="@isEdgeStencilSet"/>
		     	
			<xsl:if test = "$isNodeStencilSet='true' or $isEdgeStencilSet='true' ">
			 	<xsl:variable name="id" select="@id"/>
	 		    		
	 			<a rel="oryx-render">
	 				<xsl:attribute name="href">
	 					<xsl:value-of select="concat('#',$id)" />
					</xsl:attribute>
				</a>
				
	  			<xsl:call-template name="DFS-for-adding-render"/>
			</xsl:if>	
		</xsl:for-each>
	</xsl:template>
	
</xsl:stylesheet>
		