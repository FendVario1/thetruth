package eu.rationality.thetruth;

import org.jivesoftware.smack.packet.Presence;
import org.junit.Test;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class NickTest {
    BareJid testBareJid = JidCreate.bareFrom("test@example.de");
    public NickTest() throws XmppStringprepException {}

    @Test
    public void testNick() throws Weechat.WeechatCallException {
/*        final class FinalClass {
            final String finalMethod() { return "something"; }
        }

        FinalClass concrete = new FinalClass();

        FinalClass mock = mock(FinalClass.class);
        given(mock.finalMethod()).willReturn("not anymore");

        assertFalse(mock.finalMethod().equals(concrete.finalMethod()));
*/
        Weechat.setAPIInstance(new WeechatTest());
        ServerBuffer mockk = mock(ServerBuffer.class);
        given(mockk.getNativeId()).willReturn(111L);
        Nick nick = new Nick(mockk, testBareJid, "name",new Presence(Presence.Type.available));
        assertNotNull(nick);
    }

    @Test
    public void testRegisterBuffer() throws Weechat.WeechatCallException {
        Weechat.setAPIInstance(new WeechatTest());
        ServerBuffer buf = mock(ServerBuffer.class);
        given(buf.getNativeId()).willReturn(111L);
        Nick nick = new Nick(buf, testBareJid, "name",new Presence(Presence.Type.available));
        Buffer dumbuf = new DummyBuffer();
        nick.registerBuffer(dumbuf);
    }
    @Test
    public void testDeregisterBuffer(){
    }


    @Test
    public void testUpdateInfo() {
    }

    @Test
    public void testUpdatePresence() {
    }


    @Test
    public void testUpdateBuffers(){
    }

    @Test
    public void testDestroy() {
    }

}