package com.myit;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

public class AppTest {

    @Test
    public void test(){
        ProcessEngineConfiguration engineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
        ProcessEngine processEngine = engineConfiguration.buildProcessEngine();


    }
}
