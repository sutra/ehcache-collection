package com.github.sutra.ehcachecollection;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.ehcache.Ehcache;

import org.apache.commons.collections.primitives.ArrayIntList;
import org.apache.commons.collections.primitives.IntIterator;
import org.apache.commons.collections.primitives.IntList;

/**
 * <a href="http://ehcache.org">Ehcache</a> based implementation of the
 * {@link Set} interface, with predictable iteration order. 
 * 
 * @author Sutra Zhou
 */
public class LinkedEhcacheSet<E extends Serializable> extends AbstractSet<E> implements Serializable {

	private static final long serialVersionUID = 2013051301L;

	private final IntList keys;

	private final EhcacheMap<Integer, E> map;

	/**
	 * @param cacheName
	 */
	public LinkedEhcacheSet(String cacheName) {
		keys = new ArrayIntList();
		map = new EhcacheMap<Integer, E>(cacheName);
	}

	public LinkedEhcacheSet(Ehcache cache) {
		keys = new ArrayIntList();
		map = new EhcacheMap<Integer, E>(cache);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<E> iterator() {
		IntList clonedKeys;
		synchronized (keys) {
			clonedKeys = new ArrayIntList(keys.size());
			clonedKeys.addAll(keys);
		}

		final IntIterator keyIterator = clonedKeys.iterator();
		Iterator<E> iterator = new Iterator<E>() {
			private E currentElement;

			public boolean hasNext() {
				return keyIterator.hasNext();
			}

			public E next() {
				int key = keyIterator.next();
				currentElement = map.get(key);
				return currentElement;
			}

			public void remove() {
				if (currentElement == null) {
					throw new IllegalStateException();
				}
				LinkedEhcacheSet.this.remove(currentElement);
				currentElement = null;
			}
		};
		return iterator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return keys.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return keys.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return keys.contains(o.hashCode());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(E e) {
		int key = e.hashCode();
		synchronized (keys) {
			if (!keys.contains(key)) {
				keys.add(key);
				map.put(key, e);
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean remove(Object o) {
		int key = o.hashCode();
		boolean removed;
		synchronized (keys) {
			removed = keys.removeElement(key);
		}
		if (removed) {
			map.remove(key);
		}
		return removed;
	}

	public void clear() {
		synchronized (keys) {
			keys.clear();
		}
		map.clear();
	}

}
