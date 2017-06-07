package cn.thinkingdata.crawler.base;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Spliterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import cn.thinkingdata.crawler.exception.CrawlerException;
import cn.thinkingdata.crawler.http.method.GzipGetMethod;
import cn.thinkingdata.crawler.http.method.GzipPostMethod;
import cn.thinkingdata.crawler.util.DateUtil;

import com.google.common.base.Throwables;

public abstract class AbstractBaseCrawler {
	protected static Logger logger = LoggerFactory.getLogger(AbstractBaseCrawler.class);

	protected static final String CRAWL_DATA_BASE_DIR = "c://hexun/";

	public JdbcTemplate jdbcTemplateHive;
	public JdbcTemplate jdbcTemplateWeb;
	public JdbcTemplate jdbcTemplateGasNew;
	protected RedisTemplate<String, String> redisTemplate;
	protected Long scheduleId;

	protected Date date; 	
	protected String[] otherParams;
	protected String yestodayString;
	protected String dateString;

	public enum HttpMethodEnum {GET,POST};

	public abstract void run() throws CrawlerException;

	protected void deleteLocalFile(String filePath){
		if(filePath == null){
			return;
		}
		File file = new File(filePath);
		if(file.exists()){
			file.delete();
		}
	}

	protected void deleteLocalFile(Collection<String> filePathList){
		for(String filePath : filePathList){
			File file = new File(filePath);
			if(file.exists()){
				file.delete();
			}
		}
	}

	protected GetMethod instanceGetMethod(String url,String host){
		GetMethod getMethod = new GetMethod(url);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler()); 
		getMethod.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
		getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		getMethod.setRequestHeader("Host", host);
		return getMethod;
	}

	protected PostMethod instancePostMethod(String url,String host){
		PostMethod post = new PostMethod(url);
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler()); 
		post.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
		post.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		post.setRequestHeader("Host", host);
		return post;
	}


	protected GzipGetMethod instancGzipGetMethod(String url,String host){
		GzipGetMethod getMethod = new GzipGetMethod(url);
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler()); 
		getMethod.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
		getMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		getMethod.setRequestHeader("Host", host);
		return getMethod;
	}

	protected GzipPostMethod instancGzipPostMethod(String url,String host){
		GzipPostMethod post = new GzipPostMethod(url);
		post.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler()); 
		post.getParams().setParameter("http.protocol.cookie-policy",CookiePolicy.BROWSER_COMPATIBILITY);
		post.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:14.0) Gecko/20100101 Firefox/14.0.1");
		post.setRequestHeader("Host", host);
		return post;
	}
	
	
	protected String getUrlContent(String url, HttpMethodEnum httpMethodEnum, boolean isGzip,String host, Map<String, String> requestHeaderMap,String charSet) throws CrawlerException{
		return getUrlContent(url, httpMethodEnum, isGzip, host, requestHeaderMap, null,charSet);		
	}
	
	protected String getUrlContent(String url, HttpMethodEnum httpMethodEnum, boolean isGzip,String host, Map<String, String> requestHeaderMap) throws CrawlerException{
		return getUrlContent(url, httpMethodEnum, isGzip, host, requestHeaderMap, null,"utf-8");		
	}
	
	protected String getUrlContent(String url, HttpMethodEnum httpMethodEnum, boolean isGzip,String host, Map<String, String> requestHeaderMap, Map<String, String> requestDataMap) throws CrawlerException{
		return getUrlContent(url, httpMethodEnum,isGzip,host,requestHeaderMap,requestDataMap, "utf-8");
	}
	
	protected String getUrlContent(String url, HttpMethodEnum httpMethodEnum, boolean isGzip,String host, Map<String, String> requestHeaderMap, Map<String, String> requestDataMap, String charSet) throws CrawlerException{		
		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(90000); 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(90000);

		HttpMethodBase httpMethod = null;
		try{
			if(httpMethodEnum == HttpMethodEnum.GET){
				if(isGzip){
					httpMethod = instancGzipGetMethod(url, host);
				}else{
					httpMethod = instanceGetMethod(url, host);
				}
			}else if(httpMethodEnum == HttpMethodEnum.POST){
				if(isGzip){
					httpMethod = instancGzipPostMethod(url, host);
				}else{
					httpMethod = instancePostMethod(url, host);
				}
			}
			for(Entry<String, String> entry : requestHeaderMap.entrySet()){
				httpMethod.addRequestHeader(entry.getKey(), entry.getValue());
			}
			if(requestDataMap != null && requestDataMap.size() > 0 && httpMethodEnum == HttpMethodEnum.POST){
				for(Entry<String, String> entry : requestDataMap.entrySet()){
					PostMethod tempMethod = (PostMethod) httpMethod;
					tempMethod.addParameter(entry.getKey(), entry.getValue());
				}
			}		
			
			int statusCode = httpClient.executeMethod(httpMethod);
			
			if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_MOVED_TEMPORARILY && (!url.contains("gametanzi"))) {
				logger.error("http request failed!, http status code: " + statusCode + ", status text:" + httpMethod.getStatusText());
				throw new CrawlerException("http request failed!, http status code: " + statusCode + ", status text:" + httpMethod.getStatusText());
			}
			
			String cookieStr = getCookieFromHttpClient(httpClient);
			requestHeaderMap.put("Cookie", cookieStr);
			
			String responseStr = httpMethod.getResponseBodyAsString();
			String responseCharset = httpMethod.getResponseCharSet();
			
			responseStr = responseStr.replaceAll("[\u2028-\u2029]", "");
			return responseStr;
		}catch(Exception e){
			throw new CrawlerException(e);
		}finally{
			if(httpMethod != null){
				httpMethod.releaseConnection();
			}
		}
	}
	
	protected HttpResponseDo getUrlContentAndHeader(String url, HttpMethodEnum httpMethodEnum, boolean isGzip,String host, Map<String, String> requestHeaderMap, Map<String, String> requestDataMap, String charSet) throws CrawlerException{		
		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(90000); 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(90000);

		HttpMethodBase httpMethod = null;
		try{
			if(httpMethodEnum == HttpMethodEnum.GET){
				if(isGzip){
					httpMethod = instancGzipGetMethod(url, host);
				}else{
					httpMethod = instanceGetMethod(url, host);
				}
			}else if(httpMethodEnum == HttpMethodEnum.POST){
				if(isGzip){
					httpMethod = instancGzipPostMethod(url, host);
				}else{
					httpMethod = instancePostMethod(url, host);
				}
			}
			for(Entry<String, String> entry : requestHeaderMap.entrySet()){
				httpMethod.addRequestHeader(entry.getKey(), entry.getValue());
			}
			if(requestDataMap != null && requestDataMap.size() > 0 && httpMethodEnum == HttpMethodEnum.POST){
				for(Entry<String, String> entry : requestDataMap.entrySet()){
					PostMethod tempMethod = (PostMethod) httpMethod;
					tempMethod.addParameter(entry.getKey(), entry.getValue());
				}
			}		
			
			int statusCode = httpClient.executeMethod(httpMethod);
			
			if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_MOVED_TEMPORARILY) {
				logger.error("http request failed!, http status code: " + statusCode + ", status text:" + httpMethod.getStatusText());
				throw new CrawlerException("http request failed!, http status code: " + statusCode + ", status text:" + httpMethod.getStatusText());
			}
			
			String cookieStr = getCookieFromHttpClient(httpClient);
			requestHeaderMap.put("Cookie", cookieStr);
			Header headers[] = httpMethod.getResponseHeaders();
			Map<String, String> responseHeaderMap = new HashMap<>();
			for(Header header : headers){
				responseHeaderMap.put(header.getName(), header.getValue());
			}
			
			String responseStr = httpMethod.getResponseBodyAsString();
			String responseCharset = httpMethod.getResponseCharSet();
			if(responseCharset.equalsIgnoreCase("iso-8859-1")){
				responseStr =new String(responseStr.getBytes("ISO-8859-1"), charSet);
			}
			
			responseStr = responseStr.replaceAll("[\u2028-\u2029]", "");
			HttpResponseDo resDo = new HttpResponseDo(responseStr, responseHeaderMap);
			
			return resDo;
		}catch(Exception e){
			throw new CrawlerException(e);
		}finally{
			if(httpMethod != null){
				httpMethod.releaseConnection();
			}
		}
	}

	protected String getUrlContentWithRetry(String url, HttpMethodEnum httpMethodEnum, boolean isGzip,String host, Map<String, String> requestHeaderMap,int retryTimes,String charSet) throws CrawlerException{
		return getUrlContentWithRetry(url, httpMethodEnum, isGzip, host, requestHeaderMap, null, retryTimes,charSet);
	}
	
	protected String getUrlContentWithRetry(String url, HttpMethodEnum httpMethodEnum, boolean isGzip,String host, Map<String, String> requestHeaderMap,int retryTimes) throws CrawlerException{
		return getUrlContentWithRetry(url, httpMethodEnum, isGzip, host, requestHeaderMap, null, retryTimes,"utf-8");
	}

	protected String getUrlContentWithRetry(String url, HttpMethodEnum httpMethodEnum, boolean isGzip,String host, Map<String, String> requestHeaderMap,Map<String, String> requestDataMap,int retryTimes) throws CrawlerException{
		return getUrlContentWithRetry(url, httpMethodEnum, isGzip,host, requestHeaderMap, requestDataMap, retryTimes, "utf-8");
	}
	
	protected String getUrlContentWithRetry(String url, HttpMethodEnum httpMethodEnum, boolean isGzip,String host, Map<String, String> requestHeaderMap,Map<String, String> requestDataMap,int retryTimes,String charSet) throws CrawlerException{
		int failStaySeconds = 0;
		try{
			for(int i = 1; i <= retryTimes; i++){
				failStaySeconds += i * 5;
				try{
					String responseStr = getUrlContent(url, httpMethodEnum, isGzip, host,requestHeaderMap, requestDataMap,charSet);
					return responseStr;
				}catch(CrawlerException e){
					logger.info("--------------http request retry, retry times: " + i + "----------------");
					logger.warn(Throwables.getStackTraceAsString(e));
					TimeUnit.SECONDS.sleep(failStaySeconds);					
				}
			}
			logger.error("http request failed after retry times: " + retryTimes);
			throw new CrawlerException("http request failed after retry times: " + retryTimes);
		}catch(Exception e){
			throw new CrawlerException(e);
		}
	}

	protected String getFinalDataName(String tableName){
		String finalDataName = CRAWL_DATA_BASE_DIR + DateUtil.getShortDatePath(new Date()) + "/" + tableName + "/" + UUID.randomUUID().toString();
		//String finalDataName = CRAWL_DATA_BASE_DIR + tableName + "/" + dateString + "/" + DateUtil.getPreciseShortDateString(new Date());
		return finalDataName;
	}

	private String getCookieFromHttpClient(HttpClient httpClient){
		Cookie[] cookies = httpClient.getState().getCookies();
		StringBuilder sb = new StringBuilder();
		for(Cookie cookie : cookies){
			sb.append(cookie.toExternalForm()).append("; ");
		}
		if(sb.length() > 0){
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
	protected String getString(String patternStr, String content) throws CrawlerException{
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()){
			return matcher.group(1);
		}
		return "";
	}

	public JdbcTemplate getJdbcTemplateHive() {
		return jdbcTemplateHive;
	}
	public void setJdbcTemplateHive(JdbcTemplate jdbcTemplateHive) {
		this.jdbcTemplateHive = jdbcTemplateHive;
	}
	public JdbcTemplate getJdbcTemplateWeb() {
		return jdbcTemplateWeb;
	}
	public void setJdbcTemplateWeb(JdbcTemplate jdbcTemplateWeb) {
		this.jdbcTemplateWeb = jdbcTemplateWeb;
	}
	public JdbcTemplate getJdbcTemplateGasNew() {
		return jdbcTemplateGasNew;
	}
	public void setJdbcTemplateGasNew(JdbcTemplate jdbcTemplateGasNew) {
		this.jdbcTemplateGasNew = jdbcTemplateGasNew;
	}
	public RedisTemplate<String, String> getRedisTemplate() {
		return redisTemplate;
	}
	public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
	public Long getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
		dateString = DateUtil.getPartitionString(date);
		yestodayString = DateUtil.getOffsetDatePartitionString(date, -1);		
	}
	public String[] getOtherParams() {
		return otherParams;
	}
	public void setOtherParams(String[] otherParams) {
		this.otherParams = otherParams;
	}

}

