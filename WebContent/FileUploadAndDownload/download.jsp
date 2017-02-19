<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<font color="red">${message }</font>
	<c:if test="${empty fileList }">
	<font color="red">没有可下载资源</font>		
	</c:if>
	<br>
	<c:forEach items="${fileList }" var="file">
		FileName：${file.fileName }<br><br>
		Desc：${file.fileDesc }<br><br>
		<c:url value="/servlet/DownLoadServlet" var="downUrl">
			<c:param name="fileId" value="${file.id }"></c:param>
			<c:param name="fileName" value="${file.fileName }"></c:param>
		</c:url>
		<a href="${downUrl }">下载</a>
		<hr>
	</c:forEach>
	<br>
	<a href="${pageContext.request.contextPath }/FileUploadAndDownload/upload.jsp">继续上传</a>
</body>
</html>