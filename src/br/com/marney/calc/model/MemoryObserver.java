package br.com.marney.calc.model;

@FunctionalInterface
public interface MemoryObserver {

	public void changeValue (String newValue);
}
