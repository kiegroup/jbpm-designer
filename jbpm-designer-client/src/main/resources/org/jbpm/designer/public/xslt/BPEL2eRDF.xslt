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
			<xsl:if test="local-name()!='id' and local-name()!='boundLUX' and local-name()!='boundLUY' and local-name()!='boundRLX' and local-name()!='boundRLY' and local-name()!='messageType' and local-name()!='element' and local-name()!='isNodeStencilSet' and local-name()!='isEdgeStencilSet'">
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
						<xsl:text>http://b3mn.org/stencilset/bpel#</xsl:text>
						<xsl:choose>
							<xsl:when test="local-name()='elseif'">
								<xsl:text>if_branch</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="local-name()" />
							</xsl:otherwise>
						</xsl:choose>
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
				
				<xsl:if test="local-name()='assign' or local-name()='if' or local-name()='elseif' or local-name()='else' or local-name()='flow' or local-name()='pick' or local-name()='onMessage' or local-name()='sequence' or local-name()='while' or local-name()='repeatUntil' or local-name()='forEach' or local-name()='scope' or local-name()='onAlarm' or local-name()='onEvent' or local-name()='eventHandlers'or local-name()='faultHandlers'or local-name()='compensationHandler' or local-name()='terminationHandler' or local-name()='catch' or local-name()='catchAll'">
				    <xsl:call-template name="add-children-nodes">
						<xsl:with-param name="parentID" select="$id"/>	
					</xsl:call-template>	
				</xsl:if>	
			</xsl:if>				
		</xsl:for-each>	
	</xsl:template>


	<xsl:template name="add-complex-type-elements">
		
		<!-- in <process> -->
		<xsl:if test="local-name()='process'">	
			<!--<oryx-extensions>-->
			<xsl:for-each select="extensions">
				<xsl:variable name="numberOfExtensionElement" select="count(extension)"/>
				<xsl:if test="not($numberOfExtensionElement=0)">
					<span>
						<xsl:attribute name="class">oryx-extensions</xsl:attribute>	
						
						<xsl:text>{totalCount:</xsl:text>
						<xsl:value-of select="$numberOfExtensionElement" /><xsl:text>, items: [{</xsl:text>
						
						<xsl:for-each select="extension[not(position()=last())]">
							<xsl:variable name="namespace" select='@namespace'/>
							<xsl:variable name="mustUnderstand" select='@mustUnderstand'/>
							
							<xsl:text>namespace: "</xsl:text><xsl:value-of select="$namespace" /><xsl:text>", </xsl:text>
							<xsl:text>mustUnderstand: "</xsl:text><xsl:value-of select="$mustUnderstand" /><xsl:text>"} </xsl:text>
							<xsl:choose>
									<xsl:when test="position()=last()">
										<xsl:text>]}</xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>, {</xsl:text>
									</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>	
			
			<!--<oryx-import>-->
			<xsl:variable name="numberOfImportElement" select="count(import)"/>
			<xsl:if test="not($numberOfImportElement=0)">
				<span>
					<xsl:attribute name="class">oryx-import</xsl:attribute>	
					
					<xsl:text>{totalCount:</xsl:text><xsl:value-of select="$numberOfImportElement" /><xsl:text>, items: [{</xsl:text>
					
					<xsl:for-each select="import[not(position()=last())]">
						<xsl:variable name="namespace" select='@namespace'/>
						<xsl:variable name="location" select='@location'/>
						<xsl:variable name="importType" select='@importType'/>
						
						<xsl:text>namespace: "</xsl:text><xsl:value-of select="$namespace" /><xsl:text>", </xsl:text>
						<xsl:text>location: "</xsl:text><xsl:value-of select="$location" /><xsl:text>", </xsl:text>
						<xsl:text>importType: "</xsl:text><xsl:value-of select="$importType" /><xsl:text>"} </xsl:text>

						<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:text>]}</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>, {</xsl:text>
								</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>	
				</span>
			</xsl:if>		
		</xsl:if>
		
		<!--- in <process> or <scope> -->
		<xsl:if test="local-name()='process' or local-name()='scope'">
			<!--<oryx-variables>-->
			<xsl:for-each select="variables|abs:variables|exc:variables">
				<xsl:variable name="numberOfVariableElement" select="count(variable|abs:variable|exc:variable)"/>
				<xsl:if test="not($numberOfVariableElement=0)">
					<span>
						<xsl:attribute name="class">oryx-variables</xsl:attribute>	
						
						<xsl:text>{totalCount:</xsl:text><xsl:value-of select="$numberOfVariableElement" /><xsl:text>, items: [{</xsl:text>
						
						<xsl:for-each select="variable|abs:variable|exc:variable">
							<xsl:variable name="name" select='@name'/>
							<xsl:variable name="messageType" select='@messageType'/>
							<xsl:variable name="type" select='@type'/>
							<xsl:variable name="element" select='@element'/>
							<xsl:variable name="fromspectype" select='from/@type | abs:from/@type | exc:from/@type '/>
							<xsl:variable name="fromspecvariablename" select='from/@variable | abs:from/@variable | exc:from/@variable '/>
							<xsl:variable name="fromspecpart" select='from/@part | abs:from/@part | exc:from/@part '/>
							<xsl:variable name="fromspecpartnerLink" select='from/@partnerLink | abs:from/@partnerLink | exc:from/@partnerLink '/>
							<xsl:variable name="fromspecendpointReference" select='from/@endpointReference | abs:from/@endpointReference | exc:from/@endpointReference'/>
							<xsl:variable name="fromspecquerylanguage" select='from/query/@queryLanguage | abs:from/abs:query/@queryLanguage | exc:from/exc:query/@queryLanguage'/>
							<xsl:variable name="fromspecquery" select='from/query | abs:from/abs:query | exc:from/exc:query'/>
							<xsl:variable name="fromspecproperty" select='from/@property | abs:from/@property | exc:from/@property '/>
							<xsl:variable name="fromspecexpressionlanguage" select='from/@expressionLanguage | abs:from/@expressionLanguage | exc:from/@expressionLanguage '/>
							<xsl:variable name="fromspecexpression" select='from | abs:from | exc:from'/>
							<xsl:variable name="fromspecliteral" select='from/literal | abs:from/abs:literal | exc:from/exc:literal' />
										
							<xsl:text>name: "</xsl:text><xsl:value-of select="$name" /><xsl:text>", </xsl:text>
							<xsl:text>messageType: "</xsl:text><xsl:value-of select="$messageType" /><xsl:text>", </xsl:text>
							<xsl:text>type: "</xsl:text><xsl:value-of select="$type" /><xsl:text>", </xsl:text>
							<xsl:text>element: "</xsl:text><xsl:value-of select="$element" /><xsl:text>", </xsl:text>
							<xsl:text>fromspectype: "</xsl:text><xsl:value-of select="$fromspectype" /><xsl:text>", </xsl:text>
							<xsl:text>fromspecvariablename: "</xsl:text><xsl:value-of select="$fromspecvariablename" /><xsl:text>", </xsl:text>
							<xsl:text>fromspecpart: "</xsl:text><xsl:value-of select="$fromspecpart" /><xsl:text>", </xsl:text>
							<xsl:text>fromspecpartnerLink: "</xsl:text><xsl:value-of select="$fromspecpartnerLink" /><xsl:text>", </xsl:text>
							<xsl:text>fromspecendpointReference: "</xsl:text><xsl:value-of select="$fromspecendpointReference" /><xsl:text>", </xsl:text>
							<xsl:text>fromspecquerylanguage: "</xsl:text><xsl:value-of select="$fromspecquerylanguage" /><xsl:text>", </xsl:text>
							<xsl:text>fromspecquery: "</xsl:text><xsl:value-of select="$fromspecquery" /><xsl:text>", </xsl:text>
							<xsl:text>fromspecproperty: "</xsl:text><xsl:value-of select="$fromspecproperty" /><xsl:text>", </xsl:text>
							<xsl:text>fromspecexpressionlanguage: "</xsl:text><xsl:value-of select="$fromspecexpressionlanguage" /><xsl:text>", </xsl:text>
							<xsl:text>fromspecexpression: "</xsl:text><xsl:value-of select="normalize-space($fromspecexpression)" /><xsl:text>", </xsl:text> <!-- FIXME: normalize-space crashes python code. Trim, however, is not available in XPath 1.0 -->
							<xsl:text>fromspecliteral: "</xsl:text><xsl:value-of select="$fromspecliteral" /><xsl:text>"}</xsl:text>
							<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:text>]}</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>, {</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>
			
			<!--<oryx-PartnerLinks>-->
			<xsl:for-each select="partnerLinks | abs:partnerLinks | exc:partnerLinks">
				<xsl:variable name="numberOfPartnerLinkElement" select="count(partnerLink | abs:partnerLink | exc:partnerLinks)"/>
				<xsl:if test="not($numberOfPartnerLinkElement=0)">
					<span>
						<xsl:attribute name="class">oryx-partnerlinks</xsl:attribute>	
						
						<xsl:text>{totalCount:</xsl:text><xsl:value-of select="$numberOfPartnerLinkElement" /><xsl:text>, items: [{</xsl:text>
						
						<xsl:for-each select="partnerLink">
							<xsl:variable name="name" select='@name'/>
							<xsl:variable name="PartnerLinkType" select='@partnerLinkType'/>
							<xsl:variable name="myRole" select='@myRole'/>
							<xsl:variable name="partnerRole" select='@partnerRole'/>
							<xsl:variable name="initializePartnerRole" select='@initializePartnerRole'/>
							<xsl:text>name: "</xsl:text><xsl:value-of select="$name" /><xsl:text>", </xsl:text>
							<xsl:text>PartnerLinkType: "</xsl:text><xsl:value-of select="$PartnerLinkType" /><xsl:text>", </xsl:text>
							<xsl:text>myRole: "</xsl:text><xsl:value-of select="$myRole" /><xsl:text>", </xsl:text>
							<xsl:text>partnerRole: "</xsl:text><xsl:value-of select="$partnerRole" /><xsl:text>", </xsl:text>
							<xsl:text>initializePartnerRole: "</xsl:text><xsl:value-of select="$initializePartnerRole" /><xsl:text>"} </xsl:text>
							<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:text>]}</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>, {</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</span>
				</xsl:if>	
			</xsl:for-each>	
			
			<!--<oryx-CorrelationSets>-->
			<xsl:for-each select="correlationSets | abs:correlationSets | exc:correlationSets">
				<xsl:variable name="numberOfCorrelationSetElement" select="count(correlationSet|abs:correlationSet|exc:correlationSet)"/>
				<xsl:if test="not($numberOfCorrelationSetElement=0)">
					<span>
						<xsl:attribute name="class">oryx-correlationsets</xsl:attribute>	
						
						<xsl:text>{totalCount:</xsl:text><xsl:value-of select="$numberOfCorrelationSetElement" /><xsl:text>, items: [{</xsl:text>
						
						<xsl:for-each select="correlationSet">
							<xsl:variable name="name" select='@name'/>
							<xsl:variable name="properties" select='@properties'/>
							<xsl:text>name: "</xsl:text><xsl:value-of select="$name" /><xsl:text>", </xsl:text>
							<xsl:text>properties: "</xsl:text><xsl:value-of select="$properties" /><xsl:text>"} </xsl:text>
							<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:text>]}</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>, {</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>	
			
			<!--<oryx-MessageExchanges>-->
			<xsl:for-each select="messageExchanges|abs:messageExchanges|exc:messageExchanges">
				<xsl:variable name="numberOfMessageExchangeElement" select="count(messageExchange|abs:messageExchange|exc:messageExchange)"/>
				<xsl:if test="not($numberOfMessageExchangeElement=0)">
					<span>
						<xsl:attribute name="class">oryx-messageexchanges</xsl:attribute>	
						
						<xsl:text>{totalCount:</xsl:text><xsl:value-of select="$numberOfMessageExchangeElement" /><xsl:text>, items: [{</xsl:text>
						
						<xsl:for-each select="messageExchange|abs:messageExchange|exc:messageExchange">
							<xsl:variable name="name" select='@name'/>
							
							<xsl:text>name: "</xsl:text><xsl:value-of select="$name" /><xsl:text>"}</xsl:text>

							<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:text>]}</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>, {</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>	
		</xsl:if>	
		
		<!-- in <invoke> or <receive> or <reply> -->
		<xsl:if test="local-name()='invoke' or local-name()='receive' or local-name()='reply'">
			<!--<oryx-Correlations>-->
			<xsl:for-each select="correlations|abs:correlations|exc:correlations">
				<xsl:variable name="numberOfCorrelationElement" select="count(correlation|abs:correlation|exc:correlation)"/>
				<xsl:if test="not($numberOfCorrelationElement=0)">
					<span>
						<xsl:attribute name="class">oryx-correlations</xsl:attribute>	
						
						<xsl:text>{totalCount:</xsl:text><xsl:value-of select="$numberOfCorrelationElement" /><xsl:text>, items: [{</xsl:text>
						
						<xsl:for-each select="correlation|abs:correlation|exc:correlation">
							<xsl:variable name="Correlation" select='@set'/>
							<xsl:variable name="initiate" select='@initiate'/>
							<xsl:variable name="pattern" select='@pattern'/>
							
							<xsl:text>Correlation: "</xsl:text><xsl:value-of select="$Correlation" /><xsl:text>", </xsl:text>
							<xsl:text>initiate: "</xsl:text><xsl:value-of select="$initiate" /><xsl:text>", </xsl:text>
							<xsl:text>pattern: "</xsl:text><xsl:value-of select="$pattern" /><xsl:text>"} </xsl:text>
							
							<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:text>]}</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>, {</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>	
		</xsl:if>	
		
		<!-- in <onMessage> or <onEvent> -->
		<xsl:if test="local-name()='onMessage' or local-name()='onEvent'">
			<!--<oryx-Correlations>-->
			<xsl:for-each select="correlations|abs:correlations|exc:correlations">
				<xsl:variable name="numberOfCorrelationElement" select="count(correlation|abs:correlation|exc:correlation)"/>
				<xsl:if test="not($numberOfCorrelationElement=0)">
					<span>
						<xsl:attribute name="class">oryx-correlations</xsl:attribute>	
						
						<xsl:text>{totalCount:</xsl:text><xsl:value-of select="$numberOfCorrelationElement" /><xsl:text>, items: [{</xsl:text>
						
						<xsl:for-each select="correlation|abs:correlation|exc:correlation">
							<xsl:variable name="Correlation" select='@set'/>
							<xsl:variable name="initiate" select='@initiate'/>
							
							<xsl:text>Correlation: "</xsl:text><xsl:value-of select="$Correlation" /><xsl:text>", </xsl:text>
							<xsl:text>initiate: "</xsl:text><xsl:value-of select="$initiate" /><xsl:text>"} </xsl:text>

							<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:text>]}</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>, {</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>	
		</xsl:if>
		
		<!-- in <invoke> or <reply> -->
		<xsl:if test="local-name()='invoke' or local-name()='reply'">
			<!--<oryx-toParts>-->
			<xsl:for-each select="toParts|abs:toParts|exc:toParts">
				<xsl:variable name="numberOfToPartElement" select="count(toPart|abs:toPart|exc:toPart)"/>
				<xsl:if test="not($numberOfToPartElement=0)">
					<span>
						<xsl:attribute name="class">oryx-toparts</xsl:attribute>	
						
						<xsl:text>{totalCount:</xsl:text><xsl:value-of select="$numberOfToPartElement" /><xsl:text>, items: [{</xsl:text>
						
						<xsl:for-each select="toPart|abs:toPart|exc:toPart">
							<xsl:variable name="part" select='@part'/>
							<xsl:variable name="fromVariable" select='@fromVariable'/>
							
							<xsl:text>part: "</xsl:text><xsl:value-of select="$part" /><xsl:text>", </xsl:text>
							<xsl:text>fromVariable: "</xsl:text><xsl:value-of select="$fromVariable" /><xsl:text>"} </xsl:text>
							<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:text>]}</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>, {</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>		
		</xsl:if>
		
		<!-- in <invoke> or <onEvent>  or <onMessage>-->
		<xsl:if test="local-name()='invoke' or local-name()='onEvent' or local-name()='onMessage'">
			<!--<oryx-fromParts>-->
			<xsl:for-each select="fromParts|abs:fromParts|exc:fromParts">
				<xsl:variable name="numberOfFromPartElement" select="count(fromPart|abs:fromPart|exc:fromPart)"/>
				<xsl:if test="not($numberOfFromPartElement=0)">
					<span>
						<xsl:attribute name="class">oryx-fromparts</xsl:attribute>	
						
						<xsl:text>{totalCount:</xsl:text><xsl:value-of select="$numberOfFromPartElement" /><xsl:text>, items: [{</xsl:text>
						
						<xsl:for-each select="fromPart[not(position()=last())]">
							<xsl:variable name="part" select='@part'/>
							<xsl:variable name="toVariable" select='@toVariable'/>
							
							<xsl:text>part: "</xsl:text><xsl:value-of select="$part" /><xsl:text>", </xsl:text>
							<xsl:text>toVariable: "</xsl:text><xsl:value-of select="$toVariable" /><xsl:text>"} </xsl:text>
							
							<xsl:choose>
								<xsl:when test="position()=last()">
									<xsl:text>]}</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>, {</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>	
					</span>
				</xsl:if>	
			</xsl:for-each>		
		</xsl:if>		
	</xsl:template>


	<xsl:template name="add-elements">
		<!--<documentation>-->
		<xsl:for-each select="documentation|abs:documentation|exc:documentation">
			<xsl:call-template name="add-standard-element">
				<xsl:with-param name="elementName" select="name()"/>
				<xsl:with-param name="valueOfElement" select="."/>
			</xsl:call-template>	
		</xsl:for-each>	
		
		<!--<targets>-->
		<xsl:for-each select="targets|abs:targets|exc:targets">
			<xsl:for-each select="joinCondition|abs:joinCondition|exc:joinCondition">
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
		<xsl:if test="local-name()='copy'">
			<xsl:call-template name="add-from-spec-elements"/>				
			<xsl:call-template name="add-to-spec-elements"/>
		</xsl:if>	
		
		<!--<extensionActivity>-->
		<xsl:if test="local-name()='extensionActivity'">
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
		<xsl:if test="local-name()='wait' or local-name()='onAlarm'">
			<xsl:for-each select="for|abs:for|exc:for|until|abs:until|exc:until">
				<xsl:if test="local-name()='for'">
					<xsl:call-template name="add-standard-element">
						<xsl:with-param name="elementName">forOrUntil</xsl:with-param>
						<xsl:with-param name="valueOfElement">for</xsl:with-param>
					</xsl:call-template>
				</xsl:if>
				<xsl:if test="local-name()='until'">
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
		<xsl:if test="local-name()='while' or local-name()='repeatUntil'">
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
		<xsl:if test="local-name()='forEach'">
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
			<xsl:for-each select="finalCounterValue|abs:finalCounterValue|exc:finalCounterValue">
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
			<xsl:for-each select="completionCondition|abs:completionCondition|exc:completionCondition">
				<xsl:for-each select="branches|abs:branches|exc:branches">
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
		<xsl:if test="local-name()='onAlarm'">
			<xsl:for-each select="repeatEvery|abs:repeatEvery|exc:repeatEvery">
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
		<xsl:if test="local-name()='onEvent'">
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
		