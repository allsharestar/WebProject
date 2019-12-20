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
	
	// 외부에서 생성한 SqlSessionFactory를 등록할 수 있게 addBean메서드 추가
	// 매개변수로 객체 이름과 객체 주소를 받아서 objTable에 보관합니다.
	public void addBean(String name, Object obj) {
		objTable.put(name, obj);
	}
	
	// prepareAnnotationObjects()의 이름을 알기쉽게 변경하였고 매개변수, 접근 제한자 변경함
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
	
	// 기존 prepareObjects의 이름을 앞의 이름에 맞추어 변경하였습니다.
	// 그리고 이 메서드 또한 외부에서 호출해야 하므로 접근지시자를 public으로 변경하였고
	// 기존에는 프로퍼티 객체를 직접 받았으나 이제는
	// 프로퍼티 파일의 경로를 매개변수로 받아서 내부에서 Property 객체를 생성하도록 변경하였습니다.
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
	
	// 의존 객체를 주입하는 이 메서드도 외부에서 호출할 수 있게 private에서 public으로 변경하였습니다.
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
				// 셋터 메서드를 찾았으면 셋터 메서드의 매개변수와 타입이 일치하는 객체를 objTable에서 찾습니다.
				dependency = findObjectByType(m.getParameterTypes()[0]);
				// 의존 객체를 찾았으면, 셋터 메서드를 호출합니다. m.invoke 해당 메서드 실행
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
