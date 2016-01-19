
package faf.pad4.common;

import java.io.InputStream;
import java.io.OutputStream;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author andrew
 */
public class JsonSerializer implements Serializer {
    public <T> void serialize(T object, OutputStream out) throws Exception {
        ObjectMapper om = new ObjectMapper();
        om.writerWithDefaultPrettyPrinter().writeValue(out, object);
    }

    public <T> T deserialize(InputStream in, Class<T> clazz) throws Exception {
        ObjectMapper om = new ObjectMapper();
        return (T) om.readValue(in, clazz);
    }
}
