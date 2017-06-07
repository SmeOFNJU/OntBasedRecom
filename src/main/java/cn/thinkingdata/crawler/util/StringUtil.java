package cn.thinkingdata.crawler.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

public class StringUtil {
	private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
	private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
	private static final String regEx_html = "<[^>]+>";
	
	public static String delHTMLTag(String htmlStr) {  
		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);  
		Matcher m_script = p_script.matcher(htmlStr);  
		htmlStr = m_script.replaceAll(""); // 过滤script标签  

		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);  
		Matcher m_style = p_style.matcher(htmlStr);  
		htmlStr = m_style.replaceAll(""); // 过滤style标签  

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);  
		Matcher m_html = p_html.matcher(htmlStr);  
		htmlStr = m_html.replaceAll(""); // 过滤html标签  
		
		htmlStr = StringEscapeUtils.unescapeHtml4(htmlStr);
		htmlStr= htmlStr.replaceAll("\r|\n", "");
		htmlStr= htmlStr.replaceAll("\t", "");
		htmlStr= htmlStr.replaceAll("\001|\002|\003", " ");
		return htmlStr.trim(); // 返回文本字符串  
	}
	
	public static String delScriptTag(String htmlStr){
		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);  
		Matcher m_script = p_script.matcher(htmlStr);  
		htmlStr = m_script.replaceAll(""); // 过滤script标签  
		return htmlStr.trim(); // 返回文本字符串  
	}
	
	
	public static String delUselessTag(String htmlStr) {  
		
		htmlStr = StringEscapeUtils.unescapeHtml4(htmlStr);
		htmlStr= htmlStr.replaceAll("\r|\n", "\\\\n");
		htmlStr= htmlStr.replaceAll("\t", "\\\\t");
		htmlStr= htmlStr.replaceAll("\001|\002|\003", " ");
		return htmlStr.trim(); // 返回文本字符串  
	}
	
	public static String getUrlHost(String url){
		Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
		Matcher m = p.matcher(url);
		if(m.find()){
			return m.group();
		}
		return null;
	}
	
	public static String delBuluoTag(String content)
	{
		String ret = content;
		ret = ret.replace("&nbsp;", " ");
		ret = ret.replace("&amp;", "&");
		ret = ret.replace("&quot;", "\"");
		ret = ret.replace("\n", "<br>");
		ret = ret.replace("\t", " ");
		ret = ret.replace("\r", "");
		ret = ret.replace("\001|\002\003", "");
		
		while(true)
		{
			int index = ret.indexOf("{{");
			if (index != -1)
			{
				int index2 = ret.indexOf("}}");
				if (index2 != -1)
				{
					ret = ret.substring(0, index) + ret.substring(index+2, index2-2) + ret.substring(index2+2);
				} 
				else
					break;
			}
			else
				break;
		}
		return ret;
	}
}
