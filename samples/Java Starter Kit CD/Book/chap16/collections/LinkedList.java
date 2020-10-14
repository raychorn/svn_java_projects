package  collections;

import  java.util.Enumeration;  // added

public class  LinkedList {
    private Node  root;

    public Enumeration  enumerate() {
        return new LinkedListEnumerator(root);
    }
}

class  Node {
    private Object  contents;
    private Node    next;

    public  Object  contents() {
        return contents;
    }

    public  Node    next() {
        return next;
    }
}

class  LinkedListEnumerator implements Enumeration {
    private Node  currentNode;

    LinkedListEnumerator(Node  root) {
        currentNode = root;
    }

    public boolean  hasMoreElements() {
        return currentNode != null;
    }

    public Object   nextElement() {
        Object  anObject = currentNode.contents();

        currentNode = currentNode.next();
        return  anObject;
    }
}
