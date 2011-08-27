/* * Copyright 2011 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package code

object Config {

  /**
   * When we hit EOF, how long do we wait before trying to read more, in ms.
   */
  val readDelay = 50

  /**
   * Should the records be displayed top-down? If not, then the newest records
   * will appear at the top of the page.
   */
  val topDown = true

  /**
   * Test to see if we should display the log record.
   */
  def logRecordFilter(record: (String,String)): Boolean = true

}

