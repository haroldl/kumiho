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
package code.snippet

import net.liftweb.common.Logger
import net.liftweb.http.{S, SHtml}
import net.liftweb.http.js.JsCmds.SetValById

import code.comet.LogServer

object LogFilePost extends Logger {

  def render = SHtml.onSubmit(s => {
    SetValById("field1", "hello")
    SetValById("field2", s)
    SetValById("new_log_record", s)
  })

}
