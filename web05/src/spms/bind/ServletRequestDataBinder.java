package spms.bind;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Set;

import javax.servlet.ServletRequest;

public class ServletRequestDataBinder {
	
	// ����Ʈ ��Ʈ�ѷ����� ȣ���ϴ� �޼���, ��û �Ű������� ���� ������ �̸�, ������ Ÿ���� �޾Ƽ� ������ ��ü(��: Member, String, Date, Integer ��)�� ����� ���� �Ѵ�.
	public static Object bind(ServletRequest request, Class<?> dataType, String dataName) throws Exception {
		// dataType�� �⺻ Ÿ������ �ƴ��� �˻��ϴ� ���Դϴ�. ���� �⺻ Ÿ���̶�� ��� ��ü�� �����Ͽ� ��ȯ�Ѵ�.
		// isPrimitiveType�޼���� int, long, float, double, boolean, java.util.Date, java.lang.String Ÿ�Կ� ���� �⺻ Ÿ������ �����Ͽ� true�� ��ȯ
		if(isPrimitiveType(dataType)) {
			// �⺻ Ÿ���� ��ü�� ������ �� ȣ��, ��û �Ű������� �����κ��� String�̳� Date���� �⺻ Ÿ�� ��ü�� ����
			return createValueObject(dataType, request.getParameter(dataName));
		}
		
		// Member Ŭ����ó�� dataType�� �⺻ Ÿ���� �ƴ� ���� ��û �Ű������� �̸��� ��ġ�ϴ� ���� �޼��带 ã�Ƽ� ȣ���մϴ�.
		// ���� ��û �Ű������� �̸� ����� ����ϴ�.
		// request.getParameterMap�� �Ű������� �̸��� ���� �� ��ü�� ��Ƽ� ��ȯ�մϴ�.
		// �츮�� �ʿ��� ���� �Ű������� �̸��̱� ������ Map�� keySet�� ȣ���� �̸� ��ϸ� �����ϴ�.
		Set<String> paramNames = request.getParameterMap().keySet();
		// �׸��� ���� ������ ��ü�� �����մϴ�. Class�� newInstance�� ����ϸ� �ش� Ŭ������ �ν��Ͻ��� ���� �� �ֽ��ϴ�.
		// new �����ڸ� ������� �ʰ� �̷� ������ ��ü�� ������ �� �ֽ��ϴ�.
		Object dataObject = dataType.newInstance();
		
		Method m = null;
		
		for(String paramName : paramNames) {
			// ������ Ÿ�� Ŭ�������� �Ű����� �̸��� ��ġ�ϴ� ������Ƽ(���� �޼���)�� ã���ϴ�.
			// ���ο� ����� �޼���� ������Ÿ��(Class)�� �Ű����� �̸�(String)�� �ָ� ���� �޼��带 ã�Ƽ� ��ȯ�մϴ�.
			m = findSetter(dataType, paramName);
			// ���� �޼��带 ã������ ������ ������ dataObject�� ���� ȣ���մϴ�.
			// ���� �޼��带 ȣ���� �� ��û �Ű������� ���� �� ���Ŀ� ���߾� �ѱ�ϴ�.
			// createValueObject(
			// 	m.getParameterTypes()[0], // ���� �޼����� �Ű����� Ÿ��
			// 	request.getParaeter(paramName) // ��û �Ű������� ��
			// )
			// createValueObject�޼���� ��û �Ű������� ���� ������ �⺻ Ÿ���� ��ü�� ����� �ݴϴ�.
			// �̷��� ��û �Ű������� ������ŭ �ݺ��ϸ鼭, ������ ��ü(��:Member)�� ���� ���� �Ҵ��մϴ�.
			if(m != null) {
				m.invoke(dataObject, createValueObject(m.getParameterTypes()[0], request.getParameter(paramName)));
			}
		}
		
		return dataObject;
	}
	
	// �Ű������� �־��� Ÿ���� �⺻ Ÿ������ �˻��ϴ� �޼����Դϴ�. ���ǹ��� �̿��Ͽ� ������ Ÿ���� int���� �ƴϸ� Integer Ŭ�������� ���� �˻��մϴ�.
	private static boolean isPrimitiveType(Class<?> type) {
		if(type.getName().equals("int") || type == Integer.class ||
			type.getName().equals("long") || type == Long.class ||
			type.getName().equals("float")|| type == Float.class||
			type.getName().equals("double") || type == Double.class ||
			type.getName().equals("boolean")|| type == Boolean.class||
			type == Date.class || type == String.class) {
			return true;
		}
		return false;
	}
	
	// �⺻ Ÿ���� ��� ���� �޼��尡 ���� ������ ���� �Ҵ��� �� �����ϴ�. ���� �����ڸ� ȣ���� �� ���� �Ҵ��մϴ�.
	// �׷��� createValueObject�޼��带 ���� ���̴�. �� �޼���� ���ͷ� ���� �Ҵ��� �� ���� �⺻ Ÿ�Կ� ���� ��ü�� �����ϴ� �޼����Դϴ�.
	private static Object createValueObject(Class<?> type, String value) {
		if(type.getName().equals("int") || type == Integer.class) return new Integer(value);
		else if(type.getName().equals("float") || type == Float.class) return new Float(value);
		else if(type.getName().equals("double") || type == Double.class) return new Double(value);
		else if(type.getName().equals("long") || type == Long.class) return new Long(value);
		else if(type.getName().equals("boolean") || type == Boolean.class) return new Boolean(value);
		else if(type == Date.class) return java.sql.Date.valueOf(value);
		else return value;
	}
	
	// Ŭ����(type)�� �����Ͽ� �־��� �̸�(name)�� ��ġ�ϴ� ���� �޼��带 ã���ϴ�.
	private static Method findSetter(Class<?> type, String name) {
		// ���� ���� ������ Ÿ�Կ��� �޼��� ����� ����ϴ�.
		Method[] methods = type.getMethods();
		
		String propName = null;
		// �޼��� ����� �ݺ��Ͽ� ���� �޼��忡 ���ؼ��� �۾��� �����մϴ�. startsWith("set"), set���� �������� ������ continue
		for(Method m : methods) {
			if(!m.getName().startsWith("set")) continue;
			propName = m.getName().substring(3);
			// ���� �޼����� ��� ��û �Ű������� �̸��� ��ġ�ϴ��� �˻��Ѵ�. ���� �޼����� �̸����� set�� �����Ϸ��� substring(3)�޼��� Ȱ��
			// ��ҹ��ڸ� �������� �ʱ� ���� ��� �ҹ��ڷ� �ٲ� ������ ���մϴ�.
			// ��ġ�ϴ� ���� �޼��带 ã�Ҵٸ� ��� ��ȯ�մϴ�. return m;
			if(propName.toLowerCase().equals(name.toLowerCase())) return m;
		}
		
		return null;
	}
}
