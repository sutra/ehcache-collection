package com.github.sutra.ehcachecollection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sutra Zhou
 */
public class EhcacheMapTest {

	private EhcacheMap<String, String> em;

	private Map<String, String> comparisonMap;

	@Before
	public void setUp() {
		em = new EhcacheMap<String, String>(getClass().getName());
		comparisonMap  = new HashMap<String, String>();
	}

	@After
	public void tearDown() {
		em.clear();
	}

	@Test
	public void testConstructor() {
		CacheManager cacheManager = CacheManager.create();
		Ehcache cache = cacheManager.getCache(getClass().getName());
		new EhcacheMap<Serializable, Serializable>(cache);

		try {
			new EhcacheMap<Serializable, Serializable>("");
			fail("NullPointerException should be thrown.");
		} catch (NullPointerException e) {
		}
	}

	@Test
	public void testEhcacheMap() {
		comparisonMap.put("a", "A");
		em.put("a", "A");
		assertEquals("A", comparisonMap.get("a"));
		assertEquals("A", em.get("a"));

		assertEquals(1, comparisonMap.size());
		assertEquals(1, em.size());

		comparisonMap.put("null", null);
		em.put("null", null);

		comparisonMap.put(null, null);
		em.put(null, null);

		assertEquals(3, comparisonMap.size());
		assertEquals(2, em.size()); // null keys does not support.
	}

	@Test
	public void testKeySet() {
		this.testKeySet(comparisonMap);

		this.testKeySet(em);
	}

	private void testKeySet(Map<String, String> map) {
		map.put("1-key", "1-value");
		Set<String> keySet = map.keySet();
		assertEquals(1, keySet.size());
		assertTrue(map.containsKey("1-key"));

		keySet.remove("1-key");
		assertEquals(0, keySet.size());
		assertFalse(map.containsKey("1-key"));

		map.put("2-key", "2-value");
		assertEquals(1, keySet.size());
		assertFalse(map.isEmpty());

		keySet.clear();
		assertEquals(0, keySet.size());
		assertTrue(map.isEmpty());

		try {
			keySet.add("2-key");
			fail("UnsupportedOperationException should be thrown.");
		} catch (UnsupportedOperationException ex) {
		}
	}

	@Test
	public void testEntrySet() {
		this.testEntrySet(comparisonMap);

		this.testEntrySet(em);
	}

	private void testEntrySet(Map<String, String> map) {
		map.put("1-key", "1-value");
		Set<Map.Entry<String, String>> entrySet = map.entrySet();
		assertEquals(1, entrySet.size());
		assertTrue(map.containsKey("1-key"));
		Map.Entry<String, String> entry = entrySet.iterator().next();

		entry.setValue("1-value"); // old value
		assertEquals("1-value", map.get("1-key"));

		entry.setValue("1-new-value"); // new value
		assertEquals("1-new-value", map.get("1-key"));

		entrySet.remove(entrySet.iterator().next());
		assertFalse(map.containsKey("1-key"));

		assertTrue(map.isEmpty());
		if (map instanceof EhcacheMap) {
			assertNull(entry.setValue("1-value")); // when set then entry has been removed
		}

		map.put("2-key", "2-value");
		assertFalse(map.isEmpty());
		entrySet.clear();
		assertEquals(0, entrySet.size());
		assertTrue(map.isEmpty());

		try {
			entrySet.add(entry);
			fail("UnsupprotedOperationException should be thrown.");
		} catch (UnsupportedOperationException ex) {
		}

		map.put("1-key", "1-value");
		Iterator<Map.Entry<String, String>> iterator = entrySet.iterator();

		try {
			iterator.remove();
			fail("IllegalStateException should be thrown.");
		} catch (IllegalStateException e) {
		}

		while (iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
		assertTrue(map.isEmpty());
	}

	@Test
	public void testValues() {
		this.testValues(comparisonMap);

		this.testValues(em);
	}

	private void testValues(Map<String, String> map) {
		map.put("1-key", "1-value");

		Map<String, String> toPut = new HashMap<String, String>();
		toPut.put("2-key", "2-value");
		toPut.put("3-key", "3-value");

		map.putAll(toPut);

		Collection<String> values = map.values();
		assertEquals(3, values.size());
		assertEquals("1-value", map.get("1-key"));
		assertEquals("2-value", map.get("2-key"));
		assertEquals("3-value", map.get("3-key"));
		assertTrue(map.containsKey("1-key"));
		assertTrue(map.containsKey("2-key"));
		assertTrue(map.containsKey("3-key"));
		assertTrue(map.containsValue("1-value"));
		assertTrue(map.containsValue("3-value"));
		assertTrue(map.containsValue("3-value"));

		assertTrue(values.remove("1-value"));
		assertNull(map.get("1-key"));
		assertFalse(map.containsKey("1-key"));
		assertFalse(map.containsValue("1-value"));
		assertFalse(values.remove("1-value")); // remove again

		Iterator<String> iterator = values.iterator();

		try {
			iterator.remove();
			fail("IllegalStateException should be thrown.");
		} catch (IllegalStateException e) {
		}

		while (iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
		assertTrue(values.isEmpty());
		assertTrue(map.isEmpty());

		map.put("1-key", "1-value");
		assertFalse(map.isEmpty());
		values.clear();
		assertTrue(map.isEmpty());
	}

	@Test
	public void testEqualsHashCode() {
		this.testEqualsHashCode(comparisonMap);

		this.testEqualsHashCode(em);
	}

	private void testEqualsHashCode(Map<String, String> map) {
		Map<String, String> expected = new HashMap<String, String>();
		map.put("1-key", "1-value");
		expected.put("1-key", "1-value");

		assertTrue(expected.equals(map));
		assertEquals(expected.hashCode(), map.hashCode());
	}

	@Test
	public void testSerializable() throws IOException, ClassNotFoundException {
		this.testSerializable(comparisonMap);

		this.testSerializable(em);
	}

	private void testSerializable(Map<String, String> map) throws IOException,
			ClassNotFoundException {
		map.put("1-key", "1-value");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutput oo = new ObjectOutputStream(baos);
		oo.writeObject(map);
		oo.close();

		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		ObjectInput oi = new ObjectInputStream(bais);
		@SuppressWarnings("unchecked")
		Map<String, String> actual = (Map<String, String>) oi.readObject();
		assertEquals(map.size(), actual.size());
		assertEquals("1-value", actual.get("1-key"));
		assertEquals(map, actual);
	}

}
