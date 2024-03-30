package com.longjiang;

import co.elastic.clients.elasticsearch.watcher.Schedule;
import org.junit.jupiter.api.Test;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class STest {
    @Autowired
    private Scheduler scheduler;
    @Test
    public void testDeleteJob(){
        try {
            boolean reslut = scheduler.deleteJob(new JobKey("LongJiangJob", "LongJiangJobGroup"));
            System.out.println(reslut);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

}
