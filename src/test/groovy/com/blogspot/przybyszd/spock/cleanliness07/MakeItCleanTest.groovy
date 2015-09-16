package com.blogspot.przybyszd.spock.cleanliness07

import com.blogspot.przybyszd.spock.bean.PersonDao
import com.blogspot.przybyszd.spock.dto.Person
import org.springframework.jdbc.core.JdbcTemplate
import spock.lang.Specification

class MakeItCleanTest extends Specification {

    JdbcTemplate jdbcTemplate = Mock(JdbcTemplate)
    PersonDao personDao = new PersonDao(jdbcTemplate)

    def "should check person"() {
        when:
            Person result = new Person("Tom", "Smith", 20)
        then:
            result != null
            result.firstName == "Tom"
            result.lastName == "Smith"
            result.age == 20
    }

    def "should check person with boolean helper method"() {
        when:
            Person result = new Person("Tom", "Smith", 20)
        then:
            checkPerson(result, "Tom", "Smith", 20)
    }

    def "should check person with assert helper method"() {
        when:
            Person result = new Person("Tom", "Smith", 20)
        then:
            checkPersonWithAssert(result, "Tom", "Smith", 20)
    }

    def "should set first name, last name and age 1"() {
        when:
            Person person = new Person(firstName: "Bob", lastName: "Smith", age: 40)
        then:
            with(person) {
                firstName == "Bob"
                lastName == "Smith"
                age == 40
            }
    }

    def "should use interaction block"() {
        when:
            List result = personDao.findByLastName("Kowalski")
        then:
            result == [new Person("Jan", "Kowalski", 20)]
            interaction {
                int interactions = 1
                interactions * jdbcTemplate.queryForList(!null, _) >> [[first_name: "Jan", last_name: "Kowalski", age: 20]]
            }
    }

    def "should use interaction block 2"() {
        when:
            List result = personDao.findByLastName("Kowalski")
        then:
            result == [new Person("Jan", "Kowalski", 20)]
            interaction {
                jdbcIsCalled()
            }
    }

    def "should use find"() {
        given:
            List given = [new Person("Jan", "Kowalski", 20), new Person("Spock", "Kowalski", 2014)]
        expect:
            given.find { it.firstName == "Kowalski" }?.age == 20

    }



    void jdbcIsCalled() {
        1 * jdbcTemplate.queryForList(!null, _) >> [[first_name: "Jan", last_name: "Kowalski", age: 20]]
    }

    boolean checkPerson(Person person, String firstName, String lastName, int age) {
        person != null &&
                person.firstName == firstName &&
                person.lastName == lastName &&
                person.age == age
    }


    void checkPersonWithAssert(Person person, String firstName, String lastName, int age) {
        assert person != null
        assert person.firstName == firstName
        assert person.lastName == lastName
        assert person.age == age
    }
}
