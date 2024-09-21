@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMotionApi::class,
    ExperimentalMaterial3Api::class
)

package uz.turgunboyevjurabek.motionlayoutexample

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene

@SuppressLint("UnrememberedMutableState")
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

    val lazyListState = rememberLazyListState()

    // Track the scroll position of LazyColumn
    val isAtTop by derivedStateOf { lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0 }

    LaunchedEffect(isAtTop) {
        // Update anchoredDraggableState to trigger MotionLayout when LazyColumn is scrolled to the top
        if (isAtTop) {
            anchoredDraggableState.animateTo(AnchoredDraggableCardState.DRAGGED_DOWN)
        } else {
            anchoredDraggableState.animateTo(AnchoredDraggableCardState.DRAGGED_UP)
        }
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
                .fillMaxWidth()
                .background(Color.DarkGray)
                .layoutId("box")
                .anchoredDraggable(
                    state = anchoredDraggableState,
                    orientation = Orientation.Vertical,
                    reverseDirection = true // Yo'nalishni invers qildik
                )
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
            modifier= Modifier
                .fillMaxWidth()
                .layoutId("actions"),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                state = lazyListState
            ) {
               items(15){
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
        ConstraintLayout(
            modifier = modifier
                .fillMaxSize()
        ) {
            val (image, title, description) = createRefs()
            Image(
                painter = painterResource(id = R.drawable.jetpack_compose),
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .constrainAs(image) {
                        start.linkTo(parent.start, 5.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            )
            Text(
                text = "Jetpack Compose",
                style = TextStyle(
                    fontSize = 18.sp,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    fontStyle = FontStyle.Normal,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier
                    .constrainAs(title) {
                        start.linkTo(image.end, 10.dp)
                        top.linkTo(parent.top,10.dp)
                    }
            )
            Text(
                text = "This is a Jetpack Compose card",
                style = TextStyle(
                    fontSize = 15.sp,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium
                ),
                modifier = modifier
                    .constrainAs(description){
                        start.linkTo(image.end, 5.dp)
                        top.linkTo(title.bottom, 5.dp)
                    }
            )


        }
    }

}
