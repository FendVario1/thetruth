package eu.rationality.thetruth;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BufferTest {
    @Test
    public void testGetNativeId() {
        for (int i = 0; i<10; i++) {
            try {
                Buffer buf = new DummyBuffer();
                assertEquals("getNativeID not correct",111L, (long) buf.getNativeId());
                break;
            } catch (Exception e) { }
        }
    }

    @Test
    public void testPrint() {
        //todo
    }

    @Test
    public void testPrintErr() {
        //todo
    }
    @Test
    public void testPrint_prefix() {
        //todo
    }
    @Test
    public void testPrintMsgDateTags() {
        //todo
    }

    @Test
    public void testSendMsg() {/*function empty*/}

    @Test
    public void testReceiveCommand() {
        try {
            Buffer buf = new DummyBuffer();
            assertEquals(buf.receiveCommand("blub",null), Weechat.WEECHAT_RC_OK);
        } catch (Exception e) { }
    }

    @Test
    public void testHandleInput() {
        try {
            Buffer buf = new DummyBuffer();
            assertEquals(buf.handleInput("blub"), Weechat.WEECHAT_RC_OK);
        } catch (Exception e) { }
    }

    @Test
    public void testCreateNativeBufferException() {
     //   assertThrows("message", Weechat.WeechatCallException, Buffer.createNativeBuffer("0"));//todo todo
    }
    @Test
    public void testCreateNativeBuffer() {
     //   assertEquals("message", 1, Buffer.createNativeBuffer("1")); //todo todo
        //todo
    }

    @Test
    public void testCloseCallback() {/*function empty*/}


    private static final class DummyBuffer extends Buffer {

        public DummyBuffer() throws Weechat.WeechatCallException {
            super("test");
            nativeid = 111;
        }
    }
}