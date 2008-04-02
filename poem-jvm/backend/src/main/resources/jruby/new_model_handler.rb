module Handler
  class NewModelHandler < DefaultHandler
    def doGet(interaction)
      out = interaction.response.getWriter
      stencilset = interaction.hostname + (interaction.params['stencilset'] || "\/stencilsets\/bpmn\/bpmn.json")
      out.println("<div class=\"-oryx-canvas\" id=\"oryx-canvas123\" style=\"width:1200px; height:600px;\">")
  	  out.println("<a href=\"" + stencilset + "\" rel=\"oryx-stencilset\"></a>")
  	  out.println("<span class=\"oryx-mode\">writeable</span>")
  	  out.println("<span class=\"oryx-mode\">fullscreen</span>")
  	  out.println("</div>")
    end
  end
end