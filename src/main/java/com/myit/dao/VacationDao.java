package com.myit.dao;

import com.myit.model.VacationDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VacationDao {

    int insertVacationDTO(@Param("vacation") VacationDTO vacation);

    List<VacationDTO> queryApplyVacationDTO(@Param("userId") String userId);
}
