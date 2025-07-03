package com.yundin.service;

import com.yundin.dto.EmployeeLoginDTO;
import com.yundin.entity.Employee;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

}
