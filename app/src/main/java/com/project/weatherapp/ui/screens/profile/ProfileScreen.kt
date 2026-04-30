package com.project.weatherapp.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.project.weatherapp.ui.components.GlassContainer
import com.project.weatherapp.ui.theme.BluePrimary
import com.project.weatherapp.ui.theme.TextPrimary

@Composable
fun ProfileScreen(
    onLogout: () -> Unit
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var isMetric by remember { mutableStateOf(true) }

    // Fetch current Firebase user when this screen is entered
    val currentUser = remember { FirebaseAuth.getInstance().currentUser }
    val displayName = currentUser?.displayName?.takeIf { it.isNotBlank() } ?: "User"
    val userEmail = currentUser?.email ?: "No email"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Profile Header
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(TextPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                // Show first letter of display name as avatar
                Text(
                    text = displayName.first().uppercaseChar().toString(),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary
                )
            }
            
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(BluePrimary)
                    .clickable { /* Edit profile photo */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            displayName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Text(
            userEmail,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Preferences Section
        Text(
            "Preferences",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )
        
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Thermostat, contentDescription = null, tint = TextPrimary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Units", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    }
                    Text(
                        if (isMetric) "Metric (°C, km/h)" else "Imperial (°F, mph)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BluePrimary,
                        modifier = Modifier.clickable { isMetric = !isMetric }
                    )
                }
                
                HorizontalDivider(color = TextPrimary.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = TextPrimary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Notifications", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BluePrimary,
                            checkedTrackColor = BluePrimary.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Account Section
        Text(
            "Account",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )
        
        GlassContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clickable { /* Handle premium access */ },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFD700))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Premium Access", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                }
                
                HorizontalDivider(color = TextPrimary.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .clickable(onClick = onLogout),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFFF5252))
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Log Out", style = MaterialTheme.typography.bodyLarge, color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
