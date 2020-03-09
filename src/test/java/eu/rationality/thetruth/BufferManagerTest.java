package eu.rationality.thetruth;

import org.junit.Test;

import static org.junit.Assert.*;

public class BufferManagerTest {

    @Test
    public void getinstance() {
        BufferManager testman = BufferManager.getinstance();
        assertNotNull("BufferManager should not return Null", testman);
    }
    @Test
    public void testByidNotExisting() {
        BufferManager testman = BufferManager.getinstance();
        assertNull("id doesn't exist so byid should be NULL", testman.byid(50000));
    }
    @Test
    public void testByidRegisterExists() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        BufferManager testman = BufferManager.getinstance();
        Buffer buf = new DummyBuffer();
        testman.register(buf);
        Buffer res = testman.byid(111);
        assertNotNull("registered Buffer should not be NULL", res);
    }
    @Test
    public void testDeregisterNonExisting() {
        Weechat.setAPIInstance(new WeechatTest());
        BufferManager testman = BufferManager.getinstance();
        testman.deregister(5L);
        assertEquals("should be deregistered", null, testman.byid(5));
    }
    @Test
    public void testDeregisterExisting() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        BufferManager testman = BufferManager.getinstance();
        Buffer buf;
        buf = new DummyBuffer();
        testman.deregister(buf.nativeid);
        Buffer res = testman.byid(buf.nativeid);
        assertNull("deregistered Buffer should be Null", res);
    }

    static final class DummyBuffer extends Buffer {

        public DummyBuffer() throws Weechat.WeechatCallException {
            super("test");
        }
    }
}