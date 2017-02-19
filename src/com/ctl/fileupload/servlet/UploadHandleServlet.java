package com.ctl.fileupload.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;

import com.ctl.fileupload.beans.FileUploadBean;
import com.ctl.fileupload.db.UploadFileDao;
import com.ctl.fileupload.exception.InvalidExtNameException;
import com.ctl.fileupload.utils.FileUploadProperties;
import com.ctl.fileupload.utils.MD5FileUtil;

@WebServlet("/servlet/UploadHandleServlet")
public class UploadHandleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static String exts = null;
	private static String sizeInDisk = null;
	private static String fileMaxSize = null;
	//private static String totalFileMaxSize = null;
	private static String saveDirectory = null;
	private static String tempDirectory = null;
	
	private UploadFileDao dao = new UploadFileDao();
	
	@Override
	public void init() throws ServletException {
		exts = FileUploadProperties.getInstance().getProperty("exts");
		sizeInDisk = FileUploadProperties.getInstance().getProperty("size.in.disk");
		fileMaxSize = FileUploadProperties.getInstance().getProperty("file.max.size");
		//totalFileMaxSize = FileUploadProperties.getInstance().getProperty("total.file.max.size");
		//saveDirectory = getServletContext().getRealPath(FileUploadProperties.getInstance().getProperty("save.directory"));
		saveDirectory = FileUploadProperties.getInstance().getProperty("save.directory");
		tempDirectory = getServletContext().getRealPath(FileUploadProperties.getInstance().getProperty("temp.directory"));
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//String file = request.getParameter("file");
		//String desc = request.getParameter("desc");
		//System.out.println(file);
		//System.out.println(desc);
		
//		InputStream in = request.getInputStream();
//		Reader reader = new InputStreamReader(in);
//		BufferedReader bufferedReader = new BufferedReader(reader);
//		
//		String str = null;
//		while((str = bufferedReader.readLine()) != null){
//			System.out.println(str);
//		}
		
		File tempFile = new File(tempDirectory);
		if(!tempFile.exists()){
			tempFile.mkdirs();
		}
		File saveFile = new File(saveDirectory);
		if(!saveFile.exists()){
			saveFile.mkdirs();
		}
		
		String path = null;
		String message = null;
		
		//把需要上传的 FileItem 都放入到该 Map 中
		//键: 文件的待存放的路径, 值: 对应的 FileItem 对象
		Map<String, FileItem> uploadFiles = new HashMap<String, FileItem>();
		//存入数据库的bean的集合
		List<FileUploadBean> beans = new ArrayList<FileUploadBean>();
		try {
			//获取 ServletFileUpload 对象. 
			ServletFileUpload upload = getServletFileUpload();
			//解析请求, 得到 FileItem 的集合.
			List<FileItem> items = upload.parseRequest(request);
			//1. 构建 FileUploadBean 的集合, 同时填充 uploadFiles
			beans = buildFileUploadBeans(items, uploadFiles);
			//2. 校验扩展名:
			validateExtName(beans);
			//3. 校验文件的大小: 在解析时, 已经校验了, 我们只需要通过异常得到结果. 
			//4. 进行文件的上传操作.
			upload(uploadFiles);
			//5. 把上传的信息保存到数据库中
			saveBeans(beans);
			//6. 删除临时文件夹的临时文件，使用org.apache.commons.io.FileUtils
			FileUtils.cleanDirectory(new File(tempDirectory));
			
			message = "成功上传" + uploadFiles.size() + "个文件";
			path = "/FileUploadAndDownload/uploadSuccess.jsp";
		} catch(FileUploadBase.FileSizeLimitExceededException e) {
			e.printStackTrace();			
			message = "单个文件不能超过 10M";
			path = "/FileUploadAndDownload/upload.jsp";			
		} catch (InvalidExtNameException e) {
			e.printStackTrace();
			message = e.getMessage();
			path = "/FileUploadAndDownload/upload.jsp";
		} catch (Exception e) {
			e.printStackTrace();
			message = "文件上传失败";
			path = "/FileUploadAndDownload/upload.jsp";
		}
		
		request.setAttribute("message", message);
		request.getRequestDispatcher(path).forward(request, response);
	}

	
	private void saveBeans(List<FileUploadBean> beans) {
		dao.save(beans);
	}


	/**
	 * 文件上传前的准备工作. 得到 filePath 和 InputStream
	 * @param uploadFiles
	 * @throws IOException
	 */
	private void upload(Map<String, FileItem> uploadFiles) throws IOException {
		for(Map.Entry<String, FileItem> uploadFile: uploadFiles.entrySet()){
			String filePath = uploadFile.getKey();
			FileItem item = uploadFile.getValue();
			InputStream in = item.getInputStream();
			upload(filePath, in);
			in.close();
		}
	}
	
	/**
	 * 文件上传的 IO 方法.
	 * 
	 * @param filePath
	 * @param inputStream
	 * @throws IOException
	 */
	private void upload(String filePath, InputStream inputStream) throws IOException {
		OutputStream out = new FileOutputStream(filePath);
		byte [] buffer = new byte[1024];
		int len = 0;
		while((len = inputStream.read(buffer)) != -1){
			out.write(buffer, 0, len);
		}
		out.close();
	}


	private void validateExtName(List<FileUploadBean> beans) throws InvalidExtNameException {
		List<String> extNames = Arrays.asList(exts.split(","));
		for(FileUploadBean bean: beans){
			String fileName = bean.getFileName();
			String extName = fileName.substring(fileName.lastIndexOf(".") + 1);
			if(!extNames.contains(extName)){
				throw new InvalidExtNameException(fileName + "文件扩展名不合法");
			}
		}
	}


	/**
	 * 利用传入的 FileItem 的集合, 构建 FileUploadBean 的集合, 同时填充 uploadFiles
	 * 
	 * FileUploadBean 对象封装了: id, fileName, filePath, fileDesc
	 * uploadFiles: Map<String, FileItem> 类型, 存放文件域类型的  FileItem. 键: 待保存的文件的名字 ,值: FileItem 对象
	 * 
	 * 构建过程:
	 * 1. 对传入 FileItem 的集合进行遍历. 得到 desc 的那个 Map. 键: desc 的 fieldName(desc1, desc2 ...). 
	 * 值: desc 的那个输入的文本值
	 * 
	 * 2. 对传入 FileItem 的集合进行遍历. 得到文件域的那些 FileItem 对象, 构建对应的 key (desc1 ....) 来获取其 desc.
	 * 构建的 FileUploadBean 对象, 并填充 beans 和 uploadFiles
	 * 
	 * @param items
	 * @param uploadFiles
	 * @return
	 * @throws IOException 
	 */
	private List<FileUploadBean> buildFileUploadBeans(List<FileItem> items, Map<String, FileItem> uploadFiles) throws IOException {
		List<FileUploadBean> beans = new ArrayList<FileUploadBean>();
		
		Map<String, String> descs = new HashMap<String, String>();
		for(FileItem item: items){
			if(item.isFormField()){
				String descFieldName = item.getFieldName();
				String descValue = item.getString("UTF-8");
				descs.put(descFieldName, descValue);
			}
		}
		
		for(FileItem item: items){
			FileUploadBean bean = null;
			if(!item.isFormField()){
				//原文件名
				//注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如： c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
				//处理获取到的上传文件的文件名的路径部分，只保留文件名部分
				String fileName = item.getName();
				if(fileName == null || fileName.trim().equals("")){
					continue;
				}
                fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
                
                //文件保存名
                String fileSaveName = getFileSaveName(item);
				//文件保存目录
				String fileSaveDirectory = getFileSaveDirectory(fileSaveName, saveDirectory);
				//文件完整保存路径
				String filePath = fileSaveDirectory + "/" + fileSaveName;
				
				//文件描述
				String index = item.getFieldName().substring(4);
				String fileDesc = descs.get("desc" + index);
				
				bean = new FileUploadBean(fileName, filePath, fileDesc);
				beans.add(bean);
				//填充uploadFiles
				uploadFiles.put(filePath, item);
			}
		}
		return beans;
	}


	/**
	 * 为防止文件覆盖的现象发生，要为上传文件产生一个唯一的文件名
	 * @param item
	 * @return
	 * @throws IOException
	 */
	private String getFileSaveName(FileItem item) throws IOException {
		String fileName = item.getName();
		//得到扩展名
        String fileExtName = fileName.substring(fileName.lastIndexOf("."));
        InputStream in = item.getInputStream();
        //使用MD5算法
        String fileSaveName = MD5FileUtil.getFileMD5String(in) + fileExtName;
        if(in != null){
        	in.close();        	
        }
		return fileSaveName;
	}
	
	
	/**
	 * 为防止一个目录下面出现太多文件，要使用hash算法打散存储
	 * @param fileSaveName 文件名，要根据文件名生成存储目录
	 * @param directory 文件存储路径
	 * @return 新的存储目录
	 */
	private String getFileSaveDirectory(String fileName, String directory) {
		//得到文件名的hashCode的值，得到的就是filename这个字符串对象在内存中的地址
	     int hashcode = fileName.hashCode();
	     int dir1 = hashcode&0xf; //0--15
	     int dir2 = (hashcode&0xf0)>>4; //0-15
	     //构造新的保存目录
	     String dir = directory + "/" + dir1 + "/" + dir2;
	     File file = new File(dir);
	     if(!file.exists()){
	       file.mkdirs();
	    }
	     return dir;
	}

	
	/**
	 * 构建 ServletFileUpload 对象
	 * 从配置文件中读取了部分属性, 用户设置约束. 
	 * 该方法代码来源于文档. 
	 * @return
	 */
	private ServletFileUpload getServletFileUpload() {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(Integer.parseInt(sizeInDisk));
		factory.setRepository(new File(tempDirectory));
		
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setFileSizeMax(Integer.parseInt(fileMaxSize));
		upload.setHeaderEncoding("UTF-8");
		//异常不能正常抛出，存在问题，先注释
		//upload.setSizeMax(Integer.parseInt(totalFileMaxSize));
		
		return upload;
	}


	
	
	/**
	 * 根据跟定的文件名构建一个随机的文件名
	 * 1. 构建的文件的文件名的扩展名和给定的文件的扩展名一致
	 * 2. 利用 ServletContext 的 getRealPath 方法获取的绝对路径
	 * 3. 利用了 Random 和 当前的系统时间构建随机的文件的名字
	 * 
	 * @param fileName
	 * @return
	 */
	private String getFilePath(String fileName) {
		String extName = fileName.substring(fileName.lastIndexOf("."));
		Random random = new Random();
		
		//String filePath = getServletContext().getRealPath(saveDirectory) + "/" + System.currentTimeMillis() + random.nextInt(100000) + extName;
		String filePath = saveDirectory + "/" + System.currentTimeMillis() + random.nextInt(100000) + extName;
		return filePath;
	}

}
