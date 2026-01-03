import java.util.concurrent.Semaphore;

public class CookTaskThread extends Thread {
    private Cooking task;
    private Semaphore[] wait; // arrays of semaphore this task must wait
    private Semaphore[] signal; // arrays of semaphore for say its done

    //constructor
    public CookTaskThread(Cooking task, Semaphore[] waitFor, Semaphore[] signal) {
        this.signal = signal;
        this.task = task;
        this.wait = waitFor;
    }

    //i need my own run method
    @Override
    public void run() {
        try {
            //for each semaphore wait for pre-req tasks (until a sem is available)
            for (Semaphore semaphore : wait)
                semaphore.acquire();//decreament 1
            task.execute(); // now execute
            for (Semaphore semaphore : signal)//signal others (dependent ones) that It's done--> means available it is
                semaphore.release();//add one back
        } catch (InterruptedException e) {
            System.out.println("Task interrupted: " + e.getMessage());
        }
    }
}
