# Workout App

## Deskripsi Aplikasi

Sebuah aplikasi android untuk menunjang aktivitas workout sesorang dengan spesifikasi sebagai berikut ini:
1. Sport News : pengguna dapat melihat berita olahraga yang diperoleh dari Indonesia Sport New API.
2. Training Tracker: pengguna dapat melakukan mulai dan selesai untuk pelacakan olahraga dengan jenis berikut inti
    1. Speda : melacak jarak dan rute yang dilalui menggunakan GPS
    2. Walking/Running : melacak jumlah step yang dilakukan pengguna
3. Training History: terdapat training history yang menyimpan seluruh kegiatan latihan dengan ditampilkan dalam bentuk calender
4. Training Scheduler: pengguna dapat menentukan jadwal latihan yang dapat dikustomisasi untuk berjalan secara:
    a. Satu waktu spesifik
    b. Rutin per hari
    c. Rutin per hari tertentu

## Cara Kerja Aplikasi
### Sport News
1. Melakukan getAll berita yang ada di database
2. Kemudian disimpan ke dalam CardView menggunakan NewsAdapter
3. Setelah itu kelas CardView dimasukan ke dalam ViewRecycle
4. Jika jika dari CardView diklik maka akan menampilkan berita menggunakan WebView
### Training Tracker
1. Menampilkan halaman start tracking dengan pilihan akativitas dan stop atau start
2. Jika diklik start maka tracking true dan akan melakukan subscribe ke observer dan akan mendapatkan waktu, polyline path
3. Setelah kondisi tidak sedang tracking maka tracking tersebut akan disimpan ke dalam database
4. Kemudian menampilkan halaman TrackerViewModel dengan menampilkan hasil tracking
### Training History
1. Menampilkan halaman fargment_training_history untuk menampilkan calendar dan pick date yang akan dicek
2. Date akan dipassing ke fragme_history_list dan dimasukan ke TextView
3. Gunakan Date untuk mencari ke tracker database apakah ada data sesuai tanggal tersebut
4. Kemudian masukan instance hasil query ke adapter untuk menghasilkan kelas grid_history
5. Masukan grid_history ke recycleview
6. Berdasarkan jika diklik grid tersebut makan akan menampilkan TrackerViewModel
### Traning Scheduler
1. Menampilkan halaman TrainingScheduler
2. Disana terdapat button untuk delete dan tambah
3. Jika tampah maka akan dinavigasikan ke tampilan AddScheduleFragment
4. Setelah itu data akan dimasukan ke database ke database dan ketampilan TrainingSchedule

## Library dan Justifikasinya
1. GSON : untuk melakukan serelize dan deserelize data
2. Volley: untuk melakukan networking pada aplikasi
3. Picasso: untuk melakukan download gambar peta dan melakukan chacing image tersebut
4. Gson: untuk melakukan convert dari java object ke JSON dan juga sebaliknya
5. Glide: untuk melakukan loader pada gambar
6.

## Screenshoot Aplikasi

## Pembagian Kerja
### Fatkhan Masruri 13518053
1. Layout History
2. Dikumentasi Readme
### Iqbal Naufal 13518074
1. Secheduler Activity (notification)
### Muhammad Kamal Shafi 13518113
1. SportNews Fitur
2. Activity History
3. Scheduler Layout dan Activity
4. Navigation
### Faris Rizki Ekananda 13518125
1. Tracking Fitur
2. Database
Tugas Besar 3 IF3210 Workout App pada platform Android menggunakan Kotlin