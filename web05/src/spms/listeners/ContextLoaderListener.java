package spms.listeners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import spms.controls.LogInController;
import spms.controls.LogOutController;
import spms.controls.MemberAddController;
import spms.controls.MemberDeleteController;
import spms.controls.MemberListController;
import spms.controls.MemberUpdateController;
import spms.dao.MemberDao;
import spms.dao.MySqlMemberDao;
import spms.util.DBConnectionPool;

@WebListener
public class ContextLoaderListener implements ServletContextListener {
//	DBConnectionPool connPool;
//	BasicDataSource ds;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			ServletContext sc = event.getServletContext();
			
//			Class.forName(sc.getInitParameter("driver"));
//			connPool = new DBConnectionPool(
//					sc.getInitParameter("driver"), 
//					sc.getInitParameter("url"), 
//					sc.getInitParameter("username"), 
//					sc.getInitParameter("password"));
//			ds = new BasicDataSource();
//			ds.setDriverClassName(sc.getInitParameter("driver"));
//			ds.setUrl(sc.getInitParameter("url"));
//			ds.setUsername(sc.getInitParameter("username"));
//			ds.setPassword(sc.getInitParameter("password"));
			
			InitialContext initialContext = new InitialContext();
			DataSource ds = (DataSource)initialContext.lookup("java:comp/env/jdbc/studydb");
			
			// MemberDao�� ���� �������̽��̱� ������ �ν��Ͻ��� ������ �� �����ϴ�.
//			MemberDao memberDao = new MemberDao();
			// ���� ������ ��Ʈ�ѷ��� ���ԵǴ� ���� MySqlMemberDao��
			MySqlMemberDao memberDao = new MySqlMemberDao();
//			memberDao.setDBConnectionPool(connPool);
			memberDao.setDataSource(ds);
			
			sc.setAttribute("/auth/login.do", new LogInController().setMemberDao(memberDao));
			sc.setAttribute("/auth/logout.do", new LogOutController());
			sc.setAttribute("/member/list.do", new MemberListController().setMemberDao(memberDao));
			sc.setAttribute("/member/add.do", new MemberAddController().setMemberDao(memberDao));
			sc.setAttribute("/member/update.do", new MemberUpdateController().setMemberDao(memberDao));
			sc.setAttribute("/member/delete.do", new MemberDeleteController().setMemberDao(memberDao));
			
			// ������ ������ ����� ���� ���� ������ ServletContext�� �������� �ʽ��ϴ�.
//			sc.setAttribute("memberDao", memberDao);
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent event) {
//		connPool.closeAll();
//		try {if(ds != null) ds.close();} catch(SQLException e) {}
	}
}
