package com.importexpress.user.util;

import com.importexpress.user.pojo.UserBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class WebCookie {
    private static final Logger LOG = LogManager.getLogger(WebCookie.class);

    public static String cookie(HttpServletRequest request, String cookiev) {
        Cookie[] cookie = request.getCookies();
        if (cookie != null) {
            for (Cookie cookie2 : cookie) {
                if (cookie2.getName().equals(cookiev)) {
                    try {
                        return URLDecoder.decode(cookie2.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        LOG.error("WebCookie get user ,cookie---,encode error===" + e + "-----" + cookie2.getValue());
                    }
                }
            }
        }
        return null;
    }

    public static String cookieValue(HttpServletRequest request, String cookiev) {
        Cookie[] cookie = request.getCookies();
        if (cookie != null) {
            for (Cookie cookie2 : cookie) {
                // LOG.warn("cookie:"+cookie2.getName()+","+cookie2.getValue());
                if (cookie2.getName().equals(cookiev)) {
                    try {
                        return URLDecoder.decode(cookie2.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        LOG.error("WebCookie get user ,cookieValue----,encode error===" + e + "-----" + cookie2.getValue());
                    }
                }
            }
        }
        return null;
    }

    /**
     * 根据名字获取cookie
     *
     * @param request
     * @param name    cookie名字
     * @return
     */
    public static Cookie getCookieByName(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = ReadCookieMap(request);
        if (cookieMap.containsKey(name)) {
            Cookie cookie = cookieMap.get(name);
            if ("userCookieId".equalsIgnoreCase(name) && cookie.getValue().indexOf("=") > -1) {
                cookie.setValue(cookie.getValue().split("=")[0]);
            }
            return cookie;
        } else {
            return null;
        }
    }

    /**
     *  * 将cookie封装到Map里面  * @param request  * @return  
     */
    private static Map<String, Cookie> ReadCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

//	public static void main(String[] args) {
//		try {
//			System.out.println(URLDecoder.decode("16290%3Dtest%40001.com%3Dtest%40001.com%3DUSD%3D2%3DE5172665E83E797ED3B691D063497857", "UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

    /**
     * 判断用户是否登录
     */
    public static String[] getUser(HttpServletRequest request) {

        if (LoginHelp.isLogin(request)) {
            UserBean userBean = LoginHelp.getUserBean(request);
            String[] userinfo = {userBean.getId() + "", userBean.getEmail(), userBean.getEmail(), userBean.getCurrency(),
                    userBean.getUserCategory(), userBean.getPass()};
            return userinfo;
        } else {
            return null;
        }

//		HttpSession session = request.getSession(true);
//		UserBean user = (UserBean) session.getAttribute("userInfo");
//		String exist = "";
//		try {
//			if (user == null) {
//				exist = WebCookie.cookie(request, "userInfo");
//				if (exist != null) {
//					//exist = URLDecoder.decode(exist, "UTF-8");
//					String[] userInfo = exist.split("=");
//					String userCategory = "";
//					if (userInfo.length > 4) {
//						userCategory = userInfo[4];
//					} else {
//						userCategory = "0";
//					}
//					if(userInfo.length > 3){
//						String[] userinfo = { userInfo[0], userInfo[1], userInfo[2], userInfo[3], userCategory };
//						return userinfo;
//					}else{
//						return null;
//					}
//				}
//				exist = WebCookie.cookie(request, "pass_req");
//				if (exist != null) {
//					String username = WebCookie.cookie(request, "userName");
//					String email = WebCookie.cookie(request, "email");
//					String currency = WebCookie.cookie(request, "currency");
//					String userid = exist.split("@")[1];
//					String userCategory = WebCookie.cookie(request, "userCategory");
//					String pass = WebCookie.cookie(request, "pass_req");
//					String[] userinfo = { userid, username, email, currency, userCategory, pass };
//					return userinfo;
//				}
//			} else {
//				String[] userinfo = { user.getId() + "", user.getEmail(), user.getEmail(), user.getCurrency(),
//						user.getUserCategory(), user.getPass() };
//				return userinfo;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			LOG.error("webcookie:"+e.toString()+",userinfo:"+exist);
//		}
//		return null;
    }

    /**
     * 判断客户是否填写了反馈信息
     *
     * @param request
     * @return
     */
    public static String getIsFeedback(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object isFeedback = session.getAttribute("isFeedback");
        if (isFeedback == null || "".equals(isFeedback)) {
            isFeedback = "N";
        }
        return isFeedback.toString();
    }

    /**
     * 搜索页查询次数统计
     *
     * @param request
     * @return
     */
    public static int getSearchCount(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object searchCount = session.getAttribute("searchCount");
        int count = 0;
        if (searchCount != null && !"".equals(searchCount)) {
            count = Integer.valueOf(searchCount.toString());
        }
        return count;
    }

    /**
     * 添加cookie
     *
     * @param response 响应对象
     * @param name     Cookie的key
     * @param value    Cookie的value
     * @param maxAge   0：立即删除；负数：当浏览器关闭时自动删除；其他：存活时间
     * @return Cookie 返回类型
     * @throws
     * @author longwu
     * @date 2016-9-12 上午10:05:12
     */
    public static Cookie addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        value = value.replaceAll("\\s*", "");
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
        return cookie;
    }

    /**
     * @param response
     * @param name
     * @return Cookie
     * @Title addCookie
     * @Description 删除cookie
     */
    public static Cookie deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "sss");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return cookie;
    }
}
