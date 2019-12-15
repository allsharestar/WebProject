<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<div style="background-color:#00008b;color:#ffffff;height:20px;padding:5px;">
SPMS(Simple Project Management System)
<span style="float:right;">
	<a style="color:white;" href="<%=request.getContextPath()%>/member/list.do">회원 목록</a>
	<a style="color:white;" href="<%=request.getContextPath()%>/project/list.do">프로젝트 목록</a>
<c:choose>
<c:when test="${sessionScope.member.email != null}">
	${sessionScope.member.name}
	<a style="color:white;" href="<%=request.getContextPath()%>/auth/logout.do">로그아웃</a>
</c:when>
<c:otherwise>
	<a style="color:white;" href="<%=request.getContextPath()%>/auth/login.do">로그인</a>
</c:otherwise>
</c:choose>
</span>
</div>