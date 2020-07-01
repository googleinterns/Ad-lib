// Copyright 2020 Google LLC
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

package main.java.com.google.sps.notifiers;

public abstract class Notifier {

  private final String name;

  /** @param name String representing the name of person to be notified */
  public Notifier(String name) {
    this.name = name;
  }

  /**
   * Getter function that returns the notifications recipient's name.
   *
   * @return String representing the notifications recipients name
   */
  public String getName() {
    return this.name;
  }
  /** Method that will be used to notify the user via the notifier */
  public abstract void notifyUser();
}
