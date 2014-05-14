package com.github.sutra.ehcachecollection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sutra Zhou
 */
public class EhcacheListTest {

	private static final String CACHE_NAME = EhcacheSetTest.class.getName();

	private EhcacheList<String> el;

	private List<String> comparisonList;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		el = new EhcacheList<String>(CACHE_NAME);
		comparisonList = new ArrayList<String>();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		el.clear();
	}

	@Test
	public void testConstructor() {
		new EhcacheList<Serializable>(CACHE_NAME);

		CacheManager cacheManager = CacheManager.create();
		Ehcache cache = cacheManager.getCache(CACHE_NAME);
		new EhcacheList<Serializable>(cache);
	}

	@Test
	public void testList() {
		testList(comparisonList);
		testList(el);
	}

	private void testList(List<String> list) {
		assertEquals(0, list.size());

		assertTrue(list.add("1-value"));
		assertEquals(1, list.size());

		assertTrue(list.add("1-value"));
		assertEquals(2, list.size());
		assertEquals("1-value", list.get(0));
		assertEquals("1-value", list.get(1));

		// when set, it will not remove 1-value from cache as it is using at 0
		list.set(1, "2-value");
		assertEquals("1-value", list.get(0));
		assertEquals("2-value", list.get(1));

		// when set, it will remove 2-value from cache as no key points to 2-value
		list.set(1, "3-value");
		assertEquals("1-value", list.get(0));
		assertEquals("3-value", list.get(1));

		// revert back
		list.set(1, "2-value");
		assertEquals("1-value", list.get(0));
		assertEquals("2-value", list.get(1));

		list.add(0, "3-value");
		assertEquals(3, list.size());
		assertEquals("3-value", list.get(0));
		assertEquals("1-value", list.get(1));
		assertEquals("2-value", list.get(2));

		list.remove(0);
		assertEquals(2, list.size());
		assertEquals("1-value", list.get(0));
		assertEquals("2-value", list.get(1));

		list.addAll(Arrays.asList("3-value", "4-value"));
		assertEquals(4, list.size());
		assertEquals("1-value", list.get(0));
		assertEquals("2-value", list.get(1));
		assertEquals("3-value", list.get(2));
		assertEquals("4-value", list.get(3));

		list.addAll(1, Arrays.asList("5-value", "6-value"));
		assertEquals(6, list.size());
		assertEquals("1-value", list.get(0));
		assertEquals("5-value", list.get(1));
		assertEquals("6-value", list.get(2));
		assertEquals("2-value", list.get(3));
		assertEquals("3-value", list.get(4));
		assertEquals("4-value", list.get(5));

		if (list instanceof EhcacheList) {
			((EhcacheList<String>) list).removeRange(1, 3);
		} else {
			list.remove(2);
			list.remove(1);
		}
		assertEquals(4, list.size());
		assertEquals("1-value", list.get(0));
		assertEquals("2-value", list.get(1));
		assertEquals("3-value", list.get(2));
		assertEquals("4-value", list.get(3));

		assertEquals(-1, list.indexOf("not found"));
		assertEquals(0, list.indexOf("1-value"));
		assertEquals(1, list.indexOf("2-value"));
		assertEquals(2, list.indexOf("3-value"));
		assertEquals(3, list.indexOf("4-value"));

		assertEquals(-1, list.lastIndexOf("not found"));
		assertEquals(0, list.lastIndexOf("1-value"));
		assertEquals(1, list.lastIndexOf("2-value"));
		assertEquals(2, list.lastIndexOf("3-value"));
		assertEquals(3, list.lastIndexOf("4-value"));

		assertTrue(list.add("1-value"));
		assertEquals(4, list.lastIndexOf("1-value"));
	}

	@Test
	public void testIterator() {
		testIterator(comparisonList);
		testIterator(el);
	}

	private void testIterator(List<String> list) {
		list.add("1-value");
		list.add("2-value");
		list.add("3-value");

		Iterator<String> iterator = list.iterator();
		while (iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
	}

	@Test
	public void testSubList() {
		testSubList(comparisonList);
		testSubList(el);
	}

	private void testSubList(List<String> list) {
		list.add("1-value");
		list.add("2-value");
		list.add("3-value");

		List<String> subList = list.subList(1, 3);
		assertEquals(2, subList.size());
		assertEquals("2-value", subList.get(0));
		assertEquals("3-value", subList.get(1));

		try {
			subList = list.subList(-1, 3);
			fail("IndexOutOfBoundsException should be thrown.");
		} catch (IndexOutOfBoundsException e) {
		}

		try {
			subList = list.subList(1, 4);
			fail("IndexOutOfBoundsException should be thrown.");
		} catch (IndexOutOfBoundsException e) {
		}
	}

}
