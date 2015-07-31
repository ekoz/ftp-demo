<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML>
<html>
	<head>
		<base href="<%=basePath%>">

		<title>FILE UPLOAD</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
		<style type="text/css">
			body{
				font-size: 12px;
			}
			table{
				width:800px;
				border:1px solid #DDD;
			}
			td{
				border:1px dotted #DDD;
				padding-top:3px;
				padding-bottom:3px;
			}
		</style>
		
		<script type="text/javascript">
			function down(filepath){
				window.open('${pageContext.request.contextPath }/servlet/FileAction?action=FILE_DOWNLOAD&filepath='+encodeURI(filepath));
			}
			
			function del(filepath){
				location.href = '${pageContext.request.contextPath }/servlet/FileAction?action=FILE_DELETE&filepath='+filepath;
			}
		</script>
	</head>

	<body>
		<form action="${pageContext.request.contextPath }/servlet/FileAction?action=FILE_UPLOAD" method="post" enctype="multipart/form-data">
		    <input type="file" name="file">
		    <input type="submit" name="upload"value="上传">
		</form>
		<table cellpadding="1" cellspacing="1">
			<c:forEach items="${requestScope.list}" var="var">
				<tr>
					<td>${var }</td>
					<td><a href="javascript:void(0);" onclick="down('${var }');">下载</a></td>
					<td><a href="javascript:void(0);" onclick="del('${var }');">删除</a></td>
				</tr>
			</c:forEach>
		</table>
	</body>
</html>
