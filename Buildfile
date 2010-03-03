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
  package(:war)
  
  task :compress do
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
      Core/Controls/magnet.js Core/node.js Core/edge.js Core/abstractPlugin.js 
      Core/abstractLayouter.js }

    File.open(_('target/oryx.debug.js'), 'w') do |concat|
      files.each do |file|
        concat.write(File.read(_("src/main/js/#{file}"))
      end
    end
    
    # Get YUI Compressor
    YUICOMPRESSOR.invoke
    
    #Compress the file:
    java("-jar", YUICOMPRESSOR.to_s, "--type","js",
           _('target/oryx.core.uncompressed.js'),
           "-o", _('target/oryx.js'))
    File.open(_('target/oryx.all.js'), 'w') do |concat|
      concat.write(File.read(_("LICENSE"))
      concat.write(File.read(_("src/main/js/lib/path_parser.js"))
      concat.write(File.read(_("src/main/js/lib/prototype-1.5.1.js"))
      concat.write(File.read(_("src/main/js/lib/ext-2.0.2/adapter/ext/ext-base.js"))
      concat.write(File.read(_("src/main/js/lib/ext-2.0.2/ext-all.js"))
      concat.write(File.read(_("src/main/js/lib/ext-2.0.2/color-field.js"))
      concat.write(File.read(_("src/main/webapp/i18n/translation_en_us.js"))
    end
    
  end
  
end
