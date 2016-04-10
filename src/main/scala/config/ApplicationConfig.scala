package config

import com.typesafe.config.ConfigFactory

/**
  * Created by stiff on 2016/4/5.
  */
object ApplicationConfig {
  private val config = ConfigFactory.load()

  val multicastAddress = config.getString("dlna.multiAddress")
  val multicastPort = config.getInt("dlna.multiPort")
  val localAddress = config.getString("dlna.localAddress")
  val dlnaServer = config.getString("dlna.server")
  val defaultCacheControl = config.getInt("dlna.defaultCacheControl")
}


