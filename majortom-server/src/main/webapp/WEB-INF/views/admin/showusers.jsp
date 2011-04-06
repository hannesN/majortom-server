<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>

<h1>All Users</h1>
<a href="./">Go Back</a>

<table>
	<tr>
		<th>Username</th>
		<th>Password Hash</th>
		<th>API Key</th>
		<th>Authorities</th>
		
		
	</tr>

	<c:forEach  var="e" items="${entries}">
		<tr>
			<td><c:out value="${e.username}" /></td>
			<td><c:out value="${e.password}" /></td>
			<td><c:out value="${e.apiKey}" /></td>
			<td>
				<c:forEach  var="a" items="${e.authorities}">
					<c:out value="${a.authority}" />,
				</c:forEach>
			</td>
			<td>
			<s:url value="edituser?id=${e.username}" var="editurl"/>
			<a href="${editurl}">edit</a>
			</td>
		</tr>

	</c:forEach>
	<!--  TODO add user -->

</table>