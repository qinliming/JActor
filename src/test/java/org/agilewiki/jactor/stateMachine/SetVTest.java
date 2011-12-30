package org.agilewiki.jactor.stateMachine;

import junit.framework.TestCase;
import org.agilewiki.jactor.*;
import org.agilewiki.jactor.lpc.JLPCActor;

public class SetVTest extends TestCase {
    public void test() {
        MailboxFactory mailboxFactory = JAMailboxFactory.newMailboxFactory(1);
        try {
            Actor actor = new SetV1(mailboxFactory.createMailbox());
            JAFuture future = new JAFuture();
            System.out.println(future.send(actor, null));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mailboxFactory.close();
        }
    }

    class SetV1 extends JLPCActor {

        SetV1(Mailbox mailbox) {
            super(mailbox);
        }

        @Override
        protected void processRequest(Object unwrappedRequest, ResponseProcessor rp) throws Exception {
            SMBuilder smb = new SMBuilder();
            smb._set("Hello world!", "r1");
            smb._set(new ObjectFunc() {
                @Override
                public Object get(StateMachine stateMachine) {
                    System.out.println(stateMachine.get("r1"));
                    return null;
                }
            });
            smb.call(rp);
            //Output:
            //Hello world!
            //null
        }
    }
}