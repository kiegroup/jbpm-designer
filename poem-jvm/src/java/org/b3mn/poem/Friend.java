package org.b3mn.poem;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



@Entity
public class Friend {
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
	@Column(name="subject_id")
	private int subjectId;
	@Column(name="friend_id")
	private int friendId;
	@Column(name="model_count")
	private int modelCount;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(int subjectId) {
		this.subjectId = subjectId;
	}
	public int getFriendId() {
		return friendId;
	}
	public void setFriendId(int friendId) {
		this.friendId = friendId;
	}
	public int getModelCount() {
		return modelCount;
	}
	public void setModelCount(int model_count) {
		this.modelCount = model_count;
	}
	
	
	
}
