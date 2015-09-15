package com.blogspot.przybyszd.spock.mockingandstubing05

import spock.lang.Specification

class SpyTest extends Specification {

    List list = Spy(ArrayList, constructorArgs: [10])

    def "should use spy on list"() {
        given:
            list.add(1) >> {
                callRealMethod()
            }
            list.size() >> 10
        when:
            list.add(1)
        then:
            list.size() == 10
            list.get(0) == 1
    }
}
