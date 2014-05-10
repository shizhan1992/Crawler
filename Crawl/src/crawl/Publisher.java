package crawl;

import com.google.code.morphia.annotations.*;

public class Publisher{
	
	@Id
	String id;
	String name;
	long topicAmount;
	long commentAmount;
	
	public Publisher() {
	}
	
	public Publisher( String name, long topicAmount,
			long commentAmount) {
		this.name = name;
		this.topicAmount = topicAmount;
		this.commentAmount = commentAmount;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getTopicAmount() {
		return topicAmount;
	}
	public void setTopicAmount(long topicAmount) {
		this.topicAmount = topicAmount;
	}
	public long getCommentAmount() {
		return commentAmount;
	}
	public void setCommentAmount(long commentAmount) {
		this.commentAmount = commentAmount;
	}

}
