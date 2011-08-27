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

import net.liftweb.actor.LiftActor
import net.liftweb.common.Logger
import net.liftweb.http.ListenerManager

import code.model.Preference

object LogServer extends LiftActor with ListenerManager with Logger {

  private var maxRecords = 50
  private var logRecords : Vector[(String,String)] = Vector()
  private var paused = false

  def createUpdate = logRecords

  override def lowPriority = {
    case p: Preference => {
      if (p.name == "maxRecords") {
        maxRecords = Integer.parseInt(p.value)
        trimToMax()
        if (!paused) updateListeners()
      }
    }
    case StartRecord(logname: String, line: String) => {
      logRecords :+= (logname, line)
      trimToMax()
      if (!paused) updateListeners()
    }
    case AppendRecord(logname: String, line: String) => {
      val index = logRecords.lastIndexWhere({ case (n,l) => n == logname })
      if (index >= 0) {
        val newRecord = (logname, logRecords(index)._2 + line)
        logRecords = logRecords updated (index, newRecord)
        if (!paused) updateListeners()
      }
    }
    case Clear => {
      info("Clearing log records")
      logRecords = Vector()
      paused = false
      updateListeners()
    }
    case Pause => {
      paused = true
    }
    case Resume => {
      paused = false
      updateListeners()
    }
  }

  private def trimToMax() = {
    logRecords = logRecords drop (logRecords.size - maxRecords)      
  }

}

sealed abstract class Command
final case class StartRecord(val logname: String, val line: String) extends Command
final case class AppendRecord(val logname: String, val line: String) extends Command
final case class Clear() extends Command
final case class Pause() extends Command
final case class Resume() extends Command
