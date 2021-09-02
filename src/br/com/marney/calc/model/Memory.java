package br.com.marney.calc.model;

import java.util.ArrayList;
import java.util.List;

public class Memory {
	
	private enum CommandType {
		CLEAR, NUM, DIV, MULT, SUB, ADD, EQL, DOT, PLUS_MINUS;
	}
	
	private static final Memory instance = new Memory();
	
	private String currentText = "";
	private String bufferText = "";
	private boolean changeValue = false;
	private CommandType lastCommand = null;
	private final List<MemoryObserver> observers = new ArrayList<>();
	
	private Memory() {
		
	}

	public static Memory getInstance() {
		return instance;
	}
	
	public void addObserver(MemoryObserver observer) {
		observers.add(observer);
	}

	public String getCurrentText() {
		return currentText.isEmpty() ? "0" : currentText;
	}
	
	public void command(String text) {
		
		CommandType commandType = detectCommandType(text);
		
		if(commandType == null) {
			return;
		} else if(commandType == CommandType.CLEAR) {
			currentText = "";
			bufferText = "";
			changeValue = false;
			lastCommand = null;
		} else if(commandType == CommandType.PLUS_MINUS && currentText.contains("-")) {
			currentText = currentText.substring(1);
		} else if(commandType == CommandType.PLUS_MINUS && !currentText.contains("-")) {
			currentText = "-" + currentText;
		} else if (commandType == CommandType.NUM || commandType == CommandType.DOT) {
			currentText = changeValue ? text : currentText + text;
			changeValue = false;
		} else {
			changeValue = true;
			currentText = getResult();
			bufferText = currentText;
			lastCommand = commandType;
		}
		
		observers.forEach(o -> o.changeValue(getCurrentText()));
	}

	private String getResult() {
		if(lastCommand == null || lastCommand == CommandType.EQL) {
			return currentText;
		}
		
		double bufferNumber = Double.parseDouble(bufferText);
		double currentNumber = Double.parseDouble(currentText);
		double result = 0;
		
		switch (lastCommand) {
		case ADD:
			result = bufferNumber + currentNumber;
			break;
		case SUB:
			result = bufferNumber - currentNumber;
			break;
		case MULT:
			result = bufferNumber * currentNumber;
			break;
		case DIV:
			result = bufferNumber / currentNumber;
			break;
		default:
			break;
		}
		
		String resultString = Double.toString(result);
		boolean integer = resultString.endsWith(".0");
		return integer ? resultString.replace(".0", "") : resultString;
	}

	private CommandType detectCommandType(String text) {
		
		if(currentText.isEmpty() && text.equals("0")) {
			return null;
		}
		
		try {
			Integer.parseInt(text);
			return CommandType.NUM;
		} catch (NumberFormatException e) {
			// When isn't a number
			if("AC".equals(text)) {
				return CommandType.CLEAR;
			} else if("/".equals(text)) {
				return CommandType.DIV;
			} else if("*".equals(text)) {
				return CommandType.MULT;
			} else if("+".equals(text)) {
				return CommandType.ADD;
			} else if("-".equals(text)) {
				return CommandType.SUB;
			} else if("±".equals(text)) {
				return CommandType.PLUS_MINUS;
			} else if("=".equals(text)) {
				return CommandType.EQL;
			} else if(".".equals(text) && !currentText.contains(".")) {
				return CommandType.DOT;
			}
		}
		return null;
	}
	
}
