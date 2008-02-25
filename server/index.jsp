<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Hello World!</title>
	<meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8" />
	<style type="text/css">
	.openid_identifier {
		width:300px;
	}
	</style>
</head>
<body>
	<div>
		<fieldset>
			<legend>Sample 1:</legend>
			<form action="consumer" method="post">
				<div>
					<input type="text" name="openid_identifier" class="openid_identifier" />
					<input type="submit" name="login" value="Login" />
				</div>
			</form>
		</fieldset>

		<fieldset>
			<legend>Sample 2: using the Simple Registration extension(doc: <a href="http://code.google.com/p/openid4java/wiki/SRegHowTo">SRegHowTo</a>)</legend>
			<form action="consumer" method="post">
				<div>
					<input type="text" name="openid_identifier" class="openid_identifier" />
					<br />
					<input type="checkbox" name="nickname" value="1" id="nickname" checked="checked" />
					<label for="nickname">Nickname</label>

					<input type="checkbox" name="email" value="1" id="email" checked="checked" />
					<label for="email">Email</label>

					<input type="checkbox" name="fullname" value="1" id="fullname" checked="checked" />
					<label for="fullname">Fullname</label>

					<input type="checkbox" name="dob" value="1" id="dob" checked="checked" />
					<label for="dob">Date of birth</label>

					<input type="checkbox" name="gender" value="1" id="gender" checked="checked" />
					<label for="gender">Gender</label>

					<input type="checkbox" name="postcode" value="1" id="postcode" checked="checked" />
					<label for="postcode">Postcode</label>

					<input type="checkbox" name="country" value="1" id="country" checked="checked" />
					<label for="country">Country</label>

					<input type="checkbox" name="language" value="1" id="language" checked="checked" />
					<label for="language">Language</label>

					<input type="checkbox" name="timezone" value="1" id="timezone" checked="checked" />
					<label for="timezone">Timezone</label>

					<br />
					<input type="submit" name="login" value="Login" />
					</div>
			</form>
		</fieldset>

	</div>
</body>
</html>
