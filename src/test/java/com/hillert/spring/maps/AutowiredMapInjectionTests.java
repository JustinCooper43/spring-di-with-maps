package com.hillert.spring.maps;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This test illustrates some weird dependency injection behavior when injecting Maps.
 *
 * See the Spring Reference Documentation at: https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-autowired-annotation-qualifiers
 *
 * In this example we have 1 Bean named "myMapBean" that returns a {@code Map<String, Object>}.
 *
 * The first test asserts that "myMapBean" has only 1 entry (Expected). Spring will inject the actual myMapBean, not the underlying map.
 *
 * If you change (reverse) the method order, then both tests will fail.
 *
 * Both tests reference the same autowired dependency but do in fact get a different dependency injected.
 */
@SpringJUnitConfig(classes = AutowiredMapInjectionTests.SpringConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AutowiredMapInjectionTests {

	private final Map<String, Object> myMapBean; // Field has to be final.

	@Autowired
	public AutowiredMapInjectionTests(@Qualifier("myMapBean") Map<String, Object> myMapBean) {
		this.myMapBean = myMapBean;
	}

	/**
	 * This test passes correctly.
	 */
	@Test
	@Order(1)
	public void injectMapViaAutowiredAnnotation1() {
		assertThat(this.myMapBean).isNotNull();
		assertThat(this.myMapBean).isInstanceOf(Map.class);
		assertThat(this.myMapBean).hasSize(1); // Should return only 1 bean "myMapBean", not the underlying map.
	}

	/**
	 * This test should fail but does not.
	 */
	@Test
	@Order(2)
	public void injectMapViaAutowiredAnnotation2() {
		assertThat(this.myMapBean).isNotNull();
		assertThat(this.myMapBean).isInstanceOf(Map.class);
		assertThat(this.myMapBean).hasSize(2); // Should fail!! and return only 1 bean "myMapBean", not the underlying map.
	}

	@Configuration
	static class SpringConfig {
		@Bean
		public Map<String, Object> myMapBean() {
			final Map<String, Object> myMapBean = new HashMap<>();
			myMapBean.put("hello", "world");
			myMapBean.put("foo", "bar");
			return myMapBean;
		}
	}
}
