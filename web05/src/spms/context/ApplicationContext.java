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
	
	// �ܺο��� ������ SqlSessionFactory�� ����� �� �ְ� addBean�޼��� �߰�
	// �Ű������� ��ü �̸��� ��ü �ּҸ� �޾Ƽ� objTable�� �����մϴ�.
	public void addBean(String name, Object obj) {
		objTable.put(name, obj);
	}
	
	// prepareAnnotationObjects()�� �̸��� �˱⽱�� �����Ͽ��� �Ű�����, ���� ������ ������
	// private void prepareAnnotationObjects() throws Exception {
	public void prepareObjectsByAnnotation(String basePackage) throws Exception {
		Reflections reflector = new Reflections(basePackage);
		
		Set<Class<?>> list = reflector.getTypesAnnotatedWith(Component.class);
		String key = null;
		for(Class<?> clazz : list) {
			key = clazz.getAnnotation(Component.class).value();
			objTable.put(key, clazz.newInstance());
		}
	}
	
	// ���� prepareObjects�� �̸��� ���� �̸��� ���߾� �����Ͽ����ϴ�.
	// �׸��� �� �޼��� ���� �ܺο��� ȣ���ؾ� �ϹǷ� ���������ڸ� public���� �����Ͽ���
	// �������� ������Ƽ ��ü�� ���� �޾����� ������
	// ������Ƽ ������ ��θ� �Ű������� �޾Ƽ� ���ο��� Property ��ü�� �����ϵ��� �����Ͽ����ϴ�.
	// private void prepareObjects(Properties props) throws Exception {
	public void prepareObjectsByProperties(String propertiesPath) throws Exception {
		Properties props = new Properties();
		props.load(new FileReader(propertiesPath));
		
		Context ctx = new InitialContext();
		String key = null;
		String value = null;
		
		for(Object item : props.keySet()) {
			key = (String)item;
			value = props.getProperty(key);
			if(key.startsWith("jndi.")) {
				objTable.put(key, ctx.lookup(value));
			} else {
				objTable.put(key, Class.forName(value).newInstance());
			}
		}
	}
	
	// ���� ��ü�� �����ϴ� �� �޼��嵵 �ܺο��� ȣ���� �� �ְ� private���� public���� �����Ͽ����ϴ�.
	// private void injectDependency() throws Exception {
	public void injectDependency() throws Exception {
		for(String key : objTable.keySet()) {
			if(!key.startsWith("jndi.")) {
				callSetter(objTable.get(key));
			}
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
	
	private Object findObjectByType(Class<?> type) {
		for(Object obj : objTable.values()) {
			if(type.isInstance(obj)) {
				return obj;
			}
		}
		return null;
	}
}
