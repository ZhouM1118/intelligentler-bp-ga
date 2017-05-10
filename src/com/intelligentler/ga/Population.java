package com.intelligentler.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Population {
	
	private List<Chromosome> chromosomes = new ArrayList<Chromosome>();
	
	public void addChromosome(Chromosome chromosome) {
		this.chromosomes.add(chromosome);
	}
	
	public List<Chromosome> getChromosomeList()
	{
		return this.chromosomes;
	}
	
	public void setChromosomeList(List<Chromosome> chromosomes)
	{
		this.chromosomes = chromosomes;
	}
	
	public int getSize() {
		return this.chromosomes.size();
	}
	
	public Chromosome getRandomChromosome() {
		int numOfChromosomes = this.chromosomes.size();
		Random random = new Random();
		int indx = random.nextInt(numOfChromosomes);
		return this.chromosomes.get(indx);
	}
	
	public Chromosome getChromosomeByIndex(int indx) {
		return this.chromosomes.get(indx);
	}
	
	public void trim(int len) {
		this.chromosomes = this.chromosomes.subList(0, len);
	}
	
	public Map<String, Double> getGeneMaxAndMin(Integer geneIndex)
	{
		Map<String, Double> result = new HashMap<String, Double>();
		List<Double> genes = new ArrayList<Double>();
		for (Chromosome chromosome : chromosomes) 
		{
			genes.add(chromosome.getGene()[geneIndex]);
		}
		Collections.sort(genes);
		result.put("MAX", genes.get(genes.size() - 1));
		result.put("MIN", genes.get(0));
		return result;
	}
	
	public static void main(String[] args) {
		List<Integer> test = new ArrayList<Integer>();
		test.add(3);
		test.add(5);
		test.add(7);
		test.add(9);
		System.out.println(test);
		List<Integer> half = test.subList(0, (test.size() / 2));
		List<Integer> behind = test.subList((test.size() / 2), test.size());
		System.out.println(half);
		System.out.println(behind);
		System.out.println(test);
		test.addAll(behind);
		System.out.println(test);
	}

}
