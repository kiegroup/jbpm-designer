##############################
 # Copyright (c) 2008
 # Ole Eckermann
 #
 # Permission is hereby granted, free of charge, to any person obtaining a
 # copy of this software and associated documentation files (the "Software"),
 # to deal in the Software without restriction, including without limitation
 # the rights to use, copy, modify, merge, publish, distribute, sublicense,
 # and/or sell copies of the Software, and to permit persons to whom the
 # Software is furnished to do so, subject to the following conditions:
 #
 # The above copyright notice and this permission notice shall be included in
 # all copies or substantial portions of the Software.
 #
 # THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 # IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 # FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 # AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 # LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 # FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 # DEALINGS IN THE SOFTWARE.
 ##############################
 
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
       
       owner = interaction.params['owner']=='true'
       is_shared = interaction.params['is_shared']=='true'
       contributor = interaction.params['contributor']=='true'
       reader = interaction.params['reader']=='true'
       is_public = interaction.params['is_public']=='true'
        
       type = interaction.params['type'] || '%'
        
       models = interaction.subject.getModels(type, from, to, owner, is_shared, is_public, contributor, reader)
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