# Compose Handwriting Loader

An animated "handwriting" text-reveal loading indicator for [Compose
Multiplatform](https://www.jetbrains.com/compose-multiplatform/) — Android and iOS from one
Kotlin implementation. Text appears as if it's being written left to right, with a slanted ink-wipe
edge and a small traveling pen tip.

Originally built as the splash-screen animation for [Satia](https://satia.app), pulled out into
its own library so it's reusable (and configurable) beyond that one app.

## Install

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.rimshadpcs.composehandwriting:compose-handwriting-loader:0.1.0")
}
```

## Usage

```kotlin
HandwritingLoader(
    text = "satia",
    fontSize = 60.sp,
    color = Color.Black,
    durationMillis = 1500,
)
```

That's the whole API surface for a basic splash screen. Every visual knob is a parameter — there's
no hidden app-specific styling baked in:

```kotlin
HandwritingLoader(
    text = "loading…",
    fontFamily = MyBrandFont,
    fontSize = 32.sp,
    fontWeight = FontWeight.Medium,
    color = MaterialTheme.colorScheme.primary,
    durationMillis = 900,
    penTipRadius = 2.dp,     // 0.dp hides the pen tip entirely
    penGlowRadius = 24.dp,   // 0.dp hides the glow entirely
    onFinished = { navController.navigate("home") },
)
```

Changing `text` automatically replays the animation. To replay the *same* text (e.g. a "tap to
retry" loader), pass a separate `key`:

```kotlin
var replayCount by remember { mutableIntStateOf(0) }

HandwritingLoader(
    text = "retry",
    key = replayCount,
    onFinished = { /* ... */ },
)

Button(onClick = { replayCount++ }) { Text("Replay") }
```

Set `autoStart = false` to render the text fully revealed with no animation at all — useful for
previews, tests, or a "reduce motion" accessibility setting.

## Sample

The `sample` module is a small Android app with live controls for text, font size, duration, and
color — clone the repo and run it to try different combinations before picking values for your own
app.

## Why this exists

Built for [RevenueCat's Shipaton](https://www.revenuecat.com/shipaton/) — extracted from a real
production app's splash screen rather than written from scratch as a demo, so the animation itself
has already been through real design iteration (see the `library` module's `HandwritingLoader.kt`
for the reveal/pen-tip implementation notes).

## License

Apache License 2.0 — see [LICENSE](LICENSE).
