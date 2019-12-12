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

package com.couchbase.client.perf.workload;

import com.couchbase.client.core.deps.org.HdrHistogram.Histogram;

import java.util.Map;

public class WorkloadResult {

  private final Map<String, Map<Long, Integer>> throughput;
  private final Map<String, Histogram> latencies;

  public WorkloadResult(final Map<String, Histogram> latencies, final Map<String, Map<Long, Integer>> throughput) {
    this.latencies = latencies;
    this.throughput = throughput;
  }

  public Map<String, Histogram> latencies() {
    return latencies;
  }

  public Map<String, Map<Long, Integer>> throughput() {
    return throughput;
  }

  @Override
  public String toString() {
    return "WorkloadResult{" +
      "latencies=" + latencies +
      "throughput=" + throughput +
      '}';
  }
}
