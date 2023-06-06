package me.zhengjie.modules.quartz.task;

import lombok.extern.slf4j.Slf4j;
import me.zhengjie.modules.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @Author: XDF
 * @DateTime: 2023/5/26 20:05
 **/

//@Configuration      //主要用于标记配置类，兼备Component的效果。
//@EnableScheduling   // 开启定时任务
@Slf4j
@Service
public class SaticScheduleTask {

    @Autowired
    private UserService userService;

//    //添加定时任务
//    @Scheduled(cron = "0/5 * * * * ?")
//    //或直接指定时间间隔，例如：5秒
//    //@Scheduled(fixedRate=5000)
//    private void configureTasks() {
//        System.err.println("执行静态定时任务时间: " + LocalDateTime.now());
//    }

    //添加定时任务
//    @Scheduled(cron = "0 0 0 * * ?")
    //或直接指定时间间隔，例如：5秒
    //@Scheduled(fixedRate=5000)
    public void restSig() {
        try {
            userService.updateSigSate();
            log.info("重置签到");
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
