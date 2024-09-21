@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMotionApi::class)

package uz.turgunboyevjurabek.motionlayoutexample

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene

@Composable
fun MyScreen(modifier: Modifier) {
    val context = LocalContext.current

    val motionSceneContent= remember {
        context.resources
            .openRawResource(R.raw.motion_scane)
            .readBytes()
            .decodeToString()
    }
    val draggedDownAnchorTop= with(LocalDensity.current){200.dp.toPx()}

    val anchors= DraggableAnchors {
        AnchoredDraggableCardState.DRAGGED_UP at draggedDownAnchorTop
        AnchoredDraggableCardState.DRAGGED_DOWN at 0f
    }

    val  density= LocalDensity.current
    val anchoredDraggableState = remember {
        AnchoredDraggableState(
            initialValue = AnchoredDraggableCardState.DRAGGED_UP,
            anchors = anchors,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            animationSpec = tween()
        )
    }
    val offset = if (anchoredDraggableState.offset.isNaN()) 0f else anchoredDraggableState.offset
    val progress = (1 - (offset / draggedDownAnchorTop)).coerceIn(0f, 1f)

    MotionLayout(
        motionScene = MotionScene(content = motionSceneContent),
        progress = progress,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val properties = motionProperties(id = "profile_pic")
        Box(
            modifier = Modifier
                .anchoredDraggable(
                    state = anchoredDraggableState,
                    orientation = Orientation.Vertical,
                    reverseDirection = true // Yo'nalishni invers qildik
                )
                .fillMaxWidth()
                .background(Color.DarkGray)
                .layoutId("box")
        )
        Image(
            painter = painterResource(id = R.drawable.jetpack_compose),
            contentDescription = null,
            modifier = Modifier
                .layoutId("profile_pic")
                .clip(CircleShape)
                .border(
                    2.dp,
                    properties.value.color("background"),
                    shape = CircleShape
                )
        )
        Text(
            text = "Jetpack Compose",
            fontSize = 24.sp,
            color = properties.value.color("background"),
            modifier = Modifier
                .layoutId("username")
        )
        Column(
            modifier=Modifier
                .layoutId("actions"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn {
               items(50){
                   ListUI()
               }
            }
        }

    }

}

@Composable
fun ListUI(modifier: Modifier = Modifier) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(
                horizontal = 15.dp,
                vertical = 10.dp
            ),
        shape = RoundedCornerShape(16.dp),
    ) {

    }

}
