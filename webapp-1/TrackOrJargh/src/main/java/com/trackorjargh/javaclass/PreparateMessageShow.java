package com.trackorjargh.javaclass;

public class PreparateMessageShow {
	private Long id;
	private String name;
	private String message;
	
	public PreparateMessageShow() {
	}

	public PreparateMessageShow(Long id, String name, String message) {
		this.id = id;
		this.name = name;
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
