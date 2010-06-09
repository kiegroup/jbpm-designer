<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:oryx="http://oryx-editor.org/"
	xmlns:raziel="http://raziel.org/">

	<xsl:output method="xml" />
	
	<xsl:template match="rdf:Description">	
		<xsl:variable name="typeString" select="./oryx:type" />	
		<xsl:variable name="nodeType">
			<xsl:call-template name="get-exact-type">
				<xsl:with-param name="typeString" select="$typeString" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="$nodeType='worksheet'">
			<!-- root element -->
			<topology xmlns="urn:HPI_IAAS:choreography:schemas:choreography:topology:2006/12">
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

				<!-- extended for the crossPartnerScopes by Changhua Li -->				
				<xsl:call-template name="find-all-crossPartnerScopes"/>
				<!-- extended end -->
				
				<nodeInfoSet>
					<xsl:call-template name="record-process-infos"/>
				</nodeInfoSet>
				
				<associationEdgeInfoSet>
					<xsl:call-template name="record-association-edge-infos"/>
				</associationEdgeInfoSet>	
			</topology>	
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
	
	
	<xsl:template name="find-all-messageLinks">
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="typeString" select="./oryx:type" />	
			<xsl:variable name="nodeType">
				<xsl:call-template name="get-exact-type">
					<xsl:with-param name="typeString" select="$typeString" />
				</xsl:call-template>
			</xsl:variable>
			
			<!--messageLink-->
			<xsl:if test="$nodeType='messageLink'">
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
					
					<xsl:variable name="sender" select="./oryx:sender" />
					<xsl:if test="$sender!=''">
						<xsl:attribute name="senders">
							<xsl:value-of select="$sender" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:variable name="receiver" select="./oryx:receiver" />
					<xsl:if test="$receiver!=''">
						<xsl:attribute name="receivers">
							<xsl:value-of select="$receiver" />
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


	<xsl:template name="find-all-participants">
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="typeString" select="./oryx:type" />	
			<xsl:variable name="nodeType">
				<xsl:call-template name="get-exact-type">
					<xsl:with-param name="typeString" select="$typeString" />
				</xsl:call-template>
			</xsl:variable>
			
			<!--record all participant information-->
			<xsl:if test="$nodeType='participant'">
				<participant>
					<xsl:variable name="name" select="./oryx:name" />
					<xsl:if test="$name!=''">
						<xsl:attribute name="name">
							<xsl:value-of select="$name" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:variable name="type" select="./oryx:participanttype" />
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
					
					<xsl:variable name="copyTo" select="./oryx:copyto" />
					<xsl:if test="$copyTo!=''">
						<xsl:attribute name="copyTo">
							<xsl:value-of select="$copyTo" />
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
				</participant>
			</xsl:if>
			
			<!--record all single process als participant-->
			<xsl:if test="$nodeType='process'">
				<participant>
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
					
					<xsl:variable name="type" select="./oryx:participanttype" />
					<xsl:if test="$type!=''">
						<xsl:attribute name="type">
							<xsl:value-of select="$type" />
						</xsl:attribute>
					</xsl:if>
					<xsl:if test="$type=''">
						<xsl:attribute name="type" select="./oryx:name" />
					</xsl:if>
					
				</participant>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>	
	
	
	<xsl:template name="find-all-participantSets">
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="typeString" select="./oryx:type" />	
			<xsl:variable name="nodeType">
				<xsl:call-template name="get-exact-type">
					<xsl:with-param name="typeString" select="$typeString" />
				</xsl:call-template>
			</xsl:variable>
		
			<!--participantSet-->
			<xsl:if test="$nodeType='participantSet'">
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
					
					<xsl:variable name="type" select="./oryx:participanttype" />
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
					
					<xsl:variable name="copyTo" select="./oryx:copyto" />
					<xsl:if test="$copyTo!=''">
						<xsl:attribute name="copyTo">
							<xsl:value-of select="$copyTo" />
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
	
	
	<xsl:template name="find-all-participantTypes">
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="typeString" select="./oryx:type" />	
			<xsl:variable name="nodeType">
				<xsl:call-template name="get-exact-type">
					<xsl:with-param name="typeString" select="$typeString" />
				</xsl:call-template>
			</xsl:variable>
			
			<!--participantTypes saved in processes-->
			<xsl:if test="$nodeType='process'">
				<xsl:variable name="typeName" select="./oryx:participanttype" />
					
				<xsl:if test="$typeName!=''">
					<participantType>
						<xsl:attribute name="name">
							<xsl:value-of select="$typeName" />
						</xsl:attribute>
					
						<xsl:variable name="processName" select="./oryx:name" />
						<xsl:variable name="processNamespace" select="./oryx:targetnamespace" />
						<xsl:if test="$processName!=''">
							<xsl:attribute name="participantBehaviorDescription">
								<xsl:value-of select="$processName" />
							</xsl:attribute>
							
							<xsl:attribute name="processNamespace">
								<xsl:value-of select="$processNamespace" />
							</xsl:attribute>
						</xsl:if>					
					</participantType>
				</xsl:if>
				
				<xsl:if test="$typeName=''">
					<participantType>
						<xsl:attribute name="name" select="./oryx:name" />
						
						<xsl:variable name="processName" select="./oryx:name" />
						<xsl:variable name="processNamespace" select="./oryx:targetnamespace" />
						<xsl:if test="$processName!=''">
							<xsl:attribute name="participantBehaviorDescription">
								<xsl:value-of select="$processName" />
							</xsl:attribute>
							
							<xsl:attribute name="processNamespace">
								<xsl:value-of select="$processNamespace" />
							</xsl:attribute>
						</xsl:if>					
					</participantType>
				</xsl:if>
			</xsl:if>	
			
			<!--participantTypes saved in participantSet-->
			<xsl:if test="$nodeType='participantSet'">
				<xsl:variable name="typeName" select="./oryx:participanttype" />
				
				<xsl:if test="$typeName!=''">
					<participantType>
						<xsl:attribute name="name">
							<xsl:value-of select="$typeName" />
						</xsl:attribute>			
					</participantType>
				</xsl:if>	
			</xsl:if>	
			
			<!--participantTypes saved in participantRef-->
			<xsl:if test="$nodeType='participant'">
				<xsl:variable name="typeName" select="./oryx:participanttype" />
				
				<xsl:if test="$typeName!=''">
					<participantType>
						<xsl:attribute name="name">
							<xsl:value-of select="$typeName" />
						</xsl:attribute>			
					</participantType>
				
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>	
	

	<xsl:template name="find-all-crossPartnerScopes">
		<xsl:for-each select="//rdf:Description">
			<xsl:variable name="typeString" select="./oryx:type" />	
			<xsl:variable name="nodeType">
				<xsl:call-template name="get-exact-type">
					<xsl:with-param name="typeString" select="$typeString" />
				</xsl:call-template>
			</xsl:variable>
			
			<!--crossPartnerScopes saved in process or scope-->
			<xsl:if test="$nodeType='process' or 'scope'">
				<xsl:variable name="typeName" select="./oryx:crosspartnerscopes" />
				<xsl:variable name="elementName" select="./oryx:name" />
				<xsl:variable name="elementType" select="./oryx:type" />
				<xsl:variable name="elementId" select="(./@rdf:about)" />
			    <xsl:variable name="elementParent" select="(./raziel:parent/@rdf:resource)" />
			    		
				<xsl:if test="$typeName!=''">
					<xsl:call-template name="loop-for-adding-crossPartnerScopes">
							<xsl:with-param name="data-set" select="$typeName" />
							<xsl:with-param name="elementName" select="$elementName" />
							<xsl:with-param name="elementType" select="$nodeType" />
							<xsl:with-param name="elementId" select="$elementId" />
							<xsl:with-param name="elementParent" select="$elementParent" />
					</xsl:call-template>
				</xsl:if>
					
				<xsl:if test="$typeName=''">
					<crossPartnerScopeInfo />
				</xsl:if>
			</xsl:if>	
		</xsl:for-each>
	</xsl:template>


	<xsl:template name="loop-for-adding-crossPartnerScopes">
		<xsl:param name="data-set" />
		<xsl:param name="elementName" />
		<xsl:param name="elementType" />
		<xsl:param name="elementId" />
		<xsl:param name="elementParent" />
		
		<xsl:if test="$data-set != ''">
			<xsl:if test="contains($data-set, ',')">
				<crossPartnerScopeInfo>
					<xsl:attribute name="name">
						<xsl:value-of select="substring-before($data-set, ',')" />
					</xsl:attribute>
					
					<xsl:if test="not($elementName)">
						<xsl:attribute name="elementName">
							<xsl:value-of select="$elementType" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:if test="$elementName!=''">
						<xsl:attribute name="elementName">
							<xsl:value-of select="$elementName" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:attribute name="elementType">
						<xsl:value-of select="$elementType" />
					</xsl:attribute>
					<xsl:attribute name="elementId">
						<xsl:value-of select="$elementId" />
					</xsl:attribute>
					<xsl:attribute name="elementParent">
						<xsl:value-of select="$elementParent" />
					</xsl:attribute>
					<xsl:call-template name="find-children-nodes-of-process">
						<xsl:with-param name="searchedParentID"><xsl:value-of select="$elementId" /></xsl:with-param>
			    	</xsl:call-template>	
				</crossPartnerScopeInfo>
			</xsl:if>
			<xsl:if test="not(contains($data-set, ','))">
				<crossPartnerScopeInfo>
					<xsl:attribute name="name">
						<xsl:value-of select="$data-set" />
					</xsl:attribute>
					
					<xsl:if test="not($elementName)">
						<xsl:attribute name="elementName">
							<xsl:value-of select="$elementType" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:if test="$elementName!=''">
						<xsl:attribute name="elementName">
							<xsl:value-of select="$elementName" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:attribute name="elementType">
						<xsl:value-of select="$elementType" />
					</xsl:attribute>
					<xsl:attribute name="elementId">
						<xsl:value-of select="$elementId" />
					</xsl:attribute>
					<xsl:attribute name="elementParent">
						<xsl:value-of select="$elementParent" />
					</xsl:attribute>
					<xsl:call-template name="find-children-nodes-of-process">
						<xsl:with-param name="searchedParentID"><xsl:value-of select="$elementId" /></xsl:with-param>
			    	</xsl:call-template>
				</crossPartnerScopeInfo>
			</xsl:if>
			<xsl:call-template name="loop-for-adding-crossPartnerScopes">
				<xsl:with-param name="data-set" select="substring-after($data-set, ',')" />
				<xsl:with-param name="elementName" select="$elementName" />
				<xsl:with-param name="elementType" select="$elementType" />
				<xsl:with-param name="elementId" select="$elementId" />
				<xsl:with-param name="elementParent" select="$elementParent" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>


	<xsl:template name="find-children-nodes-of-process">
		<xsl:param name="searchedParentID" />
		
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="currentParentID"><xsl:value-of select="(./raziel:parent/@rdf:resource)" /></xsl:variable>         
			<xsl:if test="$currentParentID = $searchedParentID">
      		  	<xsl:variable name="currentID" select="@rdf:about" />
      		  	<xsl:variable name="currentName" select="./oryx:name" />
				<xsl:variable name="typeString" select="./oryx:type" />	
				<xsl:variable name="nodeType">
					<xsl:call-template name="get-exact-type">
						<xsl:with-param name="typeString" select="$typeString" />
					</xsl:call-template>
				</xsl:variable>
				
				<child>
					<xsl:attribute name="childId">
						<xsl:value-of select="$currentID" />
					</xsl:attribute>
					
					<xsl:if test="$currentName='' or (not($currentName))">
						<xsl:attribute name="childName">
							<xsl:value-of select="$nodeType" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:if test="$currentName!=''">
						<xsl:attribute name="childName">
							<xsl:value-of select="$currentName" />
						</xsl:attribute>
					</xsl:if>

					<xsl:attribute name="childType">
						<xsl:value-of select="$nodeType" />
					</xsl:attribute>
				</child>
				
				<xsl:call-template name="find-children-nodes-of-process">
					<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
			    </xsl:call-template>	
			</xsl:if>
		</xsl:for-each>
	</xsl:template>				
	

	<xsl:template name="find-children-nodes">
		<xsl:param name="searchedParentID" />
		
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="currentParentID"><xsl:value-of select="(./raziel:parent/@rdf:resource)" /></xsl:variable>         
			<xsl:if test="$currentParentID = $searchedParentID">
      		  	<xsl:variable name="currentID" select="@rdf:about" />
				<xsl:variable name="typeString" select="./oryx:type" />	
				<xsl:variable name="nodeType">
					<xsl:call-template name="get-exact-type">
						<xsl:with-param name="typeString" select="$typeString" />
					</xsl:call-template>
				</xsl:variable>
				
				<xsl:if test="$nodeType='invoke' or 'reply' or 'onMessage' or 'receive' or 'onEvent'">
					<sendOrReceiveActivity> 
						
						<xsl:variable name="name"><xsl:value-of select="./oryx:name" /></xsl:variable>

						<!-- extended by changhua Li (to ensure that the activityName is not empty, but onMessage or onEvent)-->						
						<xsl:if test="$name!=''">
							<xsl:attribute name="activityName">
								<xsl:value-of select="$name" />
							</xsl:attribute>
						</xsl:if>
						
						<xsl:if test="$name='' or (not($name))">
							<xsl:attribute name="activityName">
								<xsl:value-of select="$nodeType" />
							</xsl:attribute>
						</xsl:if>
						<!-- extended ende -->
												
						<xsl:attribute name="id">
							<xsl:value-of select="$currentID" />
						</xsl:attribute>
						
						<xsl:if test="$nodeType='invoke' or 'reply'">
							<xsl:call-template name="add-outgoing-elements">
								<xsl:with-param name="researchedTargetType">messageLink</xsl:with-param>
							</xsl:call-template>
						</xsl:if>
						
					</sendOrReceiveActivity>
				</xsl:if>
				
				<xsl:call-template name="find-children-nodes">
					<xsl:with-param name="searchedParentID"><xsl:value-of select="$currentID" /></xsl:with-param>
			    </xsl:call-template>	
			</xsl:if>
		</xsl:for-each>
	</xsl:template>				
				
				
	<xsl:template name="get-exact-type">
		<xsl:param name="typeString" />
		<xsl:value-of select="substring-after($typeString, '#')" />
	</xsl:template>
	

	<xsl:template name="get-number-of-elements-in-complex-type">
		<xsl:param name="original_content" />
		<xsl:value-of select="substring-before(substring-after($original_content, 'totalCount%27%3A'), '%2C%20%27items') " />
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
	

	<xsl:template name="record-association-edge-infos">
        <xsl:for-each select="//rdf:Description">
			<xsl:variable name="typeString" select="./oryx:type" />	
			<xsl:variable name="type">
				<xsl:call-template name="get-exact-type">
					<xsl:with-param name="typeString" select="$typeString" />
				</xsl:call-template>
			</xsl:variable>
			
			<!--associationEdge-->
			<xsl:if test="$type='directedAssociation'">
				<associationEdge>
					<xsl:variable name="currentID" select="@rdf:about" />
					<xsl:if test="$currentID!=''">
						<xsl:attribute name="id">
							<xsl:value-of select="$currentID" />
						</xsl:attribute>
					</xsl:if>
					
					<xsl:call-template name="add-outgoing-elements">
						<xsl:with-param name="researchedTargetType">all</xsl:with-param>
					</xsl:call-template>
				</associationEdge>
			</xsl:if>	
		</xsl:for-each>
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
				<xsl:variable name="participantType" select="./oryx:participanttype" />
				<xsl:variable name="processName" select="./oryx:name" />
					
				<process>
					<xsl:if test="$processID!=''">
						<xsl:attribute name="id">
							<xsl:value-of select="$processID" />
						</xsl:attribute>
					</xsl:if>		
					
					<xsl:if test="$participantType!=''">
						<xsl:attribute name="participantType">
							<xsl:value-of select="$participantType" />
						</xsl:attribute>
					</xsl:if>
					
					<!-- extended by Changhua Li (create the children elements of process)-->
					<xsl:if test="$processName!=''">
						<xsl:attribute name="name">
							<xsl:value-of select="$processName" />
						</xsl:attribute>
					</xsl:if>		
					
					<xsl:call-template name="find-children-nodes-of-process">
						<xsl:with-param name="searchedParentID"><xsl:value-of select="$processID" /></xsl:with-param>
			    	</xsl:call-template>
			    	<!-- extended end -->
			    	
				</process>	
				
				<xsl:call-template name="find-children-nodes">
					<xsl:with-param name="searchedParentID"><xsl:value-of select="$processID" /></xsl:with-param>
			    </xsl:call-template>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>