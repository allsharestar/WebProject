package spms.listeners;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

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
			// 기존 코드는 프로퍼티와 애노테이션 만으로 객체를 생성했기 때문에 ApplicationContext가 모두 처리하였습니다.
			// 변경전 코드
			// ServletContext sc = event.getServletContext()
			// String propertiesPath = sc.getRealPath(sc.getInitParameter("contextConfigLocation"))
			// applicationContext = new ApplicationContext(propertiesPath)
			// 전에는 ApplicationContext생성자에 프로퍼티 파일의 경로를 넘겨주면 프로퍼티 파일에 들옥된 객체 뿐만아니라 @Component 애노테이션이 붙은 클래스를 찾아서 객체를 생성했습니다.
			
			// SqlSessionFactory 객체를 별도로 생성해서 등록해야 하기 때문에 기존의 방식처럼 객체 생성과 의존 객체 주입을 생성자에서 일괄처리 할 수 없습니다.
			// 따라서 ApplicationContext 객체를 생성할 때 기본 생성자를 호출하도록 변경하였습니다.
			applicationContext = new ApplicationContext();
			
			// mybatis-config.xml은 SqlSessionFactory를 생성할 때 사용할 설계도면이다.
			// build()의 매개변수 값으로 이 파일의 입력 스트림을 넘겨줘야 합니다.
			// mybatis 설정 파일은 보통 자바 클래스 경로(CLASSPATH)에 둡니다.
			// 이 파일의 입력 스트림을 얻기 위해 mybatis에서 제공하는 Resources 클래스를 사용했다.
			String resource = "spms/dao/mybatis-config.xml";
			// Resources의 getResourceAsStream() 메서드를 이용하면 자바 클래스 경로에 있는 파일의 입력 스트림을 손쉽게 얻을 수 있습니다.
			InputStream inputStream = Resources.getResourceAsStream(resource);
			// SqlSessionFactory는 new 연산자로 객체를 생성할 수 없습니다. 조금 복잡한 방식으로 객체를 생성합니다.
			// SqlSession 객체를 만드는 공장 객체입니다. 이 객체를 만들려면 SqlSessionFactoryBuilder 클래스를 이용해야 합니다. build를 호출해야함
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
			
			// SqlSessionFactory 객체를 생성했으면 ApplicationContext에 등록해야합니다. 그래야만 DAO에 주입할 수 있습니다.
			// ApplicationContext에 추가된 addBean()을 호출하여 SqlSessionFactory객체를 등록하였습니다.
			applicationContext.addBean("sqlSessionFactory", sqlSessionFactory);
			
			ServletContext sc = event.getServletContext();
			String propertiesPath = sc.getRealPath(sc.getInitParameter("contextConfigLocation"));
			
			// 이제 프로퍼티 파일의 내용에 따라 객체를 생성하도록 ApplicationContext에 지시합니다.
			applicationContext.prepareObjectsByProperties(propertiesPath);
			
			// @Component 애노테이션이 붙은 클래스를 찾아 객체를 생성합니다.
			applicationContext.prepareObjectsByAnnotation("");
			
			// 마지막으로 ApplicationContext에서 관리하는 객체들을 조사하여 의존 객체를 주입합니다.
			applicationContext.injectDependency();
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
