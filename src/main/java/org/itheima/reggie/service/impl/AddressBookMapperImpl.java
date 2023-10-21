package org.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.itheima.reggie.entity.AddressBook;

import org.itheima.reggie.mapper.AddressBookMapper;

import org.itheima.reggie.service.AddressBookService;

import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AddressBookMapperImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {


}
