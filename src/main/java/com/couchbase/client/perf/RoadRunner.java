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

package com.couchbase.client.perf;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.env.ClusterEnvironment;
import com.couchbase.client.perf.info.ClientInfo;
import com.couchbase.client.perf.output.HtmlOutput;
import com.couchbase.client.perf.workload.Workload;
import com.couchbase.client.perf.workload.WorkloadFactory;
import com.couchbase.client.perf.workload.WorkloadResult;
import com.couchbase.client.perf.writer.FileWriter;
import com.couchbase.client.perf.writer.Writer;
import com.electronwill.nightconfig.core.file.FileConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.couchbase.client.java.ClusterOptions.clusterOptions;

public class RoadRunner {

  public static void main(final String... args) throws Exception {
    CommandLine options = parseArgs(args);

    RunnerConfig config;
    if (options.hasOption("config")) {
      config = RunnerConfig.from(FileConfig.of(options.getOptionValue("config")));
    } else {
      config = RunnerConfig.defaults();
    }
    System.out.println(config);

    ClusterEnvironment env = ClusterEnvironment.builder().build();
    Cluster cluster = Cluster.connect(
      config.connectionString(),
      clusterOptions(config.username(), config.password()).environment(env)
    );
    Bucket bucket = cluster.bucket(config.bucket());
    Collection collection = bucket.defaultCollection();

    ExecutorService executor = Executors.newFixedThreadPool(config.numThreads());

    final AtomicBoolean running = new AtomicBoolean(true);

    System.out.println("Starting the workload");
    Workload workload = WorkloadFactory.create(collection, config);

    for (int i = 0; i < config.numThreads(); i++) {
      executor.submit(() -> {
        while (running.get()) {
          workload.execute();
        }
      });
    }

    long start = System.nanoTime();
    boolean startedMeasure = false;
    while (true) {
      Thread.sleep(1000);
      long end = System.nanoTime();
      Duration runtime = Duration.ofNanos(end - start);

      if (!startedMeasure && runtime.getSeconds() >= config.warmupDuration().getSeconds()) {
        System.out.println("Starting the recording now");
        startedMeasure = true;
        workload.measure(true);
      }

      if (runtime.getSeconds() >= (config.runDuration().getSeconds() + config.warmupDuration().getSeconds())) {
        System.out.println("Time elapsed, stopping");
        break;
      }
    }

    running.set(false);
    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.DAYS);

    WorkloadResult workloadResult = workload.complete();
    cluster.disconnect();
    env.shutdown();

    HtmlOutput output = new HtmlOutput(workloadResult, new ClientInfo(cluster.environment()));

    Writer writer = new FileWriter();
    writer.write(output);
  }

  private static CommandLine parseArgs(final String... args) throws ParseException {
    return new DefaultParser().parse(
      new Options().addOption("c", "config", true, "The config file path"),
      args
    );
  }

}
