package org.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.itheima.reggie.common.R;
import org.itheima.reggie.dto.DishDTO;
import org.itheima.reggie.entity.Category;
import org.itheima.reggie.entity.Dish;
import org.itheima.reggie.entity.DishFlavor;
import org.itheima.reggie.service.CategoryService;
import org.itheima.reggie.service.DishFlavorService;
import org.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDTO dishDTO){
        log.info(dishDTO.toString());

        dishService.saveWithFlavor(dishDTO);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器对象
        Page<Dish> dishInfo = new Page<>(page,pageSize);
        Page<DishDTO> dishDTOPage=new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(dishInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(dishInfo,dishDTOPage,"records");

        List<Dish> records = dishInfo.getRecords();
        List<DishDTO> list=records.stream().map((item)->{
            DishDTO dishDTO =new DishDTO();

            BeanUtils.copyProperties(item,dishDTO);

            Long categoryId = item.getCategoryId();//分类id
            //跟据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category!=null){
                String categoryName = category.getName();
                dishDTO.setCategoryName(categoryName);
            }

            return dishDTO;
        }).collect(Collectors.toList());

        dishDTOPage.setRecords(list);

        return R.success(dishDTOPage);
    }

    /**
     * 跟据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDTO> get(@PathVariable Long id){
        DishDTO byIdWithFlavor = dishService.getByIdWithFlavor(id);

        return R.success(byIdWithFlavor);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @Transactional
    public R<String> update(@RequestBody DishDTO dishDTO){
        LambdaQueryWrapper<DishDTO> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishDTO::getId,dishDTO.getId());
        dishService.updateWithFlavor(dishDTO);
        return R.success("修改菜品成功");
    }

    /**
     * 跟据条件来查询对应的菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDTO>> list(Dish dish){

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        //查询状态为起售状态的菜品
        queryWrapper.eq(Dish::getStatus,1);
        List<Dish> dishList = dishService.list(queryWrapper);
        List<DishDTO>dishDTOList =dishList.stream().map((item)->{
            DishDTO dishDTO =new DishDTO();

            BeanUtils.copyProperties(item,dishDTO);

            Long categoryId = item.getCategoryId();//分类id
            //跟据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category!=null){
                String categoryName = category.getName();
                dishDTO.setCategoryName(categoryName);
            }
            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1=new LambdaQueryWrapper();
            queryWrapper1.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper1);
            dishDTO.setFlavors(dishFlavors);

            return dishDTO;
        }).collect(Collectors.toList());


        return R.success(dishDTOList);
    }

    /* todo -----------------------------------------------------自己写的--------------------------------------------------------*/


    /**
     * 菜品的批量删除，同时删除相连的dish_flavor
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.deleteWithFlavor(ids);
        return R.success("测试");
    }
    /**
     * 菜品的批量启用/停用
     * @param status
     * @param ids
     * @return
     */
    @PostMapping ("/status/{status}")
    @Transactional
    public R<String> update(@PathVariable int status,long[] ids){

        Dish dish =new Dish();//拿了dish来接收
        List<Dish> dishList =new ArrayList<>();

        for (long id : ids) {
            LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Dish::getId, id);
            dish = dishService.getOne(queryWrapper);
            dish.setStatus(status);
            dishList.add(dish);
            log.warn(dish.toString());
        }
        dishService.updateBatchById(dishList);
        
       return R.success("测试");
    }
}
