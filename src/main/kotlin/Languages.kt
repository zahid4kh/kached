import dev.snipme.highlights.model.SyntaxLanguage

enum class Languages {
    NONE,
    C,
    CPP,
    DART,
    JAVA,
    KOTLIN,
    RUST,
    CSHARP,
    JAVASCRIPT,
    PERL,
    PYTHON,
    RUBY,
    SHELL,
    SWIFT,
    TYPESCRIPT,
    GO,
    PHP
}

fun Languages.toSyntaxLanguage(): SyntaxLanguage {
    return when (this) {
        Languages.NONE -> SyntaxLanguage.DEFAULT
        Languages.C -> SyntaxLanguage.C
        Languages.CPP -> SyntaxLanguage.CPP
        Languages.DART -> SyntaxLanguage.DART
        Languages.JAVA -> SyntaxLanguage.JAVA
        Languages.KOTLIN -> SyntaxLanguage.KOTLIN
        Languages.RUST -> SyntaxLanguage.RUST
        Languages.CSHARP -> SyntaxLanguage.CSHARP
        Languages.JAVASCRIPT -> SyntaxLanguage.JAVASCRIPT
        Languages.PERL -> SyntaxLanguage.PERL
        Languages.PYTHON -> SyntaxLanguage.PYTHON
        Languages.RUBY -> SyntaxLanguage.RUBY
        Languages.SHELL -> SyntaxLanguage.SHELL
        Languages.SWIFT -> SyntaxLanguage.SWIFT
        Languages.TYPESCRIPT -> SyntaxLanguage.TYPESCRIPT
        Languages.GO -> SyntaxLanguage.GO
        Languages.PHP -> SyntaxLanguage.PHP
    }
}

fun Languages.getFileExtension(): String {
    return when (this) {
        Languages.C -> ".c"
        Languages.CPP -> ".cpp"
        Languages.DART -> ".dart"
        Languages.JAVA -> ".java"
        Languages.KOTLIN -> ".kt"
        Languages.RUST -> ".rs"
        Languages.CSHARP -> ".cs"
        Languages.JAVASCRIPT -> ".js"
        Languages.PERL -> ".pl"
        Languages.PYTHON -> ".py"
        Languages.RUBY -> ".rb"
        Languages.SHELL -> ".sh"
        Languages.SWIFT -> ".swift"
        Languages.TYPESCRIPT -> ".ts"
        Languages.GO -> ".go"
        Languages.PHP -> ".php"
        Languages.NONE -> ".txt"
    }
}