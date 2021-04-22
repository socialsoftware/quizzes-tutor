package pt.ulisboa.tecnico.socialsoftware.apigateway


import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.PropertySource
import org.springframework.test.context.ActiveProfiles

@TestConfiguration
@PropertySource("classpath:application-test-int.properties")
class BeanConfigurationIT {

}