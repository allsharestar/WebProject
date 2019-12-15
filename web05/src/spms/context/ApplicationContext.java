package spms.context;

import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.reflections.Reflections;

import spms.annotation.Component;

// ApplicationContext�� ���� ������ ������ ��Ʈ�ѷ��� DAO�� �߰��Ǵ��� ContextLoaderListener�� �������� �ʱ� �����Դϴ�.
public class ApplicationContext {
	// ������Ƽ�� ������ ��� ��ü�� �غ��ϸ�, ��ü�� ������ �����Ұ� �ʿ��ѵ� �̸� ���� �ؽ� ���̺��� �غ��մϴ�.
	Hashtable<String, Object> objTable = new Hashtable<String, Object>();
	// ����, �ؽ� ���̺��� ��ü�� ����(getter) �޼��嵵 �����մϴ�.
	public Object getBean(String key) {
		return objTable.get(key);
	}
	
	// ApplicationContext �����ڰ� ȣ��Ǹ� �Ű������� ������ ������Ƽ ������ ������ �ε��ؾ� �մϴ�. �̸� ���� PropertiesŬ������ ����߽��ϴ�.
	public ApplicationContext(String propertiesPath) throws Exception {
		// Properties�� '�̸�=��' ���·� �� ������ �ٷ� �� ����ϴ� Ŭ�����Դϴ�.
		Properties props = new Properties();
		// Properties�� load�޼���� FileReader�� ���� �о���� ������Ƽ ������ Ű-�� ���·� ���� �ʿ� �����մϴ�.
		// Ű : jndi.dataSource, �� : java:comp/env/jdbc/studydb
		// Ű�� ���� WEB-INF/application-context.properties�� �����س���
		props.load(new FileReader(propertiesPath));
		
		// ������Ƽ ������ ������ �ε�������, �׿� ���� ��ü�� �غ��ؾ��ϴµ� prepareObjects�� �� ���� �����ϴ� �޼����̴�.
		prepareObjects(props);
		// �ֳ����̼��� ���� Ŭ������ ã�Ƽ� ��ü�� �غ��ϴ� ���� �߰��Ǿ���. �ؿ� �޼��尡 ������
		prepareAnnotationObjects();
		injectDependency();
	}
	
	// �� �޼���� �ڹ� classpath�� ������ @Component �ֳ����̼��� ���� Ŭ������ ã���ϴ�.
	// �� ��ü�� �����Ͽ� ��ü ���̺� ��� ���� �մϴ�. �� �۾��� ���� 'Reflections��� ���� �ҽ� ���̺귯�� Ȱ������
	private void prepareAnnotationObjects() throws Exception {
		// ���¼ҽ���. �츮�� ���ϴ� Ŭ������ ã�� �ִ� �����Դϴ�.
		// �����ڿ� �Ѱ� �ִ� �Ű����� ���� Ŭ������ ã�� �� ����ϴ� ��Ű���Դϴ�.
		// ���� �Ű������� spms��� spms��Ű�� �� �� ���� ��Ű���� ��� �����ϴ�.
		// �� ���ڿ��� �־���. �� �ڹ� classpath�� �ִ� ��� ��Ű���� �˻��϶�� ���Դϴ�.
		Reflections reflector = new Reflections("");
		
		// Reflections�� getTypesAnnotatedWith�޼��带 ����ϸ� �ֳ����̼��� ���� Ŭ������ ã�� �� �ֽ��ϴ�.
		// �� �޼����� �Ű����� ���� �ֳ����̼��� Ŭ����(java.lang.Class ��ü)�Դϴ�.
		// @Component �ֳ����̼��� ���� Ŭ������ ã�� ������ ���� �ڵ�ó�� �ֳ����̼� Ŭ������ �����ϸ� �˴ϴ�.
		// ��ȯ�Ǵ� ���� @Component �ֳ����̼��� ����� Ŭ���� ����Դϴ�.
		Set<Class<?>> list = reflector.getTypesAnnotatedWith(Component.class);
		String key = null;
		
		for(Class<?> clazz : list) {
			// getAnnotation()�� ���� Ŭ�����κ��� �ֳ����̼��� �����մϴ�.
			// �ֳ����̼ǿ� ���ǵ� �޼��带 ȣ���Ͽ� �Ӽ����� ���� �� �ֽ��ϴ�. ������ ���� value()�� ȣ���մϴ�.
			key = clazz.getAnnotation(Component.class).value();
			// �̷��� �ֳ����̼��� ���� �˾Ƴ� ��ü �̸�(key)���� �ν��Ͻ��� �����մϴ�.
			objTable.put(key, clazz.newInstance());
		}
	}
	
	private void prepareObjects(Properties props) throws Exception {
		// ���� JNDI ��ü�� ã�� �� ����� InitialContext�� �غ��մϴ�.
		Context ctx = new InitialContext();
		String key = null;
		String value = null;
		
		// �ݺ����� ���� ������Ƽ�� ����ִ� ������ ������ ��ü�� �����մϴ�.
		// Properties�κ��� Ŭ���� �̸��� �������� Ű(key)�� �˾ƾ� �մϴ�.
		// keySet() �޼���� Properties�� ����� Ű ����� ��ȯ�մϴ�.
		// Ű : jndi.dataSource, �� : java:comp/env/jdbc/studydb
		for(Object item : props.keySet()) {
			key = (String)item;
			value = props.getProperty(key);
			// ���� Ű�� "jndi."�� �����Ѵٸ� ��ü�� �������� �ʰ�, InitialContext�� ���� ����ϴ�.
			// InitialContext�� lookup�޼���� JNDI �������̽���  ���� ��Ĺ ������ ��ϵ� ��ü�� ã���ϴ�.
			if(key.startsWith("jndi.")) objTable.put(key, ctx.lookup(value));
			// �� ���� ��ü�� Class.forName�� ȣ���Ͽ� Ŭ������ �ε��ϰ�, newInstance�� ����Ͽ� �ν��Ͻ��� �����մϴ�.
			else objTable.put(key, Class.forName(value).newInstance());
			// �̷��� ������ ��ü�� ��ü���̺� objTable�� ����˴ϴ�.
			// ex) Ű "memberDao", �� "0x200", 0x200 = MySqlMemberDao �ν��Ͻ��� �ּ�(0x200) ����
		}
	}
	
	
	// ��Ĺ �����κ��� ��ü�� �������ų� (��: DataSource) ���� ��ü�� ����������(��: Memberdao) ������ �� ��ü�� �ʿ�� �ϴ� ���� ��ü�� �Ҵ��� �־�� �մϴ�.
	// �̷����� �ϴ� �޼��尡 injectDependency�̴�.
	private void injectDependency() throws Exception {
		for(String key : objTable.keySet()) {
			// ��ü �̸��� "jndi."�� �����ϴ� ��� ��Ĺ �������� ������ ��ü�̹Ƿ� ���� ��ü�� �����ؼ��� �� �ȴ�.
			// �׷��� �����ϰ� ������ ��ü�� ���ؼ��� ���� �޼���� ȣ���մϴ�. �� ���� �޼��尡 ���ϴ� ��ü�� �Ҵ��մϴ�.
			// callSetter�� �Ű������� �־��� ��ü�� ���� ���� �޼��带 ã�Ƽ� ȣ���ϴ� ���� �մϴ�.
			if(!key.startsWith("jndi,")) callSetter(objTable.get(key));
		}
	}
	
	private void callSetter(Object obj) throws Exception {
		Object dependency = null;
		
		for(Method m : obj.getClass().getMethods()) {
			if(m.getName().startsWith("set")) {
				// ���� �޼��带 ã������ ���� �޼����� �Ű������� Ÿ���� ��ġ�ϴ� ��ü�� objTable���� ã���ϴ�.
				dependency = findObjectByType(m.getParameterTypes()[0]);
				// ���� ��ü�� ã������, ���� �޼��带 ȣ���մϴ�. m.invoke �ش� �޼��� ����
				if(dependency != null) m.invoke(obj, dependency);
			}
		}
	}
	
	// �� �޼���� ���� �޼��带 ȣ���� �� �Ѱ��� ���� ��ü�� ã�� �����մϴ�.
	private Object findObjectByType(Class<?> type) {
		// objTable�� ��� �ִ� ��ü�� ��� �����ϴ�.
		for(Object obj : objTable.values()) {
			// ���� ���� �޼����� �Ű����� Ÿ�԰� ��ġ�ϴ� ��ü�� ã�Ҵٸ� �� ��ü�l �ּҸ� �����մϴ�.
			// Class�� isInstance�޼���� �־��� ��ü�� �ش� Ŭ���� �Ǵ� �������̽��� �ν��Ͻ����� �˻��մϴ�.
			if(type.isInstance(obj)) return obj;
		}
		return null;
	}
}
