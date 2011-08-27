/*
 * Copyright 2011 LinkedIn, Inc
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
package code.comet

import net.liftweb.common.Logger
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.CometActor
import net.liftweb.http.CometListener
import net.liftweb.http.RequestVar
import net.liftweb.http.SHtml
import net.liftweb.http.js.JE
import net.liftweb.http.js.JsCmds
import net.liftweb.http.js.JsCmds._

import scala.annotation.tailrec

import code.Config

class Tail extends CometActor with CometListener with Logger {

  private object paused extends RequestVar(false)

  private var logRecords: Vector[(String,String)] = Vector()

  def registerWith = LogServer

  override def lowPriority = {
    case v: Vector[(String,String)] => {
      if ( !paused.get )
    	logRecords = v
      reRender()
    }
  }

  def render = {
    if (Config.topDown) {
      partialUpdate { scrollToBottom }
    }
    val recs = if (Config.topDown) logRecords else logRecords.reverse
    "div *" #> <pre>{ (recs filter Config.logRecordFilter map renderLogRecord).mkString("") }</pre>
  }

  def renderLogRecord(record: (String, String)): String = {
    record match {
      case (logname: String, line: String) => {
        padToLength(logname, 8, " ") + " " + ensureHasNewline(line)
      }
    }
  }

  @tailrec
  final def padToLength(value: String, length: Int, padChar: String): String = {
    if (value.length() >= length) value else padToLength(value + padChar, length, padChar)
  }

  private def scrollToBottom =
    JsRaw("""var objDiv = document.getElementById("logs");
             objDiv.scrollTop = objDiv.scrollHeight;""")

  private def ensureHasNewline(line: String): String =
    if (line endsWith "\n") line else line + "\n"

}

