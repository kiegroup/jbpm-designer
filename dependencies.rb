
YUICOMPRESSOR = "com.yahoo.platform.yui:yuicompressor:jar:2.3.6"

ORBIT_SOURCES	= group("org.json", "de.hpi.bpt", "org.apache.commons.collections", "org.apache.commons.configuration",
    "org.apache.commons.collections", "org.jdom", 
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