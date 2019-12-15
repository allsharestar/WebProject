import java.util.ListResourceBundle;

public class MyResourceBundle extends ListResourceBundle {
	@Override
	protected Object[][] getContents() {
		return new Object[][] {
			{"OK", "확인"},
			{"Cancel", "취소"},
			{"Reset", "재설정"},
			{"Submit", "제출"}
		};
	}

}
