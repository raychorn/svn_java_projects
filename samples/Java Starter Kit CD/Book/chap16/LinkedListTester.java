class LinkedListTester {
    public static void main(String argv[]) {

collections.LinkedList  aLinkedList = null; /* createLinkedList(); */
java.util.Enumeration   e = aLinkedList.enumerate();

while (e.hasMoreElements()) {
    Object  anObject = e.nextElement();
    // do something useful with anObject
}

    }
}
