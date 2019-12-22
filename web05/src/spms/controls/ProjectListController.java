package spms.controls;

import java.util.HashMap;
import java.util.Map;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.dao.MySqlProjectDao;

@Component("/project/list.do")
public class ProjectListController implements Controller, DataBinding {
	MySqlProjectDao projectDao;
	
	public ProjectListController setProjectDao(MySqlProjectDao projectDao) {
		this.projectDao = projectDao;
		return this;
	}

	@Override
	public Object[] getDataBinders() {
		return new Object[] {"orderCond", String.class};
	}

	@Override
	public String execute(Map<String, Object> model) throws Exception {
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("orderCond", model.get("orderCond"));
		
		model.put("projects", projectDao.selectList(paramMap));
		return "/project/ProjectList.jsp";
	}
}
