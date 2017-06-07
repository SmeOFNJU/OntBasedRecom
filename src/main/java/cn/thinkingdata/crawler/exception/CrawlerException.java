package cn.thinkingdata.crawler.exception;

public class CrawlerException extends Exception {

private static final long serialVersionUID = 775542761589760244L;
	
    public CrawlerException(String message) {
        super(message);
    }

    public CrawlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrawlerException(Throwable cause) {
        super(cause);
    }

}
