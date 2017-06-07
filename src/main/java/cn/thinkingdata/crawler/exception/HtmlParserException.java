package cn.thinkingdata.crawler.exception;

public class HtmlParserException extends Exception {

private static final long serialVersionUID = 775542761589760244L;
	
    public HtmlParserException(String message) {
        super(message);
    }

    public HtmlParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public HtmlParserException(Throwable cause) {
        super(cause);
    }

}
