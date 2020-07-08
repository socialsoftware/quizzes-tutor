package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

@DataJpaTest
class ConfirmRegistrationTest extends SpockTest {



	def "user confirms registration successfully" () {

	}

	def "registration confirmation unsuccessful" () {
		// user doesn't exist
		// user is already active
		// password is empty
		// password is null
	}

}
