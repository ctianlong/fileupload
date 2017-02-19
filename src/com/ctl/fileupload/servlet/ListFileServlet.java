package com.ctl.fileupload.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ctl.fileupload.beans.FileUploadBean;
import com.ctl.fileupload.db.UploadFileDao;


@WebServlet("/servlet/ListFileServlet")
public class ListFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private UploadFileDao dao = new UploadFileDao();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<FileUploadBean> beans = dao.getFiles();
		request.setAttribute("fileList", beans);
		request.getRequestDispatcher("/FileUploadAndDownload/download.jsp").forward(request, response);
	}

}
