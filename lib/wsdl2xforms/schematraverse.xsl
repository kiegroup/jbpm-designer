<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:tm="tambet.matiisen@gmail.com">
<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" /> 

<!-- Debug flag. -->
<xsl:param name="debug" select="false()"/>

<!-- Suppress text output by default for all modes. -->
<xsl:template match="text()|@*" mode="#all"/>

<xsl:function name="tm:schemas">
  <xsl:param name="document" />
  <xsl:param name="namespace" />
  <xsl:if test="$debug"><xsl:message select="concat('schemas: ', $namespace)"/></xsl:if>

  <!-- Find all schemas for this namespace in this document or referenced documents. -->
  <xsl:for-each select="$document">
    <!-- If this document contains schema with the same targetNamespace. -->
    <xsl:sequence select="//xsd:schema[@targetNamespace = $namespace]"/>
    <!-- Included documents must have the same namespace as parent document. -->
    <xsl:sequence select="tm:schemas(document(//xsd:schema/xsd:include[../@targetNamespace = $namespace and @schemaLocation]/@schemaLocation), $namespace)"/>
    <!-- Imported documents may have different namespace or the same namespace as parent. --> 
    <xsl:sequence select="tm:schemas(document(//xsd:schema/xsd:import[@namespace = $namespace and @schemaLocation]/@schemaLocation), $namespace)"/>
  </xsl:for-each>
</xsl:function>

<!-- Main worker template, resolves element/attribute/type names in schema. -->
<!-- Implemented as regular template to allow overridden after import. -->
<xsl:template match="/" mode="#all">
  <xsl:param name="name" />
  <xsl:param name="what" />
  <xsl:param name="context" />
  <xsl:if test="$debug"><xsl:message select="concat('resolve: ', $name, ', ', $what)"/></xsl:if>

  <xsl:variable name="qname" select="resolve-QName($name, $context)"/>
  <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
  <xsl:variable name="namespace" select="namespace-uri-from-QName($qname)"/>

  <!-- Take the first occurrence of type, if the same type is defined twice or the same file is included twice. -->
  <xsl:apply-templates select="(tm:schemas(/, $namespace)/*[matches(name(), $what) and @name = $localname])[1]" mode="#current"/>
</xsl:template>

<xsl:template match="*" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name())"/></xsl:if>
  <xsl:apply-templates mode="#current"/>
</xsl:template>

<xsl:template match="xsd:element" name="traverseElement" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @name)"/></xsl:if>
  <xsl:choose>
    <!-- Check if based on existing element, then resolve that element. -->
    <xsl:when test="@ref">
      <xsl:apply-templates select="/" mode="#current">
        <xsl:with-param name="name" select="@ref"/>
        <xsl:with-param name="what" select="'element'"/>
        <xsl:with-param name="context" select="."/>
      </xsl:apply-templates>
    </xsl:when>
    <!-- Check if based on type, then resolve that type. -->
    <xsl:when test="@type">
      <xsl:apply-templates select="/" mode="#current">
        <xsl:with-param name="name" select="@type"/>
        <xsl:with-param name="what" select="'(simple|complex)Type'"/>
        <xsl:with-param name="context" select="."/>
      </xsl:apply-templates>
    </xsl:when>
    <!-- Otherwise type might be inline, apply templates to children. -->
    <xsl:otherwise>
      <xsl:apply-templates mode="#current"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="xsd:attribute" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @name)"/></xsl:if>
  <xsl:choose>
    <!-- Check if based on existing attribute, then resolve that attribute. -->
    <xsl:when test="@ref">
      <xsl:apply-templates select="/" mode="#current">
        <xsl:with-param name="name" select="@ref"/>
        <xsl:with-param name="what" select="'attribute'"/>
        <xsl:with-param name="context" select="."/>
      </xsl:apply-templates>
    </xsl:when>
    <!-- Check if based on type, then resolve that type. -->
    <xsl:when test="@type">
      <xsl:apply-templates select="/" mode="#current">
        <xsl:with-param name="name" select="@type"/>
        <xsl:with-param name="what" select="'(simple|complex)Type'"/>
        <xsl:with-param name="context" select="."/>
      </xsl:apply-templates>
    </xsl:when>
    <!-- Otherwise type might be inline, apply templates to children. -->
    <xsl:otherwise>
      <xsl:apply-templates mode="#current"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="xsd:group" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @name)"/></xsl:if>
  <xsl:choose>
    <!-- Check if based on existing group, then resolve that group. -->
    <xsl:when test="@ref">
      <xsl:apply-templates select="/" mode="#current">
        <xsl:with-param name="name" select="@ref"/>
        <xsl:with-param name="what" select="'group'"/>
        <xsl:with-param name="context" select="."/>
      </xsl:apply-templates>
    </xsl:when>
    <!-- Otherwise content should be inline, apply templates to children. -->
    <xsl:otherwise>
      <xsl:apply-templates mode="#current" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="xsd:attributeGroup" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @name)"/></xsl:if>
  <xsl:choose>
    <!-- Check if based on existing group, then resolve that group. -->
    <xsl:when test="@ref">
      <xsl:apply-templates select="/" mode="#current">
        <xsl:with-param name="name" select="@ref"/>
        <xsl:with-param name="what" select="'attributeGroup'"/>
        <xsl:with-param name="context" select="."/>
      </xsl:apply-templates>
    </xsl:when>
    <!-- Otherwise content should be inline, apply templates to children. -->
    <xsl:otherwise>
      <xsl:apply-templates mode="#current" />
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="xsd:restriction" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @base)"/></xsl:if>
  <!-- Restrictions of base type first. -->
  <xsl:apply-templates select="/" mode="#current">
    <xsl:with-param name="name" select="@base"/>
    <!-- Consider only simple types, because in case of complex type 
         the restriction must duplicate all constraints of base type. -->
    <xsl:with-param name="what" select="'simpleType'"/>
    <xsl:with-param name="context" select="."/>
  </xsl:apply-templates>
  <!-- Then restrictions of this type. -->
  <xsl:apply-templates mode="#current"/>
</xsl:template>

<xsl:template match="xsd:extension" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @base)"/></xsl:if>
  <!-- Elements of base type first. -->
  <xsl:apply-templates select="/" mode="#current">
    <xsl:with-param name="name" select="@base"/>
    <xsl:with-param name="what" select="'(simple|complex)Type'"/>
    <xsl:with-param name="context" select="."/>
  </xsl:apply-templates>
  <!-- Add additional elements/attributes, if defined by extension. -->
  <xsl:apply-templates mode="#current"/>
</xsl:template>

<xsl:template match="xsd:list" mode="#all">
  <xsl:if test="$debug"><xsl:message select="concat('traverse: ', name(), ', ', @itemType)"/></xsl:if>
  <!-- Resolve item type. -->
  <xsl:apply-templates select="/" mode="#current">
    <xsl:with-param name="name" select="@itemType"/>
    <xsl:with-param name="what" select="'(simple|complex)Type'"/>
    <xsl:with-param name="context" select="."/>
  </xsl:apply-templates>
</xsl:template>

</xsl:stylesheet>
