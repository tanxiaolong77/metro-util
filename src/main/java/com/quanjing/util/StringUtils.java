package com.quanjing.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 
 * @author 
 *
 */
public class StringUtils {
	
	private static String randString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static boolean isLetter(char c) {
		int k = 0x80;
		return c / k == 0 ? true : false;
	}

    /**
     * http://domain or http://domain/
     * return  http://domain/
     * @param domain
     * @return
     */
    public static String domainSplit(String domain){
        if(StringUtils.isEmpty(domain)) return "/";
        if(domain.trim().endsWith("/")){
            domain = domain.trim();
            domain = domain.substring(0,domain.length()-1);
        }
        domain = domain+"/";
        return  domain;
    }
	/**
	 * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
	 * 
	 * @param s
	 *            需要得到长度的字符串
	 * @return i得到的字符串长度
	 */
	public static int strlength(String s) {
		if (s == null)
			return 0;
		char[] c = s.toCharArray();
		int len = 0;
		for (int i = 0; i < c.length; i++) {
			len++;
			if (!isLetter(c[i])) {
				len++;
			}
		}
		return len;
	}

	/**
	 * 截取一段字符的长度,不区分中英文,如果数字不正好，则少取一个字符位
	 * 
	 * 
	 * @param origin
	 *            原始字符串
	 * @param len
	 *            截取长度(一个汉字长度按2算的)
	 * @param c
	 *            后缀
	 * @return 返回的字符串
	 */
	public static String tosubstring(String origin, int len, String c) {
		if (origin == null || origin.equals("") || len < 1)
			return "";
		byte[] strByte = new byte[len];
		if (len > strlength(origin)) {
			return origin + c;
		}
		try {
			System.arraycopy(origin.getBytes("GBK"), 0, strByte, 0, len);
			int count = 0;
			for (int i = 0; i < len; i++) {
				int value = (int) strByte[i];
				if (value < 0) {
					count++;
				}
			}
			if (count % 2 != 0) {
				len = (len == 1) ? ++len : --len;
			}
			origin = "";
			return new String(strByte, 0, len, "GBK") + c;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String join(Collection<String> list, String split) {
		StringBuilder buf = new StringBuilder();
		for (String str : list) {
			if (null != str && !("").equals(str)) {
				buf.append(str).append(split);
			}
		}
		return removeEndLetter(buf.toString());
	}
	
	public static String removeEndLetter(String str) {
		if (null == str || str.length() <= 0)
			return "";
		return str.substring(0, str.length() - 1);
	}
	
	
	public static int getWordCountRegex(String s) {
		s = s.replaceAll("[^\\x00-\\xff]", "**");
		int length = s.length();
		return length;
	}  
  
	/** 
     * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1 
     *  
     * @param s 需要得到长度的字符串 
     * @return i得到的字符串长度 
     */  
    public static int length(String s) {  
        if (s == null)  
            return 0;  
        char[] c = s.toCharArray();  
        int len = 0;  
        for (int i = 0; i < c.length; i++) {  
            len++;  
            if (!isLetter(c[i])) {  
                len++;  
            }  
        }  
        return len;  
    }  
  
    /** 
     * 截取一段字符的长度,不区分中英文,如果数字不正好，则少取一个字符位 
     *  
     *  
     * @param  origin 原始字符串 
     * @param len 截取长度(一个汉字长度按2算的) 
     * @param c 后缀            
     * @return 返回的字符串 
     */  
    public static String substring(String origin, int len) {  
        if (origin == null || origin.equals("") || len < 1)  
            return "";  
        byte[] strByte = new byte[len];  
        if (len > length(origin)) {  
            return origin;  
        }  
        try {  
            System.arraycopy(origin.getBytes("GBK"), 0, strByte, 0, len);  
            int count = 0;  
            for (int i = 0; i < len; i++) {  
                int value = (int) strByte[i];  
                if (value < 0) {  
                    count++;  
                }  
            }  
            if (count % 2 != 0) {  
                len = (len == 1) ? ++len : --len;  
            }  
            return new String(strByte, 0, len, "GBK");  
        } catch (UnsupportedEncodingException e) {  
            throw new RuntimeException(e);  
        }  
    } 
    
    private static final String pinyinRegex = "^[A-Za-z]*$";
    /**
     * 判断是否全是拼音或者字母
     * @param str
     * @return
     */
    public static boolean isPinyin(String str) {
    	if (null == str) {
    		return false;
    	}
    	Pattern pattern = Pattern.compile(pinyinRegex);
    	Matcher matcher = pattern.matcher(str);
    	matcher.find();
    	return matcher.matches();
    }

    



    
   
    
    /**
     * right trim "trim"
     * @param str
     * @param trim
     * @return
     */
    public static String TrimRight(String str,String trim){
    	if(str!=null&&!str.trim().equals("")){
//    		if(str.trim().substring(0,1).equals(",")){
//    			str = str.substring(1,str.length());
//    		}
    		if(str.trim().substring(str.length()-trim.length(),str.length()).equals(trim)){
    			str = str.substring(0,str.length()-trim.length());
    		}
    	}
    	return str;
    }
    
    /**
     * 判定string的包含关系   str1是否包含str2
     * @return  -1  不包含
     * 					0  包含并且完全相同
     * 					1  包含
     */
    public static Integer checkStrInclude(String str1,String str2){
    	if(str1!=null&&!str1.equals("")&&str2!=null&&!str2.equals("")){
    		if(str1.equals(str2)){
    			return 0;
    		}else{
    			if(str1.indexOf(str2)<0){
    				return str1.indexOf(str2);//return 1;
    			}else{
    				return 1;
    			}
    		}
    	}else{
    		return null;
    	}
    }
    /**
     * 生成数字字母随机字符串
     */
    public static String randomStr(int count) {
    	StringBuffer buf = new StringBuffer();
    	Random random = new Random();
    	for(int i = 0; i < count; i++) {
    		buf.append(randString.charAt(random.nextInt(randString.length())));
    	}
    	return buf.toString();
    }
    
    
	public static String getUUID(){
		   String s = UUID.randomUUID().toString();    
		   //去掉“-”符号    
		   return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);    
 }
	/**
	 * 判断是否为空
	 * @param str
	 * @return
	 */
	public static boolean isNullOrEmpty(String str){
    	if(null == str || "".equals(str))
    		return true;
    	return false;
    }
	
	/**
	 * 判断是否为空
	 * @param str
	 * @return
	 */
    public static boolean isNullOrEmpty(Object str){
    	if(null == str || "".equals(str))
    		return true;
    	return false;
    }
	// 判断是否为空�?
		public static boolean isEmpty(String Value) {
			return (Value == null || Value.trim().equals(""));
		}

		/*
		 * @function:判空 @author:
		 */
		public static boolean isEmpty(List list) {
			if (list == null || list.size() == 0)
				return true;
			else
				return false;
		}

		/*
		 * @function:判空 @author:
		 */
		public static boolean isEmpty(Set set) {
			if (set == null || set.size() == 0)
				return true;
			else
				return false;
		}

		/*
		 * @function:判空 @author:
		 */
		public static boolean isEmpty(Map map) {
			if (map == null || map.size() == 0)
				return true;
			else
				return false;
		}

		// 判断是否为空�?
		public static boolean isEmpty(Object Value) {
			if (Value == null)
				return true;
			else
				return false;
		}

		public static boolean isEmpty(StringBuffer value) {
			if (value == null || value.length() <= 0)
				return true;
			else
				return false;
		}

		public static boolean isEmpty(Double value) {
			if (value == null || value.doubleValue() == 0.0)
				return true;
			else
				return false;
		}

		// 判断是否为空�?
		public static boolean isEmpty(Long obj) {
			if (obj == null || obj.longValue() == 0)
				return true;
			else
				return false;
		}

		// 判断是否为空�?
		public static boolean isEmpty(Object[] Value) {
			if (Value == null || Value.length == 0)
				return true;
			else
				return false;
		}
		
		public static boolean isNotNullEmpty(String str){
	    	if(null != str || !"".equals(str))
	    		return true;
	    	return false;
	    }
	
		/**
		 * 判断是不是手机号
		 * ^1[3|4|5|8][0-9]\\d{4,8}$
		 * */
		public static boolean isPhoneNumber(String phoneNumber){
			// && phoneNumber.matches("^(1[3|4|5|7|8][0-9])\\d{8}$")
			if(!isEmpty(phoneNumber)&&phoneNumber.length() == 11){
				return true;
			}else{
				return false;
			}
		}
		

		
		/**
		 * 判断是不是邮箱
		 * ^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+$
		 * ^[a-zA-Z0-9_]+@[a-zA-Z0-9]+(\\.[a-zA-Z]+)+$
		 * */
		public static boolean isEmail(String email){
			if(!isEmpty(email)&&email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+$")){
				return true;
			}else{
				return false;
			}
		}
		
		/**
		 * 判断密码
		 * */
		public static boolean isPassWord(String pass){
			if(!isNullOrEmpty(pass)&&pass.length()>5&&pass.length()<21){
				return true;
			}else{
				return false;
			}
		}
		
		
		// 过滤特殊字符 
	    public   static   String StringFilter(String   str)   throws   PatternSyntaxException   {   
	            // 只允许字母和数字       
	           // String   regEx  =  "[^a-zA-Z0-9]";                     
	           // 清除掉所有特殊字符  
	           String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";  
	          Pattern   p   =   Pattern.compile(regEx);     
	          Matcher   m   =   p.matcher(str);     
	          return   m.replaceAll("").trim();     
	    }
	    
	    
		
		  public static String IDCardValidate(String IDStr) throws ParseException {
		         String errorInfo=null ;// 记录错误信息
		         String[] ValCodeArr = { "1", "0", "x", "9", "8", "7", "6", "5", "4",
		                 "3", "2" };
		         String[] Wi = { "7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
		                 "9", "10", "5", "8", "4", "2" };
		         String Ai = "";
		         // ================ 号码的长度 15位或18位 ================
		         if (IDStr.length() != 15 && IDStr.length() != 18) {
		             errorInfo = "身份证号码长度应该为15位或18位。";
		             return errorInfo;
		         }
		         // =======================(end)========================
		 
		        // ================ 数字 除最后以为都为数字 ================
		         if (IDStr.length() == 18) {
		             Ai = IDStr.substring(0, 17);
		         } else if (IDStr.length() == 15) {
		             Ai = IDStr.substring(0, 6) + "19" + IDStr.substring(6, 15);
		         }
		         if (isNumeric(Ai) == false) {
		             errorInfo = "身份证15位号码都应为数字 ; 18位号码除最后一位外，都应为数字。";
		             return errorInfo;
		         }
		         // =======================(end)========================
		 
		        // ================ 出生年月是否有效 ================
		         String strYear = Ai.substring(6, 10);// 年份
		         String strMonth = Ai.substring(10, 12);// 月份
		         String strDay = Ai.substring(12, 14);// 月份
		         Date bithday=TimeUtil.strToDate(strYear   + strMonth  + strDay, "yyyyMMdd");
		         
		         if (bithday==null) {
		             errorInfo = "身份证生日无效。";
		             return errorInfo;
		         }
		         GregorianCalendar gc = new GregorianCalendar();
		         try {
		             if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 100|| (gc.getTime().getTime() - bithday.getTime()) < 0) {
		                 errorInfo = "身份证生日不在有效范围。";
		                 return errorInfo;
		             }
		         } catch (NumberFormatException e) {
		             e.printStackTrace();
		         }
		         if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) == 0) {
		             errorInfo = "身份证月份无效";
		             return errorInfo;
		         }
		         if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
		             errorInfo = "身份证日期无效";
		             return errorInfo;
		         }
		         // =====================(end)=====================
		 
		         
		        // ================ 判断最后一位的值 ================
		         int TotalmulAiWi = 0;
		         for (int i = 0; i < 17; i++) {
		             TotalmulAiWi = TotalmulAiWi
		                     + Integer.parseInt(String.valueOf(Ai.charAt(i)))
		                     * Integer.parseInt(Wi[i]);
		         }
		         int modValue = TotalmulAiWi % 11;
		         String strVerifyCode = ValCodeArr[modValue];
		         Ai = Ai + strVerifyCode;
		 
		        if (IDStr.length() == 18) {
		             if (Ai.equals(IDStr) == false) {
		                 errorInfo = "身份证无效，不是合法的身份证号码";
		                 return errorInfo;
		             }
		         }
		        
		         // =====================(end)=====================
		         return errorInfo;
		     }
		  
		  
		  	/**
		      * 功能：判断字符串是否为数字
		      * 
		      * @param str
		      * @return
		      */
		     public static boolean isNumeric(String str) {
		         Pattern pattern = Pattern.compile("[0-9]*");
		         Matcher isNum = pattern.matcher(str);
		         if (isNum.matches()) {
		             return true;
		         } else {
		             return false;
		         }
		     }
		     
		     
		
		/**
		 * 生成5位随机数字
		 * */
		public static int randomNum() {
			
			Random random = new Random();
	
			int num = -1;
	
			while (true) {
	
				num = (int) (random.nextDouble() * (100000 - 10000) + 10000);
	
				if (!(num + "").contains("4"))
					break;
	
			}
	
			return num;
		}
		
		/**
		 * 取字符串中间某一段以其他字符代替
		 * 
		 */
		public static String subString(String oldstr,int begin,int end,String replace) {
			String returnstr;
			if(isEmpty(oldstr)||oldstr.length()<end){
				returnstr = null;
			}else{
				String a;
				String b;
				a = oldstr.substring(0, begin);
				b = oldstr.substring(end,oldstr.length());
				returnstr = a+replace+b;
			}
			return returnstr;
		}
		
		/**
		 * 获取6位随机数字
		 * 
		 */
		public static String getRandomNum(){
			Random random = new Random();
			double num = random.nextDouble();
			String code = "" + num;
			code = code.substring(2,8);
			return code;
		}
		
	
		public static String random(String prefix,int count){
			prefix=prefix+TimeUtil.dateToStr(new Date(),"yyMMdd");
			return prefix+randomStr(count);
		}

	/**
	 * 判断是否含有敏感词
	 * 
	 * @param comment
	 * @return
	 */
	public static boolean checkValid(String comment) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		String encodeComment = null;
		try {
			encodeComment = URLEncoder.encode(comment, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			encodeComment = comment;
		}
		paramMap.put("text", encodeComment);
		String json = HttpClientUtil.sendHttpRequest("get", "http://mapp.tiankong.com/valid/textcheck", paramMap);
		return "0".equals(json);
	}
	
	/**
	 * 判断是否有注入风险
	 * @param str
	 * @return
	 */
	public static boolean isExistSqlRisk(String str) {
		str = str.toUpperCase();
		if (str.indexOf("DELETE ") >= 0 || str.indexOf("UPDATE ") >= 0 || str.indexOf("SELECT ") >= 0
				|| str.indexOf("UNION ") >= 0 || str.indexOf("COUNT(") >= 0 || str.indexOf(" OR ") >= 0
				|| str.indexOf(" AND ") >= 0 || str.indexOf("DROP ") >= 0 || str.indexOf("%") >= 0
				|| str.indexOf("*") >= 0 || str.indexOf("CONCAT ") >= 0 || str.indexOf("INSERT INTO") >= 0) {
			return true;
		}
		return false;
	}
	
	public static String getExceptionAllInfo(Exception ex) {
		String sOut = ex.getMessage();
		StackTraceElement[] trace = ex.getStackTrace();
		for (StackTraceElement s : trace) {
			sOut += "\t " + s + "\r\n";
		}
		return sOut;
	}
	
}
