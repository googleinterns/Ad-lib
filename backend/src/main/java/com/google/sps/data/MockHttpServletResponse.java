// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

public class MockHttpServletResponse implements HttpServletResponse {
  public void sendError(int sc, String message) {
    System.out.println("HttpServletResponse error " + sc + ": " + message);
  }

  public void sendStatus(int sc) {
    System.out.println("HttpServletResponse status " + sc);
  }

  public void setContentType(String contentType) {
    System.out.println("HttpServletResponse content type: " + contentType);
  }

  public PrintWriter getWriter() {
    return new PrintWriter(System.out);
  }
}
