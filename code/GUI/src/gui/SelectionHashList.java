package gui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SelectionHashList<T> {
	private HashMap<Integer, SelectionHashListEntry> hashMap;
	private SelectionHashListEntry first, last;
	private SelectionHashListEntry firstSelected, lastSelected;
	private int selectedCounter;
	private List<T> list = new List<T>() {
		@Override
		public int size() {
			return hashMap.size();
		}
		@Override
		public boolean isEmpty() {
			return hashMap.isEmpty();
		}
		@Override
		public boolean contains(Object o) {
			return hashMap.containsValue(o);
		}

		@Override
		public Iterator<T> iterator() {
			return new Iterator<T>() {
				private SelectionHashListEntry current = first;
				
				@Override
				public boolean hasNext() {
					return current != null;
				}

				@Override
				public T next() {
					T next = current.getValue();
					current = current.getNext();
					return next;
				}
			};
		}

		@Override
		public Object[] toArray() {
			Object[] array = new Object[list.size()];
			int i = 0;
			for (T t : list) {
				array[i++] = t;
			}
			return array;
		}

		@Override
		public <F> F[] toArray(F[] a) {
			System.err.println("Nicht implementiert.");
			return null;
		}

		@Override
		public boolean add(T e) {
			System.err.println("Nicht implementiert.");
			return false;
		}

		@Override
		public boolean remove(Object o) {
			System.err.println("Nicht implementiert.");
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			System.err.println("Nicht implementiert.");
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends T> c) {
			System.err.println("Nicht implementiert.");
			return false;
		}

		@Override
		public boolean addAll(int index, Collection<? extends T> c) {
			System.err.println("Nicht implementiert.");
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			System.err.println("Nicht implementiert.");
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			System.err.println("Nicht implementiert.");
			return false;
		}

		@Override
		public void clear() {
			System.err.println("Nicht implementiert.");
		}

		@Override
		public T get(int index) throws IndexOutOfBoundsException {
			int i = 0;
			T elementToReturn = null;
			if (list.size() > index) {
				for (T t : list) {
					if (i++ == index) {
						elementToReturn = t;
						break;
					}
				}
			} else {
				throw new IndexOutOfBoundsException();
			}
			return elementToReturn;
		}

		@Override
		public T set(int index, T element) {
			System.err.println("Nicht implementiert.");
			return null;
		}

		@Override
		public void add(int index, T element) {
			System.err.println("Nicht implementiert.");
		}

		@Override
		public T remove(int index) {
			System.err.println("Nicht implementiert.");
			return null;
		}

		@Override
		public int indexOf(Object o) {
			System.err.println("Nicht implementiert.");
			return 0;
		}

		@Override
		public int lastIndexOf(Object o) {
			System.err.println("Nicht implementiert.");
			return 0;
		}

		@Override
		public ListIterator<T> listIterator() {
			System.err.println("Nicht implementiert.");
			return null;
		}

		@Override
		public ListIterator<T> listIterator(int index) {
			System.err.println("Nicht implementiert.");
			return null;
		}

		@Override
		public List<T> subList(int fromIndex, int toIndex) {
			System.err.println("Nicht implementiert.");
			return null;
		}
	};
	private List<T> selected = new List<T>() {
		@Override
		public int size() {
			return selectedCounter;
		}
		@Override
		public boolean isEmpty() {
			return selectedCounter == 0;
		}
		@Override
		public boolean contains(Object o) {
			boolean contains = false;
			if (first != null) {
				if (o != null && o.getClass().equals(first.getValue().getClass())) {
					contains = hashMap.get(o.hashCode()).isSelected();
				}
			}
			return contains;
		}

		@Override
		public Iterator<T> iterator() {
			return  new Iterator<T>() {
				private SelectionHashListEntry current = firstSelected;
				
				@Override
				public boolean hasNext() {
					return current != null;
				}

				@Override
				public T next() {
					T next = current.getValue();
					current = current.getNextSelected();
					return next;
				}
			};
		}

		@Override
		public Object[] toArray() {
			Object[] array = new Object[selectedCounter];
			int i = 0;
			for (T t : selected) {
				array[i++] = t;
			}
			return array;
		}

		@Override
		public <F> F[] toArray(F[] array) {
			System.err.println("Nicht implementiert.");
			return null;
		}

		@Override
		public boolean add(T e) {
			System.err.println("Nicht implementiert.");
			return false;
		}

		@Override
		public boolean remove(Object o) {
			System.err.println("Nicht implementiert.");
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			System.err.println("Nicht implementiert.");
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends T> c) {
			System.err.println("Nicht implementiert.");
			return false;
		}

		@Override
		public boolean addAll(int index, Collection<? extends T> c) {
			System.err.println("Nicht implementiert.");
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			System.err.println("Nicht implementiert.");
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			System.err.println("Nicht implementiert.");
			return false;
		}

		@Override
		public void clear() {
			System.err.println("Nicht implementiert.");
		}

		@Override
		public T get(int index) {
			int i = 0;
			T elementToReturn = null;
			if (selected.size() > index) {
				for (T t : selected) {
					if (i++ == index) {
						elementToReturn = t;
						break;
					}
				}
			} else {
				throw new IndexOutOfBoundsException();
			}
			return elementToReturn;
		}

		@Override
		public T set(int index, T element) {
			System.err.println("Nicht implementiert.");
			return null;
		}

		@Override
		public void add(int index, T element) {
			System.err.println("Nicht implementiert.");
		}

		@Override
		public T remove(int index) {
			System.err.println("Nicht implementiert.");
			return null;
		}

		@Override
		public int indexOf(Object o) {
			System.err.println("Nicht implementiert.");
			return 0;
		}

		@Override
		public int lastIndexOf(Object o) {
			System.err.println("Nicht implementiert.");
			return 0;
		}

		@Override
		public ListIterator<T> listIterator() {
			System.err.println("Nicht implementiert.");
			return null;
		}

		@Override
		public ListIterator<T> listIterator(int index) {
			System.err.println("Nicht implementiert.");
			return null;
		}

		@Override
		public List<T> subList(int fromIndex, int toIndex) {
			System.err.println("Nicht implementiert.");
			return null;
		}
	};
	
	public SelectionHashList() {
		hashMap = new HashMap<Integer, SelectionHashListEntry>();
		first = null;
		last = null;
		firstSelected = null;
		lastSelected = null;
		selectedCounter = 0;
	}
	
	public void insert(T t) {
		SelectionHashListEntry e = new SelectionHashListEntry(t, null, null);
		if (first == null) {
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
			if (last.getValue().equals(e.getValue())) {
				last = e.getPrev();
			}
			if (first.getValue().equals(e.getValue())) {
				first = e.getNext();
			}
			if (e.getPrev() != null) {
				e.getPrev().setNext(e.getNext());
			} 
			if (e.getNext() != null) {
				e.getNext().setNext(e.getPrev());
			}
			if (e.isSelected()) {
				if (lastSelected.getValue().equals(e.getValue())) {
					lastSelected = e.getPrevSelected();
				}
				if (firstSelected.getValue().equals(e.getValue())) {
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
	
	public boolean setSelected(Integer id, boolean selected) {
		boolean changed = false;
		if (hashMap.containsKey(id.hashCode())) {
			SelectionHashListEntry e = hashMap.get(id.hashCode());
			if (selected && !e.isSelected()) {
				changed = true;
				selectedCounter++;
				if (firstSelected == null) {
					firstSelected = e;
					lastSelected = e;
				} else {
					lastSelected.setNextSelected(e);
					e.setPrevSelected(lastSelected);
					lastSelected = e;
				}
			} else if (!selected && e.isSelected()) {
				changed = true;
				selectedCounter--;
				if (lastSelected.getValue().equals(e.getValue())) {
					lastSelected = e.getPrevSelected();
				}
				if (firstSelected.getValue().equals(e.getValue())) {
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
		return changed;
	}
	
	public List<T> get() {
		return list;
	}
	
	public List<T> getSelected() {
		return selected;
	}
	
	public T getElement(Integer id) {
		SelectionHashListEntry e = hashMap.get(id.hashCode());
		return e == null ? null : e.getValue();
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
			return firstSelected == this || prevSelected != null || nextSelected != null; 
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
	}
}
