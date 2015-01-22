package gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SelectionHashList<T> implements Iterable<T> {
	private HashMap<Integer, SelectionHashListEntry> hashMap;
	private SelectionHashListEntry first, last;
	private SelectionHashListEntry firstSelected, lastSelected;
	public static void main(String[] args) {
		
	}
	
	public SelectionHashList() {
		hashMap = new HashMap<Integer, SelectionHashListEntry>();
		first = null;
		last = null;
		firstSelected = null;
		lastSelected = null;
	}
	
	public void insert(T t) {
		SelectionHashListEntry e = new SelectionHashListEntry(t, null, null);
		if(first == null) {
			first = e;
			last = e;
		} else {
			last.setNext(e);
			e.setPrev(last);
			last = e;
		}
		hashMap.put(e.hashCode(), e);
	}
	
	public void addAll(List<T> l) {
		for (T t : l) {
			insert(t);
		}
	}
	
	public void clear() {
		hashMap.clear();
		first = null;
		last = null;
		firstSelected = null;
		lastSelected = null;
	}
	public void remove(T t) {
		if (hashMap.containsKey(t.hashCode())) {
			SelectionHashListEntry e = hashMap.get(t.hashCode());
			if (last.equals(e)) {
				last = e.getPrev();
			}
			if (first.equals(e)) {
				first = e.getNext();
			}
			if (e.getPrev() != null) {
				e.getPrev().setNext(e.getNext());
			} 
			if (e.getNext() != null) {
				e.getNext().setNext(e.getPrev());
			}
			if (e.isSelected()) {
				if (lastSelected.equals(e)) {
					lastSelected = e.getPrevSelected();
				}
				if (firstSelected.equals(e)) {
					firstSelected = e.getNextSelected();
				}
				if (e.getPrevSelected() != null) {
					e.getPrevSelected().setNextSelected((e.getNextSelected()));
				} 
				if (e.getNextSelected() != null) {
					e.getNextSelected().setNextSelected(e.getPrevSelected());
				}
			}
			hashMap.remove(t.hashCode());
		}
	}
	public void setSelected(T t, boolean selected) {
		setSelected(t.hashCode(), selected);
	}
	public void setSelected(int hashCode, boolean selected) {
		if (hashMap.containsKey(hashCode())) {
			SelectionHashListEntry e = hashMap.get(hashCode);
			if (selected && !e.isSelected()) {
				if(firstSelected == null) {
					firstSelected = e;
					lastSelected = e;
				} else {
					lastSelected.setNext(e);
					e.setPrevSelected(lastSelected);
					lastSelected = e;
				}
			} else if (e.isSelected()) {
				if (lastSelected.equals(e)) {
					lastSelected = e.getPrevSelected();
				}
				if (firstSelected.equals(e)) {
					firstSelected = e.getNextSelected();
				}
				if (e.getPrevSelected() != null) {
					e.getPrevSelected().setNextSelected((e.getNextSelected()));
				} 
				if (e.getNextSelected() != null) {
					e.getNextSelected().setNextSelected(e.getPrevSelected());
				}
			}
		}
	}
	
	public List<T> getAll() { // TODO: faster
		List<T> list = new ArrayList<T>();
		for (T t : this) {
			list.add(t);
		}
		return list;
	}
	
	public List<T> getSelected() { // TODO: faster
		List<T> list = new ArrayList<T>();
		
		Iterator<T> iterator = getSelctedIterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;
	}
	
	private Iterator<T> getSelctedIterator() {
		return new Iterator<T>() {
			private SelectionHashListEntry current = first;
			
			@Override
			public boolean hasNext() {
				return current.getNext() != null;
			}

			@Override
			public T next() {
				T next = null;
				if (hasNext()) {
					next = current.getNext().getValue();
					current = current.getNext();
				}
				return next;
			}
		};
	}

	
	private class SelectionHashListEntry {
		private T value;
		private SelectionHashListEntry next, prev;
		private SelectionHashListEntry nextSelected, prevSelected;
		public SelectionHashListEntry(T value, SelectionHashListEntry prev, SelectionHashListEntry next) {
			this.value = value;
			this.prev = prev;
			this.next = next;
		}
		public SelectionHashListEntry getNext() {
			return next;
		}
		public SelectionHashListEntry getPrev() {
			return prev;
		}
		public SelectionHashListEntry getNextSelected() {
			return nextSelected;
		}
		public SelectionHashListEntry getPrevSelected() {
			return prevSelected;
		}
		public boolean isSelected() {
			return firstSelected == this || prevSelected != null || nextSelected == null; 
		}
		public T getValue() {
			return value;
		}
		
		public void setNext(SelectionHashListEntry e) {
			next = e;
		}
		public void setNextSelected(SelectionHashListEntry e) {
			nextSelected = e;
		}
		public void setPrev(SelectionHashListEntry e) {
			prev = e;
		}
		public void setPrevSelected(SelectionHashListEntry e) {
			prevSelected = e;
		}
		@Override
		public int hashCode() {
			return value.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			return value.equals(o);
		}
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private SelectionHashListEntry current = first;
			
			@Override
			public boolean hasNext() {
				return current.getNext() != null;
			}

			@Override
			public T next() {
				T next = null;
				if (hasNext()) {
					next = current.getNext().getValue();
					current = current.getNext();
				}
				return next;
			}
		};
	}
}
