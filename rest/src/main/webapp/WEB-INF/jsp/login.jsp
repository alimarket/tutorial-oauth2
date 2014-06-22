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
    <title>Resources provider - Login</title>
</head>

<body class="page">

<h1>Resources provider - Login</h1>

<c:if test="${not empty param.error}">
    <h2>Woops!</h2>

    <p class="error">
        <c:choose>
            <c:when test="${param.error == 'authentication'}">
                Your login attempt was not successful.
            </c:when>
            <c:when test="${param.error == 'authorization'}">
                You are not permitted to access that resource.
            </c:when>
            <c:otherwise>
                Unknown error!
            </c:otherwise>
        </c:choose>
        <c:if test="${SPRING_SECURITY_LAST_EXCEPTION != null}">
            <br/>Reason: <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>
        </c:if>
    </p>
</c:if>

<form action="/login" method="post">
    <fieldset>
        <legend>Login</legend>
        <p>
            <label for="username">Username</label>
            <input type="text" id="username" name="username"/>
        </p>

        <p>
            <label for="password">Password</label>
            <input type="password" id="password" name="password"/>
        </p>
        <!-- if using RememberMeConfigurer make sure remember-me matches RememberMeConfigurer#rememberMeParameter -->
        <p>
            <label for="remember-me">Remember Me?</label>
            <input type="checkbox" id="remember-me" name="remember-me"/>
        </p>

        <div>
            <button type="submit" class="btn">Log in</button>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </div>
    </fieldset>
</form>
</body>
</html>
