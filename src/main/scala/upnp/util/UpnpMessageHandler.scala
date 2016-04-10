package upnp.util

import java.util.Date

import akka.actor.{Actor, ActorLogging}
import org.slf4j.LoggerFactory

/**
  * Created by stiff on 2016/4/10.
  */
final case class UpnpMessage(upnpMethod:String, headers:Map[String,String])

final case class UpnpMessageReceived(raw:String)

class UpnpMessageHandler extends Actor with ActorLogging{
  val usnParser = """uuid:(.*)::urn:(.*):(.*):\d""".r
  val headerReg = """(.*): (.*)""".r
  def decodeMessage(raw:String):UpnpMessage = {
    val lines = raw.split("\r\n")
    var firstLine = true
    val tempMap = collection.mutable.HashMap.empty[String,String]
    var method:String = ""
    lines.foreach( l => {
      val trim = l.trim
      if(firstLine) {
        method = trim
        firstLine = false
      }
      else  {
        trim match {
          case headerReg(header,value) => tempMap += (header -> value)
          case _ => log.warning("Upnp header {} is not parsed", trim)
        }
      }
    })
    UpnpMessage(method,tempMap.toMap)
  }

  def receive = {
    case UpnpMessageReceived(data) => {
      val upnpMessage = decodeMessage(data)
      log.debug("method is {} ",upnpMessage.upnpMethod)
      if(log.isDebugEnabled)
        upnpMessage.headers.foreach( entry => log.debug("header is {} \t value is {}",entry._1,entry._2))

    }
  }

  private def handleNotify(message:UpnpMessage): Unit = {
    val deviceDescription = parseDeviceDescription(message.headers)
    if(deviceDescription != null) {

    }
  }

  private def handle200OK(message:UpnpMessage):Unit = {
    val deviceDescription = parseDeviceDescription(message.headers)
    if(deviceDescription != null) {

    }
  }

  private def parseDeviceDescription(headers:Map[String,String]):DeviceDescription = {
    try{
      val location = Location(headers)
      val cacheControl = CacheControl(headers)
      val usn = USN(headers)

      //parse usn
      usn match {
        case usnParser(uuid,schemas,deviceType) => DeviceDescription(uuid,schemas,deviceType,location,cacheControl,new Date)
        case _ => log.warning("device does not recognize"); null
      }
    } catch {
      case Exception => { null}
    }

  }
}
