package cn.thinkingdata.crawler.http.method;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;


public class GzipPostMethod extends PostMethod{

	public GzipPostMethod(String uri) {  
		super(uri);  
		setRequestHeader("Accept-Encoding", "gzip,deflate");
	}

	/** 
	 * Post response as string whether response is GZipped or not 
	 *  
	 * @return 
	 * @throws IOException 
	 */  
	@Override  
	public String getResponseBodyAsString() throws IOException {  
		GZIPInputStream gzin;  
		if (getResponseBody() != null || getResponseStream() != null) {
			if(getResponseHeader("Content-Encoding") != null  
					&& getResponseHeader("Content-Encoding").getValue().toLowerCase().indexOf("gzip") > -1) {
				//For GZip response
				InputStream is = super.getResponseBodyAsStream();
				gzin = new GZIPInputStream(is);
				String respCharset = getResponseCharSet();
				if(respCharset.equalsIgnoreCase("gb2312")){
					respCharset = "gbk";
				}
				
				InputStreamReader isr = new InputStreamReader(gzin,respCharset);
				java.io.BufferedReader br = new java.io.BufferedReader(isr);
				StringBuffer sb = new StringBuffer();
				String tempbf;
				while ((tempbf = br.readLine()) != null) {
					//logger.info("gzip line is:"+tempbf);
					sb.append(tempbf);
					sb.append("\r\n");
				}
				isr.close();  
				gzin.close();  
				return sb.toString();  
			}  else {  
				//For no gzip response  
				//logger.info("reponse is not gzip format");
				return super.getResponseBodyAsString();  
			}  
		} else {  
			return null;  
		}  
	} 

//	@Override
//	public InputStream getResponseBodyAsStream() throws IOException{
//		GZIPInputStream gzin = null; 
//		InputStream is = super.getResponseBodyAsStream();
//
//		if(super.getResponseHeader("Content-Encoding") != null  
//				&& super.getResponseHeader("Content-Encoding").getValue().toLowerCase().indexOf("gzip") > -1) {				
//			gzin= new GZIPInputStream(is);
//			return gzin;
//		}else{
//			return is;
//		}
//
//
//	}
}


