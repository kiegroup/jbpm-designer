module Handler
  class ModelHandler < DefaultHandler
    include_class 'org.b3mn.poem.Persistance'
    def doGet(interaction)
      representation = interaction.object.read
    	Persistance.commit
      model = {'title' => representation.getTitle, 'content' => representation.getContent}
      interaction.response.setStatus(200)
    	interaction.response.setContentType "application/xhtml+xml"
    	out = interaction.response.getWriter
    	out.print(Helper.getOryxModel(model))
    end

    def doPut(interaction)
      representation = interaction.object.read
      representation.setContent(interaction.params['data'])
      representation.update
      interaction.response.setStatus(200)
    end

    def doPost(interaction)
      representation = interaction.object.read
      representation.setContent(interaction.params['data'])
      representation.update
      interaction.response.setStatus(200)
    end
    
    def doDelete(interaction)
      interaction.object.delete
      interaction.response.setStatus(200)
    end
  end
end