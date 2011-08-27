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

import java.io.FileNotFoundException
import java.io.IOException

import scala.util.matching.Regex.Match
import scala.util.matching.Regex

import net.liftweb.actor.LiftActor
import net.liftweb.common.Logger

import code.Config
import code.comet.LogServer
import code.comet.StartRecord
import code.helper.FileTailer
import code.model.LogFileInfo
import code.comet.Command
import code.comet.AppendRecord

class LogDaemon(val logname: String, val filename: String, val recordtype: String) extends Thread with Logger {

  @volatile private var paused: Boolean = false

  def pauseMe(): Unit = {
    paused = true
  }

  def resumeMe(): Unit = {
    paused = false
  }

  override def run() = {
    val handler: ((String, String) => Command) = recordtype match {
      case "timestamp" => LogDaemon.timestampHandler
      case _ => LogDaemon.newlineHandler
    }

    while (true) {
      var tailer: FileTailer = null
      var notFound = false
      while (tailer == null) {
        try {
          tailer = new FileTailer(filename, Config.readDelay)
          info("Opened " + filename + ", seeking to end.")
          tailer.seekToEnd()
        } catch {
          case e: FileNotFoundException => {
            if (!notFound) {
              warn("Could not find " + filename)
              notFound = true
            }
            Thread.sleep(5000)
          }
        }
      }

      try {
        while (true) {
          if (!paused) {
            LogServer ! handler(logname, tailer.readLine())
          }
        }
      } catch {
        case e: IOException => warn(e) // ignore and try to re-open the file
      }
    }
  }

}

object LogDaemon extends LiftActor with Logger {

  var runningDaemons : Map[String,LogDaemon] = Map()

  def createIfNeeded(logFileInfo: LogFileInfo) = {
    if (!runningDaemons.contains(logFileInfo.name.get)) {
      val newFilename = replaceEnvVariables(logFileInfo.filename.get)
      val daemon = new LogDaemon(logFileInfo.name.get, newFilename, logFileInfo.recordtype.get)
      daemon setDaemon true
      daemon.start()
      runningDaemons ++= Map(logFileInfo.name.get -> daemon)
      info("Started LogDaemon to tail " + newFilename +
           ", now we have " + runningDaemons.size + " daemons.")
    }
  }

  val envVarRx = """\$([a-zA-Z_]+)""".r

  def replaceEnvVariables(in: String) =
    envVarRx.replaceAllIn(in, { m : Match => System.getenv(m.group(1)) })

  override def messageHandler = {
    case logFileInfo: LogFileInfo => createIfNeeded(logFileInfo)
    case PauseDaemon(name) => runningDaemons(name).pauseMe()
    case ResumeDaemon(name) => runningDaemons(name).resumeMe()
  }

  def newlineHandler(logname: String, line: String): Command = {
    StartRecord(logname, line)
  }

  def timestampHandler(logname: String, line: String): Command = {
    val command = line match {
      case timestampRegex() => StartRecord(logname, line)
      case _ => AppendRecord(logname, line)
    }
    command
  }

  val timestampRegex = """^\d{4}[-/]\d{1,2}[-/]\d{1,2} \d{2}:\d{2}:\d{2}.*\n?""".r

}

sealed abstract class DaemonCommand
final case class PauseDaemon(name: String) extends DaemonCommand
final case class ResumeDaemon(name: String) extends DaemonCommand

