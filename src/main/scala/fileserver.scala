/*
 *    Copyright 2017 Jason Mar
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.coding.{Gzip, NoCoding}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.{ActorMaterializer, Materializer}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object fileserver {

  def handle(fsDir: String)(implicit sys: ActorSystem, mat: Materializer, dis: ExecutionContext): Route = {
    decodeRequestWith(Gzip,NoCoding){
      encodeResponseWith(NoCoding,Gzip){
        getFromBrowseableDirectory(fsDir)
      }
    }
  }

  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem()
    implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
    implicit val dispatcher: ExecutionContextExecutor = actorSystem.dispatcher

    val fsDir = sys.env.getOrElse("FS_ROOT", "/var/www/html/")
    val interface = sys.env.getOrElse("FS_ADDR", "localhost")
    val port = sys.env.getOrElse("FS_PORT", "8080").toInt
    require(new File(fsDir).exists, s"FS_ROOT $fsDir does not exist")
    require(port >= 0 && port <= 65535)

    val server = Http().bindAndHandle(
      handler = handle(fsDir),
      interface = interface,
      port = port
    )

    System.out.println(s"akka-http-fileserver started on $interface:$port for $fsDir")
  }
}
