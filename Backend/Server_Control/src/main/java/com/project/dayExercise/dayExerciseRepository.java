package com.project.dayExercise;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author Ryan Ingram
 *
 */
public interface dayExerciseRepository extends JpaRepository<dayExercise, Integer>, CrudRepository<dayExercise, Integer> {
	/**
	 *
	 * @param userId
	 * @return
	 */
	public List<dayExercise> findAllByUserId(int userId);
	/**
	 *
	 * @param userId
	 * @param planName
	 * @return
	 */
	public List<dayExercise> findAllByUserIdAndPlanName(int userId, String planName);
	/**
	 * 
	 * @param userId
	 */
    @Transactional
    public void deleteByUserId(int userId);
	/**
	 * Updates workout item in SQL table.
	 * @param userId
	 * @param planName
	 * @param complete
	 * @param exerciseId
	 * @param sets
	 * @param reps
	 */
	@Modifying
	@Transactional
	@Query(value = "UPDATE day_exercise de SET de.sets = :sets, de.reps = :reps, de.complete = :complete WHERE(de.user_id = :userId AND de.plan_name = :planName AND de.exercise_id = :exerciseId)", nativeQuery = true)
	public void markComplete(
			@Param("userId") int userId,
			@Param("planName") String planName,
			@Param("complete") boolean complete,
			@Param("exerciseId") int exerciseId,
			@Param("sets") int sets,
			@Param("reps") int reps);
	/**
	 * Delete workout item from SQL table.
	 * @param userId
	 * @param planName
	 * @param exerciseId
	 * @param sets
	 * @param reps
	 */
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM day_exercise WHERE (user_id = :userId AND plan_name = :planName AND exercise_id = :exerciseId AND sets = :sets AND reps = :reps)", nativeQuery = true)
	public void remove(
			@Param("userId") int userId,
			@Param("planName") String planName,
			@Param("exerciseId") int exerciseId,
			@Param("sets") int sets,
			@Param("reps") int reps);
	//@Query(value = "DELETE FROM day_exercise de WHERE (de.user_id = :userId AND de.plan_name = :planName AND de.exercise_id = :exerciseId AND de.sets = :sets AND de.reps = :reps)", nativeQuery = true)
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO day_exercise VALUES(entry, :complete, :exerciseId, :planName, :reps, :sets, :userId)", nativeQuery = true)
	public void addExercise(
			@Param("userId") int userId,
			@Param("planName") String planName,
			@Param("complete") boolean complete,
			@Param("exerciseId") int exerciseId,
			@Param("sets") int sets,
			@Param("reps") int reps);
	

}
