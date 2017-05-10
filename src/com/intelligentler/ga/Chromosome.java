package com.intelligentler.ga;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.intelligentler.util.DataUtil;

public class Chromosome implements Comparable<Chromosome>{
	
	private double[] gene;//染色体的基因组
	private double fitness;//适应度
	private long crossoverRate;//染色体中的基因交叉数
	private long mutateRate;//染色体中的基因变异数
	
	public double[] getGene() {
		return gene;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public Chromosome(int geneLength, double crossoverRate, double mutateRate)
	{
		this.gene = new double[geneLength];
		for (int i = 0; i < this.gene.length; i++) {
			this.gene[i] = (double)Math.random();
		}
		this.crossoverRate = Math.round(geneLength * crossoverRate);
		this.mutateRate = Math.round(geneLength * mutateRate);
	}
	
	
	/**
	 * 初始化染色体的基因
	 * @return 基因数组
	 */
	public double[] initGene() {
		for (int i = 0; i < this.gene.length; i++) {
			this.gene[i] = (double)Math.random();
		}
		return gene;
	}
	
	/**
	 * 组装对基因交叉/变异位置下标的列表（位置下标已去重）
	 * @param rate
	 * @return 位置下标的列表sole
	 */
	public List<Integer> getGeneIndexList(long rate) 
	{
		Random random = new Random();
		List<Integer> geneIndexList = new ArrayList<Integer>();
		int i = 0;
		while(i < rate)
		{
			Integer randomIndex = random.nextInt(this.gene.length);
			if (geneIndexList.contains(randomIndex)) 
			{
				continue;
			}
			else {
				geneIndexList.add(randomIndex);
				i++;
			}
		}
		return geneIndexList;
	}

	/**
	 * 染色体交叉
	 * 交叉策略：对于适应度大的染色体的基因在父染色体的基因附近方向搜索；对于适应度小的染色体基因朝着适应度大的父染色体的基因反向搜索
	 * @param anther_chromosome
	 * @return 新染色体的列表
	 */
	public List<Chromosome> crossover(Chromosome anther_chromosome) 
	{
		DataUtil dataUtil = DataUtil.getInstance();
		List<Chromosome> chromosomes = new ArrayList<Chromosome>();
		
		//组装对基因交叉位置下标的列表（位置下标已去重）this.crossoverRate
		List<Integer> geneIndexList = dataUtil.getSoleIndexList(this.crossoverRate, this.gene.length);
		
		//基因交叉
		double p = Math.random();
		for (Integer geneIndex : geneIndexList) 
		{
			if (this.fitness >= anther_chromosome.getFitness()) 
			{
				double tempGene = this.gene[geneIndex];
				this.gene[geneIndex] = ((1 + p) / 2) * this.gene[geneIndex] 
						+ ((1 - p) / 2) * anther_chromosome.getGene()[geneIndex];
				anther_chromosome.getGene()[geneIndex] = p * tempGene 
						+ (1 - p) * anther_chromosome.getGene()[geneIndex];
			}
			else 
			{
				double tempGene = anther_chromosome.getGene()[geneIndex];
				anther_chromosome.getGene()[geneIndex] = ((1 + p) / 2) * anther_chromosome.getGene()[geneIndex] 
						+ ((1 - p) / 2) * this.gene[geneIndex];
				this.gene[geneIndex] = p * tempGene 
						+ (1 - p) * anther_chromosome.getGene()[geneIndex];
			}
		}
		chromosomes.add(this);
		chromosomes.add(anther_chromosome);
		
		return chromosomes;
	}
	
	/**
	 * 染色体变异
	 * 变异策略：染色体基因元素变大或变小有相同的概率（50%），且变异的大小随着迭代次数变大而变小
	 * @param currentGeneTime
	 * @param maxGeneTime
	 * @return 变异后的新染色体
	 */
	public Chromosome mutate(Population population, int currentGeneTime, int maxGeneTime) 
	{
		DataUtil dataUtil = DataUtil.getInstance();
		List<Integer> geneIndexList = dataUtil.getSoleIndexList(this.mutateRate, this.gene.length);
		for (Integer geneIndex : geneIndexList) 
		{
			//注：max和min为下标为geneIndex的基因元素的最大和最小值，需要通过对比种群中所有的染色体的该对应的基因元素来获取
			Map<String, Double> maxAndmin = population.getGeneMaxAndMin(geneIndex);
			double max = maxAndmin.get("MAX");
			double min = maxAndmin.get("MIN");
			double r = Math.random();
			if (r >= 0.5) 
			{
				this.gene[geneIndex] += (currentGeneTime / maxGeneTime) * (max - this.gene[geneIndex]);
			}
			else 
			{
				this.gene[geneIndex] += (currentGeneTime / maxGeneTime) * (min - this.gene[geneIndex]);
			}
		}
		return this;
	}
	
	@Override
	public int compareTo(Chromosome o) {
		return Double.valueOf(this.getFitness()).compareTo(Double.valueOf(o.getFitness()));
	}
	
	public static void main(String[] args) {
		int a = 71;
		double b = 0.1;
//		int result = 0;
//		result = Math.ceil(a * b);
		System.out.println(Math.ceil(a * b));
		System.out.println(Math.floor(a * b));
		System.out.println(Math.round(a * b));
		
		Random random = new Random();
		List<Integer> geneIndexList = new ArrayList<Integer>();
		int i = 0;
		while(i < Math.round(a * b))
		{
			Integer randomIndex = random.nextInt(8);
			if (geneIndexList.contains(randomIndex)) 
			{
				continue;
			}
			else {
				geneIndexList.add(randomIndex);
				i++;
			}
		}
		System.out.println(geneIndexList);
		
	}

	
}
