# Sketchware Plus Architecture Documentation

## Overview

This document provides a comprehensive overview of the Sketchware Plus architecture, component relationships, and design patterns.

## Table of Contents

1. [System Architecture](#system-architecture)
2. [Core Components](#core-components)
3. [Package Structure](#package-structure)
4. [Build System](#build-system)
5. [Editor System](#editor-system)
6. [Project Management](#project-management)
7. [Data Flow](#data-flow)
8. [Design Patterns](#design-patterns)

## System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐   │
│  │Activities│  │Fragments │  │ Dialogs  │  │  Views   │   │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                     Business Logic Layer                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  Compilers   │  │   Editors    │  │  Managers    │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                       Data Layer                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ File System  │  │    Cache     │  │  Utilities   │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

### Component Interaction

```
User Interface
      │
      ├─► ProjectManager ──► FileSystem
      │         │
      │         └─► BuildSystem ──┬─► JavaCompiler
      │                           ├─► ResourceCompiler
      │                           ├─► DexCompiler
      │                           └─► APKBuilder
      │
      └─► CodeEditor ──► SyntaxHighlighter
              │
              └─► AutoComplete
```

## Core Components

### 1. Project Builder (`a.a.a.ProjectBuilder`)

**Purpose**: Orchestrates the entire build process for Android projects.

**Key Responsibilities**:
- Coordinating compilation steps
- Managing dependencies
- Handling build configuration
- Generating APK/AAB files

**Key Methods**:
```java
public class ProjectBuilder {
    public void compile()               // Main compilation entry point
    public void compileResources()      // Compile Android resources
    public void compileJava()           // Compile Java source code
    public void compileDex()            // Convert to DEX format
    public void buildApk()              // Build final APK
}
```

### 2. Manifest Generator (`a.a.a.Ix`)

**Purpose**: Generates AndroidManifest.xml files.

**Features**:
- Dynamic manifest generation
- Permission management
- Component registration
- SDK version configuration

### 3. Activity Source Generator (`a.a.a.Jx`)

**Purpose**: Generates Java source code for activities.

**Features**:
- Activity lifecycle code generation
- Event listener generation
- View initialization code
- Data binding setup

### 4. Component Code Generator (`a.a.a.Lx`)

**Purpose**: Generates code for components (listeners, services, etc.).

**Features**:
- Listener implementations
- Service code generation
- Broadcast receiver code
- Content provider code

### 5. Layout XML Generator (`a.a.a.Ox`)

**Purpose**: Generates XML layout files.

**Features**:
- View hierarchy generation
- Constraint layout support
- Material Design components
- Custom view support

## Package Structure

### Core Packages

```
pro.sketchware/
├── activities/          # All activity classes
│   ├── main/           # Main app activities
│   ├── editor/         # Code editor activities
│   └── settings/       # Settings activities
│
├── fragments/          # Fragment classes
│   ├── settings/       # Settings fragments
│   └── projects/       # Project management fragments
│
├── utility/            # Utility classes
│   ├── BuildCacheManager.java
│   ├── PerformanceMonitor.java
│   ├── FileUtil.java
│   └── SketchwareUtil.java
│
├── beans/             # Data models
│   ├── ProjectBean.java
│   ├── ViewBean.java
│   └── ComponentBean.java
│
└── lib/               # Internal libraries
    ├── highlighter/   # Syntax highlighting
    └── base/          # Base classes

mod/                   # Community modifications
├── jbk/              # JBK's contributions
│   ├── build/        # Build system enhancements
│   ├── editor/       # Editor improvements
│   └── util/         # Utilities
│
├── pranav/           # Pranav's contributions
│   ├── build/        # R8 compiler integration
│   └── viewbinding/  # View binding support
│
├── hey/              # Hey Studios contributions
│   └── studios/      # Various features
│
└── agus/             # Agus's contributions
    └── jcoderz/      # DEX manipulation tools
```

## Build System

### Build Pipeline

```
┌──────────────┐
│ Source Files │
└──────┬───────┘
       │
       ▼
┌──────────────────┐
│ Resource Compile │ ──► AAPT2
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  Java Compile    │ ──► ECJ (Eclipse Compiler)
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│ Kotlin Compile   │ ──► Kotlinc (if applicable)
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│   DEX Compile    │ ──► D8/R8
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│  APK Builder     │ ──► APKBuilder
└──────┬───────────┘
       │
       ▼
┌──────────────────┐
│   Sign & Align   │ ──► ZipAlign
└──────────────────┘
```

### Build Performance Optimizations

1. **Incremental Compilation**
   - Cache compiled classes
   - Only recompile changed files
   - Track dependencies

2. **Parallel Processing**
   - Resource compilation
   - Multiple Java file compilation
   - DEX file generation

3. **Build Cache**
   ```java
   BuildCacheManager cache = BuildCacheManager.getInstance(context);
   String key = cache.generateCacheKey(sourceFile);
   if (cache.isCached(key)) {
       return cache.get(key, CompiledClass.class);
   }
   ```

## Editor System

### Code Editor Architecture

```
CodeEditorActivity
       │
       ├─► SoraEditor (Main editor component)
       │      ├─► TextMate Grammar
       │      ├─► Language Support
       │      └─► Syntax Highlighting
       │
       ├─► AutoComplete
       │      ├─► Identifier Analysis
       │      └─► Suggestion Provider
       │
       └─► ErrorChecker
              ├─► Syntax Validation
              └─► Semantic Analysis
```

### Syntax Highlighting

Uses TextMate grammars for accurate syntax highlighting:

```java
// Language configuration
language-textmate/
├── java.tmLanguage.json
├── kotlin.tmLanguage.json
├── xml.tmLanguage.json
└── themes/
    ├── darcula.json
    └── light.json
```

## Project Management

### Project Structure

```
Sketchware Projects/
└── [project_id]/
    ├── project              # Project metadata
    ├── file                 # File list
    ├── library              # Library configuration
    ├── resource             # Resource definitions
    ├── logic/              # Logic files
    │   └── [screen_id].java
    ├── view/               # View definitions
    │   └── [screen_id].xml
    └── data/              # Project data
```

### Data Models

```java
// Project Bean
class ProjectBean {
    String id;
    String name;
    String packageName;
    int minSdk;
    int targetSdk;
    List<ActivityBean> activities;
}

// Activity Bean
class ActivityBean {
    String name;
    String javaName;
    String xmlName;
    List<ViewBean> views;
    List<ComponentBean> components;
}
```

## Data Flow

### Project Creation Flow

```
User Input
    │
    ▼
Validate Project Info
    │
    ▼
Create Project Structure
    │
    ▼
Generate Initial Files
    │
    ▼
Save Project Metadata
    │
    ▼
Open Project
```

### Build Flow

```
User Triggers Build
    │
    ▼
Initialize BuildProgressReceiver
    │
    ▼
Start PerformanceMonitor
    │
    ├─► Check BuildCache
    │
    ├─► Compile Resources (parallel)
    │
    ├─► Compile Java/Kotlin (parallel)
    │
    ├─► Generate DEX files
    │
    ├─► Build APK
    │
    └─► Sign & Align
        │
        ▼
    Report Success/Failure
```

## Design Patterns

### 1. Singleton Pattern

Used for managers and utilities:

```java
public class BuildCacheManager {
    private static BuildCacheManager instance;
    
    public static synchronized BuildCacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new BuildCacheManager(context);
        }
        return instance;
    }
}
```

### 2. Builder Pattern

Used for complex object creation:

```java
ProjectBuilder builder = new ProjectBuilder(context)
    .setOutputPath(outputDir)
    .setProguardEnabled(true)
    .setMinifyEnabled(false)
    .build();
```

### 3. Observer Pattern

Used for build progress updates:

```java
interface BuildProgressReceiver {
    void onProgress(String message, int progress);
    void onError(String error);
    void onComplete(File apkFile);
}
```

### 4. Strategy Pattern

Used for different compilation strategies:

```java
interface CompilationStrategy {
    void compile(List<File> sourceFiles);
}

class JavaCompilationStrategy implements CompilationStrategy { }
class KotlinCompilationStrategy implements CompilationStrategy { }
```

### 5. Factory Pattern

Used for creating different types of components:

```java
class ComponentFactory {
    public static Component createComponent(String type) {
        switch (type) {
            case "button": return new ButtonComponent();
            case "textview": return new TextViewComponent();
            // ...
        }
    }
}
```

## Performance Considerations

### Memory Management

1. **Use WeakReferences for large objects**
2. **Implement proper cache eviction**
3. **Release resources in onDestroy()**
4. **Use RecyclerView for lists**

### Build Performance

1. **Incremental compilation** - Only compile changed files
2. **Parallel processing** - Use multiple threads
3. **Cache reuse** - Store compiled artifacts
4. **Resource optimization** - Compress and optimize resources

### Example: Performance Monitoring

```java
PerformanceMonitor monitor = PerformanceMonitor.getInstance(context);

// Track operation
try (PerformanceMonitor.AutoTracker tracker = monitor.track("build_project")) {
    // Build code here
}

// Generate report
String report = monitor.generateReport();
Log.d(TAG, report);
```

## Security Considerations

### Code Obfuscation

ProGuard/R8 rules are configured to:
- Obfuscate class names
- Remove unused code
- Optimize bytecode
- Keep necessary reflection-based code

### Data Protection

1. **Sensitive data encryption**
2. **Secure key storage**
3. **Permission management**
4. **Input validation**

## Testing Strategy

### Unit Tests

```java
@Test
public void testProjectBuilder() {
    ProjectBuilder builder = new ProjectBuilder(context);
    Result result = builder.compile(testProject);
    assertTrue(result.isSuccess());
}
```

### Integration Tests

```java
@Test
public void testFullBuildCycle() {
    // Create project
    Project project = createTestProject();
    
    // Build
    File apk = buildProject(project);
    
    // Verify APK
    assertTrue(apk.exists());
    assertTrue(isValidApk(apk));
}
```

## Future Architecture Improvements

### Planned Enhancements

1. **Modular Architecture**
   - Feature modules
   - Dynamic delivery
   - Better separation of concerns

2. **Clean Architecture**
   - Domain layer
   - Data layer
   - Presentation layer separation

3. **Dependency Injection**
   - Hilt/Dagger integration
   - Better testability
   - Loose coupling

4. **MVVM Pattern**
   - ViewModel implementation
   - LiveData/Flow usage
   - Better UI state management

## References

- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Design Patterns](https://refactoring.guru/design-patterns)

---

**Document Version**: 1.0  
**Last Updated**: November 2024  
**Maintainer**: Sketchware Plus Team
