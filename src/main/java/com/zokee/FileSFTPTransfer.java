/*
 * Power by www.xiaoi.com
 */
package com.zokee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

/**
 * @author <a href="mailto:eko.z@outlook.com">eko.zhan</a>
 * @date Jul 20, 2015 2:54:53 PM
 * @version 1.0
 */
public class FileSFTPTransfer extends FileTransfer {

	protected Session session = null;
	protected ChannelSftp channel = null;
	
	public boolean initFtpInfo(String host, String port, String username, String password) throws Exception {
		boolean success = false;
		try {
			JSch jsch = new JSch(); // 创建JSch对象
			session = jsch.getSession(username, host, StringUtils.isNotBlank(port) ? Integer.valueOf(port) : 22);
			session.setPassword(password); // 设置密码
			
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config); // 为Session对象设置properties
			session.setTimeout(60000); // 设置timeout时间
			session.connect(); // 通过Session建立链接
			
			Channel chan = session.openChannel("sftp"); // 打开SFTP通道
			chan.connect(); // 建立SFTP通道的连接
			
			channel = (ChannelSftp) chan;
			
			success = true;
		} catch (Exception e) {
			throw e;
		}
		return success;
	}
	
	public void fileTransfer(File file, String path) throws Exception {
		this.fileTransfer(file, path, file.getName());
	}

	public void fileTransfer(File file, String path, String fileName) throws Exception {
		FileInputStream fileInputStream = null;
		try {
			path = getFtpRoot() + path;
			try {
				channel.ls(path);
			} catch (Exception e) {
				channel.mkdir(path);
			}
			fileInputStream = new FileInputStream(file);
			channel.put(fileInputStream, (path.endsWith("/") ? path : path + "/") + fileName, ChannelSftp.OVERWRITE);
		} catch (Exception e) {
			throw e;
		} finally{
			if (fileInputStream!=null){
				fileInputStream.close();
			}
		}
	}
	
	public void fileTransfer(InputStream in, String path, String fileName) throws Exception{
		try {
			path = getFtpRoot() + path;
			try {
				channel.ls(path);
			} catch (Exception e) {
				channel.mkdir(path);
			}
			channel.put(in, (path.endsWith("/") ? path : path + "/") + fileName, ChannelSftp.OVERWRITE);
		} catch (Exception e) {
			throw e;
		} finally{
			if (in!=null){
				in.close();
			}
		}
	}
	
	public List<String> listFile(String path) throws Exception{
		List<String> list = new ArrayList<String>();
		path = getFtpRoot() + path;
		Vector<LsEntry> v = channel.ls(path);
		for (LsEntry lsEntry : v) {
			if(!".".equals(lsEntry.getFilename()) && !"..".equals(lsEntry.getFilename())){
				SftpATTRS attrs = lsEntry.getAttrs();
				if(attrs.isDir()){
					
				}else{
					list.add((path.endsWith("/") ? path : path + "/") + lsEntry.getFilename());
				}
			}
		}
		return list;
	}
	
	/**
	 * 根据指定的服务器上文件的相对路径，下载到指定的磁盘位置
	 * @param filePath	服务器上文件的相对路径	attached/image/20150528/20150528171741_991.jpg
	 * @param localPath	存储在本地的磁盘位置 	D:\jetty-6.1.26\webapps\micromsg\
	 * @throws Exception 
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
			channel.get(filename, out);
			out.flush();
		} catch (SftpException e) {
			throw e;
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
	 * 读取文件流
	 * @param filePath
	 * @return
	 * @throws Exception
	 */
	public InputStream fileRead(String filePath) throws Exception{
		InputStream inputStream = channel.get(getFtpRoot() + filePath);
		return inputStream;
	}
	
	public void closeConntection() {
		try {
			if (channel != null) {
				channel.quit();
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void fileDelete(String path) throws Exception {
		channel.rm(getFtpRoot() + path);
	}
}
