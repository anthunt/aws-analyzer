package com.anthunt.aws.network.utils;

public class Color {
	
	private String hex;
	private int r;
	private int g;
	private int b;
	private int alpha;
	
	public Color(String hex, int r, int g, int b, int alpha) {
		this.hex = hex;
		this.r = r;
		this.g = g;
		this.b = b;
		this.alpha = alpha;
	}
	
	public String getHex() {
		return this.hex;
	}
	
	public int getR() {
		return this.r;
	}
	
	public int getG() {
		return this.g;
	}
	
	public int getB() {
		return this.b;
	}
	
	public int getAlpha() {
		return this.alpha;
	}
	
}
