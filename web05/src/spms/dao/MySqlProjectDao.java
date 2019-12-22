package spms.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import spms.annotation.Component;
import spms.vo.Project;

@Component("projectDao")
public class MySqlProjectDao implements ProjectDao{
	// JDBC���� �ҽ� ���� ����
	// �������� �����ͺ��̽� Ŀ�ؼ��� ��� ���ؼ� DataSource ��ü�� �ʿ������� mybatis�� ����ϸ� �� �̻� �ʿ��������.
	// ��� SqlSessionFactory��ü�� ���� �޼��带 �����Ѵ�. SqlSessionFactory�� SQL�� ������ �� ����� ������ ����� �ش�.
	// SqlSession�� SQL�� �����ϴ� �����̴�. �� ��ü�� SQL���� �����Ѵ�. ���� ��ü�� ������ �� ���� SqlSessionFactory�� ���ؼ��� ���� �� �ִ�.
	// sqlSession = sqlSessionFactory.openSession()
	SqlSessionFactory sqlSessionFactory;
	
	// SqlSession�� �ֿ�޼���
	// selectList() select���� ����, �� ��ü ����� ��ȯ��
	// selectOne() select���� ����, �ϳ��� �� ��ü�� ��ȯ��
	// insert() insert���� ����, ��ȯ���� �Է��� �������� ����
	// update() update���� ����, ��ȯ���� ������ �������� ����
	// delete() delete���� ����, ��ȯ���� ������ �������� ����

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	@Override
	public List<Project> selectList(HashMap<String, Object> paramMap) throws Exception {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			// �Ű������� SQL���̵��̴�. spms.dao.ProjectDao�� SQL������ ���ӽ����̽� �̸��̰�
			// selectList�� SQL ���� ���Ͽ��� selectList��� ���̵� ���� select�±׸� ����Ű�� ���̴�.
			return sqlSession.selectList("spms.dao.ProjectDao.selectList", paramMap);
		} finally {
			// DBĿ�ؼ�ó�� ��� �Ŀ��� �ݾƾ��մϴ�.
			sqlSession.close();
		}
	}

	@Override
	public int insert(Project project) throws Exception { // ������Ʈ ���
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			int count = sqlSession.insert("spms.dao.ProjectDao.insert", project);
			sqlSession.commit();
			return count;
		} finally {
			sqlSession.close();
		}
	}
	
	public Project selectOne(int no) throws Exception {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			return sqlSession.selectOne("spms.dao.ProjectDao.selectOne", no);
		} finally {
			sqlSession.close();
		}
	}
	
	public int update(Project project) throws Exception {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			Project original = sqlSession.selectOne("spms.dao.ProjectDao.selectOne", project.getNo());
			
			Hashtable<String, Object> paramMap = new Hashtable<String, Object>();
			
			if(!project.getTitle().equals(original.getTitle())) paramMap.put("title", project.getTitle());
			if(!project.getContent().equals(original.getContent())) paramMap.put("content", project.getContent());
			if(project.getStartDate().compareTo(original.getStartDate()) != 0) paramMap.put("startDate", project.getStartDate());
			if(project.getEndDate().compareTo(original.getEndDate()) != 0) paramMap.put("endDate", project.getEndDate());
			if(project.getState() != original.getState()) paramMap.put("state", project.getState());
			if(!project.getTags().equals(original.getTags())) paramMap.put("tags", project.getTags());
			
			if(paramMap.size() > 0) paramMap.put("no", project.getNo());
			
			int count = sqlSession.update("spms.dao.ProjectDao.update", paramMap);
			sqlSession.commit();
			return count;
		} finally {
			sqlSession.close();
		}
	}
	
	@Override
	public int delete(int no) throws Exception {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			int count = sqlSession.delete("spms.dao.ProjectDao.delete", no);
			sqlSession.commit();
			return count;
		} finally {
			sqlSession.close();
		}
	}
}
