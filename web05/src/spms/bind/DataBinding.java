package spms.bind;

public interface DataBinding {
	// Ŭ���̾�Ʈ�� ���� �����Ͱ� �ʿ��� ���
	// getDataBinders�� ��ȯ���� �������� �̸��� Ÿ�� ������ ���� Object�迭
	// new Object[]{"�������̸�", "������Ÿ��", "�������̸�", "������Ÿ��", ...}
	// �̸��� Ÿ���� �� ������ ������� ������ �ۼ�, �迭�� ũ��� �׻� ¦���̴�.
	Object[] getDataBinders();
}
