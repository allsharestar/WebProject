package spms.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import spms.dao.MySqlMemberDao;
import spms.vo.Member;

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 프런트 컨트롤러 적용
		request.setAttribute("viewUrl", "/auth/LogInForm.jsp");
//		RequestDispatcher rd = request.getRequestDispatcher("/auth/LogInForm.jsp");
//		rd.forward(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		
		try {
			ServletContext sc = this.getServletContext();
			MySqlMemberDao memberDao = (MySqlMemberDao)sc.getAttribute("memberDao");
			
			Member member = memberDao.exist(request.getParameter("email"), request.getParameter("password"));
			
			if(member != null) {
				HttpSession session = request.getSession();
				session.setAttribute("member", member);
				
				request.setAttribute("viewUrl", "redirect:../member/list.do");
//				response.sendRedirect("../member/list");
			} else {
				request.setAttribute("viewUrl", "redirect:logInFail.jsp");
//				RequestDispatcher rd = request.getRequestDispatcher("/auth/LogInFail.jsp");
//				rd.forward(request, response);
			}
		} catch(Exception e) {
			throw new ServletException(e);
//			e.printStackTrace();
//			request.setAttribute("error", e);
//			RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
//			rd.forward(request, response);
		}
	}
}
