<%@
page
	language="java" 
	contentType="text/html; charset=UTF-8" 
	pageEncoding="UTF-8" 
%>
<%
String v1 = "";
String v2 = "";
String result = "";
String[] selected = {"", "", "", ""};

// 값이 있을 때만 꺼낸다. v1과 v2가 비어있지 않을 때
if(!request.getParameter("v1").equals("") && !request.getParameter("v2").equals("")){
	v1 = request.getParameter("v1");
	v2 = request.getParameter("v2");
	String op = request.getParameter("op");
	
	result = calculate(Integer.parseInt(v1), Integer.parseInt(v2), op);
	
	if("+".equals(op))
		selected[0] = "selected";
	else if("-".equals(op))
		selected[1] = "selected";
	else if("*".equals(op))
		selected[2] = "selected";
	else if("/".equals(op))
		selected[3] = "selected";
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>계산기</title>
</head>
<body>
	<h2>JSP 계산기</h2>
	<form action="Calculator.jsp" method="get">
		<input type="text" name="v1" size="4" value="<%=v1%>">
		<select name="op">
			<option value="+" <%=selected[0]%>>+</option>
			<option value="-" <%=selected[1]%>>-</option>
			<option value="*" <%=selected[2]%>>*</option>
			<option value="/" <%=selected[3]%>>/</option>
		</select>
		<input type="text" name="v2" size="4" value="<%=v2%>">
		<input type="submit" value="=">
		<input type="text" size="8" value="<%=result %>"><br>
	</form>
</body>
</html>
<%!
// JSP선언문 !르호 시작하는거는 서블릿 클래스의 멤버(변수나 메서드)를 선언할 때 사용하는 태그
// 위치는 위, 아래, 가운데 어디든 상관없다. 왜냐하면, 선언문은 _jspService()안에 복사되는 것이 아니라
//	_jspService() 밖의 클래스 블록 안에 복사되기 때문이다.
private String calculate(int a, int b, String op){
	int r = 0;
	
	if("+".equals(op))
		r = a + b;
	else if("-".equals(op))
		r = a - b;
	else if("*".equals(op))
		r = a * b;
	else if("/".equals(op))
		r = a / b;
	
	return Integer.toString(r);
}
%>