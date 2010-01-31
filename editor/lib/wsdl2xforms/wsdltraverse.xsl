<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema">
<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" /> 

<!-- Debug flag. -->
<xsl:param name="debug" select="false()"/>

<xsl:template match="/">
  <xsl:if test="$debug"><xsl:message select="'traverse: root'"/></xsl:if>
  <xsl:apply-templates select="/wsdl:definitions/wsdl:service"/>
</xsl:template>

<xsl:template match="wsdl:service" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @name)"/></xsl:if>
  <xsl:apply-templates mode="#current">
    <xsl:with-param name="service" select="@name" tunnel="yes"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="wsdl:port" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @name)"/></xsl:if>
  <xsl:variable name="qname" select="resolve-QName(@binding, .)"/>
  <xsl:variable name="binding" select="local-name-from-QName($qname)"/>
  <xsl:apply-templates select="/wsdl:definitions/wsdl:binding[@name = $binding]" mode="#current">
    <xsl:with-param name="binding" select="$binding" tunnel="yes"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="wsdl:binding" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @name)"/></xsl:if>
  <xsl:variable name="qname" select="resolve-QName(@type, .)"/>
  <xsl:variable name="portType" select="local-name-from-QName($qname)"/>
  <xsl:apply-templates mode="#current">
    <xsl:with-param name="portType" select="$portType" tunnel="yes"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="wsdl:binding/wsdl:operation" mode="#all">
  <xsl:param name="portType" tunnel="yes" />
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @name)"/></xsl:if>
  <xsl:variable name="operation" select="@name"/>
  <xsl:apply-templates mode="#current">
    <xsl:with-param name="operation" select="$operation" tunnel="yes"/>
  </xsl:apply-templates>
  <xsl:apply-templates select="/wsdl:definitions/wsdl:portType[@name=$portType]/wsdl:operation[@name=$operation][1]" mode="#current">
    <xsl:with-param name="operation" select="$operation" tunnel="yes"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="wsdl:portType/wsdl:operation" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @name)"/></xsl:if>
  <xsl:apply-templates mode="#current"/>
</xsl:template>

<xsl:template match="wsdl:input" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @message)"/></xsl:if>
  <xsl:variable name="qname" select="resolve-QName(@message, .)"/>
  <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
  <xsl:variable name="namespace" select="namespace-uri-from-QName($qname)"/>
  <xsl:apply-templates select="/wsdl:definitions/wsdl:message[@name = $localname]" mode="#current">
    <xsl:with-param name="formtype" select="'input'" tunnel="yes"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="wsdl:output" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @message)"/></xsl:if>
  <xsl:variable name="qname" select="resolve-QName(@message, .)"/>
  <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
  <xsl:variable name="namespace" select="namespace-uri-from-QName($qname)"/>
  <xsl:apply-templates select="/wsdl:definitions/wsdl:message[@name = $localname]" mode="#current">
    <xsl:with-param name="formtype" select="'output'" tunnel="yes"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="wsdl:message" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @name)"/></xsl:if>
  <xsl:apply-templates mode="#current" />
</xsl:template>

<xsl:template match="wsdl:part" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @name)"/></xsl:if>
  <xsl:choose>
    <xsl:when test="@type">
      <xsl:apply-templates select="/" mode="#current">
        <xsl:with-param name="name" select="@type"/>
        <xsl:with-param name="what" select="'(simple|complex)Type'"/>
        <xsl:with-param name="context" select="."/>
      </xsl:apply-templates>
    </xsl:when>
    <xsl:when test="@element">
      <xsl:apply-templates select="/" mode="#current">
        <xsl:with-param name="name" select="@element"/>
        <xsl:with-param name="what" select="'element'"/>
        <xsl:with-param name="context" select="."/>
      </xsl:apply-templates>
    </xsl:when>
  </xsl:choose>
</xsl:template>

</xsl:stylesheet>
