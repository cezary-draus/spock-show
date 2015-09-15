package com.blogspot.przybyszd.spock.mockingandstubing05
import com.blogspot.przybyszd.spock.bean.PersonDao
import com.blogspot.przybyszd.spock.dto.Person
import com.blogspot.przybyszd.spock.configuration.Slow
import org.springframework.jdbc.core.JdbcTemplate
import spock.lang.Specification

@Slow
class MockTest extends Specification {

    JdbcTemplate jdbcTemplate = Mock(JdbcTemplate)

    PersonDao personDao = new PersonDao(jdbcTemplate)

    def "should persist one person"() {
        given:
            Person person = new Person("John", "Smith", 20)
        when:
            personDao.persist(person)
        then:
            1 * jdbcTemplate.execute("Insert into person (first_name, last_name, age) values ('John', 'Smith', 20)")
    }

    def "should call DB with valid inserts when persist persons"() {
        given:
            List<Person> persons = [new Person("John", "Smith", 20), new Person("Jan", "Kowalski", 15)]
        when:
            personDao.persist(persons)
        then:
            1 * jdbcTemplate.execute("Insert into person (first_name, last_name, age) values ('John', 'Smith', 20)")
            1 * jdbcTemplate.execute("Insert into person (first_name, last_name, age) values ('Jan', 'Kowalski', 15)")
    }

    def "should call DB twice when persist 2 persons"() {
        given:
            List<Person> persons = [new Person("John", "Smith", 20), new Person("Jan", "Kowalski", 15)]
        when:
            personDao.persist(persons)
        then:
            2 * jdbcTemplate.execute(_)
    }

    def "should persist many persons 3"() {
        given:
            List<Person> persons = [new Person("John", "Smith", 20), new Person("Jan", "Kowalski", 15)]
        when:
            personDao.persist(persons)
        then:
            (1..3) * jdbcTemplate.execute(_)
    }

    def "should persist many persons 4"() {
        given:
            List<Person> persons = [new Person("John", "Smith", 20), new Person("Jan", "Kowalski", 15)]
        when:
            personDao.persist(persons)
        then:
            (1.._) * jdbcTemplate.execute(_)
    }

    def "should persist many persons 5"() {
        given:
            List<Person> persons = [new Person("John", "Smith", 20), new Person("Jan", "Kowalski", 15)]
        when:
            personDao.persist(persons)
        then:
            _ * jdbcTemplate.execute(_)
    }

    def "should persist many persons 6"() {
        given:
            List<Person> persons = [new Person("John", "Smith", 20), new Person("Jan", "Kowalski", 15)]
        when:
            personDao.persist(persons)
        then:
            2 * _.execute(_)
    }

    def "should persist many persons 7"() {
        given:
            List<Person> persons = [new Person("John", "Smith", 20), new Person("Jan", "Kowalski", 15)]
        when:
            personDao.persist(persons)
        then:
            2 * jdbcTemplate._(_)
    }

    def "should persist many persons 8"() {
        given:
            List<Person> persons = [new Person("John", "Smith", 20), new Person("Jan", "Kowalski", 15)]
        when:
            personDao.persist(persons)
        then:
            2 * jdbcTemplate./exe.*/(_)
    }

    def "should persist many persons 9"() {
        given:
            List<Person> persons = [new Person("John", "Smith", 20), new Person("Jan", "Kowalski", 15)]
        when:
            personDao.persist(persons)
        then:
            2 * jdbcTemplate.execute {
                String sql -> sql.endsWith("('John', 'Smith', 20)") || sql.endsWith("('Jan', 'Kowalski', 15)")
            }
    }

    def "should persist many persons in order"() {
        given:
            List<Person> persons = [new Person("John", "Smith", 20), new Person("Jan", "Kowalski", 15)]
        when:
            personDao.persist(persons)
        then:
            1 * jdbcTemplate.execute("Insert into person (first_name, last_name, age) values ('John', 'Smith', 20)")
        then:
            1 * jdbcTemplate.execute("Insert into person (first_name, last_name, age) values ('Jan', 'Kowalski', 15)")
    }

    def "should persist many persons with mock and its implementation"() {
        given:
            jdbcTemplate = Mock(JdbcTemplate) {
                2 * execute {
                    String sql -> sql.endsWith("('John', 'Smith', 20)") || sql.endsWith("('Jan', 'Kowalski', 15)")
                }
            }
            personDao = new PersonDao(jdbcTemplate)
            List<Person> persons = [new Person("John", "Smith", 20), new Person("Jan", "Kowalski", 15)]
        expect:
            personDao.persist(persons)
    }

    def "should find one person and check invocation"() {
        when:
            List result = personDao.findByLastName("Kowalski")
        then:
            result == [new Person("Jan", "Kowalski", 20)]
            1 * jdbcTemplate.queryForList(_, _) >> [[first_name: "Jan", last_name: "Kowalski", age: 20]]
    }

    def "should find one person and check invocation with any parameters"() {
        when:
            List result = personDao.findByLastName("Kowalski")
        then:
            result == [new Person("Jan", "Kowalski", 20)]
            1 * jdbcTemplate.queryForList(*_) >> [[first_name: "Jan", last_name: "Kowalski", age: 20]]
    }

    def "should find one person and check invocation with second parameter not Smith"() {
        when:
            List result = personDao.findByLastName("Kowalski")
        then:
            result == [new Person("Jan", "Kowalski", 20)]
            1 * jdbcTemplate.queryForList(_, !(["Smith"] as Object[])) >> [[first_name: "Jan", last_name: "Kowalski", age: 20]]
    }

    def "should find one person and check invocation with first parameter not null"() {
        when:
            List result = personDao.findByLastName("Kowalski")
        then:
            result == [new Person("Jan", "Kowalski", 20)]
            1 * jdbcTemplate.queryForList(!null, _) >> [[first_name: "Jan", last_name: "Kowalski", age: 20]]
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

    def "should find one person and check invocation external with first parameter not null 2"() {
        when:
            List result = personDao.findByLastName("Kowalski")
        then:
            result == [new Person("Jan", "Kowalski", 20)]
            interaction {
                int interactions = 1
                interactions * jdbcTemplate.queryForList(!null, _) >> [[first_name: "Jan", last_name: "Kowalski", age: 20]]
            }
    }

    void queryForListCalledOnceWithFirstName() {
        1 * jdbcTemplate.queryForList(!null, _) >> [[first_name: "Jan", last_name: "Kowalski", age: 20]]
    }
}
