# Kached

A simple desktop application for managing code snippets offline. Built with Kotlin and Compose for Desktop.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.1.20-blue.svg?logo=kotlin)](https://kotlinlang.org) [![Compose](https://img.shields.io/badge/Compose-1.8.0-blue.svg?logo=jetpack-compose)](https://www.jetbrains.com/lp/compose-multiplatform/)

## What it does

- **Store code snippets** with title, description, and code (with optionally selectable language)
- **Syntax highlighting** for 17+ programming languages (Kotlin, Java, Python, JavaScript, C++, etc.)
- **Export snippets** as `.txt`, `.md`, or language-specific files (`.kt`, `.py`, `.js`, etc.)
- **Dark/light mode** support (Material3 Theming)
- **Offline storage** - all data saved locally
- **Cross-platform** - Windows, Linux

## Screenshots

The app has a simple interface with a main screen, snippet creation form, and a dynamic grid view of all your saved snippets.

## Build from source

### Prerequisites

- JDK 17 or later
- Kotlin 2.1.20 or later

### Make Gradle Wrapper Executable (Linux/macOS)

```bash
chmod +x gradlew
```

### Running the Application

**Standard run:**
```bash
./gradlew run
```

**With hot reload (for development):**
```bash
./gradlew :runHot --mainClass Kached --auto
```

### Building Native Distribution

Build a native installer for your platform:

```bash
./gradlew packageDistributionForCurrentOS
```

**Platform-specific builds:**
- `./gradlew packageDmg` - macOS DMG
- `./gradlew packageMsi` - Windows MSI
- `./gradlew packageDeb` - Linux DEB
- `./gradlew packageExe` - Windows EXE

## Usage

1. **Add snippets** - Click "New Snippet", enter title, description, and code
2. **Choose language** - Select from the dropdown for proper syntax highlighting
3. **View snippets** - Click "View All Snippets" to see your collection
4. **Export snippets** - Use the download icon to export as text, markdown, or source files
5. **Expand view** - Click the maximize icon for full syntax-highlighted view

## Data Storage

All snippets and settings are stored locally in:
- **Linux/macOS:** `~/.kached/`
- **Windows:** `%USERPROFILE%\.kached\`

Your data stays on your machine - no internet required.

## Supported Languages

C, C++, C#, Dart, Go, Java, JavaScript, Kotlin, Perl, PHP, Python, Ruby, Rust, Shell, Swift, TypeScript

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](LICENSE) for details.