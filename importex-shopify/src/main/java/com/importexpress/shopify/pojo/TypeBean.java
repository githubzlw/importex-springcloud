package com.importexpress.shopify.pojo;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**规格数据
 * @author abc
 *
 */
@Data
@Accessors(chain = true)
@Builder
public class TypeBean implements Serializable, Comparable<TypeBean>,Cloneable{
	private static final long serialVersionUID = 6798772789095149437L;
	@Tolerate
	public TypeBean(){}
	private String type;//规格类型
	private String value;//规格值
	private String img;//图片
	private String id;//id
	private String lableType;
	private String sell;
	public void setValue(String value) {
		if(value!=null){
			value = value.replaceAll("\"", "").replaceAll("'", "");
		}
		this.value = value;
	}
	@Override
	public int compareTo(TypeBean o) {
		TypeBean t = (TypeBean) o;

		String type1 = t.getType();
		String type2 = this.getType();

		if(type1==null||type1.isEmpty()||type2==null||type2.isEmpty()){
			return 0;
		}
		return type1.compareTo(type2);
	}
	@Override
	public TypeBean clone()  {
		TypeBean bean = null;
		try {
			bean = (TypeBean)super.clone();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bean;
	}
}
