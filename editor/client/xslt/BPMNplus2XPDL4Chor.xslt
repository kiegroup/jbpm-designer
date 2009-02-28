<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:chor="http://iaas.uni-stuttgart.de/schemas/XPDL4chor"
	xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha"
	xmlns:xh="http://www.w3.org/1999/xhtml" 
	exclude-result-prefixes="xh">
	
	<!-- ********* Template for splitting whitespace separated String lists for elements without attributes ********** -->
	<xsl:template name="string-tokens">
		<xsl:param name="list" />
		<xsl:param name="elementName" />
		<xsl:variable name="newlist" select="concat(normalize-space($list), ' ')" />
		<xsl:variable name="first" select="substring-before($newlist, ' ')" />
		<xsl:variable name="remaining" select="substring-after($newlist, ' ')" />		
		<xsl:if test="string-length(normalize-space($first))>0">
			<xsl:element name="{$elementName}"><xsl:value-of select="$first" /></xsl:element>
		</xsl:if>
		<xsl:if test="$remaining">
			<xsl:call-template name="string-tokens">
				<xsl:with-param name="list" select="$remaining" />
				<xsl:with-param name="elementName" select="$elementName"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- ********* Template for splitting whitespace separated String lists for categories  ********** -->
	<xsl:template name="category-tokens">
		<xsl:param name="list" />
		<xsl:variable name="newlist" select="concat(normalize-space($list), ' ')" />
		<xsl:variable name="first" select="substring-before($newlist, ' ')" />
		<xsl:variable name="remaining" select="substring-after($newlist, ' ')" />		
		<xsl:if test="string-length(normalize-space($first))>0">
			<xpdl:Category Id="{concat($first, '_id')}" Name="{$first}"/>
		</xsl:if>
		<xsl:if test="$remaining">
			<xsl:call-template name="category-tokens">
				<xsl:with-param name="list" select="$remaining" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>	
	
	<!-- ********* Template for splitting whitespace separeted String lists of correlation properties ********** -->
	<xsl:template name="properties-tokens">
		<xsl:param name="list" />
		<xsl:variable name="newlist" select="concat(normalize-space($list), ' ')" />
		<xsl:variable name="first" select="substring-before($newlist, ' ')" />
		<xsl:variable name="remaining" select="substring-after($newlist, ' ')" />		
		<xsl:if test="string-length(normalize-space($first))>0">
			<element name="{$first}"/>
		</xsl:if>
		<xsl:if test="$remaining">
			<xsl:call-template name="properties-tokens">
				<xsl:with-param name="list" select="$remaining" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- ********* Template for splitting complex property values of Imports ********** -->
	<xsl:template name="import-tokens">
		<xsl:param name="list" />
		<xsl:variable name="first" select="substring-after(substring-before($list, '}'),'{')" />		
		<xsl:variable name="remaining" select="substring-after($list, '}')" />
		
		<xsl:variable name="namespace" select="substring-before(substring-after($first, 'namespace:&quot;'),'&quot;')" />
		<xsl:variable name="location" select="substring-before(substring-after($first, 'location:&quot;'),'&quot;')" />
		<xsl:variable name="type" select="substring-before(substring-after($first, 'importType:&quot;'),'&quot;')" />
		<xsl:variable name="prefix" select="substring-before(substring-after($first, 'prefix:&quot;'),'&quot;')" />
		<chor:Import>
			<xsl:if test="$namespace">
				<xsl:attribute name="Namespace">
					<xsl:value-of select="$namespace" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$location">
				<xsl:attribute name="Location">
					<xsl:value-of select="$location" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$type">
				<xsl:attribute name="ImportType">
					<xsl:value-of select="$type" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$prefix">
				<xsl:attribute name="Prefix">
					<xsl:value-of select="$prefix" />
				</xsl:attribute>
			</xsl:if>
		</chor:Import>
		<xsl:if test="$remaining">
			<xsl:call-template name="import-tokens">
				<xsl:with-param name="list" select="$remaining" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="imports-tokens">
		<xsl:param name="list" />
		<xsl:variable name="newlist" select="substring-before(substring-after(normalize-space($list), '['),']')" />
		<xsl:if test="$newlist">
			<chor:Imports>
				<xsl:call-template name="import-tokens">
					<xsl:with-param name="list" select="$newlist" />
				</xsl:call-template>
			</chor:Imports>
		</xsl:if>
	</xsl:template>
	
	<!-- ********* Template for createing Data Fields for correlation sets  ********** -->
	<xsl:template name="correlationSet-tokens">
		<xsl:param name="correlationSet" />
		<xsl:variable name="first" select="substring-after(substring-before($correlationSet, '}'),'{')" />		
		<xsl:variable name="remaining" select="substring-after($correlationSet, '}')" />
		
		<xsl:variable name="name" select="substring-before(substring-after($first, 'name:&quot;'),'&quot;')" />
		<xsl:variable name="properties" select="substring-before(substring-after($first, 'properties:&quot;'),'&quot;')" />
		<xpdl:DataField Id="{concat('id_',$name)}" Name="{$name}" Correlation="true">
			<xpdl:DataType>
				<xpdl:SchemaType>
					<schema xmlns="http://www.w3c.org/2001/XMLSchema">
						<xsl:call-template name="properties-tokens">
							<xsl:with-param name="list" select="$properties" />
						</xsl:call-template>			
					</schema>
				</xpdl:SchemaType>
			</xpdl:DataType>
		</xpdl:DataField>
		<xsl:if test="$remaining">
			<xsl:call-template name="correlationSet-tokens">
				<xsl:with-param name="correlationSet" select="$remaining" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="correlationSets-tokens">
		<xsl:param name="correlationSets" />
		<xsl:variable name="newlist" select="substring-before(substring-after(normalize-space($correlationSets), '['),']')" />
		<xsl:if test="$newlist">
			<xpdl:DataFields>				
				<xsl:call-template name="correlationSet-tokens">
					<xsl:with-param name="correlationSet" select="$newlist" />
				</xsl:call-template>
			</xpdl:DataFields>
		</xsl:if>
	</xsl:template>
	
	<!-- ********* Template for splitting complex property values of FromSpec ********** -->
	<xsl:template name="from-spec-tokens">
		<xsl:param name="list" />
		<xsl:variable name="first" select="substring-after(substring-before($list, '}'),'{')" />		
		<xsl:variable name="remaining" select="substring-after($list, '}')" />

		<xsl:variable name="type" select="substring-before(substring-after($first, 'fromspectype:&quot;'),'&quot;')" />
		<xsl:variable name="variableName" select="substring-before(substring-after($first, 'variablename:&quot;'),'&quot;')" />
		<xsl:variable name="part" select="substring-before(substring-after($first, 'part:&quot;'),'&quot;')" />
		<xsl:variable name="queryLanguage" select="substring-before(substring-after($first, 'querylanguage:&quot;'),'&quot;')" />
		<xsl:variable name="query" select="substring-before(substring-after($first, 'query:&quot;'),'&quot;')" />
		<xsl:variable name="property" select="substring-before(substring-after($first, 'property:&quot;'),'&quot;')" />
		<xsl:variable name="expressionLanguage" select="substring-before(substring-after($first, 'expressionlanguage:&quot;'),'&quot;')" />
		<xsl:variable name="expression" select="substring-before(substring-after($first, 'expression:&quot;'),'&quot;')" />
		<xsl:variable name="literal" select="substring-before(substring-after($first, 'literal:&quot;'),'&quot;')" />
		<chor:FromSpec>
			<xsl:attribute name="Type">
				<xsl:value-of select="$type" />
			</xsl:attribute>
			<xsl:if test="$variableName">
				<xsl:attribute name="Location">
					<xsl:value-of select="$variableName" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$part">
				<xsl:attribute name="Part">
					<xsl:value-of select="$part" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$property">
				<xsl:attribute name="Property">
					<xsl:value-of select="$property" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$type='Literal'">
				<chor:Literal><xsl:value-of select="$literal" /></chor:Literal>
			</xsl:if>
			<xsl:if test="$type='Expression'">
				<chor:Expression>
					<xsl:if test="$expressionLanguage">
						<xsl:attribute name="ScriptGrammar">
							<xsl:value-of select="$expressionLanguage" />
						</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="$expression" />
				</chor:Expression>
			</xsl:if>
			<xsl:if test="$type='Query'">
				<chor:Query>
					<xsl:if test="$queryLanguage">
						<xsl:attribute name="QueryLanguage">
							<xsl:value-of select="$queryLanguage" />
						</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="$query" />
				</chor:Query>
			</xsl:if>
		</chor:FromSpec>
		<xsl:if test="$remaining">
			<xsl:call-template name="from-spec-tokens">
				<xsl:with-param name="list" select="$remaining" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="from-specs-tokens">
		<xsl:param name="list" />
		<xsl:variable name="newlist" select="substring-before(substring-after(normalize-space($list), '['),']')" />
		<xsl:if test="$newlist">
			<xsl:call-template name="from-spec-tokens">
				<xsl:with-param name="list" select="$newlist" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!-- ********* Template for splitting complex property values of Correlations ********** -->
	<xsl:template name="correlation-tokens">
		<xsl:param name="list" />
		<xsl:variable name="first" select="substring-after(substring-before($list, '}'),'{')" />		
		<xsl:variable name="remaining" select="substring-after($list, '}')" />
		
		<xsl:variable name="set" select="substring-before(substring-after($first, 'set:&quot;'),'&quot;')" />
		<xsl:variable name="initiate" select="substring-before(substring-after($first, 'initiate:&quot;'),'&quot;')" />
		<xsl:variable name="pattern" select="substring-before(substring-after($first, 'pattern:&quot;'),'&quot;')" />
		<chor:Correlation>
			<xsl:if test="$set">
				<xsl:attribute name="Set">
					<xsl:value-of select="$set" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$initiate">
				<xsl:attribute name="Initiate">
					<xsl:value-of select="$initiate" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$pattern">
				<xsl:attribute name="Pattern">
					<xsl:value-of select="$pattern" />
				</xsl:attribute>
			</xsl:if>
		</chor:Correlation>
		<xsl:if test="$remaining">
			<xsl:call-template name="correlation-tokens">
				<xsl:with-param name="list" select="$remaining" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="correlations-tokens">
		<xsl:param name="list" />
		<xsl:variable name="newlist" select="substring-before(substring-after(normalize-space($list), '['),']')" />
		<xsl:if test="$newlist">
			<chor:Correlations>
				<xsl:call-template name="correlation-tokens">
					<xsl:with-param name="list" select="$newlist" />
				</xsl:call-template>
			</chor:Correlations>
		</xsl:if>
	</xsl:template>
	
	<!-- ********* Template for splitting complex property values of ToParts ********** -->
	<xsl:template name="toPart-tokens">
		<xsl:param name="list" />
		<xsl:variable name="first" select="substring-after(substring-before($list, '}'),'{')" />		
		<xsl:variable name="remaining" select="substring-after($list, '}')" />
		
		<xsl:variable name="fromVariable" select="substring-before(substring-after($first, 'fromvariable:&quot;'),'&quot;')" />
		<xsl:variable name="part" select="substring-before(substring-after($first, 'part:&quot;'),'&quot;')" />
		<chor:ToPart>
			<xsl:if test="$fromVariable">
				<xsl:attribute name="FromVariable">
					<xsl:value-of select="$fromVariable" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$part">
				<xsl:attribute name="Part">
					<xsl:value-of select="$part" />
				</xsl:attribute>
			</xsl:if>
		</chor:ToPart>
		<xsl:if test="$remaining">
			<xsl:call-template name="toPart-tokens">
				<xsl:with-param name="list" select="$remaining" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="toParts-tokens">
		<xsl:param name="list" />
		<xsl:variable name="newlist" select="substring-before(substring-after(normalize-space($list), '['),']')" />
		<xsl:if test="$newlist">
			<chor:ToParts>
				<xsl:call-template name="toPart-tokens">
					<xsl:with-param name="list" select="$newlist" />
				</xsl:call-template>
			</chor:ToParts>
		</xsl:if>
	</xsl:template>
	
	<!-- ********* Template for splitting complex property values of fromParts ********** -->
	<xsl:template name="fromPart-tokens">
		<xsl:param name="list" />
		<xsl:variable name="first" select="substring-after(substring-before($list, '}'),'{')" />		
		<xsl:variable name="remaining" select="substring-after($list, '}')" />
		
		<xsl:variable name="toVariable" select="substring-before(substring-after($first, 'tovariable:&quot;'),'&quot;')" />
		<xsl:variable name="part" select="substring-before(substring-after($first, 'part:&quot;'),'&quot;')" />
		<chor:FromPart>
			<xsl:if test="$toVariable">
				<xsl:attribute name="ToVariable">
					<xsl:value-of select="$toVariable" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$part">
				<xsl:attribute name="Part">
					<xsl:value-of select="$part" />
				</xsl:attribute>
			</xsl:if>
		</chor:FromPart>
		<xsl:if test="$remaining">
			<xsl:call-template name="fromPart-tokens">
				<xsl:with-param name="list" select="$remaining" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="fromParts-tokens">
		<xsl:param name="list" />
		<xsl:variable name="newlist" select="substring-before(substring-after(normalize-space($list), '['),']')" />
		<xsl:if test="$newlist">
			<chor:FromParts>
				<xsl:call-template name="fromPart-tokens">
					<xsl:with-param name="list" select="$newlist" />
				</xsl:call-template>
			</chor:FromParts>
		</xsl:if>
	</xsl:template>
	
	<!-- ********* Template for splitting complex property values of Copy ********** -->
	<xsl:template name="copy-tokens">
		<xsl:param name="list" />
		<xsl:variable name="first" select="substring-after(substring-before($list, '}'),'{')" />		
		<xsl:variable name="remaining" select="substring-after($list, '}')" />
		
		<xsl:variable name="keepSrcElementName" select="substring-before(substring-after($first, 'keepsrcelementname:'),',')" />
		<xsl:variable name="ignoreMissingFromData" select="substring-before(substring-after($first, 'ignoremissingfromdata:'),',')" />
		<xsl:variable name="fromSpecType" select="substring-before(substring-after($first, 'fromspectype:&quot;'),'&quot;')" />
		<xsl:variable name="fromSpecVariableName" select="substring-before(substring-after($first, 'fromspecvariablename:&quot;'),'&quot;')" />
		<xsl:variable name="fromSpecPart" select="substring-before(substring-after($first, 'fromspecpart:&quot;'),'&quot;')" />
		<xsl:variable name="fromSpecQueryLanguage" select="substring-before(substring-after($first, 'fromspecquerylanguage:&quot;'),'&quot;')" />
		<xsl:variable name="fromSpecQuery" select="substring-before(substring-after($first, 'fromspecquery:&quot;'),'&quot;')" />
		<xsl:variable name="fromSpecProperty" select="substring-before(substring-after($first, 'fromspecproperty:&quot;'),'&quot;')" />
		<xsl:variable name="fromSpecExpressionLanguage" select="substring-before(substring-after($first, 'fromspecexpressionlanguage:&quot;'),'&quot;')" />
		<xsl:variable name="fromSpecExpression" select="substring-before(substring-after($first, 'fromspecexpression:&quot;'),'&quot;')" />
		<xsl:variable name="fromSpecLiteral" select="substring-before(substring-after($first, 'fromspecliteral:&quot;'),'&quot;')" />
		<xsl:variable name="toSpecType" select="substring-before(substring-after($first, 'tospectype:&quot;'),'&quot;')" />
		<xsl:variable name="toSpecVariableName" select="substring-before(substring-after($first, 'tospecvariablename:&quot;'),'&quot;')" />
		<xsl:variable name="toSpecPart" select="substring-before(substring-after($first, 'tospecpart:&quot;'),'&quot;')" />
		<xsl:variable name="toSpecQueryLanguage" select="substring-before(substring-after($first, 'tospecquerylanguage:&quot;'),'&quot;')" />
		<xsl:variable name="toSpecQuery" select="substring-before(substring-after($first, 'tospecquery:&quot;'),'&quot;')" />
		<xsl:variable name="toSpecProperty" select="substring-before(substring-after($first, 'tospecproperty:&quot;'),'&quot;')" />
		<xsl:variable name="toSpecExpressionLanguage" select="substring-before(substring-after($first, 'tospecexpressionlanguage:&quot;'),'&quot;')" />
		<xsl:variable name="toSpecExpression" select="substring-before(substring-after($first, 'tospecexpression:&quot;'),'&quot;')" />
		
		<Copy>
			<xsl:attribute name="KeepSrcElementName">
				<xsl:value-of select="$keepSrcElementName" />
			</xsl:attribute>
			<xsl:attribute name="IgnoreMissingFromData">
				<xsl:value-of select="$ignoreMissingFromData" />
			</xsl:attribute>
			<FromSpec
				Type="{$fromSpecType}">
				<xsl:if test="$fromSpecVariableName">
					<xsl:attribute name="VariableName">
						<xsl:value-of select="$fromSpecVariableName" />
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$fromSpecPart">
					<xsl:attribute name="Part">
						<xsl:value-of select="$fromSpecPart" />
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$fromSpecProperty">
					<xsl:attribute name="Property">
						<xsl:value-of select="$fromSpecProperty" />
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$fromSpecType='Literal'">
					<Literal><xsl:value-of select="$fromSpecLiteral" /></Literal>
				</xsl:if>
				<xsl:if test="$fromSpecType='Expression'">
					<Expression>
						<xsl:if test="$fromSpecExpressionLanguage">
							<xsl:attribute name="ScriptGrammar">
								<xsl:value-of select="$fromSpecExpressionLanguage" />
							</xsl:attribute>
						</xsl:if>
						<xsl:value-of select="$fromSpecExpression" />
					</Expression>
				</xsl:if>
				<xsl:if test="$fromSpecType='Variable'">
					<xsl:if test="$fromSpecQuery">
						<Query>
							<xsl:if test="$fromSpecQueryLanguage">
								<xsl:attribute name="QueryLanguage">
									<xsl:value-of select="$fromSpecQueryLanguage" />
								</xsl:attribute>
							</xsl:if>
							<xsl:value-of select="$fromSpecQuery" />
						</Query>
					</xsl:if>
				</xsl:if>
			</FromSpec>
			<ToSpec
				Type="{$toSpecType}">
				<xsl:if test="$toSpecVariableName">
					<xsl:attribute name="VariableName">
						<xsl:value-of select="$toSpecVariableName" />
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$toSpecPart">
					<xsl:attribute name="Part">
						<xsl:value-of select="$toSpecPart" />
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$toSpecProperty">
					<xsl:attribute name="Property">
						<xsl:value-of select="$toSpecProperty" />
					</xsl:attribute>
				</xsl:if>
				<xsl:if test="$toSpecType='Expression'">
					<Expression>
						<xsl:if test="$toSpecExpressionLanguage">
							<xsl:attribute name="ScriptGrammar">
								<xsl:value-of select="$toSpecExpressionLanguage" />
							</xsl:attribute>
						</xsl:if>
						<xsl:value-of select="$toSpecExpression" />
					</Expression>
				</xsl:if>
				<xsl:if test="$toSpecType='Variable'">
					<xsl:if test="$toSpecQuery">
						<Query>
							<xsl:if test="$toSpecQueryLanguage">
								<xsl:attribute name="QueryLanguage">
									<xsl:value-of select="$toSpecQueryLanguage" />
								</xsl:attribute>
							</xsl:if>
							<xsl:value-of select="$toSpecQuery" />
						</Query>
					</xsl:if>
				</xsl:if>
			</ToSpec>
		</Copy>
		<xsl:if test="$remaining">
			<xsl:call-template name="copy-tokens">
				<xsl:with-param name="list" select="$remaining" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template name="copies-tokens">
		<xsl:param name="list" />
		<xsl:variable name="newlist" select="substring-before(substring-after(normalize-space($list), '['),']')" />
		<xsl:if test="$newlist">
			<xsl:call-template name="copy-tokens">
				<xsl:with-param name="list" select="$newlist" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<!--  **************************** Template for Object element ****************** -->
	<xsl:template name="object">
		<xsl:param name="objectParent" />
		<xpdl:Object
			Id="{$objectParent/@id}">
			<xsl:variable name="categories" select="$objectParent/xh:span[@class='oryx-categories']" />
			<xsl:variable name="documentation" select="$objectParent/xh:span[@class='oryx-documentation']" />
			<!-- Categories -->
			<xsl:if test="string-length(normalize-space($categories))>0">
				<xpdl:Categories>
					<xsl:call-template name="category-tokens">
						<xsl:with-param name="list" select="$categories" />
					</xsl:call-template>
				</xpdl:Categories>
			</xsl:if>
			<!-- Documentation -->
			<xsl:if test="string-length(normalize-space($documentation))>0">
				<xpdl:Documentation>
					<xsl:value-of select="$documentation" />
				</xpdl:Documentation>
			</xsl:if>
		</xpdl:Object>
	</xsl:template>
	
	<!--  **************************** Template for NodeGraphicsInfo element ****************** -->
	<xsl:template name="nodeGraphicsInfos">
		<xsl:param name="node" />
		<xsl:variable name="bounds" select="$node/xh:span[@class='oryx-bounds']" />
		<xsl:variable name="x" select="substring-before($bounds, ',')" />
		<xsl:variable name="remaining1" select="substring-after($bounds, ',')" />
		<xsl:variable name="y" select="substring-before($remaining1, ',')" />
		<xsl:variable name="remaining2" select="substring-after($remaining1, ',')" />
		<xsl:variable name="width" select="substring-before($remaining2, ',')" />
		<xsl:variable name="height" select="substring-after($remaining2, ',')" />
		<xpdl:NodeGraphicsInfos>
			<xpdl:NodeGraphicsInfo Width="{$width - $x}" Height="{$height - $y}">
				<xpdl:Coordinates XCoordinate="{$x}" YCoordinate="{$y}" />
			</xpdl:NodeGraphicsInfo>
		</xpdl:NodeGraphicsInfos>
	</xsl:template>
	
	<!--  **************************** Template for timer trigger ****************** -->
	<xsl:template name="triggerTimer">
		<xsl:variable name="timeType" select="xh:span[@class='oryx-timetype']" />
		<xsl:variable name="timeExpression" select="xh:span[@class='oryx-timeexpression']" />
		<xsl:variable name="timeExpressionLanguage" select="xh:span[@class='oryx-timeexpressionlanguage']" />
		<xsl:variable name="repeatEvery" select="xh:span[@class='oryx-repeatevery']" />
		<xsl:variable name="repeatEveryLanguage" select="xh:span[@class='oryx-repeateverylanguage']" />
		
		<xpdl:TriggerTimer>
			<xsl:if test="$timeType='Cycle'">
				<xsl:attribute name="TimeCycle">
					<xsl:value-of select="$timeExpression" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="$timeType='Date'">
				<xsl:attribute name="TimeDate">
					<xsl:value-of select="$timeExpression" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length(normalize-space($timeExpressionLanguage))>0">
				<xsl:attribute name="chor:TimeLanguage">
					<xsl:value-of select="$timeExpressionLanguage" />
				</xsl:attribute>
			</xsl:if>
			<xsl:if test="string-length(normalize-space($repeatEvery))>0">
				<chor:TimeDeadline>
					<xsl:if test="string-length(normalize-space($repeatEveryLanguage))>0">
						<xsl:attribute name="ScriptGrammar">
							<xsl:value-of select="$repeatEveryLanguage" />
						</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="$repeatEvery" />
				</chor:TimeDeadline>
			</xsl:if>
		</xpdl:TriggerTimer>
	</xsl:template>
	
	<!-- ********************** Template for trigger-result message **************-->
	<xsl:template name="triggerResultMessage">
		<xsl:param name="id" />
		<xpdl:TriggerResultMessage
			chor:OpaqueOutput="{xh:span[@class='oryx-opaqueoutput']}">
			<xsl:variable name="messageExchange" select="xh:span[@class='oryx-messageexchange']" />
			<xsl:if test="string-length(normalize-space($messageExchange))>0">
				<xsl:attribute name="chor:MessageExchange">
					<xsl:value-of select="$messageExchange" />
				</xsl:attribute>
			</xsl:if>
			<xpdl:Message Id="{concat($id,'_message')}"/>
			<xsl:call-template name="correlations-tokens">
				<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-correlations']" /></xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="fromParts-tokens">
				<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-fromparts']" /></xsl:with-param>
			</xsl:call-template>
		</xpdl:TriggerResultMessage>
	</xsl:template>
	
	<!-- ************** Template for generating Activities content ******************** -->
	<xsl:template name="activities">
		<xsl:param name="activities" />
		<xsl:for-each select="$activities">
			<xsl:variable name="type" select="xh:span[@class='oryx-activitytype']" />
			<xsl:variable name="gatewayType" select="xh:span[@class='oryx-gatewaytype']" />
			<xsl:variable name="eventType" select="xh:span[@class='oryx-eventtype']" />
			<xsl:variable name="loopType" select="xh:span[@class='oryx-looptype']" />
			<xsl:variable name="id" select="@id" />
			<xsl:variable name="name" select="xh:span[@class='oryx-name']" />					
			<xsl:variable name="suppressJoinFailure" select="xh:span[@class='oryx-suppressjoinfailure']" />								
			<xsl:if test="string-length(normalize-space($type))>0 or 
				string-length(normalize-space($gatewayType))>0 or 
				string-length(normalize-space($eventType))>0">
				<xpdl:Activity 
					Id="{$id}">
					<xsl:if test="string-length(normalize-space($name))>0">
						<xsl:attribute name="Name">
							<xsl:value-of select="$name" />
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="string-length(normalize-space($suppressJoinFailure))>0">
						<xsl:attribute name="chor:SuppressJoinFailure">
							<xsl:value-of select="$suppressJoinFailure" />
						</xsl:attribute>
					</xsl:if>
			<!-- ******************** Route ********************** -->
					<xsl:if test="string-length(normalize-space($gatewayType))>0">
						<xsl:variable name="instantiate" select="xh:span[@class='oryx-instantiate']" />
						<xpdl:Route GatewayType="{$gatewayType}">
							<xsl:if test="$instantiate">
								<xsl:attribute name="Instantiate">
									<xsl:value-of select="$instantiate" />
								</xsl:attribute>
							</xsl:if>
						</xpdl:Route>
						<xsl:if test="$gatewayType='XOR'">
							<xpdl:TransitionRestrictions>
								<xpdl:TransitionRestriction>
									<xsl:if test="xh:span[@class='oryx-xortype']='Event'">
										<xpdl:Split Type="XOREVENT" />
									</xsl:if>
									<xsl:if test="xh:span[@class='oryx-xortype']='Data'">
										<xpdl:Split Type="XOR" />
										<!-- TODO: transition Refs depending on defined transition order? -->
									</xsl:if>
								</xpdl:TransitionRestriction>
							</xpdl:TransitionRestrictions>
						</xsl:if>
					</xsl:if>
			<!-- ******************** XPDL Tasks ********************** -->
					<xsl:if test="$type='Task'">
						<xsl:variable name="taskType" select="xh:span[@class='oryx-tasktype']" />
						<xsl:if test="$taskType='Service'">
							<xpdl:Implementation>
								<xpdl:Task>
									<xpdl:TaskService 
										chor:OpaqueInput="{xh:span[@class='oryx-opaqueinput']}" 
										chor:OpaqueOutput="{xh:span[@class='oryx-opaqueoutput']}">
										<xpdl:MessageIn Id="{concat($id,'_messagein')}"/>
										<xpdl:MessageOut Id="{concat($id,'_messageout')}"/>
										
										<xsl:call-template name="correlations-tokens">
											<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-correlations']" /></xsl:with-param>
										</xsl:call-template>
										<xsl:call-template name="toParts-tokens">
											<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-toparts']" /></xsl:with-param>
										</xsl:call-template>
										<xsl:call-template name="fromParts-tokens">
											<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-fromparts']" /></xsl:with-param>
										</xsl:call-template>
									</xpdl:TaskService>
								</xpdl:Task>
							</xpdl:Implementation>
						</xsl:if>
						<xsl:if test="$taskType='Receive'">
							<xpdl:Implementation>
								<xpdl:Task>
									<xsl:variable name="messageExchange" select="xh:span[@class='oryx-messageexchange']" />
									<xpdl:TaskReceive
										Instantiate="{xh:span[@class='oryx-instantiate']}"
										chor:OpaqueOutput="{xh:span[@class='oryx-opaqueoutput']}">
										<xpdl:Message Id="{concat($id,'_message')}"/>
										<xsl:if test="string-length(normalize-space($messageExchange))>0">
											<xsl:attribute name="chor:MessageExchange">
												<xsl:value-of select="$messageExchange" />
											</xsl:attribute>
										</xsl:if>
										<xsl:call-template name="correlations-tokens">
											<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-correlations']" /></xsl:with-param>
										</xsl:call-template>
										<xsl:call-template name="fromParts-tokens">
											<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-fromparts']" /></xsl:with-param>
										</xsl:call-template>
									</xpdl:TaskReceive>
								</xpdl:Task>
							</xpdl:Implementation>
						</xsl:if>
						<xsl:if test="$taskType='Send'">
							<xpdl:Implementation>
								<xpdl:Task>
									<xsl:variable name="messageExchange" select="xh:span[@class='oryx-messageexchange']" />
									<xsl:variable name="faultName" select="xh:span[@class='oryx-faultname']" />
									<xpdl:TaskSend
										chor:OpaqueInput="{xh:span[@class='oryx-opaqueinput']}">
										<xsl:if test="string-length(normalize-space($messageExchange))>0">
											<xsl:attribute name="chor:MessageExchange">
												<xsl:value-of select="$messageExchange" />
											</xsl:attribute>
										</xsl:if>
										<xsl:if test="string-length(normalize-space($faultName))>0">
											<xsl:attribute name="chor:FaultName">
												<xsl:value-of select="$faultName" />
											</xsl:attribute>
										</xsl:if>
										<xpdl:Message Id="{concat($id,'_message')}"/>
										<xsl:call-template name="correlations-tokens">
											<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-correlations']" /></xsl:with-param>
										</xsl:call-template>
										<xsl:call-template name="toParts-tokens">
											<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-toparts']" /></xsl:with-param>
										</xsl:call-template>
									</xpdl:TaskSend>
								</xpdl:Task>
							</xpdl:Implementation>
						</xsl:if>
					</xsl:if>
			<!-- ************* BlockActivitiy ****************** -->
					<xsl:if test="$type='SubProcess'">
						<xsl:variable name="isolated" select="xh:span[@class='oryx-isolated']" />
						<xsl:variable name="exitOnStandardFault" select="xh:span[@class='oryx-exitonstandardfault']" />
						<xsl:variable name="index" select="generate-id(.)" />
						<xpdl:BlockActivity 
							ActivitySetId="{concat(concat($id,'_activitySet_'),$index)}">
							<xsl:if test="string-length(normalize-space($isolated))>0">
								<xsl:attribute name="chor:Isolated">
									<xsl:value-of select="$isolated"/>
								</xsl:attribute>
							</xsl:if>
							<xsl:if test="string-length(normalize-space($exitOnStandardFault))>0">
								<xsl:attribute name="chor:ExitOnStandardFault">
									<xsl:value-of select="$exitOnStandardFault"/>
								</xsl:attribute>
							</xsl:if>
							<xsl:if test="xh:span[@class='oryx-embeddedsubprocesstype']='Scope'">
								<chor:Scope>
									<xsl:variable name="messageExchanges" select="xh:span[@class='oryx-messageexchanges']" />
									<chor:MessageExchanges>
										<xsl:if test="string-length(normalize-space($messageExchanges))>0" >
											<xsl:call-template name="string-tokens">
												<xsl:with-param name="list" select="$messageExchanges" />
												<xsl:with-param name="elementName">chor:MessageExchange</xsl:with-param>
											</xsl:call-template>
										</xsl:if>
									</chor:MessageExchanges>
								</chor:Scope>
							</xsl:if>
							<xsl:if test="xh:span[@class='oryx-embeddedsubprocesstype']='Handler'">
								<chor:Handler HandlerType="{xh:span[@class='oryx-handlertype']}" />
							</xsl:if>
						</xpdl:BlockActivity>
					</xsl:if>
				<!-- ******************** Event ********************** -->
					<xsl:if test="string-length(normalize-space($eventType))>0">
						<xpdl:Event>
							<xsl:variable name="trigger" select="xh:span[@class='oryx-trigger']" />
							<xsl:if test="$eventType='Start'" >
								<xpdl:StartEvent Trigger="{$trigger}">
									<xsl:if test="$trigger='Message'">
										<xsl:call-template name="triggerResultMessage">
											<xsl:with-param name="id" select="$id" />
										</xsl:call-template>
									</xsl:if>
									<xsl:if test="$trigger='Timer'">
										<xsl:call-template name="triggerTimer" />
									</xsl:if>
								</xpdl:StartEvent>
							</xsl:if>
							<xsl:if test="$eventType='Intermediate'" >
								<xsl:variable name="target" select="xh:span[@class='oryx-target']" />
								<xpdl:IntermediateEvent>
									<xsl:if test="$trigger='Termination'">
										<xsl:attribute name="Trigger">None</xsl:attribute>
										<xsl:attribute name="chor:IsTermination">true</xsl:attribute>
									</xsl:if>
									<xsl:if test="$trigger!='Termination'">
										<xsl:attribute name="Trigger">
											<xsl:value-of select="$trigger"></xsl:value-of>
										</xsl:attribute>
									</xsl:if>
									<xsl:if test="string-length(normalize-space($target))>0">
										<xsl:attribute name="Target">
											<xsl:value-of select="$target" />
										</xsl:attribute>
									</xsl:if>
									<xsl:if test="$trigger='Message'">
										<xsl:call-template name="triggerResultMessage">
											<xsl:with-param name="id" select="$id" />
										</xsl:call-template>
									</xsl:if>
									<xsl:if test="$trigger='Timer'">
										<xsl:call-template name="triggerTimer" />
									</xsl:if>
									<xsl:if test="$trigger='Error'">
										<xpdl:ResultError ErrorCode="{xh:span[@class='oryx-errorcode']}" />
									</xsl:if>
									<xsl:if test="$trigger='Compensation'">
										<xsl:variable name="activity" select="xh:span[@class='oryx-activity']" />
										<xpdl:ResultCompensation>
											<xsl:if test="string-length(normalize-space($activity))>0" >
												<xsl:attribute name="ActivityId">
													<xsl:value-of select="$activity" />
												</xsl:attribute>
											</xsl:if>
										</xpdl:ResultCompensation>
									</xsl:if>
								</xpdl:IntermediateEvent>
							</xsl:if>
							<xsl:if test="$eventType='End'" >
								<xpdl:EndEvent Result="None" />
							</xsl:if>
						</xpdl:Event>
					</xsl:if>
				<!-- correlation sets for scope -->
					<xsl:variable name="correlationSets" select="xh:span[@class='oryx-correlationsets']"/>
						<xsl:call-template name="correlationSets-tokens">
						<xsl:with-param name="correlationSets" select="$correlationSets" />
					</xsl:call-template>
				<!-- ************* Loop ******************** -->
					<xsl:if test="$loopType != 'None'">
						<xpdl:Loop LoopType="{$loopType}">
							<xsl:if test="$loopType ='Standard'">
								<xsl:variable name="loopConditionLanguage" select="xh:span[@class='oryx-st_loopconditionlanguage']" />
								<xpdl:LoopStandard
									LoopCondition="{xh:span[@class='oryx-st_loopcondition']}"
									TestTime="{xh:span[@class='oryx-st_testtime']}">
									<xsl:if test="string-length(normalize-space($loopConditionLanguage))>0">
										<xsl:attribute name="chor:LoopConditionLanguage">
											<xsl:value-of select="$loopConditionLanguage" />
										</xsl:attribute>
									</xsl:if>
								</xpdl:LoopStandard>
							</xsl:if>
							<xsl:if test="$loopType='MultiInstance'">
								<xsl:variable name="mi_StartCounterValueLanguage" select="xh:span[@class='oryx-mi_startcountervaluelanguage']" />
								<xsl:variable name="mi_FinalCounterValueLanguage" select="xh:span[@class='oryx-mi_finalcountervaluelanguage']" />
								<xsl:variable name="mi_CompletionCondition" select="xh:span[@class='oryx-mi_completioncondition']" />
								<xsl:variable name="mi_CompletionConditionLanguage" select="xh:span[@class='oryx-mi_completionconditionlanguage']" />
								<xsl:variable name="mi_SuccessfulBranchesOnly" select="xh:span[@class='oryx-mi_successfulbranchesonly']" />
								 <xpdl:LoopMultiInstance
									MI_Condition=""
									MI_Ordering="{xh:span[@class='oryx-mi_ordering']}"
									MI_FlowCondition="{xh:span[@class='oryx-mi_flowcondition']}">
									<xsl:if test="string-length(normalize-space($mi_SuccessfulBranchesOnly))>0">
										<xsl:attribute name="chor:SuccessfulBranchesOnly">
											<xsl:value-of select="$mi_SuccessfulBranchesOnly" />
										</xsl:attribute>
									</xsl:if>
									<chor:StartCounterValue>
										<xsl:if test="string-length(normalize-space($mi_StartCounterValueLanguage))>0">
											<xsl:attribute name="ScriptGrammar">
												<xsl:value-of select="$mi_StartCounterValueLanguage" />
											</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="xh:span[@class='oryx-mi_startcountervalue']"/>
									</chor:StartCounterValue>
									<chor:FinalCounterValue >
										<xsl:if test="string-length(normalize-space($mi_FinalCounterValueLanguage))>0">
											<xsl:attribute name="ScriptGrammar">
												<xsl:value-of select="$mi_FinalCounterValueLanguage" />
											</xsl:attribute>
										</xsl:if>
										<xsl:value-of select="xh:span[@class='oryx-mi_finalcountervalue']"/>
									</chor:FinalCounterValue>
									<xsl:if test="string-length(normalize-space($mi_CompletionCondition))>0">
										<chor:CompletionCondition>
											<xsl:if test="string-length(normalize-space($mi_CompletionConditionLanguage))>0">
												<xsl:attribute name="ScriptGrammar">
													<xsl:value-of select="$mi_CompletionConditionLanguage" />
												</xsl:attribute>
											</xsl:if>
											<xsl:value-of select="$mi_CompletionCondition"/>
										</chor:CompletionCondition>
									</xsl:if>
								</xpdl:LoopMultiInstance>
							</xsl:if>
						</xpdl:Loop>
					</xsl:if>
					
					<!-- add Object element -->
					<xsl:call-template name="object">
						<xsl:with-param name="objectParent" select="." />
					</xsl:call-template>
									
					<!-- add NodeGraphicsInfos -->
					<xsl:call-template name="nodeGraphicsInfos">
						<xsl:with-param name="node" select="." />
					</xsl:call-template>		
					
			<!-- ************* XPDL4Chor Tasks ******************** -->
					<xsl:if test="$type='Task'">
						<xsl:variable name="taskType" select="xh:span[@class='oryx-tasktype']" />
						<xsl:if test="$taskType='Assign'">
							<xpdl:Extensions/>
							<chor:Task>
								<TaskAssign
									Validate="{xh:span[@class='oryx-validate']}">								
									
									<xsl:call-template name="copies-tokens">
										<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-copy']" /></xsl:with-param>
									</xsl:call-template>								
								</TaskAssign>
							</chor:Task>
						</xsl:if>
						<xsl:if test="$taskType='Empty'">
							<xpdl:Extensions/>
							<chor:Task>
								<TaskEmpty />
							</chor:Task>
						</xsl:if>
						<xsl:if test="$taskType='Validate'">
							<xpdl:Extensions/>
							<chor:Task>
								<TaskValidate />
							</chor:Task>
						</xsl:if>
						<xsl:if test="$taskType='None'">
							<xpdl:Extensions/>
							<chor:Task>
								<TaskNone />
							</chor:Task>
						</xsl:if>
					</xsl:if>
				</xpdl:Activity>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
  <!-- ************** Template for extracting relevant Transitions ******************** -->
  <xsl:template name="relevantTransitions">
    <xsl:param name="resId" />
    <xsl:variable name="activitiesOutRefs" select="/xh:html/xh:body/xh:div[xh:a[@rel='raziel-parent' and @href=concat('#',$resId)]]/xh:a[@rel='raziel-outgoing']"/>
    <xsl:for-each select="$activitiesOutRefs">
      <xsl:variable name="outRef" select="@href"/>
      <xsl:variable name="relevantTransitions" select="/xh:html/xh:body/xh:div[xh:a[@rel='raziel-parent' and @href='#oryx-canvas123'] and @id=substring($outRef,2)]"/>
      <xsl:call-template name="transitions">
        <xsl:with-param name="transitions" select="$relevantTransitions" />
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

	<!-- ************** Template for generating Transitions content ******************** -->
	<xsl:template name="transitions">
		<xsl:param name="transitions" />
		<xsl:for-each select="$transitions">
			<xsl:variable name="type" select="xh:span[@class='oryx-type']" />
			<xsl:variable name="id" select="@id" />
			<xsl:variable name="name" select="xh:span[@class='oryx-name']" />
			<xsl:variable name="internalOutRef" select="xh:a[@rel='raziel-outgoing']/@href" />
			<xsl:if test="$type='http://b3mn.org/stencilset/bpmnplus#SequenceFlow' or 
						$type='http://b3mn.org/stencilset/bpmnplus#ConditionalFlow' or
						$type='http://b3mn.org/stencilset/bpmnplus#DefaultFlow'">
				<xpdl:Transition
					Id="{$id}"
					From="{/xh:html/xh:body/xh:div[xh:a[@rel='raziel-outgoing' and @href=concat('#',$id)]]/@id}"
					To="{/xh:html/xh:body/xh:div[@id=substring($internalOutRef,2)]/@id}">
					<xsl:if test="string-length(normalize-space($name))>0">
						<xsl:attribute name="Name">
							<xsl:value-of select="$name" />
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$type='http://b3mn.org/stencilset/bpmnplus#ConditionalFlow'">
						<xsl:variable name="conditionExpressionLanguage" select="xh:span[@class='oryx-conditionexpressionlanguage']" />
						<xpdl:Condition Type="CONDITION">
							<xpdl:Expression>
								<xsl:if test="string-length(normalize-space($conditionExpressionLanguage))>0">
									<xsl:attribute name="ScriptGrammar">
										<xsl:value-of select="$conditionExpressionLanguage"/>
									</xsl:attribute>
								</xsl:if>
								<xsl:value-of select="xh:span[@class='oryx-conditionexpression']"/>
							</xpdl:Expression>
						</xpdl:Condition>
					</xsl:if>
					<xsl:if test="$type='http://b3mn.org/stencilset/bpmnplus#DefaultFlow'">
						<xpdl:Condition Type="OTHERWISE">
						</xpdl:Condition>
					</xsl:if>
				</xpdl:Transition>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<!-- ************** Template for generating ActivitySets content ******************** -->
	<xsl:template name="activitySets">
		<xsl:param name="blockActivities" />
		<xsl:for-each select="$blockActivities">
			<xsl:variable name="type" select="xh:span[@class='oryx-activitytype']" />
			<xsl:if test="$type='SubProcess'">
				<xsl:variable name="resourceId" select="@id" />
				<xsl:variable name="innerActivities" select="/xh:html/xh:body/xh:div[xh:a[@rel='raziel-parent' and @href=concat('#',$resourceId)]]"/>
				<xsl:variable name="index" select="generate-id(.)" />
				<xsl:call-template name="activitySets">
					<xsl:with-param name="blockActivities" select="$innerActivities" />
				</xsl:call-template>
				<xpdl:ActivitySet
					Id="{concat(concat($resourceId,'_activitySet_'),$index)}">
					<xpdl:Activities>																
						<xsl:call-template name="activities">
							<xsl:with-param name="activities" select="$innerActivities" />
						</xsl:call-template>		
					</xpdl:Activities>
					<xpdl:Transitions>
						<xsl:call-template name="relevantTransitions">
							<xsl:with-param name="resId" select="@id" />
						</xsl:call-template>
					</xpdl:Transitions>
				</xpdl:ActivitySet>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<!-- ************** Template for generating Lanes in Pools and Pool Sets ******************** -->
	<xsl:template name="lanes">
		<xsl:param name="childLanes" />
		<xsl:param name="poolId" />
		<xsl:param name="poolSetId" />
		<xsl:if test="count($childLanes)>0">
			<xpdl:Lanes>
				<xsl:for-each select="$childLanes">
					<xsl:variable name="laneName" select="xh:span[@class='oryx-name']" />
					<xpdl:Lane 
						Id="{@id}">
						<xsl:if test="string-length(normalize-space($poolId))>0">
							<xsl:attribute name="ParentPool">
								<xsl:value-of select="$poolId"></xsl:value-of>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="string-length(normalize-space($poolSetId))>0">
							<xsl:attribute name="chor:ParentPoolSet">
								<xsl:value-of select="$poolSetId"></xsl:value-of>
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="string-length(normalize-space($laneName))>0">
							<xsl:attribute name="Name">
								<xsl:value-of select="$laneName"></xsl:value-of>
							</xsl:attribute>
						</xsl:if>
						
						<!-- add Object element (Categories and Documentation) -->
						<xsl:call-template name="object">
							<xsl:with-param name="objectParent" select="." />
						</xsl:call-template>
						
						<!-- add NodeGraphicsInfos -->
						<xsl:call-template name="nodeGraphicsInfos">
							<xsl:with-param name="node" select="." />
						</xsl:call-template>
					</xpdl:Lane>
				</xsl:for-each>
			</xpdl:Lanes>
		</xsl:if>
	</xsl:template>

	<!-- ************** Template for generating XPDL4Chor package ******************** -->
	<xsl:template match="/xh:html/xh:body">
		<xpdl:Package
			xmlns:chor="http://iaas.uni-stuttgart.de/schemas/XPDL4chor"
			xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://iaas.uni-stuttgart.de/schemas/XPDL4chor http://www.iaas.uni-stuttgart.de/schemas/bpel4chor/XPDL4chor.xsd http://www.wfmc.org/2004/XPDL2.0alpha http://www.wfmc.org/standards/docs/TC-1025_bpmnxpdl_24.xsd">
			<xpdl:PackageHeader>
				<xpdl:XPDLVersion>2.0</xpdl:XPDLVersion>
				<xpdl:Vendor>Oryx</xpdl:Vendor>
			</xpdl:PackageHeader>
<!-- *********************** Pools ******************************** -->
			<xsl:variable name="Pools" select="xh:div[xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#Pool']"/>
			<xsl:if test="count($Pools)>0">
				<xpdl:Pools>
					<xsl:for-each select="$Pools">
						<xsl:variable name="id" select="@id" />
						<xpdl:Pool 
							Id="{$id}" 
							Name="{xh:span[@class='oryx-name']}"
							Process="{xh:span[@class='oryx-processid']}"
							chor:TargetNamespace="{xh:span[@class='oryx-processtargetnamespace']}"
							chor:Prefix="{xh:span[@class='oryx-processprefix']}"
							chor:Containment="{xh:span[@class='oryx-containment']}"
							BoundaryVisible="{xh:span[@class='oryx-boundaryvisible']}">
							<xsl:variable name="participantRef" select="xh:span[@class='oryx-participantname']" />
							<xsl:if test="string-length(normalize-space($participantRef))>0">
								<xsl:attribute name="Participant">
									<xsl:value-of select="$participantRef"></xsl:value-of>
								</xsl:attribute>
							</xsl:if>
							
							<!-- determine Lanes -->
							<xsl:variable name="childLanes" select="/xh:html/xh:body/xh:div[xh:a[@rel='raziel-parent' and @href=concat('#',$id)]]" />
							<xsl:call-template name="lanes">
								<xsl:with-param name="childLanes" select="$childLanes" />
								<xsl:with-param name="poolId" select="$id" />
							</xsl:call-template>
								
							<!-- add Object element (Categories and Documentation) -->
							<xsl:call-template name="object">
								<xsl:with-param name="objectParent" select="." />
							</xsl:call-template>
							
							<!-- add NodeGraphicsInfos -->
							<xsl:call-template name="nodeGraphicsInfos">
								<xsl:with-param name="node" select="." />
							</xsl:call-template>
							
							<xsl:call-template name="string-tokens">
								<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-selects']" /></xsl:with-param>
								<xsl:with-param name="elementName">chor:Selects</xsl:with-param>
							</xsl:call-template>
							<xsl:call-template name="imports-tokens">
								<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-imports']" /></xsl:with-param>
							</xsl:call-template>
						</xpdl:Pool>
					</xsl:for-each>				
				</xpdl:Pools>
			</xsl:if>
<!-- ******************* Message Flows ********************** -->
			<xsl:variable name="MessageFlows" select="xh:div[xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#MessageFlow']"/>
			<xsl:if test="count($MessageFlows)>0">
				<xpdl:MessageFlows>
					<xsl:for-each select="$MessageFlows">
						<xsl:variable name="id" select="@id"/>
						<xsl:variable name="name" select="xh:span[@class='oryx-name']"/>
						<xpdl:MessageFlow 
							Id="{$id}"
							Source="{xh:span[@class='oryx-source']}"
							Target="{xh:span[@class='oryx-target']}">				
							<xsl:if test="string-length(normalize-space($name))>0">
								<xsl:attribute name="Name">
									<xsl:value-of select="$name" />
								</xsl:attribute>
							</xsl:if>
							<xpdl:Message 
								Id="{concat($id,'_message')}" 
								Name="{xh:span[@class='oryx-message']}">
							</xpdl:Message>
						</xpdl:MessageFlow>
					</xsl:for-each>
				</xpdl:MessageFlows>
			</xsl:if>
<!-- ******************* Associations ********************** -->
			<xsl:variable name="undirectedAssociations" select="xh:div[xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#UndirectedAssociation']"/>
			<xsl:variable name="directedAssociations" select="xh:div[xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#DirectedAssociation']"/>
			<xsl:if test="count($undirectedAssociations)>0 or count($directedAssociations)>0">
				<xpdl:Associations>
					<xsl:for-each select="$undirectedAssociations">
						<xsl:variable name="name" select="xh:span[@class='oryx-name']"/>
						<xpdl:Association 
							Id="{@id}"
							Source="{xh:span[@class='oryx-source']}"
							Target="{xh:span[@class='oryx-target']}"
							AssociationDirection="{xh:span[@class='oryx-direction']}">
							<xsl:if test="string-length(normalize-space($name))>0">
								<xsl:attribute name="Name">
									<xsl:value-of select="$name" />
								</xsl:attribute>
							</xsl:if>
						</xpdl:Association>
					</xsl:for-each>
					<xsl:for-each select="$directedAssociations">
						<xsl:variable name="name" select="xh:span[@class='oryx-name']"/>
						<xpdl:Association 
							Id="{@id}"
							Source="{xh:span[@class='oryx-source']}"
							Target="{xh:span[@class='oryx-target']}"
							AssociationDirection="{xh:span[@class='oryx-direction']}">
							<xsl:if test="string-length(normalize-space($name))>0">
								<xsl:attribute name="Name">
									<xsl:value-of select="$name" />
								</xsl:attribute>
							</xsl:if>
						</xpdl:Association>
					</xsl:for-each>
				</xpdl:Associations>
			</xsl:if>
<!-- ******************* Artifacts *************************** -->
			<xsl:variable name="VarDataObjects" select="xh:div[xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#MessageVariableDataObject' or 
										 			xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#FaultVariableDataObject' or
										  			xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#StandardVariableDataObject' or 
										  			xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#CounterVariableDataObject']"/>
			<xsl:variable name="RefDataObjects" select="xh:div[xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#ParticipantReferenceDataObject']"/>							 
			<xsl:variable name="SetDataObjects" select="xh:div[xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#ParticipantSetDataObject']"/>
			<xsl:if test="(count($VarDataObjects) + count($RefDataObjects) + count($SetDataObjects))>0">
				<xpdl:Artifacts>
				<!-- ******************** Variable Data Object ******************* -->
					<xsl:for-each select="$VarDataObjects">
						<xsl:variable name="id" select="@id"/>
						<xsl:variable name="subProcess" select="xh:span[@class='oryx-subprocess']"/>
						<xsl:variable name="process" select="xh:span[@class='oryx-process']"/>
						<xsl:variable name="pool" select="xh:span[@class='oryx-pool']"/>
						<xsl:variable name="poolSet" select="xh:span[@class='oryx-poolset']"/>
						<xpdl:Artifact 
							Id="{$id}"
							Name="{xh:span[@class='oryx-name']}"
							ArtifactType="DataObject">
							<xsl:if test="string-length(normalize-space($subProcess))>0">
								<xsl:variable name="subProcessNode" select="/xh:html/xh:body/xh:div[@id=$subProcess]" />
								<xsl:variable name="index" select="generate-id($subProcessNode)" />
								<xsl:attribute name="chor:SubProcess">
									<xsl:value-of select="concat(concat($subProcess,'_activitySet_'),$index)" />
								</xsl:attribute>
							</xsl:if>
							<xsl:if test="string-length(normalize-space($process))>0">
								<xsl:attribute name="chor:Process">
									<xsl:value-of select="$process" />
								</xsl:attribute>
							</xsl:if>
							<xsl:if test="string-length(normalize-space($pool))>0">
								<xsl:attribute name="chor:Pool">
									<xsl:value-of select="$pool" />
								</xsl:attribute>
							</xsl:if>
							<xsl:if test="string-length(normalize-space($poolSet))>0">
								<xsl:attribute name="chor:PoolSet">
									<xsl:value-of select="$poolSet" />
								</xsl:attribute>
							</xsl:if>
							<xpdl:DataObject 
								Id="{concat($id,'_DataObject')}"
								RequiredForStart="true" 
								ProducedAtCompletion="true">
								<xsl:variable name="type" select="xh:span[@class='oryx-vartype']"/>
								<xsl:variable name="prefix" select="xh:span[@class='oryx-deftypeprefix']"/>
								<xsl:variable name="value" select="xh:span[@class='oryx-deftypevalue']"/>
								<chor:VariableDataObject 
									Type="{$type}">
									<xsl:if test="xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#MessageVariableDataObject' or 
												  xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#FaultVariableDataObject' or
												  xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#StandardVariableDataObject'">
										<xsl:attribute name="VariableType"><xsl:value-of select="xh:span[@class='oryx-deftype']"/></xsl:attribute>
										<xsl:attribute name="VariableTypeValue"><xsl:value-of select="concat(concat($prefix,':'),$value)"/></xsl:attribute>
									</xsl:if>
									<xsl:if test="xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#StandardVariableDataObject'">
										<xsl:call-template name="from-specs-tokens">
											<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-fromspec']" /></xsl:with-param>
										</xsl:call-template>
									</xsl:if>
								</chor:VariableDataObject>
							</xpdl:DataObject>
						</xpdl:Artifact> 
					</xsl:for-each>
				<!-- ******************** Participant Reference Data Object ******************* -->
					<xsl:for-each select="$RefDataObjects">
						<xsl:variable name="id" select="@id"/>				
						<xsl:variable name="pool" select="xh:span[@class='oryx-pool']"/>
						<xsl:variable name="poolSet" select="xh:span[@class='oryx-poolset']"/>
						<xpdl:Artifact
							Id="{$id}"
							Name="{xh:span[@class='oryx-name']}"
							ArtifactType="DataObject">
							<xsl:if test="string-length(normalize-space($pool))>0">
								<xsl:attribute name="chor:Pool">
									<xsl:value-of select="$pool" />
								</xsl:attribute>
							</xsl:if>
							<xsl:if test="string-length(normalize-space($poolSet))>0">
								<xsl:attribute name="chor:PoolSet">
									<xsl:value-of select="$poolSet" />
								</xsl:attribute>
							</xsl:if>
							<xpdl:DataObject 
								Id="{concat($id,'_DataObject')}"
								RequiredForStart="true" 
								ProducedAtCompletion="true">
								<chor:ParticipantReferenceDataObject>
									<xsl:variable name="scope" select="xh:span[@class='oryx-scope']"/>
									<xsl:if test="string-length(normalize-space($scope))>0">
										<xsl:attribute name="Scope">
											<xsl:value-of select="$scope" />
										</xsl:attribute>
									</xsl:if>	
									<xsl:variable name="copyTo" select="xh:span[@class='oryx-copyto']"/>
									<xsl:if test="string-length(normalize-space($copyTo))>0">
										<xsl:attribute name="CopyTo">
											<xsl:value-of select="$copyTo" />
										</xsl:attribute>
									</xsl:if>
									<xsl:variable name="containment" select="xh:span[@class='oryx-containment']"/>
									<xsl:if test="string-length(normalize-space($containment))>0">
										<xsl:attribute name="Containment">
											<xsl:value-of select="$containment" />
										</xsl:attribute>
									</xsl:if>
									<xsl:call-template name="string-tokens">
										<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-selects']" /></xsl:with-param>
										<xsl:with-param name="elementName">chor:Selects</xsl:with-param>
									</xsl:call-template>
								</chor:ParticipantReferenceDataObject>
							</xpdl:DataObject>
						</xpdl:Artifact>
					</xsl:for-each>
				<!-- ******************** Participant Set Data Object ******************* -->
					<xsl:for-each select="$SetDataObjects">
						<xsl:variable name="id" select="@id"/>				
						<xsl:variable name="pool" select="xh:span[@class='oryx-pool']"/>
						<xsl:variable name="poolSet" select="xh:span[@class='oryx-poolset']"/>
						<xpdl:Artifact
							Id="{$id}"
							Name="{xh:span[@class='oryx-name']}"
							ArtifactType="DataObject">
							<xsl:if test="string-length(normalize-space($pool))>0">
								<xsl:attribute name="chor:Pool">
									<xsl:value-of select="$pool" />
								</xsl:attribute>
							</xsl:if>
							<xsl:if test="string-length(normalize-space($poolSet))>0">
								<xsl:attribute name="chor:PoolSet">
									<xsl:value-of select="$poolSet" />
								</xsl:attribute>
							</xsl:if>
							<xpdl:DataObject 
								Id="{concat($id,'_DataObject')}"
								RequiredForStart="true" 
								ProducedAtCompletion="true">
								<chor:ParticipantSetDataObject>
									<xsl:variable name="scope" select="xh:span[@class='oryx-scope']"/>
									<xsl:if test="string-length(normalize-space($scope))>0">
										<xsl:attribute name="Scope">
											<xsl:value-of select="$scope" />
										</xsl:attribute>
									</xsl:if>	
									<xsl:variable name="copyTo" select="xh:span[@class='oryx-copyto']"/>
									<xsl:if test="string-length(normalize-space($copyTo))>0">
										<xsl:attribute name="CopyTo">
											<xsl:value-of select="$copyTo" />
										</xsl:attribute>
									</xsl:if>
								</chor:ParticipantSetDataObject>
							</xpdl:DataObject>
						</xpdl:Artifact>
					</xsl:for-each>
				</xpdl:Artifacts>
			</xsl:if>		
	<!-- ************************** Processes ******************************* -->
			<xpdl:WorkflowProcesses>
				<xsl:for-each select="xh:div[xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#Pool' or 
										xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#PoolSet']">
					<xsl:variable name="suppressJoinFailure" select="xh:span[@class='oryx-suppressjoinfailure']"/>
					<xsl:variable name="enableInstanceCompensation" select="xh:span[@class='oryx-enableinstancecompensation']"/>
					<xsl:variable name="queryLanguage" select="xh:span[@class='oryx-querylanguage']"/>
					<xsl:variable name="expressionLanguage" select="xh:span[@class='oryx-expressionlanguage']"/>
					<xsl:variable name="exitOnStandardFault" select="xh:span[@class='oryx-exitonstandardfault']"/>
					<xsl:variable name="messageExchanges" select="xh:span[@class='oryx-messageexchanges']" />
					<xpdl:WorkflowProcess 
						Id="{xh:span[@class='oryx-processid']}"
						Name="{xh:span[@class='oryx-processname']}">
						<xsl:if test="string-length(normalize-space($suppressJoinFailure))>0">
							<xsl:attribute name="SuppressJoinFailure">
								<xsl:value-of select="$suppressJoinFailure" />
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="string-length(normalize-space($enableInstanceCompensation))>0">
							<xsl:attribute name="EnableInstanceCompensation">
								<xsl:value-of select="$enableInstanceCompensation" />
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="string-length(normalize-space($queryLanguage))>0">
							<xsl:attribute name="chor:QueryLanguage">
								<xsl:value-of select="$queryLanguage" />
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="string-length(normalize-space($expressionLanguage))>0">
							<xsl:attribute name="chor:ExpressionLanguage">
								<xsl:value-of select="$expressionLanguage" />
							</xsl:attribute>
						</xsl:if>
						<xsl:if test="string-length(normalize-space($exitOnStandardFault))>0">
							<xsl:attribute name="chor:ExitOnStandardFault">
								<xsl:value-of select="$exitOnStandardFault" />
							</xsl:attribute>
						</xsl:if>
						<xpdl:ProcessHeader />
						<xsl:variable name="correlationSets" select="xh:span[@class='oryx-processcorrelationsets']"/>
						<xsl:call-template name="correlationSets-tokens">
							<xsl:with-param name="correlationSets" select="$correlationSets" />
						</xsl:call-template>
						
						<xsl:variable name="poolId" select="@id" />
						<xsl:variable name="childLanes" select="/xh:html/xh:body/xh:div[xh:a[@rel='raziel-parent' and @href=concat('#',$poolId)]]" />
						
						<xpdl:ActivitySets>
							<xsl:for-each select="$childLanes">
								<xsl:variable name="laneId" select="@id"/>
								<xsl:variable name="blockActivities" select="/xh:html/xh:body/xh:div[xh:a[@rel='raziel-parent' and @href=concat('#',$laneId)]]"/>						
								<xsl:call-template name="activitySets">
									<xsl:with-param name="blockActivities" select="$blockActivities" />
								</xsl:call-template>		
							</xsl:for-each>
						</xpdl:ActivitySets>
			<!-- ************************ Activities *********************************+ -->
						<xpdl:Activities>																
							<xsl:for-each select="$childLanes">
								<xsl:variable name="laneId" select="@id"/>
								<xsl:variable name="childActivities" select="/xh:html/xh:body/xh:div[xh:a[@rel='raziel-parent' and @href=concat('#',$laneId)]]"/>						
								<xsl:call-template name="activities">
									<xsl:with-param name="activities" select="$childActivities" />
								</xsl:call-template>		
							</xsl:for-each>
						</xpdl:Activities>
			<!-- ************************ Transitions *********************************+ -->
						<xpdl:Transitions>
							<xsl:for-each select="$childLanes">
								<xsl:call-template name="relevantTransitions">
									<xsl:with-param name="resId" select="@id" />
								</xsl:call-template>
							</xsl:for-each>
						</xpdl:Transitions>

						<xpdl:Extensions/>
			<!-- ************************ Message Exchanges *********************************+ -->
						<chor:MessageExchanges>
							<xsl:if test="string-length(normalize-space($messageExchanges))>0" >
								<xsl:call-template name="string-tokens">
									<xsl:with-param name="list" select="$messageExchanges" />
									<xsl:with-param name="elementName">chor:MessageExchange</xsl:with-param>
								</xsl:call-template>
							</xsl:if>
						</chor:MessageExchanges>
					</xpdl:WorkflowProcess>
				</xsl:for-each>
			</xpdl:WorkflowProcesses>
	<!-- *********************** Pool Sets ******************************** -->
			<xsl:variable name="PoolSets" select="xh:div[xh:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmnplus#PoolSet']"/>
			<xsl:if test="count($PoolSets)>0">
				<chor:PoolSets>
					<xsl:for-each select="$PoolSets">
						<xsl:variable name="id" select="@id" />
						<xpdl:PoolSet
							Id="{$id}" 
							Name="{xh:span[@class='oryx-name']}"
							Process="{xh:span[@class='oryx-processid']}"
							TargetNamespace="{xh:span[@class='oryx-processtargetnamespace']}"
							Prefix="{xh:span[@class='oryx-processprefix']}"
							BoundaryVisible="{xh:span[@class='oryx-boundaryvisible']}">
							
							<!-- determine Lanes -->
							<xsl:variable name="childLanes" select="/xh:html/xh:body/xh:div[xh:a[@rel='raziel-parent' and @href=concat('#',$id)]]" />
							<xsl:call-template name="lanes">
								<xsl:with-param name="childLanes" select="$childLanes" />
								<xsl:with-param name="poolSetId" select="$id" />
							</xsl:call-template>
								
							<!-- add Object element (Categories and Documentation) -->
							<xsl:call-template name="object">
								<xsl:with-param name="objectParent" select="." />
							</xsl:call-template>
							
							<!-- add NodeGraphicsInfos -->
							<xsl:call-template name="nodeGraphicsInfos">
								<xsl:with-param name="node" select="." />
							</xsl:call-template>
							
							<xsl:call-template name="imports-tokens">
								<xsl:with-param name="list"><xsl:value-of select="xh:span[@class='oryx-imports']" /></xsl:with-param>
							</xsl:call-template>
						</xpdl:PoolSet>
					</xsl:for-each>	
				</chor:PoolSets>
			</xsl:if>
		</xpdl:Package>
	</xsl:template>
</xsl:stylesheet>