require "repositories.rb"
require "dependencies.rb"

# Keep this structure to allow the build system to update version numbers.
VERSION_NUMBER = "1.0.0.003-SNAPSHOT"

desc "Intalio|Process Designer"
define "designer" do
  project.version = VERSION_NUMBER
  project.group = "com.intalio.bpms.web" 
  
  compile.with JSON, SERVLET, JBPT, COMMONS, JDOM, LOG4J, BPMNQ,
  RHINO, MAIL, VELOCITY, XALAN, CSV, BATIK, FOP, JAXB, ATLAS
  package(:war).include( _("src/main/webapp"), :as => ".")
  package(:war).libs = [JSON, SERVLET, JBPT, COMMONS, JDOM, LOG4J, BPMNQ,
  RHINO, MAIL, VELOCITY, XALAN, CSV, BATIK, FOP, JAXB, ATLAS]
  # TODO compress js.
end
