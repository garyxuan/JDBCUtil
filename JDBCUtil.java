package com.garyxuan.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;



/**
 * 数据库连接工具类
 * 1.reparedStatement用于处理动态SQL语句，在执行前会有一个预编译过程，这个过程是有时间开销的，虽然相对数据库的操作，该时间开销可以忽略不计，但是PreparedStatement的预编译结果会被缓存，下次执行相同的预编译语句时，就不需要编译，只要将参数直接传入编译过的语句执行代码中就会得到执行，所以，对于批量处理可以大大提高效率。
 * 2.Statement每次都会执行SQL语句，相关数据库都要执行SQL语句的编译。
 * 作为开发者，应该尽可能以PreparedStatement代替Statement,原因如下：
 * 1.代码的可读性和可维护性。
 * 2.PreparedStatement尽最大可能提高性能。
 * 3.极大的提高了安全性。
 * @author garyxuan
 * @version 1.0.0
 */
public class JDBCUtil {
	//数据库地址 dataBaseName替换为数据库名称
	public static final String url = "jdbc:mysql://localhost/dataBaseName?useSSL=false&characterEncoding=utf8";
	//驱动信息
	public static final String driver = "com.mysql.jdbc.Driver";
	//用户名 userName为数据库用户名
	public static final String user = "userName";
	//密码 password为数据库密码
	public static final String password = "password";
	private Connection conn = null;
	private PreparedStatement pstm = null;
	private ResultSet rs = null;
		
	public JDBCUtil() {
		//加载数据库驱动程序
		try {
			Class.forName(driver);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取数据库连接
	 * @return
	 */
	public Connection getConnection() {
		try {
			//获得到数据库的连接
			conn = DriverManager.getConnection(url,user,password);
			System.out.println("JDBCUtil getConnection " + conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * 关闭数据路连接
	 */
	public void releaseConnectn() {
		if (rs != null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (pstm != null){
			try {
				pstm.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if (conn != null) {
			try {
				conn.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 查询多条记录
	 * @param sql sql语句
	 * @param param sql语句参数
	 * @return 返回结果表，每个元素为一行结果
	 */
	public List<Map<String, Object>> executeQuery(String sql, List<Object> params) throws Exception{
		List<Map<String,Object>> list = null;
		int index = 1;
		try {
			pstm = conn.prepareStatement(sql);//sql语句被预编译存储在prepareStatement对象中，然后可以使用此对象多次高效地执行该语句
			if (params != null && !params.isEmpty()) {
				for (int i = 0; i < params.size(); i++) {
					pstm.setObject(index++, params.get(i));
				}
			}
			rs = pstm.executeQuery();
			list = new ArrayList<Map<String, Object>>();
			ResultSetMetaData rsmd = rs.getMetaData();
			while (rs.next()) {
				Map<String,Object> map = new HashMap<String,Object>();
				for (int j = 1; j <= rsmd.getColumnCount(); j++) {
					String col_key = rsmd.getColumnName(j);
					Object col_value = rs.getObject(col_key);
					if (col_value == null) {
						col_value = "";
					}
					map.put(col_key,col_value);	
				}
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstm != null)
					pstm.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}
		
	/**
	 * 执行SQL增、删、改语句
	 * @param sql sql语句
	 * @param param sql语句参数
	 * @return 受影响的行数
	 */
	public int executeUpdate(String sql, List<Object> params) {
		int rows = 0;
		int index = 1;
		try {
			pstm = this.conn.prepareStatement(sql);
			if (params != null && !params.isEmpty()) {
				for (int i = 0; i < params.size(); i++) {
					pstm.setObject(index++, params.get(i));
				}
			}
			rows = pstm.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstm != null)
					pstm.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
		return rows;
	}
	
	
	/**
	 * 反射机制查询单条记录,将结果转化为传入类的实例对象
	 * 确保查询的所有字段在类中有相同名称的成员变量
	 * @param sql sql语句
	 * @param param sql语句参数
	 * @param cls 模板类
	 * @return 返回模板类实例对象 
	 */
	public <T> T findSimpleRefResult(String sql, List<Object> params, Class<T> cls) {
		T resultObject = null;
		int index = 1;
		try {
			pstm = this.conn.prepareStatement(sql);
			if (params != null && !params.isEmpty()) {
				for (int i = 0; i < params.size(); i++) {
					pstm.setObject(index++, params.get(i));
				}
			}
			rs = pstm.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int col_len = rsmd.getColumnCount();
			while (rs.next()) {
				resultObject = cls.newInstance();
				for (int i = 1; i <= col_len; i++) {
					String col_key = rsmd.getColumnName(i);
					Object col_value = rs.getObject(col_key);
					if (col_value == null) {
						col_value = "";
					}
					Field field = cls.getDeclaredField(col_key);//获取 实例变量
					field.setAccessible(true);//打开javabean的访问权限
					field.set(resultObject, col_value);//设置实例变量的值
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstm != null)
					pstm.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
		return resultObject;
	}
	
	/**
	 * 反射机制查询多条记录,将结果转化为传入类的实例对象
	 * 确保查询的所有字段在类中有相同名称的成员变量
	 * @param sql sql语句
	 * @param param sql语句参数
	 * @param cls 模板类
	 * @return 返回模板类实例对象 list
	 */
	public <T> List<T> findMoreRefResult(String sql, List<Object> params, Class<T> cls) throws Exception{
		List<T> list = new ArrayList<T>();
		int index = 1;
		try {
			pstm = this.conn.prepareStatement(sql);
			if (params != null && !params.isEmpty()) {
				for (int i = 0; i < params.size(); i++) {
					pstm.setObject(index++, params.get(i));
				}
			}
			rs = pstm.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int col_len = rsmd.getColumnCount();
			while (rs.next()) {
				T resultObject = cls.newInstance();
				for (int i = 1; i <= col_len; i++) {
					String col_key = rsmd.getColumnName(i);
					Object col_value = rs.getObject(col_key);
					if (col_value == null) {
						col_value = "";
					}
					Field field = cls.getDeclaredField(col_key);//获取 实例变量
					field.setAccessible(true);//打开javabean的访问权限，否则无法设置
					field.set(resultObject, col_value);//设置实例变量的值
				}
				list.add(resultObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (pstm != null)
					pstm.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
		return list;
	}
}
