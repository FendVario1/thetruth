package eu.rationality.thetruth;

import org.junit.Test;
import eu.rationality.thetruth.Buffer;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.swing.*;

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
    public void testByidRegisterExists() {
        BufferManager testman = BufferManager.getinstance();
        for (int i = 0; i<10; i++) {
            try {
                Buffer buf = new DummyBuffer();
                Buffer res = testman.byid(111);
                assertNotNull("registered Buffer should not be NULL", res);
                break;
            } catch (Exception e) { }
        }

    }
    @Test
    public void testDeregisterNonExisting() {
        BufferManager testman = BufferManager.getinstance();
        try {
            testman.deregister(5L);
        } catch (Exception e) {
            System.out.println("Exception: " + e.toString());
        }
    }
    @Test
    public void testDeregisterExisting() {
        BufferManager testman = BufferManager.getinstance();
        Buffer buf;
        for (int i = 0; i<10; i++) {
            try {
            buf = new DummyBuffer();
            testman.deregister(buf.nativeid);
            Buffer res = testman.byid(buf.nativeid);
            assertNull("deregistered Buffer should be Null", res);
            break;
            } catch (Exception e) {
                System.out.println("Exception: " + e.toString());
            }
        }
    }

    private static final class DummyBuffer extends Buffer {

        public DummyBuffer() throws Weechat.WeechatCallException {  //TODO do whatever's necessary
            super("test");
            nativeid = 111;
        }
    }
}


/*
    private static final class DummyConnectionConfiguration extends ConnectionConfiguration {

        protected DummyConnectionConfiguration(Builder builder) {
            super(builder);
        }

        public static Builder builder() {
            return new Builder();
        }

        private static final class Builder
                        extends ConnectionConfiguration.Builder<Builder, DummyConnectionConfiguration> {

            @Override
            public DummyConnectionConfiguration build() {
                return new DummyConnectionConfiguration(this);
            }

            @Override
            protected Builder getThis() {
                return this;
            }

*/

/*
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MyTests {

    @Test
    public void multiplicationOfZeroIntegersShouldReturnZero() {
        MyClass tester = new MyClass(); // MyClass is tested

        // assert statements
        assertEquals(0, tester.multiply(10, 0), "10 x 0 must be 0");
        assertEquals(0, tester.multiply(0, 10), "0 x 10 must be 0");
        assertEquals(0, tester.multiply(0, 0), "0 x 0 must be 0");
    }
}
*/