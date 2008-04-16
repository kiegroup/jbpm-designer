module Handler
  class MetaHandler < DefaultHandler
    def doGet(interaction)
      Helper.jsonResponse(interaction.response, Helper.getModelInfo(interaction))
    end
  end
  
  class InfoHandler < DefaultHandler
    def doGet(interaction)
      representation = interaction.object.read
      output = Helper.toHash(representation, %w{Title Summary Updated Created Type})
      output['edit_uri'] = interaction.hostname + interaction.object.getUri + '/info'
      output['self_uri'] = interaction.hostname + interaction.object.getUri + '/self'
      output['meta_uri'] = interaction.hostname + interaction.object.getUri + '/info-access'
      Helper.jsonResponse(interaction.response, output)
    end
      
    def doPost(interaction)
      representation = interaction.object.read
      puts '==== PARAMS ===='
      interaction.params.each do |key, value|
        puts 'key: ' + key + ', value: ' + value
        representation.send "set#{key.capitalize}", value
      end
      representation.update 
      Helper.jsonResponse(interaction.response, Helper.getModelInfo(interaction, interaction.object))
    end
  end

  class AccessHandler < DefaultHandler
    def doPost(interaction)
      subject = Identity.ensureSubject(interaction.params.subject).getUserHierarchy
      object = interaction['object'].getHierarchy
      term = interaction.params.predicate
      right = Interaction.exist(subject, object, term)
      unless right
        right = Interaction.new
        right.setSubject(subject)
        right.setObject(object)
        right.setScheme('http://b3mn.org/http')
        right.setTerm(term)
        right.setObject_self = true
        right.save()
        interaction.response.setStatus(201)
      else
        interaction.response.setStatus(200)
      end
      location = object.getUri + right.getUri
      output = toHash(right, %w{Subject Predicate})
      output['uri'] = location
      interaction.response.addHeader('Location', location)
      out = interaction.response.getWriter
      out.print(ActiveSupport::JSON.encode(output))
    end
    
    def doDelete(interaction)
      # Attentation! HTTP-Server interaction vs HibernateClass Interaction.
      right = Interaction.exist(interaction.params[id])
      if right
        right.delete
        interaction.response.setStatus(200)
      else
        interaction.response.setStatus(404)
      end
    end
  end

end

    