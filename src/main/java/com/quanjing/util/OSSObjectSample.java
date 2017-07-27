package com.quanjing.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.codec.digest.Md5Crypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;



/**
 * 该示例代码展示了如果在OSS中创建和删除一个Bucket，以及如何上传和下载一个文件。
 * 
 * 该示例代码的执行过程是：
 * 1. 检查指定的Bucket是否存在，如果不存在则创建它；
 * 2. 上传一个文件到OSS；
 * 3. 下载这个文件到本地；
 * 4. 清理测试资源：删除Bucket及其中的所有Objects。
 * 
 * 尝试运行这段示例代码时需要注意：
 * 1. 为了展示在删除Bucket时除了需要删除其中的Objects,
 *    示例代码最后为删除掉指定的Bucket，因为不要使用您的已经有资源的Bucket进行测试！
 * 2. 请使用您的API授权密钥填充ACCESS_ID和ACCESS_KEY常量；
 * 3. 需要准确上传用的测试文件，并修改常量uploadFilePath为测试文件的路径；
 *    修改常量downloadFilePath为下载文件的路径。
 * 4. 该程序仅为示例代码，仅供参考，并不能保证足够健壮。
 *
 */
@Component("oSSObject")
public class OSSObjectSample {
	
	private static final Logger logger = LoggerFactory.getLogger(OSSObjectSample.class);
	
	@Value("${OSS_ACCESS_ID}")
    private   String ACCESS_ID ;
	@Value("${OSS_ACCESS_KEY}")
    private   String ACCESS_KEY;
	@Value("${OSS_ENDPOINT}")
    private   String OSS_ENDPOINT ;
	@Value("${OSS_bucketName}")
    private   String bucketName ;
	
    
    
    public  void  delete(String url) {
  	  ClientConfiguration config = new ClientConfiguration();
        OSSClient client = new OSSClient(this.OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY, config);
        ensureBucket(client,bucketName);
        setBucketPublicReadable(client,bucketName);
        logger.warn("delete image :"+url);
        try {
      	  String key=url.replace("http://"+bucketName+".oss.aliyuncs.com/", "");
      	  client.deleteObject(bucketName, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
  }
    
    @Deprecated
    private  void  replace(String url, InputStream is){
    	  ClientConfiguration config = new ClientConfiguration();
          OSSClient client = new OSSClient(this.OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY, config);
          ensureBucket(client,bucketName);
          setBucketPublicReadable(client,bucketName);
          ObjectMetadata objectMeta = new ObjectMetadata();
         // objectMeta.setContentLength(file.length());
          // 可以在metadata中标记文件类型
          objectMeta.setContentType("image/jpeg");
          
          logger.warn("replace image :"+url);
          try {
        	  String key=url.replace("http://"+bucketName+".oss.aliyuncs.com/", "");
  			  client.putObject(bucketName, key, is, objectMeta);
  		} catch (Exception e) {
  			e.printStackTrace();
  		}
    }
    	
   
  	
	
	
	public  String  save(String md5, MultipartFile file)   throws Exception{
	      InputStream is = null;
        try {
          is = file.getInputStream();
        } catch (IOException e) {
          e.printStackTrace();
        }
    	  return saveByInputStream(md5 , file.getOriginalFilename() , is );
	}
   
	//增加上传流接口
	public  String  saveByInputStream(String md5, String fileName , InputStream is) throws Exception{
    
    logger.info("md5="+md5);
    String filePath=md5.substring(0, 3)+"/"+md5.substring(3, 6)+"/"+md5;
    // 可以使用ClientConfiguration对象设置代理服务器、最大重试次数等参数。
      ClientConfiguration config = new ClientConfiguration();
      OSSClient client = new OSSClient(this.OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY, config);

      ensureBucket(client,bucketName);
      setBucketPublicReadable(client,bucketName);
      // 获取Object，返回结果为OSSObject对象
      logger.info("bucketName=" + bucketName);
     
      logger.debug("正在上传...");
//          fileName = System.currentTimeMillis() + "." +getExtensionName(fileName);
      String url = uploadPic(client, bucketName, filePath  , is.available(),  fileName , is);
      logger.info("success,url="+url);
      return url;
}
	//增加上传流接口用户图片
	public  String  saveUserImgByInputStream(String md5, String fileName , InputStream is,long fileSize) throws Exception{
		
		logger.info("md5="+md5);
		String filePath=md5.substring(0, 3)+"/"+md5.substring(3, 6)+"/"+md5;
		// 可以使用ClientConfiguration对象设置代理服务器、最大重试次数等参数。
		ClientConfiguration config = new ClientConfiguration();
		OSSClient client = new OSSClient(this.OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY, config);
		
		ensureBucket(client,bucketName);
		setBucketPublicReadable(client,bucketName);
		// 获取Object，返回结果为OSSObject对象
		logger.info("bucketName=" + bucketName);
		
		logger.debug("正在上传...");
//          fileName = System.currentTimeMillis() + "." +getExtensionName(fileName);
		String url = uploadPic(client, bucketName, filePath  , fileSize,  fileName , is);
		logger.info("success,url="+url);
		return url;
	}
  
	
	
	public  String  saveSmallPic(String md5, InputStream  is,String type,int width) throws Exception{
		
    	logger.info("md5="+md5);
    	String filePath=md5.substring(0, 3)+"/"+md5.substring(3, 6)+"/"+md5;
    	// 可以使用ClientConfiguration对象设置代理服务器、最大重试次数等参数。
        ClientConfiguration config = new ClientConfiguration();
        OSSClient client = new OSSClient(this.OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY, config);

        ensureBucket(client,bucketName);
        setBucketPublicReadable(client,bucketName);
        // 获取Object，返回结果为OSSObject对象
        logger.info("bucketName=" + bucketName);
       
        logger.debug("正在上传...");
        
        ObjectMetadata objectMeta = new ObjectMetadata();
        // 可以在metadata中标记文件类型
        
        objectMeta.setContentType("image/jpeg");
        
        String key = width+"."+type;
        String fname=filePath+"/"+key;
        logger.info(fname);
        client.putObject(bucketName, fname, is, objectMeta);
        
        return "http://"+bucketName+".oss.aliyuncs.com/"+fname;
        
	}
    
	
	
    /*
     * Java文件操作 获取文件扩展名
     *
     */
    public static String getExtensionName(String filename) { 
        if ((filename != null) && (filename.length() > 0)) { 
            int dot = filename.lastIndexOf('.'); 
            if ((dot >-1) && (dot < (filename.length() - 1))) { 
                return filename.substring(dot + 1); 
            } 
        } 
        return filename; 
    } 

    
   

    // 如果Bucket不存在，则创建它。
    private static void ensureBucket(OSSClient client, String bucketName)
            throws OSSException, ClientException{

        if (client.doesBucketExist(bucketName)){
        	logger.info(bucketName+" Bucket Exist!!!");
            return;
        }

        //创建bucket
        client.createBucket(bucketName);
    }

    // 删除一个Bucket和其中的Objects 
    private static void deleteBucket(OSSClient client, String bucketName)
            throws OSSException, ClientException {

        ObjectListing ObjectListing = client.listObjects(bucketName);
        List<OSSObjectSummary> listDeletes = ObjectListing
                .getObjectSummaries();
        for (int i = 0; i < listDeletes.size(); i++) {
            String objectName = listDeletes.get(i).getKey();
            // 如果不为空，先删除bucket下的文件
            client.deleteObject(bucketName, objectName);
        }
        client.deleteBucket(bucketName);
    }

    // 把Bucket设置为所有人可读
    private static void setBucketPublicReadable(OSSClient client, String bucketName)
            throws OSSException, ClientException {
        //创建bucket
    	if(!client.doesBucketExist(bucketName)){
    		 client.createBucket(bucketName);
    		//设置bucket的访问权限
    	    client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
    	}
    	
        
    }
    
    // 上传文件
    private static String uploadPic(OSSClient client, String bucketName, String filePath , long fileSize, String fileName , InputStream is)
            throws OSSException, ClientException, FileNotFoundException {
        ObjectMetadata objectMeta = new ObjectMetadata();
        objectMeta.setContentLength(fileSize);
        // 可以在metadata中标记文件类型
        String type="";
    
      type = getExtensionName(fileName);
    
        if(type==null){
          throw new ClientException("图片格式有误");
        }
        objectMeta.setContentType("image/jpeg");
        
        String key = EncryptUtils.baseMd5Encode(fileName)+"."+type;
        String fname=filePath+"/"+key;
        logger.info(fname);
        PutObjectResult ret=client.putObject(bucketName, fname, is, objectMeta);
        
        return "http://"+bucketName+".oss.aliyuncs.com/"+fname;
    }

    // 下载文件
    private static void downloadFile(OSSClient client, String bucketName, String key, String filename)
            throws OSSException, ClientException {
        client.getObject(new GetObjectRequest(bucketName, key),
                new File(filename));
    }
    
    public String uploadFile(String md5, MultipartFile file, String path, String contentType) {
    	InputStream is = null;
        try {
          is = file.getInputStream();
        } catch (IOException e) {
          e.printStackTrace();
        }
        logger.info("md5="+md5);
        String filePath = path + md5.substring(0, 3)+"/"+md5.substring(3, 6)+"/"+md5;
        // 可以使用ClientConfiguration对象设置代理服务器、最大重试次数等参数。
          ClientConfiguration config = new ClientConfiguration();
          OSSClient client = new OSSClient(this.OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY, config);

          ensureBucket(client,bucketName);
          setBucketPublicReadable(client,bucketName);
          logger.debug("正在上传文件...");
          ObjectMetadata objectMeta = new ObjectMetadata();
          objectMeta.setContentLength(file.getSize());
          if (StringUtils.isEmpty(contentType)) {
        	  objectMeta.setContentType(contentType);
          }
          
          String type= getExtensionName(file.getOriginalFilename());
          if(type==null){
            throw new ClientException("文件格式有误");
          }
          String key = EncryptUtils.baseMd5Encode(file.getOriginalFilename())+"."+type;
          String fname=filePath+"/"+key;
          logger.info(fname);
          client.putObject(bucketName, fname, is, objectMeta);
          
          return "http://"+bucketName+".oss.aliyuncs.com/"+fname;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)throws Exception {
    	
    	
      String ACCESS_ID = "Eqb5wEvN1pFq3yMZ";
      String OSS_ENDPOINT="http://oss.aliyuncs.com/";
      String ACCESS_KEY="82ZIxA3em4Hg5vw0rv5PadADczFhQu";
      String bucketName = "quanjing-photo";
      // 可以使用ClientConfiguration对象设置代理服务器、最大重试次数等参数。
      ClientConfiguration config = new ClientConfiguration();
      OSSClient client = new OSSClient(OSS_ENDPOINT, ACCESS_ID, ACCESS_KEY, config);
   // 获取用户的Bucket列表
      List<Bucket> buckets = client.listBuckets();

      // 遍历Bucket
      for (Bucket bucket : buckets) {
          System.out.println(bucket.getName());
      }
      ensureBucket(client, bucketName);
      
      try {
    	  client.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);

      } catch(OSSException e){ 
      	e.printStackTrace();
      	if(e.getErrorCode().equals("NoSuchKey")){
      		//文件不存在
      		System.out.println("文件不存在");
//      		client.c
      	}
      }
      
    	//System.out.println(getExtensionName("xhs.jpg"));
    	//print();
    }
    
    
}
