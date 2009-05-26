<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright (c) 2009 Falko Menge

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
-->
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oryx="http://oryx-editor.org/"
    xmlns:raziel="http://raziel.org/"
>

    <xsl:output method="xml" indent="yes" encoding="UTF-8"
        doctype-public="-//W3C//DTD XHTML Basic 1.1//EN"
        doctype-system="http://www.w3.org/TR/xhtml-basic/xhtml-basic11.dtd"
        cdata-section-elements="script style"
    />

    <!-- Suppress text output by default for all modes. -->
    <xsl:template match="text()|@*"/><!-- mode="#all" cause an NS_ERROR_FAILURE in FF3 => probably XSLT 2.0 -->

    <!-- Root Node -->
    <xsl:template match="/">
        <html>
            <head>
                <title>Documentation generated out of a BPMN Model</title>
                <style type="text/css">
      <xsl:text><![CDATA[
         body {
            font-size: 75%;
            font-family: sans-serif;
         }
         div {
            margin-bottom: 5px;
            padding: 5px;
            border: 1px solid lightgrey;
         }
         h1, h2, h3 {
            margin-top: 0px;
         }
      ]]></xsl:text>
                </style>
            </head>
            <body>
                <xsl:for-each select="//rdf:Description[oryx:type='http://b3mn.org/stencilset/bpmn1.1#BPMNDiagram']">
                    <div class="BPMNDiagram">
                    <h1><xsl:value-of select="oryx:name" /></h1>
                    <p><xsl:value-of select="oryx:documentation" /></p>
                    <xsl:for-each select="//rdf:Description[oryx:type='http://b3mn.org/stencilset/bpmn1.1#Pool' and raziel:parent/@rdf:resource = current()/@rdf:about]">
                        <div class="Pool">
                        <xsl:if test="string-length(oryx:name) &gt; 0">
                            <h1><xsl:value-of select="oryx:name" /></h1>
                        </xsl:if>
                        <xsl:if test="string-length(oryx:documentation) &gt; 0">
                            <p><xsl:value-of select="oryx:documentation" /></p>
                        </xsl:if>
                        <xsl:for-each select="//rdf:Description[oryx:type='http://b3mn.org/stencilset/bpmn1.1#Lane' and raziel:parent/@rdf:resource = current()/@rdf:about]">
                            <div class="Lane">
                            <xsl:if test="string-length(oryx:name) &gt; 0">
                                <h2><xsl:value-of select="oryx:name" /></h2>
                            </xsl:if>
                            <xsl:if test="string-length(oryx:documentation) &gt; 0">
                                <p><xsl:value-of select="oryx:documentation" /></p>
                            </xsl:if>
                            <xsl:for-each select="//rdf:Description[oryx:type='http://b3mn.org/stencilset/bpmn1.1#Task' and raziel:parent/@rdf:resource = current()/@rdf:about]">
                                <div class="Task">
                                <xsl:if test="string-length(oryx:name) &gt; 0">
                                    <h3><xsl:value-of select="oryx:name" /></h3>
                                </xsl:if>
                                <xsl:if test="string-length(oryx:documentation) &gt; 0">
                                    <p><xsl:value-of select="oryx:documentation" /></p>
                                </xsl:if>
                                <xsl:if test="string-length(oryx:refuri) &gt; 0">
                                    <a href="{oryx:refuri}"><xsl:text>Detailed Description</xsl:text>
                                    <xsl:if test="string-length(oryx:name) &gt; 0">
                                        <xsl:text> of Task '</xsl:text>
                                        <xsl:value-of select="oryx:name" />
                                        <xsl:text>'</xsl:text>
                                    </xsl:if>
                                    </a>
                                </xsl:if>
                                </div>
                            </xsl:for-each>
                            </div>
                        </xsl:for-each>
                        </div>
                    </xsl:for-each>
                    </div>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
