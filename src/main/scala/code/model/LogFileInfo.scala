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
package code.model

import net.liftweb.mapper._

class LogFileInfo extends LongKeyedMapper[LogFileInfo] {

  def getSingleton = LogFileInfo

  def primaryKeyField = id
  object id extends MappedLongIndex(this)
  object name extends MappedString(this, 40)
  object filename extends MappedString(this, 200)
  object recordtype extends MappedString(this, 200)
}

object LogFileInfo extends LogFileInfo with LongKeyedMetaMapper[LogFileInfo]
