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

class Testdata < ActiveRecord::Migration
  def self.up   
    execute %q{
      insert into "identity" (uri) values ('https://openid.hpi.uni-potsdam.de/user/mathias.weske');
      insert into "identity" (uri) values ('https://openid.hpi.uni-potsdam.de/user/hagen.overdick');
      insert into "identity" (uri) values ('https://openid.hpi.uni-potsdam.de/user/gero.decker');
      insert into "identity" (uri) values ('https://openid.hpi.uni-potsdam.de/user/martin.czuchra');
      insert into "identity" (uri) values ('https://openid.hpi.uni-potsdam.de/user/ole.eckermann');
      
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
      insert into "structure" (hierarchy, ident_id) select 'U21', id from identity where uri = 'https://openid.hpi.uni-potsdam.de/user/mathias.weske';
      insert into "structure" (hierarchy, ident_id) select 'U22', id from identity where uri = 'https://openid.hpi.uni-potsdam.de/user/hagen.overdick';
      insert into "structure" (hierarchy, ident_id) select 'U23', id from identity where uri = 'https://openid.hpi.uni-potsdam.de/user/gero.decker';
      insert into "structure" (hierarchy, ident_id) select 'U24', id from identity where uri = 'https://openid.hpi.uni-potsdam.de/user/martin.czuchra';
      insert into "structure" (hierarchy, ident_id) select 'U25', id from identity where uri = 'https://openid.hpi.uni-potsdam.de/user/ole.eckermann';
    }
    
    execute %q{
      insert into "structure" (hierarchy, ident_id) select 'U251', id from identity where uri = '/data/model/1';
      insert into "structure" (hierarchy, ident_id) select 'U252', id from identity where uri = '/data/model/2';
      insert into "structure" (hierarchy, ident_id) select 'U253', id from identity where uri = '/data/model/3';
      insert into "structure" (hierarchy, ident_id) select 'U254', id from identity where uri = '/data/model/4';
      insert into "structure" (hierarchy, ident_id) select 'U255', id from identity where uri = '/data/model/5';
      insert into "structure" (hierarchy, ident_id) select 'U256', id from identity where uri = '/data/model/6';
      insert into "structure" (hierarchy, ident_id) select 'U257', id from identity where uri = '/data/model/7';
      insert into "structure" (hierarchy, ident_id) select 'U258', id from identity where uri = '/data/model/8';
      insert into "structure" (hierarchy, ident_id) select 'U259', id from identity where uri = '/data/model/9';
      insert into "structure" (hierarchy, ident_id) select 'U250', id from identity where uri = '/data/model/10';
    }
    
    execute %q{
      insert into "interaction" (subject, object, scheme, term) select 'U1', 'U251', 'http://b3mn.org/http', 'write';
      insert into "interaction" (subject, object, scheme, term) select 'U1', 'U252', 'http://b3mn.org/http', 'read';
      insert into "interaction" (subject, object, scheme, term) select 'U1', 'U254', 'http://b3mn.org/http', 'read';
      insert into "interaction" (subject, object, scheme, term) select 'U23', 'U251', 'http://b3mn.org/http', 'write';
      insert into "interaction" (subject, object, scheme, term) select 'U23', 'U252', 'http://b3mn.org/http', 'write';
      insert into "interaction" (subject, object, scheme, term) select 'U23', 'U254', 'http://b3mn.org/http', 'write';
    }

    
    execute %q{
      insert into "representation"(ident_id, type, title, summary, mime_type)
        select id, 'http://b3mn.org/stencilset/bpmn#', 'SvgTest', 'Ganz tolles Ding!', 'application/xhtml+xml'
      	from identity where uri = '/data/model/1';
      
      INSERT INTO "content" (id, erdf, svg)
        SELECT representation.id, '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>', ''
        FROM identity INNER JOIN representation ON identity.id=representation.ident_id 
        WHERE identity.uri='/data/model/1';
         
      insert into "representation"(ident_id, type, title, summary, mime_type)
        select id, 'http://b3mn.org/stencilset/bpmn#', 'Example', 'Ganz tolles Ding!', 'application/xhtml+xml'
        from identity where uri = '/data/model/2';
        
      INSERT INTO "content" (id, svg, erdf)
        SELECT representation.id, '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN"  "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg xmlns="http://www.w3.org/2000/svg" width="1000" height="600" viewBox="0 0 5 5"><rect id="black_stripe" fill="#000" width="5" height="3"/> </svg>',  '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
        FROM identity INNER JOIN representation ON identity.id=representation.ident_id 
        WHERE identity.uri='/data/model/2';

        
      insert into "representation"(ident_id, type, title, summary, mime_type)
        select id, 'http://b3mn.org/stencilset/bpmn#', 'New Process', 'Ganz tolles Ding!', 'application/xhtml+xml'
        from identity where uri = '/data/model/3';
        
      INSERT INTO "content" (id, svg, erdf)
        SELECT representation.id, '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN"  "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg xmlns="http://www.w3.org/2000/svg" width="1000" height="600" viewBox="0 0 5 5"><rect id="black_stripe" fill="#000" width="5" height="3"/> </svg>', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
        FROM identity INNER JOIN representation ON identity.id=representation.ident_id 
        WHERE identity.uri='/data/model/3';
        
      insert into "representation"(ident_id, type, title, summary, mime_type)
        select id, 'http://b3mn.org/stencilset/bpmn#', 'Brot kaufen', 'Beispiel aus Wikipedia', 'application/xhtml+xml'       
         from identity where uri = '/data/model/4';
        
      INSERT INTO "content" (id, svg, erdf)
        SELECT representation.id, '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN"  "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg xmlns="http://www.w3.org/2000/svg" width="1000" height="600" viewBox="0 0 5 5"><rect id="black_stripe" fill="#000" width="5" height="3"/> </svg>', '<div id="resource0"><span class="oryx-type">http://b3mn.org/stencilset/bpmn#Task</span><span class="oryx-id"></span><span class="oryx-categories"></span><span class="oryx-documentation"></span><span class="oryx-name">Brot
        verkaufen</span><span class="oryx-pool"></span><span class="oryx-lanes"></span><span class="oryx-activitytype">Task</span><span class="oryx-status">None</span><span class="oryx-inputsets"></span><span class="oryx-inputs"></span><span class="oryx-outputsets"></span><span class="oryx-outputs"></span><span class="oryx-iorules"></span><span class="oryx-startquantity">1</span><span class="oryx-looptype">None</span><span class="oryx-loopcondition"></span><span class="oryx-loopcounter">1</span><span class="oryx-loopmaximum">1</span><span class="oryx-testtime">After</span><span class="oryx-mi_condition"></span><span class="oryx-mi_ordering">Sequential</span><span class="oryx-mi_flowcondition">All</span><span class="oryx-complexmi_condition"></span><span class="oryx-iscompensation"></span><span class="oryx-tasktype">None</span><span class="oryx-inmessage"></span><span class="oryx-outmessage"></span><span class="oryx-implementation">Webservice</span><span class="oryx-message"></span><span class="oryx-instatiate"></span><span class="oryx-performers"></span><span class="oryx-script"></span><span class="oryx-taskref"></span><span class="oryx-bgcolor">#ffffcc</span><span class="oryx-isskippable"></span><span class="oryx-bounds">222,20,322,100</span><a rel="raziel-outgoing" href="#resource6"/></div><div id="resource1"><span class="oryx-type">http://b3mn.org/stencilset/bpmn#Task</span><span class="oryx-id"></span><span class="oryx-categories"></span><span class="oryx-documentation"></span><span class="oryx-name">Brot
        backen</span><span class="oryx-pool"></span><span class="oryx-lanes"></span><span class="oryx-activitytype">Task</span><span class="oryx-status">None</span><span class="oryx-inputsets"></span><span class="oryx-inputs"></span><span class="oryx-outputsets"></span><span class="oryx-outputs"></span><span class="oryx-iorules"></span><span class="oryx-startquantity">1</span><span class="oryx-looptype">None</span><span class="oryx-loopcondition"></span><span class="oryx-loopcounter">1</span><span class="oryx-loopmaximum">1</span><span class="oryx-testtime">After</span><span class="oryx-mi_condition"></span><span class="oryx-mi_ordering">Sequential</span><span class="oryx-mi_flowcondition">All</span><span class="oryx-complexmi_condition"></span><span class="oryx-iscompensation"></span><span class="oryx-tasktype">None</span><span class="oryx-inmessage"></span><span class="oryx-outmessage"></span><span class="oryx-implementation">Webservice</span><span class="oryx-message"></span><span class="oryx-instatiate"></span><span class="oryx-performers"></span><span class="oryx-script"></span><span class="oryx-taskref"></span><span class="oryx-bgcolor">#ffffcc</span><span class="oryx-isskippable"></span><span class="oryx-bounds">86,20,186,100</span><a rel="raziel-outgoing" href="#resource4"/></div><div id="resource2"><span class="oryx-type">http://b3mn.org/stencilset/bpmn#StartTimerEvent</span><span class="oryx-id"></span><span class="oryx-categories"></span><span class="oryx-documentation"></span><span class="oryx-pool"></span><span class="oryx-lanes"></span><span class="oryx-eventtype">Start</span><span class="oryx-trigger">Timer</span><span class="oryx-timedate">20/04/07</span><span class="oryx-timecycle"></span><span class="oryx-bgcolor">#ffffff</span><span class="oryx-bounds">16,45,46,75</span><a rel="raziel-outgoing" href="#resource3"/></div><div id="resource3"><span class="oryx-type">http://b3mn.org/stencilset/bpmn#SequenceFlow</span><span class="oryx-id"></span><span class="oryx-categories"></span><span class="oryx-documentation"></span><span class="oryx-name"></span><span class="oryx-source"></span><span class="oryx-target"></span><span class="oryx-conditiontype">None</span><span class="oryx-quantity">1</span><span class="oryx-bounds">46.765625,60,85.78125,60</span><a rel="raziel-outgoing" href="#resource1"/><span class="oryx-dockers">15 15 50 40  # </span><a rel="raziel-parent" href="#"/></div><div id="resource4"><span class="oryx-type">http://b3mn.org/stencilset/bpmn#SequenceFlow</span><span class="oryx-id"></span><span class="oryx-categories"></span><span class="oryx-documentation"></span><span class="oryx-name"></span><span class="oryx-source"></span><span class="oryx-target"></span><span class="oryx-conditiontype">None</span><span class="oryx-quantity">1</span><span class="oryx-bounds">186.9375,60,221.0625,60.00000000000001</span><a rel="raziel-outgoing" href="#resource0"/><span class="oryx-dockers">50 40 50 40  # </span><a rel="raziel-parent" href="#"/></div><div id="resource5"><span class="oryx-type">http://b3mn.org/stencilset/bpmn#Task</span><span class="oryx-id"></span><span class="oryx-categories"></span><span class="oryx-documentation"></span><span class="oryx-name">Kasse
        abrechnen</span><span class="oryx-pool"></span><span class="oryx-lanes"></span><span class="oryx-activitytype">Task</span><span class="oryx-status">None</span><span class="oryx-inputsets"></span><span class="oryx-inputs"></span><span class="oryx-outputsets"></span><span class="oryx-outputs"></span><span class="oryx-iorules"></span><span class="oryx-startquantity">1</span><span class="oryx-looptype">None</span><span class="oryx-loopcondition"></span><span class="oryx-loopcounter">1</span><span class="oryx-loopmaximum">1</span><span class="oryx-testtime">After</span><span class="oryx-mi_condition"></span><span class="oryx-mi_ordering">Sequential</span><span class="oryx-mi_flowcondition">All</span><span class="oryx-complexmi_condition"></span><span class="oryx-iscompensation"></span><span class="oryx-tasktype">None</span><span class="oryx-inmessage"></span><span class="oryx-outmessage"></span><span class="oryx-implementation">Webservice</span><span class="oryx-message"></span><span class="oryx-instatiate"></span><span class="oryx-performers"></span><span class="oryx-script"></span><span class="oryx-taskref"></span><span class="oryx-bgcolor">#ffffcc</span><span class="oryx-isskippable"></span><span class="oryx-bounds">372,20,472,100</span><a rel="raziel-outgoing" href="#resource8"/></div><div id="resource6"><span class="oryx-type">http://b3mn.org/stencilset/bpmn#SequenceFlow</span><span class="oryx-id"></span><span class="oryx-categories"></span><span class="oryx-documentation"></span><span class="oryx-name"></span><span class="oryx-source"></span><span class="oryx-target"></span><span class="oryx-conditiontype">None</span><span class="oryx-quantity">1</span><span class="oryx-bounds">322.21875,61,371.78125,59</span><a rel="raziel-outgoing" href="#resource5"/><span class="oryx-dockers">50 40 50 40  # </span><a rel="raziel-parent" href="#"/></div><div id="resource7"><span class="oryx-type">http://b3mn.org/stencilset/bpmn#EndEvent</span><span class="oryx-id"></span><span class="oryx-categories"></span><span class="oryx-documentation"></span><span class="oryx-pool"></span><span class="oryx-lanes"></span><span class="oryx-eventtype">End</span><span class="oryx-result">None</span><span class="oryx-message"></span><span class="oryx-implementation">Web Service</span><span class="oryx-errorcode"></span><span class="oryx-activity"></span><span class="oryx-linkid"></span><span class="oryx-processref"></span><span class="oryx-results"></span><span class="oryx-bgcolor">#ffffff</span><span class="oryx-bounds">512,46,540,74</span></div><div id="resource8"><span class="oryx-type">http://b3mn.org/stencilset/bpmn#SequenceFlow</span><span class="oryx-id"></span><span class="oryx-categories"></span><span class="oryx-documentation"></span><span class="oryx-name"></span><span class="oryx-source"></span><span class="oryx-target"></span><span class="oryx-conditiontype">None</span><span class="oryx-quantity">1</span><span class="oryx-bounds">472.5625,61,511.1875,59</span><a rel="raziel-outgoing" href="#resource7"/><span class="oryx-dockers">50 40 14 14  # </span><a rel="raziel-parent" href="#"/></div><div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/><a rel="oryx-render" href="#resource0"/><a rel="oryx-render" href="#resource1"/><a rel="oryx-render" href="#resource2"/><a rel="oryx-render" href="#resource3"/><a rel="oryx-render" href="#resource4"/><a rel="oryx-render" href="#resource5"/><a rel="oryx-render" href="#resource6"/><a rel="oryx-render" href="#resource7"/><a rel="oryx-render" href="#resource8"/></div>'        
        FROM identity INNER JOIN representation ON identity.id=representation.ident_id 
        WHERE identity.uri='/data/model/4';
        
      insert into "representation"(ident_id, type, title, summary, mime_type)
        select id, 'http://b3mn.org/stencilset/bpmn#', 'Amazon', 'Ganz tolles Ding!', 'application/xhtml+xml'        
        from identity where uri = '/data/model/5';
        
      INSERT INTO "content" (id, svg, erdf)
        SELECT representation.id, '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN"  "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg xmlns="http://www.w3.org/2000/svg" width="1000" height="600" viewBox="0 0 5 5"><rect id="black_stripe" fill="#000" width="5" height="3"/> </svg>', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
        FROM identity INNER JOIN representation ON identity.id=representation.ident_id 
        WHERE identity.uri='/data/model/5';
        
      insert into "representation"(ident_id, type, title, summary, mime_type)
        select id, 'http://b3mn.org/stencilset/bpmn#', 'Fachstudie', 'Ganz tolles Ding!', 'application/xhtml+xml'        
        from identity where uri = '/data/model/6';
        
      INSERT INTO "content" (id, svg, erdf)
        SELECT representation.id,  '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN"  "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg xmlns="http://www.w3.org/2000/svg" width="1000" height="600" viewBox="0 0 5 5"><rect id="black_stripe" fill="#000" width="5" height="3"/> </svg>', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
        FROM identity INNER JOIN representation ON identity.id=representation.ident_id 
        WHERE identity.uri='/data/model/6';
        
      insert into "representation"(ident_id, type, title, summary, mime_type)
        select id, 'http://b3mn.org/stencilset/bpmn#', 'Elaboration', 'Ganz tolles Ding!', 'application/xhtml+xml'        
        from identity where uri = '/data/model/7';
        
      INSERT INTO "content" (id, svg, erdf)
        SELECT representation.id, '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN"  "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg xmlns="http://www.w3.org/2000/svg" width="1000" height="600" viewBox="0 0 5 5"><rect id="black_stripe" fill="#000" width="5" height="3"/> </svg>', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/bpmn/bpmn.json"/></div>'
        FROM identity INNER JOIN representation ON identity.id=representation.ident_id 
        WHERE identity.uri='/data/model/7';
        
      insert into "representation"(ident_id, type, title, summary, mime_type)
        select id, 'http://b3mn.org/stencilset/epc#', 'Test', 'Ganz tolles Ding!', 'application/xhtml+xml' 
        from identity where uri = '/data/model/8';
        
      INSERT INTO "content" (id, svg, erdf)
        SELECT representation.id, '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN"  "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg xmlns="http://www.w3.org/2000/svg" width="1000" height="600" viewBox="0 0 5 5"><rect id="black_stripe" fill="#000" width="5" height="3"/> </svg>', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/epc/epc.json"/></div>'
        FROM identity INNER JOIN representation ON identity.id=representation.ident_id 
        WHERE identity.uri='/data/model/8';
        
      insert into "representation"(ident_id, type, title, summary, mime_type)
        select id,   'http://b3mn.org/stencilset/epc#', 'TheAnyProcess', 'Ganz tolles Ding!', 'application/xhtml+xml'        
        from identity where uri = '/data/model/9';
        
      INSERT INTO "content" (id, svg, erdf)
        SELECT representation.id,  '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN"  "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg xmlns="http://www.w3.org/2000/svg" width="1000" height="600" viewBox="0 0 5 5"><rect id="black_stripe" fill="#000" width="5" height="3"/> </svg>', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/epc/epc.json"/></div>'
        FROM identity INNER JOIN representation ON identity.id=representation.ident_id 
        WHERE identity.uri='/data/model/9';
        
      insert into "representation"(ident_id, type, title, summary, mime_type)
        select id, 'http://b3mn.org/stencilset/epc#', 'Some Process', 'Ganz tolles Ding!', 'application/xhtml+xml'         
        from identity where uri = '/data/model/10';
        
      INSERT INTO "content" (id, svg, erdf)
        SELECT representation.id, '<?xml version="1.0" encoding="UTF-8"?><!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN"  "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"><svg xmlns="http://www.w3.org/2000/svg" width="1000" height="600" viewBox="0 0 5 5"><rect id="black_stripe" fill="#000" width="5" height="3"/> </svg>', '<div id="oryxcanvas" class="-oryx-canvas"><span class="oryx-mode">writeable</span><span class="oryx-mode">fullscreen</span><a rel="oryx-stencilset" href="/oryx/stencilsets/epc/epc.json"/></div>'
        FROM identity INNER JOIN representation ON identity.id=representation.ident_id 
        WHERE identity.uri='/data/model/10';     
     }

 
  end
end