package com.example.SpringBatchTutorial.job._1_helloworld;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/schema-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@SpringBatchTest
@SpringBootTest(classes = {com.example.SpringBatchTutorial.SpringBatchTestConfig.class, HelloWorldJobConfig.class})
class HelloWorldJobConfigTest {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	public void success() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		Assertions.assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
	}
}