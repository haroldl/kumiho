package code
package snippet

import org.specs._
import org.specs.runner.JUnit4
import org.specs.runner.ConsoleRunner
import net.liftweb._
import http._
import net.liftweb.util._
import net.liftweb.common._
import org.specs.matcher._
import org.specs.specification._
import Helpers._
import lib._


class ControlsTestSpecsAsTest extends JUnit4(ControlsTestSpecs)
object ControlsTestSpecsRunner extends ConsoleRunner(ControlsTestSpecs)

object ControlsTestSpecs extends Specification {
  val session = new LiftSession("", randomString(20), Empty)
  val stableTime = now

  override def executeExpectations(ex: Examples, t: =>Any): Any = {
    S.initIfUninitted(session) {
      DependencyFactory.time.doWith(stableTime) {
        super.executeExpectations(ex, t)
      }
    }
  }

  "Controls Snippet" should {
    "Put the time in the node" in {
      "a" must be equalTo("a")
    }
  }
}
