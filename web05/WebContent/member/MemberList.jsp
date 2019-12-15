<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원 목록</title>
</head>
<body>
	<jsp:include page="/Header.jsp" />

	<h1>회원 목록</h1>
	<p><a href='add.do'>신규 회원</a></p>
	<table>
		<tr><th>번호</th><th>이름</th><th>이메일</th><th>가입일</th><th></th></tr>
		<c:forEach var="member" items="${members}">
			<tr>
				<td>${member.no}</td>
				<td><a href='update.do?no=${member.no}'>${member.name}</a></td>
				<td>${member.email}</td>
				<td>${member.createdDate}</td>
				<td><a href='delete.do?no=${member.no}'>삭제</a></td>
			<tr>
		</c:forEach>
	</table>
		
	<jsp:include page="/Tail.jsp" />
</body>
</html>