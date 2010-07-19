
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
VERSION_NUMBER = "1.0.0.013-SNAPSHOT"

def compress(files, uncompressed, compressed)
  
  File.open(uncompressed, 'w') do |concat|
    files.each do |file|
      concat.write(File.read(file))
    end
  end
  
  # Get YUI Compressor
  artifact(YUICOMPRESSOR).invoke
  
  #Compress the file:
  Java::Commands::java("-jar", artifact(YUICOMPRESSOR).to_s, "--type","js",
         uncompressed,
         "-o", compressed)
  
end

desc "Process Designer"
define "designer" do
  project.version = VERSION_NUMBER
  project.group = "com.intalio.bpms.web" 
  
  compile.with dependencies, "junit:junit:jar:4.0" #ORBIT_SOURCES, ORBIT_BINARIES, 
  compile.options.source = "1.5"
  compile.options.target = "1.5"
  
  webContent = "WebContent"

  package(:bundle).include(_("src/main/webapp/js/Plugins/profiles.xml"), :path => ".")
  package(:bundle).include(_("src/main/webapp"), :as => webContent)
  package(:war).include(_("src/main/webapp/js/Plugins/profiles.xml"), :path => ".")
  package(:war).include(_("src/main/webapp"), :as => '.')
  
  package(:war).libs = WAR_LIBS
   
  read_m = ::Buildr::Packaging::Java::Manifest.parse(File.read(_("META-INF/MANIFEST.MF"))).main
  read_m["Jetty-WarFolderPath"] = webContent
  read_m["Bundle-Version"] = project.version
  package(:bundle).with :manifest => read_m
  
  compress = task :compress do
    #concatenate those files:
    files = JSON.parse(File.read(_("src/main/webapp/js/js_files.json")))["files"]
    files.collect! {|f| _("src/main/webapp/js/#{f}")}
    compress(files, _('target/oryx.uncompressed.js'), _('target/oryx.js'))
  end
  
  package(:bundle).enhance [compress] do |package_bundle|
    package_bundle.include(_('target/oryx.uncompressed.js'), :path => webContent)
    package_bundle.include(_('target/oryx.js'), :path => webContent)
    
    default = File.read(_("src/main/webapp/profiles/default.xml"))
    files = default.scan(/source=\"(.*?)\"/).to_a.flatten
    files.collect! {|f| _("src/main/webapp/js/Plugins/#{f}")}
    compress(files, _("target/default.uncompressed.js"), _("target/default.js"))
    package_bundle.include(_('target/default.js'), :path => "#{webContent}/profiles")    
  end
  
  package(:war).enhance [compress]
  package(:war).enhance do |package_war|
    #webContent is '.' then
    webContent = '.'
    package_war.include(_('target/oryx.uncompressed.js'), :path => webContent)
    package_war.include(_('target/oryx.js'), :path => webContent)
    
    default = File.read(_("src/main/webapp/profiles/default.xml"))
    files = default.scan(/source=\"(.*?)\"/).to_a.flatten
    files.collect! {|f| _("src/main/webapp/js/Plugins/#{f}")}
    compress(files, _("target/default.uncompressed.js"), _("target/default.js"))
    package_war.include(_('target/default.js'), :path => "#{webContent}/profiles")
    
    
  end
end
