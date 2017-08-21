package chat.libertaria.world.connect_chat.chat.store;

import com.snappydb.SnappydbException;

/**
 * Created by furszy on 8/18/17.
 */

class CantInsertProfileException extends Exception {
    public CantInsertProfileException(String message) {
        super(message);
    }

    public CantInsertProfileException(String s, Exception e) {
        super(s,e);
    }
}
