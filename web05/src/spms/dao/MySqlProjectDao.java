package spms.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import spms.annotation.Component;
import spms.vo.Project;

@Component("projectDao")
public class MySqlProjectDao implements ProjectDao{
	DataSource ds;
	
	public void setDataSource(DataSource ds) {
		this.ds = ds;
	}
	
	@Override
	public List<Project> selectList() throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select PNO, PNAME, STA_DATE, END_DATE, STATE from PROJECTS order by PNO desc");
			
			ArrayList<Project> projects = new ArrayList<Project>();
			
			while(rs.next()) {
				projects.add(new Project()
						.setNo(rs.getInt("PNO"))
						.setTitle(rs.getString("PNAME"))
						.setStartDate(rs.getDate("STA_DATE"))
						.setEndDate(rs.getDate("END_DATE"))
						.setState(rs.getInt("STATE")));
			}
			
			return projects;
		} catch(Exception e) {
			throw e;
		} finally {
			try {if(rs != null) rs.close();} catch(Exception e) {}
			try {if(stmt != null) stmt.close();} catch(Exception e) {}
			try {if(conn != null) conn.close();} catch(Exception e) {}
		}
	}

	@Override
	public int insert(Project project) throws Exception { // 프로젝트 등록
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = ds.getConnection();
			stmt = conn.prepareStatement("insert into PROJECTS(PNAME, CONTENT, STA_DATE, END_DATE, STATE, CRE_DATE, TAGS) values(?, ?, ?, ?, 0, now(), ?)");
			stmt.setString(1, project.getTitle());
			stmt.setString(2, project.getContent());
			stmt.setDate(3, new java.sql.Date(project.getStartDate().getTime()));
			stmt.setDate(4, new java.sql.Date(project.getEndDate().getTime()));
			stmt.setString(5, project.getTags());
			
			return stmt.executeUpdate();
		}catch(Exception e) {
			throw e;
		} finally {
			try {if(conn != null) conn.close();} catch(Exception e) {}
			try {if(stmt != null) stmt.close();} catch(Exception e) {}
		}
	}
	
	public Project selectOne(int no) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from PROJECTS where PNO = " + no);
			rs.next();
			
			Project project = new Project()
					.setNo(rs.getInt("PNO"))
					.setTitle(rs.getString("PNAME"))
					.setContent(rs.getString("CONTENT"))
					.setStartDate(rs.getDate("STA_DATE"))
					.setEndDate(rs.getDate("END_DATE"))
					.setState(rs.getInt("STATE"))
					.setTags(rs.getString("TAGS"));
			
			return project;
		}catch(Exception e) {
			throw e;
		}finally {
			try {if(rs != null) rs.close();} catch(Exception e) {}
			try {if(stmt != null) stmt.close();} catch(Exception e) {}
			try {if(conn != null) conn.close();} catch(Exception e) {}
		}
	}
	
	public int update(Project project) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = ds.getConnection();
			stmt = conn.prepareStatement("update PROJECTS set PNAME=?, CONTENT=?, STA_DATE=?, END_DATE=?, STATE=?, TAGS=?");
			stmt.setString(1, project.getTitle());
			stmt.setString(2, project.getContent());
			stmt.setDate(3, new java.sql.Date(project.getStartDate().getTime()));
			stmt.setDate(4, new java.sql.Date(project.getEndDate().getTime()));
			stmt.setInt(5, project.getState());
			stmt.setString(6, project.getTags());
			
			return stmt.executeUpdate();
		} catch(Exception e) {
			throw e;
		} finally {
			try {if(stmt != null) stmt.close();} catch(Exception e) {}
			try {if(conn != null) conn.close();} catch(Exception e) {}
		}
	}
	
	@Override
	public int delete(int no) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			return stmt.executeUpdate("delete from PROJECTS where PNO = " + no);
		}catch(Exception e) {
			throw e;
		} finally {
			try {if(stmt != null) stmt.close();} catch(Exception e) {}
			try {if(conn != null) conn.close();} catch(Exception e) {}
		}
	}
}
