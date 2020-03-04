package eu.rationality.thetruth;

import org.junit.Test;

import static org.junit.Assert.*;

public class BufferTest {

    /*@Test
    public void testGetNativeId() {
        for (int i = 0; i<10; i++) {
            try {
                Buffer buf = new BufferManagerTest.DummyBuffer();
                assertEquals("getNativeID not correct",111L, (long) buf.getNativeId());
                break;
            } catch (Exception e) { }
        }
    }*/

    @Test
    public void sendMsg() {
        //TODO evtl
    }

    @Test
    public void receiveCommand() {
        try {
            Buffer buf = new DummyBuffer();
            //assertEquals();buf.receiveCommand("blub",null);
        } catch (Exception exception) { }
    }

    @Test
    public void handleInput() {
    }

    @Test
    public void closeCallback() {
    }


    private static final class DummyBuffer extends Buffer {

        public DummyBuffer() throws Weechat.WeechatCallException {
            super("test");
            nativeid = 111;
        }
    }
}