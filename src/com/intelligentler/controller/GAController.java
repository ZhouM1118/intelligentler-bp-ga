package com.intelligentler.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.intelligentler.bp.AnnClassifier;
import com.intelligentler.bp.DataNode;
import com.intelligentler.ga.Chromosome;
import com.intelligentler.ga.Fitness;
import com.intelligentler.ga.Population;
import com.intelligentler.util.DataUtil;

public class GAController {
	
	public static int populationSize = 150;
	public static double crossoverRate = 0.7;
	public static double mutateRate = 0.01;
	public static int maxGeneTime = 50;
//	public static double threshold = 1e-5;
	
	/**
	 * 初始化种群
	 * @param populationSize 种群规模
	 * @return 种群
	 */
	public static Population initPopulation()
	{
		Population population = new Population();
		for (int i = 0; i < populationSize; i++) 
		{
			Chromosome chromosome = new Chromosome(100, crossoverRate, mutateRate);
			population.addChromosome(chromosome);
		}
		return population;
	}
	
	@SuppressWarnings("unchecked")
	public static double[] getGene(String monitorName) throws Exception
	{
		//创建初始种群
		Population population = initPopulation();
		
		//构建遗传算法的神经网络
		String trainfile = "/Users/ming.zhou/Doctor/research/deep learning/data/国控历史空气质量监测数据_4a7a038a3868dd3d27b65c36734a8f57_20161124/bp/湖北省-station_day-武汉市-2016-01.csv";
		String[] monitorNames = {"沌口新区", "东湖梨园", "汉阳月湖", "汉口花桥", 
				"武昌紫阳", "青山钢花", "汉口江滩", "东湖高新", "吴家山", "沉湖七壕"};
//		String monitorName = "东湖梨园";
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
		
		for (Chromosome chromosome : population.getChromosomeList()) 
		{
			//计算采用误差值，故而fitness的值越小说明该染色体的适应度越大
			double fitness = Fitness.getInstance().calculate(annClassifier, dataList, chromosome);
			chromosome.setFitness(fitness);
		}
		
		for (int k = 0; k < maxGeneTime; k++) 
		{
			List<Chromosome> chromosomes = population.getChromosomeList();
			//将当前种群克隆一份
			List<Chromosome> cloneChromosomes = new ArrayList<Chromosome>();
			Collections.addAll(cloneChromosomes, new Chromosome[chromosomes.size()]);
			Collections.copy(cloneChromosomes, chromosomes);
			
			//染色体交叉
			long crossoverNum = Math.round(populationSize * crossoverRate);//交叉数
			List<Integer> soleCrossoverIndexList = util.getSoleIndexList(crossoverNum, populationSize);
			//保证交叉的个数是偶数，以便交叉处理
			if (soleCrossoverIndexList.size() % 2 != 0) {
				soleCrossoverIndexList.remove(soleCrossoverIndexList.size() - 1);
			}
			List<Integer> frontHalfIndexList = soleCrossoverIndexList.subList(0, (soleCrossoverIndexList.size() / 2));
			List<Integer> behindHalfIndexList = soleCrossoverIndexList.subList((soleCrossoverIndexList.size() / 2), soleCrossoverIndexList.size());
			for (int i = 0; i < frontHalfIndexList.size(); i++) 
			{
				for (int j = 0; j < behindHalfIndexList.size(); j++) 
				{
					if (i == j) 
					{
						List<Chromosome> crossoverChromosomes = chromosomes.get(frontHalfIndexList.get(i))
								.crossover(chromosomes.get(behindHalfIndexList.get(j)));
						cloneChromosomes.addAll(crossoverChromosomes);
					}
				}
			}
			
			//染色体变异
			long mutateNum = Math.round(populationSize * mutateRate);//变异数
			List<Integer> soleMutateIndexList = util.getSoleIndexList(mutateNum, populationSize);
			for (Integer mutateIndex : soleMutateIndexList) 
			{
				cloneChromosomes.add(chromosomes.get(mutateIndex).mutate(population, k, maxGeneTime));
			}
			
			//根据适应度函数来选择染色体进入下一个种群
			for (Chromosome chromosome : cloneChromosomes) 
			{
				//计算采用误差值，故而fitness的值越小说明该染色体的适应度越大
				double fitness = Fitness.getInstance().calculate(annClassifier, dataList, chromosome);
				chromosome.setFitness(fitness);
			}
			
			Collections.sort(cloneChromosomes);
			population.setChromosomeList(cloneChromosomes);
			population.trim(populationSize);
		}
		
		Chromosome chromosome = population.getChromosomeByIndex(0);
		return chromosome.getGene();
//		double dit = chromosome.getFitness();
//		System.out.println(dit);
	}
	
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception
	{
		//创建初始种群
		Population population = initPopulation();
		
		//构建遗传算法的神经网络
		String trainfile = "/Users/ming.zhou/Doctor/research/deep learning/data/国控历史空气质量监测数据_4a7a038a3868dd3d27b65c36734a8f57_20161124/bp/湖北省-station_day-武汉市-2016-01.csv";
		String[] monitorNames = {"沌口新区", "东湖梨园", "汉阳月湖", "汉口花桥", 
				"武昌紫阳", "青山钢花", "汉口江滩", "东湖高新", "吴家山", "沉湖七壕"};
		String monitorName = "东湖梨园";
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
		
		for (Chromosome chromosome : population.getChromosomeList()) 
		{
			//计算采用误差值，故而fitness的值越小说明该染色体的适应度越大
			double fitness = Fitness.getInstance().calculate(annClassifier, dataList, chromosome);
			chromosome.setFitness(fitness);
		}
		
		for (int k = 0; k < maxGeneTime; k++) 
		{
			List<Chromosome> chromosomes = population.getChromosomeList();
			//将当前种群克隆一份
			List<Chromosome> cloneChromosomes = new ArrayList<Chromosome>();
			Collections.addAll(cloneChromosomes, new Chromosome[chromosomes.size()]);
			Collections.copy(cloneChromosomes, chromosomes);
			
			//染色体交叉
			long crossoverNum = Math.round(populationSize * crossoverRate);//交叉数
			List<Integer> soleCrossoverIndexList = util.getSoleIndexList(crossoverNum, populationSize);
			//保证交叉的个数是偶数，以便交叉处理
			if (soleCrossoverIndexList.size() % 2 != 0) {
				soleCrossoverIndexList.remove(soleCrossoverIndexList.size() - 1);
			}
			List<Integer> frontHalfIndexList = soleCrossoverIndexList.subList(0, (soleCrossoverIndexList.size() / 2));
			List<Integer> behindHalfIndexList = soleCrossoverIndexList.subList((soleCrossoverIndexList.size() / 2), soleCrossoverIndexList.size());
			for (int i = 0; i < frontHalfIndexList.size(); i++) 
			{
				for (int j = 0; j < behindHalfIndexList.size(); j++) 
				{
					if (i == j) 
					{
						List<Chromosome> crossoverChromosomes = chromosomes.get(frontHalfIndexList.get(i))
								.crossover(chromosomes.get(behindHalfIndexList.get(j)));
						cloneChromosomes.addAll(crossoverChromosomes);
					}
				}
			}
			
			//染色体变异
			long mutateNum = Math.round(populationSize * mutateRate);//变异数
			List<Integer> soleMutateIndexList = util.getSoleIndexList(mutateNum, populationSize);
			for (Integer mutateIndex : soleMutateIndexList) 
			{
				cloneChromosomes.add(chromosomes.get(mutateIndex).mutate(population, k, maxGeneTime));
			}
			
			//根据适应度函数来选择染色体进入下一个种群
			for (Chromosome chromosome : cloneChromosomes) 
			{
				//计算采用误差值，故而fitness的值越小说明该染色体的适应度越大
				double fitness = Fitness.getInstance().calculate(annClassifier, dataList, chromosome);
				chromosome.setFitness(fitness);
			}
			
			Collections.sort(cloneChromosomes);
			population.setChromosomeList(cloneChromosomes);
			population.trim(populationSize);
		}
		
		Chromosome chromosome = population.getChromosomeByIndex(0);
		double dit = chromosome.getFitness();
		System.out.println(dit);
	}

}
