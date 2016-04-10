package upnp.util

import java.util.Date

import config.ApplicationConfig

/**
  * Created by stiff on 2016/4/10.
  */
private object UpnpHeaders {
  final val location = "LOCATION"
  final val usn = "USN"
  final val nts = "NTS"
  final val `cache-control` = "CACHE-CONTROL"
  final val server = "SERVER"
}
trait UpnpHeaderParser[T] {
  def apply(headers:Map[String,String]):T
}
private object CacheControl extends UpnpHeaderParser[Int]{
  def apply(headers: Map[String,String]): Int = {
    val header = headers.getOrElse(UpnpHeaders.`cache-control`,"")
    if(header.length > 0) {
      val reg = """max-age=(\d+)""".r
      header match {
        case reg(second) => second.toInt
        case _ => ApplicationConfig.defaultCacheControl
      }
    } else
      ApplicationConfig.defaultCacheControl
  }
}

private object Location extends UpnpHeaderParser[String]{
  override def apply(headers: Map[String, String]): String = {
    val header = headers.getOrElse(UpnpHeaders.location,"")
    if(header.length > 0) {
      header
    } else
      throw new Exception("Location header is not found!")
  }
}

private object USN extends UpnpHeaderParser[String]{
  override def apply(headers: Map[String, String]): String = {
    val header = headers.getOrElse(UpnpHeaders.usn,"")
    if(header.length > 0) {
      header
    } else
      throw new Exception("USN header is not found!")
  }
}

object NTS extends UpnpHeaderParser[Boolean]  {
  override def apply(headers: Map[String, String]): Boolean = {
    val header = headers.getOrElse(UpnpHeaders.nts,"")
    if(header.length > 0 && """ssdp:alive""".equals(header)) {
      true
    } else
      false
  }
}

case class DeviceDescription(uuid:String,schema:String,deviceType:String,location:String,cacheControl:Int,var lastUpdate:Date)


