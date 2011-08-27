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
package snippet 

import java.util.Date

import scala.xml.{NodeSeq, Text}

import net.liftweb.util._
import net.liftweb.common._
import net.liftweb.http.SHtml
import net.liftweb.http.js.{JsCmds, JsCmd}

import Helpers._
import code.comet.{Clear, LogServer, Pause, Resume}
import code.lib._
import code.model.LogFileInfo

class Controls extends Logger {
  lazy val date: Box[Date] = DependencyFactory.inject[Date] // inject the date

  // replace the contents of the element with id "time" with the date
  def howdy = "#time *" #> date.map(_.toString)

  def random = "#uuid *" #> java.util.UUID.randomUUID.toString

  def findLogFileInfo = "#logfiles *" #> Controls.getLogFileMarkup

  def clearButtonForm(xhtml: NodeSeq): NodeSeq = {
    def process(): JsCmd = {
      info("Clear button pressed")
      LogServer ! Clear
      JsCmds.Noop
    }
    SHtml.submit("Clear", () => { process() ; JsCmds.Noop }) ++
    SHtml.hidden(process)
  }

  def pauseButtonForm(xhtml: NodeSeq): NodeSeq = {
    def process(): JsCmd = {
      info("Pause button pressed")
      LogServer ! Pause
      JsCmds.Noop
    }
    SHtml.submit("Pause", () => { process() ; JsCmds.Noop }) ++
    SHtml.hidden(process)
  }

  def resumeButtonForm(xhtml: NodeSeq): NodeSeq = {
    def process(): JsCmd = {
      info("Resume button pressed")
      LogServer ! Resume
      JsCmds.Noop
    }
    SHtml.submit("Resume", () => { process() ; JsCmds.Noop }) ++
    SHtml.hidden(process)
  }

}

object Controls {

  def getLogFileMarkup =
    <ul>
      {
        LogFileInfo.findAll() map {
    	  logFileInfo: LogFileInfo =>
            <li> { logFileInfo.name }: { logFileInfo.filename } ({ logFileInfo.recordtype }) </li>
        }
      }
    </ul>

}
