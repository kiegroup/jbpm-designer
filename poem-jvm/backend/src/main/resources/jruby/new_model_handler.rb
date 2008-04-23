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
    
    def doPost(interaction)
      title = interaction.params['title'] || 'New Process'
      type = interaction.params['type'] || 'bpmn'
      mime_type = interaction.params['mime_type'] || 'application/xhtml+xml'
      language = interaction.params['language'] || 'en_US'
      summary = interaction.params['summary'] || 'is new'

      identity = Identity.newModel(interaction.subject, title, type, mime_type, language, summary, interaction.params['data']);
      interaction.response.addHeader('Location', interaction.hostname + identity.getUri + '/self')
      interaction.response.setStatus(201)
    end
  end  
end