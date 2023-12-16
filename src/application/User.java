package application;

import java.util.ArrayList;
import java.util.List;

class User {
	private int id;
	private String name;
	private Main main;
	private String preferCipher = "AES";
	private List<String> textTitleList = new ArrayList<>();

	public User(int id, String name, Main main) {
		this.id = id;
		this.name = name;
		this.main = main;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getTextTitleList() {
		return textTitleList;
	}

	public void setTextTitleList(List<String> textTitleList) {
		this.textTitleList = textTitleList;
	}

	public String getPreferCipher() {
		return preferCipher;
	}

	public void setPreferCipher(String preferCipher) {
		this.preferCipher = preferCipher;
	}

}
