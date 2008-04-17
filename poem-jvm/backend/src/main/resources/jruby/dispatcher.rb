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

ServerInteraction = Struct.new :subject, :object, :params, :request, :response, :hostname

class Dispatcher
  def dispatch(request,response)
    hostname = 'http://' + `hostname`.chomp + ':8080/poem-backend-1.0/poem'
    rights = {
      'read' => ['Get'],
      'write' => ['Get', 'Put'],
      'owner' => ['Get', 'Post', 'Put', 'Delete']
    }
    puts '======= NEW REQUEST =========='
    puts request.getMethod.capitalize + ': ' + request.getPathInfo
    openid = 'http://ole.myopenid.com/'# request.getSession.getAttributes("openid")
    uri = request.getPathInfo
    if(Helper.getRelation(uri) == '/repository')
      handler = Handler::RepositoryHandler.new
      handler.handleRequest(ServerInteraction.new(Identity.ensureSubject(openid),nil, nil, request, response, hostname))
    elsif(Helper.getRelation(uri) == '/model_types')
      handler = Handler::TypeHandler.new
      handler.handleRequest(ServerInteraction.new(Identity.ensureSubject(openid), nil, nil, request, response, hostname))
    elsif(Helper.getRelation(uri) == '/model')
      handler = Handler::CollectionHandler.new
      handler.handleRequest(ServerInteraction.new(Identity.ensureSubject(openid), nil, Helper.getParams(request), request, response, hostname))
    elsif(Helper.getRelation(uri) == '/new')
      handler = Handler::NewModelHandler.new
      handler.handleRequest(ServerInteraction.new(nil, nil, Helper.getParams(request), request, response, hostname))
    else
      scope = Identity.instance(Helper.getObjectPath(uri))
      access = scope.access(openid, Helper.getRelation(uri)) unless scope.nil?
      unless access.nil?
        puts access.getTerm + 'and' + access.getAccess_term
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



