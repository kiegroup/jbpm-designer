module Handler
  class CollectionHandler < DefaultHandler
    def doGet(interaction)
       if interaction.params['from']
         from = Time.parse(interaction.params['from'])
       else 
         from = Time.parse("01-01-1970")
       end
       
       if interaction.params['to']
         to = Time.parse(interaction.params['to'])
       else
         to = Time.now+100000
       end
        
       type = interaction.params['type'] || '%'
        
       models = interaction.subject.getModels(type, from, to)
       output = []
       models.each do |model|
         output << Helper.getModelInfo(interaction, Identity.instance(model.getIdent_id))
       end
       Helper.jsonResponse(interaction.response, output)
    end
    
    def doPost(interaction)
      identity = Identity.newModel(interaction.subject, interaction.params[title], interaction.params[type], interaction.params[mime_type], interaction.params[language], interaction.params[summary], interaction.params[content])
      interaction.response.setStatus(201)
      interaction.response.setHeader("location", identity.getUri)
    end
  end
end