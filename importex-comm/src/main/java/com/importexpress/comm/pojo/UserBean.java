package com.importexpress.comm.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserBean implements Serializable {

	private static final long serialVersionUID = -358458436351860319L;
	
	private int id;
	/**
	 * userCookieId 我司唯一用户标识
	 */
	private String sessionId;
	private String email;
	private String name;
	private String pass;
	private String token;
	private String sequence;
	//激活码
	private String activationCode;
	//����״激活装填
	private int activationState;
	//头像
	private String picture;
	private Date createtime;
	private Date activationTime;
	private Date activationPassTime;
	private String activationPassCode;
	private String currency;//货币类型
	private int logReg;//登录还是注册
	private String count;//购物车商品数
	private double applicable_credit;
	private double availableM;//用户余额
	private String businessName;
	private int countryId;
	private String countryName;
	//用户标识  1表示该用户为商家   2 表示为个人  0 默认 ，4 新的个人标识：2018年4月20日
	private String userCategory;
	private String area;
	private int isTogether;//登录是否存在合并购物车
	//api key
	private String signkey;

	/**
	 * login 错误信息
	 */
	private String errorMessage;

	/**
	 * 跳转的页面
	 */
	private String jumpUrl;
	/**
	 * 用户记录信息
	 */
	private String info;
	private String dropshipping1;
	private String proPurl;
	private String purl;
	private String uid;
	private String gbid;
	private String pre;
	private String rfq;
	private String custype;
	private String bfm;
	private String searchUrl;
	private String redirect;
	private String pc_flag;
	private String bind;
	private String type;
	private String google_email;
	/**
	 * 标识 如果是购物车页面跳转到登陆页面则显示contione to check out
	 */
	private int mark;

	/**
	 * 0：老用户默认0，1:快速注册未激活，2：快速注册激活
	 */
	private int isfastregistered;
	/**
	 * 商业信息
	 */
	private String businessIntroduction;
	/**
	 * 注册来自那个页面
	 */
	private  String fromPage;
	/**
	 * 客户使用的什么客户端
	 */
	private int device;
	/**
	 * 是否绑定了google
	 */
	private String bind_google;
	/**
	 * 是否绑定了facebook
	 */
	private String bind_facebook;
	/**
	 * 明文密码
	 */
	private String uuid;
	/**
	 * 用户whatsapp 账号
	 */
	private String whatsapp;
	/**
	 * Business name or website/facebook
	 */
	private String business;

	private double cashBack;
	private double firstDiscount;
	/**
	 * facebook
	 */
	private String facebook;
	/**
	 * website
	 */
	private String website;

	/**
	 * shopify标识 0没有申请， 1申请
	 */
	private Integer shopifyFlag;

	/**
	 * shopify店铺名称
	 */
	private String shopifyName;


	/**
	 * 网站标识
	 */
	private int site;

	/**
	 * 授权标识 0未授权 1授权
	 */
	private int authorizationFlag;
}
