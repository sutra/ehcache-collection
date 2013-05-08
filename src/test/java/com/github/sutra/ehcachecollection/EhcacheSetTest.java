package com.github.sutra.ehcachecollection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.junit.Test;

import com.github.sutra.ehcachecollection.EhcacheSet;

/**
 * @author Sutra Zhou
 */
public class EhcacheSetTest {

	@Test
	public void testEhcacheSet() {
		EhcacheSet<MyObject> es = new EhcacheSet<MyObject>(getClass().getName());
		MyObject area = new MyObject("areaName");
		es.add(area);
		assertEquals(area, es.iterator().next());
		assertEquals(area, es.toArray()[0]);

		assertEquals(area.getName(), new ArrayList<MyObject>(es).get(0).getName());
		assertNotNull(new ArrayList<MyObject>(es).get(0).getName());
	}

}
