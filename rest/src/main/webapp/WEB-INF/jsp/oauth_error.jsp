<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <!-- Always force latest IE rendering engine or request Chrome Frame -->
    <meta content="IE=edge,chrome=1" http-equiv="X-UA-Compatible">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link href="/styles/screen.css" rel="stylesheet" type="text/css"/>
    <title>Resources provider - OAuth2 error</title>
</head>

<body class="page">

<h1>Resources provider - OAuth2 error</h1>

<p>
    <c:out value="${message}"/> ( <c:out value="${error.summary}"/> )
</p>

<p>Please go back to your client application and try again, or contact the owner and ask for support</p>

</body>
</html>
