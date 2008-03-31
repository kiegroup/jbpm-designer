include_class 'org.b3mn.poem.Representation'
include_class 'org.b3mn.poem.Interaction' 

module Handler
  class DefaultHandler
    def doGet(interaction)
      interaction.response.setStatus(403)
      out = interaction.response.getWriter
      out.println("Forbidden!")
    end
    def doPost(interaction)
      interaction.response.setStatus(403)
      out = interaction.response.getWriter
      out.println("Forbidden!")
    end
    def doPut(interaction)
      interaction.response.setStatus(403)
      out = interaction.response.getWriter
      out.println("Forbidden!")
    end
    def doDelete(interaction)
      interaction.response.setStatus(403)
      out = interaction.response.getWriter
      out.println("Forbidden!")
    end
  
    def handleRequest(interaction)
      send "do#{interaction.request.getMethod.capitalize}", interaction
    end
  end
end

