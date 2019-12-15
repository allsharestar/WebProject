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

// 프런트 컨트롤러
@WebServlet("*.do")
public class DispatcherServlet extends HttpServlet{
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		String servletPath = request.getServletPath();
		try {
			ServletContext sc = this.getServletContext();
			
			// 맵 객체 준비 MemberListController가 사용할 객체를 준비하면 Map객체에 담아 전달
			HashMap<String, Object> model = new HashMap<String, Object>();
			model.put("session", request.getSession());
			
			// ServletContext보관소에 저장된 MemberDao 객체를 꺼내서 Map객체에 담음
//			model.put("memberDao", sc.getAttribute("memberDao"));
			// ContextLoaderListener에서 MemberDao객체를 전부 주입했음
			
			
			// 페이지 컨트롤러는 Controller의 구현체이기 때문에, 인터페이스 타입의 참조 변수로 선언
			Controller pageController = (Controller)sc.getAttribute(servletPath);
			
			
			// 밑에 if ~ else문을 자동화 시켜줄 거임
			// DataBinding을 구현했는지 여부를 검사하하여, 해당 인터페이스를 구현한 경우에만 prepareRequestData호출
			if(pageController instanceof DataBinding) {
				prepareRequestData(request, model, (DataBinding)pageController);
			}
			
			// if ~ else 조건문에서 페이지 컨트롤러가 사용할 데이터를 준비하는 부분을 제외하고는 모두 제거
			// 밑부터는 pageController 제거할거임
//			if("/member/list.do".equals(servletPath)) {
//				이 부분은 ContextLoaderListener에 정의가 되어있으므로 필요없음
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
			
			// 이전까지는 인클루딩으로 실행을 위임했었지만 이제는 일반 클래스이기 때문에 메서드로 호출
			String viewUrl = pageController.execute(model);
			
			// Map객체는 페이지 컨트롤러에게 데이터나 객체를 보낼 때 사용되기도 하지만
			// 페이지 컨트롤러의 실행 결과물을 받을 때도 사용합니다.
			// 따라서 페이지 컨트롤러의 실행이 끝난 다음, Map 객체에 보관되어 있는 데잍나 객체를 JSP가 사용할 수 있도록 ServletRequest에 복사합니다.
			for(String key : model.keySet()) {
				request.setAttribute(key, model.get(key));
			}
			
			// JSP로 실행을 위임하거나 리다이렉트를 위한 코드들
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
		// 우선 페이지 컨트롤러에게 필요한 데이터가 무엇인지 묻습니다.
		// getDataBinders가 반환하는 것은 ["데이터이름", 데이터타입, "데이터이름", 데이터타입, ...]으로 나열된 Object배열
		Object[] dataBinders = dataBinding.getDataBinders();
		// 배열을 반복하기 전에 배열에서 꺼낸 값을 보관할 임시 변수를 준비합니다.
		// 데이터이름(String), 데이터타입(Class), 데이터객체(Object)를 위한 참조 변수들
		String dataName = null;
		Class<?> dataType = null;
		Object dataObj = null;
		
		// 데이터 이름과 데이터 타입을 꺼내기 쉽게 2씩 증가하면서 반복문을 돌립니다.
		for(int i = 0; i < dataBinders.length; i+=2) {
			dataName = (String)dataBinders[i];
			dataType = (Class<?>)dataBinders[i+1];

			// bind메서드는 dataName과 일치하는 요청 매개변수를 찾고, dataType을 통해 해당 클래스의 인스턴스를 생성합니다.
			// 찾은 매개변수 값을 인스턴스에 저장하며 그 인스턴스를 반환합니다.
			dataObj = ServletRequestDataBinder.bind(request, dataType, dataName);
			
			// bind메서드가 반환함 데이터 객체는 Map객체에 답습니다. 이 작업을 통해 페이지 컨트롤러가 사용할 데이터를 준비하는 것 입니다.
			model.put(dataName, dataObj);
		}
	}
}
