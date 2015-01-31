package gui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Class for quickly selecting and iterating over elements.
 * 
 * @author Maximilian Awiszus
 * @version 1.0
 * 
 * @param <T>
 *            Type of elements which will be in this data structure
 */
public class SelectionHashList<T> {
    private HashMap<Integer, SelectionHashListEntry> hashMap;
    private SelectionHashListEntry first;
    private SelectionHashListEntry last;
    private SelectionHashListEntry firstSelected;
    private SelectionHashListEntry lastSelected;
    private int selectedCounter;
    private int counter;
    private List<T> list = new List<T>() {
        @Override
        public int size() {
            return counter;
        }

        @Override
        public boolean isEmpty() {
            return counter == 0;
        }

        @Override
        public boolean contains(Object o) {
            SelectionHashListEntry e = hashMap.get(o);
            return e != null && e.isVisible();
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
                if (o != null
                        && o.getClass().equals(first.getValue().getClass())) {
                    contains = hashMap.get(o.hashCode()).isSelected();
                }
            }
            return contains;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
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

    /**
     * Creates an empty SelectionHashList.
     */
    public SelectionHashList() {
        hashMap = new HashMap<Integer, SelectionHashListEntry>();
        first = null;
        last = null;
        firstSelected = null;
        lastSelected = null;
        counter = 0;
        selectedCounter = 0;
    }

    /**
     * Insert or update a item in data structure. An inserted element is
     * deselected by default.
     * 
     * @param t
     *            item which will be inserted or updated
     */
    public void update(T t) {
        if (hashMap.containsValue(t)) {
            SelectionHashListEntry e = hashMap.get(t);
            if (e.isVisible()) {
                e.setValue(t);
            } else {
                insertIntoList(e);
            }
        } else {
            insertNewElement(t);
        }
    }

    private void insertNewElement(T t) { // TODO: what if exists?
        SelectionHashListEntry e = new SelectionHashListEntry(t, null, null);
        insertIntoList(e);
        hashMap.put(e.hashCode(), e);
    }

    private void insertIntoList(SelectionHashListEntry e) {
        e.setVisible(true);
        if (first == null) {
            first = e;
            last = e;
        } else {
            last.setNext(e);
            e.setPrev(last);
            last = e;
        }
        counter++;
    }

    /**
     * Insert or update many elements to the data structure. All inserted
     * elements are deselected by default.
     * 
     * @param l
     *            Element which will be inserted or updated
     */
    public void updateAll(List<T> l) {
        for (T t : l) {
            update(t);
        }
    }

    /**
     * Empty data structure / remove all elements including selected items
     */
    public void clear() {
        hashMap.clear();
        first = null;
        last = null;
        firstSelected = null;
        lastSelected = null;
    }

    /**
     * Remove many elements. If element is selected it will remain selected.
     */
    public void removeAll() {
        for (T t : list) {
            remove(t);
        }
    }

    /**
     * Remove specific element. If element is selected it will remain selected.
     * 
     * @param t
     *            which will be removed.
     */
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
            if (!e.isSelected()) {
                hashMap.remove(t.hashCode());
            } else {
                e.setVisible(false);
            }
            counter--;
        }
    }

    /**
     * Select or deselect an element if it exists in the data structure.
     * 
     * @param t
     *            Element which will be selected / deselected
     * @param selected
     *            is true if element should be selected, false otherwise
     * @return true if list has changed during operation, false otherwise
     */
    public boolean setSelected(T t, boolean selected) {
        return setSelected(t.hashCode(), selected);
    }

    /**
     * Select or deselect an element if it exists in the data structure.
     * 
     * @param id
     *            of element which should be selected / deselected
     * @param selected
     *            is true if element should be selected, false otherwise
     * @return true if list has changed during operation, false otherwise
     */
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
                    e.getPrevSelected().setNextSelected(e.getNextSelected());
                }
                if (e.getNextSelected() != null) {
                    e.getNextSelected().setPrevSelected(e.getPrevSelected());
                }
                e.setNextSelected(null);
                e.setPrevSelected(null);
                if (!e.isVisible()) {
                    hashMap.remove(e.hashCode());
                }
            }
        }
        return changed;
    }

    /**
     * Get list of all inserted elements.
     * 
     * @return list of insrted elements
     */
    public List<T> get() {
        return list;
    }

    /**
     * Get list of all selected elements.
     * 
     * @return list of selected elements
     */
    public List<T> getSelected() {
        return selected;
    }

    /**
     * Get element with specific id
     * 
     * @param id
     *            of element
     * @return element with id or null
     */
    public T getElement(Integer id) {
        SelectionHashListEntry e = hashMap.get(id.hashCode());
        return e == null ? null : e.getValue();
    }

    private class SelectionHashListEntry {
        private T value;
        private SelectionHashListEntry next, prev;
        private SelectionHashListEntry nextSelected, prevSelected;
        private boolean visible;

        public SelectionHashListEntry(T value, SelectionHashListEntry prev,
                SelectionHashListEntry next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
            visible = true;
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
            return firstSelected == this || prevSelected != null
                    || nextSelected != null;
        }

        public T getValue() {
            return value;
        }

        public boolean isVisible() {
            return visible;
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

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public void setValue(T t) {
            value = t;
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

}
