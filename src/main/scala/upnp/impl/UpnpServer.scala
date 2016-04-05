package upnp.impl

import java.net.{InetAddress, MulticastSocket}

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef}
import config.ApplicationConfig

/**
  * Created by stiff on 2016/4/5.
  */
final case class UpnpServiceFound(description:String)
class UpnpServer(val deviceManager:ActorRef) extends Actor with ActorLogging{

  private val UDP_BUFFER_SIZE = 1024
  private val TIME_TO_LIVE = 4

  private val multiSocket = createMultiSocket()

  override def preStart(): Unit = {
    super.preStart()

  }


  override def postStop(): Unit = super.postStop()

  def receive = {

  }

  private def createMultiSocket() = {
    val socket = new MulticastSocket(ApplicationConfig.multicastPort)
    val socketGroup = InetAddress.getByName(ApplicationConfig.multicastAddress)

    socket.joinGroup(socketGroup)
    socket.setTimeToLive(TIME_TO_LIVE)
    socket.setReuseAddress(true)

    socket
  }
}
