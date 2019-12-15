package spms.bind;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Set;

import javax.servlet.ServletRequest;

public class ServletRequestDataBinder {
	
	// 프런트 컨트롤러에서 호출하는 메서드, 요청 매개변수의 값과 데이터 이름, 데이터 타입을 받아서 데이터 객체(예: Member, String, Date, Integer 들)를 만드는 일을 한다.
	public static Object bind(ServletRequest request, Class<?> dataType, String dataName) throws Exception {
		// dataType이 기본 타입인지 아닌지 검사하는 일입니다. 만약 기본 타입이라면 즉시 객체를 생성하여 반환한다.
		// isPrimitiveType메서드는 int, long, float, double, boolean, java.util.Date, java.lang.String 타입에 대해 기본 타입으로 간주하여 true를 반환
		if(isPrimitiveType(dataType)) {
			// 기본 타입의 객체를 생성할 때 호출, 요청 매개변수의 값으로부터 String이나 Date등의 기본 타입 객체를 생성
			return createValueObject(dataType, request.getParameter(dataName));
		}
		
		// Member 클래스처럼 dataType이 기본 타입이 아닌 경우는 요청 매개변수의 이름과 일치하는 셋터 메서드를 찾아서 호출합니다.
		// 먼저 요청 매개변수의 이름 목록을 얻습니다.
		// request.getParameterMap은 매개변수의 이름과 값을 맵 객체에 담아서 반환합니다.
		// 우리가 필요한 것은 매개변수의 이름이기 때문에 Map의 keySet을 호출해 이름 목록만 꺼냅니다.
		Set<String> paramNames = request.getParameterMap().keySet();
		// 그리고 값을 저장할 객체를 생성합니다. Class의 newInstance를 사용하면 해당 클래스의 인스턴스를 얻을 수 있습니다.
		// new 연산자를 사용하지 않고도 이런 식으로 객체를 생성할 수 있습니다.
		Object dataObject = dataType.newInstance();
		
		Method m = null;
		
		for(String paramName : paramNames) {
			// 데이터 타입 클래스에서 매개변수 이름과 일치하는 프로퍼티(셋터 메서드)를 찾습니다.
			// 내부에 선언된 메서드로 데이터타입(Class)과 매개변수 이름(String)을 주면 셋터 메서드를 찾아서 반환합니다.
			m = findSetter(dataType, paramName);
			// 셋터 메서드를 찾았으면 이전에 생성한 dataObject에 대해 호출합니다.
			// 셋터 메서드를 호출할 때 요청 매개변수의 값을 그 형식에 맞추어 넘깁니다.
			// createValueObject(
			// 	m.getParameterTypes()[0], // 셋터 메서드의 매개변수 타입
			// 	request.getParaeter(paramName) // 요청 매개변수의 값
			// )
			// createValueObject메서드는 요청 매개변수의 값을 가지고 기본 타입의 객체를 만들어 줍니다.
			// 이렇게 요청 매개변수의 개수만큼 반복하면서, 데이터 객체(예:Member)에 대해 값을 할당합니다.
			if(m != null) {
				m.invoke(dataObject, createValueObject(m.getParameterTypes()[0], request.getParameter(paramName)));
			}
		}
		
		return dataObject;
	}
	
	// 매개변수로 주어진 타입이 기본 타입인지 검사하는 메서드입니다. 조건문을 이용하여 데이터 타입이 int인지 아니면 Integer 클래스인지 등을 검사합니다.
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
	
	// 기본 타입의 경우 셋터 메서드가 없기 때문에 값을 할당할 수 없습니다. 보통 생성자를 호출할 때 값을 할당합니다.
	// 그래서 createValueObject메서드를 만든 것이다. 이 메서드는 셋터로 값을 할당할 수 없는 기본 타입에 대해 객체를 생성하는 메서드입니다.
	private static Object createValueObject(Class<?> type, String value) {
		if(type.getName().equals("int") || type == Integer.class) return new Integer(value);
		else if(type.getName().equals("float") || type == Float.class) return new Float(value);
		else if(type.getName().equals("double") || type == Double.class) return new Double(value);
		else if(type.getName().equals("long") || type == Long.class) return new Long(value);
		else if(type.getName().equals("boolean") || type == Boolean.class) return new Boolean(value);
		else if(type == Date.class) return java.sql.Date.valueOf(value);
		else return value;
	}
	
	// 클래스(type)을 조사하여 주어진 이름(name)과 일치하는 셋터 메서드를 찾습니다.
	private static Method findSetter(Class<?> type, String name) {
		// 제일 먼저 데이터 타입에서 메서드 목록을 얻습니다.
		Method[] methods = type.getMethods();
		
		String propName = null;
		// 메서드 목록을 반복하여 셋터 메서드에 대해서만 작업을 수행합니다. startsWith("set"), set으로 시작하지 않으면 continue
		for(Method m : methods) {
			if(!m.getName().startsWith("set")) continue;
			propName = m.getName().substring(3);
			// 셋터 메서드일 경우 요청 매개변수의 이름과 일치하는지 검사한다. 셋터 메서드의 이름에서 set은 제외하려고 substring(3)메서드 활용
			// 대소문자를 구분하지 않기 위해 모두 소문자로 바꾼 다음에 비교합니다.
			// 일치하는 셋터 메서드를 찾았다면 즉시 반환합니다. return m;
			if(propName.toLowerCase().equals(name.toLowerCase())) return m;
		}
		
		return null;
	}
}
