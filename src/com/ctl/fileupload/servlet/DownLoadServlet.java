package com.ctl.fileupload.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ctl.fileupload.db.UploadFileDao;

@WebServlet("/servlet/DownLoadServlet")
public class DownLoadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private UploadFileDao dao = new UploadFileDao();
       
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileId = request.getParameter("fileId");
		String filePath = dao.getFilePathWithId(Integer.parseInt(fileId));
		
		File file = new File(filePath);
		if(!file.exists()){
			String message = "您要下载的资源已被删除";
			request.setAttribute("message", message);
			request.getRequestDispatcher("/servlet/ListFileServlet").forward(request, response);
			return;
		}
		
		response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment;filename=" + request.getParameter("fileName"));
		//以下均没必要，因为在这里fileName是从前台传过来的，本身编码即为ISO-8859-1，而response张的Header在前台显示时
		//就是用ISO-8859-1显示
		//response.setCharacterEncoding("UTF-8");
		//String fileName = new String(request.getParameter("fileName").getBytes("ISO-8859-1"), "UTF-8");
		//URLEncoder.encode(fileName, "UTF-8")
		
		InputStream in = new FileInputStream(file);
		OutputStream out = response.getOutputStream();
		
		byte[] buffer = new byte[1024];
		int len = 0;
		while((len = in.read(buffer)) != -1){
			out.write(buffer, 0, len);
		}
		in.close();
		out.close();
	}

}
