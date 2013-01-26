package com.placeme.model;

import java.io.Serializable;

public class CardInfo implements Serializable {
	private static final long	serialVersionUID	= 8777612934304299447L;

	private String				title;
	private String				content;
	private String				type;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String url) {
		this.content = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CardInfo [title=").append(title).append(", content=").append(content).append(", type=")
				.append(type).append("]");
		return builder.toString();
	}
}
