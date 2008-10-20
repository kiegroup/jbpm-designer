<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:oryx="http://oryx-editor.org/"
	xmlns:raziel="http://raziel.org/">

	<xsl:output method="xml" />
	
	<xsl:template match="rdf:Description">	
		<xsl:variable name="type" select="./oryx:type" />
		<xsl:variable name="title" select="./oryx:title" />
		<xsl:variable name="realID"><xsl:value-of select="@rdf:about" /></xsl:variable>
		<xsl:variable name="id">
			<xsl:call-template name="get-id-string">
				<xsl:with-param name="id_"><xsl:value-of select="$realID" /></xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- BPEL -->
		<xsl:if test="$type='http://b3mn.org/stencilset/bpel#BPEL'">
			<process>
                <xsl:call-template name="add-attributes"/>
                <xsl:call-template name="add-properties"/>	
				<xsl:call-template name="find-children-nodes">
					<xsl:with-param name="id_"><xsl:value-of select="$id" /></xsl:with-param>
			    </xsl:call-template>
			</process>
	 	</xsl:if>
		
	</xsl:template>
	
	<xsl:template name="find-children-nodes">
	</xsl:template>
	
	<xsl:template name="add-attributes">
	</xsl:template>
	
	<xsl:template name="add-propterties">
	</xsl:template>				
	
	<xsl:template name="get-id-string">
		<xsl:param name="id_" />
		<xsl:value-of select="substring-after($id_, '#oryx')" />
	</xsl:template>

</xsl:stylesheet>