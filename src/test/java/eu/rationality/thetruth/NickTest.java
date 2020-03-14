package eu.rationality.thetruth;

import org.jivesoftware.smack.packet.Presence;
import org.junit.Test;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class NickTest {
    BareJid testBareJid = JidCreate.bareFrom("test@example.de");
    public NickTest() throws XmppStringprepException {}

    @Test
    public void testNick() {
        Weechat.setAPIInstance(new WeechatTest());
        ServerBuffer mockk = mock(ServerBuffer.class);
        given(mockk.getNativeId()).willReturn(111L);
        Nick nick = new Nick(mockk, testBareJid, "name",new Presence(Presence.Type.available));
        assertNotNull(nick);
    }

    private Nick makeNick() {
        Weechat.setAPIInstance(new WeechatTest());
        ServerBuffer buf = mock(ServerBuffer.class);
        given(buf.getNativeId()).willReturn(111L);
        Nick nick = new Nick(buf, testBareJid, "name",new Presence(Presence.Type.available));
        return nick;
    }

    @Test
    public void testRegisterBuffer() throws Weechat.WeechatCallException {
        Nick nick = makeNick();
        Buffer dumBuf = new DummyBuffer();
        nick.registerBuffer(dumBuf);
    }
    @Test
    public void testDeregisterBuffer() throws Weechat.WeechatCallException {
        Nick nick = makeNick();
        Buffer dumBuf = new DummyBuffer();
        nick.registerBuffer(dumBuf);
        nick.deregisterBuffer(dumBuf);
    }
    @Test
    public void testDeregisterBufferNotExisting() throws Weechat.WeechatCallException {
        Nick nick = makeNick();
        Buffer dumBuf = new DummyBuffer();
        nick.deregisterBuffer(dumBuf);
    }

    @Test
    public void testUpdateInfo() {
        Nick nick = makeNick();
        nick.updateInfo(testBareJid, "testName");
    }

    @Test
    public void testUpdatePresenceToAway() {
        Nick nick = makeNick();
        nick.updatePresence(new Presence(Presence.Type.available, "status", 1, Presence.Mode.away));
        assertTrue(nick.prefixColor.equals("yellow"));
    }
    @Test
    public void testUpdatePresenceToAvailable() {
        Nick nick = makeNick();
        nick.updatePresence(new Presence(Presence.Type.available));
        assertTrue(nick.prefixColor.equals("green"));
    }
    @Test
    public void testUpdatePresenceToOffline() {
        Nick nick = makeNick();
        nick.updatePresence(new Presence(Presence.Type.unavailable));
        assertTrue(nick.prefixColor.equals("red"));
    }

    @Test
    public void testUpdateBuffers(){
        Nick nick = makeNick();
        nick.updateBuffers();
    }

    @Test
    public void testDestroy() {
        Nick nick = makeNick();
        nick.destroy();
    }
}