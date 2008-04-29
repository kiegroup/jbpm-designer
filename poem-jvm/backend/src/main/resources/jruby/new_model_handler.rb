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
  class NewModelHandler < DefaultHandler
    def doGet(interaction)
      stencilset = '/oryx' + (interaction.params['stencilset'] || "\/stencilsets\/bpmn\/bpmn.json")
      
      content = "<div id=\"oryx-canvas123\" class=\"-oryx-canvas\">"
  	  content << "<span class=\"oryx-mode\">writeable</span>"
  	  content << "<span class=\"oryx-mode\">fullscreen</span>"
  	  content << "<a href=\"" + stencilset + "\" rel=\"oryx-stencilset\"></a>\n"
  	  content << "</div>\n"
  	  model = {'title' => 'New Process Model', 'content' => content}

      out = interaction.response.getWriter
      out.print(Helper.getOryxModel(model))
      
      interaction.response.setStatus(200)
    	interaction.response.setContentType "application/xhtml+xml"
    end
    
    def doPost(interaction)
      title = interaction.params['title'] || 'New Process'
      type = interaction.params['type'] || 'bpmn'
      mime_type = interaction.params['mime_type'] || 'application/xhtml+xml'
      language = interaction.params['language'] || 'en_US'
      summary = interaction.params['summary'] || 'is new'

      identity = Identity.newModel(interaction.subject, title, type, mime_type, language, summary, interaction.params['data']);
      interaction.response.addHeader('Location', interaction.hostname + identity.getUri + '/self')
      interaction.response.setStatus(201)
    end
  end  
end