<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<form:form action="createtm" method="post" enctype="multipart/form-data">
	<table>
		<tr>
			<td>Base Locator:</td>
			<td><form:input path="baselocator"/></td>
		</tr>

		<tr>
			<td>In Memory:</td>
			<td><form:checkbox path="inmemory" /></td>
		</tr>
		
		<tr>
			<td>Local file path:</td>
			<td><form:input path="filePath" /></td>
		</tr>
		
		<tr>
			<td>Initial Capacity:</td>
			<td><form:input path="initialCapacity" /></td>
		</tr>

		<tr>
			<td>Topic Map File:</td>
			<td><input name="file" id="file" type="file"/></td>
		</tr>

		<tr>
			<td></td>
			<td><input type="submit"></td>
		</tr>

	</table>

</form:form>