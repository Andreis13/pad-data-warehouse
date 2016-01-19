
package faf.pad4.warehouse;

import java.util.List;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Collections;
import javax.xml.bind.JAXBException;
import faf.pad4.common.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author andrew
 */
public class EmployeesController {
    public EmployeesController(HttpExchange he) {
        this.he = he;
        this.params = parseQuery(he.getRequestURI().getQuery());
        this.headers = he.getRequestHeaders();
        this.in = he.getRequestBody();
        this.out = he.getResponseBody();
        this.storage = EmployeeStorage.getInstance();
    }

    public void index() throws Exception {
        int limit = Integer.parseInt(params.getOrDefault("limit", "0"));
        int offset = Integer.parseInt(params.getOrDefault("offset", "0"));

        List<Employee> list = storage.getList(offset, limit);

        he.sendResponseHeaders(200, 0);
        sendResponse(new EmployeeListWrapper(list));
    }

    public void show() throws Exception {

        int id = Integer.parseInt(params.get("id"));

        try {
            Employee e = storage.get(id);
            he.sendResponseHeaders(200, 0);
            sendResponse(e);
        } catch (IndexOutOfBoundsException ex) {
            recordNotFound();
        }
    }

    public void create() throws Exception {
        String from = headers.getFirst("From");

        if (from == null) {
            preconditionFailed("Missing 'From' header.");
            return;
        }

        if (inputSerializer() == null) {
            badRequest("Unsuported 'Content-Type'.");
            return;
        }

        Employee newEmployee = inputSerializer().deserialize(in, Employee.class);
        int id = storage.add(newEmployee, from);

        he.sendResponseHeaders(201, 0);
        sendResponse(newEmployee);
    }

    public void update() throws Exception {
        if (inputSerializer() == null) {
            badRequest("Unsuported 'Content-Type'.");
            return;
        }

        int id = Integer.parseInt(params.get("id"));

        try {
            Employee e = inputSerializer().deserialize(in, Employee.class);
            storage.update(id, e);
            he.sendResponseHeaders(200, 0);
            sendResponse(e);
        } catch (IndexOutOfBoundsException ex) {
            recordNotFound();
        }
    }

    public void getUpdates() throws Exception {
        String from = headers.getFirst("From");
        String modSince = headers.getFirst("If-Modified-Since");

        if (from == null) {
            preconditionFailed("Missing 'From' header.");
            return;
        }

        if (modSince == null) {
            preconditionFailed("Missing 'If-Modified-Since' header.");
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        Date modSinceDate;
        try {
            modSinceDate = dateFormat.parse(modSince);
        } catch (ParseException ex) {
            badRequest("Could not parse 'If-Modified-Since' header.");
            return;
        }

        List<Employee> list = storage.getChangesSince(modSinceDate, from);

        if (list.isEmpty()) {
            notModified();
            return;
        }

        he.sendResponseHeaders(200, 0);
        sendResponse(new EmployeeListWrapper(list));
    }

    protected void badRequest(String msg) throws IOException {
        he.sendResponseHeaders(400, msg.getBytes().length);
        out.write(msg.getBytes());
        he.close();
    }

    protected void preconditionFailed(String msg) throws IOException {
        he.sendResponseHeaders(402, msg.getBytes().length);
        out.write(msg.getBytes());
        he.close();
    }

    protected void recordNotFound() throws IOException {
        String msg = "Record not found.";
        he.sendResponseHeaders(404, msg.getBytes().length);
        out.write(msg.getBytes());
        he.close();
    }

    protected void notModified() throws IOException {
        he.sendResponseHeaders(304, -1);
        he.close();
    }

    protected Map<String, String> parseQuery(String query) {
        HashMap<String, String> params = new HashMap<String, String>();
        if (query == null) { return params; }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int i = pair.indexOf("=");
            if (i == -1) { continue; }
            params.put(pair.substring(0,i), pair.substring(i+1));
        }
        return params;
    }

    protected Serializer inputSerializer() {
        return SERIALIZERS.get(headers.getFirst("Content-Type"));
    }

    protected Serializer outputSerializer() {
        return SERIALIZERS.getOrDefault(headers.getFirst("Accept"), SERIALIZERS.get(null));
    }

    protected <T> void sendResponse(T obj) throws Exception {
        outputSerializer().serialize(obj, out);
        he.close();
    }

    protected HttpExchange he;
    protected Map<String, String> params;
    protected Headers headers;
    protected InputStream in;
    protected OutputStream out;
    protected DataStorage<Employee> storage;

    protected static final Map<String, Serializer> SERIALIZERS;

    static {
        HashMap<String, Serializer> map = new HashMap<String, Serializer>();

        map.put("application/xml", new XmlSerializer());
        map.put("application/json", new JsonSerializer());
        map.put(null, map.get("application/json"));

        SERIALIZERS = Collections.unmodifiableMap(map);
    }
}
