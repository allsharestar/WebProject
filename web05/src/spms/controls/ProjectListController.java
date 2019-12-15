package spms.controls;

import java.util.Map;

import spms.annotation.Component;
import spms.dao.MySqlProjectDao;

@Component("/project/list.do")
public class ProjectListController implements Controller {
	MySqlProjectDao projectDao;
	
	public ProjectListController setProjectDao(MySqlProjectDao projectDao) {
		this.projectDao = projectDao;
		return this;
	}

	@Override
	public String execute(Map<String, Object> model) throws Exception {
		model.put("projects", projectDao.selectList());
		return "/project/ProjectList.jsp";
	}
}
