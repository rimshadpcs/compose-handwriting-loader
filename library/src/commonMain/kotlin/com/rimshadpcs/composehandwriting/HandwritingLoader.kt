package com.rimshadpcs.composehandwriting

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

/**
 * An animated "handwriting" reveal: [text] appears as if it's being written left to right, with
 * a slanted ink-wipe edge and a small traveling pen tip. Runs once per unique [key] (default:
 * once per [text] value) and calls [onFinished] when the reveal completes — handy for a splash
 * screen that should navigate on, a loading indicator, or a one-off hero animation.
 *
 * Every visual knob is a parameter — there's no hidden app-specific styling. All platform
 * defaults come from whatever [FontFamily]/[Color] Compose resolves in the caller's own context
 * ([FontFamily.Default], [LocalContentColor]), so this reads correctly in both a light and dark
 * theme out of the box, and matches the caller's own font unless overridden.
 *
 * @param text The string to reveal. Change this (or [key]) to re-run the animation.
 * @param modifier Applied to the outer container.
 * @param key Identity that controls when the animation restarts. Defaults to [text] itself, so
 * changing the text automatically replays the reveal; pass a separate counter/UUID if you want to
 * replay the *same* text (e.g. a "tap to retry" loader).
 * @param fontFamily Typeface for the revealed text. Defaults to [FontFamily.Default].
 * @param fontSize Size of the revealed text. Defaults to 48sp.
 * @param fontWeight Weight of the revealed text. Defaults to [FontWeight.Normal].
 * @param color Ink color for both the text and the pen tip/glow. Defaults to
 * [LocalContentColor.current], which follows the caller's Material theme automatically.
 * @param durationMillis How long the full reveal takes, start to finish.
 * @param easing Timing curve for the reveal. Defaults to [FastOutSlowInEasing].
 * @param penTipRadius Radius of the small dot drawn at the writing edge. Set to 0.dp to hide it.
 * @param penGlowRadius Radius of the soft glow behind the pen tip. Set to 0.dp to hide it.
 * @param autoStart When false, the composable renders [text] fully revealed immediately and never
 * animates — useful for previews/tests, or a "reduce motion" accessibility setting.
 * @param onFinished Called once, on the main thread, when a reveal completes. Not called at all
 * when [autoStart] is false.
 */
@Composable
public fun HandwritingLoader(
    text: String,
    modifier: Modifier = Modifier,
    key: Any? = text,
    fontFamily: FontFamily = FontFamily.Default,
    fontSize: TextUnit = 48.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = LocalContentColor.current,
    durationMillis: Int = 1500,
    easing: Easing = FastOutSlowInEasing,
    penTipRadius: Dp = 3.dp,
    penGlowRadius: Dp = 40.dp,
    autoStart: Boolean = true,
    onFinished: (() -> Unit)? = null,
) {
    val progressAnimatable = remember(key) { Animatable(if (autoStart) 0f else 1f) }

    LaunchedEffect(key, autoStart) {
        if (!autoStart) return@LaunchedEffect
        progressAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis, easing = easing),
        )
        onFinished?.invoke()
    }

    val progress = progressAnimatable.value

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Text and pen tip share one Box so both read the same coordinate space/size.
        Box(modifier = Modifier.wrapContentSize()) {
            // The reveal itself: a slanted clip path sweeping left to right, so the leading edge
            // looks like ink spreading rather than a hard vertical wipe.
            Box(
                modifier = Modifier.drawWithContent {
                    val width = size.width
                    val currentX = width * progress
                    val slant = size.height.coerceAtMost(60f) * 0.5f

                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(currentX + slant, 0f)
                        lineTo(currentX - slant, size.height)
                        lineTo(0f, size.height)
                        close()
                    }

                    clipPath(path) {
                        this@drawWithContent.drawContent()
                    }
                }
            ) {
                Text(
                    text = text,
                    fontFamily = fontFamily,
                    fontSize = fontSize,
                    fontWeight = fontWeight,
                    color = color,
                )
            }

            // The traveling pen tip, with a subtle sine-wave bob for a hand-written feel — only
            // drawn mid-reveal, and only if the caller hasn't zeroed out both radii.
            if (penTipRadius > 0.dp || penGlowRadius > 0.dp) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    if (progress <= 0.01f || progress >= 0.99f) return@Canvas

                    val currentX = size.width * progress
                    val verticalOffset = sin(progress * 15f) * 10f
                    val penPos = Offset(currentX, size.height / 2 + verticalOffset)

                    if (penGlowRadius > 0.dp) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(color.copy(alpha = 0.3f), Color.Transparent),
                                center = penPos,
                                radius = penGlowRadius.toPx(),
                            ),
                            center = penPos,
                            radius = penGlowRadius.toPx(),
                        )
                    }
                    if (penTipRadius > 0.dp) {
                        drawCircle(color = color, center = penPos, radius = penTipRadius.toPx())
                    }
                }
            }
        }
    }
}
