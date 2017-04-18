package com.github.aikivinen.birtdemo.domain;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class CrudDemoEntity {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	
	private String textVal1;
	private String textVal2;
	private String textVal3;

	private int intVal1;
	private int intVal2;
	private int intVal3;

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public String getTextVal1() {
		return textVal1;
	}

	public void setTextVal1(String textVal1) {
		this.textVal1 = textVal1;
	}

	public String getTextVal2() {
		return textVal2;
	}

	public void setTextVal2(String textVal2) {
		this.textVal2 = textVal2;
	}

	public String getTextVal3() {
		return textVal3;
	}

	public void setTextVal3(String textVal3) {
		this.textVal3 = textVal3;
	}

	public int getIntVal1() {
		return intVal1;
	}

	public void setIntVal1(int intVal1) {
		this.intVal1 = intVal1;
	}

	public int getIntVal2() {
		return intVal2;
	}

	public void setIntVal2(int intVal2) {
		this.intVal2 = intVal2;
	}

	public int getIntVal3() {
		return intVal3;
	}

	public void setIntVal3(int intVal3) {
		this.intVal3 = intVal3;
	}

}
