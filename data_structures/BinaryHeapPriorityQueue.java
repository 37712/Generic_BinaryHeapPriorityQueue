
/*  CS310 Spring 2018
    Programming Assignment #3
    Carlos A. Gamino Reyes
*/

/*  The PriorityQueue ADT may store objects in any order.  However,
    removal of objects from the PQ must follow specific criteria.
    The object of highest priority that has been in the PQ longest
    must be the object returned by the remove() method.  FIFO return
    order must be preserved for objects of identical priority.
   
    Ranking of objects by priority is determined by the Comparable<E>
    interface.  All objects inserted into the PQ must implement this
    interface.
*/

package data_structures;

import java.util.Iterator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

public class BinaryHeapPriorityQueue
        <E extends Comparable <E>> implements PriorityQueue <E> {
    
    private Wrapper<E> [] storage;
    private long entryNumber;
    private int currentSize, maxSize, modCounter;
    
    public BinaryHeapPriorityQueue(){
        this(DEFAULT_MAX_CAPACITY);
    }
    
    public BinaryHeapPriorityQueue(int size){
	maxSize = size;
	currentSize = 0;
        entryNumber = 0;
        modCounter = 0;
        storage = new Wrapper[maxSize];
        
    }
    
    //  Inserts a new object into the priority queue.  Returns true if
    //  the insertion is successful.  If the PQ is full, the insertion
    //  is aborted, and the method returns false.
    public boolean insert(E obj){
        if(isFull()) return false;
        storage[currentSize++] = new Wrapper<E>(obj);
        trickleUp();// to sort inserted element
        modCounter++;
        return true;
    }
    
    // sort new element inserted at the end/bottom of the heap
    private void trickleUp(){
        int child = currentSize-1;
        int parent = (child-1) >> 1;// efficiently divides by 2
        Wrapper<E> value = storage[child];
        while(parent >= 0 && value.compareTo(storage[parent]) < 0){
            storage[child] = storage[parent];
            child = parent;
            parent = (parent-1) >> 1;// efficiently divides by 2
        }
        storage[child] = value;
    }
    
    //  Removes the object of highest priority that has been in the
    //  PQ the longest, and returns it.  Returns null if the PQ is empty.
    public E remove(){
        if(isEmpty()) return null;
        E tmp = storage[0].data;//element to return
        storage[0] = storage[currentSize-1];
        currentSize--;
        trickleDown();// to sort the heap
        modCounter++;
        return tmp;
    }
    
    // sort new element inserted at the top of the heap
    private void trickleDown(){
        int current = 0;// top of tree
        int child = getNextChild(current);
        Wrapper<E> tmp;
        while(child != -1 && storage[current].compareTo(storage[child]) > 0){
            tmp = storage[current];
            storage[current] = storage[child];
            storage[child] = tmp;
            current = child;
            child = getNextChild(current);
        }
    }
    
    // get smallest child of parent, based on data or entryNumber
    private int getNextChild(int current){
        int left = (current << 1)+1;// multiplies by 2, then adds 1
        int right = left+1;
        if(right < currentSize){// if there are two child
            if(storage[left].compareTo(storage[right]) < 0)
                return left;// left child is smaller
            return right;// right child is smaller
        }
        if (left < currentSize) return left;// if there is only one child
        return -1;// if no child
    }
    
    //  Deletes all instances of the parameter obj from the PQ if found, and
    //  returns true.  Returns false if no match to the parameter obj is found.
    public boolean delete(E obj){
        boolean flag = false;
        BinaryHeapPriorityQueue tmpHeap = new BinaryHeapPriorityQueue(currentSize);
        while(!isEmpty()){
            E tmp = remove();
            if(tmp.compareTo(obj) != 0){
                tmpHeap.insert(tmp);
                flag = true;
            }
        }
        if(flag){// if elements where deleted
            storage = tmpHeap.storage;
            currentSize = tmpHeap.currentSize;
            modCounter++;
        }
        return flag;
    }
   
    //  Returns the object of highest priority that has been in the
    //  PQ the longest, but does NOT remove it. 
    //  Returns null if the PQ is empty.
    public E peek(){
        if(isEmpty()) return null;
        return storage[0].data;
    } 
    
    //  Returns true if the priority queue contains the specified element
    //  false otherwise.
    public boolean contains(E obj){
        for(int i = 0; i<currentSize; i++){
            if(obj.compareTo(storage[i].data) == 0){
                return true;
            }
        }
        return false;
    }
   
    //  Returns the number of objects currently in the PQ.
    public int size(){ return currentSize; }
      
    //  Returns the PQ to an empty state.
    public void clear(){ currentSize = 0; modCounter++; }
   
    //  Returns true if the PQ is empty, otherwise false
    public boolean isEmpty(){ return currentSize == 0;}
   
    //  Returns true if the PQ is full, otherwise false.  List based
    //  implementations should always return false.
    public boolean isFull(){ return currentSize == maxSize; }  
    
    //  Returns an iterator of the objects in the PQ, in no particular
    //  order.  
    public Iterator<E> iterator(){
        return new IteratorHelper();
    }
    
    private class IteratorHelper implements Iterator<E>{
        
        int iterIndex;
        long modNumber;
        
        public IteratorHelper(){ iterIndex = 0; modNumber = modCounter; }
        
        public boolean hasNext(){
            if(modNumber != modCounter) // fail fast
                throw new ConcurrentModificationException();
            return iterIndex < currentSize;
        }
        
        public E next(){
            if(!hasNext())
                    throw new NoSuchElementException();
            return storage[iterIndex++].data;
        }
        
        public void remove(){
            throw new UnsupportedOperationException();
        }
    }
    
    // needed to keep/preserve ordering of duplicate keys
    protected class Wrapper<E extends Comparable <E>> implements Comparable<Wrapper<E>>{
        
        long number;
        E data;
        
        public Wrapper(E obj){
            number = entryNumber++;
            data = obj;
        }
        
        public int compareTo(Wrapper<E> obj){
            if(data.compareTo(obj.data) == 0)       // if data is equal, use
                return (int) (number - obj.number); // sequence number
            return data.compareTo(obj.data);
        }
        
    }
}
