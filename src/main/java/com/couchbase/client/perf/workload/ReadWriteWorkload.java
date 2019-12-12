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

import com.couchbase.client.core.deps.io.netty.util.collection.LongObjectHashMap;
import com.couchbase.client.core.deps.org.HdrHistogram.Histogram;
import com.couchbase.client.core.deps.org.LatencyUtils.LatencyStats;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.perf.RunnerConfig;
import com.couchbase.client.perf.WorkloadConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ReadWriteWorkload implements Workload {

  public final LongObjectHashMap<Integer> readOpsPerSecond;
  public final LongObjectHashMap<Integer> writeOpsPerSecond;
  public final LongObjectHashMap<Integer> combinedOpsPerSecond;

  public static String NAME = "readwrite";

  private final WorkloadConfig config;
  private final Collection collection;

  private final LatencyStats readLatencies = new LatencyStats();
  private final LatencyStats writeLatencies = new LatencyStats();
  private final LatencyStats combinedLatencies = new LatencyStats();

  private volatile boolean measure = false;
  private final long startOffset;

  public ReadWriteWorkload(Collection collection, RunnerConfig runnerConfig) {
    this.config = runnerConfig.workloadConfig();
    this.collection = collection;
    this.readOpsPerSecond = new LongObjectHashMap<>((int) runnerConfig.runDuration().getSeconds());
    this.writeOpsPerSecond = new LongObjectHashMap<>((int) runnerConfig.runDuration().getSeconds());
    this.combinedOpsPerSecond = new LongObjectHashMap<>((int) runnerConfig.runDuration().getSeconds());
    this.startOffset = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime());
  }

  @Override
  public void execute() {
    String id = "airline_10";

    long start = System.nanoTime();
    GetResult result = collection.get(id);
    long end = System.nanoTime();
    if (measure) {
      recordOp(true);
      readLatencies.recordLatency(end - start);
      combinedLatencies.recordLatency(end - start);
    }

    start = System.nanoTime();
    collection.upsert(id, result.contentAsObject());
    end = System.nanoTime();
    if (measure) {
      recordOp(false);
      writeLatencies.recordLatency(end - start);
      combinedLatencies.recordLatency(end - start);
    }
  }

  private void recordOp(boolean read) {
    LongObjectHashMap<Integer> m = read ? readOpsPerSecond : writeOpsPerSecond;

    long now = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()) - startOffset;
    Integer value  = m.get(now);
    if (value == null) {
      m.put(now, (Integer) 1);
    } else {
      m.put(now, (Integer) (value + 1));
    }

    value = combinedOpsPerSecond.get(now);
    if (value == null) {
      combinedOpsPerSecond.put(now, (Integer) 1);
    } else {
      combinedOpsPerSecond.put(now, (Integer) (value + 1));
    }
  }

  @Override
  public WorkloadResult complete() {
    Map<String, Histogram> latencies = new HashMap<>();
    latencies.put("read", readLatencies.getIntervalHistogram());
    latencies.put("write", writeLatencies.getIntervalHistogram());
    latencies.put("combined", combinedLatencies.getIntervalHistogram());

    Map<String, Map<Long, Integer>> throughput = new HashMap<>();
    throughput.put("combined", combinedOpsPerSecond);
    throughput.put("read", readOpsPerSecond);
    throughput.put("write", writeOpsPerSecond);
    return new WorkloadResult(latencies, throughput);
  }

  @Override
  public void measure(boolean measure) {
    this.measure = measure;
  }

}
