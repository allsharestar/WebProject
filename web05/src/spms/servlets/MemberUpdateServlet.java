package spms.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import spms.dao.MySqlMemberDao;
import spms.vo.Member;

@WebServlet("/member/update")
public class MemberUpdateServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			// ���ؽ�Ʈ �ʱ�ȭ �Ű������� ���� �������� ServletContext ��ü�� �ʿ��ϴ�.
			ServletContext sc = this.getServletContext();
			MySqlMemberDao memberDao = (MySqlMemberDao)sc.getAttribute("memberDao");
			
			// request�� ȸ�� ��� ������ �����Ѵ�.
			req.setAttribute("member", memberDao.selectOne(Integer.parseInt(req.getParameter("no"))));
			req.setAttribute("viewUrl", "/member/MemberUpdateForm.jsp");
			
//			res.setContentType("text/html; charset=UTF-8");
//			// JSP�� ����� �����Ѵ�.
//			RequestDispatcher rd = req.getRequestDispatcher("/member/MemberUpdateForm.jsp");
//			rd.include(req, res);
		} catch(Exception e) {
			throw new ServletException(e);
//			e.printStackTrace();
//			req.setAttribute("Error", e);
//			RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
//			rd.forward(req, res);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		PreparedStatement stmt = null;
		
		try {
			// ���ؽ�Ʈ �ʱ�ȭ �Ű������� ���� �������� ServletContext ��ü�� �ʿ��ϴ�.
			ServletContext sc = this.getServletContext();
			MySqlMemberDao memberDao = (MySqlMemberDao)sc.getAttribute("memberDao");
			
			// ����Ʈ ��Ʈ�ѷ� ����
//			memberDao.update(new Member()
//					.setNo(Integer.parseInt(req.getParameter("no")))
//					.setName(req.getParameter("name"))
//					.setEmail(req.getParameter("email")));
			
			Member member = (Member) req.getAttribute("member");
			memberDao.update(member);
			
			req.setAttribute("viewUrl", "redirect:list.do");
//			res.sendRedirect("list");
		} catch(Exception e) {
			throw new ServletException(e);
//			e.printStackTrace();
//			req.setAttribute("Error", e);
//			RequestDispatcher rd = req.getRequestDispatcher("/Error.jsp");
//			rd.forward(req, res);
		}
	}
}
