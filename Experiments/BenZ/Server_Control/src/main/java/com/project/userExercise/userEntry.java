package com.project.userExercise;


// Finalized Ryan Ingram

// Object for every interaction with front end.
// Difference with userExercise object is exercise to exerciseId and day needs to be added

public class userEntry {

	private int userId;
	private String planName;
	private String exercise;
	private int sets;
	private int reps;
	private String saveDate;
	
	public userEntry() {
		
	}
	
	public userEntry(int userId, String planName, String exercise, int sets, int reps, String saveDate) {
		super();
		this.userId = userId;
		this.planName = planName;
		this.exercise = exercise;
		this.sets = sets;
		this.reps = reps;
		this.saveDate = saveDate;
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getPlanName() {
		return planName;
	}
	public void setPlanName(String planName) {
		this.planName = planName;
	}
	public String getExercise() {
		return exercise;
	}
	public void setExercise(String exercise) {
		this.exercise = exercise;
	}
	public int getSets() {
		return sets;
	}
	public void setSets(int sets) {
		this.sets = sets;
	}
	public int getReps() {
		return reps;
	}
	public void setReps(int reps) {
		this.reps = reps;
	}
	public String getSaveDate() {
		return saveDate;
	}
	public void setSaveDate(String saveDate) {
		this.saveDate = saveDate;
	}
	
	
}
