<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Hello World!</title>
	<meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8" />
</head>
<body>
	<div>
		Login Success!
			<div>
			queryString:
			${pageContext.request.queryString}
			</div>
		<div>
			<dl>
				<dt>Your OpenID: ${identifier}</dt>
				<dt>Nickname: ${nickname}</dt>
				<dt>Email: ${email}</dt>
				<dt>Fullname: ${fullname}</dt>
				<dt>Date of birth: ${dob}</dt>
				<dt>Gender: ${gender}</dt>
				<dt>Postcode: ${postcode}</dt>
				<dt>Country: ${country}</dt>
				<dt>Language: ${language}</dt>
				<dt>Timezone: ${timezone}</dt>
			</dl>
		</div>
		<div>
			<a href="logout.jsp">Logout</a>
		</div>
	</div>
</body>
</html>
