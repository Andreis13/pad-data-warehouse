
package faf.pad4.common;

import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.IOException;
import javax.xml.bind.annotation.*;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

/**
 *
 * @author andrew
 */
@XmlRootElement
public class Employee implements Serializable, Identifiable {
    public Employee() {
        this.firstName = "";
        this.lastName = "";
        this.department = "";
        this.salary = 0.0;
    }
    public Employee(String fn, String ln, String dep, double s) {
        this.firstName = fn;
        this.lastName = ln;
        this.department = dep;
        this.salary = s;
    }

    public Integer getID() {
        return id;
    }

    public void setID(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (id != null) {
            s.append(id);
            s.append(": ");
        }

        s.append(String.format("%-10s ", firstName));
        s.append(String.format("%-10s @ ", lastName));
        s.append(department);
        s.append(" -> ");
        s.append(salary);
        return s.toString();
    }

    private Integer id;
    @XmlElement
    public String firstName;
    @XmlElement
    public String lastName;
    @XmlElement
    public String department;
    @XmlElement
    public double salary;
}
