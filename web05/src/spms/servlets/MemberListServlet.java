package spms.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spms.dao.MySqlMemberDao;
import spms.vo.Member;

@WebServlet("/member/list")
public class MemberListServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			ServletContext sc = this.getServletContext();
			// ServletContext보관소에서 DB커넥션을 꺼냅니다.
//			Connection conn = (Connection) sc.getAttribute("conn");
			
//			MemberDao memberDao = new MemberDao();
//			memberDao.setConnection(conn);
			MySqlMemberDao memberDao = (MySqlMemberDao)sc.getAttribute("memberDao");
			
			request.setAttribute("members", memberDao.selectList());
			request.setAttribute("viewUrl", "/member/MemberList.jsp");
			
//			response.setContentType("text/html; charset=UTF-8");
//			// JSP로 출력을 위임한다.
//			RequestDispatcher rd = request.getRequestDispatcher("/member/MemberList.jsp");
//			rd.include(request, response);
			//DispatcherServlet으로 기능 위임함
		} catch(Exception e) {
			throw new ServletException(e);
			// 오류처리 페이지로 위임하는것도 프런트 컨트롤러 DispatcherServlet이 하기때문에 제거 함
//			e.printStackTrace();
//			request.setAttribute("error", e);
//			RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
//			rd.forward(request, response);
		}
	}
}
