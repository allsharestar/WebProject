package spms.controls;

import java.util.Map;

import spms.annotation.Component;
import spms.dao.MySqlMemberDao;

@Component("/member/list.do")
public class MemberListController implements Controller {
	MySqlMemberDao memberDao;
	
	public MemberListController setMemberDao(MySqlMemberDao memberDao) {
		this.memberDao = memberDao;
		return this;
	}

	@Override
	public String execute(Map<String, Object> model) throws Exception {
		// 외부에서 MemberDao 객체를 주입해 줄 것이기 때문에 이제 더 이상 Map객체에서 MemberDao를 꺼낼 필요가 없습니다.
//		MemberDao memberDao = (MemberDao)model.get("memberDao");
		model.put("members", memberDao.selectList());
		return "/member/MemberList.jsp";
	}

}
