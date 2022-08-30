package com.harishkannarao.restdatarabbitmq.integration;

import com.harishkannarao.restdatarabbitmq.runner.SpringBootTestRunner;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class RestDataRabbitmqApplicationTests extends AbstractBaseIntegrationTest {

	@Test
	void contextLoads() {
		String applicationUrl = SpringBootTestRunner.getApplicationUrl();
		Assertions.assertThat(applicationUrl).isNotBlank();
	}

}
