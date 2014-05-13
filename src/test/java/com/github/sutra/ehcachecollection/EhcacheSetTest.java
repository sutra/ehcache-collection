package com.github.sutra.ehcachecollection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Sutra Zhou
 */
public class EhcacheSetTest {

	private EhcacheSet<String> es;

	private Set<String> comparisonSet;

	@Before
	public void setUp() {
		es = new EhcacheSet<String>(getClass().getName());
		comparisonSet = new HashSet<String>();
	}

	@After
	public void tearDown() {
		es.clear();
	}

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
