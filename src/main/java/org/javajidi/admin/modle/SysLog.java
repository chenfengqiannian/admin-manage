package org.javajidi.admin.modle;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * 操作日志
 *
 */
public class SysLog implements Serializable {

	/** 主键 */
	private Long id;

	/** 用户ID */
	private Long uid;

	/** 日志内容 */
	private String content;

	/** 用户操作 */
	private String operation;

	/** 创建时间 */
	private Date crTime;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUid() {
		return this.uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOperation() {
		return this.operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Date getCrTime() {
		return this.crTime;
	}

	public void setCrTime(Date crTime) {
		this.crTime = crTime;
	}

}