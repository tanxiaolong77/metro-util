package com.quanjing.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;


/**
 * @author
 * 
 */
public class EmailUtil {
	
	private static String mail_smtp_host;
	private static String mail_smtp_account;
	private static String mail_smtp_password;
	

	private static BlockingQueue<Map> sendQueue = new LinkedBlockingQueue<Map>(20000);
	
	private static Set<String> hasSend = new HashSet<String>();
	private final static int keepSend = 120;
	
	
	
	private static EmailUtil _instance = null;
    
	public static EmailUtil getInstance() {
		if (_instance == null)
			_instance = new EmailUtil();
		return _instance;
	}
    
	private EmailUtil() {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("/application.properties"); 
		  Properties p = new Properties();   
		  try {   
		   p.load(inputStream);
		   mail_smtp_host = p.getProperty("mail.smtp.host");
		   mail_smtp_account = p.getProperty("mail.smtp.account");
		   mail_smtp_password = p.getProperty("mail.smtp.password");
		  } catch (IOException e1) {   
		   e1.printStackTrace();   
		  }
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					Map mail = null;
					try {
						mail = sendQueue.take();
						if (mail != null) {
							Set<String> file = (Set<String>) mail.get("file");
							if(file!=null&&!file.isEmpty()){
								realSend((String) mail.get("title"), (String) mail
										.get("content"),file, (String[]) mail
										.get("receivers"));
							}else{
								realSend((String) mail.get("title"), (String) mail
										.get("content"), (String[]) mail.get("receivers"));
							}
							
						}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}).start();
		
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(keepSend*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (true) {
					hasSend.clear();
					try {
						Thread.sleep(keepSend*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
	}

	
	
	public void send(String title, String content, String... receivers) {
		Map map = new HashMap();
		map.put("title", title);
		map.put("content", content);
		map.put("receivers", receivers);
		String md5=EncryptUtils.Md5Encode(content);
		if(!hasSend.contains(md5)){
			hasSend.add(md5);
			sendQueue.add(map);
		}
		
	}
	
	public void send(String title,String content,Set<String> file,String... receivers) {
		Map map = new HashMap();
		map.put("title", title);
		map.put("content", content);
		map.put("receivers", receivers);
		map.put("file", file);
		sendQueue.add(map);
	}
	
	/**
	 * 发送邮件
	 * 
	 * @param tto
	 * @param ttitle
	 * @param tcontent
	 * @param toName
	 */
	private static void realSend(String title, String content,
			Set<String> file, String... receivers) {
		Properties props = new Properties();
		props.put("mail.smtp.host", mail_smtp_host);// 其实这一部没加也发现有什么错
		props.put("mail.smtp.auth", "true");
		Session s = Session.getInstance(props);
		Address[] tos = null;
		MimeMessage message = new MimeMessage(s);
		try {
			InternetAddress from = new InternetAddress(mail_smtp_account);// 设置写信人
			message.setFrom(from);
			message.setSubject(title);// 设置主题
			message.setSentDate(new Date());// 设置时间
			if (receivers != null) {
				// 为每个邮件接收者创建一个地址
				tos = new InternetAddress[receivers.length];
				for (int i = 0; i < receivers.length; i++) {
					String toname = receivers[i];
					tos[i] = new InternetAddress(toname);
				}
			}
			// 将所有接收者地址都添加到邮件接收者属性中
			message.setRecipients(Message.RecipientType.TO, tos);
			// 给消息对象设置内容
			BodyPart mdp = new MimeBodyPart();// 新建一个存放信件内容的BodyPart对象
			mdp.setContent(content, "text/html;charset=gbk"); // 给BodyPart对象设置内容和格式/编码方式
			Multipart multipart = new MimeMultipart();// 新建一个MimeMultipart对象用来存放对象
			multipart.addBodyPart(mdp);// 将BodyPart加入到MimeMultipart对象中
			
			if (file!=null&&!file.isEmpty()) {// 有附件
				Iterator<String> it=file.iterator();
				while (it.hasNext()) {
					mdp = new MimeBodyPart();
					String filename =it.next(); // 选择出每一个附件名
					FileDataSource fds = new FileDataSource(filename); // 得到数据源
					mdp.setDataHandler(new DataHandler(fds)); // 得到附件本身并至入BodyPart
					// contentPart.setFileName(fds.getName());
					// //得到文件名同样至入BodyPart，并解决中文名乱码问题
					mdp.setFileName(MimeUtility.encodeText(fds.getName()));
					multipart.addBodyPart(mdp);
				}
				
			}
			message.setContent(multipart);// 把multipart作为消息对象的内容
			message.saveChanges();
			Transport transport = s.getTransport("smtp");
			transport.connect(mail_smtp_host, mail_smtp_account,mail_smtp_password);// 
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void realSend(String title, String content,
			String... receivers) {
		realSend(title,content,null,receivers);
	}
	public static void main(String[] args) {
		EncryptUtils encryptUtils = new EncryptUtils();
	}
	
	
}
