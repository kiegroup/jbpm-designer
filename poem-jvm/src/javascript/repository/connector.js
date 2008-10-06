







/*


// Deprecated
Repository.connector = {
		user : null,

		init : function(openid) {
			Ext.Ajax.request({
				method : "GET",
				url : "./user",
				success : function(response, options) {
					eval("var usr = "+ response.responseText);
					Repository.connector.user = usr;
					Ext.Msg.alert("DEBUG", Repository.connector.user);
				}
			});
		},
		update : function() {
			Ext.Ajax.request({
				method : "POST",
				url : "./user",
				params : {
					json_data : Ext.util.JSON.encode(user)
				},
				failure : function(response, options) {
					Ext.Msg.alert("ERROR", "Updating user information failed \n" + response.responseText);
				}
			});
		},
		// Models connector
		models : {
			_cache : {},
			getTags : function(model_id, callback) {
				Ext.Ajax.request({
					method : "GET",
					url : "./model/" + model_id + "/tag",
					success : function(response, options) {
						// Deserialize json
						eval("var tags = "+ response.responseText);	
						callback(tags);
					}
			}
		}
}*/