package com.example.demo.quartz

import org.quartz.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class QuartzConfig {
    @Bean
    fun jobDetail() = newJobDetail("Qrtz_JobDetail1", DemoJob::class.java)

    @Bean
    fun demoJobService() = DemoJobService()

    @Bean
    fun demoJob(service: DemoJobService) = DemoJob(service)

    @Bean
    fun trigger1(jobDetail: JobDetail) = newTrigger(jobDetail, "Qrtz_Trigger1", Duration.ofSeconds(1))

    companion object {
        private fun newJobDetail(name: String, jobClass: Class<out Job>): JobDetail =
            JobBuilder.newJob()
                .ofType(jobClass)
                .storeDurably()
                .withIdentity(name)
                .build()

        private fun newTrigger(jobDetail: JobDetail, name: String, interval: Duration): SimpleTrigger =
            TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(name)
                .withSchedule(
                    SimpleScheduleBuilder.simpleSchedule()
                        .repeatForever()
                        .withIntervalInMilliseconds(interval.toMillis())
                )
                .build()
    }
}
