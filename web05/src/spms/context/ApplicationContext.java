package spms.context;

import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

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
		injectDependency();
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
