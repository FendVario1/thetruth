package eu.rationality.thetruth;

import org.jivesoftware.smack.roster.Roster;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ServerBufferTest {

    @Test
    public void testServerBuffer() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        Server serv = mock(Server.class);
        given(serv.getJID()).willReturn("test@example.de");
        Roster rost = mock(Roster.class);
        ServerBuffer servBuf = new ServerBuffer(serv, rost);
        assertNotNull(servBuf);
    }

    @Test
    public void testGetNicklist() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        Server serv = mock(Server.class);
        given(serv.getJID()).willReturn("test@example.de");
        Roster rost = mock(Roster.class);
        ServerBuffer servBuf = new ServerBuffer(serv, rost);
        assertNotNull(servBuf.getNicklist());
    }

    @Test
    public void testGetServer() {
    }

    @Test
    public void testPrintSelfMessage() {
    }

    @Test
    public void testHandleInput() {
    }

    @Test
    public void testReceiveCommand() {
    }

    @Test
    public void testCloseCallback() {
    }
}

