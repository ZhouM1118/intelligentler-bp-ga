package com.intelligentler.bp;

import java.util.List;

/**
 * @author ming.zhou
 * 样本节点类，也即数据节点
 */
public class DataNode
{
	private List<Double> mAttribList;
	private double attrib_max;
	private double attrib_min;
	//训练节点将next_aqi用来计算训练误差；测试节点将next_aqi用来计算测试误差(均方误差或平均绝对百分比误差)
	private double next_aqi;
	private double aqi_result;

	public double getAqi_result() {
		return aqi_result;
	}

	public void setAqi_result(double aqi_result) {
		this.aqi_result = aqi_result;
	}

	public double getNext_aqi() {
		return next_aqi;
	}

	public void setNext_aqi(double next_aqi) {
		this.next_aqi = next_aqi;
	}

	public double getAttrib_max() {
		return attrib_max;
	}

	public void setAttrib_max(double attrib_max) {
		this.attrib_max = attrib_max;
	}

	public double getAttrib_min() {
		return attrib_min;
	}

	public void setAttrib_min(double attrib_min) {
		this.attrib_min = attrib_min;
	}

	public List<Double> getAttribList()
	{
		return mAttribList;
	}

	public void setAttribList(List<Double> attribList)
	{
		this.mAttribList = attribList;
	}

}
