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
			<topology>
				<xsl:variable name="name" select="./oryx:name" />
				<xsl:if test="$name!=''">
					<xsl:attribute name="name">
						<xsl:value-of select="concat($name,'topology') " />
					</xsl:attribute>
				</xsl:if>
				
				<xsl:variable name="targetNamespace" select="./oryx:targetnamespace" />
				<xsl:if test="$targetNamespace!=''">
					<xsl:attribute name="targetNamespace">
						<xsl:value-of select="$targetNamespace" />
					</xsl:attribute>
				</xsl:if>
				
				<xsl:call-template name="add-otherxmlns-attribute"/>
					
				<participantTypes>
					<xsl:call-template name="find-all-participantTypes"/>
				</participantTypes>	
				
				<participants>
					<xsl:call-template name="find-all-participants"/>
					<xsl:call-template name="find-all-participantSets"/>
				</participants>	
				
				<messageLinks>
					<xsl:call-template name="find-all-messageLinks"/>
				</messageLinks>	
				
				<nodeInfoSet>
					<xsl:call-template name="record-process-infos"/>
				</nodeInfoSet>
				
			</topology>	
	 	</xsl:if>
	</xsl:template>
	
	<xsl:template name="record-process-infos">
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="typeString" select="./oryx:type" />	
			<xsl:variable name="type">
				<xsl:call-template name="get-exact-type">
					<xsl:with-param name="typeString" select="$typeString" />
				</xsl:call-template>
			</xsl:variable>
			
			<xsl:if test="$type='process'">
				<xsl:variable name="processID"><xsl:value-of select="@rdf:about" /></xsl:variable>
				<process>
					<xsl:if test="$processID!=''">
						<xsl:attribute name="processID">
							<xsl:value-of select="$processID" />
						</xsl:attribute>
					</xsl:if>		
				</process>	
				
				<xsl:call-template name="find-children-nodes">
					<xsl:with-param name="searchedParentID"><xsl:value-of select="$processID" /></xsl:with-param>
					<xsl:with-param name="processID"><xsl:value-of select="$processID" /></xsl:with-param>
			    </xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="find-children-nodes">
		<xsl:param name="searchedParentID" />
		<xsl:param name="processID" />
		
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="currentParentID"><xsl:value-of select="(./raziel:parent/@rdf:resource)" /></xsl:variable>         
			<xsl:if test="$currentParentID = $searchedParentID">
      		  	<xsl:variable name="currentID" select="@rdf:about" />
				<xsl:variable name="typeString" select="./oryx:type" />	
				<xsl:variable name="type">
					<xsl:call-template name="get-exact-type">
						<xsl:with-param name="typeString" select="$typeString" />
					</xsl:call-template>
				</xsl:variable>
				
				<xsl:if test="$type='invoke' or 'reply' or 'onMessage' or 'receive'">
					<sendOrReceiveActivity> 
					
						<xsl:variable name="name"><xsl:value-of select="./oryx:name" /></xsl:variable>
						
						<xsl:attribute name="name">
							<xsl:value-of select="$name" />
						</xsl:attribute>
						
						<xsl:attribute name="id">
							<xsl:value-of select="$currentID" />
						</xsl:attribute>
						
						<xsl:attribute name="processID">
							<xsl:value-of select="$processID" />
						</xsl:attribute>
						
						<xsl:if test="$type='invoke' or 'reply'">
							<xsl:call-template name="add-outgoing-elements">
								<xsl:with-param name="researchedTargetType">messageLink</xsl:with-param>
							</xsl:call-template>
						</xsl:if>
						
					</sendOrReceiveActivity>
				</xsl:if>
				
				<xsl:call-template name="find-children-nodes">
					<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
					<xsl:with-param name="processID"><xsl:value-of select="$processID" /></xsl:with-param>
			    </xsl:call-template>	
			</xsl:if>
		</xsl:for-each>
	</xsl:template>				
				
				
	<xsl:template name="find-all-participantTypes">
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="typeString" select="./oryx:type" />	
			<xsl:variable name="type">
				<xsl:call-template name="get-exact-type">
					<xsl:with-param name="typeString" select="$typeString" />
				</xsl:call-template>
			</xsl:variable>
			
			<!--participantTypes saved in processes-->
			<xsl:if test="$type='process'">
				<xsl:variable name="typeName" select="./oryx:participanttype" />
					
				<xsl:if test="$typeName!=''">
					<participantType>
						<xsl:attribute name="name">
							<xsl:value-of select="$typeName" />
						</xsl:attribute>
					
						<xsl:variable name="processName" select="./oryx:name" />
						<xsl:variable name="namespace" select="./oryx:targetnamespace" />
						<xsl:if test="$processName!=''">
							<xsl:attribute name="participantBehaviorDescription">
								<xsl:value-of select="$processName" />
							</xsl:attribute>
							
							<xsl:attribute name="namespace">
								<xsl:value-of select="$namespace" />
							</xsl:attribute>
						</xsl:if>					
					</participantType>
				</xsl:if>
			</xsl:if>	
			
			<!--participantTypes saved in participantSet-->
			<xsl:if test="$type='participantSet'">
				<xsl:variable name="outgoingLinks" select="count(./raziel:outgoing)" />
				
				<xsl:if test="$outgoingLinks='0'">	
					<xsl:variable name="typeName" select="./oryx:participanttype" />
					<xsl:if test="$typeName!=''">
						<participantType>
							<xsl:attribute name="name">
								<xsl:value-of select="$typeName" />
							</xsl:attribute>			
						</participantType>
					</xsl:if>	
				</xsl:if>
			</xsl:if>	
			
			<!--participantTypes saved in participantRef-->
			<xsl:if test="$type='participant'">
				<xsl:variable name="outgoingLinks" select="count(./raziel:outgoing)" />
				
				<xsl:if test="$outgoingLinks='0'">	
					<xsl:variable name="typeName" select="./oryx:participanttype" />
					<xsl:if test="$typeName!=''">
						<participantType>
							<xsl:attribute name="name">
								<xsl:value-of select="$typeName" />
							</xsl:attribute>			
						</participantType>
					</xsl:if>	
				</xsl:if>
			</xsl:if>
			
		</xsl:for-each>
	</xsl:template>	
	
	<xsl:template name="find-all-participants">
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="typeString" select="./oryx:type" />	
			<xsl:variable name="type">
				<xsl:call-template name="get-exact-type">
					<xsl:with-param name="typeString" select="$typeString" />
				</xsl:call-template>
			</xsl:variable>
			
			<!--record all participant information-->
			<xsl:if test="$type='participant'">
				<participant>
					<xsl:variable name="name" select="./oryx:name" />
					<xsl:if test="$name!=''">
						<xsl:attribute name="name">
							<xsl:value-of select="$name" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:variable name="type" select="./oryx:type" />
					<xsl:if test="$type!=''">
						<xsl:attribute name="type">
							<xsl:value-of select="$type" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:variable name="selects" select="./oryx:selects" />
					<xsl:if test="$selects!=''">
						<xsl:attribute name="selects">
							<xsl:value-of select="$selects" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:variable name="scope" select="./oryx:scope" />
					<xsl:if test="$scope!=''">
						<xsl:attribute name="scope">
							<xsl:value-of select="$scope" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:call-template name="add-outgoing-elements">
						<xsl:with-param name="researchedTargetType">directedAssociation</xsl:with-param>
					</xsl:call-template>
				</participant>
			</xsl:if>

		</xsl:for-each>
	</xsl:template>	
	
	<xsl:template name="find-all-participantSets">
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="typeString" select="./oryx:type" />	
			<xsl:variable name="type">
				<xsl:call-template name="get-exact-type">
					<xsl:with-param name="typeString" select="$typeString" />
				</xsl:call-template>
			</xsl:variable>
		
			<!--participantSet-->
			<xsl:if test="$type='participantSet'">
				<participantSet>
					<xsl:variable name="id" select="@rdf:about" />
					<xsl:if test="$id!=''">
						<xsl:attribute name="id">
							<xsl:value-of select="$id" />
						</xsl:attribute>
					</xsl:if>
					
					
					<xsl:variable name="name" select="./oryx:name" />
					<xsl:if test="$name!=''">
						<xsl:attribute name="name">
							<xsl:value-of select="$name" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:variable name="type" select="./oryx:type" />
					<xsl:if test="$type!=''">
						<xsl:attribute name="type">
							<xsl:value-of select="$type" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:variable name="scope" select="./oryx:scope" />
					<xsl:if test="$scope!=''">
						<xsl:attribute name="scope">
							<xsl:value-of select="$scope" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:variable name="forEach" select="./oryx:foreach" />
					<xsl:if test="$forEach!=''">
						<xsl:attribute name="forEach">
							<xsl:value-of select="$forEach" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:call-template name="add-outgoing-elements">
						<xsl:with-param name="researchedTargetType">directedAssociation</xsl:with-param>
					</xsl:call-template>
				</participantSet>
			</xsl:if>		
		</xsl:for-each>
	</xsl:template>	
	
	
	<xsl:template name="find-all-messageLinks">
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="typeString" select="./oryx:type" />	
			<xsl:variable name="type">
				<xsl:call-template name="get-exact-type">
					<xsl:with-param name="typeString" select="$typeString" />
				</xsl:call-template>
			</xsl:variable>
			
			<!--messageLink-->
			<xsl:if test="$type='messageLink'">
				<messageLink>
					<xsl:variable name="currentID" select="@rdf:about" />
					<xsl:if test="$currentID!=''">
						<xsl:attribute name="id">
							<xsl:value-of select="$currentID" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:variable name="name" select="./oryx:name" />
					<xsl:if test="$name!=''">
						<xsl:attribute name="name">
							<xsl:value-of select="$name" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:variable name="bindSenderTo" select="./oryx:bindsenderto" />
					<xsl:if test="$bindSenderTo!=''">
						<xsl:attribute name="bindSenderTo">
							<xsl:value-of select="$bindSenderTo" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:variable name="messageName" select="./oryx:messagename" />
					<xsl:if test="$messageName!=''">
						<xsl:attribute name="messageName">
							<xsl:value-of select="$messageName" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:variable name="participantRefs" select="./oryx:participantrefs" />
					<xsl:if test="$participantRefs!=''">
						<xsl:attribute name="participantRefs">
							<xsl:value-of select="$participantRefs" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:variable name="copyParticipantRefsTo" select="./oryx:copyparticipantrefsto" />
					<xsl:if test="$copyParticipantRefsTo!=''">
						<xsl:attribute name="copyParticipantRefsTo">
							<xsl:value-of select="$copyParticipantRefsTo" />
						</xsl:attribute>
					</xsl:if>

					<xsl:call-template name="add-outgoing-elements">
						<xsl:with-param name="researchedTargetType">all</xsl:with-param>
					</xsl:call-template>
					
				</messageLink>
			</xsl:if>	
		</xsl:for-each>
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
	
	<xsl:template name="add-outgoing-elements">
		<xsl:param name="researchedTargetType"/>
		
		<xsl:variable name="numberOfOutgoingLinks" select="count(./raziel:outgoing)" />
		<xsl:if test="$numberOfOutgoingLinks!=0">
			<xsl:call-template name="loop-for-adding-outgoing-elements">
				<xsl:with-param name="i">1</xsl:with-param>
				<xsl:with-param name="count" select="$numberOfOutgoingLinks" />
				<xsl:with-param name="researchedTargetType" select="$researchedTargetType" />
			</xsl:call-template>
		</xsl:if>	
	</xsl:template>	
	
	<xsl:template name="loop-for-adding-outgoing-elements">
		<xsl:param name="i"/>
 		<xsl:param name="count"/>
		<xsl:param name="researchedTargetType"/>
			
 		<xsl:if test="$i &lt;= $count">
 			<xsl:variable name="targetID" select="(./raziel:outgoing/@rdf:resource)[$i]" />
			
			<xsl:for-each select="//rdf:Description">
				<xsl:variable name="currentID"><xsl:value-of select="@rdf:about" /></xsl:variable>         
				<xsl:if test="$currentID = $targetID">
      		  		<xsl:variable name="typeString" select="./oryx:type" />	
					<xsl:variable name="type">
						<xsl:call-template name="get-exact-type">
							<xsl:with-param name="typeString" select="$typeString" />
						</xsl:call-template>
					</xsl:variable>
					
					<xsl:if test="$researchedTargetType='all'">
						<outgoingLink>
							<xsl:attribute name="targetID">
								<xsl:value-of select="$targetID" />
							</xsl:attribute>
						</outgoingLink>	
					</xsl:if>
						
					<xsl:if test="$type=$researchedTargetType">
						<outgoingLink>
							<xsl:attribute name="targetID">
								<xsl:value-of select="$targetID" />
							</xsl:attribute>
						</outgoingLink>	
					</xsl:if>		
				
				</xsl:if>
			</xsl:for-each>			
			
  			<xsl:call-template name="loop-for-adding-outgoing-elements">
   				<xsl:with-param name="i" select="$i + 1"/>
   				<xsl:with-param name="count" select="$count"/>
				<xsl:with-param name="researchedTargetType" select="$researchedTargetType" />
  			</xsl:call-template>
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
	
	
</xsl:stylesheet>