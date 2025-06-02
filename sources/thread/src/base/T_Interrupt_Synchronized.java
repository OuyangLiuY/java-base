package base;

public class T_Interrupt_Synchronized {

    private static final Object o = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(()-> {
            synchronized (o) {
                SleepHelper.sleepSeconds(10);
            }
        });
        t1.start();
        SleepHelper.sleepSeconds(1);
        Thread t2 = new Thread(()-> {
            synchronized (o) {
            }
            System.out.println("t2 end!");
        });
        t2.start();
        t2.interrupt();
    }
}
