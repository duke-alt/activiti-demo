package com.itheima.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * @author DuYaAn
 * @ClassName MyTaskListener.java
 * @createTime 2021年05月20日 11:18:00
 */
public class MyTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        if (delegateTask.getName().equals("创建出差申请") && delegateTask.getEventName().equals("create")) {
            //这里指定任务负责人
            delegateTask.setAssignee("张三");
        }
    }
}
