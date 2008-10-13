/**
 * @author nico
 */
if (!window.google || !google.gears) {
    location.href = "http://gears.google.com/?action=install&message=Install Gears to use Oryx on a Stick." +
                    "&return=" + location.href + "&name=Oryx on a Stick";
  }
  
//get permission
google.gears.factory.getPermission();

//create database
var db = google.gears.factory.create('beta.database');
db.open("oryx-database");
db.execute('create table if not exists Models' +
           ' (ID int, Name text, Model text, editorUrl text, Type text)');
var rs = db.execute('select * from Models');

var models = "";
while (rs.isValidRow()) {
  models += '<a href="' + rs.field(3) + '" target="_blank">' + rs.field(1) + ' (' + rs.field(4) + ')' + '</a>';
  models += '<input type="button" name="DeleteModel" value="Delete Model" onclick="deleteModel( + ' + rs.field(0) + ')"><br/>'

  rs.next();
}

db.close();

var cont = document.getElementById("existingmodels");
cont.innerHTML=models;

function deleteModel(id) {
	var db = google.gears.factory.create('beta.database');
	db.open("oryx-database");
	var rs = db.execute('delete from Models where ID=?', [id]);
	db.close();
	window.location.reload();
}

