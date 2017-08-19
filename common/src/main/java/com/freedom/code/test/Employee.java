package com.freedom.code.test;

public class Employee {
	private String name;
	private int age;
	private int deptment;
	private double bonus;
	
	public Employee(String name,int age,int deptment,double bonus){
		this.name = name;
		this.age = age;
		this.deptment = deptment;
		this.bonus = bonus;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getDeptment() {
		return deptment;
	}
	public void setDeptment(int deptment) {
		this.deptment = deptment;
	}
	public double getBonus() {
		return bonus;
	}
	public void setBonus(double bonus) {
		this.bonus = bonus;
	}
	
}
