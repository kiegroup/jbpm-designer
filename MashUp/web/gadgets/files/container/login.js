/**
 * Copyright (c) 2009
 * Helen Kaltegaertner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/


/*
 * Login works so far (user is registered in the oryx repository as well),
 * but login dialog is always displayed.
 * 
 */



var currentUser;

if (! Container)
	var Container = {};

Container.login = (function(){
	
	return {
	
		// variable currentUser has to be initialized when the page is loaded
		// extracting the openId from the cookie.
		// Not yet implemented!
		// Login dialog is never exchanged by the logout dialog.
		// To try the logout functionality though return false in the method below.
		isPublicUser: function(){
			if (!currentUser)
				return true;
			
			return currentUser == 'public';
		},
		
		init: function() {
			if (this.isPublicUser())
				Container.login.showLogin();
			else
				Container.login.showLogout();
		},
		
		showLogin: function(){
			document.getElementById("login").innerHTML = 
				'<div><form action="http://localhost:8080/backend/poem/login?redirect=/gadgets/files/container/home.html"'+ 
						'method="post" id="openid_login" >'+
						'<span>'+
							'<img src="/backend/images/repository/hpi.png" onclick="Container.login.changeOpenId(\'https://openid.hpi.uni-potsdam.de/user/username\' , 39, 8)"/>'+
							'<img src="/backend/images/repository/blogger.png" onclick="Container.login.changeOpenId(\'http://username.blogspot.com/\' , 7, 8)"/>'+
							'<img src="/backend/images/repository/getopenid.png" onclick="Container.login.changeOpenId(\'http://getopenid.com/username\', 21, 8)"/>'+
						'</span>'+
					'<input type="text" name="openid_identifier" id="openid_login_openid" class="text gray"'+
						'value="getopenid.com/helenk"/>'+
					'<input type="submit" value="login"/>'+
				'</form></div>';
			
		},
		
		showLogout: function(){
			document.getElementById("login").innerHTML = 
				'<form action="http://localhost:8080/backend/poem/login?logout=true&redirect=/gadgets/files/container/home.html"'+ 
						'method="post" id="openid_login">'+
				'<div>'+
					'[user]'+
					'<input type="submit" class="button" value="logout" />'+
				'</div>'+
			'</form>';
		},
		
		// not yet in use
		login: function(){
			currentUser = document.getElementById("openid_login_openid").value;
			Container.login.showLogout();
			return true;
		},
		
		// not yet in use
		logout: function(){
			currentUser = 'public';
			Container.login.showLogin();
			return true;
		},
		
		// not yet fully implemented
		collectUserData: function(){
			
			new Ajax.Request("/backend/poem/user",  {
				method: "get",
				asynchronous : false,
				onSuccess: function(transport) {
					this._userData = transport.responseText.evalJSON();
					
				//	this._busyHandler.end.invoke();
				}.bind(this),
				onFailure: function() {
						alert("Error loading user data.")
				//		this._busyHandler.end.invoke();
				}
			});
			
		},
		
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
	}
})();
			