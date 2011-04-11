<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %> 
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>


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

