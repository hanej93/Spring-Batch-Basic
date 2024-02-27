package com.example.SpringBatchTutorial.job._3_jobListener;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

/**
 * desc: Hello World를 출력
 * run: --spring.batch.job.name=jobListenerJob
 */
// @Configuration
@RequiredArgsConstructor
public class JobListenerConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	public Job jobListenerJob(Step jobListenerStep) {
		return new JobBuilder("jobListenerJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.listener(new JobLoggerListener())
			.start(jobListenerStep)
			.build();
	}

	@Bean
	@JobScope
	public Step jobListenerStep(Tasklet jobListenerTasklet) {
		return new StepBuilder("jobListenerStep", jobRepository)
			.tasklet(jobListenerTasklet, transactionManager)
			.build();
	}

	@Bean
	@StepScope
	public Tasklet jobListenerTasklet() {
		return (contribution, chunkContext) -> {
			System.out.println("job listener Tasklet");
			throw new RuntimeException();
			// return RepeatStatus.FINISHED;
		};
	}
}
