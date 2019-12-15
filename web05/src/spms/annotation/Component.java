package spms.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// 애노테이션을 정의할 때 애노테이션의 유지 정책을 지정해야한다.
// 유지정책이란 애노테이션 정보를 언제까지 유지할 것인지 설정하는 문법입니다.
// RUNTIME으로 지정했기 때문에, 실행 중에도 언제든지 @Component 애노테이션의 속성값을 참조할 수 있습니다.
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
	// 객체 이름을 저장하는 용도로 사용할 'value'라는 기본 속성을 정의합니다.
	// value속성은 값을 설정할 때 이름을 생략할 수 있는 특별한 기능이 있습니다.
	// 애노테이션의 속성을 선언하는 문법은 인터페이스에서 메서드를 선언하는 문법과 비슷합니다.
	// 그러나 인터페이스의 메서드와 달리 '기본값'을 지정할 수 있습니다.
	// 속성 선언 다음에 오는 'default'키워드가 기본값을 지정하는 문법입니다.
	// 즉 value속성의 값을 지정하지 않으면 default로 지정한 값(밑에는 빈 문자열)이 할당됩니다.
	String value() default "";
}
