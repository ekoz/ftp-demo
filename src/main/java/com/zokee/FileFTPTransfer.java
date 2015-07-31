package com.zokee;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

/**
 * ftp传送文件
 * 
 * @author andy.yuan
 * 
 * @author eko.zhan
 * 
 */
public class FileFTPTransfer extends FileTransfer{

	protected FTPClient ftp;
	
	public boolean initFtpInfo(String host, String port, String username, String password) throws Exception {
		boolean success = false;
		try {
			ftp = new FTPClient();
			int reply;
			ftp.connect(host, StringUtils.isNotBlank(port) ? Integer.valueOf(port) : 21);// 连接FTP服务器
			// 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
			ftp.login(username, password);// 登录
//			ftp.setControlEncoding("GBK");//这里设置编码
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				return success;
			}
			success = true;
		} catch (Exception e) {
			throw e;
		}
		return success;
	}

	/**
	 * 向FTP服务器上传文件
	 * 
	 * @param filename
	 *            上传到FTP服务器上的文件名
	 * @param input
	 *            输入流
	 * @return 成功返回true，否则返回false
	 */
	protected boolean uploadFile(String filename, String path, InputStream input) throws Exception {
		boolean success = false;
		try {
			path = getFtpRoot() + path;
			
			if (!ftp.changeWorkingDirectory(path)) {// 改变工作目录到path。如果false,创建目录后更改
				if (ftp.makeDirectory(path)) {
					if (ftp.changeWorkingDirectory(path)) {
						ftp.storeFile(filename, input);
						input.close();
						success = true;
					} else {
						success = false;
						new RuntimeException("ftp：Failed to change directory,请检查用户权限");
					}

				} else {
					success = false;
					new RuntimeException("ftp：Create directory operation failed,请检查用户权限");
				}
			} else {
				ftp.storeFile(filename, input);
				input.close();
				success = true;
			}
		} catch (IOException e) {
			throw e;
		}
		return success;
	}

	public void closeConntection() {
		if (ftp != null) {
			try {
				ftp.logout();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (ftp.isConnected()) {
					try {
						ftp.disconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 将本地文件上传到FTP服务器上
	 * 
	 */
	public void fileTransfer(File file, String path) throws Exception {
		fileTransfer(file, path, file.getName());
	}

	/**
	 * 将本地文件上传到FTP服务器上
	 * 
	 */
	public void fileTransfer(File file, String path, String fileName) throws Exception {
		try {
			FileInputStream in = new FileInputStream(file);
			uploadFile(fileName, path, in);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void fileTransfer(InputStream in, String path, String fileName) throws Exception {
		try {
			uploadFile(fileName, path, in);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 将字符串写入到该上传到FTP服务器文件中
	 * 
	 */
	public void stringTransfer(String content, String fileName, String path) throws Exception {
		try {
			InputStream input = new ByteArrayInputStream(content.getBytes("utf-8"));
			boolean flag = uploadFile(fileName, path, input);
			System.out.println(flag);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 提供直接上传的方法。该方法集成了，打开连接，上传文件，关闭连接，3个步骤
	 * 
	 * @param url
	 * @param port
	 * @param username
	 * @param password
	 * @param file
	 * @param path
	 * @throws Exception
	 */
	public void fileThroughTransfer(String url, String port, String username, String password, File file,
			String path, String fileName) throws Exception {
		boolean initFtpInfo = false;
		try {
			initFtpInfo = initFtpInfo(url, port, username, password); // 初始化
																		// ftp对象
		} catch (Exception e) {
			throw new RuntimeException("机器\"" + url + "\"ftp建立连接失败", e);
		}
		if (initFtpInfo) {
			try {
				fileTransfer(file, path,fileName);// 开始上传
			} catch (Exception e) {
				throw new RuntimeException("机器\"" + url + "\"图片ftp保存失败", e);
			}
		}
		try {
			closeConntection(); // 关闭内存
		} catch (Exception e) {
			throw new RuntimeException("机器\"" + url + "\"ftp链接断开异常", e);
		}
	}

	/**
	 * 根据指定的服务器上文件的相对路径，下载到指定的磁盘位置，最终存储本地磁盘路径应该是 localPath + filePath
	 * @param filePath	服务器上文件的相对路径	attached/image/20150528/20150528171741_991.jpg
	 * @param localPath	存储在本地的磁盘位置 	D:\jetty-6.1.26\webapps\micromsg\
	 */
	public void fileDownload(String filePath, String localPath) throws Exception{
		OutputStream out = null;
		
		try {
			//服务器文件地址
			String filename = getFtpRoot() + filePath;
			//目标地址（磁盘）
			String dst = localPath + filePath;
			
			File dir = new File(dst.substring(0, dst.lastIndexOf("/")));
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(dst);
			out = new FileOutputStream(file);
			ftp.retrieveFile(filename, out);
			out.flush();
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally{
			try {
				if (out!=null){
					out.close();
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}
	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public InputStream fileRead(String filePath) throws Exception{
		InputStream inputStream = ftp.retrieveFileStream(getFtpRoot() + filePath);
		return inputStream;
	}

	@Override
	public List<String> listFile(String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fileDelete(String path) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
