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
      interaction.params.each do |key, value|
        representation.send "set#{key.capitalize}", value
      end
      representation.update 
      Helper.jsonResponse(interaction.response, Helper.getModelInfo(interaction, interaction.object))
    end
  end

  class AccessHandler < DefaultHandler
    def doPost(interaction)
      subject = Identity.ensureSubject(interaction.params['subject'])
      subject_hierarchy = subject.getUserHierarchy
      object_hierarchy = interaction.object.getModelHierarchy
      term = interaction.params['predicate']
      right = Interaction.exist(subject_hierarchy, object_hierarchy, term)
      unless right
        right = Interaction.new
        right.setSubject(subject_hierarchy)
        right.setObject(object_hierarchy)
        right.setScheme('http://b3mn.org/http')
        right.setTerm(term)
        right.setObject_self(true)
        right.save()
        interaction.response.setStatus(201)
      else
        interaction.response.setStatus(200)
      end
      
      location = interaction.hostname + interaction.object.getUri + right.getUri
      output = {'predicate' => right.getPredicate}
      output['subject'] = subject.getUri
      output['uri'] = location
      interaction.response.addHeader('Location', location)
      out = interaction.response.getWriter
      out.print(ActiveSupport::JSON.encode(output))
    end
    
    def doDelete(interaction)
      # Attentation! HTTP-Server interaction vs HibernateClass Interaction.
      right = Interaction.exist(interaction.params['id'].to_i)
      if right
        right.delete
        interaction.response.setStatus(200)
      else
        interaction.response.setStatus(404)
      end
    end
  end

end

    