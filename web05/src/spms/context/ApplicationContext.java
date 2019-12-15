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

// ApplicationContext를 만든 이유는 페이지 컨트롤러나 DAO가 추가되더라도 ContextLoaderListener를 변경하지 않기 위함입니다.
public class ApplicationContext {
	// 프로퍼티에 설정된 대로 객체를 준비하면, 객체를 저장할 보관소가 필요한데 이를 위해 해시 테이블을 준비합니다.
	Hashtable<String, Object> objTable = new Hashtable<String, Object>();
	// 또한, 해시 테이블에서 객체를 꺼낼(getter) 메서드도 정의합니다.
	public Object getBean(String key) {
		return objTable.get(key);
	}
	
	// ApplicationContext 생성자가 호출되면 매개변수로 지정된 프로퍼티 파일의 내용을 로딩해야 합니다. 이를 위해 Properties클래스를 사용했습니다.
	public ApplicationContext(String propertiesPath) throws Exception {
		// Properties는 '이름=값' 형태로 된 파일을 다룰 때 사용하는 클래스입니다.
		Properties props = new Properties();
		// Properties의 load메서드는 FileReader를 통해 읽어들인 프로퍼티 내용을 키-값 형태로 내부 맵에 보관합니다.
		// 키 : jndi.dataSource, 값 : java:comp/env/jdbc/studydb
		// 키와 값은 WEB-INF/application-context.properties에 정의해놨음
		props.load(new FileReader(propertiesPath));
		
		// 프로퍼티 파일의 내용을 로딩했으면, 그에 따라 객체를 준비해야하는데 prepareObjects가 그 일을 수행하는 메서드이다.
		prepareObjects(props);
		// 애노테이션이 붙은 클래스를 찾아서 객체를 준비하는 것이 추가되었다. 밑에 메서드가 수행함
		prepareAnnotationObjects();
		injectDependency();
	}
	
	// 이 메서드는 자바 classpath를 뒤져서 @Component 애노테이션이 붙은 클래스를 찾습니다.
	// 그 객체를 생성하여 객체 테이블에 담는 일을 합니다. 이 작업을 위해 'Reflections라는 오픈 소스 라이브러리 활요했음
	private void prepareAnnotationObjects() throws Exception {
		// 오픈소스임. 우리가 원하는 클래스를 찾아 주는 도구입니다.
		// 생성자에 넘겨 주는 매개변수 값은 클래스를 찾을 때 출발하는 패키지입니다.
		// 만약 매개변수가 spms라면 spms패키지 및 그 하위 패키지를 모두 뒤집니다.
		// 빈 문자열을 넣었음. 즉 자바 classpath에 있는 모든 패키지를 검색하라는 뜻입니다.
		Reflections reflector = new Reflections("");
		
		// Reflections의 getTypesAnnotatedWith메서드를 사용하면 애노테이션이 붙은 클래스를 찾을 수 있습니다.
		// 이 메서드의 매개변수 값은 애노테이션의 클래스(java.lang.Class 객체)입니다.
		// @Component 애노테이션이 붙은 클래스를 찾고 싶으면 앞의 코드처럼 애노테이션 클래스를 지정하면 됩니다.
		// 반환되는 값은 @Component 애노테이션이 선언된 클래스 목록입니다.
		Set<Class<?>> list = reflector.getTypesAnnotatedWith(Component.class);
		String key = null;
		
		for(Class<?> clazz : list) {
			// getAnnotation()을 통해 클래스로부터 애노테이션을 추출합니다.
			// 애노테이션에 정의된 메서드를 호출하여 속성값을 꺼낼 수 있습니다. 다음과 같이 value()를 호출합니다.
			key = clazz.getAnnotation(Component.class).value();
			// 이렇게 애노테이션을 통해 알아낸 객체 이름(key)으로 인스턴스를 저장합니다.
			objTable.put(key, clazz.newInstance());
		}
	}
	
	private void prepareObjects(Properties props) throws Exception {
		// 먼저 JNDI 객체를 찾을 때 사용할 InitialContext를 준비합니다.
		Context ctx = new InitialContext();
		String key = null;
		String value = null;
		
		// 반복문을 통해 프로퍼티에 들어있는 정보를 꺼내서 객체를 생성합니다.
		// Properties로부터 클래스 이름을 꺼내려면 키(key)를 알아야 합니다.
		// keySet() 메서드는 Properties에 저장된 키 목록을 반환합니다.
		// 키 : jndi.dataSource, 값 : java:comp/env/jdbc/studydb
		for(Object item : props.keySet()) {
			key = (String)item;
			value = props.getProperty(key);
			// 만약 키가 "jndi."로 시작한다면 객체를 생성하지 않고, InitialContext를 통해 얻습니다.
			// InitialContext의 lookup메서드는 JNDI 인터페이스를  통해 톰캣 서버에 등록된 객체를 찾습니다.
			if(key.startsWith("jndi.")) objTable.put(key, ctx.lookup(value));
			// 그 밖의 객체는 Class.forName을 호출하여 클래스를 로딩하고, newInstance를 사용하여 인스턴스를 생성합니다.
			else objTable.put(key, Class.forName(value).newInstance());
			// 이렇게 생성한 객체는 객체테이블 objTable에 저장됩니다.
			// ex) 키 "memberDao", 값 "0x200", 0x200 = MySqlMemberDao 인스턴스의 주소(0x200) 보관
		}
	}
	
	
	// 톰캣 서버로부터 객체를 가져오거나 (예: DataSource) 직접 객체를 생성했으면(예: Memberdao) 이제는 각 객체가 필요로 하는 의존 객체를 할당해 주어야 합니다.
	// 이런일을 하는 메서드가 injectDependency이다.
	private void injectDependency() throws Exception {
		for(String key : objTable.keySet()) {
			// 객체 이름이 "jndi."로 시작하는 경우 톰캣 서버에서 제공한 객체이므로 의존 객체를 주입해서는 안 된다.
			// 그래서 제외하고 나머지 객체에 대해서는 셋터 메서드는 호출합니다. 즉 셋터 메서드가 원하는 객체를 할당합니다.
			// callSetter는 매개변수로 주어진 객체에 대해 셋터 메서드를 찾아서 호출하는 일을 합니다.
			if(!key.startsWith("jndi,")) callSetter(objTable.get(key));
		}
	}
	
	private void callSetter(Object obj) throws Exception {
		Object dependency = null;
		
		for(Method m : obj.getClass().getMethods()) {
			if(m.getName().startsWith("set")) {
				// 셋터 메서드를 찾았으면 셋터 메서드의 매개변수와 타입이 일치하는 객체를 objTable에서 찾습니다.
				dependency = findObjectByType(m.getParameterTypes()[0]);
				// 의존 객체를 찾았으면, 셋터 메서드를 호출합니다. m.invoke 해당 메서드 실행
				if(dependency != null) m.invoke(obj, dependency);
			}
		}
	}
	
	// 이 메서드는 셋터 메서드를 호출할 때 넘겨줄 의존 객체를 찾는 일을합니다.
	private Object findObjectByType(Class<?> type) {
		// objTable에 들어 있는 객체를 모두 뒤집니다.
		for(Object obj : objTable.values()) {
			// 만약 셋터 메서드의 매개변수 타입과 일치하는 객체를 찾았다면 그 객체릐 주소를 리턴합니다.
			// Class의 isInstance메서드는 주어진 객체가 해당 클래스 또는 인터페이스의 인스턴스인지 검사합니다.
			if(type.isInstance(obj)) return obj;
		}
		return null;
	}
}
