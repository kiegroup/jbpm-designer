require 'handler'
#require 'activesupport'

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
          to = Time.now
        end
        
        if interaction.params['type']
          type = interaction.params['type']
        else
          type = '%'
        end
        
        models = interaction.subject.getModels(type, from, to)
        #TODO: Each representation and access to json inda response
        models.each do |model|
          Identity.instance(model.getIdent_id).getAccess
        end
      end
    end

  class InfoHandler < DefaultHandler
      def doGet(interaction)
        #TODO: Write Response as json
        interaction.response.setStatus(200)
        out = interaction.response.getWriter
        #out.print(ActiveSupport::Json.encode(interaction.object.read))
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

end