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
  Handler::InfoHander.new.handleRequest(interaction)
end

class Dispatcher
  def dispatch(request,response)

    openid = 'http://ole.myopenid.com/'
    rel = 'self'
    uri = request.getPathInfo
    servlet = Identity.instance(uri).access(openid,rel)
    handler = Handlers[servlet.getPlugin_relation][servlet.getAccess_term]
    handler.call(Interaction.new(Identity.instance(openid), Identity.instance(uri), getParams(request), request, response))
    
  end
  
  def getParams(request)
    params = {}
    request.getParameterNames.each do |key|
      params[key] = request.getParameter(key)
    end
    return params
  end
  
end


