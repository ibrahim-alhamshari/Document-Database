import org.junit.jupiter.api.Test;
import queue.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class QueueTest {

    @Test
    public void enqueue(){
        Queue<Integer> queue =  new Queue<>();
        queue.enqueue(45);
        assertEquals(12 , queue.enqueue(12) );
        assertEquals(73 , queue.enqueue(73));
        assertEquals(31 , queue.enqueue(31));
    }


    @Test
    public void dequeue(){
        Queue<Integer> queue =  new Queue<>();
        queue.enqueue(7);
        queue.enqueue(12);
        queue.enqueue(3);
        queue.enqueue(5);
        queue.enqueue(9);

        assertEquals(7 , queue.dequeue());
        assertEquals(12 , queue.dequeue());
        assertEquals(3 , queue.dequeue());
    }


    @Test
    public void isContain(){
        Queue<Integer> queue =  new Queue<>();
        queue.enqueue(6);
        queue.enqueue(12);
        queue.enqueue(81);
        queue.enqueue(5);

        assertEquals(true , queue.isContain(81));
        assertEquals(false , queue.isContain(10));
        assertEquals(true , queue.isContain(6));
        assertEquals(false , queue.isContain(1));
    }


    @Test
    public void isEmpty(){
        Queue<String> queue =  new Queue<>();
        queue.enqueue("Ahmad");
        queue.enqueue("Khaled");
        queue.enqueue("Waleed");

        assertEquals(false , queue.isEmpty());
        queue.dequeue();
        queue.dequeue();
        assertEquals(false , queue.isEmpty());
        queue.dequeue();
        assertEquals(true , queue.isEmpty());
    }
}
