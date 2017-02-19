<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="${pageContext.request.contextPath }/scripts/jquery-1.7.2.js"></script>
<script type="text/javascript">

	$(function(){
		
		var i = 2;
		//1.获取#addFile,并为其添加click响应函数
		$("#addFile").click(function(){
			//2.利用jQuery生成以下节点，注意数字的变化,并添加到相应节点的前面
			//其中“删除”按钮可以删除当前的file和desc相关的节点
			/*
			File1:<input type="file" name="file"/>
			Desc1:<input type="text" name="desc"/><input type="button" id="deleteFile" value="删除"/>
			*/
			$(this).parent().parent().before("<tr class='file'><td>File" 
					+ i + ":</td><td><input type='file' name='file" 
					+ i + "'/></td></tr><tr class='desc'><td>Desc" 
					+ i + ":</td><td><input type='text' name='desc" 
					+ i + "'/><input type='button' id='delete" 
					+ i + "' value='删除'/></td></tr>");
			//获取新添加的删除按钮
			$("#delete" + i).click(function(){
				var $tr = $(this).parent().parent();
				$tr.prev("tr").remove();
				$tr.remove();
				
				$(".file").each(function(index){
					var n = index + 1;
					$(this).find("td:first").text("File" + n);
					$(this).find("td:last input").attr("name", "file" + n);
				});
				
				$(".desc").each(function(index){
					var n = index + 1;
					$(this).find("td:first").text("Desc" + n);
					$(this).find("td:last input:first").attr("name", "desc" + n);
					$(this).find("td:last input:last").attr("id", "delete" + n);					
				});
				i--;
			});
			
			i++;
		});
		

	});
</script>

</head>
<body>

	<font color="red">${message }</font>
	<br>
	<form action="${pageContext.request.contextPath }/servlet/UploadHandleServlet" method="post" enctype="multipart/form-data">
	
		<table>
			<tr class="file">
				<td>File1:</td>
				<td><input type="file" name="file1"/></td>
			</tr>
			<tr class="desc">
				<td>Desc1:</td>
				<td><input type="text" name="desc1"/></td>
			</tr>
			<tr>
				<td><input type="submit" id="submit" value="上传文件"/></td>
				<td><input type="button" id="addFile" value="新增一个附件"/></td>
			</tr>
		</table>		
		
	</form>
	<br>
	<a href="${pageContext.request.contextPath }/servlet/ListFileServlet">下载文件</a>

</body>
</html>