package spms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import spms.annotation.Component;
import spms.util.DBConnectionPool;
import spms.vo.Member;

@Component("memberDao")
public class MySqlMemberDao implements MemberDao {
	SqlSessionFactory sqlSessionFactory;
	
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}
	
	public List<Member> selectList(HashMap<String, Object> paramMap) throws Exception {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			return sqlSession.selectList("spms.dao.MemberDao.selectList", paramMap);
		} finally {
			sqlSession.close();
		}
	}
	
	public int insert(Member member) throws Exception { // 회원 등록
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			int count = sqlSession.insert("spms.dao.MemberDao.insert", member);
			sqlSession.commit();
			return count;
		} finally {
			sqlSession.close();
		}
	}
	public int delete(int no) throws Exception { // 회원 삭제
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			int count = sqlSession.delete("spms.dao.MemberDao.delete", no);
			sqlSession.commit();
			return count;
		} finally {
			sqlSession.close();
		}
	}
	public Member selectOne(int no) throws Exception { // 회원 상세 정보 조회
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			return sqlSession.selectOne("spms.dao.MemberDao.selectOne", no);
		} finally {
			sqlSession.close();
		}
	}
	
	public int update(Member member) throws Exception { // 회원 정보 변경
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			Member original = sqlSession.selectOne("spms.dao.MemberDao.selectOne", member.getNo());
			
			Hashtable<String, Object> paramMap = new Hashtable<String, Object>();
			
			if(!member.getName().equals(original.getName())) paramMap.put("name", member.getName());
			if(!member.getEmail().equals(original.getEmail())) paramMap.put("email", member.getEmail());
			
			if(paramMap.size() > 0) paramMap.put("no", member.getNo());
			
			int count = sqlSession.update("spms.dao.MemberDao.update", paramMap);
			sqlSession.commit();
			return count;
		} finally {
			sqlSession.close();
		}
	}
		
	public Member exist(String email, String password) throws Exception { // 있으면 Member객체 리턴, 없으면 null 리턴
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("email", email);
		paramMap.put("password", password);
		
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			return sqlSession.selectOne("spms.dao.MemberDao.exist", paramMap);
		} finally {
			sqlSession.close();
		}
	}
}
