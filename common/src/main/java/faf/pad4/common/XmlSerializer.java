
package faf.pad4.common;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBContext;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author andrew
 */
public class XmlSerializer implements Serializer {
    public <T> void serialize(T object, OutputStream out) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
        Marshaller m = jaxbContext.createMarshaller();

        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        m.marshal(object, out);
    }

    public <T> T deserialize(InputStream in, Class<T> clazz) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller u = jaxbContext.createUnmarshaller();

        return (T) u.unmarshal(in);
    }
}
