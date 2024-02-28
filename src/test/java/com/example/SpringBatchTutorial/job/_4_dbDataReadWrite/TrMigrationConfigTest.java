package com.example.SpringBatchTutorial.job._4_dbDataReadWrite;


import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.example.SpringBatchTutorial.core.domain.accounts.AccountsRepository;
import com.example.SpringBatchTutorial.core.domain.orders.Orders;
import com.example.SpringBatchTutorial.core.domain.orders.OrdersRepository;

@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/schema-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@SpringBatchTest
@SpringBootTest(classes = {com.example.SpringBatchTutorial.SpringBatchTestConfig.class, TrMigrationConfig.class})
class TrMigrationConfigTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private OrdersRepository ordersRepository;

	@Autowired
	private AccountsRepository accountsRepository;

	@AfterEach
	public void cleanUpEach() {
		ordersRepository.deleteAll();
		accountsRepository.deleteAll();
	}

	@Test
	public void success_noData() throws Exception {
		// when
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();

		// then
		assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
		assertThat(0).isEqualTo(accountsRepository.count());
	}

	@Test
	public void success_existsDate() throws Exception {
		// given
		Orders order1 = new Orders(null, "kakao_gift", 15000, new Date());
		Orders order2 = new Orders(null, "naver_gift", 15000, new Date());

		ordersRepository.save(order1);
		ordersRepository.save(order2);

		// when
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
		assertThat(2).isEqualTo(accountsRepository.count());
	}
}
