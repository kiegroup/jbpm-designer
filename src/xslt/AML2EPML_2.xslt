<?xml version="1.0"?>
	
	<!-- Copyright Jan Mendling, 2003, 2004, 2005 -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:epml="http://www.epml.de" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.0">
    <xsl:output method="xml" indent="yes"/>
    <xsl:variable name="ModelTable">
        <xsl:for-each select="//*[name()='Model'][@Model.Type='MT_EEPC']">
            <xsl:value-of select="@Model.ID"/><xsl:value-of select="concat(' ',position(),' ')"/>
        </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="ObjDefTable">
        <xsl:for-each select="//*[name()='ObjDef']">
            <xsl:value-of select="@ObjDef.ID"/><xsl:value-of select="concat(' ',position(),' ')"/>
        </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="ObjOccTable">
        <xsl:for-each select="//*[name()='ObjOcc']">
            <xsl:value-of select="@ObjOcc.ID"/><xsl:value-of select="concat(' ',position(),' ')"/>
        </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="CxnOccFirst">
    	<xsl:value-of select="count(//*[name()='ObjOcc'])+1"/>
    </xsl:variable>
    <xsl:variable name="CxnOccTable">
        <xsl:for-each select="//*[name()='CxnOcc']">
            <xsl:value-of select="@CxnOcc.ID"/><xsl:value-of select="concat(' ',position(),' ')"/>
        </xsl:for-each>
    </xsl:variable>
		
    <xsl:template match="/">
        <xsl:element name="epml:epml">
	     <xsl:copy-of select=".//namespace::epml[.='http://www.epml.de#']"/>
	     <xsl:copy-of select=".//namespace::xsi[.='http://www.w3.org/2001/XMLSchema-instance#']"/>
	     <xsl:attribute name="xsi:schemaLocation">http://www.epml.de EPML_111_draft.XSD</xsl:attribute>
	     <xsl:element name="coordinates">
	     		<xsl:attribute name="xOrigin">leftToRight</xsl:attribute>
	     		<xsl:attribute name="yOrigin">topToBottom</xsl:attribute>
	     </xsl:element>
            <definitions>
				<xsl:for-each select="//*[name()='ObjDef']">
					<xsl:element name="definition">
						<xsl:attribute name="defId"><xsl:value-of select="position()"/></xsl:attribute>
					</xsl:element>
				</xsl:for-each>
            </definitions>
            <attributeTypes>
			    <xsl:if test="//GUID">
					<xsl:element name="attributeType">
						<xsl:attribute name="typeId">GUID</xsl:attribute>
					</xsl:element>
			    </xsl:if>
				<xsl:element name="attributeType">
					<xsl:attribute name="typeId">OT</xsl:attribute>
				</xsl:element>
				<xsl:element name="attributeType">
					<xsl:attribute name="typeId">ST</xsl:attribute>
				</xsl:element>
				<xsl:call-template name="distinct"/>
            </attributeTypes>
            <directory name="Root">
                <xsl:apply-templates select="/*/*[name()='Group'][@Group.ID='Group.Root']/*[name()='Model'][@Model.Type='MT_EEPC']" mode="Epc"/>
                <xsl:apply-templates select="/*/*[name()='Group'][@Group.ID='Group.Root']/*[name()='Group']" mode="Dir"/>
            </directory>
        </xsl:element>
    </xsl:template>
        
    <xsl:template match="*" mode="Dir">
        <xsl:element name="directory">
            <xsl:attribute name="name">
				<xsl:value-of select="./*[name()='AttrDef']/*[name()='AttrValue'][../@AttrDef.Type='AT_NAME']"/>
			</xsl:attribute>
        <xsl:apply-templates select="./*[name()='Model'][@Model.Type='MT_EEPC']" mode="Epc"/>
        <xsl:apply-templates select="./*[name()='Group']" mode="Dir"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="*" mode="Epc">
        <xsl:element name="epc">
            <xsl:attribute name="epcId">
                <xsl:call-template name="Count">
                    <xsl:with-param name="Item" select="'Model'"/>
                    <xsl:with-param name="Id" select="@Model.ID"/>
                </xsl:call-template>
            </xsl:attribute>
            <xsl:attribute name="name">
				<xsl:value-of select="./*[name()='AttrDef']/*[name()='AttrValue'][../@AttrDef.Type='AT_NAME']"/>
			</xsl:attribute>
			<xsl:for-each select="AttrDef[@AttrDef.Type!='AT_NAME']">
				<xsl:element name="attribute">
					<xsl:attribute name="typeRef"><xsl:value-of select="@AttrDef.Type"/></xsl:attribute>	
					<xsl:attribute name="value"><xsl:value-of select="AttrValue"/></xsl:attribute>	
				</xsl:element>	
			</xsl:for-each>
			<xsl:if test="./GUID">
				<xsl:element name="attribute">
					<xsl:attribute name="typeRef">GUID</xsl:attribute>	
					<xsl:attribute name="value"><xsl:value-of select="./GUID"/></xsl:attribute>
				</xsl:element>	
			</xsl:if>					
            <xsl:apply-templates select="./*[name()='ObjOcc']" mode="Elements"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="*" mode="Elements">
        <xsl:variable name="ObjDefID" select="@ObjDef.IdRef"/>
        <xsl:variable name="epmlDefRef">
			<xsl:call-template name="Count">
				<xsl:with-param name="Item" select="'ObjDef'"/>
				<xsl:with-param name="Id" select="$ObjDefID"/>
			</xsl:call-template>
		</xsl:variable>
        <xsl:variable name="OT" select="/*[name()='AML']/*[name()='Group']//*[name()='ObjDef'][@ObjDef.ID=$ObjDefID]/@TypeNum"/>
        <xsl:variable name="Name" 
			select="/*[name()='AML']/*[name()='Group']//*[name()='ObjDef'][@ObjDef.ID=$ObjDefID]/*[name()='AttrDef'][@AttrDef.Type='AT_NAME']/*"/>
        <xsl:variable name="ST" select="@SymbolNum"/>
        <xsl:variable name="epmlId">
            <xsl:call-template name="Count">
                <xsl:with-param name="Item" select="'ObjOcc'"/>
                <xsl:with-param name="Id" select="@ObjOcc.ID"/>
            </xsl:call-template>
		</xsl:variable>
        <xsl:variable name="Links" select="/*[name()='AML']/*[name()='Group']//*[name()='ObjDef'][@ObjDef.ID=$ObjDefID]/@LinkedModels.IdRefs"/>
        <xsl:variable name="DefRef">
            <xsl:call-template name="Count">
                <xsl:with-param name="Item" select="'ObjDef'"/>
                <xsl:with-param name="Id" select="$ObjDefID"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="ExplTyp">
            <xsl:call-template name="ExplTyp">
                <xsl:with-param name="OT" select="$OT"/>
                <xsl:with-param name="ST" select="$ST"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="ImplTyp">
            <!--xsl:call-template name="ImplTyp">
                <xsl:with-param name="OccId" select="@ObjOcc.ID"/>
                <xsl:with-param name="Expl" select="$ExplTyp"/>
            </xsl:call-template-->
        </xsl:variable>
        
        <xsl:element name="{$ExplTyp}">
            <!--xsl:if test="$ExplTyp!='AND' and $ExplTyp!='OR' and $ExplTyp!='XOR'">
                <xsl:attribute name="DefRef"><xsl:value-of select="$DefRef"/></xsl:attribute>
            </xsl:if-->
            <xsl:attribute name="id"><xsl:value-of select="$epmlId"/></xsl:attribute>
			<xsl:attribute name="defRef"><xsl:value-of select="$epmlDefRef"/></xsl:attribute>
            <xsl:if test="string-length($Name) > 1">
	            <xsl:element name="name"><xsl:value-of select="$Name"/></xsl:element>
	            <xsl:element name="description"><xsl:value-of select="$Name"/></xsl:element>
            </xsl:if>
            <xsl:element name="graphics">
                <xsl:element name="position">
	                <xsl:attribute name="x"><xsl:value-of select="./*[name()='Position']/@Pos.X"/></xsl:attribute>
	                <xsl:attribute name="y"><xsl:value-of select="./*[name()='Position']/@Pos.Y"/></xsl:attribute>
	                <xsl:choose>
	                	<xsl:when test="./*[name()='Size']/@Size.dX > 1">
	                		<xsl:attribute name="width"><xsl:value-of select="./*[name()='Size']/@Size.dX"/></xsl:attribute>
	                	</xsl:when>
	                	<xsl:otherwise>
	                		<xsl:attribute name="width">250</xsl:attribute>
	                	</xsl:otherwise>
	                </xsl:choose>
	                <xsl:choose>
	                	<xsl:when test="./*[name()='Size']/@Size.dX > 1">
	                		<xsl:attribute name="height"><xsl:value-of select="./*[name()='Size']/@Size.dY"/></xsl:attribute>
	                	</xsl:when>
	                	<xsl:otherwise>
	                		<xsl:attribute name="height">156</xsl:attribute>
	                	</xsl:otherwise>
	                </xsl:choose>
                </xsl:element>
            </xsl:element>
            <xsl:if test="/*[name()='AML']/*[name()='Group']//*[name()='ObjDef'][@ObjDef.ID=$ObjDefID]/@LinkedModels.IdRefs">
                <xsl:call-template name="Links">
                    <xsl:with-param name="string" select="concat($Links,' ')"/>
                </xsl:call-template>
            </xsl:if>
			<xsl:for-each select="//*[@ObjDef.ID=$ObjDefID]/AttrDef[@AttrDef.Type!='AT_NAME']">
				<xsl:element name="attribute">
					<xsl:attribute name="typeRef"><xsl:value-of select="@AttrDef.Type"/></xsl:attribute>	
					<xsl:attribute name="value"><xsl:value-of select="AttrValue"/></xsl:attribute>	
				</xsl:element>	
			</xsl:for-each>	
			<xsl:if test="//*[@ObjDef.ID=$ObjDefID]/GUID">
				<xsl:element name="attribute">
					<xsl:attribute name="typeRef">GUID</xsl:attribute>	
					<xsl:attribute name="value"><xsl:value-of select="//*[@ObjDef.ID=$ObjDefID]/GUID"/></xsl:attribute>	
				</xsl:element>	
			</xsl:if>	
			<xsl:element name="attribute">
				<xsl:attribute name="typeRef">OT</xsl:attribute>	
				<xsl:attribute name="value"><xsl:value-of select="$OT"/></xsl:attribute>	
			</xsl:element>	
			<xsl:element name="attribute">
				<xsl:attribute name="typeRef">ST</xsl:attribute>	
				<xsl:attribute name="value"><xsl:value-of select="$ST"/></xsl:attribute>	
			</xsl:element>	
        </xsl:element>
        <xsl:for-each select="./*[name()='CxnOcc']">
			<xsl:variable name="ToOccId" select="@ToObjOcc.IdRef"/>
			<xsl:variable name="ToDefId" select="/*[name()='AML']/*[name()='Group']//*[name()='ObjOcc'][@ObjOcc.ID=$ToOccId]/@ObjDef.IdRef"/>
			<xsl:variable name="ToOT" select="/*[name()='AML']/*[name()='Group']//*[name()='ObjDef'][@ObjDef.ID=$ToDefId]/@TypeNum"/>	
			<xsl:choose>
				<xsl:when test="($OT='OT_FUNC' or $OT='OT_EVT' or $OT='OT_RULE') and ($ToOT='OT_FUNC' or $ToOT='OT_EVT' or $ToOT='OT_RULE')">
					<xsl:apply-templates select="." mode="Arcs">
						<xsl:with-param name="Impl" select="$ImplTyp"/>
						<xsl:with-param name="Expl" select="$ExplTyp"/>
						<xsl:with-param name="FromId" select="$epmlId"/>
					</xsl:apply-templates>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="." mode="Relations">
						<xsl:with-param name="Impl" select="$ImplTyp"/>
						<xsl:with-param name="Expl" select="$ExplTyp"/>
						<xsl:with-param name="FromId" select="$epmlId"/>
					</xsl:apply-templates>
				</xsl:otherwise>
			</xsl:choose>	
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="*" mode="Arcs">
        <xsl:param name="Impl"/>
        <xsl:param name="Expl"/>
        <xsl:param name="FromId"/>
        <xsl:variable name="ToOccId" select="@ToObjOcc.IdRef"/>
        <xsl:variable name="CxnOccId" select="@CxnOcc.ID"/>
        <xsl:variable name="CxnDefId" select="@CxnDef.IdRef"/>
		<xsl:variable name="CxnDefType" select="/*[name()='AML']/*[name()='Group']//*[name()='ObjDef']/*[name()='CxnDef'][@CxnDef.ID=$CxnDefId]/@CxnDef.Type"/>		
        <xsl:variable name="ToId">
            <xsl:call-template name="Count">
                <xsl:with-param name="Item" select="'ObjOcc'"/>
                <xsl:with-param name="Id" select="$ToOccId"/>
            </xsl:call-template>
		</xsl:variable>
        <xsl:element name="arc">
            <xsl:attribute name="id">
	            <xsl:call-template name="Count">
	                <xsl:with-param name="Item" select="'CxnOcc'"/>
	                <xsl:with-param name="Id" select="$CxnOccId"/>
	            </xsl:call-template>
	     </xsl:attribute>
            <xsl:element name="flow">
	            <xsl:attribute name="source"><xsl:value-of select="$FromId"/></xsl:attribute>
	            <xsl:attribute name="target"><xsl:value-of select="$ToId"/></xsl:attribute>
            </xsl:element>
            <xsl:element name="graphics">
	            <xsl:for-each select="./*[name()='Position']">
	                <xsl:element name="position">
	                    <xsl:attribute name="x"><xsl:value-of select="@Pos.X"/></xsl:attribute>
	                    <xsl:attribute name="y"><xsl:value-of select="@Pos.Y"/></xsl:attribute>
	                </xsl:element>
	            </xsl:for-each>
            </xsl:element>
 			<xsl:if test="/*[name()='AML']/*[name()='Group']//*[@CxnDef.ID=$CxnDefId]/*[name()='GUID']">
				<xsl:element name="attribute">
					<xsl:attribute name="typeRef">GUID</xsl:attribute>	
					<xsl:attribute name="value"><xsl:value-of select="/*[name()='AML']/*[name()='Group']//*[@CxnDef.ID=$CxnDefId]/*[name()='GUID']"/></xsl:attribute>	
				</xsl:element>	
			</xsl:if>	
			<xsl:element name="attribute">
					<xsl:attribute name="typeRef">OT</xsl:attribute>	
					<xsl:attribute name="value"><xsl:value-of select="$CxnDefType"/></xsl:attribute>	
			</xsl:element>
			<xsl:for-each select="/*[name()='AML']/*[name()='Group']//*[@CxnDef.ID=$CxnDefId]/*[name()='AttrDef'][@AttrDef.Type!='AT_NAME']">
				<xsl:element name="attribute">
					<xsl:attribute name="typeRef"><xsl:value-of select="@AttrDef.Type"/></xsl:attribute>	
					<xsl:attribute name="value"><xsl:value-of select="AttrValue"/></xsl:attribute>	
				</xsl:element>	
			</xsl:for-each>	
        </xsl:element>
    </xsl:template>
 
     <xsl:template match="*" mode="Relations">
        <xsl:param name="Impl"/>
        <xsl:param name="Expl"/>
        <xsl:param name="FromId"/>
        <xsl:variable name="ToOccId" select="@ToObjOcc.IdRef"/>
        <xsl:variable name="CxnOccId" select="@CxnOcc.ID"/>
        <xsl:variable name="CxnDefId" select="@CxnDef.IdRef"/>
		<xsl:variable name="CxnDefType" select="/*[name()='AML']/*[name()='Group']//*[name()='ObjDef']/*[name()='CxnDef'][@CxnDef.ID=$CxnDefId]/@CxnDef.Type"/>		
        <xsl:variable name="ToId">
            <xsl:call-template name="Count">
                <xsl:with-param name="Item" select="'ObjOcc'"/>
                <xsl:with-param name="Id" select="$ToOccId"/>
            </xsl:call-template>
		</xsl:variable>
        <xsl:element name="relation">
            <xsl:attribute name="id">
	            <xsl:call-template name="Count">
	                <xsl:with-param name="Item" select="'CxnOcc'"/>
	                <xsl:with-param name="Id" select="$CxnOccId"/>
	            </xsl:call-template>
			</xsl:attribute>
			<xsl:attribute name="from"><xsl:value-of select="$FromId"/></xsl:attribute>
			<xsl:attribute name="to"><xsl:value-of select="$ToId"/></xsl:attribute>
            <xsl:element name="graphics">
	            <xsl:for-each select="./*[name()='Position']">
	                <xsl:element name="position">
	                    <xsl:attribute name="x"><xsl:value-of select="@Pos.X"/></xsl:attribute>
	                    <xsl:attribute name="y"><xsl:value-of select="@Pos.Y"/></xsl:attribute>
	                </xsl:element>
	            </xsl:for-each>
            </xsl:element>
 			<xsl:if test="/*[name()='AML']/*[name()='Group']//*[@CxnDef.ID=$CxnDefId]/*[name()='GUID']">
				<xsl:element name="attribute">
					<xsl:attribute name="typeRef">GUID</xsl:attribute>	
					<xsl:attribute name="value"><xsl:value-of select="/*[name()='AML']/*[name()='Group']//*[@CxnDef.ID=$CxnDefId]/*[name()='GUID']"/></xsl:attribute>	
				</xsl:element>	
			</xsl:if>	
			<xsl:element name="attribute">
					<xsl:attribute name="typeRef">OT</xsl:attribute>	
					<xsl:attribute name="value"><xsl:value-of select="$CxnDefType"/></xsl:attribute>	
			</xsl:element>
			<xsl:for-each select="/*[name()='AML']/*[name()='Group']//*[@CxnDef.ID=$CxnDefId]/*[name()='AttrDef'][@AttrDef.Type!='AT_NAME']">
				<xsl:element name="attribute">
					<xsl:attribute name="typeRef"><xsl:value-of select="@AttrDef.Type"/></xsl:attribute>	
					<xsl:attribute name="value"><xsl:value-of select="AttrValue"/></xsl:attribute>	
				</xsl:element>	
			</xsl:for-each>	
        </xsl:element>
    </xsl:template>
   
    <xsl:template name="Count">
        <xsl:param name="Item"/>
        <xsl:param name="Id"/>
        <xsl:if test="$Item='Model'">
            <xsl:value-of select="substring-before(substring-after(substring-after($ModelTable,$Id),' '),' ')"/>
        </xsl:if>
        <xsl:if test="$Item='ObjDef'">
            <xsl:value-of select="substring-before(substring-after(substring-after($ObjDefTable,$Id),' '),' ')"/>
        </xsl:if>
        <xsl:if test="$Item='ObjOcc'">
            <xsl:value-of select="substring-before(substring-after(substring-after($ObjOccTable,$Id),' '),' ')"/>
        </xsl:if>
        <xsl:if test="$Item='CxnOcc'">
            <xsl:value-of select="$CxnOccFirst + number(substring-before(substring-after(substring-after($CxnOccTable,$Id),' '),' ') )"/>
        </xsl:if>
        </xsl:template>
    
    <xsl:template name="ExplTyp">
        <xsl:param name="OT"/>
        <xsl:param name="ST"/>
        <xsl:choose>
            <xsl:when test="$ST='ST_OPR_XOR_1'">xor</xsl:when>
            <xsl:when test="$ST='ST_OPR_AND_1'">and</xsl:when>
            <xsl:when test="$ST='ST_OPR_OR_1'">or</xsl:when>
            <xsl:when test="$OT='OT_FUNC'">function</xsl:when>
            <xsl:when test="$OT='OT_EVT'">event</xsl:when>
            <xsl:when test="$ST='ST_PRCS_IF'">processInterface</xsl:when>
            <xsl:when test="contains($OT,'_ORG') or contains($OT,'_PERS') or contains($OT,'_EMPL')">participant</xsl:when>
            <xsl:when test="contains($OT,'_APP') or contains($OT,'_CMP') or contains($OT,'_MOD') or contains($OT,'_PACK')">application</xsl:when>
            <xsl:when test="contains($OT,'_CLS') or contains($OT,'_INFO') or contains($OT,'_KPI') or contains($OT,'_LST') or contains($OT,'_OBJ') or contains($OT,'_TERM')">application</xsl:when>
            <xsl:otherwise>HELPMEPLEASE</xsl:otherwise>
        </xsl:choose>    
    </xsl:template>
    

    <xsl:template name="ArcType">
        <xsl:param name="Expl"/>
        <xsl:param name="OccId"/>
        <xsl:variable name="ToId" select="//*[@ObjOcc.ID=$OccId]//@ToObjOcc.IdRef"/>
        <xsl:variable name="NextST" select="//*[@ObjOcc.ID=$ToId]/@SymbolNum"/>
        <xsl:choose>
            <xsl:when test="$Expl='Function'">functionEventArc</xsl:when>
            <xsl:when test="$Expl='ProcessIF'">functionEventArc</xsl:when>
            <xsl:when test="$Expl='Event'">eventFunctionArc</xsl:when>
            <xsl:when test="$Expl='AND' or $Expl='OR' or $Expl='XOR'">
                <xsl:call-template name="TransitiveArc">
                    <xsl:with-param name="OccId" select="$ToId"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>UnknownArc</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="TransitiveArc">
        <xsl:param name="OccId"/>
        <xsl:variable name="NextId" select="//*[@ObjOcc.ID=$OccId]//@ToObjOcc.IdRef[1]"/>
        <xsl:variable name="OccDefId" select="//*[@ObjOcc.ID=$OccId]//@ObjDef.IdRef"/>
        <xsl:variable name="OccOT" select="/*[name()='AML']/*[name()='Group']//*[name()='ObjDef'][@ObjDef.ID=$OccDefId]/@TypeNum"/>
        <xsl:choose>
            <xsl:when test="$OccOT='OT_FUNC'">eventFunctionArc</xsl:when>
            <xsl:when test="$OccOT='OT_EVT'">functionEventArc</xsl:when>
            <xsl:when test="$OccOT='OT_RULE' and //*[@ObjOcc.ID=$OccId]//@ToObjOcc.IdRef[1]">
                <xsl:call-template name="TransitiveArc">
                    <xsl:with-param name="OccId" select="$NextId"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>UnknownArcRecursive <xsl:value-of select="$OccId"/></xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="Links" match="*" mode="hinterlegung">
            <xsl:param name="string"/>
            <xsl:param name="delimiter" select="' '"/>
            <!-- Rekursives Aufsplitten der Hinterlegungsliste -->
            <xsl:choose>
                    <xsl:when test="2 > string-length($string)"/>
                    <xsl:otherwise>
                            <xsl:variable name="teilstring" select="substring-before($string,$delimiter)"/>
                            <xsl:if test="contains($ModelTable,$teilstring)">
                                <xsl:element name="toProcess">
                                    <xsl:attribute name="linkToEpcId"><xsl:value-of select="substring-before(substring-after(substring-after($ModelTable,$teilstring),' '),' ')"/></xsl:attribute>
                                </xsl:element>
                            </xsl:if>
                            <xsl:call-template name="Links">
                                    <xsl:with-param name="string" select="substring-after($string,$delimiter)"/>
                            </xsl:call-template>
                    </xsl:otherwise>
            </xsl:choose>
    </xsl:template>
    
    <xsl:template name="distinct">
		<xsl:param name="processed" select="'AT_NAME'"/>	
		<xsl:variable name="current" select="//@AttrDef.Type[not(contains(concat(' ',$processed,' '),.))][1]"/>
		<xsl:if test="string-length($current) > 0">
			<xsl:element name="attributeType">
				<xsl:attribute name="typeId"><xsl:value-of select="$current"/>
				</xsl:attribute>	
			</xsl:element>	
			<xsl:call-template name="distinct">
				<xsl:with-param name="processed" select="concat(' ',$current,' ',$processed,' ')"/>	
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
    
</xsl:stylesheet>
    
