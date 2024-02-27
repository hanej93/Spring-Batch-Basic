package com.example.SpringBatchTutorial.job._2_validatedParam;

import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.SpringBatchTutorial.job._2_validatedParam.validator.FileParamValidator;

import lombok.RequiredArgsConstructor;

/**
 * desc: 파일 이름 파라미터 전달 그리고 검증
 * run: --spring.batch.job.name=validatedParamJob fileName=test.csv
 */
// @Configuration
@RequiredArgsConstructor
public class ValidatedParamJobConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	public Job validatedParamJob(Step validatedParamStep) {
		return new JobBuilder("validatedParamJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			// .validator(new FileParamValidator())
			.validator(multipleValidator())
			.start(validatedParamStep)
			.build();
	}

	private CompositeJobParametersValidator multipleValidator() {
		CompositeJobParametersValidator validator = new CompositeJobParametersValidator();
		validator.setValidators(Arrays.asList(new FileParamValidator()));
		return validator;
	}

	@Bean
	@JobScope
	public Step validatedParamStep(Tasklet validatedParamTasklet) {
		return new StepBuilder("validatedParamStep", jobRepository)
			.tasklet(validatedParamTasklet, transactionManager)
			.build();
	}

	@Bean
	@StepScope
	public Tasklet validatedParamTasklet(@Value("#{jobParameters['fileName']}") String fileName) {
		return (contribution, chunkContext) -> {
			System.out.println("fileName = " + fileName);
			System.out.println("Validated Param Tasklet");
			return RepeatStatus.FINISHED;
		};
	}
}
