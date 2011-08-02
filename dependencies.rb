
YUICOMPRESSOR = "com.yahoo.platform.yui:yuicompressor:jar:2.3.6"

ORBIT_SOURCES	= group("org.json", "de.hpi.bpt", "org.apache.commons.collections", "org.apache.commons.configuration", "org.jdom", 
    "org.apache.velocity", "org.supercsv",  "org.apache.xmlgraphics.fop", "com.sun.xml.bind", 
    "com.sun.xml.bind.jaxb1", "de.hpi.bpt.epc", "com.thoughtworks.xstream", "org.xmappr",
                           :under=>"com.intalio.cloud.orbit", :version=>"1.0.0.010")
                           
ORBIT_BINARIES	= ["com.intalio.cloud.orbit-prefetched:javax.servlet:jar:2.5.0.v200910301333", 
   "com.intalio.cloud.orbit-prefetched:org.apache.commons.httpclient:jar:3.1.0.v20080605-1935", 
   "com.intalio.cloud.orbit-prefetched:org.apache.commons.fileupload:jar:1.2.0.v20080604-1500", 
   "com.intalio.cloud.orbit-prefetched:org.apache.commons.lang:jar:2.4.0.v20081016-1030", 
   "com.intalio.cloud.orbit-prefetched:log4j.over.slf4j:jar:1.5.11", 
   "com.intalio.cloud.orbit-prefetched:org.mozilla.javascript:jar:1.7.2.v200909291707", 
   "com.intalio.cloud.orbit-prefetched:javax.mail.glassfish:jar:1.4.1.v200808130215", 
   "com.intalio.cloud.orbit-prefetched:org.apache.batik.transcoder:jar:1.7.0.v200903091627",
   "com.intalio.cloud.orbit-prefetched:org.apache.xalan:jar:2.7.1.v200905122109",
   "com.intalio.cloud.orbit-prefetched:org.apache.xerces:jar:2.9.0.v200909240008"]
   
BPMN2_LIBS = ["org.eclipse:org.eclipse.bpmn2:jar:0.7.0.003",
              "org.eclipse:org.eclipse.emf.common:jar:2.6.0.v20100427-1455",
              "org.eclipse:org.eclipse.emf.ecore.xmi:jar:2.5.0.v20100317-1336",
              "org.eclipse:org.eclipse.emf.ecore:jar:2.6.0.v20100427-1455"]

BATIK_LIBS = ["batik:batik-parser:jar:1.6-1",
              "batik:batik-transcoder:jar:1.6-1",
              "batik:batik-extension:jar:1.6-1",
              "batik:batik-dom:jar:1.6-1",
              "batik:batik-xml:jar:1.6-1",
              "batik:batik-bridge:jar:1.6-1",
              "batik:batik-css:jar:1.6-1",
              "batik:batik-svg-dom:jar:1.6-1",
              "batik:batik-svggen:jar:1.6-1",
              "batik:batik-util:jar:1.6-1",
              "batik:batik-ext:jar:1.6-1",
              "batik:batik-script:jar:1.6-1",
              "batik:batik-gvt:jar:1.6-1",
              "batik:batik-awt-util:jar:1.6-1",
	          "commons-io:commons-io:jar:1.4",
              "org.apache.xmlgraphics:xmlgraphics-commons:jar:1.4"]
   
WAR_LIBS = ["org.json:json:jar:20090211", "jbpt:jbpt:jar:0.1.0", "commons-collections:commons-collections:jar:3.2.1",
  "commons-configuration:commons-configuration:jar:1.6", "org.jdom:jdom:jar:1.1", "org.apache.velocity:velocity:jar:1.6.4",
  "org.supercsv:SuperCSV:jar:1.52", "org.apache.xmlgraphics:fop:jar:0.95", "com.sun.xml.bind:jaxb-impl:jar:2.2",
  "com.sun.xml.bind:jaxb1-impl:jar:2.2", "de.hpi:atlas:jar:1.0.0", "com.thoughtworks.xstream:xstream:jar:1.3.1",
  "org.xmappr:xmappr:jar:0.9.3", "org.slf4j:slf4j-api:jar:1.5.8",
  "javax.servlet:servlet-api:jar:2.5", "commons-httpclient:commons-httpclient:jar:3.1",
  "commons-fileupload:commons-fileupload:jar:1.2.1", "commons-lang:commons-lang:jar:2.5",
  "org.slf4j:log4j-over-slf4j:jar:1.6.0", "rhino:js:jar:1.7R2", "javax.mail:mail:jar:1.4.1",
  "batik:batik-transcoder:jar:1.6", "xalan:xalan:jar:2.7.1", "xerces:xercesImpl:jar:2.9.1",
  "org.codehaus.jackson:jackson-core-asl:jar:1.7.4", "org.codehaus.jackson:jackson-mapper-asl:jar:1.7.4",
  "org.mvel:mvel2:jar:2.1-SNAPSHOT", "org.jbpm:jbpm-flow-builder:jar:5.2.0-SNAPSHOT", "org.jbpm:jbpm-bpmn2-emfextmodel:jar:5.2.0-SNAPSHOT", 
  "org.drools:drools-core:jar:5.2.0-SNAPSHOT",
  "org.antlr:stringtemplate:jar:3.2.1",
  "org.eclipse:osgi:jar:3.5.0.v20090520", "avalon-framework:avalon-framework:jar:4.1.4", YUICOMPRESSOR] | BPMN2_LIBS | BATIK_LIBS

WAR_LIBS_JBOSS = ["org.json:json:jar:20090211", "jbpt:jbpt:jar:0.1.0", "commons-collections:commons-collections:jar:3.2.1",
  "commons-configuration:commons-configuration:jar:1.6", "org.jdom:jdom:jar:1.1", "org.apache.velocity:velocity:jar:1.6.4",
  "org.supercsv:SuperCSV:jar:1.52", "org.apache.xmlgraphics:fop:jar:0.95", "com.sun.xml.bind:jaxb-impl:jar:2.2",
  "com.sun.xml.bind:jaxb1-impl:jar:2.2", "de.hpi:atlas:jar:1.0.0", "com.thoughtworks.xstream:xstream:jar:1.3.1",
  "org.xmappr:xmappr:jar:0.9.3", "org.slf4j:slf4j-api:jar:1.5.8", "org.slf4j:slf4j-jdk14:jar:1.5.6",
  "commons-httpclient:commons-httpclient:jar:3.1",
  "commons-fileupload:commons-fileupload:jar:1.2.1", "commons-lang:commons-lang:jar:2.5",
  "rhino:js:jar:1.7R2", "javax.mail:mail:jar:1.4.1",
  "org.codehaus.jackson:jackson-core-asl:jar:1.7.4", "org.codehaus.jackson:jackson-mapper-asl:jar:1.7.4",
  "org.mvel:mvel2:jar:2.1-SNAPSHOT", "org.jbpm:jbpm-flow-builder:jar:5.2.0-SNAPSHOT", "org.jbpm:jbpm-bpmn2-emfextmodel:jar:5.2.0-SNAPSHOT", 
  "org.drools:drools-core:jar:5.2.0-SNAPSHOT",
  "org.antlr:stringtemplate:jar:3.2.1",
  "org.eclipse:osgi:jar:3.5.0.v20090520", "avalon-framework:avalon-framework:jar:4.1.4", YUICOMPRESSOR] | BPMN2_LIBS | BATIK_LIBS
  

WAR_LIBS_JBOSS7 = ["org.json:json:jar:20090211", "jbpt:jbpt:jar:0.1.0", "commons-collections:commons-collections:jar:3.2.1",
  "commons-configuration:commons-configuration:jar:1.6", "org.jdom:jdom:jar:1.1", "org.apache.velocity:velocity:jar:1.6.4",
  "org.supercsv:SuperCSV:jar:1.52", "org.apache.xmlgraphics:fop:jar:0.95", "com.sun.xml.bind:jaxb-impl:jar:2.2",
  "com.sun.xml.bind:jaxb1-impl:jar:2.2", "de.hpi:atlas:jar:1.0.0", "com.thoughtworks.xstream:xstream:jar:1.3.1",
  "org.xmappr:xmappr:jar:0.9.3", "org.slf4j:slf4j-api:jar:1.5.8", "org.slf4j:slf4j-jdk14:jar:1.5.6",
  "commons-httpclient:commons-httpclient:jar:3.1",
  "commons-fileupload:commons-fileupload:jar:1.2.1", "commons-lang:commons-lang:jar:2.5",
  "rhino:js:jar:1.7R2", "javax.mail:mail:jar:1.4.1",
  "org.codehaus.jackson:jackson-core-asl:jar:1.7.4", "org.codehaus.jackson:jackson-mapper-asl:jar:1.7.4",
  "org.mvel:mvel2:jar:2.1-SNAPSHOT", "org.jbpm:jbpm-flow-builder:jar:5.2.0-SNAPSHOT", "org.jbpm:jbpm-bpmn2-emfextmodel:jar:5.2.0-SNAPSHOT", 
  "org.drools:drools-core:jar:5.2.0-SNAPSHOT",
  "org.antlr:stringtemplate:jar:3.2.1", "antlr:antlr:jar:2.7.7",
  "org.eclipse:osgi:jar:3.5.0.v20090520", "org.slf4j:log4j-over-slf4j:jar:1.5.8", "avalon-framework:avalon-framework:jar:4.1.4", YUICOMPRESSOR] | BPMN2_LIBS | BATIK_LIBS

WAR_LIBS_EPN = ["org.json:json:jar:20090211", "jbpt:jbpt:jar:0.1.0", "commons-collections:commons-collections:jar:3.2.1",
  "commons-configuration:commons-configuration:jar:1.6", "org.jdom:jdom:jar:1.1", "org.apache.velocity:velocity:jar:1.6.4",
  "org.supercsv:SuperCSV:jar:1.52", "org.apache.xmlgraphics:fop:jar:0.95", "com.sun.xml.bind:jaxb-impl:jar:2.2",
  "com.sun.xml.bind:jaxb1-impl:jar:2.2", "de.hpi:atlas:jar:1.0.0", "com.thoughtworks.xstream:xstream:jar:1.3.1",
  "org.xmappr:xmappr:jar:0.9.3", "org.slf4j:slf4j-api:jar:1.5.8", "org.slf4j:slf4j-jdk14:jar:1.5.6",
  "commons-httpclient:commons-httpclient:jar:3.1",
  "commons-fileupload:commons-fileupload:jar:1.2.1", "commons-lang:commons-lang:jar:2.5",
  "rhino:js:jar:1.7R2", "javax.mail:mail:jar:1.4.1",
  "org.codehaus.jackson:jackson-core-asl:jar:1.7.4", "org.codehaus.jackson:jackson-mapper-asl:jar:1.7.4",
  "org.mvel:mvel2:jar:2.1-SNAPSHOT", "org.jbpm:jbpm-flow-builder:jar:5.2.0-SNAPSHOT", "org.jbpm:jbpm-bpmn2-emfextmodel:jar:5.2.0-SNAPSHOT",
  "org.drools:drools-core:jar:5.2.0-SNAPSHOT",
  "org.antlr:stringtemplate:jar:3.2.1",
  "org.eclipse:osgi:jar:3.5.0.v20090520", "avalon-framework:avalon-framework:jar:4.1.4", YUICOMPRESSOR] | BPMN2_LIBS | BATIK_LIBS 

