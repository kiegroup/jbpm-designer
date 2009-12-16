
if(!Repository) var Repository = {};
 
Repository.Templates = {
		login :  new Ext.XTemplate(		'<div id="oryx_repository_header" onmouseover="this.className = \'mouseover\'" onmouseout="this.className = \'\'">'+
					        '<a href="http://oryx-project.org" target="_blank">'+
						    '<img src="/backend/images/style/oryx.small.gif" id="oryx_repository_logo" alt="ORYX Logo" title="ORYX"/>'+
					    '</a>'+
					
						'<tpl if="isPublicUser">'+
							'<form action="login" method="post" id="openid_login">'+
								'<div>'+
									'<span>'+
										'<img src="/backend/images/repository/hpi.png" onclick="Repository.Templates.login.changeOpenId(\'https://openid.hpi.uni-potsdam.de/user/username\' , 39, 8)"/>'+
										'<img src="/backend/images/repository/blogger.png" onclick="Repository.Templates.login.changeOpenId(\'http://username.blogspot.com/\' , 7, 8)"/>'+
										'<img src="/backend/images/repository/getopenid.png" onclick="Repository.Templates.login.changeOpenId(\'http://getopenid.com/username\', 21, 8)"/>'+
									'</span>'+
									'<input type="text" name="openid_identifier" id="openid_login_openid" class="text gray" value={Repository.I18N.Repository.openIdSample} onblur="if(this.value.replace(/^\s+/, \'\').replace(/\s+$/, \'\').length==0) {this.value=Repository.I18N.Repository.openIdSample; this.className+=\' gray\';}" onfocus="this.className = this.className.replace(/ gray/ig, \'\'); if(this.value==Repository.I18N.Repository.openIdSample) this.value=\'\';" />'+
									'<input type="submit" class="button" value={Repository.I18N.Repository.login} />'+
								'</div>'+
							'</form>'+
						'</tpl>'+
						
						'<tpl if="!isPublicUser">'+
							'<form action="login?logout=true" method="post" id="openid_login">'+
								'<div>'+
									'<div style="display:inline;" class="login_name">{Repository.I18N.Repository.sayHello}, {currentUser}</div>'+
									'<input type="submit" class="button" value={Repository.I18N.Repository.logout} />'+
								'</div>'+
							'</form>'+
					
						'</tpl>'+
					
						'<div style="clear: both;"></div>'+
					'</div>', 
					{						
						changeOpenId: function(url, start, size){
							var o = document.getElementById('openid_login_openid');
							o.value = url;
							o.focus();
							
							if (window.ActiveXObject) {
								try {
									var tr = o.createTextRange();
									tr.collapse(true);
									tr.moveStart('character', start);
									tr.moveEnd('character', size);
									tr.select();
								} 
								catch (e) {
								}
							}
							else {
								o.setSelectionRange(start, start + size);
							}
						}
					}), // end of login
		login2 : function () {
			tpl = new Ext.XTemplate('<div id="oryx_repository_header"> <tpl if="isPublicUser"> PUBLIC </tpl><tpl if="!isPublicUser">  NOT PUBLIC </tpl></div>');
			//tpl.compile;
			return tpl;
		}
}