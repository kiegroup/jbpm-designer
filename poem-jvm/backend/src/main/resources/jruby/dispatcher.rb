require 'java'

require 'encoding'

require 'helper'
require 'default_handler'
require 'info_handler'
require 'model_handler'
require 'collection_handler'
require 'new_model_handler'
require 'repository_handler'

include_class 'org.b3mn.poem.Identity'
include_class 'org.b3mn.poem.Access'
include_class 'org.oryxeditor.server.auth.OpenIDAuthenticationServlet'

ServerInteraction = Struct.new :subject, :object, :params, :request, :response, :hostname

class Dispatcher
  def dispatch(request,response)
    hostname = 'http://' + request.getServerName + ':' + request.getServerPort.to_s + request.getContextPath +  request.getServletPath
    puts request.getMethod.capitalize + ': ' + hostname + request.getPathInfo
    rights = {
      'read' => ['Get'],
      'write' => ['Get', 'Put'],
      'owner' => ['Get', 'Post', 'Put', 'Delete']
    }
    relations = {
      '/repository' => 'RepositoryHandler',
      '/model_types' => 'TypeHandler',
      '/model' => 'CollectionHandler',
      '/new' => 'NewModelHandler'
    }
    
    openid = request.getSession.getAttribute(OpenIDAuthenticationServlet::OPENID_SESSION_IDENTIFIER) || 'public'
    uri = request.getPathInfo

    if(handler_name = relations[Helper.getRelation(uri)])
      handler = Handler.module_eval("#{handler_name}").new
      handler.handleRequest(ServerInteraction.new(Identity.ensureSubject(openid), nil, Helper.getParams(request), request, response, hostname))    
    else
      scope = Identity.instance(Helper.getObjectPath(uri))
      access = scope.access(openid, Helper.getRelation(uri)) unless scope.nil?
      unless access.nil?
        if(rights[access.getAccess_term].include?(request.getMethod.capitalize))
          if (Handler.constants.include?(access.getTerm))
            handler = Handler.module_eval("#{access.getTerm}").new
            handler.handleRequest(ServerInteraction.new(Identity.ensureSubject(openid), Identity.instance(Helper.getObjectPath(uri)), Helper.getParams(request), request, response, hostname))
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



