/*
 *  Copyright 2002-2019 Barcelona Supercomputing Center (www.bsc.es)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package es.bsc.distrostreamlib.api.objects;

/**
 * Monitored Kafka topics.
 */
public class ODSTopics {

    protected static final String TOPIC_REGULAR_MESSAGES_PREFIX = "regular-messages";
    protected static final String TOPIC_SYSTEM_MESSAGES = "system-messages";


    private ODSTopics() {
        // Nothing to do
    }

}
