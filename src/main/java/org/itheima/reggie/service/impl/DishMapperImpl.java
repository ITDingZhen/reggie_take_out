package org.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.itheima.reggie.dto.DishDTO;
import org.itheima.reggie.entity.Category;
import org.itheima.reggie.entity.Dish;
import org.itheima.reggie.entity.DishFlavor;
import org.itheima.reggie.mapper.CategoryMapper;
import org.itheima.reggie.mapper.DishMapper;
import org.itheima.reggie.service.CategoryService;
import org.itheima.reggie.service.DishFlavorService;
import org.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishMapperImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;



    /**
     * 新增菜品，同时保存对应的口味数据
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        //保存菜品基本信息到菜品表dish
        this.save(dishDTO);
        Long dishId = dishDTO.getId();


        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors=flavors.stream().map((item)->{
                    item.setDishId(dishId);
                    return item;
                }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }


    /**
     *跟据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDTO getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表来查询
        Dish dish = this.getById(id);

        DishDTO dishDTO = new DishDTO();
        BeanUtils.copyProperties(dish,dishDTO);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish!=null,DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        dishDTO.setFlavors(list);

        return dishDTO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //更新dish表基本信息
        this.updateById(dishDTO);
        //清理当前菜品的口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDTO.getId());

        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据---flavor表的insert操作
        List<DishFlavor> flavors = dishDTO.getFlavors();

        flavors =flavors.stream().map((item)->{
            item.setDishId(dishDTO.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }
    @Transactional
    @Override
    public void deleteWithFlavor(List<Long> ids){
        for (long id:ids){
            LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId,id);
            dishFlavorService.remove(queryWrapper);
        }
        this.removeByIds(ids);
    }


}
