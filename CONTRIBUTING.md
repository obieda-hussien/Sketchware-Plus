# Contributing Guide for Sketchware Plus

Thank you for your interest in contributing to Sketchware Plus! This guide will help you get started.

## Table of Contents

- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Code Style](#code-style)
- [Making Changes](#making-changes)
- [Testing](#testing)
- [Submitting Changes](#submitting-changes)
- [Code Review Process](#code-review-process)
- [Community Guidelines](#community-guidelines)

## Getting Started

### Prerequisites

Before you begin, ensure you have:

- **JDK 17** or higher installed
- **Android Studio** (latest stable version recommended)
- **Git** for version control
- **4GB RAM** minimum (8GB recommended for smoother development)
- **Android SDK** with API 24-35 installed

### Fork and Clone

1. Fork the repository on GitHub
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/Sketchware-Plus.git
   cd Sketchware-Plus
   ```

3. Add upstream remote:
   ```bash
   git remote add upstream https://github.com/obieda-hussien/Sketchware-Plus.git
   ```

## Development Setup

### 1. Import Project

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned directory
4. Wait for Gradle sync to complete

### 2. Build the Project

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

### 3. Run on Device/Emulator

- Use Android Studio's run button, or
- Command line: `./gradlew installDebug`

## Code Style

### Java Code Style

Follow standard Java conventions:

```java
// Good
public class MyClass {
    private static final String TAG = "MyClass";
    private String myVariable;
    
    public void myMethod(String parameter) {
        if (parameter != null) {
            // Do something
        }
    }
}
```

### Kotlin Code Style

Follow Kotlin official style guide:

```kotlin
// Good
class MyClass {
    companion object {
        const val TAG = "MyClass"
    }
    
    private var myVariable: String? = null
    
    fun myMethod(parameter: String?) {
        parameter?.let {
            // Do something
        }
    }
}
```

### XML Layout Style

```xml
<!-- Use meaningful IDs -->
<TextView
    android:id="@+id/project_name_text"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="@string/project_name" />
```

### General Guidelines

- **Line length**: Maximum 120 characters
- **Indentation**: 4 spaces (no tabs)
- **Comments**: Use JavaDoc/KDoc for public APIs
- **Naming**:
  - Classes: PascalCase
  - Methods/variables: camelCase
  - Constants: UPPER_SNAKE_CASE
  - Resources: snake_case

## Making Changes

### 1. Create a Branch

Always create a new branch for your changes:

```bash
# Feature branch
git checkout -b feature/add-new-feature

# Bug fix branch
git checkout -b fix/fix-bug-name

# Documentation
git checkout -b docs/update-readme
```

### 2. Make Your Changes

- **Focus**: Make small, focused changes
- **Quality**: Write clean, readable code
- **Documentation**: Update relevant documentation
- **Comments**: Add comments for complex logic

### 3. Commit Your Changes

Follow our commit message convention:

```bash
# Format: <type>: <description>

# Examples:
git commit -m "feat: Add new block type for API calls"
git commit -m "fix: Fix crash on project deletion"
git commit -m "docs: Update installation instructions"
git commit -m "style: Format code in ProjectBuilder"
git commit -m "refactor: Simplify compilation logic"
git commit -m "test: Add tests for resource compiler"
git commit -m "chore: Update dependencies"
```

#### Commit Types

- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation only
- `style`: Code style/formatting (no logic change)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks
- `perf`: Performance improvements
- `ci`: CI/CD changes

## Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test
./gradlew test --tests "com.example.MyTest"

# Run with coverage
./gradlew test jacocoTestReport
```

### Writing Tests

#### Unit Test Example

```java
public class ProjectBuilderTest {
    @Test
    public void testProjectCompilation() {
        // Given
        ProjectBuilder builder = new ProjectBuilder(context);
        Project project = createTestProject();
        
        // When
        Result result = builder.compile(project);
        
        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getApkFile());
    }
}
```

#### UI Test Example

```kotlin
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Test
    fun testProjectCreation() {
        onView(withId(R.id.create_project_button))
            .perform(click())
        
        onView(withId(R.id.project_name_input))
            .perform(typeText("Test Project"))
        
        onView(withId(R.id.create_button))
            .perform(click())
        
        onView(withText("Test Project"))
            .check(matches(isDisplayed()))
    }
}
```

## Submitting Changes

### 1. Update Your Branch

Before submitting, update your branch with latest changes:

```bash
git fetch upstream
git rebase upstream/main
```

### 2. Push Your Changes

```bash
git push origin feature/your-feature-name
```

### 3. Create Pull Request

1. Go to your fork on GitHub
2. Click "New Pull Request"
3. Select your branch
4. Fill in the PR template:

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
How to test these changes

## Screenshots (if applicable)
Add screenshots for UI changes

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-reviewed the code
- [ ] Added comments for complex logic
- [ ] Updated documentation
- [ ] Added tests
- [ ] All tests pass
- [ ] No new warnings
```

## Code Review Process

### What to Expect

1. **Initial Review**: Within 2-3 days
2. **Feedback**: Reviewers may request changes
3. **Discussion**: Be open to feedback and discussion
4. **Approval**: Once approved, changes will be merged

### Review Criteria

- ✅ Code quality and style
- ✅ Test coverage
- ✅ Documentation
- ✅ Performance impact
- ✅ Security considerations
- ✅ Backward compatibility

### Responding to Feedback

- Be professional and respectful
- Ask questions if feedback is unclear
- Make requested changes promptly
- Explain your reasoning when disagreeing

## Community Guidelines

### Communication

- **Be respectful**: Treat everyone with respect
- **Be constructive**: Provide helpful feedback
- **Be patient**: Remember everyone is learning
- **Be clear**: Communicate clearly and concisely

### Where to Get Help

- **Discord**: [Join our server](http://discord.gg/kq39yhT4rX)
- **GitHub Discussions**: For general questions
- **GitHub Issues**: For bug reports and feature requests

### Recognition

Contributors will be recognized in:
- About Team activity in the app
- GitHub contributors list
- Release notes (for significant contributions)

## Advanced Topics

### Working with Large Changes

For major features:

1. Open an issue first to discuss
2. Break into smaller PRs if possible
3. Document architecture decisions
4. Provide detailed testing instructions

### Working on Core Features

If modifying core compilation/build system:

1. Discuss with maintainers first
2. Ensure backward compatibility
3. Add comprehensive tests
4. Update documentation thoroughly

### Adding Dependencies

Before adding new dependencies:

1. Check if it's really needed
2. Verify it's actively maintained
3. Check license compatibility
4. Discuss with maintainers
5. Document why it's needed

## Resources

- [Android Development Best Practices](https://developer.android.com/topic/performance)
- [Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html)
- [Git Best Practices](https://git-scm.com/book/en/v2)

## Questions?

If you have questions not covered here:

1. Check existing documentation
2. Search GitHub issues
3. Ask in Discord
4. Create a discussion on GitHub

---

Thank you for contributing to Sketchware Plus! Your contributions help keep this project alive and thriving. 🎉
