/*
 * Ext JS Library 2.3.0
 * Copyright(c) 2006-2009, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */


Ext.util.JSON=new(function(){var useHasOwn=!!{}.hasOwnProperty;var pad=function(n){return n<10?"0"+n:n;};var m={"\b":'\\b',"\t":'\\t',"\n":'\\n',"\f":'\\f',"\r":'\\r','"':'\\"',"\\":'\\\\'};var encodeString=function(s){if(/["\\\x00-\x1f]/.test(s)){return'"'+s.replace(/([\x00-\x1f\\"])/g,function(a,b){var c=m[b];if(c){return c;}
c=b.charCodeAt();return"\\u00"+
Math.floor(c/16).toString(16)+
(c%16).toString(16);})+'"';}
return'"'+s+'"';};var encodeArray=function(o){var a=["["],b,i,l=o.length,v;for(i=0;i<l;i+=1){v=o[i];switch(typeof v){case"undefined":case"function":case"unknown":break;default:if(b){a.push(',');}
a.push(v===null?"null":Ext.util.JSON.encode(v));b=true;}}
a.push("]");return a.join("");};this.encodeDate=function(o){return'"'+o.getFullYear()+"-"+
pad(o.getMonth()+1)+"-"+
pad(o.getDate())+"T"+
pad(o.getHours())+":"+
pad(o.getMinutes())+":"+
pad(o.getSeconds())+'"';};this.encode=function(o){if(typeof o=="undefined"||o===null){return"null";}else if(Ext.isArray(o)){return encodeArray(o);}else if(Ext.isDate(o)){return Ext.util.JSON.encodeDate(o);}else if(typeof o=="string"){return encodeString(o);}else if(typeof o=="number"){return isFinite(o)?String(o):"null";}else if(typeof o=="boolean"){return String(o);}else{var a=["{"],b,i,v;for(i in o){if(!useHasOwn||o.hasOwnProperty(i)){v=o[i];switch(typeof v){case"undefined":case"function":case"unknown":break;default:if(b){a.push(',');}
a.push(this.encode(i),":",v===null?"null":this.encode(v));b=true;}}}
a.push("}");return a.join("");}};this.decode=function(json){return eval("("+json+')');};})();Ext.encode=Ext.util.JSON.encode;Ext.decode=Ext.util.JSON.decode;