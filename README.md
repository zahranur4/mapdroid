# Mapdroid 🗺️
**Aplikasi Android Peta Vektor berbasis [MapLibre Native](https://maplibre.org)**

Mapdroid adalah aplikasi peta mobile Android modern dan open-source yang dibangun menggunakan MapLibre Native SDK for Android, terintegrasi dengan workflow otomatisasi CI/CD GitHub Actions untuk auto-tagging dan pembuatan rilis APK secara langsung, serta dilengkapi dengan landing page interaktif bertenaga `croot.js`.

---

## 🎯 Fitur & Implementasi Tugas

Proyek ini telah mengimplementasikan seluruh 3 tugas utama:

### 1. 🚀 GitHub Workflow Auto Tag & Release APK
- File konfigurasi workflow tersimpan di [`.github/workflows/release.yml`](.github/workflows/release.yml).
- **Triggers**: Otomatis aktif setiap kali ada commit yang di-push ke branch `main`, atau dapat dipicu secara manual melalui `workflow_dispatch`.
- **Auto Tagging**: Menghasilkan tag versi rilis otomatis berbasis urutan build (misal: `v1.0.1`, `v1.0.2`, dst.).
- **Build APK**: Mengkompilasi release APK secara terstandarisasi (`assembleRelease`) dan menandatanganinya (signed) agar dapat langsung dipasang di ponsel Android pengguna.
- **Rilis & Unduh Langsung**: Mengunggah berkas `mapdroid.apk` dan `mapdroid-<tag>.apk` ke GitHub Releases menggunakan `softprops/action-gh-release@v2`.
- **Tautan Unduh Langsung APK**:
  ```
  https://github.com/zahranur4/mapdroid/releases/latest/download/mapdroid.apk
  ```

### 2. 🌐 Landing Page Interaktif (`index.html`) Berbasis `croot.js`
- File landing page: [`index.html`](index.html).
- Menggunakan modul JavaScript [`croot.js`](croot.js) dan [`js/main.js`](js/main.js).
- **Fitur Landing Page**:
  - **Dynamic GitHub Release Fetching**: Memanggil GitHub API (`/releases/latest`) menggunakan helper `getJSON` dari `croot.js` untuk menampilkan status rilis, tag versi, tanggal rilis, ukuran berkas APK, dan catatan rilis secara langsung.
  - **Direct Download Button**: Tombol unduh APK langsung (`mapdroid.apk`) di header, hero section, dan rilis card.
  - **Panduan Lengkap Instalasi**: Petunjuk langkah-demi-langkah bagi pengguna untuk mengunduh, mengizinkan sumber tidak dikenal (Unknown Sources), memasang, dan menjalankan aplikasi.
  - **Panduan Pengembang**: Informasi teknis struktur proyek, dependensi, dan perintah build lokal.
  - **Desain Modern & Responsif**: Dibangun dengan Tailwind CSS untuk tampilan optimal di perangkat desktop, tablet, maupun smartphone.

### 3. 📱 Aplikasi Android Peta Menggunakan MapLibre
- Menggunakan library resmi **MapLibre Native Android SDK** (`org.maplibre.gl:android-sdk:11.5.2`).
- **Fitur Aplikasi Peta**:
  - **Rendering Peta Vektor Halus 60 FPS**: Berbasis OpenGL/Vulkan tanpa lag.
  - **Multi-Style Switcher**: Pengguna dapat mengganti tampilan peta dengan mudah:
    - *OpenFreeMap Liberty* (Default)
    - *OpenFreeMap Positron* (Light Minimalist)
    - *OpenFreeMap Bright* (High Contrast)
    - *MapLibre Demotiles*
  - **Deteksi Lokasi GPS**: Tombol *"Lokasi Saya"* yang meminta izin lokasi dan memusatkan kamera peta ke posisi GPS pengguna.
  - **Kontrol Navigasi**: Tombol Zoom In, Zoom Out, dan Kompas (Arah Utara).
  - **Marker Pin Interaktif**: Sentuh lama (*long click*) di peta untuk menaruh pin kustom dengan keterangan koordinat latitude & longitude.
  - **Indikator Koordinat Real-Time**: Menampilkan posisi lintang, bujur, dan tingkat zoom kamera secara langsung saat peta digeser.

---

## 📁 Struktur Repositori

```
mapdroid/
├── .github/
│   └── workflows/
│       └── release.yml             # GitHub Actions: Auto-tag & Release APK
├── app/
│   ├── build.gradle                # Konfigurasi modul Android & dependensi MapLibre
│   ├── proguard-rules.pro          # ProGuard rules untuk MapLibre
│   └── src/main/
│       ├── AndroidManifest.xml     # Manifest aplikasi & izin INTERNET / LOCATION
│       ├── java/com/zahranur/mapdroid/
│       │   ├── MapdroidApplication.kt # Inisialisasi MapLibre Native SDK
│       │   └── MainActivity.kt        # Activity peta utama (kontrol, style, GPS)
│       └── res/
│           ├── layout/activity_main.xml  # Layout tampilan MapView & tombol kontrol
│           ├── values/                   # Strings, Colors, Themes
│           ├── drawable/                 # Vektor icon (layers, gps, zoom, compass)
│           ├── mipmap-anydpi-v26/        # Adaptive app icon
│           └── xml/                      # Backup & data extraction rules
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── croot.js                        # Modul utility helper JSCroot ES6
├── js/
│   ├── croot.js                    # Re-export croot.js
│   └── main.js                     # Logika landing page & fetch GitHub Release
├── index.html                      # Landing page Mapdroid
├── build.gradle                    # Root build script
├── settings.gradle                 # Konfigurasi repository (Google, MavenCentral)
├── gradle.properties               # JVM args & AndroidX settings
├── gradlew                         # Gradle wrapper shell script (Linux/macOS)
├── gradlew.bat                     # Gradle wrapper batch script (Windows)
└── README.md                       # Dokumentasi proyek
```

---

## 🛠️ Cara Kompilasi & Build APK Lokal

### Prasyarat
- **JDK**: Java Development Kit 17 atau lebih baru.
- **Android SDK**: Android API level 34 (Android 14) atau kompatibel.

### Menjalankan Build
Di terminal direktori proyek:

**Di Linux / macOS:**
```bash
chmod +x gradlew
./gradlew assembleRelease
```

**Di Windows PowerShell / CMD:**
```powershell
.\gradlew.bat assembleRelease
```

Hasil berkas APK siap pasang akan berada di:
```
app/build/outputs/apk/release/app-release.apk
```

---

## 🚀 Alur Kerja CI/CD (Push to GitHub)

Untuk memicu pembuatan tag dan rilis APK secara otomatis:
1. Pastikan seluruh perubahan di-commit:
   ```bash
   git add .
   git commit -m "feat: complete MapLibre Android app, landing page, and release workflow"
   ```
2. Lakukan push ke remote GitHub:
   ```bash
   git push origin main
   ```
3. Buka tab **Actions** pada repositori GitHub (`https://github.com/zahranur4/mapdroid/actions`) untuk memantau proses kompilasi.
4. Setelah selesai, berkas APK akan langsung tersedia di tab **Releases** (`https://github.com/zahranur4/mapdroid/releases`), dan tombol unduh di `index.html` akan langsung mengarah ke rilis terbaru!
