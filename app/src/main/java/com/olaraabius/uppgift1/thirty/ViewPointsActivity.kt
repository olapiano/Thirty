package com.olaraabius.uppgift1.thirty

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.olaraabius.uppgift1.thirty.model.PointsCategory
import com.olaraabius.uppgift1.thirty.ui.theme.ThirtyTheme
import androidx.compose.runtime.remember

/**
 * An activity that shows the current points registered
 * and gives the possibility to restart game
 *
 * @author Ola Råbius Magnusson
 * @since 2023-06-26
 */

private const val TAG = "ViewPointsActivity"

private const val PACKAGE = "com.olaraabius.uppgift1.thirty"
private const val EXTRA_POINTS_LIST = "$PACKAGE.points"

private var pointsList: MutableList<Int> = mutableListOf()

class ViewPointsActivity() : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate()")

        /** points received from Main Activity */
        pointsList = intent.getIntegerArrayListExtra(EXTRA_POINTS_LIST) as MutableList<Int>

        Log.d(TAG, "$pointsList")
        // pointsList = intent.extras?.getIntegerArrayList(EXTRA_POINTS_LIST)!!
        setContent {
            ThirtyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PointsLayout(this::setResult)
                }
            }
        }
    }

    companion object {
        fun newIntent(packageContext: Context, points: List<Int>): Intent {
            return Intent(packageContext, ViewPointsActivity::class.java).apply {
                putIntegerArrayListExtra(EXTRA_POINTS_LIST, points as ArrayList<Int>)
            }
        }
    }
}

/*
 The parent of the different layouts
 Chooses between 2 child layouts:
 - PortraitLayout
 - LandscapeLayout
 depending on screen width
 */
@Composable
fun PointsLayout(setResult:(resultCode: Int, data: Intent)->Unit) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    val (points, setPoints) = remember { mutableStateOf(pointsList) }

    if (screenWidth < 600.dp) {
        PortraitLayout(context, points, setPoints, setResult)
    } else {
        LandscapeLayout(context, points, setPoints, setResult)
    }
}

/*
    2 layouts: PortraitLayout and LandscapeLayout that contains
    - List with the points registered and total sum, received from MainActivity
    - Back-button: Opens MainActivity
    - Restart-button: Opens MainActivity and sends the value "restart": true
 */
@Composable
fun LandscapeLayout(context: Context, points: MutableList<Int>, setPoints:(MutableList<Int>) -> Unit, setResult: (resultCode: Int, data: Intent) -> Unit) {
    
    Row (
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 6.dp)
    ) {
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 18.dp)
                .weight(1F)
        ) {
            PointsList(points)
        }

        Column (
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight()
        ) {
            Buttons(context, setPoints, setResult)
        }
    }
}

@Composable
fun PortraitLayout(context: Context, points: MutableList<Int>, setPoints:(MutableList<Int>) -> Unit, setResult: (resultCode: Int, data: Intent) -> Unit) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 64.dp, bottom = 24.dp)
    ) {
        Column (modifier = Modifier.padding(start = 18.dp)){
            PointsList(points)
        }

        Buttons(context, setPoints, setResult)
    }
}

private fun restartDialog(context: Context, setPoints:(MutableList<Int>) -> Unit, setResult: (resultCode: Int, data: Intent) -> Unit) {
    val builder = AlertDialog.Builder(context)
    val data = Intent()
    builder
        .setMessage(context.resources.getString(R.string.dialog_restart_game_message))
        .setTitle(context.resources.getString(R.string.alert_message_title))
        .setCancelable(false)
        .setPositiveButton(context.resources.getString(R.string.affirmative_button)) { _, _ ->
            setResult(Activity.RESULT_OK, data)
            setPoints(PointsCategory.values().map { 0 } as MutableList<Int>)
        }
        .setNegativeButton(context.resources.getString(R.string.no_button)) { _, _ ->
            setResult(Activity.RESULT_CANCELED, data)
        }
    val alertDialog = builder.create()
    alertDialog.show()
}

/*
    Restart-button
 */
@Composable
fun Buttons(context: Context, setPoints:(MutableList<Int>) -> Unit, setResult:(resultCode: Int, data: Intent)->Unit) {
    Row (
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(onClick = {
            restartDialog(context, setPoints, setResult)
        },
            modifier = Modifier
                .weight(1F)
                .padding(6.dp)
        ) {
            Text (stringResource(R.string.restart_button))
        }
    }

}

/*
    List of registered points
    See comment after PointsLayout()
 */
@Composable
fun PointsList(points: MutableList<Int>) {
    PointsCategory.values().map {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .width(150.dp)

        ) {
            Text ("$it")
            Text ("${points[it.ordinal]}")
        }
    }
    Divider(modifier = Modifier.width(150.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .width(150.dp)
    )  {
        Text (stringResource(R.string.total_points_text))
        Text ("${points.sum()}")
    }
}

@Preview(showBackground = true)
@Composable
fun PointsLayoutPreview() {
    ThirtyTheme {
        // PointsLayout()
    }
}

