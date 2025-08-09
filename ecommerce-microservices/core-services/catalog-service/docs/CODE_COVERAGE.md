# Code Coverage with JaCoCo

This project uses JaCoCo (Java Code Coverage) for measuring and reporting code coverage during testing.

## 📊 Current Coverage Status

- **Instruction Coverage**: 30.9% (404/1306)
- **Branch Coverage**: 24.7% (36/146)  
- **Line Coverage**: 33.4% (99/296)

## 🎯 Coverage Goals

- **Instruction Coverage**: ≥80%
- **Branch Coverage**: ≥70%

## 🛠️ Setup

JaCoCo is configured in `pom.xml` with the following features:

### Maven Plugin Configuration

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.13</version>
    <executions>
        <!-- Prepare JaCoCo agent for unit tests -->
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        
        <!-- Generate coverage report -->
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        
        <!-- Check coverage thresholds -->
        <execution>
            <id>check</id>
            <phase>verify</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Exclusions

The following classes are excluded from coverage analysis:
- `**/ProductServiceApplication.class` - Main application class
- `**/config/**` - Configuration classes
- `**/dto/**` - Data Transfer Objects
- `**/*$HibernateProxy*` - Hibernate generated proxy classes
- `**/*$HibernateInstantiator*` - Hibernate generated classes

## 🚀 Usage

### Generate Coverage Report

```bash
# Run tests with coverage
mvn clean test

# Run specific tests with coverage
mvn test -Dtest=CategorySpecificationTest,ProductSpecificationTest,ProductServiceTest

# Run tests and check coverage thresholds
mvn clean verify
```

### Using the Coverage Script

```bash
# Run the provided coverage script
./coverage.sh
```

This script will:
1. Run unit tests with JaCoCo instrumentation
2. Generate HTML, CSV, and XML coverage reports
3. Display coverage summary statistics
4. Provide suggestions for improving coverage

## 📈 Coverage Reports

After running tests, coverage reports are generated in:

- **HTML Report**: `target/site/jacoco/index.html` (Interactive browsable report)
- **CSV Report**: `target/site/jacoco/jacoco.csv` (Machine-readable data)
- **XML Report**: `target/site/jacoco/jacoco.xml` (CI/CD integration)

### Viewing the HTML Report

```bash
# Open in browser
open target/site/jacoco/index.html

# Or use the file:// URL
file:///path/to/project/target/site/jacoco/index.html
```

## 📋 Coverage Metrics

JaCoCo measures several types of coverage:

### Instruction Coverage
- Percentage of JVM bytecode instructions executed
- Most granular coverage metric
- **Current**: 30.9%

### Branch Coverage  
- Percentage of code branches (if/else, switch, loops) executed
- Measures decision points in code
- **Current**: 24.7%

### Line Coverage
- Percentage of source code lines executed
- Human-readable coverage metric
- **Current**: 33.4%

### Method Coverage
- Percentage of methods executed
- Useful for identifying untested methods

### Class Coverage
- Percentage of classes with at least one executed instruction

## 🎯 Improving Coverage

To reach the 80% instruction coverage goal, focus on:

### High Impact Areas
1. **CategoryService** - 0% coverage, 44 lines
2. **CategoryController** - 0% coverage, 11 lines  
3. **ProductController** - 0% coverage, 11 lines
4. **GlobalExceptionHandler** - 0% coverage, 20 lines

### Testing Strategy
1. **Service Layer Tests**: Add tests for CategoryService methods
2. **Controller Tests**: Use @WebMvcTest for endpoint testing
3. **Exception Handling**: Test error scenarios and validation
4. **Edge Cases**: Test boundary conditions and error paths

### Test Categories to Add
- **CategoryService Integration Tests**: Database operations
- **REST API Tests**: HTTP endpoint testing with MockMvc
- **Error Handling Tests**: Exception scenarios
- **Validation Tests**: Input validation and constraints

## 🔧 Configuration Options

### Coverage Thresholds

Current thresholds in `pom.xml`:
```xml
<limits>
    <limit>
        <counter>INSTRUCTION</counter>
        <value>COVEREDRATIO</value>
        <minimum>0.80</minimum>
    </limit>
    <limit>
        <counter>BRANCH</counter>
        <value>COVEREDRATIO</value>
        <minimum>0.70</minimum>
    </limit>
</limits>
```

### Java 24 Compatibility

JaCoCo 0.8.13 is configured to work with Java 24, including:
- `XX:+EnableDynamicAgentLoading` for agent loading
- Exclusion of Hibernate proxy classes
- Proper Surefire integration

## 🚨 CI/CD Integration

For continuous integration:

```bash
# Fail build if coverage below threshold
mvn clean verify

# Generate coverage for external tools
mvn clean test jacoco:report

# Coverage data location
target/jacoco.exec
target/site/jacoco/jacoco.xml
```

## 📝 Best Practices

1. **Run coverage regularly** during development
2. **Focus on business logic** rather than getters/setters
3. **Test edge cases** to improve branch coverage
4. **Use coverage gaps** to identify untested scenarios
5. **Set realistic thresholds** that encourage quality
6. **Exclude generated code** from coverage analysis

## 🔗 Useful Links

- [JaCoCo Official Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [Maven JaCoCo Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [Coverage Best Practices](https://www.jacoco.org/jacoco/trunk/doc/bestpractices.html)
