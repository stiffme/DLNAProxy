package upnp

import java.net._
import java.nio.channels.DatagramChannel

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.io.{IO, Udp}
import akka.io.Inet.{DatagramChannelCreator, SocketOptionV2}
import config.ApplicationConfig
import upnp.util.{UpnpMessageHandler, UpnpMessageReceived}

/**
  * Created by stiff on 2016/4/5.
  */
class UpnpServer() extends Actor with ActorLogging{
  import context.system
  val opts = List(Inet4ProtocolFamily(), MulticastGroup())
  val upnpMessageHandler = context.system.actorOf(Props[UpnpMessageHandler],"UpnpMessageHandler")

  IO(Udp) ! Udp.Bind(self, new InetSocketAddress(ApplicationConfig.localAddress, ApplicationConfig.multicastPort), opts)

  def receive = {
    case Udp.Bound(local) => {
      context.become(ready(sender()))
    }
  }

  def ready(socket:ActorRef):Receive = {
    case Udp.Received(data, remote) =>  {
      val stringData = data.decodeString("UTF-8")
      log.debug("udp data is {}",stringData)
      upnpMessageHandler ! UpnpMessageReceived(stringData)
    }
    case Udp.Unbind  => socket ! Udp.Unbind
    case Udp.Unbound => context.stop(self)
  }



}


final case class Inet4ProtocolFamily() extends DatagramChannelCreator {
  @scala.throws[Exception](classOf[Exception])
  override def create(): DatagramChannel = {
    val dc = DatagramChannel.open(StandardProtocolFamily.INET)
    dc.setOption(StandardSocketOptions.SO_REUSEADDR,Boolean.box(true))
    dc
  }
}

final case class MulticastGroup() extends SocketOptionV2  {
  override def afterBind(s: DatagramSocket): Unit = {
    import config.ApplicationConfig
    val group = InetAddress.getByName(ApplicationConfig.multicastAddress)
    val networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(ApplicationConfig.localAddress))

    s.getChannel.join(group,networkInterface)
  }
}