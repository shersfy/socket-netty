package org.shersfy.server.test;

import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class SocketClient {
    
    public static void main(String[] args) throws Exception {
        singleThread();
//        multiThread();
    }
    
    
    public static void singleThread() throws Exception {
        Socket sock = new Socket("localhost", 20188);
        OutputStream output = sock.getOutputStream();
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入:");
        while(sc.hasNextLine()) {
            String msg = sc.nextLine();
            System.out.println(msg);
            output.write(msg.getBytes());
            output.flush();
        }
        sc.close();
        sock.close();
    }
    
    public static void multiThread() throws Exception {
        
        CountDownLatch latch = new CountDownLatch(10);
        for(int cnt = 0; cnt<10; cnt++) {
            final int thread = cnt;
            new Thread(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        Socket sock = new Socket("localhost", 20188);
                        OutputStream output = sock.getOutputStream();
                        for(int i=0; i<10; i++) {
                            output.write(String.format("client %s sleep %s seconds", thread, i).getBytes());
                            output.flush();
                            Thread.sleep(1000);
                        }
                        sock.close();
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            }).start();
        }
        latch.await();
        
    }

}
