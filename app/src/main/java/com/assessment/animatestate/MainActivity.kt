package com.assessment.animatestate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.assessment.animatestate.ui.theme.AnimateStateTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.animation.core.Spring.DampingRatioHighBouncy
import androidx.compose.animation.core.Spring.StiffnessVeryLow
import androidx.compose.animation.animateColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimateStateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RotationDemo(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

enum class BoxColor {Yellow, Magenta}

@Composable
fun RotationDemo(modifier: Modifier = Modifier) {
    var rotated by remember { mutableStateOf(false) }
    val angle by animateFloatAsState(targetValue = if (rotated) 360f else 0f,
                                     animationSpec = tween(durationMillis = 2500,
                                                           easing = LinearEasing),
                                     label = "Rotate")
    
    Column(horizontalAlignment = Alignment.CenterHorizontally,
           modifier = modifier.fillMaxWidth()) {
        Image(painter = painterResource(R.drawable.propeller),
              contentDescription = "fan",
              modifier = Modifier.rotate(angle).padding(10.dp).size(300.dp))
        Button(onClick = { rotated = !rotated },
               modifier = Modifier.padding(10.dp)) {
            Text(text = "Rotate Propeller")
        }
    }
}


@Composable
fun ColorChangeDemo(modifier: Modifier = Modifier) {
    var colorState by remember { mutableStateOf(BoxColor.Yellow) }
    val animatedColor: Color by animateColorAsState(targetValue = when (colorState) {
                                                                    BoxColor.Yellow -> Color.Magenta
                                                                    BoxColor.Magenta -> Color.Yellow},
                                                    animationSpec = tween(4500), label = "ColorChange")

    Column(horizontalAlignment = Alignment.CenterHorizontally,
           modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.padding(20.dp).size(200.dp).background(animatedColor))
        Button(onClick = {colorState = when (colorState) {
                                            BoxColor.Yellow -> BoxColor.Magenta
                                            BoxColor.Magenta -> BoxColor.Yellow }},
               modifier = Modifier.padding(10.dp)) {
                    Text(text = "Change Color")
        }
    }
}

enum class BoxPosition {Start, End}

@Composable
fun MotionDemo(modifier: Modifier = Modifier){
    val screenWidth = (LocalConfiguration.current.screenWidthDp.dp)
    var boxState by remember { mutableStateOf(BoxPosition.Start) }
    val boxSideLength = 70.dp

    /*Spring behavior adds a bounce effect to animations and is applied using the
      spring() function via the animationSpec parameter. The two key parameters
      to the spring() function are damping ratio and stiffness . The damping ratio
      defines the speed at which the bouncing effect decays and is declared as a Float value.
      The stiffness parameter defines the strength of the spring. When using a lower stiffness,
      the range of motion of the bouncing effect will be greater.*/
    /*val animatedOffset: Dp by animateDpAsState(targetValue = when (boxState) {
                                                                BoxPosition.Start -> 0.dp
                                                                BoxPosition.End -> screenWidth - boxSideLength },
                                                animationSpec = spring(dampingRatio = DampingRatioHighBouncy,
                                                                       stiffness = StiffnessVeryLow),
                                                label = "Motion")*/
     /*Keyframes allow different duration and easing values to be applied at specific points
      in an animation timeline. A keyframe specification begins by declaring the total
      required duration for the entire animation to complete. That duration is then marked
      by timestamps declaring how much of the total animation should be completed at that
      point based on the state unit type (for example Float, Dp, Int, etc.). These timestamps
      are created via calls to the at() function.*/
      val animatedOffset: Dp by animateDpAsState(targetValue = when (boxState) {
                                                                BoxPosition.Start -> 0.dp
                                                                BoxPosition.End -> screenWidth - boxSideLength },
                                                 animationSpec = keyframes {durationMillis = 1000
                                                                            100.dp.at(10) using LinearEasing
                                                                            110.dp.at(500) using FastOutSlowInEasing
                                                                            200.dp.at(700) using LinearOutSlowInEasing},
                                                 label = "Motion")

    Column(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.offset(x = animatedOffset, y = 20.dp)
                               .size(boxSideLength)
                               .background(Color.Magenta))
        Spacer(modifier = Modifier.height(50.dp))
        Button(onClick = { boxState = when (boxState){
                                        BoxPosition.Start -> BoxPosition.End
                                        BoxPosition.End -> BoxPosition.Start }},
               modifier = Modifier.padding(20.dp).align(Alignment.CenterHorizontally)){
            Text(text = "Move Box")
        }
    }
}

/*Multiple animations can be run in parallel based on a single target state using
the updateTransition() function. This function is passed the target state and returns
a Transition instance to which multiple child animations may be added. When the target state
changes, the transition will run all of the child animations concurrently.
To demonstrate updateTransition in action, we will modify the example to perform both
the color change and motion animations based on changes to the boxState value.
The Transition class includes a collection of functions that are used to add animation
to children. These functions use the naming convention of animate<Type>() depending on
the unit type used for the animation such as animateFloat(), animateDp() and animateColor().*/
@Composable
fun TransitionDemo(modifier: Modifier = Modifier){
    var boxState by remember { mutableStateOf(BoxPosition.Start)}
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val transition = updateTransition(targetState = boxState, label = "Color and Motion")

    val animatedColor: Color by transition.animateColor(transitionSpec = {tween(4000)},
                                                        label = "colorAnimation") { state ->  when (state) {
                                                                                                BoxPosition.Start -> Color.Yellow
                                                                                                BoxPosition.End -> Color.Magenta }
    }
    val animatedOffset: Dp by transition.animateDp(transitionSpec = {tween(4000)},
                                                   label = "offsetAnimation") { state -> when (state) {
                                                                                            BoxPosition.Start -> 0.dp
                                                                                            BoxPosition.End -> screenWidth - 70.dp }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.offset(x = animatedOffset, y = 20.dp).size(70.dp).background(animatedColor))
        Spacer(modifier = Modifier.height(50.dp))
        Button(onClick = { boxState = when (boxState) {
                                        BoxPosition.Start -> BoxPosition.End
                                        BoxPosition.End -> BoxPosition.Start }},
               modifier = Modifier.padding(20.dp).align(Alignment.CenterHorizontally)) {
            Text(text = "Start Animation")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RotationPreview() {
    AnimateStateTheme {
        //RotationDemo()
        //ColorChangeDemo()
        //MotionDemo()
        TransitionDemo()
    }
}