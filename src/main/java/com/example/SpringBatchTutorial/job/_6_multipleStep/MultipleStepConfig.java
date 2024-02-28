package com.example.SpringBatchTutorial.job._6_multipleStep;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.SpringBatchTutorial.job._5_fileDataReadWrite.PlayerFieldSetMapper;
import com.example.SpringBatchTutorial.job._5_fileDataReadWrite.dto.Player;
import com.example.SpringBatchTutorial.job._5_fileDataReadWrite.dto.PlayerYears;

import lombok.RequiredArgsConstructor;

/**
 * desc: 다중 step을 사용하기 및 step to step 데이터 전달
 * run: --spring.batch.job.name=multipleStepJob
 */
// @Configuration
@RequiredArgsConstructor
public class MultipleStepConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	public Job fileDataReadWriteJob() {
		return new JobBuilder("multipleStepJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(multipleStep1())
			.next(multipleStep2())
			.next(multipleStep3())
			.build();
	}

	@Bean
	@JobScope
	public Step multipleStep1() {
		return new StepBuilder("multipleStep1", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				System.out.println("step1");
				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}

	@Bean
	@JobScope
	public Step multipleStep2() {
		return new StepBuilder("multipleStep2", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				System.out.println("step2");

				ExecutionContext executionContext = chunkContext
					.getStepContext()
					.getStepExecution()
					.getJobExecution()
					.getExecutionContext();

				executionContext.put("someKey", "hello!");

				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}

	@Bean
	@JobScope
	public Step multipleStep3() {
		return new StepBuilder("multipleStep3", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				System.out.println("step3");

				ExecutionContext executionContext = chunkContext
					.getStepContext()
					.getStepExecution()
					.getJobExecution()
					.getExecutionContext();

				System.out.println(executionContext.get("someKey"));

				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}
}
