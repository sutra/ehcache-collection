package com.github.sutra.ehcachecollection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sutra Zhou
 */
public class EhcacheSetTest {

	private static final String CACHE_NAME = EhcacheSetTest.class.getName();

	private EhcacheSet<String> es;

	private Set<String> comparisonSet;

	@Before
	public void setUp() {
		es = new EhcacheSet<String>(CACHE_NAME);
		comparisonSet = new HashSet<String>();
	}

	@After
	public void tearDown() {
		es.clear();
	}

	@Test
	public void testConstructor() {
		new EhcacheSet<Serializable>(CACHE_NAME);

		CacheManager cacheManager = CacheManager.create();
		Ehcache cache = cacheManager.getCache(CACHE_NAME);
		new EhcacheSet<Serializable>(cache);
	}

	@Test
	public void testEhcacheSet() {
		assertTrue(es.isEmpty());
		assertFalse(es.contains("1-value"));
		assertFalse(es.remove("1-value"));

		assertTrue(es.add("1-value"));
		assertFalse(es.add("1-value")); // add twice

		assertFalse(es.isEmpty());
		assertEquals("1-value", es.iterator().next());
		assertEquals("1-value", es.toArray()[0]);

		assertEquals("1-value", new ArrayList<String>(es).get(0));

		assertTrue(es.contains("1-value"));
		assertTrue(es.remove("1-value"));
		assertTrue(es.isEmpty());
		assertFalse(es.contains("1-value"));
		assertFalse(es.remove("1-value")); // remove again
	}

	@Test
	public void testEqualsHashCode() {
		this.testEqualsHashCode(es);

		this.testEqualsHashCode(comparisonSet);
	}

	private void testEqualsHashCode(Set<String> set) {
		Set<String> expected = new HashSet<String>();
		set.add("1-value");
		expected.add("1-value");

		assertTrue(expected.equals(set));
		assertEquals(expected.hashCode(), set.hashCode());
	}

	@Test
	public void testSerializable() throws IOException, ClassNotFoundException {
		this.testSerializable(comparisonSet);

		this.testSerializable(es);
	}

	private void testSerializable(Set<String> set) throws IOException,
			ClassNotFoundException {
		set.add("1-value");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(baos);
		oo.writeObject(set);
		oo.close();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInput oi = new ObjectInputStream(bais);
		@SuppressWarnings("unchecked")
		Set<String> actual = (Set<String>) oi.readObject();
		assertEquals(set.size(), actual.size());
		assertEquals("1-value", actual.iterator().next());
		assertEquals(set, actual);
	}

}
