package com.intelligentler.bp;

public class NetworkNode
{
	public static final int TYPE_INPUT = 0;
	public static final int TYPE_HIDDEN = 1;
	public static final int TYPE_OUTPUT = 2;

	private int type;

	public void setType(int type)
	{
		this.type = type;
	}

	// 节点前向输入输出值
	private double mForwardInputValue;
	private double mForwardOutputValue;

	// 节点反向输入输出值
	private double mBackwardInputValue;
	private double mBackwardOutputValue;

	public NetworkNode()
	{
	}

	public NetworkNode(int type)
	{
		this.type = type;
	}

	/**
	 * sigmoid函数，这里用tan-sigmoid，经测试其效果比log-sigmoid好！
	 * 
	 * @param in
	 * @return
	 */
	private double forwardSigmoid(double in)
	{
		switch (type)
		{
			//输入层节点的激活函数为透明函数
			case TYPE_INPUT:
				return in;
			//隐藏层和输出层节点的激活函数为tan-sigmoid
			case TYPE_HIDDEN:
			case TYPE_OUTPUT:
				return tanhS(in);
		}
		return 0;
	}

	/**
	 * log-sigmoid函数
	 * 
	 * @param in
	 * @return
	 */
	private double logS(double in)
	{
		return (double) (1 / (1 + Math.exp(-in)));
	}

	/**
	 * log-sigmoid函数的导数
	 * 
	 * @param in
	 * @return
	 */
	private double logSDerivative(double in)
	{
		return mForwardOutputValue * (1 - mForwardOutputValue) * in;
	}

	/**
	 * tan-sigmoid函数
	 * 
	 * @param in
	 * @return
	 */
	private double tanhS(double in)
	{
		return (double) ((Math.exp(in) - Math.exp(-in)) / (Math.exp(in) + Math
				.exp(-in)));
	}

	/**
	 * tan-sigmoid函数的导数
	 * 
	 * @param in
	 * @return
	 */
	private double tanhSDerivative(double in)
	{
		return (double) (1 - Math.pow(mForwardOutputValue, 2));
	}

	/**
	 * 误差反向传播时，输出层的反向输出值为：-(输入值＊激活函数的导数)，隐藏层的反向输出值为：输入值＊激活函数的导数
	 * 
	 * @param in
	 * @return
	 */
	private double backwardPropagate(double in)
	{
		switch (type)
		{
			case TYPE_INPUT:
				return in;
			case TYPE_HIDDEN:
				return in * tanhSDerivative(in);
			case TYPE_OUTPUT:
				return -(in * tanhSDerivative(in));
		}
		return 0;
	}

	public double getForwardInputValue()
	{
		return mForwardInputValue;
	}

	public void setForwardInputValue(double mInputValue)
	{
		this.mForwardInputValue = mInputValue;
		setForwardOutputValue(mInputValue);
	}

	public double getForwardOutputValue()
	{
		return mForwardOutputValue;
	}

	private void setForwardOutputValue(double mInputValue)
	{
		this.mForwardOutputValue = forwardSigmoid(mInputValue);
	}

	public double getBackwardInputValue()
	{
		return mBackwardInputValue;
	}

	public void setBackwardInputValue(double mBackwardInputValue)
	{
		this.mBackwardInputValue = mBackwardInputValue;
		setBackwardOutputValue(mBackwardInputValue);
	}

	public double getBackwardOutputValue()
	{
		return mBackwardOutputValue;
	}

	private void setBackwardOutputValue(double input)
	{
		this.mBackwardOutputValue = backwardPropagate(input);
	}

}
