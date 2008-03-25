module Handler
  require 'rubygems'
  require 'activesupport'
  
  include_class 'org.b3mn.poem.Interaction'
  
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
       Helper.jsonResponse(interaction.response, output))
    end
  end
  
  class MetaHandler < DefaultHandler
    def doGet(interaction)
      Helper.jsonResponse(interaction.response, Helper.getModelInfo(interaction, representation))
    end
  end
  
  class InfoHandler < DefaultHandler
    def doGet(interaction)
      representation = interaction.object.read
      output = Helper.toHash(representation, %w{Title Summary Updated Created Type})
      output['edit_uri'] = interaction.hostname + interaction.object.getUri + '/info'
      output['self_uri'] = interaction.hostname + interaction.object.getUri + '/self'
      Helper.jsonResponse(interaction.response, output)
    end
      
    def doPut(interaction)
      representation = interaction.object.read
      interaction.params.each do |key, value|
        representation.send "set#{key.capitalize}", value
      end
      representation.update 
      Helper.jsonResponse(interaction.response, Helper.getModelInfo(interaction, representation))
    end
  end

  class AccessHandler < DefaultHandler
    def doPost(interaction)
      subject = Identity.instance(interaction.params.subject).getHierarchy
      object = interaction['object'].getHierarchy
      term = interaction.params.predicate
      unless Interaction.exist(subject, object, term)
        right = new Interaction
        right.setSubject(subject)
        right.setObject(object)
        right.setScheme('http://b3mn.org/http')
        right.setTerm(term)
        right.save()
      end
    end
    
    def doDelete(interaction)
      # Attentation! HTTP-Server interaction vs HibernateClass Interaction.
      Interaction.getInteraction(interaction.params[id].to_i).delete
      interaction.response.setStatus(200)
    end
  end

end

module Helper
  
  def self.jsonResponse(response, output)
    response.setStatus(200)
    out = response.getWriter
    out.print(ActiveSupport::JSON.encode(output))
  end
  
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
      access_rights << toHash(right, %w{Subject Predicate Uri})
    end
    access = {'access_rights' => access_rights, 'edit_uri' => interaction.hostname + model.getUri + '/access'}
    output = {'uris'=>uris, 'info'=>info,'access'=>access}
  end
end
    