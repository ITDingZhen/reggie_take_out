package org.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/*
* 分类实体
* */
@Data
public class Category {
    private static final long serialVersionUID = 1L;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    //todo 加了这个注解就不会丢失精度
    private long id;
    //类型 1.菜品分类 2.套餐分类
    private Integer type;
    private String name;
    private Integer sort;

    @TableField(fill = FieldFill.INSERT)//插入时填充字段
    private LocalDateTime createTime;
    @TableField(fill=FieldFill.INSERT_UPDATE)//插入和更新时填充字段
    private LocalDateTime updateTime;

    @TableField(fill=FieldFill.INSERT)//插入时填充字段
    private long createUser;
    @TableField(fill=FieldFill.INSERT_UPDATE)//插入和更新时填充字段
    private long updateUser;
}
