package com.blogspot.przybyszd.spock.flow02

import com.blogspot.przybyszd.spock.dto.Person
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

@Stepwise
class SpockFLowTest extends Specification {

    @Shared
    StringWriter writer

    Person person

    def setupSpec() {
        println "In setup spec"
        writer = new StringWriter()
    }

    def setup() {
        println "In setup"
        person = new Person(firstName: "Tom", lastName: "Smith", age: 21)
    }

    def cleanup() {
        println "In cleanup"
        person = null
    }

    def cleanupSpec() {
        println "In cleanup spec"
        writer.close()
    }

    def "should check firstName"() {
        setup:
            println "setup in test"
            println "should check firstName"
        expect:
            person.firstName == "Tom"
        cleanup:
            println "Cleanup after test"
    }

    def "should check lastName"() {
        println "should check lastName"
        expect:
            person.lastName == "Smith"
    }

    def "should check age"() {
        println "should check age"
        expect:
            person.age == 21
    }

    @Unroll
    def "should use full flow"() {
        given: "alias for setup"
            println "Setup fixture"
        when:
            println "stimulus"
        then:
            println "check results"
        and:
            println "and check another results $parameter"
        when:
            println "second stimulus"
        then:
            println "check second results"
        expect:
            println "next expect"
        cleanup:
            println "cleanup"
        where:
            parameter << ["the first parameter", "the second parameter"]

    }

}
