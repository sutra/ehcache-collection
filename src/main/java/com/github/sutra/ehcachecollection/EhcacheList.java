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
	private static final long serialVersionUID = -5397536041485685485L;

	private transient IntList keys;

	private transient EhcacheMap<Integer, E> map;

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
		IntIterator ii = keys.iterator();
		int key;
		Object e;
		int index = -1;
		while (ii.hasNext()) {
			key = ii.next();
			e = map.get(key);
			if (!e.equals(o)) {
				index++;
			} else {
				break;
			}
		}
		return index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<E> iterator() {
		final IntIterator ii = keys.iterator();
		Iterator<E> iterator = new Iterator<E>() {
			private int currentKey;

			public boolean hasNext() {
				return ii.hasNext();
			}

			public E next() {
				currentKey = ii.next();
				return map.get(currentKey);
			}

			public void remove() {
				keys.removeElement(currentKey);
				map.remove(currentKey);
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
		int oldKey = keys.get(index);
		E oldElement = map.remove(oldKey);

		int newKey = element.hashCode();
		keys.set(index, newKey);
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
