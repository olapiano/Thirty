package com.olaraabius.uppgift1.thirty

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.olaraabius.uppgift1.thirty.model.PointsCategory
import com.olaraabius.uppgift1.thirty.ui.theme.ThirtyTheme

/**
 * An activity that shows the current points registered
 * and gives the possibility to restart game
 *
 * @author Ola Råbius Magnusson
 * @since 2023-06-26
 */

// private const val TAG = "ViewPointsActivity"

private var pointsList: MutableList<Int> = mutableListOf()

class ViewPointsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** points received from Main Activity */
        pointsList = intent.extras?.getIntegerArrayList("points")!!

        setContent {
            ThirtyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PointsLayout()
                }
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
fun PointsLayout() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    if (screenWidth < 600.dp) {
        PortraitLayout(context)
    } else {
        LandscapeLayout(context)
    }
}

/*
    2 layouts: PortraitLayout and LandscapeLayout that contains
    - List with the points registered and total sum, received from MainActivity
    - Back-button: Opens MainActivity
    - Restart-button: Opens MainActivity and sends the value "restart": true
 */
@Composable
fun LandscapeLayout(context: Context) {
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
            PointsList()
        }

        Column (
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight()
        ) {
            Buttons(context)
        }
    }
}

@Composable
fun PortraitLayout(context: Context) {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(top = 64.dp, bottom = 24.dp)
    ) {
        Column (modifier = Modifier.padding(start = 18.dp)){
            PointsList()
        }
        Buttons(context)
    }
}

/*
    Back-button
    Restart-button
    See comment after PointsLayout()
 */
@Composable
fun Buttons(context: Context) {
    Row (
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Button(onClick = {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("restart", false)
            context.startActivity(intent)
        },
            modifier = Modifier
                .weight(1F)
                .padding(6.dp)
        ) {
            Text (stringResource(R.string.back_button))
        }

        Button(onClick = {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra("restart", true)
            context.startActivity(intent)
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
fun PointsList() {
    PointsCategory.values().map {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .width(150.dp)

        ) {
            Text ("$it")
            Text ("${pointsList[it.ordinal]}")
        }
    }
    Divider(modifier = Modifier.width(150.dp))
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .width(150.dp)
    )  {
        Text (stringResource(R.string.total_points_text))
        Text ("${pointsList.sum()}")
    }
}

@Preview(showBackground = true)
@Composable
fun PointsLayoutPreview() {
    ThirtyTheme {
        PointsLayout()
    }
}