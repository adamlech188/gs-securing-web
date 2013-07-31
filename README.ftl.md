<#assign project_id="gs-securing-web">

What you'll build
-----------------

This guide walks you through creating a simple web application with resources that are protected by Spring Security. The application requires the user to log in through a login form.


What you'll need
----------------

 - About 15 minutes
 - <@prereq_editor_jdk_buildtools/>


## <@how_to_complete_this_guide jump_ahead='Set up Spring Security'/>


<a name="scratch"></a>
Set up the project
------------------

<@build_system_intro/>

<@create_directory_structure_hello/>

### Create a Maven POM

    <@snippet path="pom.xml" prefix="initial"/>

<@bootstrap_starter_pom_disclaimer/>


Create an unsecured web application
-----------------------------------

Before you can apply security to a web application, you need a web application to secure. The steps in this section walk you through creating a very simple web application. Then you secure it with Spring Security in the next section.

The web application includes two simple views: a home page and a "Hello World" page. The home page is defined in the following Thymeleaf template:

    <@snippet path="src/main/resources/templates/home.html" prefix="initial"/>

As you can see, this simple view include a link to the page at "/hello". That is defined in the following Thymeleaf template:

    <@snippet path="src/main/resources/templates/hello.html" prefix="initial"/>

The web application is based on Spring MVC. Thus you need to configure Spring MVC and set up view controllers to expose these templates. Here's a configuration class for configuring Spring MVC in the application.

    <@snippet path="src/main/java/hello/MvcConfig.java" prefix="initial"/>

The `@EnableWebMvc` annotation configures much of Spring MVC. Meanwhile, the `addViewControllers()` method (overriding the method of the same name in `WebMvcConfigurerAdapter`) adds four view controllers. Two of the view controllers reference the view whose name is "home" (defined in `home.html`), and another references the view named "hello" (defined in `hello.html`). The fourth view controller references another view named "login". You'll create that view in the next section.

At this point, you could jump ahead to the _[Run the application](#run)_ section and run the application. The logout link won't work, but otherwise it's a functioning Spring MVC application.

With the base simple web application created, you can add security to it.


<a name="initial"></a>
Set up Spring Security
---------------------

Suppose that you want to prevent unauthorized users from viewing the greeting page at "/hello". As it is now, if users click the link on the home page, they see the greeting with no barriers to stop them. You need to add a barrier that forces the user to sign in before seeing that page.

You do that by configuring Spring Security in the application. Here's a security configuration that ensures that only authenticated users can see the secret greeting:

    <@snippet path="src/main/java/hello/WebSecurityConfig.java" prefix="complete"/>

The `WebSecurityConfig` class is annotated with `@EnableWebSecurity` to enable Spring Security's web security support. It also extends `WebSecurityConfigurerAdapter` and overrides a couple of its methods to set some specifics of the web security configuration.

The `configure()` method defines which URL paths should be secured and which should not.Specifically, the "/hello" path is configured to require the user to have the USER role.If the user is found not to have the USER role, it could mean that the user hasn't signed in yet and will be automatically taken to the login page, or that the user has not been assigned the USER role. Meanwhile, the "/**" path (using Ant-style wildcarding to indicate all paths not previously constrained) is configured to permit access to all users, authenticated or not. 

THe `configure()` method goes on to indicate that after a successful login the user's browser is redirected to "/hello". When logging out, the user is redirected to "/".

As for the `registerAuthentication()` method, it sets up an in-memory user store with a single user. That user is given a username of "user", a password of "password", and a role of "USER".

All that's left to do is create the login page. There's already a view controller for the "login" view, so you only need to create the login view itself:

    <@snippet path="src/main/resources/templates/login.html" prefix="complete"/>

As you can see, this Thymeleaf template simply presents a form that captures a username and password and posts them to "/login". As configured, Spring Security provides a filter that intercepts that request and authenticates the user.


Make the application executable
-------------------------------

Although it is possible to package this service as a traditional _web application archive_ or [WAR][u-war] file for deployment to an external application server, the simpler approach demonstrated below creates a _standalone application_. You package everything in a single, executable JAR file, driven by a good old Java `main()` method. And along the way, you use Spring's support for embedding the [Tomcat][u-tomcat] servlet container as the HTTP runtime, instead of deploying to an external instance.

### Create a main class

    <@snippet path="src/main/java/hello/Application.java" prefix="complete"/>

The `main()` method defers to the [`SpringApplication`][] helper class, providing `Application.class` as an argument to its `run()` method. This tells Spring to read the annotation metadata from `Application` and to manage it as a component in the _[Spring application context][u-application-context]_.

The `@ComponentScan` annotation tells Spring to search recursively through the `hello` package and its children for classes marked directly or indirectly with Spring's [`@Component`][] annotation. This directive ensures that Spring finds and registers the `WebConfig` and `WebSecurityConfig`, because they are marked with `@Configuration`, which in turn is a kind of `@Component` annotation. In effect, those configuration classes are also used to configure Spring.

The [`@EnableAutoConfiguration`][] annotation switches on reasonable default behaviors based on the content of your classpath. For example, because the application depends on the embeddable version of Tomcat (tomcat-embed-core.jar), a Tomcat server is set up and configured with reasonable defaults on your behalf. And because the application also depends on Spring MVC (spring-webmvc.jar), a Spring MVC [`DispatcherServlet`][] is configured and registered for you — no `web.xml` necessary! Auto-configuration is a powerful, flexible mechanism. See the [API documentation][`@EnableAutoConfiguration`] for further details.

### <@build_an_executable_jar/>


<a name="run"></a>
<@run_the_application/>

```
... app starts up ...
```

Once the application starts up, point your browser to http://localhost:8080. You should see the home page:

![The application's home page](images/home.png)

When you click on the link, it attempts to take you to the greeting page at `/hello`. But because that page is secured and you have not yet logged in, it takes you to the login page:

![The login page](images/login.png)

At the login page, sign in as the test user by entering "user" and "password" for the username and password fields, respectively. Once you submit the login form, you are authenticated and then taken to the greeting page:

![The secured greeting page](images/greeting.png)

If you click on the "logout" link, your authentication is revoked, and you are returned to the home page where you'll need to log in again before seeing the greeting page.


Summary
-------
Congratulations! You have developed a simple web application that is secured with Spring Security.


[u-war]: /understanding/war
[u-tomcat]: /understanding/tomcat
[u-application-context]: /understanding/application-context
[`SpringApplication`]: http://static.springsource.org/spring-bootstrap/docs/0.5.0.BUILD-SNAPSHOT/javadoc-api/org/springframework/bootstrap/SpringApplication.html
[`@Component`]: http://static.springsource.org/spring/docs/current/javadoc-api/org/springframework/stereotype/Component.html
[`@EnableAutoConfiguration`]: http://static.springsource.org/spring-bootstrap/docs/0.5.0.BUILD-SNAPSHOT/javadoc-api/org/springframework/bootstrap/context/annotation/SpringApplication.html
[`DispatcherServlet`]: http://static.springsource.org/spring/docs/current/javadoc-api/org/springframework/web/servlet/DispatcherServlet.html