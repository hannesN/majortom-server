<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>

<h1>All Topic Maps</h1>
<a href="./">Go Back</a>

<table>
	<tr>
		<th>Id</th>
		<th>Base Locator</th>
		
	</tr>

	<c:forEach  var="e" items="${entries}">
		<tr>
			<td><c:out value="${e.key}" /></td>
			<td><c:out value="${e.value}" /></td>
			<td>
			<s:url value="cleartopicmap?id=${e.key}" var="delurl"/>
			<a href="${delurl}">Clear</a>
			</td>
			<td>
			<s:url value="removetopicmap?id=${e.key}" var="delurl"/>
			<a href="${delurl}">Remove</a>
			</td>
			<td>
			<s:url value="closetopicmap?id=${e.key}" var="delurl"/>
			<a href="${delurl}">Close</a>
			</td>
		</tr>

	</c:forEach>
	<!--  TODO iterate through all topic maps -->

</table>