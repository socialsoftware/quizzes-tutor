package pt.ulisboa.tecnico.socialsoftware.tutor.user.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User

@DataJpaTest
class ImportUsersTest extends SpockTest {

    def user

    def setup() {




    }

    def 'import users from csv file' () {
        when:
        userService.importListOfUsers(CSVFILE);
        and:
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        System.out.println(CSVFILE)
        then:
        1==1;
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
