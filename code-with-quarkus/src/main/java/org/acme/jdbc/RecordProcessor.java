package org.acme.jdbc;

import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class RecordProcessor implements Processor {

    static Logger log = LoggerFactory.getLogger(RecordProcessor.class);

    @Override
    @ApplicationScoped
    public void process(Exchange msg) {
        log.trace("Processing msg {}", msg);
        Map<String, Object> record = msg.getIn().getBody(Map.class);
        log.info("Processing record {}", record);
        // Do something useful with this record.
    }
}
