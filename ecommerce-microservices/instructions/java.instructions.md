---
description: 'Guidelines for building Java base applications'
applyTo: '**/*.java'
---

# Java Development

## General Instructions

- First, prompt the user if they want to integrate static analysis tools (SonarQube, PMD, Checkstyle)
  into their project setup. If yes, provide guidance on tool selection and configuration.
- If the user declines static analysis tools or wants to proceed without them, continue with implementing the Best practices, bug patterns and code smell prevention guidelines outlined below.
- Address code smells proactively during development rather than accumulating technical debt.
- Focus on readability, maintainability, and performance when refactoring identified issues.
- Use IDE / Code editor reported warnings and suggestions to catch common patterns early in development.

## Best practices

- **Records**: For classes primarily intended to store data (e.g., DTOs, immutable data structures), **Java Records should be used instead of traditional classes**.
- **Pattern Matching**: Utilize pattern matching for `instanceof` and `switch` expression to simplify conditional logic and type casting.
- **Type Inference**: Use `var` for local variable declarations to improve readability, but only when the type is explicitly clear from the right-hand side of the expression.
- **Immutability**: Favor immutable objects. Make classes and fields `final` where possible. Use collections from `List.of()`/`Map.of()` for fixed data. Use `Stream.toList()` to create immutable lists.
- **Streams and Lambdas**: Use the Streams API and lambda expressions for collection processing. Employ method references (e.g., `stream.map(Foo::toBar)`).
- **Null Handling**: Avoid returning or accepting `null`. Use `Optional<T>` for possibly-absent values and `Objects` utility methods like `equals()` and `requireNonNull()`.

### Naming Conventions

- Follow Google's Java style guide:
  - `UpperCamelCase` for class and interface names.
  - `lowerCamelCase` for method and variable names.
  - `UPPER_SNAKE_CASE` for constants.
  - `lowercase` for package names.
- Use nouns for classes (`UserService`) and verbs for methods (`getUserById`).
- Avoid abbreviations and Hungarian notation.

### Spring Boot Best Practices
- Use **@RestController** for REST endpoints
- Use **@Service** for business logic
- Use **@Repository** for data access layer
- Use **@Entity** for JPA entities
- Prefer **constructor injection** over field injection
- Use **@Validated** for input validation
- Use **ResponseEntity** for HTTP responses with proper status codes
- Use **@Transactional** for database transactions

## API Design Guidelines

### REST Conventions
- Use **plural nouns** for resource endpoints: `/api/products`, `/api/orders`
- Use **HTTP methods** appropriately: GET, POST, PUT, DELETE
- Use **proper HTTP status codes**: 200, 201, 400, 404, 500
- Use **consistent response format** for all endpoints
- Implement **pagination** for list endpoints
- Use **query parameters** for filtering and sorting

## Database Conventions

### JPA Entities
- Use **@Entity** and **@Table** annotations
- Always include **@Id** and **@GeneratedValue**
- Use **@Column** for custom column mappings
- Use **@CreatedDate** and **@LastModifiedDate** for auditing
- Implement **equals()** and **hashCode()** for entities

### Relationships
- Use **@OneToMany**, **@ManyToOne**, **@ManyToMany** appropriately
- Use **FetchType.LAZY** by default
- Use **@JoinColumn** for foreign key mappings
- Avoid **bidirectional relationships** unless necessary

## Security Guidelines

### Authentication & Authorization
- Use **JWT tokens** for stateless authentication
- Implement **role-based access control** with Spring Security
- Use **@PreAuthorize** for method-level security
- Always validate and sanitize user inputs
- Use **BCrypt** for password hashing

### Data Protection
- Never log sensitive information (passwords, tokens, personal data)
- Use **@JsonIgnore** for password fields
- Implement **input validation** on all endpoints
- Use **HTTPS** for all communications

## Microservices Patterns

### Service Communication
- Prefer **REST APIs** for synchronous communication
- Use **message queues** (RabbitMQ) for asynchronous communication
- Implement **circuit breaker** pattern for fault tolerance
- Use **service discovery** (Eureka) for service registration

### Configuration
- Use **Spring Cloud Config** for centralized configuration
- Use **@ConfigurationProperties** for typed configuration
- Externalize environment-specific values
- Use **profiles** for different environments (dev, test, prod)

## Error Handling

### Exception Handling
- Create **custom exceptions** for business logic errors
- Use **@ControllerAdvice** for global exception handling
- Return **meaningful error messages** to clients
- Log errors with **appropriate log levels**

## Testing Guidelines

### Unit Testing
- Use **JUnit 5** for unit tests
- Use **Mockito** for mocking dependencies
- Test **business logic** in service classes
- Use **@MockBean** for Spring Boot tests
- Aim for **90%+ code coverage**

### Integration Testing
- Use **@SpringBootTest** for integration tests
- Use **TestContainers** for database testing
- Test **complete API workflows**
- Use **@Transactional** with **@Rollback** for test data cleanup

## Performance Considerations

### Caching
- Use **@Cacheable** for expensive operations
- Use **Redis** for distributed caching
- Cache **frequently accessed data** (products, user sessions)
- Implement **cache invalidation** strategies

### Database Optimization
- Use **database indexes** for frequently queried fields
- Implement **connection pooling**
- Use **pagination** for large datasets
- Optimize **N+1 query problems** with **@EntityGraph**


### Bug Patterns

| Rule ID | Description                                                 | Example / Notes                                                                                  |
| ------- | ----------------------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| `S2095` | Resources should be closed                                  | Use try-with-resources when working with streams, files, sockets, etc.                           |
| `S1698` | Objects should be compared with `.equals()` instead of `==` | Especially important for Strings and boxed primitives.                                           |
| `S1905` | Redundant casts should be removed                           | Clean up unnecessary or unsafe casts.                                                            |
| `S3518` | Conditions should not always evaluate to true or false      | Watch for infinite loops or if-conditions that never change.                                     |
| `S108`  | Unreachable code should be removed                          | Code after `return`, `throw`, etc., must be cleaned up.                                          |

## Code Smells

| Rule ID | Description                                            | Example / Notes                                                               |
| ------- | ------------------------------------------------------ | ----------------------------------------------------------------------------- |
| `S107`  | Methods should not have too many parameters            | Refactor into helper classes or use builder pattern.                          |
| `S121`  | Duplicated blocks of code should be removed            | Consolidate logic into shared methods.                                        |
| `S138`  | Methods should not be too long                         | Break complex logic into smaller, testable units.                             |
| `S3776` | Cognitive complexity should be reduced                 | Simplify nested logic, extract methods, avoid deep `if` trees.                |
| `S1192` | String literals should not be duplicated               | Replace with constants or enums.                                              |
| `S1854` | Unused assignments should be removed                   | Avoid dead variables—remove or refactor.                                      |
| `S109`  | Magic numbers should be replaced with constants        | Improves readability and maintainability.                                     |
| `S1188` | Catch blocks should not be empty                       | Always log or handle exceptions meaningfully.                                 |

## Build and Verification

- After adding or modifying code, verify the project continues to build successfully.
- If the project uses Maven, run `mvn clean install`.
- Ensure all tests pass as part of the build.

## When suggesting code, please:
1. Follow these conventions and patterns
2. Include proper error handling and validation
3. Add appropriate logging statements
4. Include relevant unit tests when applicable
5. Use the latest Spring Boot 3.5 features and best practices
6. Consider security implications
7. Suggest performance optimizations where relevant
8. Include proper documentation and comments
