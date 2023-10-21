package org.itheima.reggie.dto;


import lombok.Data;
import org.itheima.reggie.entity.Dish;
import org.itheima.reggie.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDTO extends Dish {
    //菜品对应的口味数据
    private List<DishFlavor> flavors =new ArrayList<>();

    private String CategoryName;

    private Integer copies;

}
