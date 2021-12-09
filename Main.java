import java.util.concurrent.Semaphore;
import java.util.Random;

public class Main {
    static final int BUFFER_SIZE = 10;  
    static final int num_check = 10;     
    static int end = num_check;         
    static int [] buffer = new int[BUFFER_SIZE];
    static int next_in = 0 ;
    static int next_out = 0;
    static int f = 0;
    static int e = buffer.length;
    static boolean check = false;
    static Semaphore P = new Semaphore(buffer.length);
    static Semaphore V = new Semaphore(0);
    static Random random = new Random();
    
    // thread for simulatneous run
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    producer();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    consumer();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    public static void producer() throws InterruptedException{
        while (!check){
            int k1 = random.nextInt(BUFFER_SIZE)+0;

            for(int i = 0; i < k1; i++){
                if(P.availablePermits()>0){
                    // operating P
                    P.acquire(e);
                    buffer[(next_in + i) % buffer.length] += 1;
                    // operating V
                    V.release(f);
                }else{
                    break;
                }
            }
            next_in += (k1 % buffer.length);
            System.out.println("Producer <= " + next_in);

            end--;
            if(end <= 0){
                System.out.println("Producer without race problem.");
                check = true;
            }
            // thread sleep for random time interval
            Thread.sleep((int) (Math.random()*100 + 0));
        }
    }
    
    public static void consumer() throws InterruptedException {
        while (!check){
            // consumer thread sleep for random time interval
            Thread.sleep((int) (Math.random()*100 + 0));
            // generate random k2 between 0 and buffer size.
            int k2 = random.nextInt(BUFFER_SIZE) + 0;
           
            int data;
            for(int i = 0; i < k2-1; i++){
                // operating P
                P.acquire(f);
                data=buffer[(next_out + i) % buffer.length];
                //report race condition
                if(data > 1){
                    System.out.println("REPORT: Race Condition");
                    System.exit(1);
                }
                // operating V
                V.release(e);           
            }
            // next_out update
            next_out += (k2 % buffer.length);
            System.out.println("Consumer =>  " + next_out);

            //end program
            end--;
            if(end <= 0) {
                System.out.println("Consumer without race problem.");
                check = true;
            }
            Thread.sleep((int) (Math.random()*100 + 0));
        }
    }    
}