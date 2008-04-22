module Handler
  class NewModelHandler < DefaultHandler
    def doGet(interaction)
      stencilset = '/oryx' + (interaction.params['stencilset'] || "\/stencilsets\/bpmn\/bpmn.json")
      
      content = "<div id=\"oryx-canvas123\" class=\"-oryx-canvas\">"
  	  content << "<span class=\"oryx-mode\">writeable</span>"
  	  content << "<span class=\"oryx-mode\">fullscreen</span>"
  	  content << "<a href=\"" + stencilset + "\" rel=\"oryx-stencilset\"></a>\n"
  	  content << "</div>\n"
  	  model = {'title' => 'New Process Model', 'content' => content}

      out = interaction.response.getWriter
      out.print(Helper.getOryxModel(model))
      
      interaction.response.setStatus(200)
    	interaction.response.setContentType "application/xhtml+xml"
    end
  end
  
  def doPost(interaction)
    identity = Identity.newModel(interaction.subject.getUri, interaction.params['title'], interaction.params['type'], 'application/xhtml+xml', 'en_US', interaction.params['summary'], interaction.params['data']);
    interaction.response.addHeader('Location', identity.getUri)
    interaction.response.setStatus(201)
  end
  
end