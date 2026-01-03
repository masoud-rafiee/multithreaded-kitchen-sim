public class Cooking {
    private String name;
    private int duration;

    //constructor
    public Cooking(String Name, int Duration) {
        this.name = Name;
        this.duration = Duration;
    }

    //EXECUTION
    public void execute() throws InterruptedException {
        System.out.println("Starting Task: " + name);
        Thread.sleep(duration * 1000);//to convert to sec
        System.out.println("Completed Task: " + name);
    }
}