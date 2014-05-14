package com.github.sutra.ehcachecollection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.ehcache.Ehcache;

import org.apache.commons.collections.primitives.ArrayIntList;
import org.apache.commons.collections.primitives.IntIterator;
import org.apache.commons.collections.primitives.IntList;

/**
 * <a href="http://ehcache.org">Ehcache</a> based implementation of the
 * {@link List} interface.
 * 
 * @author Sutra Zhou
 */
public class EhcacheList<E extends Serializable> extends AbstractList<E>
		implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2014051301L;

	private IntList keys;

	private EhcacheMap<Integer, E> map;

	/**
	 *
	 */
	public EhcacheList(String cacheName) {
		keys = new ArrayIntList();
		map = new EhcacheMap<Integer, E>(cacheName);
	}

	public EhcacheList(Ehcache cache) {
		keys = new ArrayIntList();
		map = new EhcacheMap<Integer, E>(cache);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E get(int index) {
		int key = keys.get(index);
		return map.get(key);
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
	public boolean add(E e) {
		int key = e.hashCode();
		keys.add(key);
		map.put(key, e);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(int index, E element) {
		int key = element.hashCode();
		keys.add(index, key);
		map.put(key, element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E remove(int index) {
		int key = keys.removeElementAt(index);
		return map.remove(key);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		keys.clear();
		map.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for (E e : c) {
			changed |= add(e);
		}
		return changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		for (E e : c) {
			add(index++, e);
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		for (int i = toIndex - 1; i >= fromIndex; i--) {
			int key = keys.removeElementAt(i);
			map.remove(key);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int indexOf(Object o) {
		for (int i = 0, l = keys.size(); i < l; i++) {
			if (o.equals(map.get(keys.get(i)))) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<E> iterator() {
		Iterator<E> iterator = new Iterator<E>() {
			private int cursor = 0;   // index of next element to return
			private int lastRet = -1; // index of last element returned; -1 if no such

			public boolean hasNext() {
				return cursor != EhcacheList.this.size();
			}

			public E next() {
				int currentKey = keys.get(cursor);
				lastRet = cursor;
				cursor++;
				return map.get(currentKey);
			}

			public void remove() {
				int currentKey = keys.get(lastRet);
				keys.removeElement(currentKey);
				map.remove(currentKey);
				cursor--;
			}

		};
		return iterator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int lastIndexOf(Object o) {
		int key;
		Object e;
		int i;
		for (i = keys.size() - 1; i >= 0; i--) {
			key = keys.get(i);
			e = map.get(key);
			if (e.equals(o)) {
				break;
			}
		}
		return i;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E set(int index, E element) {
		final E oldElement;

		int oldKey = keys.get(index);

		int newKey = element.hashCode();
		keys.set(index, newKey);

		if (!keys.contains(oldKey)) {
			// Remove only if no key is pointing to it

			oldElement = map.remove(oldKey);
		} else {
			oldElement = map.get(oldKey);
		}

		map.put(newKey, element);

		return oldElement;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		IntList subIntList = keys.subList(fromIndex, toIndex);
		IntIterator intIterator = subIntList.iterator();
		List<E> subList = new ArrayList<E>();
		int key;
		E e;
		while (intIterator.hasNext()) {
			key = intIterator.next();
			e = map.get(key);
			subList.add(e);
		}
		return subList;
	}

}
