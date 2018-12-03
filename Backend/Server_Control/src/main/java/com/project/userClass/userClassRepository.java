package com.project.userClass;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface userClassRepository extends CrudRepository<userClass, Integer> {

	
	public List<userClass> findAllByUserId(int userId);
	
	public List<userClass> findAllByClassId(int classId);
	
	public void deleteByUserId(int userId);
}
