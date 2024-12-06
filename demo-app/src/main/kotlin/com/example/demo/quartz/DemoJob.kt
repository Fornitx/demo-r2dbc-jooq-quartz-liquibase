package com.example.demo.quartz

import org.quartz.DisallowConcurrentExecution
import org.quartz.JobExecutionContext
import org.springframework.scheduling.quartz.QuartzJobBean

@DisallowConcurrentExecution
class DemoJob(private val service: DemoJobService) : QuartzJobBean() {
    override fun executeInternal(context: JobExecutionContext) = service.callJob()
}
