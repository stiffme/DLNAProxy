import akka.actor.{ActorSystem, Props}
import upnp.UpnpServer



/**
 * @author ${user.name}
 */
object App {


  def main(args : Array[String]) {
    implicit val actorSystem = ActorSystem("ActorSystem")
    val upnpServer = actorSystem.actorOf(Props[UpnpServer],"UpnpServer")

  }

}
