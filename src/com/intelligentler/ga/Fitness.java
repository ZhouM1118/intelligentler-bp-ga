package com.intelligentler.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.intelligentler.bp.AnnClassifier;
import com.intelligentler.bp.DataNode;
import com.intelligentler.util.DataUtil;

public class Fitness {
	
	private static Fitness instance = null;
	
	public static synchronized Fitness getInstance()
	{
		if (instance == null)
			instance = new Fitness();
		return instance;
	}
	
	public Double calculate(AnnClassifier annClassifier, List<DataNode> dataList, Chromosome chromosome)
	{
		DataUtil dataUtil = DataUtil.getInstance();
		//每次根据染色体的基因来对神经网络中的权重和阀值进行赋值
		annClassifier.GA_reset(chromosome.getGene());
		//暂时取第一个样本数据来训练
		DataNode test = dataList.get(0);
		double aqi_result = annClassifier.test(test);
		aqi_result = dataUtil.back_normalized(test, aqi_result);
		double next_aqi = test.getNext_aqi();
		//result越小说明适应度越大，方便后续对种群中的染色体排序
		double result = Math.abs(aqi_result - next_aqi);
//		double result = 1 / Math.pow((aqi_result - next_aqi), 2);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception
	{
		//构建遗传算法的神经网络
		String trainfile = "/Users/ming.zhou/Doctor/research/deep learning/data/国控历史空气质量监测数据_4a7a038a3868dd3d27b65c36734a8f57_20161124/bp/湖北省-station_day-武汉市-2016-01.csv";
		String[] monitorNames = {"沌口新区", "东湖梨园", "汉阳月湖", "汉口花桥", 
				"武昌紫阳", "青山钢花", "汉口江滩", "东湖高新", "吴家山", "沉湖七壕"};
		String monitorName = "沌口新区";
		DataUtil util = DataUtil.getInstance();
		Map<String, Map<String, Object>> sampleMap = util.getSampleMap(trainfile, ",");
		List<List<Double>> orignalList = (List<List<Double>>) sampleMap.get(monitorName).get("orignalList");
		List<Double> next_aqis = (List<Double>) sampleMap.get(monitorName).get("next_aqis");
		List<DataNode> dataList = new ArrayList<DataNode>();
		DataNode node = new DataNode();
		List<Double> sampleList = util.normalized(node, orignalList.get(0));
		node.setAttribList(sampleList);
		node.setNext_aqi(next_aqis.get(0));
		dataList.add(node);
		AnnClassifier annClassifier = new AnnClassifier(sampleList.size(), sampleList.size() + 4, 1);
		
		Chromosome chromosome = new Chromosome(0, 0, 0);
		Fitness.getInstance().calculate(annClassifier, dataList, chromosome);
	}

}
