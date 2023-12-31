package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.result.Result;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * @param employeeDTO
     */
    Result save(EmployeeDTO employeeDTO);

    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * @return
     */
    PageResult queryEmployee(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     * @return
     */
    Result settingStatus(Integer status, Long id);

    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    Result getEmployee(Long id);

    /**
     * 编辑员工
     * @param employeeDTO
     * @return
     */
    Result updateEmployee(EmployeeDTO employeeDTO);
}
