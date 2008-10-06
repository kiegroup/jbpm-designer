<?
/**
 * Copyright (c) 2006
 * Martin Czuchra, Nicolas Peters, Daniel Polak, Willi Tscheschner
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

/* This is an alternative back end for the Oryx Business Process Editor */

// no error reporting in production mode.
error_reporting(0);

// constants for error display
define('DATABASE_DOWN', 'The database seems to be down. Try again later!');
define('DATABASE_ERROR', 'There seems to be an error with the database.<br/>The site\'s administrator has been notified.');

// initializing database connection
$link = mysql_connect("localhost", "oryx", "") or
	showError(DATABASE_DOWN, mysql_error());

// selecting database
mysql_select_db("oryx") or
	showError(DATABASE_ERROR, mysql_error());

// If there are POST-REQUESTs -> Store in to database
if($_POST['resource']) {

	$reso = $_POST['resource'];
	$data = $_POST['data'];

	writeToMySQL($reso, $data);


// If there are GET-REQUESTs -> Get the data
} else if($_GET['resource']) {

	// set the contenttype to application/xhtml+xml
	header('Content-type: application/xhtml+xml');

	// Get the resource-id
	$resource = $_GET['resource'];

	// Check if there is the resource in the database
	$result = mysql_query("SELECT ID, Site FROM sites WHERE Name = '".$resource."' LIMIT 1");
	$row = mysql_fetch_row($result);
	$data = "";
	// If not
	if ($row[0] == "") {
		// Create a new Canvas without datas
		$bo = '			<div class="-oryx-canvas" id="oryx-canvas123" style="width:1200px; height:600px;">

			<a href="./stencilsets/bpmn/bpmn.json" rel="oryx-stencilset"></a>
			
			<span class="oryx-mode">writeable</span>
			<span class="oryx-mode">fullscreen</span>
		</div>
';

		$data = template($bo);
	} else {
		// If so, get the data
		$data = template($row[1]);
	}

	// Retrun the data
	echo $data;

// If there are no POST or GET-REQUEST
} else {

	// get all process ids and names.
	$processes = mysql_query("SELECT ID, Name FROM sites");
	if(mysql_errno())
		showError(DATABASE_ERROR, mysql_error());

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title>Oryx - Process Overview</title>
</head>
<body>
	<div style='text-align: center;'>
	
		<p>
		<form action='<? echo $_SERVER['PHP_SELF'] ?>' method='get'>
			Create a new process:<br/>
			<img src='./images/crystal/empty.png' style='float: clear;' width='128' height='128'/><br/>
			<input type='text' name='resource' value='' />
			<input type='submit' value='Add'/>
		</form>
		</p>
<?
	
	if ($processes) {
		
		?>
		<div style='text-align:left; float: clear;'>Or review an existing one:<br/></div>
		<?
	
		while ($entry = mysql_fetch_row($processes)) {
		
		?>
		    <div style='padding: 16px; display: inline; float: left;'>
		        
		        <div>
				<a href='<? echo $_SERVER['PHP_SELF'] ?>?resource=<? echo $entry[1] ?>' style='text-decoration: none'>
					<img src='./images/crystal/misc.png' border='0' width='128' height='128'/><br/>
			        <? echo $entry[1] ?>
				</a>
				</div>
		    </div>
		<?
		}
	} else {
	
		?>
		There currently are no saved processes.<br/>
		<?
	
	}
?>
	</div>
</body>
</html>
<?
}


function writeToMySQL($resource, $data) {

	$result = mysql_query("SELECT ID FROM sites WHERE Name = '".$resource."' LIMIT 1");
	
	$row = mysql_fetch_row($result);

	// Update
	if($row[0] != ""){
		echo mysql_query("UPDATE sites SET Site = '".$data."' WHERE Name = '".$resource."'") or die('Update error: '.mysql_errno().', '.mysql_error());
	// Insert
	}else {
		mysql_query("INSERT INTO sites VALUES ('','".$resource."','".$data."')") or die('INSERT error: '.mysql_errno().', '.mysql_error());
	}

}

function template($body) {
	global $resource;
	return '<?xml version="1.0" encoding="utf-8"?>
	<html xmlns="http://www.w3.org/1999/xhtml"
					    xmlns:b3mn="http://b3mn.org/2007/b3mn"
					    xmlns:ext="http://b3mn.org/2007/ext"
						xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
						xmlns:atom="http://b3mn.org/2007/atom+xhtml">

					    <head profile="http://purl.org/NET/erdf/profile">

					        <title>'.$resource.' - Oryx</title>

        <!-- libraries -->
        <script src="lib/prototype-1.5.1.js" type="text/javascript" />
        <script src="lib/path_parser.js" type="text/javascript" />
        <script src="lib/ext-1.0/adapter/yui/yui-utilities.js" type="text/javascript" />
        <script src="lib/ext-1.0/adapter/yui/ext-yui-adapter.js" type="text/javascript" />
        <script src="lib/ext-1.0/ext-all-debug.js" type="text/javascript" />
        <script src="lib/ext-1.0/ColorField.js" type="text/javascript" />
        <style media="screen" type="text/css">
			@import url("lib/ext-1.0/resources/css/ext-all.css");
			@import url("lib/ext-1.0/resources/css/ytheme-gray.css");
		</style>

				
        <script src="shared/kickstart.js" type="text/javascript" />
        <script src="shared/erdfparser.js" type="text/javascript" />
        <script src="shared/datamanager.js" type="text/javascript" />

        <!-- oryx editor -->
        <script src="oryx.js" type="text/javascript" />
        <link rel="Stylesheet" media="screen" href="css/theme_norm.css" type="text/css" />

        <!-- erdf schemas -->
        <link rel="schema.dc" href="http://purl.org/dc/elements/1.1/" />
        <link rel="schema.dcTerms" href="http://purl.org/dc/terms/ " />
        <link rel="schema.b3mn" href="http://b3mn.org" />
        <link rel="schema.oryx" href="http://oryx-editor.org/" />
        <link rel="schema.raziel" href="http://raziel.org/" />

		<meta name="oryx.type" content="http://b3mn.org/stencilset/bpmn#BPMNDiagram" />
							
					    </head>

					    <body>

							'.$body.'

					    </body>
					</html>';

}



function showError($nice, $real) {
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
	<title>Oryx - Error</title>
</head>
<body>
	<div style='text-align: center;'>
		<img src='./images/crystal/error.png' style='padding-top: 200px;' width='128' height='128'/>
		<p><? echo $nice ?></p>
		<!-- ACTUAL ERROR: <? echo $real ?> -->
	</div>
</body>
</html>
<?
die;
}
?>