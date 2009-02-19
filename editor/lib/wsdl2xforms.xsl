<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xhtml="http://www.w3.org/1999/xhtml" 
    xmlns:xforms="http://www.w3.org/2002/xforms"
    xmlns:events="http://www.w3.org/2001/xml-events"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/" 
    xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
    xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/">

<xsl:import href="schema2xforms.xsl"/>
<xsl:import href="wsdltraverse.xsl"/>

<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" /> 

<xsl:param name="wsdlid"></xsl:param>
<xsl:param name="outdir"></xsl:param>

<!-- URL for showing the XML content of request. -->
<xsl:param name="debugurl" select="false()" />
<!-- URL for showing the XML content of response. -->
<xsl:param name="urlxml" select="false()" />

<!-- Debug flag. -->
<xsl:param name="debug" select="false()"/>

<!-- Method for submit, by default 'post'. -->
<xsl:param name="method" select="'post'" />

<xsl:template match="wsdl:port">
  <xsl:if test="$debug"><xsl:message select="concat('service: ', name(), ', ', @name)"/></xsl:if>
  <xsl:apply-imports>
    <xsl:with-param name="url" select="soap:address/@location" tunnel="yes"/>
  </xsl:apply-imports>
</xsl:template>

<xsl:template match="wsdl:binding/wsdl:operation">
  <!-- Default file name is operation.xhtml, but this can be changed by importing template. -->
  <xsl:param name="filename" select="concat($outdir, '/', @name, '.xhtml')" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('binding: ', name(), ', ', @name)"/></xsl:if>
  <!--<xsl:result-document href="{$filename}">
    <xsl:apply-imports/>
  </xsl:result-document> -->
</xsl:template>

<xsl:template match="wsdl:portType/wsdl:operation">
  <xsl:if test="$debug"><xsl:message select="concat('portType: ', name(), ', ', @name)"/></xsl:if>
  <xhtml:html>
    <xsl:namespace name="tns" select="/wsdl:definitions/@targetNamespace" />
    <xsl:apply-templates select="." mode="html">
      <xsl:with-param name="tnsprefix" select="'tns'" tunnel="yes"/>
      <xsl:with-param name="formname" select="concat($wsdlid, '.', ../@name, '.', @name)" tunnel="yes"/>
    </xsl:apply-templates>
  </xhtml:html>
</xsl:template>

<!-- Html generation. -->

<xsl:template match="wsdl:portType/wsdl:operation" mode="html">
  <xsl:if test="$debug"><xsl:message select="concat('html: ', name(), ', ', @name)"/></xsl:if>
  <xhtml:head>
    <xsl:apply-templates select="." mode="head" />
  </xhtml:head>
  <xhtml:body>
    <xsl:apply-templates select="." mode="body" />
  </xhtml:body>
</xsl:template>

<xsl:template match="wsdl:portType/wsdl:operation" mode="head">
  <xsl:if test="$debug"><xsl:message select="concat('head: ', name(), ', ', @name)"/></xsl:if>
  <!-- Allow extensions to set their own title. -->
  <xsl:apply-templates select="wsdl:documentation" mode="title" />
  <xforms:model>
    <xsl:apply-templates select="." mode="model" />
  </xforms:model>
</xsl:template>

<xsl:template match="wsdl:portType/wsdl:operation" mode="body">
  <xsl:if test="$debug"><xsl:message select="concat('body: ', name(), ', ', @name)"/></xsl:if>
  <!-- Allow extensions to set their own heading. -->
  <xsl:apply-templates select="wsdl:documentation" mode="heading"/>
  <xsl:apply-templates select="." mode="form"/>
</xsl:template>

<!-- Model generation. -->

<xsl:template match="wsdl:portType/wsdl:operation" mode="model">
  <xsl:if test="$debug"><xsl:message select="concat('model: ', name(), ', ', @name)"/></xsl:if>
  <!-- Generate instances. -->
  <xsl:apply-templates select="." mode="instance" />
  <!-- Generate lookup instances. -->
  <xsl:apply-templates select="." mode="lookup" />
  <!-- Generate schema for validation. -->
  <!--xsl:apply-templates select="." mode="schema" /-->
  <!-- Generate bindings. -->
  <xsl:apply-templates select="." mode="bind" />
  <!-- Generate submission and events. -->
  <xsl:apply-templates select="." mode="submission" />
  <!-- Generate event to activate request form. -->
  <xsl:apply-templates select="." mode="activate" />
</xsl:template>

<xsl:template match="wsdl:portType/wsdl:operation" mode="activate">
  <xsl:param name="formname" tunnel="yes"/>
  <!-- When output instance has been populated from request body, then activate response form, instead of request form. -->
  <xforms:toggle case="{$formname}.request" events:event="xforms-ready" if="not(boolean(instance('{$formname}.output')/SOAP-ENV:Body))"/>
  <xforms:toggle case="{$formname}.response" events:event="xforms-ready" if="boolean(instance('{$formname}.output')/SOAP-ENV:Body)"/>
</xsl:template>

<!-- Instance generation. -->

<xsl:template match="wsdl:portType/wsdl:operation" mode="instance">
  <xsl:apply-imports/>
  <!-- Generate helper instance. -->
  <xsl:call-template name="createTempInstance"/>
</xsl:template>

<xsl:template match="wsdl:input" mode="instance">
  <xsl:param name="formname" tunnel="yes"/>
  <xsl:param name="message" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('instance: ', name(), ', ', @message)"/></xsl:if>
  <xsl:variable name="qname" select="resolve-QName(@message, .)"/>
  <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
  <xsl:variable name="namespace" select="namespace-uri-from-QName($qname)"/>
  <xforms:instance id="{$formname}.input">
    <SOAP-ENV:Envelope SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
      <SOAP-ENV:Header>
        <xsl:apply-templates select="." mode="instance-soap-header"/>
      </SOAP-ENV:Header>
      <SOAP-ENV:Body>
        <xsl:element name="ns5:{if ($message) then $message else $localname}" namespace="{$namespace}">
          <xsl:apply-imports/>
        </xsl:element>
      </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>
  </xforms:instance>
</xsl:template>

<!-- Empty template to prevent imported templates to traverse the tree. -->
<xsl:template match="wsdl:input" mode="instance-soap-header"/>

<xsl:template match="wsdl:output" mode="instance">
  <xsl:param name="formname" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('instance: ', name(), ', ', @message)"/></xsl:if>
  <xsl:variable name="qname" select="resolve-QName(@message, .)"/>
  <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
  <xsl:variable name="namespace" select="namespace-uri-from-QName($qname)"/>
  <xforms:instance id="{$formname}.output">
    <SOAP-ENV:Envelope SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/"/>
  </xforms:instance>
</xsl:template>

<xsl:template match="wsdl:part" mode="instance">
  <xsl:param name="element" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('instance: ', name(), ', ', @name)"/></xsl:if>
  <xsl:choose>
    <xsl:when test="@type">
      <!--xsl:element name="{@name}" namespace="{/wsdl:definitions/@targetNamespace}"-->
      <xsl:element name="{if ($element) then $element else @name}" namespace="">
        <xsl:apply-imports/>
      </xsl:element>
    </xsl:when>
    <xsl:when test="@element">
      <xsl:apply-imports>
        <!-- Element name must be part name. -->
        <xsl:with-param name="element" select="if ($element) then $element else @name" tunnel="yes"/>
      </xsl:apply-imports>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template match="/" mode="instance">
  <xsl:param name="name" />
  <xsl:param name="what" />
  <xsl:param name="context" />
  <xsl:param name="formtype" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('instance: ', $name, ', ', $what)"/></xsl:if>

  <xsl:variable name="qname" select="resolve-QName($name, $context)"/>
  <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
  <xsl:variable name="namespace" select="namespace-uri-from-QName($qname)"/>

  <xsl:choose>
    <!-- In case of Array type add it directly, don't resolve. -->
    <xsl:when test="ends-with($what, 'Type') and $localname = 'Array' and $namespace = 'http://schemas.xmlsoap.org/soap/encoding/'">
      <xsl:if test="$formtype = 'input'">
        <xsl:attribute name="type" select="concat('SOAP-ENC:',$localname)" namespace="http://www.w3.org/2001/XMLSchema-instance" />
        <xsl:attribute name="arrayType" namespace="http://schemas.xmlsoap.org/soap/encoding/" />
      </xsl:if>
    </xsl:when>
    <xsl:otherwise>
      <!-- Resolve type/element/attribute. -->
      <xsl:apply-imports>
        <xsl:with-param name="name" select="$name"/>
        <xsl:with-param name="what" select="$what"/>
        <xsl:with-param name="context" select="$context"/>
      </xsl:apply-imports>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- Lookup generation. -->

<!-- Schema generation. -->

<xsl:template match="wsdl:operation" mode="schema">
  <xsl:if test="$debug"><xsl:message select="concat('schema: ', name(), ', ', @name)"/></xsl:if>
  <!-- This creates duplicate namespace declarations, which looks ugly. -->
  <!--xsd:schema>
    <xsl:copy-of select="/wsdl:definitions/wsdl:types/xsd:schema/*"/>
    <xsl:apply-templates mode="schema" />
  </xsd:schema-->
  <!-- This gives better result, but the code is nonintuitive. -->
  <xsl:apply-templates select="/wsdl:definitions/wsdl:types/xsd:schema" mode="schema">
    <xsl:with-param name="node" select="*"/>
  </xsl:apply-templates>
</xsl:template>

<xsl:template match="xsd:schema" mode="schema">
  <xsl:param name="node"/>
  <xsl:if test="$debug"><xsl:message select="concat('schema: ', name(), ', ', $node/name())"/></xsl:if>
  <xsl:copy>
    <xsl:copy-of select="*"/>
    <xsl:apply-templates select="$node" mode="#current" />
  </xsl:copy>
</xsl:template>

<xsl:template match="wsdl:input" mode="schema">
  <xsl:param name="message" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('schema: ', name(), ', ', @message)"/></xsl:if>
  <xsl:variable name="qname" select="resolve-QName(@message, .)"/>
  <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
  <xsd:element name="{if ($message) then $message else $localname}">
    <xsl:apply-imports/>
  </xsd:element>
</xsl:template>

<xsl:template match="wsdl:output" mode="schema">
  <xsl:param name="message" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('schema: ', name(), ', ', @message)"/></xsl:if>
  <xsl:variable name="qname" select="resolve-QName(@message, .)"/>
  <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
  <xsd:element name="{if ($message) then $message else $localname}">
    <xsl:apply-imports/>
  </xsd:element>
</xsl:template>

<xsl:template match="wsdl:message" mode="schema">
  <xsl:if test="$debug"><xsl:message select="concat('schema: ', name(), ', ', @name)"/></xsl:if>
  <xsd:complexType>
    <xsd:sequence>
      <xsl:apply-imports/>
    </xsd:sequence>
  </xsd:complexType>
</xsl:template>

<xsl:template match="wsdl:part" mode="schema">
  <xsl:if test="$debug"><xsl:message select="concat('schema: ', name(), ', ', @name)"/></xsl:if>
  <xsl:choose>
    <xsl:when test="@type">
      <xsd:element name="{@name}" type="{@type}"/>
    </xsl:when>
    <xsl:when test="@element">
      <xsd:element name="{@name}" ref="{@element}"/>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<!-- Submission generation. -->

<xsl:template match="wsdl:operation" mode="submission">
  <xsl:param name="formname" tunnel="yes"/>
  <xsl:param name="url" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('submission: ', name(), ', ', @name)"/></xsl:if>
  <xforms:submission id="{$formname}.submission" action="{$url}" mediatype="text/xml" ref="instance('{$formname}.input')" method="{$method}" replace="instance" instance="{$formname}.output">
    <xsl:apply-templates select="." mode="submission-actions" />
  </xforms:submission>
  <xsl:if test="$debugurl">
    <xforms:submission id="{$formname}.debug" action="{$debugurl}" mediatype="text/xml" ref="instance('{$formname}.input')" method="post" replace="all" validate="false">
      <xsl:apply-templates select="." mode="submission-actions" />
    </xforms:submission>
  </xsl:if>
  <xsl:if test="$urlxml">
    <xforms:submission id="{$formname}.savexml" action="{$urlxml}" mediatype="text/xml" ref="instance('{$formname}.output')" method="post" replace="all"/>
  </xsl:if>
</xsl:template>

<xsl:template match="wsdl:operation" mode="submission-actions">
  <xsl:if test="$debug"><xsl:message select="concat('submission-actions: ', name(), ', ', @name)"/></xsl:if>
  <xforms:action events:event="xforms-submit">
    <xsl:apply-templates select="." mode="submission-actions-submit" />
  </xforms:action>
  <xforms:action events:event="xforms-submit-done">
    <xsl:apply-templates select="." mode="submission-actions-submit-done" />
  </xforms:action>
  <xforms:action events:event="xforms-submit-error">
    <xsl:apply-templates select="." mode="submission-actions-submit-error" />
  </xforms:action>
  <!-- Must be done at this level, because of deferred updates. -->
  <!-- Otherwise nonrelevant fields are not shown while error message is displayed. -->
  <xsl:apply-templates select="." mode="submission-actions-submit-error-message" />
</xsl:template>

<xsl:template match="wsdl:operation" mode="submission-actions-submit">
  <xsl:if test="$debug"><xsl:message select="concat('submission-actions-submit: ', name(), ', ', @name)"/></xsl:if>
  <xforms:setvalue ref="instance('temp')/relevant" value="false()"/>
</xsl:template>

<xsl:template match="wsdl:operation" mode="submission-actions-submit-done">
  <xsl:param name="formname" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('submission-actions-submit-done: ', name(), ', ', @name)"/></xsl:if>
  <xforms:setvalue ref="instance('temp')/relevant" value="true()"/>
  <xforms:toggle case="{$formname}.response" />
</xsl:template>

<xsl:template match="wsdl:operation" mode="submission-actions-submit-error">
  <xsl:if test="$debug"><xsl:message select="concat('submission-actions-submit-error: ', name(), ', ', @name)"/></xsl:if>
  <xforms:setvalue ref="instance('temp')/relevant" value="true()"/>
</xsl:template>

<xsl:template match="wsdl:operation" mode="submission-actions-submit-error-message">
  <xforms:message level="modal" events:event="xforms-submit-error">
    <xsl:apply-templates select="." mode="submission-actions-submit-error-message-label" />
  </xforms:message>
</xsl:template>

<xsl:template match="wsdl:operation" mode="submission-actions-submit-error-message-label">Please ensure that all field have valid contents.</xsl:template>

<!-- Bindings generation. -->

<xsl:template match="wsdl:input" mode="bind">
  <xsl:param name="formname" tunnel="yes"/>
  <xsl:param name="message" tunnel="yes"/>
  <xsl:param name="tnsprefix" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('bind: ', name(), ', ', @message)"/></xsl:if>
  <xsl:variable name="qname" select="resolve-QName(@message, .)"/>
  <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
  <xsl:variable name="namespace" select="namespace-uri-from-QName($qname)"/>
  <!-- Assume message is in target namespace. -->
  <xforms:bind nodeset="{concat('instance(''', $formname, '.input'')/SOAP-ENV:Body/', $tnsprefix, ':',if ($message) then $message else $localname)}">
    <xsl:apply-imports />
  </xforms:bind>
</xsl:template>

<xsl:template match="wsdl:output" mode="bind">
  <xsl:param name="formname" tunnel="yes"/>
  <xsl:param name="message" tunnel="yes"/>
  <xsl:param name="tnsprefix" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('bind: ', name(), ', ', @message)"/></xsl:if>
  <xsl:variable name="qname" select="resolve-QName(@message, .)"/>
  <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
  <xsl:variable name="namespace" select="namespace-uri-from-QName($qname)"/>
  <!-- Assume message is in target namespace. -->
  <xforms:bind nodeset="{concat('instance(''', $formname, '.output'')/SOAP-ENV:Body/', $tnsprefix, ':',if ($message) then $message else $localname)}">
    <xsl:apply-imports/>
  </xforms:bind>
</xsl:template>

<xsl:template match="wsdl:part" mode="bind">
  <xsl:param name="root" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('bind: ', name(), ', ', @name)"/></xsl:if>
  <xsl:choose>
    <xsl:when test="@type">
      <xforms:bind nodeset="{@name}">
        <!-- Create dummy element that mips template matches. -->
        <xsl:variable name="dummy" as="element() *">
          <xsd:element name="{@name}"/>
        </xsl:variable>
        <xsl:apply-templates select="$dummy" mode="mips"/>
        <xsl:apply-imports>
          <xsl:with-param name="root" select="''" tunnel="yes" />
        </xsl:apply-imports>
        <xsl:apply-templates select="$dummy" mode="nillable"/>
      </xforms:bind>
    </xsl:when>
    <xsl:when test="@element">
      <xsl:apply-imports>
        <xsl:with-param name="element" select="@name" tunnel="yes" />
      </xsl:apply-imports>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template match="/" mode="bind">
  <xsl:param name="name" />
  <xsl:param name="what" />
  <xsl:param name="context" />
  <xsl:param name="formtype" tunnel="yes" />
  <xsl:if test="$debug"><xsl:message select="concat('bind: ', $name, ', ', $what)"/></xsl:if>

  <xsl:variable name="qname" select="resolve-QName($name, $context)"/>
  <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
  <xsl:variable name="namespace" select="namespace-uri-from-QName($qname)"/>

  <xsl:choose>
    <xsl:when test="ends-with($what, 'Type') and $localname = 'Array' and $namespace = 'http://schemas.xmlsoap.org/soap/encoding/'">
      <xsl:if test="$formtype = 'input'">
        <xsl:variable name="qname" select="resolve-QName($context/xsd:sequence/xsd:element/@type, $context/xsd:sequence/xsd:element)"/>
        <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
        <xsl:variable name="namespace" select="namespace-uri-from-QName($qname)"/>

        <!-- Calculate count of nodes into arrayType attribute. -->
        <!-- Subtract 3: insert-trigger, delete-trigger and prototype item. -->
        <xforms:bind nodeset="@SOAP-ENC:arrayType" calculate="concat('{if ($namespace = 'http://www.w3.org/2001/XMLSchema') then concat('xsd:', $localname) else if ($namespace != '') then concat('tns:', $localname) else 'xsd:anyType'}[',count(../*) - 3,']')">
          <xsl:if test="$namespace != 'http://www.w3.org/2001/XMLSchema' and $namespace != ''">
            <xsl:namespace name="tns" select="$namespace" />
          </xsl:if>
        </xforms:bind>
      </xsl:if>
    </xsl:when>
    <xsl:otherwise>
      <!-- Resolve type/element/attribute. -->
      <xsl:apply-imports>
        <xsl:with-param name="name" select="$name"/>
        <xsl:with-param name="what" select="$what"/>
        <xsl:with-param name="context" select="$context"/>
      </xsl:apply-imports>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- Form generation. -->

<xsl:template match="wsdl:portType/wsdl:operation" mode="form">
  <xsl:if test="$debug"><xsl:message select="concat('form: ', name(), ', ', @name)"/></xsl:if>
  <xforms:switch>
    <xsl:apply-imports/>
  </xforms:switch>
</xsl:template>

<xsl:template match="wsdl:input" mode="form">
  <xsl:param name="formname" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('form: ', name(), ', ', @message)"/></xsl:if>
  <xforms:case id="{$formname}.request">
    <xsl:apply-imports/>
    <xforms:group class="actions">
      <xsl:apply-templates select="." mode="form-actions"/>
    </xforms:group>
  </xforms:case>
</xsl:template>

<xsl:template match="wsdl:input" mode="form-actions">
  <xsl:param name="formname" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('form-actions: ', name(), ', ', @message)"/></xsl:if>
  <xforms:submit submission="{$formname}.submission">
    <xsl:apply-templates select="." mode="form-submit"/>
  </xforms:submit>
  <xsl:if test="$debugurl">
    <xforms:submit submission="{$formname}.debug">
      <xforms:label>Debug</xforms:label>
    </xforms:submit>
  </xsl:if>
</xsl:template>

<xsl:template match="wsdl:input" mode="form-submit">
  <xsl:if test="$debug"><xsl:message select="concat('form-submit: ', name(), ', ', @message)"/></xsl:if>
  <xforms:label>Submit</xforms:label>
</xsl:template>

<xsl:template match="wsdl:output" mode="form">
  <xsl:param name="formname" tunnel="yes"/>
  <xsl:param name="message" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('form: ', name(), ', ', @message)"/></xsl:if>
  <xforms:case id="{$formname}.response">
    <xsl:apply-imports/>
    <xsl:apply-templates select="." mode="form-fault"/>
    <xforms:group class="actions">
      <xsl:apply-templates select="." mode="form-actions"/>
    </xforms:group>
  </xforms:case>
</xsl:template>

<xsl:template match="wsdl:output" mode="form-fault">
  <xsl:param name="formname" tunnel="yes" />
  <xforms:group ref="{concat('instance(''', $formname, '.output'')')}/SOAP-ENV:Body/SOAP-ENV:Fault" class="fault">
    <xforms:output ref="faultstring"/>
  </xforms:group>
</xsl:template>

<xsl:template match="wsdl:output" mode="form-actions">
  <xsl:param name="formname" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('form-actions: ', name(), ', ', @message)"/></xsl:if>
  <xforms:trigger>
    <xsl:apply-templates select="." mode="form-again"/>
    <xforms:toggle events:event="DOMActivate" case="{$formname}.request"/>
  </xforms:trigger>
  <xsl:if test="$urlxml">
    <xforms:submit submission="{$formname}.savexml">
      <xforms:label>Show XML</xforms:label>
    </xforms:submit>
  </xsl:if>
</xsl:template>

<xsl:template match="wsdl:output" mode="form-again">
  <xsl:if test="$debug"><xsl:message select="concat('form-again: ', name(), ', ', @message)"/></xsl:if>
  <xforms:label>Back</xforms:label>
</xsl:template>

<xsl:template match="wsdl:message" mode="form">
  <xsl:param name="formname" tunnel="yes"/>
  <xsl:param name="formtype" tunnel="yes"/>
  <xsl:param name="message" tunnel="yes"/>
  <xsl:param name="tnsprefix" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('form: ', name(), ', ', @name)"/></xsl:if>
  <!--xsl:variable name="qname" select="resolve-QName(@name, .)"/>
  <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
  <xsl:variable name="namespace" select="namespace-uri-from-QName($qname)"/-->
  <xsl:variable name="ref" select="concat('instance(''', $formname, '.', $formtype, ''')/SOAP-ENV:Body/', $tnsprefix, ':', if ($message) then $message else @name)"/>
  <!-- Assume message is in target namespace. -->
  <xforms:group ref="{$ref}">
    <xsl:apply-imports>
      <xsl:with-param name="parentref" select="$ref" tunnel="yes"/>
      <xsl:with-param name="ref" select="'.'" tunnel="yes"/>
      <xsl:with-param name="root" tunnel="yes"/>
    </xsl:apply-imports>
  </xforms:group>
</xsl:template>

<xsl:template match="wsdl:part" mode="form">
  <xsl:if test="$debug"><xsl:message select="concat('form: ', name(), ', ', @name)"/></xsl:if>
  <xsl:choose>
    <xsl:when test="@type">
      <xsl:apply-imports>
        <xsl:with-param name="node" select="." tunnel="yes"/>
        <xsl:with-param name="ref" select="@name" tunnel="yes"/>
      </xsl:apply-imports>
    </xsl:when>
    <xsl:when test="@element">
      <xsl:apply-imports>
        <xsl:with-param name="node" select="." tunnel="yes"/>
        <xsl:with-param name="element" select="@name" tunnel="yes"/>
      </xsl:apply-imports>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<xsl:template match="xsd:restriction | xsd:extension" mode="form bind">
  <xsl:param name="element" tunnel="yes"/>
  <xsl:param name="formtype" tunnel="yes"/>
  <xsl:if test="$debug"><xsl:message select="concat('form: ', name(), ', ', @base)"/></xsl:if>

  <xsl:variable name="qname" select="resolve-QName(@base, .)"/>
  <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
  <xsl:variable name="namespace" select="namespace-uri-from-QName($qname)"/>

  <!-- Resolve type/element/attribute. -->
  <xsl:apply-imports>
    <!-- Array elements can have any name. To cope with that, use * on output forms. -->
    <!-- Input forms must use element name, otherwise insert-trigger and delete-trigger
         would be considered rows too. -->
    <xsl:with-param name="element" select="if ($localname = 'Array' and $namespace = 'http://schemas.xmlsoap.org/soap/encoding/' and $formtype = 'output') then '*' else $element" tunnel="yes"/>
  </xsl:apply-imports>
</xsl:template>

<xsl:template match="/" mode="form">
  <xsl:param name="name" />
  <xsl:param name="what" />
  <xsl:param name="context" />
  <xsl:if test="$debug"><xsl:message select="concat('form: ', $name, ', ', $what)"/></xsl:if>

  <xsl:variable name="qname" select="resolve-QName($name, $context)"/>
  <xsl:variable name="localname" select="local-name-from-QName($qname)"/>
  <xsl:variable name="namespace" select="namespace-uri-from-QName($qname)"/>

  <xsl:choose>
    <xsl:when test="ends-with($what, 'Type') and $localname = 'Array' and $namespace = 'http://schemas.xmlsoap.org/soap/encoding/'">
      <!-- Ignore Array type. -->      
    </xsl:when>
    <xsl:otherwise>
      <!-- Resolve type/element/attribute. -->
      <xsl:apply-imports>
        <xsl:with-param name="name" select="$name"/>
        <xsl:with-param name="what" select="$what"/>
        <xsl:with-param name="context" select="$context"/>
      </xsl:apply-imports>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="wsdl:part" mode="label">
  <xforms:label><xsl:value-of select="@name"/></xforms:label>
</xsl:template>

</xsl:stylesheet>
