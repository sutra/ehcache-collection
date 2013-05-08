package com.github.sutra.ehcachecollection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Test;

import com.github.sutra.ehcachecollection.LinkedEhcacheSet;

/**
 * @author Sutra Zhou
 */
public class LinkedEhcacheSetTest {

	@Test
	public void testLinkedEhcacheSet() {
		LinkedEhcacheSet<MyObject> les = new LinkedEhcacheSet<MyObject>(getClass().getName());
		MyObject area = new MyObject("areaName");
		les.add(area);
		assertEquals(area, les.iterator().next());
		assertEquals(area, les.toArray()[0]);

		assertEquals(area.getName(), new ArrayList<MyObject>(les).get(0).getName());
		assertNotNull(new ArrayList<MyObject>(les).get(0).getName());
	}

}
