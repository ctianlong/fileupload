<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>

	<font>${message }</font>
	<br><br>
	<a href="${pageContext.request.contextPath }/servlet/ListFileServlet">下载文件</a>
	<br><br>
	<a href="${pageContext.request.contextPath }/FileUploadAndDownload/upload.jsp">继续上传</a>

</body>
</html>