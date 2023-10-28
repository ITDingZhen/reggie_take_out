package org.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.itheima.reggie.common.R;
import org.itheima.reggie.dto.SetmealDTO;
import org.itheima.reggie.entity.Category;
import org.itheima.reggie.entity.Setmeal;
import org.itheima.reggie.service.CategoryService;
import org.itheima.reggie.service.SetmealDishService;
import org.itheima.reggie.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
@Api(tags = "套餐相关接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    @ApiOperation("新增套餐接口")
    public R<String> save(@RequestBody SetmealDTO setmealDTO){
        log.warn("套餐信息：{}",setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return R.success("测试");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",required = true),
            @ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true),
            @ApiImplicitParam(name = "name",value = "套餐名称",required = false)
            //required = true 表示这个参数是必须的
    })
    public  R<Page> page(int page, int pageSize, String name){
        //分页构造器
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        Page<SetmealDTO> DTOPage=new Page<>();
        //构造查询条件
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);
        BeanUtils.copyProperties(pageInfo,DTOPage,"records");

        List<Setmeal> records=pageInfo.getRecords();
        List<SetmealDTO> list=records.stream().map((item)->{
            SetmealDTO setmealDTO =new SetmealDTO();

            BeanUtils.copyProperties(item,setmealDTO);

            Long categoryId = item.getCategoryId();//分类id
            //跟据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category!=null){
                String categoryName = category.getName();
                setmealDTO.setCategoryName(categoryName);
            }

            return setmealDTO;
        }).collect(Collectors.toList());
        log.warn(list.toString());
        DTOPage.setRecords(list);
        return R.success(DTOPage);
    }

    /**
     * 删除套餐接口
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    @ApiOperation("删除套餐接口")
    public R<String> delete(@RequestParam  List<Long> ids){
        log.warn("ids ->"+ ids);
        setmealService.deleteWithDish(ids);
        return R.success("套餐数据删除成功");
    }

    /**
     * 修改套餐状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping ("/status/{status}")
    @ApiOperation("修改套餐状态接口")
    public R<String> updateStatus(@PathVariable int status,long[] ids){

        Setmeal setmeal =new Setmeal();
        List<Setmeal> setmealList=new ArrayList<>();

        for (long id : ids) {
            LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Setmeal::getId, id);
            setmeal = setmealService.getOne(queryWrapper);
            setmeal.setStatus(status);
            setmealList.add(setmeal);
            log.warn(setmeal.toString());
        }
        setmealService.updateBatchById(setmealList);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
    @ApiOperation("根据条件查询套餐数据接口")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);
    }

//    /**
//     * 根据条件查询套餐数据
//     * @param setmeal
//     * @return
//     */
//    @GetMapping("/list")
//    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
//    public R<List<Setmeal>> list(Setmeal setmeal){
//        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
//        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
//        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
//        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
//
//        List<Setmeal> setmealList = setmealService.list(queryWrapper);
//        return R.success(setmealList);
//    }



    //todo 未完成
    @GetMapping ("/{id}")
    @ApiOperation("修改套餐接口(未完成)")
    public R<SetmealDTO> update(@PathVariable long id) {
        
        Long categoryId = setmealService.getById(id).getCategoryId();
        String name = categoryService.getById(categoryId).getName();

        SetmealDTO byIdWithDish = setmealService.getByIdWithDish(id, name);

        log.warn(byIdWithDish.toString());
        return R.success(byIdWithDish);
    }


}
