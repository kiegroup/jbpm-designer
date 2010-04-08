
require "buildr4osgi"

require "repositories.rb"
require "dependencies.rb"

# Keep this structure to allow the build system to update version numbers.
VERSION_NUMBER = "1.0.0.006-SNAPSHOT"

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
  
  WebContent="WebContent"

  package(:bundle).include(_("src/main/js/Plugins/profiles.xml"), :path => ".")
  package(:bundle).include(_("src/main/webapp"), :as => WebContent)

  read_m = ::Buildr::Packaging::Java::Manifest.parse(File.read(_("META-INF/MANIFEST.MF"))).main
  read_m["Jetty-WarFolderPath"]="WebContent"
  package(:bundle).with(:manifest=>read_m)
  
  package(:bundle).enhance do |package_war|
    #concatenate those files:
    files = %w{ utils.js kickstart.js erdfparser.js datamanager.js clazz.js 
      config.js oryx.js Core/SVG/editpathhandler.js Core/SVG/minmaxpathhandler.js 
      Core/SVG/pointspathhandler.js Core/SVG/svgmarker.js Core/SVG/svgshape.js 
      Core/SVG/svgshape.js Core/SVG/label.js Core/Math/math.js 
      Core/StencilSet/stencil.js Core/StencilSet/property.js 
      Core/StencilSet/propertyitem.js Core/StencilSet/complexpropertyitem.js 
      Core/StencilSet/rules.js Core/StencilSet/stencilset.js 
      Core/StencilSet/stencilsets.js Core/command.js Core/bounds.js 
      Core/uiobject.js Core/abstractshape.js Core/canvas.js Core/main.js 
      Core/svgDrag.js Core/shape.js Core/Controls/control.js 
      Core/Controls/magnet.js Core/Controls/docker.js Core/node.js Core/edge.js Core/abstractPlugin.js 
      Core/abstractLayouter.js }
    files.collect! {|f| _("src/main/js/#{f}")}
      
    compress(files, _('target/oryx.uncompressed.js'), _('target/oryx.js'))
    package_war.include(_('target/oryx.uncompressed.js'), :path => WebContent)
    package_war.include(_('target/oryx.js'), :path => WebContent)
    
    default = File.read(_("src/main/webapp/profiles/default.xml"))
    files = default.scan(/source=\"(.*)\"/).to_a.flatten
    files.collect! {|f| _("src/main/js/Plugins/#{f}")}
    compress(files, _("target/default.uncompressed.js"), _("target/default.js"))
    package_war.include(_('target/default.js'), :path => "#{WebContent}/profiles")    
  end
  
  
end
