/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.quanjing.util.imgSDK;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 *
 * @author jusisli
 */
public class PicInfo {
	private String url; // 下载url
	private String fileId; // 图片资源的唯一标识
	private int    uploadTime; // 图片上传时间，unix时间戳
	private int    size; // 图片大小，单位byte
	private String md5; // 图片md5
	private int    width;
	private int    height;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public int getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(int uploadTime) {
		this.uploadTime = uploadTime;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public PicInfo() {
	}
	public PicInfo(String url, String fileId, int uploadTime, int size, String md5, int width, int height) {
		this.url = url;
		this.fileId = fileId;
		this.uploadTime = uploadTime;
		this.size = size;
		this.md5 = md5;
		this.width = width;
		this.height = height;
	}


	public String toString() {
		return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
				.append("Url",getUrl())
				.append("FileId",getFileId())
				.append("UploadTime",getUploadTime())
				.append("Size",getSize())
				.append("Md5",getMd5())
				.append("Width",getWidth())
				.append("Height",getHeight())
				.toString();
	}
};