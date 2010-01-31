require "repositories.rb"
require "dependencies.rb"

# Keep this structure to allow the build system to update version numbers.
VERSION_NUMBER = "1.0.0.001-SNAPSHOT"

desc "Oryx"
define "oryx" do
  project.version = VERSION_NUMBER
  project.group = "com.intalio.bpms.web" 
  
  file(_('dist/oryx.war')).enhance do
    system('ant rebuild-all') or fail "Error in the ant packaging script"
  end
  oryx = artifact("#{project.group}:oryx:war:#{project.version}").from(_('dist/oryx.war').to_s)
  backend = artifact("#{project.group}:backend:war:#{project.version}").from(_('dist/backend.war').to_s)
  
  upload.enhance [oryx, backend]
  
  upload do
    oryx.upload
    backend.upload
  end
  
  clean.enhance do
    rm_rf 'dist/*'
  end
end
