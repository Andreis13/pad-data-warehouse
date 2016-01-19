
package faf.pad4.datasource;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.lang.Exception;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ListIterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.io.IOException;
import faf.pad4.common.*;

/**
 *
 * @author andrew
 */
public class DataSource {
    public static void main(String[] args) throws Exception {
        new DataSource(args).run();
    }

    public DataSource(String[] args) throws Exception {
        this.hostName = args[0];
        this.dataFilePath = args[1];
        this.warehouseHostName = args[2];
        this.pollPeriod = 10000;
        this.employees = new HashMap<Integer, Employee>();

        if (args[3].equals("json")) {
            this.mimeType = "application/json";
            this.serializer = new JsonSerializer();
        } else if (args[3].equals("xml")) {
            this.mimeType = "application/xml";
            this.serializer = new XmlSerializer();
        } else {
            throw new Exception("Wrong format.");
        }
    }

    public void run() throws Exception {
        loadData();
        pollUpdates();
    }

    protected void loadData() throws Exception {
        InputStream fileIn = new FileInputStream(new File(dataFilePath));

        EmployeeListWrapper list = new JsonSerializer().deserialize(fileIn, EmployeeListWrapper.class);

        URL url = new URL("http://" + warehouseHostName + "/employee/");

        Iterator<Employee> it = list.getEmployees().listIterator();
        while (it.hasNext()) {
            Employee e = it.next();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", mimeType);
            conn.setRequestProperty("Accept", mimeType);
            conn.setRequestProperty("From", hostName);
            conn.setDoOutput(true);
            OutputStream out = conn.getOutputStream();
            serializer.serialize(e, out);

            InputStream in = conn.getInputStream();
            e = serializer.deserialize(in, Employee.class);
            employees.put(e.getID(), e);

            conn.disconnect();
        }
    }

    protected void pollUpdates() throws Exception {
        URL url = new URL("http://" + warehouseHostName + "/update/employees/");

        while (true) {
            Date t = new Date();
            Thread.sleep(pollPeriod);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", mimeType);
            conn.setRequestProperty("From", hostName);
            conn.setIfModifiedSince(t.getTime());

            if (conn.getResponseCode() == 304) {
                conn.disconnect();
                continue;
            }

            InputStream in = conn.getInputStream();
            EmployeeListWrapper list = serializer.deserialize(in, EmployeeListWrapper.class);

            list.getEmployees().forEach((e) -> {
                employees.put(e.getID(), e);
            });

            System.out.printf("Updated %d records.\n", list.getEmployees().size());

            conn.disconnect();
        }
    }

    protected String dataFilePath;
    protected String hostName;
    protected String warehouseHostName;
    protected String mimeType;
    protected int pollPeriod;
    protected Map<Integer, Employee> employees;
    protected Serializer serializer;
}
