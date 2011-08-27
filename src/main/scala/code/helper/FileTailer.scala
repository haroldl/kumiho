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
package helper

import java.io._
import scala.annotation.tailrec
import net.liftweb.common.Logger

/**
 * Tail a file.
 * Not thread safe.
 */
class FileTailer (val filename: String, val sleepTimeMillis: Int) extends Logger {

  private val file = new File(filename)
  private val bufferSize = 4096
  private val buffer : Array[Char] = new Array(bufferSize)
  private val lineBuf = new StringBuilder(bufferSize)

  private var in = new FileReader(file)
  private var lastEOFSize: Long = 0L

  def b = lineBuf

  def readToBuffer(): Unit = {
    in.read(buffer, 0, bufferSize) match {
      case -1 => {
        val newSize = file.length()
        if (newSize < lastEOFSize) reopen()
        lastEOFSize = newSize
        Thread.sleep(sleepTimeMillis)
      }
      case num => lineBuf.appendAll(buffer, 0, num)
    }
  }

  @tailrec
  final def readLine(): String = {
    lineBuf.indexOf("\n") match {
      case -1 => {
        readToBuffer()
        readLine()
      }
      case n => {
        val line = lineBuf.substring(0, n+1)
        lineBuf.delete(0, n+1)
        line
      }
    } 
  }

  def tail(callback: String => Unit) = {
    while (true) {
      callback(readLine())
    }
  }

  @tailrec
  final def seekToEnd(): Unit = {
    in.read(buffer, 0, bufferSize) match {
      case -1 =>
      case num => seekToEnd()
    }
  }

  private def reopen(): Unit = {
    info("Reopening " + filename)
    in = new FileReader(file)
    lastEOFSize = 0
    seekToEnd()
  }

}

