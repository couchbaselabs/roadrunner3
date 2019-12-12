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

import com.couchbase.client.perf.info.ClientInfo;
import com.couchbase.client.perf.workload.WorkloadResult;

import java.nio.file.Files;
import java.nio.file.Paths;

public class HtmlOutput implements Output {

  private final JsonOutput jsonOutput;

  public HtmlOutput(WorkloadResult result, ClientInfo clientInfo) {
    this.jsonOutput = new JsonOutput(result, clientInfo);
  }

  @Override
  public String export() {
    try {
      String template = new String(Files.readAllBytes(Paths.get(getClass().getResource("/template.html").toURI())));
      return template.replace("$$VALUES$$", jsonOutput.export());
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}
