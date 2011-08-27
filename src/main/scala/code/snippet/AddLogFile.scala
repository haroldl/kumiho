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

import scala.xml.NodeSeq

import net.liftweb.common.{Full, Logger}
import net.liftweb.http.RequestVar
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Noop
import net.liftweb.http.js.JsCmds.Replace
import net.liftweb.util.BindHelpers._

import code.daemon.LogDaemon
import code.model.LogFileInfo

object AddLogFile extends Logger {

  def addModel(name: String, filename: String, recordtype: String): LogFileInfo = {
    val logFile = LogFileInfo.create
    logFile.name(name)
    logFile.filename(filename)
    logFile.recordtype(recordtype)
    logFile.save()
    debug("Saved New LogFileInfo")
    logFile
  }

  def render = {
    var name = ""
    var filename = ""
    var recordtype = ""
    def process(): JsCmd = {
      val logFileInfo = addModel(name, filename, recordtype)
      LogDaemon ! logFileInfo
      Replace("logfiles", Controls.getLogFileMarkup)
    }

    "name=name" #> SHtml.text(name, name = _, "id" -> "name", "maxLength" -> "40") &
    "name=filename" #> SHtml.text(filename, filename = _, "id" -> "filename", "maxLength" -> "200") &
    "name=recordtype" #> (SHtml.radio(List("timestamp", "line"), Full("timestamp"), recordtype = _).toForm ++
                          SHtml.hidden(process))
  }

}
