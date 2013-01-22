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

    <xsl:output method="text" indent="no" encoding="UTF-8"/>

    <xsl:template match="/">
        <xsl:for-each select="//rdf:Description[oryx:type='http://b3mn.org/stencilset/bpmn1.1#BPMNDiagram']">
            <xsl:text>digraph "</xsl:text>
            <xsl:value-of select="oryx:name" />
            <xsl:text>" {
</xsl:text>
            <!-- comment -->
            <xsl:text>    /* Diagram: </xsl:text>
            <xsl:value-of select="oryx:documentation" />
            <xsl:text>*/
</xsl:text>
            <xsl:text>    graph [fontname = Verdana, rankdir = LR]
</xsl:text>
            <xsl:text>    node [labelloc = bottom, fontname = Verdana, style = bold, shape = box, shapefile = "human.png", height = 1.1, width = 1.1]
</xsl:text>
            <xsl:text>    edge [len = 2.0, minlen = 2.0]
</xsl:text>
            <!-- nodes -->
            <xsl:for-each select="
                //rdf:Description [
                    oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#Pool'
                    and
                    raziel:parent/@rdf:resource = current()/@rdf:about
                ]"
            >
                <xsl:for-each select="
                    //rdf:Description [
                        oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#Lane'
                        and
                        raziel:parent/@rdf:resource = current()/@rdf:about
                    ]"
                >
                    <!-- comment -->
                    <xsl:text>    /* Lane: </xsl:text>
                    <xsl:value-of select="oryx:name" />
                    <xsl:text> (</xsl:text>
                    <xsl:value-of select="oryx:documentation" />
                    <xsl:text>) */
</xsl:text>
                    <!-- node -->
                    <xsl:text>    "</xsl:text>
                    <xsl:value-of select="@rdf:about" />
                    <xsl:text>" [label = "</xsl:text>
                    <xsl:value-of select="oryx:name" />
                    <xsl:text>", URL = "javascript:jumpToScreen(this, '</xsl:text>
                    <xsl:value-of select="substring-after(@rdf:about, '#')" />
<!--
                    <xsl:value-of select="oryx:refuri" />
-->
                    <xsl:text>');"]
</xsl:text>
                </xsl:for-each>
            </xsl:for-each>

            <!-- edges -->
            <xsl:for-each select="
                //rdf:Description [
                    oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#SequenceFlow'
                ]
            ">
                <xsl:variable name="sourceTask" select="
                    //rdf:Description [
                        oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#Task'
                        and
                        raziel:outgoing/@rdf:resource = current()/@rdf:about
                    ]
                " />
                <xsl:variable name="targetTask" select="
                    //rdf:Description [
                        oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#Task'
                        and
                        @rdf:about = current()/raziel:target/@rdf:resource
                    ]
                " />
                <xsl:variable name="sourceLane" select="$sourceTask/raziel:parent/@rdf:resource" />
                <xsl:variable name="targetLane" select="$targetTask/raziel:parent/@rdf:resource" />
                <xsl:if test="$sourceLane != $targetLane">
                    <!-- comment -->
                    <xsl:text>    /* SquenceFlow from "</xsl:text>
                    <xsl:value-of select="$sourceTask/oryx:name" />
                    <xsl:text>" to "</xsl:text>
                    <xsl:value-of select="$targetTask/oryx:name" />
                    <xsl:text>" */
</xsl:text>
                    <!-- edge -->
                    <xsl:text>    "</xsl:text>
                    <xsl:value-of select="$sourceLane" />
                    <xsl:text>" ->
    "</xsl:text>
                    <xsl:value-of select="$targetLane" />
                    <xsl:text>" [label = "</xsl:text>
                    <xsl:choose>
                        <xsl:when test="string-length(oryx:conditionexpression) &gt; 0">
                            <xsl:value-of select="oryx:conditionexpression" />
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="oryx:name" />
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:text>", URL = "</xsl:text>
                    <xsl:value-of select="oryx:refuri" />
                    <xsl:text>"]
</xsl:text>
                </xsl:if>
            </xsl:for-each>
            <xsl:text>}</xsl:text>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
