package com.scd.potatoeslb.spring.dao;

import java.util.List;
import com.scd.potatoeslb.data.Farmer;

public interface IFarmerDAO {
	Farmer getFarmerById(Long id);
	List<Farmer> getAllFarmers();
	boolean deleteFarmer(Long id);
	boolean updateFarmer(Farmer farmer);
	int createFarmer(Farmer farmer);
}

