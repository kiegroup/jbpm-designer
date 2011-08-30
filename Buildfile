
require "buildr4osgi"
require "repositories.rb"
require "dependencies.rb"

require "json"

=begin
# In IRB, paste this script to change the versions of the resolved dependencies for the com.intalio.cloud.orbit dep's,
# as Maven versions differ from bundle versions.
require 'YAML'
yaml = YAML.load(File.read("dependencies.yml"))
yaml.each do |project| project[1]["dependencies"].collect! {|dep| if dep.match(/^com\.intalio\.cloud\.orbit:/)
                                                                    arr = dep.split(":")
                                                                    arr.pop
                                                                    arr << "1.0.0.012"
                                                                    arr.join(":")
                                                                  else
                                                                    dep
                                                                  end
                                                                }
end
File.open("dependencies.yml", "w") do |f| f.write YAML.dump(yaml) end
=end

ENV['OSGi'] = [ENV['ORBIT_SOURCES'], ENV['ORBIT_BINARIES'], ENV['OSGi']].compact.join ';'

# Match to the right group by using a search on the fs.
OSGi::GroupMatcher.instance.group_matchers << Proc.new {|id| 
  if !(!ENV['ORBIT_SOURCES'].nil? && Dir[File.join(ENV['ORBIT_SOURCES'], "**", "*#{id}*")].empty?)
    "com.intalio.cloud.orbit"
  elsif !(!ENV['ORBIT_BINARIES'].nil? && Dir[File.join(ENV['ORBIT_BINARIES'], "**", "*#{id}*")].empty?)
    "com.intalio.cloud.orbit-prefetched"
  end
}

# Keep this structure to allow the build system to update version numbers.
VERSION_NUMBER = "1.0.0.055"

desc "Process Designer"
define "designer" do
  project.version = VERSION_NUMBER
  project.group = "com.intalio.bpms.web" 
  
  compile.with WAR_LIBS, "junit:junit:jar:4.7"
  compile.options.source = "1.5"
  compile.options.target = "1.5"
  
  webContent = "WebContent/"

  package(:bundle).include(_("src/main/webapp"), :as => webContent).exclude('WEB-INF/tomcat_web.xml').exclude('WEB-INF/epn_web.xml').exclude('WEB-INF/jboss-web.xml')
  package(:war).include(_("src/main/webapp"), :as => '.').exclude('WEB-INF/tomcat_web.xml').exclude('WEB-INF/epn_web.xml').exclude('WEB-INF/jboss-web.xml')
  package(:war, :classifier => "jboss").include(_("src/main/webapp"), :as => '.').exclude('WEB-INF/tomcat_web.xml').exclude('WEB-INF/epn_web.xml')
  package(:war, :classifier => "jboss7").include(_("src/main/webapp"), :as => '.').exclude('WEB-INF/tomcat_web.xml').exclude('WEB-INF/epn_web.xml')
  #package(:war, :classifier => "epn").include(_("src/main/webapp"), :as => '.').exclude('WEB-INF/tomcat_web.xml').exclude('WEB-INF/epn_web.xml').exclude('WEB-INF/jboss-web.xml')

  package(:war).libs = WAR_LIBS
  package(:war, :classifier => "jboss").libs = WAR_LIBS_JBOSS
  package(:war, :classifier => "jboss7").libs = WAR_LIBS_JBOSS7
  #package(:war, :classifier => "epn").libs = WAR_LIBS_EPN
  
  package(:war, :classifier => "jboss").include(_('src/main/webapp/WEB-INF/tomcat_web.xml'), :as=>'WEB-INF/web.xml')
  package(:war, :classifier => "jboss7").include(_('src/main/webapp/WEB-INF/tomcat_web.xml'), :as=>'WEB-INF/web.xml')
  #package(:war, :classifier => "epn").include(_('src/main/webapp/WEB-INF/epn_web.xml'), :as=>'WEB-INF/web.xml')

  read_m = ::Buildr::Packaging::Java::Manifest.parse(File.read(_("META-INF/MANIFEST.MF"))).main
  read_m["Jetty-WarFolderPath"] = webContent
  read_m["Bundle-Version"] = project.version
  package(:bundle).with :manifest => read_m
  
  
  read_j = ::Buildr::Packaging::Java::Manifest.parse(File.read(_("META-INF/MANIFEST-JBOSS.MF"))).main
  read_j["Bundle-Version"] = project.version
  package(:war, :classifier => "jboss").with :manifest => read_j
  package(:war, :classifier => "jboss7").with :manifest => read_j
  
  package(:sources)
  
  task :jboss do
	p 'deploying to jboss...'
   	Java.org.apache.tools.ant.Main.main( ['-file', 'jbossdeploy.xml', 'deploy', '-DdesignerVersion='+VERSION_NUMBER] )
  end
end
