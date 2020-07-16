package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

@DataJpaTest
class DeleteExternalInactiveUsersTest extends SpockTest{

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration { }

    def "there are no incative external users, and it is tried to delete inactive external users" (){

    }

    def "there are some inactive external users and deletes them" (){
        
    }
}
