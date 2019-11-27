package com.importexpress.user.util;

import com.importexpress.user.pojo.UserBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@Service
public class UserWebUtil {
	/**
	 * @Title: getUserInfo
	 * @Author: cjc
	 * @Despricetion:Description : 获取用户信息主要是获取正式用户或者是游客：用户id ,国家
	 * @Date: 2019/3/8 9:22:58
	 * @Param: [request, response]
	 * @Return: com.importExpress.pojo.UserBean
	 */
	public UserBean getUserInfo(HttpServletRequest request, HttpServletResponse response){
		UserBean userBean = LoginHelp.getUserBean(request);
		if(userBean==null){
			log.info("userBean.toString(),null");
			UserBean blankUserBean = new UserBean();
			blankUserBean.setId(0);
			blankUserBean.setCurrency("USD");

//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//设置日期格式
//            String qId = (request.getSession().getId()).hashCode() + "_" + df.format(System.currentTimeMillis());
//            qId = qId.replaceAll(":|\\s*", "");
//            String userCookieIdAndCountyrIdStr =  qId+"="+36;
//            WebCookie.addCookie(response, "userCookieId", userCookieIdAndCountyrIdStr, 31536000);
            String[] userCookieIdFromCookie = LoginHelp.getUserCookieIdFromCookie(request, response);
            if(userCookieIdFromCookie!=null){
                //非首次进入
                blankUserBean.setSessionId(userCookieIdFromCookie[0]);
				int countryId = 36;
				try{
					countryId = Integer.parseInt(userCookieIdFromCookie[1]);
				}catch(Exception e){
					countryId = 36;
					log.error("get countryId from getUserCookieIdFromCookie ",e);
				}
                blankUserBean.setCountryId(countryId);
				// blankUserBean.setCountryName(Utility.getCountryEnNameById(countryId));
				request.getSession().setAttribute(LoginHelp.COOKIEID_USER_ID,userCookieIdFromCookie[0]);
            }else{
                //首次进入
                String qId = UUID.randomUUID().toString();
                int countryId = LoginHelp.getCountryId(request, response);
                LoginHelp.saveUserCookieIdToCookie(request,response,qId+"="+countryId);

                blankUserBean.setSessionId(qId);
                blankUserBean.setCountryId(countryId);
				// blankUserBean.setCountryName(Utility.getCountryEnNameById(countryId));
				request.getSession().setAttribute(LoginHelp.COOKIEID_USER_ID,qId);
            }
			String currency = (String)request.getSession().getAttribute("curency");
			blankUserBean.setCurrency(StringUtils.isBlank(currency) ? "USD" : currency);
			return blankUserBean;
		}else{
			log.info("userBean.toString(),[{}]",userBean.toString());
			// userBean.setCountryName(Utility.getCountryEnNameById(userBean.getCountryId()));
			return userBean;
		}
	}
}
