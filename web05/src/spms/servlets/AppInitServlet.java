package spms.servlets;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class AppInitServlet extends HttpServlet{

	@Override
	public void init(ServletConfig config) throws ServletException {
		System.out.println("AppInitServlet �غ�..");
		super.init(config);
		
		try {
			ServletContext sc = this.getServletContext();
			Class.forName(sc.getInitParameter("driver"));
			Connection conn = DriverManager.getConnection(
					sc.getInitParameter("url"),
					sc.getInitParameter("username"),
					sc.getInitParameter("password"));
			sc.setAttribute("conn", conn);
			System.out.println("DBĿ�ؼ� �غ� ĭ��");
		} catch(Throwable e) {
			throw new ServletException(e);
		} 
	}
	
	@Override
	public void destroy() {
		System.out.println("AppInitServlet ������..");
		super.destroy();
		Connection conn = (Connection)this.getServletContext().getAttribute("conn");
		try {if(conn != null && !conn.isClosed()) conn.close(); } catch(Exception e) {}
	}
}
