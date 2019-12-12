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

import com.couchbase.client.core.util.Golang;
import com.electronwill.nightconfig.core.file.FileConfig;

import java.time.Duration;

public class RunnerConfig {

  private final String connectionString;
  private final String bucket;
  private final String username;
  private final String password;
  private final int numThreads;
  private final Duration warmupDuration;
  private final Duration runDuration;
  private final WorkloadConfig workloadConfig;

  public static RunnerConfig from(final FileConfig config) {
    return new RunnerConfig(
      config.get("connectionString"),
      config.get("bucket"),
      config.get("username"),
      config.get("password"),
      config.get("numThreads"),
      Golang.parseDuration(config.get("warmupDuration")),
      Golang.parseDuration(config.get("runDuration")),
      WorkloadConfig.defaults() // TODO
    );
  }

  public static RunnerConfig defaults() {
    return new RunnerConfig(
      "couchbase://127.0.0.1",
      "travel-sample",
      "Administrator",
      "password",
      1,
      Duration.ofSeconds(30),
      Duration.ofSeconds(60),
      WorkloadConfig.defaults()
    );
  }

  private RunnerConfig(final String connectionString, final String bucket, final String username, final String password,
                       final int numThreads, final Duration warmupDuration, final Duration runDuration,
                       final WorkloadConfig workloadConfig) {
    this.connectionString = connectionString;
    this.bucket = bucket;
    this.username = username;
    this.password = password;
    this.numThreads = numThreads;
    this.warmupDuration = warmupDuration;
    this.runDuration = runDuration;
    this.workloadConfig = workloadConfig;
  }

  public String connectionString() {
    return connectionString;
  }

  public String bucket() {
    return bucket;
  }

  public String username() {
    return username;
  }

  public String password() {
    return password;
  }

  public int numThreads() {
    return numThreads;
  }

  public Duration warmupDuration() {
    return warmupDuration;
  }

  public Duration runDuration() {
    return runDuration;
  }

  public WorkloadConfig workloadConfig() {
    return workloadConfig;
  }

  @Override
  public String toString() {
    return "RunnerConfig{" +
      "connectionString='" + connectionString + '\'' +
      ", bucket='" + bucket + '\'' +
      ", username='" + username + '\'' +
      ", numThreads=" + numThreads +
      ", warmupDuration=" + warmupDuration +
      ", runDuration=" + runDuration +
      ", workloadConfig=" + workloadConfig +
      '}';
  }
}
