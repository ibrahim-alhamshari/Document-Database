import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import queue.Queue;

import static org.junit.jupiter.api.Assertions.*;

public class QueueTest {

    Queue<Integer> queue;

    @BeforeEach
    public void initializeQueue(){
        queue =  new Queue<>();
        queue.enqueue(7);
        queue.enqueue(12);
        queue.enqueue(3);
        queue.enqueue(5);
        queue.enqueue(9);
    }


    @Test
    @DisplayName("Test for enqueue() method")
    public void enqueue(){
        assertEquals(12 , queue.enqueue(12) );
        assertEquals(73 , queue.enqueue(73));
        assertEquals(31 , queue.enqueue(31));
    }


    @Test
    @DisplayName("Test for dequeue() method")
    public void dequeue(){
        assertEquals(7 , queue.dequeue());
        assertEquals(12 , queue.dequeue());
        assertEquals(3 , queue.dequeue());
    }


    @Test
    @DisplayName("Test for isContain() method")
    public void isContain(){
        assertTrue(queue.isContain(12));
        assertFalse(queue.isContain(10));
        assertTrue(queue.isContain(3));
        assertFalse(queue.isContain(1));
    }


    @Test
    @DisplayName("Test for isEmpty() method")
    public void isEmpty(){
        assertFalse(queue.isEmpty());
        queue.dequeue();
        queue.dequeue();
        assertFalse(queue.isEmpty());
        queue.dequeue();
        queue.dequeue();
        queue.dequeue();
        assertTrue(queue.isEmpty());
    }
}
