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
	
	// ���� ����Ʈ ��Ʈ�ѷ����� ����ؾ� �ϹǷ� Ŭ���� �̸������� ȣ���� �� �ְ� static���� �����Ѵ�.
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			// ���� �ڵ�� ������Ƽ�� �ֳ����̼� ������ ��ü�� �����߱� ������ ApplicationContext�� ��� ó���Ͽ����ϴ�.
			// ������ �ڵ�
			// ServletContext sc = event.getServletContext()
			// String propertiesPath = sc.getRealPath(sc.getInitParameter("contextConfigLocation"))
			// applicationContext = new ApplicationContext(propertiesPath)
			// ������ ApplicationContext�����ڿ� ������Ƽ ������ ��θ� �Ѱ��ָ� ������Ƽ ���Ͽ� ����� ��ü �Ӹ��ƴ϶� @Component �ֳ����̼��� ���� Ŭ������ ã�Ƽ� ��ü�� �����߽��ϴ�.
			
			// SqlSessionFactory ��ü�� ������ �����ؼ� ����ؾ� �ϱ� ������ ������ ���ó�� ��ü ������ ���� ��ü ������ �����ڿ��� �ϰ�ó�� �� �� �����ϴ�.
			// ���� ApplicationContext ��ü�� ������ �� �⺻ �����ڸ� ȣ���ϵ��� �����Ͽ����ϴ�.
			applicationContext = new ApplicationContext();
			
			// mybatis-config.xml�� SqlSessionFactory�� ������ �� ����� ���赵���̴�.
			// build()�� �Ű����� ������ �� ������ �Է� ��Ʈ���� �Ѱ���� �մϴ�.
			// mybatis ���� ������ ���� �ڹ� Ŭ���� ���(CLASSPATH)�� �Ӵϴ�.
			// �� ������ �Է� ��Ʈ���� ��� ���� mybatis���� �����ϴ� Resources Ŭ������ ����ߴ�.
			String resource = "spms/dao/mybatis-config.xml";
			// Resources�� getResourceAsStream() �޼��带 �̿��ϸ� �ڹ� Ŭ���� ��ο� �ִ� ������ �Է� ��Ʈ���� �ս��� ���� �� �ֽ��ϴ�.
			InputStream inputStream = Resources.getResourceAsStream(resource);
			// SqlSessionFactory�� new �����ڷ� ��ü�� ������ �� �����ϴ�. ���� ������ ������� ��ü�� �����մϴ�.
			// SqlSession ��ü�� ����� ���� ��ü�Դϴ�. �� ��ü�� ������� SqlSessionFactoryBuilder Ŭ������ �̿��ؾ� �մϴ�. build�� ȣ���ؾ���
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
			
			// SqlSessionFactory ��ü�� ���������� ApplicationContext�� ����ؾ��մϴ�. �׷��߸� DAO�� ������ �� �ֽ��ϴ�.
			// ApplicationContext�� �߰��� addBean()�� ȣ���Ͽ� SqlSessionFactory��ü�� ����Ͽ����ϴ�.
			applicationContext.addBean("sqlSessionFactory", sqlSessionFactory);
			
			ServletContext sc = event.getServletContext();
			String propertiesPath = sc.getRealPath(sc.getInitParameter("contextConfigLocation"));
			
			// ���� ������Ƽ ������ ���뿡 ���� ��ü�� �����ϵ��� ApplicationContext�� �����մϴ�.
			applicationContext.prepareObjectsByProperties(propertiesPath);
			
			// @Component �ֳ����̼��� ���� Ŭ������ ã�� ��ü�� �����մϴ�.
			applicationContext.prepareObjectsByAnnotation("");
			
			// ���������� ApplicationContext���� �����ϴ� ��ü���� �����Ͽ� ���� ��ü�� �����մϴ�.
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
