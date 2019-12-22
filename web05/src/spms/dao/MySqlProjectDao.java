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
	// JDBC관련 소스 전부 삭제
	// 이전에는 데이터베이스 커넥션을 얻기 위해서 DataSource 객체가 필요했으나 mybatis를 사용하면 더 이상 필요없어진다.
	// 대신 SqlSessionFactory객체와 셋터 메서드를 선언한다. SqlSessionFactory는 SQL을 실행할 때 사용할 도구를 만들어 준다.
	// SqlSession은 SQL을 실핼하는 도구이다. 이 객체로 SQL문을 실행한다. 직접 객체를 생성할 수 없고 SqlSessionFactory를 통해서만 얻을 수 있다.
	// sqlSession = sqlSessionFactory.openSession()
	SqlSessionFactory sqlSessionFactory;
	
	// SqlSession의 주요메서드
	// selectList() select문을 실핼, 값 객체 목록을 반환함
	// selectOne() select문을 실행, 하나의 값 객체를 반환함
	// insert() insert문을 실행, 반환값은 입력한 데이터의 개수
	// update() update문을 실행, 반환값은 변경한 데이터의 개수
	// delete() delete문을 실행, 반환값은 삭제한 데이터의 개수

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	@Override
	public List<Project> selectList(HashMap<String, Object> paramMap) throws Exception {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			// 매개변수는 SQL아이디이다. spms.dao.ProjectDao는 SQL맵페의 네임스페이스 이름이고
			// selectList는 SQL 맵퍼 파일에서 selectList라는 아이디를 갖는 select태그를 가리키는 것이다.
			return sqlSession.selectList("spms.dao.ProjectDao.selectList", paramMap);
		} finally {
			// DB커넥션처럼 사용 후에는 닫아야합니다.
			sqlSession.close();
		}
	}

	@Override
	public int insert(Project project) throws Exception { // 프로젝트 등록
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
