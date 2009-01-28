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

module Handler
  class JavaHandler
    def doGet(interaction)
      @servlet.doGet(interaction.request,  interaction.response,  interaction.subject,  interaction.object, interaction.hostname)
    end
    def doPost(interaction)
      @servlet.doPost(interaction.request,  interaction.response,  interaction.subject,  interaction.object, interaction.hostname)
    end
    def doPut(interaction)
      @servlet.doPut(interaction.request,  interaction.response,  interaction.subject,  interaction.object, interaction.hostname)
    end
    def doDelete(interaction)
      @servlet.doDelete(interaction.request,  interaction.response,  interaction.subject,  interaction.object, interaction.hostname)
    end
  
    def handleRequest(interaction, servlet_name, context)
      include_class 'org.b3mn.poem.servlets.' + servlet_name
      # Create new instance of the java plugin
      @servlet = ObjectSpace.const_get(servlet_name).new
      # Initialize the instance with the servlet context
      @servlet.setServletContext(context)
      send "do#{interaction.request.getMethod.capitalize}", interaction
    end
  end
end