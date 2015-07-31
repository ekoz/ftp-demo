/*
 * Power by www.xiaoi.com
 */
package com.zokee;

import java.io.File;

import org.junit.Test;

/**
 * @author <a href="mailto:eko.z@outlook.com">eko.zhan</a>
 * @date Jul 21, 2015 2:11:14 PM
 * @version 1.0
 */
public class TestFileTransfer {

	@Test
	public void testUpload(){
		FileTransfer ftpClient = FileTransfer.getInstance();
		File file = new File("E:\\ConvertTester\\CeairFile\\201410170958022830.pdf");
		try {
			ftpClient.fileTransfer(file, "lucy/");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			ftpClient.closeConntection();
		}
	}
	
	@Test
	public void testDownload(){
		FileTransfer ftpClient = FileTransfer.getInstance();
		try {
			ftpClient.fileDownload("lucy/201410170958022830.pdf", "E:\\ConvertTester\\CeairFile\\123\\");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			ftpClient.closeConntection();
		}
	}
}
