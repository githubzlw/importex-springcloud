package com.importexpress.user.util;

import com.importexpress.user.pojo.UserBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;

/**
 * login工具类errage
 *
 * @author luohao
 * @date 2019/3/19
 */
@Slf4j
public class LoginHelp {


    private final static String IS_LOGIN = "IS_LOGIN";

    private final static String USER_INFO = "userInfo";
    private final static String COUNTRY_ID = "countryid";
    private final static String SESSION_USER_NAME = "sessionName";
    public final static String COOKIEID_USER_ID = "userCookieId";
    //step v1. @author: cjc @date：2019/4/19 10:19:27   Description : 需要在登陆页面记录用户名密码,记录一个新的避免对其他产生干扰
    private final static String USERNAME_USERPASS = "NAME_PASS";
    public static final String FIRSTADDCAR = "FIRSTADDCAR";

    private LoginHelp() {
    }

    /**
     * 获取客户信息
     * 注意：游客的时候，返回null
     *
     * @param request
     * @return
     */
    public static UserBean getUserBean(HttpServletRequest request) {

        if (isLogin(request)) {
            UserBean userBean = (UserBean) request.getSession().getAttribute(USER_INFO);
            Assert.notNull(userBean);
            return userBean;
        } else {
            log.warn("The user is not login status(maybe old login status)");
            //return null;
            //Added <V1.0.1> Start： cjc 2019/11/11 4:00 下午 Description : 无用户信息则去生成新的用户信息

            //End：
            return null;
        }
    }

    /**
     * 判断顾客是否已经登陆
     *
     * @param request
     * @return
     */
    public static boolean isLogin(HttpServletRequest request) {
        Assert.notNull(request);
        Boolean isLogin = (Boolean) request.getSession().getAttribute(IS_LOGIN);
        return isLogin == null ? false : isLogin;
    }
}

