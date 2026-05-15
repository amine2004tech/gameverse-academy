

import ma.ac.esi.gameverseacademy.controller.*;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        String webappDir = new File("webapp").getAbsolutePath();
        if (!new File(webappDir).exists()) {
            webappDir = new File("src/main/webapp").getAbsolutePath();
        }
        
        Context ctx = tomcat.addWebapp("/gameverseacademy", webappDir);
        ctx.setParentClassLoader(Main.class.getClassLoader());

        // External Resource Mapping for Portable Version
        // This allows assets/ and uploads/ folders at the root to be served as /assets and /uploads
        File externalAssets = new File("assets");
        if (externalAssets.exists()) {
            org.apache.catalina.WebResourceRoot resources = new org.apache.catalina.webresources.StandardRoot(ctx);
            resources.addPreResources(new org.apache.catalina.webresources.DirResourceSet(resources, "/assets", externalAssets.getAbsolutePath(), "/"));
            ctx.setResources(resources);
            System.out.println("[Main] Mapped external assets from: " + externalAssets.getAbsolutePath());
        }

        // Register ALL servlets
        Tomcat.addServlet(ctx, "ProfileController",      new ProfileController()).setLoadOnStartup(1);
        Tomcat.addServlet(ctx, "LoginController",        new LoginController()).setLoadOnStartup(1);
        Tomcat.addServlet(ctx, "LogoutController",       new LogoutController()).setLoadOnStartup(1);
        Tomcat.addServlet(ctx, "ModController",          new ModController()).setLoadOnStartup(1);
        Tomcat.addServlet(ctx, "ModDetailsController",   new ModDetailsController()).setLoadOnStartup(1);
        org.apache.catalina.Wrapper modSubmit = Tomcat.addServlet(ctx, "ModSubmitController", new ModSubmitController());
        modSubmit.setLoadOnStartup(1);
        modSubmit.setMultipartConfigElement(new javax.servlet.MultipartConfigElement(System.getProperty("java.io.tmpdir"), 1024 * 1024 * 50, 1024 * 1024 * 100, 1024 * 1024 * 2));
        Tomcat.addServlet(ctx, "ReviewController",       new ReviewController()).setLoadOnStartup(1);
        Tomcat.addServlet(ctx, "UpdateReviewController", new UpdateReviewController()).setLoadOnStartup(1);
        Tomcat.addServlet(ctx, "DeleteReviewController", new DeleteReviewController()).setLoadOnStartup(1);
        Tomcat.addServlet(ctx, "DeleteModController",    new DeleteModController()).setLoadOnStartup(1);
        Tomcat.addServlet(ctx, "DownloadModController",  new DownloadController()).setLoadOnStartup(1);
        Tomcat.addServlet(ctx, "AdminController",        new AdminController()).setLoadOnStartup(1);

        ctx.addServletMappingDecoded("/ProfileController",      "ProfileController");
        ctx.addServletMappingDecoded("/LoginController",        "LoginController");
        ctx.addServletMappingDecoded("/LogoutController",       "LogoutController");
        ctx.addServletMappingDecoded("/ModController",          "ModController");
        ctx.addServletMappingDecoded("/ModDetailsController",   "ModDetailsController");
        ctx.addServletMappingDecoded("/ModSubmitController",    "ModSubmitController");
        ctx.addServletMappingDecoded("/DeleteModController",    "DeleteModController");
        ctx.addServletMappingDecoded("/ReviewController",       "ReviewController");
        ctx.addServletMappingDecoded("/UpdateReviewController", "UpdateReviewController");
        ctx.addServletMappingDecoded("/DeleteReviewController", "DeleteReviewController");
        ctx.addServletMappingDecoded("/DownloadModController",  "DownloadModController");
        ctx.addServletMappingDecoded("/AdminController",        "AdminController");

        tomcat.start();
        System.out.println("=============================================");
        System.out.println("  GameVerse Academy RUNNING on port 8080");
        System.out.println("=============================================");
        System.out.println(">>> http://localhost:8080/gameverseacademy/ModDetailsController?id=5");
        System.out.println(">>> http://localhost:8080/gameverseacademy/mods.jsp");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try { tomcat.stop(); tomcat.destroy(); }
            catch (Exception e) { e.printStackTrace(); }
        }));

        tomcat.getServer().await();
    }
}
