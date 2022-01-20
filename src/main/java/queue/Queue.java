package queue;

public class Queue<T> {

    public Node<T> front;
    public Node<T> rear;

    public T enqueue(T value){
        Node<T> newNode = new Node<>(value);

        if(rear==null){
            rear = newNode;
            front=newNode;
        }else {
            rear.next=newNode;
            rear = newNode;
        }

        System.out.println("Value added to enqueue: " + value);
        return rear.value;
    }


    public T dequeue(){
        Node<T> tmp = front;

        try {
            front = front.next;
            tmp.next=null;
            System.out.println("dequeue: " + tmp.value);
        }catch (Exception e){
            System.out.println("You are pass an empty queue");
        }

        return tmp.value;
    }


    public boolean isContain(T value){
        if(front==null)
            return false;

        Node<T> tmp=front;

        while (tmp != null){
            if(tmp.value.equals(value)){
                return true;
            }
            tmp=tmp.next;
        }

        return false;
    }


    public boolean isEmpty(){
        return front==null;
    }


}
