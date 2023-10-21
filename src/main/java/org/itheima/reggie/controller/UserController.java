package org.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.itheima.reggie.common.R;
import org.itheima.reggie.entity.User;
import org.itheima.reggie.service.UserService;
import org.itheima.reggie.utils.SMSUtils;
import org.itheima.reggie.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession httpSession){
        //1.获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
            //2.获取四位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.warn("code={}",code);
            //3.调用阿里云提供的短信服务完成发送短信
            //SMSUtils.sendMessage("cn-hangzhou","",phone,code);
            //4.需要将生成的验证码保存到Session
            httpSession.setAttribute(phone,code);
            return R.success("手机验证码发送成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @param httpSession
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession httpSession){
        log.warn("map = {}",map);

        //获取手机号
        String phone = map.get("phone").toString();
        //获取保存的验证码
        String code = map.get("code").toString();
        //从Session中获得验证码
        Object codeInSession = httpSession.getAttribute(phone);
        //进行验证码比对
        if(codeInSession != null && codeInSession.equals(code)){
            //如果比对成功，说明登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user == null){
                //判断当前手机号是否为新用户,如果是新用户，自动完成注册
                user=new User();
                user.setPhone(phone);
                userService.save(user);
            }
            httpSession.setAttribute("user",user.getId());
            return R.success(user);
        }

        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request){
        //清理session中保存的员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");

    }



}
