package eu.rationality.thetruth;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BufferManager {
	private static BufferManager instance = null;
	private Map<Long, Buffer> id2buffer;

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public static BufferManager getinstance() {
		if (instance == null) {
			instance = new BufferManager();
		}
		return instance;
	}
	
	private BufferManager() {
		this.id2buffer = new HashMap<>();
	}
	
	public Buffer byid(long id) {
		return id2buffer.get(id);
	}
	
	public void register(Buffer b) {
		id2buffer.put(b.getNativeId(), b);
	}
	
	public void deregister(Long nativeid) {
		Buffer removed = id2buffer.remove(nativeid);
		if (removed == null) {
			LOGGER.log(Level.WARNING, "Closing buffer " + Long.toHexString(nativeid)
					+ " which is not registered with the BufferManager");
		} else {
			removed.closeCallback();
		}
	}
}
