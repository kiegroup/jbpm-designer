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
			
		<xsl:if test="$type='BPEL'">
			<!-- BPEL -->
			<xsl:variable name="realID"><xsl:value-of select="@rdf:about" /></xsl:variable>
			<process>
                <xsl:call-template name="add-standard-attributes"/>
				
				<xsl:call-template name="add-documentation-construct"/>
				
				<xsl:variable name="targetNamespace" select="./oryx:targetnamespace" />
				<xsl:if test="$targetNamespace!=''">
					<xsl:attribute name="targetNamespace">
						<xsl:value-of select="$targetNamespace" />
					</xsl:attribute>
				</xsl:if>		
				
				<xsl:variable name="abstractProcessProfile" select="./oryx:abstractprocessprofile" />
				<xsl:if test="$abstractProcessProfile!=''">
					<xsl:attribute name="abstractProcessProfile">
						<xsl:value-of select="$abstractProcessProfile" />
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
				
				<!--xsl:variable name="xmlns" select="./oryx:xmlns" />
				<xsl:if test="$xmlns!=''">
					<xsl:attribute name="xmlns">
						<xsl:value-of select="$xmlns" />
					</xsl:attribute>
				</xsl:if-->
					
				<xsl:call-template name="add-otherxmlns-attribute"/>
				
				<xsl:call-template name="add-extension-declaration"/>		
				
				<xsl:call-template name="add-import-construct"/>
				
				<xsl:call-template name="add-variables-construct"/>	
				
				<xsl:call-template name="add-PartnerLinks-construct"/>	
				
				<xsl:call-template name="find-children-nodes">
					<xsl:with-param name="searchedParentID" select="$realID" />
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
						<xsl:with-param name="typeString" select="$typeString" />
					</xsl:call-template>
				</xsl:variable>
				
				<!--invoke-->
				<xsl:if test="$type='invoke'">
					<invoke>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>

		            </invoke>
				</xsl:if>
				
				<!--receive-->
				<xsl:if test="$type='receive'">
					<receive>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </receive>
				</xsl:if>
				
				<!--reply-->
				<xsl:if test="$type='reply'">
					<reply>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </reply>
				</xsl:if>
				
				<!--assign-->
				<xsl:if test="$type='assign'">
					<assign>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
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
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </copy>
				</xsl:if>
				
				<!--empty-->
				<xsl:if test="$type='empty'">
					<empty>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </empty>
				</xsl:if>
				
				<!--opaqueActivity-->
				<xsl:if test="$type='opaqueActivity'">
					<opaqueActivity>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </opaqueActivity>
				</xsl:if>
				
				<!--validate-->
				<xsl:if test="$type='validate'">
					<validate>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </validate>
				</xsl:if>
				
				<!--extensionActivity-->
				<xsl:if test="$type='extensionActivity'">
					<extensionActivity>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </extensionActivity>
				</xsl:if>
				
				<!--wait-->
				<xsl:if test="$type='wait'">
					<wait>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </wait>
				</xsl:if>
				
				<!--throw-->
				<xsl:if test="$type='throw'">
					<throw>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </throw>
				</xsl:if>
				
				<!--exit-->
				<xsl:if test="$type='exit'">
					<exit>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </exit>
				</xsl:if>
				
				<!--rethrow-->
				<xsl:if test="$type='rethrow'">
					<rethrow>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </rethrow>
				</xsl:if>
				
				<!--if-->
				<xsl:if test="$type='if'">
					<if>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$realID" /></xsl:with-param>
					    </xsl:call-template>
		            </if>
				</xsl:if>
				
				<!--if_branch-->
				<xsl:if test="$type='if_branch'">
					<xsl:if test="$childrenCounter=1">
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$realID" /></xsl:with-param>
					    </xsl:call-template>
		            </xsl:if>

					<xsl:if test="not($childrenCounter=1)">
						<elseif>
							<xsl:call-template name="find-children-nodes">
								<xsl:with-param name="searchedParentID"><xsl:value-of select="$realID" /></xsl:with-param>
						    </xsl:call-template>
						</elseif>
		            </xsl:if>					
					
				</xsl:if>
				
				<!--else_branch-->
				<xsl:if test="$type='else_branch'">
					<else>
						<xsl:call-template name="find-children-nodes">
							<xsl:with-param name="searchedParentID"><xsl:value-of select="$realID" /></xsl:with-param>
					    </xsl:call-template>						
		            </else>
				</xsl:if>
				
				<!--flow-->
				<xsl:if test="$type='flow'">
					<flow>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </flow>
				</xsl:if>
				
				<!--pick-->
				<xsl:if test="$type='pick'">
					<pick>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </pick>
				</xsl:if>
				
				<!--onMessage-->
				<xsl:if test="$type='onMessage'">
					<onMessage>
						<xsl:call-template name="add-documentation-construct"/>
		            </onMessage>
				</xsl:if>
				
				<!--sequence-->
				<xsl:if test="$type='sequence'">
					<sequence>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </sequence>
				</xsl:if>
				
				<!--while-->
				<xsl:if test="$type='while'">
					<while>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </while>
				</xsl:if>
				
				<!--repeatUntil-->
				<xsl:if test="$type='repeatUntil'">
					<repeatUntil>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </repeatUntil>
				</xsl:if>
				
				<!--forEach-->
				<xsl:if test="$type='forEach'">
					<forEach>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </forEach>
				</xsl:if>
				
				<!--compensate-->
				<xsl:if test="$type='compensate'">
					<compensate>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </compensate>
				</xsl:if>
				
				<!--compensateScope-->
				<xsl:if test="$type='compensateScope'">
					<compensateScope>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </compensateScope>
				</xsl:if>
				
				<!--scope-->
				<xsl:if test="$type='scope'">
					<scope>
						<xsl:call-template name="add-standard-attributes"/>
						<xsl:call-template name="add-documentation-construct"/>
						<xsl:call-template name="add-exitOnStandardFault-attribute"/>
						<xsl:call-template name="add-variables-construct"/>	
						<xsl:call-template name="add-PartnerLinks-construct"/>
						<xsl:call-template name="add-standard-elements"/>
		            </scope>
				</xsl:if>
				
				<!--onAlarm-->
				<xsl:if test="$type='onAlarm'">
					<onAlarm>
						<xsl:call-template name="add-documentation-construct"/>
		            </onAlarm>
				</xsl:if>
				
				<!--onEvent-->
				<xsl:if test="$type='onEvent'">
					<onEvent>
						<xsl:call-template name="add-documentation-construct"/>
		            </onEvent>
				</xsl:if>
				
				<!--compensationHandler-->
				<xsl:if test="$type='compensationHandler'">
					<compensationHandler>
						<xsl:call-template name="add-documentation-construct"/>
		            </compensationHandler>
				</xsl:if>
				
				<!--terminationHandler-->
				<xsl:if test="$type='terminationHandler'">
					<terminationHandler>
						<xsl:call-template name="add-documentation-construct"/>
		            </terminationHandler>
				</xsl:if>
				
				<!--catch-->
				<xsl:if test="$type='catch'">
					<catch>
						<xsl:call-template name="add-documentation-construct"/>
		            </catch>
				</xsl:if>
				
				<!--catchAll-->
				<xsl:if test="$type='catchAll'">
					<catchAll>
						<xsl:call-template name="add-documentation-construct"/>
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
		
		<xsl:variable name="suppressJoinFailure" select="./oryx:suppressjoinfailure" />
		<xsl:if test="$suppressJoinFailure!=''">
			<xsl:attribute name="suppressJoinFailure">
				<xsl:value-of select="$suppressJoinFailure" />
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
					<xsl:if test="$JC_opaque = 'true'">
						<xsl:attribute name="opaque">true</xsl:attribute>
					</xsl:if>
	    			<xsl:value-of select="$JC_boolExp"/>
				</joinCondition>
			</targets>
		</xsl:if>					   		
	</xsl:template>
	
	<xsl:template name="add-documentation-construct">
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
	
	<xsl:template name="add-import-construct">
		<xsl:variable name="import" select="./oryx:import" />
		<xsl:if test="$import!=''">
			
			<xsl:variable name="count">
				<xsl:call-template name="get-number-of-elements-in-complex-type">
					<xsl:with-param name="original_content" select="$import" />
				</xsl:call-template>
			</xsl:variable>	
			
			<xsl:call-template name="loop-for-adding-import-construct">
				<xsl:with-param name="i">1</xsl:with-param>
				<xsl:with-param name="count" select="$count" />
				<xsl:with-param name="data-set" select="$import" />
			</xsl:call-template>
				
		</xsl:if>
	</xsl:template>
		
	<xsl:template name="loop-for-adding-import-construct">
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
			
  			<xsl:call-template name="loop-for-adding-import-construct">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
   				<xsl:with-param name="data-set" select="substring-after($data-set,'%22%7D%2C%20%7B')"/>
  			</xsl:call-template>
 		</xsl:if>
    </xsl:template>
	
	<xsl:template name="add-variables-construct">
		<xsl:variable name="variables" select="./oryx:variables" />
		<xsl:if test="$variables!=''">
			<variables>
				<xsl:variable name="count">
					<xsl:call-template name="get-number-of-elements-in-complex-type">
						<xsl:with-param name="original_content" select="$variables" />
					</xsl:call-template>
				</xsl:variable>	
				
				<xsl:call-template name="loop-for-adding-variables-construct">
					<xsl:with-param name="i">1</xsl:with-param>
					<xsl:with-param name="count" select="$count" />
					<xsl:with-param name="data-set" select="$variables" />
				</xsl:call-template>
			</variables>	
		</xsl:if>
	</xsl:template>
		
	<xsl:template name="loop-for-adding-variables-construct">
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
			
  			<xsl:call-template name="loop-for-adding-variables-construct">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
   				<xsl:with-param name="data-set" select="substring-after($data-set,'%22%7D%2C%20%7B')"/>
  			</xsl:call-template>
 		</xsl:if>
    </xsl:template>
	
	<xsl:template name="add-PartnerLinks-construct">
		<xsl:variable name="partnerLinks" select="./oryx:partnerlinks" />
		<xsl:if test="$partnerLinks!=''">
			<partnerLinks>
				<xsl:variable name="count">
					<xsl:call-template name="get-number-of-elements-in-complex-type">
						<xsl:with-param name="original_content" select="$partnerLinks" />
					</xsl:call-template>
				</xsl:variable>	
				
				<xsl:call-template name="loop-for-adding-PartnerLinks-construct">
					<xsl:with-param name="i">1</xsl:with-param>
					<xsl:with-param name="count" select="$count" />
					<xsl:with-param name="data-set" select="$partnerLinks" />
				</xsl:call-template>
			</partnerLinks>	
		</xsl:if>
	</xsl:template>
		
	<xsl:template name="loop-for-adding-PartnerLinks-construct">
		<xsl:param name="i"/>
 		<xsl:param name="count"/>
		<xsl:param name="data-set"/>
			
 		<xsl:if test="$i &lt;= $count">
 			<xsl:variable name="name" select="substring-before(substring-after($data-set, 'name%3A%22'), '%22%2C%20PartnerLinkType') " />
 			<xsl:variable name="partnerLinkType" select="substring-before(substring-after($data-set, 'PartnerLinkType%3A%22'), '%22%2C%20myRole') " />
 			<xsl:variable name="myRole" select="substring-before(substring-after($data-set, 'myRole%3A%22'), '%22%2C%20partnerRole') " />
 			<xsl:variable name="partnerRole" select="substring-before(substring-after($data-set, 'partnerRole%3A%22'), '%22%2C%20mustUnderstand') " />
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
			
  			<xsl:call-template name="loop-for-adding-PartnerLinks-construct">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
   				<xsl:with-param name="data-set" select="substring-after($data-set,'%22%7D%2C%20%7B')"/>
  			</xsl:call-template>
 		</xsl:if>
    </xsl:template>
	
	<xsl:template name="get-id-string">
		<xsl:param name="id_" />
		<xsl:value-of select="substring-after($id_, '#oryx')" />
	</xsl:template>
	
	<xsl:template name="get-exact-type">
		<xsl:param name="typeString" />
		<xsl:value-of select="substring-after($typeString, 'bpel#')" />
	</xsl:template>
	
	<xsl:template name="get-number-of-elements-in-complex-type">
		<xsl:param name="original_content" />
		<xsl:value-of select="substring-before(substring-after($original_content, 'totalCount%27%3A'), '%2C%20%27items') " />
	</xsl:template>	
			
</xsl:stylesheet>