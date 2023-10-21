package org.itheima.reggie.dto;

import lombok.Data;
import org.itheima.reggie.entity.Setmeal;
import org.itheima.reggie.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDTO extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
