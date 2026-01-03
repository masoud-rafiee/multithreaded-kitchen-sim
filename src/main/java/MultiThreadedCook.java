import java.util.concurrent.Semaphore;

public class MultiThreadedCook {
    public static void main(String[] args) {
        //create semaphore for task synchronization
        Semaphore[] taskCompleted = new Semaphore[9];//hold completed status of a task(sem)
        for (int i = 0; i < taskCompleted.length; i++) {
            taskCompleted[i] = new Semaphore(0);//every sem needs to wait until someone calls release
        }
        //cooking tasks naming
        Cooking[] Tasks = new Cooking[9];
        Tasks[0] = new Cooking("Cut onions ", 3);
        Tasks[1] = new Cooking("Mince meat ", 5);
        Tasks[2] = new Cooking("Slice aubergines ", 4);
        Tasks[3] = new Cooking("Make sauce ", 7);
        Tasks[4] = new Cooking("Finish Bechamel ", 6);
        Tasks[5] = new Cooking("Layout the layers ", 8);
        Tasks[6] = new Cooking("Put Bechamel and Cheese ", 4);
        Tasks[7] = new Cooking("Turn on oven ", 2);
        Tasks[8] = new Cooking("Cook ", 10);

        /*2d array for task dependencies since each task has its own semaphores to wait for*/
        Semaphore[][] wait = new Semaphore[9][];
        Semaphore[][] signal = new Semaphore[9][];

        //task 1: cut onions
        wait[0] = new Semaphore[0];//dont wait
        signal[0] = new Semaphore[]{taskCompleted[0]};//signal task 4

        //mince meat: task 2
        wait[1] = new Semaphore[0];//dont wait
        signal[1] = new Semaphore[]{taskCompleted[1]}; //signals task 4

        //task 3: slice aubergines
        wait[2] = new Semaphore[0];//wait for none
        signal[2] = new Semaphore[]{taskCompleted[2]}; //signals task 6

        //task 4 : make sauce
        wait[3] = new Semaphore[]{taskCompleted[0], taskCompleted[1]};//wait for tasks 1 and 2
        signal[3] = new Semaphore[]{taskCompleted[3]};//signal task 6

        //task 5: finished bechemel
        wait[4] = new Semaphore[0];//no waiting
        signal[4] = new Semaphore[]{taskCompleted[4]};//signal task 7

        //task 6: layout the layers
        wait[5] = new Semaphore[]{taskCompleted[2], taskCompleted[3]};//wait for tasks 3 and 4
        signal[5] = new Semaphore[]{taskCompleted[5]};

        //task 7: Put Bechamel and Cheese
        wait[6] = new Semaphore[]{taskCompleted[4], taskCompleted[5]}; //waits for Tasks 5, 6
        signal[6] = new Semaphore[]{taskCompleted[6]}; //signals Task 9

        //task 8: Turn on oven
        wait[7] = new Semaphore[0]; //Waits for none
        signal[7] = new Semaphore[]{taskCompleted[7]}; //signals Task 9

        //task 9: Cook
        wait[8] = new Semaphore[]{taskCompleted[6], taskCompleted[7]}; //waits for Tasks 7, 8
        signal[8] = new Semaphore[0]; //Signals none

        //starting the threads
        Thread[] threads = new Thread[9];
        for (int i = 0; i < threads.length; i++) {
            final int taskIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    //wait for all dependencies
                    for (Semaphore semaphore : wait[taskIndex]) {
                        semaphore.acquire();
                    }
                    //exxecute the task
                    Tasks[taskIndex].execute();

                    //signal completion to dependent tasks
                    for (Semaphore semaphore : signal[taskIndex]) {
                        semaphore.release();
                    }
                } catch (InterruptedException e) {
                    System.out.println("Task " + (taskIndex + 1) + " interrupted!");
                }
            });
            threads[i].start();
        }
        //wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted! :| ");
            }
        }
        System.out.println("\n\n-------------\nRecipe Completed!");
    }
}