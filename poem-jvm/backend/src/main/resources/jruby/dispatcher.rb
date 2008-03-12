require 'java'
require 'handler'
require 'info_handler'

include_class 'org.b3mn.poem.Identity'
include_class 'org.b3mn.poem.Access'

Interaction = Struct.new :subject, :object, :params, :request, :response

Handlers = Hash.new do |handler, relation|
  handler[relation] = Hash.new do |relations, term|
    schemes[term] = Proc.new do |interaction|
      PoEM::Handler::DefaultHandler.new.handleRequest(interaction)
    end
  end
end

Handlers['self']['read'] = Proc.new do |interaction|
  Handler::ModelReadHandler.new.handleRequest(interaction)
end
Handlers['self']['write'] = Proc.new do |interaction|
  Handler::ModelWriteHandler.new.handleRequest(interaction)
end
Handlers['self']['owner'] = Proc.new do |interaction|
  Handler::ModelWriteHandler.new.handleRequest(interaction)
end

Handlers['info']['read'] = Proc.new do |interaction|
  Handler::InfoHandler.new.handleRequest(interaction)
end
Handlers['info']['write'] = Proc.new do |interaction|
  Handler::InfoWriteHandler.new.handleRequest(interaction)
end
Handlers['info']['access'] = Proc.new do |interaction|
  Handler::InfoWriteHandler.new.handleRequest(interaction)
end

Handlers['model'][''] = Proc.new do |interaction|
  Handler::CollectionHandler.new.handleRequest(interaction)
end

class Dispatcher
  def dispatch(request,response)

    openid = 'http://ole.myopenid.com/'# request.getSession.getAttributes("openid")
    uri = request.getPathInfo
    if(getRelation(uri) == 'model')
      handler = Handlers['model']['']
      handler.call(Interaction.new(Identity.instance(openid), nil, getParams(request), request, response))
    else
      servlet = Identity.instance(getObjectPath(uri)).access(openid, getRelation(uri))
      handler = Handlers[servlet.getPlugin_relation][servlet.getAccess_term]
      handler.call(Interaction.new(Identity.instance(openid), Identity.instance(uri), getParams(request), request, response))
    end
  end
  
  def getParams(request)
    params = {}
    request.getParameterNames.each do |key|
      params[key] = request.getParameter(key)
    end
    return params
  end
  
  def getObjectPath(uri)
    return uri.gsub(/(\/[^\/]+\/?)$/, "")
  end
  
  def getRelation(uri)
    return uri.match(/(\/[^\/]+\/*)$/).to_s.gsub(/\/*$/, "")
  end
  
end


