package com.example.SpringBatchTutorial.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
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

@Configuration
@RequiredArgsConstructor
public class HelloWorldJobConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	public Job helloWorldJob() {
		return new JobBuilder("helloWorldJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(helloWorldStep())
			.build();
	}

	@Bean
	@JobScope
	public Step helloWorldStep() {
		return new StepBuilder("helloWorldStep", jobRepository)
			.tasklet(helloWorldTasklet(), transactionManager)
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
