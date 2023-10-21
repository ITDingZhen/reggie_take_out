package org.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.itheima.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
