<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes"/>
        
    <xsl:template match="/">
        <AML>
            <Header-Info CreateTime="00:00:00.000" CreateDate="01-01-2005" DatabaseName="EPML" UserName="system" ArisExeVersion="62"/>
            <Language LocaleId="1031" Codepage="1252">
                <LanguageName>German</LanguageName>
                <LogFont FaceName="Arial" Height="-13" Width="0" Escapement="0" Orientation="0" Weight="400" Italic="NO" Underline="NO" StrikeOut="NO" CharSet="0" OutPrecision="0" ClipPrecision="0" Quality="0" PitchAndFamily="0" Color="0"/>
            </Language>
            <Database>
                <AttrDef AttrDef.Type="AT_NAME_LGINDEP">
                    <AttrValue LocaleId="1031">EPML</AttrValue>
                </AttrDef>
                <AttrDef AttrDef.Type="AT_CREATOR">
                    <AttrValue LocaleId="1031">EPML</AttrValue>
                </AttrDef>
                <AttrDef AttrDef.Type="AT_CREAT_TIME_STMP">
                    <AttrValue LocaleId="1031">00:00:00.000;01/01/2005</AttrValue>
                </AttrDef>
            </Database>
            <FontStyleSheet FontSS.ID="FontSS.11----4-----c--">
                <AttrDef AttrDef.Type="AT_NAME">
                    <AttrValue LocaleId="1031">Standard</AttrValue>
                </AttrDef>
                <FontNode LocaleId="1031" FaceName="Arial" Height="-13" Width="0" Escapement="0" Orientation="0" Weight="400" Italic="NO" Underline="NO" StrikeOut="NO" CharSet="0" OutPrecision="0" ClipPrecision="0" Quality="0" PitchAndFamily="0" Color="0"/>
            </FontStyleSheet>       
            <Group Group.ID="Group.Root">
                <xsl:apply-templates select="/*/*[name()='directory']/*[name()='directory']" mode="Dir"/>
                <xsl:apply-templates select="/*/*[name()='directory']/*[name()='epc']" mode="Epc"/>
                <xsl:apply-templates select="/*/*[name()='definitions']/*[name()='definition']" mode="ObjDef"/>
            </Group>
        </AML>
    </xsl:template>
        
    <xsl:template match="*" mode="Dir">
        <xsl:element name="directory">
            <AttrDef AttrDef.Type="AT_NAME">
                <AttrValue LocaleId="1031"><xsl:value-of select="@name"/></AttrValue>
            </AttrDef>
            <xsl:apply-templates select="*[name()='directory']" mode="Dir"/>
            <xsl:apply-templates select="*[name()='epc']" mode="Epc"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="*" mode="Epc">
        <xsl:element name="Model">
            <xsl:attribute name="Model.ID">Model.<xsl:value-of select="300000+@epcId"/>4-----u--</xsl:attribute>
            <xsl:attribute name="Model.Type">MT_EEPC</xsl:attribute>
            <xsl:if test="./*[name()='attribute'][@typeRef='GUID']">
                <xsl:element name="GUID"><xsl:value-of select="./*[name()='attribute'][@typeRef='GUID']/@value"/></xsl:element>
            </xsl:if>
            <xsl:element name="AttrDef">
                <xsl:attribute name="AttrDef.Type">AT_NAME</xsl:attribute>
                <xsl:element name="AttrValue">
                    <xsl:attribute name="LocaleId">1031</xsl:attribute>
                    <xsl:value-of select="@name"/>                  
                </xsl:element>
            </xsl:element>
            <xsl:apply-templates select="*[name()='attribute'][@typeRef!='GUID']" mode="AttrDef"/>
            <xsl:apply-templates select="*[name()!='attribute'][name()!='arc'][name()!='relation']" mode="Elements"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="*" mode="AttrDef">
        <xsl:element name="AttrDef">
            <xsl:attribute name="AttrDef.Type">
                <xsl:value-of select="@typeRef"/>
            </xsl:attribute>
            <xsl:element name="AttrValue">
                <xsl:attribute name="LocaleId">1031</xsl:attribute>
                <xsl:value-of select="@value"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="*" mode="Elements">
        <xsl:variable name="elementid"><xsl:value-of select="@id"/></xsl:variable>
        <xsl:element name="ObjOcc">
            <xsl:attribute name="ObjOcc.ID">ObjOcc.<xsl:value-of select="100000+@id"/>4-----x--</xsl:attribute>
            <xsl:attribute name="ObjDef.IdRef">ObjDef.<xsl:value-of select="200000+@defRef"/>4-----p--</xsl:attribute>
            <xsl:if test="../*[name()='arc'][./*[name()='flow']/@source=$elementid]|../*[name()='relation'][@from=$elementid]">
                <xsl:attribute name="ToCxnOccs.IdRefs">
                    <xsl:for-each select="../*[name()='arc'][./*[name()='flow']/@source=$elementid]">CxnOcc.<xsl:value-of select="100000+@id"/>4-----y-- </xsl:for-each>
                    <xsl:for-each select="../*[name()='relation'][@from=$elementid]">CxnOcc.<xsl:value-of select="100000+@id"/>4-----y-- </xsl:for-each>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="Active">YES</xsl:attribute>
            <xsl:attribute name="Shadow">NO</xsl:attribute>
            <xsl:attribute name="Visible">YES</xsl:attribute>
            <xsl:attribute name="Zorder"><xsl:value-of select="@id"/></xsl:attribute>
            <xsl:attribute name="SymbolNum">
                <xsl:choose>
                    <xsl:when test="./*[name()='attribute']/@*[@typeRef='ST']"><xsl:value-of select="./*[name()='attribute']/@value[../@typeRef='ST']"/></xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="name()='event'">ST_EV</xsl:when>
                            <xsl:when test="name()='function'">ST_FUNC</xsl:when>
                            <xsl:when test="name()='processInterface'">ST_PRCS_IF</xsl:when>
                            <xsl:when test="name()='and'">ST_OPR_AND_1</xsl:when>
                            <xsl:when test="name()='or'">ST_OPR_OR_1</xsl:when>
                            <xsl:when test="name()='xor'">ST_OPR_XOR_1</xsl:when>
                            <xsl:when test="name()='application'">ST_APPL_SYS</xsl:when>
                            <xsl:when test="name()='participant'">ST_ORG_UNIT_TYPE_1</xsl:when>
                            <xsl:when test="name()='dataField'">ST_BUSY_OBJ</xsl:when>
                            <xsl:otherwise>HELPMEPLEASE</xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:if test="./*[name()='graphics']/*[name()='position']">
                <xsl:element name="Position">
                    <xsl:attribute name="Pos.X"><xsl:value-of select="./*[name()='graphics']/*[name()='position']/@x"/></xsl:attribute>
                    <xsl:attribute name="Pos.Y"><xsl:value-of select="./*[name()='graphics']/*[name()='position']/@y"/></xsl:attribute>
                </xsl:element>
                <xsl:element name="Size">
                    <xsl:attribute name="Size.dX"><xsl:value-of select="./*[name()='graphics']/*[name()='position']/@width"/></xsl:attribute>
                    <xsl:attribute name="Size.dY"><xsl:value-of select="./*[name()='graphics']/*[name()='position']/@height"/></xsl:attribute>
                </xsl:element>
            </xsl:if>
            <xsl:for-each select="../*[name()='arc'][./*[name()='flow']/@source=$elementid]">
                <xsl:element name="CxnOcc">
                    <xsl:attribute name="CxnOcc.ID">CxnOcc.<xsl:value-of select="100000+@id"/>4-----y--</xsl:attribute>
                    <xsl:attribute name="CxnDef.IdRef">CxnDef.<xsl:value-of select="400000+@id"/>4-----q--</xsl:attribute>
                    <xsl:attribute name="ToObjOcc.IdRef">ObjOcc.<xsl:value-of select="100000+(./*[name()='flow']/@target)"/>4-----x--</xsl:attribute>
                    <xsl:attribute name="Active">YES</xsl:attribute>
                    <xsl:attribute name="Diagonal">NO</xsl:attribute>
                    <xsl:attribute name="Visible">YES</xsl:attribute>
                    <xsl:attribute name="Zorder"><xsl:value-of select="@id"/></xsl:attribute>
                    <xsl:element name="Pen">
                        <xsl:attribute name="Color">0</xsl:attribute>
                        <xsl:attribute name="Style">2</xsl:attribute>
                        <xsl:attribute name="Width">1</xsl:attribute>
                    </xsl:element>
                    <xsl:for-each select=".//*[name()='position']">
                        <xsl:element name="Position">
                            <xsl:attribute name="Pos.X"><xsl:value-of select="@x"/></xsl:attribute>
                            <xsl:attribute name="Pos.Y"><xsl:value-of select="@y"/></xsl:attribute>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:element>
            </xsl:for-each>
            <xsl:for-each select="../*[name()='relation'][@from=$elementid]">
                <xsl:element name="CxnOcc">
                    <xsl:attribute name="CxnOcc.ID">CxnOcc.<xsl:value-of select="100000+@id"/>4-----y--</xsl:attribute>
                    <xsl:attribute name="CxnDef.IdRef">CxnDef.<xsl:value-of select="400000+@id"/>4-----q--</xsl:attribute>
                    <xsl:attribute name="ToObjOcc.IdRef">ObjOcc.<xsl:value-of select="100000+@to"/>4-----x--</xsl:attribute>
                    <xsl:attribute name="Active">YES</xsl:attribute>
                    <xsl:attribute name="Diagonal">NO</xsl:attribute>
                    <xsl:attribute name="Visible">YES</xsl:attribute>
                    <xsl:attribute name="Zorder"><xsl:value-of select="@id"/></xsl:attribute>
                    <xsl:element name="Pen">
                        <xsl:attribute name="Color">0</xsl:attribute>
                        <xsl:attribute name="Style">2</xsl:attribute>
                        <xsl:attribute name="Width">1</xsl:attribute>
                        <xsl:for-each select=".//*[name()='position']">
                            <xsl:element name="Position">
                                <xsl:attribute name="Pos.X"><xsl:value-of select="@x"/></xsl:attribute>
                                <xsl:attribute name="Pos.Y"><xsl:value-of select="@y"/></xsl:attribute>
                            </xsl:element>
                        </xsl:for-each>
                    </xsl:element>
                </xsl:element>
            </xsl:for-each>
            <xsl:element name="AttrOcc">
                <xsl:attribute name="AttrTypeNum">AT_NAME</xsl:attribute>
                <xsl:attribute name="Port">CENTER</xsl:attribute>
                <xsl:attribute name="OrderNum">0</xsl:attribute>
                <xsl:attribute name="Alignment">CENTER</xsl:attribute>
                <xsl:attribute name="SymbolFlag">TEXT</xsl:attribute>
                <xsl:attribute name="FontSS.IdRef">FontSS.11----4-----c--</xsl:attribute>
                <xsl:attribute name="OffsetX">0</xsl:attribute>
                <xsl:attribute name="OffsetY">0</xsl:attribute>
            </xsl:element>
            <!--xsl:for-each select="./*[name()='attribute']">
                <xsl:element name="AttrOcc">
                    <xsl:attribute name="AttrTypeNum"><xsl:value-of select="@typeRef"/></xsl:attribute>
                    <xsl:attribute name="Port">CENTER</xsl:attribute>
                    <xsl:attribute name="OrderNum">0</xsl:attribute>
                    <xsl:attribute name="Alignment">CENTER</xsl:attribute>
                    <xsl:attribute name="SymbolFlag">TEXT</xsl:attribute>
                    <xsl:attribute name="FontSS.IdRef">FontSS.11</xsl:attribute>
                    <xsl:attribute name="OffsetX">0</xsl:attribute>
                    <xsl:attribute name="OffsetY">0</xsl:attribute>
                </xsl:element>
            </xsl:for-each-->
        </xsl:element>
    </xsl:template>
    
    <!-- EPC id muss ebenfalls berücksichtigt werden -->
    <xsl:template match="*" mode="ObjDef">
        <xsl:variable name="defid" select="@defId"/>
        <xsl:variable name="elementid" select="//@id[../@defRef=$defid]"/>
        <xsl:variable name="type" select="name(//*[@defRef=$defid])"/>
        <xsl:variable name="OT">
            <xsl:choose>
                    <xsl:when test="./*[name()='attribute']/@*[@typeRef='OT']"><xsl:value-of select="./*[name()='attribute']/@value[../@typeRef='OT']"/></xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="$type='event'">OT_EVT</xsl:when>
                            <xsl:when test="$type='function'">OT_FUNC</xsl:when>
                            <xsl:when test="$type='processInterface'">OT_FUNC</xsl:when>
                            <xsl:when test="$type='and'">OT_RULE</xsl:when>
                            <xsl:when test="$type='or'">OT_RULE</xsl:when>
                            <xsl:when test="$type='xor'">OT_RULE</xsl:when>
                            <xsl:when test="$type='application'">OT_APPL_SYS</xsl:when>
                            <xsl:when test="$type='participant'">OT_ORG_UNIT_TYPE</xsl:when>
                            <xsl:when test="$type='dataField'">OT_BUSY_OBJ</xsl:when>
                            <xsl:otherwise>HELPMEPLEASE</xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:element name="ObjDef">
            <xsl:attribute name="ObjDef.ID">ObjDef.<xsl:value-of select="200000+@defId"/>4-----p--</xsl:attribute>
            <xsl:attribute name="TypeNum"><xsl:value-of select="$OT"/></xsl:attribute>
            <xsl:if test="//*[@defRef=$defid]/*[name()='toProcess']">
                <xsl:attribute name="LinkedModels.IdRefs">
                    <xsl:for-each select="./*[name()='toProcess']">Model.<xsl:value-of select="300000+@linkedEpcId"/>4-----u--<xsl:value-of select="' '"/></xsl:for-each>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="../*[name()='arc'][./*[name()='flow']/@source=$elementid]|../*[name()='relation'][@from=$elementid]">
                <xsl:attribute name="ToCxnDefs.IdRefs">
                    <xsl:for-each select="../*[name()='arc'][./*[name()='flow']/@source=$elementid]">CxnDef.<xsl:value-of select="400000+@id"/>4-----q-- </xsl:for-each>
                    <xsl:for-each select="../*[name()='relation'][@from=$elementid]">CxnDef.<xsl:value-of select="400000+@id"/>4-----q-- </xsl:for-each>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="//*[@defRef=$defid]/*[name()='attribute'][@typeRef='GUID']">
                <xsl:element name="GUID"><xsl:value-of select="//*[@defRef=$defid]/*[name()='attribute'][@typeRef='GUID']/@value"/></xsl:element>
            </xsl:if>
            <xsl:element name="AttrDef">
                <xsl:attribute name="AttrDef.Type">AT_NAME</xsl:attribute>
                <xsl:element name="AttrValue">
                    <xsl:attribute name="LocaleId">1031</xsl:attribute>
                    <xsl:value-of select="//*[@defRef=$defid]/*[name()='name']"/>
                </xsl:element>
            </xsl:element>
            <xsl:for-each select="//*[@defRef=$defid]/*[name()='attribute'][@typeRef!='GUID' and @typeRef!='OT' and @typeRef!='ST']">
                <xsl:element name="AttrDef">
                    <xsl:attribute name="AttrDef.Type"><xsl:value-of select="@typeRef"/></xsl:attribute>
                    <xsl:element name="AttrValue">
                        <xsl:attribute name="LocaleId">1031</xsl:attribute>
                        <xsl:value-of select="@value"/>
                    </xsl:element>
                </xsl:element>
            </xsl:for-each>
            
            <xsl:for-each select="//*[@defRef=$defid]">
                <xsl:variable name="epcid" select="../@epcId"/>
                <xsl:variable name="elementid2" select="@id"/>
                <xsl:for-each select="//*[@epcId=$epcid]/*[name()='arc']/*[name()='flow'][@source=$elementid2]">
                    <xsl:variable name="target" select="@target"/>
                    <xsl:variable name="targettype" select="name(//*[@epcId=$epcid]/*[@id=$target])"/>
                    <!--xsl:element name="test"><xsl:value-of select="../*[name()='attribute']/@value[../@typeRef='GUID']"/></xsl:element-->
                    <xsl:variable name="ToOT">
                        <xsl:choose>
                                <xsl:when test="../*[name()='attribute']/@value[../@typeRef='OT']"><xsl:value-of select="../*[name()='attribute']/@value[../@typeRef='OT']"/></xsl:when>
                                <xsl:otherwise>
                                    <xsl:choose>
                                        <xsl:when test="$targettype='event'"><xsl:value-of select="$OT"/>.CT_CRT_1.OT_EVT</xsl:when>
                                        <xsl:when test="$targettype='function'"><xsl:value-of select="$OT"/>.CT_CRT_1.OT_FUNC</xsl:when>
                                        <xsl:when test="$targettype='processInterface'"><xsl:value-of select="$OT"/>.CT_CRT_1.OT_FUNC</xsl:when>
                                        <xsl:when test="$targettype='and'"><xsl:value-of select="$OT"/>.CT_CRT_1.OT_RULE</xsl:when>
                                        <xsl:when test="$targettype='or'"><xsl:value-of select="$OT"/>.CT_CRT_1.OT_RULE</xsl:when>
                                        <xsl:when test="$targettype='xor'"><xsl:value-of select="$OT"/>.CT_CRT_1.OT_RULE</xsl:when>
                                        <xsl:when test="$targettype='application'"><xsl:value-of select="$OT"/>.CT_CRT_1.OT_APPL_SYS</xsl:when>
                                        <xsl:when test="$targettype='participant'"><xsl:value-of select="$OT"/>.CT_CRT_1.OT_ORG_UNIT_TYPE</xsl:when>
                                        <xsl:when test="$targettype='dataField'"><xsl:value-of select="$OT"/>.CT_CRT_1.OT_BUSY_OBJ</xsl:when>
                                        <xsl:otherwise>HELPMEPLEASE</xsl:otherwise>
                                    </xsl:choose>
                                </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <xsl:element name="CxnDef">
                        <xsl:attribute name="CxnDef.ID">CxnDef.<xsl:value-of select="400000+(../@id)"/>4-----q--</xsl:attribute>
                        <xsl:attribute name="CxnDef.Type">
                            <xsl:choose>
                                <xsl:when test="../*/*[name()='attribute'][@typeRef='OT']"><xsl:value-of select="../*/*[name()='attribute'][@typeRef='OT']/@value"/></xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$ToOT"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:attribute name="ToObjDef.IdRef">ObjOcc.<xsl:value-of select="100000+(../../*[@id=$target]/@defRef)"/>4-----x--</xsl:attribute>
                        <xsl:if test="../*[name()='attribute']/@value[../@typeRef='GUID']">
                            <xsl:element name="GUID"><xsl:value-of select="../*[name()='attribute']/@value[../@typeRef='GUID']"/></xsl:element>
                        </xsl:if>
                        <xsl:for-each select="../*[name()='attribute'][@typeRef!='GUID' and @typeRef!='OT']">
                            <xsl:element name="AttrDef">
                                <xsl:attribute name="AttrDef.Type"><xsl:value-of select="@typeRef"/></xsl:attribute>
                                <xsl:element name="AttrValue">
                                    <xsl:attribute name="LocaleId">1031</xsl:attribute>
                                    <xsl:value-of select="@value"/>
                                </xsl:element>
                            </xsl:element>
                        </xsl:for-each>                 
                    </xsl:element>
                </xsl:for-each>
            </xsl:for-each>
            
            <xsl:variable name="elementid3" select="@id"/>
            <xsl:for-each select="../*[name()='relation'][@from=$elementid3]">
                <xsl:variable name="target" select="@to"/>
                <xsl:variable name="targettype" select="name(..//*[@id=$target])"/>
                <xsl:variable name="ToOT">
                    <xsl:choose>
                            <xsl:when test="../*[@id=$target]/*[name()='attribute']/@*[@typeRef='OT']"><xsl:value-of select="../*[@id=$target]/*[name()='attribute']/@value[../@typeRef='OT']"/></xsl:when>
                            <xsl:otherwise>
                                <xsl:choose>
                                    <xsl:when test="$targettype='event'">OT_EVT</xsl:when>
                                    <xsl:when test="$targettype='function'">OT_FUNC</xsl:when>
                                    <xsl:when test="$targettype='processInterface'">OT_FUNC</xsl:when>
                                    <xsl:when test="$targettype='and'">OT_RULE</xsl:when>
                                    <xsl:when test="$targettype='or'">OT_RULE</xsl:when>
                                    <xsl:when test="$targettype='xor'">OT_RULE</xsl:when>
                                    <xsl:when test="$targettype='application'">OT_APPL_SYS</xsl:when>
                                    <xsl:when test="$targettype='participant'">OT_ORG_UNIT_TYPE</xsl:when>
                                    <xsl:when test="$targettype='dataField'">OT_BUSY_OBJ</xsl:when>
                                    <xsl:otherwise>HELPMEPLEASE</xsl:otherwise>
                                </xsl:choose>
                            </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:element name="CxnDef">
                    <xsl:attribute name="CxnDef.ID">CxnDef.<xsl:value-of select="400000+@id"/>4-----q--</xsl:attribute>
                    <xsl:attribute name="CxnDef.Type">
                        <xsl:choose>
                            <xsl:when test="./*[name()='attribute']/@*[@typeRef='OT']"><xsl:value-of select="./*[name()='attribute']/@value[../@typeRef='OT']"/></xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="$OT"/>.CT_CRT_1.<xsl:value-of select="$ToOT"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <xsl:attribute name="ToObjDef.IdRef">ObjOcc.<xsl:value-of select="200000+(../*[@id=$target]/@defRef)"/>4-----x--</xsl:attribute>
                    <xsl:if test="./*[name()='attribute'][@typeRef='GUID']">
                        <xsl:element name="GUID"><xsl:value-of select="./*[name()='attribute'][@typeRef='GUID']/@value"/></xsl:element>
                    </xsl:if>
                    <xsl:for-each select="./*[name()='attribute'][@typeRef!='GUID']">
                        <xsl:element name="AttrDef">
                            <xsl:attribute name="AttrDef.Type"><xsl:value-of select="@typeRef"/></xsl:attribute>
                            <xsl:element name="AttrValue">
                                <xsl:attribute name="LocaleId">1031</xsl:attribute>
                                <xsl:value-of select="@value"/>
                            </xsl:element>
                        </xsl:element>
                    </xsl:for-each>                 
                </xsl:element>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
    
