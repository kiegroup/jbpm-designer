require 'java'
require 'handler'
require 'info_handler'

include_class 'org.b3mn.poem.Identity'
include_class 'org.b3mn.poem.Access'

Interaction = Struct.new :subject, :object, :params, :request, :response

class Dispatcher
  def dispatch(request,response)
    rights = {
      'read' => ['Get'],
      'write' => ['Get', 'Put'],
      'owner' => ['Get', 'Post', 'Put', 'Delete']
    }
      
    openid = 'http://ole.myopenid.com/'# request.getSession.getAttributes("openid")
    uri = request.getPathInfo
    if(getRelation(uri) == 'model')
      handler = Handler::CollectionHandler.new
      handler.handleRequest(Interaction.new(Identity.instance(openid), nil, getParams(request), request, response))
    else
      access = Identity.instance(getObjectPath(uri)).access(openid, getRelation(uri))
      unless access.nil?
        if(rights[access.getAccess_term].include?(request.getMethod.capitalize))
          if (Handler.constants.include?(access.getTerm))
            handler = Handler.module_eval("#{access.getTerm}").new
            handler.handleRequest(Interaction.new(Identity.instance(openid), Identity.instance(getObjectPath(uri)), getParams(request), request, response))
          else
            response.setStatus(501)
            out = response.getWriter
            out.println("Handler does not exist!")
          end
        else
          response.setStatus(403)
          out = response.getWriter
          out.println("Forbidden!")
        end
      else
        response.setStatus(404)
        out = response.getWriter
        out.println("Unknown Relation!")
      end
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


