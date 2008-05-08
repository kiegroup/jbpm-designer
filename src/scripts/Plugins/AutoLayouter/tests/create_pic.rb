$LOAD_PATH.unshift File.join(File.dirname(__FILE__), "..", "src")

require 'layout'
require 'constants'

layout = OryxLayouter::Layout.new
OryxLayouter::Pic_Creation_Mode = :only_pic
simple = false
if simple
  layout.load_model(File.join(OryxLayouter::Test_Path.path, "simple_sample"), 
    "http://b3mn.hpi.uni-potsdam.de/server.php?resource=simple_sample")
else
  layout.load_model(File.join(OryxLayouter::Test_Path.path, "medium_sample"), 
    "http://b3mn.hpi.uni-potsdam.de/server.php?resource=TryThis")
end
  