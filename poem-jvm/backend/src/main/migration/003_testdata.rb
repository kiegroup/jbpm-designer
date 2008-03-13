class Testdata < ActiveRecord::Migration
  def self.up   
    execute %q{
      insert into "identity" (uri) values ('BPT');
      insert into "identity" (uri) values ('Hiwi');
      insert into "identity" (uri) values ('Assistant');
      insert into "identity" (uri) values ('http://weske.myopenid.com/');
      insert into "identity" (uri) values ('http://hagen.myopenid.com/');
      insert into "identity" (uri) values ('http://gero.myopenid.com/');
      insert into "identity" (uri) values ('http://martin.myopenid.com/');
      insert into "identity" (uri) values ('http://matthias.myopenid.com/');
      insert into "identity" (uri) values ('http://ole.myopenid.com/');
      
      insert into "identity" (uri) values ('/data/model');
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
      insert into "identity" (uri) values ('/data/model/internal');
      insert into "identity" (uri) values ('/data/model/internal/1');
    }
    
    execute %q{
      insert into "structure" (hierarchy, ident_id) select 'U', id from identity where uri = '';
      insert into "structure" (hierarchy, ident_id) select 'U1', id from identity where uri = 'BPT';
      insert into "structure" (hierarchy, ident_id) select 'U11', id from identity where uri = 'http://weske.myopenid.com/';
      insert into "structure" (hierarchy, ident_id) select 'U12', id from identity where uri = 'Assistant';
      insert into "structure" (hierarchy, ident_id) select 'U13', id from identity where uri = 'Hiwi';
      insert into "structure" (hierarchy, ident_id) select 'U121', id from identity where uri = 'http://hagen.myopenid.com/';
      insert into "structure" (hierarchy, ident_id) select 'U122', id from identity where uri = 'http://gero.myopenid.com/';
      insert into "structure" (hierarchy, ident_id) select 'U131', id from identity where uri = 'http://martin.myopenid.com/';
      insert into "structure" (hierarchy, ident_id) select 'U132', id from identity where uri = 'http://matthias.myopenid.com/';
      insert into "structure" (hierarchy, ident_id) select 'U133', id from identity where uri = 'http://ole.myopenid.com/';
    }
    
    execute %q{
      insert into "structure" (hierarchy, ident_id) select 'D',  id from identity where uri = '/data/model';
      insert into "structure" (hierarchy, ident_id) select 'D1', id from identity where uri = '/data/model/1';
      insert into "structure" (hierarchy, ident_id) select 'D2', id from identity where uri = '/data/model/2';
      insert into "structure" (hierarchy, ident_id) select 'D3', id from identity where uri = '/data/model/3';
      insert into "structure" (hierarchy, ident_id) select 'D4', id from identity where uri = '/data/model/4';
      insert into "structure" (hierarchy, ident_id) select 'D5', id from identity where uri = '/data/model/5';
      insert into "structure" (hierarchy, ident_id) select 'D6', id from identity where uri = '/data/model/6';
      insert into "structure" (hierarchy, ident_id) select 'D7', id from identity where uri = '/data/model/7';
      insert into "structure" (hierarchy, ident_id) select 'D8', id from identity where uri = '/data/model/8';
      insert into "structure" (hierarchy, ident_id) select 'D9', id from identity where uri = '/data/model/9';
      insert into "structure" (hierarchy, ident_id) select 'D0', id from identity where uri = '/data/model/10';
      
      insert into "structure" (hierarchy, ident_id) select 'I', id from identity where uri = '/data/model/internal';
      insert into "structure" (hierarchy, ident_id) select 'I1', id from identity where uri = '/data/model/internal/1';
    }
    
    execute %q{
      insert into "interaction" (subject, object, scheme, term) select 'U', 'D', 'http://b3mn.org/http', 'read';
      insert into "interaction" (subject, object, scheme, term) select 'U1', 'I', 'http://b3mn.org/http', 'write';
      insert into "interaction" (subject, object, scheme, term) select 'U133', 'D1', 'http://b3mn.org/http', 'owner';
      insert into "interaction" (subject, object, scheme, term) select 'U132', 'D2', 'http://b3mn.org/http', 'owner';
      insert into "interaction" (subject, object, scheme, term) select 'U131', 'D3', 'http://b3mn.org/http', 'owner';
      insert into "interaction" (subject, object, scheme, term) select 'U133', 'D4', 'http://b3mn.org/http', 'owner';
      insert into "interaction" (subject, object, scheme, term) select 'U132', 'D5', 'http://b3mn.org/http', 'owner';
      insert into "interaction" (subject, object, scheme, term) select 'U121', 'D6', 'http://b3mn.org/http', 'owner';
      insert into "interaction" (subject, object, scheme, term) select 'U122', 'D7', 'http://b3mn.org/http', 'owner';
      insert into "interaction" (subject, object, scheme, term) select 'U122', 'D8', 'http://b3mn.org/http', 'owner';
      insert into "interaction" (subject, object, scheme, term) select 'U133', 'D9', 'http://b3mn.org/http', 'owner';
      insert into "interaction" (subject, object, scheme, term) select 'U133', 'D0', 'http://b3mn.org/http', 'owner';
    }
    
    execute %q{
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'MyProcess', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/files/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/1';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'Example', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/files/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/2';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'New Process', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/files/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/3';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'Brot kaufen', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/files/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/4';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'Amazon', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/files/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/5';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'Fachstudie', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/files/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/6';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'Elaboration', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/files/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/7';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'epc', 'Test', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/files/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/8';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'epc', 'TheAnyProcess', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/files/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/9';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'epc', 'Some Process', 'Ganz tolles Ding!', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/files/stencilsets/bpmn/bpmn.json"/></div>'
      	from identity where uri = '/data/model/10';
      insert into "representation"(ident_id, type, title, summary, mime_type, content)
        select id, 'bpmn', 'Secret Process', 'How to rule the world', 'application/xhtml+xml', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/files/stencilsets/bpmn/bpmn.json"/></div>'
    	from identity where uri = '/data/model/11';
    }
    
    execute %q{
      insert into "plugin"(rel, scheme, term, title)
      	select '/self', 'ruby', 'ModelHandler', 'Oryx Editor';
      insert into "plugin"(rel, scheme, term, title)
      	select '/info', 'ruby', 'InfoHandler', 'edit info';
      insert into "plugin"(rel, scheme, term, title)
        select '/access', 'ruby', 'AccessHander', 'edit access';
      insert into "plugin"(rel, scheme, term, title)
        select '/info-access', 'ruby', 'MetaHandler', 'About';
      	
    }

 
  end
end