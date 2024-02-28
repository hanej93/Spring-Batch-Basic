package com.example.SpringBatchTutorial.job._4_dbDataReadWrite;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.SpringBatchTutorial.core.domain.accounts.Accounts;
import com.example.SpringBatchTutorial.core.domain.accounts.AccountsRepository;
import com.example.SpringBatchTutorial.core.domain.orders.Orders;
import com.example.SpringBatchTutorial.core.domain.orders.OrdersRepository;

import lombok.RequiredArgsConstructor;

/**
 * desc: 주문 테이블 -> 정산 테이블 데이터 이관
 * run: --spring.batch.job.name=trMigrationJob
 */
@Configuration
@RequiredArgsConstructor
public class TrMigrationConfig {

	public static final int CHUNK_SIZE = 5;
	private final OrdersRepository ordersRepository;
	private final AccountsRepository accountsRepository;

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	public Job trMigrationJob(Step trMigrationStep) {
		return new JobBuilder("trMigrationJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(trMigrationStep)
			.build();
	}

	@Bean
	@JobScope
	public Step trMigrationStep(ItemReader trOrdersReader, ItemProcessor trOrdersProcessor, ItemWriter trOrdersWriter) {
		return new StepBuilder("trMigrationStep", jobRepository)
			.<Orders, Accounts>chunk(CHUNK_SIZE, transactionManager)
			.reader(trOrdersReader)
			// .writer(chunk -> {
			// 	chunk.forEach(System.out::println);
			// })
			.processor(trOrdersProcessor)
			.writer(trOrdersWriter)
			.build();
	}

	// @Bean
	// @StepScope
	// public RepositoryItemWriter<Accounts> trOrdersWriter() {
	// 	return new RepositoryItemWriterBuilder<Accounts>()
	// 		.repository(accountsRepository)
	// 		.methodName("save")
	// 		.build();
	// }

	@Bean
	@StepScope
	public ItemWriter<Accounts> trOrderWriter() {
		return chunk -> {
			List<? extends Accounts> items = chunk.getItems();
			items.forEach(item -> accountsRepository.save(item));
		};
	}

	@Bean
	@StepScope
	public ItemProcessor<Orders, Accounts> trOrdersProcessor() {
		return item -> new Accounts(item);
	}

	@Bean
	@StepScope
	public RepositoryItemReader<Orders> trOrdersReader() {
		return new RepositoryItemReaderBuilder<Orders>()
			.name("trOrdersReader")
			.repository(ordersRepository)
			.methodName("findAll")
			.pageSize(CHUNK_SIZE)
			.arguments(Arrays.asList())
			.sorts(Collections.singletonMap("id", Sort.Direction.ASC))
			.build();
	}
}
