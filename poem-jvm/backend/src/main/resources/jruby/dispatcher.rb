##############################
 # Copyright (c) 2008
 # Ole Eckermann, Hagen Overdick
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


require 'java'

require 'encoding'
require 'cgi'

require 'helper'
require 'default_handler'
require 'info_handler'
require 'model_handler'
require 'collection_handler'
require 'new_model_handler'
require 'repository_handler'
require 'java_handler'

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
      'write' => ['Get', 'Post'],
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
          if (access.getScheme == 'java')
            handler = Handler::JavaHandler.new
            handler.handleRequest(ServerInteraction.new(Identity.ensureSubject(openid), Identity.instance(Helper.getObjectPath(uri)), nil, request, response, hostname), access.getTerm)
          elsif (access.getScheme == 'ruby' && Handler.constants.include?(access.getTerm))
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



