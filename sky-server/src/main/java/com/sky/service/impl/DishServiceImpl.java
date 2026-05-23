package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetMealDishMapper setMealDishMapper;

    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        //向菜品表插入一条数据

        //新增菜品只需要加菜的数据,dishDTO包含了flavors,不需要
        //所以新建了一个POJO,将dishDTO中的数据拷贝到dish中
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);
        //这不能直接获得ID，因为根据接口文档，ID是非必须的，所以前端传不过来
        //要与SQL语句配合
        Long dishId=dish.getId();


        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors!=null && flavors.size()>0){
            //向口味表插入n条数据
            flavors.forEach(dishFlavor->{
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult dishPageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        //这里不能用Dish，根据接口文档，返回的VO属性喝Dish不同
        //应该再写一个DishVO
        Page<DishVO> page=dishMapper.pageQuery(dishPageQueryDTO);
        long total = page.getTotal();
        List<DishVO> records=page.getResult();
        PageResult pageResult=new PageResult(total,records);
        return pageResult;
    }

    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品能不能删除--是否存在已售中的菜品？
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus()== StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);//不允许删除异常
            }

        }
        //判断当前菜品能不能删除--是否被套餐关联？
        List<Long> setMealIds=setMealDishMapper.getSetMealDishIdsByDishId(ids);
        if(setMealIds!=null && setMealIds.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //for循环SQL，而且有两条性能太低
        //删除菜品表中的菜品数据
//        for (Long id : ids) {
//            dishMapper.deleteByIds(id);
//            //删除菜品关联中的口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        }

        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }

    public DishVO getById(Long id) {
        Dish dish=dishMapper.getById(id);
        List<DishFlavor> dishFlavor= dishFlavorMapper.getByDishId(id);
        DishVO dishVO=new DishVO();
        //这里source为null是因为，例如：id为52的口味数据，看数据库，id为52的口味数据对应了两条口味列表
        //必须要用List列表来接收，如果只是写一个单独的单一对象，mybatis会无法将两条数据插入一个单一对象中，从而导致为null
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavor);

        return dishVO;
    }


    public void updateWithFlavor(DishDTO dishDTO) {
        //修改基础信息
        //因为DishDTO里有flavors属性，这里修改基础信息先不动口味
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);

        //从技术层面来说，先把菜品的口味数据删除，再重新插入会更好
        //这样就不要额外写删除SQL了，
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        //插入新的口味数据
        List<DishFlavor> flavor=dishDTO.getFlavors();
        if(flavor!=null && flavor.size()>0){
            flavor.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavor);
        }
    }

    public List<DishVO> getDishByCategoryId(Dish dish) {
        //用懒狗写法的话，xml文件写的时候可以灵活的省略未赋值（为null）的值，这样就可以做到筛选，只Select出对应categoryId的菜品，然后存入List
        List<Dish> dishList=dishMapper.getDishByCategoryId(dish);//像这种用列表来接收的不需要new ArrayList
        List<DishVO> dishVOList=new ArrayList<>();//而这种新建一个列表并且在此界面进行add等操作的需要new，对象数据必须要ArrayList

        for(int i=0;i<dishList.size();i++){
            DishVO dishVO=new DishVO();
            BeanUtils.copyProperties(dishList.get(i),dishVO);
            //根据菜品id查询对应的口味
            List<DishFlavor> dishFlavorList=dishFlavorMapper.getByDishId(dishList.get(i).getId());
            dishVO.setFlavors(dishFlavorList);
            dishVOList.add(dishVO);
        }
        return dishVOList;
    }

    public void startOrStop(Integer status, Long id) {
        dishMapper.startOrStop(status,id);
    }

}
