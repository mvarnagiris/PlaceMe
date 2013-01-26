package com.placeme.model;

import java.io.Serializable;

public class Place implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5667437911915083960L;
	private String	name;
	private String	connurbation;
	private String	postcode;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getConnurbation() {
		return connurbation;
	}

	public void setConnurbation(String connurbation) {
		this.connurbation = connurbation;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Place [name=").append(name).append(", connurbation=").append(connurbation)
				.append(", postcode=").append(postcode).append("]");
		return builder.toString();
	}

}
