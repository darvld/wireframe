# Project sample

This project showcases how to use the code generator and the Ktor runtime plugin to create a GraphQL application with
Wireframe.

## Structure

- `build.gradle.kts` shows how to install and configure dependencies, including the Gradle plugin.
- `src/App.kt` is the entry point for the application. You can explore the functions used there for more information on
  how to setup the application.
- `resources` contains configuration files for the Ktor server, Logback (SLF4J) settings, and the index page used to
  display the GraphiQL UI.
- `graphql` contains the GraphQL schema used by the server.