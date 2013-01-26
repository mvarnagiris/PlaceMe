package com.placeme.model;

import java.io.Serializable;

public class CardInfo implements Serializable {
	private static final long	serialVersionUID	= 8777612934304299447L;

	private String	title;
	private String	url;
	private String	type;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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
		builder.append("CardInfo [title=").append(title).append(", url=").append(url).append(", type=").append(type)
				.append("]");
		return builder.toString();
	}
}
