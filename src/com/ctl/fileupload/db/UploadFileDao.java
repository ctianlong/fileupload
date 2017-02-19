package com.ctl.fileupload.db;

import java.sql.Connection;
import java.util.List;

import com.ctl.fileupload.beans.FileUploadBean;

public class UploadFileDao extends DAO<FileUploadBean>{
	
	public String getFilePathWithId(Integer id) {
		
		Connection conn = null;
		
		try {
			conn = JDBCUtils.getConnection();
			String sql = "SELECT file_path FROM uploadfile_info WHERE id = ?";
			return getValue(conn, sql, id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			JDBCUtils.release(conn);
		}
		return null;
	}
	
	public List<FileUploadBean> getFiles(){
		
		Connection conn = null;
		
		try {
			conn = JDBCUtils.getConnection();
			String sql = "SELECT id, file_name fileName, file_path filePath, " +
					"file_desc fileDesc FROM uploadfile_info";
			return getForList(conn, sql);			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			JDBCUtils.release(conn);
		}
		
		return null;
	} 
	
	public void save(List<FileUploadBean> uploadFiles){
		
		Connection conn = null;
		
		try {
			conn = JDBCUtils.getConnection();
			String sql = "INSERT INTO uploadfile_info (file_name, file_path, file_desc) VALUES " +
					"(?, ?, ?)";
			for(FileUploadBean file: uploadFiles){
				update(conn, sql, file.getFileName(), file.getFilePath(), file.getFileDesc());
			}			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			JDBCUtils.release(conn);
		}
		
	}
	
}
