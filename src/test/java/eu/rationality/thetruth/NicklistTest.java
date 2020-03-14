package eu.rationality.thetruth;

import org.jivesoftware.smack.roster.Roster;
import org.junit.Test;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import static org.mockito.Mockito.mock;

public class NicklistTest {
    BareJid testBareJid = JidCreate.bareFrom("test@example.de");
    public NicklistTest() throws XmppStringprepException {
    }

/*
        Weechat.setAPIInstance(new WeechatTest());
        ServerBuffer mockk = mock(ServerBuffer.class);
        given(mockk.getNativeId()).willReturn(111L);
        Nick nick = new Nick(mockk, testBareJid, "name",new Presence(Presence.Type.available));
        assertNotNull(nick);
 */


    @Test
    public void testNicklist() {
        Weechat.setAPIInstance(new WeechatTest());
        ServerBuffer serv = mock(ServerBuffer.class);
        Roster rost = mock(Roster.class);
        Nicklist nl = new Nicklist(serv, rost);
    }

    private Nicklist makeNicklist() {
        Weechat.setAPIInstance(new WeechatTest());
        ServerBuffer serv = mock(ServerBuffer.class);
        Roster rost = mock(Roster.class);
        Nicklist nl = new Nicklist(serv, rost);
        return nl;
    }

    @Test
    public void testRosterEntries() {
    }

    @Test
    public void testEntriesAdded() {
    }

    @Test
    public void testEntriesUpdated() {
    }

    @Test
    public void testEntriesDeleted() {
    }

    @Test
    public void testPresenceChanged() {
    }

    @Test
    public void testProcessPresence() {
    }

    @Test
    public void testGetJidFromString() {
    }

    @Test
    public void testRegisterBuffer() {
    }

    @Test
    public void testDeregisterBuffer() {
    }

    @Test
    public void testAddUser() {
    }

    @Test
    public void testRemoveUser() {
    }
}