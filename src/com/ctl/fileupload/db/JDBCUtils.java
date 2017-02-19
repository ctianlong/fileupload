package com.ctl.fileupload.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.ctl.fileupload.exception.DBException;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JDBCUtils {

	private static DataSource dataSource = null;
	
	static{
		dataSource = new ComboPooledDataSource("javaweb");
	}
	
	public static Connection getConnection(){  
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("数据库连接错误");
		}
	}
 
	public static void release(Connection connection) {
		try {
			if(connection != null){
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException("数据库连接错误");
		}
	}
	
}
