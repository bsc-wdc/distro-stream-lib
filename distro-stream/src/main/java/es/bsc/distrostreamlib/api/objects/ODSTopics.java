package es.bsc.distrostreamlib.api.objects;

import java.util.Arrays;
import java.util.List;


public class ODSTopics {

    protected static final String TOPIC_REGULAR_MESSAGES = "regular-messages";
    protected static final String TOPIC_SYSTEM_MESSAGES = "system-messages";

    protected static final List<String> ALL_TOPICS = Arrays.asList(TOPIC_REGULAR_MESSAGES, TOPIC_SYSTEM_MESSAGES);


    public ODSTopics() {
        // Nothing to do
    }

}
