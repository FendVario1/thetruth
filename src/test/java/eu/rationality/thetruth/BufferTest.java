package eu.rationality.thetruth;

import org.junit.Test;

import static org.junit.Assert.*;

public class BufferTest {
    @Test
    public void testGetNativeId() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        Buffer buf = new DummyBuffer();
        assertEquals("getNativeID not correct",111L, (long) buf.getNativeId());
    }

    @Test
    public void testPrint() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        Buffer tBuffer = new DummyBuffer();
        tBuffer.print("myString");
    }

    @Test
    public void testPrintErr() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        Buffer tBuf = new DummyBuffer();
        tBuf.printErr("testing");
    }
    @Test
    public void testPrint_prefix() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        Buffer tBuf = new DummyBuffer();
        tBuf.print_prefix("testPrefix", "testing");
    }
    @Test
    public void testPrintMsgDateTags() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        Buffer tBuf = new DummyBuffer();
        tBuf.printMsgDateTags(555, "testSender", "testData", "testTags");
    }

    @Test
    public void testSendMsg() {/*function empty*/}

    @Test
    public void testReceiveCommand() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        Buffer buf = new DummyBuffer();
        assertEquals(buf.receiveCommand("blub",null), Weechat.WEECHAT_RC_OK);
    }
//todo
    @Test
    public void testHandleInput() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        Buffer buf = new DummyBuffer();
        assertEquals("should return WEECHAT_RC_OK", Weechat.WEECHAT_RC_OK, buf.handleInput("testString"));
    }

    @Test
    public void testCreateNativeBufferException() {
        Weechat.setAPIInstance(new WeechatTest());
        try {
            Buffer.createNativeBuffer("0");
        } catch (Weechat.WeechatCallException e) {
            assertTrue(true);
            return;
        }
        assertTrue("Should throw WeechatCallException",false);


    }
    @Test
    public void testCreateNativeBuffer() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        assertEquals("nativeid should be 111", 111, Buffer.createNativeBuffer("111"));
    }

    @Test
    public void testCloseCallback() {/*function empty*/}


}

class DummyBuffer extends Buffer {

    public DummyBuffer() throws Weechat.WeechatCallException {
        super("testBufferName");
    }
}