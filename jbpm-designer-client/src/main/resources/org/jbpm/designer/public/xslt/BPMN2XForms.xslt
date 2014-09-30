<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright (c) 2008-2009 Falko Menge

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

<!--
Can be tested using:
java -jar editor/lib/saxon9.jar -s editor/test/examples/testcase-BPMN2XForms-001.rdf -xsl editor/client/xslt/BPMN2XForms.xslt
An according output file for comparison resides at:
editor/test/examples/testcase-BPMN2XForms-001.xhtml
-->

<!--
Explicitly supported cases are:
    - Parallel gateways that are split and join at the same time
    - XForms that have multiple instances and cases (but not models)
-->

<!-- TODO avoid the following assumptions:
     - only one model in imported XForms
     - only one start event in the process
     - the last case in the xml of an imported XForms is expected to be the
       response/result case or the last one in a series of cases
     - unsure: parallel splits have to be block structured
     - unsure: free of loops
     - unsure: only one activity in each branch of a parallel split
-->
<!-- TODO use different IDs for models and cases -->
<!-- TODO highlight current activity in process model using MOVI API -->
<!-- TODO support event-based exclusive gateway -->

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"

    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:oryx="http://oryx-editor.org/"
    xmlns:raziel="http://raziel.org/"

    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xforms="http://www.w3.org/2002/xforms"
    xmlns:events="http://www.w3.org/2001/xml-events"

    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
    xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/"
>

    <!-- controls output of debug messages -->
    <xsl:param name="debug" select="true()" />

    <xsl:output method="xml" indent="yes" encoding="UTF-8"
        doctype-public="-//W3C//DTD XHTML Basic 1.1//EN"
        doctype-system="http://www.w3.org/TR/xhtml-basic/xhtml-basic11.dtd"
        cdata-section-elements="script style"
    />

    <!-- Suppress text output by default for all but the *-copy modes. -->
    <xsl:template match="text()|@*"/>
    <xsl:template match="text()|@*" mode="model"/>
    <xsl:template match="text()|@*" mode="view"/>
    <xsl:template match="text()|@*" mode="view-for-parallel-gateways"/>

    <!-- Root Node -->
    <xsl:template match="/">
        <!-- TODO remove these variables, which are specific to the test case -->
        <xsl:variable name="test1" select="'http://localhost:8180/backend/poem/model/237/self#oryx_606560F1-BE0B-4130-81CC-F77202E2F27A'"/>
        <xsl:variable name="test2" select="'http://localhost:8180/backend/poem/model/237/self#oryx_E6E357E0-6407-4221-9C4F-E4CE9F5FE42F'"/>
        <xsl:variable name="test3" select="'http://localhost:8180/backend/poem/model/237/self#oryx_1A426703-F199-4123-BE6B-452A2900ADDF'"/>
        <xsl:variable name="test4" select="'http://localhost:8180/backend/poem/model/237/self#oryx_249FD0F7-EEFF-4A08-ACB5-D3F23F1E0A42'"/>
        <xsl:variable name="test5" select="'http://localhost:8180/backend/poem/model/237/self#oryx_24055331-44F5-483D-A612-6ABB2E609096'"/>
        <xsl:if test="$debug"><xsl:message>root</xsl:message></xsl:if>
        <xhtml:html>
            <xhtml:head>
                <xhtml:title>Aggregated User Interface</xhtml:title>
                <xhtml:style type="text/css">
                    <xsl:call-template name="css"/>
                </xhtml:style>
                <!--
                <xforms:model id="execution_state">
                    <xforms:instance id="parallel_splits">
                        <xhtml:div class="execution_state">
                            <xhtml:span id=""></xhtml:span>
                        </xhtml:div>
                    </xforms:instance>
                </xforms:model>
                -->
                <xsl:apply-templates select="//rdf:Description" mode="model"/>
                <!-- [ @rdf:about = $test1 or @rdf:about = $test2 or @rdf:about = $test4 ] -->
                <!-- [ oryx:name = 'check Company Name'  or oryx:name = 'check Disqualified Directors Register' ] -->
            </xhtml:head>
            <xhtml:body>
                <xhtml:h1>
                    <xsl:value-of select="//rdf:Description[ oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#BPMNDiagram' ]/oryx:name"/>
                </xhtml:h1>
                <!--
                <xforms:output model="execution_state" ref="instance('parallel_splits')"/>
                -->
                <xforms:switch>
                    <xforms:case id="process_model_hidden" selected="true">
                        <xforms:trigger>
                            <xforms:label>Show Process Model</xforms:label>
                            <xforms:toggle events:event="DOMActivate" case="process_model_shown"/>
                        </xforms:trigger>
                    </xforms:case>
                    <xforms:case id="process_model_shown" selected="true">
                        <xforms:trigger>
                            <xforms:label>Hide Process Model</xforms:label>
                            <xforms:toggle events:event="DOMActivate" case="process_model_hidden"/>
                        </xforms:trigger>
                        <xhtml:img src="{
                            substring-before(
                                //rdf:Description[
                                    oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#BPMNDiagram'
                                ][1]/@rdf:about,
                                '/self'
                            )}/png"/>
                    </xforms:case>
                </xforms:switch>
                <!--
                -->
                <!--
                <xsl:apply-templates select="//rdf:Description[ oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#AND_Gateway' ]" mode="view-for-parallel-gateways"/>
                -->
                <xforms:switch>
                    <xforms:case id="no_gateway" selected="true"/>
                    <xsl:apply-templates select="//rdf:Description[ oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#AND_Gateway' ]" mode="view-for-parallel-gateways"/>
                </xforms:switch>
                <xforms:switch>
                    <xforms:case id="no_node" selected="false"/>
                    <xsl:apply-templates select="//rdf:Description" mode="view"/>
                </xforms:switch>
            </xhtml:body>
        </xhtml:html>
    </xsl:template>

    <!-- XForms Model -->
    <xsl:template match="rdf:Description[ oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#Task' ]" mode="model">
        <xsl:if test="$debug">
            <xsl:message/>
            <xsl:message><xsl:value-of select="concat('Model for ', substring-after(oryx:type, '#'), ': ', oryx:name)"/></xsl:message>
            <xsl:message><xsl:value-of select="concat('    id: ', @rdf:about)"/></xsl:message>
            <xsl:message><xsl:value-of select="concat('    ui: ', oryx:xform)"/></xsl:message>
        </xsl:if>
        <xsl:variable name="model" select="@rdf:about"/>
        <xsl:variable name="xformURL" select="oryx:xform"/>
        <xsl:for-each select="document($xformURL)//xforms:model">
            <xforms:model id="{concat($model, '_model_', @id)}">
                <!-- TODO generate a unique id for each model
                     maybe by adding existing model IDs to the Oryx task ID.
                     Problem with that: nested elements with relative references
                     Solution might be to check for the instance() function inside
                     ref attributes
                -->
                <xsl:apply-templates select="./*" mode="model-copy">
                    <xsl:with-param name="xformURL" select="$xformURL"/>
                </xsl:apply-templates>
            </xforms:model>
        </xsl:for-each>
    </xsl:template>

    <!-- copy all model elements -->
    <xsl:template match="node() | @*" mode="model-copy">
        <xsl:param name="xformURL"/>
        <xsl:copy>
            <xsl:apply-templates select="@* | node()" mode="model-copy"> 
                <xsl:with-param name="xformURL" select="$xformURL"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <!-- support external for instances with relative URLs -->
    <xsl:template match="xforms:instance/@src" mode="model-copy">
        <xsl:param name="xformURL"/>
        <xsl:if test="$debug">
            <xsl:message><xsl:value-of select="concat('    instance: ', .)"/></xsl:message>
        </xsl:if>
        <xsl:attribute name="src">
            <!-- TODO parse URL correctly not just with heuristics -->
            <xsl:if test="not(contains(., '://'))">
                <!-- add directory URL -->
                <xsl:call-template name="substring-before-last"> 
                    <xsl:with-param name="input" select="$xformURL" /> 
                    <xsl:with-param name="substr" select="'/'" /> 
                </xsl:call-template> 
                <xsl:text>/</xsl:text>
            </xsl:if>
            <xsl:value-of select="."/>
        </xsl:attribute>
    </xsl:template>


    <!-- XForms View -->

    <!-- Start Events and Exclusive Gateways -->
    <!-- TODO decide Data-based Exclusive (XOR) Gateways automatically using XPath -->
    <xsl:template mode="view" match="rdf:Description[
        (
            starts-with(oryx:type, 'http://b3mn.org/stencilset/bpmn1.1#Start')
            and
            ends-with(oryx:type, 'Event')
        )
        or
        oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#Exclusive_Eventbased_Gateway'
        or
        oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#Exclusive_Databased_Gateway'
    ]">
        <xsl:if test="$debug">
            <xsl:message/>
            <xsl:message><xsl:value-of select="concat('View for ', substring-after(oryx:type, '#'), ': ', oryx:name)"/></xsl:message>
            <xsl:message><xsl:value-of select="concat('    id: ', @rdf:about)"/></xsl:message>
        </xsl:if>
        <xforms:case id="{@rdf:about}">
            <xsl:choose>
                <xsl:when test="
                    starts-with(oryx:type, 'http://b3mn.org/stencilset/bpmn1.1#Start')
                    and
                    ends-with(oryx:type, 'Event')
                ">
                    <xsl:attribute name="selected">true</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="selected">false</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:call-template name="triggersForSuccessors">
                <xsl:with-param name="currentNode" select="."/>
            </xsl:call-template>
        </xforms:case>
    </xsl:template>

    <!-- Parallel Gateways -->
    <xsl:template match="rdf:Description[ oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#AND_Gateway' ]" mode="view-for-parallel-gateways">
        <xsl:if test="$debug">
            <xsl:message/>
            <xsl:message><xsl:value-of select="concat('View for ', substring-after(oryx:type, '#'), ': ', oryx:name)"/></xsl:message>
            <xsl:message><xsl:value-of select="concat('    id: ', @rdf:about)"/></xsl:message>
        </xsl:if>
        <xforms:case id="{@rdf:about}" selected="false">
            <xsl:variable name="outgoingSequenceFlows">
                <xsl:call-template name="getOutgoingSequenceFlows">
                    <xsl:with-param name="currentNode" select="."/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="incomingSequenceFlows">
                <xsl:call-template name="getIncomingSequenceFlows">
                    <xsl:with-param name="currentNode" select="."/>
                </xsl:call-template>
            </xsl:variable>
            <!-- determine gateway type -->
            <xsl:variable name="isSplit" select="count($outgoingSequenceFlows/rdf:Description) &gt; 1"/>
            <xsl:variable name="isJoin" select="count($incomingSequenceFlows/rdf:Description) &gt; 1"/>
            <xsl:if test="$debug">
                <!-- a gateway can be split and join at the same time -->
                <xsl:if test="$isSplit">
                    <xsl:message>    Gateway is a Split</xsl:message>
                </xsl:if>
                <xsl:if test="$isJoin">
                    <xsl:message>    Gateway is a Join</xsl:message>
                </xsl:if>
            </xsl:if>
            <xsl:call-template name="triggersForSuccessors">
                <xsl:with-param name="currentNode" select="."/>
                <xsl:with-param name="hideParallelSplit" select="$isJoin"/>
            </xsl:call-template>
        </xforms:case>
    </xsl:template>

    <!-- End Events -->
    <xsl:template match="rdf:Description[ oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#EndEvent' ]" mode="view">
        <xsl:if test="$debug">
            <xsl:message/>
            <xsl:message><xsl:value-of select="concat('View for ', substring-after(oryx:type, '#'), ': ', oryx:name)"/></xsl:message>
            <xsl:message><xsl:value-of select="concat('    id: ', @rdf:about)"/></xsl:message>
        </xsl:if>
        <xforms:case id="{@rdf:about}" selected="false">
            <xsl:text>The process is finished.</xsl:text>
            <xhtml:br/>
            <!-- TODO either reset the form on restart or remove trigger -->
            <xsl:call-template name="trigger">
                <xsl:with-param name="nextNodes" select="
                    //rdf:Description [
                        oryx:type='http://b3mn.org/stencilset/bpmn1.1#StartEvent'
                    ]
                "/>
                <xsl:with-param name="label" select="'Restart the Process'"/>
            </xsl:call-template>
        </xforms:case>
    </xsl:template>

    <!-- Tasks -->
    <xsl:template match="rdf:Description[ oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#Task' ]" mode="view">
        <xsl:if test="$debug">
            <xsl:message/>
            <xsl:message><xsl:value-of select="concat('View for ', substring-after(oryx:type, '#'), ': ', oryx:name)"/></xsl:message>
            <xsl:message><xsl:value-of select="concat('    id: ', @rdf:about)"/></xsl:message>
            <xsl:message><xsl:value-of select="concat('    ui: ', oryx:xform)"/></xsl:message>
        </xsl:if>
        <xforms:case id="{@rdf:about}" selected="false">
            <xhtml:h2><xsl:value-of select="oryx:name"/></xhtml:h2>
            <xsl:variable name="nextNodes">
                <xsl:call-template name="getNextNodes">
                    <xsl:with-param name="currentNode" select="."/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:choose>
                <xsl:when test="string-length(oryx:xform) &gt; 0">
                    <xsl:apply-templates select="document(oryx:xform)//xhtml:body/*" mode="view-copy">
                        <xsl:with-param name="model" select="@rdf:about"/>
                        <xsl:with-param name="nextNodes" select="$nextNodes/rdf:Description"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <!-- TODO shadow processing if a service has been defined -->
                    <xhtml:p>No user interface has been specified for this activity.</xhtml:p>
                    <xsl:call-template name="trigger">
                        <xsl:with-param name="nextNodes" select="$nextNodes/rdf:Description"/>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xforms:case>
    </xsl:template>

    <!-- copy all view elements, but add respectively exchange model references -->
    <xsl:template match="node() | @*" mode="view-copy">
        <xsl:param name="model"/>
        <xsl:param name="nextNodes"/>
        <xsl:copy>
            <!-- TODO check whether submissions can be reference via models
                 if not: make submission IDs unique -->
            <xsl:if test="@ref or @submission">
                <xsl:attribute name="model">
                    <!-- TODO avoid assumption that model references are always
                         used if an XForm contains only one model, which has an
                         id attribute -->
                    <!-- avoids assumption of only one model per input file -->
                    <xsl:value-of select="concat($model, '_model_', @model)"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates select="@* | node()" mode="view-copy"> 
                <xsl:with-param name="model" select="$model"/>
                <xsl:with-param name="nextNodes" select="$nextNodes"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <!-- remove existing model attributes from view elements -->
    <xsl:template match="@model" mode="view-copy"/>

    <!-- add trigger for the next node to the last imported case -->
    <xsl:template match="xforms:case[last()]" mode="view-copy">
        <xsl:param name="model"/>
        <xsl:param name="nextNodes"/>
        <xsl:if test="$debug"><xsl:message><xsl:value-of select="concat('    last case id: ', @id)"/></xsl:message></xsl:if>
        <xsl:copy>
            <xsl:apply-templates select="@* | node()" mode="view-copy"> 
                <xsl:with-param name="model" select="$model"/>
                <xsl:with-param name="nextNodes" select="$nextNodes"/>
            </xsl:apply-templates>
            <xsl:call-template name="trigger">
                <xsl:with-param name="nextNodes" select="$nextNodes"/>
            </xsl:call-template>
        </xsl:copy>
    </xsl:template>
    

    <!-- Named Templates -->

    <!-- named template to create triggers for the successors of the current task -->
    <xsl:template name="triggersForSuccessors">
        <xsl:param name="currentNode"/>
        <xsl:param name="label"/>
        <xsl:param name="hideParallelSplit" select="false()"/>
        <xsl:variable name="nextNodes">
            <xsl:call-template name="getNextNodes">
                <xsl:with-param name="currentNode" select="$currentNode"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:call-template name="trigger">
            <xsl:with-param name="label" select="$label"/>
            <xsl:with-param name="nextNodes" select="$nextNodes/rdf:Description"/>
            <xsl:with-param name="hideParallelSplit" select="$hideParallelSplit"/>
        </xsl:call-template>
    </xsl:template>

    <!-- named template that returns the successors of the current task -->
    <xsl:template name="getNextNodes">
        <xsl:param name="currentNode"/>
        <xsl:variable name="outgoingSequenceFlows">
            <xsl:call-template name="getOutgoingSequenceFlows">
                <xsl:with-param name="currentNode" select="$currentNode"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="nextNodes" select="
            //rdf:Description [
                @rdf:about = $outgoingSequenceFlows/rdf:Description/raziel:outgoing/@rdf:resource
            ]
        "/>
        <xsl:if test="$debug">
            <xsl:message>    Next Nodes: </xsl:message>
            <xsl:for-each select="$nextNodes">
                <xsl:message><xsl:value-of select="concat('        ', @rdf:about)"/></xsl:message>
            </xsl:for-each>
        </xsl:if>
        <xsl:copy-of select="
            //rdf:Description [
                @rdf:about = $outgoingSequenceFlows/rdf:Description/raziel:outgoing/@rdf:resource
            ]
        "/>
    </xsl:template>

    <!-- named template that returns the outgoing sequence flows of the current node -->
    <xsl:template name="getOutgoingSequenceFlows">
        <xsl:param name="currentNode"/>
        <xsl:variable name="outgoingSequenceFlows" select="
            //rdf:Description [
                oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#SequenceFlow'
                and
                @rdf:about = $currentNode/raziel:outgoing/@rdf:resource
            ]
        "/>
        <xsl:if test="$debug">
            <xsl:message>    Outgoing Sequence Flows: </xsl:message>
            <xsl:for-each select="$outgoingSequenceFlows">
                <xsl:message><xsl:value-of select="concat('        ', @rdf:about)"/></xsl:message>
            </xsl:for-each>
        </xsl:if>
        <xsl:copy-of select="$outgoingSequenceFlows"/>
    </xsl:template>

    <!-- named template that returns the incoming sequence flows of the current node -->
    <xsl:template name="getIncomingSequenceFlows">
        <xsl:param name="currentNode"/>
        <xsl:variable name="incomingSequenceFlows" select="
            //rdf:Description [
                oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#SequenceFlow'
                and
                raziel:outgoing/@rdf:resource = $currentNode/@rdf:about
            ]
        "/>
        <xsl:if test="$debug">
            <xsl:message>    Incoming Sequence Flows: </xsl:message>
            <xsl:for-each select="$incomingSequenceFlows">
                <xsl:message><xsl:value-of select="concat('        ', @rdf:about)"/></xsl:message>
            </xsl:for-each>
        </xsl:if>
        <xsl:copy-of select="$incomingSequenceFlows"/>
    </xsl:template>

    <!-- named template to create triggers for the next node -->
    <xsl:template name="trigger">
        <xsl:param name="nextNodes"/>
        <xsl:param name="label"/>
        <xsl:param name="hideParallelSplit" select="false()"/>
        <xsl:for-each select="$nextNodes">
            <xforms:trigger>
                <xforms:label>
                    <xsl:choose>
                        <xsl:when test="$label != ''">
                            <xsl:value-of select="$label"/>
                        </xsl:when>
                        <xsl:when test="oryx:name != ''">
                            <xsl:value-of select="oryx:name"/>
                        </xsl:when>
                        <xsl:when test="oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#EndEvent'">
                            <xsl:text>Finish</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>Next</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xforms:label>
                <xsl:if test="$hideParallelSplit">
                    <xforms:toggle events:event="DOMActivate" case="no_gateway"/>
                </xsl:if>
                <xsl:if test="oryx:type = 'http://b3mn.org/stencilset/bpmn1.1#AND_Gateway'">
                    <xforms:toggle events:event="DOMActivate" case="no_node"/>
                </xsl:if>
                <xforms:toggle events:event="DOMActivate" case="{@rdf:about}"/>
            </xforms:trigger>
        </xsl:for-each>
    </xsl:template>

    <!-- named template containing the cascading stylesheet for the output -->
    <xsl:template name="css">
        <xsl:if test="$debug"><xsl:message>css</xsl:message></xsl:if>
      <xsl:text><![CDATA[
        @namespace xf url("http://www.w3.org/2002/xforms");
        @namespace url("http://www.w3.org/1999/xhtml");

        body {
            font-size: 75%;
            font-family: sans-serif;
        }

        div {
            padding: 5px 0px 5px 0px;
            vertical-align: top;
        }

        xf|input, 
        xf|secret, 
        xf|textarea, 
        xf|output, 
        xf|trigger, 
        xf|submit,
        xf|select,
        xf|select1 {
            display: table-cell;
            vertical-align: bottom;
            line-height: 1em;
            padding: 0px 5px 0px 5px;
        }

        xf|input > xf|label, 
        xf|secret > xf|label, 
        xf|textarea > xf|label, 
        xf|output > xf|label, 
        xf|trigger > xf|label, 
        xf|submit > xf|label,
        xf|select > xf|label,
        xf|select1 > xf|label {
            display: table-caption;
            white-space: nowrap; /* avoids word wrapping of labels */
            text-align: left;
            font-weight: bold;
            width: 100%;
        }

        xf|group {
            display: table-cell;
            line-height: 1em;
            vertical-align: top;
            margin: 5px; /* doesn't work; I guess, because of display: table-cell; */
            padding: 5px;
            border-collapse: collapse;
            border: 1px dashed grey;
        }

        xf|group > xf|label {
            display: table-caption;
            font-weight: bold;
            text-align: center;
            background-color: #cccccc;
            margin-bottom: 7px;
            padding: 3px 0px 3px 0px;
            width: 100%;
        }
         
        *:required {
            background-color: yellow;
        }
         
        *:invalid  {
            background-color: pink;
        }
      ]]></xsl:text>
    </xsl:template>

    <xsl:template name="substring-before-last">
        <xsl:param name="input" /> 
        <xsl:param name="substr" /> 
        <xsl:if test="$substr and contains($input, $substr)"> 
            <xsl:variable name="temp" select="substring-after($input, $substr)" /> 
            <xsl:value-of select="substring-before($input, $substr)" /> 
            <xsl:if test="contains($temp, $substr)"> 
                <xsl:value-of select="$substr" /> 
                <xsl:call-template name="substring-before-last"> 
                    <xsl:with-param name="input" select="$temp" /> 
                    <xsl:with-param name="substr" select="$substr" /> 
                </xsl:call-template> 
            </xsl:if> 
        </xsl:if> 
    </xsl:template>

</xsl:stylesheet>
