package spms.controls;

import java.util.Map;

import spms.annotation.Component;
import spms.bind.DataBinding;
import spms.dao.MySqlProjectDao;
import spms.vo.Project;

@Component("/project/update.do")
public class ProjectUpdateController implements Controller, DataBinding{
	MySqlProjectDao projectDao;
	
	public ProjectUpdateController setProjectDao(MySqlProjectDao projectDao) {
		this.projectDao = projectDao;
		return this;
	}
	
	@Override
	public Object[] getDataBinders() {
		return new Object[] {"no", Integer.class, "project", Project.class};
	}

	@Override
	public String execute(Map<String, Object> model) throws Exception {
		Project project = (Project)model.get("project");
		
		if(project.getTitle() == null) {
			Integer no = (Integer)model.get("no");
			project = projectDao.selectOne(no);
			model.put("project", project);
			return "/project/ProjectUpdateForm.jsp";
		} else {
			projectDao.update(project);
			return "redirect:list.do";
		}
	}
}
