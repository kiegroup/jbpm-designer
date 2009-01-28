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
  class MetaHandler < DefaultHandler
    def doGet(interaction)
      Helper.jsonResponse(interaction.response, Helper.getModelMetadata(interaction))
    end
  end
  
  class InfoHandler < DefaultHandler
    def doGet(interaction)
      Helper.jsonResponse(interaction.response, Helper.getModelInfo(interaction.object, interaction.hostname))
    end
      
    def doPost(interaction)
      Representation.update(interaction.object.getId, interaction.params['title'], interaction.params['summary'], nil, nil)
      Helper.jsonResponse(interaction.response, Helper.getModelMetadata(interaction, interaction.object))
    end
  end

  class AccessHandler < DefaultHandler
    def doPost(interaction)
      subject = Identity.ensureSubject(interaction.params['subject'].downcase)
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
      # Attention! HTTP-Server interaction vs HibernateClass Interaction.
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

    