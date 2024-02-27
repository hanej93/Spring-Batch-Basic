package com.example.SpringBatchTutorial.job._1_helloworld;

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
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

/**
 * desc: Hello World를 출력
 * run: --spring.batch.job.name=helloWorldJob
 */
// @Configuration
@RequiredArgsConstructor
public class HelloWorldJobConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	public Job helloWorldJob(Step helloWorldStep) {
		return new JobBuilder("helloWorldJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(helloWorldStep)
			.build();
	}

	@Bean
	@JobScope
	public Step helloWorldStep(Tasklet helloWorldTasklet) {
		return new StepBuilder("helloWorldStep", jobRepository)
			.tasklet(helloWorldTasklet, transactionManager)
			.build();
	}

	@Bean
	@StepScope
	public Tasklet helloWorldTasklet() {
		return (contribution, chunkContext) -> {
			System.out.println("Hello World Spring Batch");
			return RepeatStatus.FINISHED;
		};
	}
}
