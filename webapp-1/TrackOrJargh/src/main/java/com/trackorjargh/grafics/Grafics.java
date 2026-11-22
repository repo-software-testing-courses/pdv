package com.trackorjargh.grafics;

public class Grafics{
	private String name;
	private double points;
	
	public Grafics() {
	}

	public Grafics(String name, double points) {
		this.name = name;
		this.points = points;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPoints() {
		return points;
	}

	public void setPoints(double points) {
		this.points = points;
	}
}
