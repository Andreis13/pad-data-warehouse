
package faf.pad4.warehouse;

import faf.pad4.common.Employee;

/**
 *
 * @author andrew
 */
public class EmployeeStorage extends DataStorage<Employee> {

    private EmployeeStorage() {
        super();
    }

    public static synchronized EmployeeStorage getInstance() {
        if (instance == null) {
            instance = new EmployeeStorage();
        }
        return instance;
    }

    private static EmployeeStorage instance;
}
