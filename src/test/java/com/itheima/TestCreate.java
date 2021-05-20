package com.itheima;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

/**
 * @author DuYaAn
 * @ClassName TestCreate.java
 * @createTime 2021年05月19日 10:26:00
 */
public class TestCreate {
    /**
     * 使用activiti提供的默认方式创建mysql表
     */
    @Test
    public void testCreateTable(){
        //需要使用activiti提供的工具类 ProcessEngine ,使用方法getDefaultProcessEngine
        //getDefaultProcessEngine会默认从resource下读取名字为activiti.cfg.xml的文件
        //创建processEngine时,就会创建mysql的表
        //getDefaultProcessEngine方法内部在运行时会创建对象
       /* ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();*/

        //使用自定义方式创建 配置文件的名字可以自定义 bean名字也可以自定义

        ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.
                createProcessEngineConfigurationFromResource("activiti.cfg.xml","processEngineConfiguration");
        //获取流程引擎对象
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();

        System.out.println(processEngine);
    }
}
