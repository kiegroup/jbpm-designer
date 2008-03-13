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
    if(Helper.getRelation(uri) == '/model')
      handler = Handler::CollectionHandler.new
      handler.handleRequest(Interaction.new(Identity.instance(openid), nil, Helper.getParams(request), request, response))
    else
      access = Identity.instance(Helper.getObjectPath(uri)).access(openid, Helper.getRelation(uri))
      unless access.nil?
        if(rights[access.getAccess_term].include?(request.getMethod.capitalize))
          if (Handler.constants.include?(access.getTerm))
            handler = Handler.module_eval("#{access.getTerm}").new
            handler.handleRequest(Interaction.new(Identity.instance(openid), Identity.instance(Helper.getObjectPath(uri)), Helper.getParams(request), request, response))
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
end
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
  
end


