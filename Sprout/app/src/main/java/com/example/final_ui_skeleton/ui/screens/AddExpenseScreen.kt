package com.example.final_ui_skeleton.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_ui_skeleton.model.Spending
import com.example.final_ui_skeleton.ui.components.AppBottomBar
import com.example.final_ui_skeleton.ui.components.AppColors
import com.example.final_ui_skeleton.ui.components.CategoryChip
import com.example.final_ui_skeleton.ui.components.PrimaryButton
import com.example.final_ui_skeleton.viewmodel.SproutViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * 1. What: Lets the user log a new expense entering amount, merchant, category, and date, and also picture the receipt.
 * 2. Who: Used by AppNavigation.
 * 3. When: Executed when the user taps the add expense button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: SproutViewModel = viewModel(),
    onExpenseAdded: () -> Unit = {},
    onNavigateDashboard: () -> Unit = {},
    onNavigateBudgets: () -> Unit = {},
    onNavigateRecommend: () -> Unit = {},
    onNavigateSettings: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Personal") }
    var merchant by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    var customCategory by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var receiptText by remember { mutableStateOf("") }
    var receiptImagePath by remember { mutableStateOf("") }
    var showCamera by remember { mutableStateOf(false) }
    var isProcessingReceipt by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(java.util.Date()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("M/d/yyyy", Locale.getDefault()) }
    val today = remember(selectedDate) { dateFormatter.format(selectedDate) }

    if (showDatePicker) {
        val calendar = java.util.Calendar.getInstance().apply { time = selectedDate }
        android.app.DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.time
                showDatePicker = false
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        ).show()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) showCamera = true }

    if (showCamera) {
        CameraScreen(
            onImageCaptured = { uri ->
                showCamera = false
                isProcessingReceipt = true
                receiptImagePath = uri.toString()
                val image = InputImage.fromFilePath(context, uri)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        receiptText = visionText.text
                        val amountRegex = Regex("""\$?\d+\.\d{2}""")
                        val amounts = amountRegex.findAll(visionText.text)
                            .mapNotNull { it.value.replace("$", "").toDoubleOrNull() }.toList()
                        if (amounts.isNotEmpty() && amount.isBlank()) {
                            amount = amounts.max().toString()
                        }
                        if (note.isBlank() && receiptText.isNotBlank()) {
                            note = receiptText.take(100)
                        }
                        isProcessingReceipt = false
                    }
                    .addOnFailureListener { isProcessingReceipt = false }
            },
            onDismiss = { showCamera = false }
        )
        return
    }

    val finalCategory = if (selectedCategory == "Other" && customCategory.isNotBlank()) customCategory else selectedCategory

    Scaffold(
        containerColor = AppColors.Background,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Background)
            )
        },
        bottomBar = {
            AppBottomBar(
                currentRoute = "budgets",
                onDashboard = onNavigateDashboard,
                onBudgets = onNavigateBudgets,
                onRecommend = onNavigateRecommend,
                onSettings = onNavigateSettings
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Add an Expense", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("Log a purchase in seconds", fontSize = 13.sp, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(16.dp))

            // amount input — no negatives allowed
            Surface(
                color = Color(0xFFD8D4EC),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().border(2.dp, AppColors.Primary, RoundedCornerShape(16.dp))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                    Text("Amount", fontWeight = FontWeight.Medium, fontSize = 16.sp)
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.toDoubleOrNull()?.let { it >= 0 } == true) {
                                amount = input
                            }
                        },
                        placeholder = { Text("0.00") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // merchant input
            OutlinedTextField(
                value = merchant,
                onValueChange = { merchant = it },
                label = { Text("Merchant (optional)") },
                placeholder = { Text("e.g. Whole Foods, Amazon...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // scan receipt button
            Surface(
                color = if (receiptText.isNotBlank()) Color(0xFF2E7D32).copy(alpha = 0.15f) else Color(0xFFCFC8F0),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp).clickable {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            ) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    when {
                        isProcessingReceipt -> {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = AppColors.Primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Reading receipt...", fontSize = 15.sp)
                        }
                        receiptText.isNotBlank() -> {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = Color(0xFF2E7D32))
                            Spacer(Modifier.width(8.dp))
                            Text("Receipt scanned ✓", fontSize = 15.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                        }
                        else -> {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.DarkGray)
                            Spacer(Modifier.width(8.dp))
                            Text("Scan receipt", fontSize = 15.sp)
                        }
                    }
                }
            }

            if (receiptText.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Surface(color = Color.White, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Receipt extracted:", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                        Text(receiptText.take(150) + if (receiptText.length > 150) "..." else "", fontSize = 11.sp, color = Color.DarkGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // personal / shared toggle
            Surface(color = Color.White, shape = RoundedCornerShape(50), modifier = Modifier.fillMaxWidth()) {
                Row {
                    listOf("Personal", "Shared Budget").forEach { tab ->
                        Surface(
                            color = if (selectedTab == tab) AppColors.Primary else Color.Transparent,
                            shape = RoundedCornerShape(50),
                            modifier = Modifier.weight(1f)
                        ) {
                            TextButton(onClick = { selectedTab = tab }) {
                                Text(tab, color = if (selectedTab == tab) Color.White else Color.Black)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Category", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Essentials", fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.align(Alignment.Start)) {
                listOf("Groceries", "Rent", "Toiletries").forEach { cat ->
                    CategoryChip(label = cat, selected = selectedCategory == cat, onClick = { selectedCategory = cat })
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.align(Alignment.Start)) {
                CategoryChip(label = "Transport", selected = selectedCategory == "Transport", onClick = { selectedCategory = "Transport" })
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Leisure", fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.align(Alignment.Start)) {
                listOf("Dining Out", "Shopping", "Fun").forEach { cat ->
                    CategoryChip(label = cat, selected = selectedCategory == cat, onClick = { selectedCategory = cat })
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Other", fontWeight = FontWeight.Medium, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.align(Alignment.Start)) {
                CategoryChip(label = "Other", selected = selectedCategory == "Other", onClick = { selectedCategory = "Other" })
            }
            if (selectedCategory == "Other") {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customCategory,
                    onValueChange = { customCategory = it },
                    label = { Text("Custom category") },
                    placeholder = { Text("e.g. Medical, Gym, Pet...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(color = Color.White, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = AppColors.Primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Date", color = AppColors.Primary)
                    }
                    Text(today, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (optional)") },
                placeholder = { Text("Add a note...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "Add Expense",
                onClick = {
                    val amountDouble = amount.toDoubleOrNull() ?: 0.0
                    if (amountDouble > 0 && finalCategory.isNotEmpty()) {
                        val spending = Spending(
                            date = today,
                            merchant = merchant,
                            category = finalCategory,
                            perOrShared = selectedTab,
                            amount = amountDouble,
                            description = note,
                            receiptText = receiptText,
                            receiptImagePath = receiptImagePath
                        )
                        viewModel.addExpense(spending) { onExpenseAdded() }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun PreviewAddExpenseScreen() {
    AddExpenseScreen()
}


/**
 * 1. What: Displays a live camera preview and lets the user capture a photo of a receipt
 * 2. Who: Called byAddExpenseScreen.
 * 3. When:Executes when the user taps “Scan receipt".
 */
@Composable
fun CameraScreen(onImageCaptured: (Uri) -> Unit, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    DisposableEffect(Unit) { onDispose { cameraExecutor.shutdown() } }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = androidx.camera.core.Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val capture = ImageCapture.Builder().build()
                    imageCapture = capture
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, capture)
                    } catch (e: Exception) { e.printStackTrace() }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        Column(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    val photoFile = File(context.cacheDir, "receipt_${System.currentTimeMillis()}.jpg")
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    imageCapture?.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(output: ImageCapture.OutputFileResults) { onImageCaptured(uri) }
                            override fun onError(exc: ImageCaptureException) { exc.printStackTrace() }
                        })
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
                shape = RoundedCornerShape(50),
                modifier = Modifier.size(72.dp)
            ) { Icon(Icons.Default.PhotoCamera, contentDescription = "Capture", modifier = Modifier.size(32.dp)) }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.White, fontWeight = FontWeight.Bold) }
        }
    }
}

@Preview
@Composable
fun PreviewCameraScreen() {
    CameraScreen(onImageCaptured = {}, onDismiss= {})
}
