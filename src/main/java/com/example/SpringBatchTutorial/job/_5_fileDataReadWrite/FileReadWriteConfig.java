package com.example.SpringBatchTutorial.job._5_fileDataReadWrite;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.SpringBatchTutorial.job._5_fileDataReadWrite.dto.Player;
import com.example.SpringBatchTutorial.job._5_fileDataReadWrite.dto.PlayerYears;

import lombok.RequiredArgsConstructor;

/**
 * desc: 파일 읽고 쓰기
 * run: --spring.batch.job.name=fileReadWriteJob
 */
// @Configuration
@RequiredArgsConstructor
public class FileReadWriteConfig {

	public static final int CHUNK_SIZE = 5;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	public Job fileDataReadWriteJob(Step fileReadWriteStep) {
		return new JobBuilder("fileReadWriteJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(fileReadWriteStep)
			.build();
	}

	@Bean
	@JobScope
	public Step fileReadWriteStep(ItemReader playerItemReader, ItemProcessor playerItemProcessor, ItemWriter playerItemWriter) {
		return new StepBuilder("fileReadWriteStep", jobRepository)
			.<Player, PlayerYears>chunk(CHUNK_SIZE, transactionManager)
			.reader(playerItemReader)
			// .writer(chunk -> {
			// 	chunk.forEach(System.out::println);
			// })
			.processor(playerItemProcessor)
			.writer(playerItemWriter)
			.build();
	}

	@Bean
	@StepScope
	public ItemProcessor<Player, PlayerYears> playerItemProcessor() {
		return item -> new PlayerYears(item);
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Player> playerItemReader() {
		return new FlatFileItemReaderBuilder<Player>()
			.name("playerItemReader")
			.resource(new FileSystemResource("Players.csv"))
			.lineTokenizer(new DelimitedLineTokenizer())
			.fieldSetMapper(new PlayerFieldSetMapper())
			.linesToSkip(1)
			.build();
	}

	@Bean
	@StepScope
	public FlatFileItemWriter<PlayerYears> playerItemWriter() {
		BeanWrapperFieldExtractor<PlayerYears> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(new String[]{"ID", "lastName", "position", "yearsExperience"});
		fieldExtractor.afterPropertiesSet();

		DelimitedLineAggregator<PlayerYears> lineAggregator = new DelimitedLineAggregator<>();
		lineAggregator.setDelimiter(",");
		lineAggregator.setFieldExtractor(fieldExtractor);

		FileSystemResource outputResource = new FileSystemResource("player_output.txt");

		return new FlatFileItemWriterBuilder<PlayerYears>()
			.name("playItemWriter")
			.resource(outputResource)
			.lineAggregator(lineAggregator)
			.build();
	}
}
