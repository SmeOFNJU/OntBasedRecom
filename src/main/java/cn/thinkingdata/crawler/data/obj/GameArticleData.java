package cn.thinkingdata.crawler.data.obj;

import cn.thinkingdata.crawler.util.DateUtil;


public class GameArticleData extends CrawledData{
	public static final String TABLE_NAME = "game_article";
	
	protected String source = null;
	protected String title = null;
	protected String postDate = null;
	protected String classify = null;
	protected String content = null;
	protected String tags = null;
	protected String rawHtmlContent = null;
	protected String url = null;
	protected String crawl_time = DateUtil.getCurrentPreciseDateString();
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPostDate() {
		return postDate;
	}

	public void setPostDate(String postDate) {
		this.postDate = postDate;
	}

	public String getClassify() {
		return classify;
	}

	public void setClassify(String classify) {
		this.classify = classify;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getRawHtmlContent() {
		return rawHtmlContent;
	}

	public void setRawHtmlContent(String rawHtmlContent) {
		this.rawHtmlContent = rawHtmlContent;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
