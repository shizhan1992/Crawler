package crawl;

import java.util.Date;
import com.google.code.morphia.annotations.Id;

public class Topic{
	@Id
	String id;
	String title;
	Publisher publisher;
	String uri;
	long topicClick;
	long topicComment;
	Date topicPublishDate;
	
	public Topic() {
	}

	public Topic(String title, Publisher publisher, String uri,
			long topicClick, long topicComment, Date topicPublishDate,
			Date updateDate) {
		super();
		this.title = title;
		this.publisher = publisher;
		this.uri = uri;
		this.topicClick = topicClick;
		this.topicComment = topicComment;
		this.topicPublishDate = topicPublishDate;
		this.updateDate = updateDate;
	}

	Date updateDate;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
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

	public Date getTopicPublishDate() {
		return topicPublishDate;
	}

	public void setTopicPublishDate(Date topicPublishDate) {
		this.topicPublishDate = topicPublishDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}
}


