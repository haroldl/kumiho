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

import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.http.rest.RestHelper
import net.liftweb.json.JsonAST.{JString, JObject, JField}
import net.liftweb.mapper.By

import code.model.LogFileInfo

object LogFileRest extends RestHelper {

  serve {
    case XmlGet("logfile" :: name :: _, _) => renderXML(name)
    case JsonGet("logfile" :: name :: _, _) =>
      JObject(List(JField("a", JString("hello, restful world, " + name))))
  }

  private def renderXML(name: String) = {
    lookupLogFile(name) match {
      case Full(logFileInfo) => <div>
          Name: { logFileInfo.name } <br/>
          Filename: { logFileInfo.filename } <br/>
        </div>
      case _ => <div>
          Sorry, couldn't find a log file named { name }.
        </div>
    }
  }

  private def lookupLogFile(name: String): Box[LogFileInfo] = {
    LogFileInfo.find(By(LogFileInfo.name, name))
  }

}

