@file:Suppress("TestFunctionName")

package me.saket.squiggles

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams.RenderingMode
import kotlinx.coroutines.delay
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
class SquigglySliderTest {
  @get:Rule val paparazzi = Paparazzi(
    deviceConfig = DeviceConfig.PIXEL_5,
    renderingMode = RenderingMode.SHRINK,
  )

  @Test fun `flatten squiggles when slider is dragged`() {
    paparazzi.gif(2.seconds) {
      Scaffold {
        val interactionSource = remember { MutableInteractionSource() }
        SquigglySlider(
          value = 0.5f,
          onValueChange = {},
          interactionSource = interactionSource,
          squigglesSpec = SquigglySlider.SquigglesSpec(
            wavelength = 48.dp,
            amplitude = 4.dp,
            strokeWidth = 8.dp,
          )
        )

        // Mimic a drag gesture on the slider.
        LaunchedEffect(Unit) {
          delay(0.5.seconds)
          val start = DragInteraction.Start()
          interactionSource.emit(start)
          delay(1.seconds)
          interactionSource.emit(DragInteraction.Stop(start))
        }
      }
    }
  }

  @Test fun `zero slider value`() {
    paparazzi.snapshot {
      Scaffold {
        SquigglySlider(
          value = 0f,
          onValueChange = {},
        )
      }
    }
  }

  @Test fun `zero amplitude`() {
    paparazzi.snapshot {
      Scaffold {
        SquigglySlider(
          value = 0.5f,
          onValueChange = {},
          squigglesSpec = SquigglySlider.SquigglesSpec(
            amplitude = 0.dp
          ),
          squigglesAnimator = SquigglySlider.SquigglesAnimator(animationProgress = stateOf(0.5f)),
        )
      }
    }
  }

  @Test fun `non-zero amplitude`() {
    paparazzi.snapshot {
      Scaffold {
        SquigglySlider(
          value = 0.5f,
          onValueChange = {},
          squigglesSpec = SquigglySlider.SquigglesSpec(
            amplitude = 2.dp,
            wavelength = 24.dp,
          ),
          squigglesAnimator = SquigglySlider.SquigglesAnimator(animationProgress = stateOf(0.5f)),
        )
      }
    }
  }

  @Test fun `over-sized stroke width`() {
    paparazzi.snapshot {
      Scaffold {
        SquigglySlider(
          value = 0.6f,
          onValueChange = {},
          squigglesSpec = SquigglySlider.SquigglesSpec(strokeWidth = 30.dp),
          squigglesAnimator = SquigglySlider.SquigglesAnimator(animationProgress = stateOf(1f)),
        )
      }
    }
  }

  @Test fun `super-thin stroke width`() {
    paparazzi.snapshot {
      Scaffold {
        SquigglySlider(
          value = 0.6f,
          onValueChange = {},
          squigglesSpec = SquigglySlider.SquigglesSpec(strokeWidth = 1.dp),
          squigglesAnimator = SquigglySlider.SquigglesAnimator(animationProgress = stateOf(1f)),
        )
      }
    }
  }

  @Composable
  private fun Scaffold(content: @Composable BoxScope.() -> Unit) {
    MaterialTheme(
      colorScheme = dynamicDarkColorScheme(LocalContext.current)
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(MaterialTheme.colorScheme.surface)
          .padding(vertical = 24.dp, horizontal = 16.dp),
        content = content,
        contentAlignment = Alignment.Center
      )
    }
  }
}

private fun <T> stateOf(value: T): State<T> {
  return mutableStateOf(value)
}

private fun Paparazzi.gif(
  duration: Duration = 500.milliseconds,
  fps: Int = 60,
  content: @Composable () -> Unit,
) {
  val view = ComposeView(context)
  view.setContent(content)
  gif(
    view = view,
    end = duration.inWholeMilliseconds,
    fps = fps,
  )
}
