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

module Helper
  include_class 'org.b3mn.poem.Persistance'
  @@model_types = {
      "http://b3mn.org/stencilset/bpmn#" => {
          :uri         => "/stencilsets/bpmn/bpmn.json",
          :title       => "BPMN",
          :description => "Business Process Model Notation",
          :icon_url    => "/oryx/stencilsets/bpmn/bpmn.png"
      },
      "http://b3mn.org/stencilset/petrinet#" => {
          :uri         => "/stencilsets/petrinets/petrinet.json",
          :title       => "Petri Net",
          :description => "Petri Net",
          :icon_url => "/oryx/stencilsets/petrinets/petrinets.png"
      },
      "http://b3mn.org/stencilset/epc#" => {
          :uri         => "/stencilsets/epc/epc.json",
          :title       => "EPC",
          :description => "Event-Driven Process Chain",
          :icon_url    => "/oryx/stencilsets/epc/epc.png"
      },
      "http://www.example.org/workflownets#" => {
          :uri         => "/stencilsets/workflownets/workflownets.json",
          :title       => "Workflow Net",
          :description => "Workflow Net",
          :icon_url    => "/oryx/stencilsets/workflownets/workflownets.png"
      },
  	  "http://b3mn.org/stencilset/bpmn1.1#" => {
          :uri         => "/stencilsets/bpmn1.1/bpmn1.1.json",
          :title       => "BPMN 1.1",
          :description => "Business Process Model Notation 1.1",
          :icon_url    => "/oryx/stencilsets/bpmn1.1/bpmn1.1.png"
      },
  	  "http://www.fmc-modeling.org/stencilsets/fmcblockdiagram#" => {
          :uri         => "/stencilsets/fmcblockdiagram/fmcblockdiagram.json",
          :title       => "FMC Block Diagram",
          :description => "FMC Block Diagrams show compositional structures as a composition of collaborating system components.",
          :icon_url    => "/oryx/stencilsets/fmcblockdiagram/fmcblockdiagram.png"
      }
  }
  
  def self.toHash(obj, keys)
    output = {}
    keys.each do |key|
      method = "get#{key.capitalize}"
      output[key.downcase] = obj.send(method).to_s if obj.respond_to?(method)
    end
    return output
  end
  
  def self.getParams(request)
    params = {}
    request.getParameterNames.each do |key|
      value = (CGI.escapeHTML(request.getParameter(key)) unless key == 'data' || key == 'svg') || request.getParameter(key)
      params[key] = value
    end
    return params
  end
  
  def self.getObjectPath(uri)
    return uri.gsub(/(\/[^\/]+\/?)$/, "")
  end
  
  def self.getRelation(uri)
    return uri.match(/(\/[^\/]+\/*)$/).to_s.gsub(/\/*$/, "")
  end
  
  def self.jsonResponse(response, output)
    response.setStatus(200)
    out = response.getWriter
    out.print(ActiveSupport::JSON.encode(output))
  end
  
  def self.getModelMetadata(interaction, model = nil)
    model ||= interaction.object
    uris = []
    interaction.subject.getServlets.each do |servlet|
      unless ['/access', '/info'].include?(servlet.getRel)
        uris << { 'href' => interaction.hostname + model.getUri + servlet.getRel,
                  'title' => servlet.getTitle }
      end 
    end
    info = getModelInfo(model, interaction.hostname)
    access_rights = []
    model.getAccess.each do |right|
      item = toHash(right, %w{Subject Predicate})
      item['uri'] = interaction.hostname + right.getUri
      access_rights << item
    end
    access = {'access_rights' => access_rights, 'edit_uri' => interaction.hostname + model.getUri + '/access'}
    output = {'uris'=>uris, 'info'=>info,'access'=>access}
  end
  
  def self.getModelInfo(model, hostname)
    representation = model.read
    output = Helper.toHash(representation, %w{Title Summary Updated Created Type})
    output['edit_uri'] = hostname + model.getUri + '/info'
    output['self_uri'] = hostname + model.getUri + '/self'
    output['meta_uri'] = hostname + model.getUri + '/info-access'
    output['icon_url'] = @@model_types[representation.getType][:icon_url]
    return output
  end
    
  def self.getModelTypes()    
    types = @@model_types.collect { |key, value|
      {:namespace => key}.merge(value)
    }
  end
  
  def self.getOryxModel(representation)
    oryx_path = '/oryx/'
  	model = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
  	model << "<html xmlns=\"http://www.w3.org/1999/xhtml\"\n"
  	model << "xmlns:b3mn=\"http://b3mn.org/2007/b3mn\"\n"
  	model << "xmlns:ext=\"http://b3mn.org/2007/ext\"\n"
  	model << "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
  	model << "xmlns:atom=\"http://b3mn.org/2007/atom+xhtml\">\n"
  	model << "<head profile=\"http://purl.org/NET/erdf/profile\">\n"
  	model << "<title>" + representation['title'] + " - Oryx</title>\n"
  	model << "<!-- libraries -->\n"
  	model << "<script src=\"" + oryx_path + "lib/prototype-1.5.1.js\" type=\"text/javascript\" />\n"
  	model << "<script src=\"" + oryx_path + "lib/path_parser.js\" type=\"text/javascript\" />\n"
  	model << "<script src=\"" + oryx_path + "lib/ext-2.0.2/adapter/yui/yui-utilities.js\" type=\"text/javascript\" />\n"
  	model << "<script src=\"" + oryx_path + "lib/ext-2.0.2/adapter/yui/ext-yui-adapter.js\" type=\"text/javascript\" />\n"
  	model << "<script src=\"" + oryx_path + "lib/ext-2.0.2/ext-all.js\" type=\"text/javascript\" />\n"
  	model << "<script src=\"" + oryx_path + "lib/ext-2.0.2/color-field.js\" type=\"text/javascript\" />\n"
  	model << "<style media=\"screen\" type=\"text/css\">\n"
  	model << "@import url(\"" + oryx_path + "lib/ext-2.0.2/resources/css/ext-all.css\");\n"
  	model << "@import url(\"" + oryx_path + "lib/ext-2.0.2/resources/css/xtheme-gray.css\");\n"
  	model << "</style>\n"

  	model << "<script src=\"" + oryx_path + "shared/kickstart.js\" type=\"text/javascript\" />\n"
  	model << "<script src=\"" + oryx_path + "shared/erdfparser.js\" type=\"text/javascript\" />\n"
  	model << "<script src=\"" + oryx_path + "shared/datamanager.js\" type=\"text/javascript\" />\n"
  	model << "<!-- oryx editor -->\n"
  	model << "<script src=\"" + oryx_path + "oryx.js\" type=\"text/javascript\" />\n"
  	model << "<script src=\"" + oryx_path + "i18n/translation_en_us.js\" type=\"text/javascript\" />\n"
  	model << "<link rel=\"Stylesheet\" media=\"screen\" href=\"" + oryx_path + "css/theme_norm.css\" type=\"text/css\" />\n"

  	model << "<!-- erdf schemas -->\n"
  	model << "<link rel=\"schema.dc\" href=\"http://purl.org/dc/elements/1.1/\" />\n"
  	model << "<link rel=\"schema.dcTerms\" href=\"http://purl.org/dc/terms/\" />\n"
  	model << "<link rel=\"schema.b3mn\" href=\"http://b3mn.org\" />\n"
  	model << "<link rel=\"schema.oryx\" href=\"http://oryx-editor.org/\" />\n"
  	model << "<link rel=\"schema.raziel\" href=\"http://raziel.org/\" />\n"
  	model << "</head>\n"
  	
  	model << "<body style=\"overflow:hidden;\"><div class='processdata' style='display:none'>\n"
  	model << representation['content']
  	model << "\n"
  	model << "</div>\n"
  	model << "</body>\n"
  	model << "</html>"
  end
  
end
