package chat.libertaria.world.connect_chat.chat.store;

/**
 * Created by furszy on 8/18/17.
 */

class ProfileNotFoundException extends Exception {

    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
