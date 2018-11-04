package com.project.userExercise;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userExercise")
public class userExerciseController {

	@Autowired
	private userExerciseService userExerciseService;
	
	/*
	 * USE CASE: A user adds a single exercise (Not involved with plan)
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/addSingle")
	public void addUserSingleExercise(@RequestBody userAddEntry userAddEntry) {
		userExerciseService.addUserSingleExercise(userAddEntry);
	}
	
	/*
	 * 
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{userId}/{date}")
	public List<userEntry> getExercisesForDay(@PathVariable int userId, @PathVariable String date) {
		return userExerciseService.getExercisesForDay(userId, date);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/remove")
	public void removeUserExercise(@RequestBody userEntry userEntry) {
		userExerciseService.removeUserExercise(userEntry);
	}
	
	/*
	@RequestMapping(method = RequestMethod.POST, value = "/update")
	public void updateUserExercise(@RequestBody userEntry userEntry) {
		userExerciseService.updateUserExercise(userEntry);
	}*/
	
	/*
	@RequestMapping(method = RequestMethod.POST, value = "/complete")
	public void markComplete(@RequestBody userEntry userEntry) {
		userExerciseService.markComplete(userEntry);
	}*/
	
}
