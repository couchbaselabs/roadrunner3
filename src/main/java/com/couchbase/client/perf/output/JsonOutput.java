/*
 * Copyright (c) 2019 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.couchbase.client.perf.output;

import com.couchbase.client.core.deps.org.HdrHistogram.Histogram;
import com.couchbase.client.core.json.Mapper;
import com.couchbase.client.perf.info.ClientInfo;
import com.couchbase.client.perf.workload.WorkloadResult;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public class JsonOutput implements Output {

  private final WorkloadResult workloadResult;
  private final ClientInfo clientInfo;

  public JsonOutput(WorkloadResult workloadResult, ClientInfo clientInfo) {
    this.workloadResult = workloadResult;
    this.clientInfo = clientInfo;
  }

  @Override
  public String export() {
    final Map<String, Object> output = new TreeMap<>();

    List<Double> wantedLatencies = Arrays.asList(10.0, 20.0, 30.0, 40.0, 50.0, 90.0, 95.0, 99.0, 99.9, 99.99, 99.999, 99.9999);

    final Map<String, Object> workload = new TreeMap<>();
    Map<String, Histogram> latencies = workloadResult.latencies();
    for (Map.Entry<String, Histogram> latency : latencies.entrySet()) {
      Map<String, Object> data = new TreeMap<>();
      Histogram histogram = latency.getValue();
      Map<String, Long> l = new TreeMap<>();

      for (double val : wantedLatencies) {
        l.put(Double.toString(val), TimeUnit.NANOSECONDS.toMicros(histogram.getValueAtPercentile(val)));
      }

      data.put("latencies", l);

      if (workloadResult.throughput().get(latency.getKey()) != null) {
        data.put("throughput", workloadResult.throughput().get(latency.getKey()));
      }

      data.put("max_latency", TimeUnit.NANOSECONDS.toMicros(histogram.getMaxValue()));
      data.put("min_latency", TimeUnit.NANOSECONDS.toMicros(histogram.getMinValue()));

      workload.put(latency.getKey(), data);
      workload.put("_unit", "µs");
    }
    output.put("workload", workload);

    Map<String, Object> info = new TreeMap<>();
    info.put("client", clientInfo.export());
    output.put("info", info);
    return Mapper.encodeAsStringPretty(output);
  }
}
