package pt.ulisboa.tecnico.socialsoftware.tutor.quiz.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.test.web.servlet.MockMvc
import pt.ulisboa.tecnico.socialsoftware.tutor.config.WebSecurityConfig
import spock.lang.Specification
import spock.mock.DetachedMockFactory

@WebMvcTest(controllers = [QuizController])
class CreateQuizControllerMethodSpockTest extends Specification {
    @Autowired
    protected MockMvc mvc




    def 'create quiz with one question and two options'() {
        given:
        Map request = [
            "completed": true,
            "date": "2019-07-24T21:25:08.491Z",
            "id": 0,
            "questions": [
                "additionalProp1": [
                    "active": true,
                    "content": "string",
                    "difficulty": 0,
                    "image": [
                        "url": "string",
                        "width": 0
                    ],
                    "options": [
                            [
                                "content": "string",
                                "correct": true,
                                "option": 0
                            ]
                    ]
                ],
                "additionalProp2": [
                    "active": true,
                    "content": "string",
                    "difficulty": 0,
                    "image": [
                        "url": "string",
                        "width": 0
                    ],
                    "options": [
                            [
                                "content": "string",
                                "correct": true,
                                "option": 0
                            ]
                    ]
                ],
                "additionalProp3": [
                    "active": true,
                    "content": "string",
                    "difficulty": 0,
                    "image": [
                        "url": "string",
                        "width": 0
                    ],
                    "options": [
                            [
                                "content": "string",
                                "correct": true,
                                "option": 0
                            ]
                    ]
                ]
            ],
            "series": 0,
            "title": "string",
            "type": "string",
            "version": "string",
            "year": 0
        ]

        when:
        def results = mvc.perform(post('/quizzes').contentType(APPLICATION_JSON).content(toJson(request)))

        then:
        results.andExpect(status().isCreated())
    }


    @TestConfiguration
    static class StubConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        WebSecurityConfig webSecurityConfig() {
            return detachedMockFactory.Mock(WebSecurityConfig)
        }
    }
}
