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

package com.couchbase.client.perf.writer;

import com.couchbase.client.perf.output.Output;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileWriter implements Writer {

  private final String filename;

  public FileWriter() {
    this.filename = "output-" + System.currentTimeMillis() + ".html";
  }

  @Override
  public void write(Output output) {
    System.out.println("Writing result to " + filename);

    try {
      Files.write(Paths.get(filename), output.export().getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException("Could not write file", e);
    }
  }

}
