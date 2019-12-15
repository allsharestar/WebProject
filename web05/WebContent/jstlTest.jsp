<%@ page import="java.util.*" %>
<%@ page import="spms.vo.Member" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>jstl실습</title>
</head>
<body>
	<%
	pageContext.setAttribute("myRB", ResourceBundle.getBundle("MyResourceBundle"));
	
	pageContext.setAttribute("scores", new int[]{90, 80, 70, 100}); 
	
	List<String> nameList = new LinkedList<String>();
	nameList.add("홍길동");
	nameList.add("임꺽정");
	nameList.add("일지매");
	pageContext.setAttribute("nameList", nameList);
	
	Map<String, String> map = new HashMap<String, String>();
	map.put("s01", "홍길동");
	map.put("s02", "임꺽정");
	map.put("s03", "일지매");
	pageContext.setAttribute("map", map);
	
	pageContext.setAttribute("member",
			new Member()
			.setNo(100)
			.setName("홍길동")
			.setEmail("allsharestar@gmail.com"));
	%>
	${myRB.OK}<br>
	${nameList[2]}<br>
	${scores[1]}<br>
	${map.s02}<br>
	${member.email}<br>
	<c:out value="안녕하세요" /><br>
	<c:out value="${null}">반갑습니다</c:out><br>
	<c:out value="안녕하세요!">반값습니다</c:out><br>
	<c:out value="${null }" /><br>
	
	<h3>값 설정 방식</h3>
	<c:set var="username1" value="홍길동" />
	<c:set var="username2">임꺽정</c:set>
	${username1}<br>
	${username2}<br>
	
	<h3>기본 보관소 - page</h3>
	${pageScope.username1}<br>
	${requestScope.username1}<br>
	
	<h3>보관소 지정 - scope 속성</h3>
	<c:set var="username3" scope="request">일지매</c:set>
	${pageScope.username3}<br>
	${requestScope.username3}<br>
	
	<h3>기존의 값 덮어씀</h3>
	<% pageContext.setAttribute("username4", "똘이장군"); %>
	기존 값 = ${username4}<br>
	<c:set var="username4" value="주먹대장" />
	덮어쓴 값 = ${username4}<br>
		
	<h3>객체의 프로퍼티 값 변경</h3>
	<%!
	public static class MyMember{
		int no;
		String name;
		
		public int getNo(){
			return no;
		}
		public void setNo(int no){
			this.no = no;
		}
		public String getName(){
			return name;
		}
		public void setName(String name){
			this.name = name;
		}
	}
	%>
	<%
	MyMember member = new MyMember();
	member.setNo(100);
	member.setName("홍길동");
	pageContext.setAttribute("member", member);
	%>
	${member.name}<br>
	<c:set target="${member}" property="name" value="임꺽정" />
	${member.name}<br>
	
	<h3>보관소에 저장된 값 제거</h3>
	<% pageContext.setAttribute("username1", "홍길동"); %>
	${username1}<br>
	<c:remove var="username1" />
	${username1}<br>
	
	<h3>c:if태그</h3>
	<c:if test="${10 > 20}" var="result1">
		10은 20보다 크다.<br>
	</c:if>
	${result1}<br>
	
	<c:if test="${10 < 20}" var="result2">
		20은 10보다 크다.<br>
	</c:if>
	${result2}<br>
	
	<c:set var="userid" value="admin" />
	<c:choose>
		<c:when test="${userid == 'hong'}">
			홍길동님 반갑습니다.
		</c:when>
		<c:when test="${userid == 'leem'}">
			★전지탱님 반갑습니다★
		</c:when>
		<c:when test="${userid == 'admin'}">
			관리자 전용 페이지입니다.
		</c:when>
		<c:otherwise>
			등록되지 않은 사용자입니다.
		</c:otherwise>
	</c:choose>
	
	<% pageContext.setAttribute("nameList", new String[]{"전지영", "전지탱", "전지훈"}); %>
	<ul>
		<c:forEach var="name" items="${nameList}">
			<li>${name}</li>
		</c:forEach>
	</ul>
	
	<% pageContext.setAttribute("nameList2", new String[]{"홍길동", "임꺽정", "일지매", "주먹대장", "똘이장군"}); %>
	<ul>
		<c:forEach var="name" items="${nameList2}" begin="2" end="3">
			<li>${name}</li>
		</c:forEach>
	</ul>
	
	<% 
	ArrayList<String> nameList3 = new ArrayList<String>();
	nameList3.add("홍길동");
	nameList3.add("임꺽정");
	nameList3.add("일지매");
	nameList3.add("주먹대장");
	nameList3.add("똘이장군");
	pageContext.setAttribute("nameList3", new String[]{"홍길동", "임꺽정", "일지매", "주먹대장", "똘이장군"});
	%>
	<ul>
		<c:forEach var="name" items="${nameList3}">
			<li>${name}</li>
		</c:forEach>
	</ul>
	
	<% pageContext.setAttribute("nameList4", "홍길동, 임꺽정, 일지매, 전지훈, 전티영"); %>
	<ul>
		<c:forEach var="name" items="${nameList4}">
			<li>${name}</li>
		</c:forEach>
	</ul>
	
	<ul>
		<c:forEach var="no" begin="1" end="6">
			<li><a href="jstl0${no}.jsp">JSTL 예제 ${no}</a></li>
		</c:forEach>
	</ul>
	
	<% pageContext.setAttribute("tokens", "v1=20&v2=30&op=+"); %>
	<ul>
		<c:forTokens var="item" items="${tokens}" delims="&">
			<li>${item}</li>
		</c:forTokens>
	</ul>
	
	<c:url var="calcUrl" value="http://localhost:9999/calc">
		<c:param name="v1" value="20" />
		<c:param name="v2" value="30" />
		<c:param name="op" value="+" />
	</c:url>
	<a href="${calcUrl}">계산하기</a><br>
	
	<fmt:parseDate var="date1" value="2013-11-16" pattern="yyyy-MM-dd" />
	${date1}<br>
	<fmt:formatDate value="${date1}" pattern="MM/dd/yy" />
		
	<jsp:include page="/Tail.jsp" />
</body>
</html>