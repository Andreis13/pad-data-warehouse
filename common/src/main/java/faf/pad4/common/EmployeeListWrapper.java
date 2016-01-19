
package faf.pad4.common;

import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.IOException;
import javax.xml.bind.annotation.*;

/**
 *
 * @author andrew
 */
@XmlRootElement(name="employees")
public class EmployeeListWrapper {
    public EmployeeListWrapper() {
    }

    public EmployeeListWrapper(List<Employee> employees) {
        this.employees = employees;
    }

    @XmlElement(name="employee")
    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    private List<Employee> employees;
}
