package com.cczj.urlservice.task;

import com.cczj.framework.aop.TaskRunManager;
import com.cczj.urlservice.service.url.UrlService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@EnableScheduling
public class VisitUrlTask {

    @Resource
    private UrlService urlService;

    @Scheduled(fixedRate = 10000)
    @TaskRunManager(name = "保存短链访问记录")
    public void saveVisit(){
        this.urlService.receiveMessage();
    }


    //每5分钟执行一次
    @Scheduled(cron = "0 0/5 * * * ?")
    @TaskRunManager(name = "清理过期短链")
    public void timeout(){
        String expireTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.urlService.timeout(expireTime);
    }
}
