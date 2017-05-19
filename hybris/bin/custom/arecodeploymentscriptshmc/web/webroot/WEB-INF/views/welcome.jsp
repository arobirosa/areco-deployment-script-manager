<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Welcome to arecodeploymentscriptshmc</title>
    <link rel="stylesheet" href="<c:url value="/static/arecodeploymentscriptshmc-webapp.css"/>" type="text/css"
          media="screen, projection"/>
</head>
<div class="container">
    <img src="<c:url value="${logoUrl}" />" alt="Hybris platform logo"/>

    <h2>Welcome to "arecodeploymentscriptshmc" extension</h2>

    <h3><b>Getting started</b></h3>

    <div>
        <p>
            This extension was generated using yEmpty template. It contains basic Spring MVC with sample Controller as
            well as WebAppMediaFilter enabled. Now you should go through the crucial configuration files and adjust them
            to your needs. Feel free to remove default controller and jsp pages.
        </p>

        <ul>
            <li><b>resources/arecodeploymentscriptshmc-items.xml</b> - here you can model your items</li>
            <li><b>resources/arecodeploymentscriptshmc-spring.xml</b> - here you can define your services</li>
            <li><b>web/webroot/WEB-INF/config/arecodeploymentscriptshmc-spring-mvc-config.xml</b> - here is a Spring MVC related
                configuration
            </li>
            <li><b>web/webroot/WEB-INF/config/arecodeploymentscriptshmc-web-app-config.xml</b> - here you can define web related services,
                facades etc.
            </li>
            <li><b>web/webroot/WEB-INF/config/arecodeploymentscriptshmc-spring-security-config.xml</b> - here you can configure your Spring Security settings</li>
            <li><b>web/webroot/WEB-INF/web.xml</b> - here you can configure filters, servlets etc.</li>
            <li><b>web/webroot/WEB-INF/views</b> - here you can keep your jsp pages</li>
            <li><b>web/webroot/static</b> - here you can keep your static files, javascripts, css etc.</li>
        </ul>

        <p>
            This extension comes with basic Spring Security configuration which is disabled by default. If you want to
            enable it go to the <em>web/webroot/WEB-INF/web.xml</em> file and uncomment filter named <b>springSecurityFilter</b>,
            its filter mapping and restart application.
        </p>
    </div>
</div>
</html>