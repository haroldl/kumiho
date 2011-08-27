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
package code.daemon

import org.specs.Specification

import code.comet.StartRecord
import code.comet.AppendRecord

class LogDaemonTest extends Specification {

  "LogDaemon statics" should {
    "recognize that timestamps start log records" in {
      val record = """2011/08/24 15:01:10.319 ERROR [MyClassName] [Thread-379] [xyz]
"""
      LogDaemon.timestampHandler("foo", record) must be equalTo(StartRecord("foo", record))
    }

    "recognize timestamps with dashes" in {
      val record = """2011-08-24 15:39:55 HTMLManager: list: Listing contexts for virtual host 'localhost'
"""
      LogDaemon.timestampHandler("foo", record) must be equalTo(StartRecord("foo", record))
    }

    "recognize timestamps even without the newline" in {
      val record = """2011/08/24 15:01:10.319 ERROR [MyClassName] [Thread-379] [xyz]"""
      LogDaemon.timestampHandler("foo", record) must be equalTo(StartRecord("foo", record))
    }

    "recognize that non-timestamps continue a log record" in {
      val record = """  at com.linkedin.MyClassName.doSomethingCool(MyClassName.java:123)"""
      LogDaemon.timestampHandler("foo", record) must be equalTo(AppendRecord("foo", record))
    }

    "replace environment variables in file names" in {
      LogDaemon.replaceEnvVariables("$HOME") must be equalTo(System.getenv("HOME"))
      LogDaemon.replaceEnvVariables("$HOME/abc") must be equalTo(System.getenv("HOME") + "/abc")
    }
  }
}

