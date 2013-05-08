package com.github.sutra.ehcachecollection;

import java.io.Serializable;

/**
 * This class is only for test.
 * 
 * @author Sutra Zhou
 */
public class MyObject implements Serializable {

	private static final long serialVersionUID = -1350033994097969871L;

	private String name;

	public MyObject() {
	}

	public MyObject(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

}
