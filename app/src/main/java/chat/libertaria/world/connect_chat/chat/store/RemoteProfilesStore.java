package chat.libertaria.world.connect_chat.chat.store;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.KeyIterator;
import com.snappydb.SnappydbException;

import java.util.HashMap;
import java.util.Map;

public class RemoteProfilesStore {

    private static final String DB_NAME = "addresses";

    private DB snappyDb;

    public RemoteProfilesStore(Context context) throws SnappydbException {
        snappyDb = DBFactory.open(context,DB_NAME);
    }

    public RemoteProfilesStore(String folder) throws SnappydbException {
        snappyDb = DBFactory.open(folder,DB_NAME);
    }

    /**
     *
     */
    public void insert(String profilePubKey, String profileName) throws CantInsertProfileException {
        try {
            snappyDb.put(profilePubKey, profileName);
        } catch (SnappydbException e) {
            e.printStackTrace();
            throw new CantInsertProfileException("Cant insert: "+profileName,e);
        }
    }

    /**
     * Get the address status
     */
    public String getProfileName(String profilePubKey) throws ProfileNotFoundException {
        try {
            return snappyDb.getObject(profilePubKey,String.class);
        } catch (SnappydbException e) {
            e.printStackTrace();
            throw new ProfileNotFoundException("Cant insert: "+profilePubKey,e);
        }
    }



    public Map<String, String> map() {
        Map<String,String> map = new HashMap<>();
        KeyIterator keyIterator = null;
        try {
            keyIterator = snappyDb.allKeysIterator();
            while (keyIterator.hasNext()){
                String[] keys = keyIterator.next(50);
                for (String key : keys) {
                    String name = snappyDb.getObject(key,String.class);
                    map.put(key,name);
                }
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        } finally {
            if (keyIterator!=null){
                keyIterator.close();
            }
        }
        return map;
    }


    public void close() throws SnappydbException {
        if (snappyDb!=null){
            snappyDb.close();
        }
    }

}
