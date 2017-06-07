package cn.thinkingdata.crawler.main;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.common.base.Throwables;

import cn.thinkingdata.crawler.base.AbstractBaseCrawler;
import cn.thinkingdata.crawler.util.DateUtil;

public class CrawlTaskMain {
	private static Logger logger = LoggerFactory.getLogger(CrawlTaskMain.class);
	
	public static void main(String[] args) {
		try{
			String[] beanXmls = {"bean.xml","bean_crawlers.xml"};
			String crawlerName  = "HeXunCrawler";
						
			logger.info("--------------start crawler:[ " + crawlerName + " ]--------------");
			AbstractBaseCrawler action = null;
			Object obj = null;
			Object jdObj = null;
			
			ApplicationContext ctx = new ClassPathXmlApplicationContext(beanXmls);
			try{
				obj=ctx.getBean(crawlerName);
				
				if(null != obj && obj instanceof AbstractBaseCrawler){
	        		 action = (AbstractBaseCrawler) obj;	        		 
	        	 }else{
	        		 logger.error("Bean :" + crawlerName + " cann't be found!! please check you configurations!!!");
	        		 throw new RuntimeException("bean definition not found!please check your configuration!");
	        	}
				
			}catch(Exception e){
				logger.error("load method error:" + e);
	   		    throw new RuntimeException("bean definition not found!please check your configuration!");
			}
			action.run();
			logger.info("--------------crawler finished:[ " + crawlerName + " ]--------------");		
		}catch(Exception e){
			logger.error(Throwables.getStackTraceAsString(e));
            System.exit(1);
		}
		
	}
}
