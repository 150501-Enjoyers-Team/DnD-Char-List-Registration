package com.unigroup.dndcharlist.config;

import com.unigroup.dndcharlist.utils.GoogleKeyKeeper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class GoogleScheduledTask implements SchedulingConfigurer {

    @Autowired
    private GoogleKeyKeeper tickService;

    @Bean
    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
                new Runnable() {
                    @Override
                    public void run() {

                        tickService.updateGoogleJwk();
                    }
                },
                new Trigger() {
                    @Override
                    public Instant nextExecution(TriggerContext triggerContext) {
                        Optional<Date> lastCompletionTime =
                                Optional.ofNullable(triggerContext.lastCompletionTime());
                        Instant nextTime = lastCompletionTime.orElseGet(Date::new).toInstant()
                                .plusSeconds(tickService.getDelay());
                        return nextTime;
                    }
                }
        );
    }
}
