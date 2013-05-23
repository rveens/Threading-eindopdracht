package nl.avans.threading;

/**
 * Created with IntelliJ IDEA.
 * User: rick
 * Date: 5/23/13
 * Time: 8:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class ThreadExamples {

    Thread t;

    public ThreadExamples()
    {
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Dit is een test");
            }
        });
    }

    /* Starting a thread: http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html */
    private void startThreadExample()
    {
        t.start(); // Start thread t. The Run method of the Runnable is executed on another thread.
    }

    /* Joining threads http://docs.oracle.com/javase/tutorial/essential/concurrency/join.html */
    private void joinThread()
    {
        try {
            t.join(); // Have the current thread (in which this function is executed) wait for thread t to finish.
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}

