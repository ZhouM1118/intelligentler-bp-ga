package com.intelligentler.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.intelligentler.bp.AnnClassifier;
import com.intelligentler.bp.DataNode;
import com.intelligentler.util.DataUtil;

public class BPController {
	
	public static void test(String trainfile, String testfile, String outputfile, String monitorName) throws Exception
	{
		String separator = ",";
		double eta =  0.02;
		int nIter = 500;
		DataUtil util = DataUtil.getInstance();
		List<DataNode> trainList = util.getDataList(trainfile, separator, monitorName, 30);
		List<DataNode> testList = util.getDataList(testfile, separator, monitorName, 10);
		BufferedWriter output = new BufferedWriter(new FileWriter(new File(outputfile)));
		AnnClassifier annClassifier = new AnnClassifier(trainList.get(0)
				.getAttribList().size(), trainList.get(0).getAttribList()
				.size() + 4, 1);
		annClassifier.setTrainNodes(trainList);
		annClassifier.train(eta, nIter, GAController.getGene(monitorName));
		
		List<DataNode> compareList = new ArrayList<DataNode>();
		for (int i = 0; i < testList.size(); i++)
		{
			DataNode test = testList.get(i);
			double aqi_result = annClassifier.test(test);
			List<Double> attribs = test.getAttribList();
			attribs.add(test.getNext_aqi());
			aqi_result = util.back_normalized(test, aqi_result);
			test.setAqi_result(aqi_result);
			compareList.add(test);
			for (int n = 0; n < attribs.size(); n++)
			{
				output.write(attribs.get(n) + ",");
				output.flush();
			}
			output.write(aqi_result + "\n");
		}
//		output.write("均方误差值为：" + util.getMSE(compareList) + "\n");
		output.write("平均绝对百分比误差值为：" + util.getMAPE(compareList) + "\n");
		//获取格式化对象
		NumberFormat nt = NumberFormat.getPercentInstance();
		//设置百分数精确度2即保留两位小数
		nt.setMinimumFractionDigits(2);
		//最后格式化并输出
		output.write("准确率为：" + nt.format(1 - util.getMAPE(compareList)) + "\n");
		output.flush();
		output.close();
	}

	public static void main(String[] args) throws Exception
	{
		String trainfile = "/Users/ming.zhou/Doctor/research/deep learning/data/国控历史空气质量监测数据_4a7a038a3868dd3d27b65c36734a8f57_20161124/bp/湖北省-station_day-武汉市-2016-01.csv";
		String testfile = "/Users/ming.zhou/Doctor/research/deep learning/data/国控历史空气质量监测数据_4a7a038a3868dd3d27b65c36734a8f57_20161124/bp/湖北省-station_day-武汉市-2016-02.csv";
		String[] monitorNames = {"沌口新区", "东湖梨园", "汉阳月湖", "汉口花桥", 
				"武昌紫阳", "青山钢花", "汉口江滩", "东湖高新", "吴家山", "沉湖七壕"};
		for (String monitorName : monitorNames) {
//		String monitorName = "汉口江滩";
			String outputfile = "/Users/ming.zhou/Doctor/research/deep learning/data/国控历史空气质量监测数据_4a7a038a3868dd3d27b65c36734a8f57_20161124/ga/" + monitorName + "_1_ga_result.txt";
			test(trainfile, testfile, outputfile, monitorName);
		}
		
	}

}
