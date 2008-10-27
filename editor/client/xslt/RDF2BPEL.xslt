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
				<xsl:with-param name="typeString"><xsl:value-of select="$typeString" /></xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
			
		<xsl:if test="$type='BPEL'">
			<!-- BPEL -->
			<xsl:variable name="realID"><xsl:value-of select="@rdf:about" /></xsl:variable>
			<process>
                <xsl:call-template name="add-standard-attributes"/>
				<xsl:call-template name="add-standard-elements"/>	
				<xsl:call-template name="find-children-nodes">
					<xsl:with-param name="searchedParentID"><xsl:value-of select="$realID" /></xsl:with-param>
			    </xsl:call-template>
			</process>
	 	</xsl:if>
		
	</xsl:template>
	
	<xsl:template name="find-children-nodes">
		<xsl:param name="searchedParentID" />
		<xsl:variable name="childrenCounter" select="0" />
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="currentParentID"><xsl:value-of select="*/@rdf:resource" /></xsl:variable>         
			<xsl:if test="$currentParentID = $searchedParentID">
				<xsl:variable name="childrenCounter" select="$childrenCounter + 1" />
      		  	<xsl:variable name="realID"><xsl:value-of select="@rdf:about" /></xsl:variable>
				<xsl:variable name="typeString" select="./oryx:type" />	
				<xsl:variable name="type">
					<xsl:call-template name="get-exact-type">
						<xsl:with-param name="typeString"><xsl:value-of select="$typeString" /></xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
				
				<!--invoke-->
				<xsl:if test="$type='invoke'">
					<invoke>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </invoke>
				</xsl:if>
				
				<!--receive-->
				<xsl:if test="$type='receive'">
					<receive>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </receive>
				</xsl:if>
				
				<!--reply-->
				<xsl:if test="$type='reply'">
					<reply>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </reply>
				</xsl:if>
				
				<!--assign-->
				<xsl:if test="$type='assign'">
					<assign>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
				        <xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$realID" /></xsl:with-param>
					    </xsl:call-template>
					</assign>
				</xsl:if>
				
				<!--copy-->
				<xsl:if test="$type='copy'">
					<copy>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </copy>
				</xsl:if>
				
				<!--empty-->
				<xsl:if test="$type='empty'">
					<empty>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </empty>
				</xsl:if>
				
				<!--opaqueActivity-->
				<xsl:if test="$type='opaqueActivity'">
					<opaqueActivity>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </opaqueActivity>
				</xsl:if>
				
				<!--validate-->
				<xsl:if test="$type='validate'">
					<validate>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </validate>
				</xsl:if>
				
				<!--extensionActivity-->
				<xsl:if test="$type='extensionActivity'">
					<extensionActivity>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </extensionActivity>
				</xsl:if>
				
				<!--wait-->
				<xsl:if test="$type='wait'">
					<wait>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </wait>
				</xsl:if>
				
				<!--throw-->
				<xsl:if test="$type='throw'">
					<throw>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </throw>
				</xsl:if>
				
				<!--exit-->
				<xsl:if test="$type='exit'">
					<exit>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </exit>
				</xsl:if>
				
				<!--rethrow-->
				<xsl:if test="$type='rethrow'">
					<rethrow>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </rethrow>
				</xsl:if>
				
				<!--if-->
				<xsl:if test="$type='if'">
					<if>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$realID" /></xsl:with-param>
					    </xsl:call-template>
		            </if>
				</xsl:if>
				
				<!--if_branch-->
				<xsl:if test="$type='if_branch'">
					<xsl:if test="$childrenCounter=1">
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$realID" /></xsl:with-param>
					    </xsl:call-template>
		            </xsl:if>

					<xsl:if test="not($childrenCounter=1)">
						<elseif>
						    <xsl:call-template name="add-standard-attributes"/>
						    <xsl:call-template name="add-standard-elements"/>
							<xsl:call-template name="find-children-nodes">
								<xsl:with-param name="searchedParentID"><xsl:value-of select="$realID" /></xsl:with-param>
						    </xsl:call-template>
						</elseif>
		            </xsl:if>					
					
				</xsl:if>
				
				<!--else_branch-->
				<xsl:if test="$type='else_branch'">
					<else>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$realID" /></xsl:with-param>
					    </xsl:call-template>						
		            </else>
				</xsl:if>
				
				<!--flow-->
				<xsl:if test="$type='flow'">
					<flow>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </flow>
				</xsl:if>
				
				<!--pick-->
				<xsl:if test="$type='pick'">
					<pick>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </pick>
				</xsl:if>
				
				<!--onMessage-->
				<xsl:if test="$type='onMessage'">
					<onMessage>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </onMessage>
				</xsl:if>
				
				<!--sequence-->
				<xsl:if test="$type='sequence'">
					<sequence>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </sequence>
				</xsl:if>
				
				<!--while-->
				<xsl:if test="$type='while'">
					<while>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </while>
				</xsl:if>
				
				<!--repeatUntil-->
				<xsl:if test="$type='repeatUntil'">
					<repeatUntil>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </repeatUntil>
				</xsl:if>
				
				<!--forEach-->
				<xsl:if test="$type='forEach'">
					<forEach>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </forEach>
				</xsl:if>
				
				<!--compensate-->
				<xsl:if test="$type='compensate'">
					<compensate>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </compensate>
				</xsl:if>
				
				<!--compensateScope-->
				<xsl:if test="$type='compensateScope'">
					<compensateScope>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </compensateScope>
				</xsl:if>
				
				<!--scope-->
				<xsl:if test="$type='scope'">
					<scope>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </scope>
				</xsl:if>
				
				<!--onAlarm-->
				<xsl:if test="$type='onAlarm'">
					<onAlarm>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </onAlarm>
				</xsl:if>
				
				<!--onEvent-->
				<xsl:if test="$type='onEvent'">
					<onEvent>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </onEvent>
				</xsl:if>
				
				<!--compensationHandler-->
				<xsl:if test="$type='compensationHandler'">
					<compensationHandler>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </compensationHandler>
				</xsl:if>
				
				<!--terminationHandler-->
				<xsl:if test="$type='terminationHandler'">
					<terminationHandler>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </terminationHandler>
				</xsl:if>
				
				<!--catch-->
				<xsl:if test="$type='catch'">
					<catch>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-standard-elements"/>
		            </catch>
				</xsl:if>
				
				<!--catchAll-->
				<xsl:if test="$type='catchAll'">
					<catchAll>
		            </catchAll>
				</xsl:if>				
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="add-standard-attributes">
		<xsl:variable name="name" select="./oryx:name" />
		<xsl:if test="$name!=''">
			<xsl:attribute name="name">
				<xsl:value-of select="$name" />
			</xsl:attribute>
		</xsl:if>
		
		<xsl:variable name="suppressJoinFailure" select="./suppressJoinFailure" />
		<xsl:if test="$suppressJoinFailure!=''">
			<xsl:attribute name="suppressJoinFailure">
				<xsl:value-of select="$suppressJoinFailure" />
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="add-standard-elements">
	</xsl:template>
		
	
	<xsl:template name="get-id-string">
		<xsl:param name="id_" />
		<xsl:value-of select="substring-after($id_, '#oryx')" />
	</xsl:template>
	
	<xsl:template name="get-exact-type">
		<xsl:param name="typeString" />
		<xsl:value-of select="substring-after($typeString, 'bpel#')" />
	</xsl:template>

</xsl:stylesheet>