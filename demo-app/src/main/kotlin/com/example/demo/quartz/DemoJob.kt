package com.example.demo.quartz

import org.quartz.DisallowConcurrentExecution
import org.quartz.Job
import org.quartz.JobExecutionContext

@DisallowConcurrentExecution
class DemoJob(private val service: DemoJobService) : Job {
    override fun execute(context: JobExecutionContext) {
        service.callJob()
    }
}
