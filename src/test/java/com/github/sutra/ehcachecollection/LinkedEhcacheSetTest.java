package com.github.sutra.ehcachecollection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sutra Zhou
 */
public class LinkedEhcacheSetTest {

	private static final String CACHE_NAME = LinkedEhcacheSetTest.class.getName();

	private LinkedEhcacheSet<String> les;

	private LinkedHashSet<String> comparisonSet;

	@Before
	public void setUp() {
		les = new LinkedEhcacheSet<String>(CACHE_NAME);
		comparisonSet = new LinkedHashSet<String>();
	}

	@After
	public void tearDown() {
		les.clear();
	}

	@Test
	public void testConstructor() {
		new LinkedEhcacheSet<Serializable>(CACHE_NAME);

		CacheManager cacheManager = CacheManager.create();
		Ehcache cache = cacheManager.getCache(CACHE_NAME);
		new LinkedEhcacheSet<Serializable>(cache);
	}

	@Test
	public void testLinkedSet() {
		testLinkedSet(comparisonSet);
		testLinkedSet(les);
	}

	private void testLinkedSet(final Set<String> set) {
		assertTrue(set.isEmpty());
		assertFalse(set.contains("1-value"));

		assertTrue(set.add("1-value"));
		assertFalse(set.add("1-value"));
		assertFalse(set.isEmpty());
		assertTrue(set.contains("1-value"));

		assertEquals("1-value", set.iterator().next());
		assertEquals("1-value", set.toArray()[0]);

		assertEquals("1-value", new ArrayList<String>(set).get(0));

		assertTrue(set.remove("1-value"));
		assertFalse(set.remove("1-value"));
	}

	@Test
	public void testIterator() {
		testIterator(comparisonSet);
		testIterator(les);
	}

	private void testIterator(Set<String> set) {
		set.add("3-value");
		set.add("2-value");
		set.add("1-value");

		Iterator<String> iterator = set.iterator();
		try {
			iterator.remove();
			fail("IllegalStateException should be thrown.");
		} catch (IllegalStateException e) {
		}

		assertTrue(iterator.hasNext());
		assertEquals("3-value", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("2-value", iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals("1-value", iterator.next());
		assertFalse(iterator.hasNext());

		iterator = set.iterator();

		try {
			iterator.remove();
			fail("IllegalStateException should be thrown.");
		} catch (IllegalStateException e) {
		}
		while (iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
		try {
			iterator.remove();
			fail("IllegalStateException should be thrown.");
		} catch (IllegalStateException e) {
		}
	}

}
