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
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oryx="http://oryx-editor.org/"
    xmlns:raziel="http://raziel.org/"
    xmlns:xmi="http://www.omg.org/XMI"
    xmlns:de.hpi.sam.dtrp.model="http://ExchangeFormatModel/1.0"
    >

    <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

    <xsl:template match="/">
        <xsl:for-each select="//rdf:Description[oryx:type='http://b3mn.org/stencilset/bpmn1.1#BPMNDiagram']">
            <de.hpi.sam.dtrp.model:Process xmi:version="2.0" name="{oryx:name}" description="{oryx:documentation}" id="{@rdf:about}">
            <xsl:for-each select="//rdf:Description[oryx:type='http://b3mn.org/stencilset/bpmn1.1#Pool' and raziel:parent/@rdf:resource = current()/@rdf:about]">
                <xsl:for-each select="//rdf:Description[oryx:type='http://b3mn.org/stencilset/bpmn1.1#Lane' and raziel:parent/@rdf:resource = current()/@rdf:about]">
                    <roles name="{oryx:name}" description="{oryx:documentation}" id="{@rdf:about}">
                    <xsl:for-each select="//rdf:Description[oryx:type='http://b3mn.org/stencilset/bpmn1.1#Task' and raziel:parent/@rdf:resource = current()/@rdf:about]">
                        <activities name="{oryx:name}" description="{oryx:documentation}" id="{@rdf:about}" goals="{oryx:goals}" end="{//rdf:Description[oryx:type='http://b3mn.org/stencilset/bpmn1.1#SequenceFlow' and raziel:target/@rdf:resource = current()/@rdf:about][1]/@rdf:about}">
                        
                        <xsl:for-each select="
                            //rdf:Description [
                                oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#SequenceFlow'
                                and
                                current()/raziel:outgoing/@rdf:resource = @rdf:about
                            ]
                        ">
                            <start id="{@rdf:about}"
                                revEnd="{
                                    //rdf:Description [
                                        @rdf:about = current()/raziel:target/@rdf:resource
                                        and
                                        oryx:type='http://b3mn.org/stencilset/bpmn1.1#Task'
                                    ][ 1 ]/@rdf:about
                                }"
                                artifacts="{
                                    //rdf:Description [
                                        @rdf:about = //rdf:Description [
                                            @rdf:about = current()/raziel:outgoing/@rdf:resource
                                            and
                                            oryx:type='http://b3mn.org/stencilset/bpmn1.1#Association_Undirected'
                                        ][ 1 ]/raziel:target/@rdf:resource
                                        and
                                        oryx:type='http://b3mn.org/stencilset/bpmn1.1#DataObject'
                                    ][ 1 ]/@rdf:about
                                }"
                            >
                                <xsl:choose>
                                    <xsl:when test="string-length(oryx:conditionexpression) &gt; 0">
                                        <xsl:attribute name="name">
                                            <xsl:value-of select="oryx:conditionexpression"/>
                                        </xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:attribute name="name">
                                            <xsl:value-of select="oryx:name"/>
                                        </xsl:attribute>
                                    </xsl:otherwise>
                                </xsl:choose>
                                <type><xsl:value-of select="oryx:communicationchanneltype"/></type>
                            </start>
                        </xsl:for-each>
                        </activities>
                    </xsl:for-each>
                    </roles>
                </xsl:for-each>
            </xsl:for-each>
            <xsl:for-each select="//rdf:Description[oryx:type='http://b3mn.org/stencilset/bpmn1.1#DataObject']">
                <!-- TODO: check that DataObject is in current Diagram
                (the following doesn't work: raziel:parent/@rdf:resource = current()/@rdf:about )
                => could be done via parrent of sequence flow
                Only necessary if multiple diagrams are contained in one RDF file,
                which is not current practice -->
                <!-- TODO: ensure that it is impossible to have an undirected
                association going from a data object to a sequence flow -->
                <artifacts  name="{oryx:name}" description="{oryx:documentation}" id="{@rdf:about}" picture="{oryx:picture}"
                    revArtifacts="{
                        //rdf:Description[
                            oryx:type='http://b3mn.org/stencilset/bpmn1.1#SequenceFlow'
                            and
                            raziel:outgoing/@rdf:resource = //rdf:Description[
                                oryx:type='http://b3mn.org/stencilset/bpmn1.1#Association_Undirected'
                                and
                                raziel:target/@rdf:resource = current()/@rdf:about
                            ][1]/@rdf:about
                        ]/@rdf:about
                    }"/>
<!--
oryx:type='http://b3mn.org/stencilset/bpmn1.1#SequenceFlow' and 
//rdf:Description[@rdf:about=//rdf:Description[oryx:type='http://b3mn.org/stencilset/bpmn1.1#Association_Undirected']/raziel:target/@rdf:resource]
//rdf:Description[@rdf:about=//rdf:Description[oryx:type='http://b3mn.org/stencilset/bpmn1.1#SequenceFlow']/raziel:outgoing/@rdf:resource]/oryx:type
-->
            </xsl:for-each>
            </de.hpi.sam.dtrp.model:Process>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
