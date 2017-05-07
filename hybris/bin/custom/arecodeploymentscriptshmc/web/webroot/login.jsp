<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <link rel="stylesheet" href="<c:url value="/static/arecodeploymentscriptshmc-webapp.css"/>" type="text/css"
          media="screen, projection"/>
</head>
<body>
<div class="container">
    <form action="<c:url value="/j_spring_security_check"/>" method="POST">
        <div id="logincontrols" class="logincontrols">
            <div id="loginErrors">&nbsp;
                <c:if test="${not empty param.login_error}">
                    <c:out value="Login failed: ${SPRING_SECURITY_LAST_EXCEPTION.message}"/>
                </c:if>
            </div>
            <fieldset class="login-form">
                <p>
                    <input type="text" name="j_username" placeholder="Username" value="admin"/>
                </p>
                <p>
                    <input type="password" name="j_password" placeholder="Password" value=""/>
                </p>
                <p>
                    <label><input type="checkbox" name="_spring_security_remember_me" class="checkbox"
                                  id="_spring_security_remember_me"/> Remember Login</label>
                </p>
                <p>
                    <button type="submit" class="button" autofocus>login</button>
                    <input type="hidden"
                           name="${_csrf.parameterName}"
                           value="${_csrf.token}"/>
                </p>
                </fi>
            </fieldset>
    </form>
</div>
</body>
</html>