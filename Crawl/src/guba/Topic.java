package guba;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Topic implements Serializable, GraphicNode {
	String code;
	String url;
	String title;
	long topicClick;
	long topicComment;
	Date publishDate;
	Date updateDate;
	String publisher;//publisher name
	Set<Publisher> commentators = new HashSet<Publisher>();  
	Set<Comment> comment = new HashSet<Comment>();
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUri() {
		return url;
	}

	public void setUri(String uri) {
		this.url = uri;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getTopicClick() {
		return topicClick;
	}

	public void setTopicClick(long topicClick) {
		this.topicClick = topicClick;
	}

	public long getTopicComment() {
		return topicComment;
	}

	public void setTopicComment(long topicComment) {
		this.topicComment = topicComment;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public Set<Comment> getComment() {
		return comment;
	}

	public void setComment(Set<Comment> comment) {
		this.comment = comment;
	}



}
