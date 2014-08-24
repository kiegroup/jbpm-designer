/*
 * Ext JS Library 2.3.0
 * Copyright(c) 2006-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */


Ext.DomQuery=function(){var cache={},simpleCache={},valueCache={};var nonSpace=/\S/;var trimRe=/^\s+|\s+$/g;var tplRe=/\{(\d+)\}/g;var modeRe=/^(\s?[\/>+~]\s?|\s|$)/;var tagTokenRe=/^(#)?([\w-\*]+)/;var nthRe=/(\d*)n\+?(\d*)/,nthRe2=/\D/;var opera=Ext.isOpera;function child(p,index){var i=0;var n=p.firstChild;while(n){if(n.nodeType==1){if(++i==index){return n;}}
n=n.nextSibling;}
return null;};function next(n){while((n=n.nextSibling)&&n.nodeType!=1);return n;};function prev(n){while((n=n.previousSibling)&&n.nodeType!=1);return n;};function children(d){var n=d.firstChild,ni=-1;while(n){var nx=n.nextSibling;if(n.nodeType==3&&!nonSpace.test(n.nodeValue)){d.removeChild(n);}else{n.nodeIndex=++ni;}
n=nx;}
return this;};function byClassName(c,a,v){if(!v){return c;}
var r=[],ri=-1,cn;for(var i=0,ci;ci=c[i];i++){if((' '+ci.className+' ').indexOf(v)!=-1){r[++ri]=ci;}}
return r;};function attrValue(n,attr){if(!n.tagName&&typeof n.length!="undefined"){n=n[0];}
if(!n){return null;}
if(attr=="for"){return n.htmlFor;}
if(attr=="class"||attr=="className"){return n.className;}
return n.getAttribute(attr)||n[attr];};function getNodes(ns,mode,tagName){var result=[],ri=-1,cs;if(!ns){return result;}
tagName=tagName||"*";if(typeof ns.getElementsByTagName!="undefined"){ns=[ns];}
if(!mode){for(var i=0,ni;ni=ns[i];i++){cs=ni.getElementsByTagName(tagName);for(var j=0,ci;ci=cs[j];j++){result[++ri]=ci;}}}else if(mode=="/"||mode==">"){var utag=tagName.toUpperCase();for(var i=0,ni,cn;ni=ns[i];i++){cn=opera?ni.childNodes:(ni.children||ni.childNodes);for(var j=0,cj;cj=cn[j];j++){if(cj.nodeName==utag||cj.nodeName==tagName||tagName=='*'){result[++ri]=cj;}}}}else if(mode=="+"){var utag=tagName.toUpperCase();for(var i=0,n;n=ns[i];i++){while((n=n.nextSibling)&&n.nodeType!=1);if(n&&(n.nodeName==utag||n.nodeName==tagName||tagName=='*')){result[++ri]=n;}}}else if(mode=="~"){var utag=tagName.toUpperCase();for(var i=0,n;n=ns[i];i++){while((n=n.nextSibling)){if(n.nodeName==utag||n.nodeName==tagName||tagName=='*'){result[++ri]=n;}}}}
return result;};function concat(a,b){if(b.slice){return a.concat(b);}
for(var i=0,l=b.length;i<l;i++){a[a.length]=b[i];}
return a;}
function byTag(cs,tagName){if(cs.tagName||cs==document){cs=[cs];}
if(!tagName){return cs;}
var r=[],ri=-1;tagName=tagName.toLowerCase();for(var i=0,ci;ci=cs[i];i++){if(ci.nodeType==1&&ci.tagName.toLowerCase()==tagName){r[++ri]=ci;}}
return r;};function byId(cs,attr,id){if(cs.tagName||cs==document){cs=[cs];}
if(!id){return cs;}
var r=[],ri=-1;for(var i=0,ci;ci=cs[i];i++){if(ci&&ci.id==id){r[++ri]=ci;return r;}}
return r;};function byAttribute(cs,attr,value,op,custom){var r=[],ri=-1,st=custom=="{";var f=Ext.DomQuery.operators[op];for(var i=0,ci;ci=cs[i];i++){if(ci.nodeType!=1){continue;}
var a;if(st){a=Ext.DomQuery.getStyle(ci,attr);}
else if(attr=="class"||attr=="className"){a=ci.className;}else if(attr=="for"){a=ci.htmlFor;}else if(attr=="href"){a=ci.getAttribute("href",2);}else{a=ci.getAttribute(attr);}
if((f&&f(a,value))||(!f&&a)){r[++ri]=ci;}}
return r;};function byPseudo(cs,name,value){return Ext.DomQuery.pseudos[name](cs,value);};var isIE=window.ActiveXObject?true:false;eval("var batch = 30803;");var key=30803;function nodupIEXml(cs){var d=++key;cs[0].setAttribute("_nodup",d);var r=[cs[0]];for(var i=1,len=cs.length;i<len;i++){var c=cs[i];if(!c.getAttribute("_nodup")!=d){c.setAttribute("_nodup",d);r[r.length]=c;}}
for(var i=0,len=cs.length;i<len;i++){cs[i].removeAttribute("_nodup");}
return r;}
function nodup(cs){if(!cs){return[];}
var len=cs.length,c,i,r=cs,cj,ri=-1;if(!len||typeof cs.nodeType!="undefined"||len==1){return cs;}
if(isIE&&typeof cs[0].selectSingleNode!="undefined"){return nodupIEXml(cs);}
var d=++key;cs[0]._nodup=d;for(i=1;c=cs[i];i++){if(c._nodup!=d){c._nodup=d;}else{r=[];for(var j=0;j<i;j++){r[++ri]=cs[j];}
for(j=i+1;cj=cs[j];j++){if(cj._nodup!=d){cj._nodup=d;r[++ri]=cj;}}
return r;}}
return r;}
function quickDiffIEXml(c1,c2){var d=++key;for(var i=0,len=c1.length;i<len;i++){c1[i].setAttribute("_qdiff",d);}
var r=[];for(var i=0,len=c2.length;i<len;i++){if(c2[i].getAttribute("_qdiff")!=d){r[r.length]=c2[i];}}
for(var i=0,len=c1.length;i<len;i++){c1[i].removeAttribute("_qdiff");}
return r;}
function quickDiff(c1,c2){var len1=c1.length;if(!len1){return c2;}
if(isIE&&c1[0].selectSingleNode){return quickDiffIEXml(c1,c2);}
var d=++key;for(var i=0;i<len1;i++){c1[i]._qdiff=d;}
var r=[];for(var i=0,len=c2.length;i<len;i++){if(c2[i]._qdiff!=d){r[r.length]=c2[i];}}
return r;}
function quickId(ns,mode,root,id){if(ns==root){var d=root.ownerDocument||root;return d.getElementById(id);}
ns=getNodes(ns,mode,"*");return byId(ns,null,id);}
return{getStyle:function(el,name){return Ext.fly(el).getStyle(name);},compile:function(path,type){type=type||"select";var fn=["var f = function(root){\n var mode; ++batch; var n = root || document;\n"];var q=path,mode,lq;var tk=Ext.DomQuery.matchers;var tklen=tk.length;var mm;var lmode=q.match(modeRe);if(lmode&&lmode[1]){fn[fn.length]='mode="'+lmode[1].replace(trimRe,"")+'";';q=q.replace(lmode[1],"");}
while(path.substr(0,1)=="/"){path=path.substr(1);}
while(q&&lq!=q){lq=q;var tm=q.match(tagTokenRe);if(type=="select"){if(tm){if(tm[1]=="#"){fn[fn.length]='n = quickId(n, mode, root, "'+tm[2]+'");';}else{fn[fn.length]='n = getNodes(n, mode, "'+tm[2]+'");';}
q=q.replace(tm[0],"");}else if(q.substr(0,1)!='@'){fn[fn.length]='n = getNodes(n, mode, "*");';}}else{if(tm){if(tm[1]=="#"){fn[fn.length]='n = byId(n, null, "'+tm[2]+'");';}else{fn[fn.length]='n = byTag(n, "'+tm[2]+'");';}
q=q.replace(tm[0],"");}}
while(!(mm=q.match(modeRe))){var matched=false;for(var j=0;j<tklen;j++){var t=tk[j];var m=q.match(t.re);if(m){fn[fn.length]=t.select.replace(tplRe,function(x,i){return m[i];});q=q.replace(m[0],"");matched=true;break;}}
if(!matched){throw'Error parsing selector, parsing failed at "'+q+'"';}}
if(mm[1]){fn[fn.length]='mode="'+mm[1].replace(trimRe,"")+'";';q=q.replace(mm[1],"");}}
fn[fn.length]="return nodup(n);\n}";eval(fn.join(""));return f;},select:function(path,root,type){if(!root||root==document){root=document;}
if(typeof root=="string"){root=document.getElementById(root);}
var paths=path.split(",");var results=[];for(var i=0,len=paths.length;i<len;i++){var p=paths[i].replace(trimRe,"");if(!cache[p]){cache[p]=Ext.DomQuery.compile(p);if(!cache[p]){throw p+" is not a valid selector";}}
var result=cache[p](root);if(result&&result!=document){results=results.concat(result);}}
if(paths.length>1){return nodup(results);}
return results;},selectNode:function(path,root){return Ext.DomQuery.select(path,root)[0];},selectValue:function(path,root,defaultValue){path=path.replace(trimRe,"");if(!valueCache[path]){valueCache[path]=Ext.DomQuery.compile(path,"select");}
var n=valueCache[path](root);n=n[0]?n[0]:n;var v=(n&&n.firstChild?n.firstChild.nodeValue:null);return((v===null||v===undefined||v==='')?defaultValue:v);},selectNumber:function(path,root,defaultValue){var v=Ext.DomQuery.selectValue(path,root,defaultValue||0);return parseFloat(v);},is:function(el,ss){if(typeof el=="string"){el=document.getElementById(el);}
var isArray=Ext.isArray(el);var result=Ext.DomQuery.filter(isArray?el:[el],ss);return isArray?(result.length==el.length):(result.length>0);},filter:function(els,ss,nonMatches){ss=ss.replace(trimRe,"");if(!simpleCache[ss]){simpleCache[ss]=Ext.DomQuery.compile(ss,"simple");}
var result=simpleCache[ss](els);return nonMatches?quickDiff(result,els):result;},matchers:[{re:/^\.([\w-]+)/,select:'n = byClassName(n, null, " {1} ");'},{re:/^\:([\w-]+)(?:\(((?:[^\s>\/]*|.*?))\))?/,select:'n = byPseudo(n, "{1}", "{2}");'},{re:/^(?:([\[\{])(?:@)?([\w-]+)\s?(?:(=|.=)\s?['"]?(.*?)["']?)?[\]\}])/,select:'n = byAttribute(n, "{2}", "{4}", "{3}", "{1}");'},{re:/^#([\w-]+)/,select:'n = byId(n, null, "{1}");'},{re:/^@([\w-]+)/,select:'return {firstChild:{nodeValue:attrValue(n, "{1}")}};'}],operators:{"=":function(a,v){return a==v;},"!=":function(a,v){return a!=v;},"^=":function(a,v){return a&&a.substr(0,v.length)==v;},"$=":function(a,v){return a&&a.substr(a.length-v.length)==v;},"*=":function(a,v){return a&&a.indexOf(v)!==-1;},"%=":function(a,v){return(a%v)==0;},"|=":function(a,v){return a&&(a==v||a.substr(0,v.length+1)==v+'-');},"~=":function(a,v){return a&&(' '+a+' ').indexOf(' '+v+' ')!=-1;}},pseudos:{"first-child":function(c){var r=[],ri=-1,n;for(var i=0,ci;ci=n=c[i];i++){while((n=n.previousSibling)&&n.nodeType!=1);if(!n){r[++ri]=ci;}}
return r;},"last-child":function(c){var r=[],ri=-1,n;for(var i=0,ci;ci=n=c[i];i++){while((n=n.nextSibling)&&n.nodeType!=1);if(!n){r[++ri]=ci;}}
return r;},"nth-child":function(c,a){var r=[],ri=-1;var m=nthRe.exec(a=="even"&&"2n"||a=="odd"&&"2n+1"||!nthRe2.test(a)&&"n+"+a||a);var f=(m[1]||1)-0,l=m[2]-0;for(var i=0,n;n=c[i];i++){var pn=n.parentNode;if(batch!=pn._batch){var j=0;for(var cn=pn.firstChild;cn;cn=cn.nextSibling){if(cn.nodeType==1){cn.nodeIndex=++j;}}
pn._batch=batch;}
if(f==1){if(l==0||n.nodeIndex==l){r[++ri]=n;}}else if((n.nodeIndex+l)%f==0){r[++ri]=n;}}
return r;},"only-child":function(c){var r=[],ri=-1;;for(var i=0,ci;ci=c[i];i++){if(!prev(ci)&&!next(ci)){r[++ri]=ci;}}
return r;},"empty":function(c){var r=[],ri=-1;for(var i=0,ci;ci=c[i];i++){var cns=ci.childNodes,j=0,cn,empty=true;while(cn=cns[j]){++j;if(cn.nodeType==1||cn.nodeType==3){empty=false;break;}}
if(empty){r[++ri]=ci;}}
return r;},"contains":function(c,v){var r=[],ri=-1;for(var i=0,ci;ci=c[i];i++){if((ci.textContent||ci.innerText||'').indexOf(v)!=-1){r[++ri]=ci;}}
return r;},"nodeValue":function(c,v){var r=[],ri=-1;for(var i=0,ci;ci=c[i];i++){if(ci.firstChild&&ci.firstChild.nodeValue==v){r[++ri]=ci;}}
return r;},"checked":function(c){var r=[],ri=-1;for(var i=0,ci;ci=c[i];i++){if(ci.checked==true){r[++ri]=ci;}}
return r;},"not":function(c,ss){return Ext.DomQuery.filter(c,ss,true);},"any":function(c,selectors){var ss=selectors.split('|');var r=[],ri=-1,s;for(var i=0,ci;ci=c[i];i++){for(var j=0;s=ss[j];j++){if(Ext.DomQuery.is(ci,s)){r[++ri]=ci;break;}}}
return r;},"odd":function(c){return this["nth-child"](c,"odd");},"even":function(c){return this["nth-child"](c,"even");},"nth":function(c,a){return c[a-1]||[];},"first":function(c){return c[0]||[];},"last":function(c){return c[c.length-1]||[];},"has":function(c,ss){var s=Ext.DomQuery.select;var r=[],ri=-1;for(var i=0,ci;ci=c[i];i++){if(s(ss,ci).length>0){r[++ri]=ci;}}
return r;},"next":function(c,ss){var is=Ext.DomQuery.is;var r=[],ri=-1;for(var i=0,ci;ci=c[i];i++){var n=next(ci);if(n&&is(n,ss)){r[++ri]=ci;}}
return r;},"prev":function(c,ss){var is=Ext.DomQuery.is;var r=[],ri=-1;for(var i=0,ci;ci=c[i];i++){var n=prev(ci);if(n&&is(n,ss)){r[++ri]=ci;}}
return r;}}};}();Ext.query=Ext.DomQuery.select;