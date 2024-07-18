// Name: Ahmad Soboh
// Student ID: 300294253
//
// The Planting Synchronization Problem
//
import java.util.concurrent.Semaphore;

public class Planting {
    public static void main(String args[]) {
        // Create Student, TA, Professor threads
        TA ta = new TA();
        Professor prof = new Professor(ta);
        Student stdnt = new Student(ta);

        // Start the threads
        prof.start();
        ta.start();
        stdnt.start();

        // Wait for prof to call it quits
        try { prof.join(); } catch (InterruptedException e) { };
        // Terminate the TA and Student Threads
        ta.interrupt();
        stdnt.interrupt();
    }
}

class Student extends Thread {
    TA ta;
    public Student(TA taThread) {
        ta = taThread;
    }

    public void run() {
        while (true) {
            try {
                ta.studentSemaphore.acquire();
                ta.shovel.acquire();
                
                System.out.println("Student: Got the shovel");
                Thread.sleep((int) (100 * Math.random()));
                ta.incrHoleDug();
                System.out.println("Student: Hole " + ta.getHoleDug() + " Dug");
                
                ta.shovel.release();
                System.out.println("Student: Letting go of the shovel");
                
                ta.professorSemaphore.release();
                ta.studentSemaphore.release();
                
                if (isInterrupted()) break;
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("Student is done");
    }
}

class TA extends Thread {
    private int holeFilledNum = 0;
    private int holePlantedNum = 0;
    private int holeDugNum = 0;
    private final int MAX = 5;

    Semaphore shovel = new Semaphore(1);
    Semaphore professorSemaphore = new Semaphore(0);
    Semaphore taSemaphore = new Semaphore(0);
    Semaphore studentSemaphore = new Semaphore(MAX);

    public int getMAX() { return MAX; }
    public void incrHoleDug() { holeDugNum++; }
    public int getHoleDug() { return holeDugNum; }
    public void incrHolePlanted() { holePlantedNum++; }
    public int getHolePlanted() { return holePlantedNum; }

    public void run() {
        while (true) {
            try {
                taSemaphore.acquire();
                shovel.acquire();
                
                System.out.println("TA: Got the shovel");
                Thread.sleep((int) (100 * Math.random()));
                holeFilledNum++;
                System.out.println("TA: The hole " + holeFilledNum + " has been filled");
                
                shovel.release();
                System.out.println("TA: Letting go of the shovel");
                
                studentSemaphore.release();
                
                if (isInterrupted()) break;
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("TA is done");
    }
}

class Professor extends Thread {
    TA ta;

    public Professor(TA taThread) {
        ta = taThread;
    }

    public void run() {
        while (ta.getHolePlanted() <= 20) {
            try {
                ta.professorSemaphore.acquire();
                
                Thread.sleep((int) (50 * Math.random()));
                ta.incrHolePlanted();
                System.out.println("Professor: All be advised that I have completed planting hole " +
                        ta.getHolePlanted());
                
                ta.taSemaphore.release();
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("Professor: We have worked enough for today");
    }
}
