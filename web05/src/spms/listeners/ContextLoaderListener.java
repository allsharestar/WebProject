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

import spms.context.ApplicationContext;
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
	static ApplicationContext applicationContext;
//	DBConnectionPool connPool;
//	BasicDataSource ds;
	
	// 당장 프런트 컨트롤러에서 사용해야 하므로 클래스 이름만으로 호출할 수 있게 staticㅇ로 선언한다.
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			ServletContext sc = event.getServletContext();
			
			// 프로퍼티 파일의 이름과 경로 정보도 web.xml파일로부터 읽어 오게 처리하였습니다. ServletContext의 getInitParameter를 호출하여 web.xml에 설정된 매개변수 정보를 가져옵니다.
			String propertiesPath = sc.getRealPath(sc.getInitParameter("contextConfigLocation"));
			// 그리고 ApplicationContext 객체를 생성할 때 생성자의 매겨변수로 넘겨줍니다.
			applicationContext = new ApplicationContext(propertiesPath);
			
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
			
//			InitialContext initialContext = new InitialContext();
//			DataSource ds = (DataSource)initialContext.lookup("java:comp/env/jdbc/studydb");
			
			// MemberDao는 이제 인터페이스이기 때문에 인스턴스를 생성할 수 없습니다.
//			MemberDao memberDao = new MemberDao();
			// 이제 페이지 컨트롤러에 주입되는 것은 MySqlMemberDao임
//			MySqlMemberDao memberDao = new MySqlMemberDao();
//			memberDao.setDBConnectionPool(connPool);
//			memberDao.setDataSource(ds);
			
//			sc.setAttribute("/auth/login.do", new LogInController().setMemberDao(memberDao));
//			sc.setAttribute("/auth/logout.do", new LogOutController());
//			sc.setAttribute("/member/list.do", new MemberListController().setMemberDao(memberDao));
//			sc.setAttribute("/member/add.do", new MemberAddController().setMemberDao(memberDao));
//			sc.setAttribute("/member/update.do", new MemberUpdateController().setMemberDao(memberDao));
//			sc.setAttribute("/member/delete.do", new MemberDeleteController().setMemberDao(memberDao));
			
			// 별도로 꺼내서 사용할 일이 없기 때문에 ServletContext에 저장하지 않습니다.
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
