package com.example.campuseyeai.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.example.campuseyeai.database.Student
import java.io.File
import android.graphics.BitmapFactory

@Composable
fun StudentCard(
    student: Student,
    onGenerateEmbedding: () -> Unit
) {

    val imageFile =
        File(student.imageFolder, "center.jpg")

    Card {

        Column(
            Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (imageFile.exists()) {

                    val bitmap =
                        BitmapFactory.decodeFile(
                            imageFile.absolutePath
                        )

                    Image(
                        bitmap.asImageBitmap(),
                        null,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                    )

                } else {

                    Icon(
                        Icons.Default.Person,
                        null,
                        modifier = Modifier.size(70.dp)
                    )

                }

                Spacer(Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        student.fullName,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(student.admissionNo)

                    Text(student.className)

                }

            }

            Spacer(Modifier.height(12.dp))

            val generated =
                student.centerEmbedding.isNotBlank()

            Button(

                modifier = Modifier.fillMaxWidth(),

                enabled = !generated,

                onClick = onGenerateEmbedding

            ) {

                Text(

                    if (generated)
                        "Embeddings Generated"
                    else
                        "Generate Embeddings"

                )

            }

        }

    }

}