package com.longjiang.Config;

import com.longjiang.quartz.LongJiangJob;
import com.longjiang.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {
    //@Bean
    public JobDetailFactoryBean LongJiangJobDetail(){
        JobDetailFactoryBean factoryBean=new JobDetailFactoryBean();
        factoryBean.setJobClass(LongJiangJob.class);
        factoryBean.setName("LongJiangJob");
        factoryBean.setGroup("LongJiangJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }
    //@Bean
    public SimpleTriggerFactoryBean LongJiangTrigger(JobDetail LongJiangJobDetail){
        SimpleTriggerFactoryBean factoryBean=new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(LongJiangJobDetail);
        factoryBean.setName("LongJiangTrigger");
        factoryBean.setGroup("LongJiangGroup");
        factoryBean.setRepeatInterval(3000);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail(){
        JobDetailFactoryBean factoryBean=new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("PostScoreJob");
        factoryBean.setGroup("HLJJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }
    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail){
        SimpleTriggerFactoryBean factoryBean=new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshJobDetailTrigger");
        factoryBean.setGroup("HLJGroup");
        factoryBean.setRepeatInterval(1000*60*5);
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }
}
