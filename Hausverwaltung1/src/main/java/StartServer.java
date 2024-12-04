import com.sun.net.httpserver.HttpServer;
import jdk.internal.loader.Resource;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.Authenticator;
import java.net.URI;

public class StartServer {
    public static void main(String[] args) {
        final String pack = "dev.bsinfo.ressource";
        String url = "https://localhost:8000/rest";
        System.out.println("Start Server");
        System.out.println(url);
         //final ResourceConfig rc = new ResourceConfig().packages(pack).register(Authenticator.class);
        final ResourceConfig rc = new ResourceConfig().packages(pack);
        final HttpServer server = JdkHttpServerFactory.createHttpServer(URI.create(url),rc);
        System.out.println("Ready for Requests....");
    }
}
