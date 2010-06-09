<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:oryx="http://oryx-editor.org/"
	xmlns:raziel="http://raziel.org/">

	<xsl:output method="xml" />
	
	<xsl:template match="rdf:Description">	
		<xsl:variable name="typeString" select="./oryx:type" />	
		<xsl:variable name="type">
			<xsl:call-template name="get-exact-type">
				<xsl:with-param name="typeString" select="$typeString" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="$type='worksheet'">
			<!-- root element -->
			<worksheet>
				<xsl:variable name="realID"><xsl:value-of select="@rdf:about" /></xsl:variable>
				<xsl:call-template name="find-children-nodes">
					<xsl:with-param name="searchedParentID"><xsl:value-of select="$realID" /></xsl:with-param>
			    </xsl:call-template>
			</worksheet>	
	 	</xsl:if>
	</xsl:template>

	
	<xsl:template name="find-children-nodes">
		<xsl:param name="searchedParentID" />
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="currentParentID"><xsl:value-of select="(./raziel:parent/@rdf:resource)" /></xsl:variable>         
			<xsl:if test="$currentParentID = $searchedParentID">
      		  	<xsl:variable name="currentID"><xsl:value-of select="@rdf:about" /></xsl:variable>
				<xsl:variable name="typeString" select="./oryx:type" />	
				<xsl:variable name="type">
					<xsl:call-template name="get-exact-type">
						<xsl:with-param name="typeString" select="$typeString" />
					</xsl:call-template>
				</xsl:variable>
				
				<!--process-->
				<xsl:if test="$type='process'">
					
					<xsl:variable name="existsAbstractProcessProfileElement" select="count(./oryx:abstractprocessprofile)" />
					
					<!-- bpel4chor editor -->
					<xsl:if test="$existsAbstractProcessProfileElement=0">
						<process xmlns="http://docs.oasis-open.org/wsbpel/2.0/process/abstract">
							<xsl:attribute name="abstractProcessProfile">urn:HPI_IAAS:choreography:profile:2006/12</xsl:attribute>
							<xsl:call-template name="add-children-of-process-element"/>
							<xsl:call-template name="record-link-nodes"/>
						</process>
					</xsl:if>
					
					<!-- bpel editor -->
					<xsl:if test="$existsAbstractProcessProfileElement!=0">
						<xsl:variable name="abstractProcessProfile" select="./oryx:abstractprocessprofile" />
						
						<xsl:if test="$abstractProcessProfile='' or $abstractProcessProfile='null'">
							<process xmlns="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
								<xsl:call-template name="add-children-of-process-element"/>
								<xsl:call-template name="record-link-nodes"/>
							</process>	
						</xsl:if>
						
						<xsl:if test="$abstractProcessProfile!='' and $abstractProcessProfile!='null'">
							<process xmlns="http://docs.oasis-open.org/wsbpel/2.0/process/abstract">
								
								<xsl:if test="$abstractProcessProfile='observableBehavior'">
									<xsl:attribute name="abstractProcessProfile">http://docs.oasis-open.org/wsbpel/2.0/process/abstract/ap11/2006/08</xsl:attribute>
								</xsl:if>	
								
								<xsl:if test="$abstractProcessProfile='templates'">
									<xsl:attribute name="abstractProcessProfile">http://docs.oasis-open.org/wsbpel/2.0/process/abstract/simple-template/2006/08</xsl:attribute>
								</xsl:if>	
								
								<xsl:if test="$abstractProcessProfile!='observableBehavior' and $abstractProcessProfile!='templates'">
									<xsl:attribute name="abstractProcessProfile">
										<xsl:value-of select="$abstractProcessProfile"/>
									</xsl:attribute>	
								</xsl:if>
								
								<xsl:call-template name="add-children-of-process-element"/>
								
								<xsl:call-template name="record-link-nodes"/>
							</process>	
						</xsl:if>
					</xsl:if>
				</xsl:if>	
					
				<!--invoke-->
				<xsl:if test="$type='invoke'">
					<invoke>
						<xsl:call-template name="add-standard-attributes"/>
						
						<xsl:call-template name="add-bounds-attribute"/>

						<xsl:call-template name="add-partnerLink-portType-operation-attributes"/>
						
						<xsl:variable name="inputVariable" select="./oryx:inputvariable" />
						<xsl:if test="$inputVariable!=''">
							<xsl:attribute name="inputVariable">
								<xsl:value-of select="$inputVariable" />
							</xsl:attribute>
						</xsl:if>	
						
						<xsl:variable name="outputVariable" select="./oryx:outputvariable" />
						<xsl:if test="$outputVariable!=''">
							<xsl:attribute name="outputVariable">
								<xsl:value-of select="$outputVariable" />
							</xsl:attribute>
						</xsl:if>
						
						<xsl:call-template name="add-documentation-element"/>
						
						<xsl:call-template name="add-standard-elements"/>
						
						<xsl:call-template name="add-correlations-element"/>
						
						<xsl:call-template name="add-toParts-element"/>
						
						<xsl:call-template name="add-outgoing-element"/>
						
						<xsl:call-template name="add-fromParts-element"/>			
		            </invoke>
				</xsl:if>	
				
				<!--receive-->
				<xsl:if test="$type='receive'">
					<receive>
						<xsl:call-template name="add-standard-attributes"/>
		
						<xsl:call-template name="add-bounds-attribute"/>
						
						<xsl:call-template name="add-partnerLink-portType-operation-attributes"/>
		                						
						<xsl:call-template name="add-variable-attribute"/>	
						
						<xsl:call-template name="add-createInstance-attribute"/>
						
						<xsl:call-template name="add-messageExchange-attribute"/>	
						
						<xsl:call-template name="add-documentation-element"/>
						
						<xsl:call-template name="add-standard-elements"/>
						
						<xsl:call-template name="add-outgoing-element"/>
						
						<xsl:call-template name="add-correlations-element"/>				
					</receive>
				</xsl:if>
				
				<!--reply-->
				<xsl:if test="$type='reply'">
					<reply>
						<xsl:call-template name="add-standard-attributes"/>
						
						<xsl:call-template name="add-bounds-attribute"/>

						<xsl:call-template name="add-partnerLink-portType-operation-attributes"/>
						
						<xsl:call-template name="add-variable-attribute"/>
						
						<xsl:call-template name="add-messageExchange-attribute"/>
						
						<xsl:call-template name="add-faultName-attribute"/>	
												
		            	<xsl:call-template name="add-correlations-element"/>
						
						<xsl:call-template name="add-toParts-element"/>
						
						<xsl:call-template name="add-documentation-element"/>
						
						<xsl:call-template name="add-outgoing-element"/>
						
						<xsl:call-template name="add-standard-elements"/>
					</reply>
				</xsl:if>
				
				<!--assign-->
				<xsl:if test="$type='assign'">
					<assign>
						<xsl:call-template name="add-standard-attributes"/>
						
						<xsl:call-template name="add-bounds-attribute"/>
						
						<xsl:variable name="validate" select="./oryx:validate" />
						<xsl:if test="$validate!=''">
							<xsl:attribute name="validate">
								<xsl:value-of select="$validate" />
							</xsl:attribute>
						</xsl:if>
						
						<xsl:call-template name="add-documentation-element"/>
						
						<xsl:call-template name="add-standard-elements"/>
											
						<xsl:call-template name="add-outgoing-element"/>
						
				        <xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
					</assign>
				</xsl:if>
				
				<!--copy-->
				<xsl:if test="$type='copy'">
					<copy>
						<xsl:call-template name="add-standard-attributes"/>
						
						<xsl:call-template name="add-bounds-attribute"/>

						<xsl:variable name="keepSrcElementName" select="./oryx:keepsrcelementname" />
						<xsl:if test="$keepSrcElementName!=''">
							<xsl:attribute name="keepSrcElementName">
								<xsl:value-of select="$keepSrcElementName" />
							</xsl:attribute>
						</xsl:if>
						
						<xsl:variable name="ignoreMissingFromData" select="./oryx:ignoremissingfromdata" />
						<xsl:if test="$ignoreMissingFromData!=''">
							<xsl:attribute name="ignoreMissingFromData">
								<xsl:value-of select="$ignoreMissingFromData" />
							</xsl:attribute>
						</xsl:if>
						
						<xsl:call-template name="add-from-spec-elements"/>
						
						<xsl:call-template name="add-to-spec-elements"/>
						
						<xsl:call-template name="add-documentation-element"/>
		            </copy>
				</xsl:if>
				
				<!--empty-->
				<xsl:if test="$type='empty'">
					<empty>
						<xsl:call-template name="add-standard-attributes"/>
		            	<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-outgoing-element"/>
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-standard-elements"/>						
					</empty>
				</xsl:if>
				
				<!--opaqueActivity-->
				<xsl:if test="$type='opaqueActivity'">
					<opaqueActivity>
						<xsl:call-template name="add-standard-attributes"/>
		            	<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-outgoing-element"/>
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-standard-elements"/>
					</opaqueActivity>
				</xsl:if>
				
				<!--validate-->
				<xsl:if test="$type='validate'">
					<validate>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-bounds-attribute"/>

						<xsl:variable name="variables" select="./oryx:variables" />
						<xsl:if test="$variables!=''">
							<xsl:attribute name="variables">
								<xsl:value-of select="$variables" />
							</xsl:attribute>
						</xsl:if>	
						
						<xsl:call-template name="add-outgoing-element"/>
						<xsl:call-template name="add-documentation-element"/>						
						<xsl:call-template name="add-standard-elements"/>							
		            </validate>
				</xsl:if>
				
				<!--extensionActivity-->
				<xsl:if test="$type='extensionActivity'">
					<extensionActivity>
						<xsl:call-template name="add-bounds-attribute"/>
						
						<xsl:variable name="elementName" select="./oryx:elementname" />
						<xsl:if test="$elementName!=''">
							<xsl:element name="{$elementName}">
								<xsl:call-template name="add-standard-attributes"/>
								<xsl:call-template name="add-documentation-element"/>
								<xsl:call-template name="add-standard-elements"/>								
							</xsl:element>
						</xsl:if>
						
						<xsl:if test="$elementName=''">
							<xsl:element name="anyElementQName">
								<xsl:call-template name="add-standard-attributes"/>
								<xsl:call-template name="add-documentation-element"/>
								<xsl:call-template name="add-standard-elements"/>								
							</xsl:element>
						</xsl:if>	

						<xsl:call-template name="add-outgoing-element"/>						
		            </extensionActivity>
				</xsl:if>
				
				<!--wait-->
				<xsl:if test="$type='wait'">
					<wait>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-outgoing-element"/>						
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-standard-elements"/>
						<xsl:call-template name="add-ForOrUntil-element"/>
		            </wait>
				</xsl:if>
				
				<!--throw-->
				<xsl:if test="$type='throw'">
					<throw>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-faultName-attribute"/>
						<xsl:call-template name="add-faultVariable-attribute"/>
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-outgoing-element"/>						
						<xsl:call-template name="add-standard-elements"/>						
		            </throw>
				</xsl:if>
				
				<!--exit-->
				<xsl:if test="$type='exit'">
					<exit>
						<xsl:call-template name="add-standard-attributes"/>
		            	<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-outgoing-element"/>
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-standard-elements"/>						
					</exit>
				</xsl:if>
				
				<!--rethrow-->
				<xsl:if test="$type='rethrow'">
					<rethrow>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-outgoing-element"/>
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-standard-elements"/>						
		            </rethrow>
				</xsl:if>
				
				<!--if-->
				<xsl:if test="$type='if'">
					<if>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-outgoing-element"/>
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-standard-elements"/>						
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </if>
				</xsl:if>
				
				<!--if_branch-->
				<xsl:if test="$type='if_branch'">
					<elseif>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-condition-element"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
					</elseif>				
				</xsl:if>
				
				<!--else_branch-->
				<xsl:if test="$type='else_branch'">
					<else>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>						
		            </else>
				</xsl:if>
				
				<!--flow-->
				<xsl:if test="$type='flow'">
					<flow>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-outgoing-element"/>						
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-standard-elements"/>
					 	<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </flow>
				</xsl:if>
				
				<!--pick-->
				<xsl:if test="$type='pick'">
					<pick>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-createInstance-attribute"/>
						<xsl:call-template name="add-outgoing-element"/>
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-standard-elements"/>						
				        <xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </pick>
				</xsl:if>
				
				<!--onMessage-->
				<xsl:if test="$type='onMessage'">
					<onMessage>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-variable-attribute"/>	
						<xsl:call-template name="add-messageExchange-attribute"/>
						<xsl:call-template name="add-partnerLink-portType-operation-attributes"/>
						<xsl:call-template name="add-documentation-element"/>		            	
						<xsl:call-template name="add-correlations-element"/>
						<xsl:call-template name="add-fromParts-element"/>
					    <xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
					</onMessage>
				</xsl:if>
				
				<!--sequence-->
				<xsl:if test="$type='sequence'">
					<sequence>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-outgoing-element"/>
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-standard-elements"/>						
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </sequence>
				</xsl:if>
				
				<!--while-->
				<xsl:if test="$type='while'">
					<while>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-outgoing-element"/>						
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-standard-elements"/>
						<xsl:call-template name="add-condition-element"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </while>
				</xsl:if>
				
				<!--repeatUntil-->
				<xsl:if test="$type='repeatUntil'">
					<repeatUntil>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-outgoing-element"/>
						<xsl:call-template name="add-condition-element"/>
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-standard-elements"/>						
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </repeatUntil>
				</xsl:if>
				
				<!--forEach-->
				<xsl:if test="$type='forEach'">
					<forEach>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-bounds-attribute"/>

						<xsl:variable name="counterName" select="./oryx:countername" />
						<xsl:if test="$counterName!=''">
							<xsl:attribute name="counterName">
								<xsl:value-of select="$counterName" />
							</xsl:attribute>
						</xsl:if>	
						
						<xsl:variable name="parallel" select="./oryx:parallel" />
						<xsl:if test="$parallel!=''">
							<xsl:attribute name="parallel">
								<xsl:value-of select="$parallel" />
							</xsl:attribute>
						</xsl:if>	
						
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-outgoing-element"/>						
						<xsl:call-template name="add-standard-elements"/>						
						<xsl:call-template name="add-counterValue-elements"/>
						<xsl:call-template name="add-completionCondition-element"/>
						
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </forEach>
				</xsl:if>
				
				<!--compensate-->
				<xsl:if test="$type='compensate'">
					<compensate>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-outgoing-element"/>
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-standard-elements"/>						
		            </compensate>
				</xsl:if>
				
				<!--compensateScope-->
				<xsl:if test="$type='compensateScope'">
					<compensateScope>
						<xsl:call-template name="add-standard-attributes"/>						
						<xsl:call-template name="add-bounds-attribute"/>
					
						<xsl:variable name="target" select="./oryx:target" />
						<xsl:if test="$target!=''">
							<xsl:attribute name="target">
								<xsl:value-of select="$target" />
							</xsl:attribute>
						</xsl:if>	
						
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-outgoing-element"/>						
						<xsl:call-template name="add-standard-elements"/>
		            </compensateScope>
				</xsl:if>
				
				<!--scope-->
				<xsl:if test="$type='scope'">
					<scope>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-exitOnStandardFault-attribute"/>
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-outgoing-element"/>												
						<xsl:call-template name="add-variables-element"/>	
						<xsl:call-template name="add-partnerLinks-element"/>				
						<xsl:call-template name="add-correlationSets-element"/>
						<xsl:call-template name="add-messageExchanges-element"/>
						<xsl:call-template name="add-standard-elements"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </scope>
				</xsl:if>
				
				<!--onAlarm-->
				<xsl:if test="$type='onAlarm'">
					<onAlarm>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="add-ForOrUntil-element"/>
						<xsl:call-template name="add-repeatEvery-element"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </onAlarm>
				</xsl:if>
				
				<!--onEvent-->
				<xsl:if test="$type='onEvent'">
					<onEvent>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-variable-attribute"/>
						<xsl:call-template name="add-messageExchange-attribute"/>
						<xsl:call-template name="add-faultMessageOrFaultElement-attribute"/>						
						<xsl:call-template name="add-partnerLink-portType-operation-attributes"/>
						<xsl:call-template name="add-documentation-element"/>		            	
						<xsl:call-template name="add-correlations-element"/>
						<xsl:call-template name="add-fromParts-element"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>	
					</onEvent>
				</xsl:if>
				
				<!--eventHandlers-->
				<xsl:if test="$type='eventHandlers'">
					<eventHandlers>
						<xsl:call-template name="add-bounds-attribute"/>						
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </eventHandlers>
				</xsl:if>
				
				<!--faultHandlers-->
				<xsl:if test="$type='faultHandlers'">
					<faultHandlers>
						<xsl:call-template name="add-bounds-attribute"/>						
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </faultHandlers>
				</xsl:if>
				
				<!--compensationHandler-->
				<xsl:if test="$type='compensationHandler'">
					<compensationHandler>
						<xsl:call-template name="add-bounds-attribute"/>						
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </compensationHandler>
				</xsl:if>
				
				<!--terminationHandler-->
				<xsl:if test="$type='terminationHandler'">
					<terminationHandler>
						<xsl:call-template name="add-bounds-attribute"/>						
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </terminationHandler>
				</xsl:if>
				
				<!--catch-->
				<xsl:if test="$type='catch'">
					<catch>
						<xsl:call-template name="add-bounds-attribute"/>	
						<xsl:call-template name="add-faultName-attribute"/>
						<xsl:call-template name="add-faultVariable-attribute"/>
						<xsl:call-template name="add-faultMessageType-attribute"/>
						<xsl:call-template name="add-faultElement-attribute"/>
						<xsl:call-template name="add-documentation-element"/>	
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </catch>
				</xsl:if>
				
				<!--catchAll-->
				<xsl:if test="$type='catchAll'">
					<catchAll>
						<xsl:call-template name="add-bounds-attribute"/>
						<xsl:call-template name="add-documentation-element"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					    </xsl:call-template>
		            </catchAll>
				</xsl:if>				
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

		
	<xsl:template name="add-bounds-attribute">	
		<xsl:variable name="bounds" select="./oryx:bounds" />
		<xsl:if test="$bounds!=''">
			<xsl:attribute name="bounds">
				<xsl:value-of select="$bounds" />
			</xsl:attribute>
		</xsl:if>
	</xsl:template>	
	
	
	<xsl:template name="add-children-of-process-element">	
		<xsl:variable name="currentID"><xsl:value-of select="@rdf:about" /></xsl:variable>
		
        <xsl:call-template name="add-standard-attributes"/>
		
		<xsl:call-template name="add-documentation-element"/>
		
     	<xsl:call-template name="add-bounds-attribute"/>

		<xsl:variable name="targetNamespace" select="./oryx:targetnamespace" />
		<xsl:if test="$targetNamespace!=''">
			<xsl:attribute name="targetNamespace">
				<xsl:value-of select="$targetNamespace" />
			</xsl:attribute>	
		</xsl:if>		
		
		<xsl:variable name="queryLanguage" select="./oryx:querylanguage" />
		<xsl:if test="$queryLanguage!=''">
			<xsl:attribute name="queryLanguage">
				<xsl:value-of select="$queryLanguage" />
			</xsl:attribute>
		</xsl:if>	
		
		<xsl:variable name="expressionLanguage" select="./oryx:expressionlanguage" />
		<xsl:if test="$expressionLanguage!=''">
			<xsl:attribute name="expressionLanguage">
				<xsl:value-of select="$expressionLanguage" />
			</xsl:attribute>
		</xsl:if>	
		
  		<xsl:call-template name="add-exitOnStandardFault-attribute"/>

		<xsl:call-template name="add-otherxmlns-attribute"/>
		
		<xsl:call-template name="add-extension-declaration"/>		
		
		<xsl:call-template name="add-import-element"/>
		
		<xsl:call-template name="add-variables-element"/>	
		
		<xsl:call-template name="add-partnerLinks-element"/>	
		
		<xsl:call-template name="add-correlationSets-element"/>
		
		<xsl:call-template name="add-messageExchanges-element"/>
		
		<xsl:call-template name="find-children-nodes">
			<xsl:with-param name="searchedParentID" select="$currentID" />
	    </xsl:call-template>
	</xsl:template>
	
	
	<xsl:template name="add-completionCondition-element">
		<xsl:variable name="expressionLanguage" select="./oryx:branches_explang" />
		<xsl:variable name="successfulBranchesOnly" select="./oryx:successfulbranchesonly" />
		<xsl:variable name="branches_counter" select="./oryx:branches_intexp" />
		<xsl:variable name="opaque" select="./oryx:branches_opaque" />
		
		<xsl:if test="$expressionLanguage!='' or $successfulBranchesOnly!='' or $branches_counter!=''">
			<completionCondition>
				<branches>
					<xsl:if test="$expressionLanguage!=''">
						<xsl:attribute name="expressionLanguage">
							<xsl:value-of select="$expressionLanguage" />
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$successfulBranchesOnly!=''">
						<xsl:attribute name="successfulBranchesOnly">
							<xsl:value-of select="$successfulBranchesOnly" />
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$opaque='true'">
						<xsl:attribute name="opaque">yes</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="$branches_counter" />
				</branches>
			</completionCondition>
		</xsl:if>
	</xsl:template>
	
	
	<xsl:template name="add-condition-element">
		<xsl:variable name="expressionLanguage" select="./condition_expressionlanguage" />
		<xsl:variable name="expression" select="./oryx:condition_booleanexpression" />
		<xsl:variable name="opaque" select="./oryx:condition_opaque" />
		
		<condition>
			<xsl:if test="$expressionLanguage!=''">
				<xsl:attribute name="expressionLanguage">
					<xsl:value-of select="$expressionLanguage" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$opaque='true'">
				<xsl:attribute name="opaque">yes</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="$expression" />
		</condition>
	</xsl:template>
	
	
	<xsl:template name="add-correlations-element">
		<xsl:variable name="correlations" select="./oryx:correlations" />
		<xsl:if test="$correlations!=''">
			<correlations>
				<xsl:variable name="count">
					<xsl:call-template name="get-number-of-elements-in-complex-type">
						<xsl:with-param name="original_content" select="$correlations" />
					</xsl:call-template>
				</xsl:variable>	
				
				<xsl:call-template name="loop-for-adding-correlations-element">
					<xsl:with-param name="i">1</xsl:with-param>
					<xsl:with-param name="count" select="$count" />
					<xsl:with-param name="data-set" select="$correlations" />
				</xsl:call-template>
			</correlations>	
		</xsl:if>
	</xsl:template>
	
	
	<xsl:template name="add-correlationSets-element">
		<xsl:variable name="correlationSets" select="./oryx:correlationsets" />
		<xsl:if test="$correlationSets!=''">
			<correlationSets>
				<xsl:variable name="count">
					<xsl:call-template name="get-number-of-elements-in-complex-type">
						<xsl:with-param name="original_content" select="$correlationSets" />
					</xsl:call-template>
				</xsl:variable>	
				
				<xsl:call-template name="loop-for-adding-correlationSets-element">
					<xsl:with-param name="i">1</xsl:with-param>
					<xsl:with-param name="count" select="$count" />
					<xsl:with-param name="data-set" select="$correlationSets" />
				</xsl:call-template>
			</correlationSets>	
		</xsl:if>
	</xsl:template>
	

	<xsl:template name="add-counterValue-elements">
		<xsl:variable name="s_expressionLanguage" select="./oryx:start_explang" />
		<xsl:variable name="s_expression" select="./oryx:start_intexp" />
		<xsl:variable name="s_opaque" select="./oryx:start_opaque" />		
		
		<startCounterValue>
			<xsl:if test="$s_expressionLanguage!=''">
				<xsl:attribute name="expressionLanguage">
					<xsl:value-of select="$s_expressionLanguage" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$s_opaque='true'">
				<xsl:attribute name="opaque">yes</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="$s_expression" />
		</startCounterValue>
		
		<xsl:variable name="f_expressionLanguage" select="./oryx:final_explang" />
		<xsl:variable name="f_expression" select="./oryx:final_intexp" />
		<xsl:variable name="f_opaque" select="./oryx:final_opaque" />		
		
		<finalCounterValue>
			<xsl:if test="$f_expressionLanguage!=''">
				<xsl:attribute name="expressionLanguage">
					<xsl:value-of select="$f_expressionLanguage" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$f_opaque='true'">
				<xsl:attribute name="opaque">yes</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="$f_expression" />
		</finalCounterValue>	
	</xsl:template>



	<xsl:template name="add-createInstance-attribute">
		<xsl:variable name="createInstance" select="./oryx:createinstance" />

		<xsl:if test="$createInstance!=''">
			<xsl:attribute name="createInstance">
				<xsl:value-of select="$createInstance" />
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	
			
	<xsl:template name="add-documentation-element">
		<xsl:variable name="documentation" select="./oryx:documentation" />
		<xsl:if test="$documentation!=''">
			<documentation>
				<xsl:value-of select="$documentation" />
			</documentation>
		</xsl:if>
	</xsl:template>
		

	<xsl:template name="add-exitOnStandardFault-attribute">
		<xsl:variable name="exitOnStandardFault" select="./oryx:exitonstandardfault" />
		<xsl:if test="$exitOnStandardFault!=''">
			<xsl:attribute name="exitOnStandardFault">
				<xsl:value-of select="$exitOnStandardFault" />
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	

	<xsl:template name="add-extension-declaration">
		<xsl:variable name="extensions" select="./oryx:extensions" />
		<xsl:if test="$extensions!=''">
			<extensions>
				<xsl:variable name="count">
					<xsl:call-template name="get-number-of-elements-in-complex-type">
						<xsl:with-param name="original_content" select="$extensions" />
					</xsl:call-template>
				</xsl:variable>	
				
				<xsl:call-template name="loop-for-adding-extension-declaration">
					<xsl:with-param name="i">1</xsl:with-param>
					<xsl:with-param name="count" select="$count" />
					<xsl:with-param name="data-set" select="$extensions" />
				</xsl:call-template>
			</extensions>	
		</xsl:if>
	</xsl:template>
	

	<xsl:template name="add-faultElement-attribute">
		<xsl:variable name="faultElement" select="./oryx:faultelement" />

		<xsl:if test="$faultElement!=''">
			<xsl:attribute name="faultElement">
				<xsl:value-of select="$faultElement" />
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	
	
	<xsl:template name="add-faultMessageOrFaultElement-attribute">
		<xsl:variable name="type" select="./oryx:choicetype" />
		<xsl:variable name="value" select="./oryx:choicevalue" />
		
		<xsl:if test="$type='messageType'">
			<xsl:attribute name="faultMessageType">
				<xsl:value-of select="$value" />
			</xsl:attribute>
		</xsl:if>	
		
		<xsl:if test="$type='element'">
			<xsl:attribute name="element">
				<xsl:value-of select="$value" />
			</xsl:attribute>
		</xsl:if>
	</xsl:template>

	<xsl:template name="add-faultMessageType-attribute">
		<xsl:variable name="faultMessageType" select="./oryx:faultmessagetype" />

		<xsl:if test="$faultMessageType!=''">
			<xsl:attribute name="faultMessageType">
				<xsl:value-of select="$faultMessageType" />
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	
	
	<xsl:template name="add-faultName-attribute">
		<xsl:variable name="faultName" select="./oryx:faultname" />

		<xsl:if test="$faultName!=''">
			<xsl:attribute name="faultName">
				<xsl:value-of select="$faultName" />
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	

	<xsl:template name="add-faultVariable-attribute">
		<xsl:variable name="faultVariable" select="./oryx:faultvariable" />

		<xsl:if test="$faultVariable!=''">
			<xsl:attribute name="faultVariable">
				<xsl:value-of select="$faultVariable" />
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	
	
	<xsl:template name="add-ForOrUntil-element">
		<xsl:variable name="expressionLanguage" select="./oryx:comum_expressionlanguage" />
		<xsl:variable name="expression" select="./oryx:expressionfororuntil" />
		<xsl:variable name="opaque" select="./oryx:foruntil_opaque" />
		<xsl:variable name="ForOrUntil" select="./oryx:fororuntil" />
		
		<xsl:if test="$ForOrUntil='for'">
			<for>
				<xsl:if test="$expressionLanguage!=''">
					<xsl:attribute name="expressionLanguage">
						<xsl:value-of select="$expressionLanguage" />
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$opaque='true'">
					<xsl:attribute name="opaque">yes</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="$expression" />	
			</for>
		</xsl:if>	
		
		<xsl:if test="$ForOrUntil='until'">
			<until>
				<xsl:if test="$expressionLanguage!=''">
					<xsl:attribute name="expressionLanguage">
						<xsl:value-of select="$expressionLanguage" />
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$opaque='true'">
					<xsl:attribute name="opaque">yes</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="$expression" />	
			</until>
		</xsl:if>	
	</xsl:template>
	
	
	<xsl:template name="add-from-spec-elements">
		<xsl:variable name="fromspectype" select="./oryx:fromspectype" />
		<xsl:variable name="fromspecvariablename" select="./oryx:fromspecvariablename" />
		<xsl:variable name="fromspecpart" select="./oryx:fromspecpart" />
		<xsl:variable name="fromspecpartnerLink" select="./oryx:fromspecpartnerlink" />
		<xsl:variable name="fromspecendpointReference" select="./oryx:fromspecendpointreference" />
		<xsl:variable name="fromspecquerylanguage" select="./oryx:fromspecquerylanguage" />
		<xsl:variable name="fromspecquery" select="./oryx:fromspecquery" />
		<xsl:variable name="fromspecproperty" select="./oryx:fromspecproperty" />
		<xsl:variable name="fromspecexpressionlanguage" select="./oryx:fromspecexpressionlanguage" />
		<xsl:variable name="fromspecexpression" select="./oryx:fromspecexpression" />
		<xsl:variable name="fromspecliteral" select="./oryx:fromspecliteral" />	
		
		<from>
		
			<xsl:if test="$fromspecpartnerLink!='' or $fromspecendpointReference!=''">
				<xsl:attribute name="partnerLink">
					<xsl:value-of select="$fromspecpartnerLink" />
				</xsl:attribute>
				<xsl:attribute name="endpointReference">
					<xsl:value-of select="$fromspecendpointReference" />
				</xsl:attribute>
			</xsl:if>		
		
			<xsl:if test="$fromspecvariablename!='' and $fromspecproperty!=''">
				<xsl:attribute name="variable">
					<xsl:value-of select="$fromspecvariablename" />
				</xsl:attribute>
				<xsl:attribute name="property">
					<xsl:value-of select="$fromspecproperty" />
				</xsl:attribute>
			</xsl:if>	
		
			<xsl:if test="$fromspecexpressionlanguage!='' or $fromspecexpression!=''">
				<xsl:attribute name="expressionLanguage">
					<xsl:value-of select="$fromspecexpressionlanguage" />
				</xsl:attribute>
				<xsl:value-of select="$fromspecexpression" />						
			</xsl:if>	
		
			<xsl:if test="$fromspecliteral!=''">
				<literal>
					<xsl:value-of select="$fromspecliteral" />
				</literal>
			</xsl:if>
			
			<xsl:if test="$fromspecvariablename!='' and $fromspecpart!=''">
				<xsl:attribute name="variable">
					<xsl:value-of select="$fromspecvariablename" />
				</xsl:attribute>
				<xsl:attribute name="part">
					<xsl:value-of select="$fromspecpart" />
				</xsl:attribute>
				<xsl:if test="$fromspecquery!='' or $fromspecquerylanguage!=''">
					<query>
						<xsl:attribute name="queryLanguage">
							<xsl:value-of select="$fromspecquerylanguage" />
						</xsl:attribute>
						<xsl:value-of select="$fromspecquery" />
					</query>	
				</xsl:if>	
			</xsl:if>	
		</from>
    </xsl:template>
	
		
	<xsl:template name="add-fromParts-element">
		<xsl:variable name="fromParts" select="./oryx:fromparts" />
		<xsl:if test="$fromParts!=''">
			<fromParts>
				<xsl:variable name="count">
					<xsl:call-template name="get-number-of-elements-in-complex-type">
						<xsl:with-param name="original_content" select="$fromParts" />
					</xsl:call-template>
				</xsl:variable>	
				
				<xsl:call-template name="loop-for-adding-fromParts-element">
					<xsl:with-param name="i">1</xsl:with-param>
					<xsl:with-param name="count" select="$count" />
					<xsl:with-param name="data-set" select="$fromParts" />
				</xsl:call-template>
			</fromParts>	
		</xsl:if>
	</xsl:template>
	
	
	<xsl:template name="add-import-element">
		<xsl:variable name="import" select="./oryx:import" />
		<xsl:if test="$import!=''">
			
			<xsl:variable name="count">
				<xsl:call-template name="get-number-of-elements-in-complex-type">
					<xsl:with-param name="original_content" select="$import" />
				</xsl:call-template>
			</xsl:variable>	
			
			<xsl:call-template name="loop-for-adding-import-element">
				<xsl:with-param name="i">1</xsl:with-param>
				<xsl:with-param name="count" select="$count" />
				<xsl:with-param name="data-set" select="$import" />
			</xsl:call-template>		
		</xsl:if>
	</xsl:template>
				
	<xsl:template name="add-messageExchange-attribute">
		<xsl:variable name="messageExchange" select="./oryx:messageexchange" />

		<xsl:if test="$messageExchange!=''">
			<xsl:attribute name="messageExchange">
				<xsl:value-of select="$messageExchange" />
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	
	
	<xsl:template name="add-messageExchanges-element">
		<xsl:variable name="messageExchanges" select="./oryx:messageexchanges" />
		<xsl:if test="$messageExchanges!=''">
			<messageExchanges>
				<xsl:variable name="count">
					<xsl:call-template name="get-number-of-elements-in-complex-type">
						<xsl:with-param name="original_content" select="$messageExchanges" />
					</xsl:call-template>
				</xsl:variable>	
				
				<xsl:call-template name="loop-for-adding-messageExchanges-element">
					<xsl:with-param name="i">1</xsl:with-param>
					<xsl:with-param name="count" select="$count" />
					<xsl:with-param name="data-set" select="$messageExchanges" />
				</xsl:call-template>
			</messageExchanges>	
		</xsl:if>
	</xsl:template>
	
	
	<xsl:template name="add-otherxmlns-attribute">
		<xsl:variable name="otherxmlns" select="./oryx:otherxmlns" />
		<xsl:if test="$otherxmlns!=''">
			<xsl:variable name="count">
				<xsl:call-template name="get-number-of-elements-in-complex-type">
					<xsl:with-param name="original_content" select="$otherxmlns" />
				</xsl:call-template>
			</xsl:variable>
			
			<xsl:call-template name="loop-for-adding-otherxmlns-attribute">
				<xsl:with-param name="i">1</xsl:with-param>
				<xsl:with-param name="count" select="$count" />
				<xsl:with-param name="data-set" select="$otherxmlns" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
		
						
	<xsl:template name="add-outgoing-element">
		<xsl:variable name="numberOfOutgoingLinks" select="count(./raziel:outgoing)" />
		<xsl:if test="$numberOfOutgoingLinks!=0">
			<xsl:call-template name="loop-for-adding-outgoing-attribute">
				<xsl:with-param name="i">1</xsl:with-param>
				<xsl:with-param name="count" select="$numberOfOutgoingLinks" />
			</xsl:call-template>
		</xsl:if>	
	</xsl:template>
	

	<xsl:template name="add-partnerLinks-element">
		<xsl:variable name="partnerLinks" select="./oryx:partnerlinks" />
		<xsl:if test="$partnerLinks!=''">
			<partnerLinks>
				<xsl:variable name="count">
					<xsl:call-template name="get-number-of-elements-in-complex-type">
						<xsl:with-param name="original_content" select="$partnerLinks" />
					</xsl:call-template>
				</xsl:variable>	
				
				<xsl:call-template name="loop-for-adding-partnerLinks-element">
					<xsl:with-param name="i">1</xsl:with-param>
					<xsl:with-param name="count" select="$count" />
					<xsl:with-param name="data-set" select="$partnerLinks" />
				</xsl:call-template>
			</partnerLinks>	
		</xsl:if>
	</xsl:template>
	

	<xsl:template name="add-partnerLink-portType-operation-attributes">
		<xsl:variable name="partnerLink" select="./oryx:partnerlink" />
		<xsl:variable name="portType" select="./oryx:porttype" />
		<xsl:variable name="operation" select="./oryx:operation" />
		
		<xsl:if test="$partnerLink!=''">
			<xsl:attribute name="partnerLink">
				<xsl:value-of select="$partnerLink" />
			</xsl:attribute>
		</xsl:if>	

		<xsl:if test="$portType!=''">
			<xsl:attribute name="portType">
				<xsl:value-of select="$portType" />
			</xsl:attribute>
		</xsl:if>

		<xsl:if test="$operation!=''">
			<xsl:attribute name="operation">
				<xsl:value-of select="$operation" />
			</xsl:attribute>
		</xsl:if>	
	</xsl:template>	
	

	<xsl:template name="add-repeatEvery-element">
		<xsl:variable name="expressionLanguage" select="./oryx:repeatexpressionlanguage" />
		<xsl:variable name="expression" select="./oryx:repeattimeexpression" />
		<xsl:variable name="opaque" select="./oryx:repeat_opaque" />
		
		<xsl:if test="$expressionLanguage!='' or $opaque!='' or $expression!=''">
			<repeatEvery>
				<xsl:if test="$expressionLanguage!=''">
					<xsl:attribute name="expressionLanguage">
						<xsl:value-of select="$expressionLanguage" />
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$opaque='true'">
					<xsl:attribute name="opaque">yes</xsl:attribute>
				</xsl:if>
				<xsl:value-of select="$expression" />
			</repeatEvery>
		</xsl:if>
	</xsl:template>
	
							
	<xsl:template name="add-standard-attributes">			
		<xsl:variable name="name" select="./oryx:name" />
		<xsl:if test="$name!=''">
			<xsl:attribute name="name">
				<xsl:value-of select="$name" />
			</xsl:attribute>
		</xsl:if>
		
		<xsl:variable name="suppressJoinFailure" select="./oryx:suppressjoinfailure" />
		<xsl:if test="$suppressJoinFailure!=''">
			<xsl:attribute name="suppressJoinFailure">
				<xsl:value-of select="$suppressJoinFailure" />
			</xsl:attribute>
		</xsl:if>		
		
		<xsl:variable name="id" select="@rdf:about"/>
		<xsl:if test="$id!=''">
			<xsl:attribute name="id">
				<xsl:value-of select="$id" />
			</xsl:attribute>
		</xsl:if>
					
	</xsl:template>
	
	
	<xsl:template name="add-standard-elements">		
		<xsl:variable name="JC_expLang" select="./oryx:joincond_explang" />
		<xsl:variable name="JC_boolExp" select="./oryx:joincond_boolexp" />
		<xsl:variable name="JC_opaque" select="./oryx:joincond_opaque" />
		
		<xsl:if test=" $JC_expLang!='' or $JC_boolExp!=''">
	    	<targets>
	    		<joinCondition>
	    			<xsl:attribute name="expressionLanguage">
	    				<xsl:value-of select="$JC_expLang" />
					</xsl:attribute>
					<xsl:if test="$JC_opaque='true'">
						<xsl:attribute name="opaque">yes</xsl:attribute>
					</xsl:if>
	    			<xsl:value-of select="$JC_boolExp"/>
				</joinCondition>
			</targets>
		</xsl:if>					   		
	</xsl:template>
	

	<xsl:template name="add-to-spec-elements">
		<xsl:variable name="tospectype" select="./oryx:tospectype" />
		<xsl:variable name="tospecvariablename" select="./oryx:tospecvariablename" />
		<xsl:variable name="tospecpart" select="./oryx:tospecpart" />
		<xsl:variable name="tospecpartnerLink" select="./oryx:tospecpartnerlink" />
		<xsl:variable name="tospecquerylanguage" select="./oryx:tospecquerylanguage" />
		<xsl:variable name="tospecquery" select="./oryx:tospecquery" />
		<xsl:variable name="tospecproperty" select="./oryx:tospecproperty" />
		<xsl:variable name="tospecexpressionlanguage" select="./oryx:tospecexpressionlanguage" />
		<xsl:variable name="tospecexpression" select="./oryx:tospecexpression" />
		
		<to>
					
			<xsl:if test="$tospecpartnerLink!=''">
				<xsl:attribute name="partnerLink">
					<xsl:value-of select="$tospecpartnerLink" />
				</xsl:attribute>
			</xsl:if>		
		
			<xsl:if test="$tospecvariablename!='' and $tospecproperty!=''">
				<xsl:attribute name="variable">
					<xsl:value-of select="$tospecvariablename" />
				</xsl:attribute>
				<xsl:attribute name="property">
					<xsl:value-of select="$tospecproperty" />
				</xsl:attribute>
			</xsl:if>	
		
			<xsl:if test="$tospecexpressionlanguage!='' or $tospecexpression!=''">
				<xsl:attribute name="expressionLanguage">
					<xsl:value-of select="$tospecexpressionlanguage" />
				</xsl:attribute>
				<xsl:value-of select="$tospecexpression" />	
			</xsl:if>	
			
			<xsl:if test="$tospecvariablename!='' and $tospecpart!=''">
				<xsl:attribute name="variable">
					<xsl:value-of select="$tospecvariablename" />
				</xsl:attribute>
				<xsl:attribute name="part">
					<xsl:value-of select="$tospecpart" />
				</xsl:attribute>
				<xsl:if test="$tospecquery!='' or $tospecquerylanguage!=''">
					<query>
						<xsl:attribute name="queryLanguage">
							<xsl:value-of select="$tospecquerylanguage" />
						</xsl:attribute>
						<xsl:value-of select="$tospecquery" />
					</query>	
				</xsl:if>
			</xsl:if>
		</to>	
    </xsl:template>
	
	
	<xsl:template name="add-toParts-element">
		<xsl:variable name="toParts" select="./oryx:toparts" />
		<xsl:if test="$toParts!=''">
			<toParts>
				<xsl:variable name="count">
					<xsl:call-template name="get-number-of-elements-in-complex-type">
						<xsl:with-param name="original_content" select="$toParts" />
					</xsl:call-template>
				</xsl:variable>	
				
				<xsl:call-template name="loop-for-adding-toParts-element">
					<xsl:with-param name="i">1</xsl:with-param>
					<xsl:with-param name="count" select="$count" />
					<xsl:with-param name="data-set" select="$toParts" />
				</xsl:call-template>
			</toParts>	
		</xsl:if>
	</xsl:template>
	

	<xsl:template name="add-variable-attribute">
		<xsl:variable name="variable" select="./oryx:variable" />

		<xsl:if test="$variable!=''">
			<xsl:attribute name="variable">
				<xsl:value-of select="$variable" />
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	
			
	<xsl:template name="add-variables-element">
		<xsl:variable name="variables" select="./oryx:variables" />
		<xsl:if test="$variables!=''">
			<variables>
				<xsl:variable name="count">
					<xsl:call-template name="get-number-of-elements-in-complex-type">
						<xsl:with-param name="original_content" select="$variables" />
					</xsl:call-template>
				</xsl:variable>	
				
				<xsl:call-template name="loop-for-adding-variables-element">
					<xsl:with-param name="i">1</xsl:with-param>
					<xsl:with-param name="count" select="$count" />
					<xsl:with-param name="data-set" select="$variables" />
				</xsl:call-template>
			</variables>	
		</xsl:if>
	</xsl:template>
	

	
	<xsl:template name="get-exact-type">
		<xsl:param name="typeString" />
		<xsl:value-of select="substring-after($typeString, '#')" />
	</xsl:template>

	
	<xsl:template name="get-number-of-elements-in-complex-type">
		<xsl:param name="original_content" />
		<xsl:value-of select="substring-before(substring-after($original_content, 'totalCount%27%3A'), '%2C%20%27items') " />
	</xsl:template>	
	
	
	
	<xsl:template name="loop-for-adding-correlations-element">
		<xsl:param name="i"/>
 		<xsl:param name="count"/>
		<xsl:param name="data-set"/>
			
 		<xsl:if test="$i &lt;= $count">
 			<xsl:variable name="set" select="substring-before(substring-after($data-set, 'Correlation%3A%22'), '%22%2C%20initiate') " />
 			<xsl:variable name="initiate" select="substring-before(substring-after($data-set, 'initiate%3A%22'), '%22%2C%20pattern') " />
 			<xsl:variable name="pattern" select="substring-before(substring-after($data-set, 'pattern%3A%22'), '%22%7D') " />
			
			<correlation>
				<xsl:if test="$set!=''">
					<xsl:attribute name="set">
						<xsl:value-of select="$set" />
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$initiate!=''">
					<xsl:attribute name="initiate">
						<xsl:value-of select="$initiate" />
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$pattern!=''">
					<xsl:attribute name="pattern">
						<xsl:value-of select="$pattern" />
					</xsl:attribute>
				</xsl:if>			
			</correlation>
			
  			<xsl:call-template name="loop-for-adding-correlations-element">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
   				<xsl:with-param name="data-set" select="substring-after($data-set,'%22%7D%2C%20%7B')"/>
  			</xsl:call-template>
 		</xsl:if>
    </xsl:template>
	
	
	<xsl:template name="loop-for-adding-correlationSets-element">
		<xsl:param name="i"/>
 		<xsl:param name="count"/>
		<xsl:param name="data-set"/>
			
 		<xsl:if test="$i &lt;= $count">
 			<xsl:variable name="name" select="substring-before(substring-after($data-set, 'name%3A%22'), '%22%2C%20properties') " />
 			<xsl:variable name="properties" select="substring-before(substring-after($data-set, 'properties%3A%22'), '%22%7D') " />
			
			<correlationSet>
				<xsl:attribute name="name">
					<xsl:value-of select="$name" />
				</xsl:attribute>
				<xsl:attribute name="properties">
					<xsl:value-of select="$properties" />
				</xsl:attribute>				
			</correlationSet>
			
  			<xsl:call-template name="loop-for-adding-correlationSets-element">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
   				<xsl:with-param name="data-set" select="substring-after($data-set,'%22%7D%2C%20%7B')"/>
  			</xsl:call-template>
 		</xsl:if>
    </xsl:template>
	
		
	<xsl:template name="loop-for-adding-extension-declaration">
		<xsl:param name="i"/>
 		<xsl:param name="count"/>
		<xsl:param name="data-set"/>
			
 		<xsl:if test="$i &lt;= $count">
 			<xsl:variable name="namespace" select="substring-before(substring-after($data-set, 'namespace%3A%22'), '%22%2C%20mustUnderstand') " />
			<xsl:variable name="mustUnderstand" select="substring-before(substring-after($data-set, 'mustUnderstand%3A%22'), '%22%7D') " />
			
			<extension>
				<xsl:attribute name="namespace">
					<xsl:value-of select="$namespace" />
				</xsl:attribute>
				<xsl:attribute name="mustUnderstand">
					<xsl:value-of select="$mustUnderstand" />
				</xsl:attribute>
			</extension>
			
  			<xsl:call-template name="loop-for-adding-extension-declaration">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
   				<xsl:with-param name="data-set" select="substring-after($data-set,'%22%7D%2C%20%7B')"/>
  			</xsl:call-template>
 		</xsl:if>
    </xsl:template>
	
		
	<xsl:template name="loop-for-adding-fromParts-element">
		<xsl:param name="i"/>
 		<xsl:param name="count"/>
		<xsl:param name="data-set"/>
			
 		<xsl:if test="$i &lt;= $count">
  			<xsl:variable name="part" select="substring-before(substring-after($data-set, 'part%3A%22'), '%22%2C%20toVariable') " />
 			<xsl:variable name="toVariable" select="substring-before(substring-after($data-set, 'toVariable%3A%22'), '%22%7D') " />
			
			<fromPart>
				<xsl:attribute name="part">
					<xsl:value-of select="$part" />
				</xsl:attribute>	
				<xsl:attribute name="toVariable">
					<xsl:value-of select="$toVariable" />
				</xsl:attribute>		
			</fromPart>
			
  			<xsl:call-template name="loop-for-adding-fromParts-element">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
   				<xsl:with-param name="data-set" select="substring-after($data-set,'%22%7D%2C%20%7B')"/>
  			</xsl:call-template>
 		</xsl:if>
    </xsl:template>
	
	
	<xsl:template name="loop-for-adding-import-element">
		<xsl:param name="i"/>
 		<xsl:param name="count"/>
		<xsl:param name="data-set"/>
			
 		<xsl:if test="$i &lt;= $count">
 			<xsl:variable name="namespace" select="substring-before(substring-after($data-set, 'namespace%3A%22'), '%22%2C%20location') " />
			<xsl:variable name="location" select="substring-before(substring-after($data-set, 'location%3A%22'), '%22%2C%20importType') " />
			<xsl:variable name="importType" select="substring-before(substring-after($data-set, 'importType%3A%22'), '%22%7D') " />
		    
			<import>
				<xsl:attribute name="namespace">
					<xsl:value-of select="$namespace" />
				</xsl:attribute>
				<xsl:attribute name="location">
					<xsl:value-of select="$location" />
				</xsl:attribute>
				<xsl:attribute name="importType">
					<xsl:value-of select="$importType" />
				</xsl:attribute>
			</import>
			
  			<xsl:call-template name="loop-for-adding-import-element">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
   				<xsl:with-param name="data-set" select="substring-after($data-set,'%22%7D%2C%20%7B')"/>
  			</xsl:call-template>
 		</xsl:if>
    </xsl:template>
	

	<xsl:template name="loop-for-adding-messageExchanges-element">
		<xsl:param name="i"/>
 		<xsl:param name="count"/>
		<xsl:param name="data-set"/>
			
 		<xsl:if test="$i &lt;= $count">
 			<xsl:variable name="name" select="substring-before(substring-after($data-set, 'name%3A%22'), '%22%7D') " />
			
			<messageExchange>
				<xsl:attribute name="name">
					<xsl:value-of select="$name" />
				</xsl:attribute>			
			</messageExchange>
			
  			<xsl:call-template name="loop-for-adding-messageExchanges-element">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
   				<xsl:with-param name="data-set" select="substring-after($data-set,'%22%7D%2C%20%7B')"/>
  			</xsl:call-template>
 		</xsl:if>
    </xsl:template>
	
	
	<xsl:template name="loop-for-adding-otherxmlns-attribute">
		<xsl:param name="i"/>
 		<xsl:param name="count"/>
		<xsl:param name="data-set"/>
			
 		<xsl:if test="$i &lt;= $count">
 			<xsl:variable name="prefix" select="substring-before(substring-after($data-set, 'prefix%3A%22'), '%22%2C%20namespace') " />
			<xsl:variable name="namespace" select="substring-before(substring-after($data-set, 'namespace%3A%22'), '%22%7D') " />
			<xsl:variable name="attribute-name" select="concat('xmlns:',$prefix)" />
			<xsl:attribute name="{$attribute-name}">
				<xsl:value-of select="$namespace"/>
			</xsl:attribute>
			
  			<xsl:call-template name="loop-for-adding-otherxmlns-attribute">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
   				<xsl:with-param name="data-set" select="substring-after($data-set,'%22%7D%2C%20%7B')"/>
  			</xsl:call-template>
 		</xsl:if>
    </xsl:template>


	<xsl:template name="loop-for-adding-outgoing-attribute">
		<xsl:param name="i"/>
 		<xsl:param name="count"/>
			
 		<xsl:if test="$i &lt;= $count">
 			<xsl:variable name="linkID" select="(./raziel:outgoing/@rdf:resource)[$i]" />
			
			<!--record only links information-->
			<xsl:for-each select="//rdf:Description">
				<xsl:variable name="ID"><xsl:value-of select="@rdf:about" /></xsl:variable>
				<xsl:if test="$linkID = $ID">
					
					<xsl:variable name="typeString" select="./oryx:type" />	
					<xsl:variable name="type">
						<xsl:call-template name="get-exact-type">
							<xsl:with-param name="typeString" select="$typeString" />
						</xsl:call-template>
					</xsl:variable>
						
					<xsl:if test="$type='link'">
						<outgoingLink>
							<xsl:attribute name="linkID">
								<xsl:value-of select="$linkID" />
							</xsl:attribute>
						</outgoingLink>		
					</xsl:if>
				</xsl:if>
			</xsl:for-each>			
			
  			<xsl:call-template name="loop-for-adding-outgoing-attribute">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
  			</xsl:call-template>
 		</xsl:if>
    </xsl:template>
	

	<xsl:template name="loop-for-adding-partnerLinks-element">
		<xsl:param name="i"/>
 		<xsl:param name="count"/>
		<xsl:param name="data-set"/>
			
 		<xsl:if test="$i &lt;= $count">
 			<xsl:variable name="name" select="substring-before(substring-after($data-set, 'name%3A%22'), '%22%2C%20PartnerLinkType') " />
 			<xsl:variable name="partnerLinkType" select="substring-before(substring-after($data-set, 'PartnerLinkType%3A%22'), '%22%2C%20myRole') " />
 			<xsl:variable name="myRole" select="substring-before(substring-after($data-set, 'myRole%3A%22'), '%22%2C%20partnerRole') " />
 			<xsl:variable name="partnerRole" select="substring-before(substring-after($data-set, 'partnerRole%3A%22'), '%22%2C%20initializePartnerRole') " />
			<xsl:variable name="initializePartnerRole" select="substring-before(substring-after($data-set, 'initializePartnerRole%3A%22'), '%22%7D') " />
			
			<partnerLink>
				<xsl:if test="$name!=''">
					<xsl:attribute name="name">
						<xsl:value-of select="$name" />
					</xsl:attribute>
				</xsl:if>
				
				<xsl:if test="$partnerLinkType!=''">
					<xsl:attribute name="partnerLinkType">
						<xsl:value-of select="$partnerLinkType" />
					</xsl:attribute>
				</xsl:if>
				
				<xsl:if test="$myRole!=''">
					<xsl:attribute name="myRole">
						<xsl:value-of select="$myRole" />
					</xsl:attribute>
				</xsl:if>
				
				<xsl:if test="$partnerRole!=''">
					<xsl:attribute name="partnerRole">
						<xsl:value-of select="$partnerRole" />
					</xsl:attribute>
				</xsl:if>
				
				<xsl:if test="$initializePartnerRole!=''">
					<xsl:attribute name="initializePartnerRole">
						<xsl:value-of select="$initializePartnerRole" />
					</xsl:attribute>
				</xsl:if>	
					
			</partnerLink>
			
  			<xsl:call-template name="loop-for-adding-partnerLinks-element">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
   				<xsl:with-param name="data-set" select="substring-after($data-set,'%22%7D%2C%20%7B')"/>
  			</xsl:call-template>
 		</xsl:if>
    </xsl:template>
	

	<xsl:template name="loop-for-adding-toParts-element">
		<xsl:param name="i"/>
 		<xsl:param name="count"/>
		<xsl:param name="data-set"/>
			
 		<xsl:if test="$i &lt;= $count">
  			<xsl:variable name="part" select="substring-before(substring-after($data-set, 'part%3A%22'), '%22%2C%20Correlation') " />
 			<xsl:variable name="fromVariable" select="substring-before(substring-after($data-set, 'fromVariable%3A%22'), '%22%7D') " />
			
			<toPart>
				<xsl:attribute name="part">
					<xsl:value-of select="$part" />
				</xsl:attribute>	
				<xsl:attribute name="fromVariable">
					<xsl:value-of select="$fromVariable" />
				</xsl:attribute>		
			</toPart>
			
  			<xsl:call-template name="loop-for-adding-toParts-element">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
   				<xsl:with-param name="data-set" select="substring-after($data-set,'%22%7D%2C%20%7B')"/>
  			</xsl:call-template>
 		</xsl:if>
    </xsl:template>
	
				
	<xsl:template name="loop-for-adding-variables-element">
		<xsl:param name="i"/>
 		<xsl:param name="count"/>
		<xsl:param name="data-set"/>
			
 		<xsl:if test="$i &lt;= $count">
 			<xsl:variable name="name" select="substring-before(substring-after($data-set, 'name%3A%22'), '%22%2C%20messageType') " />
 			<xsl:variable name="messageType" select="substring-before(substring-after($data-set, 'messageType%3A%22'), '%22%2C%20type') " />
 			<xsl:variable name="type" select="substring-before(substring-after($data-set, 'type%3A%22'), '%22%2C%20element') " />
 			<xsl:variable name="element" select="substring-before(substring-after($data-set, 'element%3A%22'), '%22%2C%20fromspectype') " />
 			<xsl:variable name="fromspectype" select="substring-before(substring-after($data-set, 'fromspectype%3A%22'), '%22%2C%20fromspecvariablename') " />
 			<xsl:variable name="fromspecvariablename" select="substring-before(substring-after($data-set, 'fromspecvariablename%3A%22'), '%22%2C%20fromspecpart') " />
 			<xsl:variable name="fromspecpart" select="substring-before(substring-after($data-set, 'fromspecpart%3A%22'), '%22%2C%20fromspecpartnerLink') " />
 			<xsl:variable name="fromspecpartnerLink" select="substring-before(substring-after($data-set, 'fromspecpartnerLink%3A%22'), '%22%2C%20fromspecendpointReference') " />
 			<xsl:variable name="fromspecendpointReference" select="substring-before(substring-after($data-set, 'fromspecendpointReference%3A%22'), '%22%2C%20fromspecquerylanguage') " />
 			<xsl:variable name="fromspecquerylanguage" select="substring-before(substring-after($data-set, 'fromspecquerylanguage%3A%22'), '%22%2C%20fromspecquery') " />
 			<xsl:variable name="fromspecquery" select="substring-before(substring-after($data-set, 'fromspecquery%3A%22'), '%22%2C%20fromspecproperty') " />
 			<xsl:variable name="fromspecproperty" select="substring-before(substring-after($data-set, 'fromspecproperty%3A%22'), '%22%2C%20fromspecexpressionlanguage') " />
 			<xsl:variable name="fromspecexpressionlanguage" select="substring-before(substring-after($data-set, 'fromspecexpressionlanguage%3A%22'), '%22%2C%20fromspecexpression') " />
 			<xsl:variable name="fromspecexpression" select="substring-before(substring-after($data-set, 'fromspecexpression%3A%22'), '%22%2C%20messageType') " />
			<xsl:variable name="fromspecliteral" select="substring-before(substring-after($data-set, 'fromspecliteral%3A%22'), '%22%7D') " />
			
			<variable>
				<xsl:attribute name="name">
					<xsl:value-of select="$name" />
				</xsl:attribute>
				<xsl:attribute name="messageType">
					<xsl:value-of select="$messageType" />
				</xsl:attribute>
				<xsl:attribute name="type">
					<xsl:value-of select="$type" />
				</xsl:attribute>
				<xsl:attribute name="element">
					<xsl:value-of select="$element" />
				</xsl:attribute>
				
				<xsl:if test="$fromspecvariablename!='' and $fromspecpart!=''">
					<from>
						<xsl:attribute name="variable">
							<xsl:value-of select="$fromspecvariablename" />
						</xsl:attribute>
						<xsl:attribute name="part">
							<xsl:value-of select="$fromspecpart" />
						</xsl:attribute>
						<xsl:if test="$fromspecquery!='' or $fromspecquerylanguage!=''">
							<quary>
								<xsl:attribute name="queryLanguage">
									<xsl:value-of select="$fromspecquerylanguage" />
								</xsl:attribute>
								<xsl:value-of select="$fromspecquery" />
							</quary>	
						</xsl:if>	
					</from>
				</xsl:if>
				
				<xsl:if test="$fromspecpartnerLink!='' or $fromspecendpointReference!=''">
					<from>
						<xsl:attribute name="partnerLink">
							<xsl:value-of select="$fromspecpartnerLink" />
						</xsl:attribute>
						<xsl:attribute name="endpointReference">
							<xsl:value-of select="$fromspecendpointReference" />
						</xsl:attribute>
					</from>
				</xsl:if>		
				
				<xsl:if test="$fromspecvariablename!='' and $fromspecproperty!=''">
					<from>
						<xsl:attribute name="variable">
							<xsl:value-of select="$fromspecvariablename" />
						</xsl:attribute>
						<xsl:attribute name="property">
							<xsl:value-of select="$fromspecproperty" />
						</xsl:attribute>
					</from>
				</xsl:if>	
				
				<xsl:if test="$fromspecexpressionlanguage!='' or $fromspecexpression!=''">
					<from>
						<xsl:attribute name="expressionLanguage">
							<xsl:value-of select="$fromspecexpressionlanguage" />
						</xsl:attribute>
						<xsl:value-of select="$fromspecexpression" />						
					</from>
				</xsl:if>	
				
				<xsl:if test="$fromspecliteral!=''">
					<from>
						<literal>
							<xsl:value-of select="$fromspecliteral" />
						</literal>
					</from>
				</xsl:if>	
			</variable>
			
  			<xsl:call-template name="loop-for-adding-variables-element">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
   				<xsl:with-param name="data-set" select="substring-after($data-set,'%22%7D%2C%20%7B')"/>
  			</xsl:call-template>
 		</xsl:if>
    </xsl:template>
	
	<xsl:template name="record-link-nodes">
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="typeString" select="./oryx:type" />	
			<xsl:variable name="type">
				<xsl:call-template name="get-exact-type">
					<xsl:with-param name="typeString" select="$typeString" />
				</xsl:call-template>
			</xsl:variable>

			<xsl:if test="$type='link'">
				<linkInfoSet>
					<xsl:variable name="id" select="@rdf:about " />
 					<xsl:variable name="linkName" select="./oryx:linkname" />
					<xsl:variable name="targetID" select="./raziel:outgoing/@rdf:resource" />
 					<xsl:variable name="transCond_expLang" select="./oryx:tc_expressionlanguage" />
		 			<xsl:variable name="transCond_boolExp" select="./oryx:transition_expression " />
		 			<xsl:variable name="transCond_opaque" select="./oryx:tc_opaque" />
					
					<xsl:if test="$id!=''">
						<xsl:attribute name="id">
							<xsl:value-of select="$id" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:if test="$linkName!=''">
						<xsl:attribute name="linkName">
							<xsl:value-of select="$linkName" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:if test="$targetID!=''">
						<xsl:attribute name="targetID">
							<xsl:value-of select="$targetID" />
						</xsl:attribute>
					</xsl:if>					
					
					<transitionCondition>
						<xsl:if test="$transCond_expLang!=''">
							<xsl:attribute name="transCond_expLang">
								<xsl:value-of select="$transCond_expLang" />
							</xsl:attribute>
						</xsl:if>
						
						<xsl:if test="$transCond_opaque='true'">
							<xsl:attribute name="transCond_opaque">yes</xsl:attribute>
						</xsl:if>
						
						<xsl:if test="$transCond_boolExp!=''">
							<xsl:value-of select="$transCond_boolExp" />
						</xsl:if>
					</transitionCondition>
	
				</linkInfoSet>	
			</xsl:if>
		</xsl:for-each>	
	</xsl:template>			
	
	
</xsl:stylesheet>