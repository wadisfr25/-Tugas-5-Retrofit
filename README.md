# PasienAPI

Aplikasi Android Kotlin sederhana untuk login ke API lalu menampilkan daftar pasien dalam `RecyclerView`.

Project ini menggunakan:
- `Retrofit` untuk request HTTP ke API
- `Gson Converter` untuk parsing JSON
- `RecyclerView` untuk menampilkan daftar pasien
- `SharedPreferences` untuk menyimpan token login

Alur aplikasi:
1. Pengguna login melalui endpoint `POST /api/login`
2. Token dari API disimpan secara lokal
3. Setelah login berhasil, aplikasi membuka halaman daftar pasien
4. Data pasien diambil dari endpoint `GET /api/pasien` dengan header `Authorization: Bearer {token}`

Endpoint utama:
- Base URL: `https://api.pahrul.my.id/`
- Login: `https://api.pahrul.my.id/api/login`
- Daftar pasien: `https://api.pahrul.my.id/api/pasien`

Struktur penting:
- `MainActivity` untuk proses login
- `PatientsActivity` untuk menampilkan daftar pasien
- `network/ApiService.kt` untuk definisi endpoint Retrofit
- `network/ApiClient.kt` untuk konfigurasi Retrofit
- `adapter/PatientAdapter.kt` untuk `RecyclerView`

Fitur tambahan:
- Konfirmasi logout dengan pilihan `Ya` atau `Tidak`

Cara menjalankan:
1. Buka project di Android Studio
2. Jalankan di emulator atau perangkat Android
3. Login menggunakan akun API yang valid

