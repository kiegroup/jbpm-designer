<?xml version="1.0" encoding="UTF-8"?>
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
                            <xsl:variable name="edgeName" select="oryx:name" />
                            <xsl:choose>
                                <!-- TODO: avoid code duplication since the only difference is start/@name -->
                                <xsl:when test="string-length(oryx:conditionexpression) &gt; 0">
                                    <start name="{oryx:conditionexpression}" id="{@rdf:about}"
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
                                            ][ 1 ]/@rdf:about }">
                                      <type><xsl:value-of select="oryx:communicationchanneltype"/></type>
                                    </start>
                                </xsl:when>
                                <xsl:otherwise>
                                    <start name="{oryx:name}" id="{@rdf:about}"
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
                                            ][ 1 ]/@rdf:about }">
                                      <type><xsl:value-of select="oryx:communicationchanneltype"/></type>
                                    </start>
                                </xsl:otherwise>
                            </xsl:choose>
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
