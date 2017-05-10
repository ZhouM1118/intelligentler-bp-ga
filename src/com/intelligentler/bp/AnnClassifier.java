package com.intelligentler.bp;

import java.util.ArrayList;
import java.util.List;

import com.intelligentler.util.DataUtil;

/**
 * @author ming.zhou
 * 人工神经网络
 */
public class AnnClassifier
{
	private int mInputCount;
	private int mHiddenCount;
	private int mOutputCount;

	private List<NetworkNode> mInputNodes;
	private List<NetworkNode> mHiddenNodes;
	private List<NetworkNode> mOutputNodes;

	private double[][] mInputHiddenWeight;
	private double[][] mHiddenOutputWeight;
	private double[] mHiddenThreshold;
	private double[] mOutpuThreshold;

	private List<DataNode> trainNodes;
	
	public final static int MAX = 1;
	public final static int MIN = -1;

	//初始化神经网络的训练样本列表
	public void setTrainNodes(List<DataNode> trainNodes)
	{
		this.trainNodes = trainNodes;
	}

	//初始化神经网络的基本结构
	public AnnClassifier(int inputCount, int hiddenCount, int outputCount)
	{
		trainNodes = new ArrayList<DataNode>();
		mInputCount = inputCount;
		mHiddenCount = hiddenCount;
		mOutputCount = outputCount;
		
		mInputNodes = new ArrayList<NetworkNode>();
		mHiddenNodes = new ArrayList<NetworkNode>();
		mOutputNodes = new ArrayList<NetworkNode>();
		
		mInputHiddenWeight = new double[mInputCount][mHiddenCount];
		mHiddenOutputWeight = new double[mHiddenCount][mOutputCount];
		mHiddenThreshold = new double[mHiddenCount];
		mOutpuThreshold = new double[mOutputCount];
	}
	
	//神经网络的一次训练
	public void train(double eta, int n, double[] gene)
	{
//		reset();
		GA_reset(gene);
		for (int i = 0; i < n; i++)
		{
			for (int j = 0; j < trainNodes.size(); j++)
			{
				forward(trainNodes.get(j).getAttribList());
				backward(trainNodes.get(j));
				updateWeights(eta);
			}
			System.out.println("n = " + i);
		}
	}
	
	/**
	 * 初始化神经网络的神经节点和其的权重与阈值
	 */
	private void reset()
	{
		mInputNodes.clear();
		mHiddenNodes.clear();
		mOutputNodes.clear();
		for (int i = 0; i < mInputCount; i++)
		{
			mInputNodes.add(new NetworkNode(NetworkNode.TYPE_INPUT));
		}
		for (int i = 0; i < mHiddenCount; i++)
		{
			mHiddenNodes.add(new NetworkNode(NetworkNode.TYPE_HIDDEN));
		}
		for (int i = 0; i < mOutputCount; i++)
		{
			mOutputNodes.add(new NetworkNode(NetworkNode.TYPE_OUTPUT));
		}
		
		for (int i = 0; i < mInputCount; i++)
		{
			for (int j = 0; j < mHiddenCount; j++)
			{
				mInputHiddenWeight[i][j] = (double) (Math.random());
			}
		}
		for (int i = 0; i < mHiddenCount; i++)
		{
			for (int j = 0; j < mOutputCount; j++)
			{
				mHiddenOutputWeight[i][j] = (double) (Math.random());
			}
		}
		for (int i = 0; i < mHiddenCount; i++) 
		{
			mHiddenThreshold[i] = (double)(Math.random());
		}
		for (int i = 0; i < mOutputCount; i++) 
		{
			mOutpuThreshold[i] = (double)(Math.random());
		}
	}
	
	/**
	 * 遗传算法－神经网络中初始化权值和阈值
	 * @param gene 染色体中的基因
	 */
	public void GA_reset(double[] gene)
	{
		mInputNodes.clear();
		mHiddenNodes.clear();
		mOutputNodes.clear();
		for (int i = 0; i < mInputCount; i++)
		{
			mInputNodes.add(new NetworkNode(NetworkNode.TYPE_INPUT));
		}
		for (int i = 0; i < mHiddenCount; i++)
		{
			mHiddenNodes.add(new NetworkNode(NetworkNode.TYPE_HIDDEN));
		}
		for (int i = 0; i < mOutputCount; i++)
		{
			mOutputNodes.add(new NetworkNode(NetworkNode.TYPE_OUTPUT));
		}
		
		for (int k = 0; k < gene.length; k++) {
			if (k >= 0 && k < (mInputCount * mHiddenCount)) 
			{
				for (int i = 0; i < mInputCount; i++)
				{
					for (int j = 0; j < mHiddenCount; j++)
					{
						mInputHiddenWeight[i][j] = gene[k];
					}
				}
			}
			
			if (k >= (mInputCount * mHiddenCount) && k < (mInputCount * mHiddenCount + mHiddenCount)) 
			{
				for (int i = 0; i < mHiddenCount; i++) 
				{
					mHiddenThreshold[i] = gene[k];
				}
			}
			
			if (k >= (mInputCount * mHiddenCount + mHiddenCount) 
					&& k < (mInputCount * mHiddenCount + mHiddenCount * 2)) 
			{
				for (int j = 0; j < mHiddenCount; j++)
				{
					for (int t = 0; t < mOutputCount; t++)
					{
						mHiddenOutputWeight[j][t] = gene[k];
					}
				}
			}
			
			if (k >= (mInputCount * mHiddenCount + mHiddenCount * 2)) 
			{
				for (int t = 0; t < mOutputCount; t++) 
				{
					mOutpuThreshold[t] = gene[k];
				}
			}
			
		}
	}

	/**
	 * 前向传播
	 */
	private void forward(List<Double> trainNode_attribList)
	{
		// 输入层
		for (int i = 0; i < trainNode_attribList.size(); i++)
		{
			mInputNodes.get(i).setForwardInputValue(trainNode_attribList.get(i));
		}
		// 隐层
		for (int j = 0; j < mHiddenCount; j++)
		{
			float temp = 0;
			for (int i = 0; i < mInputCount; i++)
			{
				temp += mInputHiddenWeight[i][j] * mInputNodes.get(i).getForwardOutputValue();
			}
			mHiddenNodes.get(j).setForwardInputValue(temp - mHiddenThreshold[j]);
		}
		// 输出层
		for (int t = 0; t < mOutputCount; t++)
		{
			float temp = 0;
			for (int j = 0; j < mHiddenCount; j++)
			{
				temp += mHiddenOutputWeight[j][t] * mHiddenNodes.get(j).getForwardOutputValue();
			}
			mOutputNodes.get(t).setForwardInputValue(temp - mOutpuThreshold[t]);
		}
	}

	/**
	 * 反向传播
	 */
	private void backward(DataNode node)
	{
		// 输出层
		for (int t = 0; t < mOutputCount; t++)
		{
			// 输出层计算误差并把误差反向传播
			double next_aqi = node.getNext_aqi();
			double aqi_result = DataUtil.getInstance().back_normalized(node, mOutputNodes.get(t).getForwardOutputValue());
			double back_input = Math.pow((aqi_result - next_aqi), 2) / 2;
			mOutputNodes.get(t).setBackwardInputValue(back_input);
		}
		// 隐层
		for (int j = 0; j < mHiddenCount; j++)
		{
			double temp = 0;
			for (int t = 0; t < mOutputCount; t++)
			{
				temp += mHiddenOutputWeight[j][t] * mOutputNodes.get(t).getBackwardOutputValue();
			}
			mHiddenNodes.get(j).setBackwardInputValue(temp);
		}
	}

	/**
	 * 更新权重和阈值
	 * 每个权重的梯度都等于与其相连的前一层节点的输出乘以与其相连的后一层的反向传播的输出
	 * 每个阈值的梯度为当前一层的反向传播的输出的负值
	 */
	private void updateWeights(double eta)
	{
		// 更新隐层到输出层的权重矩阵和阈值数组
		for (int j = 0; j < mHiddenCount; j++)
		{
			for (int t = 0; t < mOutputCount; t++)
			{
				mHiddenOutputWeight[j][t] -= eta
						* mHiddenNodes.get(j).getForwardOutputValue()
						* mOutputNodes.get(t).getBackwardOutputValue();
				mOutpuThreshold[t] -= eta * (-mOutputNodes.get(t).getBackwardOutputValue());
			}
			
		}
		// 更新输入层到隐层的权重矩阵
		for (int i = 0; i < mInputCount; i++)
		{
			for (int j = 0; j < mHiddenCount; j++)
			{
				mInputHiddenWeight[i][j] -= eta
						* mInputNodes.get(i).getForwardOutputValue()
						* mHiddenNodes.get(j).getBackwardOutputValue();
				mHiddenThreshold[j] -= eta * (-mHiddenNodes.get(j).getBackwardOutputValue());
			}
		}
		
	}
	
	public double test(DataNode dn)
	{
		forward(dn.getAttribList());
		return mOutputNodes.get(0).getForwardOutputValue();
	}
	
	public static void main(String[] args) {
		double a = 10;
		double b = 5;
		System.out.println(Math.pow((a - b), 2) / 2);
	}
}
