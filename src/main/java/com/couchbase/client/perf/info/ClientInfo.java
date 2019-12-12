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

package com.couchbase.client.perf.info;

import com.couchbase.client.java.env.ClusterEnvironment;

import java.util.Map;
import java.util.TreeMap;

public class ClientInfo {

  private final String version;

  public ClientInfo(ClusterEnvironment env) {
    version = env.clientVersion().orElse("unknown");
  }

  public Map<String, Object> export() {
    Map<String, Object> export = new TreeMap<>();
    export.put("version", version);
    return export;
  }

}
