<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xpdl="http://www.wfmc.org/2004/XPDL2.0alpha">

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
    <!-- <xpdl:Object Id="{$objectParent/span[@class='oryx-id']}"> -->
    <xpdl:Object
      Id="{$objectParent/@id}">
      <xsl:variable name="categories" select="$objectParent/span[@class='oryx-categories']" />
      <xsl:variable name="documentation" select="$objectParent/span[@class='oryx-documentation']" />
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
    <xsl:variable name="parentRef" select="$node/a[@rel='raziel-parent']/@href" />
    <xsl:variable name="parent" select="/div[@class='processdata']/div[@id=substring($parentRef,2)]" />
    <xsl:choose>
      <xsl:when test="$parent/span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#Lane'">
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
    <xsl:variable name="bounds" select="$node/span[@class='oryx-bounds']" />
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
    <xsl:variable name="timeDate" select="span[@class='oryx-timedate']" />
    <xsl:variable name="timeCycle" select="span[@class='oryx-timecycle']" />
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
      <xsl:variable name="type" select="span[@class='oryx-activitytype']" />
      <xsl:variable name="gatewayType" select="span[@class='oryx-gatewaytype']" />
      <xsl:variable name="eventType" select="span[@class='oryx-eventtype']" />
      <xsl:variable name="loopType" select="span[@class='oryx-looptype']" />
      <!-- <xsl:variable name="id" select="span[@class='oryx-id']" /> -->
      <xsl:variable name="id" select="@id" />
      <xsl:variable name="name" select="span[@class='oryx-name']" />
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
            <xsl:variable name="instantiate" select="span[@class='oryx-instantiate']" />
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
                  <xsl:if test="span[@class='oryx-xortype']='Event'">
                    <xpdl:Split Type="XOREVENT" />
                  </xsl:if>
                  <xsl:if test="span[@class='oryx-xortype']='Data'">
                    <xpdl:Split Type="XOR" />
                    <!-- TODO: transition Refs depending on defined transition order? -->
                  </xsl:if>
                </xpdl:TransitionRestriction>
              </xpdl:TransitionRestrictions>
            </xsl:if>
          </xsl:if>
          <!-- ******************** XPDL Tasks ********************** -->
          <xsl:if test="$type='Task'">
            <xsl:variable name="taskType" select="span[@class='oryx-tasktype']" />
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
										Instantiate="{span[@class='oryx-instantiate']}">
                    <xpdl:Message Id="{concat($id,'_message')}"/>
                  </xpdl:TaskReceive>
                </xpdl:Task>
              </xpdl:Implementation>
            </xsl:if>
            <xsl:if test="$taskType='Send'">
              <xpdl:Implementation>
                <xpdl:Task>
                  <xsl:variable name="faultName" select="span[@class='oryx-faultname']" />
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
              <xsl:variable name="trigger" select="span[@class='oryx-trigger']" />
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
                <xsl:variable name="target" select="span[@class='oryx-target']" />
                <xsl:variable name="targetRef" select="/div[@class='processdata']/div[a[@rel='raziel-outgoing' and @href=concat('#',$id)]]/@id" />
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
                    <xpdl:ResultError ErrorCode="{span[@class='oryx-errorcode']}" />
                  </xsl:if>
                  <xsl:if test="$trigger='Compensation'">
                    <xsl:variable name="activity" select="span[@class='oryx-activity']" />
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
                  <xsl:variable name="result" select="span[@class='oryx-result']" />
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
                    <xpdl:ResultError ErrorCode="{span[@class='oryx-errorcode']}" />
                  </xsl:if>
                  <xsl:if test="$trigger='Compensation'">
                    <xsl:variable name="activity" select="span[@class='oryx-activity']" />
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
									LoopCondition="{span[@class='oryx-loopcondition']}"
									TestTime="{span[@class='oryx-testtime']}">
                </xpdl:LoopStandard>
              </xsl:if>
              <xsl:if test="$loopType='MultiInstance'">
                <xpdl:LoopMultiInstance
                 MI_Condition="{span[@class='oryx-mi_condition']}"
                 MI_Ordering="{span[@class='oryx-mi_ordering']}"
                 MI_FlowCondition="{span[@class='oryx-mi_flowcondition']}">
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
    <xsl:variable name="activitiesOutRefs" select="/div[@class='processdata']/div[a[@rel='raziel-parent' and @href=concat('#',$resId)]]/a[@rel='raziel-outgoing']"/>
    <xsl:for-each select="$activitiesOutRefs">
      <xsl:variable name="outRef" select="@href"/>
      <xsl:variable name="relevantTransitions" select="/div[@class='processdata']/div[a[@rel='raziel-parent' and @href='#oryx-canvas123'] and @id=substring($outRef,2)]"/>
      <xsl:call-template name="transitions">
        <xsl:with-param name="transitions" select="$relevantTransitions" />
      </xsl:call-template>
    </xsl:for-each>
  </xsl:template>

  <!-- ************** Template for generating Transitions content ******************** -->
  <xsl:template name="transitions">
    <xsl:param name="transitions" />
    <xsl:for-each select="$transitions">
      <xsl:variable name="type" select="span[@class='oryx-type']" />
      <!-- <xsl:variable name="id" select="span[@class='oryx-id']" /> -->
      <xsl:variable name="id" select="@id" />
      <xsl:variable name="name" select="span[@class='oryx-name']" />
      <xsl:variable name="internalOutRef" select="a[@rel='raziel-outgoing']/@href" />
      <xsl:variable name="conditionType" select="span[@class='oryx-conditiontype']" />
      <xsl:if test="$type='http://b3mn.org/stencilset/bpmn1.1#SequenceFlow'">
        <xpdl:Transition
					Id="{$id}"
					From="{/div[@class='processdata']/div[a[@rel='raziel-outgoing' and @href=concat('#',$id)]]/@id}"
					To="{/div[@class='processdata']/div[@id=substring($internalOutRef,2)]/@id}">
          <xsl:if test="string-length(normalize-space($name))>0">
            <xsl:attribute name="Name">
              <xsl:value-of select="$name" />
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="$conditionType='Expression'">
            <xsl:variable name="conditionExpressionLanguage" select="span[@class='oryx-conditionexpressionlanguage']" />
            <xpdl:Condition Type="CONDITION">
              <xpdl:Expression>
                <xsl:if test="string-length(normalize-space($conditionExpressionLanguage))>0">
                  <xsl:attribute name="ScriptGrammar">
                    <xsl:value-of select="$conditionExpressionLanguage"/>
                  </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="span[@class='oryx-conditionexpression']"/>
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
      <xsl:variable name="type" select="span[@class='oryx-activitytype']" />
      <xsl:if test="$type='Sub-Process'">
        <xsl:variable name="resourceId" select="@id" />
        <xsl:variable name="blockActivityId" select="span[@class='oryx-id']" />
        <xsl:variable name="innerActivities" select="/div[@class='processdata']/div[a[@rel='raziel-parent' and @href=concat('#',$resourceId)]]"/>
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
          <xsl:variable name="laneName" select="span[@class='oryx-name']" />
          <!--<xpdl:Lane Id="{span[@class='oryx-id']}"> -->
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
  <xsl:template match="/div[@class='processdata']">
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
      <xsl:variable name="Pools" select="div[span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#Pool']"/>
      <xsl:if test="count($Pools)>0">
        <xpdl:Pools>
          <xsl:for-each select="$Pools">
            <!-- <xsl:variable name="id" select="span[@class='oryx-id']" /> -->
            <xsl:variable name="id" select="@id" />
            <!-- <xpdl:Pool 
							Id="{$id}" 
							Name="{span[@class='oryx-name']}"
							Process="{span[@class='oryx-processRef']}"
							BoundaryVisible="{span[@class='oryx-boundaryvisible']}"> -->
            <xpdl:Pool 
							Id="{$id}" 
							Name="{span[@class='oryx-name']}"
							Process="{concat($id,'_process')}"
							BoundaryVisible="{span[@class='oryx-boundaryvisible']}">
              <xsl:variable name="participantRef" select="span[@class='oryx-participantRef']" />
              <xsl:if test="string-length(normalize-space($participantRef))>0">
                <xsl:attribute name="Participant">
                  <xsl:value-of select="$participantRef"></xsl:value-of>
                </xsl:attribute>
              </xsl:if>

              <!-- determine Lanes -->
              <xsl:variable name="childLanes" select="/div[@class='processdata']/div[a[@rel='raziel-parent' and @href=concat('#',$id)]]" />
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
        </xpdl:Pools>
      </xsl:if>
      <!-- ******************* Message Flows ********************** -->
      <xsl:variable name="MessageFlows" select="div[span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#MessageFlow']"/>
      <xsl:if test="count($MessageFlows)>0">
        <xpdl:MessageFlows>
          <xsl:for-each select="$MessageFlows">
            <!-- <xsl:variable name="id" select="span[@class='oryx-id']"/> -->
            <xsl:variable name="id" select="@id"/>
            <xsl:variable name="name" select="span[@class='oryx-name']"/>
            <xsl:variable name="internalOutRef" select="a[@rel='raziel-outgoing']/@href" />
            <!--
            <xpdl:MessageFlow 
							Id="{$id}"
							Source="{span[@class='oryx-source']}"
							Target="{span[@class='oryx-target']}"> -->
            <xpdl:MessageFlow 
							Id="{$id}"
              Source="{/div[@class='processdata']/div[a[@rel='raziel-outgoing' and @href=concat('#',$id)]]/@id}"
              Target="{/div[@class='processdata']/div[@id=substring($internalOutRef,2)]/@id}">
              <xsl:if test="string-length(normalize-space($name))>0">
                <xsl:attribute name="Name">
                  <xsl:value-of select="$name" />
                </xsl:attribute>
              </xsl:if>
              <xpdl:Message 
								Id="{concat($id,'_message')}" 
								Name="{span[@class='oryx-message']}">
              </xpdl:Message>
            </xpdl:MessageFlow>
          </xsl:for-each>
        </xpdl:MessageFlows>
      </xsl:if>
      <!-- ******************* Associations ********************** -->
      <xsl:variable name="undirectedAssociations" select="div[span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#UndirectedAssociation']"/>
      <xsl:variable name="directedAssociations" select="div[span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#DirectedAssociation']"/>
      <xsl:if test="count($undirectedAssociations)>0 or count($directedAssociations)>0">
        <xpdl:Associations>
          <xsl:for-each select="$undirectedAssociations">
            <xsl:variable name="name" select="span[@class='oryx-name']"/>
            <xsl:variable name="id" select="@id"/>
            <xsl:variable name="internalOutRef" select="a[@rel='raziel-outgoing']/@href" />
            <!--             <xpdl:Association 
							Id="{span[@class='oryx-id']}"
							Source="{span[@class='oryx-source']}"
							Target="{span[@class='oryx-target']}"
							AssociationDirection="{span[@class='oryx-direction']}"> -->
            <xpdl:Association 
							Id="{$id}"
              Source="{/div[@class='processdata']/div[a[@rel='raziel-outgoing' and @href=concat('#',$id)]]/@id}"
              Target="{/div[@class='processdata']/div[@id=substring($internalOutRef,2)]/@id}">
              AssociationDirection="{span[@class='oryx-direction']}">
              <xsl:if test="string-length(normalize-space($name))>0">
                <xsl:attribute name="Name">
                  <xsl:value-of select="$name" />
                </xsl:attribute>
              </xsl:if>
            </xpdl:Association>
          </xsl:for-each>
          <xsl:for-each select="$directedAssociations">
            <xsl:variable name="name" select="span[@class='oryx-name']"/>
            <xsl:variable name="id" select="@id"/>
            <xsl:variable name="internalOutRef" select="a[@rel='raziel-outgoing']/@href" />
            <!--             <xpdl:Association 
							Id="{span[@class='oryx-id']}"
							Source="{span[@class='oryx-source']}"
							Target="{span[@class='oryx-target']}"
							AssociationDirection="{span[@class='oryx-direction']}"> -->
            <xpdl:Association 
							Id="{$id}"
              Source="{/div[@class='processdata']/div[a[@rel='raziel-outgoing' and @href=concat('#',$id)]]/@id}"
              Target="{/div[@class='processdata']/div[@id=substring($internalOutRef,2)]/@id}">
              AssociationDirection="{span[@class='oryx-direction']}">
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
      <xsl:variable name="VarDataObjects" select="div[span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#DataObject']"/>
      <xsl:if test="(count($VarDataObjects))>0">
        <xpdl:Artifacts>
          <!-- ******************** Variable Data Object ******************* -->
          <xsl:for-each select="$VarDataObjects">
            <!-- <xsl:variable name="id" select="span[@class='oryx-id']"/> -->
            <xsl:variable name="id" select="@id"/>
            <xsl:variable name="pool" select="span[@class='oryx-pool']"/>
            <xsl:variable name="lanes" select="span[@class='oryx-lanes']"/>
            <xsl:variable name="state" select="span[@class='oryx-state']"/>
            <xpdl:Artifact
							Id="{$id}"
							Name="{span[@class='oryx-name']}"
							ArtifactType="DataObject"
							State="{$state}">
              <xsl:variable name="requiredForStart" select="span[@class='oryx-requiredForStart']"/>
              <xsl:variable name="producedAtCompletion" select="span[@class='oryx-producedAtCompletion']"/>
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
        <xsl:for-each select="div[span[@class='oryx-type']='http://b3mn.org/stencilset/bpmn1.1#Pool']">
          <xsl:variable name="suppressJoinFailure" select="span[@class='oryx-suppressjoinfailure']"/>
          <xsl:variable name="enableInstanceCompensation" select="span[@class='oryx-enableinstancecompensation']"/>
          <xsl:variable name="queryLanguage" select="span[@class='oryx-querylanguage']"/>
          <xsl:variable name="expressionLanguage" select="span[@class='oryx-expressionlanguage']"/>
          <!-- <xpdl:WorkflowProcess 
						Id="{span[@class='oryx-processRef']}"
						Name="{span[@class='oryx-processName']}"> -->
          <xpdl:WorkflowProcess 
						Id="{concat(@id,'_process')}"
						Name="{span[@class='oryx-processName']}">
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
            <xsl:variable name="childLanes" select="/div[@class='processdata']/div[a[@rel='raziel-parent' and @href=concat('#',$poolId)]]" />

            <xpdl:ActivitySets>
              <xsl:for-each select="$childLanes">
                <xsl:variable name="laneId" select="@id"/>
                <xsl:variable name="blockActivities" select="/div[@class='processdata']/div[a[@rel='raziel-parent' and @href=concat('#',$laneId)]]"/>
                <xsl:call-template name="activitySets">
                  <xsl:with-param name="blockActivities" select="$blockActivities" />
                </xsl:call-template>
              </xsl:for-each>
            </xpdl:ActivitySets>
            <!-- ************************ Activities ********************************* -->
            <xpdl:Activities>
              <xsl:for-each select="$childLanes">
                <xsl:variable name="laneId" select="@id"/>
                <xsl:variable name="childActivities" select="/div[@class='processdata']/div[a[@rel='raziel-parent' and @href=concat('#',$laneId)]]"/>
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
      </xpdl:WorkflowProcesses>
    </xpdl:Package>
  </xsl:template>
</xsl:stylesheet>
