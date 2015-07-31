/*
 * Power by www.xiaoi.com
 */
package com.zokee;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

/**
 * @author <a href="mailto:eko.z@outlook.com">eko.zhan</a>
 * @date Jul 20, 2015 2:49:11 PM
 * @version 1.0
 */
public abstract class FileTransfer {
	//noftp|-1;sftp|0;ftp|1
	private final static String FTP_MODE = "ftp";
	private final static String SFTP_MODE = "sftp";
	private final static String NOFTP_MODE = "noftp";
	
	private static ResourceBundle bundle = ResourceBundle.getBundle("app");
	
	private static String mode;
	private static String host;
	private static String port;
	private static String username;
	private static String password;
	private static String ftpRoot;
	
	static{
		mode = bundle.getString("ftpmode");
		host = bundle.getString("ftphost");
		port = bundle.getString("ftpport");
		username = bundle.getString("ftpusr");
		password = bundle.getString("ftppwd");
		ftpRoot = bundle.getString("ftproot");
	}
	
	/**
	 * 获取FTPClient实例
	 * @return
	 */
	public static FileTransfer getInstance(){
		FileTransfer ftpClient = null;
		if (mode.toLowerCase().equals(FTP_MODE) || mode.equals("1")){
			ftpClient = new FileFTPTransfer();
		}else if (mode.toLowerCase().equals(SFTP_MODE) || mode.equals("0")){
			ftpClient = new FileSFTPTransfer();
		}
		if (ftpClient!=null){
			try {
				ftpClient.initFtpInfo(host, port, username, password);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (StringUtils.isBlank(ftpRoot)){
				ftpRoot = "/";
			}
		}
		return ftpClient;
	}
	/**
	 * 初始化连接
	 * @param host
	 * @param port
	 * @param username
	 * @param password
	 * @throws Exception
	 * @return
	 */
	protected abstract boolean initFtpInfo(String host, String port, String username, String password) throws Exception;
	/**
	 * 将本地文件上传到FTP服务器上
	 * @param file			磁盘文件
	 * @param path			服务器相对路径，如： lily/files/
	 * @throws Exception
	 */
	public abstract void fileTransfer(File file, String path) throws Exception;
	/**
	 * 将本地文件上传到FTP服务器上
	 * @param file			磁盘文件
	 * @param path			服务器相对路径，如： lily/files/
	 * @param fileName		指定文件名
	 * @throws Exception
	 */
	public abstract void fileTransfer(File file, String path, String fileName) throws Exception;
	/**
	 * 将文件流上传到FTP服务器上
	 * @param in			文件流
	 * @param path			服务器相对路径，如： lily/files/
	 * @param fileName		指定文件名
	 * @throws Exception
	 */
	public abstract void fileTransfer(InputStream in, String path, String fileName) throws Exception;
	/**
	 * 根据指定的服务器上文件的相对路径，下载到指定的磁盘位置
	 * @param filePath	服务器上文件的相对路径（该路径是相对于配置的ftproot路径）		lucy/201410170958022830.pdf
	 * @param localPath	存储在本地的磁盘位置
	 * @throws Exception
	 */
	public abstract void fileDownload(String filePath, String localPath) throws Exception;
	/**
	 * 读取文件流
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public abstract InputStream fileRead(String filePath) throws Exception;
	/**
	 * 获取目录下的所有文件
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public abstract List<String> listFile(String path) throws Exception;
	/**
	 * 删除文件
	 * @param path
	 * @throws Exception
	 */
	public abstract void fileDelete(String path) throws Exception;
	/**
	 * 释放连接
	 */
	public abstract void closeConntection();
	
	////////////////////////////Getter/Setter//////////////////////////////////////////////////
	public static String getHost() {
		return host;
	}
	public static void setHost(String host) {
		FileTransfer.host = host;
	}
	public static String getPort() {
		return port;
	}
	public static void setPort(String port) {
		FileTransfer.port = port;
	}
	public static String getUsername() {
		return username;
	}
	public static void setUsername(String username) {
		FileTransfer.username = username;
	}
	public static String getPassword() {
		return password;
	}
	public static void setPassword(String password) {
		FileTransfer.password = password;
	}
	public static String getFtpRoot() {
		return ftpRoot;
	}
	public static void setFtpRoot(String ftpRoot) {
		FileTransfer.ftpRoot = ftpRoot;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////
	
}
