import java.util.ListResourceBundle;

public class MyResourceBundle extends ListResourceBundle {
	@Override
	protected Object[][] getContents() {
		return new Object[][] {
			{"OK", "Ȯ��"},
			{"Cancel", "���"},
			{"Reset", "�缳��"},
			{"Submit", "����"}
		};
	}

}
