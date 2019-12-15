package spms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import spms.util.DBConnectionPool;
import spms.vo.Member;

public class MySqlMemberDao implements MemberDao {
//	DBConnectionPool connPool;
	DataSource ds;
	
	public void setDataSource(DataSource ds) {
		this.ds = ds;
	}
	
//	public void setDBConnectionPool(DBConnectionPool connPool) {
//		this.connPool = connPool;
//	}
	
	public List<Member> selectList() throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ds.getConnection();
//			conn = connPool.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select MNO, MNAME, EMAIL, CRE_DATE from MEMBERS order by MNO ASC");
			
			ArrayList<Member> members = new ArrayList<Member>();
			
			while(rs.next()) {
				members.add(new Member()
						.setNo(rs.getInt("MNO"))
						.setName(rs.getString("MNAME"))
						.setEmail(rs.getString("EMAIL"))
						.setCreatedDate(rs.getDate("CRE_DATE"))
						);
			}
			return members;
		} catch (Exception e) {
			throw e;
		} finally {
			try {if(rs != null) rs.close();} catch(Exception e) {}
			try {if(stmt != null) stmt.close();} catch(Exception e) {}
//			if(conn != null) connPool.returnConnection(conn);
			try {if(conn != null) conn.close();} catch(Exception e) {}
		}
	}
	
	public int insert(Member member) throws Exception { // 회원 등록
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
//			conn = connPool.getConnection();
			conn = ds.getConnection();
			stmt = conn.prepareStatement("INSERT INTO MEMBERS(EMAIL, PWD, MNAME, CRE_DATE, MOD_DATE)"
					+ "VALUES (?, ?, ?, NOW(), NOW())");
			stmt.setString(1, member.getEmail());
			stmt.setString(2, member.getPassword());
			stmt.setString(3, member.getName());
			
			return stmt.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			try {if(stmt != null) stmt.close();} catch(Exception e) {}
			try {if(conn != null) conn.close();} catch(Exception e) {}
//			if(conn != null) connPool.returnConnection(conn);
		}
	}
	public int delete(int no) throws Exception { // 회원 삭제
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
//			conn = connPool.getConnection();
			conn = ds.getConnection();
			stmt = conn.prepareStatement("delete from MEMBERS where MNO=?");
			stmt.setInt(1, no);
			
			return stmt.executeUpdate();
		} catch(Exception e) {
			throw e;
		} finally {
			try {if(stmt != null) stmt.close();} catch(Exception e) {}
			try {if(conn != null) conn.close();} catch(Exception e) {}
//			if(conn != null) connPool.returnConnection(conn);
		}
	}
	public Member selectOne(int no) throws Exception { // 회원 상세 정보 조회
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
//			conn = connPool.getConnection();
			conn = ds.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select MNO, EMAIL, MNAME, CRE_DATE"
					+ " from MEMBERS where MNO = " + no);
			rs.next();
			
			Member member = new Member()
					.setEmail(rs.getString("EMAIL"))
					.setName(rs.getString("MNAME"))
					.setNo(rs.getInt("MNO"))
					.setCreatedDate(rs.getDate("CRE_DATE"));
			
			return member;
		} catch (Exception e){
			throw e;
		} finally {
			try {if(stmt != null) stmt.close();} catch(Exception e) {}
			try {if(rs != null) rs.close();} catch(Exception e) {}
			try {if(conn != null) conn.close();} catch(Exception e) {}
//			if(conn != null) connPool.returnConnection(conn);
		}
	}
	
	public int update(Member member) throws Exception { // 회원 정보 변경
		Connection conn = null;
		PreparedStatement stmt = null;
				
		try {
//			conn = connPool.getConnection();
			conn = ds.getConnection();
			stmt = conn.prepareStatement("update MEMBERS set EMAIL=?, MNAME=?, MOD_DATE=now() where MNO=?");
			stmt.setString(1, member.getEmail());
			stmt.setString(2, member.getName());
			stmt.setInt(3, member.getNo());
			
			return stmt.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			try {if(stmt != null) stmt.close();} catch(Exception e) {}
			try {if(conn != null) conn.close();} catch(Exception e) {}
//			if(conn != null) connPool.returnConnection(conn);
		}
	}
		
	public Member exist(String email, String password) throws Exception { // 있으면 Member객체 리턴, 없으면 null 리턴
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {
//			conn = connPool.getConnection();
			conn = ds.getConnection();
			stmt = conn.prepareStatement("select MNAME, EMAIL from MEMBERS where EMAIL=? and PWD = ?");
			stmt.setString(1, email);
			stmt.setString(2, password);
			
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				Member member = new Member()
						.setEmail(rs.getString("EMAIL"))
						.setName(rs.getString("MNAME"));
				
				return member;
			}
			return null;
		} catch(Exception e) {
			throw e;
		} finally {
			try {if(stmt != null) stmt.close();} catch(Exception e) {}
			try {if(rs != null) rs.close();} catch(Exception e) {}
			try {if(conn != null) conn.close();} catch(Exception e) {}
//			if(conn != null) connPool.returnConnection(conn);
		}
	}
}
