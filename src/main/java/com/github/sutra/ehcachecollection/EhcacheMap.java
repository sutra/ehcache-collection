package com.github.sutra.ehcachecollection;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 * <a href="http://ehcache.org">Ehcache</a> based implementation of the
 * {@link Map} interface.
 * 
 * @author Sutra Zhou
 */
public class EhcacheMap<K extends Serializable, V extends Serializable>
		extends AbstractMap<K, V>
		implements Map<K, V>, Serializable {

	private static final long serialVersionUID = 2014051301L;

	private final class KeySet<E> extends AbstractSet<E>  {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<E> iterator() {
			return getKeys().iterator();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return getKeys().size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean remove(Object o) {
			return cache.remove(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			EhcacheMap.this.clear();
		}

		@SuppressWarnings("unchecked")
		private List<E> getKeys() {
			return cache.getKeys();
		}

	}

	private final class Entry extends AbstractMap.SimpleEntry<K, V> {

		private static final long serialVersionUID = 2014051301L;

		public Entry(K key, V value) {
			super(key, value);
		}

		/**
		 * {@inheritDoc}
		 */
		public V setValue(V value) {
			super.setValue(value);

			K key = getKey();
			Element element = cache.get(key);
			@SuppressWarnings("unchecked")
			V previousValue = element != null ? (V) element.getObjectValue() : null;
			cache.put(new Element(key, value));
			return previousValue;
		}

	}

	private final class EntrySet extends AbstractSet<Map.Entry<K, V>>  {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<Map.Entry<K, V>> iterator() {

			final Iterator<K> keyIterator = EhcacheMap.this.keySet().iterator();

			return new Iterator<Map.Entry<K, V>>() {

				private Map.Entry<K, V> currentEntry;

				@Override
				public boolean hasNext() {
					return keyIterator.hasNext();
				}

				@Override
				public Map.Entry<K, V> next() {
					final K key = keyIterator.next();
					final V value = get(key);
					currentEntry = new Entry(key, value);
					return currentEntry;
				}

				@Override
				public void remove() {
					EhcacheMap.this.remove(currentEntry.getKey());
				}
			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return EhcacheMap.this.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean remove(Object o) {
			@SuppressWarnings("unchecked")
			K key = ((Map.Entry<K, V>) o).getKey();
			cache.remove(key);
			return super.remove(o);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			EhcacheMap.this.clear();
			super.clear();
		}

	}

	private final class Values extends AbstractCollection<V> {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<V> iterator() {
			final Iterator<K> keyIterator = EhcacheMap.this.keySet().iterator();

			return new Iterator<V>() {

				private K currentKey;

				@Override
				public boolean hasNext() {
					return keyIterator.hasNext();
				}

				@Override
				public V next() {
					currentKey = keyIterator.next();
					return EhcacheMap.this.get(currentKey);
				}

				@Override
				public void remove() {
					EhcacheMap.this.remove(currentKey);
				}
			};
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public int size() {
			return EhcacheMap.this.size();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean remove(Object obj) {
			Set<K> keysToRemove = new HashSet<K>();
			Set<Map.Entry<K, V>> set = entrySet();
			for (Map.Entry<K, V> entry : set) {
				if (entry.getValue().equals(obj)) {
					keysToRemove.add(entry.getKey());
				}
			}

			for (K key : keysToRemove) {
				EhcacheMap.this.remove(key);
			}

			return keysToRemove.size() > 0;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void clear() {
			EhcacheMap.this.clear();
			super.clear();
		}

	}

	private final String cacheName;

	private transient Ehcache cache;

	public EhcacheMap(String cacheName) {
		this.cacheName = cacheName;
		this.cache = getCache(cacheName);
	}

	public EhcacheMap(Ehcache cache) {
		this.cacheName = cache.getName();
		this.cache = cache;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsKey(Object key) {
		return cache.get(key) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsValue(Object value) {
		return cache.isValueInCache(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public V get(Object key) {
		Element element = cache.get(key);
		if (element != null) {
			return (V) element.getObjectValue();
		} else {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear() {
		cache.removeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<K> keySet() {
		return new KeySet<K>();
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Map.Entry<K, V>> entrySet() {
		return new EntrySet();
	}

	/**
	 * {@inheritDoc}
	 */
	public V put(K key, V value) {
		cache.put(new Element(key, value));
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			cache.put(new Element(entry.getKey(), entry.getValue()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return cache.getSize();
	}

	/**
	 * {@inheritDoc}
	 */
	public V remove(Object key) {
		V value = get(key);
		cache.remove(key);
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	public Collection<V> values() {
		return new Values();
	}

	/**
	 * Returns the cache with the specified name.
	 *
	 * @param cacheName the cache name.
	 * @return the cache with the specifie name.
	 */
	private Ehcache getCache(String cacheName) {
		CacheManager cacheManager = CacheManager.create();
		Ehcache cache = cacheManager.getCache(cacheName);
		if (cache == null) {
			throw new NullPointerException("Cache \"" + cacheName
					+ "\" does not exist.");
		}
		return cache;
	}

	private Object readResolve() {
		this.cache = getCache(cacheName);
		return this;
	}

}
