package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
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
        //这里的getBytes是因为MD5等是数学上处理二进制数据的，计算机只认二进制。不认我们人看的字符串
        //所以要求传入的参数必须是byte[]类型,即字节数组类型
        password=DigestUtils.md5DigestAsHex(password.getBytes());
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

    public void save(EmployeeDTO employeeDTO) {
        Employee employee=new Employee();
        //这里从前面的DTO拿属性过来， 然后存入POJO中
        //DTO是前端传入数据封装的实体，
        //这里本来应该要一个个set的，但是DTO和POJO中的很多属性是一致的，可以用copy来直接赋值
        //前提是属性名字要一致
        BeanUtils.copyProperties(employeeDTO,employee);//前后顺序不能变
        employee.setStatus(StatusConstant.ENABLE);
        //对密码进行MD5加密，哪怕是默认的
        //不要直接DEFAULT_PASSWORD,这样会导致import导入一个类进去，有点乱，这样加个类前缀. 就不会
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
         //设置当前记录创建人ID
        //从LocalThread中提取线程中的ID，这个ID是JwtToken设置的ID。看源码
        //每一次执行程序都会创建一个单独的线程ID
        employee.setCreateUser(BaseContext.getCurrentId());
        employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.insert(employee);
    }

    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //开始分页查询
        //用PageHelper工具
        //基于LocalThread来本地的传入page和pageSize
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        //这是PageHelper插件的固定写法，得到了一个page对象，就可以对页数和长度进行定义
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
        List<Employee> records = page.getResult();
        //把总页数和当前数据量total和records封装进PageResult
        //因为接口规范要求传入PageResult类型
        PageResult pageResult=new PageResult(total,records);
        return pageResult;

    }


}
