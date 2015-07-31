/*
 * Power by www.xiaoi.com
 */
package com.zokee.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.zokee.FileTransfer;

/**
 * @author <a href="mailto:eko.z@outlook.com">eko.zhan</a>
 * @date Jul 23, 2015 9:36:25 AM
 * @version 1.0
 */
public class FileAction extends HttpServlet {

	 private static FileTransfer ftpClient = FileTransfer.getInstance();
	 
	 private final static String FILE_UPLOAD = "FILE_UPLOAD";
	 private final static String FILE_DOWNLOAD = "FILE_DOWNLOAD";
	 private final static String FILE_DELETE = "FILE_DELETE";
	 private final static String Encoding_UTF8 = "UTF-8";
	
	/**
	 * Constructor of the object.
	 */
	public FileAction() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		if (StringUtils.isBlank(action)){
			//附件列表
			list(request, response);
		}else{
			if (action.equals(FILE_UPLOAD)){
				upload(request, response);
			}else if (action.equals(FILE_DOWNLOAD)){
				download(request, response);
			}else if (action.equals(FILE_DELETE)){
				delete(request, response);
			}
		}
		
		
		
	}

	private void delete(HttpServletRequest request, HttpServletResponse response) {
		String filepath = request.getParameter("filepath");
		
		if (filepath.indexOf(ftpClient.getFtpRoot())!=-1){
			filepath = filepath.substring(filepath.lastIndexOf(ftpClient.getFtpRoot())+ftpClient.getFtpRoot().length());
		}
		
		String filename = FilenameUtils.getName(filepath);
		
		try {
			ftpClient.fileDelete(filepath);
			response.sendRedirect("FileAction");
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

	private void list(HttpServletRequest request, HttpServletResponse response) {
		try {
			List<String> list = ftpClient.listFile("");
			request.setAttribute("list", list);
			
			RequestDispatcher rd = request.getRequestDispatcher("../upload.jsp");
			rd.forward(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}
	
	private void upload(HttpServletRequest request, HttpServletResponse response){
		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart){
			try {
				request.setCharacterEncoding(Encoding_UTF8);
				
				// Create a new file upload handler
				ServletFileUpload upload = new ServletFileUpload();
				upload.setHeaderEncoding(Encoding_UTF8);
				
				// Parse the request
				FileItemIterator iter = upload.getItemIterator(request);
				while (iter.hasNext()) {
				    FileItemStream item = iter.next();
				    String name = item.getFieldName();
				    InputStream stream = item.openStream();
				    if (item.isFormField()) {
				        System.out.println("Form field [" + name + "] with value " + Streams.asString(stream, Encoding_UTF8) + " detected.");
				    } else {
				    	String filename = item.getName();
				    	

				        if (StringUtils.isNotBlank(filename)){
				        	filename = URLDecoder.decode(filename, Encoding_UTF8);
					    	
					        System.out.println("File field [" + name + "] with file name " + item.getName() + " detected.");
					        // Process the input stream
					        
					        
					        //这里上传文件
					        filename = FilenameUtils.getName(filename);

							ftpClient.fileTransfer(stream, "", filename);
				        }
						
				    }
				}
				
				response.sendRedirect("FileAction");
			} catch (FileUploadException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		
		 
	}

	private void download(HttpServletRequest request, HttpServletResponse response){
		try {
			InputStream in = null;
			
			String filepath = request.getParameter("filepath");
			if (filepath.indexOf(ftpClient.getFtpRoot())!=-1){
				filepath = filepath.substring(filepath.lastIndexOf(ftpClient.getFtpRoot())+ftpClient.getFtpRoot().length());
			}
			
			String filename = FilenameUtils.getName(filepath);
			String filetype = "." + FilenameUtils.getExtension(filename);
						
			ServletOutputStream out = response.getOutputStream();
			
			try {
				in = ftpClient.fileRead(filepath);
				
				System.out.println(filename);
				
				//文件名乱码问题
				if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") >0){
					filename = URLEncoder.encode(filename, Encoding_UTF8);//IE浏览器
				}else{
					filename = new String(filename.getBytes(Encoding_UTF8), "ISO8859-1");//firefox浏览器
				}
				
				response.addHeader("Content-Disposition", "attachment;filename=" + filename);
//	            response.addHeader("Content-Length", "" + in.length());
//	            response.setContentType(MimeUtils.getString(filetype));
				response.setContentType("application/x-msdownload");
				response.setCharacterEncoding(Encoding_UTF8);
				
				IOUtils.copy(in, out);

			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				IOUtils.closeQuietly(in);
			}
			
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
