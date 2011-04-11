<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<form:form action="modifyuser" method="post">
	<table>
		<tr>
			<td>Username:</td>
			<td><form:input path="username" /><br />
			<form:errors path="username"></form:errors></td>
		</tr>
		<tr>
			<td>Password:</td>
			<td><form:input path="plainPassword" /><br />
			<form:errors path="plainPassword"></form:errors></td>
		</tr>
		<tr>
			<td>Enabled</td>
			<td><form:checkbox path="enabled" /></td>
		</tr>
		<tr>
			<td>authorities2</td>
			<td><form:checkboxes path="plainAuthorities" items="${items2}"/> <br />
			<form:errors path="plainAuthorities"></form:errors></td>
		</tr>
		<tr>
			<td></td>
			<td><input type="submit"></td>
		</tr>

	</table>
<form:hidden path="apiKey"/>
<form:hidden path="password"/>
</form:form>

