package org.agilewiki.jactor.lpc.timing;

import junit.framework.TestCase;
import org.agilewiki.jactor.concurrent.JAThreadManager;
import org.agilewiki.jactor.concurrent.ThreadManager;
import org.agilewiki.jactor.lpc.*;

public class MailboxTest extends TestCase {
    public void testTiming() {
        int c = 2;
        int b = 3;
        int p = 1;
        int t = 1;

        //int c = 40000000;
        //int b = 1;
        //int p = 16;
        //int t = 4;

        //burst size of 1
        //16 parallel runs of 80000000 messages each.
        //1280000000 messages sent with 4 threads.
        //msgs per sec = 275328027
        //3.6 nanoseconds per message test

        //int c = 40000;
        //int b = 1000;
        //int p = 16;
        //int t = 4;

        //burst size of 1000
        //16 parallel runs of 80000000 messages each.
        //1280000000 messages sent with 4 threads.
        //msgs per sec = 293040293
        //3.4 nanoseconds per message

        MailboxFactory mailboxFactory = JMailboxFactory.newMailboxFactory(t);
        try {
            LPCActor[] senders = new LPCActor[p];
            int i = 0;
            while (i < p) {
                LPCMailbox echoMailbox = mailboxFactory.createMailbox();
                LPCActor echo = new Echo(echoMailbox);
                echo.setInitialBufferCapacity(b + 10);
                LPCMailbox senderMailbox = mailboxFactory.createAsyncMailbox();
                if (b == 1) senders[i] = new Sender1(senderMailbox, echo, c, b);
                else senders[i] = new Sender(senderMailbox, echo, c, b);
                senders[i].setInitialBufferCapacity(b + 10);
                i += 1;
            }
            Driver driver = new Driver(mailboxFactory.createMailbox(), senders, p);
            JLPCFuture future = new JLPCFuture();
            future.send(driver, future);
            future.send(driver, future);
            long t0 = System.currentTimeMillis();
            future.send(driver, future);
            long t1 = System.currentTimeMillis();
            System.out.println("" + p + " parallel runs of " + (2L * c * b) + " messages each.");
            System.out.println("" + (2L * c * b * p) + " messages sent with " + t + " threads.");
            if (t1 != t0)
                System.out.println("msgs per sec = " + ((2L * c * b * p) * 1000L / (t1 - t0)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mailboxFactory.close();
        }
    }
}