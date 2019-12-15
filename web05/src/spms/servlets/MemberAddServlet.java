package spms.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spms.dao.MySqlMemberDao;
import spms.vo.Member;

@WebServlet("/member/add")
public class MemberAddServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// JSP로 출력을 위임한다.
		// 프런트 컨트롤러 적용
//		response.setContentType("text/html; charset=UTF-8");
//		RequestDispatcher rd = request.getRequestDispatcher("/member/MemberForm.jsp");
//		rd.include(request, response);
		request.setAttribute("viewUrl", "/member/MemberForm.jsp");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			ServletContext sc = this.getServletContext();
			MySqlMemberDao memberDao = (MySqlMemberDao)sc.getAttribute("memberDao");
			
			// 프런트 컨트롤러 적용
//			memberDao.insert(new Member()
//					.setName(req.getParameter("name"))
//					.setPassword(req.getParameter("password"))
//					.setEmail(req.getParameter("email")));
			
			Member member = (Member)req.getAttribute("member");
			memberDao.insert(member);
			
			req.setAttribute("viewUrl", "redirect:list.do");
//			res.sendRedirect("list");
		} catch(Exception e) {
			throw new ServletException(e);
//			e.printStackTrace();
//			req.setAttribute("error", e);
//			RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
//			rd.forward(req, res);
		}
	}
}
