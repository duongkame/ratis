/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ratis.server.protocol;

import org.apache.ratis.proto.RaftProtos.ReadIndexRequestProto;
import org.apache.ratis.proto.RaftProtos.ReadIndexReplyProto;
import org.apache.ratis.proto.RaftProtos.AppendEntriesReplyProto;
import org.apache.ratis.proto.RaftProtos.AppendEntriesRequestProto;
import org.apache.ratis.util.ReferenceCountedObject;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface RaftServerAsynchronousProtocol {

  /**
   * It is recommended to override {@link #appendEntriesAsync(ReferenceCountedObject)} instead.
   * Then, it does not have to override this method.
   */
  default CompletableFuture<AppendEntriesReplyProto> appendEntriesAsync(AppendEntriesRequestProto request)
      throws IOException {
    throw new UnsupportedOperationException();
  }

  /**
   * A referenced counted request is submitted from a client for processing.
   * Implementations of this method should retain the request, process it and then release it.
   * The request may be retained even after the future returned by this method has completed.
   *
   * @return a future of the reply
   * @see ReferenceCountedObject
   */
  default CompletableFuture<AppendEntriesReplyProto> appendEntriesAsync(
      ReferenceCountedObject<AppendEntriesRequestProto> requestRef) throws IOException {
    // Default implementation for backward compatibility.
    try {
      return appendEntriesAsync(requestRef.retain())
          .whenComplete((r, e) -> requestRef.release());
    } catch (Exception e) {
      requestRef.release();
      throw e;
    }
  }

  CompletableFuture<ReadIndexReplyProto> readIndexAsync(ReadIndexRequestProto request)
      throws IOException;
}
