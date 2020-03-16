/*
 * Copyright 2016 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.harvard.gis.hhypermap.bop.ingest

import com.fasterxml.jackson.annotation.JsonProperty
import edu.harvard.gis.hhypermap.bop.kafkastreamsbase.DwConfiguration
import org.apache.solr.client.solrj.SolrRequest
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient
import org.apache.solr.common.util.NamedList
import org.hibernate.validator.constraints.NotEmpty
import java.util.concurrent.atomic.AtomicReference
import javax.validation.constraints.Pattern


class IngestDwConfiguration : DwConfiguration() {

  // Kafka stuff:

  @JsonProperty @NotEmpty
  var kafkaSourceTopic: String? = null

  @JsonProperty
  val kafkaOffsetCommitIntervalMs: Long = 30000

  /** governed by KafkaConsumer */
  @JsonProperty("kafkaConsumer")
  var kafkaConsumerConfig: MutableMap<String,Any> = mutableMapOf()

  override fun postBuild() {
    super.postBuild()
    kafkaConsumerConfig = replaceHyphenatedKeys(kafkaConsumerConfig)
  }

  // Solr stuff:

  @JsonProperty @NotEmpty @Pattern(regexp = "http://(.+)/")
  var solrConnectionString: String? = null

  @JsonProperty @NotEmpty
  var solrCollection: String? = null

  /** Concurrent threads to load Solr. Be careful! */
  @JsonProperty
  var solrThreadCount = 1

  /** Max size of pending queue before blocks. */
  @JsonProperty
  var solrQueueSize = 1000

  /** If this many milliseconds pass, the streamed connection to Solr will end. */
  @JsonProperty
  var solrPollQueueTimeMs = 1000 // SolrJ ConcurrentUpdateSolrClient defaults to 250

  fun newSolrClient() : ConcurrentUpdateSolrClient {
    return ConcurrentUpdateSolrClient.Builder(solrConnectionString).withQueueSize(solrQueueSize).withThreadCount(solrThreadCount).build();
  }

}