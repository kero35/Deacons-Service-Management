package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AttendanceStatus
import com.example.data.model.AttendanceType
import com.example.data.model.Member
import com.example.ui.components.DeaconHeader
import com.example.ui.components.MetricStatCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.AbsentRedBg
import com.example.ui.theme.BurgundyPrimary
import com.example.ui.theme.GoldSecondary
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenBg
import com.example.ui.viewmodel.DeaconsViewModel

@Composable
fun DashboardScreen(
    viewModel: DeaconsViewModel,
    onNavigateToMembers: () -> Unit,
    onNavigateToAttendance: () -> Unit,
    onNavigateToAssessments: () -> Unit,
    onNavigateToReports: () -> Unit,
    onSelectMember: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val members by viewModel.members.collectAsStateWithLifecycle()
    val groups by viewModel.groups.collectAsStateWithLifecycle()
    val attendances by viewModel.attendances.collectAsStateWithLifecycle()
    val hymnAssessments by viewModel.hymnAssessments.collectAsStateWithLifecycle()
    val overdueMembersInfo by viewModel.overdueMembersInfo.collectAsStateWithLifecycle()
    val followUpOverdue = overdueMembersInfo.filter { it.hasAnyOverdue }
    val today = viewModel.todayDate

    // Compute Metrics
    val totalMembers = members.size
    val totalGroups = groups.size

    val serviceAttendances = attendances.filter { it.type == AttendanceType.SERVICE }
    val presentCount = serviceAttendances.count { it.status == AttendanceStatus.PRESENT }
    val absentCount = serviceAttendances.count { it.status == AttendanceStatus.ABSENT }
    val serviceRate = if (serviceAttendances.isNotEmpty()) {
        (presentCount * 100) / serviceAttendances.size
    } else 0

    // Mass Attendance for current month
    val massAttendances = attendances.filter { it.type == AttendanceType.MASS }
    val massPresent = massAttendances.count { it.status == AttendanceStatus.PRESENT }
    val massTotal = massAttendances.size.coerceAtLeast(1)
    val massRatioText = "$massPresent / ${members.size * 2}"

    // Top performers (members with highest assessment scores or full attendance)
    val topMembers = members.take(4)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            DeaconHeader(
                title = "لوحة التحكم والمتابعة",
                subtitle = "مدرسة الشمامسة - القديس اسطفانوس بمير"
            )
        }

        // Stats Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "إجمالي المخدومين",
                        value = "$totalMembers مخدوم",
                        icon = Icons.Default.People,
                        accentColor = BurgundyPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "المجموعات",
                        value = "$totalGroups مجموعات",
                        icon = Icons.Default.Groups,
                        accentColor = GoldSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "حضور الخدمة",
                        value = "$presentCount حاضر",
                        icon = Icons.Default.CheckCircle,
                        accentColor = PresentGreen,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "غياب الخدمة",
                        value = "$absentCount غائب",
                        icon = Icons.Default.Close,
                        accentColor = AbsentRed,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "حضور القداسات (شهرياً)",
                        value = massRatioText,
                        icon = Icons.Default.Church,
                        accentColor = Color(0xFF673AB7),
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "متوسط نسبة الحضور",
                        value = "$serviceRate%",
                        icon = Icons.Default.TrendingUp,
                        accentColor = Color(0xFF00796B),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Spacer(modifier = Modifier.height(12.dp))
            SectionHeader(title = "الإجراءات السريعة", icon = Icons.Default.Assignment)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    QuickActionCard(
                        title = "تسجيل الحضور",
                        subtitle = "خدمة وقداسات",
                        icon = Icons.Default.CalendarMonth,
                        color = BurgundyPrimary,
                        onClick = onNavigateToAttendance
                    )
                }
                item {
                    QuickActionCard(
                        title = "تسميع الألحان",
                        subtitle = "تسجيل الدرجات",
                        icon = Icons.Default.MusicNote,
                        color = GoldSecondary,
                        onClick = onNavigateToAssessments
                    )
                }
                item {
                    QuickActionCard(
                        title = "إضافة مخدوم",
                        subtitle = "بيانات جديدة",
                        icon = Icons.Default.Add,
                        color = PresentGreen,
                        onClick = onNavigateToMembers
                    )
                }
                item {
                    QuickActionCard(
                        title = "التقارير الشاملة",
                        subtitle = "تصدير ومعاينة Excel",
                        icon = Icons.Default.TrendingUp,
                        color = Color(0xFF0288D1),
                        onClick = onNavigateToReports
                    )
                }
            }
        }

        // Top Performers Section
        if (members.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = "أفضل المخدومين تميزاً",
                    icon = Icons.Default.Star,
                    actionText = "عرض الكل",
                    onActionClick = onNavigateToMembers
                )
            }

            items(topMembers) { member ->
                TopMemberCard(
                    member = member,
                    onClick = { onSelectMember(member.id) }
                )
            }

            // Follow-up Required Section (Realtime Overdue Grace Period alerts)
            if (followUpOverdue.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(
                        title = "مخدومين يحتاجون متابعة (${followUpOverdue.size})",
                        icon = Icons.Default.Warning
                    )
                }

                items(followUpOverdue, key = { it.member.id }) { overdueInfo ->
                    FollowUpMemberCard(
                        member = overdueInfo.member,
                        reason = overdueInfo.summaryReasons.joinToString(" • "),
                        isAttendanceAlert = overdueInfo.isAttendanceOverdue,
                        onClick = { onSelectMember(overdueInfo.member.id) }
                    )
                }
            }
        } else {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.People,
                            contentDescription = null,
                            tint = BurgundyPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "قاعدة البيانات فارغة وجاهزة",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BurgundyPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "يمكنك إضافة مخدومين جدد أو استيراد ملف Excel المعتمد بالكامل من شاشة المخدومين أو التقارير.",
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TopMemberCard(
    member: Member,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() }
            .testTag("top_member_${member.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(BurgundyPrimary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.fullName.firstOrNull()?.toString() ?: "م",
                    fontWeight = FontWeight.Bold,
                    color = BurgundyPrimary,
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${member.schoolClass} — ${member.area}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = PresentGreenBg
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = GoldSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "متميز",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PresentGreen
                    )
                }
            }
        }
    }
}

@Composable
fun FollowUpMemberCard(
    member: Member,
    reason: String,
    isAttendanceAlert: Boolean = true,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val alertAccentColor = if (isAttendanceAlert) Color(0xFFFF8A80) else Color(0xFFFFD54F)
    val alertBgColor = if (isAttendanceAlert) Color(0xFF2C1B1B) else Color(0xFF2B2516)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() }
            .testTag("follow_up_card_${member.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = alertBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(alertAccentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = alertAccentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = member.fullName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = reason,
                        style = MaterialTheme.typography.bodySmall,
                        color = alertAccentColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Call Parent and Visit / Record
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val callPhone = member.parentPhone.ifBlank { member.phone }
                if (callPhone.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$callPhone"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("call_parent_button_${member.id}"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = alertAccentColor)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "اتصال",
                            modifier = Modifier.size(16.dp),
                            tint = alertAccentColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "اتصال بولي الأمر",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = alertAccentColor
                        )
                    }
                }

                OutlinedButton(
                    onClick = { onClick() },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("visit_button_${member.id}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldSecondary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "افتقاد",
                        modifier = Modifier.size(16.dp),
                        tint = GoldSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "تسجيل افتقاد",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldSecondary
                    )
                }
            }
        }
    }
}
