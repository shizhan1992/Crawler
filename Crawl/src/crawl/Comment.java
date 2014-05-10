package crawl;

import java.util.Date;

import com.google.code.morphia.annotations.*;

public class Comment{
	@Id
	String id;
	Date publishDate;
	String topicUrl;
	String commentContent;
	String topicTitle;
	Publisher publisher;
	
	public Comment() {
	}

	public Comment(Date publishDate, String topicUrl, String commentContent,
			String topicTitle, Publisher publisher) {
		super();
		this.publishDate = publishDate;
		this.topicUrl = topicUrl;
		this.commentContent = commentContent;
		this.topicTitle = topicTitle;
		this.publisher = publisher;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	public String getTopicTitle() {
		return topicTitle;
	}

	public void setTopicTitle(String topicTitle) {
		this.topicTitle = topicTitle;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public String getTopicUrl() {
		return topicUrl;
	}

	public void setTopicUrl(String topicUrl) {
		this.topicUrl = topicUrl;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

}
