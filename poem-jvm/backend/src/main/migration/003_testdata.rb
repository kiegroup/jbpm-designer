class Testdata < ActiveRecord::Migration
  def self.up   
    execute %q{
      insert into "identity" (uri) values ('root');
      insert into "identity" (uri) values ('public');
      insert into "identity" (uri) values ('groups');
      insert into "identity" (uri) values ('ownership');
      insert into "identity" (uri) values ('BPT');
      insert into "identity" (uri) values ('Hiwi');
      insert into "identity" (uri) values ('Assistant');
      insert into "identity" (uri) values ('http://weske.myopenid.com/');
      insert into "identity" (uri) values ('http://hagen.myopenid.com/');
      insert into "identity" (uri) values ('http://gero.myopenid.com/');
      insert into "identity" (uri) values ('http://martin.myopenid.com/');
      insert into "identity" (uri) values ('http://matthias.myopenid.com/');
      insert into "identity" (uri) values ('http://ole.myopenid.com/');
      
      insert into "identity" (uri) values ('/data/model/1');
      insert into "identity" (uri) values ('/data/model/2');
      insert into "identity" (uri) values ('/data/model/3');
      insert into "identity" (uri) values ('/data/model/4');
      insert into "identity" (uri) values ('/data/model/5');
      insert into "identity" (uri) values ('/data/model/6');
      insert into "identity" (uri) values ('/data/model/7');
      insert into "identity" (uri) values ('/data/model/8');
      insert into "identity" (uri) values ('/data/model/9');
      insert into "identity" (uri) values ('/data/model/10');
    }
    
    execute %q{
      insert into "structure" (hierarchy, ident_id) select 'U', id from identity where uri = 'root';
      insert into "structure" (hierarchy, ident_id) select 'U1', id from identity where uri = 'public';
      insert into "structure" (hierarchy, ident_id) select 'U2', id from identity where uri = 'ownership';
      insert into "structure" (hierarchy, ident_id) select 'U3', id from identity where uri = 'groups';
      
      insert into "structure" (hierarchy, ident_id) select 'U31', id from identity where uri = 'BPT';      
      insert into "structure" (hierarchy, ident_id) select 'U312', id from identity where uri = 'Assistant';
      insert into "structure" (hierarchy, ident_id) select 'U313', id from identity where uri = 'Hiwi';
      
      insert into "structure" (hierarchy, ident_id) select 'U21', id from identity where uri = 'http://weske.myopenid.com/';
      insert into "structure" (hierarchy, ident_id) select 'U22', id from identity where uri = 'http://hagen.myopenid.com/';
      insert into "structure" (hierarchy, ident_id) select 'U23', id from identity where uri = 'http://gero.myopenid.com/';
      insert into "structure" (hierarchy, ident_id) select 'U24', id from identity where uri = 'http://martin.myopenid.com/';
      insert into "structure" (hierarchy, ident_id) select 'U25', id from identity where uri = 'http://matthias.myopenid.com/';
      insert into "structure" (hierarchy, ident_id) select 'U26', id from identity where uri = 'http://ole.myopenid.com/';
    }
    
    execute %q{
      insert into "structure" (hierarchy, ident_id) select 'U221', id from identity where uri = '/data/model/1';
      insert into "structure" (hierarchy, ident_id) select 'U222', id from identity where uri = '/data/model/2';
      insert into "structure" (hierarchy, ident_id) select 'U223', id from identity where uri = '/data/model/3';
      insert into "structure" (hierarchy, ident_id) select 'U251', id from identity where uri = '/data/model/4';
      insert into "structure" (hierarchy, ident_id) select 'U252', id from identity where uri = '/data/model/5';
      insert into "structure" (hierarchy, ident_id) select 'U253', id from identity where uri = '/data/model/6';
      insert into "structure" (hierarchy, ident_id) select 'U261', id from identity where uri = '/data/model/7';
      insert into "structure" (hierarchy, ident_id) select 'U262', id from identity where uri = '/data/model/8';
      insert into "structure" (hierarchy, ident_id) select 'U263', id from identity where uri = '/data/model/9';
      insert into "structure" (hierarchy, ident_id) select 'U264', id from identity where uri = '/data/model/10';
    }
    
    execute %q{
      insert into "interaction" (subject, object, scheme, term) select 'U1', 'U221', 'http://b3mn.org/http', 'read';
      insert into "interaction" (subject, object, scheme, term) select 'U1', 'U222', 'http://b3mn.org/http', 'read';
      insert into "interaction" (subject, object, scheme, term) select 'U1', 'U223', 'http://b3mn.org/http', 'read';
      insert into "interaction" (subject, object, scheme, term) select 'U1', 'U251', 'http://b3mn.org/http', 'read';
      insert into "interaction" (subject, object, scheme, term) select 'U1', 'U252', 'http://b3mn.org/http', 'read';
      insert into "interaction" (subject, object, scheme, term) select 'U1', 'U253', 'http://b3mn.org/http', 'read';
      insert into "interaction" (subject, object, scheme, term) select 'U1', 'U261', 'http://b3mn.org/http', 'read';
      insert into "interaction" (subject, object, scheme, term) select 'U1', 'U264', 'http://b3mn.org/http', 'read';
      insert into "interaction" (subject, object, scheme, term) select 'U22', 'U264', 'http://b3mn.org/http', 'write';
      insert into "interaction" (subject, subject_descend, object, object_self, object_descend, object_restrict_to_parent, scheme, term) select 'U2',true,'U2',false,true,true,'http://b3mn.org/http','owner';
    }

    
    execute %q{
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'MyProcess', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/1';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'Example', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/2';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'New Process', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/3';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'Brot kaufen', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/4';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'Amazon', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/5';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'Fachstudie', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/6';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'Elaboration', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/7';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'epc', 'Test', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/8';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'epc', 'TheAnyProcess', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/9';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'epc', 'Some Process', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/10';
    }
    
    execute %q{
      insert into "plugin"(rel, scheme, term, title)
      	select '/self', 'ruby', 'ModelHandler', 'Oryx Editor';
      insert into "plugin"(rel, scheme, term, title)
      	select '/info', 'ruby', 'InfoHandler', 'edit info';
      insert into "plugin"(rel, scheme, term, title)
        select '/access', 'ruby', 'AccessHandler', 'edit access';
      insert into "plugin"(rel, scheme, term, title)
        select '/info-access', 'ruby', 'MetaHandler', 'About';
    }

 
  end
end