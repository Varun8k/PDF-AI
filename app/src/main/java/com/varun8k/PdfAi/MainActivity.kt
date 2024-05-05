package com.varun8k.PdfAi

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spr.jetpack_loading.components.indicators.PulsatingDot
import com.varun8k.PdfAi.ui.theme.Teal
import com.varun8k.PdfAi.ui.theme.Teeal
import com.varun8k.PdfAi.ui.theme.TestingTheme
import com.varun8k.PdfAi.viewmodel.PdfAiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: PdfAiViewModel by viewModels()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PDFScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel,
                        applicationContext,
                        contentResolver
                    )
                }
            }
        }
        viewModel.uploadStatus.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, ChatWithPdf::class.java))
            } else {
                Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(viewModel: PdfAiViewModel) {
    val isLoading by viewModel.isLoading.observeAsState()
    Box {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
            title = {
                Text(
                    "PDF AI"
                )
            },
            navigationIcon = {
                IconButton(onClick = { /* do something */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.app),
                        contentDescription = "Action button",
                        modifier = Modifier.size(
                            height = 30.dp, width = 30.dp
                        )
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* do something */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.king),
                        contentDescription = "Crown Action button",
                        modifier = Modifier.size(height = 28.dp, width = 25.dp)
                    )
                }
            },
        )
        if (isLoading == true) {
            Surface(
                color = Color(0xBBBBBBBB),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    PulsatingDot(
                        color = Teeal,
                        ballDiameter = 60f,
                        animationDuration = 1000
                    )
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun PDFScreen(
    modifier: Modifier = Modifier,
    viewModel: PdfAiViewModel,
    applicationContext: Context,
    contentResolver: ContentResolver
) {
    val result = remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        result.value = it
        if (it != null) {
            viewModel.uploadFile(it, applicationContext, contentResolver)
        }
    }


    Scaffold(topBar = {
        AppBar(viewModel)
    }) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
                    .background(Color.White),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                DocImage()
                Text(
                    text = "Chat with your",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 15.dp)
                )
                Text(
                    text = "PDF",
                    fontSize = 21.sp,
                    color = Color.Blue,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "You can ask question, get summaries. find\ninformation and more",
                    modifier = Modifier.padding(25.dp),
                    textAlign = TextAlign.Center,
                    color = Color.DarkGray
                )
                Button(
                    onClick = { launcher.launch(arrayOf("application/pdf")) },
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 10.dp,
                        pressedElevation = 15.dp,
                        disabledElevation = 0.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    colors = ButtonDefaults.buttonColors(Color(Teal))
                ) {
                    Text(text = "Select File", fontSize = 20.sp)
                }
            }


        }
    }
}


@Composable
fun DocImage() {
    Image(
        painter = painterResource(id = R.drawable.people),
        contentDescription = null,
        modifier = Modifier.size(width = 270.dp, height = 170.dp),
        contentScale = ContentScale.Crop
    )
}

@Preview(showBackground = true)
@Composable
fun PdfAIPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    TestingTheme {
        PDFScreen(
            viewModel = PdfAiViewModel(),
            applicationContext = context,
            contentResolver = context.contentResolver
        )
    }
}
