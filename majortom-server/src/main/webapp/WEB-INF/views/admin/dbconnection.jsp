<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Admin Login</title>
</head>
<body>

<form:form action="modifydb" method="post">
<table>
<tr>
<td>Host:</td>
<td><form:input path="host" /></td>
</tr>

<tr>
<td>Name:</td>
<td><form:input path="name" /></td>
</tr>

<tr>
<td>Username:</td>
<td><form:input path="username" /></td>
</tr>

<tr>
<td>Password:</td>
<td><form:input path="password" /></td>
</tr>
<!-- 
<tr>
<td>DB Dialect:</td>
<td><form:select path="dialect" />
</td>
</tr>
 -->
<tr>
<td></td>
<td><input type="submit"></td>
</tr>

</table>

</form:form>

</body>
</html>