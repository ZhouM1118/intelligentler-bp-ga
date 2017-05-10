package com.intelligentler.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.intelligentler.bp.AnnClassifier;
import com.intelligentler.bp.DataNode;

/**
 * @author ming.zhou
 * 数据助手类
 */
public class DataUtil
{
	private static DataUtil instance = null;
	private Map<String, List<List<Double>>> dataMap;
	private List<Double> next_aqis;
	private Map<String, Map<String, Object>> sampleMap;

	public Map<String, Map<String, Object>> getSampleMap() {
		return sampleMap;
	}

	public void setSampleMap(Map<String, Map<String, Object>> sampleMap) {
		this.sampleMap = sampleMap;
	}

	public DataUtil()
	{
		dataMap = new HashMap<String, List<List<Double>>>();
		sampleMap = new HashMap<String, Map<String, Object>>();
	}
	
	public static synchronized DataUtil getInstance()
	{
		if (instance == null)
			instance = new DataUtil();
		return instance;
	}

	/**
	 * 根据文件生成训练集／测试集
	 * 
	 * @param fileName 文件名
	 * @param sep 分隔符
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<DataNode> getDataList(String fileName, String sep, String monitorName, int number)
			throws Exception
	{
		sampleMap.clear();
		List<DataNode> list = new ArrayList<DataNode>();
		sampleMap = getSampleMap(fileName, sep);
		List<List<Double>> orignalList = (List<List<Double>>) sampleMap.get(monitorName).get("orignalList");
		List<Double> next_aqis = (List<Double>) sampleMap.get(monitorName).get("next_aqis");
		
		List<List<Double>> sub_orignalList = null;
		if (number == 30) //训练样本不需要截取样本集
		{
			//除去monitorList最后一行数据
			orignalList.remove(orignalList.size() - 1);
			sub_orignalList = orignalList;
		}
		else 
		{
			sub_orignalList = orignalList.subList(0, number);
		}
		for (int i = 0; i < sub_orignalList.size(); i++) {
//			monitorList.get(i).add(next_aqis.get(i));
			DataNode node = new DataNode();
			List<Double> attribList = normalized(node, sub_orignalList.get(i));
			node.setAttribList(attribList);
			node.setNext_aqi(next_aqis.get(i));
			list.add(node);
		}
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, Object>> getSampleMap(String fileName, String sep) throws Exception
	{
		Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
		BufferedReader br = new BufferedReader(new FileReader(
				new File(fileName)));
		String line = null;
		br.readLine();
		
		List<List<Double>> originalList = null;
		List<Double> next_aqis = null;
		//每次读取一行数据，把相应的数据存入dataMap中
		while ((line = br.readLine()) != null)
		{
			String original[] = line.split(sep);
			List<Double> attribList = new ArrayList<Double>();
			Map<String, Object> data1Map = new HashMap<String, Object>();
			
			//如果有该监测点的数据列表，则在其数据列表的基础上添加数据
			if (result.containsKey(original[3])) 
			{
				originalList = (List<List<Double>>) result.get(original[3]).get("orignalList");
				next_aqis = (List<Double>) result.get(original[3]).get("next_aqis");
			}
			else 
			{
				originalList = new ArrayList<List<Double>>();
				next_aqis = new ArrayList<Double>();
			}
			
			//为每个样本的特征值赋值，组装attribList
			for (int i = 0; i < original.length - 1; i++) 
			{
				if (i >= 8 && i != 12) {
					if (original[i] == null || original[i].isEmpty()) {
						original[i] = "0";
					}
					attribList.add(Double.valueOf(original[i]));
				}
			}
			originalList.add(attribList);
			
			//设置“下一日aqi”，从originalList中第二行开始记录空气数据中的aqi数据
			if (originalList.size() > 1) 
			{
				double now_aqi = originalList.get(originalList.size() - 1).get(0);
				next_aqis.add(now_aqi);
			}
			
			data1Map.put("orignalList", originalList);
			data1Map.put("next_aqis", next_aqis);
			result.put(original[3], data1Map);
//			System.out.println(sampleMap);
		}
		if (br != null) {
			br.close();
		}
		return result;
	}
	
	private void getDataMap(String fileName, String sep) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(
				new File(fileName)));
		String line = null;
		br.readLine();
		List<List<Double>> monitorList = null;
		//每次读取一行数据，把相应的数据存入dataMap中
		while ((line = br.readLine()) != null)
		{
			String original[] = line.split(sep);
			List<Double> originalList = new ArrayList<Double>();
			
			//如果有该监测点的数据列表，则在其数据列表的基础上添加数据
			if (dataMap.containsKey(original[3])) 
			{
				monitorList = dataMap.get(original[3]);
			}
			else {
				monitorList = new ArrayList<List<Double>>();
			}
			
			for (int i = 0; i < original.length - 1; i++) 
			{
				if (i >= 8 && i != 12) {
					if (original[i] == null || original[i].isEmpty()) {
						original[i] = "0";
					}
					originalList.add(Double.valueOf(original[i]));
				}
			}
			monitorList.add(originalList);
			dataMap.put(original[3], monitorList);
			
			//设置“下一日aqi”，从monitorList中第二行开始记录空气数据中的aqi数据
			if (dataMap.get(original[3]).size() > 1) 
			{
				double now_aqi = monitorList.get(monitorList.size() - 1).get(0);
				next_aqis.add(now_aqi);
			}
		}
		
		if (br != null) {
			br.close();
		}
	}
	
	/**
	 * 归一化处理
	 * @param original 样本数据
	 * @return 归一后的样本数据
	 */
	public List<Double> normalized(DataNode node, List<Double> originalList)
	{
//		List<Double> originalList = new ArrayList<Double>();
//		List<List<Double>> monitorList = null;
//		
//		if (dataMap.containsKey(original[3])) 
//		{
//			monitorList = dataMap.get(original[3]);
//		}
//		else {
//			monitorList = new ArrayList<List<Double>>();
//			dataMap.put(original[3], monitorList);
//		}
//		
//		for (int i = 0; i < original.length - 1; i++) 
//		{
//			if (i >= 8 && i != 12) {
//				if (original[i] == null || original[i].isEmpty()) {
//					original[i] = "0";
//				}
//				originalList.add(Double.valueOf(original[i]));
//			}
//		}
//		monitorList.add(originalList);
//		dataMap.put(original[3], monitorList);
//		
//		//设置“下一日aqi”
//		if (dataMap.get(original[3]).size() > 1) //根据list中更后一行的空气数据中的aqi数据来设置前一行中的“下一日aqi”
//		{
//			double now_aqi = monitorList.get(monitorList.size() - 1).get(0);
//			next_aqis.add(now_aqi);
//		}
//		node.setNext_aqi(Double.valueOf(original[original.length - 1]));
		
		List<Double> attribList = new ArrayList<Double>();
		Collections.addAll(attribList, new Double[originalList.size()]);
		Collections.copy(attribList, originalList);
		Collections.sort(originalList);
		
		double original_min = originalList.get(0);
		double original_max = originalList.get(originalList.size()-1);
		node.setAttrib_max(original_max);
		node.setAttrib_min(original_min);
		for (int i = 0; i < attribList.size(); i++) {
			double normal = (AnnClassifier.MAX - AnnClassifier.MIN) * 
					(attribList.get(i)- original_min) / (original_max - original_min) 
					+ AnnClassifier.MIN;
			attribList.set(i, normal);
		}
		return attribList;
	}
	
	/**
	 * 遗传算法中的样本数据归一化
	 * @param originalList 样本数据
	 * @return 归一化后的样本数据
	 */
	public List<Double> normalized( List<Double> originalList)
	{
		List<Double> attribList = new ArrayList<Double>();
		Collections.addAll(attribList, new Double[originalList.size()]);
		Collections.copy(attribList, originalList);
		Collections.sort(originalList);
		
		double original_min = originalList.get(0);
		double original_max = originalList.get(originalList.size()-1);
		
		for (int i = 0; i < attribList.size(); i++) {
			double normal = (AnnClassifier.MAX - AnnClassifier.MIN) * 
					(attribList.get(i)- original_min) / (original_max - original_min) 
					+ AnnClassifier.MIN;
			attribList.set(i, normal);
		}
		return attribList;
	}
	
	/**
	 * 反归一化处理
	 * @param attribs 神经模型的输出结果
	 * @return 反归一后的aqi结果
	 */
	public double back_normalized(DataNode node, double next_aqi) 
	{
		double result = (next_aqi - AnnClassifier.MIN) * 
				(node.getAttrib_max() - node.getAttrib_min()) / (AnnClassifier.MAX - AnnClassifier.MIN) + node.getAttrib_min();
		return result;
	}
	
	/**
	 * 计算MSE均方误差
	 * @param compareList 测试节点的结果
	 * @return MSE均方误差
	 */
	public double getMSE(List<DataNode> compareList)
	{
		double temp = 0;
		for (int i = 0; i < compareList.size(); i++) 
		{
			DataNode node = compareList.get(i);
			temp += (Math.pow((node.getAqi_result() - node.getNext_aqi()), 2) / compareList.size());
		}
		return temp;
	}
	
	/**
	 * 计算MAPE平均绝对百分比误差
	 * @param compareList 测试节点的结果
	 * @return MAPE平均绝对百分比误差
	 */
	public double getMAPE(List<DataNode> compareList) {
		double temp = 0;
		for (int i = 0; i < compareList.size(); i++) 
		{
			DataNode node = compareList.get(i);
			temp += (Math.abs(node.getAqi_result() - node.getNext_aqi()) / node.getNext_aqi());
		}
		return (temp / compareList.size());
	}
	
	/**
	 * 组装在bound范围内取值唯一的列表，列表大小为rate
	 * @return 位置下标唯一的列表
	 */
	public List<Integer> getSoleIndexList(long rate, int bound) 
	{
		Random random = new Random();
		List<Integer> soleIndexList = new ArrayList<Integer>();
		int i = 0;
		while(i < rate)
		{
			Integer randomIndex = random.nextInt(bound);
			if (soleIndexList.contains(randomIndex)) 
			{
				continue;
			}
			else {
				soleIndexList.add(randomIndex);
				i++;
			}
		}
		return soleIndexList;
	}
	
	public static void main(String[] args) throws Exception
	{
//		String[] original = {"63.1","194.6","1.950","35.8","53.8","32","4.190","78"};
		String[] original = {"63.1","194.6","1.950","35.8","53.8","32","4.190","-0.21048533610173892"};
		List<Double> originalList = new ArrayList<Double>();
//		for(String attrib : original)
//		{
//			originalList.add(Double.valueOf(attrib));
//		}
//		List<Double> test = DataUtil.getInstance().normalized(original);
//		System.out.println(test);
//		System.out.println(DataUtil.getInstance().back_normalized(originalList));
//		System.out.println(Math.abs(-12.897));
		
		String aString = "2016-01-01 00:00:00";
		String bString = "2016-01-02 00:00:00";
		String cString = "2016-01-11 00:00:00";
//		System.out.println(aString.compareTo(bString));
//		System.out.println(aString.compareTo(cString));
//		System.out.println(bString.compareTo(cString));
		String fileName = "/Users/ming.zhou/Doctor/research/deep learning/data/国控历史空气质量监测数据_4a7a038a3868dd3d27b65c36734a8f57_20161124/湖北省-station_day-武汉市-2016-01.csv";
//		DataUtil.getInstance().getDataList(fileName, ",");
		String teString = "124.0, 93.0, 127.0, 122.0, 68.0, 97.0, 167.0, 173.0, 103.0, 93.0, 131.0, 78.0, 48.0, 45.0, 51.0, 71.0, 78.0, 106.0, 119.0, 113.0, 84.0, 151.0, 227.0, 158.0, 95.0, 73.0, 110.0, 109.0, 127.0, 133.0, 117.0, 93.0, 197.0, 229.0, 162.0, 98.0, 84.0, 120.0, 102.0, 125.0, 150.0, 136.0, 94.0, 191.0, 226.0, 175.0, 99.0, 83.0, 120.0, 111.0, 110.0, 133.0, 127.0, 90.0, 173.0, 223.0, 160.0, 107.0, 89.0, 108.0, 109.0, 131.0, 141.0, 134.0, 89.0, 195.0, 239.0, 193.0, 99.0, 81.0, 124.0, 92.0, 123.0, 136.0, 125.0, 88.0, 187.0, 217.0, 154.0, 99.0, 77.0, 111.0, 103.0, 119.0, 132.0, 124.0, 93.0, 199.0, 208.0, 173.0, 97.0, 81.0, 122.0, 107.0, 122.0, 160.0, 145.0, 89.0, 171.0, 217.0, 161.0, 106.0, 88.0, 123.0, 113.0, 125.0, 151.0, 140.0, 85.0, 172.0, 206.0, 159.0, 93.0, 77.0, 113.0, 109.0, 119.0, 144.0, 118.0, 81.0, 152.0, 291.0, 117.0, 101.0, 63.0, 97.0, 92.0";
		String[] strings = teString.split(",");
		System.out.println(strings.length);
		
	}
}
