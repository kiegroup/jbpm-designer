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
      params[key] = request.getParameter(key)
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