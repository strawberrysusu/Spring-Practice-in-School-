<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title><spring:message code="member.register"/></title>
</head>
<body>
<h2>Step2 - <spring:message code="member.info"/></h2>
<form:form action="step3" modelAttribute="registerRequest">
    <p>
        <label for="email"><spring:message code="email"/> : </label>
        <form:input path="email" />
        <form:errors path="email" />
    </p>
    <p>
        <label for="name"><spring:message code="name"/> : </label>
        <form:input path="name" />
        <form:errors path="name" />
    </p>
    <p>
        <label for="password"><spring:message code="password"/> : </label>
        <form:password path="password" />
        <form:errors path="password" />
    </p>
    <p>
        <label for="confirmPassword"><spring:message code="password.confirm"/> : </label>
        <form:password path="confirmPassword" />
        <form:errors path="confirmPassword" />
    </p>
    <input type="submit" value="<spring:message code="register.btn"/>" />
</form:form>
</body>
</html>
