package cn.thinkingdata.crawler.task.hexun;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringEscapeUtils;

import com.google.common.base.Throwables;

import cn.thinkingdata.crawler.base.AbstractBaseCrawler;
import cn.thinkingdata.crawler.base.AbstractBaseCrawler.HttpMethodEnum;
import cn.thinkingdata.crawler.data.obj.GameArticleData;
import cn.thinkingdata.crawler.exception.CrawlerException;
import cn.thinkingdata.crawler.util.DateUtil;
import cn.thinkingdata.crawler.util.StringUtil;

public class HeXunCrawler extends AbstractBaseCrawler {
	private static HashSet<String> crawledUrlSet = new HashSet<>();

	private static final String PAGE_URL_PRE = "http://roll.hexun.com/roolNews_listRool.action?type=all&ids=108&date=%s&page=%d";
	private static final String HOST = "roll.hexun.com";
	private static final String DUMP_FILE = "C://hexun/hexun.txt";
	
	private static final boolean CRAWL_TOTAL = false;

	@Override
	public void run() throws CrawlerException {
		try{
			String startDate = "2017-03-01";
			BufferedWriter writer = new BufferedWriter(new FileWriter(DUMP_FILE));
			
			while(true){
				HashMap<String, String> headerMap = new HashMap<>();
				headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				headerMap.put("Accept-Language", "zh-CN");
				headerMap.put("Content-Type", "text/html; charset=utf-8");
				
				int maxPage = 50;
				for (int page = 1; page < maxPage; page++){
					try{
						String url = String.format(PAGE_URL_PRE, startDate, page);
						String urlContent = getUrlContentWithRetry(url, HttpMethodEnum.GET, false, HOST, headerMap, 3);
						logger.info("get title list:" + url);
						TimeUnit.MILLISECONDS.sleep(5);
						JSONObject jsonObject = JSONObject.fromObject(urlContent);
						int sum = Integer.parseInt(jsonObject.getString("sum"));
						maxPage = sum/30 + 1;
						
						JSONArray jsonArray = jsonObject.getJSONArray("list");
						for (int i = 0; i < jsonArray.size(); i++){
							JSONObject tmpObject = jsonArray.getJSONObject(i);
							String tmpUrl = tmpObject.getString("titleLink").replace(" ", "");
							String tmpUrlContent = getUrlContentWithRetry(tmpUrl, HttpMethodEnum.GET, false, HOST, headerMap, 3);
							tmpUrlContent = new String(tmpUrlContent.getBytes("iso-8859-1"),"gbk");
							logger.info("get article:" + tmpUrl);
							TimeUnit.MILLISECONDS.sleep(5);
							String title = getNeedString(tmpUrlContent, "<h1>(.*?)</h1>");
							String publishTime = getNeedString(tmpUrlContent, "<span class=\"pr20\">(.*?)</span>");
							String content = StringUtil.delHTMLTag(getNeedString(tmpUrlContent, "<div class=\"art_contextBox\">(.*?)<div"));
							writer.write(title + "\t" + publishTime + "\t" + tmpUrl + "\t" + content);
							writer.newLine();
							writer.flush();
						}
					}
					catch(Exception e){
						logger.error(Throwables.getStackTraceAsString(e));
						continue;
					}
				}
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟   
				java.util.Date date= sdf.parse(startDate);  
				startDate = DateUtil.tomorrow(date);
				if (startDate.compareTo("2017-04-01") >= 0){
					break;
				}
			}
			
		}catch(Exception e){
			logger.error(Throwables.getStackTraceAsString(e));
			throw new CrawlerException(e);
		}finally{
			
		}
	}
	
	public String getNeedString (String content, String patternStr){
		Pattern pattern = Pattern.compile(patternStr, Pattern.DOTALL);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()){
			return matcher.group(1);
		}
		return "";
	}
}
