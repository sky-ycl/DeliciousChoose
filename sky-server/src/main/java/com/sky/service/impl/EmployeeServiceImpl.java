package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.MyThreadLocal;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {


    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        // 判断员工的账号状态 0表示禁用 1表示启用

        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 对前端传过来的密码进行md5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    @Override
    public Result save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee = employee.builder()
                //设置账号的状态 默认是正常的 1是正常的  0是锁定的
                .status(StatusConstant.ENABLE)
                // 设置用户密码(密码进行加密)
                .password(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                //设置当前记录创建人id和修改人id
                .createUser(MyThreadLocal.getCurrentId())
                .updateUser(MyThreadLocal.getCurrentId()).build();
        // 对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        int count = employeeMapper.insert(employee);
        if (count > 0) {
            return Result.success();
        }
        return Result.error("新增失败");
    }

    @Override
    public PageResult queryEmployee(EmployeePageQueryDTO employeePageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
        List<Employee> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
     * 启用和禁用员工账号
     *
     * @param status
     * @param id
     * @return
     */
    @Override
    public Result settingStatus(Integer status, Long id) {
        Employee employee = Employee.builder()
                .status(status)
                .id(id).build();
        int count = employeeMapper.update(employee);
        if(count>0){
            return Result.success("修改成功");
        }
        return Result.error("失败");
    }

    /**
     * 根据id查询用户信息
     * @param id
     * @return
     */
    @Override
    public Result getEmployee(Long id) {
        Employee employee = employeeMapper.selectUserById(id);
        employee.setPassword("********");
        return Result.success(employee);
    }

    /**
     * 编辑员工
     * @param employeeDTO
     * @return
     */
    @Override
    public Result updateEmployee(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO,employee);

        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(MyThreadLocal.getCurrentId());

        int count = employeeMapper.update(employee);
        if(count<1){
            return Result.error("修改失败");
        }
        return Result.success("修改成功");
    }
}
