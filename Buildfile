require "repositories.rb"
require "dependencies.rb"

# Keep this structure to allow the build system to update version numbers.
VERSION_NUMBER = "1.0.0.003-SNAPSHOT"

desc "Intalio|Process Designer"
define "designer" do
  project.version = VERSION_NUMBER
  project.group = "com.intalio.bpms.web" 
  
  
  oryx = artifact("#{project.group}:designer:war:#{project.version}").from(_('dist/oryx.war').to_s)
  #backend = artifact("#{project.group}:backend:war:#{project.version}").from(_('dist/backend.war').to_s)
  
  build do 
    system('ant rebuild-all') or fail "Error in the ant packaging script"
  end
  
  install do
    oryx.install
    #backend.install
  end
  
  upload do
    oryx.upload
    #backend.upload
  end
  
  clean do
    rm_rf _('dist')
  end
end
