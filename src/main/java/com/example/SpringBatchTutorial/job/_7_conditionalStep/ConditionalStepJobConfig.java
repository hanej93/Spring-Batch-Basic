package com.example.SpringBatchTutorial.job._7_conditionalStep;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import lombok.RequiredArgsConstructor;

/**
 * desc: step 결과의 따른 다음 step 분기 처리
 * run param: --job.name=conditionalStepJob
 */
// @Configuration
@RequiredArgsConstructor
public class ConditionalStepJobConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	public Job conditionalStepJob() {
		return new JobBuilder("conditionalStepJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(conditionalStartStep())
				.on("FAILED").to(conditionalFailStep())
			.from(conditionalStartStep())
				.on("COMPLETED").to(conditionalCompletedStep())
			.from(conditionalStartStep())
				.on("*").to(conditionalAllStep())
			.end()
			.build();
	}

	@JobScope
	@Bean
	public Step conditionalStartStep() {
		return new StepBuilder("conditionalStartStep", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				System.out.println("conditional Start Step");
				return RepeatStatus.FINISHED;
				// throw new Exception("Exception!!");
			}, transactionManager)
			.build();
	}

	@JobScope
	@Bean
	public Step conditionalAllStep() {
		return new StepBuilder("conditionalAllStep", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				System.out.println("conditional All Step");
				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}

	@JobScope
	@Bean
	public Step conditionalFailStep() {
		return new StepBuilder("conditionalFailStep", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				System.out.println("conditional Fail Step");
				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}

	@JobScope
	@Bean
	public Step conditionalCompletedStep() {
		return new StepBuilder("conditionalCompletedStep", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				System.out.println("conditional Completed Step");
				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}
}