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

class Dbroot < ActiveRecord::Migration
  def self.up 
    # create basic data tree  
    execute %q{
      insert into "identity" (uri) values ('root');
      insert into "identity" (uri) values ('public');
      insert into "identity" (uri) values ('groups');
      insert into "identity" (uri) values ('ownership');
    }
    
    execute %q{
      insert into "structure" (hierarchy, ident_id) select 'U', id from identity where uri = 'root';
      insert into "structure" (hierarchy, ident_id) select 'U1', id from identity where uri = 'public';
      insert into "structure" (hierarchy, ident_id) select 'U2', id from identity where uri = 'ownership';
      insert into "structure" (hierarchy, ident_id) select 'U3', id from identity where uri = 'groups';
      }
    
    # set owner rights: Each childnote of 'ownership' is owner of its childs
    execute %q{
      insert into "interaction" (subject, subject_descend, object, object_self, object_descend, object_restrict_to_parent, scheme, term) select 'U2',true,'U2',false,true,true,'http://b3mn.org/http','owner';
    }
    
    # create basic plugin relation
    execute %q{
      insert into plugin (rel, scheme, term, title)
      	select '/self', 'ruby', 'ModelHandler', 'Oryx Editor';
      insert into plugin (rel, scheme, term, title)
      	select '/info', 'ruby', 'InfoHandler', 'edit info';
      insert into plugin (rel, scheme, term, title)
        select '/access', 'ruby', 'AccessHandler', 'edit access';
      insert into plugin (rel, scheme, term, title)
        select '/info-access', 'ruby', 'MetaHandler', 'About';
      insert into plugin (rel, scheme, term, title) 
        select '/svg', 'java', 'ImageRenderer', 'Model as SVG';
      insert into plugin (rel, scheme, term, title) 
        select '/pdf', 'java', 'PdfRenderer', 'Model as PDF';
      insert into plugin (rel, scheme, term, title) 
        select '/png', 'java', 'PngRenderer', 'Model as PNG'; 
      insert into plugin (rel, scheme, term, title) 
        select '/rdf', 'java', 'RdfExporter', 'Model as RDF'; 
          
    }

  end
end