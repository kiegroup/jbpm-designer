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
  class ModelHandler < DefaultHandler
    include_class 'org.b3mn.poem.Persistance'
    def doGet(interaction)
      representation = interaction.object.read
      model = {'title' => representation.getTitle, 'content' => representation.getContent}
      interaction.response.setStatus(200)
    	interaction.response.setContentType "application/xhtml+xml"
    	out = interaction.response.getWriter
    	out.print(Helper.getOryxModel(model))
    end

    def doPut(interaction)
      interaction.response.setStatus(200)
    end

    def doPost(interaction)
      if(interaction.params['data'] && interaction.params['svg'])
        Representation.update(interaction.object.getId, nil, nil, interaction.params['data'], interaction.params['svg'])
        interaction.response.setStatus(200)
      else
        interaction.response.setStatus(400)
        out.println("data and/or svg missing")
      end
    end
    
    def doDelete(interaction)
      interaction.object.delete
      interaction.response.setStatus(200)
    end
  end
end