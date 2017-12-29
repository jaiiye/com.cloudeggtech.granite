package com.cloudeggtech.granite.framework.plumbing.mybatis;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class PersistentObjectIterator<T> implements Iterator<T>{
	
	private int position;
	private int page;
	
	private int maxFetchSize;
	
	private List<T> persistentObjects;
	
	public PersistentObjectIterator() {
		this(20);
	}
	
	public PersistentObjectIterator(int maxFetchSize) {
		this.maxFetchSize = maxFetchSize;
		position = 0;
		page = 0;
	}

	@Override
	public boolean hasNext() {
		if (needFetchMore()) {
			fetch();
		}
		
		if (position == 0 && persistentObjects.size() == 0)
			return false;
		
		return position < persistentObjects.size();
	}

	private boolean needFetchMore() {
		if (persistentObjects == null) {
			return true;
		}
		
		if (persistentObjects.size() == maxFetchSize && position == maxFetchSize) {
			return true;
		}
		
		return false;
	}

	private void fetch() {
		persistentObjects = doFetch(page * maxFetchSize, maxFetchSize);
		page++;
		position = 0;
	}
	
	protected abstract List<T> doFetch(int offset, int limit);

	@Override
	public T next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		
		T peristentObject = persistentObjects.get(position);
		position++;
		
		return peristentObject;
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
