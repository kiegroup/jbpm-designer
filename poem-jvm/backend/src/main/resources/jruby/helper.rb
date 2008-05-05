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
      value = (CGI.escapeHTML(request.getParameter(key)) unless key == 'data') || request.getParameter(key)
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
  
  def self.getModelInfo(interaction, model = nil)
    model ||= interaction.object
    uris = []
    interaction.subject.getServlets.each do |servlet|
      unless ['/access', '/info'].include?(servlet.getRel)
        uris << { 'href' => interaction.hostname + model.getUri + servlet.getRel,
                  'title' => servlet.getTitle }
      end 
    end
    info = toHash(model.read, %w{Title Summary Updated Created Type})
    info['edit_uri'] = interaction.hostname + model.getUri + '/info'
    info['self_uri'] = interaction.hostname + model.getUri + '/self'
    info['meta_uri'] = interaction.hostname + model.getUri + '/info-access'
    info['icon_url'] = '/oryx/stencilsets/' + info['type'] + '/' + info['type'] + '.png'
    access_rights = []
    model.getAccess.each do |right|
      item = toHash(right, %w{Subject Predicate})
      item['uri'] = interaction.hostname + right.getUri
      access_rights << item
    end
    access = {'access_rights' => access_rights, 'edit_uri' => interaction.hostname + model.getUri + '/access'}
    output = {'uris'=>uris, 'info'=>info,'access'=>access}
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
  	model << "<div class='processdata'></div>\n"
  	model << "</body>\n"
  	model << "</html>"
  end
  
end