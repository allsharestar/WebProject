package spms.bind;

public interface DataBinding {
	// 클라이언트가 보낸 데이터가 필요한 경우
	// getDataBinders의 반환값은 데이터의 이름과 타입 정보를 담은 Object배열
	// new Object[]{"데이터이름", "데이터타입", "데이터이름", "데이터타입", ...}
	// 이름과 타입이 한 쌍으로 순서대로 오도록 작성, 배열의 크기는 항상 짝수이다.
	Object[] getDataBinders();
}
