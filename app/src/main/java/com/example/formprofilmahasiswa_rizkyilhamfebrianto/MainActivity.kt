package com.example.formprofilmahasiswa_rizkyilhamfebrianto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.formprofilmahasiswa_rizkyilhamfebrianto.ui.theme.FormProfilMahasiswaRizkyIlhamFebriantoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FormProfilMahasiswaRizkyIlhamFebriantoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ProfilMahasiswaScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

private val daftarJurusan = listOf(
    "Teknik Informatika",
    "Sistem Informasi",
    "Teknik Elektro",
    "Manajemen"
)

@Composable
fun ProfilMahasiswaScreen(modifier: Modifier = Modifier) {
    // ===== STATE: data yang bisa berubah & dipantau Compose =====
    var nama by rememberSaveable { mutableStateOf("") }
    var nim by rememberSaveable { mutableStateOf("") }
    var jurusan by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var semester by rememberSaveable { mutableFloatStateOf(1f) }
    var aktif by rememberSaveable { mutableStateOf(true) }
    var tersimpan by rememberSaveable { mutableStateOf(false) }
    var menuJurusanTerbuka by remember { mutableStateOf(false) }

    // Nilai turunan dari state: otomatis dihitung ulang saat state berubah
    val namaValid = nama.isNotBlank()
    val nimValid = nim.length >= 8 && nim.all { it.isDigit() }
    val formValid = namaValid && nimValid && jurusan.isNotEmpty() && gender.isNotEmpty()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Form Profil Mahasiswa", style = MaterialTheme.typography.headlineSmall)

        // ===== EVENT: onValueChange dipanggil setiap user mengetik =====
        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it; tersimpan = false },
            label = { Text("Nama Lengkap") },
            isError = nama.isNotEmpty() && !namaValid,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nim,
            onValueChange = { input ->
                nim = input.filter { it.isDigit() } // hanya angka
                tersimpan = false
            },
            label = { Text("NIM") },
            isError = nim.isNotEmpty() && !nimValid,
            supportingText = {
                if (nim.isNotEmpty() && !nimValid) Text("NIM minimal 8 digit angka")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Dropdown jurusan
        Text("Jurusan")
        Box {
            OutlinedButton(onClick = { menuJurusanTerbuka = true }) {
                Text(jurusan.ifEmpty { "Pilih jurusan" })
            }
            DropdownMenu(
                expanded = menuJurusanTerbuka,
                onDismissRequest = { menuJurusanTerbuka = false }
            ) {
                daftarJurusan.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            jurusan = item
                            tersimpan = false
                            menuJurusanTerbuka = false
                        }
                    )
                }
            }
        }

        // Radio button jenis kelamin
        Text("Jenis Kelamin")
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            listOf("Laki-laki", "Perempuan").forEach { pilihan ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.selectable(
                        selected = gender == pilihan,
                        onClick = { gender = pilihan; tersimpan = false },
                        role = Role.RadioButton
                    )
                ) {
                    RadioButton(selected = gender == pilihan, onClick = null)
                    Text(pilihan, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }

        // Slider semester
        Text("Semester: ${semester.toInt()}")
        Slider(
            value = semester,
            onValueChange = { semester = it; tersimpan = false },
            valueRange = 1f..14f,
            steps = 12
        )

        // Switch status aktif
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Status mahasiswa aktif", modifier = Modifier.weight(1f))
            Switch(checked = aktif, onCheckedChange = { aktif = it; tersimpan = false })
        }

        // Tombol Simpan & Reset
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { tersimpan = true },
                enabled = formValid // tombol aktif/nonaktif mengikuti state
            ) { Text("Simpan") }

            OutlinedButton(onClick = {
                nama = ""; nim = ""; jurusan = ""; gender = ""
                semester = 1f; aktif = true; tersimpan = false
            }) { Text("Reset") }
        }

        Spacer(Modifier.height(4.dp))

        // ===== RECOMPOSITION: bagian ini digambar ulang otomatis saat state berubah =====
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Pratinjau Profil", fontWeight = FontWeight.Bold)
                Text("Nama      : ${nama.ifBlank { "-" }}")
                Text("NIM       : ${nim.ifBlank { "-" }}")
                Text("Jurusan   : ${jurusan.ifBlank { "-" }}")
                Text("Gender    : ${gender.ifBlank { "-" }}")
                Text("Semester  : ${semester.toInt()}")
                Text("Status    : ${if (aktif) "Aktif" else "Tidak aktif"}")
                if (tersimpan) {
                    Text(
                        "✅ Profil berhasil disimpan!",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfilMahasiswaPreview() {
    FormProfilMahasiswaRizkyIlhamFebriantoTheme {
        ProfilMahasiswaScreen()
    }
}