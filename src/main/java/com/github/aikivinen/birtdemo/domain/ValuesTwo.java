package com.github.aikivinen.birtdemo.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ValuesTwo extends CrudDemoEntity {

	@Id
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
