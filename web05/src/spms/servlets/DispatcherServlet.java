package spms.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import spms.bind.DataBinding;
import spms.bind.ServletRequestDataBinder;
import spms.controls.Controller;
import spms.controls.LogInController;
import spms.controls.LogOutController;
import spms.controls.MemberAddController;
import spms.controls.MemberDeleteController;
import spms.controls.MemberListController;
import spms.controls.MemberUpdateController;
import spms.vo.Member;

// ����Ʈ ��Ʈ�ѷ�
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet{
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		String servletPath = request.getServletPath();
		try {
			ServletContext sc = this.getServletContext();
			
			// �� ��ü �غ� MemberListController�� ����� ��ü�� �غ��ϸ� Map��ü�� ��� ����
			HashMap<String, Object> model = new HashMap<String, Object>();
			model.put("session", request.getSession());
			
			// ServletContext�����ҿ� ����� MemberDao ��ü�� ������ Map��ü�� ����
//			model.put("memberDao", sc.getAttribute("memberDao"));
			// ContextLoaderListener���� MemberDao��ü�� ���� ��������
			
			
			// ������ ��Ʈ�ѷ��� Controller�� ����ü�̱� ������, �������̽� Ÿ���� ���� ������ ����
			Controller pageController = (Controller)sc.getAttribute(servletPath);
			
			
			// �ؿ� if ~ else���� �ڵ�ȭ ������ ����
			// DataBinding�� �����ߴ��� ���θ� �˻����Ͽ�, �ش� �������̽��� ������ ��쿡�� prepareRequestDataȣ��
			if(pageController instanceof DataBinding) {
				prepareRequestData(request, model, (DataBinding)pageController);
			}
			
			// if ~ else ���ǹ����� ������ ��Ʈ�ѷ��� ����� �����͸� �غ��ϴ� �κ��� �����ϰ�� ��� ����
			// �غ��ʹ� pageController �����Ұ���
//			if("/member/list.do".equals(servletPath)) {
//				�� �κ��� ContextLoaderListener�� ���ǰ� �Ǿ������Ƿ� �ʿ����
//				pageController = new MemberListController();
//			} 
//			if("/member/add.do".equals(servletPath)) {
//				if(request.getParameter("email") != null) {
//					model.put("member", new Member()
//							.setEmail(request.getParameter("email"))
//							.setPassword(request.getParameter("password"))
//							.setName(request.getParameter("name")));
//				}
//			} else if("/member/update.do".equals(servletPath)) {
//				if(request.getParameter("email") != null) {
//					model.put("member", new Member()
//							.setNo(Integer.parseInt(request.getParameter("no")))
//							.setEmail(request.getParameter("email"))
//							.setName(request.getParameter("name")));
//				} else {
//					model.put("no", Integer.parseInt(request.getParameter("no")));
//				}
//			} else if("/member/delete.do".equals(servletPath)) {
//				model.put("no", Integer.parseInt(request.getParameter("no")));
//			} else if("/auth/login.do".equals(servletPath)) {
//				if(request.getParameter("email") != null) {
//					model.put("loginInfo", new Member()
//							.setEmail(request.getParameter("email"))
//							.setPassword(request.getParameter("password")));
//				}
//			}
			
			// ���������� ��Ŭ������� ������ �����߾����� ������ �Ϲ� Ŭ�����̱� ������ �޼���� ȣ��
			String viewUrl = pageController.execute(model);
			
			// Map��ü�� ������ ��Ʈ�ѷ����� �����ͳ� ��ü�� ���� �� ���Ǳ⵵ ������
			// ������ ��Ʈ�ѷ��� ���� ������� ���� ���� ����մϴ�.
			// ���� ������ ��Ʈ�ѷ��� ������ ���� ����, Map ��ü�� �����Ǿ� �ִ� ���泪 ��ü�� JSP�� ����� �� �ֵ��� ServletRequest�� �����մϴ�.
			for(String key : model.keySet()) {
				request.setAttribute(key, model.get(key));
			}
			
			// JSP�� ������ �����ϰų� �����̷�Ʈ�� ���� �ڵ��
			if(viewUrl.startsWith("redirect:")) {
				response.sendRedirect(viewUrl.substring(9));
				return;
			} else {
				RequestDispatcher rd = request.getRequestDispatcher(viewUrl);
				rd.include(request, response);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			request.setAttribute("error", e);
			RequestDispatcher rd = request.getRequestDispatcher("/Error.jsp");
			rd.forward(request, response);
		}
	}
	
	private void prepareRequestData(HttpServletRequest request, HashMap<String, Object> model, DataBinding dataBinding) throws Exception {
		// �켱 ������ ��Ʈ�ѷ����� �ʿ��� �����Ͱ� �������� �����ϴ�.
		// getDataBinders�� ��ȯ�ϴ� ���� ["�������̸�", ������Ÿ��, "�������̸�", ������Ÿ��, ...]���� ������ Object�迭
		Object[] dataBinders = dataBinding.getDataBinders();
		// �迭�� �ݺ��ϱ� ���� �迭���� ���� ���� ������ �ӽ� ������ �غ��մϴ�.
		// �������̸�(String), ������Ÿ��(Class), �����Ͱ�ü(Object)�� ���� ���� ������
		String dataName = null;
		Class<?> dataType = null;
		Object dataObj = null;
		
		// ������ �̸��� ������ Ÿ���� ������ ���� 2�� �����ϸ鼭 �ݺ����� �����ϴ�.
		for(int i = 0; i < dataBinders.length; i+=2) {
			dataName = (String)dataBinders[i];
			dataType = (Class<?>)dataBinders[i+1];

			// bind�޼���� dataName�� ��ġ�ϴ� ��û �Ű������� ã��, dataType�� ���� �ش� Ŭ������ �ν��Ͻ��� �����մϴ�.
			// ã�� �Ű����� ���� �ν��Ͻ��� �����ϸ� �� �ν��Ͻ��� ��ȯ�մϴ�.
			dataObj = ServletRequestDataBinder.bind(request, dataType, dataName);
			
			// bind�޼��尡 ��ȯ�� ������ ��ü�� Map��ü�� ����ϴ�. �� �۾��� ���� ������ ��Ʈ�ѷ��� ����� �����͸� �غ��ϴ� �� �Դϴ�.
			model.put(dataName, dataObj);
		}
	}
}
