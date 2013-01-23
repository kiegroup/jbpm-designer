<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha"
	xmlns:x="http://www.w3.org/1999/xhtml">

  <!-- ********* Template for splitting whitespace separeted String lists for elements without attributes ********** -->
  <xsl:template name="string-tokens">
    <xsl:param name="list" />
    <xsl:param name="elementName" />
    <xsl:variable name="newlist" select="concat(normalize-space($list), ' ')" />
    <xsl:variable name="first" select="substring-before($newlist, ' ')" />
    <xsl:variable name="remaining" select="substring-after($newlist, ' ')" />
    <xsl:if test="string-length(normalize-space($first))>0">
      <xsl:element name="{$elementName}">
        <xsl:value-of select="$first" />
      </xsl:element>
    </xsl:if>
    <xsl:if test="$remaining">
      <xsl:call-template name="string-tokens">
        <xsl:with-param name="list" select="$remaining" />
        <xsl:with-param name="elementName" select="$elementName"/>
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <!-- ********* Template for splitting whitespace separated String lists for categories  ********** -->
  <xsl:template name="category-tokens">
    <xsl:param name="list" />
    <xsl:variable name="newlist" select="concat(normalize-space($list), ' ')" />
    <xsl:variable name="first" select="substring-before($newlist, ' ')" />
    <xsl:variable name="remaining" select="substring-after($newlist, ' ')" />
    <xsl:if test="string-length(normalize-space($first))>0">
      <xpdl:Category Id="{concat($first, '_id')}" Name="{$first}"/>
    </xsl:if>
    <xsl:if test="$remaining">
      <xsl:call-template name="category-tokens">
        <xsl:with-param name="list" select="$remaining" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <!-- ********* Template for splitting whitespace separeted String lists of correlation properties ********** -->
  <xsl:template name="properties-tokens">
    <xsl:param name="list" />
    <xsl:variable name="newlist" select="concat(normalize-space($list), ' ')" />
    <xsl:variable name="first" select="substring-before($newlist, ' ')" />
    <xsl:variable name="remaining" select="substring-after($newlist, ' ')" />
    <xsl:if test="string-length(normalize-space($first))>0">
      <element name="{$first}"/>
    </xsl:if>
    <xsl:if test="$remaining">
      <xsl:call-template name="properties-tokens">
        <xsl:with-param name="list" select="$remaining" />
      </xsl:call-template>
    </xsl:if>
  </xsl:template>

  <!--  **************************** Template for Object element ****************** -->
  <xsl:template name="object">
    <xsl:param name="objectParent" />
    <!-- <xpdl:Object Id="{$objectParent/x:span[@class='oryx-id']}"> -->
    <xpdl:Object
      Id="{generate-id(.)}">
      <xsl:variable name="categories" select="$objectParent/x:span[@class='oryx-categories']" />
      <xsl:variable name="documentation" select="$objectParent/x:span[@class='oryx-documentation']" />
      <!-- Categories -->
      <xsl:if test="string-length(normalize-space($categories))>0">
        <xpdl:Categories>
          <xsl:call-template name="category-tokens">
            <xsl:with-param name="list" select="$categories" />
          </xsl:call-template>
        </xpdl:Categories>
      </xsl:if>
      <!-- Documentation -->
      <xsl:if test="string-length(normalize-space($documentation))>0">
        <xpdl:Documentation>
          <xsl:value-of select="$documentation" />
        </xpdl:Documentation>
      </xsl:if>
    </xpdl:Object>
  </xsl:template>

  <!--  **************************** Template for determing containing lane recursively ****************** -->
  <xsl:template name="getParentLane">
    <xsl:param name="node" />
    <xsl:variable name="parentRef" select="$node/x:a[@rel='raziel-parent']/@href" />
    <xsl:variable name="parent" select="//x:div[@id=substring($parentRef,2)]" />
    <xsl:choose>
      <xsl:when test="$parent/x:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#Lane'">
        <xsl:value-of select="$parent/@id"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:if test="string-length(normalize-space($parentRef))>0">
          <xsl:call-template name="getParentLane">
            <xsl:with-param name="node" select="$parent"/>
          </xsl:call-template>
        </xsl:if>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>


  <!--  **************************** Template for NodeGraphicsInfo element ****************** -->
  <xsl:template name="nodeGraphicsInfos">
    <xsl:param name="node" />
    <xsl:variable name="bounds" select="$node/x:span[@class='oryx-bounds']" />
    <xsl:variable name="x" select="substring-before($bounds, ',')" />
    <xsl:variable name="remaining1" select="substring-after($bounds, ',')" />
    <xsl:variable name="y" select="substring-before($remaining1, ',')" />
    <xsl:variable name="remaining2" select="substring-after($remaining1, ',')" />
    <xsl:variable name="width" select="substring-before($remaining2, ',')" />
    <xsl:variable name="height" select="substring-after($remaining2, ',')" />
    <xsl:variable name="laneId">
      <xsl:call-template name="getParentLane">
        <xsl:with-param name="node" select="$node"/>
      </xsl:call-template>
    </xsl:variable>
    <xpdl:NodeGraphicsInfos>
      <xpdl:NodeGraphicsInfo Width="{$width - $x}" Height="{$height - $y}" LaneId="{$laneId}">
        <xpdl:Coordinates XCoordinate="{$x}" YCoordinate="{$y}" />
      </xpdl:NodeGraphicsInfo>
    </xpdl:NodeGraphicsInfos>
  </xsl:template>

  <!--  **************************** Template for timer trigger ****************** -->
  <xsl:template name="triggerTimer">
    <xsl:variable name="timeDate" select="x:span[@class='oryx-timedate']" />
    <xsl:variable name="timeCycle" select="x:span[@class='oryx-timecycle']" />
    <xpdl:TriggerTimer>
      <xsl:if test="string-length(normalize-space($timeDate))>0">
        <xsl:attribute name="TimeDate">
          <xsl:value-of select="$timeDate" />
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="string-length(normalize-space($timeCycle))>0">
        <xsl:attribute name="TimeCycle">
          <xsl:value-of select="$timeCycle" />
        </xsl:attribute>
      </xsl:if>
    </xpdl:TriggerTimer>
  </xsl:template>

  <!-- ********************** Template for trigger-result message **************-->
  <xsl:template name="triggerResultMessage">
    <xsl:param name="id" />
    <xpdl:TriggerResultMessage>
      <xpdl:Message Id="{concat($id,'_message')}"/>
    </xpdl:TriggerResultMessage>
  </xsl:template>

  <!-- ************** Template for generating Activities content ******************** -->
  <xsl:template name="activities">
    <xsl:param name="activities" />
    <xsl:for-each select="$activities">
      <xsl:variable name="type" select="x:span[@class='oryx-activitytype']" />
      <xsl:variable name="gatewayType" select="x:span[@class='oryx-gatewaytype']" />
      <xsl:variable name="eventType" select="x:span[@class='oryx-eventtype']" />
      <xsl:variable name="loopType" select="x:span[@class='oryx-looptype']" />
      <!-- <xsl:variable name="id" select="x:span[@class='oryx-id']" /> -->
      <xsl:variable name="id" select="@id" />
      <xsl:variable name="name" select="x:span[@class='oryx-name']" />
      <xsl:if test="string-length(normalize-space($type))>0 or 
				string-length(normalize-space($gatewayType))>0 or 
				string-length(normalize-space($eventType))>0">
        <xpdl:Activity
					Id="{$id}">
          <xsl:if test="string-length(normalize-space($name))>0">
            <xsl:attribute name="Name">
              <xsl:value-of select="$name" />
            </xsl:attribute>
          </xsl:if>
          <!-- ******************** Route ********************** -->
          <xsl:if test="string-length(normalize-space($gatewayType))>0">
            <xsl:variable name="instantiate" select="x:span[@class='oryx-instantiate']" />
            <xpdl:Route GatewayType="{$gatewayType}">
              <xsl:if test="$instantiate">
                <xsl:attribute name="Instantiate">
                  <xsl:value-of select="$instantiate" />
                </xsl:attribute>
              </xsl:if>
            </xpdl:Route>
            <xsl:if test="$gatewayType='XOR'">
              <xpdl:TransitionRestrictions>
                <xpdl:TransitionRestriction>
                  <xsl:if test="x:span[@class='oryx-xortype']='Event'">
                    <xpdl:Split Type="XOREVENT" />
                  </xsl:if>
                  <xsl:if test="x:span[@class='oryx-xortype']='Data'">
                    <xpdl:Split Type="XOR" />
                    <!-- TODO: transition Refs depending on defined transition order? -->
                  </xsl:if>
                </xpdl:TransitionRestriction>
              </xpdl:TransitionRestrictions>
            </xsl:if>
          </xsl:if>
          <!-- ******************** XPDL Tasks ********************** -->
          <xsl:if test="$type='Task'">
            <xsl:variable name="taskType" select="x:span[@class='oryx-tasktype']" />
            <xsl:if test="$taskType='Service'">
              <xpdl:Implementation>
                <xpdl:Task>
                  <xpdl:TaskService>
                    <xpdl:MessageIn Id="{concat($id,'_messagein')}"/>
                    <xpdl:MessageOut Id="{concat($id,'_messageout')}"/>
                  </xpdl:TaskService>
                </xpdl:Task>
              </xpdl:Implementation>
            </xsl:if>
            <xsl:if test="$taskType='Receive'">
              <xpdl:Implementation>
                <xpdl:Task>
                  <xpdl:TaskReceive
										Instantiate="{x:span[@class='oryx-instantiate']}">
                    <xpdl:Message Id="{concat($id,'_message')}"/>
                  </xpdl:TaskReceive>
                </xpdl:Task>
              </xpdl:Implementation>
            </xsl:if>
            <xsl:if test="$taskType='Send'">
              <xpdl:Implementation>
                <xpdl:Task>
                  <xsl:variable name="faultName" select="x:span[@class='oryx-faultname']" />
                  <xpdl:TaskSend>
                    <xpdl:Message Id="{concat($id,'_message')}"/>
                  </xpdl:TaskSend>
                </xpdl:Task>
              </xpdl:Implementation>
            </xsl:if>
          </xsl:if>
          <!-- ************* BlockActivitiy ****************** -->
          <xsl:if test="$type='Sub-Process'">
            <xsl:variable name="index" select="generate-id(.)" />
            <xpdl:BlockActivity 
							ActivitySetId="{concat(concat($id,'_activitySet_'),$index)}">
            </xpdl:BlockActivity>
          </xsl:if>
          <!-- ******************** Event ********************** -->
          <xsl:if test="string-length(normalize-space($eventType))>0">
            <xpdl:Event>
              <xsl:variable name="trigger" select="x:span[@class='oryx-trigger']" />
              <xsl:if test="$eventType='Start'" >
                <xpdl:StartEvent Trigger="{$trigger}">
                  <xsl:if test="$trigger='Message'">
                    <xsl:call-template name="triggerResultMessage">
                      <xsl:with-param name="id" select="$id" />
                    </xsl:call-template>
                  </xsl:if>
                  <xsl:if test="$trigger='Timer'">
                    <xsl:call-template name="triggerTimer" />
                  </xsl:if>
                </xpdl:StartEvent>
              </xsl:if>
              <xsl:if test="$eventType='Intermediate'" >
                <xsl:variable name="target" select="x:span[@class='oryx-target']" />
                <xsl:variable name="targetRef" select="//x:div[x:a[@rel='raziel-outgoing' and @href=concat('#',$id)]]/@id" />
                <xpdl:IntermediateEvent>
                  <xsl:attribute name="Trigger">
                    <xsl:value-of select="$trigger"></xsl:value-of>
                  </xsl:attribute>
                  <xsl:if test="string-length(normalize-space($target))>0 or string-length(normalize-space($targetRef))>0">
                    <xsl:choose>
                      <xsl:when test="string-length(normalize-space($targetRef))>0">
                        <xsl:attribute name="Target">
                          <xsl:value-of select="$targetRef" />
                        </xsl:attribute>
                      </xsl:when>
                      <xsl:otherwise>
                        <xsl:attribute name="Target">
                          <xsl:value-of select="$target" />
                        </xsl:attribute>
                      </xsl:otherwise>
                    </xsl:choose>
                  </xsl:if>
                  <xsl:if test="$trigger='Message'">
                    <xsl:call-template name="triggerResultMessage">
                      <xsl:with-param name="id" select="$id" />
                    </xsl:call-template>
                  </xsl:if>
                  <xsl:if test="$trigger='Timer'">
                    <xsl:call-template name="triggerTimer" />
                  </xsl:if>
                  <xsl:if test="$trigger='Error'">
                    <xpdl:ResultError ErrorCode="{x:span[@class='oryx-errorcode']}" />
                  </xsl:if>
                  <xsl:if test="$trigger='Compensation'">
                    <xsl:variable name="activity" select="x:span[@class='oryx-activity']" />
                    <xpdl:ResultCompensation>
                      <xsl:if test="string-length(normalize-space($activity))>0" >
                        <xsl:attribute name="ActivityId">
                          <xsl:value-of select="$activity" />
                        </xsl:attribute>
                      </xsl:if>
                    </xpdl:ResultCompensation>
                  </xsl:if>
                </xpdl:IntermediateEvent>
              </xsl:if>
              <xsl:if test="$eventType='End'" >
                <xpdl:EndEvent>
                  <xsl:variable name="result" select="x:span[@class='oryx-result']" />
                  <xsl:attribute name="Result">
                    <xsl:value-of select="$result"></xsl:value-of>
                  </xsl:attribute>
                  <xsl:if test="$trigger='Message'">
                    <xsl:call-template name="triggerResultMessage">
                      <xsl:with-param name="id" select="$id" />
                    </xsl:call-template>
                  </xsl:if>
                  <xsl:if test="$trigger='Timer'">
                    <xsl:call-template name="triggerTimer" />
                  </xsl:if>
                  <xsl:if test="$trigger='Error'">
                    <xpdl:ResultError ErrorCode="{x:span[@class='oryx-errorcode']}" />
                  </xsl:if>
                  <xsl:if test="$trigger='Compensation'">
                    <xsl:variable name="activity" select="x:span[@class='oryx-activity']" />
                    <xpdl:ResultCompensation>
                      <xsl:if test="string-length(normalize-space($activity))>0" >
                        <xsl:attribute name="ActivityId">
                          <xsl:value-of select="$activity" />
                        </xsl:attribute>
                      </xsl:if>
                    </xpdl:ResultCompensation>
                  </xsl:if>
                </xpdl:EndEvent>
              </xsl:if>
            </xpdl:Event>
          </xsl:if>
          <!-- ************* Loop ******************** -->
          <xsl:if test="$loopType != 'None'">
            <xpdl:Loop LoopType="{$loopType}">
              <xsl:if test="$loopType ='Standard'">
                <xpdl:LoopStandard
									LoopCondition="{x:span[@class='oryx-loopcondition']}"
									TestTime="{x:span[@class='oryx-testtime']}">
                </xpdl:LoopStandard>
              </xsl:if>
              <xsl:if test="$loopType='MultiInstance'">
                <xpdl:LoopMultiInstance
                 MI_Condition="{x:span[@class='oryx-mi_condition']}"
                 MI_Ordering="{x:span[@class='oryx-mi_ordering']}"
                 MI_FlowCondition="{x:span[@class='oryx-mi_flowcondition']}">
                </xpdl:LoopMultiInstance>
              </xsl:if>
            </xpdl:Loop>
          </xsl:if>

          <!-- add Object element -->
          <xsl:call-template name="object">
            <xsl:with-param name="objectParent" select="." />
          </xsl:call-template>

          <!-- add NodeGraphicsInfos -->
          <xsl:call-template name="nodeGraphicsInfos">
            <xsl:with-param name="node" select="." />
          </xsl:call-template>
        </xpdl:Activity>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <!-- ************** Template for extracting relevant Transitions ******************** -->
  <xsl:template name="relevantTransitions">
    <xsl:param name="resId" />
    <xsl:variable name="activitiesOutRefs" select="//x:div[x:a[@rel='raziel-parent' and @href=concat('#',$resId)]]/x:a[@rel='raziel-outgoing']"/>
    <xsl:for-each select="$activitiesOutRefs">
      <xsl:variable name="outRef" select="@href"/>
      <xsl:variable name="relevantTransitions" select="//x:div[x:a[@rel='raziel-parent' and @href='#oryx-canvas123'] and @id=substring($outRef,2)]"/>
      <xsl:call-template name="transitions">
        <xsl:with-param name="transitions" select="$relevantTransitions" />
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

  <!-- ************** Template for generating Transitions content ******************** -->
  <xsl:template name="transitions">
    <xsl:param name="transitions" />
    <xsl:for-each select="$transitions">
      <xsl:variable name="type" select="x:span[@class='oryx-type']" />
      <!-- <xsl:variable name="id" select="x:span[@class='oryx-id']" /> -->
      <xsl:variable name="id" select="@id" />
      <xsl:variable name="name" select="x:span[@class='oryx-name']" />
      <xsl:variable name="internalOutRef" select="x:a[@rel='raziel-outgoing']/@href" />
      <xsl:variable name="conditionType" select="x:span[@class='oryx-conditiontype']" />
      <xsl:if test="$type='http://b3mn.org/stencilset/bpmn1.1#SequenceFlow'">
        <xpdl:Transition
					Id="{$id}"
					From="{//x:div[x:a[@rel='raziel-outgoing' and @href=concat('#',$id)]]/@id}"
					To="{//x:div[@id=substring($internalOutRef,2)]/@id}">
          <xsl:if test="string-length(normalize-space($name))>0">
            <xsl:attribute name="Name">
              <xsl:value-of select="$name" />
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="$conditionType='Expression'">
            <xsl:variable name="conditionExpressionLanguage" select="x:span[@class='oryx-conditionexpressionlanguage']" />
            <xpdl:Condition Type="CONDITION">
              <xpdl:Expression>
                <xsl:if test="string-length(normalize-space($conditionExpressionLanguage))>0">
                  <xsl:attribute name="ScriptGrammar">
                    <xsl:value-of select="$conditionExpressionLanguage"/>
                  </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="x:span[@class='oryx-conditionexpression']"/>
              </xpdl:Expression>
            </xpdl:Condition>
          </xsl:if>
          <xsl:if test="$conditionType='Default'">
            <xpdl:Condition Type="OTHERWISE">
            </xpdl:Condition>
          </xsl:if>
        </xpdl:Transition>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <!-- ************** Template for generating ActivitySets content ******************** -->
  <xsl:template name="activitySets">
    <xsl:param name="blockActivities" />
    <xsl:for-each select="$blockActivities">
      <xsl:variable name="type" select="x:span[@class='oryx-activitytype']" />
      <xsl:if test="$type='Sub-Process'">
        <xsl:variable name="resourceId" select="@id" />
        <xsl:variable name="blockActivityId" select="x:span[@class='oryx-id']" />
        <xsl:variable name="innerActivities" select="//x:div[x:a[@rel='raziel-parent' and @href=concat('#',$resourceId)]]"/>
        <xsl:variable name="index" select="generate-id(.)" />
        <xsl:call-template name="activitySets">
          <xsl:with-param name="blockActivities" select="$innerActivities" />
        </xsl:call-template>
        <xpdl:ActivitySet
					Id="{concat(concat($blockActivityId,'_activitySet_'),$index)}">
          <xpdl:Activities>
            <xsl:call-template name="activities">
              <xsl:with-param name="activities" select="$innerActivities" />
            </xsl:call-template>
          </xpdl:Activities>
          <xpdl:Transitions>
            <xsl:call-template name="relevantTransitions">
              <xsl:with-param name="laneId" select="$resourceId" />
            </xsl:call-template>
          </xpdl:Transitions>
        </xpdl:ActivitySet>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <!-- ************** Template for generating Lanes in Pools ******************** -->
  <xsl:template name="lanes">
    <xsl:param name="childLanes" />
    <xsl:param name="poolId" />
    <xsl:if test="count($childLanes)>0">
      <xpdl:Lanes>
        <xsl:for-each select="$childLanes">
          <xsl:variable name="laneName" select="x:span[@class='oryx-name']" />
          <!--<xpdl:Lane Id="{x:span[@class='oryx-id']}"> -->
          <xpdl:Lane 
						Id="{@id}">
            <xsl:if test="string-length(normalize-space($poolId))>0">
              <xsl:attribute name="ParentPool">
                <xsl:value-of select="$poolId"></xsl:value-of>
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="string-length(normalize-space($laneName))>0">
              <xsl:attribute name="Name">
                <xsl:value-of select="$laneName"></xsl:value-of>
              </xsl:attribute>
            </xsl:if>

            <!-- add Object element (Categories and Documentation) -->
            <xsl:call-template name="object">
              <xsl:with-param name="objectParent" select="." />
            </xsl:call-template>

            <!-- add NodeGraphicsInfos -->
            <xsl:call-template name="nodeGraphicsInfos">
              <xsl:with-param name="node" select="." />
            </xsl:call-template>
          </xpdl:Lane>
        </xsl:for-each>
      </xpdl:Lanes>
    </xsl:if>
  </xsl:template>

  <!-- ************** Template for generating XPDL package ******************** -->
  <xsl:template match="/">
    <xpdl:Package
			xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://www.wfmc.org/2004/XPDL2.0alpha http://www.wfmc.org/standards/docs/TC-1025_bpmnxpdl_24.xsd"
      Id="{generate-id(.)}">
      <xpdl:PackageHeader>
        <xpdl:XPDLVersion>2.0</xpdl:XPDLVersion>
        <xpdl:Vendor>Oryx</xpdl:Vendor>
        <xpdl:Created />
      </xpdl:PackageHeader>
      <!-- *********************** Pools ******************************** -->
      <xsl:variable name="Pools" select="//x:div[x:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#Pool']"/>
	  <!-- Get Id of canvas-->
	  <xsl:variable name="canvasId" select="//x:div[x:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#BPMNDiagram']/@id" />
	  <!-- Get elements that are direct children of the canvas (no flows, no other pools) -->
	  <xsl:variable name="canvasChildren" select="//x:div[x:a[@rel='raziel-parent' and @href=concat('#',$canvasId)] 
	  and x:span[@class='oryx-type']!='http://b3mn.org/stencilset/bpmn1.1#MessageFlow'
	  and x:span[@class='oryx-type']!='http://b3mn.org/stencilset/bpmn1.1#UndirectedAssociation'
	  and x:span[@class='oryx-type']!='http://b3mn.org/stencilset/bpmn1.1#DirectedAssociation'
	  and x:span[@class='oryx-type']!='http://b3mn.org/stencilset/bpmn1.1#DataObject'
	  and x:span[@class='oryx-type']!='http://b3mn.org/stencilset/bpmn1.1#Pool']" />
      <xpdl:Pools>
	  <xsl:if test="count($Pools)>0">
		  <xsl:for-each select="$Pools">
			<!-- <xsl:variable name="id" select="x:span[@class='oryx-id']" /> -->
			<xsl:variable name="id" select="@id" />
			<!-- <xpdl:Pool 
							Id="{$id}" 
							Name="{x:span[@class='oryx-name']}"
							Process="{x:span[@class='oryx-processRef']}"
							BoundaryVisible="{x:span[@class='oryx-boundaryvisible']}"> -->
			<xpdl:Pool 
							Id="{$id}" 
							Name="{x:span[@class='oryx-name']}"
							Process="{concat($id,'_process')}"
							BoundaryVisible="{x:span[@class='oryx-boundaryvisible']}">
			  <xsl:variable name="participantRef" select="x:span[@class='oryx-participantRef']" />
			  <xsl:if test="string-length(normalize-space($participantRef))>0">
				<xsl:attribute name="Participant">
				  <xsl:value-of select="$participantRef"></xsl:value-of>
				</xsl:attribute>
			  </xsl:if>

			  <!-- determine Lanes -->
			  <xsl:variable name="childLanes" select="//x:div[x:a[@rel='raziel-parent' and @href=concat('#',$id)]]" />
			  <xsl:call-template name="lanes">
				<xsl:with-param name="childLanes" select="$childLanes" />
				<xsl:with-param name="poolId" select="$id" />
			  </xsl:call-template>

			  <!-- add Object element (Categories and Documentation) -->
			  <xsl:call-template name="object">
				<xsl:with-param name="objectParent" select="." />
			  </xsl:call-template>

			  <!-- add NodeGraphicsInfos -->
			  <xsl:call-template name="nodeGraphicsInfos">
				<xsl:with-param name="node" select="." />
			  </xsl:call-template>
			</xpdl:Pool>
		  </xsl:for-each>
	  </xsl:if>
	  <!-- Check for elements that are not contained in a pool -->
	  <!-- It must also be considered in the part creating the actual workflow processes -->
  	  <xsl:if test="count($canvasChildren)>0">
		<xpdl:Pool 
						Id="{$canvasId}" 
						Name="Background Pool"
						Process="{concat($canvasId,'_process')}"
						BoundaryVisible="false">
			<xpdl:Lanes>
			  <xpdl:Lane 
				Id="{generate-id(.)}" 
				ParentPool="{$canvasId}"
				Name="Background Lane">
				
				<xpdl:Object Id="{generate-id(.)}">
				<xpdl:Documentation><xsl:text>This Lane belongs to the background Pool and has been created automatically.</xsl:text></xpdl:Documentation>
				</xpdl:Object>
				<xpdl:NodeGraphicsInfos>
				  <xpdl:NodeGraphicsInfo Width="1000" Height="1000">
					<xpdl:Coordinates XCoordinate="0" YCoordinate="0" />
				  </xpdl:NodeGraphicsInfo>
				</xpdl:NodeGraphicsInfos>
				
			  </xpdl:Lane>
			</xpdl:Lanes>
			<xpdl:Object Id="{generate-id(.)}">
			<xpdl:Documentation><xsl:text>This is the background Pool, which has been created automatically.</xsl:text></xpdl:Documentation>
			</xpdl:Object>
			<xpdl:NodeGraphicsInfos>
			  <xpdl:NodeGraphicsInfo Width="1000" Height="1000">
				<xpdl:Coordinates XCoordinate="0" YCoordinate="0" />
			  </xpdl:NodeGraphicsInfo>
			</xpdl:NodeGraphicsInfos>
		</xpdl:Pool>
	  </xsl:if>
      </xpdl:Pools>

      <!-- ******************* Message Flows ********************** -->
      <xsl:variable name="MessageFlows" select="//x:div[x:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#MessageFlow']"/>
      <xsl:if test="count($MessageFlows)>0">
        <xpdl:MessageFlows>
          <xsl:for-each select="$MessageFlows">
            <!-- <xsl:variable name="id" select="x:span[@class='oryx-id']"/> -->
            <xsl:variable name="id" select="@id"/>
            <xsl:variable name="name" select="x:span[@class='oryx-name']"/>
            <xsl:variable name="internalOutRef" select="x:a[@rel='raziel-outgoing']/@href" />
            <!--
            <xpdl:MessageFlow 
							Id="{$id}"
							Source="{x:span[@class='oryx-source']}"
							Target="{x:span[@class='oryx-target']}"> -->
            <xpdl:MessageFlow 
							Id="{$id}"
              Source="{//x:div[x:a[@rel='raziel-outgoing' and @href=concat('#',$id)]]/@id}"
              Target="{//x:div[@id=substring($internalOutRef,2)]/@id}">
              <xsl:if test="string-length(normalize-space($name))>0">
                <xsl:attribute name="Name">
                  <xsl:value-of select="$name" />
                </xsl:attribute>
              </xsl:if>
              <xpdl:Message 
								Id="{concat($id,'_message')}" 
								Name="{x:span[@class='oryx-message']}">
              </xpdl:Message>
            </xpdl:MessageFlow>
          </xsl:for-each>
        </xpdl:MessageFlows>
      </xsl:if>
      <!-- ******************* Associations ********************** -->
      <xsl:variable name="undirectedAssociations" select="//x:div[x:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#UndirectedAssociation']"/>
      <xsl:variable name="directedAssociations" select="//x:div[x:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#DirectedAssociation']"/>
      <xsl:if test="count($undirectedAssociations)>0 or count($directedAssociations)>0">
        <xpdl:Associations>
          <xsl:for-each select="$undirectedAssociations">
            <xsl:variable name="name" select="x:span[@class='oryx-name']"/>
            <xsl:variable name="id" select="@id"/>
            <xsl:variable name="internalOutRef" select="x:a[@rel='raziel-outgoing']/@href" />
            <!--             <xpdl:Association 
							Id="{x:span[@class='oryx-id']}"
							Source="{x:span[@class='oryx-source']}"
							Target="{x:span[@class='oryx-target']}"
							AssociationDirection="{x:span[@class='oryx-direction']}"> -->
            <xpdl:Association 
							Id="{$id}"
              Source="{//x:div[x:a[@rel='raziel-outgoing' and @href=concat('#',$id)]]/@id}"
              Target="{//x:div[@id=substring($internalOutRef,2)]/@id}"
              AssociationDirection="{x:span[@class='oryx-direction']}">
              <xsl:if test="string-length(normalize-space($name))>0">
                <xsl:attribute name="Name">
                  <xsl:value-of select="$name" />
                </xsl:attribute>
              </xsl:if>
            </xpdl:Association>
          </xsl:for-each>
          <xsl:for-each select="$directedAssociations">
            <xsl:variable name="name" select="x:span[@class='oryx-name']"/>
            <xsl:variable name="id" select="@id"/>
            <xsl:variable name="internalOutRef" select="x:a[@rel='raziel-outgoing']/@href" />
            <!--             <xpdl:Association 
							Id="{x:span[@class='oryx-id']}"
							Source="{x:span[@class='oryx-source']}"
							Target="{x:span[@class='oryx-target']}"
							AssociationDirection="{x:span[@class='oryx-direction']}"> -->
            <xpdl:Association 
							Id="{$id}"
              Source="{//x:div[x:a[@rel='raziel-outgoing' and @href=concat('#',$id)]]/@id}"
              Target="{//x:div[@id=substring($internalOutRef,2)]/@id}"
              AssociationDirection="{x:span[@class='oryx-direction']}">
              <xsl:if test="string-length(normalize-space($name))>0">
                <xsl:attribute name="Name">
                  <xsl:value-of select="$name" />
                </xsl:attribute>
              </xsl:if>
            </xpdl:Association>
          </xsl:for-each>
        </xpdl:Associations>
      </xsl:if>
      <!-- ******************* Artifacts *************************** -->
      <xsl:variable name="VarDataObjects" select="//x:div[x:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#DataObject']"/>
      <xsl:if test="(count($VarDataObjects))>0">
        <xpdl:Artifacts>
          <!-- ******************** Variable Data Object ******************* -->
          <xsl:for-each select="$VarDataObjects">
            <!-- <xsl:variable name="id" select="x:span[@class='oryx-id']"/> -->
            <xsl:variable name="id" select="@id"/>
            <xsl:variable name="pool" select="x:span[@class='oryx-pool']"/>
            <xsl:variable name="lanes" select="x:span[@class='oryx-lanes']"/>
            <xsl:variable name="state" select="x:span[@class='oryx-state']"/>
            <xpdl:Artifact
							Id="{$id}"
							Name="{x:span[@class='oryx-name']}"
							ArtifactType="DataObject"
							State="{$state}">
              <xsl:variable name="requiredForStart" select="x:span[@class='oryx-requiredForStart']"/>
              <xsl:variable name="producedAtCompletion" select="x:span[@class='oryx-producedAtCompletion']"/>
              <xpdl:DataObject 
								Id="{concat($id,'_DataObject')}"
								RequiredForStart="{$requiredForStart}" 
								ProducedAtCompletion="{$producedAtCompletion}">
              </xpdl:DataObject>
            </xpdl:Artifact>
          </xsl:for-each>
        </xpdl:Artifacts>
      </xsl:if>
      <!-- ************************** Processes ******************************* -->
      <xpdl:WorkflowProcesses>
        <xsl:for-each select="//x:div[x:span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#Pool']">
          <xsl:variable name="suppressJoinFailure" select="x:span[@class='oryx-suppressjoinfailure']"/>
          <xsl:variable name="enableInstanceCompensation" select="x:span[@class='oryx-enableinstancecompensation']"/>
          <xsl:variable name="queryLanguage" select="x:span[@class='oryx-querylanguage']"/>
          <xsl:variable name="expressionLanguage" select="x:span[@class='oryx-expressionlanguage']"/>
          <!-- <xpdl:WorkflowProcess 
						Id="{x:span[@class='oryx-processRef']}"
						Name="{x:span[@class='oryx-processName']}"> -->
          <xpdl:WorkflowProcess 
						Id="{concat(@id,'_process')}"
						Name="{x:span[@class='oryx-processName']}">
            <xsl:if test="string-length(normalize-space($suppressJoinFailure))>0">
              <xsl:attribute name="SuppressJoinFailure">
                <xsl:value-of select="$suppressJoinFailure" />
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="string-length(normalize-space($enableInstanceCompensation))>0">
              <xsl:attribute name="EnableInstanceCompensation">
                <xsl:value-of select="$enableInstanceCompensation" />
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="string-length(normalize-space($queryLanguage))>0">
              <xsl:attribute name="QueryLanguage">
                <xsl:value-of select="$queryLanguage" />
              </xsl:attribute>
            </xsl:if>
            <xsl:if test="string-length(normalize-space($expressionLanguage))>0">
              <xsl:attribute name="ExpressionLanguage">
                <xsl:value-of select="$expressionLanguage" />
              </xsl:attribute>
            </xsl:if>
            <xpdl:ProcessHeader />

            <xsl:variable name="poolId" select="@id" />
            <xsl:variable name="childLanes" select="//x:div[x:a[@rel='raziel-parent' and @href=concat('#',$poolId)]]" />

            <xpdl:ActivitySets>
              <xsl:for-each select="$childLanes">
                <xsl:variable name="laneId" select="@id"/>
                <xsl:variable name="blockActivities" select="//x:div[x:a[@rel='raziel-parent' and @href=concat('#',$laneId)]]"/>
                <xsl:call-template name="activitySets">
                  <xsl:with-param name="blockActivities" select="$blockActivities" />
                </xsl:call-template>
              </xsl:for-each>
            </xpdl:ActivitySets>
            <!-- ************************ Activities ********************************* -->
            <xpdl:Activities>
              <xsl:for-each select="$childLanes">
                <xsl:variable name="laneId" select="@id"/>
                <xsl:variable name="childActivities" select="//x:div[x:a[@rel='raziel-parent' and @href=concat('#',$laneId)]]"/>
                <xsl:call-template name="activities">
                  <xsl:with-param name="activities" select="$childActivities" />
                </xsl:call-template>
              </xsl:for-each>
            </xpdl:Activities>
            <!-- ************************ Transitions ********************************* -->
            <xpdl:Transitions>
              <xsl:for-each select="$childLanes">
                <xsl:call-template name="relevantTransitions">
                  <xsl:with-param name="resId" select="@id" />
                </xsl:call-template>
              </xsl:for-each>
            </xpdl:Transitions>
            <xpdl:Extensions/>
          </xpdl:WorkflowProcess>
        </xsl:for-each>
		<!-- Treat the elements potentially contained in the background pool -->
		<xsl:if test="count($canvasChildren)>0">
			<xpdl:WorkflowProcess 
				Id="{concat(@canvasId,'_process')}"
				Name="Background Pool Process">
				<xpdl:ProcessHeader />

				<!-- ************************ Activity Sets ********************************* -->
				<xpdl:ActivitySets>
					<xsl:for-each select="$canvasChildren">
						<xsl:call-template name="activitySets">
						  <xsl:with-param name="blockActivities" select="$canvasChildren" />
						</xsl:call-template>
					</xsl:for-each>
				</xpdl:ActivitySets>
				<!-- ************************ Activities ********************************* -->
				<xpdl:Activities>
					<xsl:for-each select="$canvasChildren">
						<xsl:call-template name="activities">
						  <xsl:with-param name="activities" select="$canvasChildren" />
						</xsl:call-template>
				  </xsl:for-each>
				</xpdl:Activities>
				<!-- ************************ Transitions ********************************* -->
				<xpdl:Transitions>
					<xsl:for-each select="$canvasChildren">
						<xsl:call-template name="relevantTransitions">
						  <xsl:with-param name="resId" select="$canvasId" />
						</xsl:call-template>
				  </xsl:for-each>
				</xpdl:Transitions>
				<xpdl:Extensions/>
			</xpdl:WorkflowProcess>
		</xsl:if>
      </xpdl:WorkflowProcesses>
    </xpdl:Package>
  </xsl:template>
</xsl:stylesheet>
