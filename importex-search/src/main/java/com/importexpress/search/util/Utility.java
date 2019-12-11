package com.importexpress.search.util;

import com.google.common.collect.Lists;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.pojo.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;

@Slf4j
@Component
public class Utility {
	/*工具类是一堆静态字段和函数的集合，其不应该被实例化；但是，
	Java 为每个没有明确定义构造函数的类添加了一个隐式公有构造函数，为了避免不必要的实例化，应该显式定义私有构造函数来屏蔽这个隐式公有构造函数*/
	private Utility(){}
	//家具搜索类别
	public static final String [] homeCatid={"4","6","200514001","79","120","9260"};
	public static final String [] AbrasivesList={"Abrasive Blasting","Abrasive Brushes and Wheel Kits","Abrasive Dressing Products","Abrasive Rolls and Kits","Abrasive Sharpening Stones","Deburring Tools","Flap Wheels","Mounted Points and Kits","Polishing","Sanding Belts and Kits","Sanding Discs and Kits","Sanding Hand Pads and Sponges","Sandpaper and Kits","Surface Conditioning Wheels","Tumblers and Media","Sandblasting Equipment","Other Tools","Abrasive Cut-Off and Chop Wheels","Cup Grinding Wheels","Depressed Center Wheels","Diamond and CBN Grinding Wheels","Diamond Segment Grinding Wheel","Grinding Cones and Plugs","Surface Grinding Wheels"};
	private static final String[] email_suffix = "@gmail.com,@yahoo.com,@msn.com,@hotmail.com,@aol.com,@ask.com,@live.com,@0355.net,@yeah.net,@googlemail.com,@sina.com,@sohu.com,@yahoo.com.cn".split(",");
	public static String base = "abcdefghijklmnopqrstuvwxyz0123456789";
	/**
	 * 美元汇率
	 */
	public static final double EXCHANGE_RATE = 6.6;
	//Added <V1.0.1> Start： cjc 2018/11/22 15:25:52 Description: eub 运费更新
	public static final double PERGRAMUSA = 0.066d;

	/**
	 * 生成数字型长度为【length】的随机数
	 * @param length
	 * @return
	 */
	public static String genNumericalRandom(int length) {
		Random rm = new Random();
		// 获得随机数
		double pross = rm.nextDouble();
		// 将获得的获得随机数转化为字符串
		String fixLenthString = String.valueOf(pross);
		// 返回固定的长度的随机数
		return fixLenthString.substring(2, length + 2);
	}

	/**
	 * 返回随机List
	 * @param list 备选
	 * @param selected 备选数量
	 * @return
	 */
	public static List<Product> getRandomNumList(List<Product> list, int selected) {
		List<Product> reList = Lists.newArrayList();
		Random random = new Random();
		// 先抽取，备选数量的个数
		if (list.size() >= selected) {
			for (int i = 0; i < selected; i++) {
				// 随机数的范围为0-list.size()-1;
				int target = random.nextInt(list.size());
				reList.add(list.get(target));
				list.remove(target);
			}
		} else {
			selected = list.size();
			for (int i = 0; i < selected; i++) {
				// 随机数的范围为0-list.size()-1;
				int target = random.nextInt(list.size());
				reList.add(list.get(target));
				list.remove(target);
			}
		}

		return reList;
	}

	public static String getMACAddress() throws Exception {
		InetAddress ia = InetAddress.getLocalHost();
		// 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

		// 下面代码是把mac地址拼装成String
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			// mac[i] & 0xFF 是为了把byte转化为正整数
			String s = Integer.toHexString(mac[i] & 0xFF);
			sb.append(s.length() == 1 ? 0 + s : s);
		}

		// 把字符串所有小写字母改为大写成为正规的mac地址并返回
		return sb.toString().toUpperCase().replaceAll("-", "");
	}

	public static boolean checkCatid(String catids){
		List<String> lists=Arrays.asList(homeCatid);
		boolean flag=true;
		if(StringUtils.isBlank(catids)){
			return flag;
		}
		String [] catid=catids.split(",");
		for(int i=0;i<catid.length;i++){
			if(lists.contains(catid[i])){
				flag=false;
				break;
			}
		}
		return flag;
	}

	/**
	 * 截取名称获取新的搜索词
	 * @param keyword
	 * @return
	 */
	public static String getNewKeyword(String keyword){
		if(org.apache.commons.lang3.StringUtils.isBlank(keyword) || StringUtils.indexOf(keyword, " ") < 1) {
			return keyword;
		}
		return keyword.substring(0, keyword.lastIndexOf(" "));
	}

	/**ABC->AB或者AC或者BC
	 * @param keyword
	 * @param lastIndex
	 * @return
	 */
	public static String newArrayKeyword(String keyword,int lastIndex) {
		String[] keys = keyword.split("(\\s+)");
		if(keys.length != 3 || lastIndex > 2 || lastIndex < 0) {
			return keyword;
		}
		String[] newArray = new String[] {keys[0]+" "+keys[1],keys[1]+" "+keys[2],keys[0]+" "+keys[2]};
		return newArray[lastIndex];
	}
	/**
	 * @Title: substringTitle
	 * @Author: cjc
	 * @Despricetion: 截取产品name
	 * @Date: 2019/4/25 11:32:15
	 * @Param: [title]
	 * @Return: void
	 */
	public static void substringTitle(String title) {
		String[] split = title.split(" ");
		if(split.length>10){
			title = "";
			for(int i = 0;i<10;i++){
				if(i<9){
					title +=  split[i]+" ";
				}else{
					title +=  split[i];
				}
			}
		}
	}


	/**排列组合C(n,m)
	 * @param keyword
	 * @return
	 */
	public static String[] combination(String keyword) {
		String[] keys = keyword.split("(\\s+)");
		int length = keys.length;
		length = length > 8 ? 8 : length;
		int cLength = length - 1 > 4 ? 4 : 	length - 1;
		int arrayNum = arrayNum(length, cLength);
		if(arrayNum < 1) {
			return null;
		}
		String[] newArray = arrayLoop(keys, length, arrayNum, cLength);
		return newArray;
	}

	private static String[] arrayLoop(String[] keys, int length, int arrayNum, int cLength) {
		boolean thirdCicrl = cLength > 2;
		boolean fourthCicrl = cLength == 4;
		String[] newArray = new String[arrayNum];
		int k = 0;
		for(int i=0;i<length-cLength + 1;i++) {
			for(int j=i+1;j<length;j++) {

				if(thirdCicrl) {
					for(int l=j+1;l<length;l++) {

						if(fourthCicrl) {
							for(int h=l+1;h<length;h++) {
								if(k < arrayNum) {
									newArray[k] = keys[i]+" "+keys[j]+" "+keys[l]+" "+keys[h];
									k++;
								}
							}
						}else {
							if(k < arrayNum) {
								newArray[k] = keys[i]+" "+keys[j]+" "+keys[l];
								k++;
							}
						}
					}
				}else {
					if(k < arrayNum) {
						newArray[k] = keys[i]+" "+keys[j];
						k++;
					}

				}
			}
		}
		return newArray;
	}


	/**C(n,m)从长度为n的数组中取m个值排列组合的组合个数
	 * @param n 数组个数
	 * @param m 组合
	 * @return
	 */
	public static int arrayNum(int n,int m) {
		return n < m ? 0 : n > m ? factorial(n) / (factorial(m) * factorial(n - m)) : 1;
	}

	/**n的阶乘n!(考虑实际数据使用主处理8以下的阶乘)
	 * @param n
	 * @return
	 */
	public static int factorial(int n) {
		return n > 8 ? 40320 : n > 1 ? n * factorial(n-1) : 1;
	}



}

