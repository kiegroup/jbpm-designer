
require "buildr4osgi"

require "repositories.rb"
require "dependencies.rb"

require "json"

# Keep this structure to allow the build system to update version numbers.
VERSION_NUMBER = "1.0.0.007-SNAPSHOT"

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

desc "Intalio|Process Designer"
define "designer" do
  project.version = VERSION_NUMBER
  project.group = "com.intalio.bpms.web" 
  
  compile.with ORBIT_SOURCES, ORBIT_BINARIES
  compile.options.source = "1.5"
  compile.options.target = "1.5"
  
  webContent = "WebContent"

  package(:bundle).include(_("src/main/webapp/js/Plugins/profiles.xml"), :path => ".")
  package(:bundle).include(_("src/main/webapp"), :as => webContent)

  read_m = ::Buildr::Packaging::Java::Manifest.parse(File.read(_("META-INF/MANIFEST.MF"))).main
  read_m["Jetty-WarFolderPath"] = webContent
  read_m["Bundle-Version"] = project.version
  package(:bundle).with :manifest => read_m
  
  task :compress do
    #concatenate those files:
    files = JSON.parse(File.read(_("src/main/webapp/js/js_files.json")))["files"]
    files.collect! {|f| _("src/main/webapp/js/#{f}")}
    compress(files, _('target/oryx.uncompressed.js'), _('target/oryx.js'))
  end
  
  package(:bundle).enhance [:compress] do |package_bundle|
    package_bundle.include(_('target/oryx.uncompressed.js'), :path => webContent)
    package_bundle.include(_('target/oryx.js'), :path => webContent)
    
    default = File.read(_("src/main/webapp/profiles/default.xml"))
    files = default.scan(/source=\"(.*)\"/).to_a.flatten
    files.collect! {|f| _("src/main/webapp/js/Plugins/#{f}")}
    compress(files, _("target/default.uncompressed.js"), _("target/default.js"))
    package_bundle.include(_('target/default.js'), :path => "#{webContent}/profiles")    
  end
end
