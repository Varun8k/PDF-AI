package com.varun8k.PdfAi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.varun8k.PdfAi.api.RetrofitClient
import com.varun8k.PdfAi.model.ChatMessage
import com.varun8k.PdfAi.model.QuestionRequestBody
import com.varun8k.PdfAi.ui.theme.Teeal
import com.varun8k.PdfAi.ui.theme.TestingTheme
import com.spr.jetpack_loading.components.indicators.PulsatingDot
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChatWithPdf : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestingTheme {
                Chat(this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun Chat(context:ChatWithPdf) {
    var text by remember { mutableStateOf("") }
    var chatMessages by remember { mutableStateOf(emptyList<ChatMessage>()) }
    var isLoading by remember { mutableStateOf(false) }
    Scaffold(topBar = {
        ChatAppbar(context)
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(White)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 18.dp)
            ) {
                if (chatMessages.isEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.app),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 70.dp)
                            .size(width = 80.dp, height = 80.dp)
                    )
                    Text(
                        text = "How can I help you today?",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(top = 80.dp),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(chatMessages.size) { index ->
                            ChatMessage(chatMessages[index])
                            if(chatMessages.size-1 == index && isLoading) {
                                Row {

                                    Image(
                                        painter = painterResource(id = R.drawable.app),
                                        contentDescription = "Action button",
                                        modifier = Modifier.size(
                                            height = 25.dp, width = 25.dp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "PDFAI",
                                        modifier = Modifier
                                            .padding(start = 5.dp),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Box(
                                    ///contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(start = 60.dp, top = 8.dp)
                                ) {

                                    PulsatingDot(
                                        color = Teeal,
                                        ballDiameter = 30f,
                                        animationDuration = 2000,
                                    )
                                }
                            }
                        }

                    }

                }
            }

            OutlinedTextField(
                value = text,
                onValueChange = { newText ->
                    text = newText
                    Log.d("TextInput", "Text value changed: $newText")
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                placeholder = { Text(text = "Message PdfAI...", color = Gray) },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            chatMessages += ChatMessage(text, isUserMessage = true)
                            GlobalScope.launch {
                                isLoading = true
                                val requestBody = QuestionRequestBody(text)
                                text = ""
                                val response = RetrofitClient.apiService.question(requestBody)
                                isLoading = false
                                chatMessages += ChatMessage(
                                    response.body()!!.answer,
                                    isUserMessage = false
                                )

                            }

                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Email icon",
                            tint = Teeal
                        )
                    }
                },

                colors = TextFieldDefaults.textFieldColors(
                    containerColor = White,
                    cursorColor = Teeal,
                    focusedIndicatorColor = Teeal,
                    unfocusedIndicatorColor = Gray
                ),
                shape = RoundedCornerShape(15.dp)
            )
        }
    }
}

@Composable
fun ChatMessage(message: ChatMessage) {
    Row {

        Image(
            painter = if (message.isUserMessage) painterResource(id = R.drawable.user) else painterResource(
                id = R.drawable.app
            ),
            contentDescription = "Action button",
            modifier = Modifier.size(
                height = 25.dp, width = 25.dp
            ).clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (message.isUserMessage) "You" else "PDFAI",
            modifier = Modifier
                .padding(start = 5.dp),
            fontWeight = FontWeight.SemiBold
        )

    }

    Text(
        text = message.answer,
        modifier = Modifier.padding(start = 40.dp, end = 8.dp, bottom = 20.dp)
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAppbar(context:ChatWithPdf) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
        modifier = Modifier.drawBehind {
            val bordersize = 1.dp.toPx()
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = bordersize
            )
        },
        title = {
            Text(
                "PDF AI"
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.app),
                contentDescription = "Action button",
                modifier = Modifier.size(
                    height = 30.dp, width = 32.dp
                )
            )
        },
        actions = {
            IconButton(onClick = { context.finish()}) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Email icon",
                    tint = Teeal
                )
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestingTheme {

//        input()
        Chat(ChatWithPdf())
    }
}