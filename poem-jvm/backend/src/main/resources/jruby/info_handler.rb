module Handler
  require 'rubygems'
  require 'activesupport'
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
         puts Time.now
       end
        
       if interaction.params['type']
         type = interaction.params['type']
       else
         type = '%'
       end
        
       models = interaction.subject.getModels(type, from, to)
       output = []
       models.each do |model|
         output << Helper.getModelInfo(interaction, Identity.instance(model.getIdent_id))
       end
       out = interaction.response.getWriter
       out.print(ActiveSupport::JSON.encode(output))
    end
  end
  
  class MetaHandler < DefaultHandler
    def doGet(interaction)
      output = Helper.getModelInfo(interaction)
      out = interaction.response.getWriter
      out.print(ActiveSupport::JSON.encode(output))
    end
  end
  
  class InfoHandler < DefaultHandler
    def doGet(interaction)
      interaction.response.setStatus(200)
      representation = interaction.object.read
      out = interaction.response.getWriter
      output = Helper.toHash(representation, %w{Title Summary Updated Created Type})
      output['edit_uri'] = interaction.hostname + interaction.object.getUri + '/info'
      output['self_uri'] = interaction.hostname + interaction.object.getUri + '/self'
      out.print(ActiveSupport::JSON.encode(output))
    end
      
    def doPut(interaction)
      representation = interaction.object.read
      interaction.params.each do |key, value|
        representation.send "set#{key.capitalize}", value
      end
      representation.update
      interaction.response.setStatus(200)
    end
  end

  class AccessHandler < DefaultHandler
    def doPost(interaction)
      
    end
  end

end

module Helper
  def self.getModelInfo(interaction, model = nil)
    model = interaction.object if model.nil?
    uris = []
    interaction.subject.getServlets.each do |servlet|
      unless ['/info', '/access', '/self'].include?(servlet.getRel)
        uris << { 'href' => interaction.hostname + model.getUri + servlet.getRel,
                  'title' => servlet.getTitle }
      end 
    end
    info = toHash(model.read, %w{Title Summary Updated Created Type})
    info['edit_uri'] = interaction.hostname + model.getUri + '/info'
    info['self_uri'] = interaction.hostname + model.getUri + '/self'
    access_rights = []
    model.getAccess.each do |right|
      access_rights << toHash(right, %w{Subject Predicate Url})
    end
    access = {'access_rights' => access_rights, 'edit_uri' => interaction.hostname + model.getUri + '/access'}
    output = {'uris'=>uris, 'info'=>info,'access'=>access}
  end
end
    