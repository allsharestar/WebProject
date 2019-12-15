package spms.context;

import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

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
		injectDependency();
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
	
	private void injectDependency() throws Exception {
		for(String key : objTable.keySet()) {
			if(!key.startsWith("jndi,")) callSetter(objTable.get(key));
		}
	}
	
	private void callSetter(Object obj) throws Exception {
		Object dependency = null;
		
		for(Method m : obj.getClass().getMethods()) {
			if(m.getName().startsWith("set")) {
				dependency = findObjectByType(m.getParameterTypes()[0]);
				if(dependency != null) m.invoke(obj, dependency);
			}
		}
	}
	
	private Object findObjectByType(Class<?> type) {
		for(Object obj : objTable.values()) {
			if(type.isInstance(obj)) return obj;
		}
		return null;
	}
}
