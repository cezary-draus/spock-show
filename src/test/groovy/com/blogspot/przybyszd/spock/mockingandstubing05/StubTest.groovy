package com.blogspot.przybyszd.spock.mockingandstubing05

import com.blogspot.przybyszd.spock.bean.PersonDao
import com.blogspot.przybyszd.spock.dto.Person
import org.springframework.dao.DataAccessException
import org.springframework.dao.DataRetrievalFailureException
import org.springframework.jdbc.core.JdbcTemplate
import spock.lang.Specification

class StubTest extends Specification {

    JdbcTemplate jdbcTemplate = Mock(JdbcTemplate)

    PersonDao personDao = new PersonDao(jdbcTemplate)

    def "should find one person"() {
        given:
            jdbcTemplate.queryForList(*_) >> [[first_name: "Jan", last_name: "Kowalski", age: 20]]
        expect:
            personDao.findByLastName("Kowalski") == [new Person("Jan", "Kowalski", 20)]
    }

    def "should find one person with stub"() {
        given:
            jdbcTemplate = Stub(JdbcTemplate)
            personDao = new PersonDao(jdbcTemplate)
            jdbcTemplate.queryForList(
                "select first_name, last_name, age from person where last_name = ?",
                ["Kowalski"]
            ) >> [[first_name: "Jan", last_name: "Kowalski", age: 20]]
        expect:
            personDao.findByLastName("Kowalski") == [new Person("Jan", "Kowalski", 20)]
    }

    def "should find one person with stub and implementation inside it"() {
        given:
            jdbcTemplate = Stub(JdbcTemplate) {
                queryForList(
                    "select first_name, last_name, age from person where last_name = ?",
                    ["Kowalski"]
                ) >> [[first_name: "Jan", last_name: "Kowalski", age: 20]]
            }
            personDao = new PersonDao(jdbcTemplate)
        expect:
            personDao.findByLastName("Kowalski") == [new Person("Jan", "Kowalski", 20)]
    }

    def "should find many times person"() {
        given:
            jdbcTemplate.queryForList(_, _) >> [[first_name: "Jan", last_name: "Kowalski", age: 20]]
        expect:
            personDao.findByLastName("Kowalski") == [new Person("Jan", "Kowalski", 20)]
            personDao.findByLastName("Kowalski") == [new Person("Jan", "Kowalski", 20)]
    }

    def "should find many times person 2"() {
        given:
            jdbcTemplate.queryForList(_, _) >> [[first_name: "Jan", last_name: "Kowalski", age: 20]] >> [[first_name: "Jan", last_name: "Kowalski", age: 25]]
        expect:
            personDao.findByLastName("Kowalski") == [new Person("Jan", "Kowalski", 20)]
            personDao.findByLastName("Kowalski") == [new Person("Jan", "Kowalski", 25)]
    }

    def "should find many times person 4"() {
        given:
            jdbcTemplate.queryForList(_, _) >>> [
                [[first_name: "Jan", last_name: "Kowalski", age: 20]],
                [[first_name: "Jan", last_name: "Kowalski", age: 15]]
            ]
        expect:
            personDao.findByLastName("Kowalski") == [new Person("Jan", "Kowalski", 20)]
            personDao.findByLastName("Kowalski") == [new Person("Jan", "Kowalski", 15)]
    }

    def "should throw exception on second find"() {
        given:
            jdbcTemplate.queryForList(_, _) >> [
                [first_name: "Jan", last_name: "Kowalski", age: 20]
            ] >> { throw new DataRetrievalFailureException("Cannot retrieve data") }
        expect:
            personDao.findByLastName("Kowalski") == [new Person("Jan", "Kowalski", 20)]
        when:
            personDao.findByLastName("Kowalski")
        then:
            thrown(DataAccessException)
    }

    def "should throw exception on second find 2"() {
        given:
            int counter = 0
            jdbcTemplate.queryForList(_, _) >> {
                if (counter == 0) {
                    ++counter
                    [[first_name: "Jan", last_name: "Kowalski", age: 20]]
                } else {
                    throw new DataRetrievalFailureException("Cannot retrieve data")
                }
            }
        expect:
            personDao.findByLastName("Kowalski") == [new Person("Jan", "Kowalski", 20)]
        when:
            personDao.findByLastName("Kowalski")
        then:
            thrown(DataAccessException)
    }


    def "should find one person and check invocation external with first parameter not null"() {
        when:
            List result = personDao.findByLastName("Kowalski")
        then:
            result == [new Person("Jan", "Kowalski", 20)]
            interaction {
                queryForListCalledOnceWithFirstName()
            }
    }

    void queryForListCalledOnceWithFirstName() {
        1 * jdbcTemplate.queryForList(!null, _) >> [[first_name: "Jan", last_name: "Kowalski", age: 20]]
    }
}
