package com.importexpress.search.util;

import com.google.common.collect.Lists;
import com.importexpress.comm.util.StrUtils;
import com.importexpress.search.pojo.Category;
import com.importexpress.search.pojo.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
	/**
	 * 美元汇率
	 */
	public static final double EXCHANGE_RATE = 6.6;
	//Added <V1.0.1> Start： cjc 2018/11/22 15:25:52 Description: eub 运费更新
	public static final double PERGRAMUSA = 0.066d;


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
					newArray[k] = getArrayStr(keys,k,arrayNum,new int[]{i,j});
					k++;
				}
			}
		}
		return newArray;
	}

	private static String getArrayStr(String[] arr,int k,int arrayNum,int[] index){
		if(k < arrayNum) {
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<index.length;i++){
				sb.append(arr[index[i]]).append(" ");
			}
			return sb.toString().trim();
		}
		return "";
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

