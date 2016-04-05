import akka.actor.ActorSystem
import org.slf4j.bridge.SLF4JBridgeHandler


/**
 * @author ${user.name}
 */
object App {


  def main(args : Array[String]) {
    implicit val actorSystem = ActorSystem("ActorSystem")


  }

}
