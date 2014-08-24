/*
 * Ext JS Library 2.3.0
 * Copyright(c) 2006-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */


Ext.data.SortTypes={none:function(s){return s;},stripTagsRE:/<\/?[^>]+>/gi,asText:function(s){return String(s).replace(this.stripTagsRE,"");},asUCText:function(s){return String(s).toUpperCase().replace(this.stripTagsRE,"");},asUCString:function(s){return String(s).toUpperCase();},asDate:function(s){if(!s){return 0;}
if(Ext.isDate(s)){return s.getTime();}
return Date.parse(String(s));},asFloat:function(s){var val=parseFloat(String(s).replace(/,/g,""));if(isNaN(val))val=0;return val;},asInt:function(s){var val=parseInt(String(s).replace(/,/g,""));if(isNaN(val))val=0;return val;}};

Ext.data.Record=function(data,id){this.id=(id||id===0)?id:++Ext.data.Record.AUTO_ID;this.data=data||{};};Ext.data.Record.create=function(o){var f=Ext.extend(Ext.data.Record,{});var p=f.prototype;p.fields=new Ext.util.MixedCollection(false,function(field){return field.name;});for(var i=0,len=o.length;i<len;i++){p.fields.add(new Ext.data.Field(o[i]));}
f.getField=function(name){return p.fields.get(name);};return f;};Ext.data.Record.AUTO_ID=1000;Ext.data.Record.EDIT='edit';Ext.data.Record.REJECT='reject';Ext.data.Record.COMMIT='commit';Ext.data.Record.prototype={dirty:false,editing:false,error:null,modified:null,join:function(store){this.store=store;},set:function(name,value){if(String(this.data[name])==String(value)){return;}
this.dirty=true;if(!this.modified){this.modified={};}
if(typeof this.modified[name]=='undefined'){this.modified[name]=this.data[name];}
this.data[name]=value;if(!this.editing&&this.store){this.store.afterEdit(this);}},get:function(name){return this.data[name];},beginEdit:function(){this.editing=true;this.modified={};},cancelEdit:function(){this.editing=false;delete this.modified;},endEdit:function(){this.editing=false;if(this.dirty&&this.store){this.store.afterEdit(this);}},reject:function(silent){var m=this.modified;for(var n in m){if(typeof m[n]!="function"){this.data[n]=m[n];}}
this.dirty=false;delete this.modified;this.editing=false;if(this.store&&silent!==true){this.store.afterReject(this);}},commit:function(silent){this.dirty=false;delete this.modified;this.editing=false;if(this.store&&silent!==true){this.store.afterCommit(this);}},getChanges:function(){var m=this.modified,cs={};for(var n in m){if(m.hasOwnProperty(n)){cs[n]=this.data[n];}}
return cs;},hasError:function(){return this.error!=null;},clearError:function(){this.error=null;},copy:function(newId){return new this.constructor(Ext.apply({},this.data),newId||this.id);},isModified:function(fieldName){return!!(this.modified&&this.modified.hasOwnProperty(fieldName));}};

Ext.StoreMgr=Ext.apply(new Ext.util.MixedCollection(),{register:function(){for(var i=0,s;s=arguments[i];i++){this.add(s);}},unregister:function(){for(var i=0,s;s=arguments[i];i++){this.remove(this.lookup(s));}},lookup:function(id){return typeof id=="object"?id:this.get(id);},getKey:function(o){return o.storeId||o.id;}});

Ext.data.Store=function(config){this.data=new Ext.util.MixedCollection(false);this.data.getKey=function(o){return o.id;};this.baseParams={};this.paramNames={"start":"start","limit":"limit","sort":"sort","dir":"dir"};if(config&&config.data){this.inlineData=config.data;delete config.data;}
Ext.apply(this,config);if(this.url&&!this.proxy){this.proxy=new Ext.data.HttpProxy({url:this.url});}
if(this.reader){if(!this.recordType){this.recordType=this.reader.recordType;}
if(this.reader.onMetaChange){this.reader.onMetaChange=this.onMetaChange.createDelegate(this);}}
if(this.recordType){this.fields=this.recordType.prototype.fields;}
this.modified=[];this.addEvents('datachanged','metachange','add','remove','update','clear','beforeload','load','loadexception');if(this.proxy){this.relayEvents(this.proxy,["loadexception"]);}
this.sortToggle={};if(this.sortInfo){this.setDefaultSort(this.sortInfo.field,this.sortInfo.direction);}
Ext.data.Store.superclass.constructor.call(this);if(this.storeId||this.id){Ext.StoreMgr.register(this);}
if(this.inlineData){this.loadData(this.inlineData);delete this.inlineData;}else if(this.autoLoad){this.load.defer(10,this,[typeof this.autoLoad=='object'?this.autoLoad:undefined]);}};Ext.extend(Ext.data.Store,Ext.util.Observable,{remoteSort:false,pruneModifiedRecords:false,lastOptions:null,destroy:function(){if(this.storeId||this.id){Ext.StoreMgr.unregister(this);}
this.data=null;Ext.destroy(this.proxy);this.reader=null;this.purgeListeners();},add:function(records){records=[].concat(records);if(records.length<1){return;}
for(var i=0,len=records.length;i<len;i++){records[i].join(this);}
var index=this.data.length;this.data.addAll(records);if(this.snapshot){this.snapshot.addAll(records);}
this.fireEvent("add",this,records,index);},addSorted:function(record){var index=this.findInsertIndex(record);this.insert(index,record);},remove:function(record){var index=this.data.indexOf(record);if(index>-1){this.data.removeAt(index);if(this.pruneModifiedRecords){this.modified.remove(record);}
if(this.snapshot){this.snapshot.remove(record);}
this.fireEvent("remove",this,record,index);}},removeAt:function(index){this.remove(this.getAt(index));},removeAll:function(){this.data.clear();if(this.snapshot){this.snapshot.clear();}
if(this.pruneModifiedRecords){this.modified=[];}
this.fireEvent("clear",this);},insert:function(index,records){records=[].concat(records);for(var i=0,len=records.length;i<len;i++){this.data.insert(index,records[i]);records[i].join(this);}
this.fireEvent("add",this,records,index);},indexOf:function(record){return this.data.indexOf(record);},indexOfId:function(id){return this.data.indexOfKey(id);},getById:function(id){return this.data.key(id);},getAt:function(index){return this.data.itemAt(index);},getRange:function(start,end){return this.data.getRange(start,end);},storeOptions:function(o){o=Ext.apply({},o);delete o.callback;delete o.scope;this.lastOptions=o;},load:function(options){options=options||{};if(this.fireEvent("beforeload",this,options)!==false){this.storeOptions(options);var p=Ext.apply(options.params||{},this.baseParams);if(this.sortInfo&&this.remoteSort){var pn=this.paramNames;p[pn["sort"]]=this.sortInfo.field;p[pn["dir"]]=this.sortInfo.direction;}
this.proxy.load(p,this.reader,this.loadRecords,this,options);return true;}else{return false;}},reload:function(options){this.load(Ext.applyIf(options||{},this.lastOptions));},loadRecords:function(o,options,success){if(!o||success===false){if(success!==false){this.fireEvent("load",this,[],options);}
if(options.callback){options.callback.call(options.scope||this,[],options,false);}
return;}
var r=o.records,t=o.totalRecords||r.length;if(!options||options.add!==true){if(this.pruneModifiedRecords){this.modified=[];}
for(var i=0,len=r.length;i<len;i++){r[i].join(this);}
if(this.snapshot){this.data=this.snapshot;delete this.snapshot;}
this.data.clear();this.data.addAll(r);this.totalLength=t;this.applySort();this.fireEvent("datachanged",this);}else{this.totalLength=Math.max(t,this.data.length+r.length);this.add(r);}
this.fireEvent("load",this,r,options);if(options.callback){options.callback.call(options.scope||this,r,options,true);}},loadData:function(o,append){var r=this.reader.readRecords(o);this.loadRecords(r,{add:append},true);},getCount:function(){return this.data.length||0;},getTotalCount:function(){return this.totalLength||0;},getSortState:function(){return this.sortInfo;},applySort:function(){if(this.sortInfo&&!this.remoteSort){var s=this.sortInfo,f=s.field;this.sortData(f,s.direction);}},sortData:function(f,direction){direction=direction||'ASC';var st=this.fields.get(f).sortType;var fn=function(r1,r2){var v1=st(r1.data[f]),v2=st(r2.data[f]);return v1>v2?1:(v1<v2?-1:0);};this.data.sort(direction,fn);if(this.snapshot&&this.snapshot!=this.data){this.snapshot.sort(direction,fn);}},setDefaultSort:function(field,dir){dir=dir?dir.toUpperCase():"ASC";this.sortInfo={field:field,direction:dir};this.sortToggle[field]=dir;},sort:function(fieldName,dir){var f=this.fields.get(fieldName);if(!f){return false;}
if(!dir){if(this.sortInfo&&this.sortInfo.field==f.name){dir=(this.sortToggle[f.name]||"ASC").toggle("ASC","DESC");}else{dir=f.sortDir;}}
var st=(this.sortToggle)?this.sortToggle[f.name]:null;var si=(this.sortInfo)?this.sortInfo:null;this.sortToggle[f.name]=dir;this.sortInfo={field:f.name,direction:dir};if(!this.remoteSort){this.applySort();this.fireEvent("datachanged",this);}else{if(!this.load(this.lastOptions)){if(st){this.sortToggle[f.name]=st;}
if(si){this.sortInfo=si;}}}},each:function(fn,scope){this.data.each(fn,scope);},getModifiedRecords:function(){return this.modified;},createFilterFn:function(property,value,anyMatch,caseSensitive){if(Ext.isEmpty(value,false)){return false;}
value=this.data.createValueMatcher(value,anyMatch,caseSensitive);return function(r){return value.test(r.data[property]);};},sum:function(property,start,end){var rs=this.data.items,v=0;start=start||0;end=(end||end===0)?end:rs.length-1;for(var i=start;i<=end;i++){v+=(rs[i].data[property]||0);}
return v;},filter:function(property,value,anyMatch,caseSensitive){var fn=this.createFilterFn(property,value,anyMatch,caseSensitive);return fn?this.filterBy(fn):this.clearFilter();},filterBy:function(fn,scope){this.snapshot=this.snapshot||this.data;this.data=this.queryBy(fn,scope||this);this.fireEvent("datachanged",this);},query:function(property,value,anyMatch,caseSensitive){var fn=this.createFilterFn(property,value,anyMatch,caseSensitive);return fn?this.queryBy(fn):this.data.clone();},queryBy:function(fn,scope){var data=this.snapshot||this.data;return data.filterBy(fn,scope||this);},find:function(property,value,start,anyMatch,caseSensitive){var fn=this.createFilterFn(property,value,anyMatch,caseSensitive);return fn?this.data.findIndexBy(fn,null,start):-1;},findBy:function(fn,scope,start){return this.data.findIndexBy(fn,scope,start);},collect:function(dataIndex,allowNull,bypassFilter){var d=(bypassFilter===true&&this.snapshot)?this.snapshot.items:this.data.items;var v,sv,r=[],l={};for(var i=0,len=d.length;i<len;i++){v=d[i].data[dataIndex];sv=String(v);if((allowNull||!Ext.isEmpty(v))&&!l[sv]){l[sv]=true;r[r.length]=v;}}
return r;},clearFilter:function(suppressEvent){if(this.isFiltered()){this.data=this.snapshot;delete this.snapshot;if(suppressEvent!==true){this.fireEvent("datachanged",this);}}},isFiltered:function(){return this.snapshot&&this.snapshot!=this.data;},afterEdit:function(record){if(this.modified.indexOf(record)==-1){this.modified.push(record);}
this.fireEvent("update",this,record,Ext.data.Record.EDIT);},afterReject:function(record){this.modified.remove(record);this.fireEvent("update",this,record,Ext.data.Record.REJECT);},afterCommit:function(record){this.modified.remove(record);this.fireEvent("update",this,record,Ext.data.Record.COMMIT);},commitChanges:function(){var m=this.modified.slice(0);this.modified=[];for(var i=0,len=m.length;i<len;i++){m[i].commit();}},rejectChanges:function(){var m=this.modified.slice(0);this.modified=[];for(var i=0,len=m.length;i<len;i++){m[i].reject();}},onMetaChange:function(meta,rtype,o){this.recordType=rtype;this.fields=rtype.prototype.fields;delete this.snapshot;if(meta.sortInfo){this.sortInfo=meta.sortInfo;}else if(this.sortInfo&&!this.fields.get(this.sortInfo.field)){delete this.sortInfo;}
this.modified=[];this.fireEvent('metachange',this,this.reader.meta);},findInsertIndex:function(record){this.suspendEvents();var data=this.data.clone();this.data.add(record);this.applySort();var index=this.data.indexOf(record);this.data=data;this.resumeEvents();return index;}});

Ext.data.SimpleStore=function(config){Ext.data.SimpleStore.superclass.constructor.call(this,Ext.apply(config,{reader:new Ext.data.ArrayReader({id:config.id},Ext.data.Record.create(config.fields))}));};Ext.extend(Ext.data.SimpleStore,Ext.data.Store,{loadData:function(data,append){if(this.expandData===true){var r=[];for(var i=0,len=data.length;i<len;i++){r[r.length]=[data[i]];}
data=r;}
Ext.data.SimpleStore.superclass.loadData.call(this,data,append);}});

Ext.data.Connection=function(config){Ext.apply(this,config);this.addEvents("beforerequest","requestcomplete","requestexception");Ext.data.Connection.superclass.constructor.call(this);};Ext.extend(Ext.data.Connection,Ext.util.Observable,{timeout:30000,autoAbort:false,disableCaching:true,disableCachingParam:'_dc',request:function(o){if(this.fireEvent("beforerequest",this,o)!==false){var p=o.params;if(typeof p=="function"){p=p.call(o.scope||window,o);}
if(typeof p=="object"){p=Ext.urlEncode(p);}
if(this.extraParams){var extras=Ext.urlEncode(this.extraParams);p=p?(p+'&'+extras):extras;}
var url=o.url||this.url;if(typeof url=='function'){url=url.call(o.scope||window,o);}
if(o.form){var form=Ext.getDom(o.form);url=url||form.action;var enctype=form.getAttribute("enctype");if(o.isUpload||(enctype&&enctype.toLowerCase()=='multipart/form-data')){return this.doFormUpload(o,p,url);}
var f=Ext.lib.Ajax.serializeForm(form);p=p?(p+'&'+f):f;}
var hs=o.headers;if(this.defaultHeaders){hs=Ext.apply(hs||{},this.defaultHeaders);if(!o.headers){o.headers=hs;}}
var cb={success:this.handleResponse,failure:this.handleFailure,scope:this,argument:{options:o},timeout:o.timeout||this.timeout};var method=o.method||this.method||((p||o.xmlData||o.jsonData)?"POST":"GET");if(method=='GET'&&(this.disableCaching&&o.disableCaching!==false)||o.disableCaching===true){var dcp=o.disableCachingParam||this.disableCachingParam;url+=(url.indexOf('?')!=-1?'&':'?')+dcp+'='+(new Date().getTime());}
if(typeof o.autoAbort=='boolean'){if(o.autoAbort){this.abort();}}else if(this.autoAbort!==false){this.abort();}
if((method=='GET'||o.xmlData||o.jsonData)&&p){url+=(url.indexOf('?')!=-1?'&':'?')+p;p='';}
this.transId=Ext.lib.Ajax.request(method,url,cb,p,o);return this.transId;}else{Ext.callback(o.callback,o.scope,[o,null,null]);return null;}},isLoading:function(transId){if(transId){return Ext.lib.Ajax.isCallInProgress(transId);}else{return this.transId?true:false;}},abort:function(transId){if(transId||this.isLoading()){Ext.lib.Ajax.abort(transId||this.transId);}},handleResponse:function(response){this.transId=false;var options=response.argument.options;response.argument=options?options.argument:null;this.fireEvent("requestcomplete",this,response,options);Ext.callback(options.success,options.scope,[response,options]);Ext.callback(options.callback,options.scope,[options,true,response]);},handleFailure:function(response,e){this.transId=false;var options=response.argument.options;response.argument=options?options.argument:null;this.fireEvent("requestexception",this,response,options,e);Ext.callback(options.failure,options.scope,[response,options]);Ext.callback(options.callback,options.scope,[options,false,response]);},doFormUpload:function(o,ps,url){var id=Ext.id();var frame=document.createElement('iframe');frame.id=id;frame.name=id;frame.className='x-hidden';if(Ext.isIE){frame.src=Ext.SSL_SECURE_URL;}
document.body.appendChild(frame);if(Ext.isIE){document.frames[id].name=id;}
var form=Ext.getDom(o.form),buf={target:form.target,method:form.method,encoding:form.encoding,enctype:form.enctype,action:form.action};form.target=id;form.method='POST';form.enctype=form.encoding='multipart/form-data';if(url){form.action=url;}
var hiddens,hd;if(ps){hiddens=[];ps=Ext.urlDecode(ps,false);for(var k in ps){if(ps.hasOwnProperty(k)){hd=document.createElement('input');hd.type='hidden';hd.name=k;hd.value=ps[k];form.appendChild(hd);hiddens.push(hd);}}}
function cb(){var r={responseText:'',responseXML:null};r.argument=o?o.argument:null;try{var doc;if(Ext.isIE){doc=frame.contentWindow.document;}else{doc=(frame.contentDocument||window.frames[id].document);}
if(doc&&doc.body){r.responseText=doc.body.innerHTML;}
if(doc&&doc.XMLDocument){r.responseXML=doc.XMLDocument;}else{r.responseXML=doc;}}
catch(e){}
Ext.EventManager.removeListener(frame,'load',cb,this);this.fireEvent("requestcomplete",this,r,o);Ext.callback(o.success,o.scope,[r,o]);Ext.callback(o.callback,o.scope,[o,true,r]);setTimeout(function(){Ext.removeNode(frame);},100);}
Ext.EventManager.on(frame,'load',cb,this);form.submit();form.target=buf.target;form.method=buf.method;form.enctype=buf.enctype;form.encoding=buf.encoding;form.action=buf.action;if(hiddens){for(var i=0,len=hiddens.length;i<len;i++){Ext.removeNode(hiddens[i]);}}}});Ext.Ajax=new Ext.data.Connection({autoAbort:false,serializeForm:function(form){return Ext.lib.Ajax.serializeForm(form);}});

Ext.data.Field=function(config){if(typeof config=="string"){config={name:config};}
Ext.apply(this,config);if(!this.type){this.type="auto";}
var st=Ext.data.SortTypes;if(typeof this.sortType=="string"){this.sortType=st[this.sortType];}
if(!this.sortType){switch(this.type){case"string":this.sortType=st.asUCString;break;case"date":this.sortType=st.asDate;break;default:this.sortType=st.none;}}
var stripRe=/[\$,%]/g;if(!this.convert){var cv,dateFormat=this.dateFormat;switch(this.type){case"":case"auto":case undefined:cv=function(v){return v;};break;case"string":cv=function(v){return(v===undefined||v===null)?'':String(v);};break;case"int":cv=function(v){return v!==undefined&&v!==null&&v!==''?parseInt(String(v).replace(stripRe,""),10):'';};break;case"float":cv=function(v){return v!==undefined&&v!==null&&v!==''?parseFloat(String(v).replace(stripRe,""),10):'';};break;case"bool":case"boolean":cv=function(v){return v===true||v==="true"||v==1;};break;case"date":cv=function(v){if(!v){return'';}
if(Ext.isDate(v)){return v;}
if(dateFormat){if(dateFormat=="timestamp"){return new Date(v*1000);}
if(dateFormat=="time"){return new Date(parseInt(v,10));}
return Date.parseDate(v,dateFormat);}
var parsed=Date.parse(v);return parsed?new Date(parsed):null;};break;}
this.convert=cv;}};Ext.data.Field.prototype={dateFormat:null,defaultValue:"",mapping:null,sortType:null,sortDir:"ASC"};

Ext.data.DataReader=function(meta,recordType){this.meta=meta;this.recordType=Ext.isArray(recordType)?Ext.data.Record.create(recordType):recordType;};Ext.data.DataReader.prototype={};

Ext.data.DataProxy=function(){this.addEvents('beforeload','load');Ext.data.DataProxy.superclass.constructor.call(this);};Ext.extend(Ext.data.DataProxy,Ext.util.Observable,{destroy:function(){this.purgeListeners();}});

Ext.data.MemoryProxy=function(data){Ext.data.MemoryProxy.superclass.constructor.call(this);this.data=data;};Ext.extend(Ext.data.MemoryProxy,Ext.data.DataProxy,{load:function(params,reader,callback,scope,arg){params=params||{};var result;try{result=reader.readRecords(this.data);}catch(e){this.fireEvent("loadexception",this,arg,null,e);callback.call(scope,null,arg,false);return;}
callback.call(scope,result,arg,true);},update:function(params,records){}});

Ext.data.HttpProxy=function(conn){Ext.data.HttpProxy.superclass.constructor.call(this);this.conn=conn;this.useAjax=!conn||!conn.events;};Ext.extend(Ext.data.HttpProxy,Ext.data.DataProxy,{getConnection:function(){return this.useAjax?Ext.Ajax:this.conn;},load:function(params,reader,callback,scope,arg){if(this.fireEvent("beforeload",this,params)!==false){var o={params:params||{},request:{callback:callback,scope:scope,arg:arg},reader:reader,callback:this.loadResponse,scope:this};if(this.useAjax){Ext.applyIf(o,this.conn);if(this.activeRequest){Ext.Ajax.abort(this.activeRequest);}
this.activeRequest=Ext.Ajax.request(o);}else{this.conn.request(o);}}else{callback.call(scope||this,null,arg,false);}},loadResponse:function(o,success,response){delete this.activeRequest;if(!success){this.fireEvent("loadexception",this,o,response);o.request.callback.call(o.request.scope,null,o.request.arg,false);return;}
var result;try{result=o.reader.read(response);}catch(e){this.fireEvent("loadexception",this,o,response,e);o.request.callback.call(o.request.scope,null,o.request.arg,false);return;}
this.fireEvent("load",this,o,o.request.arg);o.request.callback.call(o.request.scope,result,o.request.arg,true);},update:function(dataSet){},updateResponse:function(dataSet){},destroy:function(){if(!this.useAjax){this.conn.abort();}else if(this.activeRequest){Ext.Ajax.abort(this.activeRequest);}
Ext.data.HttpProxy.superclass.destroy.call(this);}});

Ext.data.ScriptTagProxy=function(config){Ext.data.ScriptTagProxy.superclass.constructor.call(this);Ext.apply(this,config);this.head=document.getElementsByTagName("head")[0];};Ext.data.ScriptTagProxy.TRANS_ID=1000;Ext.extend(Ext.data.ScriptTagProxy,Ext.data.DataProxy,{timeout:30000,callbackParam:"callback",nocache:true,load:function(params,reader,callback,scope,arg){if(this.fireEvent("beforeload",this,params)!==false){var p=Ext.urlEncode(Ext.apply(params,this.extraParams));var url=this.url;url+=(url.indexOf("?")!=-1?"&":"?")+p;if(this.nocache){url+="&_dc="+(new Date().getTime());}
var transId=++Ext.data.ScriptTagProxy.TRANS_ID;var trans={id:transId,cb:"stcCallback"+transId,scriptId:"stcScript"+transId,params:params,arg:arg,url:url,callback:callback,scope:scope,reader:reader};var conn=this;window[trans.cb]=function(o){conn.handleResponse(o,trans);};url+=String.format("&{0}={1}",this.callbackParam,trans.cb);if(this.autoAbort!==false){this.abort();}
trans.timeoutId=this.handleFailure.defer(this.timeout,this,[trans]);var script=document.createElement("script");script.setAttribute("src",url);script.setAttribute("type","text/javascript");script.setAttribute("id",trans.scriptId);this.head.appendChild(script);this.trans=trans;}else{callback.call(scope||this,null,arg,false);}},isLoading:function(){return this.trans?true:false;},abort:function(){if(this.isLoading()){this.destroyTrans(this.trans);}},destroyTrans:function(trans,isLoaded){this.head.removeChild(document.getElementById(trans.scriptId));clearTimeout(trans.timeoutId);if(isLoaded){window[trans.cb]=undefined;try{delete window[trans.cb];}catch(e){}}else{window[trans.cb]=function(){window[trans.cb]=undefined;try{delete window[trans.cb];}catch(e){}};}},handleResponse:function(o,trans){this.trans=false;this.destroyTrans(trans,true);var result;try{result=trans.reader.readRecords(o);}catch(e){this.fireEvent("loadexception",this,o,trans.arg,e);trans.callback.call(trans.scope||window,null,trans.arg,false);return;}
this.fireEvent("load",this,o,trans.arg);trans.callback.call(trans.scope||window,result,trans.arg,true);},handleFailure:function(trans){this.trans=false;this.destroyTrans(trans,false);this.fireEvent("loadexception",this,null,trans.arg);trans.callback.call(trans.scope||window,null,trans.arg,false);},destroy:function(){this.abort();Ext.data.ScriptTagProxy.superclass.destroy.call(this);}});

Ext.data.JsonReader=function(meta,recordType){meta=meta||{};Ext.data.JsonReader.superclass.constructor.call(this,meta,recordType||meta.fields);};Ext.extend(Ext.data.JsonReader,Ext.data.DataReader,{read:function(response){var json=response.responseText;var o=eval("("+json+")");if(!o){throw{message:"JsonReader.read: Json object not found"};}
return this.readRecords(o);},onMetaChange:function(meta,recordType,o){},simpleAccess:function(obj,subsc){return obj[subsc];},getJsonAccessor:function(){var re=/[\[\.]/;return function(expr){try{return(re.test(expr))?new Function("obj","return obj."+expr):function(obj){return obj[expr];};}catch(e){}
return Ext.emptyFn;};}(),readRecords:function(o){this.jsonData=o;if(o.metaData){delete this.ef;this.meta=o.metaData;this.recordType=Ext.data.Record.create(o.metaData.fields);this.onMetaChange(this.meta,this.recordType,o);}
var s=this.meta,Record=this.recordType,f=Record.prototype.fields,fi=f.items,fl=f.length;if(!this.ef){if(s.totalProperty){this.getTotal=this.getJsonAccessor(s.totalProperty);}
if(s.successProperty){this.getSuccess=this.getJsonAccessor(s.successProperty);}
this.getRoot=s.root?this.getJsonAccessor(s.root):function(p){return p;};if(s.id){var g=this.getJsonAccessor(s.id);this.getId=function(rec){var r=g(rec);return(r===undefined||r==="")?null:r;};}else{this.getId=function(){return null;};}
this.ef=[];for(var i=0;i<fl;i++){f=fi[i];var map=(f.mapping!==undefined&&f.mapping!==null)?f.mapping:f.name;this.ef[i]=this.getJsonAccessor(map);}}
var root=this.getRoot(o),c=root.length,totalRecords=c,success=true;if(s.totalProperty){var v=parseInt(this.getTotal(o),10);if(!isNaN(v)){totalRecords=v;}}
if(s.successProperty){var v=this.getSuccess(o);if(v===false||v==='false'){success=false;}}
var records=[];for(var i=0;i<c;i++){var n=root[i];var values={};var id=this.getId(n);for(var j=0;j<fl;j++){f=fi[j];var v=this.ef[j](n);values[f.name]=f.convert((v!==undefined)?v:f.defaultValue,n);}
var record=new Record(values,id);record.json=n;records[i]=record;}
return{success:success,records:records,totalRecords:totalRecords};}});

Ext.data.XmlReader=function(meta,recordType){meta=meta||{};Ext.data.XmlReader.superclass.constructor.call(this,meta,recordType||meta.fields);};Ext.extend(Ext.data.XmlReader,Ext.data.DataReader,{read:function(response){var doc=response.responseXML;if(!doc){throw{message:"XmlReader.read: XML Document not available"};}
return this.readRecords(doc);},readRecords:function(doc){this.xmlData=doc;var root=doc.documentElement||doc;var q=Ext.DomQuery;var recordType=this.recordType,fields=recordType.prototype.fields;var sid=this.meta.id;var totalRecords=0,success=true;if(this.meta.totalRecords){totalRecords=q.selectNumber(this.meta.totalRecords,root,0);}
if(this.meta.success){var sv=q.selectValue(this.meta.success,root,true);success=sv!==false&&sv!=='false';}
var records=[];var ns=q.select(this.meta.record,root);for(var i=0,len=ns.length;i<len;i++){var n=ns[i];var values={};var id=sid?q.selectValue(sid,n):undefined;for(var j=0,jlen=fields.length;j<jlen;j++){var f=fields.items[j];var v=q.selectValue(Ext.isEmpty(f.mapping,true)?f.name:f.mapping,n,f.defaultValue);v=f.convert(v,n);values[f.name]=v;}
var record=new recordType(values,id);record.node=n;records[records.length]=record;}
return{success:success,records:records,totalRecords:totalRecords||records.length};}});

Ext.data.ArrayReader=Ext.extend(Ext.data.JsonReader,{readRecords:function(o){var sid=this.meta?this.meta.id:null;var recordType=this.recordType,fields=recordType.prototype.fields;var records=[];var root=o;for(var i=0;i<root.length;i++){var n=root[i];var values={};var id=((sid||sid===0)&&n[sid]!==undefined&&n[sid]!==""?n[sid]:null);for(var j=0,jlen=fields.length;j<jlen;j++){var f=fields.items[j];var k=f.mapping!==undefined&&f.mapping!==null?f.mapping:j;var v=n[k]!==undefined?n[k]:f.defaultValue;v=f.convert(v,n);values[f.name]=v;}
var record=new recordType(values,id);record.json=n;records[records.length]=record;}
return{records:records,totalRecords:records.length};}});
