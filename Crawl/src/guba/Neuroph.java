/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package org.neuroph.contrib.samples.timeseries;
package guba;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.core.learning.DataSet;
import org.neuroph.core.learning.DataSetRow;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * 
 * @author zoran
 */
public class Neuroph implements LearningEventListener {
	NeuralNetwork neuralNet;
	DataSet trainingSet;
	DataSet testingSet;

	// public static void main(String[] args) {
	// Neuroph tts = new Neuroph();
	//
	// tts.train();
	// tts.testNeuralNetwork();
	// }

	public void train(String inputFilePath) {
		inputFilePath = inputFilePath.substring(inputFilePath.length() - 17,
				inputFilePath.length());
		 System.out.println("path:"+inputFilePath+" "+inputFilePath.length());
		String inputFileName_train = Neuroph.class.getResource(inputFilePath)
				.getFile();
//		System.out.println("inputFileName_train:"+inputFileName_train);
		String inputFileName_test = Neuroph.class.getResource(inputFilePath)
				.getFile();
		// create MultiLayerPerceptron neural network
		neuralNet = new MultiLayerPerceptron(TransferFunctionType.TANH, 11, 16,
				1);
		MomentumBackpropagation learningRule = (MomentumBackpropagation) neuralNet
				.getLearningRule();
		learningRule.setLearningRate(0.2);
		learningRule.setMomentum(0.5);
		learningRule.setMaxError(0.8);
		// learningRule.addObserver(this);
		learningRule.addListener(this);

		// create training set from file
		trainingSet = DataSet.createFromFile(inputFileName_train, 11, 1, ",");
		testingSet = DataSet.createFromFile(inputFileName_test, 11, 1, ",");
		// train the network with training set
		neuralNet.learn(trainingSet);

		// add observer here

		// System.out.println("Done training.");

	}

	/**
	 * Prints network output for the each element from the specified training
	 * set.
	 * 
	 * @param neuralNet
	 *            neural network
	 * @param trainingSet
	 *            training set
	 */
	public void testNeuralNetwork() {
		// System.out.println("Testing network...");
		List<Double> desireOutputs = new LinkedList();
		List<Double> outputs = new LinkedList();
		for (DataSetRow trainingElement : testingSet.getRows()) {
			neuralNet.setInput(trainingElement.getInput());
			neuralNet.calculate();
			double[] networkOutput = neuralNet.getOutput();

			System.out.print("Input: "
					+ Arrays.toString(trainingElement.getInput()));
			System.out.print(" desireOutput: "
					+ Arrays.toString(trainingElement.getDesiredOutput()));
			System.out.println(" Output: " + Arrays.toString(networkOutput));
			desireOutputs.add(trainingElement.getDesiredOutput()[0]);
			outputs.add(networkOutput[0]);
		}
		// 存储训练结果至TrainResult.csv
		String file2Path = Main.BasicPath + "TrainSet.xls";
		String file3Path = Main.BasicPath + "TrainResult.xls";
		File file3 = new File(file3Path);
		NeuralNetworkData neuralNetworkData = new NeuralNetworkData();
		try {
			neuralNetworkData.copyExcel(file2Path, file3Path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// double[] desireOutput = trainingElement.getDesiredOutput();
		// double[] outputs = networkOutput;
		Workbook rwb;
		try {
			rwb = Workbook.getWorkbook(file3);
			WritableWorkbook wwb = Workbook.createWorkbook(file3, rwb);// copy
			WritableSheet ws = wwb.getSheet(0);

			for (int i = 0; i < outputs.size(); i++) {
				double output = outputs.get(i);
				Label label = new Label(13, i, "" + output);// 将预测结果添加到最后一列
				try {
					ws.addCell(label);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			wwb.write();
			try {
				wwb.close();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (BiffException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//将预测结果存入MongoDB中的result collection中
		DBObject result = new BasicDBObject();
		
		
	}

	// @Override
	// public void update(Observable arg0, Object arg1) {
	// SupervisedLearning rule = (SupervisedLearning)arg0;
	// System.out.println( "Training, Network Epoch " +
	// rule.getCurrentIteration() + ", Error:" + rule.getTotalNetworkError());
	// }

	@Override
	public void handleLearningEvent(LearningEvent event) {
		SupervisedLearning rule = (SupervisedLearning) event.getSource();
		System.out.println("Training, Network Epoch "
				+ rule.getCurrentIteration() + ", Error:"
				+ rule.getTotalNetworkError());
	}

}
