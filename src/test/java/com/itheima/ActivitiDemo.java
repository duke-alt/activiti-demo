package com.itheima;

import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @author DuYaAn
 * @ClassName ActivitiDemo.java
 * @createTime 2021年05月19日 13:39:00
 */
public class ActivitiDemo {
    @Test
    public void testDeployment() {
        //1.创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.得到RepositoryService实例
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3.使用RepositoryService进行部署  添加bpmn资源和 png资源
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("bpmn/evection.bpmn").
                addClasspathResource("bpmn/evection.png").name("出差申请流程")
                .deploy();
        //4.输出部署信息
        System.out.println("流程部署id:" + deployment.getId());
        System.out.println("流程部署名称:" + deployment.getName());
    }

    /**
     * 压缩包部署方式
     */
    @Test
    public void deployProcessByZip() {
        // 定义zip输入流
        InputStream inputStream = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream(
                        "bpmn/evection.zip");
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        // 获取repositoryService
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine
                .getRepositoryService();
        // 流程部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
        System.out.println("流程部署id：" + deployment.getId());
        System.out.println("流程部署名称：" + deployment.getName());
    }

    /**
     * 启动流程实例
     */
    @Test
    public void testStartProcess() {
        //1、创建ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2、获取RunTimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //3、根据流程定义Id启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myEvection");
        //输出内容
        System.out.println("流程定义id:" + processInstance.getProcessInstanceId());
        System.out.println("流程实例id:" + processInstance.getId());
        System.out.println("当前活动id:" + processInstance.getActivityId());
    }

    /**
     * 查询个人待执行的任务
     */
    @Test
    public void testFindPersonalTaskList() {
        //获取流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取任务相关的service taskService
        TaskService taskService = processEngine.getTaskService();
        //根据流程key和任务的负责人 查询任务
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey("myEvection")   //流程key
                .taskAssignee("zhangsan")  //查询的负责人
                .list();
        for (Task task : taskList) {
            System.out.println("流程实例id=" + task.getProcessInstanceId());
            System.out.println("任务id=" + task.getId());
            System.out.println("任务的负责人=" + task.getAssignee());
            System.out.println("任务的名称=" + task.getName());
        }
    }

    /**
     * 完后个人任务
     */
    @Test
    public void completeTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取任务相关的service taskService
        TaskService taskService = processEngine.getTaskService();
        //根据流程key和任务的负责人 查询任务
        //获取jerry - myEvection 对应的任务
        /*Task task = taskService.createTaskQuery()
                .processDefinitionKey("myEvection")   //流程key
                .taskAssignee("jerry")  //查询的负责人
                .singleResult();*/
      /*  Task task = taskService.createTaskQuery()
                .processDefinitionKey("myEvection")   //流程key
                .taskAssignee("jack")  //查询的负责人
                .singleResult();*/
        Task task = taskService.createTaskQuery()
                .processDefinitionKey("myEvection")   //流程key
                .taskAssignee("rose")  //查询的负责人
                .singleResult();
       /* Task task = taskService.createTaskQuery()
                .processDefinitionKey("myEvection") //流程Key
                .taskAssignee("zhangsan")  //要查询的负责人
                .singleResult();*/

        System.out.println("流程实例id=" + task.getProcessInstanceId());
        System.out.println("任务id=" + task.getId());
        System.out.println("任务的负责人=" + task.getAssignee());
        System.out.println("任务的名称=" + task.getName());
        //完成jerry的任务
        taskService.complete(task.getId());
    }

    /**
     * 查询流程的定义
     */

    @Test
    public void queryProcessDefinition() {
        //获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //得到ProcessDefinitionQuery对象
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        //查询出当前所有的流程定义
        //条件:processDefinitionKey = evection
        //orderByProcessDefinitionVersion 按照版本排序 desc倒序 返回一个list集合
        List<ProcessDefinition> definitionList = processDefinitionQuery.processDefinitionKey("myEvection")
                .orderByProcessDefinitionVersion()
                .desc().list();
        for (ProcessDefinition processDefinition : definitionList) {
            System.out.println("流程定义 id=" + processDefinition.getId());
            System.out.println("流程定义 name=" + processDefinition.getName());
            System.out.println("流程定义 key=" + processDefinition.getKey());
            System.out.println("流程定义 Version=" + processDefinition.getVersion());
            System.out.println("流程部署ID =" + processDefinition.getDeploymentId());
        }
    }

    /**
     * 流程的删除
     */
/*    @Test
    public void deleteDeployment(){
        //流程部署id
        String deployment = "1";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //通过流程引擎获取repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //删除流程的定义,如果该流程定义已有流程实例启动则删除是出错
        repositoryService.deleteDeployment(deployment);
        //设置true 级联删除流程定义,即使该流程有流程实例启动也可以删除,设置为FALSE非级别删除方式
        //repositoryService.deleteDeployment(deployment,true);
    }*/

    /**
     * 通过流程定义对象获取流程定义资源，获取bpmn和png
     */
    @Test
    public void deleteDeployment() {
        //获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //根据部署id,删除部署信息,如果想要级联删除,可以添加第二个参数,true
        repositoryService.deleteDeployment("1");
    }

    public void queryBpmnFile() throws IOException {
        //1.得到引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.获取repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //3.得到查询器:ProcessDefinitionQuery,设置查询条件,得到想要的流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myEvection")
                .singleResult();
        //4.通过流程定义信息,得到部署
        String deploymentId = processDefinition.getDeploymentId();
        //5.通过repositoryService的方法,实现读取图片信息和bpmn信息
        //png流
        InputStream pngInput = repositoryService.getResourceAsStream(deploymentId, processDefinition.getResourceName());
        //bpmn流
        InputStream bpmnInput = repositoryService.getResourceAsStream(deploymentId, processDefinition.getResourceName());
        //6.构造outputstream流
        File file_png = new File("d:/evectionflow01.png");
        File file_bpmn = new File("d:/evectionflow01.bpmn");
        FileOutputStream bpmnOut = new FileOutputStream(file_bpmn);
        FileOutputStream pngOut = new FileOutputStream(file_png);
        //7.输入流,输出流的转换
        IOUtils.copy(pngInput, pngOut);
        IOUtils.copy(bpmnInput, bpmnOut);
        //8.关闭流
        pngInput.close();
        bpmnInput.close();
        pngOut.close();
        bpmnOut.close();
    }

    /**
     * 查看历史信息
     */
    @Test
    public void findHistoryInfo() {
        //获取引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取historyService
        HistoryService historyService = processEngine.getHistoryService();
        //获取actinst表的查询对象
        HistoricActivityInstanceQuery instanceQuery = historyService.createHistoricActivityInstanceQuery();
        //查询actinst表,条件:根据InstanceId查询
        //instanceQuery.processInstanceId("2501");
        //查询actinst表,条件:根据DefinitionId查询
        instanceQuery.processDefinitionId("myEvection:1:4");
        //增加排序操作,orderByHistoryActivityInstanceStartTime 根据开始时间排序asc升序
        instanceQuery.orderByHistoricActivityInstanceStartTime().asc();
        //查询所有内容
        List<HistoricActivityInstance> activityInstanceList = instanceQuery.list();
        //输出
        for (HistoricActivityInstance hi : activityInstanceList) {
            System.out.println(hi.getActivityId());
            System.out.println(hi.getActivityName());
            System.out.println(hi.getProcessDefinitionId());
            System.out.println(hi.getProcessInstanceId());
            System.out.println("<==========================>");
        }
    }

    /**
     * 启动流程实例,添加businessKey
     */
    @Test
    public void addBusinessKey() {
        //1.得到ProcessEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //2.得到RuntimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //3.启动流程实例,同时还要指定业务标识businessKey,也就是出差申请单id,这里是1001
        ProcessInstance processInstance = runtimeService.
                startProcessInstanceByKey("myEvection", "1001");
        //4.输出相关信息
        System.out.println("业务id==" + processInstance.getBusinessKey());
    }

    /**
     * 查询流程实例
     */
    @Test
    public void queryProcessInstance() {
        //流程定义
        String processDefinitionKey = "evection";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取runtimeService
        RuntimeService runtimeService = processEngine.getRuntimeService();
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processDefinitionKey)
                .list();
        for (ProcessInstance processInstance : list) {
            System.out.println("---------------");
            System.out.println("流程实例id:" + processInstance.getProcessInstanceId());
            System.out.println("所属流程定义id:" + processInstance.getProcessDefinitionId());
            System.out.println("是否执行完成:" + processInstance.isEnded());
            System.out.println("是否暂停:" + processInstance.isSuspended());
            System.out.println("当前活动标识:" + processInstance.getActivityId());
        }
    }

    /**
     * 全部流程实例挂起与激活
     */
    @Test
    public void suspendAllProcessInstance() {
        //获取processEngine
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        //获取repositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        //查询流程定义对象
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("myEvection").singleResult();
        //得到当前流程定偶的实例是否为暂停状态
        boolean suspended = processDefinition.isSuspended();
        //流程定义id
        String processDefinitionId = processDefinition.getId();
        //判断是否为暂停
        if (suspended) {
            //如果是暂停,可以执行激活操作,参数1:流程定义id ,参数2:是否激活, 参数3:激活时间
            repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
            System.out.println("流程定义:" + processDefinitionId + ",已激活");
        } else {
            //如果是激活状态,可以暂停,参数1:流程定义id ,参数2:是否激活, 参数3:激活时间
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
            System.out.println("流程定义:" + processDefinitionId + ",已挂起");
        }


    }

    /**
     * 单个实例挂起和激活
     */
    @Test
    public void suspendSingleProcessInstance() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //查询流程定义的对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId("30001")
                .singleResult();
        //得到当前流程定义的实例是否都为暂停状态
        boolean suspended = processInstance.isSuspended();
        //流程定义id
        String processDefinitionId = processInstance.getId();
        //判断是否为暂停
        if (suspended) {
            //如果是暂停,可以执行激活操作,参数:流程定义id
            runtimeService.activateProcessInstanceById(processDefinitionId);
            System.out.println("流程定义:" + processDefinitionId + ".已激活");
        } else {
            //如果是激活状态,可以暂停,参数:流程定义id
            runtimeService.suspendProcessInstanceById(processDefinitionId);
            System.out.println("流程定义:" + processDefinitionId + ",已挂起");
        }
    }

    /**
     * 测试完成个人任务
     */
   /* @Test
    public void completTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        Task task = taskService.createTaskQuery().processInstanceId("30001").taskAssignee("zhangsan").singleResult();
        System.out.println("流程实例id=" + task.getProcessInstanceId());
        System.out.println("任务Id=" + task.getId());
        System.out.println("任务负责人=" + task.getAssignee());
        System.out.println("任务名称=" + task.getName());
        taskService.complete(task.getId());
    }*/

    /**
     * 设置流程负责人
     */
    @Test
    public void assigneeUEL() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //设置assignee的取值,用户可以再界面上设置流程的执行
        Map<String, Object> assigneeMap = new HashMap<>();
        assigneeMap.put("assignee0", "张三");
        assigneeMap.put("assignee1", "李经理");
        assigneeMap.put("assignee2", "王总经理");
        assigneeMap.put("assignee3", "赵财务");
        //启动流程实例,同时还要设置流程定义的assignee的值
        runtimeService.startProcessInstanceByKey("myEvection", assigneeMap);
        System.out.println(processEngine.getName());

    }

    /**
     * 查询当前个人待执行的任务
     */
    @Test
    public void findPersonTaskList() {
        // 流程定义key
        String processDefinitionKey = "myEvection1";
        // 任务负责人
        String assignee = "张三";
        // 获取TaskService
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey(processDefinitionKey)
                .includeProcessVariables()
                .taskAssignee(assignee)
                .list();
        for (Task task : taskList) {
            System.out.println("----------------------------");
            System.out.println("流程实例id： " + task.getProcessInstanceId());
            System.out.println("任务id： " + task.getId());
            System.out.println("任务负责人： " + task.getAssignee());
            System.out.println("任务名称： " + task.getName());
        }
    }

    /**
     * 关联 businessKey
     */
    @Test
    public void findProcessInstance() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        //查询流程定义的对象
        Task task = taskService.createTaskQuery()
                .processInstanceId("myEvection")
                .taskAssignee("张三")
                .singleResult();
        //使用task对象获取实例id
        String processInstanceId = task.getProcessInstanceId();
        //使用实例id获取流程实例对象
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        //使用processInstance,得到businessKey
        String businessKey = processInstance.getBusinessKey();
        System.out.println("businessKey = " + businessKey);
    }

    /**
     * 完成任务,判断当前用户是否有权限
     */
    @Test
    public  void completTask(){
        String taskId = "15005";
        String assignee = "张三";
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        TaskService taskService = processEngine.getTaskService();
        /*
        完成任务钱,需要校验该负责人可以完成当前的任务
        检验方法: 根据任务id和任务负责人查询当前任务,如果查到该用户有权限,就完成
         */
        Task task = taskService.createTaskQuery().taskId(taskId).taskAssignee(assignee).singleResult();
        if (task != null){
            taskService.complete(taskId);
            System.out.println("任务完成");
        }
    }
}
