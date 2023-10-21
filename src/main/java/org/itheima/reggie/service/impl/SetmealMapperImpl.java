package org.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.itheima.reggie.common.CustomException;
import org.itheima.reggie.dto.SetmealDTO;
import org.itheima.reggie.entity.Category;
import org.itheima.reggie.entity.Setmeal;
import org.itheima.reggie.entity.SetmealDish;
import org.itheima.reggie.mapper.SetmealMapper;
import org.itheima.reggie.service.CategoryService;
import org.itheima.reggie.service.SetmealDishService;
import org.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealMapperImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /*@Autowired
    private CategoryService categoryService;*/

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDTO setmealDTO) {
        //保存套餐的基本信息,操作setmeal表，执行insert操作
        this.save(setmealDTO);
        //保存套餐和菜品的关联信息,操作setmeal_dish表，执行insert操作
        //todo 自己改的
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDTO.getId());
        }
        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    @Transactional
    public void deleteWithDish(List<Long> ids){
        //todo 自己写的
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,1);
        log.error("要删除的套餐有启用中的");
        this.count(queryWrapper);
        if (this.count(queryWrapper) > 0) {
            //如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //如果可以删除.先删除套餐表中的数据——setmeal
        this.removeByIds(ids);
        //删除关系表中的数据——setmeal_dish
        LambdaQueryWrapper<SetmealDish> queryWrapper1=new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(queryWrapper1);
    }

    @Override
    public SetmealDTO getByIdWithDish(long id,String cName) {
        Setmeal setmeal = this.getById(id);
        SetmealDTO setmealDTO=new SetmealDTO();

        BeanUtils.copyProperties(setmeal,setmealDTO);

        LambdaQueryWrapper<SetmealDish> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal!=null,SetmealDish::getSetmealId,id);

        List<SetmealDish> setmealDishList=setmealDishService.list(queryWrapper);

        setmealDTO.setSetmealDishes(setmealDishList);
        setmealDTO.setCategoryName(cName);
        /*setmealDTO.setCategoryName(category.getName());*/

        return setmealDTO;
    }

}
