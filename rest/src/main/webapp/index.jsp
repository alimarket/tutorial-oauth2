<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
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
    <title>Resources provider</title>
</head>
<body class="page">

<nav>
    <ul>
        <li><a href="/">Home</a></li>
        <authz:authorize ifNotGranted="ROLE_USER">
            <li><a href="/login">Login</a></li>
        </authz:authorize>
        <authz:authorize ifAllGranted="ROLE_USER">
            <li><a href="/logout">Logout</a></li>
        </authz:authorize>
    </ul>
</nav>
<h1>Resources provider</h1>

<p>This webapp is the services provider.
</p>

<authz:authorize ifAllGranted="ROLE_USER">
    <p>Welcome!
    </p>
</authz:authorize>

</body>
</html>
