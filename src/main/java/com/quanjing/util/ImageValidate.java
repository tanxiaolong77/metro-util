package com.quanjing.util;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.quanjing.util.imgSDK.PicCloud;
import com.quanjing.util.imgSDK.PicInfo;
import com.quanjing.util.imgSDK.PornDetectInfo;
import com.quanjing.util.imgSDK.SliceUploadInfo;
import com.quanjing.util.imgSDK.UploadResult;

/***
 * 色情图片鉴别
* @Title: Demo.java
* @Company Beijing Panorama Media Inc.
* @author xiaolong.Tan 
* @date 2016年6月12日 下午3:08:26
* 
* result int 供参考的识别结果，0正常，1黄图，2疑似图片 
* confidence double 识别为黄图的置信度，范围0-100；是normal_score, hot_score, porn_score的综合评分 
* normal_score double 图片为正常图片的评分 
* hot_score double 图片为性感图片的评分 
* porn_score double 图片为色情图片的评分 
* forbid_status int 封禁状态，0表示正常，1表示图片已被封禁（只有存储在万象优图的图片才会被封禁） 
 */
@Component("imageValidate")
public class ImageValidate {
	

    private  int app_id;

	private String secret_id;

	private String secret_key;

	private String bucket;        //空间名
	
	@PostConstruct
	private void initial() {
		Properties properties = new Properties();
		try {
			properties.load(this.getClass().getResourceAsStream("/imageValidate.properties"));
			app_id = Integer.parseInt(properties.getProperty("app_id"));
			secret_id = properties.getProperty("secret_id");
			secret_key = properties.getProperty("secret_key");
			bucket = properties.getProperty("bucket");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
     
       
        /***
         * 图片评分
         * @param url
         * 图片地址
         * @return
         */
        public PornDetectInfo imageValidate(String url){
            PicCloud pc = new PicCloud(app_id, secret_id, secret_key,bucket);
            PornDetectInfo info = pc.pornDetect(url);
            return info;
        }
}
