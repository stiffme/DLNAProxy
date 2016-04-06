package upnp.impl

import java.net.{DatagramPacket, InetAddress, MulticastSocket}

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorRef}
import config.ApplicationConfig

/**
  * Created by stiff on 2016/4/5.
  */
final case class UpnpServiceFound(description:String)
class UpnpServer(val deviceManager:ActorRef) extends Actor with ActorLogging{

  final case class UpnpMessageReeived(datagramPacket: DatagramPacket)

  private val UDP_BUFFER_SIZE = 1024
  private val TIME_TO_LIVE = 4
  private val multiSocket = createMultiSocket()
  private var receiveQuit = false

  private val receiveThread = new Thread(new Runnable {
    override def run(): Unit = {
      val datagramPacket = new DatagramPacket(new Array[Byte](UDP_BUFFER_SIZE),UDP_BUFFER_SIZE)
      while(receiveQuit == false) {
        multiSocket.receive(datagramPacket)
        val udpMessage = datagramPacket.clone()
        UpnpServer.this.self ! UpnpMessageReeived(datagramPacket)
      }
    }
  })
  override def preStart(): Unit = {
    super.preStart()
    receiveThread.start()
  }


  override def postStop(): Unit ={
    super.postStop()
    receiveQuit = true
  }

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
