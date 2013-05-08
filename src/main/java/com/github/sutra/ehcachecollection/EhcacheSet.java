package com.github.sutra.ehcachecollection;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.ehcache.Ehcache;

/**
 * <a href="http://ehcache.org">Ehcache</a> based implementation of the
 * {@link Set} interface.
 * 
 * @author Sutra Zhou
 */
public class EhcacheSet<E extends Serializable> extends AbstractSet<E>
		implements Serializable {

	private static class DummyObject implements Serializable {

		private static final long serialVersionUID = 1L;

	}

	private static final long serialVersionUID = -3327815800474989254L;

	private transient EhcacheMap<E, Serializable> map;

	// Dummy value to associate with an Object in the backing Map
	private static final Serializable PRESENT = new DummyObject();

	/**
	 *
	 */
	public EhcacheSet(String cacheName) {
		map = new EhcacheMap<E, Serializable>(cacheName);
	}

	public EhcacheSet(Ehcache cache) {
		map = new EhcacheMap<E, Serializable>(cache);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<E> iterator() {
		return map.keySet().iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return map.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		return map.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(Object o) {
		return map.containsKey(o);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean add(E e) {
		return map.put(e, PRESENT) == null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean remove(Object o) {
		return map.remove(o) == PRESENT;
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		map.clear();
	}

}
