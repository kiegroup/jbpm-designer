##############################
 # Copyright (c) 2008
 # Ole Eckermann
 #
 # Permission is hereby granted, free of charge, to any person obtaining a
 # copy of this software and associated documentation files (the "Software"),
 # to deal in the Software without restriction, including without limitation
 # the rights to use, copy, modify, merge, publish, distribute, sublicense,
 # and/or sell copies of the Software, and to permit persons to whom the
 # Software is furnished to do so, subject to the following conditions:
 #
 # The above copyright notice and this permission notice shall be included in
 # all copies or substantial portions of the Software.
 #
 # THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 # IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 # FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 # AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 # LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 # FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 # DEALINGS IN THE SOFTWARE.
 ##############################

module Handler
  class RepositoryHandler < DefaultHandler
    def doGet(interaction)
      
      java_script_includes = ['log', 'application', 'repository', 'model_properties']
      stylesheet_links = ['openid', 'repository', 'model_properties']
      ext_path = '/poem-backend-1.0/ext/'
      
      interaction.response.setStatus(200)
      interaction.response.setContentType("text/html")
      out = interaction.response.getWriter
      
      out.println('<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">')
      out.println('<html>')
      out.println('<head>')
      out.println('<meta http-equiv="Content-Type" content="text/html; charset=utf-8">')
    	out.println('<link rel="stylesheet" type="text/css" href="' + ext_path + 'resources/css/ext-all.css">')
    	out.println('<link rel="stylesheet" type="text/css" href="' + ext_path + 'resources/css/xtheme-gray.css">')
      out.println('<script type="text/javascript" src="' + ext_path + 'adapter/ext/ext-base.js"></script>')
      out.println('<script type="text/javascript" src="' + ext_path + 'ext-all-debug.js"></script>')
      java_script_includes.each do |java_script|
        out.println('<script type="text/javascript" src="/poem-backend-1.0/javascripts/' + java_script + '.js"></script>')
      end
      stylesheet_links.each do |stylesheet|
        out.println('<link rel="stylesheet" type="text/css" href="/poem-backend-1.0/stylesheets/' + stylesheet + '.css">')
      end
      out.println('<script type="text/javascript">Ext.onReady(function(){Repository.app.init("' + interaction.subject.getUri + '");});</script>')  
      out.println('<title>Oryx - Repository</title>')
      out.println('</head>')
      out.println('<body>')
      out.println('</body>')
      out.println('</html>')
    end
  end
  
  class TypeHandler < DefaultHandler
    def doGet(interaction)
      interaction.response.setStatus(200)
      out = interaction.response.getWriter
      
      output = ActiveSupport::JSON.encode(
      [
          {:id => "bpmn", :title => "BPMN", :description => "Business Process Model Notation", :uri => "/stencilsets/bpmn/bpmn.json", :icon_url => "/oryx/stencilsets/bpmn/bpmn.png"},
          {:id => "petrinet", :title => "Petri Net", :description => "Petri Net", :uri => "/stencilsets/petrinets/petrinet.json", :icon_url => "/oryx/stencilsets/petrinets/petrinets.png"},
          {:id => "epc", :title => "EPC", :description => "Event-Driven Process Chain", :uri => "/stencilsets/epc/epc.json", :icon_url => "/oryx/stencilsets/epc/epc.png"},
          {:id => "workflownet", :title => "Workflow Net", :description => "Workflow Net", :uri => "/stencilsets/workflownets/workflownets.json", :icon_url => "/oryx/stencilsets/workflownets/workflownets.png"}
      ])
      out.print(output);

    end
  end
end