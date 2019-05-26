package com.myit.dao;

import com.myit.model.VacationDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VacationDao {

    int insertVacationDTO(@Param("vacation") VacationDTO vacation);
}
