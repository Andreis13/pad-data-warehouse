
package faf.pad4.common;

import java.lang.Exception;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author andrew
 */
public interface Serializer {
    public abstract <T> void serialize(T object, OutputStream out) throws Exception;
    public abstract <T> T deserialize(InputStream in, Class<T> clazz) throws Exception;
}
