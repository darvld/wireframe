# 0.6.0 (December 2022)

## New Features

- Support for custom type mappings.

## Improvements

- Decoupled the runtime from Ktor dependencies. It is now possible to develop integrations with other web frameworks.
- Context plugins have been reworked to allow access to the underlying integration's request information (e.g. Ktor's
  ApplicationCall).

## Fixes

- Generated resolvers are now correctly marked as `suspending`.
- The `ResolversDSL` marker is now correctly applied to avoid scope pollution.
- Published artifacts now have the correct id.