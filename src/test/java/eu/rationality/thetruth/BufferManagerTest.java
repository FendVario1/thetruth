package eu.rationality.thetruth;

import org.junit.Test;

import static org.junit.Assert.*;

public class BufferManagerTest {

    @Test
    public void getinstance() {
        BufferManager testman = BufferManager.getInstance();
        assertNotNull("BufferManager should not return Null", testman);
    }
    @Test
    public void testByidNotExisting() {
        BufferManager testman = BufferManager.getInstance();
        assertNull("id doesn't exist so byid should be NULL", testman.byId(50000));
    }
    @Test
    public void testByidRegisterExists() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        BufferManager testman = BufferManager.getInstance();
        Buffer buf = new DummyBuffer();
        testman.register(buf);
        Buffer res = testman.byId(111);
        assertNotNull("registered Buffer should not be NULL", res);
    }
    @Test
    public void testDeregisterNonExisting() {
        Weechat.setAPIInstance(new WeechatTest());
        BufferManager testman = BufferManager.getInstance();
        testman.deregister(5L);
        assertEquals("should be deregistered", null, testman.byId(5));
    }
    @Test
    public void testDeregisterExisting() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        BufferManager testman = BufferManager.getInstance();
        Buffer buf;
        buf = new DummyBuffer();
        testman.deregister(buf.nativeId);
        Buffer res = testman.byId(buf.nativeId);
        assertNull("deregistered Buffer should be Null", res);
    }
}