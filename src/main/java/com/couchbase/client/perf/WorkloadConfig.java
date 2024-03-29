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

import java.util.Collections;
import java.util.Map;

public class WorkloadConfig {

  private final String name;

  private final Map<String, Object> properties;

  public static WorkloadConfig defaults() {
    return new WorkloadConfig("readwrite", Collections.emptyMap());
  }

  private WorkloadConfig(String name, Map<String, Object> properties) {
    this.name = name;
    this.properties = properties;
  }

  public String name() {
    return name;
  }

  public Map<String, Object> properties() {
    return properties;
  }

}
