package com.blogspot.przybyszd.spock.async

import spock.lang.Specification
import spock.util.concurrent.PollingConditions

class PoolingConditionTest extends Specification {

    def "should wait for job done"() {
        given:
            PollingConditions pollingConditions = new PollingConditions(timeout: 5)
            String response = null
        when:
            new Thread(new Runnable() {
                @Override
                void run() {
                    Thread.sleep(1000)
                    response = '42'
                }
            }).start()
        then:
            pollingConditions.eventually {
                response == '42'
            }
    }
}