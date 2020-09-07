package com.importexpress.search.util;

import com.google.common.collect.Lists;
import com.importexpress.search.pojo.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

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
}

