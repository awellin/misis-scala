package ru.misis

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import ru.misis.registry.{ItemRegistry, MenuRegistry, UserRegistry}
import ru.misis.routes.{MenuRoutes, UserRoutes}
import akka.http.scaladsl.server.Directives._

import scala.util.Failure
import scala.util.Success

//#main-class
object QuickstartApp {
    //#start-http-server
    private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
        // Akka HTTP still needs a classic ActorSystem to start
        import system.executionContext

        val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
        futureBinding.onComplete {
            case Success(binding) =>
                val address = binding.localAddress
                system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
            case Failure(ex) =>
                system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
                system.terminate()
        }
    }

    //#start-http-server
    def main(args: Array[String]): Unit = {
        //#server-bootstrapping
        val rootBehavior = Behaviors.setup[Nothing] { context =>
            implicit val contextImpl = context.system
            implicit val exContenxt = contextImpl.executionContext

            val userRegistryActor = context.spawn(UserRegistry(), "UserRegistryActor")
            context.watch(userRegistryActor)
            val itemRegistryActor = context.spawn(ItemRegistry.apply(), "ItemRegistryActor")
            context.watch(itemRegistryActor)
            val menuRegistryActor = context.spawn(new MenuRegistry(itemRegistryActor).apply(), "MenuRegistryActor")
            context.watch(menuRegistryActor)

            val userRoutes = new UserRoutes(userRegistryActor)(context.system)
            val menuRoutes = new MenuRoutes(menuRegistryActor)
            startHttpServer(userRoutes.routes ~ menuRoutes.routes)(context.system)

            Behaviors.empty
        }
        val system = ActorSystem[Nothing](rootBehavior, "HelloAkkaHttpServer")
        //#server-bootstrapping
    }
}

//#main-class