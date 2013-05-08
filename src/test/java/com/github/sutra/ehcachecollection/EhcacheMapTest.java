package com.github.sutra.ehcachecollection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.github.sutra.ehcachecollection.EhcacheMap;

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

	@Test
	public void testEhcacheMap() {
		em.put("a", "A");
		assertEquals("A", em.get("a"));
	}

	@Test
	public void testKeySet() {
		this.testKeySet(comparisonMap);

		this.testKeySet(em);
	}

	private void testKeySet(Map<String, String> map) {
		map.put("1-key", "1-value");
		Set<String> keySet = map.keySet();
		assertTrue(map.containsKey("1-key"));
		keySet.remove("1-key");
		assertFalse(map.containsKey("1-key"));

		map.put("2-key", "2-value");
		assertFalse(map.isEmpty());
		keySet.clear();
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
		assertTrue(map.containsKey("1-key"));
		Map.Entry<String, String> entry = entrySet.iterator().next();

		entry.setValue("1-new-value");
		assertEquals("1-new-value", map.get("1-key"));

		entrySet.remove(entrySet.iterator().next());
		assertFalse(map.containsKey("1-key"));

		map.put("2-key", "2-value");
		assertFalse(map.isEmpty());
		entrySet.clear();
		assertTrue(map.isEmpty());

		try {
			entrySet.add(entry);
			fail("UnsupprotedOperationException should be thrown.");
		} catch (UnsupportedOperationException ex) {
		}
	}

	@Test
	public void testValues() {
		this.testValues(comparisonMap);

		this.testValues(em);
	}

	private void testValues(Map<String, String> map) {
		map.put("1-key", "1-value");
		map.put("2-key", "2-value");
		map.put("3-key", "3-value");
		Collection<String> values = map.values();
		assertTrue(map.containsKey("1-key"));
		values.remove("1-value");
		assertFalse(map.containsKey("1-key"));

		assertFalse(map.isEmpty());
		map.clear();
		assertTrue(map.isEmpty());
	}

}
